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
 * Created on 2013/02/12
 *
 */ 
package org.rcsb.mbt.model;

/**
 *  Bird contains information about Biologically Interesting Molecule Reference Dictionary (BIRD)
 *  parsed from the _pdbx_molecule category, for example PDB 1PNV:
 *  loop_
 *  _pdbx_molecule.instance_id 
 *  _pdbx_molecule.prd_id 
 * _pdbx_molecule.asym_id 
 * 1 PRD_000204 C 
 * 1 PRD_000204 E 
 *  <P>
 *  @author	Peter W. Rose
 */
public class Bird {
	/**
	 * The id for this molecule from  Biologically Interesting molecule Reference Dictionary (BIRD)
	 */
	String prdId;

	/**
	 * The asym id each molecular component
	 */
	String asymId;

	/**
	 * The instanceId differentiates different instances of the same BIRD molecule
	 */
	String instanceId;
	
	/**
	 * The name of the BIRD molecule
	 */
	String name;

	
	public Bird(String prdId, String asymId, String instanceId) {
		this.prdId = prdId;
		this.asymId = asymId;
		this.instanceId = instanceId;
	}
	
	/**
	 * @return the prdId
	 */
	public String getPrdId() {
		return prdId;
	}

	/**
	 * @param prdId the prdId to set
	 */
	public void setPrdId(String prdId) {
		this.prdId = prdId;
	}

	/**
	 * @return the asymId
	 */
	public String getAsymId() {
		return asymId;
	}

	/**
	 * @param enityId the enityId to set
	 */
	public void setEnityId(String enityId) {
		this.asymId = enityId;
	}

	/**
	 * @return the instanceId
	 */
	public String getInstanceId() {
		return instanceId;
	}

	/**
	 * @param instanceId the instanceId to set
	 */
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns true if the passed in instance of the Bird object refers to the same Bird instance.
	 * Note, a Bird molecule may be a group of molecules, so one Bird molecule may be assigned multiple
	 * asymIds. Therefore, we do not compare the asymIds.
	 * @param bird
	 * @return
	 */
	public boolean isSameInstance(Bird bird) {
		return this.prdId.equals(bird.getPrdId()) && this.instanceId.equals(bird.getInstanceId());
	}
	
	public String toString() {
		return prdId + "(" + instanceId + ")";
	}
}
