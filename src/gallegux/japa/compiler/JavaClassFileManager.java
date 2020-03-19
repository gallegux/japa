/*
 * Este documento es parte del proyecto 'Juntando Líneas': 
 * https://code.google.com/p/juntando-lineas/
 * 
 * Copyright (C) 2011 Antonio Sánchez - Todos los derechos reservados
 * 
 * 'Juntando Líneas' es software libre: Ud. puede redistribuirlo y/o modificarlo 
 * bajo los términos de la Licencia Pública General de GNU tal como está publicada 
 * por la Fundación para el Software Libre, ya sea la versión 3 de la Licencia, o 
 * (a su elección) cualquier versión posterior.
 * 
 * 'Juntando Líneas' se distribuye con la esperanza de que sea útil, pero 
 * SIN GARANTÍA ALGUNA; ni siquiera la garantía implícita MERCANTIL o de 
 * APTITUD PARA UN PROPÓSITO DETERMINADO. Consulte los detalles de la 
 * Licencia Pública General GNU para obtener una información más detallada. 
 * 
 * Debería haber recibido una copia de la Licencia Pública General GNU junto a 
 * 'Juntando Líneas'. En caso contrario, consulte <http://www.gnu.org/licenses/>.
 * 
 * @author Antonio Sánchez
 */

package gallegux.japa.compiler;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;

/**
 * JavaFileManager que administra código fuente y bytecodes en memoria, además
 * de en el sistema de archivos.
 * 
 * Para cargar las clases compiladas en memoria con esta instancia hacerlo 
 * a través de 'getClassLoader(null)'.
 * 
 * Para pasar código fuente en memoria hacerlo a través de
 *  'JLJavaFileManager.crearFuenteEnMemoria'.
 * 
 * Para escribir bytecodes en sistema de archivos de debe cumplir en el FileManager subyacente:
 *    fileManager.getLocation(StandardLocation.CLASS_OUTPUT) != null;
 */
class JavaClassFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> 
{
    //el cargador de clases en memoria
    JavaCompiledClassLoader cargador; 
    
    //aquí se irán guardando los bytecodes compilados asociándolos al correspondiente nombre 
    //cualificado de la clase que representan
    Map<String, byte[]> bytecodesEnMemoria = new HashMap<String, byte[]>();

    
    public JavaClassFileManager(StandardJavaFileManager fileManager) {
        this(fileManager, null);
    }

    
    /**
     * @param cargador el ClassLoader padre para el ClassLoader en memoria de esta instancia
     */
    public JavaClassFileManager(StandardJavaFileManager fileManager, ClassLoader cargador) 
    {
        super(fileManager);
        
        //utilizamos como ClassLoader padre aquel desde el cual se invoca el constructor        
        if (cargador == null)
            cargador = getClass().getClassLoader();
        
        this.cargador = new JavaCompiledClassLoader(cargador);
    }

    
    
    /**
     * Crea un java fuente en memoria con el código fuente suministrado para que pueda ser compilado.
     */
    static JavaFileObject crearFuenteEnMemoria(String nombreCualificado, CharSequence javaFuente) 
    {
        return new FuenteEnMemoria(nombreCualificado, javaFuente);
    }
    
    

    /**
     * @return un JavaFileObject para escribir en memoria los bytecodes resultantes de la compilación.
     */
    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String qualifiedName, Kind kind, FileObject sibling) 
    		throws IOException 
    {
    /* Para lograr la compilación en memoria necesitamos la anulación de este método para 
     * que en lugar de escribir en un JavaFileObject que escribirá en el sistema de archivos 
     * (comportamiento por defecto) lo que haga sea devolver un JavaFileObject que lo que 
     * haga sea escribir los bytes en memoria de tal forma que en coordinación con esta clase
     * sea posible la carga de esos bytecodes, lo cual en este caso se hará mediante un
     * ClassLoader específico.
     */
        //según lo especificado por esta clase esta es la condición para escribir en memoria
        if (fileManager.getLocation(StandardLocation.CLASS_OUTPUT) == null) {
            //devolvemos un JavaFileObject que sabe escribir bytes en memoria
            return new BytesEnMemoria(qualifiedName);
        } else {
            //en otro caso hacemos la implementación por defecto, que escribe al sistema de 
            //archivos, y concretamente a la carpeta especificada por 
            return fileManager.getJavaFileForOutput(location, qualifiedName, kind, sibling);
        }
    }

    
    
    /**
     * @return el cargador de clases para las clases compiladas en memoria con esta instancia.
     */
    @Override
    public JavaCompiledClassLoader getClassLoader(Location location) {
        return cargador;
    }

    
    
    /**
     * @return el FileManager subyacente.
     */
    StandardJavaFileManager getFileManager() {
        return fileManager;
    }
    
    

    /**
     * JavaFileObject para representar código fuente en memoria
     */
    private static final class FuenteEnMemoria extends SimpleJavaFileObject 
    {
        CharSequence códigoFuente;

        FuenteEnMemoria(String name, CharSequence code) {
            super(URI.create("mem:///" + name.replace('.', '/') + Kind.SOURCE.extension), 
                    Kind.SOURCE);
            this.códigoFuente = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return códigoFuente;
        }
    }

    
    
    /**
     * JavaFileObject que escribe los bytecodes compilados en memoria
     */
    private final class BytesEnMemoria extends SimpleJavaFileObject {
        String nombreCualificado;

        BytesEnMemoria(String name) {
            super(URI.create("mem:///" + name.replace('.', '/') + Kind.CLASS.extension), 
                    Kind.CLASS);
            this.nombreCualificado = name;
        }

        @Override
        public OutputStream openOutputStream() {
            //guarda los bytes en el mapa 'bytecodesEnMemoria' 
            //tan pronto termine el compilador de escribir los bytes
            return new FilterOutputStream(new ByteArrayOutputStream()) {
                @Override
                public void close() throws IOException {
                    out.close();
                    ByteArrayOutputStream bos = (ByteArrayOutputStream) out;
                    bytecodesEnMemoria.put(nombreCualificado, bos.toByteArray());
                }
            };
        }
    }

    
    
    /**
     * Carga los bytes de las clases compiladas en memoria con esta instancia
     */
    private class JavaCompiledClassLoader extends ClassLoader {

        JavaCompiledClassLoader(ClassLoader padre) {
            super(padre);
        }

        @Override
        protected Class<?> findClass(final String nombreCualificado)
                throws ClassNotFoundException {
            //consumimos los bytes porque a partir de ahora estarán cargados
            byte[] bytes = bytecodesEnMemoria.remove(nombreCualificado);
            
            if (bytes != null) 
                return defineClass(nombreCualificado, bytes, 0, bytes.length);

            //clase no encontrada ni aquí ni el classloader padre
            throw new ClassNotFoundException(nombreCualificado);
        }
    }

    
}
