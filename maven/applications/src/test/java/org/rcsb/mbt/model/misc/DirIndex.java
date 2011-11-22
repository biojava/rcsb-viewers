package org.rcsb.mbt.model.misc;

import java.io.File;

public class DirIndex{

	public myDirIndex PROJECTDIR = new myDirIndex();				// the top-level project directory
	public myDirIndex INPUT_TOPTESTDIR = new myDirIndex();			// the top-level test input directory (testInputDirName)
	public myDirIndex INPUT_GENERALDIR  = new myDirIndex();		  	// directory containing general input data
	public myDirIndex	INPUT_MOLECULESDIR= new myDirIndex();	  	// directory containing sample molecules

	public myDirIndex INPUT_CLASSTESTDIR= new myDirIndex();		// the class test input directory
	public myDirIndex  INPUT_COMPAREFILESDIR= new myDirIndex(); 	// the compare files input directory (if using)
	public myDirIndex 	OUTPUT_TOPTESTDIR= new myDirIndex();		// the top-level test output directory (testOutputDirName)
	public myDirIndex 		OUTPUT_CLASSTESTDIR= new myDirIndex();		// the class test output directory
	public myDirIndex 		OUTPUT_COMPAREFILESDIR= new myDirIndex(); 	// the compare files output directory (if using)
	public myDirIndex 		OUTPUT_DIFFDIR= new myDirIndex();			// contains the diff results



}
class myDirIndex{
	protected File path;
	public void setPath(File path) { this.path = path; }
	public File getPath() { if ( path == null ) return null; return this.path; }
}
