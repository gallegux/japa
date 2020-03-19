package gallegux.java.test;


import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.net.URL;
import java.io.File;
import java.io.FileWriter;

/**
 * Probaremos:
 *  1. Compilaci�n est�ndar: desde y hacia el sistema de archivos.
 *  2. Compilaci�n din�mica: c�digo fuente en memoria y bytechodes hacia el sistema de archivos.
 *  3. Compilaci�n en memoria: c�digo fuente en memoria y bytecodes en memoria
 * 
 * En cada caso se probar� tambi�n la ejecuci�n din�mica.
 * 
 * Tambi�n comprobamos como la compilaci�n y ejecuci�n funciona para clases internas.
 * 
 * @author Antonio S�nchez
 */
public class ElCompiladorTest {

    static final File DIR_PRUEBAS = new File("pruebas");
    static final File DIR_FUENTE = new File(DIR_PRUEBAS, "src");
    static final String NOMBRE_PAQUETE = "paquete";
    static final File DIR_CLASES = new File(DIR_PRUEBAS, "classes");
    
    static final String NOMBRE_CLASE = "Clase";
    static final String NOMBRE_COMPLETO_CLASE = NOMBRE_PAQUETE + '.' + NOMBRE_CLASE; // "paquete.Clase"
    static final String NOMBRE_CLASE_INTERNA = "Clase$Interna";
    static final String NOMBRE_COMPLETO_CLASE_INTERNA = NOMBRE_PAQUETE + '.' + NOMBRE_CLASE_INTERNA; // "paquete.Clase.Interna"
    static final String NOMBRE_M�TODO = "m�todo";
    static final String NOMBRE_M�TODO_INTERNO = "interno";
    static final File RUTA_FUENTE = new File(DIR_FUENTE, NOMBRE_PAQUETE + File.separatorChar + NOMBRE_CLASE + ".java"); // "pruebas/src/paquete/Clase.java"
    static final File RUTA_CLASE = new File(DIR_CLASES, NOMBRE_PAQUETE + File.separatorChar + NOMBRE_CLASE + ".class"); // "pruebas/classes/paquete/Clase.class"
    static final File RUTA_CLASE_INTERNA = new File(DIR_CLASES, NOMBRE_PAQUETE + File.separatorChar + NOMBRE_CLASE_INTERNA + ".class"); // "pruebas/classes/paquete/Clase$Interna.class"
    static final String RESPUESTA = "ESTA ES LA RESPUESTA DEL M�TODO.";
    static final String RESPUESTA_INTERNA = "ESTA ES LA RESPUESTA DEL M�TODO INTERNO.";
    static final String C�DIGO_FUENTE =
            "package paquete; \n"
            + "\n"
            + "public class Clase {\n"
            + "    public String m�todo() {\n"
            + "        System.out.println(\"Ejecutando Clase.m�todo()\");  \n"
            + "        return  \"" + RESPUESTA + "\"; \n"
            + "    }\n"
            + "\n"
            + "    public static class Interna {\n"
            + "        public String interno() {    \n"
            + "            System.out.println(\"Ejecutando Clase$Interna.interno()\"); \n"
            + "            return  \"" + RESPUESTA_INTERNA + "\"; \n"
            + "        }\n"
            + "    }\n"
            + "}\n";
    
    private ElCompilador compilador;

    public ElCompiladorTest() {
    	compilador = new ElCompilador();
    }

    
    public static void setUpClass() throws Exception {
        //creamos c�digo fuente en el sistema de archivos
        RUTA_FUENTE.getParentFile().mkdirs();
        FileWriter fw = new FileWriter(RUTA_FUENTE);
        fw.write(C�DIGO_FUENTE);
        fw.close();
    }

   
    public static void tearDownClass() throws Exception {
        limpiarDir(DIR_PRUEBAS); //limpiamos lo creado en el sistema de archivos
    }

    static boolean limpiarDir(File dir) {
        if (dir.isDirectory()) {
            String[] hijos = dir.list();
            for (int i = 0; i < hijos.length; i++) {
                if (!limpiarDir(new File(dir, hijos[i]))) 
                    return false;
            }
        }

        return dir.delete();
    }


    public void setUp() throws MalformedURLException {
        compilador = new ElCompilador();
        DIR_CLASES.mkdirs();
    }


    public void tearDown() {
        limpiarDir(DIR_CLASES); //nos aseguramos de que no haya ning�n .class
    }

    /**
     * Del sistema de archivos al sistema de archivos
     */

