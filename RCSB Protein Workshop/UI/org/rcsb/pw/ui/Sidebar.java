package org.rcsb.pw.ui;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.pw.ui.mutatorPanels.MutatorBasePanel;




public class Sidebar extends JPanel implements ChangeListener
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 4223763741039492L;
	//private Model model = null;
    public JTabbedPane tabs = null;
//    public BatchApplyPanel batchPanel = null;
    
    public MutatorBasePanel mutators = null;
    public ColoringOptions coloring = null;
    public GlobalOptionsPanel tools = null;
    public CreditsPanel credits = null;
    public DebugTab debug = null;
    
    public Sidebar()
    {
    	super(null, false);
    	super.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    	
        this.tabs = new JTabbedPane();
//        this.batchPanel = new BatchApplyPanel();
        this.mutators = new MutatorBasePanel();
        this.tools = new GlobalOptionsPanel();
        this.coloring = new ColoringOptions();
        this.credits = new CreditsPanel();
        if(VFAppBase.sgetSceneController().isDebugEnabled()) {
        	this.debug = new DebugTab();
        }
        
        this.tabs.setDoubleBuffered(false);
        this.tabs.addTab("Tools", this.mutators);
        this.tabs.addTab("Shortcuts", this.coloring);
        this.tabs.addTab("Options", this.tools);
        this.tabs.addTab("Help and Credits", this.credits);
        if(VFAppBase.sgetSceneController().isDebugEnabled()) {
        	this.tabs.addTab("Debug", this.debug);
        }
        //this.tabs.addTab("Debug", Debug.getPanel());
        
        this.tabs.addChangeListener(this);
        
        super.add(this.tabs);
    }


	public void stateChanged(final ChangeEvent e) {
        // user has implicitly disabled this mode if it was enabled.
		VFAppBase.sgetSceneController().setColorSelectorSampleModeEnabled(false);
	}
}
