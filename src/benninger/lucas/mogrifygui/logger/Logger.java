package benninger.lucas.mogrifygui.logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	static String logString;
	private static String logText = "";

	public void log(String arg) {
		//TODO:
		//	->  Some sort of filtering for different log files

		Date date = new Date();
		logString = dateFormat.format(date) + ": " + arg;
		System.out.println(logString);
		logText = logText + "\n" + arg;

	}
	
	public void logf(String arg){
		Date date = new Date();
		logString = dateFormat.format(date) + ": " + arg;
		System.out.printf(logString);
		logText = logText + arg;
	}

	public void dumpLog(String arg, String file) {
		//Write to log file
		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
			out.println(logText);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
