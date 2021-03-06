package main;


import java.util.Properties;

/**
 * This class delegates the reading of properties. 
 * It provides a number of static strings as property keys.
 * 
 * @author W.H. Mook
 * @author Gerben G. Meyer
 *
 */

public class Settings {

	// APPLICATION
	public final static String APPLICATION_NAME = "application_name";
	public final static String APPLICATION_NAME_ABBREVIATION = "application_name_abbreviation";
	public final static String APPLICATION_ICON = "application_icon";
	public final static String APPLICATION_VERSION = "application_version";

	// DIRECTORY LOCATIONS
	public final static String HTML_DATA_DIR = "html_data_dir";

	// SERVER SETTINGS
	public final static String XML_PORT = "xml_port";
	public final static String XML_SERVER_PASSWORD = "xml_server_password";
	public final static String HTTP_PORT = "http_port";
	public final static String HTTP_SERVER_ADDRESS = "http_server_address";
	public final static String GOOGLE_MAPS_V2_API_KEY = "google_maps_v2_api_key";
	public final static String GOOGLE_ANALYTICS_KEY = "google_analytics_key";
	
	// AGENT SETTINGS
	public final static String AGENT_EXECUTION_PRIORITY = "agent_execution_priority";
	public final static String PAUSE_AGENT_EXECUTION_WHEN_PUTTING_AGENTS = "pause_agent_execution_when_putting_agents";
	public final static String AGENT_PROBLEM_DETECTION_ENABLED = "agent_problem_detection_enabled";
	public final static String AGENT_PROBLEM_LEARNING_ENABLED = "agent_problem_learning_enabled";
	
	// DATABASE SETUP
	public final static String DATABASE_HOST = "database_host";
	public final static String DATABASE_USER = "database_user";
	public final static String DATABASE_PASSWORD = "database_password";
	public final static String DATABASE_NAME = "database_name";	
	
	// HTML INTERFACE
	public final static String SHOW_ALL_OBJECTS = "show_all_objects";
	public final static String SHOW_OVERVIEW_LISTS = "show_overview_lists";
	public final static String SHOW_SMALL_DETAILS_PANE = "show_small_details_pane";
	public final static String DEFAULT_AGENT = "default_agent";
	public final static String DEFAULT_CLUSTERING = "default_clustering";
	public final static String KEYWORD_DEEPLINK = "keyword_deeplink";
	
	// NOTIFICATION SETTINGS
	public final static String NOTIFICATION_EMAIL_ENABLED = "notification_email_enabled";
	public final static String NOTIFICATION_EMAIL_MINUTES_THRESHOLD = "notification_email_threshold";
	public final static String NOTIFICATION_EMAIL_STATUS_THRESHOLD = "notification_email_status_threshold";
	public final static String NOTIFICATION_EMAIL_RECIPIENT_ADDRESS = "notification_email_recipient";
	public final static String NOTIFICATION_EMAIL_CONTENT = "notification_email_content";
	public static final String NOTIFICATION_EMAIL_ALLOWED_TYPES = "notification_email_allowed_types";
	
	// SMTP SETTINGS
	public final static String SMTP_EMAIL_ADDRESS = "smtp_email_address";
	public final static String SMTP_SERVER = "smtp_server";
	public final static String SMTP_USERNAME = "smtp_username";
	public final static String SMPT_PASSWORD = "smtp_password";

	private static Properties settings;
	
	/**
	 * Constructs a new Settings object from a Properties object.
	 * 
	 * @param settings the Properties to be used with this Settings  
	 */
	public Settings(Properties settings){
		Settings.settings = settings;
	}

	/**
	 * Retrieves the corresponding setting value for a key. 
	 * 
	 * @param key the property name to get
	 * @return the string value of the property
	 */
	public static synchronized String getProperty(String key) {
		if (settings == null) {
			return null;
		}
		
		String property = settings.getProperty(key);
		if (property == null){
			System.err.println("Setting "+key+" is not defined!");
			return null;
		}
		return property.trim();
	}
}