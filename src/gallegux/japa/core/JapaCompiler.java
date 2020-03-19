package gallegux.japa.core;



import java.util.logging.Logger;


public class JapaCompiler 
{
	static final String INCLUDE_START_TAG = "<include>";
	static final String INCLUDE_END_TAG = "</include>";

	static final String START_TAG_IMPORTS = "<imports>";
	static final String END_TAG_IMPORTS = "</imports>";

	static final String START_TAG_DEFINITIONS = "<definitions>";
	static final String END_TAG_DEFINITIONS = "</definitions>";

	static final String START_TAG_EXECUTE = "<execute>";
	static final String END_TAG_EXECUTE = "</execute>";

	static final String START_TAG_VALUE = "<%=";
	static final String END_TAG_VALUE = "%>";
	static final String START_TAG_CODE = "<%";
	static final String END_TAG_CODE = "%>";
	
	static final String OUT_EMPTY_1 = "out.print(\"\");";
	static final String OUT_EMPTY_2 = "out.println(\"\");";
	
	
	
//	private static String leerFichero(String fichero)
//	{
//		StringBuilder s = new StringBuilder();
//
//		try {
//			BufferedReader br = new BufferedReader(new FileReader(fichero));
//			
//			String linea;
//			while ( (linea = br.readLine()) != null ) {
//				s.append(linea);
//				s.append('\n');
//			}
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return s.toString();
//	}
	

	
//	private static String escribirFichero(String fichero, String texto)
//	{
//		StringBuilder s = new StringBuilder();
//
//		try {
//			FileWriter fw = new FileWriter(fichero);
//			fw.write(texto);
//			fw.close();
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return s.toString();
//	}
	
	
	
	public static String compile(String paquete, String clase, String srcJapa)
	{
		String imports = getBlock(srcJapa, START_TAG_IMPORTS, END_TAG_IMPORTS);
		String definitions = getBlock(srcJapa, START_TAG_DEFINITIONS, END_TAG_DEFINITIONS);
		String execute = getBlock(srcJapa, START_TAG_EXECUTE, END_TAG_EXECUTE);
		
		definitions = compilePart(definitions, true);
		execute = compilePart(execute, false);
		
		StringBuilder sb = new StringBuilder();
		
		if (paquete != null) {
			sb.append("package ");
			sb.append(paquete);
			sb.append(";\n\n");
		}
		
		sb.append("import gallegux.japa.core.*;\n");
		sb.append(imports);
		sb.append("\n\n");
		
		sb.append("public class ");
		sb.append(clase);
		sb.append(" extends JavaPage {\n\n");
		
		sb.append(definitions);
		sb.append("\n\n");
		
		sb.append("public void execute(JapaParams params, JapaHeaders headers) throws RuntimeException {\n");
		sb.append("out.print(\"");
		sb.append(execute);
		sb.append("\");");
		sb.append("\n}\n\n}");
		
		removeSubstring(sb, OUT_EMPTY_2);
		
		return sb.toString();
	}
	
	
	
	private static void removeSubstring(StringBuilder sb, String substr)
	{
		int pos = 0;
		while ( (pos = sb.indexOf(substr, pos)) != -1 ) {
			sb.replace(pos, pos + substr.length(), "");
		}
	}
	
	
	
 	private static String getBlock(String src, String tagIniBlock, String tagEndBlock)
	{
		int ini = src.indexOf(tagIniBlock) + tagIniBlock.length();
		int fin = src.indexOf(tagEndBlock);
		
		return src.substring(ini, fin);
	}
	
	
	
	private static String compilePart(String srcJapa, boolean repetarSaltosLinea)
	{
		String[] lineas = srcJapa.split("\n");
		StringBuilder sb = new StringBuilder();
		
		for (String linea: lineas) {
			sb.append(linea);
			if (repetarSaltosLinea) sb.append('\n');
		}
		
		lineas = null;
		
		int pos = 0, posFin = -1;
		String reemplazante;
		
		while ( (pos = sb.indexOf(START_TAG_VALUE, posFin+1)) != -1) {
			posFin = sb.indexOf(END_TAG_VALUE, pos);
			
			//reemplazado = sb.substring(pos, posFin+END_TAG_VALUE.length()); 
			reemplazante = "\");\nout.print(" + sb.substring(pos + START_TAG_VALUE.length(), posFin) + ");\nout.print(\"";
			sb.replace(pos, posFin + END_TAG_VALUE.length(), reemplazante);
		}
		

		pos = -1;
		while ( (pos = sb.indexOf(START_TAG_CODE, pos)) != -1) {
			sb.replace(pos, pos+START_TAG_CODE.length(), "\");\n");
		}
		
		pos = -1;
		while ( (pos = sb.indexOf(END_TAG_CODE, pos)) != -1) {
			sb.replace(pos, pos+END_TAG_CODE.length(), "out.print(\"");
		}
		
		removeSubstring(sb, OUT_EMPTY_1);
		
		return sb.toString();
	}
	
	
	
	public static String insertIncludes(String srcJapa, String referenceDir)
	{
		int startTag = 0, endTag = 0;
		String fileToInclude, textToInclude, textToReplace;
		
		while ( (startTag = srcJapa.indexOf(INCLUDE_START_TAG)) != -1 ) {
			if ( (endTag = srcJapa.indexOf(INCLUDE_END_TAG, startTag)) != -1 ) {
				textToReplace = srcJapa.substring(startTag, endTag + INCLUDE_END_TAG.length());
				fileToInclude = srcJapa.substring(startTag + INCLUDE_START_TAG.length(), endTag).trim();
				
				try {
					textToInclude = Util.obtainContent(referenceDir + fileToInclude);
					srcJapa = srcJapa.replace(textToReplace, textToInclude);
					textToInclude = null;
				}
				catch (Exception e1) {
					Logger.getLogger("gallegux.japa.core.JapaCompiler").warning(e1.toString());
					srcJapa = srcJapa.replace(textToReplace, "");
				}
			}
		}
		
		return srcJapa;				
	}
	

	

}
