<?php
  session_start();
  set_include_path($_SESSION['INCLUDE_PATH']);
  include_once "resources/snippets/prefix.php";
?>
    <p>
      Look in the 'RCSB MBT Libs' project, in the source dir 'Structure Model', package 'org.rcsb.mbt.model' for most
      of this (unless otherwise specified).
    </p>
    <p>
      'Structure' is an abstract class. The loaders derive a helper class from it, and use it to push off all their
      discovered records, without analysis.
    </p>
    <p>
      'StructureMap' is the real core of the structure model. The information kept here is what is actually contains
      the atom/bond/fragment relationships (The raw types have been moved to 'org.rcsb.mbt.model.interim').
    </p>
    <p>
      First, any definitions that are picked up in the file are kept in a list along with all of the other
      'StructureComponent'-derived items defined there (Atoms, Residues, Chains, Bonds). This list is kept in the
      'Structure' class (abstract class derived by loader into a loader-specific implementation). They simply consist
      of raw information as they were collected from the file. These classes ('Coil', 'Helix', 'Strand', 'Turn'),
      derive from 'Conformation' (which is derived from 'StructureComponent').
    </p>
    <p>
      If they exist, these records are examined (in 'StructureMap' - look for 'generateFragments()' and
      'loadFragments()'). An intermediate type called 'RangeMap' is used to store residue ranges for each Conformation
      type found.
    </p>
    <p>
      If they don't exist, then 'deriveFragments()' is called, which creates a
      'org.rcsb.mbt.model.util.DerivedInformation' object used to synthesize the ranges through a heuristic
      ('Kabsch-Sander' is the algorithm cited in the comments.) Basically, it consists of subdividing ranges until the
      conformation is determined. Note the 'Ss'-prefix helper classes. 'Ss' stands for 'SecondaryStructure'.
    </p>
    <p>
      Finally, the completed 'Range' objects are traversed and turned into 'Fragment' types, which is the destination
      type and is what ultimately ends up in the StructureMap lists. Each fragment has a 'ConformationType' (which is
      just another 'ComponentType' set to indicate what conformation it is, and a list of residues that make it up.
    </p>
    <p>
      (Note that if 'deriveFragments()' throws an exception, it tries a 'loadFragments()', again. Might be just to
      clear everything out?)
    </p>
    <p>
      (Note 2: in PDB files, conformation information is ignored. Fragments are always derived.)
    </p>
<?php
  include_once "resources/snippets/suffix.php";
?>