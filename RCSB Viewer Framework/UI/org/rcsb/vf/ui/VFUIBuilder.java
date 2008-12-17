package org.rcsb.vf.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import org.rcsb.mbt.ui.mainframe.UIBuilder;
import org.rcsb.vf.controllers.app.VFAppBase;

public class VFUIBuilder extends UIBuilder
{
	
	
	@Override
	public void run()
	{
		super.run();
		
		final JMenuItem saveImageItem = new JMenuItem("Save Image...");					
		/**
		 * Save Image Item Listener
		 */
		saveImageItem.addActionListener(
			new ActionListener()
				{
				public void actionPerformed(ActionEvent arg0) {
					VFAppBase.sgetDocController().saveImage();
				}
			});

		insertMenuItemBefore(fileMenu, preFileExitSeparator, saveImageItem);
		fileMenu.add(saveImageItem);
						// doesn't work - we need to insert before the separator...
	}
}
