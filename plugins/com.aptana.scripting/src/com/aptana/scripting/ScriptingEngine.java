package com.aptana.scripting;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.jruby.embed.EmbedEvalUnit;
import org.jruby.embed.EvalFailedException;
import org.jruby.embed.LocalContextProvider;
import org.jruby.embed.ParseFailedException;
import org.jruby.embed.PathType;
import org.jruby.embed.ScriptingContainer;

import com.aptana.util.ResourceUtils;


public class ScriptingEngine
{
	private static final String BUILTIN_LIBRARY = "framework"; //$NON-NLS-1$
	
	private static ScriptingEngine instance;
	private static ScriptingContainer scriptingContainer;

	/**
	 * ScriptingEngine
	 */
	private ScriptingEngine()
	{
	}

	/**
	 * getInstance
	 * 
	 * @return
	 */
	public static ScriptingEngine getInstance()
	{
		if (instance == null)
		{
			instance = new ScriptingEngine();
		}

		return instance;
	}

	/**
	 * getBuiltinsLoadPath
	 * 
	 * @return
	 */
	public static String getBuiltinsLoadPath()
	{
		URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path(BUILTIN_LIBRARY), null);
		String result = null;
		
		if (url != null)
		{
			result = ResourceUtils.resourcePathToString(url);
		}
		
		return result;
	}
	
	/**
	 * getScriptingContainer
	 * 
	 * @return
	 */
	public ScriptingContainer getScriptingContainer()
	{
		if (scriptingContainer == null)
		{
			scriptingContainer = new ScriptingContainer();
			
			try
			{
				File pluginFile = FileLocator.getBundleFile(Activator.getDefault().getBundle());
				
				scriptingContainer.getProvider().getRubyInstanceConfig().setJRubyHome(pluginFile.getAbsolutePath());
			}
			catch (IOException e)
			{
				String message = MessageFormat.format(
					Messages.ScriptingEngine_Error_Setting_JRuby_Home,
					new Object[] { e.getMessage() }
				);
			
				Activator.logError(message, e);
				ScriptLogger.logError(message);
			}
		}

		return scriptingContainer;
	}

	/**
	 * runScript
	 * 
	 * @param fullPath
	 */
	public Object runScript(String fullPath, List<String> loadPaths)
	{
		ScriptingContainer container = this.getScriptingContainer();
		Object result = null;

		if (loadPaths != null && loadPaths.size() > 0)
		{
			LocalContextProvider provider = container.getProvider();

			if (provider != null)
			{
				provider.setLoadPaths(loadPaths);
			}
		}

		// TODO: $0 should work, but until then, we'll use this hack so scripts
		// can get its full path
		container.put("$fullpath", fullPath); //$NON-NLS-1$
		
		// compile
		try
		{
			EmbedEvalUnit unit = container.parse(PathType.ABSOLUTE, fullPath);
			
			// execute
			result = unit.run();
		}
		catch (ParseFailedException e)
		{
			String message = MessageFormat.format(
				Messages.ScriptingEngine_Parse_Error,
				new Object[] { fullPath, e.getMessage() }
			);
			
			ScriptLogger.logError(message);
		}
		catch (EvalFailedException e)
		{
			String message = MessageFormat.format(
				Messages.ScriptingEngine_Execution_Error,
				new Object[] { fullPath, e.getMessage() }
			);
			
			ScriptLogger.logError(message);
		}
		
		return result;
	}
}
