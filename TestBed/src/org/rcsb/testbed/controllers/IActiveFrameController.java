package org.rcsb.testbed.controllers;

import org.rcsb.testbed.UI.DocumentFrameBase;

/**
 * This abstracts the interface for controlling active frames.  Can be implemented over
 * tabbed, MDI, etc.
 * 
 * @author rickb
 *
 */
public interface IActiveFrameController
{
	public DocumentFrameBase getActiveFrame();
	public void setActiveFrame(DocumentFrameBase frame);
}
