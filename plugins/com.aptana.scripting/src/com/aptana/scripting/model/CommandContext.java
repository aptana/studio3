package com.aptana.scripting.model;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.jruby.RubyIO;

import com.aptana.scripting.Activator;
import com.aptana.scripting.ScriptingEngine;

public class CommandContext
{
	// Key names for well known keys in the map
	public static final String WORKING_DIRECTORY = "working_directory"; //$NON-NLS-1$

	public static final String ACTIVE_PROJECT_NAME = "active_project_name"; //$NON-NLS-1$
	public static final String ACTIVE_PROJECT_FOLDER = "active_project_folder"; //$NON-NLS-1$
	public static final String INPUT_TYPE = "input_type"; //$NON-NLS-1$

	private static final String CONTEXT_CONTRIBUTOR_ID = "contextContributors"; //$NON-NLS-1$
	private static final String TAG_CONTRIBUTOR = "contributor"; //$NON-NLS-1$
	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
	
	private static ContextContributor[] contextContributors;
	
	private Map<String,Object> _map;
	private InputStream _inputStream;
	//private Ruby _runtime;

	/**
	 * getContextContributors
	 * 
	 * @return
	 */
	public static ContextContributor[] getContextContributors()
	{
		if (contextContributors == null)
		{
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			List<ContextContributor> contributors = new ArrayList<ContextContributor>();

			if (registry != null)
			{
				IExtensionPoint extensionPoint = registry.getExtensionPoint(Activator.PLUGIN_ID, CONTEXT_CONTRIBUTOR_ID);

				if (extensionPoint != null)
				{
					IExtension[] extensions = extensionPoint.getExtensions();

					for (IExtension extension : extensions)
					{
						IConfigurationElement[] elements = extension.getConfigurationElements();

						for (IConfigurationElement element : elements)
						{
							if (element.getName().equals(TAG_CONTRIBUTOR))
							{
								try
								{
									ContextContributor contributor = (ContextContributor) element.createExecutableExtension(ATTR_CLASS);
									
									contributors.add(contributor);
								}
								catch (CoreException e)
								{
									String message = MessageFormat.format(
										Messages.CommandElement_Error_Creating_Contributor,
										new Object[] { e.getMessage() }
									);
									
									Activator.logError(message, e);
								}
							}
						}
					}
				}
			}

			contextContributors = contributors.toArray(new ContextContributor[contributors.size()]);
		}
		
		return contextContributors;
	}
	
	/**
	 * CommandContext
	 */
	CommandContext(CommandElement command)
	{
		this._map = new HashMap<String,Object>();
		for (ContextContributor contributor : getContextContributors())
		{
			contributor.modifyContext(command, this);
		}
	}
	
	/**
	 * get
	 * 
	 * @param key
	 * @return
	 */
	public Object get(String key)
	{
		return this._map.get(key);
	}
	
	/**
	 * getInputStream
	 * 
	 * @return
	 */
	public InputStream getInputStream()
	{
		return this._inputStream;
	}

	/**
	 * getMap
	 * 
	 * @return
	 */
	public Map<String,Object> getMap()
	{
		return this._map;
	}

	/**
	 * in
	 * 
	 * @return
	 */
	public RubyIO in()
	{
		return new RubyIO(
			ScriptingEngine.getInstance().getScriptingContainer().getRuntime(),
			this.getInputStream()
		);
	}
	
	/**
	 * put
	 * 
	 * @param key
	 * @param value
	 */
	public void put(String key, Object value)
	{
		this._map.put(key, value);
	}
	
	/**
	 * putAll
	 * 
	 * @param entries
	 */
	public void putAll(Map<String,Object> entries)
	{
		this._map.putAll(entries);
	}

	/**
	 * @param inputStream
	 */
	public void setInputStream(InputStream inputStream)
	{
		this._inputStream = inputStream;
	}
}
