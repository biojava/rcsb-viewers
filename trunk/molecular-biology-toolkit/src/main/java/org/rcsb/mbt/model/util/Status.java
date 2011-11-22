/*
 * BioJava development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence. This should
 * be distributed with the code. If you do not have a copy,
 * see:
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors. These should be listed in @author doc comments.
 *
 * For more information on the BioJava project and its aims,
 * or to join the biojava-l mailing list, visit the home page
 * at:
 *
 * http://www.biojava.org/
 *
 * This code was contributed from the Molecular Biology Toolkit
 * (MBT) project at the University of California San Diego.
 *
 * Please reference J.L. Moreland, A.Gramada, O.V. Buzko, Qing
 * Zhang and P.E. Bourne 2005 The Molecular Biology Toolkit (MBT):
 * A Modular Platform for Developing Molecular Visualization
 * Applications. BMC Bioinformatics, 6:21.
 *
 * The MBT project was funded as part of the National Institutes
 * of Health PPG grant number 1-P01-GM63208 and its National
 * Institute of General Medical Sciences (NIGMS) division. Ongoing
 * development for the MBT project is managed by the RCSB
 * Protein Data Bank(http://www.pdb.org) and supported by funds
 * from the National Science Foundation (NSF), the National
 * Institute of General Medical Sciences (NIGMS), the Office of
 * Science, Department of Energy (DOE), the National Library of
 * Medicine (NLM), the National Cancer Institute (NCI), the
 * National Center for Research Resources (NCRR), the National
 * Institute of Biomedical Imaging and Bioengineering (NIBIB),
 * the National Institute of Neurological Disorders and Stroke
 * (NINDS), and the National Institute of Diabetes and Digestive
 * and Kidney Diseases (NIDDK).
 *
 * Created on 2007/02/08
 *
 */ 
package org.rcsb.mbt.model.util;


import java.util.Vector;


/**
 *  Provides a toolkit-wide (non-UI) static status message output mechanism.
 *  This should be used to produce textual status messages to the user.
 *  
 *  By default, messages are printed to the current terminal, but
 *  authors may elect to add StatusListener objects (such as a StatusPanel)
 *  in order to capture the output for display in scrolling status GUIs,
 *  output logs, etc.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.util.StatusListener
 *  @see	org.rcsb.mbt.model.util.StatusEvent
 */
public class Status
{
	//
	// Public output LEVEL values.
	//

	/**
	 * On a scale of 1 to 5, how important is the message.
	 * A status level is passed to the output method in order for an
	 * application to specify the intent level of the message, and,
	 * the level is also used to set the constraint level for output.
	 * Only messages at or above this level will be output.
	 * Default terminal output is not generated.
	 */
	public static final int LEVEL_QUIET = 0;

	/**
	 * On a scale of 1 to 5, how important is the message.
	 * A status level is passed to the output method in order for an
	 * application to specify the intent level of the message, and,
	 * the level is also used to set the constraint level for output.
	 * Only messages at or above this level will be output.
	 * Default terminal output is sent to STDERR.
	 */
	public static final int LEVEL_ERROR = 1;

	/**
	 * On a scale of 1 to 5, how important is the message.
	 * A status level is passed to the output method in order for an
	 * application to specify the intent level of the message, and,
	 * the level is also used to set the constraint level for output.
	 * Only messages at or above this level will be output.
	 * Default terminal output is sent to STDOUT.
	 */
	public static final int LEVEL_WARNING = 2;

	/**
	 * On a scale of 1 to 5, how important is the message.
	 * A status level is passed to the output method in order for an
	 * application to specify the intent level of the message, and,
	 * the level is also used to set the constraint level for output.
	 * Only messages at or above this level will be output.
	 * Default terminal output is sent to STDOUT.
	 */
	public static final int LEVEL_REMARK = 3;

	/**
	 * On a scale of 1 to 5, how important is the message.
	 * A status level is passed to the output method in order for an
	 * application to specify the intent level of the message, and,
	 * the level is also used to set the constraint level for output.
	 * Only messages at or above this level will be output.
	 * Default terminal output is sent to STDERR.
	 */
	public static final int LEVEL_DEBUG = 4;

	/**
	 * On a scale of 1 to 5, how important is the message.
	 * A status level is passed to the output method in order for an
	 * application to specify the intent level of the message, and,
	 * the level is also used to set the constraint level for output.
	 * Only messages at or above this level will be output.
	 * Default terminal output is sent to STDERR.
	 */
	public static final int LEVEL_DUMP = 5;

	//
	// Private variables.
	//
	
	private static String lastMessage = "";
	public static String getLastMessage() { return lastMessage; }
	
	/**
	 * A Vector of human-readable level names corresponding to level numbers.
	 */
	private static Vector level_names = null;
	static
	{
		Status.level_names = new Vector( );
		Status.level_names.add( "Quiet" );
		Status.level_names.add( "Error" );
		Status.level_names.add( "Warning" );
		Status.level_names.add( "Remark" );
		Status.level_names.add( "Debug" );
		Status.level_names.add( "Dump" );
	};

