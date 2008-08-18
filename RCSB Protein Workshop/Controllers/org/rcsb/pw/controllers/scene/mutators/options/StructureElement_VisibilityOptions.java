package org.rcsb.pw.controllers.scene.mutators.options;

public class StructureElement_VisibilityOptions {
	// visibility options.
	public static final int VISIBILITY_VISIBLE = 0;	// make all elements visible
	public static final int VISIBILITY_INVISIBLE = 1;	// make all elements invisible
	public static final int VISIBILITY_TOGGLE = 2;	// inverse all elements' visibility
	
	// the current options
	private int visibility = VISIBILITY_TOGGLE;
	
	
	public void setVisibility(final int visibility) {
		switch(visibility) {
		case VISIBILITY_INVISIBLE:
		case VISIBILITY_TOGGLE:
		case VISIBILITY_VISIBLE:
			this.visibility = visibility;
			break;
		default:
			(new IllegalArgumentException()).printStackTrace();
		}
		
		this.visibility = visibility;
	}
	public int getVisibility() {
		return this.visibility;
	}
	
}
