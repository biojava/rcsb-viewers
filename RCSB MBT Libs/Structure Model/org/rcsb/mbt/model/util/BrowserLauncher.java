//  $Id: BrowserLauncher.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.//
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
//  $Log: BrowserLauncher.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:43  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.1  2004/04/30 00:32:09  moreland
//  First revision.
//
//  Revision 1.0  2004/04/29 00:15:20  moreland
//  First revision.
//


package org.rcsb.mbt.model.util;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * BrowserLauncher is a class that provides one static method, openURL, which
 * opens the default web browser for the current user of the system to the
 * given URL.  It may support other protocols depending on the system --
 * mailto, ftp, etc. -- but that has not been rigorously tested and is not
 * guaranteed to work.
 * <p>
 * Yes, this is platform-specific code, and yes, it may rely on classes on
 * certain platforms that are not part of the standard JDK.  What we're trying
 * to do, though, is to take something that's frequently desirable but
 * inherently platform-specific -- opening a default browser -- and allow
 * programmers (you, for example) to do so without worrying about dropping
 * into native code or doing anything else similarly evil.
 * <p>
 * Anyway, this code is completely in Java and will run on all JDK
 * 1.1-compliant systems without modification or a need for additional
 * libraries.  All classes that are required on certain platforms to allow
 * this to run are dynamically loaded at runtime via reflection and, if not
 * found, will not cause this to do anything other than returning an error
 * when opening the browser.
 * <p>
 * There are certain system requirements for this class, as it's running
 * through Runtime.exec(), which is Java's way of making a native system call.
 * Currently, this requires that a Macintosh have a Finder which supports the
 * GURL event, which is true for Mac OS 8.0 and 8.1 systems that have the
 * Internet Scripting AppleScript dictionary installed in the Scripting
 * Additions folder in the Extensions folder (which is installed by default as
 * far as I know under Mac OS 8.0 and 8.1), and for all Mac OS 8.5 and later
 * systems.  On Windows, it only runs under Win32 systems (Windows 95, 98, and
 * NT 4.0, as well as later versions of all).  On other systems, this drops
 * back from the inherently platform-sensitive concept of a default browser
 * and simply attempts to launch Netscape via a shell command.
 * <p>
 * This code is Copyright 1999-2001 by Eric Albert (ejalbert@cs.stanford.edu)
 * and may be redistributed or modified in any form without restrictions as
 * long as the portion of this comment from this paragraph through the end of
 * the comment is not removed.  The author requests that he be notified of any
 * application, applet, or other binary that makes use of this code, but that's
 * more out of curiosity than anything and is not required.  This software
 * includes no warranty.  The author is not repsonsible for any loss of data
 * or functionality or any adverse or unexpected effects of using this software.
 * <p>
 * Credits:
 * <br>Steven Spencer, JavaWorld magazine
 * (<a href="http://www.javaworld.com/javaworld/javatips/jw-javatip66.html">
 * Java Tip 66</a>)
 * <br>Thanks also to Ron B. Yeh, Eric Shapiro, Ben Engber, Paul Teitlebaum,
 * Andrea Cantatore, Larry Barowski, Trevor Bedzek, Frank Miedrich, and
 * Ron Rabakukk.
 * <p>
 * @author Eric Albert (<a href="mailto:ejalbert@cs.stanford.edu">ejalbert@cs.stanford.edu</a>)
 * @version 1.4b1 (Released June 20, 2001)
 */
public class BrowserLauncher
{

	/**
	 * The Java virtual machine that we are running on. Actually, in most
	 * cases we only care about the operating system, but some operating
	 * systems require us to switch on the VM.
	 */
	private static int jvm;

	/**
	 * The browser for the system
	 */
	private static Object browser;

	/**
	 * Caches whether any classes, methods, and fields that are not part of the
	 * JDK and need to be dynamically loaded at runtime loaded successfully.
	 * <p>
	 * Note that if this is <code>false</code>, <code>openURL()</code> will
	 * always return an IOException.
	 */
	private static boolean loadedWithoutErrors;

	/**
	 * The com.apple.mrj.MRJFileUtils class
	 */
	private static Class mrjFileUtilsClass;

