/**
 * Aptana Studio
 * Copyright (c) 2005-2014 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;

import beaver.Scanner;

import com.aptana.buildpath.core.BuildPathManager;
import com.aptana.buildpath.core.IBuildPathEntry;
import com.aptana.core.IFilter;
import com.aptana.core.util.AndFilter;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.core.util.VersionUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonContentAssistProcessor;
import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.editor.common.contentassist.ILexemeProvider;
import com.aptana.editor.common.contentassist.UserAgentManager;
import com.aptana.editor.common.util.EditorUtil;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.JSSourceConfiguration;
import com.aptana.editor.js.internal.JSModelUtil;
import com.aptana.editor.js.text.JSFlexLexemeProvider;
import com.aptana.index.core.Index;
import com.aptana.js.core.IJSConstants;
import com.aptana.js.core.JSLanguageConstants;
import com.aptana.js.core.index.IJSIndexConstants;
import com.aptana.js.core.index.JSIndexQueryHelper;
import com.aptana.js.core.inferencing.JSNodeTypeInferrer;
import com.aptana.js.core.inferencing.JSPropertyCollection;
import com.aptana.js.core.inferencing.JSScope;
import com.aptana.js.core.inferencing.JSTypeUtil;
import com.aptana.js.core.inferencing.RequireResolverFactory;
import com.aptana.js.core.model.FunctionElement;
import com.aptana.js.core.model.ParameterElement;
import com.aptana.js.core.model.PropertyElement;
import com.aptana.js.core.model.TypeElement;
import com.aptana.js.core.parsing.JSFlexScanner;
import com.aptana.js.core.parsing.JSParseState;
import com.aptana.js.core.parsing.JSTokenType;
import com.aptana.js.core.parsing.ThisAssignmentCollector;
import com.aptana.js.core.parsing.ast.IJSNodeTypes;
import com.aptana.js.core.parsing.ast.JSArgumentsNode;
import com.aptana.js.core.parsing.ast.JSAssignmentNode;
import com.aptana.js.core.parsing.ast.JSConstructNode;
import com.aptana.js.core.parsing.ast.JSFunctionNode;
import com.aptana.js.core.parsing.ast.JSGetPropertyNode;
import com.aptana.js.core.parsing.ast.JSIdentifierNode;
import com.aptana.js.core.parsing.ast.JSInvokeNode;
import com.aptana.js.core.parsing.ast.JSNode;
import com.aptana.js.core.parsing.ast.JSObjectNode;
import com.aptana.js.core.parsing.ast.JSParseRootNode;
import com.aptana.js.core.parsing.ast.JSPrimitiveNode;
import com.aptana.js.core.parsing.ast.JSThisNode;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.INameNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Lexeme;
import com.aptana.parsing.lexer.Range;

public class JSContentAssistProcessor extends CommonContentAssistProcessor
{
	private static final String SDK_3_4_0 = "3.4.0.GA"; //$NON-NLS-1$
	private static final String API_JSCA = "api.jsca"; //$NON-NLS-1$

	/**
	 * This class is used via {@link CollectionsUtil#filter(Collection, IFilter)} to remove duplicate proposals based on
	 * display names. Duplicate proposals are merged into a single entry
	 */
	public class ProposalMerger implements IFilter<ICompletionProposal>
	{
		private ICompletionProposal lastProposal = null;

		public boolean include(ICompletionProposal item)
		{
			boolean result;

			if (lastProposal == null || !lastProposal.getDisplayString().equals(item.getDisplayString()))
			{
				result = true;
				lastProposal = item;
			}
			else
			{
				result = false;
				// TODO: merge proposal with last proposal
			}

			return result;
		}
	}

	private static final Image JS_FUNCTION = JSPlugin.getImage("/icons/js_function.png"); //$NON-NLS-1$
	private static final Image JS_PROPERTY = JSPlugin.getImage("/icons/js_property.png"); //$NON-NLS-1$
	private static final Image JS_KEYWORD = JSPlugin.getImage("/icons/keyword.png"); //$NON-NLS-1$
	private static final Image STRING_ICON = JSPlugin.getImage("icons/string.png"); //$NON-NLS-1$

	/**
	 * Filters out internal properties.
	 */
	private static final IFilter<PropertyElement> isVisibleFilter = new IFilter<PropertyElement>()
	{
		public boolean include(PropertyElement item)
		{
			return !item.isInternal();
		}
	};

	/**
	 * Retains instance properties.
	 */
	private static final IFilter<PropertyElement> isInstanceFilter = new IFilter<PropertyElement>()
	{
		public boolean include(PropertyElement item)
		{
			String typeName = item.getOwningType();
			if (typeName.equals("module.exports") || (typeName.startsWith("$module") && typeName.endsWith(".exports")))
			{
				if ("id".equals(item.getName()) || "uri".equals(item.getName()))
				{
					return true;
				}
				return item.isClassProperty();
			}
			return item.isInstanceProperty();
		}
	};

	/**
	 * Retains class properties.
	 */
	private static final IFilter<PropertyElement> isStaticFilter = new IFilter<PropertyElement>()
	{
		public boolean include(PropertyElement item)
		{
			String typeName = item.getOwningType();
			if (typeName.equals("module.exports") || (typeName.startsWith("$module") && typeName.endsWith(".exports")))
			{
				if ("id".equals(item.getName()) || "uri".equals(item.getName()))
				{
					return true;
				}
			}
			return item.isClassProperty();
		}
	};

	/**
	 * Filters out functions that are constructors.
	 */
	private static final IFilter<PropertyElement> isNotConstructorFilter = new IFilter<PropertyElement>()
	{
		public boolean include(PropertyElement item)
		{
			if (!(item instanceof FunctionElement))
			{
				return true;
			}
			return !((FunctionElement) item).isConstructor();
		}
	};
	private static Set<String> AUTO_ACTIVATION_PARTITION_TYPES = CollectionsUtil.newSet(JSSourceConfiguration.DEFAULT,
			IDocument.DEFAULT_CONTENT_TYPE);

	private JSIndexQueryHelper indexHelper;
	private IParseNode targetNode;
	private IParseNode statementNode;
	private IRange replaceRange;
	private IRange activeRange;
	private ITextViewer textViewer;

	/**
	 * JSIndexContentAssistProcessor
	 * 
	 * @param editor
	 */
	public JSContentAssistProcessor(AbstractThemeableEditor editor)
	{
		super(editor);
	}

	/**
	 * JSContentAssistProcessor
	 * 
	 * @param editor
	 * @param activeRange
	 */
	public JSContentAssistProcessor(AbstractThemeableEditor editor, IRange activeRange)
	{
		this(editor);

		this.activeRange = activeRange;
	}

	/**
	 * @param prefix
	 * @param completionProposals
	 */
	private void addKeywords(Set<ICompletionProposal> proposals, int offset)
	{
		String[] activeUserAgentIds = getActiveUserAgentIds();
		for (String name : JSLanguageConstants.KEYWORDS)
		{
			// TODO Create a KeywordProposal class that lazily generates description, etc?
			String description = MessageFormat.format(Messages.JSContentAssistProcessor_KeywordDescription, name);
			addProposal(proposals, name, JS_KEYWORD, description, activeUserAgentIds,
					Messages.JSContentAssistProcessor_KeywordLocation, offset);
		}
	}

	/**
	 * If we're invoked inside a function that takes an object literal, propose the properties for the function's
	 * parameter type. Useful for things like Ti.UI.create* functions.
	 * 
	 * @param proposals
	 * @param offset
	 */
	protected void addObjectLiteralProperties(Set<ICompletionProposal> proposals, ITextViewer viewer, int offset)
	{
		FunctionElement function = getFunctionElement(viewer, offset);
		if (function == null)
		{
			return;
		}
		List<ParameterElement> params = function.getParameters();
		int index = getArgumentIndex(offset);

		if (0 <= index && index < params.size())
		{
			ParameterElement param = params.get(index);
			URI projectURI = getProjectURI();

			for (String type : param.getTypes())
			{
				Collection<PropertyElement> properties = getQueryHelper().getTypeProperties(type);

				for (PropertyElement property : CollectionsUtil.filter(properties, isVisibleFilter))
				{
					addProposal(proposals, property, offset, projectURI, null);
				}
			}
		}
	}

	/**
	 * addProjectGlobalFunctions
	 * 
	 * @param proposals
	 * @param offset
	 */
	private void addGlobals(Set<ICompletionProposal> proposals, int offset)
	{
		Collection<PropertyElement> projectGlobals = getQueryHelper().getGlobals(getFilename());
		if (CollectionsUtil.isEmpty(projectGlobals))
		{
			return;
		}

		String[] userAgentIds = getActiveUserAgentIds();
		URI projectURI = getProjectURI();
		for (PropertyElement property : CollectionsUtil.filter(projectGlobals, isVisibleFilter))
		{
			// TODO Use Messages.JSContentAssistProcessor_KeywordLocation for core stuff!
			String location = null;
			List<String> documents = property.getDocuments();
			if (!CollectionsUtil.isEmpty(documents))
			{
				String docString = documents.get(0);
				int index = docString.lastIndexOf('/');
				if (index != -1)
				{
					location = docString.substring(index + 1);
				}
				else
				{
					location = docString;
				}
			}
			addProposal(proposals, property, offset, projectURI, location, userAgentIds);
		}
	}

	/**
	 * addProperties
	 * 
	 * @param proposals
	 * @param offset
	 */
	protected void addProperties(Set<ICompletionProposal> proposals, int offset)
	{
		JSGetPropertyNode node = ParseUtil.getGetPropertyNode(targetNode, statementNode);
		boolean isInstance = isInstance(node);
		List<String> types = getParentObjectTypes(node, offset);

		// add all properties of each type to our proposal list
		for (String type : types)
		{
			addTypeProperties(proposals, type, offset, isInstance);
		}
	}

	private boolean isInstance(JSGetPropertyNode node)
	{
		IParseNode left = node.getChild(0);
		// if we're invoking on "this", then we should show instance properties...
		if (left instanceof JSThisNode)
		{
			return true;
		}
		// if receiver is a "new something()" call, or a primitive we know it's typically an instance.
		if (left instanceof JSConstructNode || left instanceof JSPrimitiveNode)
		{
			// Identifiers are special case. They're just variable names. We need to determine if they refer to a type
			// or an instance
			if (left instanceof JSIdentifierNode)
			{
				// FIXME Track back to last assignment to determine better
				JSIdentifierNode ident = (JSIdentifierNode) left;
				String name = ident.getNameNode().getName();
				if ("$".equals(name) || "Ti".equals(name) || "jQuery".equals(name)) // HACK for jQuery
				{
					// FIXME Do a better handling of aliases like $ or Ti
					// String global = JSTypeUtil.getGlobalType(getProject(), getFilename());
					// List<PropertyElement> aliases = getQueryHelper().getProperties(global, name);
					return false;
				}
				Collection<TypeElement> types = getQueryHelper().getTypes(name, false);
				// FIXME If the type was defined using an object literal, the actual reference is an instance! How the
				// hell do I handle that?
				if (CollectionsUtil.isEmpty(types))
				{
					// if there are no types by this name, assume it's an instance
					return true;
				}
				// cheat and assume identifiers beginning with upper case letter are types?
				return !Character.isUpperCase(name.charAt(0));
			}
			return true;
		}
		if (left instanceof JSInvokeNode)
		{
			// FIXME what about here? We need to look up the return values to determine...
			return true;
		}
		return false;
	}

	/**
	 * addProposal
	 * 
	 * @param proposals
	 * @param property
	 * @param offset
	 * @param projectURI
	 * @param overriddenLocation
	 */
	private void addProposal(Set<ICompletionProposal> proposals, PropertyElement property, int offset, URI projectURI,
			String overriddenLocation)
	{
		List<String> userAgentNameList = property.getUserAgentNames();
		String[] userAgentNames = userAgentNameList.toArray(new String[userAgentNameList.size()]);

		addProposal(proposals, property, offset, projectURI, overriddenLocation, userAgentNames);
	}

	/**
	 * addProposal
	 * 
	 * @param proposals
	 * @param property
	 * @param offset
	 * @param projectURI
	 * @param overriddenLocation
	 * @param userAgentNames
	 */
	private void addProposal(Set<ICompletionProposal> proposals, PropertyElement property, int offset, URI projectURI,
			String overriddenLocation, String[] userAgentNames)
	{
		if (isActiveByUserAgent(userAgentNames))
		{
			// calculate what text will be replaced
			int replaceLength = 0;

			if (replaceRange != null)
			{
				offset = replaceRange.getStartingOffset(); // $codepro.audit.disable questionableAssignment
				replaceLength = replaceRange.getLength();
			}

			if (property.getOwningType().startsWith("$module")) //$NON-NLS-1$
			{
				IPath path = getQueryHelper().getModulePath(property.getOwningType());
				property.setOwningType(path.toOSString());
			}
			PropertyElementProposal proposal = new PropertyElementProposal(property, offset, replaceLength, projectURI);
			proposal.setTriggerCharacters(getProposalTriggerCharacters());
			if (!StringUtil.isEmpty(overriddenLocation))
			{
				proposal.setFileLocation(overriddenLocation);
			}

			Image[] userAgents = UserAgentManager.getInstance().getUserAgentImages(getProject(), userAgentNames);
			proposal.setUserAgentImages(userAgents);

			// add the proposal to the list
			proposals.add(proposal);
		}

	}

	/**
	 * addProposal - The display name is used as the insertion text
	 * 
	 * @param proposals
	 * @param displayName
	 * @param image
	 * @param description
	 * @param userAgents
	 * @param fileLocation
	 * @param offset
	 */
	private CommonCompletionProposal addProposal(Set<ICompletionProposal> proposals, String displayName, Image image,
			String description, String[] userAgentIds, String fileLocation, int offset)
	{
		if (isActiveByUserAgent(userAgentIds))
		{
			int length = displayName.length();

			// calculate what text will be replaced
			int replaceLength = 0;

			if (replaceRange != null)
			{
				offset = replaceRange.getStartingOffset(); // $codepro.audit.disable questionableAssignment
				replaceLength = replaceRange.getLength();
			}

			// build proposal
			IContextInformation contextInfo = null;
			Image[] userAgents = UserAgentManager.getInstance().getUserAgentImages(getProject(), userAgentIds);

			CommonCompletionProposal proposal = new CommonCompletionProposal(displayName, offset, replaceLength,
					length, image, displayName, contextInfo, description);
			proposal.setFileLocation(fileLocation);
			proposal.setUserAgentImages(userAgents);
			proposal.setTriggerCharacters(getProposalTriggerCharacters());

			// add the proposal to the list
			proposals.add(proposal);
			return proposal;
		}
		return null;
	}

	protected void addSymbolsInScope(Set<ICompletionProposal> proposals, int offset)
	{
		if (targetNode == null)
		{
			return;
		}

		JSScope globalScope = ParseUtil.getGlobalScope(targetNode);
		if (globalScope == null)
		{
			return;
		}
		JSScope localScope = globalScope.getScopeAtOffset(offset);
		String fileLocation = getFilename();
		String[] userAgentNames = getActiveUserAgentIds();

		while (localScope != null && localScope != globalScope)
		{
			List<String> symbols = localScope.getLocalSymbolNames();

			for (String symbol : symbols)
			{
				boolean isFunction = false;
				JSPropertyCollection object = localScope.getLocalSymbol(symbol);
				List<JSNode> nodes = object.getValues();

				if (nodes != null)
				{
					for (JSNode node : nodes)
					{
						if (node instanceof JSFunctionNode)
						{
							isFunction = true;
							break;
						}
					}
				}

				String name = symbol;
				String description = null;
				Image image = (isFunction) ? JS_FUNCTION : JS_PROPERTY;

				// TODO Add a JSPropertyCollectionProposal that takes the object and generates the rest?
				addProposal(proposals, name, image, description, userAgentNames, fileLocation, offset);
			}

			localScope = localScope.getParentScope();
		}
	}

	/**
	 * addThisProposals
	 * 
	 * @param proposals
	 * @param offset
	 */
	protected void addThisProperties(Set<ICompletionProposal> proposals, int offset)
	{
		// find containing function or JSParseRootNode
		IParseNode activeNode = getActiveASTNode(offset);

		while (!(activeNode instanceof JSFunctionNode))
		{
			activeNode = activeNode.getParent();
			if (activeNode instanceof JSParseRootNode)
			{
				// If we've gotten to the root, just bail out.
				return;
			}
		}
		JSFunctionNode currentFunctionNode = (JSFunctionNode) activeNode;

		String functionName = getFunctionName(currentFunctionNode);

		if (functionName != null)
		{
			functionName = StringUtil.dotFirst(functionName).trim();
			if (functionName.length() == 0)
			{
				// Empty name
				functionName = null;
			}
		}

		List<JSFunctionNode> functionsToAnalyze;
		if (functionName == null)
		{
			// Unable to get a name for the current function: don't try to find any other
			// JS prototypes.
			functionsToAnalyze = Arrays.asList(currentFunctionNode);
		}
		else
		{
			// We want to match the following:
			// myFunc function(){...}
			// myFunc = function(){...}
			// myFunc.prototype.foo = function(){...}
			IParseNode parent = currentFunctionNode.getParent();
			if (parent.getNodeType() == IJSNodeTypes.ASSIGN)
			{
				parent = parent.getParent();
			}
			IParseNode[] children = parent.getChildren();
			functionsToAnalyze = new LinkedList<JSFunctionNode>();
			for (int i = 0; i < children.length; i++)
			{
				String childName = null;
				IParseNode childNode = children[i];
				JSFunctionNode jsFunctionNode = null;
				if (childNode instanceof JSFunctionNode)
				{
					jsFunctionNode = (JSFunctionNode) childNode;
					childName = jsFunctionNode.getNameNode().getName();

				}
				else if (childNode.getNodeType() == IJSNodeTypes.ASSIGN)
				{
					JSAssignmentNode assignmentNode = (JSAssignmentNode) childNode;
					IParseNode rightHandSide = assignmentNode.getRightHandSide();
					if (rightHandSide instanceof JSFunctionNode)
					{
						jsFunctionNode = (JSFunctionNode) rightHandSide;
						childName = getAssignmentLeftNodeName(assignmentNode);
					}
				}

				if (childName != null && jsFunctionNode != null)
				{
					if (StringUtil.dotFirst(childName).equals(functionName))
					{
						functionsToAnalyze.add(jsFunctionNode);
					}
				}
			}
		}

		for (JSFunctionNode function : functionsToAnalyze)
		{
			// collect all this.property assignments
			ThisAssignmentCollector collector = new ThisAssignmentCollector();
			((JSNode) function.getBody()).accept(collector);
			List<JSAssignmentNode> assignments = collector.getAssignments();

			if (!CollectionsUtil.isEmpty(assignments))
			{
				JSScope globalScope = ParseUtil.getGlobalScope(targetNode);

				if (globalScope != null)
				{
					JSScope localScope = globalScope.getScopeAtOffset(offset);
					Index index = getIndex();
					URI location = EditorUtil.getURI(editor);
					String typeName = StringUtil.concat(getNestedFunctionTypeName(function)
							+ IJSIndexConstants.NESTED_TYPE_SEPARATOR + "this"); //$NON-NLS-1$

					// infer each property and add proposal
					for (JSAssignmentNode assignment : assignments)
					{
						IParseNode lhs = assignment.getLeftHandSide();
						IParseNode rhs = assignment.getRightHandSide();
						String name = lhs.getLastChild().getText();

						JSNodeTypeInferrer nodeInferrer = new JSNodeTypeInferrer(localScope, index, location,
								getQueryHelper());
						((JSNode) rhs).accept(nodeInferrer);
						List<String> types = nodeInferrer.getTypes();

						PropertyElement property = new PropertyElement();
						property.setName(name);
						property.setHasAllUserAgents();

						if (!CollectionsUtil.isEmpty(types))
						{
							for (String type : types)
							{
								property.addType(type);
							}
						}

						addProposal(proposals, property, offset, getProjectURI(), typeName);
					}
				}
			}
		}
	}

	/**
	 * Given a function node, discover its name either declared directly or through a parent assign to the function
	 * (i.e.: myFunc function(){} or myFunc = function(){...}). May return null if unable to get the name.
	 */
	private String getFunctionName(JSFunctionNode currentFunctionNode)
	{
		String functionName = null;
		// Discover the name context name of where we are (function or assign to function).
		INameNode nameNode = currentFunctionNode.getNameNode();
		if (nameNode.getName().length() == 0)
		{
			IParseNode functionParent = currentFunctionNode.getParent();
			if (functionParent.getNodeType() == IJSNodeTypes.ASSIGN)
			{
				functionName = getAssignmentLeftNodeName((JSAssignmentNode) functionParent);
			}
		}
		else
		{
			// Found as: myFunc function(){...}
			functionName = nameNode.getName();
		}
		return functionName;
	}

	//@formatter:off 
	/**
	 * @return the left-hand side name we can discover in an assign
	 * I.e.: something as: 
	 * 
	 * myFunc = function(){...}
	 * myFunc.prototype.foo = function(){...} 
	 * 
	 * Will return myFunc / myFunc.prototype.foo
	 */
	//@formatter:on
	private String getAssignmentLeftNodeName(JSAssignmentNode assignmentNode)
	{
		IParseNode leftHandSide = assignmentNode.getLeftHandSide();
		if (leftHandSide.getNodeType() == IJSNodeTypes.GET_PROPERTY)
		{
			return leftHandSide.toString();
		}
		return null;
	}

	/**
	 * addTypeProperties
	 * 
	 * @param proposals
	 * @param typeName
	 * @param offset
	 * @param isInstance
	 */
	@SuppressWarnings("unchecked")
	protected void addTypeProperties(Set<ICompletionProposal> proposals, String typeName, int offset, boolean isInstance)
	{
		// grab all ancestors of the specified type
		List<String> allTypes = getQueryHelper().getTypeAncestorNames(typeName);

		// include the type in the list as well
		allTypes.add(0, typeName);

		// add properties and methods
		Collection<PropertyElement> properties = getQueryHelper().getTypeMembers(allTypes);
		URI projectURI = getProjectURI();
		List<IFilter<PropertyElement>> propertyFilters = CollectionsUtil.newList(isNotConstructorFilter,
				isVisibleFilter);

		// Hack for SDK < 3.4.1.GA. The api.jsca file has correctly categorized whether the methods are static or
		// instance only from 3.4.1.GA SDK. So, we can filter out static/instance based on the type. If the SDK <=
		// 3.4.0, then we shouldn't filter them at all.
		IProject project = getProject();
		if (!hasSDKLessThanOrEqualToVersion(project, SDK_3_4_0))
		{
			CollectionsUtil.addToList(propertyFilters, isInstance ? isInstanceFilter : isStaticFilter);
		}
		IFilter<PropertyElement>[] filters = propertyFilters.toArray(new IFilter[propertyFilters.size()]);
		for (PropertyElement property : CollectionsUtil.filter(properties, new AndFilter<PropertyElement>(filters)))
		{
			addProposal(proposals, property, offset, projectURI, null);
		}
	}

	protected boolean hasSDKLessThanOrEqualToVersion(IProject project, String sdkVersion)
	{
		Set<IBuildPathEntry> entries = getBuildPathManager().getBuildPaths(project);
		for (IBuildPathEntry entry : entries)
		{
			URI indexPathUri = entry.getPath();
			IPath indexPath = Path.fromOSString(indexPathUri.getPath());
			String apiJSCA = indexPath.lastSegment();
			if (!API_JSCA.equals(apiJSCA))
			{
				continue;
			}
			indexPath = indexPath.removeLastSegments(1);
			String projectSdk = indexPath.lastSegment();
			if (VersionUtil.compareVersions(sdkVersion, projectSdk) >= 0)
			{
				return true;
			}
		}
		return false;
	}

	protected BuildPathManager getBuildPathManager()
	{
		return BuildPathManager.getInstance();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.common.CommonContentAssistProcessor#computeContextInformation(org.eclipse.jface.text.ITextViewer
	 * , int)
	 */
	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset)
	{
		this.textViewer = viewer;

		List<IContextInformation> result = new ArrayList<IContextInformation>(2);
		FunctionElement function = getFunctionElement(viewer, offset);

		if (function != null)
		{
			JSArgumentsNode node = getArgumentsNode(offset);

			if (node != null)
			{
				boolean inObjectLiteral = false;

				// find argument we're in
				for (IParseNode arg : node)
				{
					if (arg.contains(offset))
					{
						// Not foolproof, but this should cover 99% of the cases we're likely to encounter
						inObjectLiteral = (arg instanceof JSObjectNode);
						break;
					}
				}

				// prevent context info popup from appearing and immediately disappearing
				if (!inObjectLiteral)
				{
					IContextInformation ci = new JSContextInformation(function, getProjectURI(),
							node.getStartingOffset());

					result.add(ci);
				}
			}
		}

		return result.toArray(new IContextInformation[result.size()]);
	}

	/**
	 * createLexemeProvider
	 * 
	 * @param document
	 * @param offset
	 * @return
	 */
	ILexemeProvider<JSTokenType> createLexemeProvider(IDocument document, int offset)
	{
		Scanner scanner = new JSFlexScanner();
		ILexemeProvider<JSTokenType> result;

		// NOTE: use active range temporarily until we get proper partitions for JS inside of HTML
		if (activeRange != null)
		{
			result = new JSFlexLexemeProvider(document, activeRange, scanner);
		}
		else if (statementNode != null)
		{
			result = new JSFlexLexemeProvider(document, statementNode, scanner);
		}
		else
		{
			result = new JSFlexLexemeProvider(document, offset, scanner);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.common.CommonContentAssistProcessor#doComputeCompletionProposals(org.eclipse.jface.text.ITextViewer
	 * , int, char, boolean)
	 */
	@Override
	protected ICompletionProposal[] doComputeCompletionProposals(ITextViewer viewer, int offset, char activationChar,
			boolean autoActivated)
	{
		this.textViewer = viewer;

		// NOTE: Using a linked hash set to preserve add-order. We need this in case we end up filtering proposals. This
		// will give precedence to the first of a collection of proposals with like names
		Set<ICompletionProposal> result = new LinkedHashSet<ICompletionProposal>();

		// grab document
		IDocument document = viewer.getDocument();

		// determine the content assist location type
		LocationType location = getLocationType(document, offset);

		// process the resulting location
		switch (location)
		{
			case IN_PROPERTY_NAME:
				addProperties(result, offset);
				break;

			case IN_ARGUMENTS:
			case IN_VARIABLE_NAME:
				addFunctionArgumentProposals(result, viewer, offset);
				//$FALL-THROUGH$
			case IN_GLOBAL:
			case IN_CONSTRUCTOR:
				addKeywords(result, offset);
				addGlobals(result, offset);
				addSymbolsInScope(result, offset);
				break;

			case IN_OBJECT_LITERAL_PROPERTY:
				addObjectLiteralProperties(result, viewer, offset);
				break;

			case IN_THIS:
				addThisProperties(result, offset);
				break;

			default:
				break;
		}

		// merge and remove duplicates from the proposal list
		List<ICompletionProposal> filteredProposalList = getMergedProposals(new ArrayList<ICompletionProposal>(result));
		ICompletionProposal[] resultList = filteredProposalList.toArray(new ICompletionProposal[filteredProposalList
				.size()]);

		// select the current proposal based on the prefix
		if (replaceRange != null)
		{
			try
			{
				String prefix = document.get(replaceRange.getStartingOffset(), replaceRange.getLength());

				setSelectedProposal(prefix, resultList);
			}
			catch (BadLocationException e) // $codepro.audit.disable emptyCatchClause
			{
				// ignore
			}
		}

		return resultList;
	}

	private void addFunctionArgumentProposals(Set<ICompletionProposal> result, ITextViewer viewer, int offset)
	{
		FunctionElement function = getFunctionElement(viewer, offset);
		if (function == null)
		{
			return;
		}

		List<ParameterElement> params = function.getParameters();
		int index = getArgumentIndex(offset);
		if (index == -1)
		{
			// if we're not on a specific arg, assume no args yet exist and we want CA for first param
			index = 0;
		}

		if ("require".equals(function.getName()))
		{
			// SPECIAL CASE!!!!

			IProject project = EditorUtil.getProject(editor);
			URI editorURI = EditorUtil.getURI(editor);
			IPath currentDirectory = Path.fromPortableString(editorURI.getPath()).removeLastSegments(1);

			List<String> possible = RequireResolverFactory.getPossibleModuleIds(project, currentDirectory,
					project.getLocation());
			String[] userAgentIds = getActiveUserAgentIds();
			if (replaceRange == null)
			{
				replaceRange = new Range(offset);
			}
			for (String moduleId : possible)
			{
				CommonCompletionProposal proposal = addProposal(result, "'" + moduleId + "'", STRING_ICON, null,
						userAgentIds, moduleId + ".js", offset);
				if (proposal != null)
				{
					proposal.setRelevance(CommonCompletionProposal.RELEVANCE_EXACT);
				}
			}
			return;
		}

		if (0 <= index && index < params.size())
		{
			ParameterElement param = params.get(index);
			List<String> constants = param.getConstants();
			if (!CollectionsUtil.isEmpty(constants))
			{
				IProject project = getProject();
				String[] userAgentIds = getActiveUserAgentIds();
				Image[] userAgents = UserAgentManager.getInstance().getUserAgentImages(getProject(), userAgentIds);
				if (replaceRange == null)
				{
					replaceRange = new Range(offset);
				}
				for (String displayName : constants)
				{
					// FIXME For constants we may want to replace back to start of where argument is, not just back to
					// last period

					// build proposal
					FunctionArgumentProposal proposal = new FunctionArgumentProposal(displayName,
							replaceRange.getStartingOffset(), replaceRange.getLength(), project);
					proposal.setUserAgentImages(userAgents);
					proposal.setTriggerCharacters(getProposalTriggerCharacters());
					// add the proposal to the list
					result.add(proposal);
				}
			}
		}
	}

	protected JSIndexQueryHelper getQueryHelper()
	{
		if (indexHelper == null)
		{
			indexHelper = JSModelUtil.createQueryHelper(editor);
		}
		return indexHelper;
	}

	/**
	 * getActiveASTNode
	 * 
	 * @param offset
	 * @return
	 */
	IParseNode getActiveASTNode(int offset)
	{
		IParseNode result = null;

		try
		{
			// grab document
			IDocument doc = getDocument();

			// grab source which is either the whole document for JS files or a subset for nested JS
			// @formatter:off
			String source =
				(activeRange != null)
					? doc.get(activeRange.getStartingOffset(), activeRange.getLength())
					: doc.get();
			// @formatter:on
			int startingOffset = (activeRange != null) ? activeRange.getStartingOffset() : 0;

			// create parse state and turn off all processing of comments
			JSParseState parseState = new JSParseState(source, startingOffset, true, true);

			// parse and grab resulting AST
			IParseNode ast = ParserPoolFactory.parse(IJSConstants.CONTENT_TYPE_JS, parseState).getRootNode();
			// TODO Use getAST()?

			if (ast != null)
			{
				result = ast.getNodeAtOffset(offset);

				// We won't get a current node if the cursor is outside of the positions
				// recorded by the AST
				if (result == null)
				{
					if (offset < ast.getStartingOffset())
					{
						result = ast.getNodeAtOffset(ast.getStartingOffset());
					}
					else if (ast.getEndingOffset() < offset)
					{
						result = ast.getNodeAtOffset(ast.getEndingOffset());
					}
				}
			}
		}
		catch (Exception e)
		{
			// ignore parse error exception since the user will get markers and/or entries in the Problems View
		}

		return result;
	}

	protected IDocument getDocument()
	{
		return textViewer.getDocument();
	}

	/**
	 * getArgumentIndex
	 * 
	 * @param offset
	 * @return -1 if none match
	 */
	private int getArgumentIndex(int offset)
	{
		JSArgumentsNode arguments = getArgumentsNode(offset);

		if (arguments != null)
		{
			for (IParseNode child : arguments)
			{
				if (child.contains(offset))
				{
					return child.getIndex();
				}
			}
		}

		return -1;
	}

	/**
	 * getArgumentsNode
	 * 
	 * @param offset
	 * @return
	 */
	private JSArgumentsNode getArgumentsNode(int offset)
	{
		IParseNode node = getActiveASTNode(offset);
		JSArgumentsNode result = null;

		// work a way up the AST to determine if we're in an arguments node
		while (node instanceof JSNode && node.getNodeType() != IJSNodeTypes.ARGUMENTS)
		{
			node = node.getParent();
		}

		// process arguments node as long as we're not to the left of the opening parenthesis
		if (node instanceof JSNode && node.getNodeType() == IJSNodeTypes.ARGUMENTS
				&& node.getStartingOffset() != offset)
		{
			result = (JSArgumentsNode) node;
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#getContextInformationValidator()
	 */
	@Override
	public IContextInformationValidator getContextInformationValidator()
	{
		return new JSContextInformationValidator();
	}

	/**
	 * getFunctionElement
	 * 
	 * @param viewer
	 * @param offset
	 * @return
	 */
	private FunctionElement getFunctionElement(ITextViewer viewer, int offset)
	{
		// process arguments node as long as we're not to the left of the opening parenthesis
		JSArgumentsNode node = getArgumentsNode(offset);
		if (node == null)
		{
			return null;
		}

		// save current replace range. A bit hacky but better than adding a flag into getLocation's signature
		IRange range = replaceRange;

		// grab the content assist location type for the symbol before the arguments list
		int functionOffset = node.getStartingOffset();
		LocationType location = getLocationType(viewer.getDocument(), functionOffset);

		// restore replace range
		replaceRange = range;

		// init type and method names
		String typeName = null;
		String methodName = null;

		switch (location)
		{
			case IN_VARIABLE_NAME:
			{
				typeName = JSTypeUtil.getGlobalType(getProject(), getFilename());
				methodName = node.getParent().getFirstChild().getText();
				break;
			}

			case IN_PROPERTY_NAME:
			{
				JSGetPropertyNode propertyNode = ParseUtil.getGetPropertyNode(node,
						((JSNode) node).getContainingStatementNode());
				List<String> types = getParentObjectTypes(propertyNode, offset);

				if (types.size() > 0)
				{
					typeName = types.get(0);
					methodName = propertyNode.getLastChild().getText();
				}
				break;
			}

			default:
				break;
		}

		if (typeName != null && methodName != null)
		{
			// TODO Extract this out to a method on query helper? Seems like something we'd do pretty often - search for
			// a function up the typer hierarchy, returning when we find our first match
			JSIndexQueryHelper helper = getQueryHelper();
			return helper.findFunctionInHierarchy(typeName, methodName);
		}

		return null;
	}

	/**
	 * getLocationByLexeme
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	LocationType getLocationByLexeme(IDocument document, int offset)
	{
		// grab relevant lexemes around the current offset
		ILexemeProvider<JSTokenType> lexemeProvider = createLexemeProvider(document, offset);

		// assume we can't determine the location type
		LocationType result = LocationType.UNKNOWN;

		// find lexeme nearest to our offset
		int index = lexemeProvider.getLexemeIndex(offset);

		if (index < 0)
		{
			int candidateIndex = lexemeProvider.getLexemeFloorIndex(offset);
			Lexeme<JSTokenType> lexeme = lexemeProvider.getLexeme(candidateIndex);

			if (lexeme != null)
			{
				if (lexeme.getEndingOffset() == offset)
				{
					index = candidateIndex;
				}
				else if (lexeme.getType() == JSTokenType.NEW)
				{
					index = candidateIndex;
				}
			}
		}

		if (index >= 0)
		{
			Lexeme<JSTokenType> lexeme = lexemeProvider.getLexeme(index);

			switch (lexeme.getType())
			{
				case DOT:
					result = LocationType.IN_PROPERTY_NAME;
					break;

				case SEMICOLON:
					if (index > 0)
					{
						Lexeme<JSTokenType> previousLexeme = lexemeProvider.getLexeme(index - 1);

						switch (previousLexeme.getType())
						{
							case IDENTIFIER:
								result = LocationType.IN_GLOBAL;
								break;

							default:
								break;
						}
					}
					break;

				case LPAREN:
					if (offset == lexeme.getEndingOffset())
					{
						Lexeme<JSTokenType> previousLexeme = lexemeProvider.getLexeme(index - 1);

						if (previousLexeme.getType() != JSTokenType.IDENTIFIER)
						{
							result = LocationType.IN_GLOBAL;
						}
					}
					break;

				case RPAREN:
					if (offset == lexeme.getStartingOffset())
					{
						result = LocationType.IN_GLOBAL;
					}
					break;

				case IDENTIFIER:
					if (index > 0)
					{
						Lexeme<JSTokenType> previousLexeme = lexemeProvider.getLexeme(index - 1);

						switch (previousLexeme.getType())
						{
							case DOT:
								result = LocationType.IN_PROPERTY_NAME;
								break;

							case NEW:
								result = LocationType.IN_CONSTRUCTOR;
								break;

							case VAR:
								result = LocationType.IN_VARIABLE_DECLARATION;
								break;

							default:
								result = LocationType.IN_VARIABLE_NAME;
								break;
						}
					}
					else
					{
						result = LocationType.IN_VARIABLE_NAME;
					}
					break;

				default:
					break;
			}
		}
		else if (lexemeProvider.size() == 0)
		{
			result = LocationType.IN_GLOBAL;
		}

		return result;
	}

	/**
	 * getLocation
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	LocationType getLocationType(IDocument document, int offset)
	{
		JSLocationIdentifier identifier = new JSLocationIdentifier(offset, getActiveASTNode(offset - 1));
		LocationType result = identifier.getType();

		targetNode = identifier.getTargetNode();
		statementNode = identifier.getStatementNode();
		replaceRange = identifier.getReplaceRange();

		// if we couldn't determine the location type with the AST, then
		// fallback to using lexemes
		if (result == LocationType.UNKNOWN)
		{
			// NOTE: this method call sets replaceRange as a side-effect
			result = getLocationByLexeme(document, offset);
		}

		return result;
	}

	/**
	 * @param result
	 * @return
	 */
	protected List<ICompletionProposal> getMergedProposals(List<ICompletionProposal> proposals)
	{
		// remove duplicates, merging duplicates into a single proposal
		return CollectionsUtil.filter(proposals, new ProposalMerger());
	}

	private String getNestedFunctionTypeName(JSFunctionNode function)
	{
		List<String> names = new ArrayList<String>();
		IParseNode current = function;

		while (current != null && !(current instanceof JSParseRootNode))
		{
			if (current instanceof JSFunctionNode)
			{
				JSFunctionNode currentFunction = (JSFunctionNode) current;

				names.add(currentFunction.getName().getText());
			}

			current = current.getParent();
		}

		Collections.reverse(names);

		return StringUtil.join(IJSIndexConstants.NESTED_TYPE_SEPARATOR, names);
	}

	/**
	 * getParentObjectTypes
	 * 
	 * @param node
	 * @param offset
	 * @return
	 */
	protected List<String> getParentObjectTypes(JSGetPropertyNode node, int offset)
	{
		return ParseUtil.getReceiverTypeNames(getQueryHelper(), getIndex(), getURI(), targetNode, node, offset);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#getPreferenceNodeQualifier()
	 */
	protected String getPreferenceNodeQualifier()
	{
		return JSPlugin.PLUGIN_ID;
	}

	/**
	 * Expose replace range field for unit tests
	 * 
	 * @return
	 */
	IRange getReplaceRange()
	{
		return replaceRange;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#isValidActivationCharacter(char, int)
	 */
	public boolean isValidActivationCharacter(char c, int keyCode)
	{
		return Character.isWhitespace(c);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#triggerAdditionalAutoActivation(char, int,
	 * org.eclipse.jface.text.IDocument, int)
	 */
	public boolean isValidAutoActivationLocation(char c, int keyCode, IDocument document, int offset)
	{
		// NOTE: If auto-activation logic changes it may be necessary to change this logic
		// to continue walking backwards through partitions until a) a valid activation character
		// or b) a non-whitespace non-valid activation character is encountered. That implementation
		// would need to skip partitions that are effectively whitespace, for example, comment
		// partitions
		boolean result = false;

		try
		{
			ITypedRegion partition = document.getPartition(offset);

			if (partition != null && AUTO_ACTIVATION_PARTITION_TYPES.contains(partition.getType()))
			{
				int start = partition.getOffset();
				int index = offset - 1;

				while (index >= start)
				{
					char candidate = document.getChar(index);

					if (candidate == ',' || candidate == '(' || candidate == '{')
					{
						result = true;
						break;
					}
					else if (!Character.isWhitespace(candidate))
					{
						break;
					}

					index--;
				}
			}
		}
		catch (BadLocationException e)
		{
			// ignore
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#isValidIdentifier(char, int)
	 */
	public boolean isValidIdentifier(char c, int keyCode)
	{
		return Character.isJavaIdentifierStart(c) || Character.isJavaIdentifierPart(c) || c == '$';
	}

	/**
	 * The currently active range
	 * 
	 * @param activeRange
	 */
	public void setActiveRange(IRange activeRange)
	{
		this.activeRange = activeRange;
	}
}
