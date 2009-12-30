package com.aptana.scripting.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.aptana.scripting.Activator;

public class CommandContext
{
	// Key names for well known keys in the map
	public static final String ENV = "ENV"; //$NON-NLS-1$
	public static final String WORKING_DIRECTORY = "ACTIVE_PROJECT_FOLDER"; //$NON-NLS-1$

	public static final String ACTIVE_PROJECT_NAME = "ACTIVE_PROJECT_NAME"; //$NON-NLS-1$
	public static final String ACTIVE_PROJECT_FOLDER = "ACTIVE_PROJECT_FOLDER"; //$NON-NLS-1$

	private static final String CONTEXT_CONTRIBUTOR_ID = "contextContributors"; //$NON-NLS-1$
	private static final String TAG_CONTRIBUTOR = "contributor"; //$NON-NLS-1$
	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
	
	private static ContextContributor[] contextContributors;

	private Map<String,Object> _map;
	
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
		
		if (command.isShellCommand())
		{
			Map<String, String> envMap = new LinkedHashMap<String, String>();
			// Inherit the environment from parent
			envMap.putAll(new ProcessBuilder().environment());
			// Install default environment
			this._map.put(ENV, envMap);
		}

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
	 * getMap
	 * 
	 * @return
	 */
	Map<String,Object> getMap()
	{
		return this._map;
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
	 * Return the value of environment variable.
	 *
	 * @param envVariableName
	 * @return
	 */
	public String getEnv(String envVariableName)
	{
		Map<String, String> envMap = getEnvMap();
		if (envMap != null)
		{
			return envMap.get(envVariableName);
		}
		return null;
	}

	/**
	 * Return ENV map.
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> getEnvMap()
	{
		if (this._map != null)
		{
			return (Map<String, String>) this._map.get(ENV);
		}
		return null;
	}

	/**
	 * Set the value of environment variable.
	 *
	 * @param envVariableName Must not be null.
	 * @param envVariableValue If not null set the value. If null, remove the env variable.
	 */
	public void putEnv(String envVariableName, String envVariableValue)
	{
		assert envVariableName != null;
		Map<String, String> envMap = getEnvMap();
		if (envMap != null)
		{
			if (envVariableValue == null)
			{
				envMap.remove(envVariableName);
			} else {
				envMap.put(envVariableName, envVariableValue);
			}
		}
	}
}