	/**
	 * The com.apple.mrj.MRJOSType class
	 */
	private static Class mrjOSTypeClass;

	/**
	 * The com.apple.MacOS.AEDesc class
	 */
	private static Class aeDescClass;

	/**
	 * The <init>(int) method of com.apple.MacOS.AETarget
	 */
	private static Constructor aeTargetConstructor;

	/**
	 * The <init>(int, int, int) method of com.apple.MacOS.AppleEvent
	 */
	private static Constructor appleEventConstructor;

	/**
	 * The <init>(String) method of com.apple.MacOS.AEDesc
	 */
	private static Constructor aeDescConstructor;

	/**
	 * The findFolder method of com.apple.mrj.MRJFileUtils
	 */
	private static Method findFolder;

	/**
	 * The getFileCreator method of com.apple.mrj.MRJFileUtils
	 */
	private static Method getFileCreator;

	/**
	 * The getFileType method of com.apple.mrj.MRJFileUtils
	 */
	private static Method getFileType;

	/**
	 * The openURL method of com.apple.mrj.MRJFileUtils
	 */
	private static Method openURL;

	/**
	 * The makeOSType method of com.apple.MacOS.OSUtils
	 */
	private static Method makeOSType;

	/**
	 * The putParameter method of com.apple.MacOS.AppleEvent
	 */
	private static Method putParameter;

	/**
	 * The sendNoReply method of com.apple.MacOS.AppleEvent
	 */
	private static Method sendNoReply;

	/**
	 * Actually an MRJOSType pointing to the System Folder on a Macintosh
	 */
	private static Object kSystemFolderType;

	/**
	 * The keyDirectObject AppleEvent parameter type
	 */
	private static Integer keyDirectObject;

	/**
	 * The kAutoGenerateReturnID AppleEvent code
	 */
	private static Integer kAutoGenerateReturnID;

	/**
	 * The kAnyTransactionID AppleEvent code
	 */
	private static Integer kAnyTransactionID;

	/**
	 * The linkage object required for JDirect 3 on Mac OS X.
	 */
	private static Object linkage;

	/**
	 * JVM constant for MRJ 2.0
	 */
	private static final int MRJ_2_0 = 0;

	/**
	 * JVM constant for MRJ 2.1 or later
	 */
	private static final int MRJ_2_1 = 1;

	/**
	 * JVM constant for Java on Mac OS X 10.0 (MRJ 3.0)
	 */
	private static final int MRJ_3_0 = 3;

	/**
	 * JVM constant for MRJ 3.1
	 */
	private static final int MRJ_3_1 = 4;

	/**
	 * JVM constant for any Windows NT JVM
	 */
	private static final int WINDOWS_NT = 5;

	/**
	 * JVM constant for any Windows 9x JVM
	 */
	private static final int WINDOWS_9x = 6;

	/**
	 * JVM constant for any other platform
	 */
	private static final int OTHER = - 1;

	/**
	 * The file type of the Finder on a Macintosh. Hardcoding "Finder" would
	 * keep non-U.S. English systems from working properly.
	 */
	private static final String FINDER_TYPE = "FNDR";

	/**
	 * The creator code of the Finder on a Macintosh, which is needed to
	 * send AppleEvents to the application.
	 */
	private static final String FINDER_CREATOR = "MACS";

	/**
	 * The name for the AppleEvent type corresponding to a GetURL event.
	 */
	private static final String GURL_EVENT = "GURL";

	/**
	 * The first parameter that needs to be passed into Runtime.exec() to
	 * open the default web browser on Windows.
	 */
	private static final String FIRST_WINDOWS_PARAMETER = "/c";

	/**
	 * The second parameter for Runtime.exec() on Windows.
	 */
	private static final String SECOND_WINDOWS_PARAMETER = "start";

	/**
	 * The third parameter for Runtime.exec() on Windows.  This is a "title"
	 * parameter that the command line expects.  Setting this parameter allows
	 * URLs containing spaces to work.
	 */
	private static final String THIRD_WINDOWS_PARAMETER = "\"\"";

