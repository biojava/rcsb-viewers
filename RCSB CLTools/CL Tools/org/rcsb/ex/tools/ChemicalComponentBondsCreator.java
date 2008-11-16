package org.rcsb.ex.tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import org.rcsb.mbt.model.util.ChemicalComponentBonds;
import org.rcsb.mbt.model.util.DebugState;



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
public class ChemicalComponentBondsCreator
{
	public static final String ftpInputPath = "ftp://ftp.wwpdb.org/pub/pdb/data/monomers/components.cif.gz";
	public static final String defaultOutputPath = "ChemicalComponentBonds.dat";
	
	public static void main(String[] args)
	{
		PrintWriter out = null;
		InputStream inputStream = null;
		GZIPInputStream gzipInputStream = null;
		String outputPath = defaultOutputPath;
		String connectedMsg;
		
		DebugState.setDebugState(true);
		
		try
		{			
			if (args.length == 0)
			{
				System.out.println("Connecting to ftp site...");
				URL ComponentBondsUrl = new URL(ftpInputPath);
				URLConnection urlConnection = ComponentBondsUrl.openConnection();
				urlConnection.setReadTimeout(10000);
				inputStream = urlConnection.getInputStream();
				connectedMsg = "Connected, streaming bond information...";
			}
			
			else
			{
				System.out.println("Opening file: " + args[0]);
				inputStream = new FileInputStream(args[0]);
				if (args.length == 2)
					outputPath = args[1];
				connectedMsg = "Reading file...";
			}

			gzipInputStream = new GZIPInputStream( inputStream );
			System.out.println(connectedMsg);
			out = new PrintWriter(outputPath);
			ChemicalComponentBonds.parseCifFileForBonds(gzipInputStream, out);
		}	
		
		catch (SocketTimeoutException e)
		{
			System.out.println("Timout (10 seconds) exceed.  Aborting.");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		finally
		{
			try
			{
			if (gzipInputStream != null)
				gzipInputStream.close();
			
			else if (inputStream != null)
				inputStream.close();
			
			if (out != null)
				out.close();
			
			System.out.println("Done.");
			}
			
			catch (IOException e)
			{
				e.printStackTrace();
			}

		}
		
	}

}
