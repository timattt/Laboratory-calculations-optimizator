/**
 * 
 */
package utils;

import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;

import java.util.Calendar;

/**
 * @author timat
 *
 */
public class LCOConsole {

	private static boolean debug = false;
	private static int currentPriority = 2;
	
	public static enum Priority {
		low, medium, high
	}
	
	private static String createTimeBox() {
		Calendar calendar = Calendar.getInstance();
		return "[" + calendar.get(HOUR_OF_DAY) + ":" + calendar.get(MINUTE) + ":" + calendar.get(SECOND)
				+ ":" + calendar.get(MILLISECOND) + "]";
	}
	
	/**
	 * Prints message if it's priority is bigger or equals to currentPriority.
	 * Default current priority is medium.
	 * @param mes
	 * @param prior : low, medium, high
	 */
	public static void say(String mes, Priority prior) {
		if (prior.ordinal() + 1 < currentPriority) {
			return;
		}
		String toSay = createTimeBox() + ": " + mes;
		System.out.println(toSay);
	}
	
	/**
	 * Says message with medium priority
	 * @param mes
	 */
	public static void say(String mes) {
		say(mes, Priority.medium);
	}
	
	/**
	 * This message is always said if debug is enabled.
	 * You can enable debug using startDebug() function
	 * @param mes
	 */
	public static void debug(String mes) {
		if (debug) {
			say(mes, Priority.high);
		}
	}
	
	/**
	 * Starts debug now debug() function will print your messages.
	 */
	public static void startDebug() {
		debug = true;
	}
	
	/**
	 * Ends debug now debug() function will no print your messages.
	 */
	public static void endDebug() {
		debug = false;
	}
	
	public static void setHighCurrentPriority() {
		currentPriority = 3;
	}
	
	public static void setMediumCurrentPriority() {
		currentPriority = 2;
	}
	
	public static void setLowCurrentPriority() {
		currentPriority = 1;
	}
	
}
