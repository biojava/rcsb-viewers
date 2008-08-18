package org.rcsb.pw.controllers.app;

import java.util.prefs.Preferences;

public class PerformanceData {
	public static final String PERFORMANCE_PREFERENCES = "performance";
	
	public PerformanceData() {
		final Preferences prefsRoot = Preferences.userNodeForPackage(this.getClass());
		final Preferences performance = prefsRoot.node(PerformanceData.PERFORMANCE_PREFERENCES);
		
	}
}
