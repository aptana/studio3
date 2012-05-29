/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.validator;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;

import com.aptana.core.IFilter;
import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.js.IJSConstants;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.parsing.ast.IJSNodeTypes;
import com.aptana.editor.js.parsing.ast.JSArgumentsNode;
import com.aptana.editor.js.parsing.ast.JSCatchNode;
import com.aptana.editor.js.parsing.ast.JSConstructNode;
import com.aptana.editor.js.parsing.ast.JSDeclarationNode;
import com.aptana.editor.js.parsing.ast.JSEmptyNode;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSGetElementNode;
import com.aptana.editor.js.parsing.ast.JSGetPropertyNode;
import com.aptana.editor.js.parsing.ast.JSIdentifierNode;
import com.aptana.editor.js.parsing.ast.JSInvokeNode;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSNumberNode;
import com.aptana.editor.js.parsing.ast.JSParametersNode;
import com.aptana.editor.js.parsing.ast.JSStringNode;
import com.aptana.index.core.build.BuildContext;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.parsing.util.ParseUtil;

public class JSLintReplacementValidator extends AbstractBuildParticipant
{

	// aptanaOptions.laxLineEnd = true;
	// aptanaOptions.undef = true;
	// aptanaOptions.browser = true;
	// aptanaOptions.jscript = true;
	// aptanaOptions.debug = true;
	// aptanaOptions.maxerr = 1000;
	// aptanaOptions.predef = ["Ti","Titanium","alert","require","exports","native","implements"];

	private static final Pattern IX = Pattern.compile("^([a-zA-Z_$][a-zA-Z0-9_$]*)$"); //$NON-NLS-1$

	/**
	 * Kind of name/label.
	 */
	enum Kind
	{
		LABEL, PARAMETER, UNPARAM, FUNCTION, BECOMING, UNCTION, EXCEPTION, VAR, UNDEF, UNUSED, CLOSURE, OUTER, GLOBAL
	}

	enum Option
	{
		// @formatter:off
		 ANON      (true),
         BITWISE   (true),
         BROWSER   (true),
         CAP       (true),
         CONTINUE  (true),
         CSS       (true),
         DEBUG     (true),
         DEVEL     (true),
         EQEQ      (true),
         ES5       (true),
         EVIL      (true),
         FORIN     (true),
         FRAGMENT  (true),
//       INDENT    (  10),
//       MAXERR    (1000),
//       MAXLEN    ( 256),
         NEWCAP    (true),
         NODE      (true),
         NOMEN     (true),
         ON        (true),
         PASSFAIL  (true),
         PLUSPLUS  (true),
         PROPERTIES(true),
         REGEXP    (true),
         RHINO     (true),
         UNDEF     (true),
         UNPARAM   (true),
         SLOPPY    (true),
         STUPID    (true),
         SUB       (true),
         VARS      (true),
         WHITE     (true),
         WINDOWS   (true),
         SAFE	   (true);
		// @formatter:on

		private boolean defValue;

		Option(boolean defValue)
		{
			this.defValue = defValue;
		}

		public boolean defaultValue()
		{
			return this.defValue;
		}
	}

	/**
	 * The current option set.
	 */
	private HashMap<Option, Boolean> options;

