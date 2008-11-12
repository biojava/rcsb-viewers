package org.rcsb.pw.tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import org.rcsb.mbt.model.util.ChemicalComponentBonds;



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
		PrintWriter out = null;
		
		try
		{			
			final FileInputStream fileInputStream = new FileInputStream( inputPath );
			GZIPInputStream gzipInputStream = new GZIPInputStream( fileInputStream );
			ArrayList<String> bondsList = ChemicalComponentBonds.parseCifFileForBonds(gzipInputStream);
			out = new PrintWriter(outputPath);
			for (String bondString : bondsList)
				out.println(bondString);
		}	
		
		catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
		
	}

}
