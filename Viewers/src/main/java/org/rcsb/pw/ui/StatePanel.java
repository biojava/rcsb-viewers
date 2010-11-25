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
package org.rcsb.pw.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.rcsb.uiApp.controllers.doc.DocController;
import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.mbt.model.util.Status;
import org.rcsb.pw.controllers.app.ProteinWorkshop;
import org.rcsb.vf.controllers.scene.SceneState;



public class StatePanel extends JPanel implements LayoutManager, ActionListener, ListSelectionListener, MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3117546451238832042L;

	private final DefaultListModel listModel = new DefaultListModel();
	
	private final JButton captureCurrentViewerStateButton = new JButton("Capture current viewer state");
	private final JTextField titleField = new JTextField("<type title here>");
	private final JButton loadStateButton = new JButton("Import state");
	private final JList stateList = new JList(this.listModel);
	private final JScrollPane listScroller = new JScrollPane(this.stateList);
	private final JButton removeButton = new JButton("Remove selected state");
	private final JButton writeButton = new JButton("Export selected state");
	
	public StatePanel() {
		super(null);
		super.setLayout(this);
		super.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Capture and restore colors, camera location, etc."),
                BorderFactory.createEmptyBorder(-1,1,1,1)));
		
		this.stateList.getSelectionModel().addListSelectionListener(this);
		
		super.add(this.captureCurrentViewerStateButton);
		super.add(this.titleField);
		super.add(this.listScroller);
		super.add(this.loadStateButton);
		super.add(this.removeButton);
		super.add(this.writeButton);
		
		this.stateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		this.captureCurrentViewerStateButton.addActionListener(this);
		this.loadStateButton.addActionListener(this);
		this.removeButton.addActionListener(this);
		this.writeButton.addActionListener(this);
		
		// cause any selected list items to become deselected when the mouse enters something else...
		VFAppBase.sgetGlGeometryViewer().glCanvas.addMouseListener(this);
		ProteinWorkshop.sgetActiveFrame().getTreeViewer().tree.addMouseListener(this);
	}

	public void addLayoutComponent(final String name, final Component comp) {}

	public void removeLayoutComponent(final Component comp) {}

	private final Dimension size = new Dimension(-1,-1);
	public Dimension preferredLayoutSize(final Container parent) {
		this.layoutContainer(parent);
		return this.size;
	}

	public Dimension minimumLayoutSize(final Container parent) {
		this.layoutContainer(parent);
		return this.size;
	}

	private Dimension previousSize = new Dimension(-1,-1);
	public void layoutContainer(final Container parent) {
		final Insets insets = super.getInsets();
		
		final Dimension newSize = super.getSize();
		if(newSize.equals(this.previousSize)) {
			return;
		}
		this.previousSize = newSize;
		
		final Dimension capturePreferred = this.captureCurrentViewerStateButton.getPreferredSize();
		final Dimension titlePreferred = this.titleField.getPreferredSize();
		final Dimension loadPreferred = this.loadStateButton.getPreferredSize();
		final Dimension removePreferred = this.removeButton.getPreferredSize();
		final Dimension writePreferred = this.writeButton.getPreferredSize();
		
		final int captureLineHeight = Math.max(capturePreferred.height, titlePreferred.height); 
		final int removeWriteLineHeight = Math.max(removePreferred.height, writePreferred.height);
		final int listHeight = 90;
		final int verticalVisualBuffer = 3;
		final int horizontalVisualBuffer = 4;
		
		int curX = insets.left;
		int curY = insets.top;
		
		
		this.captureCurrentViewerStateButton.setBounds(curX, curY, capturePreferred.width, captureLineHeight);
		curX += capturePreferred.width + horizontalVisualBuffer;
		this.titleField.setBounds(curX, curY, super.getWidth() - curX - insets.right, captureLineHeight);
		curY += captureLineHeight + verticalVisualBuffer;
		
		curX = insets.left;
		this.loadStateButton.setBounds(curX, curY, loadPreferred.width, loadPreferred.height);
		curY += loadPreferred.height + verticalVisualBuffer;
		
		this.listScroller.setBounds(curX, curY, super.getWidth() - insets.left - insets.right, listHeight);
		curY += listHeight + verticalVisualBuffer;
		
		this.removeButton.setBounds(curX, curY, removePreferred.width, removeWriteLineHeight);
		curX += removePreferred.width + horizontalVisualBuffer;
		this.writeButton.setBounds(curX, curY, writePreferred.width, removeWriteLineHeight);
		curY += removeWriteLineHeight;
		
//		Insets parentInsets = super.getParent().getInsets();
		
		this.size.height = curY + insets.bottom;
		this.size.width = 345;
	}

	public void actionPerformed(final ActionEvent e) {
		final Object source = e.getSource();
		
		DocController docController = VFAppBase.sgetDocController();
		
		if(source == this.captureCurrentViewerStateButton) {
			final SceneState state = new SceneState();
			state.captureCurrentState(this.titleField.getText());
			this.listModel.addElement(state);
		}
		
		else if(source == this.loadStateButton)
		{
			final File file = docController.askUserForXmlFile("Load");
			final SceneState state = new SceneState();
			if (state.loadState(file))
				this.listModel.addElement(state);
			
			else
				JOptionPane.showMessageDialog(null, Status.getLastMessage(),
											  Status.getLevelName(Status.LEVEL_ERROR),
											  JOptionPane.ERROR_MESSAGE);
		}
		
		else if(source == this.removeButton)
		{
			final int selectedIndex = this.stateList.getSelectedIndex();
			if(selectedIndex >= 0)
				this.listModel.removeElementAt(selectedIndex);
			
		}
		
		else if(source == this.writeButton)
		{
			final SceneState state = (SceneState)this.stateList.getSelectedValue();
			if(state != null) {
				File file = docController.askUserForXmlFile("Write");
				if(file != null) {
					final String path = file.getAbsolutePath(); 
					if(!path.endsWith(".xml")) {
						file = new File(path + ".xml");
					}
					state.writeState(file);
				}
			}
		}
		
		VFAppBase.sgetGlGeometryViewer().requestRepaint();
	}

	public void valueChanged(final ListSelectionEvent e) {
		if(!e.getValueIsAdjusting()) {
			final SceneState state = (SceneState)this.stateList.getSelectedValue();
			if(state != null) {
				state.enact();
			}
		}
	}

	// the MouseListener methods below are currently for both the tree viewer and the geometry viewer
	public void mouseClicked(final MouseEvent e) {}
	public void mousePressed(final MouseEvent e) {}
	public void mouseReleased(final MouseEvent e) {}
	public void mouseEntered(final MouseEvent e) {
		this.stateList.clearSelection();
	}
	public void mouseExited(final MouseEvent e) {}
}
