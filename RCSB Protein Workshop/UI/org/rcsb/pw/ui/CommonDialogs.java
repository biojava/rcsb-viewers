package org.rcsb.pw.ui;

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.ui.dialogs.ColorChooserDialog;


public class CommonDialogs {
	private static ColorChooserDialog colorDialog = null;
	
	public static ColorChooserDialog getColorDialog() {
		if(CommonDialogs.colorDialog == null) {
			CommonDialogs.colorDialog = new ColorChooserDialog(AppBase.sgetActiveFrame());
		}
		
		return CommonDialogs.colorDialog;
	}
	
	public static void clearMemory() {
		CommonDialogs.colorDialog = null;
	}
}
