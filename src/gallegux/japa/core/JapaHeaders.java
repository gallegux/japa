package gallegux.japa.core;

import java.util.HashMap;


public class JapaHeaders
{
	
	private HashMap<String, String> map = new HashMap<>();
	
	
	public void set(String k, String v)
	{
		map.put(k.toLowerCase(), v);
	}
	
	
	public String get(String k)
	{
		return map.get(k.toLowerCase());
	}
	
	
	public boolean has(String k)
	{
		return map.containsKey(k.toLowerCase());
	}
	
	
}
