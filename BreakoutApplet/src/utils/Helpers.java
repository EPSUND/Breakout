package utils;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/***
 * A utility class containing static help methods
 * @author Erik
 *
 */
public class Helpers {
	
	/***
	 * Gets the URL of a resource
	 * @param o An object used to get the class path
	 * @param resource The resource
	 * @return The URL as an URL object
	 */
	public static URL getResourceURL(Object o, String resource)
	{
		return o.getClass().getClassLoader().getResource(resource);
	}
	
	/***
	 * Gets the URI of a resource as a string
	 * @param o An object used to get the class path
	 * @param resource The resource
	 * @return The URI of the resource expressed as a string
	 */
	public static String getResourceURIString(Object o, String resource)
	{
		try {
			return getResourceURL(o, resource).toURI().toString();
		} catch (URISyntaxException e) {
			System.err.println("Breakout: URL malformed");
			e.printStackTrace();
			return "";
		}
	}
	
	/***
	 * Get the stack trace of an exception as a string
	 * @param e The exception
	 * @return The stack trace as a string
	 */
	public static String getStackTraceString(Exception e)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString(); // stack trace as a string
	}
	
	/**
	 * Gets the current time as a string in UTC
	 * @return The current time(in UTC) as a string
	 */
	public static String getCurrentTimeUTC()
	{
		SimpleDateFormat dateFormatUtc = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		dateFormatUtc.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormatUtc.format(new Date());
	}
	
	/**
	 * Transforms a date(in UTC) to local time
	 * @param utcDate A date in UTC 
	 * @return The date in local time as a string
	 */
	public static String utcToLocalTime(String utcDate)
	{
		try {
			SimpleDateFormat dateFormatUtc = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			dateFormatUtc.setTimeZone(TimeZone.getTimeZone("UTC"));
			Date date = dateFormatUtc.parse(utcDate);
			
			SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			return dateFormatLocal.format(date);
		} catch (ParseException e) {
			System.err.println("Columns: Could not parse date");
			e.printStackTrace();
		}
		
		return "";
	}
}
