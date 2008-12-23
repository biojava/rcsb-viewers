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
package org.rcsb.lx.ui.dialogs;

// GUI
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import org.rcsb.lx.controllers.app.LigandExplorer;
import org.rcsb.lx.glscene.jogl.LXGlGeometryViewer;
import org.rcsb.lx.ui.LXDocumentFrame;
import org.rcsb.mbt.model.geometry.ArrayLinearAlgebra;
import org.rcsb.mbt.model.util.*;


public class DistanceDialog extends JDialog implements IPickInfoReceiver {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6778406230456411103L;
	private Container contentPane = null;
	private final JPanel base = new JPanel();
	
	
	private final JLabel label1 = new JLabel();
	private final JTextField field1 = new JTextField(30);
	private final JLabel label2 = new JLabel();
	private final JTextField field2 = new JTextField(30);
	private double[] point1 = null;
	private double[] point2 = null;
	
	private final JButton okButton = new JButton("Execute");
	private final JButton cancelButton = new JButton("Cancel");
	
	private static final int FIELD_ONE = 0;
	private static final int FIELD_TWO = 1;
	
	private int activeField = DistanceDialog.FIELD_ONE;//is used to figure out which field should accept pick input from StructureViewer
	
	
	public DistanceDialog(LXDocumentFrame p)
	{	
		super((JFrame)null, "Measure distance", false);
	
		//create the panels
		this.contentPane = this.getContentPane();
		this.contentPane.setLayout(null);
		
		this.base.setPreferredSize(new Dimension(210, 130));
		this.base.setBorder(new BevelBorder(BevelBorder.RAISED));
		this.base.setLocation(new Point(5,5));
		this.base.setBounds(5, 5, 210, 130);
		this.base.setLayout(null);
		
		
		//object field
		this.label1.setForeground(Color.black);
		this.label1.setFont(new Font("Dialog", Font.PLAIN, 12));
		this.label1.setText("First atom: enter name or click");
		this.label1.setPreferredSize(new Dimension(170,20));
		this.label1.setBounds(15,15,170,20);
		this.label1.setLocation(new Point(15,15));
		this.label1.setVisible(true);
		
		this.field1.setPreferredSize(new Dimension(170,20));
		this.field1.setBounds(15,40,170,20);
		this.field1.setLocation(new Point(15,40));
		this.field1.setEditable(true);
		this.field1.setEnabled(true);
		this.field1.setVisible(true);
		this.field1.addMouseListener(new MouseAdapter(){
			public void mouseClicked(final MouseEvent m){
				DistanceDialog.this.activeField = DistanceDialog.FIELD_ONE;
			}
		});
		
		this.label2.setForeground(Color.black);
		this.label2.setFont(new Font("Dialog", Font.PLAIN, 12));
		this.label2.setText("Second atom: enter name or click");
		this.label2.setPreferredSize(new Dimension(190,20));
		this.label2.setBounds(15,65,190,20);
		this.label2.setLocation(new Point(15,65));
		this.label2.setVisible(true);
		
		this.field2.setPreferredSize(new Dimension(170,20));
		this.field2.setBounds(15,90,170,20);
		this.field2.setLocation(new Point(15,90));
		this.field2.setEditable(true);
		this.field2.setEnabled(true);
		this.field2.setVisible(true);
		this.field2.addMouseListener(new MouseAdapter(){
			public void mouseClicked(final MouseEvent m){
				DistanceDialog.this.activeField = DistanceDialog.FIELD_TWO;
			}
		});
		
		
		this.base.add(this.label1);
		this.base.add(this.field1);
		this.base.add(this.label2);
		this.base.add(this.field2);
		
		//buttons
		this.okButton.setPreferredSize(new Dimension(80,25));
		this.okButton.setLocation(new Point(20, 140));
		this.okButton.setBounds(20, 140, 80, 25);
		this.okButton.setVisible(true);
		this.okButton.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(final ActionEvent ae)
				{
					setVisible(false);					
					Status.output(Status.LEVEL_REMARK, "Distance: " + LXGlGeometryViewer.getDistString(ArrayLinearAlgebra.distance(DistanceDialog.this.point1, DistanceDialog.this.point2)));
					dispose();
				}
			});
		
		
		this.cancelButton.setPreferredSize(new Dimension(80,25));
		this.cancelButton.setLocation(new Point(120, 140));
		this.cancelButton.setBounds(120, 140, 80, 25);
		this.cancelButton.setVisible(true);
		this.cancelButton.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(final ActionEvent ae)
				{
					setVisible(false);
					dispose();
				}
			});
		
		
		this.contentPane.add(this.base);
		this.contentPane.add(this.okButton);
		this.contentPane.add(this.cancelButton);
		
		this.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(final WindowEvent e)
			{
				setVisible(false);
				dispose();
			}
			
			public void windowDeactivated(final WindowEvent e)
			{
				toFront();
			}
		});
		
		
		
		this.setSize(230, 195);

		//add this to accommodate both applet and application cases
		if (p == null)
			this.setLocationRelativeTo(null);

		else
		{
			//set location
			final Dimension d1 = this.getSize();
			final Dimension d2 = p.getSize();
		
//			int x = Math.max((d2.width - d1.width)/2, 0);
//			int y = Math.max((d2.height - d1.height)/2, 0);

			final int x = 5;
			final int y = 45;
		
			this.setBounds(x + p.getX(), y + p.getY(), d1.width, d1.height);
		}

		setVisible(true);
	}
	
	public void processPick(final double[] point, final String description_){
		String description = description_;
		if(point == null) {
			description = "";
		}
		
		if (this.activeField == DistanceDialog.FIELD_ONE){
			this.point1 = point;
			this.field1.setText(description);
			if(point != null) {
				this.activeField = DistanceDialog.FIELD_TWO;
				this.field2.requestFocus();
			}
		} else {	//FIELD_TWO
			this.point2 = point;
			this.field2.setText(description);
		}
	}
	
}
