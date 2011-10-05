/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist.index;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

import beaver.Parser;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.resources.TaskTag;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.js.IJSConstants;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.JSIndexQueryHelper;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.editor.js.inferencing.JSScope;
import com.aptana.editor.js.inferencing.JSSymbolTypeInferrer;
import com.aptana.editor.js.inferencing.JSTypeUtil;
import com.aptana.editor.js.parsing.ast.JSCommentNode;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSParseRootNode;
import com.aptana.index.core.AbstractFileIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.AbstractFileIndexingParticipant#indexFileStore(com.aptana.index.core.Index,
	 * org.eclipse.core.filesystem.IFileStore, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void indexFileStore(Index index, IFileStore file, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);

		try
		{
			if (file != null)
			{
				sub.subTask(getIndexingMessage(index, file));

				removeTasks(file, sub.newChild(10));

				// grab the source of the file we're going to parse
				String source = IOUtil.read(file.openInputStream(EFS.NONE, sub.newChild(20)), getCharset(file));

				// minor optimization when creating a new empty file
				if (source != null && source.trim().length() > 0)
				{
					IParseNode ast = ParserPoolFactory.parse(IJSConstants.CONTENT_TYPE_JS, source, sub.newChild(50));
					if (ast != null)
					{
						this.processParseResults(file, source, index, ast, sub.newChild(20));
					}
				}
			}
		}
		catch (Parser.Exception e) // $codepro.audit.disable emptyCatchClause
		{
			// ignore parse errors
		}
		catch (Throwable e)
		{
			IdeLog.logError(JSPlugin.getDefault(), e);
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
	 * @param index
	 * @param file
	 * @param monitor
	 * @param parseState
	 */
	public void processParseResults(IFileStore file, String source, Index index, IParseNode ast,
			IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		if (ast instanceof IParseRootNode)
		{
			processComments(file, source, ((IParseRootNode) ast).getCommentNodes(), sub.newChild(20));
		}
		sub.setWorkRemaining(80);

		JSScope globals = this.getGlobals(ast);
		if (globals != null)
		{
			URI location = file.toURI();

			// create new Window type for this file
			TypeElement type = new TypeElement();
			type.setName(JSTypeConstants.WINDOW_TYPE);
			type.addParentType(JSTypeConstants.GLOBAL_TYPE);

			JSSymbolTypeInferrer symbolInferrer = new JSSymbolTypeInferrer(globals, index, location);

			// add declared variables and functions from the global scope
			for (PropertyElement property : symbolInferrer.getScopeProperties())
			{
				type.addProperty(property);
			}

			// include any assignments to Window
			for (PropertyElement property : this.processWindowAssignments(index, globals, location))
			{
				type.addProperty(property);
			}

			// process window assignments in lambdas (self-invoking functions)
			for (PropertyElement property : this.processLambdas(index, globals, ast, location))
			{
				type.addProperty(property);
			}

			// associate all user agents with these properties
			for (PropertyElement property : type.getProperties())
			{
				JSTypeUtil.addAllUserAgents(property);
			}

			// write new Window type to index
			this._indexWriter.writeType(index, type, location);
		}

		sub.done();
	}

	private void processComments(IFileStore file, String source, IParseNode[] commentNodes, IProgressMonitor monitor)
	{
		if (commentNodes == null || commentNodes.length == 0)
		{
			return;
		}
		SubMonitor sub = SubMonitor.convert(monitor, commentNodes.length);
		for (IParseNode commentNode : commentNodes)
		{
			if (commentNode instanceof JSCommentNode)
			{
				processCommentNode(file, source, (JSCommentNode) commentNode);
			}
			sub.worked(1);
		}
		sub.done();
	}

	private void processCommentNode(IFileStore store, String source, JSCommentNode commentNode)
	{
		String text = getText(source, commentNode);
		if (!TaskTag.isCaseSensitive())
		{
			text = text.toLowerCase();
		}
		int lastOffset = 0;
		String[] lines = StringUtil.LINE_SPLITTER.split(text);
		for (String line : lines)
		{
			int offset = text.indexOf(line, lastOffset);

			for (TaskTag entry : TaskTag.getTaskTags())
			{
				String tag = entry.getName();
				if (!TaskTag.isCaseSensitive())
				{
					tag = tag.toLowerCase();
				}
				int index = line.indexOf(tag);
				if (index == -1)
				{
					continue;
				}

				String message = line.substring(index).trim();
				// Remove "**/" from the end of the line!
				if (message.endsWith("**/")) //$NON-NLS-1$
				{
					message = message.substring(0, message.length() - 3).trim();
				}
				// Remove "*/" from the end of the line!
				if (message.endsWith("*/")) //$NON-NLS-1$
				{
					message = message.substring(0, message.length() - 2).trim();
				}
				int start = commentNode.getStartingOffset() + offset + index;
				createTask(store, message, entry.getPriority(), getLineNumber(start, source), start,
						start + message.length());
			}

			lastOffset = offset;
		}
	}

	private String getText(String source, JSCommentNode commentNode)
	{
		return new String(source.substring(commentNode.getStartingOffset(), commentNode.getEndingOffset() + 1));
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
