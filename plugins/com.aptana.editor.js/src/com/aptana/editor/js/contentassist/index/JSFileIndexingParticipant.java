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
package com.aptana.editor.js.contentassist.index;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

import com.aptana.core.util.IOUtil;
import com.aptana.editor.common.tasks.TaskTag;
import com.aptana.editor.js.Activator;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.JSIndexQueryHelper;
import com.aptana.editor.js.contentassist.model.ContentSelector;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.editor.js.inferencing.JSScope;
import com.aptana.editor.js.inferencing.JSSymbolTypeInferrer;
import com.aptana.editor.js.inferencing.JSTypeUtil;
import com.aptana.editor.js.parsing.IJSParserConstants;
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
			Activator.logError(e.getMessage(), e);
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
	 * @see com.aptana.index.core.IFileIndexingParticipant#index(java.util.Set, com.aptana.index.core.Index,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void index(Set<IFileStore> files, Index index, IProgressMonitor monitor) throws CoreException
	{
		SubMonitor sub = SubMonitor.convert(monitor, files.size() * 100);

		for (IFileStore file : files)
		{
			if (sub.isCanceled())
			{
				throw new CoreException(Status.CANCEL_STATUS);
			}

			Thread.yield(); // be nice to other threads, let them get in before each file...

			this.indexFileStore(index, file, sub.newChild(100));
		}

		sub.done();
	}

	/**
	 * indexFileStore
	 * 
	 * @param index
	 * @param file
	 * @param monitor
	 */
	private void indexFileStore(Index index, IFileStore file, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		
		try
		{
			if (file != null)
			{
				sub.subTask(file.getName());
	
				removeTasks(file, sub.newChild(10));
	
				// grab the source of the file we're going to parse
				String source = IOUtil.read(file.openInputStream(EFS.NONE, sub.newChild(20)));
	
				// minor optimization when creating a new empty file
				if (source != null && source.trim().length() > 0)
				{
					IParseNode ast = ParserPoolFactory.parse(IJSParserConstants.LANGUAGE, source);
					sub.worked(50);
					
					if (ast != null)
					{
						this.processParseResults(file, source, index, ast, sub.newChild(20));
					}
				}
			}
		}
		catch (Throwable e)
		{
			Activator.logError(e.getMessage(), e);
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

				if (functions.isEmpty() == false)
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
			e.printStackTrace();
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
			processComments(file, source, ast.getStartingOffset(), ((IParseRootNode) ast).getCommentNodes(),
					sub.newChild(20));
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

	private void processComments(IFileStore file, String source, int initialOffset, IParseNode[] commentNodes,
			IProgressMonitor monitor)
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
				processCommentNode(file, source, initialOffset, (JSCommentNode) commentNode);
			}
			sub.worked(1);
		}
		sub.done();
	}

	private void processCommentNode(IFileStore store, String source, int initialOffset, JSCommentNode commentNode)
	{
		int offset = initialOffset;
		String text = getText(source, initialOffset, commentNode);
		if (!TaskTag.isCaseSensitive())
		{
			text = text.toLowerCase();
		}
		String[] lines = text.split("\r\n|\r|\n"); //$NON-NLS-1$
		for (String line : lines)
		{
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
				createTask(store, message, entry.getPriority(), -1, start, start + message.length());
			}
			// FIXME This doesn't take the newline into account from split!
			offset += line.length();
		}
	}

	private String getText(String source, int initialOffset, JSCommentNode commentNode)
	{
		return new String(source.substring(initialOffset + commentNode.getStartingOffset(),
				initialOffset + commentNode.getEndingOffset() + 1));
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

					if (typeNames != null && typeNames.isEmpty() == false)
					{
						JSIndexQueryHelper queryHelper = new JSIndexQueryHelper();

						result = queryHelper.getTypeMembers(index, typeNames, EnumSet.allOf(ContentSelector.class));
					}
				}
			}
		}

		return result;
	}
}
