package org.rcsb.pw.tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.zip.GZIPInputStream;

/**
 * Currently, this takes a file named Components.cif and removes unnecessary information
 * to create a small database of files.
 * 
 * @author John Beaver
 * 
 * <h2>Updates:</h2>
 * <p>
 * I've put this in it's own project.  It expects to act on the file downloaded from this address:</p>
 * <blockquote>
 * ftp://ftp.wwpdb.org/pub/pdb/data/monomers/components.cif.gz
 * </blockquote>
 * 
 * <p>
 * The working directory should be set to the project root.  When done and happy with the
 * output file, move the output file to:</p>
 * 
 * <blockquote>
 * RCSB MBT Libs/Structure Model/org/rcsb/mbt/model/util
 * </blockquote>
 * 
 * <p>
 * It then becomes a resource loaded from the jar.</p>
 * 
 * 23-Sep-08 - rickb 
 * 
 */
public class ChemicalComponentBondsCreator {
	public static final String inputPath = "Components.cif.gz";
	public static final String outputPath = "ChemicalComponentBonds.dat";
	
	public static void main(String[] args) {
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			final FileInputStream fileInputStream = new FileInputStream( inputPath );
			GZIPInputStream gzipInputStream = new GZIPInputStream( fileInputStream );
			InputStreamReader isReader = new InputStreamReader(gzipInputStream);
			in = new BufferedReader(isReader);
			out = new PrintWriter(new FileWriter(new File(outputPath)));
			
			boolean isInChemCompBondBlock = false;
			
			String line = null;
			while((line = in.readLine()) != null) {
				line = line.trim();
				
				if(isInChemCompBondBlock) {
					if(line.equals("#")) {
						isInChemCompBondBlock = false;
					} else {
						String dqline = line.replaceFirst("\"([A-Z0-9]+) ([A-Z0-9]+)\"", "$1=$2");						
						String[] split = dqline.split("\\s++");
						if(split == null || split.length != 7) {
							new Exception("Encountered unexpected data").printStackTrace();
						} else {
							for(int i = 0; i < 4; i++) {
								String s = split[i];
								if(s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
									split[i] = s.substring(1,s.length() - 1);
								}
							}
							out.println(split[0] + "\t" + split[1] + "\t" + split[2] + "\t" + split[3]);
						}
					}
				} else if(line.equals("_chem_comp_bond.pdbx_ordinal")) {
					isInChemCompBondBlock = true;
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
