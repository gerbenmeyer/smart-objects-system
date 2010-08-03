package util;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * 
 * @author Gerben
 *
 */
public class BenchMarker {

	private long lastTime = 0;
	private long firstTime = 0;
	private String name;
	private boolean enabled;

	/**
	 * 
	 * @param name
	 */
	public BenchMarker(String name) {
		this(name,true);
		
	}
	
	public BenchMarker(String name, boolean enabled){
		this.name = name;
		this.enabled = enabled;
	}

	/**
	 * 
	 */
	public void start() {
		if (!enabled) return;
		lastTime = System.currentTimeMillis();
		firstTime = lastTime;
		printString("Starting benchmark");
	}

	/**
	 * 
	 * @param taskName
	 */
	public void taskFinished(String taskName) {
		if (!enabled) return;
		long timeUsed = System.currentTimeMillis() - lastTime;

		double seconds = timeUsed / 1000.0;

		printString("Task \"" + taskName + "\" finished, time used: " + seconds + " s");

		lastTime = System.currentTimeMillis();
	}

	/**
	 * 
	 */
	public void stop() {
		if (!enabled) return;
		long timeUsed = System.currentTimeMillis() - firstTime;

		double seconds = timeUsed / 1000.0;

		printString("Benchmark finished, total time used: " + seconds + " s");
	}
	
	private void printString(String print){
		print = "["+new SimpleDateFormat("HH:mm").format(new GregorianCalendar().getTime())+" "+ name + "] "+print;
		System.out.println(print);
	}
}
