/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.configurations.processor;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.mortbay.util.ajax.JSON;
import org.mortbay.util.ajax.JSON.Convertible;
import org.mortbay.util.ajax.JSON.Output;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.configurations.ConfigurationsPlugin;

/**
 * This class represents a state of a configuration.<br>
 * The ConfigurationStatus also implements the {@link Convertible} interface, so it can easily be created and stored
 * using {@link JSON#toJSON(Object)} and {@link JSON#fromJSON(String)}
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class ConfigurationStatus implements Convertible
{
	/**
	 * Represents an unknown configuration status.
	 */
	public static final String UNKNOWN = "unknown"; //$NON-NLS-1$
	/**
	 * Represents a valid configuration status.
	 */
	public static final String OK = "ok"; //$NON-NLS-1$
	/**
	 * Represents a configuration in progress status.
	 */
	public static final String PROCESSING = "processing"; //$NON-NLS-1$
	/**
	 * Represents a configuration in progress that was stop and left in an incomplete state.
	 */
	public static final String INCOMPLETE = "incomplete"; //$NON-NLS-1$
	/**
	 * Represents a configuration error status.
	 */
	public static final String ERROR = "error"; //$NON-NLS-1$

	/**
	 * The STATUS attribute key.
	 */
	public static final String STATUS = "status"; //$NON-NLS-1$

	private static final String PREF_PREFIX = "configuration.status."; //$NON-NLS-1$

	private Map<String, String> attributes;
	private String configurationProcessorId;
	private AbstractConfigurationProcessor processor;
	private Set<String> notificationSet;

	/**
	 * Constructs a new ConfigurationStatus with a given status.
	 * 
	 * @param configurationProcessorId
	 *            The configuration process id that this status refer to
	 * @param status
	 *            One of {@link #UNKNOWN}, {@link #OK}, {@link #PROCESSING} or {@link #ERROR}
	 * @param processor
	 *            A configuration processor that will be notified on changes.
	 */
	public ConfigurationStatus(String configurationProcessorId, String status, AbstractConfigurationProcessor processor)
	{
		this.configurationProcessorId = configurationProcessorId;
		this.processor = processor;
		attributes = new HashMap<String, String>();
		notificationSet = new HashSet<String>(3);
		setAttribute(STATUS, status);
		load();
	}

	/**
	 * Constructs a new ConfigurationStatus with an {@link #UNKNOWN} status.
	 * 
	 * @param configurationProcessorId
	 *            The configuration process id that this status refer to
	 * @param processor
	 *            A configuration processor that will be notified on changes.
	 */
	public ConfigurationStatus(String configurationProcessorId, AbstractConfigurationProcessor processor)
	{
		this(configurationProcessorId, UNKNOWN, processor);
	}

	/**
	 * A convenient way to set the status of this ConfigurationStatus instance.<br>
	 * This call is the same as calling setAttribute(STATUS, status).
	 * 
	 * @param status
	 *            One of {@link #UNKNOWN}, {@link #OK}, {@link #PROCESSING}, {@link #ERROR} or {@link #INCOMPLETE}
	 * @see #setAttribute(String, String)
	 */
	public void setStatus(String status)
	{
		setAttribute(STATUS, status);
	}

	/**
	 * A convenient way to get the status of this ConfigurationStatus instance.<br>
	 * This call is the same as calling getAttribute(STATUS).
	 * 
	 * @return The status - One of {@link #UNKNOWN}, {@link #OK}, {@link #PROCESSING}, {@link #ERROR} or
	 *         {@link #INCOMPLETE}
	 */
	public String getStatus()
	{
		return getAttribute(STATUS);
	}

	/**
	 * Set an attribute for this configuration status.
	 * 
	 * @param key
	 * @param value
	 */
	public void setAttribute(String key, String value)
	{
		if (STATUS.equals(key))
		{
			// Make sure that the key matches our known statuses
			if (!(UNKNOWN.equals(value) || OK.equals(value) || PROCESSING.equals(value) || ERROR.equals(value) || INCOMPLETE
					.equals(value)))
			{
				throw new IllegalArgumentException(
						"The given state is unknown to the ConfigurationStatus (state = " + value + ')'); //$NON-NLS-1$
			}
		}
		attributes.put(key, value);
		notifyProcessor(key);
	}

	/**
	 * Removes an attribute from this configuration status.
	 * 
	 * @param key
	 *            The attribute key to remove.
	 * @throws IllegalArgumentException
	 *             in case that the attribute key is {@link #STATUS}
	 */
	public void removeAttribute(String key)
	{
		if (STATUS.equals(key))
		{
			throw new IllegalArgumentException("The status attribute cannot be removed from this ConfigurationStatus"); //$NON-NLS-1$
		}
		attributes.remove(key);
		notifyProcessor(key);
	}

	/**
	 * Returns an attribute value for the given key.
	 * 
	 * @param key
	 *            The attribute's key
	 * @return The value; or null.
	 */
	public String getAttribute(String key)
	{
		return attributes.get(key);
	}

	/**
	 * Returns a Set of all the attribute keys that this instance holds.
	 * 
	 * @return A Set of attribute keys.
	 */
	public Set<String> getAttributesKeys()
	{
		return Collections.unmodifiableSet(attributes.keySet());
	}

	/**
	 * Returns the configuration process id that this status is referring to.
	 * 
	 * @return The configuration process id.
	 */
	public String getProcessorId()
	{
		return configurationProcessorId;
	}

	/**
	 * Store this ConfigurationStatus in the preferences.
	 */
	public void store()
	{
		// We just store the attributes map as a JSON string
		IEclipsePreferences prefs = new InstanceScope().getNode(ConfigurationsPlugin.PLUGIN_ID);
		prefs.put(PREF_PREFIX + getProcessorId(), JSON.toString(attributes));
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			// Log any error while saving
			ConfigurationsPlugin.logError("Error while saving the configuration status state", e); //$NON-NLS-1$
		}
	}

	/**
	 * If a ConfigurationStatus with this ID was previously saved to the preferences, load the previous values into this
	 * instance.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void load()
	{
		IEclipsePreferences prefs = new InstanceScope().getNode(ConfigurationsPlugin.PLUGIN_ID);
		if (prefs != null)
		{
			try
			{
				String cachedAttributes = prefs.get(PREF_PREFIX + getProcessorId(), null);
				if (cachedAttributes != null)
				{
					Map map = (Map) JSON.parse(cachedAttributes);
					attributes.putAll(map);
				}
			}
			catch (Exception e)
			{
				ConfigurationsPlugin.logError("Error while loading the configuration status state", e); //$NON-NLS-1$
			}
		}
	}

	/*
	 * Notify the configuration processor with the change.
	 */
	private void notifyProcessor(String key)
	{
		synchronized (notificationSet)
		{
			notificationSet.clear();
			notificationSet.add(key);
			processor.notifyListeners(notificationSet);
		}
	}

	// ################# JSON Convertible Implementation ################
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void fromJSON(Map map)
	{
		attributes.putAll(map);
	}

	public void toJSON(Output out)
	{
		for (String key : attributes.keySet())
		{
			String value = attributes.get(key);
			try
			{
				Object outputObject = JSON.parse(value);
				out.add(key, outputObject);
			}
			catch (Exception e)
			{
				out.add(key, value);
			}
		}
	}
}
