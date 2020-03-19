package gallegux.japa.core;

import java.util.HashMap;



public class JapaSession extends HashMap<String, String>
{
	
	private String sessionId = null;
	private String clientAddress = null;
	
	
	public String getSessionId()
	{
		return this.sessionId;
	}
	
	
	
	public String getClientAddress() 
	{
		return this.clientAddress;
	}
	

}
