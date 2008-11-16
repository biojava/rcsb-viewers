package org.rcsb.ex.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

/**
 * Same principle as ChemicalComponentBondsCreator - see that file for documentation.
 * 
 * @author rickb
 *
 */
public class FindAllNucleicAcidCompoundNames {
	public static final String ftpInputPath = "ftp://ftp.wwpdb.org/pub/pdb/data/monomers/components.cif.gz";
	public static final String defaultOutputPath = "NucleicAcidCompoundNames.dat";
	
	public static void main(String[] args)
	{
		String outputPath = defaultOutputPath;
		BufferedReader in = null;
		PrintWriter out = null;
		String connectedMsg;
		
		try {
			InputStream inputStream = null;

			if (args.length == 0)
			{
				System.out.println("Connecting to ftp site...");
				URL ComponentBondsUrl = new URL(ftpInputPath);
				URLConnection urlConnection = ComponentBondsUrl.openConnection();
				urlConnection.setReadTimeout(10000);
				inputStream = urlConnection.getInputStream();
				connectedMsg = "Connected, streaming nucleotide information...";
			}
			
			else
			{
				System.out.println("Opening file: " + args[0]);
				inputStream = new FileInputStream(args[0]);
				if (args.length == 2)
					outputPath = args[1];
				connectedMsg = "Reading file...";
			}
			System.out.println(connectedMsg);
			GZIPInputStream gzipInputStream = new GZIPInputStream( inputStream );
			InputStreamReader isReader = new InputStreamReader(gzipInputStream);
			in = new BufferedReader(isReader);
			out = new PrintWriter(new FileWriter(new File(outputPath)));
			
			String curChemCompId = null;
			
			String line = null;
			int lineCount = 0, lineEvery = 10;
			
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
				}
				
				else if (line.startsWith("_chem_comp.type"))
				{
					String type = line.substring("_chem_comp.type".length()).trim();
					if(type == null || type.length() == 0)
					{
						new Exception("unexpected _chem_comp.id value").printStackTrace();
						continue;
					}
					
					if(type.equals("\"DNA LINKING\"") || type.equals("\"RNA LINKING\""))
					{
						if ((++lineCount % lineEvery) == 1)
							System.out.println(lineCount + ": " + type + " - " + curChemCompId);
						out.println(curChemCompId);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		finally
		{
			try
			{
				System.out.println("Done.");
				
				if (in != null)
					in.close();

				if(out != null)
					out.close();

			}
			
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
	}
}
