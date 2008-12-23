/*
 * BioJava development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence. This should
 * be distributed with the code. If you do not have a copy,
 * see:
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors. These should be listed in @author doc comments.
 *
 * For more information on the BioJava project and its aims,
 * or to join the biojava-l mailing list, visit the home page
 * at:
 *
 * http://www.biojava.org/
 *
 * This code was contributed from the Molecular Biology Toolkit
 * (MBT) project at the University of California San Diego.
 *
 * Please reference J.L. Moreland, A.Gramada, O.V. Buzko, Qing
 * Zhang and P.E. Bourne 2005 The Molecular Biology Toolkit (MBT):
 * A Modular Platform for Developing Molecular Visualization
 * Applications. BMC Bioinformatics, 6:21.
 *
 * The MBT project was funded as part of the National Institutes
 * of Health PPG grant number 1-P01-GM63208 and its National
 * Institute of General Medical Sciences (NIGMS) division. Ongoing
 * development for the MBT project is managed by the RCSB
 * Protein Data Bank(http://www.pdb.org) and supported by funds
 * from the National Science Foundation (NSF), the National
 * Institute of General Medical Sciences (NIGMS), the Office of
 * Science, Department of Energy (DOE), the National Library of
 * Medicine (NLM), the National Cancer Institute (NCI), the
 * National Center for Research Resources (NCRR), the National
 * Institute of Biomedical Imaging and Bioengineering (NIBIB),
 * the National Institute of Neurological Disorders and Stroke
 * (NINDS), and the National Institute of Diabetes and Digestive
 * and Kidney Diseases (NIDDK).
 *
 * Created on 2008/12/22
 *
 */ 
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
