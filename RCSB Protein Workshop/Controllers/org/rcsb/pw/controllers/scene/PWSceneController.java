package org.rcsb.pw.controllers.scene;

import org.rcsb.mbt.glscene.controller.SceneController;
import org.rcsb.mbt.glscene.jogl.GvPickEvent;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.pw.controllers.app.ProteinWorkshop;
import org.rcsb.pw.controllers.scene.mutators.Mutator;
import org.rcsb.pw.controllers.scene.mutators.MutatorEnum;


public class PWSceneController extends SceneController
{
	private final MutatorEnum mutatorEnum = new MutatorEnum();
    public MutatorEnum getMutatorEnum()
    {
        return this.mutatorEnum;
    }
    
	public void processPickEvent(final GvPickEvent pickEvent)
	{
		final StructureComponent sc = pickEvent.structureComponent;

		Mutator mut = mutatorEnum.getCurrentMutator();
		
		// Do the selection.
		mut.setShiftDown(pickEvent.mouseEvent.isShiftDown());
		mut.setCtrlDown(pickEvent.mouseEvent.isControlDown());
		mut.setConsiderClickModifiers(true);
		mut.setConsiderSelectedFlag(false);
		if (areSelectionsEnabled())
		{
			if (mut.supportsBatchMode())
				mut.toggleMutee(sc);
			else
				mut.doMutationSingle(sc);
		}
	}
}
