package org.rcsb.demo.utils;

/**
 * Output demo strings and control indenting
 * 
 * @author rickb
 *
 */
public class Output
{
	static private int indentSize = 4;
	static private String indent = "";
	static private String indentBuf = null;
	
	/**
	 * Bump the indent
	 */
	static public void incrementIndent()
	{
		if (indentBuf == null)
		{
			indentBuf = new String();
			for (int i = 0; i < indentSize; i++)
				indentBuf += ' ';
		}
		
       indent += indentBuf;
	}
	
	/**
	 * Back out the current indent
	 */
	static public void decrementIndent()
	{
		if (indent.length() >= indentSize)
			indent = indent.substring(0, indent.length() - indentSize);
	}
	
	/**
	 * Output a line with the current indent factor.
	 * 
	 * @param line
	 */
	static public void lineOut(String line)
	{
		System.out.println(line.length() == 0? "" : indent + line);
	}
}
