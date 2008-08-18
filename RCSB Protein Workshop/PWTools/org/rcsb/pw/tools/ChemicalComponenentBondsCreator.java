package org.rcsb.pw.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Currently, this takes a file named Components-rel-alt.cif and removes unnecessary information to create a small database of files.
 * @author John Beaver
 *
 */
public class ChemicalComponenentBondsCreator {
	public static final String inputPath = "/Users/jbeaver/mbt/remediated/Components-rel-alt.cif";
	public static final String outputPath = "/Users/jbeaver/mbt/remediated/ChemicalComponentBonds.dat";
	
	public static void main(String[] args) {
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			in = new BufferedReader(new FileReader(new File(inputPath)));
			out = new PrintWriter(new FileWriter(new File(outputPath)));
			
			boolean isInChemCompBondBlock = false;
			
			String line = null;
			while((line = in.readLine()) != null) {
				line = line.trim();
				
				if(isInChemCompBondBlock) {
					if(line.equals("#")) {
						isInChemCompBondBlock = false;
					} else {
						String[] split = line.split("\\s++");
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
