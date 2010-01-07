package com.aptana.scripting;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.RubyString;
import org.jruby.embed.EmbedEvalUnit;
import org.jruby.embed.EvalFailedException;
import org.jruby.embed.LocalContextProvider;
import org.jruby.embed.ParseFailedException;
import org.jruby.embed.PathType;
import org.jruby.embed.ScriptingContainer;
import org.jruby.runtime.builtin.IRubyObject;
import org.osgi.framework.Bundle;

import com.aptana.util.ResourceUtils;

public class ScriptingEngine
{
	// framework_file extension point
	private static final String FRAMEWORK_FILE_ID = "frameworkFiles"; //$NON-NLS-1$
	private static final String TAG_FILE = "file"; //$NON-NLS-1$
	private static final String ATTR_NAME = "name"; //$NON-NLS-1$

	// loadpath extension point
	private static final String LOADPATH_ID = "loadPaths"; //$NON-NLS-1$
	private static final String TAG_LOADPATH = "loadPath"; //$NON-NLS-1$
	private static final String ATTR_PATH = "path"; //$NON-NLS-1$

	private static ScriptingEngine instance;

	private ScriptingContainer _scriptingContainer;
	private List<String> _loadPaths;
	private List<String> _frameworkFiles;
	private List<RubyString> fAdded = new ArrayList<RubyString>();

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
	 * getContributedLoadPaths
	 * 
	 * @return
	 */
	public List<String> getContributedLoadPaths()
	{
		if (this._loadPaths == null)
		{
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			List<String> paths = new ArrayList<String>();

			if (registry != null)
			{
				IExtensionPoint extensionPoint = registry.getExtensionPoint(Activator.PLUGIN_ID, LOADPATH_ID);

				if (extensionPoint != null)
				{
					IExtension[] extensions = extensionPoint.getExtensions();

					for (IExtension extension : extensions)
					{
						IConfigurationElement[] elements = extension.getConfigurationElements();

						for (IConfigurationElement element : elements)
						{
							if (element.getName().equals(TAG_LOADPATH))
							{
								String path = element.getAttribute(ATTR_PATH);

								IExtension declaring = element.getDeclaringExtension();
								String declaringPluginID = declaring.getNamespaceIdentifier();
								Bundle bundle = Platform.getBundle(declaringPluginID);
								URL url = bundle.getEntry(path);
								String urlAsPath = ResourceUtils.resourcePathToString(url);

								if (urlAsPath != null && urlAsPath.length() > 0)
								{
									paths.add(urlAsPath);
								}
								else
								{
									String message = MessageFormat.format(
										Messages.ScriptingEngine_Unable_To_Convert_Load_Path,
										new Object[] { declaringPluginID, url }
									);
									
									Activator.logError(message, null);
								}
							}
						}
					}
				}
			}

			this._loadPaths = Collections.unmodifiableList(paths);
		}

		return this._loadPaths;
	}

	/**
	 * getFrameworkFiles
	 * 
	 * @return
	 */
	public List<String> getFrameworkFiles()
	{
		if (this._frameworkFiles == null)
		{
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			List<String> names = new ArrayList<String>();

			if (registry != null)
			{
				IExtensionPoint extensionPoint = registry.getExtensionPoint(Activator.PLUGIN_ID, FRAMEWORK_FILE_ID);

				if (extensionPoint != null)
				{
					IExtension[] extensions = extensionPoint.getExtensions();

					for (IExtension extension : extensions)
					{
						IConfigurationElement[] elements = extension.getConfigurationElements();

						for (IConfigurationElement element : elements)
						{
							if (element.getName().equals(TAG_FILE))
							{
								names.add(element.getAttribute(ATTR_NAME));
							}
						}
					}
				}
			}

			this._frameworkFiles = Collections.unmodifiableList(names);
		}

		return this._frameworkFiles;
	}

	/**
	 * getScriptingContainer
	 * 
	 * @return
	 */
	public ScriptingContainer getScriptingContainer()
	{
		if (this._scriptingContainer == null)
		{
			this._scriptingContainer = new ScriptingContainer();

			try
			{
				File jrubyHome = null;
				// try just exploding the jruby lib dir
				URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path("lib"), null); //$NON-NLS-1$
				if (url != null)
				{
					File lib = ResourceUtils.resourcePathToFile(url);
					// Ok, now use the parent of exploded lib dir as JRuby Home
					jrubyHome = lib.getParentFile();
				}
				else
				{
					// Ok, just assume the plugin is unpacked and pass the root of the plugin as JRuby Home
					jrubyHome = FileLocator.getBundleFile(Activator.getDefault().getBundle());
				}
				this._scriptingContainer.getProvider().getRubyInstanceConfig()
						.setJRubyHome(jrubyHome.getAbsolutePath());
			}
			catch (IOException e)
			{
				String message = MessageFormat.format(Messages.ScriptingEngine_Error_Setting_JRuby_Home,
						new Object[] { e.getMessage() });

				Activator.logError(message, e);
				ScriptLogger.logError(message);
			}
		}

		return this._scriptingContainer;
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
				Ruby runtime = provider.getRuntime();	    
				IRubyObject object = runtime.getLoadService().getLoadPath();
				RubyArray loadpathArray = (RubyArray) object;
				// wipe whatever we added before
				for (RubyString added : fAdded)
				{
					loadpathArray.remove(added);
				}
				fAdded.clear();
				// Now add our custom loadpath for this execution
				for (String loadPath : loadPaths)
				{
					RubyString toAdd = runtime.newString(loadPath.replace('\\', '/'));
					loadpathArray.append(toAdd);
					fAdded.add(toAdd); 
				}
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
			String message = MessageFormat.format(Messages.ScriptingEngine_Parse_Error, new Object[] { fullPath,
					e.getMessage() });

			ScriptLogger.logError(message);
		}
		catch (EvalFailedException e)
		{
			String message = MessageFormat.format(Messages.ScriptingEngine_Execution_Error, new Object[] { fullPath,
					e.getMessage() });

			ScriptLogger.logError(message);
		}

		return result;
	}
}
