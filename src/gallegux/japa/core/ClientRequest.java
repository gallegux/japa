package gallegux.japa.core;


import gallegux.japa.compiler.JavaSourceCompiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.tools.Diagnostic;



public class ClientRequest 
{
	
	private String method, paquete, clase, file, qName;
	private OutputStream os;
	private JapaParams params;
	private JapaHeaders headers;
	private static HashMap<String, Long> map_japaFile_date = null;
	
	private Logger logger = Logger.getLogger(getClass().getName());
	
	private static long idRequest = 0;
	

	
	public ClientRequest(String metodo, String file, JapaParams params, JapaHeaders headers, OutputStream os)
	{
		this.method = metodo;
		this.os = os;
		this.file = file;
		this.params = params;
		this.headers = headers;
		
		logger.info(String.valueOf(++idRequest));
	}
	
	
	
	public void process()// throws IOException
	{
		try {
			if (this.file.endsWith(".class")) {
				obtenerPaqueteClase();
				// los class estan en el classpath
				processClassRequest(os, this.qName);
			}
			else if (this.file.endsWith(".japa")) {
				obtenerPaqueteClase();
				processJapaRequest(os);
			}
			else {	
				// contenido estatico
				processStaticContentRequest(this.file, os);
			}
			os.flush();
			//os.close();
		}
		catch (IOException e) {
			logger.severe(e.toString());
		}

	}
		
		

	
	
	
	private void processStaticContentRequest(String url, OutputStream os)
	{
		try {
			if (this.headers.has("if-modified-since")) {
				logger.info("HTTP 304 Not Modified");
				sendNotModified304(os);
			}
			else {
				InputStream is = this.getClass().getResourceAsStream(url);
				if (is == null) {
					ResponseUtil.sendNotFound(os, this.file);
				}
				else {
					String contentType = ContentTypes.getFileContentType(this.file);
					logger.info("Copiando fichero al OutputStream");
					ResponseUtil.sendRespondeHeader(200, os, contentType, -1, this.file);
					copiar(is, os);		
					is.close();
				}
			}
		}
		catch (IOException ioe) {
			logger.warning(ioe.toString());
		}
	}
	
	
	
	private void processClassRequest(OutputStream os, String qName) 
	{
		try {
			Class clazz = Class.forName(qName);
			executeJapa(clazz, os);
		}
		catch (ClassNotFoundException e1) {
			ResponseUtil.sendNotFound(os, this.file);
		}
		catch (InstantiationException e2) {
			sendError500(os, e2);
		}
		catch (IllegalAccessException e3) {
			sendError500(os, e3);
		}
		catch (IOException e4) {
			logger.info(e4.toString());
		}
	}
	
	
	
	private void executeJapa(Class clazz, OutputStream os) 
			throws InstantiationException, IllegalAccessException, IOException
	{
		JavaPage japa = (JavaPage) clazz.newInstance();
		japa.setOutputStream(os);
		japa.executePage(this.params, this.headers);
		japa.flush();
	}
	
	
	
	private Class obtainCompiledJava(String qName)
	{
		Class clz = null;
		
		try {
			if (JapaServerConfig.isCompiledClassToDisk()) {
				ClassLoader cl = new URLClassLoader(new URL[] {JapaServerConfig.getCompiledClassDir().toURI().toURL()});
				clz = cl.loadClass(qName);
			}
			else {
				// JapaServerConfig.compilationDestination == JapaServerConfig.COMPILE_MEMORY
				clz = JavaSourceCompiler.getInstance().getClassLoader().loadClass(qName);
			}
		}
		catch (ClassNotFoundException e1) {
			logger.info(e1.toString());
		} 
		catch (MalformedURLException e2) {
			logger.severe(e2.toString());
		}
		
		return clz;
	}
	
	
	
