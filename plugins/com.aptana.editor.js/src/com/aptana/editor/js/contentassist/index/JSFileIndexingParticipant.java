/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist.index;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.js.IDebugScopes;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.JSIndexQueryHelper;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.editor.js.inferencing.JSScope;
import com.aptana.editor.js.inferencing.JSSymbolTypeInferrer;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSParseRootNode;
import com.aptana.index.core.AbstractFileIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.index.core.build.BuildContext;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.xpath.ParseNodeXPath;

public class JSFileIndexingParticipant extends AbstractFileIndexingParticipant
{
	private static XPath LAMBDAS_IN_SCOPE;
	private JSIndexWriter _indexWriter;

	static
	{
		try
		{
			LAMBDAS_IN_SCOPE = new ParseNodeXPath(
					"invoke[position() = 1]/group/function|invoke[position() = 1]/function"); //$NON-NLS-1$
		}
		catch (JaxenException e)
		{
			IdeLog.logError(JSPlugin.getDefault(), e);
		}
	}

	/**
	 * JSFileIndexingParticipant
	 */
	public JSFileIndexingParticipant()
	{
		this._indexWriter = new JSIndexWriter();
	}

	/**
	 * getGlobals
	 * 
	 * @param root
	 * @return
	 */
	protected JSScope getGlobals(IParseNode root)
	{
		JSScope result = null;

		if (root instanceof JSParseRootNode)
		{
			result = ((JSParseRootNode) root).getGlobals();
		}

		return result;
	}