	/**
	 * The shell parameters for Netscape that opens a given URL in an
	 * already-open copy of Netscape on many command-line systems.
	 */
	private static final String NETSCAPE_REMOTE_PARAMETER = "-remote";
	private static final String NETSCAPE_OPEN_PARAMETER_START = "'openURL(";
	private static final String NETSCAPE_OPEN_PARAMETER_END = ")'";

	/**
	 * The message from any exception thrown throughout the initialization
	 * process.
	 */
	private static String errorMessage;

	/**
	 * Attempts to open the default web browser to the given URL.
	 * <P>
	 * @param url The URL to open
	 * @throws IOException If the web browser is not located or launched.
	 */
	public static void openURL( final String url )
		throws IOException
	{
		if( ! BrowserLauncher.loadedWithoutErrors )
		{
			throw new IOException( "Exception in finding browser: " + BrowserLauncher.errorMessage );
		}
		Object browser = BrowserLauncher.locateBrowser();
		if( browser == null )
		{
			throw new IOException( "Unable to locate browser: " + BrowserLauncher.errorMessage );
		}

		switch( BrowserLauncher.jvm )
		{
			case MRJ_2_0:
			Object aeDesc = null;
			try
			{
				aeDesc = BrowserLauncher.aeDescConstructor.newInstance( new Object []
				{
					url
				}
				);
				BrowserLauncher.putParameter.invoke( browser, new Object []
				{
					BrowserLauncher.keyDirectObject, aeDesc
				}
				);
				BrowserLauncher.sendNoReply.invoke( browser, new Object []
				{
				}
				);
			}
			catch( final InvocationTargetException ite )
			{
				throw new IOException( "InvocationTargetException while creating AEDesc: " + ite.getMessage() );
			}
			catch( final IllegalAccessException iae )
			{
				throw new IOException( "IllegalAccessException while building AppleEvent: " + iae.getMessage() );
			}
			catch( final InstantiationException ie )
			{
				throw new IOException( "InstantiationException while creating AEDesc: " + ie.getMessage() );
			}
			finally
			{
				aeDesc = null;
				// Encourage it to get disposed if it was created
				browser = null;
				// Ditto
			}
			break;
			case MRJ_2_1:
			Runtime.getRuntime().exec( new String []
			{
				( String ) browser, url
			}
			);
			break;
			case MRJ_3_0:
			final int [] instance = new int [ 1 ];
			int result = BrowserLauncher.ICStart( instance, 0 );
			if( result == 0 )
			{
				final int [] selectionStart = new int []
				{
					0
				}
				;
				final byte [] urlBytes = url.getBytes();
				final int [] selectionEnd = new int []
				{
					urlBytes.length
				}
				;
				result = BrowserLauncher.ICLaunchURL( instance [ 0 ], new byte []
				{
					0
				}
				, urlBytes,
				urlBytes.length, selectionStart,
				selectionEnd );
				if( result == 0 )
				{
					// Ignore the return value; the URL was launched successfully
					// regardless of what happens here.
					BrowserLauncher.ICStop( instance );
				}
				else
				{
					throw new IOException( "Unable to launch URL: " + result );
				}
			}
			else
			{
				throw new IOException( "Unable to create an Internet Config instance: " + result );
			}
			break;
			case MRJ_3_1:
			try
			{
				BrowserLauncher.openURL.invoke( null, new Object []
				{
					url
				}
				);
			}
			catch( final InvocationTargetException ite )
			{
				throw new IOException( "InvocationTargetException while calling openURL: " + ite.getMessage() );
			}
			catch( final IllegalAccessException iae )
			{
				throw new IOException( "IllegalAccessException while calling openURL: " + iae.getMessage() );
			}
			break;
			case WINDOWS_NT:
			case WINDOWS_9x:
			// Add quotes around the URL to allow ampersands and other special
			// characters to work.
			Process process = Runtime.getRuntime().exec( new String []
			{
				( String ) browser,
				BrowserLauncher.FIRST_WINDOWS_PARAMETER,
				BrowserLauncher.SECOND_WINDOWS_PARAMETER,
				BrowserLauncher.THIRD_WINDOWS_PARAMETER,
				'"' + url + '"'
			}
			);
			// This avoids a memory leak on some versions of Java on Windows.
			// That's hinted at in <http://developer.java.sun.com/developer/qow/archive/68/>.
			try
			{
				process.waitFor();
				process.exitValue();
			}
			catch( final InterruptedException ie )
			{
				throw new IOException( "InterruptedException while launching browser: " + ie.getMessage() );
			}
			break;
			case OTHER:
			// Assume that we're on Unix and that Netscape is installed

			// First, attempt to open the URL in a currently running session of Netscape
			process = Runtime.getRuntime().exec( new String []
			{
				( String ) browser,
				BrowserLauncher.NETSCAPE_REMOTE_PARAMETER,
				BrowserLauncher.NETSCAPE_OPEN_PARAMETER_START +
				url +
				BrowserLauncher.NETSCAPE_OPEN_PARAMETER_END
			}
			);
			try
			{
				final int exitCode = process.waitFor();
				if( exitCode != 0 )
				{
					// if Netscape was not open
					Runtime.getRuntime().exec( new String []
					{
						( String ) browser, url
					}
					);
				}
			}
			catch( final InterruptedException ie )
			{
				throw new IOException( "InterruptedException while launching browser: " + ie.getMessage() );
			}
			break;
			default:
			// This should never occur, but if it does, we'll try the simplest thing possible
			Runtime.getRuntime().exec( new String []
			{
				( String ) browser, url
			}
			);
			break;
		}
	}

