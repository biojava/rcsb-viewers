package org.rcsb.testbed.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JToolBar;

import org.rcsb.testbed.glscene.GLViewer;


@SuppressWarnings("serial")
public abstract class GLDocumentFrameBase extends DocumentFrameBase implements ActionListener
{
	protected JToolBar ctrlBar;
	
	public GLDocumentFrameBase()
	{
		super();
		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);
		
		setBackground(Color.black);
		
		ctrlBar = new JToolBar(JToolBar.VERTICAL);
		add(ctrlBar,BorderLayout.EAST);
		
		JButton resetButton = new JButton();
		resetButton.setText("Reset");
		resetButton.setEnabled(true);
		resetButton.addActionListener(this);
		ctrlBar.add(resetButton);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals("Reset"))
		{			
		}
	}
	
	public void removeGLViewer(GLViewer glViewer)
	{
		remove(glViewer);
	}
	
	public void setGLViewer(GLViewer glViewer)
	{
		add(glViewer, BorderLayout.CENTER);
	}
}
