package org.rcsb.mbt.structLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.rcsb.mbt.model.geometry.ModelTransformationList;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.UnitCell;
import org.rcsb.mbt.model.util.PdbToNdbConverter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
		// TODO Auto-generated method stub
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
		InputStream inputStream = urlConnection.getInputStream();
		parseXMLFile(url.getFile(), inputStream);
		return handler.getStructure();
	}

	public boolean canLoad(String name) 
	{
		// TODO Auto-generated method stub
		return false;
	}

	public PdbToNdbConverter getIDConverter() {
		return handler.getIDConverter();
	}

	public String getLoaderName()
	{
		return handler.getLoaderName();
	}

	public String[] getNonProteinChainIds() {
		return handler.getNonProteinChainIds();
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