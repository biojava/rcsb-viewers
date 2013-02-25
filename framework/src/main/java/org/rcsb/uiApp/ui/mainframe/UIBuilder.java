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
package org.rcsb.uiApp.ui.mainframe;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import org.rcsb.mbt.model.util.Status;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.uiApp.controllers.doc.LoadThread;

/**
 * This is a swing runnable - it is invoked to build the ActiveFrame UI at the appropriate
 * moment.  Derived frames will derive from this, as well, and override 'run()'.
 * 
 * App-specific UI gets created in the overridden 'run()' function.
 * 
 * @author rickb
 *
 */
public class UIBuilder implements Runnable
{
	/**
	 * Swing thread runner.  Override and implement app-specific ActiveFrame UI, here.
	 */

	protected JMenuBar menuBar;
	protected JMenu fileMenu;	
	protected StatusPanel statusPanel = null;
	protected int fileMenuPreSeparatorIX;
	
	public void run()
	{
		Status.progress(-1, "Creating UI...");
		//////////////////////////////////////////////////////////////////////
		AppBase.sgetActiveFrame().setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		if (!AppBase.backgroundScreenshotOnly)
		{
			if (AppBase.getApp().isApplication())
			{
				//////////////////////////////////////////////////////////////////////
				// BEG define menu and file open dialog
				//////////////////////////////////////////////////////////////////////
				menuBar = new JMenuBar();
				fileMenu = new JMenu("File");
				
				final JMenuItem openFileItem = new JMenuItem("Open File...");
				final JMenuItem openUrlItem = new JMenuItem("Open URL...");
				final JMenuItem openPdbIdItem = new JMenuItem("Open PDB ID...");
				final JMenuItem exitItem = new JMenuItem("Exit");

				openFileItem.addActionListener(
					new ActionListener()
					{
						public void actionPerformed(final ActionEvent e)
						{
							final JFileChooser dialog = new JFileChooser();
							dialog.setFileFilter(new FileFilter()
							{
								public boolean accept(final File f)
								{
									final String fileString = f.getName();
									return f.isDirectory()
											|| fileString.endsWith(".pdb")
											|| fileString.endsWith(".ent")
											|| fileString.endsWith(".xml")
											|| fileString.endsWith(".pdb.gz")
											|| fileString.endsWith(".ent.gz")
											|| fileString.endsWith(".xml.gz");
								}

								public String getDescription()
								{
									return "structures (.pdb, .ent, .xml)";
								}
							});

							dialog.setDialogTitle("Select one or more coordinate file(s) ...");
							dialog.setDialogType(JFileChooser.OPEN_DIALOG);
							dialog.setFileHidingEnabled(true);
							dialog.setMultiSelectionEnabled(true);
							
							if (dialog.showOpenDialog(AppBase.sgetActiveFrame()) == JFileChooser.APPROVE_OPTION)
							{
								
								LoadThread loadIt = new LoadThread(dialog.getSelectedFiles());
								SwingUtilities.invokeLater(loadIt);
																		
								loadIt.start();
							}
						}
					});

				openPdbIdItem.addActionListener(
					new ActionListener()
					{
						public void actionPerformed(final ActionEvent e)
						{
							String pdbId = JOptionPane.showInputDialog(
											AppBase.sgetActiveFrame(),
											"Please enter one or more PDB ID(s) separated by commas:",
											"Open a structure...",
											JOptionPane.INFORMATION_MESSAGE);

							if (pdbId == null) {
								return;
							}
							
							String[] pdbIds = pdbId.split(",");
						
							for (int i = 0; i < pdbIds.length; i++) {
								pdbIds[i] = pdbIds[i].trim();
								if (pdbIds[i].length() != 4) {
									Status.output(Status.LEVEL_ERROR,
									"The PDB ID you entered was not valid: " + pdbIds[i]);
									return;
								}
							}
							LoadThread loadIt = new LoadThread(pdbIds);
							SwingUtilities.invokeLater(loadIt);

						}

					});

				openUrlItem.addActionListener(
					new ActionListener()
					{
					public void actionPerformed(final ActionEvent e)
					{
						String url = JOptionPane.showInputDialog(
										AppBase.sgetActiveFrame(),
										"Please enter a URL (beginning with http:// or ftp://):",
										"Open a structure...",
										JOptionPane.INFORMATION_MESSAGE);

						if (url != null)
						{
							url = url.trim();
							if (url.length() > 0)
							{
								if (url.indexOf("://") >= 0)
								{
									String pdbspec = url.substring(url.lastIndexOf('/') + 1);										
									LoadThread loadIt = new LoadThread(url, pdbspec.substring(0, pdbspec.indexOf('.')));										
									SwingUtilities.invokeLater(loadIt);
								}
								
								else
									JOptionPane.showMessageDialog(null, "The URL you entered did not contain a protocol (http://, etc.)", "Error", JOptionPane.ERROR_MESSAGE);
							}
						}
					}

				});

				exitItem.addActionListener(
					new ActionListener()
					{
						public void actionPerformed(final ActionEvent e)
						{
							System.exit(0);
										// I don't think there are any complications from doing this here -
										// an alternative is to spawn a thread and do it from there, maybe wait
										// for this thread to process all its events, and then invoke the exit.
										//
										// 04-Sep-08 - rickb
						}
					}
				);

				fileMenu.add(openFileItem);
				fileMenu.add(openUrlItem);
				fileMenu.add(openPdbIdItem);
				fileMenuPreSeparatorIX = fileMenu.getItemCount();
				fileMenu.addSeparator();
				fileMenu.add(exitItem);
				menuBar.add(fileMenu);

				AppBase.sgetActiveFrame().setJMenuBar(menuBar);
				//////////////////////////////////////////////////////////////////////
				// END define menu and file open dialog
				//////////////////////////////////////////////////////////////////////
				
				// Create a StatusPanel
				statusPanel = new StatusPanel();
				AppBase.sgetActiveFrame().getContentPane().add(BorderLayout.SOUTH, statusPanel);
			}
		}
	}
	
	/**
	 * Insert a menu item before the specified item.  Helps keep the menu list ordered
	 * reasonably.
	 * 
	 * @param menu
	 * @param beforeItem
	 * @param insertItem
	 */
	protected void insertMenuItemBefore(JMenu menu, int afterItemIX, JMenuItem insertItem)
	{
		if (afterItemIX == -1)
			menu.add(insertItem);
		
		else
			menu.insert(insertItem, afterItemIX);
	}
}

	
