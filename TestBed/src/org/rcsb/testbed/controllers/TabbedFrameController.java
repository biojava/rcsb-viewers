package org.rcsb.testbed.controllers;

import javax.swing.JTabbedPane;

import org.rcsb.testbed.UI.DocumentFrameBase;



/**

 * @author rickb
 *
 */
@SuppressWarnings("serial")
public class TabbedFrameController extends JTabbedPane implements IActiveFrameController
{
	private DocumentFrameBase activeDocument = null;
	
	public void setActiveFrame(DocumentFrameBase frame)
	{
		this.setSelectedComponent(frame);
	}
	
	public DocumentFrameBase getActiveFrame()
	{
		return (DocumentFrameBase)getSelectedComponent();
	}
}
