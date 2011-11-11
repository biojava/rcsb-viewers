

import org.rcsb.ks.controllers.app.SlideShow;
import org.rcsb.lx.controllers.app.LigandExplorer;
import org.rcsb.pw.controllers.app.ProteinWorkshop;
import org.rcsb.sv.controllers.app.SimpleViewer;

public class Demo {

	public static void main(String[] args){
		//showSimpleViewer();
		showProteinWorkshop();
		//showLigandExplorer();
		//showKiosk();
	}

	public static void showSimpleViewer(){
		final SimpleViewer app = new SimpleViewer(null);
		app.initialize(true, true);
	}
	
	public static void showProteinWorkshop(){
		final ProteinWorkshop app = new ProteinWorkshop(null);	
		app.initialize(true, true);
	}
	
	public static void showLigandExplorer() {
		final LigandExplorer app = new LigandExplorer(null);		
		app.initialize(true);
		
	}
	
	public static void showKiosk() {
		
		String[] pdbIds = new String[]{"-structure_id_list","4hhb,1cdg"};
		SlideShow show = new SlideShow(pdbIds);
		show.run();
	}
	
}