    public void testCompilarDiscoADisco() throws Exception {
        System.out.println("PROBANDO testCompilarEst�tico() - Del sistema de archivos al sistema de archivos");


        //con esto decimos que queremos bytecodes en sta. archivos
        compilador.setClassOutputLocation(DIR_CLASES); 
        //manejamos opciones de compilaci�n, en este caso d�nde debe buscar fuentes
        compilador.setOpciones("-sourcepath", DIR_FUENTE.getPath());
        boolean resultado = compilador.compilar(RUTA_FUENTE);
        
        //probamos la ejecuci�n din�mica
        ClassLoader cargador = new URLClassLoader(new URL[] {DIR_CLASES.toURI().toURL()});
        testEjecutar(cargador);
        
        System.out.println("------------------ FIN DE PRUEBA testCompilarEst�tico()");
        System.out.println();

    }

    /**
     * De memoria al sistema de archivos
     */
    public void testCompilarMemoriaADisco() throws Exception {
        System.out.println("PROBANDO testCompilarDin�mico() - De memoria al sistema de archivos");
        
        //nos aseguramos que a�n NO existen los .class
        assert(RUTA_CLASE.exists()); 
        assert(RUTA_CLASE_INTERNA.exists()); 
        
        //con esto decimos que queremos bytecodes en sta. archivos
        compilador.setClassOutputLocation(DIR_CLASES);
        compilador.compilar(NOMBRE_COMPLETO_CLASE, C�DIGO_FUENTE);
        boolean resultado = compilador.compilar(RUTA_FUENTE);
        
        assert(resultado);
        //se han creado el .class en el lugar esperado
        assert(RUTA_CLASE.exists()); 
        assert(RUTA_CLASE_INTERNA.exists()); 
        
        //probamos la ejecuci�n din�mica
        testEjecutar(new URLClassLoader(new URL[] {DIR_CLASES.toURI().toURL()}));
        
        System.out.println("------------------ FIN DE PRUEBA testCompilarDin�mico()");
        System.out.println();
    }

    /**
     * De memoria a memoria
     */    
    public void testCompilarMemoriaAMemoria() throws Exception {
        System.out.println("PROBANDO testCompilarMemoria() - De memoria a memoria");
           
        boolean resultado = compilador.compilar(NOMBRE_COMPLETO_CLASE, C�DIGO_FUENTE);
        
        //probamos la ejecuci�n din�mica
        try {
            testEjecutar(new URLClassLoader(new URL[] {DIR_CLASES.toURI().toURL()}));
            System.out.println("try ok");
        } 
        catch (ClassNotFoundException ex) {
        	System.out.println("try falla");
        }
        
        //probamos la ejecuci�n din�mica
        testEjecutar(compilador.getCargadorMemoria());
        
        System.out.println("------------------ FIN DE PRUEBA testCompilarMemoria()");
        System.out.println();
    }
    
    
    public void testCompilarDiscoAMemoria() throws Exception {
        System.out.println("PROBANDO  - De disco a memoria");
   
        boolean resultado = compilador.compilar(NOMBRE_COMPLETO_CLASE, C�DIGO_FUENTE);
        
         //probamos la ejecuci�n din�mica
        try {
            testEjecutar(new URLClassLoader(new URL[] {DIR_CLASES.toURI().toURL()}));
            System.out.println("No se ha producido la ClassNotFoundException esperada.");
        } catch (ClassNotFoundException ex) {
            //no hacer nada
        }
        
        //probamos la ejecuci�n din�mica
        testEjecutar(compilador.getCargadorMemoria());
        
        System.out.println("------------------ FIN DE PRUEBA testCompilarMemoria()");
        System.out.println();
    }
    
    
    /**
     * Probar la ejecuci�n din�mica
     */
    private void testEjecutar(ClassLoader cargador) throws Exception {
        Class clase = null;
        Object respuesta = null;
        
        //ejecuci�n din�mica de clase principal
        clase = cargador.loadClass(NOMBRE_COMPLETO_CLASE);
        respuesta = clase.getMethod(NOMBRE_M�TODO).invoke(clase.newInstance());
                
        //ejecuci�n din�mica de clase interna
        clase = cargador.loadClass(NOMBRE_COMPLETO_CLASE_INTERNA);
        respuesta = clase.getMethod(NOMBRE_M�TODO_INTERNO).invoke(clase.newInstance());
        
     }
    
    
    
    public static void main(String...arg) throws Exception
    {
    	ElCompiladorTest c = new ElCompiladorTest();
    	DIR_CLASES.mkdirs();
    	c.testCompilarMemoriaAMemoria();
//    	c.testCompilarMemoriaADisco();
//    	c.testCompilarDiscoADisco();
//    	c.testCompilarDiscoAMemoria();
    }
    
}
