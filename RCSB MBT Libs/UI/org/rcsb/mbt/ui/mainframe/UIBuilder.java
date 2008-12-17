package org.rcsb.mbt.ui.mainframe;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.doc.LoadThread;
import org.rcsb.mbt.model.util.Status;
import org.rcsb.mbt.ui.FileLocs;

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
	protected JComponent preFileExitSeparator = null;
	
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

							dialog.setDialogTitle("Select a coordinate file...");
							dialog.setDialogType(JFileChooser.OPEN_DIALOG);
							dialog.setFileHidingEnabled(true);
							dialog.setMultiSelectionEnabled(true);
							
							if (dialog.showOpenDialog(AppBase.sgetActiveFrame()) == JFileChooser.APPROVE_OPTION)
							{
								LoadThread loadIt = new LoadThread(dialog.getSelectedFiles());										
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
											"Please enter a PDB ID (4 characters):",
											"Open a structure...",
											JOptionPane.INFORMATION_MESSAGE);

							if (pdbId != null)
								pdbId = pdbId.trim();

							if (pdbId != null && pdbId.length() != 0)
							{
								if (pdbId.length() == 4)
								{
									final String url = FileLocs.pdbFileBase + pdbId + FileLocs.pdbFileExtension;
									LoadThread loadIt = new LoadThread(url, pdbId);										
									loadIt.start();
								}
								
								else
									Status.output(Status.LEVEL_ERROR,
													"The PDB ID you entered was not valid.");
							}
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
									loadIt.start();
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
				preFileExitSeparator = new JSeparator();
				fileMenu.add(preFileExitSeparator);
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
	protected void insertMenuItemBefore(JMenu menu, JComponent beforeItem, JMenuItem insertItem)
	{
		if (beforeItem == null)
			menu.add(insertItem);
		
		else
			for (int ix = 0; ix < menu.getItemCount(); ix++)
				if (menu.getItem(ix) == beforeItem)
				{
					menu.insert(insertItem, ix);
					break;
				}				
	}
}

	