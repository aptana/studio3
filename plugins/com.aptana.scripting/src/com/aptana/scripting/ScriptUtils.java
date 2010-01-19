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
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

public class ScriptUtils
{
	public static final String RADRAILS_MODULE = "RadRails";
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
	 * @param fullyQualifiedName
	 * @return
	 */
	public static IRubyObject instantiateClass(String name)
	{
		return instantiateClass(name, NO_ARGS);
	}
	
	/**
	 * instantiateClass
	 * 
	 * @param name
	 * @param args
	 * @return
	 */
	public static IRubyObject instantiateClass(String name, IRubyObject[] args)
	{
		Ruby runtime = ScriptingEngine.getInstance().getScriptingContainer().getRuntime();
		ThreadContext threadContext = runtime.getCurrentContext();
		IRubyObject result = null;
		
		// try to load the class
		RubyClass rubyClass = runtime.getClass(name);

		// instantiate it, if it exists
		if (rubyClass != null)
		{
			result = rubyClass.newInstance(threadContext, args, null);
		}
		
		return result;
	}
	
	/**
	 * instantiateClass
	 * 
	 * @param fullyQualifiedName
	 * @return
	 */
	public static IRubyObject instantiateClass(String module, String name)
	{
		return instantiateClass(module, name, NO_ARGS);
	}
	
	/**
	 * instantiateClass
	 * 
	 * @param module
	 * @param name
	 * @param args
	 * @return
	 */
	public static IRubyObject instantiateClass(String module, String name, IRubyObject[] args)
	{
		Ruby runtime = ScriptingEngine.getInstance().getScriptingContainer().getRuntime();
		ThreadContext threadContext = runtime.getCurrentContext();
		IRubyObject result = null;
		
		// try to load the module
		RubyModule rubyModule = runtime.getModule(module); //$NON-NLS-1$
		
		if (rubyModule != null)
		{
			// now try to load the class
			RubyClass rubyClass = rubyModule.getClass(name);
			
			// instantiate it, if it exists
			if (rubyClass != null)
			{
				result = rubyClass.newInstance(threadContext, args, null);
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
	 * javaToRuby
	 * 
	 * @param javaObject
	 * @return
	 */
	public static IRubyObject javaToRuby(Object javaObject)
	{
		Ruby runtime = ScriptingEngine.getInstance().getScriptingContainer().getRuntime();
		
		return JavaEmbedUtils.javaToRuby(runtime, javaObject);
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
