/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.validator;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;

import com.aptana.core.IFilter;
import com.aptana.core.IMap;
import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.core.build.Problem;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.js.IJSConstants;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.JSIndexQueryHelper;
import com.aptana.editor.js.contentassist.model.EventElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.editor.js.inferencing.JSNodeTypeInferrer;
import com.aptana.editor.js.inferencing.JSScope;
import com.aptana.editor.js.parsing.ast.JSBinaryOperatorNode;
import com.aptana.editor.js.parsing.ast.JSConstructNode;
import com.aptana.editor.js.parsing.ast.JSGetElementNode;
import com.aptana.editor.js.parsing.ast.JSGetPropertyNode;
import com.aptana.editor.js.parsing.ast.JSIdentifierNode;
import com.aptana.editor.js.parsing.ast.JSInvokeNode;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSParseRootNode;
import com.aptana.editor.js.parsing.ast.JSStringNode;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.index.core.build.BuildContext;
import com.aptana.parsing.ast.IParseError;
import com.aptana.parsing.ast.IParseError.Severity;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.util.ParseUtil;

public class JSParserValidator extends AbstractBuildParticipant
{

	public static final String ID = "com.aptana.editor.js.validator.JSParserValidator"; //$NON-NLS-1$

	private static final String ADD_EVENT_LISTENER = "addEventListener"; //$NON-NLS-1$
	private static final String REMOVE_EVENT_LISTENER = "removeEventListener"; //$NON-NLS-1$

	/**
	 * Temporary problem collector used during {@link #buildFile(BuildContext, IProgressMonitor)}.
	 */
	private List<IProblem> fProblems;

	/**
	 * A re-used {@link IDocument} object wrapping the current {@link BuildContext}s source. Useful for asking for line
	 * numbers in a performant way. Temporary lifecycle, should get instantiated on-demand, and cleaned up at the end of
	 * {@link #buildFile(BuildContext, IProgressMonitor)}
	 */
	private Document fDocument;

	/**
	 * Re-use a common instance of the query helper so we don't end up instantiating and collecting it a number of
	 * times. cleaned up at the end of {@link #buildFile(BuildContext, IProgressMonitor)}
	 */
	private JSIndexQueryHelper fQueryHelper;

	private URI fLocation;
	private String fPath;
	private BuildContext fContext;
	private Index fIndex;

	public void deleteFile(BuildContext context, IProgressMonitor monitor)
	{
		if (context == null)
		{
			return;
		}

		context.removeProblems(IJSConstants.JS_PROBLEM_MARKER_TYPE);
	}