	/**
	 * An initialization block that determines the operating system and
	 * loads the necessary runtime data.
	 */
	static
	{
		BrowserLauncher.loadedWithoutErrors = true;
		final String osName = System.getProperty( "os.name" );
		if( osName.startsWith( "Mac OS" ) )
		{
			final String mrjVersion = System.getProperty( "mrj.version" );
			final String majorMRJVersion = mrjVersion.substring( 0, 3 );
			try
			{
				final double version = Double.valueOf( majorMRJVersion ).doubleValue();
				if( version == 2 )
				{
					BrowserLauncher.jvm = BrowserLauncher.MRJ_2_0;
				}
				else if( version >= 2.1 && version < 3 )
				{
					// Assume that all 2.x versions of MRJ work the same.  MRJ 2.1 actually
					// works via Runtime.exec() and 2.2 supports that but has an openURL() method
					// as well that we currently ignore.
					BrowserLauncher.jvm = BrowserLauncher.MRJ_2_1;
				}
				else if( version == 3.0 )
				{
					BrowserLauncher.jvm = BrowserLauncher.MRJ_3_0;
				}
				else if( version >= 3.1 )
				{
					// Assume that all 3.1 and later versions of MRJ work the same.
					BrowserLauncher.jvm = BrowserLauncher.MRJ_3_1;
				}
				else
				{
					BrowserLauncher.loadedWithoutErrors = false;
					BrowserLauncher.errorMessage = "Unsupported MRJ version: " + version;
				}
			}
			catch( final NumberFormatException nfe )
			{
				BrowserLauncher.loadedWithoutErrors = false;
				BrowserLauncher.errorMessage = "Invalid MRJ version: " + mrjVersion;
			}
		}
		else if( osName.startsWith( "Windows" ) )
		{
			if( osName.indexOf( "9" ) != - 1 )
			{
				BrowserLauncher.jvm = BrowserLauncher.WINDOWS_9x;
			}
			else
			{
				BrowserLauncher.jvm = BrowserLauncher.WINDOWS_NT;
			}
		}
		else
		{
			BrowserLauncher.jvm = BrowserLauncher.OTHER;
		}

		if( BrowserLauncher.loadedWithoutErrors )
		{
			// if we haven't hit any errors yet
			BrowserLauncher.loadedWithoutErrors = BrowserLauncher.loadClasses();
		}
	}

	/**
	 * This class should be never be instantiated; this just ensures so.
	 * Please complete the missing tags for BrowserLauncher
	 */
	private BrowserLauncher()
	{
	}

