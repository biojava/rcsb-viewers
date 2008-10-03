package org.rcsb.lx.ui.dialogs;

// Core

// GUI
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import org.rcsb.lx.glscene.jogl.LXGlGeometryViewer;
import org.rcsb.lx.ui.LXDocumentFrame;
import org.rcsb.mbt.model.geometry.Algebra;
import org.rcsb.mbt.model.util.*;

// MBT

public class DihedralDialog extends JDialog implements IPickInfoReceiver {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8199319127539863277L;
	private Container contentPane = null;
	private final JPanel base = new JPanel();
	
	
	private final JLabel label1 = new JLabel();
	private final JTextField field1 = new JTextField(30);
	private final JLabel label2 = new JLabel();
	private final JTextField field2 = new JTextField(30);
	private final JLabel label3 = new JLabel();
	private final JTextField field3 = new JTextField(30);
	private final JLabel label4 = new JLabel();
	private final JTextField field4 = new JTextField(30);
	private double[] point1 = null;
	private double[] point2 = null;
	private double[] point3 = null;
	private double[] point4 = null;
	
	private static final int FIELD_ONE = 0;
	private static final int FIELD_TWO = 1;
	private static final int FIELD_THREE = 2;
	private static final int FIELD_FOUR = 3;
	
	private final JButton okButton = new JButton("Execute");
	private final JButton cancelButton = new JButton("Cancel");
	
	private int activeField = DihedralDialog.FIELD_ONE;//is used to figure out which field should accept pick input from StructureViewer
	
	
	public DihedralDialog(final LXDocumentFrame p){
	
		super((JFrame)null, "Measure dihedral angle", false);
	
		//create the panels
		this.contentPane = this.getContentPane();
		this.contentPane.setLayout(null);
		
		this.base.setPreferredSize(new Dimension(210, 230));
		this.base.setBorder(new BevelBorder(BevelBorder.RAISED));
		this.base.setLocation(new Point(5,5));
		this.base.setBounds(5, 5, 210, 230);
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
				DihedralDialog.this.activeField = DihedralDialog.FIELD_ONE;
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
				DihedralDialog.this.activeField = DihedralDialog.FIELD_TWO;
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
			public void mouseClicked(final MouseEvent m){
				DihedralDialog.this.activeField = DihedralDialog.FIELD_THREE;
			}
		});
		
		this.label4.setForeground(Color.black);
		this.label4.setFont(new Font("Dialog", Font.PLAIN, 12));
		this.label4.setText("Fourth atom: enter name or click");
		this.label4.setPreferredSize(new Dimension(190,20));
		this.label4.setBounds(15,165,190,20);
		this.label4.setLocation(new Point(15,165));
		this.label4.setVisible(true);
		
		this.field4.setPreferredSize(new Dimension(170,20));
		this.field4.setBounds(15,190,170,20);
		this.field4.setLocation(new Point(15,190));
		this.field4.setEditable(true);
		this.field4.setEnabled(true);
		this.field4.setVisible(true);
		this.field4.addMouseListener(new MouseAdapter(){
			public void mouseClicked(final MouseEvent m){
				DihedralDialog.this.activeField = DihedralDialog.FIELD_FOUR;
			}
		});
		
		
		this.base.add(this.label1);
		this.base.add(this.field1);
		this.base.add(this.label2);
		this.base.add(this.field2);
		this.base.add(this.label3);
		this.base.add(this.field3);
		this.base.add(this.label4);
		this.base.add(this.field4);
		
		//buttons
		this.okButton.setPreferredSize(new Dimension(80,25));
		this.okButton.setLocation(new Point(20, 240));
		this.okButton.setBounds(20, 240, 80, 25);
		this.okButton.setVisible(true);
		this.okButton.addActionListener(new ActionListener(){
			public void actionPerformed(final ActionEvent ae){
			
				//add error checking for selection bounds
				//eg., selection level is residue, but entered only structure name
			
				setVisible(false);

				Status.output(Status.LEVEL_REMARK, "Dihedral Angle: " + LXGlGeometryViewer.getDistString(Algebra.dihedralAngle(DihedralDialog.this.point1, DihedralDialog.this.point2, DihedralDialog.this.point3, DihedralDialog.this.point4)));

				dispose();
			}
		});
		
		
		this.cancelButton.setPreferredSize(new Dimension(80,25));
		this.cancelButton.setLocation(new Point(120, 240));
		this.cancelButton.setBounds(120, 240, 80, 25);
		this.cancelButton.setVisible(true);
		this.cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(final ActionEvent ae){
				setVisible(false);
				dispose();
			}
		});
		
		
		this.contentPane.add(this.base);
		this.contentPane.add(this.okButton);
		this.contentPane.add(this.cancelButton);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(final WindowEvent e) {
				setVisible(false);
				dispose();
			}
			
			public void windowDeactivated(final WindowEvent e){
				DihedralDialog.this.toFront();
			}
		});
		
		
		
		this.setSize(230, 295);

		//add this to accommodate both applet and application cases
		if (p == null){
			this.setLocationRelativeTo(null);
		}
		else{
		
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
	
	public void processPick(final double[] point, final String description_){
		String description = description_;
		if(point == null) {
			description = "";
		}
		
		if (this.activeField == DihedralDialog.FIELD_ONE){
			this.point1 = point;
			this.field1.setText(description);
			if(point != null) {
				this.activeField = DihedralDialog.FIELD_TWO;
				this.field2.requestFocus();
			}
		} else if (this.activeField == DihedralDialog.FIELD_TWO){
			this.point2 = point;
			this.field2.setText(description);
			if(point != null) {
				this.activeField = DihedralDialog.FIELD_THREE;
				this.field3.requestFocus();
			}
		} else if (this.activeField == DihedralDialog.FIELD_THREE){
			this.point3 = point;
			this.field3.setText(description);
			if(point != null) {
				this.activeField = DihedralDialog.FIELD_FOUR;
				this.field4.requestFocus();
			}
		} else {	//FIELD_FOUR
			this.point4 = point;
			this.field4.setText(description);
		}
	}
	
}
