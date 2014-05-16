/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.hyperlink;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.util.EditorUtil;
import com.aptana.editor.js.IDebugScopes;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.contentassist.JSLocationIdentifier;
import com.aptana.editor.js.contentassist.LocationType;
import com.aptana.editor.js.contentassist.ParseUtil;
import com.aptana.editor.js.internal.JSModelUtil;
import com.aptana.index.core.Index;
import com.aptana.js.core.index.JSIndexQueryHelper;
import com.aptana.js.core.inferencing.CommonJSResolver;
import com.aptana.js.core.inferencing.JSPropertyCollection;
import com.aptana.js.core.inferencing.JSScope;
import com.aptana.js.core.inferencing.RequireResolverFactory;
import com.aptana.js.core.model.PropertyElement;
import com.aptana.js.core.parsing.ast.IJSNodeTypes;
import com.aptana.js.core.parsing.ast.JSArgumentsNode;
import com.aptana.js.core.parsing.ast.JSFunctionNode;
import com.aptana.js.core.parsing.ast.JSGetPropertyNode;
import com.aptana.js.core.parsing.ast.JSIdentifierNode;
import com.aptana.js.core.parsing.ast.JSInvokeNode;
import com.aptana.js.core.parsing.ast.JSNode;
import com.aptana.js.core.parsing.ast.JSParseRootNode;
import com.aptana.js.core.parsing.ast.JSTreeWalker;
import com.aptana.parsing.ast.IParseNode;

/**
 * This class walks a JS AST, potentially creating hyperlinks if the specified offset is within an identifier and if the
 * identifier meets certain conditions. This class recognizes local variable declarations, local and global (within the
 * same editor) assignments, and function parameters, taking function nesting into account. For symbols that are not in
 * the current file, this class will attempt to locate, via the editor's index, all definitions of the symbol in the
 * editor's project.
 */
public class JSHyperlinkCollector extends JSTreeWalker
{
	/**
	 * A reference to the editor where hyperlinks may appear
	 */
	private AbstractThemeableEditor editor;

	/**
	 * The root of the parsed JS file in "editor"
	 */
	private JSParseRootNode ast;

	/**
	 * The offset to use when testing for hyperlinks
	 */
	private int offset;

	/**
	 * A collection of hyperlinks recognized by this instance
	 */
	private Set<IJSHyperlink> hyperlinks = new TreeSet<IJSHyperlink>();

	private JSIndexQueryHelper indexHelper;

	/**
	 * JSHyperlinkCollector
	 * 
	 * @param editor
	 *            The editor where hyperlinks may occur
	 * @param ast
	 *            The JS AST from the editor
	 * @param offset
	 *            The offset within the editor where hyperlinks may be created
	 */
	public JSHyperlinkCollector(AbstractThemeableEditor editor, JSParseRootNode ast, int offset)
	{
		this.editor = editor;
		this.ast = ast;
		this.offset = offset;

		// NOTE: using a TreeSet so we can define a custom comparator. We don't want to add duplicate hyperlinks where
		// we define a duplicate as having the same target file path since all hyperlinks should link to the same raw
		// text, essentially
		hyperlinks = new TreeSet<IJSHyperlink>(new Comparator<IJSHyperlink>()
		{
			public int compare(IJSHyperlink o1, IJSHyperlink o2)
			{
				return o1.getTargetFilePath().compareTo(o2.getTargetFilePath());
			}
		});
	}

	/**
	 * Add the specified hyperlink to the collection of links recognized by this instance
	 * 
	 * @param link
	 *            The link to add to the hyperlink collection. Null values are ignored
	 */
	protected void addHyperlink(IJSHyperlink link)
	{
		if (link != null)
		{
			hyperlinks.add(link);
		}
	}

