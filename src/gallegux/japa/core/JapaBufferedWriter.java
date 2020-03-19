package gallegux.japa.core;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;


public class JapaBufferedWriter implements JapaWriter
{
	
	private OutputStream outputStream = null;
	private StringBuilder buffer = null;
	
	
	public JapaBufferedWriter(OutputStream os) {
		this.buffer = new StringBuilder();
		this.outputStream = os;
	}
	
	
	
	public void print(boolean x) {
		this.buffer.append(x);
	}

	
	public void print(char x) {
		this.buffer.append(x);
	}

	
	public void print(double x) {
		this.buffer.append(x);
	}

	
	public void print(float x) {
		this.buffer.append(x);
	}

	
	public void print(int x) {
		this.buffer.append(x);
	}

	
	public void print(long x) {
		this.buffer.append(x);
	}

	
	public void print(Object x) {
		this.buffer.append(x);
	}

	
	public void print(String x) {
		this.buffer.append(x);
	}

	
	public void println(boolean x) {
		this.buffer.append(x);
		this.println();
	}

	
	public void println(char x) {
		this.buffer.append(x);
		this.println();
	}

	
	public void println(double x) {
		this.buffer.append(x);
		this.println();
	}

	
	public void println(float x) {
		this.buffer.append(x);
		this.println();
	}

	
	public void println(int x) {
		this.buffer.append(x);
		this.println();
	}

	
	public void println(long x) {
		this.buffer.append(x);
		this.println();
	}

	
	public void println(Object x) {
		this.buffer.append(x);
		this.println();
	}

	
	public void println(String x) {
		this.buffer.append(x);
		this.println();
	}
	
	
	public void println() {
		this.buffer.append('\n');
	}
	
	
	public void write(byte[] bytes) {
		this.buffer.append( new String(bytes) );
	}
	
	
	public void write(byte[] bytes, int start, int end) {
		this.buffer.append( new String(bytes, start, end) );
	}
	

	
	public void flush() throws IOException 
	{
		this.outputStream.write(this.buffer.toString().getBytes());
		this.outputStream.flush();
		this.clear();
	}


	
	public void clear() {
		this.buffer.delete(0, this.buffer.length() );
	}
	
	
	
	public int getLength() {
		return this.buffer.length();
	}
	
	
	
}
