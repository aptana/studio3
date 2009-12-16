package com.aptana.scripting;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.jruby.embed.LocalContextProvider;
import org.jruby.embed.PathType;
import org.jruby.embed.ScriptingContainer;


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
		
		return ResourceUtils.resourcePathToString(url);
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
				Activator.logError(e.getMessage(), e);
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

		if (loadPaths != null && loadPaths.size() > 0)
		{
			LocalContextProvider provider = container.getProvider();

			provider.setLoadPaths(loadPaths);
		}

		// TODO: $0 should work, but until then, we'll use this hack so script
		// can get its full path
		container.put("$fullpath", fullPath); //$NON-NLS-1$
		
		return container.runScriptlet(PathType.ABSOLUTE, fullPath);
	}
}
