package org.rcsb.vf.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.glscene.jogl.Constants;
import org.rcsb.mbt.model.util.Status;
import org.rcsb.mbt.ui.mainframe.DocumentFrameBase;
import org.rcsb.mbt.ui.mainframe.StatusPanel;
import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.vf.controllers.doc.VFDocController;
import org.rcsb.vf.glscene.jogl.VFGlGeometryViewer;



public abstract class VFDocumentFrameBase extends DocumentFrameBase
{
	public VFGlGeometryViewer getGlGeometryViewer() { return (VFGlGeometryViewer)super.getGlGeometryViewer(); }
	public VFDocController getDocController() { return (VFDocController)super.getDocController(); }

	protected class UIBuilder extends DocumentFrameBase.UIBuilder
	{
		protected JMenuBar menuBar;
		protected JMenu fileMenu;		
		protected StatusPanel statusPanel = null;

		public void run()
		{
			super.run();
			
			if (!AppBase.backgroundScreenshotOnly)
			{
				if (VFAppBase.getApp().isApplication())
				{
					//////////////////////////////////////////////////////////////////////
					// BEG define menu and file open dialog
					//////////////////////////////////////////////////////////////////////
					menuBar = new JMenuBar();
					fileMenu = new JMenu("File");
					
					final JMenuItem openFileItem = new JMenuItem("Open File...");
					final JMenuItem openUrlItem = new JMenuItem("Open URL...");
					final JMenuItem openPdbIdItem = new JMenuItem("Open PDB ID...");

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
								dialog.setMultiSelectionEnabled(false);
								
								if (dialog.showOpenDialog(VFDocumentFrameBase.this) == JFileChooser.APPROVE_OPTION)
								{
									final File selectedFile = dialog.getSelectedFile();

									getModel().clear();
									getDocController().loadStructure(
													selectedFile.getAbsolutePath(),
													selectedFile.getName());
									getUpdateController().resetEverything();
								}
							}
						});

					openPdbIdItem.addActionListener(
						new ActionListener()
						{
							public void actionPerformed(final ActionEvent e)
							{
								String pdbId = JOptionPane.showInputDialog(
												VFDocumentFrameBase.this,
												"Please enter a PDB ID (4 characters):",
												"Open a structure...",
												JOptionPane.INFORMATION_MESSAGE);

								if (pdbId != null)
									pdbId = pdbId.trim();

								if (pdbId != null && pdbId.length() != 0)
								{
									if (pdbId.length() == 4)
									{
										final String url = Constants.pdbFileBase
												+ pdbId
												+ Constants.pdbFileExtension;
										getModel().clear();
										getDocController().loadStructure(url, pdbId);
										getUpdateController().resetEverything();
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
											VFDocumentFrameBase.this,
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
										getModel().clear();
										getDocController().loadStructure(url, url);
										getUpdateController().resetEverything();
									}
									
									else
										Status.output(Status.LEVEL_ERROR,
													  "The URL you entered did not contain a protocol (http://, etc.)");
								}
							}
						}

					});

					fileMenu.add(openFileItem);
					fileMenu.add(openUrlItem);
					fileMenu.add(openPdbIdItem);
					menuBar.add(fileMenu);

					VFDocumentFrameBase.this.setJMenuBar(menuBar);
					//////////////////////////////////////////////////////////////////////
					// END define menu and file open dialog
					//////////////////////////////////////////////////////////////////////
					
					// Create a StatusPanel
					statusPanel = new StatusPanel();
					getContentPane().add(BorderLayout.SOUTH, statusPanel);
				}
			}
		}				
	}
	
	public VFDocumentFrameBase(String title)
	{
		super(title);
		
		try
		{
			SwingUtilities.invokeAndWait(
				new Runnable()
				{
					public void run()
					{
						// set up the standard tool tips
						final ToolTipManager ttm = ToolTipManager.sharedInstance();
						ttm.setLightWeightPopupEnabled(false); // only heavy
						// components show
						// above the 3d
						// viewer.
						ttm.setDismissDelay(20000); // wait 20 seconds before the
						// tooltip disappears.
		
						JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		
						try
						{
							UIManager.setLookAndFeel(UIManager
									.getSystemLookAndFeelClassName());
							// UIManager.setLookAndFeel("net.java.plaf.windows.WindowsLookAndFeel");
						}
						
						catch (final Exception e) {}
					}
				});
		}
		
		catch (final InterruptedException e)
		{
			e.printStackTrace();
		}
		
		catch (final InvocationTargetException e)
		{
			e.printStackTrace();
		}
	}
}
