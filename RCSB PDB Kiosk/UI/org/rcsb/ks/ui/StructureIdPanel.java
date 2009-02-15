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
import org.rcsb.ks.model.StructureAuthor;
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

	private JTextArea author_area = new JTextArea ();
	private JTextArea journal_area = new JTextArea ();
	
	
	public StructureIdPanel() {
	
		setLayout ( new BoxLayout ( this, BoxLayout.Y_AXIS ));
		setBackground(Color.black);
			
		author_area.setFont( new Font ( "Helvetica", Font.BOLD, 15 ));
		author_area.setBackground ( Color.black);
		author_area.setForeground( Color.orange );
		author_area.setWrapStyleWord( true );
		author_area.setLineWrap( true );
		
		journal_area.setFont( new Font ( "Helvetica", Font.PLAIN, 14 ));
		journal_area.setBackground ( Color.black);
		journal_area.setForeground( Color.white );
		journal_area.setWrapStyleWord( true );
		journal_area.setLineWrap( true );	
		
		add( author_area );
		add ( journal_area );
	}
	
	
	public Structure getStructure ()
	{
		return structure;
	}

	public void updateStructure(Structure structure) {
		this.structure = structure;
		
		// {{ get the pdb id from the url }}
		String ktemp = structure.getUrlString();
		
		int findex = ktemp.lastIndexOf( "/" )+1;
		if ( ktemp.endsWith ( ".xml.gz")){
			int indexof = ktemp.lastIndexOf( ".xml.gz" );
			findex = indexof - 4;
		}
		String pdbid = ktemp.substring( findex, findex+4 );
		
		KSStructureInfo structureInfo = (KSStructureInfo)structure.getStructureInfo();
		
		StructureAuthor primaryCitation = structureInfo.getStructureAuthor();
		author_area.setText(primaryCitation.getAuthorsAsString(8) +"\n" + 
				"PDB Id: "+ pdbid.toUpperCase());
		
		JournalArticle journalArticle = structureInfo.getJournalArticle();
		journal_area.setText(journalArticle.getTitle());
		journal_area.append( "\n");
		journal_area.append (journalArticle.getJournalReference());

		repaint();
	}

	public Dimension getPreferredSize() {
		return new Dimension(250, 150);
	}
}