	@SuppressWarnings("nls")
	// @formatter:off
	private Set<String> RESERVED = CollectionsUtil.newSet(
		"break", //$NON-NLS-1$
		"case", //$NON-NLS-1$
		"catch", //$NON-NLS-1$
		"continue", //$NON-NLS-1$
		"debugger", //$NON-NLS-1$
		"default", //$NON-NLS-1$
		"delete", //$NON-NLS-1$
		"do", //$NON-NLS-1$
		Messages.JSLintReplacementValidator_8,
		"finally", //$NON-NLS-1$
		"for", //$NON-NLS-1$
		"function", //$NON-NLS-1$
		"if", //$NON-NLS-1$
		"in", //$NON-NLS-1$
		"instanceof", //$NON-NLS-1$
		"new", //$NON-NLS-1$
		"return", //$NON-NLS-1$
		"switch", //$NON-NLS-1$
		"this", //$NON-NLS-1$
		"throw", //$NON-NLS-1$
		"try", //$NON-NLS-1$
		"typeof", //$NON-NLS-1$
		"var", //$NON-NLS-1$
		"void", //$NON-NLS-1$
		"while", //$NON-NLS-1$
		"with", //$NON-NLS-1$
		// Future
		"class", //$NON-NLS-1$
		"enum", //$NON-NLS-1$
		"export", //$NON-NLS-1$
		"extends", //$NON-NLS-1$
		"import", //$NON-NLS-1$
		"super", //$NON-NLS-1$
		// future strict
		"implements", //$NON-NLS-1$
		"interface", //$NON-NLS-1$
		"let", //$NON-NLS-1$
		"package", //$NON-NLS-1$
		"private", //$NON-NLS-1$
		"protected", //$NON-NLS-1$
		"public", //$NON-NLS-1$
		"static", //$NON-NLS-1$
		"yield", //$NON-NLS-1$
		// misc
		"const", //$NON-NLS-1$
		"null", //$NON-NLS-1$
		"true", //$NON-NLS-1$
		"false" //$NON-NLS-1$
	);
	// @formatter:on

	/**
	 * The set of predefined variables/globals. This populated with names based on options (for rhino, browser, DOM,
	 * Titanium, etc).
	 */
	private Set<String> predefineds;

	/**
	 * The current scope. Holds the list of defined names.
	 */
	private Scope scope;

	private Scope globalScope;

	/**
	 * The current function.
	 */
	private Function funct;

	private Function globalFunct;

	private IDocument doc;
	private String sourcePath;

	private class Function
	{
		// private String name;
		// private int line;
		// private Function context;
		private int breakage;
		private int loopage;
		private Scope scope;
		// private Object token;

		private Map<String, Kind> map = new HashMap<String, Kind>(2);

		public Function(Scope scope)
		{
			this.scope = scope;
			breakage = 0;
			loopage = 0;
		}

		public void put(String name, Kind undef)
		{
			map.put(name, undef);
		}

		public Kind get(String name)
		{
			Kind k = map.get(name);
			if (k != null)
			{
				return k;
			}
			return Kind.UNUSED; // FIXME What should I return here?
		}

		public boolean containsKey(String key)
		{
			return map.containsKey(key);
		}
	}

	private class Scope
	{
		private String string;
		private boolean writeable;
		private Function funct;

		private Map<String, Scope> names = new HashMap<String, Scope>(2);

		public Scope(String name, boolean writeable, Function funct)
		{
			this.string = name;
			this.writeable = writeable;
			this.funct = funct;
		}

		public Scope()
		{
			this(null, false, null);
		}

		public void put(String name, Scope variable)
		{
			names.put(name, variable);
		}

		public Scope get(String name)
		{
			return names.get(name);
		}
	}

	/**
	 * The unique ID of this validator/build participant.
	 */
	public static final String ID = "com.aptana.editor.js.validator.JSLintValidator"; //$NON-NLS-1$

