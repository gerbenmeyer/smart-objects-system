package model.agent.agents;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import main.Settings;
import model.agent.Agent;
import model.agent.AgentViewable;
import model.agent.collection.AgentCollection;
import model.agent.property.properties.BooleanProperty;
import model.agent.property.properties.DependenciesProperty;
import model.agent.property.properties.TimeProperty;
import util.comparators.AgentStatusComparator;
import util.enums.AgentStatus;
import util.enums.PropertyType;
import util.htmltool.HtmlPageGenerator;
import util.htmltool.HtmlTool;

public class NotifyAgent extends Agent {

	private String[] allowedTypes;

	public NotifyAgent(String id) {
		super(id);
		this.allowedTypes = Settings.getProperty(Settings.NOTIFICATION_EMAIL_ALLOWED_TYPES).split(",");
		if (get(Agent.HIDDEN).isEmpty()) {
			set(PropertyType.BOOLEAN, Agent.HIDDEN, Boolean.toString(true));
		}
		if (get("FirstRun").isEmpty()) {
			set(PropertyType.BOOLEAN, "FirstRun", Boolean.toString(true));
		}
		if (get("LastRun").isEmpty()) {
			set(PropertyType.TIME, "LastRun", TimeProperty.nowString());
		}
		if (get("ExistingIssueAgents").isEmpty()) {
			set(PropertyType.DEPENDENCIES, "ExistingIssueAgents", new DependenciesProperty("ExistingIssueAgents").toString());
		}
	}

	@Override
	public void act() throws Exception {
		TimeProperty lastRun = (TimeProperty) getProperty("LastRun");
		BooleanProperty firstRun = (BooleanProperty) getProperty("FirstRun");
		Calendar threshold = new GregorianCalendar();
		threshold.add(Calendar.MINUTE, -Integer.parseInt(Settings.getProperty(Settings.NOTIFICATION_EMAIL_MINUTES_THRESHOLD)));

		// Is it time to send the next notification email?
		if (!lastRun.getDateTime().before(threshold)) {
			return;
		}

		DependenciesProperty existingIssueIds = (DependenciesProperty) getProperty("ExistingIssueAgents");
		Vector<String> newIDs = new Vector<String>();
		Vector<String> resolvedIDs = new Vector<String>();

		Vector<String> allIds = new Vector<String>();
		Vector<String> idsToRemove = new Vector<String>();
		allIds.addAll(existingIssueIds.getList());
		List<AgentViewable> agents = new Vector<AgentViewable>();
		for (String type : allowedTypes) {
			agents.addAll(AgentCollection.getInstance().searchAgents("type:" + type.trim().toLowerCase()));
		}
		for (AgentViewable av : agents) {
			String id = av.getID();
			if (!allIds.contains(id)) {
				allIds.add(id);
			}
		}

		// sort all new IDs.
		for (String ID : allIds) {

			// retrieve agent view / status / type
			AgentViewable av = AgentCollection.getInstance().get(ID);
			if (av == null) {
				continue;
			}
			AgentStatus status = av.getStatus();
			String type = av.get(Agent.TYPE);

			// check if this agent qualifies for adding
			if (status != null && status.getValue() <= Integer.valueOf(Settings.getProperty(Settings.NOTIFICATION_EMAIL_STATUS_THRESHOLD)) && !type.isEmpty()
					&& !newIDs.contains(ID) && !existingIssueIds.getList().contains(ID)) {
				newIDs.add(ID);
			} else {
				// the ID does not fall within notification email criteria.
				// Remove it from the ID's. Reasons for this are:
				// no status, no type, or the status of the object does not
				// qualify for notification.
				newIDs.remove(ID);
				idsToRemove.add(ID);
				if (existingIssueIds.getList().contains(ID)) {
					resolvedIDs.add(ID);
				}
			}
		}

		// At this point you have 2 HashMaps with type keys
		// and status keys, referring to collections of IDs. Every ID should
		// exist once in every matrix created this way.

		// create a buffer for the content in the email.
		StringBuffer content = new StringBuffer();

		// build content for new Issues.
		if (!newIDs.isEmpty() && !firstRun.getValue()) {
			existingIssueIds.getList().removeAll(idsToRemove);

			// create header for every type.
			content.append(formatIssuesHTMLTable("New Issues", newIDs));
			content.append(formatIssuesHTMLTable("Existing Issues", existingIssueIds.getList()));
			content.append(formatIssuesHTMLTable("Resolved Issues", resolvedIDs));

			sendEmail(Settings.getProperty(Settings.NOTIFICATION_EMAIL_RECIPIENT_ADDRESS), content.toString(), new Vector<String>());

			resolvedIDs.clear();
		}

		// wrap up, transfer all newIDs to the oldID vector.
		existingIssueIds.getList().addAll(newIDs);
		newIDs.clear();

		set(PropertyType.DEPENDENCIES, "ExistingIssueAgents", existingIssueIds.toString());
		set(PropertyType.TIME, "LastRun", TimeProperty.nowString());
		set(PropertyType.BOOLEAN, "FirstRun", Boolean.toString(false));
	}

