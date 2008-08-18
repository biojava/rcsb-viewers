package org.rcsb.pw.glscene.jogl;

import java.awt.event.MouseEvent;

import org.rcsb.pw.controllers.app.ProteinWorkshop;
import org.rcsb.vf.glscene.jogl.VFGlGeometryViewer;


/**
 * Protein Workshop Viewer specific Geometry viewer.  Enables
 * PW-specific handling on events.
 * 
 * @author rickb
 *
 */
public class PWGlGeometryViewer extends VFGlGeometryViewer
{
	/**
	 * mouseClicked method.
	 */
	public void mouseClicked(final MouseEvent e)
	{
		super.mouseClicked(e);
		if (this.lastComponentMouseWasOver != null)
		{
			ProteinWorkshop.sgetSceneController().getMutatorEnum().getCurrentMutator()
					.toggleMutee(this.lastComponentMouseWasOver);
		}
	}
}
