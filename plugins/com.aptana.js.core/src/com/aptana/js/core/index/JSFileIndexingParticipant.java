/**
 * Aptana Studio
 * Copyright (c) 2005-2014 by Appcelerator, Inc. All Rights Reserved.
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
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
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
import com.aptana.js.core.parsing.ast.JSParseRootNode;
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

	private JSIndexWriter indexWriter;

	private JSIndexQueryHelper queryHelper;

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
		if (root instanceof JSParseRootNode)
		{
			return ((JSParseRootNode) root).getGlobals();
		}

		return null;
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
	private List<PropertyElement> processLambdas(Index index, JSScope globals, IParseNode node, URI location,
			IProgressMonitor monitor)
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
					SubMonitor sub = SubMonitor.convert(monitor, functions.size() * 11);
					result = new ArrayList<PropertyElement>(functions.size());

					for (JSFunctionNode function : functions)
					{
						// grab the correct scope for this function's body
						JSScope scope = globals.getScopeAtOffset(function.getBody().getStartingOffset());
						sub.worked(1);

						JSSymbolTypeInferrer infer = new JSSymbolTypeInferrer(scope, index, location, queryHelper);
						// add all properties off of "window" to our list
						result.addAll(processWindowAssignments(scope, infer, sub.newChild(5)));

						// handle any nested lambdas in this function
						result.addAll(processLambdas(index, globals, function, location, sub.newChild(5)));
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
		if (monitor.isCanceled())
		{
			return;
		}
		SubMonitor sub = SubMonitor.convert(monitor, 100);

		queryHelper = new JSIndexQueryHelper(context.getProject());
		URI location = context.getURI();

		boolean traceEnabled = IdeLog.isTraceEnabled(JSCorePlugin.getDefault(), IDebugScopes.INDEXING_STEPS);
		if (traceEnabled)
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
		try
		{
			JSSymbolTypeInferrer symbolInferrer = new JSSymbolTypeInferrer(globals, index, location, queryHelper);
			// process globals
			if (globals != null)
			{
				// TODO Should we have a big if/else that switches between this style of indexing and module indexing
				// based on if there's a module or exports property?

				// create new Global type for this file
				TypeElement globalType = JSTypeUtil.createGlobalType(JSTypeConstants.GLOBAL_TYPE);

				if (sub.isCanceled())
				{
					throw new OperationCanceledException();
				}

				// add declared variables and functions from the global scope
				if (traceEnabled)
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

				for (PropertyElement property : symbolInferrer.getScopeProperties(sub.newChild(25)))
				{
					globalType.addProperty(property);
				}

				if (sub.isCanceled())
				{
					throw new OperationCanceledException();
				}

				// include any assignments to Window
				if (traceEnabled)
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

				for (PropertyElement property : processWindowAssignments(globals, symbolInferrer, sub.newChild(25)))
				{
					globalType.addProperty(property);
				}

				if (sub.isCanceled())
				{
					throw new OperationCanceledException();
				}
				// process window assignments in lambdas (self-invoking functions)
				if (traceEnabled)
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

				for (PropertyElement property : processLambdas(index, globals, ast, location, sub.newChild(25)))
				{
					globalType.addProperty(property);
				}

				// associate all user agents with these properties
				if (traceEnabled)
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
				for (PropertyElement property : globalType.getProperties())
				{
					property.setHasAllUserAgents();
				}

				// write new Window type to index
				if (traceEnabled)
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

				indexWriter.writeType(index, globalType, location);
				sub.worked(5);
			}

			sub.setWorkRemaining(20);
			// process module API exports
			processModule(context, index, ast, location, globals, symbolInferrer, sub.newChild(20));
			processModule(context, index, globals, ast, location, sub.newChild(20));
		}
		catch (OperationCanceledException oce)
		{
			IdeLog.logWarning(JSCorePlugin.getDefault(),
					MessageFormat.format("User cancelled indexing operation on {0}", context.getURI()));
		}
		finally
		{
			sub.done();
		}
	}

	/**
	 * @param index
	 * @param ast
	 * @param location
	 * @param globals
	 * @param symbolInferrer
	 * @param monitor
	 */
	protected void processModule(BuildContext context, Index index, IParseNode ast, URI location, JSScope globals,
			JSSymbolTypeInferrer infer, IProgressMonitor monitor)
	{
		if (globals == null)
		{
			return;
		}

		SubMonitor sub = SubMonitor.convert(monitor, 100);

		// Autogenerate some unique type name to hold the module's exports
		// Then record the mapping between the filepath and the generated type's name
		// FIXME Can we use better names here? The full  location is too long, and the uuid is ugly and long.
		// Can we determine the true module id and use that? the relative path to the index root and use that?
		String moduleTypeName = location.toString(); // JSTypeUtil.getUniqueTypeName(location.toString());

		// Create a type for this module...
		TypeElement moduleType = new TypeElement();
		moduleType.setHasAllUserAgents();
		moduleType.setName(moduleTypeName);

		// Create a type for the "module instance", which is "module.exports"
		TypeElement moduleExportsType = new TypeElement();
		moduleExportsType.setHasAllUserAgents();
		moduleExportsType.setName(moduleType.getName() + ".exports"); //$NON-NLS-1$

		// Grab the defined "module" variable from the file
		JSPropertyCollection module = globals.getSymbol("module"); //$NON-NLS-1$
		if (module != null)
		{
			// Now grab "module.exports" and attach that property to our hand-generated module type from above
			PropertyElement exports = infer.getSymbolPropertyElement(module, "exports", sub.newChild(90)); //$NON-NLS-1$
			moduleType.addProperty(exports);

			// Now copy over the type info from the module.exports property to the hand-generated module instance
			for (String type : exports.getTypeNames())
			{
				// TODO Where should we strip out the type info on the elements in the collection? This doesn't seem
				// like the right place, maybe we should do it in CA processor, or index query helper's get ancestor
				// names?
				if (type.startsWith(JSTypeConstants.GENERIC_ARRAY_OPEN))
				{
					type = JSTypeConstants.ARRAY_TYPE;
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
			// Grab all properties hanging off "exports" and attach them to our hand-generated "module.exports" module
			// instance type.
			List<String> properties = exports.getPropertyNames();
			if (!CollectionsUtil.isEmpty(properties))
			{
				int work = 90 / properties.size();
				for (String property : properties)
				{
					PropertyElement propElement = infer.getSymbolPropertyElement(exports, property, sub.newChild(work));
					moduleExportsType.addProperty(propElement);
				}
			}
		}

		sub.setWorkRemaining(10);

		// Now we also add special properties that modules define
		// Add an id property to the module instance (module.exports)!
		PropertyElement idElement = new PropertyElement();
		idElement.setIsInstanceProperty(true);
		idElement.setHasAllUserAgents();
		idElement.setName("id"); //$NON-NLS-1$
		idElement.addType(JSTypeConstants.STRING_TYPE);
		String moduleId = getModuleId(context, index, ast, location);
		idElement.setDescription(moduleId); // Just pass in the actual value?
		moduleExportsType.addProperty(idElement);
		// Add a uri property to the module instance (module.exports)!
		PropertyElement uriElement = new PropertyElement();
		uriElement.setIsInstanceProperty(true);
		uriElement.setHasAllUserAgents();
		uriElement.setName("uri"); //$NON-NLS-1$
		uriElement.addType(JSTypeConstants.STRING_TYPE);
		uriElement.setDescription(location.toString()); // Just pass in the actual value?
		moduleExportsType.addProperty(uriElement);

		sub.worked(2);

		// Now write our hand-generated module type and module instance type.
		indexWriter.writeType(index, moduleExportsType, location);
		indexWriter.writeType(index, moduleType, location);

		sub.worked(5);

		// Record a mapping for the auto-generated type name we're recording (so we can look it up by the filepath)
		index.addEntry(IJSIndexConstants.MODULE_DEFINITION, moduleTypeName, location);
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
		// FIXME Do a look up based on what NodeModuleResolver does to determine the relative path...
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
	private Collection<PropertyElement> processWindowAssignments(JSScope symbols, JSSymbolTypeInferrer symbolInferrer,
			IProgressMonitor monitor)
	{
		if (symbols == null || !symbols.hasLocalSymbol(JSTypeConstants.WINDOW_PROPERTY))
		{
			return Collections.emptyList();
		}

		PropertyElement property = symbolInferrer.getSymbolPropertyElement(JSTypeConstants.WINDOW_PROPERTY, monitor);
		if (property == null)
		{
			return Collections.emptyList();
		}

		List<String> typeNames = property.getTypeNames();
		if (!CollectionsUtil.isEmpty(typeNames))
		{
			return queryHelper.getTypeMembers(typeNames);
		}

		return Collections.emptyList();
	}
}
