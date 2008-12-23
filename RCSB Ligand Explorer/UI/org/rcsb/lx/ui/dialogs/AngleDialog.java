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
//Copyright (c) 2000-2002  San Diego Supercomputer Center (SDSC),
//a facility operated jointly by the University of California,
//San Diego (UCSD) and General Atomics, San Diego, California, USA.
//
//Users and possessors of this source code are hereby granted a
//nonexclusive, royalty-free copyright and design patent license to
//use this code in individual software.  License is not granted for
//commercial resale, in whole or in part, without prior written
//permission from SDSC.  This source is provided "AS IS" without express
//or implied warranty of any kind.
//
//For further information, please see:  http://mbt.sdsc.edu
//

package org.rcsb.lx.ui.dialogs;

// GUI
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import org.rcsb.lx.glscene.jogl.LXGlGeometryViewer;
import org.rcsb.lx.ui.LXDocumentFrame;
import org.rcsb.mbt.model.geometry.ArrayLinearAlgebra;
import org.rcsb.mbt.model.util.*;


/**
 *  This class impements a dialog box used to input the three atoms required to calculate a bond angle.
 *  <P>
 *  @see org.rcsb.mbt.viewers.StructureViewer
 *  @see org.rcsb.lx.ui.dialogs.IPickInfoReceiver
 *  <P>
 *  @author Oleksandr V. Buzko
 */

public class AngleDialog extends JDialog implements IPickInfoReceiver
{
	private static final long serialVersionUID = -6986349982417631607L;
	private Container contentPane = null;
	private final JPanel base = new JPanel();
	
	
	private final JLabel label1 = new JLabel();
	private final JTextField field1 = new JTextField(30);
	private final JLabel label2 = new JLabel();
	private final JTextField field2 = new JTextField(30);
	private final JLabel label3 = new JLabel();
	private final JTextField field3 = new JTextField(30);
	private double[] point1 = null;
	private double[] point2 = null;
	private double[] point3 = null;
	
	private static final int FIELD_ONE = 0;
	private static final int FIELD_TWO = 1;
	private static final int FIELD_THREE = 2;
	
	private final JButton okButton = new JButton("Execute");
	private final JButton cancelButton = new JButton("Cancel");
	
	private int activeField = AngleDialog.FIELD_ONE;//is used to figure out which field should accept pick input from StructureViewer
	
	/**
	 * This method creates an instance of the AngleDialog class.
	 * @see org.rcsb.lx.ui.dialogs.IPickInfoReceiver
	 * @param f
	 * @param p
	 * @param s
	 */
	public AngleDialog(final LXDocumentFrame p){
	
		super((JFrame)null, "Measure bond angle", false);

		//create the panels
		this.contentPane = this.getContentPane();
		this.contentPane.setLayout(null);
		
		this.base.setPreferredSize(new Dimension(210, 180));
		this.base.setBorder(new BevelBorder(BevelBorder.RAISED));
		this.base.setLocation(new Point(5,5));
		this.base.setBounds(5, 5, 210, 180);
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
			@Override
			public void mouseClicked(final MouseEvent m){
				AngleDialog.this.activeField = AngleDialog.FIELD_ONE;
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
			@Override
			public void mouseClicked(final MouseEvent m){
				AngleDialog.this.activeField = AngleDialog.FIELD_TWO;
			}
		});
		
		this.label3.setForeground(Color.black);
		this.label3.setFont(new Font("Dialog", Font.PLAIN, 12));
		this.label3.setText("Third atom: enter name or click");
		this.label3.setPreferredSize(new Dimension(190,20));
		this.label3.setBounds(15,115,190,20);
		this.label3.setLocation(new Point(15,115));
		this.label3.setVisible(true);
		
		this.field3.setPreferredSize(new Dimension(170,20));
		this.field3.setBounds(15,140,170,20);
		this.field3.setLocation(new Point(15,140));
		this.field3.setEditable(true);
		this.field3.setEnabled(true);
		this.field3.setVisible(true);
		this.field3.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(final MouseEvent m){
				AngleDialog.this.activeField = AngleDialog.FIELD_THREE;
			}
		});
		
		
		this.base.add(this.label1);
		this.base.add(this.field1);
		this.base.add(this.label2);
		this.base.add(this.field2);
		this.base.add(this.label3);
		this.base.add(this.field3);
		
		//buttons
		this.okButton.setPreferredSize(new Dimension(80,25));
		this.okButton.setLocation(new Point(20, 190));
		this.okButton.setBounds(20, 190, 80, 25);
		this.okButton.setVisible(true);
		this.okButton.addActionListener(new ActionListener(){
			public void actionPerformed(final ActionEvent ae){
			
				//add error checking for selection bounds
				//eg., selection level is residue, but entered only structure name
			
				AngleDialog.this.setVisible(false);

				Status.output(Status.LEVEL_REMARK, "Angle: " + LXGlGeometryViewer.getDistString(ArrayLinearAlgebra.angle(AngleDialog.this.point1, AngleDialog.this.point2, AngleDialog.this.point3)));

				dispose();
			}
		});
		
		
		this.cancelButton.setPreferredSize(new Dimension(80,25));
		this.cancelButton.setLocation(new Point(120, 190));
		this.cancelButton.setBounds(120, 190, 80, 25);
		this.cancelButton.setVisible(true);
		this.cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(final ActionEvent ae){
				AngleDialog.this.setVisible(false);
				AngleDialog.this.dispose();
			}
		});
		
		
		this.contentPane.add(this.base);
		this.contentPane.add(this.okButton);
		this.contentPane.add(this.cancelButton);
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				AngleDialog.this.setVisible(false);
				AngleDialog.this.dispose();
			}
			
			@Override
			public void windowDeactivated(final WindowEvent e){
				AngleDialog.this.toFront();
			}
		});
		
		
		
		this.setSize(230, 245);

		//add this to accommodate both applet and application cases
		if (p == null){
			this.setLocationRelativeTo(null);
		}
		else
		{
		
			//set location
			final Dimension d1 = getSize();
			final Dimension d2 = p.getSize();
		
//			int x = Math.max((d2.width - d1.width)/2, 0);
//			int y = Math.max((d2.height - d1.height)/2, 0);

			final int x = 5;
			final int y = 45;
		
			this.setBounds(x + p.getX(), y + p.getY(), d1.width, d1.height);
		}

		this.setVisible(true);
	}
	
	/**
	 * This method is called by StructureViewer in response to a mouse click and sets the currently
	 * active text field to the String representation of the clicked StructureComponent.
	 * @see org.rcsb.mbt.viewers.StructureViewer
	 * @see org.rcsb.mbt.model.StructureComponent
	 */
	public void processPick(final double[] point, final String description_){
		String description = description_;
		if(point == null) {
			description = "";
		}
		
		if (this.activeField == AngleDialog.FIELD_ONE){
			this.point1 = point;
			this.field1.setText(description);
			if(point != null) {
				this.activeField = AngleDialog.FIELD_TWO;
				this.field2.requestFocus();
			}
		} else if (this.activeField == AngleDialog.FIELD_TWO){
			this.point2 = point;
			this.field2.setText(description);
			if(point != null) {
				this.activeField = AngleDialog.FIELD_THREE;
				this.field3.requestFocus();
			}
		} else {	//FIELD_THREE
			this.point3 = point;
			this.field3.setText(description);
		}
	}
	
}
