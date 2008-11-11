package org.rcsb.pw.controllers.scene.mutators.options;

public class StructureElement_VisibilityOptions
{
	// visibility options.
	public enum Visibility{ VISIBLE, INVISIBLE, TOGGLE }
	
	// the current options
	private Visibility visibility = Visibility.TOGGLE;
	
	
	public void setVisibility(final Visibility visibility)
	{
		switch(visibility) {
		case INVISIBLE:
		case TOGGLE:
		case VISIBLE:
			this.visibility = visibility;
			break;
		}
		
		this.visibility = visibility;
	}
	public Visibility getVisibility() {
		return visibility;
	}
	
}
