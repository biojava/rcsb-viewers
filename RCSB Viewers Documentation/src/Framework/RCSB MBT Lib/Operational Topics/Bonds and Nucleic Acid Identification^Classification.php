<?php
  session_start();
  set_include_path($_SESSION['INCLUDE_PATH']);
  include_once "resources/snippets/prefix.php";
?>
	  <ul class="preface">
	    <li>
	      Bond records are ignored in the loaders.  Bonds are determined either through
	      a dictionary lookup, or via calculation if the lookup fails.
	    </li>
	    <li>
	      Currently the lookup files described here are generated by an external process and are incorporated
	      directly within the 'Structure Models' jar as a resource.  This means that they can only be updated
	      if the 'Structure Models' jar is updated.
	    </li>
	    <li class="follow-on">
	      A preferable approach would be to put them in their own jar, that can be updated independently
	      of the model jar (or any functional jars.)
	    </li>
	    <li class="follow-on">
	      See the <em>RCSB Excluded</em> project, <em>CL Tools</em> directory for more information.</p>
	    </li>
	  </ul>
	  
	  <ul class="relevent-classes">
	    <li>Bond - definition class</li>
	    <li>BondFactory - Creates the bonds (static)</li>
	    <li>ChemicalComponentBonds - does lookup for bonds</li>
	    <li>NucleicAcidInfo - does lookup for nucleic acids</li>
	    <li>Octree - for calculating bonds</li>
	    <li>OctreeAtomItem - for Octree</li>
	    <li>OctreeDataItem - for Octree</li>
	  </ul>
	</p>
    <p>
      MBT maintains a dictionary of known structures. This comes from a combined .cif file that is found at this ftp
      site:
    </p>
    <code><a href="ftp://ftp.wwpdb.org/pub/pdb/data/monomers/components.cif.gz">
    	ftp://ftp.wwpdb.org/pub/pdb/data/monomers/components.cif.gz</a>
    </code>
    <p>
      This file is loaded and broken apart by an external process - see the <span class="projectname">RCSB Excluded</span> 
      project, package
      <span class="packagename">tools</span> package.
    </p>
    <p>
      <span class="classname">ChemicalComponentBondsCreator</span> is run from the commandline against the file. It's not a full parser - it just
      extracts bond information. The output of that (ChemicalComponentBonds.dat') is copied into the
      <span class="projectname">RCSB MBT Libs</span>
      project, source directory <span class="foldername">Structure Model</span>, in the package
      <span class="packagename">util</span> as a resource.
    </p>
    <p>
      At runtime, this abbreviated file is picked up and put into a hash-table. Atoms are checked against this for bond
      information.
    </p>
    <p>
      If bonds are not found for a given residue, the atoms are run through a bond-generation algorithm that determines
      bonds by distance. Atoms are arranged in an octree, first, for quick spatial checks. I don't think this is
      working correctly, right now. But, it's in there.
    </p>
    <p>
      Look in the 'RCSB MBT Libs' project, source directory 'Structure Model', in the package
      <span class="packagename">model</span> for
      the <span class="classname">StructureMap</span> class, again. In there, find <span class="method">generateBonds()</span>.
      Note it checks a flag to ignore the dictionary
      lookup and strictly use the distance algorithm (suspect this is for debugging, mainly). The
      <span class="classname">BondFactory</span> class is what does the dictionary lookup or bond calculations, depending on
      what's required.
    </p>
    <p>
      Incidentally, the same kind of mechanism is used to determined nucleic acid classification. In the 
      <span classname="projectname">RCSB Excluded</span> project, source directory <span class="foldername">CL Tools'</span>,
      the <span class="classname">FindAllNucleicAcidCompoundNames</span> is also
      run from the commandline and generates an output file ('NucleicAcidCompoundNames.dat').
    </p>
<?php
  include_once "resources/snippets/suffix.php";
?>
