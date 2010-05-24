package com.aptana.editor.common;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.aptana.parsing.ParsingPlugin;

public class ParserPoolFactory
{

	// TODO Hide instance in plugin, clean it up when plugin goes away
	private static ParserPoolFactory fgInstance;
	private Map<String, String> parsers;
	private HashMap<String, IParserPool> pools;

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
			String className = parsers.get(language);
			if (className == null)
				return null;
			pool = new ParserPool(className);
			pools.put(language, pool);
		}
		return pool;
	}

	public static ParserPoolFactory getInstance()
	{
		if (fgInstance == null)
		{
			fgInstance = new ParserPoolFactory();
		}
		return fgInstance;
	}

	private ParserPoolFactory()
	{
	}

	private static Map<String, String> getParsers()
	{
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		Map<String, String> parsers = new HashMap<String, String>();
		if (registry != null)
		{
			IExtensionPoint extensionPoint = registry.getExtensionPoint(ParsingPlugin.PLUGIN_ID,
					"parser"); //$NON-NLS-1$

			if (extensionPoint != null)
			{
				IExtension[] extensions = extensionPoint.getExtensions();

				for (IExtension extension : extensions)
				{
					IConfigurationElement[] elements = extension.getConfigurationElements();

					for (IConfigurationElement element : elements)
					{
						String language = element.getAttribute("language"); //$NON-NLS-1$
						String klass = element.getAttribute("class"); //$NON-NLS-1$
						parsers.put(language, klass);
					}
				}
			}
		}
		return parsers;
	}
}