	/**
	 * Only messages at or above this level will be output.
	 */
	private static int output_level = Status.LEVEL_REMARK; // Default to REMARK

	/**
	 * Listener objects to which output will be sent.
	 */
	private static Vector statusListeners = null;

	/**
	 * A singled shared StatusEvent object.
	 */
	private static StatusEvent statusEvent = new StatusEvent();

	//
	// StatusListener methods.
	//

	/**
	 *  Add a StatusListener object in order to start receiving StatusEvent
	 *  messages. If one or more StatusListener objects are registered, then
	 *  no messages are printed to the terminal by the output method.
	 *  <P>
	 */
	public static void addStatusListener( final StatusListener statusListener )
	{
		if ( statusListener == null ) {
			return;
		}
		if ( Status.statusListeners == null ) {
			Status.statusListeners = new Vector( );
		}
		if ( Status.statusEvent == null ) {
			Status.statusEvent = new StatusEvent();
		}
		Status.statusListeners.add( statusListener );
	}

	/**
	 *  Remove a StatusListener object in order to stop recieving StatusEvent
	 *  messages. If one or more StatusListener objects are registered, then
	 *  no messages are printed to the terminal by the output method.
	 *  <P>
	 */
	public static void removeStatusListener( final StatusListener statusListener )
	{
		if ( statusListener == null ) {
			return;
		}
		if ( Status.statusListeners == null ) {
			return;
		}
		Status.statusListeners.remove( statusListener );
		if ( Status.statusListeners.size() <= 0 ) {
			Status.statusListeners = null;
		}
	}

	//
	// OutputLevel methods.
	//

	/**
	 *  Set the output level for subsequent calls to the output method.
	 *  Only messages at or above this level will be output.
	 *  <P>
	 */
	public static void setOutputLevel( final int level )
	{
		Status.output_level = level;
	}

	/**
	 *  Set the output level for subsequent calls to the output method.
	 *  Only messages at or above this level will be output.
	 *  <P>
	 */
	public static void setOutputLevel( final String level_name )
	{
		final int levelCount = Status.getLevelCount( );
		for ( int level=0; level<levelCount; level++ )
		{
			final String name = Status.getLevelName( level );
			if ( level_name.equals( name ) )
			{
				Status.setOutputLevel( level );
				break;
			}
		}
	}

	/**
	 *  Get the output level for subsequent calls to the output method.
	 *  Only messages at or above this level will be output.
	 *  <P>
	 */
	public static int getOutputLevel( )
	{
		return Status.output_level;
	}

	/**
	 *  Get the number of supported level values.
	 *  <P>
	 */
	public static int getLevelCount( )
	{
		return Status.level_names.size( );
	}

	/**
	 *  Get the human-reable name corresponding to the given level number.
	 *  <P>
	 */
	public static String getLevelName( final int level )
	{
		return (String) Status.level_names.elementAt( level );
	}

	//
	// Output methods.
	//

	/**
	 *  Output a message at the given status level.
	 *  <P>
	 */
	public static void output( final int level, final String message )
	{
		lastMessage = message;
		
		if ( level <= Status.output_level )
		{
			if ( (Status.statusEvent == null) || (Status.statusListeners == null) )
			{
				if ( level == Status.LEVEL_REMARK ) {
					System.out.println( "Status(" + level + "): " + message );
				} else {
					System.err.println( "Status(" + level + "): " + message );
				}
			}
			else
			{
				Status.statusEvent.type = StatusEvent.TYPE_OUTPUT;
				Status.statusEvent.level = level;
				Status.statusEvent.message = message + "\n";
				Status.statusEvent.percent = 100;
				for ( int i=0; i<Status.statusListeners.size(); i++ )
				{
					final StatusListener statusListener =
						(StatusListener) Status.statusListeners.elementAt( i );
					statusListener.processStatusEvent( Status.statusEvent );
				}
			}
		}
	}

	/**
	 *  Show a short progress message and a percentage completed.
	 *  Progress percentage is clamped to a 0.0 to 1.0 range.
	 *  If the message is null, don't show progress.
	 *  If there are no listeners, don't print progress.
	 *  <P>
	 */
	public static void progress( final int percent_, final String message )
	{
		int percent = Math.min(percent_, 100);
		if ( Status.statusListeners == null ) {
			return; // Don't print progress.
		}
		
		if ( (message != null) && (Status.statusEvent == null) || (Status.statusListeners == null) )
		{
			if ( Status.output_level == Status.LEVEL_REMARK ) {
				System.out.println( "Progress(" + percent + "): " + message );
			} else {
				System.err.println( "Progress(" + percent + "): " + message );
			}
		}
		else
		{
			Status.statusEvent.type = StatusEvent.TYPE_PROGRESS;
			Status.statusEvent.level = Status.output_level;
			if ( message == null ) {
				Status.statusEvent.message = null;
			} else {
				Status.statusEvent.message = message;
			}
			Status.statusEvent.percent = percent;

			for ( int i=0; i<Status.statusListeners.size(); i++ )
			{
				final StatusListener statusListener =
					(StatusListener) Status.statusListeners.elementAt( i );
				statusListener.processStatusEvent( Status.statusEvent );
			}
		}
	}
}

