package demo;

import org.rcsb.ks.controllers.app.SlideShow;
import org.rcsb.lx.controllers.app.LigandExplorer;
import org.rcsb.pw.controllers.app.ProteinWorkshop;
import org.rcsb.sv.controllers.app.SimpleViewer;

import fr.iscpif.jogl.JOGLWrapper;

public class Demo {

	public static void main(String[] args){
		JOGLWrapper.init();

		//showSimpleViewer();
	    // showProteinWorkshop();
		 showLigandExplorer();
		//showKiosk();
	}

	public static void showSimpleViewer(){
		String[] args = new String[]{"-structure_url","http://www.rcsb.org:80/pdb/files/1STP.xml.gz","-unit_id","1","-standalone"};
		final SimpleViewer app = new SimpleViewer(args);
		app.initialize(true, true);
	}
	
	public static void showProteinWorkshop(){
		
	//	String[] pdbIds = new String[]{"-structure_url","http://www.rcsb.org:80/pdb/files/1CDG.xml.gz","-unit_id","1","-standalone"};
		String[] pdbIds = new String[]{"-structure_url","http://www.rcsb.org:80/pdb/files/1CDG.xml.gz","-standalone"};
		final ProteinWorkshop app = new ProteinWorkshop(pdbIds);	
		app.initialize(true, true);
	}
	
	public static void showLigandExplorer() {
		String[] args = new String[]{"-structure_url","http://www.rcsb.org:80/pdb/files/1STP.xml.gz","-unit_id","1","-standalone"};
//		String[] args = new String[]{"-structure_url","http://www.rcsb.org:80/pdb/files/1HWK.xml.gz","-unit_id","1","-standalone"};
		final LigandExplorer app = new LigandExplorer(args);		
		app.initialize(true);
		
	}
	
	public static void showKiosk() {
		
		String[] pdbIds = new String[]{"-structure_id_list","4hhb,1cdg"};
		SlideShow show = new SlideShow(pdbIds);
		show.run();
	}
	
}
