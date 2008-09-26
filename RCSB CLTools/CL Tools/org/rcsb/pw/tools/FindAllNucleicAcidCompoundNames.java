package org.rcsb.pw.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.zip.GZIPInputStream;

public class FindAllNucleicAcidCompoundNames {
	public static final String inputPath = "Components.cif.gz";
	public static final String outputPath = "NucleicAcidCompoundNameMapCreator.txt";
	
	public static void main(String[] args) {
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			final FileInputStream fileInputStream = new FileInputStream( inputPath );
			GZIPInputStream gzipInputStream = new GZIPInputStream( fileInputStream );
			InputStreamReader isReader = new InputStreamReader(gzipInputStream);
			in = new BufferedReader(isReader);
			out = new PrintWriter(new FileWriter(new File(outputPath)));
			
			String curChemCompId = null;
			
			String line = null;
			while((line = in.readLine()) != null) {
				line = line.trim();
				
				if(line.startsWith("_chem_comp.id")) {
					String[] split = line.split("\\s++");
					if(split == null || split.length != 2) {
						new Exception("unexpected _chem_comp.id value").printStackTrace();
						continue;
					}
					
					curChemCompId = split[1];
					if(curChemCompId.charAt(0) == '"' || curChemCompId.charAt(curChemCompId.length() - 1) == '"') {
						curChemCompId = curChemCompId.substring(1, curChemCompId.length() - 1);
					}
				} else if(line.startsWith("_chem_comp.type")) {
					String type = line.substring("_chem_comp.type".length()).trim();
					if(type == null || type.length() == 0) {
						new Exception("unexpected _chem_comp.id value").printStackTrace();
						continue;
					}
					
					if(type.equals("\"DNA LINKING\"") || type.equals("\"RNA LINKING\"")) {
						out.println("allNames.add(\"" + curChemCompId + "\");");
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(in != null) {
					in.close();
				}
				if(out != null) {
					out.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
