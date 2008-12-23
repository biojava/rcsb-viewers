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

import org.rcsb.ks.model.JournalIndex;
import org.rcsb.ks.model.KSStructureInfo;
import org.rcsb.ks.model.PrimaryCitation;
import org.rcsb.mbt.model.Structure;


@SuppressWarnings("serial")
/*
 * This is the panel on the left.
 */
public class StructureIdPanel extends JPanel
{
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
		
		journal_area.setFont( new Font ( "Helvetica", Font.PLAIN, 12 ));
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

	public void updateStructure(Structure _structure) {
		structure = _structure;
		KSStructureInfo structureInfo = (KSStructureInfo)structure.getStructureInfo();
		PrimaryCitation primaryCitation = structureInfo.getPrimaryCitation();

		// {{ get the pdb id from the url }}
		String ktemp = structure.getUrlString();
		
		int findex = ktemp.lastIndexOf( "/" )+1;
		if ( ktemp.endsWith ( ".xml.gz")){
			int indexof = ktemp.lastIndexOf( ".xml.gz" );
			findex = indexof - 4;
		}
		String pdbid = ktemp.substring( findex, findex+4 );
		
		
		author_area.setText(primaryCitation.getAuthorsAsString() +"\n" + 
				"["+ pdbid  + "] " );
		
		JournalIndex journalIndex = primaryCitation.getJournalIndex();
		
		journal_area.setText( journalIndex.getTitle () );
		journal_area.append( "\n");
		journal_area.append ( journalIndex.getJournalInfo () + "  "+journalIndex.getYear () );

		repaint();
	}

	public Dimension getPreferredSize() {
		return new Dimension(250, 150);
	}
}
