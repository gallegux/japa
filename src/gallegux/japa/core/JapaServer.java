package gallegux.japa.core;



import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;



public class JapaServer extends Thread
{

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private int port = 9999;
	private boolean terminar = false;
	
	
	
	public JapaServer(int port, String applicationName)
	{
		this.port = port;
		JapaApplicacion.applicationName = applicationName;
		logger.finer("hola");
	}
	
	
	
	public void run()
	{
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			logger.info("port " + port);
			
			while (!terminar) {
				new ProccessConnection(serverSocket.accept()).start();
			}
		}
		catch (IOException e) {
			logger.warning(e.toString());;
		}
	}
	
	
	
	public void close()
	{
		this.terminar = true;
		JapaServerConfig.close();
	}
	
	
	
	
	
	
	
//	public static void main(String...arg)
//	{
//		try {
//			// http://tutorials.jenkov.com/java-logging/configuration.html
//			FileInputStream fis = new FileInputStream("logging.config");
//			LogManager.getLogManager().readConfiguration(fis);
//			
//			JapaServer server = new JapaServer(9999, "testing");
//			server.run();
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	

}