	public void index(BuildContext context, Index index, IProgressMonitor monitor) throws CoreException
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		try
		{
			sub.subTask(getIndexingMessage(index, context.getURI()));
			processParseResults(context, index, context.getAST(), sub.newChild(20));
		}
		catch (CoreException ce)
		{
			IdeLog.logError(JSPlugin.getDefault(), ce);
		}
		finally
		{
			sub.done();
		}
	}

	/**
	 * processLambdas
	 * 
	 * @param index
	 * @param globals
	 * @param node
	 * @param location
	 */
	@SuppressWarnings("unchecked")
	private List<PropertyElement> processLambdas(Index index, JSScope globals, IParseNode node, URI location)
	{
		List<PropertyElement> result = Collections.emptyList();

		try
		{
			Object queryResult = LAMBDAS_IN_SCOPE.evaluate(node);

			if (queryResult != null)
			{
				List<JSFunctionNode> functions = (List<JSFunctionNode>) queryResult;

				if (!functions.isEmpty())
				{
					result = new ArrayList<PropertyElement>();

					for (JSFunctionNode function : functions)
					{
						// grab the correct scope for this function's body
						JSScope scope = globals.getScopeAtOffset(function.getBody().getStartingOffset());

						// add all properties off of "window" to our list
						result.addAll(this.processWindowAssignments(index, scope, location));

						// handle any nested lambdas in this function
						result.addAll(this.processLambdas(index, globals, function, location));
					}
				}
			}
		}
		catch (JaxenException e)
		{
			IdeLog.logError(JSPlugin.getDefault(), e);
		}

		return result;
	}

	/**
	 * processParseResults
	 * 
	 * @param context
	 * @param monitor
	 * @param parseState
	 */
	public void processParseResults(BuildContext context, Index index, IParseNode ast, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 80);

		// build symbol tables
		URI location = context.getURI();

		if (IdeLog.isTraceEnabled(JSPlugin.getDefault(), IDebugScopes.INDEXING_STEPS))
		{
			// @formatter:off
			String message = MessageFormat.format(
				"Building symbol tables for file ''{0}'' for index ''{1}''", //$NON-NLS-1$
				location.toString(),
				index.toString()
			);
			// @formatter:on

			IdeLog.logTrace(JSPlugin.getDefault(), message, IDebugScopes.INDEXING_STEPS);
		}

		JSScope globals = this.getGlobals(ast);

		// process globals
		if (globals != null)
		{
			// create new Window type for this file
			TypeElement type = new TypeElement();
			type.setName(JSTypeConstants.WINDOW_TYPE);
			type.addParentType(JSTypeConstants.GLOBAL_TYPE);

			// add declared variables and functions from the global scope
			if (IdeLog.isTraceEnabled(JSPlugin.getDefault(), IDebugScopes.INDEXING_STEPS))
			{
				// @formatter:off
				String message = MessageFormat.format(
					"Processing globally declared variables and functions in file ''{0}'' for index ''{1}''", //$NON-NLS-1$
					location.toString(),
					index.toString()
				);
				// @formatter:on

				IdeLog.logTrace(JSPlugin.getDefault(), message, IDebugScopes.INDEXING_STEPS);
			}

			JSSymbolTypeInferrer symbolInferrer = new JSSymbolTypeInferrer(globals, index, location);

			for (PropertyElement property : symbolInferrer.getScopeProperties())
			{
				type.addProperty(property);
			}

			// include any assignments to Window
			if (IdeLog.isTraceEnabled(JSPlugin.getDefault(), IDebugScopes.INDEXING_STEPS))
			{
				// @formatter:off
				String message = MessageFormat.format(
					"Processing assignments to ''window'' in file ''{0}'' for index ''{1}''", //$NON-NLS-1$
					location.toString(),
					index.toString()
				);
				// @formatter:on

				IdeLog.logTrace(JSPlugin.getDefault(), message, IDebugScopes.INDEXING_STEPS);
			}

			for (PropertyElement property : this.processWindowAssignments(index, globals, location))
			{
				type.addProperty(property);
			}

			// process window assignments in lambdas (self-invoking functions)
			if (IdeLog.isTraceEnabled(JSPlugin.getDefault(), IDebugScopes.INDEXING_STEPS))
			{
				// @formatter:off
				String message = MessageFormat.format(
					"Processing assignments to ''window'' within self-invoking function literals in file ''{0}'' for index ''{1}''", //$NON-NLS-1$
					location.toString(),
					index.toString()
				);
				// @formatter:on

				IdeLog.logTrace(JSPlugin.getDefault(), message, IDebugScopes.INDEXING_STEPS);
			}

			for (PropertyElement property : this.processLambdas(index, globals, ast, location))
			{
				type.addProperty(property);
			}

			// associate all user agents with these properties
			if (IdeLog.isTraceEnabled(JSPlugin.getDefault(), IDebugScopes.INDEXING_STEPS))
			{
				// @formatter:off
				String message = MessageFormat.format(
					"Assigning user agents to properties in file ''{0}'' for index ''{1}''", //$NON-NLS-1$
					location.toString(),
					index.toString()
				);
				// @formatter:on

				IdeLog.logTrace(JSPlugin.getDefault(), message, IDebugScopes.INDEXING_STEPS);
			}

			for (PropertyElement property : type.getProperties())
			{
				property.setHasAllUserAgents();
			}

			// write new Window type to index
			if (IdeLog.isTraceEnabled(JSPlugin.getDefault(), IDebugScopes.INDEXING_STEPS))
			{
				// @formatter:off
				String message = MessageFormat.format(
					"Writing indexing results to index ''{0}'' for file ''{1}''", //$NON-NLS-1$
					index.toString(),
					location.toString()
				);
				// @formatter:on

				IdeLog.logTrace(JSPlugin.getDefault(), message, IDebugScopes.INDEXING_STEPS);
			}

			this._indexWriter.writeType(index, type, location);
		}

		sub.done();
	}

	/**
	 * processWindowAssignments
	 * 
	 * @param index
	 * @param symbols
	 * @param location
	 */
	private List<PropertyElement> processWindowAssignments(Index index, JSScope symbols, URI location)
	{
		List<PropertyElement> result = Collections.emptyList();

		if (symbols != null)
		{
			if (symbols.hasLocalSymbol(JSTypeConstants.WINDOW_PROPERTY))
			{
				JSSymbolTypeInferrer symbolInferrer = new JSSymbolTypeInferrer(symbols, index, location);
				PropertyElement property = symbolInferrer.getSymbolPropertyElement(JSTypeConstants.WINDOW_PROPERTY);

				if (property != null)
				{
					List<String> typeNames = property.getTypeNames();

					if (typeNames != null && !typeNames.isEmpty())
					{
						JSIndexQueryHelper queryHelper = new JSIndexQueryHelper();

						result = queryHelper.getTypeMembers(index, typeNames);
					}
				}
			}
		}

		return result;
	}
}
