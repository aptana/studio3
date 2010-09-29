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
package com.aptana.scripting.model;

import java.io.InputStream;
import java.io.OutputStream;
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

import com.aptana.scripting.Activator;

public class CommandContext
{
	// Key names for well known keys in the map
	public static final String INPUT_TYPE = "input_type"; //$NON-NLS-1$
	public static final String INVOKED_VIA = "invoked_via"; //$NON-NLS-1$

	private static final String CONTEXT_CONTRIBUTOR_ID = "contextContributors"; //$NON-NLS-1$
	private static final String TAG_CONTRIBUTOR = "contributor"; //$NON-NLS-1$
	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
	
	private static ContextContributor[] contextContributors;
	
	private Map<String,Object> _map;
	private InputStream _inputStream;
	private OutputStream _outputStream;
	private OutputStream _errorStream;
	private OutputStream _consoleStream;
	private OutputType _outputType;
	private boolean _forcedExit;

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
	 * getConsoleStream
	 * 
	 * @return
	 */
	public OutputStream getConsoleStream()
	{
		return this._consoleStream;
	}
	
	/**
	 * getErrorStream
	 * 
	 * @return
	 */
	public OutputStream getErrorStream()
	{
		return this._errorStream;
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
	 * getOutputStream
	 * 
	 * @return
	 */
	public OutputStream getOutputStream()
	{
		return this._outputStream;
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
	 * getOutputType
	 * 
	 * @return
	 */
	public OutputType getOutputType()
	{
		return this._outputType;
	}
	
	/**
	 * isForcedExit
	 * 
	 * @return
	 */
	public boolean isForcedExit()
	{
		return this._forcedExit;
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
	 * setConsoleStream
	 * 
	 * @param stream
	 */
	public void setConsoleStream(OutputStream stream)
	{
		this._consoleStream = stream;
	}
	
	/**
	 * setErrorStream
	 * 
	 * @param stream
	 */
	public void setErrorStream(OutputStream stream)
	{
		this._errorStream = stream;
	}
	
	/**
	 * setForceExit
	 * 
	 * @param value
	 */
	public void setForcedExit(boolean value)
	{
		this._forcedExit = value;
	}
	
	/**
	 * @param inputStream
	 */
	public void setInputStream(InputStream inputStream)
	{
		this._inputStream = inputStream;
	}
	
	/**
	 * setOutputStream
	 * 
	 * @param stream
	 */
	public void setOutputStream(OutputStream stream)
	{
		this._outputStream = stream;
	}
	
	/**
	 * setOutputType
	 * 
	 * @param type
	 */
	public void setOutputType(OutputType type)
	{
		this._outputType = type;
	}

	/**
	 * setOutput
	 * 
	 * @param output
	 */
	public void setOutputType(String output)
	{
		this._outputType = OutputType.get(output);
	}
}
