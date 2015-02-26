package demo;





import org.rcsb.ks.controllers.app.SlideShow;
import org.rcsb.lx.controllers.app.LigandExplorer;
import org.rcsb.pw.controllers.app.ProteinWorkshop;
import org.rcsb.sv.controllers.app.SimpleViewer;



public class Demo {

	public static void main(String[] args){

					//showSimpleViewer();
	    // showProteinWorkshop();
		  showLigandExplorer();
		//showKiosk();
	}

	

	public static void showSimpleViewer(){

	//	String[] args = new String[]{"-structure_url","http://www.rcsb.org/pdb/files/1STP.xml.gz","-unit_id","1","-standalone", "-cAlphaFlag"};
		String[] args = new String[]{"-structure_url","http://www.rcsb.org/pdb/files/1M4X.xml.gz","-unit_id","1","-standalone", "-cAlphaFlag"};
	//	String[] args = new String[]{"-structure_url","http://www.rcsb.org/pdb/files/1HNW.xml.gz","-unit_id","1","-standalone", "-cAlphaFlag"}; // ribosome
	//	String[] args = new String[]{"-structure_url","http://www.rcsb.org/pdb/files/2mo2.xml.gz","-unit_id","1","-standalone", "-cAlphaFlag"}; // dna
	//	String[] args = new String[]{"-structure_url","http://www.rcsb.org/pdb/files/2CSE.xml.gz","-unit_id","1","-standalone", "-cAlphaFlag"};
	//	String[] args = new String[]{"-structure_url","http://www.rcsb.org/pdb/files/3IYL.xml.gz","-unit_id","1","-standalone", "-cAlphaFlag"};
	//	String[] args = new String[]{"-structure_url","http://www.rcsb.org/pdb/files/4CWU.xml.gz","-unit_id","1","-standalone", "-cAlphaFlag"};
	//	String[] args = new String[]{"-structure_url","http://www.rcsb.org/pdb/files/1ML5.xml.gz","-unit_id","1","-standalone", "-cAlphaFlag"};
	//	String[] args = new String[]{"-structure_url","http://www.rcsb.org/pdb/files/1YA7.xml.gz","-unit_id","1","-standalone", "-cAlphaFlag"};
	//	String[] args = new String[]{"-structure_url","ftp://ftp.wwpdb.org/pub/pdb/data/large_structures/XML/3j3y.xml.gz","-unit_id","1","-standalone", "-cAlphaFlag"};
	//	String[] args = new String[]{"-structure_url","http://www.rcsb.org/pdb/files/1STP.xml.gz","-unit_id","1","-standalone"};
		final SimpleViewer app = new SimpleViewer(args);
		app.initialize(true, true);
	}
	
	public static void showProteinWorkshop(){
		String[] pdbIds = new String[]{"-structure_url","http://www.rcsb.org/pdb/files/2HYY.xml.gz","-standalone"};
	//	String[] pdbIds = new String[]{"-structure_url","http://www.rcsb.org/pdb/files/1CDG.xml.gz","-unit_id","1","-standalone"};
	//	String[] pdbIds = new String[]{"-structure_url","http://www.rcsb.org/pdb/files/1CDG.xml.gz","-standalone"};
	//	String[] pdbIds = new String[]{"-structure_url","http://www.rcsb.org/pdb/files/1STP.xml.gz","-standalone"};
	//	String[] pdbIds = new String[]{"-structure_url","http://www.rcsb.org/pdb/files/1A34.xml.gz","-standalone", "-cAlphaFlag"};
	//	String[] pdbIds = new String[]{"-structure_url","http://www.rcsb.org/pdb/files/3J7L.xml.gz","-standalone", "-cAlphaFlag"};
	//	String[] pdbIds = new String[]{"-structure_url","http://www.rcsb.org/pdb/files/1M4X.xml.gz","-standalone", "-cAlphaFlag"};
	//	String[] pdbIds = new String[]{"-structure_url","http://www.rcsb.org/pdb/files/1STP.xml.gz","-standalone", "-cAlphaFlag"};
	//	String[] pdbIds = new String[]{"-structure_url","http://www.rcsb.org/pdb/files/1XI5.xml.gz","-standalone", "-cAlphaFlag"};
	//	String[] pdbIds = new String[]{"-structure_url","http://www.rcsb.org/pdb/files/1ML5.xml.gz","-standalone", "-cAlphaFlag"};
		
		final ProteinWorkshop app = new ProteinWorkshop(pdbIds);	
		app.initialize(true, true);
	}
	
	public static void showLigandExplorer() {
		System.out.println("Showing Ligand Explorer");
		String[] args = new String[]{"-structure_url","http://www.rcsb.org/pdb/files/2HYY.xml.gz","-unit_id","1","-standalone"};
//		String[] args = new String[]{"-structure_url","http://www.rcsb.org/pdb/files/1STP.xml.gz","-unit_id","1","-standalone"};
//		String[] args = new String[]{"-structure_url","http://www.rcsb.org/pdb/files/2YOK.xml.gz","-unit_id","1","-standalone"};
//		String[] args = new String[]{"-structure_url","http://www.rcsb.org/pdb/files/4FQC.xml.gz","-unit_id","1","-standalone"};
		final LigandExplorer app = new LigandExplorer(args);		
		app.initialize(true);
		
	}
	
	public static void showKiosk() {
		
		String[] pdbIds = new String[]{"-structure_id_list","4hhb,1cdg"};
		SlideShow show = new SlideShow(pdbIds);
		show.run();
	}
	
}