	/**
	 * Create a hyperlink based on the source and target nodes. It is assumed that both nodes come from the current
	 * editor associated with this instance
	 * 
	 * @param linkNode
	 *            The node where to show a hyperlink
	 * @param targetNode
	 *            The node where to jump when the hyperlink is selected
	 * @param linkType
	 *            The link type categorization
	 */
	protected void addHyperlink(JSIdentifierNode linkNode, JSNode targetNode, String linkType)
	{
		// do not jump to self
		if (linkNode != targetNode)
		{
			IRegion hyperlinkRegion = getNodeRegion(linkNode);
			URI projectURI = EditorUtil.getProjectURI(editor);
			String targetFilePath = EditorUtil.getURI(editor).toString();
			String hyperlinkText = JSHyperlinkUtil.getDocumentDisplayName(projectURI, targetFilePath);
			IRegion targetRegion = getNodeRegion(targetNode);

			addHyperlink(new JSTargetRegionHyperlink(hyperlinkRegion, linkType, hyperlinkText, targetFilePath,
					targetRegion));
		}
	}

	/**
	 * Return a collection of links collected by this instance
	 * 
	 * @return Returns a potentially empty list. This value is never null.
	 */
	public Collection<IJSHyperlink> getHyperlinks()
	{
		return hyperlinks;
	}

	/**
	 * Determine the syntactic role of the specified identifier
	 * 
	 * @param node
	 *            A JS identifier node
	 * @return Returns a string value classifier. This value may be the empty string but it is never null.
	 */
	protected String getLinkType(JSIdentifierNode node)
	{
		// assume no hint
		String result = StringUtil.EMPTY;

		// process parent
		IParseNode parent = node.getParent();

		if (parent instanceof JSNode)
		{
			switch (parent.getNodeType())
			{
				case IJSNodeTypes.GET_PROPERTY:
					if (parent.getFirstChild().getNodeType() == IJSNodeTypes.INVOKE)
					{
						result = JSAbstractHyperlink.INVOCATION_TYPE;
					}
					break;

				case IJSNodeTypes.INVOKE:
					result = JSAbstractHyperlink.INVOCATION_TYPE;
					break;

				default:
					result = JSAbstractHyperlink.VARIABLE_TYPE;
			}
		}

		return result;
	}

	/**
	 * Convert the specified JS node into an IRegion
	 * 
	 * @param node
	 *            The JS node to convert
	 * @return Returns an IRegion containing the node and it's trailing semicolon, if it exists
	 */
	protected IRegion getNodeRegion(JSNode node)
	{
		int start = node.getStart();
		int length = node.getLength();

		if (node.getSemicolonIncluded())
		{
			--length;
		}

		return new Region(start, length);
	}

	/**
	 * Process all symbols that are in scope for the offset being processed within the editor. This method creates links
	 * for symbols that refer to function parameters, local variable declarations, and local assignments, taking
	 * function nesting into account. Note that all of these link types imply links within the editor only.
	 * 
	 * @param node
	 *            The JS identifier that potentially refers to a parameter or local/global value.
	 */
	protected void processEditorSymbols(JSIdentifierNode node)
	{
		// If the identifier is "require" and the parent is a JSInvokeNode, can we resolve the path and open the
		// file?
		if (node.getParent() instanceof JSInvokeNode && node.getNameNode().getName().equals("require"))
		{
			JSInvokeNode invoke = (JSInvokeNode) node.getParent();
			JSArgumentsNode args = (JSArgumentsNode) invoke.getArguments();
			String moduleId = getPath(args);

			IProject project = EditorUtil.getProject(editor);
			URI editorURI = EditorUtil.getURI(editor);
			IPath currentDirectory = Path.fromPortableString(editorURI.getPath()).removeLastSegments(1);

			IPath requiredFile = RequireResolverFactory.resolve(moduleId, project, currentDirectory,
					project.getLocation());
			if (requiredFile != null && requiredFile.toFile().exists())
			{
				IRegion hyperlinkRegion = getNodeRegion(node);
				String targetFilePath = requiredFile.toFile().toURI().toASCIIString();
				String hyperlinkText = JSHyperlinkUtil.getDocumentDisplayName(project.getLocationURI(), targetFilePath);

				addHyperlink(new JSTargetRegionHyperlink(hyperlinkRegion, JSAbstractHyperlink.INVOCATION_TYPE,
						hyperlinkText, targetFilePath, new Region(0, 0)));
			}
			return;
		}

		JSScope globalScope = ast.getGlobals();
		JSScope activeScope = globalScope.getScopeAtOffset(offset);
		JSPropertyCollection properties = activeScope.getSymbol(node.getText());

		if (properties != null)
		{
			for (JSNode value : properties.getValues())
			{
				IParseNode parent = value.getParent();

				switch (parent.getNodeType())
				{
					case IJSNodeTypes.PARAMETERS:
					{
						addHyperlink(node, value, JSAbstractHyperlink.PARAMETER_TYPE);
						break;
					}

					case IJSNodeTypes.DECLARATION:
					{
						// prevent jumping to LHS when trying to generate a link from RHS with same name as identifier
						// for example: var is = ABC.DEF.i|s
						if (!parent.contains(node.getStartingOffset()))
						{
							JSNode targetIdentifier = (JSNode) value.getParent().getFirstChild();

							addHyperlink(node, targetIdentifier, JSAbstractHyperlink.LOCAL_DECLARTION_TYPE);
						}
						break;
					}

					default:
						if (value.getNodeType() == IJSNodeTypes.ASSIGN)
						{
							JSNode targetIdentifier = (JSNode) value.getFirstChild();

							addHyperlink(node, targetIdentifier, JSAbstractHyperlink.LOCAL_ASSIGNMENT_TYPE);
						}
						else if (value.getNodeType() == IJSNodeTypes.FUNCTION)
						{
							JSFunctionNode functionNode = (JSFunctionNode) value;
							IParseNode name = functionNode.getName();

							if (name instanceof JSNode)
							{
								addHyperlink(node, (JSNode) name, StringUtil.EMPTY);
							}
						}
				}
			}
		}
	}