	/**
	 * Called by a static initializer to load any classes, fields, and methods
	 * required at runtime to locate the user's web browser.
	 * @return <code>true</code> if all intialization succeeded
	 *			<code>false</code> if any portion of the initialization failed
	 */
	private static boolean loadClasses()
	{
		switch( BrowserLauncher.jvm )
		{
			case MRJ_2_0:
			try
			{
				final Class aeTargetClass = Class.forName( "com.apple.MacOS.AETarget" );
				final Class osUtilsClass = Class.forName( "com.apple.MacOS.OSUtils" );
				final Class appleEventClass = Class.forName( "com.apple.MacOS.AppleEvent" );
				final Class aeClass = Class.forName( "com.apple.MacOS.ae" );
				BrowserLauncher.aeDescClass = Class.forName( "com.apple.MacOS.AEDesc" );

				BrowserLauncher.aeTargetConstructor = aeTargetClass.getDeclaredConstructor( new Class []
				{
					int.class
				}
				);
				BrowserLauncher.appleEventConstructor = appleEventClass.getDeclaredConstructor( new Class []
				{
					int.class, int.class, aeTargetClass, int.class, int.class
				}
				);
				BrowserLauncher.aeDescConstructor = BrowserLauncher.aeDescClass.getDeclaredConstructor( new Class []
				{
					String.class
				}
				);

				BrowserLauncher.makeOSType = osUtilsClass.getDeclaredMethod( "makeOSType", new Class []
				{
					String.class
				}
				);
				BrowserLauncher.putParameter = appleEventClass.getDeclaredMethod( "putParameter", new Class []
				{
					int.class, BrowserLauncher.aeDescClass
				}
				);
				BrowserLauncher.sendNoReply = appleEventClass.getDeclaredMethod( "sendNoReply", new Class []
				{
				}
				);

				final Field keyDirectObjectField = aeClass.getDeclaredField( "keyDirectObject" );
				BrowserLauncher.keyDirectObject = ( Integer ) keyDirectObjectField.get( null );
				final Field autoGenerateReturnIDField = appleEventClass.getDeclaredField( "kAutoGenerateReturnID" );
				BrowserLauncher.kAutoGenerateReturnID = ( Integer ) autoGenerateReturnIDField.get( null );
				final Field anyTransactionIDField = appleEventClass.getDeclaredField( "kAnyTransactionID" );
				BrowserLauncher.kAnyTransactionID = ( Integer ) anyTransactionIDField.get( null );
			}
			catch( final ClassNotFoundException cnfe )
			{
				BrowserLauncher.errorMessage = cnfe.getMessage();
				return false;
			}
			catch( final NoSuchMethodException nsme )
			{
				BrowserLauncher.errorMessage = nsme.getMessage();
				return false;
			}
			catch( final NoSuchFieldException nsfe )
			{
				BrowserLauncher.errorMessage = nsfe.getMessage();
				return false;
			}
			catch( final IllegalAccessException iae )
			{
				BrowserLauncher.errorMessage = iae.getMessage();
				return false;
			}
			break;
			case MRJ_2_1:
			try
			{
				BrowserLauncher.mrjFileUtilsClass = Class.forName( "com.apple.mrj.MRJFileUtils" );
				BrowserLauncher.mrjOSTypeClass = Class.forName( "com.apple.mrj.MRJOSType" );
				final Field systemFolderField = BrowserLauncher.mrjFileUtilsClass.getDeclaredField( "kSystemFolderType" );
				BrowserLauncher.kSystemFolderType = systemFolderField.get( null );
				BrowserLauncher.findFolder = BrowserLauncher.mrjFileUtilsClass.getDeclaredMethod( "findFolder", new Class []
				{
					BrowserLauncher.mrjOSTypeClass
				}
				);
				BrowserLauncher.getFileCreator = BrowserLauncher.mrjFileUtilsClass.getDeclaredMethod( "getFileCreator", new Class []
				{
					File.class
				}
				);
				BrowserLauncher.getFileType = BrowserLauncher.mrjFileUtilsClass.getDeclaredMethod( "getFileType", new Class []
				{
					File.class
				}
				);
			}
			catch( final ClassNotFoundException cnfe )
			{
				BrowserLauncher.errorMessage = cnfe.getMessage();
				return false;
			}
			catch( final NoSuchFieldException nsfe )
			{
				BrowserLauncher.errorMessage = nsfe.getMessage();
				return false;
			}
			catch( final NoSuchMethodException nsme )
			{
				BrowserLauncher.errorMessage = nsme.getMessage();
				return false;
			}
			catch( final SecurityException se )
			{
				BrowserLauncher.errorMessage = se.getMessage();
				return false;
			}
			catch( final IllegalAccessException iae )
			{
				BrowserLauncher.errorMessage = iae.getMessage();
				return false;
			}
			break;
			case MRJ_3_0:
			try
			{
				final Class linker = Class.forName( "com.apple.mrj.jdirect.Linker" );
				final Constructor constructor = linker.getConstructor( new Class []
				{
					Class.class
				}
				);
				BrowserLauncher.linkage = constructor.newInstance( new Object []
				{
					BrowserLauncher.class
				}
				);
			}
			catch( final ClassNotFoundException cnfe )
			{
				BrowserLauncher.errorMessage = cnfe.getMessage();
				return false;
			}
			catch( final NoSuchMethodException nsme )
			{
				BrowserLauncher.errorMessage = nsme.getMessage();
				return false;
			}
			catch( final InvocationTargetException ite )
			{
				BrowserLauncher.errorMessage = ite.getMessage();
				return false;
			}
			catch( final InstantiationException ie )
			{
				BrowserLauncher.errorMessage = ie.getMessage();
				return false;
			}
			catch( final IllegalAccessException iae )
			{
				BrowserLauncher.errorMessage = iae.getMessage();
				return false;
			}
			break;
			case MRJ_3_1:
			try
			{
				BrowserLauncher.mrjFileUtilsClass = Class.forName( "com.apple.mrj.MRJFileUtils" );
				BrowserLauncher.openURL = BrowserLauncher.mrjFileUtilsClass.getDeclaredMethod( "openURL", new Class []
				{
					String.class
				}
				);
			}
			catch( final ClassNotFoundException cnfe )
			{
				BrowserLauncher.errorMessage = cnfe.getMessage();
				return false;
			}
			catch( final NoSuchMethodException nsme )
			{
				BrowserLauncher.errorMessage = nsme.getMessage();
				return false;
			}
			break;
			default:
			break;
		}
		return true;
	}