	/**
	 * Format the html of a category of issues ( a set of IDs ), with title and
	 * percentage and source matrices.
	 * 
	 * @param title
	 * @param typeMatrix
	 * @param stateMatrix
	 * @return
	 */
	private StringBuffer formatIssuesHTMLTable(String title, Vector<String> targetIDs) {

		StringBuffer content = new StringBuffer();

		if (targetIDs.isEmpty()) {
			return content;
		}

		content.append(HtmlTool.h2(title));

		StringBuffer tableContent = new StringBuffer();

		Collections.sort(targetIDs, new AgentStatusComparator(AgentCollection.getInstance()));
		for (String ID : targetIDs) {
			AgentViewable pov = AgentCollection.getInstance().get(ID);
			if (pov == null) {
				continue;
			}
			tableContent.append(formatIssueHTMLTableRow(pov));
		}

		content.append(HtmlTool.table(HtmlTool.tr(HtmlTool.th("") + HtmlTool.th("Label") + HtmlTool.th("Description")) + tableContent));

		return content;
	}

	private String formatIssueHTMLTableRow(AgentViewable av) {
		String img = HtmlTool.img(av.getStatus().toString().toLowerCase(), Settings.getProperty(Settings.NOTIFICATION_EMAIL_CONTENT) + av.getIcon());

		String url = Settings.getProperty(Settings.HTTP_SERVER_ADDRESS) + "?" + Settings.getProperty(Settings.KEYWORD_DEEPLINK) + "=" + av.getID();

		return HtmlTool.tr(HtmlTool.td(img) + HtmlTool.td(HtmlTool.aLink(av.get(Agent.LABEL), url)) + HtmlTool.td(av.get(Agent.DESCRIPTION)));

	}

	/**
	 * Send the email with notifications.
	 * 
	 * @param address
	 * @param content
	 * @param filesToAttach
	 * @throws Exception
	 */
	private void sendEmail(String address, String content, Vector<String> filesToAttach) throws Exception {
		Properties props = System.getProperties();
		props.put("mail.smtps.auth", "true");

		Session session = Session.getInstance(props, null);

		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(Settings.getProperty(Settings.APPLICATION_NAME_ABBREVIATION) + " <" + Settings.getProperty(Settings.SMTP_EMAIL_ADDRESS)
				+ ">"));
		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(address, false));

		String title = "[" + Settings.getProperty(Settings.APPLICATION_NAME_ABBREVIATION) + "] email notification";

		msg.setSubject(title);

		HtmlPageGenerator htmlPage = new HtmlPageGenerator(title, null);

		htmlPage.addToBodyHtml(content);

		Multipart mp = new MimeMultipart();
		MimeBodyPart htmlPart = new MimeBodyPart();

		htmlPart.setDataHandler(new DataHandler(new ByteArrayDataSource(htmlPage.generatePage().toString(), "text/html")));
		mp.addBodyPart(htmlPart);

		for (String file : filesToAttach) {
			MimeBodyPart attachPart = new MimeBodyPart();
			attachPart.attachFile(Settings.getProperty(Settings.HTML_DATA_DIR) + file);
			mp.addBodyPart(attachPart);
		}
		msg.setHeader("X-Mailer", Settings.getProperty(Settings.APPLICATION_NAME_ABBREVIATION) + " Notification Agent");
		msg.setSentDate(new Date());
		msg.setContent(mp);

		Transport t = session.getTransport("smtps");
		try {
			t.connect(Settings.getProperty(Settings.SMTP_SERVER), Settings.getProperty(Settings.SMTP_USERNAME), Settings.getProperty(Settings.SMPT_PASSWORD));
			t.sendMessage(msg, msg.getAllRecipients());
		} finally {
			t.close();
		}
	}

}