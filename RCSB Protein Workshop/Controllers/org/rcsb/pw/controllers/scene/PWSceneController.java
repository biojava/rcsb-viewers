package org.rcsb.pw.controllers.scene;

import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.pw.controllers.scene.mutators.MutatorEnum;
import org.rcsb.vf.controllers.scene.SceneController;
import org.rcsb.vf.controllers.scene.mutators.MutatorBase;
import org.rcsb.vf.glscene.jogl.GvPickEvent;

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

		MutatorBase mut = mutatorEnum.getCurrentMutator();
		
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
