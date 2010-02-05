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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;

/**
 * Same principle as ChemicalComponentBondsCreator - see that file for documentation.
 * 
 * @author rickb
 *
 */
public class ChemicalComponentTypesCreator {
	public static final String ftpInputPath = "ftp://ftp.wwpdb.org/pub/pdb/data/monomers/components.cif.gz";
	public static final String defaultOutputPath = "ChemicalComponentTypes.tsv";

	public static void main(String[] args)
	{
		String outputPath = defaultOutputPath;
		BufferedReader in = null;
		PrintWriter out = null;
		String connectedMsg;

		Set<String> types = new HashSet<String>();
		types.add("L-PEPTIDE LINKING");
		types.add("DNA LINKING");
		types.add("RNA LINKING");

		Set<String> uniqueTypes = new HashSet<String>();

		try {
			InputStream inputStream = null;

			if (args.length == 0)
			{
				System.out.println("Connecting to ftp site...");
				URL ComponentBondsUrl = new URL(ftpInputPath);
				URLConnection urlConnection = ComponentBondsUrl.openConnection();
				urlConnection.addRequestProperty("User-agent", "Mozilla/4.0 (compatible; MSIE 6.0;Windows NT 5.1; SV1)");
				urlConnection.setReadTimeout(10000);
				inputStream = urlConnection.getInputStream();
				connectedMsg = "Connected, streaming modified residue information...";
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
			String type = null;

			String line = null;

			while((line = in.readLine()) != null) {
				line = line.trim();

				if(line.startsWith("_chem_comp.id")) {
					curChemCompId = null;
					type = null;
					String[] split = line.split("\\s++");
					if(split == null || split.length != 2) {
						new Exception("unexpected _chem_comp.id value").printStackTrace();
						continue;
					}

					curChemCompId = stripQuotes(split[1]);			
				}

				else if (line.startsWith("_chem_comp.type"))
				{
					type = line.substring(15).trim();
					if(type == null || type.length() == 0)
					{
						new Exception("unexpected _chem_comp.id value").printStackTrace();
						continue;
					}

					// case is inconsistently used in Chemical Component Dictionary, convert to upper case
					type = type.toUpperCase();
					type = stripQuotes(type);
					
					// peptide linking is inconsistently used in Chemical Component Dictionary
					// filed as issue: http://tracking.wwpdb.org/jira/browse/DATAQUALITY-39
					if (type.equals("PEPTIDE-LINKING")) {
						type = "PEPTIDE LINKING";
					}
					uniqueTypes.add(type);
					System.out.println(type);
				}
				else if (line.startsWith("_chem_comp.mon_nstd_parent_comp_id"))
				{
					String nstdParent = line.substring(34).trim();
					if (nstdParent == null || nstdParent.length() == 0) {
						new Exception("unexpected _chem_comp.id value").printStackTrace();
					}

					nstdParent = stripQuotes(nstdParent);
					if (type.equals("NON-POLYMER")) {
						continue;
					}
					if (nstdParent.equals("?")) {
						out.println(curChemCompId + "\t" + type);
					} else {

						String[] token = nstdParent.split(",");
						System.out.println(Arrays.toString(token));

						if (type != null) {
							out.print(curChemCompId + "\t" + type);
							for (String name: token) {
								out.print("\t" + name.trim());
							}
							out.println();
						}
					}
				}
			}
			System.out.println("Types: " + uniqueTypes);
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

	private static String stripQuotes(String string) {
		if (string.startsWith("\"") && string.endsWith("\"")) {
			return string.substring(1, string.length()-1);
		}
		return string;
	}
}
