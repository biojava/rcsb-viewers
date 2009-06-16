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
package org.rcsb.mbt.structLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.rcsb.mbt.model.geometry.ModelTransformationList;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.UnitCell;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This is a shell around the {@linkplain org.rcsb.mbt.structLoader.StructureXMLHandler}
 * class.  Wrapping it pulls all fo the pre-load boilerplate together and makes it behave
 * the same as the other loaders (@linkplain org.rcsb.mbt.structLoader.PdbStructureLoader}, currently.)
 * <p>
 * Most of the access functions are passthroughs to the XML loader.</p>
 * @author rickb
 *
 */
public class XMLStructureLoader implements IFileStructureLoader
{
	private StructureXMLHandler handler = null;

	public void setInitialBiologicalUnitId(String id)
	{ handler.setInitialBiologicalUnitId(id); }

	public boolean canLoad(File file) {
		return file.exists();
	}

	public boolean canLoad(URL url)
	{
		return false;
	}

	public Structure load(File file) throws IOException
	{
		parseXMLFile(file.getName(), new FileInputStream(file));
		return handler.getStructure();
	}

	public Structure load(URL url) throws IOException
	{
		URLConnection urlConnection = url.openConnection();
        urlConnection.addRequestProperty("User-agent", "Mozilla/4.0 (compatible; MSIE 6.0;Windows NT 5.1; SV1)");
		InputStream inputStream = urlConnection.getInputStream();
		parseXMLFile(url.getFile(), inputStream);
		return handler.getStructure();
	}
	
	public Structure load(String dataset, InputStream is)
	{
		parseXMLFile(dataset, is);
		return handler.getStructure();
	}

	public boolean canLoad(String name) 
	{
		return false;
	}

	public String getLoaderName()
	{
		return handler.getLoaderName();
	}

	public Structure getStructure() {
		return handler.getStructure();
	}
	
	public boolean hasUnitCell()
	{
		return handler.hasUnitCell();
	}

	public UnitCell getUnitCell() {
		return handler.getUnitCell();
	}

	public Structure load(String name) throws IOException
	{
		int colonIndex = name.indexOf(':');
		boolean isUrl = name.charAt(colonIndex + 1) == '/'
				&& name.charAt(colonIndex + 2) == '/'; // else,
		
		if (isUrl)
			return load(new URL(name));
		
		else
			return load(new File(name));
	}
	
	public XMLStructureLoader(StructureXMLHandler in_handler)
	{
		handler = in_handler;
	}
	
	private void parseXMLFile(String dataset, InputStream inputStream)
	{
		try 
		{
			BufferedReader reader;
			if (dataset.endsWith(".gz"))
				reader = new BufferedReader(
						    new InputStreamReader(
								new GZIPInputStream(inputStream)));
			
			else
				reader = new BufferedReader(
						new InputStreamReader(inputStream));


			SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
			
			saxParser.parse(new InputSource(reader), handler);			
		}
		
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		
		catch (SAXException e)
		{
			e.printStackTrace();
		}
		
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public ModelTransformationList getBiologicalUnitTransformationMatrices() {
		return handler.getBiologicalUnitTransformationMatrices();
	}

	public ModelTransformationList getNonCrystallographicOperations() {
		return handler.getNonCrystallographicOperations();
	}

	public boolean hasBiologicUnitTransformationMatrices() {
		return handler.hasBiologicUnitTransformationMatrices();
	}

	public boolean hasNonCrystallographicOperations() {
		return handler.hasNonCrystallographicOperations();
	}
}
