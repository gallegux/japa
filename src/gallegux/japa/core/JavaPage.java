package gallegux.japa.core;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;




public abstract class JavaPage
{
	
	protected JapaServerConfig server = null;
	protected JapaApplicacion application = null;
	protected JapaSession session = null;
	protected JapaWriter out = null;
	protected OutputStream binaryOut;
	private int codeStatus = 200;
	private String descStatus = "OK";
	private String contentType = "text/html";
	private boolean headersSent = false;
	private boolean autoFlush = false;
	
	
	
	

	public void setOutputStream(OutputStream os)
	{
		this.binaryOut = os;
		this.out = new JapaBufferedWriter(os); 
	}
	
	
	
	/**
	 * Si el JAPA es de auto flush conviene invocar a este metodo cuanto antes
	 */
	public void setAutoFlush()
	{
		this.autoFlush = true;
		this.out = new JapaDirectWriter(this.binaryOut); 
	}
	
	

	protected void executePage(JapaParams params, JapaHeaders headers)
	{
		
		try {
			execute(params, headers);
		}
		catch (RuntimeException re) {
			if (!autoFlush) {
				this.out.clear();
				
				setStatus(500, "Internal Server Error");
				setContentType("text/plain");
			}
			
			this.out.println(re.toString());
			this.out.println();
			this.out.print("Message: ");
			this.out.println(re.getMessage());
			this.out.println();
			this.out.print("Localized message: ");
			this.out.println(re.getLocalizedMessage());
			this.out.println();
			
			for (StackTraceElement ste: re.getStackTrace()) {
				this.out.println(ste);
			}

		}

	}
	
	
	
	protected abstract void execute(JapaParams params, JapaHeaders headers) throws RuntimeException;
	
	
	
	protected void setStatus(int codeStatus, String descStatus)
	{
		this.codeStatus = codeStatus;
		this.descStatus = descStatus;
	}
	
	

	protected void setContentType(String ct)
	{
		this.contentType = ct;
	}
	
	
	
	/**
	 * Si el JAPA es auto flush, hay que rellenar las cabeceras antes y despues invocar a este metodo.
	 * Si el JAPA es buffered se invocara solo cuanto se termine la pagina
	 */
	protected void sendHeaders()
	{
		PrintWriter pw = new PrintWriter(this.binaryOut);
		
		pw.print("HTTP/1.1 ");
		pw.print(this.codeStatus);
		pw.print(' ');
		pw.println(this.descStatus);
		
		pw.print("Content-Type: ");
		pw.println(this.contentType);
		
		if (this.out.getLength() != -1) {
			pw.print("Content-Length: ");
			pw.println(this.out.getLength());
		}
		
//		pw.println("Connection: close");
		
		pw.println();
		pw.flush();
		
		this.headersSent = true;
	}
	
	
	
	/**
	 * Este metodo lo invoca la clase que crea el objeto JAPA.
	 * @throws IOException
	 */
	protected void flush() throws IOException
	{
		if (!this.headersSent && !autoFlush) {
			this.sendHeaders();
		}
		
		this.out.flush();
	}


	
}
