/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.index;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.index.core.AbstractFileIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.index.core.build.BuildContext;
import com.aptana.js.core.IDebugScopes;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.JSTypeConstants;
import com.aptana.js.core.inferencing.JSPropertyCollection;
import com.aptana.js.core.inferencing.JSScope;
import com.aptana.js.core.inferencing.JSTypeUtil;
import com.aptana.js.core.model.PropertyElement;
import com.aptana.js.core.model.TypeElement;
import com.aptana.js.core.parsing.ast.IJSNodeTypes;
import com.aptana.js.core.parsing.ast.JSCommentNode;
import com.aptana.js.core.parsing.ast.JSFunctionNode;
import com.aptana.js.core.parsing.ast.JSInvokeNode;
import com.aptana.js.core.parsing.ast.JSParseRootNode;
import com.aptana.js.core.parsing.ast.JSStringNode;
import com.aptana.js.internal.core.index.JSIndexWriter;
import com.aptana.js.internal.core.inferencing.JSSymbolTypeInferrer;
import com.aptana.js.internal.core.parsing.sdoc.SDocParser;
import com.aptana.js.internal.core.parsing.sdoc.model.DocumentationBlock;
import com.aptana.js.internal.core.parsing.sdoc.model.Tag;
import com.aptana.js.internal.core.parsing.sdoc.model.TagType;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.parsing.xpath.ParseNodeXPath;

public class JSFileIndexingParticipant extends AbstractFileIndexingParticipant
{
	private static XPath LAMBDAS_IN_SCOPE;
	private static XPath REQUIRE_INVOCATIONS;

	private JSIndexWriter indexWriter;

	static
	{
		try
		{
			LAMBDAS_IN_SCOPE = new ParseNodeXPath(
					"invoke[position() = 1]/group/function|invoke[position() = 1]/function"); //$NON-NLS-1$
		}
		catch (JaxenException e)
		{
			IdeLog.logError(JSCorePlugin.getDefault(), e);
		}

		try
		{
			REQUIRE_INVOCATIONS = new ParseNodeXPath("//invoke[identifier[position() = 1 and @value = 'require']]"); //$NON-NLS-1$
		}
		catch (JaxenException e)
		{
			IdeLog.logError(JSCorePlugin.getDefault(), e);
		}
	}

