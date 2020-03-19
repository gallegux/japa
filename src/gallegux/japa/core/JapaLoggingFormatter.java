package gallegux.japa.core;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class JapaLoggingFormatter extends Formatter 
{

	public String format(LogRecord logRecord)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(logRecord.getThreadID());
		sb.append(' ');
		sb.append(logRecord.getLevel());
		sb.append(' ');
		sb.append(logRecord.getSourceClassName());
		sb.append('_');
		sb.append(logRecord.getSourceMethodName());
		sb.append('(');
		sb.append(arrayToString(logRecord.getParameters()));
		sb.append(") ");
		sb.append(logRecord.getMessage());
		sb.append('\n');
		
		return sb.toString();
	}
	
	
	private String arrayToString(Object...objects)
	{
		String text = null;
		
		try {
			int size = objects.length;
			StringBuilder sb = new StringBuilder();
			
			for (int i = 0; i < size; i++) {
				if (objects[i] != null) {
					sb.append(objects[i].toString());
				}
				else {
					sb.append("null");
				}
				
				if (i+1 < size) {
					sb.append(',');
				}
			}
			text = sb.toString();
		}
		catch (Exception e) {
			text = "";
		}
		
		return text;		
	}
	
	
}
