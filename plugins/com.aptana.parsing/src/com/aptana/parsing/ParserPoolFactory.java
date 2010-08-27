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
	 * The main use of this class. Pass in a language (usually "text/language") and get back an IParserPool to use to
	 * "borrow" a parser instance.
	 * 
	 * @param language
	 * @return
	 */
	public synchronized IParserPool getParserPool(String language)
	{
		if (pools == null)
		{
			pools = new HashMap<String, IParserPool>();
		}
		IParserPool pool = pools.get(language);
		if (pool == null)
		{
			if (parsers == null)
			{
				parsers = getParsers();
			}
			IConfigurationElement parserExtension = parsers.get(language);
			if (parserExtension == null)
				return null;
			pool = new ParserPool(parserExtension);
			pools.put(language, pool);
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
