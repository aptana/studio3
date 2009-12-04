package com.aptana.scripting;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.URIUtil;
import org.jruby.embed.LocalContextProvider;
import org.jruby.embed.PathType;
import org.jruby.embed.ScriptingContainer;

import com.aptana.scripting.model.Messages;

public class ScriptingEngine
{
	private static final String BUILTIN_BUNDLES = "bundles"; //$NON-NLS-1$
	
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
		URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path(BUILTIN_BUNDLES), null);
		String result = null;

		try
		{
			URL fileURL = FileLocator.toFileURL(url);
			URI fileURI = URIUtil.toURI(fileURL);	// Use Eclipse to get around Java 1.5 bug on Windows
			File file = new File(fileURI);

			result = file.getAbsolutePath();
		}
		catch (IOException e)
		{
			String message = MessageFormat.format(
				Messages.BundleManager_Cannot_Locate_Built_Ins_Directory,
				new Object[] { url.toString() }
			);

			Activator.logError(message, e);
		}
		catch (URISyntaxException e)
		{
			String message = MessageFormat.format(
				Messages.BundleManager_Malformed_Built_Ins_URI,
				new Object[] { url.toString() }
			);

			Activator.logError(message, e);
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
