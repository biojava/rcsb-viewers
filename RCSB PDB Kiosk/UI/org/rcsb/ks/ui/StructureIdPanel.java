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
