package util;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * The BenchMarker class is used to create a simple benchmark which keeps track of time and provides output when a task has ended. 
 * 
 * @author Gerben G. Meyer
 */
public class BenchMarker {

	private long lastTime = 0;
	private long firstTime = 0;
	private String name;
	private boolean enabled;

	/**
	 * Constructs a new enabled BenchMarker instance with a name.
	 * 
	 * @param name the name
	 */
	public BenchMarker(String name) {
		this(name,true);
	}

	/**
	 * Constructs a new BenchMarker instance with a name.
	 * Specify the enabled parameter to enable or disable the BenchMarker.
	 * 
	 * @param name the name
	 * @param enabled enable or disable the BenchMarker
	 */
	public BenchMarker(String name, boolean enabled){
		this.name = name;
		this.enabled = enabled;
	}

	/**
	 * Start benchmarking.
	 */
	public void start() {
		if (!enabled) return;
		lastTime = System.currentTimeMillis();
		firstTime = lastTime;
		printString("Starting benchmark");
	}

	/**
	 * This method should be called when a task has been finished.
	 * It will print the time it took to finish this task from the start of the benchmarking.
	 * 
	 * @param taskName the name of the task
	 */
	public void taskFinished(String taskName) {
		if (!enabled) return;
		long timeUsed = System.currentTimeMillis() - lastTime;
		double seconds = timeUsed / 1000.0;
		printString("Task \"" + taskName + "\" finished, time used: " + seconds + " s");
		lastTime = System.currentTimeMillis();
	}

	/**
	 * Stop benchmarking and print the amount of time it ran.
	 */
	public void stop() {
		if (!enabled) return;
		long timeUsed = System.currentTimeMillis() - firstTime;
		double seconds = timeUsed / 1000.0;
		printString("Benchmark finished, total time used: " + seconds + " s");
	}
	
	/**
	 * Prints a message with a formatted time.
	 * 
	 * @param print the text to print
	 */
	private void printString(String print){
		print = "["+new SimpleDateFormat("HH:mm").format(new GregorianCalendar().getTime())+" "+ name + "] "+print;
		System.out.println(print);
	}
}