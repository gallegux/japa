package gallegux.japa.core;


import java.io.IOException;


public interface JapaWriter 
{
	

	public abstract void print(boolean x);

	
	public abstract void print(char x);

	
	public abstract void print(double x);

	
	public abstract void print(float x);

	
	public abstract void print(int x);

	
	public abstract void print(long x);

	
	public abstract void print(Object x);

	
	public abstract void print(String x);

	
	public abstract void println(boolean x);

	
	public abstract void println(char x);

	
	public abstract void println(double x);

	
	public abstract void println(float x);

	
	public abstract void println(int x);

	
	public abstract void println(long x);

	
	public abstract void println(Object x);

	
	public abstract void println(String x);
	
	
	public abstract void println();
	
	
	public abstract void write(byte[] bytes);
	
	
	public abstract void write(byte[] bytes, int start, int end);
	
	
	public abstract void flush() throws IOException ;

	
	public abstract void clear();
	
	
	public abstract int getLength();
	

}
