package org.rcsb.uiApp.controllers.app;

/**
 * Get the version of the app.
 * Each app needs to override this and provide a version number.
 * May get more sophisticated as we move on.
 * 
 * @author rickb
 *
 */
public abstract class VersionInformation
{
	public static String version() { return "No Version - app must override and set."; }
}
