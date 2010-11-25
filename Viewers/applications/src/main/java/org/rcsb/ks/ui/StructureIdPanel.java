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
package org.rcsb.ks.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.rcsb.ks.model.JournalArticle;
import org.rcsb.ks.model.KSStructureInfo;
import org.rcsb.mbt.model.Structure;


/**
 * Represents the left lower panel in the Kiosk Viewer.
 * This area displays the structure author and journal reference.
 * 
 * @author Peter Rose (revised)
 *
 */
public class StructureIdPanel extends JPanel
{
	private static final long serialVersionUID = -6698576408588609143L;

	private Structure structure = null;

	private JTextArea titleArea = new JTextArea();
	private JTextArea journalArea = new JTextArea();
	
	
	public StructureIdPanel(Dimension size) {
	
		setLayout ( new BoxLayout ( this, BoxLayout.Y_AXIS ));
		setBackground(Color.black);
			
		// scale font size by height of the panel
		titleArea.setFont( new Font ( "Helvetica", Font.BOLD, size.height/6));
		titleArea.setBackground ( Color.black);
		titleArea.setForeground( Color.orange );
		titleArea.setWrapStyleWord( true );
		titleArea.setLineWrap( true );
		
		// scale font size by height of the panel
		journalArea.setFont( new Font ( "Helvetica", Font.PLAIN, size.height/8));
		journalArea.setBackground ( Color.black);
		journalArea.setForeground( Color.white );
		journalArea.setWrapStyleWord( true );
		journalArea.setLineWrap( true );	
		
		add(titleArea);
		add(journalArea);
	}
	
	
	public Structure getStructure ()
	{
		return structure;
	}

	public void updateStructure(Structure structure) {
		this.structure = structure;
		
		KSStructureInfo structureInfo = (KSStructureInfo)structure.getStructureInfo();
		
	    titleArea.setText("PDB " 
	    		+ getPdbId(structure) + ": "
	    		+ structureInfo.getStructureTitle());
	    	
//	    StructureAuthor structureAuthor = structureInfo.getStructureAuthor();
	    
	    // print journal authors and reference
		JournalArticle journalArticle = structureInfo.getJournalArticle();
		journalArea.setText(journalArticle.getAuthorsAsString(5)
				+ "\n"
				+ journalArticle.getJournalReference());

		repaint();
	}
	
	/**
	 * get PDB id from structure url
	 * @param structure
	 * @return PDB id
	 */
	private String getPdbId(Structure structure) {
		String temp = structure.getUrlString();
		
		int findex = temp.lastIndexOf( "/" )+1;
		if (temp.endsWith ( ".xml.gz")){
			int indexof = temp.lastIndexOf( ".xml.gz" );
			findex = indexof - 4;
		}
		
		return temp.substring( findex, findex+4 ).toUpperCase();
		
	}
}
