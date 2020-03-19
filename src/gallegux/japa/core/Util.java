package gallegux.japa.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class Util 
{
	
	
	public static boolean cleanDir(String dir)
	{
		return clean(new File(dir));
	}
	
	
	
    public static boolean clean(File dir) 
    {
        if (dir.isDirectory()) {
            String[] hijos = dir.list();
            for (int i = 0; i < hijos.length; i++) {
                if (!clean(new File(dir, hijos[i]))) 
                    return false;
            }
        }

        return dir.delete();
    }
    
    
    
    
    public static InputStream obtainInputStream(String file) throws FileNotFoundException
    {
		return (JapaServerConfig.isJapaSourceFromClasspath())
				? new Object().getClass().getResourceAsStream(file)
				: new FileInputStream(JapaServerConfig.getJapaDir() + file);
    }

    
    
	public static String obtainTextFile(InputStream is)
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String linea;
		StringBuilder s = new StringBuilder();
		
		try {
			while ( (linea = br.readLine()) != null ) {
				s.append(linea);
				s.append('\n');
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return s.toString();
	}
	
	
	
	/**
	 * Obtiene el contenido de un fichero de texto
	 * @param file
	 * @return
	 */
	public static String obtainContent(String file) throws FileNotFoundException
	{
		String content = null;
		
		try {
			InputStream is = obtainInputStream(file);
			content = obtainTextFile(is);
			is.close();
		}
		catch (IOException e1) {
			Logger.getLogger("gallegux.japa.core.Util").warning(e1.toString());
		}
		
		return content;
	}

	
	
	/**
	 * Devuelve el directorio del directorio indicado
	 * - /dir1/dir2/fichero.ext --> /dir1/dir2/
	 * - /fichero.ext --> /
	 * @param file
	 * @return
	 */
	public static String getDirectory(String file)
	{
		int barra = file.lastIndexOf(file);
		
		return file.substring(0, barra+1);
	}

}
