/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.internal.parsing.ParserPool;
import com.aptana.parsing.ast.IParseRootNode;

public class ParserPoolFactory
{
	// extension point constants
	private static final String PARSER_ID = "parser"; //$NON-NLS-1$
	private static final String ELEMENT_PARSER = "parser"; //$NON-NLS-1$
	private static final String ATTR_CONTENT_TYPE = "content-type"; //$NON-NLS-1$

	private static ParserPoolFactory INSTANCE;
	private Map<String, IConfigurationElement> parsers;
	private Map<String, IParserPool> pools;

	/**
	 * Singleton!
	 * 
	 * @return
	 */
	public static synchronized ParserPoolFactory getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new ParserPoolFactory();
		}

		return INSTANCE;
	}

	/**
	 * Returns a map from language to parser extension. We don't want instances of parsers yet, just info on how to
	 * generate instances, which we can with the configuration element.
	 * 
	 * @return
	 */
	private static Map<String, IConfigurationElement> getParsers()
	{
		final Map<String, IConfigurationElement> parsers = new HashMap<String, IConfigurationElement>();

		// @formatter:off
		EclipseUtil.processConfigurationElements(
			ParsingPlugin.PLUGIN_ID,
			PARSER_ID,
			new IConfigurationElementProcessor()
			{
				public void processElement(IConfigurationElement element)
				{
					String contentType = element.getAttribute(ATTR_CONTENT_TYPE);

					parsers.put(contentType, element);
				}

				public Set<String> getSupportElementNames()
				{
					return CollectionsUtil.newSet(ELEMENT_PARSER);
				}
			}
		);
		// @formatter:on

		return parsers;
	}

	/**
	 * Don't allow multiple instances.
	 */
	private ParserPoolFactory()
	{
	}

	/**
	 * dispose
	 */
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

	/**
	 * The main use of this class. Pass in a content type and get back an IParserPool to use to "borrow" a parser
	 * instance. If the specified content type does not exist in the parser pool, then we work our way up the base
	 * content types until we find a parser or fail.
	 * 
	 * @param contentTypeId
	 * @return
	 */
	public synchronized IParserPool getParserPool(String contentTypeId)
	{
		IContentTypeManager ctm = Platform.getContentTypeManager();
		IContentType contentType = ctm.getContentType(contentTypeId);
		IParserPool result = null;

		if (pools == null)
		{
			pools = new HashMap<String, IParserPool>();
		}

		while (result == null && (contentType != null || contentTypeId != null))
		{
			if (contentType != null)
			{
				contentTypeId = contentType.getId(); // $codepro.audit.disable questionableAssignment
			}
			result = pools.get(contentTypeId);

			if (result == null)
			{
				if (parsers == null)
				{
					parsers = getParsers();
				}

				IConfigurationElement parserExtension = parsers.get(contentTypeId);

				if (parserExtension != null)
				{
					result = new ParserPool(parserExtension);
					pools.put(contentTypeId, result);
				}
				else
				{
					contentType = contentType.getBaseType();

					if (contentType == null)
					{
						break;
					}
				}
			}
		}

		return result;
	}

	/**
	 * parse
	 * 
	 * @param contentTypeId
	 * @param source
	 * @return
	 */
	public static IParseRootNode parse(String contentTypeId, String source) throws Exception // $codepro.audit.disable
																								// declaredExceptions
	{
		return parse(contentTypeId, source, null);
	}

	/**
	 * parse
	 * 
	 * @param contentTypeId
	 * @param source
	 * @return
	 */
	public static IParseRootNode parse(String contentTypeId, String source, IProgressMonitor monitor) throws Exception // $codepro.audit.disable
																														// declaredExceptions
	{
		ParseState parseState = new ParseState();
		parseState.setEditState(source, null, 0, 0);
		parseState.setProgressMonitor(monitor);

		return parse(contentTypeId, parseState);
	}

	/**
	 * parse
	 * 
	 * @param contentTypeId
	 * @param source
	 * @return
	 */
	public static IParseRootNode parse(String contentTypeId, IParseState parseState) throws Exception // $codepro.audit.disable
																										// declaredExceptions
	{
		if (contentTypeId == null)
		{
			return null;
		}

		IParserPool pool = getInstance().getParserPool(contentTypeId);
		IParseRootNode result = null;

		if (pool != null)
		{
			IParser parser = pool.checkOut();

			if (parser != null)
			{
				try
				{
					result = parser.parse(parseState);
				}
				finally
				{
					pool.checkIn(parser);
				}
			}
			else
			{
				String message = MessageFormat.format(Messages.ParserPoolFactory_Cannot_Acquire_Parser, contentTypeId);
				IdeLog.logError(ParsingPlugin.getDefault(), message, IDebugScopes.PARSING);
			}
		}
		else
		{
			if (IdeLog.isInfoEnabled(ParsingPlugin.getDefault(), null))
			{
				String message = MessageFormat.format(Messages.ParserPoolFactory_Cannot_Acquire_Parser_Pool,
						contentTypeId);
				IdeLog.logInfo(ParsingPlugin.getDefault(), message, IDebugScopes.PARSING);
			}
		}

		return result;
	}
}
