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
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSParseRootNode;
import com.aptana.index.core.IFileStoreIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.parsing.IParser;
import com.aptana.parsing.IParserPool;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.xpath.ParseNodeXPath;

public class JSFileIndexingParticipant implements IFileStoreIndexingParticipant
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
		
		if (file == null)
		{
			return;
		}
		try
		{
			sub.subTask(file.getName());

			try
			{
				// grab the source of the file we're going to parse
				String source = IOUtil.read(file.openInputStream(EFS.NONE, sub.newChild(20)));

				// minor optimization when creating a new empty file
				if (source != null && source.length() > 0)
				{
					// create parser and associated parse state
					IParserPool pool = ParserPoolFactory.getInstance().getParserPool(IJSParserConstants.LANGUAGE);

					if (pool != null)
					{
						IParser parser = pool.checkOut();

						// apply the source to the parse state and parse
						ParseState parseState = new ParseState();
						parseState.setEditState(source, source, 0, 0);
						parser.parse(parseState);

						pool.checkIn(parser);
						sub.worked(50);

						// process results
						this.processParseResults(index, parseState.getParseResult(), file.toURI());
					}
				}
			}
			catch (beaver.Parser.Exception e)
			{
				// just like in FileServer ... "not logging the parsing error here since
				// the source could be in an intermediate state of being edited by the user"
			}
			catch (Throwable e)
			{
				Activator.logError(e.getMessage(), e);
			}
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
	 * @param parseState
	 */
	public void processParseResults(Index index, IParseNode ast, URI location)
	{
		JSScope globals = this.getGlobals(ast);

		if (globals != null)
		{
			// create new Window type for this file
			TypeElement type = new TypeElement();
			type.setName(JSTypeConstants.WINDOW_TYPE);

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
