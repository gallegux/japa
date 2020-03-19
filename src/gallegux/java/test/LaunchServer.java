package gallegux.java.test;

import java.io.FileInputStream;
import java.util.logging.LogManager;

import gallegux.japa.core.JapaServer;
import gallegux.japa.core.JapaServerConfig;



public class LaunchServer 
{
	
	
	public static void main(String...arg)
	{
		try {
			FileInputStream fis = new FileInputStream("logging.config");
			LogManager.getLogManager().readConfiguration(fis);
			
			JapaServerConfig.log();
			
			JapaServer server = new JapaServer(9999, "testing");
			server.run();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
