package gallegux.japa.core;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;


public class ResponseUtil 
{
	
	private final static long DAY_MILLISECONDS = 24L*60L*60L*1000L;
	private final static long DAY_SECONDS = 24L*60L*60L;
	
	
	
	
	
	private static String getDate(long sumarDias)
	{
		String RFC1123_PATTERN =  "EEE, dd MMM yyyy HH:mm:ss z";
		TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");
		DateFormat rfc1123Format = new SimpleDateFormat(RFC1123_PATTERN, Locale.US);
		rfc1123Format.setTimeZone(GMT_ZONE);
		
		String RFC1123_DATE_PATTERN = "EEE, dd MMM yyyy HH:mm:ss zzz";
	    SimpleDateFormat dateFormat = new SimpleDateFormat(RFC1123_DATE_PATTERN);
		Date date = new Date(System.currentTimeMillis() + sumarDias * DAY_MILLISECONDS);

		return rfc1123Format.format(date);
	}
	
	
	public static void sendRespondeHeader(int codigo, OutputStream os, String contentType, int length, String file)
	{
		PrintWriter pw = new PrintWriter(os);
		
		pw.print("HTTP/1.1 ");
		pw.print(codigo);
		pw.println(" OK");
		
		if (contentType != null) {
			pw.print("Content-Type: ");
			pw.println(contentType);
		}
		
		if (length > 0) {
			pw.print("Content-Length: ");
			pw.println(length);
		}
		
		pw.print("ETag: ");
		pw.println(file.hashCode());
		pw.print("Last-Modified: ");
		pw.println(getDate(0));

		pw.println("Connection: close");

		pw.println();
		
		pw.flush();
	}
	
	
	
	public static void sendNotFound(OutputStream os, String path)
	{
		PrintWriter pw = new PrintWriter(os);
		
		pw.println("HTTP/1.1 404 Not Found");
		pw.println("Content-Type: text/plain");
		pw.println("Connection: close");
		pw.println();
		pw.println("HTTP Error 404: Not Found");
		pw.println();
		pw.println(path);
		
		pw.flush();
	}
	
	
	
	public static void sendJapaError(OutputStream os, Exception e)
	{
		PrintWriter w = new PrintWriter(os);
		
		w.println("HTTP/1.1 500 Internal Server Error");
		w.println();
		w.println("HTTP Error 500: Internal Server Error");
		w.println();
		w.println(e.getMessage());
		w.println();
		w.println(e.getLocalizedMessage());
		w.println();
		
		for (StackTraceElement s: e.getStackTrace()) {
			w.println(s.toString());
		}
		
		w.flush();
	}
	

	
}