	/**
	 * Take the arguments to a require invocation and determine the string it's building up.
	 * 
	 * @param args
	 * @return
	 */
	private String getPath(JSArgumentsNode args)
	{
		if (args.getChildCount() > 0)
		{
			IParseNode child = args.getChild(0);
			return CommonJSResolver.getModuleId(child);
		}
		return null;
	}

	/**
	 * Process an identifier as if it links to a symbol outside of the current editor.
	 * 
	 * @param node
	 *            The JS identifier that potentially refers to a project global symbol definition
	 */
	protected void processProjectGlobals(JSIdentifierNode node)
	{
		IParseNode parent = node.getParent();
		boolean valid = false;

		if (parent instanceof JSParseRootNode)
		{
			// top-level identifier
			valid = true;
		}
		else if (parent instanceof JSNode)
		{
			switch (parent.getNodeType())
			{
				case IJSNodeTypes.ARGUMENTS:
				case IJSNodeTypes.CONSTRUCT:
				case IJSNodeTypes.INVOKE:
				case IJSNodeTypes.RETURN:
				case IJSNodeTypes.STATEMENTS:
					valid = true;
					break;

				case IJSNodeTypes.GET_PROPERTY:
					IParseNode grandparent = (parent != null) ? parent.getParent() : null;

					// Any property that is on the "left" of the dot is a potential link. However, we have more criteria
					// for the last property (see else-clause below)
					if (node.getIndex() == 0
							|| (parent != null && grandparent != null && grandparent.getNodeType() == IJSNodeTypes.GET_PROPERTY))
					{
						valid = true;
					}
					else
					{
						// walk up tree until we find the first node that is not part of a series of get-properties
						while (parent != null && parent.getNodeType() == IJSNodeTypes.GET_PROPERTY)
						{
							parent = parent.getParent();
						}

						// don't create links on the last property of symbols on the LHS of assignments
						if (parent == null || parent.getNodeType() != IJSNodeTypes.ASSIGN)
						{
							valid = true;
						}
					}
					break;
			}
		}

		if (valid)
		{
			// determine location type at the offset
			JSLocationIdentifier identifier = new JSLocationIdentifier(offset, node);
			ast.accept(identifier);
			LocationType type = identifier.getType();

			switch (type)
			{
				case IN_PROPERTY_NAME:
				{
					JSGetPropertyNode propertyNode = ParseUtil.getGetPropertyNode(identifier.getTargetNode(),
							identifier.getStatementNode());
					processProperty(node, propertyNode);
					break;
				}

				case IN_VARIABLE_NAME:
				{
					processVariable(node);
					break;
				}

				default:
					break;
			}
		}
	}

