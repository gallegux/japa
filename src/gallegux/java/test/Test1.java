package gallegux.java.test;

import javax.tools.*;


public class Test1 {

	public static void main(String...arg)
	{
		String SRC = "public class MiClase{public static void main(String...arg){System.out.println(\"Hola mundo\");}}";
		String fuenteJava = /*"prueba" + java.io.File.separator +*/ "D:/java/java-src/japa/MiClase.java";
		JavaCompiler compilador = ToolProvider.getSystemJavaCompiler();
		
		StandardJavaFileManager fm = compilador.getStandardFileManager(null, null, null);
		int resultado = compilador.run(null, null, null, fuenteJava);
		
		System.out.println(resultado);
	}
}
