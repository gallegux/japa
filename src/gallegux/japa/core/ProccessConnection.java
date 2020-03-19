package gallegux.japa.core;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;
import java.util.logging.Logger;


public class ProccessConnection extends Thread 
{
	
	public static final int TIMEOUT_MS = 5000;
	public static final int BUFFER_IN_SIZE = 2048;
	
	private Socket socket = null;
	private Logger logger = Logger.getLogger(this.getClass().getName() + "-" + getName());
	
	private static long idConnection = 0;
	
	
	
	public ProccessConnection(Socket socket)
	{
		this.socket = socket;
		
		try {
			this.socket.setSoTimeout(TIMEOUT_MS);
		}
		catch (SocketException e) {
			logger.warning(e.toString());
		}
		
	}
	
	
	
	public void run() 
	{
		OutputStream os = null;
		InputStream is = null;
		byte[] buffer;
		int numBytesLeidos, dosPuntos;
		String httpRequest, linea, headerName, headerValue, metodo, url, version, file;
		String[] httpRequestLines;
		JapaHeaders headers;
		JapaParams params;
		ClientRequest clientRequest;
		boolean waitingHttpRequest;

		logger.info(String.valueOf(++idConnection));

		try {
			os = this.socket.getOutputStream();
			is = this.socket.getInputStream();
		
			waitingHttpRequest = true;

			while (waitingHttpRequest) {
				buffer = new byte[BUFFER_IN_SIZE];
				numBytesLeidos = is.read(buffer); // aqui se lanza SocketTimeoutException
				httpRequest = new String(buffer, 0, numBytesLeidos);
				buffer = null;
				//logger.info(ss);
				httpRequestLines = httpRequest.split("\n");
				httpRequest = null;
				
				
				headers = new JapaHeaders();
				
				for (int n = 1; n < httpRequestLines.length; n++) {
					// nos saltamos la primera linea porque contiene el GET o el POST
					linea = httpRequestLines[n];
					if ( (dosPuntos = linea.indexOf(":")) != -1 ) {
						headerName = linea.substring(0, dosPuntos);
						headerValue = linea.substring(dosPuntos+2).trim();
						headers.set(headerName, headerValue);
					}
				}
				linea = null;
				
	
				httpRequestLines = httpRequestLines[0].split(" ");
				metodo = httpRequestLines[0];
				url = httpRequestLines[1];
				version = httpRequestLines[2];
				httpRequestLines = null;
				
				logger.info(metodo + " " + url + " " + version);
				params = getParameters(url);
				file = getFile(url);		
				
				clientRequest = new ClientRequest(metodo, file, params, headers, os);
				clientRequest.process();
				
				metodo = null;
				file = null;
				params = null;
				clientRequest = null;
				
				waitingHttpRequest = "keep-alive".equals(headers.get("connection"));
				headers = null;
			}
		}
		catch (IOException e1) {
			logger.fine(e1.toString()+" "+this.socket);
		}
		finally {
			if (is != null) {
				try {
					is.close();
				}
				catch (IOException e) {
					logger.fine("InputStream " + e.toString());
				}
			}

			if (os != null) {
				try {
					os.close();
				}
				catch (IOException e) {
					logger.fine("OutputStream " + e.toString());
				}
			}
			
			try {
				this.socket.close();
			}
			catch (IOException ioe) {
				logger.fine("Error en socket.close()");
			}
		}
	}
	
	
	
	
	private String getFile(String url)
	{
		int fin = url.indexOf('?');
		
		if (fin == -1) {
			return url;
		}
		else {
			return url.substring(0, fin);
		}
	}
	
	
	
	private JapaParams getParameters(String url)
	{
		JapaParams params = new JapaParams();

		if (url.indexOf('?') == -1) {
			return params;
		}
		
		url = url.substring( url.indexOf('?')+1 );
		logger.info("url: " + url);

		String[] aux;
		String[] parametrosCodificados;
		String param, value;

		parametrosCodificados = url.split("\\&");
		
		for (String parametro: parametrosCodificados) {
			logger.info(parametro);
			aux = parametro.split("\\=");
			param = decodePercent(aux[0]);
			value = (aux.length == 2) ? decodePercent(aux[1]) : "";
			logger.info(param+" = "+value);
			params.put(param, value);
		}
     
		return params;
	}
	
	
	
	public String decodePercent(String s)
	{
		String decoded = null;
		
		try {
		    decoded = URLDecoder.decode(s, "UTF8");
		} 
		catch (UnsupportedEncodingException ignored) {
			logger.warning("Encoding not supported, ignored " + ignored);
		}
		return decoded;
	}
	

}
