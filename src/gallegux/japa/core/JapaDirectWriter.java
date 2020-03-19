package gallegux.japa.core;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;


public class JapaDirectWriter implements JapaWriter
{
	
	private PrintWriter printWriter = null;
	
	
	
	public JapaDirectWriter(OutputStream os) {
		this.printWriter = new PrintWriter(os);
	}
	
	
	
	public void print(boolean x) {
		this.printWriter.print(x);
	}

	
	public void print(char x) {
		this.printWriter.print(x);
	}

	
	public void print(double x) {
		this.printWriter.print(x);
	}

	
	public void print(float x) {
		this.printWriter.print(x);
	}

	
	public void print(int x) {
		this.printWriter.print(x);
	}

	
	public void print(long x) {
		this.printWriter.print(x);
	}

	
	public void print(Object x) {
		this.printWriter.print(x);
	}

	
	public void print(String x) {
		this.printWriter.print(x);
	}

	
	public void println(boolean x) {
		this.printWriter.println(x);
	}

	
	public void println(char x) {
		this.printWriter.println(x);
	}

	
	public void println(double x) {
		this.printWriter.println(x);
	}

	
	public void println(float x) {
		this.printWriter.println(x);
	}

	
	public void println(int x) {
		this.printWriter.println(x);
	}

	
	public void println(long x) {
		this.printWriter.println(x);
	}

	
	public void println(Object x) {
		this.printWriter.println(x);
	}

	
	public void println(String x) {
		this.printWriter.println(x);
	}
	
	
	public void println() {
		this.printWriter.println();
	}
	
	
	public void write(byte[] bytes) {
		this.printWriter.println( new String(bytes) );
	}
	
	
	public void write(byte[] bytes, int start, int end) {
		this.printWriter.println( new String(bytes, start, end) );
	}
	

	
	public void flush() throws IOException 
	{
		this.printWriter.flush();
	}


	
	/**
	 * It does nothing
	 */
	public void clear() {
	}
	
	
	
	/**
	 * Retorna -1
	 */
	public int getLength() {
		return -1;
	}
	
	
	
}
