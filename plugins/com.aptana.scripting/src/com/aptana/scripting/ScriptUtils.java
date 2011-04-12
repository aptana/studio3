/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.bindings.keys.IKeyLookup;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyModule;
import org.jruby.runtime.Block;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

public class ScriptUtils
{
	public static final String RUBLE_MODULE = "Ruble"; //$NON-NLS-1$
	public static final IRubyObject[] NO_ARGS = new IRubyObject[0];
	
	private static final Pattern CONTROL_PLUS = Pattern.compile("control" + Pattern.quote(KeyStroke.KEY_DELIMITER), Pattern.CASE_INSENSITIVE); //$NON-NLS-1$
	private static final String CTRL_PLUS = Matcher.quoteReplacement(IKeyLookup.CTRL_NAME + KeyStroke.KEY_DELIMITER);
	private static final Pattern OPTION_PLUS = Pattern.compile("option" + Pattern.quote(KeyStroke.KEY_DELIMITER), Pattern.CASE_INSENSITIVE); //$NON-NLS-1$
	private static final String ALT_PLUS = Matcher.quoteReplacement(IKeyLookup.ALT_NAME + KeyStroke.KEY_DELIMITER);
	
	/**
	 * ScriptUtils
	 */
	private ScriptUtils()
	{
	}
	
	/**
	 * instantiateClass
	 * 
	 * @param runtime
	 * @param name
	 * @param args
	 * @return
	 */
	public static IRubyObject instantiateClass(Ruby runtime, String name, IRubyObject ... args)
	{
		ThreadContext threadContext = runtime.getCurrentContext();
		IRubyObject result = null;
		
		// try to load the class
		RubyClass rubyClass = runtime.getClass(name);

		// instantiate it, if it exists
		if (rubyClass != null)
		{
			result = rubyClass.newInstance(threadContext, args, Block.NULL_BLOCK);
		}
		
		return result;
	}
	
	/**
	 * instantiateClass
	 * 
	 * @param runtime
	 * @param module
	 * @param name
	 * @param args
	 * @return
	 */
	public static IRubyObject instantiateClass(Ruby runtime, String module, String name, IRubyObject ... args)
	{
		ThreadContext threadContext = runtime.getCurrentContext();
		IRubyObject result = null;
		
		// try to load the module
		RubyModule rubyModule = runtime.getModule(module);
		
		if (rubyModule != null)
		{
			// now try to load the class
			RubyClass rubyClass = rubyModule.getClass(name);
			
			// instantiate it, if it exists
			if (rubyClass != null)
			{
				result = rubyClass.newInstance(threadContext, args, Block.NULL_BLOCK);
			}
		}
		
		return result;
	}
	
	/**
	 * Normalize the keyBinding string.
	 * <p>
	 * Convert control+ to CTRL+ Convert option+ to ALT+
	 * 
	 * @param keyBinding
	 * @return
	 */
	public static String normalizeKeyBinding(String keyBinding)
	{
		String result = null;

		if (keyBinding != null)
		{
			result = CONTROL_PLUS.matcher(keyBinding).replaceAll(CTRL_PLUS); // Convert control+ to CTRL+
			result = OPTION_PLUS.matcher(result).replaceAll(ALT_PLUS); // Convert option+ to ALT+
		}

		return result;
	}
	
	/**
	 * logErroWithStackTrace
	 * 
	 * @param message
	 * @param e
	 */
	public static void logErrorWithStackTrace(String message, Exception e)
	{
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		
		e.printStackTrace(writer);
		
		ScriptLogger.logError(message + "\n" + stringWriter.toString()); //$NON-NLS-1$
	}
}
