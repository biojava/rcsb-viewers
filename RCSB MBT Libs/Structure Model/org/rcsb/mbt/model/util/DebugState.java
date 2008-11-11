package org.rcsb.mbt.model.util;
/**
 * Indicate whether we're in debug state or not.
 * 
 * Why is this here?  Ostensibly, it's an app-controller parameter...
 * 
 * The problem is that a commandline app won't instantiate AppBase, nor do we want it brought
 * into the link.  Hence we create it in the model (in util, just to separate it from everything
 * else), so that we can set a debug state, but don't have to bring in an appcontroller to contain
 * it.
 * 
 * Compromises...
 * 
 * @author rickb
 *
 */
public class DebugState
{
  static private boolean debugState = false;
  
  static public void setDebugState(boolean state) { debugState = state; }
  static public boolean isDebug() { return debugState; }
}
