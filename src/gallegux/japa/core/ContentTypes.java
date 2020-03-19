package gallegux.japa.core;

import java.util.HashMap;



public class ContentTypes 
{

	/** mapeo extendion-content type */
	private static HashMap<String, String> map = null;
	
	static {
		map = new HashMap<>();
		
		// application
		map.put(".json", "application/json");
		map.put(".pdf", "application/pdf");
		map.put(".xhtml", "application/xhtml+xml");
		map.put(".xml", "application/xml");
		map.put(".dtd", "application/xml-dtd");
		map.put(".zip", "application/zip");
		map.put(".gzip", "application/gzip");
		map.put(".js", "application/javascript");
		// audio
		map.put(".mp4", "audio/mp4");
		// image
		map.put(".gif", "image/gif");
		map.put(".jpg", "image/jpeg");
		map.put(".jpeg", "image/jpeg");
		map.put(".png", "image/png");
		map.put(".bmp", "image/bmp");
		map.put(".tiff", "image/tiff");
		// text
		map.put(".css", "text/css");
		map.put(".csv", "text/csv");
		map.put(".html", "text/html");
		map.put(".htm", "text/html");
		map.put(".txt", "text/plain");
		map.put(".rtf", "text/rtf");
		map.put(".xml", "text/xml");
		// video
		map.put(".avi", "video/avi");
		map.put(".mpg", "video/mpeg");
		map.put(".mp4", "video/mp4");
		map.put(".flv", "video/x-flv");
		map.put("", "");
		// vnd
		map.put(".xls", "application/vnd.ms-excel");
		map.put(".ppt", "application/vnd.ms-powerpoint");
		map.put(".odt", "application/vnd.oasis.opendocument.text");
		map.put(".fodt", "application/vnd.oasis.opendocument.text");
		map.put(".ods", "application/vnd.oasis.opendocument.spreadsheet");
		map.put(".fods", "application/vnd.oasis.opendocument.spreadsheet");
		map.put(".odp", "application/vnd.oasis.opendocument.presentation");
		map.put(".fodp", "application/vnd.oasis.opendocument.presentation");
	}
	
	
	public static String getContentType(String ext)
	{
		String contentType = map.get(ext);
		
		if (contentType == null) {
			contentType = "application/octet-stream";
		}
		
		return contentType;
	}
	
	
	public static String getFileContentType(String file)
	{
		try {
			return getContentType( file.substring(file.lastIndexOf('.')) );
		}
		catch (Exception e) {
			return "application/octet-stream";
		}
	}
	
	

}