	public void buildFile(final BuildContext context, IProgressMonitor monitor)
	{
		if (context == null)
		{
			return;
		}

		try
		{
			context.getAST(); // Ensure a parse happened
		}
		catch (CoreException e)
		{
			// ignores the parser exception
		}

		// Set our temp fields up
		this.fContext = context;
		this.fLocation = context.getURI();
		this.fPath = fLocation.toString();
		this.fIndex = getIndex(context);
		this.fProblems = new ArrayList<IProblem>();

		try
		{
			// Add parse errors...
			Collection<IParseError> parseErrors = context.getParseErrors();
			if (!CollectionsUtil.isEmpty(parseErrors))
			{
				fProblems.addAll(CollectionsUtil.map(parseErrors, new IMap<IParseError, IProblem>()
				{

					public IProblem map(IParseError parseError)
					{
						int severity = (parseError.getSeverity() == Severity.ERROR) ? IMarker.SEVERITY_ERROR
								: IMarker.SEVERITY_WARNING;
						return new Problem(severity, parseError.getMessage(), parseError.getOffset(), parseError
								.getLength(), getLine(parseError.getOffset()), fPath);
					}
				}));
			}

			// Check for deprecated code!
			if (fIndex != null)
			{
				JSParseRootNode rootNode = (JSParseRootNode) context.getAST();
				if (rootNode != null)
				{
					final JSScope globalScope = rootNode.getGlobals();
					this.fQueryHelper = new JSIndexQueryHelper();
					ParseUtil.treeApply(rootNode, new IFilter<IParseNode>()
					{
						public boolean include(IParseNode node)
						{
							// instantiating a type
							if (node instanceof JSConstructNode)
							{
								JSConstructNode constructNode = (JSConstructNode) node;
								checkTypeForDeprecation(constructNode.getExpression(), globalScope);
							}
							// function call
							else if (node instanceof JSInvokeNode)
							{
								JSInvokeNode invokeNode = (JSInvokeNode) node;
								IParseNode expr = invokeNode.getExpression();
								if (expr instanceof JSIdentifierNode)
								{
									JSIdentifierNode ident = (JSIdentifierNode) expr;

									checkFunctionOrProperty(JSTypeConstants.WINDOW_TYPE, ident, true);
								}
								// Calls accessing functions by dot or ['name'] are handled below
							}
							// property access via dot notation. // Property access via ['property'] notation.
							else if ((node instanceof JSGetPropertyNode) || (node instanceof JSGetElementNode))
							{
								JSBinaryOperatorNode getPropertyNode = (JSBinaryOperatorNode) node;
								checkForDeprecations(getPropertyNode, globalScope,
										(getPropertyNode.getParent() instanceof JSInvokeNode));
							}
							return true;
						}
					});
				}
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(JSPlugin.getDefault(),
					MessageFormat.format("Failed to parse {0} for JS Parser Validation", fPath), e); //$NON-NLS-1$
		}

		context.putProblems(IJSConstants.JS_PROBLEM_MARKER_TYPE, fProblems);

		// Clean up the temporary fields
		this.fDocument = null;
		this.fQueryHelper = null;
		this.fPath = null;
		this.fLocation = null;
		this.fContext = null;
		this.fIndex = null;
	}

	/**
	 * Checks a property or function invocation for deprecation. First checks owning type/receiver, then checks the
	 * actually property/function.
	 * 
	 * @param getPropertyNode
	 * @param globalScope
	 * @param isFunction
	 */
	protected void checkForDeprecations(JSBinaryOperatorNode getPropertyNode, JSScope globalScope, boolean isFunction)
	{
		// Check for deprecated type of receiver
		String typeName = checkTypeForDeprecation(getPropertyNode.getLeftHandSide(), globalScope);
		if (typeName == null)
		{
			// receiver was marked as deprecated, move on
			return;
		}

		checkFunctionOrProperty(typeName, getPropertyNode.getRightHandSide(), isFunction);
	}

	protected void checkFunctionOrProperty(String typeName, IParseNode propOrFunctionNode, boolean isFunction)
	{
		String propertyName = getPropertyName(propOrFunctionNode);

		// Try to look up the property on the type in the index.
		List<? extends PropertyElement> props;
		if (isFunction)
		{
			// Look for deprecated event reference!
			if (ADD_EVENT_LISTENER.equals(propertyName) || REMOVE_EVENT_LISTENER.equals(propertyName))
			{
				checkForDeprecatedEvent(typeName, propOrFunctionNode);
			}
			props = fQueryHelper.getFunctions(fIndex, typeName, propertyName);
		}
		else
		{
			props = fQueryHelper.getProperties(fIndex, typeName, propertyName);
		}
		if (props.size() == 1)
		{
			PropertyElement theProp = props.get(0);
			if (theProp.isDeprecated())
			{
				// Mark a warning!
				fProblems.add(createWarning(MessageFormat.format(Messages.JSParserValidator_GenericDeprecated,
						isFunction ? Messages.JSParserValidator_Function : Messages.JSParserValidator_Property,
						theProp.getName()), getLine(propOrFunctionNode.getStartingOffset()), propOrFunctionNode
						.getStartingOffset(), propOrFunctionNode.getLength(), fPath));
			}
		}
	}

	protected void checkForDeprecatedEvent(String typeName, IParseNode right)
	{
		// TODO Also check for deprecated event properties by tracing the second arg callback function and looking at
		// what params it accesses off it's argument.

		// get parent of "right" until we get a JSInvokeNode
		IParseNode invokeNode = right;
		while (!(invokeNode instanceof JSInvokeNode) && invokeNode != null)
		{
			invokeNode = invokeNode.getParent();
		}
		if (invokeNode != null)
		{
			JSInvokeNode invoke = (JSInvokeNode) invokeNode;
			IParseNode args = invoke.getArguments();
			if (args.hasChildren())
			{
				IParseNode firstArg = args.getChild(0);
				if (firstArg instanceof JSStringNode)
				{
					JSStringNode firstArgString = (JSStringNode) firstArg;
					String value = StringUtil.stripQuotes(firstArgString.getText());
					List<EventElement> events = fQueryHelper.getEvents(fIndex, typeName, value);
					if (events.size() == 1)
					{
						EventElement theProp = events.get(0);
						if (theProp.isDeprecated())
						{
							// Mark a warning!
							fProblems
									.add(createWarning(
											MessageFormat.format(Messages.JSParserValidator_EventDeprecated,
													theProp.getName()), getLine(firstArg.getStartingOffset()),
											firstArg.getStartingOffset(), firstArg.getLength(), fPath));
						}
					}
				}
			}
		}
	}

	/**
	 * Tries to pull the name of the property/function from the node. For identifiers we use the name. for strings we
	 * grab the value with the quotes stripped off.
	 * 
	 * @param node
	 * @return
	 */
	protected String getPropertyName(IParseNode node)
	{
		if (node instanceof JSStringNode)
		{
			JSStringNode string = (JSStringNode) node;
			return StringUtil.stripQuotes(string.getText());
		}
		if (node instanceof JSIdentifierNode)
		{
			JSIdentifierNode ident = (JSIdentifierNode) node;
			return ident.getNameNode().getName();
		}
		return node.toString();
	}

	/**
	 * Determine the line number for the offset.
	 * 
	 * @param offset
	 * @return
	 */
	private int getLine(int offset)
	{
		try
		{
			return getDocument(fContext).getLineOfOffset(offset) + 1;
		}
		catch (BadLocationException e)
		{
			// ignore
		}
		return -1;
	}

	/**
	 * Lazily instantiate an {@link IDocument} to wrap the source for querying line numbers. See
	 * {@link #getLine(BuildContext, int)}
	 * 
	 * @param context
	 * @return
	 */
	private IDocument getDocument(BuildContext context)
	{
		if (this.fDocument == null)
		{
			String source = context.getContents();
			this.fDocument = new Document(source);
		}
		return this.fDocument;
	}

	protected Index getIndex(BuildContext context)
	{
		IProject project = context.getProject();
		if (project == null)
		{
			return null;
		}
		URI projectURI = project.getLocationURI();
		if (projectURI == null)
		{
			return null;
		}
		return getIndexManager().getIndex(projectURI);
	}

	protected IndexManager getIndexManager()
	{
		return IndexPlugin.getDefault().getIndexManager();
	}

	/**
	 * Attempts to determine the type of the {@code node} argument. Typically this is the owner of a property or
	 * receiver for a method call.
	 * 
	 * @param node
	 * @param globalScope
	 * @param fIndex
	 * @param fLocation
	 * @return
	 */
	protected String getTypeName(IParseNode node, JSScope globalScope)
	{
		JSScope scope = globalScope.getScopeAtOffset(node.getStartingOffset());
		JSNodeTypeInferrer infer = new JSNodeTypeInferrer(scope, fIndex, fLocation, fQueryHelper);
		infer.visit((JSNode) node);
		List<String> typeNames = infer.getTypes();
		if (!CollectionsUtil.isEmpty(typeNames))
		{
			return typeNames.get(0);
		}

		return node.toString();
	}

	/**
	 * A hacky method. We determine the type of the {@code node} argument. If the referenced type is deprecated, we mark
	 * a warning and return null. If the type is OK, we return it's name.
	 * 
	 * @param node
	 * @param globalScope
	 * @return
	 */
	protected String checkTypeForDeprecation(IParseNode node, JSScope globalScope)
	{
		String typeName = getTypeName(node, globalScope);
		List<TypeElement> types = fQueryHelper.getTypes(fIndex, typeName, false);
		if (types.size() == 1) // FIXME If not empty, mark if all types are deprecated.
		{
			TypeElement theProp = types.get(0);
			if (theProp.isDeprecated())
			{
				// Mark a warning!
				fProblems.add(createWarning(MessageFormat.format(Messages.JSParserValidator_TypeDeprecated, typeName),
						getLine(node.getStartingOffset()), node.getStartingOffset(), node.getLength(), fPath));
				return null;
			}
		}
		return typeName;
	}
}