	public void buildFile(BuildContext context, IProgressMonitor monitor)
	{
		if (context == null)
		{
			return;
		}

		IParseRootNode ast = null;
		try
		{
			ast = context.getAST(); // make sure a parse has happened...
		}
		catch (CoreException e)
		{
			// ignores the parser exception
		}

		this.sourcePath = context.getURI().toString();
		final List<IProblem> problems = new ArrayList<IProblem>();
		try
		{
			if (ast != null)
			{
				String source = context.getContents();
				this.doc = new Document(source);

				// Set up the JSLint fields
				options = new HashMap<Option, Boolean>();
				predefineds = CollectionsUtil.newSet(
						"Ti", "Titanium", "alert", "require", "exports", "native", "implements"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
				scope = new Scope();
				funct = new Function(scope);
				globalScope = scope;
				globalFunct = funct;

				ParseUtil.treeApply(ast, new IFilter<IParseNode>()
				{

					public boolean include(IParseNode item)
					{
						problems.addAll(handleNode(item));
						return true;
					}
				});
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(JSPlugin.getDefault(),
					MessageFormat.format("Failed to parse {0} for JS Lint Validation", sourcePath), e); //$NON-NLS-1$
		}

		this.doc = null;
		this.sourcePath = null;
		this.scope = null;
		this.funct = null;
		this.globalScope = null;
		this.globalFunct = null;

		context.putProblems(IJSConstants.JSLINT_PROBLEM_MARKER_TYPE, problems);
	}

	public void deleteFile(BuildContext context, IProgressMonitor monitor)
	{
		if (context == null)
		{
			return;
		}

		context.removeProblems(IJSConstants.JSLINT_PROBLEM_MARKER_TYPE);
	}

	private Collection<IProblem> handleNode(IParseNode node)
	{
		if (!(node instanceof JSNode))
		{
			return Collections.emptyList();
		}
		switch (node.getNodeType())
		{
			case IJSNodeTypes.CATCH:
				return validateCatch((JSCatchNode) node);

			case IJSNodeTypes.CONSTRUCT:
				return validateConstruct((JSConstructNode) node);

			case IJSNodeTypes.DECLARATION:
				return validateDeclaration((JSDeclarationNode) node);

			case IJSNodeTypes.FUNCTION:
				return validateFunction((JSFunctionNode) node);

			case IJSNodeTypes.GET_ELEMENT:
				return validateGetElement((JSGetElementNode) node);

			case IJSNodeTypes.GET_PROPERTY:
				return validateGetProperty((JSGetPropertyNode) node);

			case IJSNodeTypes.IDENTIFIER:
				return validateIdentifier((JSIdentifierNode) node);

			case IJSNodeTypes.INVOKE:
				return validateFunctionCall((JSInvokeNode) node);

			case IJSNodeTypes.NUMBER:
				return validateNumber((JSNumberNode) node);

			case IJSNodeTypes.PARAMETERS:
				return validateParameters((JSParametersNode) node);

			default:
				return Collections.emptyList();
		}
	}

	private Collection<IProblem> validateCatch(JSCatchNode node)
	{
		Collection<IProblem> problems = new ArrayList<IProblem>(2);
		problems.addAll(addLabel(Kind.EXCEPTION, node.getIdentifier().getNameNode().getName(), node));
		return problems;
	}

	private Collection<IProblem> validateDeclaration(JSDeclarationNode node)
	{
		Collection<IProblem> problems = new ArrayList<IProblem>(2);
		problems.addAll(addLabel(Kind.BECOMING, node.getIdentifier().getNameNode().getName(), node));
		return problems;
	}

	private Collection<IProblem> validateFunction(JSFunctionNode node)
	{
		Collection<IProblem> problems = new ArrayList<IProblem>(2);
		problems.addAll(addLabel(Kind.FUNCTION, node.getNameNode().getName(), node));

		return problems;
	}

	private Collection<IProblem> validateParameters(JSParametersNode node)
	{
		Collection<IProblem> problems = new ArrayList<IProblem>(2);
		for (IParseNode child : node.getChildren())
		{
			problems.addAll(addLabel(Kind.PARAMETER, child.getNameNode().getName(), child));
		}
		return problems;
	}

	private Collection<IProblem> validateConstruct(JSConstructNode node)
	{
		Collection<IProblem> problems = new ArrayList<IProblem>(2);

		JSNode expressionNode = (JSNode) node.getExpression();
		if (expressionNode instanceof JSIdentifierNode)
		{
			JSIdentifierNode identifierNode = (JSIdentifierNode) expressionNode;
			String name = identifierNode.getNameNode().getName();
			if (option(Option.EVIL) && "Function".equals(name)) //$NON-NLS-1$
			{
				// FIXME JSLint reports the warning _after_ the node, we should just mark the node.
				problems.add(createWarningAtEndOfNode(Messages.JSLintReplacementValidator_FunctionEval, identifierNode));
			}
			else if ("Object".equals(name)) //$NON-NLS-1$
			{
				problems.add(createWarning(Messages.JSLintReplacementValidator_UseObject, identifierNode));
			}
			else if ("Array".equals(name)) //$NON-NLS-1$
			{
				// if there's no paren following, mark beginning of node.
				IParseNode next = identifierNode.getNextNode();
				if (next instanceof JSEmptyNode)
				{
					problems.add(createWarning(Messages.JSLintReplacementValidator_UseArray, identifierNode));
				}
				else
				{
					// FIXME JSLint reports the warning _after_ the node, we should just mark the node.
					problems.add(createWarningAtEndOfNode(Messages.JSLintReplacementValidator_UseArray, identifierNode));
				}
			}
			else if (option(Option.NEWCAP) && !Character.isUpperCase(name.charAt(0)))
			{
				problems.add(createWarning(
						MessageFormat.format(Messages.JSLintReplacementValidator_ConstructorNameA, name),
						identifierNode));
			}
		}
		return problems;
	}

	private Collection<IProblem> validateGetElement(JSGetElementNode node)
	{
		Collection<IProblem> problems = new ArrayList<IProblem>(2);

		IParseNode property = node.getRightHandSide();

		if (property instanceof JSNumberNode)
		{
			IParseNode left = node.getLeftHandSide();
			if (left instanceof JSIdentifierNode && "arguments".equals(left.getNameNode().getName())) //$NON-NLS-1$
			{
				problems.add(createWarning(Messages.JSLintReplacementValidator_UseParam, node));
			}
		}
		else if (property instanceof JSStringNode)
		{
			String name = stripQuotes(property.getText());
			if (option(Option.EVIL) && ("eval".equals(name) || "execScript".equals(name))) //$NON-NLS-1$ //$NON-NLS-2$
			{
				problems.add(createWarning(Messages.JSLintReplacementValidator_Evil, property));
			}
			else if (option(Option.SUB) && IX.matcher(name).matches())
			{
				if (!RESERVED.contains(name))
				{
					problems.add(createWarning(
							MessageFormat.format(Messages.JSLintReplacementValidator_Subscript, name), property));
				}
			}

		}

		return problems;
	}

	private Collection<IProblem> validateGetProperty(JSGetPropertyNode getPropertyNode)
	{
		Collection<IProblem> problems = new ArrayList<IProblem>(2);

		JSNode left = (JSNode) getPropertyNode.getLeftHandSide();
		String leftName = left.getNameNode().getName();

		JSNode right = (JSNode) getPropertyNode.getRightHandSide();
		String rightName = right.getNameNode().getName();

		if (("callee".equals(rightName) || "caller".equals(rightName)) && "arguments".equals(leftName)) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		{
			problems.add(createWarning(MessageFormat.format(Messages.JSLintReplacementValidator_60, rightName),
					getPropertyNode));
		}

		if (option(Option.STUPID) && rightName.contains("Sync")) //$NON-NLS-1$
		{
			problems.add(createWarning(MessageFormat.format(Messages.JSLintReplacementValidator_Sync, rightName), right));
		}

		if (option(Option.EVIL))
		{
			if ("eval".equals(rightName) || "execScript".equals(rightName)) //$NON-NLS-1$ //$NON-NLS-2$
			{
				// FIXME JSLint reports the warning _after_ the node, we should just mark the node.
				int start = right.getEndingOffset() + 2;
				int line = -1;
				try
				{
					line = doc.getLineOfOffset(start) + 1;
				}
				catch (BadLocationException e)
				{
					// ignore
				}
				problems.add(createWarning(Messages.JSLintReplacementValidator_Evil, line, start, 1, sourcePath));
			}
			else if (("write".equals(rightName) || "writeln".equals(rightName)) && "document".equals(leftName)) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			{
				problems.add(createWarning(Messages.JSLintReplacementValidator_63, getPropertyNode));
			}
		}

		if ("split".equals(rightName) && left instanceof JSStringNode) //$NON-NLS-1$
		{
			problems.add(createWarning(Messages.JSLintReplacementValidator_UseArray, right));
		}

		return problems;
	}

	protected String stripQuotes(String name)
	{
		if (StringUtil.isEmpty(name))
		{
			return StringUtil.EMPTY;
		}
		if (name.charAt(0) == '"' || name.charAt(0) == '\'')
		{
			name = name.substring(1);
		}
		if (name.length() > 0 && (name.charAt(name.length() - 1) == '"' || name.charAt(name.length() - 1) == '\''))
		{
			name = name.substring(0, name.length() - 1);
		}
		return name;
	}

	private Collection<IProblem> validateIdentifier(JSIdentifierNode node)
	{
		Collection<IProblem> problems = new ArrayList<IProblem>(2);
		String name = node.getNameNode().getName();

		Scope variable = scope.get(name);

		if (variable == null)
		{
			if (predefineds.contains(name))
			{
				boolean writeable = false; // FIXME Grab value from predefined.get(name)
				variable = new Scope(name, writeable, globalFunct);
				globalScope.put(name, variable);
				globalFunct.put(name, Kind.VAR);
			}
			else
			{
				if (option(Option.UNDEF))
				{
					problems.add(createWarning(
							MessageFormat.format(Messages.JSLintReplacementValidator_UsedBeforeA, name), node));
				}
				variable = new Scope(name, true, funct);
				this.scope.put(name, variable);
				funct.put(name, Kind.UNDEF);
			}
		}
		else
		{
			Function site = variable.funct;
			// The name is in scope and defined in the current function.
			if (funct == site)
			{
				// Change 'unused' to 'var', and reject labels.
				switch (funct.get(name))
				{
					case BECOMING:
						problems.add(createWarning(
								MessageFormat.format(Messages.JSLintReplacementValidator_UnexpectedA, name), node));
						funct.put(name, Kind.VAR);
						break;
					case UNUSED:
						funct.put(name, Kind.VAR);
						break;
					case UNPARAM:
						funct.put(name, Kind.PARAMETER);
						break;
					case UNCTION:
						funct.put(name, Kind.FUNCTION);
						break;
					case LABEL:
						problems.add(createWarning(
								MessageFormat.format(Messages.JSLintReplacementValidator_ALabel, name), node));
						break;
				}
				// If the name is already defined in the current
				// function, but not as outer, then there is a scope error.
			}
			else
			{
				switch (funct.get(name))
				{
					case CLOSURE:
					case FUNCTION:
					case VAR:
					case UNUSED:
						problems.add(createWarning(
								MessageFormat.format(Messages.JSLintReplacementValidator_AScope, name), node));
						break;
					case LABEL:
						problems.add(createWarning(
								MessageFormat.format(Messages.JSLintReplacementValidator_ALabel, name), node));
						break;
					case OUTER:
					case GLOBAL:
						break;
					default:

						// If the name is defined in an outer function, make an outer entry, and if
						// it was unused, make it var.
						switch (site.get(name))
						{
							case BECOMING:
							case CLOSURE:
							case FUNCTION:
							case PARAMETER:
							case UNCTION:
							case UNUSED:
							case VAR:
								site.put(name, Kind.CLOSURE);
								funct.put(name, site == globalFunct ? Kind.GLOBAL : Kind.OUTER);
								break;
							case UNPARAM:
								site.put(name, Kind.PARAMETER);
								funct.put(name, Kind.OUTER);
								break;
							case UNDEF:
								funct.put(name, Kind.UNDEF);
								break;
							case LABEL:
								problems.add(createWarning(
										MessageFormat.format(Messages.JSLintReplacementValidator_ALabel, name), node));
								break;
						}
				}
			}
		}

		if ("__iterator__".equals(name) || "__proto__".equals(name)) //$NON-NLS-1$ //$NON-NLS-2$
		{
			problems.add(createWarning(MessageFormat.format(Messages.JSLintReplacementValidator_ReservedA, name), node));
		}
		else if (option(Option.NOMEN) && name.length() > 0
				&& (name.charAt(0) == '_' || name.charAt(name.length() - 1) == '_'))
		{
			problems.add(createWarning(MessageFormat.format(Messages.JSLintReplacementValidator_DanglingA, name), node));
		}
		return problems;
	}

	private Collection<IProblem> validateFunctionCall(JSInvokeNode node)
	{
		Collection<IProblem> problems = new ArrayList<IProblem>(2);
		JSNode expressionNode = (JSNode) node.getExpression();
		if (expressionNode instanceof JSIdentifierNode)
		{
			JSIdentifierNode identifierNode = (JSIdentifierNode) expressionNode;
			String name = identifierNode.getNameNode().getName();
			JSArgumentsNode args = (JSArgumentsNode) node.getArguments();
			if (option(Option.EVIL))
			{
				if ("eval".equals(name) || "execScript".equals(name)) //$NON-NLS-1$ //$NON-NLS-2$
				{
					problems.add(createWarning(Messages.JSLintReplacementValidator_Evil, expressionNode));
				}
				else if ("setTimeout".equals(name) || "setInterval".equals(name)) //$NON-NLS-1$ //$NON-NLS-2$
				{

					if (args.getChildCount() > 0 && args.getChild(0) instanceof JSStringNode)
					{
						problems.add(createWarning(Messages.JSLintReplacementValidator_ImpliedEvil, expressionNode));
					}
				}
			}

			if ("parseInt".equals(name) && args.getChildCount() == 1) //$NON-NLS-1$
			{
				problems.add(createWarning(Messages.JSLintReplacementValidator_Radix, expressionNode));
			}
			else if ("Object".equals(name)) //$NON-NLS-1$
			{
				// FIXME JSLint reports the warning _after_ the node, we should just mark the node.
				problems.add(createWarningAtEndOfNode(Messages.JSLintReplacementValidator_UseObject, identifierNode));
			}

		}
		return problems;
	}

	private Collection<IProblem> validateNumber(JSNumberNode node)
	{
		Collection<IProblem> problems = new ArrayList<IProblem>(2);
		String text = node.getText();
		if (text.length() > 0)
		{
			if (text.charAt(0) == '.')
			{
				problems.add(createWarning(
						MessageFormat.format(Messages.JSLintReplacementValidator_LeadingDecimalA, text), node));
			}
			if (text.charAt(text.length() - 1) == '.')
			{
				// FIXME JSLint reports the warning _after_ the node, we should just mark the node.
				problems.add(createWarningAtEndOfNode(
						MessageFormat.format(Messages.JSLintReplacementValidator_TrailingDecimalA, text), node));
			}
		}
		return problems;
	}

	// Define the symbol in the current function in the current scope.
	private Collection<IProblem> addLabel(Kind kind, String name, IParseNode node)
	{
		Collection<IProblem> problems = new ArrayList<IProblem>(2);

		// Global variables cannot be created in the safe subset. If a global variable
		// already exists, do nothing. If it is predefined, define it.

		if (funct == globalFunct)
		{
			if (option(Option.SAFE))
			{
				problems.add(createWarning(MessageFormat.format(Messages.JSLintReplacementValidator_AdsafeA, name),
						node));

			}
			if (!this.globalFunct.containsKey(name))
			{
				// token.writeable = typeof predefined[name] === 'boolean'
				// ? predefined[name]
				// : true;
				Scope token = new Scope();
				token.writeable = true;
				token.funct = funct;
				this.globalScope.put(name, token);
			}
			if (kind == Kind.BECOMING)
			{
				kind = Kind.VAR;
			}
			// Ordinary variables.
		}
		else
		{
			// Warn if the variable already exists.
			if (this.funct.containsKey(name))
			{
				if (this.funct.get(name) == Kind.UNDEF)
				{
					if (option(Option.UNDEF))
					{
						problems.add(createWarning(
								MessageFormat.format(Messages.JSLintReplacementValidator_UsedBeforeA, name), node));
					}
					kind = Kind.VAR;
				}
				else
				{
					problems.add(createWarning(
							MessageFormat.format(Messages.JSLintReplacementValidator_AlreadyDefined, name), node));
				}
			}
			// Add the symbol to the current function.
			else
			{
				Scope token = new Scope();
				token.funct = funct;
				token.writeable = true;
				this.scope.put(name, token);
			}
			this.funct.put(name, kind);
		}
		return problems;
	}

	private boolean option(Option option)
	{
		if (options.containsKey(option))
		{
			return options.get(option);
		}
		return option.defaultValue();
	}

	protected IProblem createWarning(String msg, IParseNode node)
	{
		int start = node.getStartingOffset();
		int line = -1;
		try
		{
			line = doc.getLineOfOffset(start) + 1;
		}
		catch (BadLocationException e)
		{
			// ignore
		}
		return createWarning(msg, line, start, node.getLength(), sourcePath);
	}

	protected IProblem createWarningAtEndOfNode(String msg, IParseNode node)
	{
		int start = node.getEndingOffset() + 1;
		int line = -1;
		try
		{
			line = doc.getLineOfOffset(start) + 1;
		}
		catch (BadLocationException e)
		{
			// ignore
		}
		return createWarning(msg, line, start, 1, sourcePath);
	}

}
