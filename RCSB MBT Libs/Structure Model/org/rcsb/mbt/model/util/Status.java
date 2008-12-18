//  $Id: Status.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
//
//  Copyright 2000-2004 The Regents of the University of California.
//  All Rights Reserved.
//
//  Permission to use, copy, modify and distribute any part of this
//  Molecular Biology Toolkit (MBT)
//  for educational, research and non-profit purposes, without fee, and without
//  a written agreement is hereby granted, provided that the above copyright
//  notice, this paragraph and the following three paragraphs appear in all
//  copies.
//
//  Those desiring to incorporate this MBT into commercial products
//  or use for commercial purposes should contact the Technology Transfer &
//  Intellectual Property Services, University of California, San Diego, 9500
//  Gilman Drive, Mail Code 0910, La Jolla, CA 92093-0910, Ph: (858) 534-5815,
//  FAX: (858) 534-7345, E-MAIL:invent@ucsd.edu.
//
//  IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
//  DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING
//  LOST PROFITS, ARISING OUT OF THE USE OF THIS MBT, EVEN IF THE
//  UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
//  THE MBT PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE
//  UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT,
//  UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES
//  NO REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR
//  EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
//  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF THE
//  MBT WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS.
//
//  For further information, please see:  http://mbt.sdsc.edu
//
//  History:
//  $Log: Status.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:43  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.2  2006/09/02 18:52:28  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.7  2004/04/09 00:15:21  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.6  2004/01/29 17:29:07  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.5  2004/01/08 22:13:41  moreland
//  If there are no listeners, don't print progress messages.
//
//  Revision 1.4  2003/04/03 22:40:33  moreland
//  Added "progress" handling support.
//
//  Revision 1.3  2003/02/27 21:23:04  moreland
//  Corrected javadoc "see" reference paths.
//
//  Revision 1.2  2003/02/03 21:50:28  moreland
//  Added some introspection methods to facilitate building GUI components.
//
//  Revision 1.1  2003/01/31 21:15:50  moreland
//  Added classes to provide a toolkit-wide static status message output mechanism.
//
//  Revision 1.0  2002/10/24 17:54:01  moreland
//  First revision.
//


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