	/**
	 * Attempts to locate the default web browser on the local system. Caches
	 * results so it only locates the browser once for each use of this class
	 * per JVM instance.
	 * <P>
	 * @return The browser for the system. Note that this may not be what you
	 *  would consider to be a standard web browser; instead, it's the
	 *  application that gets called to open the default web browser. In some
	 *  cases, this will be a non-String object that provides the means of
	 *  calling the default browser.
	 */
	private static Object locateBrowser()
	{
		if( BrowserLauncher.browser != null )
		{
			return BrowserLauncher.browser;
		}

		switch( BrowserLauncher.jvm )
		{
			case MRJ_2_0:
			try
			{
				final Integer finderCreatorCode = ( Integer ) BrowserLauncher.makeOSType.invoke( null, new Object []
				{
					BrowserLauncher.FINDER_CREATOR
				}
				);
				final Object aeTarget = BrowserLauncher.aeTargetConstructor.newInstance( new Object []
				{
					finderCreatorCode
				}
				);
				final Integer gurlType = ( Integer ) BrowserLauncher.makeOSType.invoke( null, new Object []
				{
					BrowserLauncher.GURL_EVENT
				}
				);
				final Object appleEvent = BrowserLauncher.appleEventConstructor.newInstance( new Object []
				{
					gurlType, gurlType, aeTarget, BrowserLauncher.kAutoGenerateReturnID, BrowserLauncher.kAnyTransactionID
				}
				);
				// Don't set browser = appleEvent because then the next time we call
				// locateBrowser(), we'll get the same AppleEvent, to which we'll already have
				// added the relevant parameter. Instead, regenerate the AppleEvent every time.
				// There's probably a way to do this better; if any has any ideas, please let
				// me know.
				return appleEvent;
			}
			catch( final IllegalAccessException iae )
			{
				BrowserLauncher.browser = null;
				BrowserLauncher.errorMessage = iae.getMessage();
				return BrowserLauncher.browser;
			}
			catch( final InstantiationException ie )
			{
				BrowserLauncher.browser = null;
				BrowserLauncher.errorMessage = ie.getMessage();
				return BrowserLauncher.browser;
			}
			catch( final InvocationTargetException ite )
			{
				BrowserLauncher.browser = null;
				BrowserLauncher.errorMessage = ite.getMessage();
				return BrowserLauncher.browser;
			}

			case MRJ_2_1:
			File systemFolder;
			try
			{
				systemFolder = ( File ) BrowserLauncher.findFolder.invoke( null, new Object []
				{
					BrowserLauncher.kSystemFolderType
				}
				);
			}
			catch( final IllegalArgumentException iare )
			{
				BrowserLauncher.browser = null;
				BrowserLauncher.errorMessage = iare.getMessage();
				return BrowserLauncher.browser;
			}
			catch( final IllegalAccessException iae )
			{
				BrowserLauncher.browser = null;
				BrowserLauncher.errorMessage = iae.getMessage();
				return BrowserLauncher.browser;
			}
			catch( final InvocationTargetException ite )
			{
				BrowserLauncher.browser = null;
				BrowserLauncher.errorMessage = ite.getTargetException().getClass() + ": " + ite.getTargetException().getMessage();
				return BrowserLauncher.browser;
			}
			final String [] systemFolderFiles = systemFolder.list();
			// Avoid a FilenameFilter because that can't be stopped mid-list
			for( int i = 0; i < systemFolderFiles.length; i++ )
			{
				try
				{
					final File file = new File( systemFolder, systemFolderFiles [ i ] );
					if( ! file.isFile() )
					{
						continue;
					}
					// We're looking for a file with a creator code of 'MACS' and
					// a type of 'FNDR'.  Only requiring the type results in non-Finder
					// applications being picked up on certain Mac OS 9 systems,
					// especially German ones, and sending a GURL event to those
					// applications results in a logout under Multiple Users.
					final Object fileType = BrowserLauncher.getFileType.invoke( null, new Object []
					{
						file
					}
					);
					if( BrowserLauncher.FINDER_TYPE.equals( fileType.toString() ) )
					{
						final Object fileCreator = BrowserLauncher.getFileCreator.invoke( null, new Object []
						{
							file
						}
						);
						if( BrowserLauncher.FINDER_CREATOR.equals( fileCreator.toString() ) )
						{
							BrowserLauncher.browser = file.toString();
							// Actually the Finder, but that's OK
							return BrowserLauncher.browser;
						}
					}
				}
				catch( final IllegalArgumentException iare )
				{
					//						browser = browser;
					BrowserLauncher.errorMessage = iare.getMessage();
					return null;
				}
				catch( final IllegalAccessException iae )
				{
					BrowserLauncher.browser = null;
					BrowserLauncher.errorMessage = iae.getMessage();
					return BrowserLauncher.browser;
				}
				catch( final InvocationTargetException ite )
				{
					BrowserLauncher.browser = null;
					BrowserLauncher.errorMessage = ite.getTargetException().getClass() + ": " + ite.getTargetException().getMessage();
					return BrowserLauncher.browser;
				}
			}
			BrowserLauncher.browser = null;
			break;

			case MRJ_3_0:
			case MRJ_3_1:
			BrowserLauncher.browser = "";
			// Return something non-null
			break;

			case WINDOWS_NT:
			BrowserLauncher.browser = "cmd.exe";
			break;

			case WINDOWS_9x:
			BrowserLauncher.browser = "command.com";
			break;

			case OTHER:
			default:
			BrowserLauncher.browser = "netscape";
			break;
		}

		return BrowserLauncher.browser;
	}


	/**
	 * Methods required for Mac OS X. The presence of native methods does not
	 * cause any problems on other platforms.
	 */
	private native static int ICStart( int [] instance, int signature );
	private native static int ICStop( int [] instance );
	private native static int ICLaunchURL( int instance, byte [] hint,
		byte [] data, int len, int [] selectionStart, int [] selectionEnd );


	/**
	 * Unit testing entry point.
	 */
	public static void main( final String [] args )
	{
		try
		{
			BrowserLauncher.openURL( args [ 0 ] );
		}
		catch( final java.io.IOException e )
		{
			System.err.println( "BrowserLauncher.openURL failed: " + e );
		}
	}
}

