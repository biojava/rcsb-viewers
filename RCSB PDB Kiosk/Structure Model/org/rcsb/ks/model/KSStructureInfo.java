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
 * Created on 2006/06/30
 *
 */
package org.rcsb.ks.model;

import java.util.ArrayList;
import java.util.List;

import org.rcsb.mbt.model.StructureInfo;

/**
 * Implements a StructureComponent container for Structure information data.
 * This information includes data such as authors, dates, and data sources.
 * <P>
 * 
 * @author John L. Moreland, Jeff Milton (modified for the outreach viewer)
 * @see org.rcsb.mbt.model.Structure This may either contain duplicate
 *      information in the MBT lib StructureInfo, or supercede it - not sure.
 *      The *Primary Citation* field looks suspiciously like the *author* field
 *      in the lib version.
 * 
 *      Anyway, the only place the lib version is created is in
 *      cifStructureLoader, so if cif files are being read, it may either be
 *      clobbered by or clobber this, but only for cif files.
 * 
 *      More study required.
 * 
 *      TODO: reconcile this with the MBT lib StructureInfo.
 * 
 *      30-May-08 - rickb
 * 
 */
public class KSStructureInfo extends StructureInfo {
	private StructureAuthor structureAuthor = new StructureAuthor();
	private JournalArticle journalArticle = new JournalArticle();
	private List<EntityDescriptor> entityDescriptors = new ArrayList<EntityDescriptor>();

	public void setStructureAuthor(StructureAuthor structureAuthor) {
		this.structureAuthor = structureAuthor;
	}

	public void setJournalArticle(JournalArticle journalArticle) {
		this.journalArticle = journalArticle;
	}

	public StructureAuthor getStructureAuthor() {
		return structureAuthor;
	}

	public JournalArticle getJournalArticle() {
		return journalArticle;
	}

	public void setDescriptors(List<EntityDescriptor> entityDescriptors) {
		this.entityDescriptors = entityDescriptors;
	}

	public void addDescriptor(EntityDescriptor entityDescriptor) {
		entityDescriptors.add(entityDescriptor);
	}

	public EntityDescriptor getDescriptor(String id) {
		for (EntityDescriptor descriptor : entityDescriptors) {
			if (descriptor.getEntityId().equalsIgnoreCase(id)) {
				return descriptor;
			}
		}
		return null;
	}

	public List<EntityDescriptor> getDescriptors() {
		return entityDescriptors;
	}

//	public EntityDescriptor getDescriptor(Residue _r) {
//		return null;
//
//	}
}