	/**
	 * Create links to external symbols referred by an identifier that is a property.
	 * 
	 * @param node
	 *            The JS identifier that referencing an external (outside the editor) symbol
	 * @param propertyNode
	 *            The top of the property expression
	 */
	protected void processProperty(JSIdentifierNode node, JSGetPropertyNode propertyNode)
	{
		List<PropertyElement> elements = new ArrayList<PropertyElement>();
		JSIndexQueryHelper queryHelper = createQueryHelper();
		Index index = EditorUtil.getIndex(editor);
		URI editorURI = EditorUtil.getURI(editor);
		List<String> types = ParseUtil.getReceiverTypeNames(queryHelper, index, editorURI, node, propertyNode, offset);
		if (!CollectionsUtil.isEmpty(types))
		{
			for (String typeName : types)
			{
				Collection<PropertyElement> members = queryHelper.getTypeMembers(typeName, node.getText());

				elements.addAll(members);
			}
		}

		processPropertyElements(elements, node);
	}

	protected synchronized JSIndexQueryHelper createQueryHelper()
	{
		if (indexHelper == null)
		{
			indexHelper = JSModelUtil.createQueryHelper(editor);
		}
		return indexHelper;
	}

	/**
	 * Create links, one for each property element.
	 * 
	 * @param elements
	 *            A non-null list of property elements
	 * @param node
	 *            The JS node that refers to each of the property elements
	 */
	protected void processPropertyElements(Collection<PropertyElement> elements, JSIdentifierNode node)
	{
		URI projectURI = EditorUtil.getProjectURI(editor);
		IRegion region = getNodeRegion(node);
		String linkType = getLinkType(node);
		boolean isLoggingEnabled = IdeLog.isTraceEnabled(JSPlugin.getDefault(), IDebugScopes.OPEN_DECLARATION_TYPES);

		for (PropertyElement element : elements)
		{
			// @formatter:off
			if (isLoggingEnabled)
			{
				IdeLog.logTrace(
					JSPlugin.getDefault(),
					"Hyperlink type model element: " + element.toSource(), //$NON-NLS-1$
					IDebugScopes.OPEN_DECLARATION_TYPES
				);
			}
			// @formatter:on

			List<String> documents = element.getDocuments();

			if (!CollectionsUtil.isEmpty(documents))
			{
				// @formatter:off
				if (isLoggingEnabled)
				{
					IdeLog.logTrace(
						JSPlugin.getDefault(),
						"Hyperlink type model documents: " + StringUtil.join(", ", documents), //$NON-NLS-1$ //$NON-NLS-2$
						IDebugScopes.OPEN_DECLARATION_TYPES
					);
				}
				// @formatter:on

				String elementName = element.getName();

				for (String document : documents)
				{
					// NOTE: projectURI is null during unit testing
					if (projectURI == null || JSHyperlinkUtil.isInCurrentProject(projectURI, document))
					{
						String text = JSHyperlinkUtil.getDocumentDisplayName(projectURI, document);

						addHyperlink(new JSSearchStringHyperlink(region, linkType, text, document, elementName));
					}
				}
			}
		}
	}

	/**
	 * Create links to external types referred by an identifier that is a variable.
	 * 
	 * @param node
	 */
	protected void processVariable(JSIdentifierNode node)
	{
		JSIndexQueryHelper queryHelper = createQueryHelper();
		Collection<PropertyElement> elements = queryHelper.getGlobals(EditorUtil.getFileName(editor), node.getText());

		processPropertyElements(elements, node);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSIdentifierNode)
	 */
	@Override
	public void visit(JSIdentifierNode node)
	{
		if (node.contains(offset))
		{
			processEditorSymbols(node);
			processProjectGlobals(node);
		}
	}

	public void visit(JSParseRootNode node)
	{
		if (node.contains(offset))
		{
			for (IParseNode child : node)
			{
				if (child.contains(offset))
				{
					if (child instanceof JSNode)
					{
						((JSNode) child).accept(this);
					}

					break;
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visitChildren(com.aptana.editor.js.parsing.ast.JSNode)
	 */
	@Override
	protected void visitChildren(JSNode node)
	{
		if (node.contains(offset))
		{
			for (IParseNode child : node)
			{
				if (child.contains(offset))
				{
					if (child instanceof JSNode)
					{
						((JSNode) child).accept(this);
					}

					break;
				}
			}
		}
	}
}
