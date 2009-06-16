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

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.JFrame;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.uiApp.controllers.doc.DocController;
import org.rcsb.uiApp.controllers.update.UpdateController;
import org.rcsb.uiApp.model.UIAppStructureModel;


/**
 * <p>
 * We introduce the notion of an 'Document Frame'.  The Document Frame contains a complete
 * document representation, with views (3d view, UI panel) related to that document.</p>
 * <p>
 * Note MDI functionality isn't currently supported - this is just a design implementation
 * with an eye towards facilitating that, should we desire.  Things like the menubar
 * and statusbar creation and maintaince would have to be broken out as makes sense for
 * that kind of architecture.</p>
 * <p>
 * In the normal context, this would be the 'MainFrame'.  However, should we wish to support
 * multiple documents in multiple frames (i.e. MS 'MDI' mode, the Active Frame would be derived
 * from an MDI Frame equivalent.</p>
 * 
 * @author rickb
 *
 */
public abstract class DocumentFrameBase extends JFrame
{
	private static final long serialVersionUID = 682613576170299667L;	
	
	protected boolean _showFrame;
	public boolean showFrame() { return _showFrame; }
	
	/**
	 * The doc controller has all the machinery to load, save, and parse documents (files).
	 */
	private DocController docController = null;
	public DocController getDocController()
	{
		if (docController == null) docController = AppBase.sgetAppModuleFactory().createDocController();
		return docController;
	}
	
	/**
	 * This is the molecule model.  Contains all of the structures.
	 */
	private UIAppStructureModel model = null;
	public UIAppStructureModel getModel()
	{
		if (model == null) model = (UIAppStructureModel)AppBase.sgetAppModuleFactory().createModel();
		return model;
	}

	private UpdateController _updateController = null;
	public UpdateController getUpdateController()
	{
		if (_updateController == null) _updateController = AppBase.sgetAppModuleFactory().createUpdateController();
		return _updateController;
	}
	
	protected Dimension curSize;
	
	/**
	 * Constructor
	 * @throws HeadlessException
	 */
	public DocumentFrameBase(String title, URL iconUrl) throws HeadlessException
	{
		super(title);
		if (iconUrl != null)
			this.setIconImage(Toolkit.getDefaultToolkit().createImage(iconUrl));
	}


	public void initialize(boolean showFrame)
	{
		_showFrame = showFrame;
	}
}