	/**
	 * JSFileIndexingParticipant
	 */
	public JSFileIndexingParticipant()
	{
		indexWriter = new JSIndexWriter();
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
			// ignores the parser exception
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
			Object queryResult = (LAMBDAS_IN_SCOPE != null) ? LAMBDAS_IN_SCOPE.evaluate(node) : null;

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
						result.addAll(processWindowAssignments(index, scope, location));

						// handle any nested lambdas in this function
						result.addAll(processLambdas(index, globals, function, location));
					}
				}
			}
		}
		catch (JaxenException e)
		{
			IdeLog.logError(JSCorePlugin.getDefault(), e);
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

		if (IdeLog.isTraceEnabled(JSCorePlugin.getDefault(), IDebugScopes.INDEXING_STEPS))
		{
			// @formatter:off
			String message = MessageFormat.format(
				"Building symbol tables for file ''{0}'' for index ''{1}''", //$NON-NLS-1$
				location.toString(),
				index.toString()
			);
			// @formatter:on

			IdeLog.logTrace(JSCorePlugin.getDefault(), message, IDebugScopes.INDEXING_STEPS);
		}

		JSScope globals = getGlobals(ast);

		// process globals
		if (globals != null)
		{
			// create new Window type for this file
			TypeElement type = JSTypeUtil.createGlobalType(JSTypeUtil.getGlobalType(context.getProject(),
					context.getName()));

			// add declared variables and functions from the global scope
			if (IdeLog.isTraceEnabled(JSCorePlugin.getDefault(), IDebugScopes.INDEXING_STEPS))
			{
				// @formatter:off
				String message = MessageFormat.format(
					"Processing globally declared variables and functions in file ''{0}'' for index ''{1}''", //$NON-NLS-1$
					location.toString(),
					index.toString()
				);
				// @formatter:on

				IdeLog.logTrace(JSCorePlugin.getDefault(), message, IDebugScopes.INDEXING_STEPS);
			}

			JSSymbolTypeInferrer symbolInferrer = new JSSymbolTypeInferrer(globals, index, location);

			for (PropertyElement property : symbolInferrer.getScopeProperties())
			{
				type.addProperty(property);
			}

			// include any assignments to Window
			if (IdeLog.isTraceEnabled(JSCorePlugin.getDefault(), IDebugScopes.INDEXING_STEPS))
			{
				// @formatter:off
				String message = MessageFormat.format(
					"Processing assignments to ''window'' in file ''{0}'' for index ''{1}''", //$NON-NLS-1$
					location.toString(),
					index.toString()
				);
				// @formatter:on

				IdeLog.logTrace(JSCorePlugin.getDefault(), message, IDebugScopes.INDEXING_STEPS);
			}

			for (PropertyElement property : processWindowAssignments(index, globals, location))
			{
				type.addProperty(property);
			}

			// process window assignments in lambdas (self-invoking functions)
			if (IdeLog.isTraceEnabled(JSCorePlugin.getDefault(), IDebugScopes.INDEXING_STEPS))
			{
				// @formatter:off
				String message = MessageFormat.format(
					"Processing assignments to ''window'' within self-invoking function literals in file ''{0}'' for index ''{1}''", //$NON-NLS-1$
					location.toString(),
					index.toString()
				);
				// @formatter:on

				IdeLog.logTrace(JSCorePlugin.getDefault(), message, IDebugScopes.INDEXING_STEPS);
			}

			for (PropertyElement property : processLambdas(index, globals, ast, location))
			{
				type.addProperty(property);
			}

			// associate all user agents with these properties
			if (IdeLog.isTraceEnabled(JSCorePlugin.getDefault(), IDebugScopes.INDEXING_STEPS))
			{
				// @formatter:off
				String message = MessageFormat.format(
					"Assigning user agents to properties in file ''{0}'' for index ''{1}''", //$NON-NLS-1$
					location.toString(),
					index.toString()
				);
				// @formatter:on

				IdeLog.logTrace(JSCorePlugin.getDefault(), message, IDebugScopes.INDEXING_STEPS);
			}

			for (PropertyElement property : type.getProperties())
			{
				property.setHasAllUserAgents();
			}

			// write new Window type to index
			if (IdeLog.isTraceEnabled(JSCorePlugin.getDefault(), IDebugScopes.INDEXING_STEPS))
			{
				// @formatter:off
				String message = MessageFormat.format(
					"Writing indexing results to index ''{0}'' for file ''{1}''", //$NON-NLS-1$
					index.toString(),
					location.toString()
				);
				// @formatter:on

				IdeLog.logTrace(JSCorePlugin.getDefault(), message, IDebugScopes.INDEXING_STEPS);
			}

			indexWriter.writeType(index, type, location);
		}

		// process requires
		processRequires(index, ast, location);

		// process module API exports
		processModule(context, index, ast, location);

		sub.done();
	}

	/**
	 * @param ast
	 */
	protected void processRequires(Index index, IParseNode ast, URI location)
	{
		Object queryResult = null;

		// grab all 'require("...")' invocations
		try
		{
			queryResult = (REQUIRE_INVOCATIONS != null) ? REQUIRE_INVOCATIONS.evaluate(ast) : null;
		}
		catch (JaxenException e)
		{
			IdeLog.logError(JSCorePlugin.getDefault(), e);
		}

		// process results, if any
		if (queryResult != null)
		{
			Set<String> paths = new HashSet<String>();

			@SuppressWarnings("unchecked")
			List<JSInvokeNode> invocations = (List<JSInvokeNode>) queryResult;

			for (JSInvokeNode invocation : invocations)
			{
				IParseNode arguments = invocation.getArguments();
				IParseNode firstArgument = (arguments != null) ? arguments.getFirstChild() : null;
				String text = (firstArgument instanceof JSStringNode) ? firstArgument.getText() : null;

				if (text != null && text.length() >= 2)
				{
					paths.add(text.substring(1, text.length() - 1));
				}
			}

			if (!paths.isEmpty())
			{
				indexWriter.writeRequires(index, paths, location);
			}
		}
	}

	/**
	 * @param index
	 * @param ast
	 * @param location
	 */
	protected void processModule(BuildContext context, Index index, IParseNode ast, URI location)
	{
		JSScope globals = getGlobals(ast);
		if (globals == null)
		{
			return;
		}

		String moduleId = getModuleId(context, index, ast, location);

		// Create a type for this module...
		TypeElement moduleType = new TypeElement();
		moduleType.setHasAllUserAgents();
		moduleType.setName(moduleId);

		// Create a type for the "module instance", which is "module.exports"
		TypeElement moduleExportsType = new TypeElement();
		moduleExportsType.setHasAllUserAgents();
		moduleExportsType.setName(moduleType.getName() + ".exports"); //$NON-NLS-1$

		// Grab the defined "module" variable from the file
		JSPropertyCollection module = globals.getSymbol("module"); //$NON-NLS-1$
		if (module != null)
		{
			JSSymbolTypeInferrer infer = new JSSymbolTypeInferrer(globals, index, location);
			// Now grab "module.exports" and attach that property to our hand-generated module type from above
			PropertyElement exports = infer.getSymbolPropertyElement(module, "exports"); //$NON-NLS-1$
			moduleType.addProperty(exports);

			// Now copy over the type info from the module.exports property to the hand-generated module instance
			for (String type : exports.getTypeNames())
			{
				// TODO Where should we strip out the type info on the elements in the collection? This doesn't seem
				// like the right place, maybe we should do it in CA processor, or index query helper's get ancestor
				// names?
				if (type.startsWith("Array<")) //$NON-NLS-1$
				{
					type = "Array"; //$NON-NLS-1$
				}
				moduleExportsType.addParentType(type);
			}
		}
		else
		{
			// There's no "module" object, so let's look for properties hanging off "exports" var.
			JSPropertyCollection exports = globals.getSymbol("exports"); //$NON-NLS-1$
			if (exports == null)
			{
				// Doesn't look like there's any CommonJS kung-fu here. No module.exports or exports objects
				return;
			}
			// Grab all properties hanging off "exports" and attach them to our hand-generate "module.exports" module
			// instance type.
			JSSymbolTypeInferrer infer = new JSSymbolTypeInferrer(globals, index, location);
			List<String> properties = exports.getPropertyNames();
			for (String property : properties)
			{
				PropertyElement propElement = infer.getSymbolPropertyElement(exports, property);
				moduleExportsType.addProperty(propElement);
			}
		}

		// Now we also add special properties that modules define
		// Add an id property to the module instance (module.exports)!
		PropertyElement idElement = new PropertyElement();
		idElement.setIsInstanceProperty(true);
		idElement.setHasAllUserAgents();
		idElement.setName("id"); //$NON-NLS-1$
		idElement.addType("String"); //$NON-NLS-1$
		idElement.setDescription(moduleId); // Just pass in the actual value?
		moduleExportsType.addProperty(idElement);
		// Add a uri property to the module instance (module.exports)!
		PropertyElement uriElement = new PropertyElement();
		uriElement.setIsInstanceProperty(true);
		uriElement.setHasAllUserAgents();
		uriElement.setName("uri"); //$NON-NLS-1$
		uriElement.addType("String"); //$NON-NLS-1$
		uriElement.setDescription(location.toString()); // Just pass in the actual value?
		moduleExportsType.addProperty(uriElement);

		// Now write our hand-generated module type and module instance type.
		indexWriter.writeType(index, moduleExportsType, location);
		indexWriter.writeType(index, moduleType, location);
	}

	/**
	 * Attempts to determine the id of the module defined in the file. We look for @module tags for an explicit
	 * declaration. Otherwise we assume an id based on the relative path to the project/index root.
	 * 
	 * @param context
	 * @param index
	 * @param ast
	 * @param location
	 * @return
	 */
	private String getModuleId(BuildContext context, Index index, IParseNode ast, URI location)
	{
		// We need to search for @module tag to get the declared module id!
		IParseRootNode root = (IParseRootNode) ast;
		IParseNode[] comments = root.getCommentNodes();
		if (!ArrayUtil.isEmpty(comments))
		{
			for (IParseNode comment : comments)
			{
				if (comment instanceof JSCommentNode)
				{
					JSCommentNode commentNode = (JSCommentNode) comment;
					if (commentNode.getNodeType() == IJSNodeTypes.SDOC_COMMENT)
					{
						SDocParser parser = new SDocParser();
						try
						{
							String source = context.getContents().substring(commentNode.getStart(),
									commentNode.getEnd() + 1);
							DocumentationBlock result = (DocumentationBlock) parser.parse(source);
							List<Tag> moduleTags = result.getTags(TagType.MODULE);
							if (!CollectionsUtil.isEmpty(moduleTags))
							{
								return moduleTags.get(0).getText();
							}
						}
						catch (Exception e)
						{
							// ignore errors parsing SDoc comments
						}
					}
				}
			}
		}

		// The module top-level id should be based on the path from "the conceptual module name space root."
		// Should we assume the project/index root?
		URI relativeToRoot = index.getRelativeDocumentPath(location);
		return Path.fromPortableString(relativeToRoot.getPath()).removeFileExtension().toPortableString();
	}

	/**
	 * processWindowAssignments
	 * 
	 * @param index
	 * @param symbols
	 * @param location
	 */
	private Collection<PropertyElement> processWindowAssignments(Index index, JSScope symbols, URI location)
	{
		Collection<PropertyElement> result = Collections.emptyList();

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