	/**
	 * 1. obtener el class del class loader
	 * 2. si existe el class ejecutarlo (fin)
	 * 3. si no esta en el class loader, leer el japa
	 * 4. compilar el japa
	 * 5. compilar el java
	 * 6. ejecutar el class
	 * @throws IOException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	private void processJapaRequest(OutputStream os) 
	{
		// 1
		Class clz = null;
		
		if (JapaServerConfig.isJapaSourceFromDisk()) {
			// estamos en modo de desarrollo, entonces tenemos que mirar si el .japa ha cambiado
			File japaFile = new File(JapaServerConfig.getJapaDir() + this.file);
			long fechaAnt = getDate(this.file);
			long fechaAct = japaFile.lastModified();
			
			if (fechaAnt == fechaAct) {		
				logger.finest("el .japa no ha cambiado");
				clz = obtainCompiledJava(this.qName);
			}
			else {
				logger.finest("el .japa ha cambiado");
				map_japaFile_date.put(this.file, fechaAct);
			}
		}
		else {
			clz = obtainCompiledJava(this.qName);
		}
		
		// 3
		if (clz == null) {
			String japa = null, java = null;
			JavaSourceCompiler javaCompiler = JavaSourceCompiler.getInstance();
			
			try {
				japa = Util.obtainContent(this.file);
			}
			catch (FileNotFoundException ef) {
				logger.warning(ef.toString());
			}
//			try {
//				InputStream is = (JapaServerConfig.isJapaSourceFromClasspath())
//						? this.getClass().getResourceAsStream(this.file)
//						: new FileInputStream(JapaServerConfig.getJapaDir() + this.file);
//				
//				if (is != null) {
//					japa = Util.obtainTextFile(is);
//					try { 
//						is.close(); 
//					}
//					catch (IOException ioe) {
//						logger.warning(ioe.toString());
//					}
//					is = null;
//				}
//			}
//			catch (FileNotFoundException e) {
//			}
			
			if (japa == null) {
				// no existe
				ResponseUtil.sendNotFound(os, this.file);
				return;
			}
			
			// 4.1 preparar
			japa = JapaCompiler.insertIncludes(japa, Util.getDirectory(this.file));
			
			// 4.2 compilar
			java = new JapaCompiler().compile(paquete, clase, japa);
			
			// 5
			if (JapaServerConfig.isCompiledClassToDisk()) {
				try {
					javaCompiler.setClassOutputLocation(JapaServerConfig.getCompiledClassDir());
				}
				catch (IOException ioe1) {
					logger.warning("error en setClassOutputLocation(), posiblemente se compile en memoria");
				}
			}
			
			if (JavaSourceCompiler.getInstance().compilar(this.qName, java)) {
				clz = obtainCompiledJava(this.qName);
			}
			else {
				sendCompilationErrors(JavaSourceCompiler.getInstance(), os);
			}
		}
		
		// 2
		if (clz != null) {
			try {
				// 6
				executeJapa(clz, os);
			}
			catch (Exception e) {
				logger.warning(e.toString());
			} 
		}

	}
	
	
	
	private long getDate(String japaFile)
	{
		if (map_japaFile_date == null) {
			map_japaFile_date = new HashMap<>();
		}
		
		Long date = map_japaFile_date.get(japaFile);
		
		return (date == null) ? -1 : date.longValue();
	}
	
	

	
	private void copiar(InputStream is, OutputStream os) throws IOException, NullPointerException
	{
		int n = 0;
		byte[] buffer = new byte[1024 * 16];
		
		while ( (n = is.read(buffer)) != -1 ) {
			os.write(buffer, 0, n);
			os.flush();
		}
	}
	
	
	
	
	private void sendCompilationErrors(JavaSourceCompiler compiler, OutputStream os)
	{
		PrintWriter w = new PrintWriter(os);
		
		w.println("HTTP/1.1 500 Internal Server Error");
		w.println("Content-Type: text/plain");
//		w.println("Connection: close");
		w.println();
		w.println("HTTP Error 500: Internal Server Error");
		w.println();

		for (Diagnostic d: compiler.getDiagnostics().getDiagnostics()) {
			w.println(d);
		}
		
		w.flush();
	}
	
	
	
	private static void sendError500(OutputStream os, Exception e)
	{
		PrintWriter w = new PrintWriter(os);
		
		w.println("HTTP/1.1 500 Internal Server Error");
		w.println("Content-Type: text/plain");
//		w.println("Connection: close");
		w.println();
		w.println("HTTP Error 500: Internal Server Error");
		w.println();
		w.println(e.getMessage());
		w.println();
		w.println(e.getLocalizedMessage());
		w.println();
		
		for (StackTraceElement ste: e.getStackTrace()) {
			w.println(ste);
		}
		
		w.flush();
	}
	
	
	
	private static void sendNotModified304(OutputStream os)
	{
		PrintWriter w = new PrintWriter(os);
		
		w.println("HTTP/1.1 304 Not Modified");
//		w.println("Connection: close");
		w.println();
		
		w.flush();
	}
	
	
	
	private void obtenerPaqueteClase()
	{
		String japaFile = this.file.substring(1).replace('-', '_');
		int barra = japaFile.lastIndexOf('/');
		int punto = japaFile.lastIndexOf('.');
		
		if (barra == -1) {
			this.paquete = null;
			this.clase = japaFile.substring(0, punto);
			this.qName = this.clase;
		}
		else {
			this.paquete = japaFile.substring(0, barra).replace('/', '.');
			this.clase = japaFile.substring(barra+1, punto);
			this.qName = paquete + "." + clase;
		}
	}
	
	
	
	
	
	
}
