package org.rcsb.mbt.model.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DelimitedFileReader reads delimited text files such as comma-separated values (CSV)
 * or tab-separated values (TSV) files. The (arbitrary) delimiter is specified in the constructor of the class.
 * 
 * @author Peter Rose
 *
 */
public class DelimitedFileReader {
	 private Pattern pattern;	 
	 private BufferedReader reader;
	 private boolean withHeader;
	 private boolean trimWhiteSpace = false;
	 private boolean readFile = false;
	 private int maxColumns = 0;

	 private List<List<String>> data = new ArrayList<List<String>>();
	 private List<String> headers = new ArrayList<String>();

	 /**
	  * Constructor
	  * @param reader bufferedReader
	  * @param delimiter the delimiter character, i.e. "\t" for TSV files or "," for CSV files
	  * @param withHeader set to true if file has a header line
	  */
	 public DelimitedFileReader(BufferedReader reader, String delimiter, boolean withHeader) {
		 // The rather involved pattern used to match CSV's consists of three
	     // alternations: the first matches a quoted field, the second unquoted,
	     // the third a null field.
		 // Source: http://www.java2s.com/Code/Java/Development-Class/SimpledemoofCSVmatchingusingRegularExpressions.htm
		 this.reader = reader;
		 this.withHeader = withHeader;
		 pattern = Pattern.compile("\"([^\"]+?)\"" + delimiter + "?|([^" + delimiter + "]+)" + delimiter + "?|" + delimiter);
	 }
	 
	 /**
	  * Sets a flag if leading or trailing white space should be trimmed from the data items. Default is false.
	  * @param trim true if white space should be trimmed from the data items
	  */
	 public void setTrimWhiteSpace(boolean trim) {
		 this.trimWhiteSpace = trim;
	 }
	 
	 /**
	  * Returns a list of header strings. Note this method requires that the withHeader flag was
	  * set to true in the constructor.
	  * @return
	  * @throws IOException
	  */
	 public List<String> getHeader() throws IOException {
		 if (! readFile) {
			 parseFile();
			 fillEmptyCells();
			 readFile = true;
		 }
		 return  headers;
	 }
	 
	 /**
	  * Returns a list of lists for text items.
	  * @return list of text items
	  * @throws IOException
	  */
	 public List<List<String>> getData() throws IOException {
		 if (! readFile) {
			 parseFile();
			 fillEmptyCells();
			 readFile = true;
		 }
		 return data;
	 }
	 
	 private void parseFile() throws IOException {
		 String line = null;

		 int count = 0;
		 try {
			 while ((line = reader.readLine()) != null) {
				 if (withHeader && count == 0) {
					 headers = parseLine(line);
				 } else {
					 List<String> items = parseLine(line);
					 data.add(items);
				 }
				 count ++;
			 }
		 } catch (IOException e) {
			 throw new IOException("Error reading line: " + (count+1));
		 }
	 }

	 private List<String> parseLine(String line) {
		 List<String> list = new ArrayList<String>();
		 Matcher m = pattern.matcher(line);
		 // For each field
		 while (m.find()) {
			 String match = m.group();
			 if (match == null)
				 break;
			 if (match.endsWith(",")) {  // trim trailing ,
				 match = match.substring(0, match.length() - 1);
			 }
			 if (match.startsWith("\"")) { // assume also ends with
				 match = match.substring(1, match.length() - 1);
			 }
			 if (match.length() == 0)
				 match = "";
			 
			 if (trimWhiteSpace) {
				 match = match.trim();
			 }
			 list.add(match);
		 }
		 maxColumns = Math.max(maxColumns, list.size());
		 return list;
	 }

	 private void fillEmptyCells() {
		 for (List<String> row: data) {
			 int missingColumns = maxColumns - row.size();
			 for (int i = 0; i < missingColumns; i++) {
				 row.add("");
			 }
		 }
	 }

}
