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
package com.aptana.parsing;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.aptana.internal.parsing.ParserPool;

public class ParserPoolFactory
{

	private static ParserPoolFactory fgInstance;
	private Map<String, IConfigurationElement> parsers;
	private HashMap<String, IParserPool> pools;

	/**
	 * The main use of this class. Pass in a language (usually "text/language") or content type and get back an
	 * IParserPool to use to "borrow" a parser instance.
	 * 
	 * @param languageOrContentType
	 * @return
	 */
	public synchronized IParserPool getParserPool(String languageOrContentType)
	{
		if (pools == null)
		{
			pools = new HashMap<String, IParserPool>();
		}
		IParserPool pool = pools.get(languageOrContentType);
		if (pool == null)
		{
			if (parsers == null)
			{
				parsers = getParsers();
			}
			IConfigurationElement parserExtension = parsers.get(languageOrContentType);
			if (parserExtension == null)
				return null;
			pool = new ParserPool(parserExtension);
			pools.put(languageOrContentType, pool);
		}
		return pool;
	}

	/**
	 * Singleton!
	 * 
	 * @return
	 */
	public static ParserPoolFactory getInstance()
	{
		if (fgInstance == null)
		{
			fgInstance = new ParserPoolFactory();
		}
		return fgInstance;
	}

	/**
	 * Don't allow multiple instances.
	 */
	private ParserPoolFactory()
	{
	}

	/**
	 * Returns a map from language to parser extension. We don't want instances of parsers yet, just info on how to
	 * generate instances, which we can with the configuration element.
	 * 
	 * @return
	 */
	private static Map<String, IConfigurationElement> getParsers()
	{
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		Map<String, IConfigurationElement> parsers = new HashMap<String, IConfigurationElement>();
		if (registry != null)
		{
			IExtensionPoint extensionPoint = registry.getExtensionPoint(ParsingPlugin.PLUGIN_ID, "parser"); //$NON-NLS-1$

			if (extensionPoint != null)
			{
				IExtension[] extensions = extensionPoint.getExtensions();

				for (IExtension extension : extensions)
				{
					IConfigurationElement[] elements = extension.getConfigurationElements();

					for (IConfigurationElement element : elements)
					{
						String language = element.getAttribute("language"); //$NON-NLS-1$
						parsers.put(language, element);
						String contentType = element.getAttribute("content-type"); //$NON-NLS-1$
						parsers.put(contentType, element);
					}
				}
			}
		}
		return parsers;
	}

	synchronized void dispose()
	{
		if (pools != null)
		{
			// Clean all the parsers up!
			for (Map.Entry<String, IParserPool> entry : pools.entrySet())
			{
				IParserPool pool = entry.getValue();
				pool.dispose();
			}
			pools.clear();
			pools = null;
		}
		if (parsers != null)
		{
			parsers.clear();
			parsers = null;
		}
	}
}
