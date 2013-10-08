package network.server.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Felix Ekdahl
 * A simpel concurrent logger to log some actions.
 */
public class ActionLogger extends Thread {
	
	/** Will autolog every set time. */
	private static boolean autoLog = true;
	
	/** Will automatically empty the buffer to the file every x seconds */
	private static int logInterval = 5;
	
	/** The destination of the log file. */
	private static final String LOGGER_FILE_DST = "logs/" + System.currentTimeMillis() + ".log";
	
	/** Concurrent queue for logging. */
	private static Queue<String> logsBuffer = new ConcurrentLinkedQueue<String>();
	
	/** Log file containing the information. */
	private static File logFile = new File(LOGGER_FILE_DST);
	
	/** Will create a new directory if none is present. */
	private ActionLogger() {
		File dir = new File("logs/");
		if(!dir.exists()) {
			dir.mkdir();
		}
		start();
	}
	
	/** Initializes the logger, starting a thread that
	 *  automatically checks the buffer and flushes it. */
	public static void initLogger(boolean autoLog, int seconds) {
		ActionLogger.autoLog = autoLog;
		ActionLogger.logInterval = seconds;
		new ActionLogger();
		
		// Sorry for the mess, this is kind of redundant anyway though.
		String initLog = "---------------GENERAL---------------\n" +
				"Date: " + new SimpleDateFormat("hh:ss dd-MM-yyyy")
								.format(Calendar.getInstance().getTime()) + "\n" +
				"OS: " + System.getProperty("os.name") + "\n" +
				"OS Version: " + System.getProperty("os.version") + "\n" +
				"Java Version: " + System.getProperty("java.version") + "\n" +
						   "------------------------------------\n\n";
		
		log(initLog);
	}
	
	@Override
	public void run() {
		CountDownLatch latch = new CountDownLatch(1);
		while(autoLog) {
			try {
				latch.await(logInterval, TimeUnit.SECONDS);
				saveLogs();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/** Empties the buffer to a output file in logs/ */
	public static void saveLogs() {
		if(logsBuffer.isEmpty()) return;
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)));
			String logStr = null;
			while((logStr = logsBuffer.poll()) != null) {
				pw.println(logStr);
			}
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** Appends the text supplied in the parameter to the Output Buffer queue. */
	public static void log(String t) {
		logsBuffer.offer(t);
	}
}
