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
 * Created on 2008/12/22
 *
 */ 
package org.rcsb.vf.controllers.app;
/////////////////////////////////////////////////////////
//Bare Bones Browser Launch                          //
//Version 1.5 (December 10, 2005)                    //
//By Dem Pilafian                                    //
//Supports: Mac OS X, GNU/Linux, Unix, Windows XP    //
//Example Usage:                                     //
// String url = "http://www.centerkey.com/";       //
// BareBonesBrowserLaunch.openURL(url);            //
//Public Domain Software -- Free to Use as You Like  //
/////////////////////////////////////////////////////////

import java.io.IOException;
import java.lang.reflect.Method;
import javax.swing.JOptionPane;

public class BBBrowserLauncher {

private static final String errMsg = "Error attempting to launch web browser";

public static void openURL(String url) throws IOException {
  String osName = System.getProperty("os.name");
  String browser = null;
  
  try
  {
     if (osName.startsWith("Mac OS"))
     {
        Class fileMgr = Class.forName("com.apple.eio.FileManager");
        Method openURL = fileMgr.getDeclaredMethod("openURL",
           new Class[] {String.class});
        openURL.invoke(null, new Object[] {url});
     }
     
     else if (osName.startsWith("Windows"))
        Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
     
     else
     { //assume Unix or Linux
        String[] browsers = {
           "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };

        for (int count = 0; count < browsers.length && browser == null; count++)
           if (Runtime.getRuntime().exec(
                 new String[] {"which", browsers[count]}).waitFor() == 0)
              browser = browsers[count];
        if (browser == null)
           throw new Exception("Could not find web browser");
        else
           Runtime.getRuntime().exec(new String[] {browser, url});
        }
     }
  
  catch (Exception e) {
     throw new IOException (errMsg + ":\n" + e.getLocalizedMessage());
     }
  }

}
