package gallegux.japa.core;


import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;


public class JapaServerConfig
{
	
	// compilation destination
	private final static boolean COMPILE_DISK = true;
	private final static boolean COMPILE_MEMORY = false;
	
	// japa source
	private final static boolean SOURCE_CLASSPATH = true;
	private final static boolean SOURCE_DISK = false;
		
	private static boolean compilationDestination = COMPILE_MEMORY;  // 1 en disco, 0 en memoria
	private static File compiledClassDir = null;

	private static boolean japaSource = SOURCE_CLASSPATH;
	private static String japasDir = null;

	
	
	
	
	public static void setJapaDir(String dir) {
		if (dir == null) {
			japaSource = SOURCE_CLASSPATH;
			japasDir = null;
		}
		else {
			japaSource = SOURCE_DISK;
			japasDir = dir;
		}
	}
	
	
	public static String getJapaDir() {
		return japasDir;
	}
	
	
	public static boolean isJapaSourceFromDisk() {
		return japaSource == SOURCE_DISK;
	}
	
	
	public static boolean isJapaSourceFromClasspath() {
		return japaSource == SOURCE_CLASSPATH;
	}
	
	
	public static void setCompiledClassToDisk() {
		compilationDestination = COMPILE_DISK;
	}
	
	
	public static void setCompiledClassToMemory() {
		compilationDestination = COMPILE_MEMORY;
	}
	
	
	public static boolean isCompiledClassToDisk() {
		return compilationDestination == COMPILE_DISK;
	}
	

	public static boolean isCompiledClassToMemory() {
		return compilationDestination == COMPILE_MEMORY;
	}
	

	
	public static File getCompiledClassDir()
	{
		if (compiledClassDir == null) {
			compiledClassDir = new File( System.getProperty("java.io.tmpdir"), JapaApplicacion.applicationName);
			compiledClassDir.mkdirs();
			Util.clean(compiledClassDir);
			compiledClassDir.mkdirs();
			Logger.getLogger("gallegux.japa.core.JapaServerConfig").info(compiledClassDir.getAbsolutePath());
		}
		return compiledClassDir;
	}


	public static void close()
	{
		if (compilationDestination == COMPILE_DISK) Util.clean(getCompiledClassDir());
	}
	
	
	
	public static void log()
	{
		Logger log = Logger.getLogger("gallegux.japa.core.JapaServerConfig");
		
		log.info( (compilationDestination == COMPILE_DISK)
			? "Compilation destination: DISK " + compiledClassDir
			: "Compilation destination: MEMORY" );

		log.info( (japaSource == SOURCE_CLASSPATH)
				? "Japa files source: CLASSPATH"
				: "Japa files source: DISK " + japasDir ) ;
	}
	

	

}
