/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.build;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.build.BuildContext;
import com.aptana.js.core.IJSConstants;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.parsing.ast.IJSNodeTypes;
import com.aptana.js.core.parsing.ast.JSArgumentsNode;
import com.aptana.js.core.parsing.ast.JSAssignmentNode;
import com.aptana.js.core.parsing.ast.JSBinaryBooleanOperatorNode;
import com.aptana.js.core.parsing.ast.JSBinaryOperatorNode;
import com.aptana.js.core.parsing.ast.JSBreakNode;
import com.aptana.js.core.parsing.ast.JSCatchNode;
import com.aptana.js.core.parsing.ast.JSCommentNode;
import com.aptana.js.core.parsing.ast.JSConditionalNode;
import com.aptana.js.core.parsing.ast.JSConstructNode;
import com.aptana.js.core.parsing.ast.JSContinueNode;
import com.aptana.js.core.parsing.ast.JSDeclarationNode;
import com.aptana.js.core.parsing.ast.JSDoNode;
import com.aptana.js.core.parsing.ast.JSEmptyNode;
import com.aptana.js.core.parsing.ast.JSForNode;
import com.aptana.js.core.parsing.ast.JSFunctionNode;
import com.aptana.js.core.parsing.ast.JSGetElementNode;
import com.aptana.js.core.parsing.ast.JSGetPropertyNode;
import com.aptana.js.core.parsing.ast.JSGroupNode;
import com.aptana.js.core.parsing.ast.JSIdentifierNode;
import com.aptana.js.core.parsing.ast.JSIfNode;
import com.aptana.js.core.parsing.ast.JSInvokeNode;
import com.aptana.js.core.parsing.ast.JSNode;
import com.aptana.js.core.parsing.ast.JSNumberNode;
import com.aptana.js.core.parsing.ast.JSParametersNode;
import com.aptana.js.core.parsing.ast.JSRegexNode;
import com.aptana.js.core.parsing.ast.JSReturnNode;
import com.aptana.js.core.parsing.ast.JSStatementsNode;
import com.aptana.js.core.parsing.ast.JSStringNode;
import com.aptana.js.core.parsing.ast.JSSwitchNode;
import com.aptana.js.core.parsing.ast.JSThrowNode;
import com.aptana.js.core.parsing.ast.JSVarNode;
import com.aptana.js.core.parsing.ast.JSWhileNode;
import com.aptana.parsing.ast.IParseError;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.parsing.util.ParseUtil;

import beaver.Symbol;

public class JSStyleValidator extends AbstractBuildParticipant
{

	/**
	 * MAXIMUM allowed number in javascript
	 */
	private static final BigDecimal MAX_NUMBER = new BigDecimal("1.7976931348623157e+308"); //$NON-NLS-1$

	/**
	 * MINIMUM allowed number in javascript
	 */
	private static final BigDecimal MIN_NUMBER = new BigDecimal("-1.7976931348623157e+308"); //$NON-NLS-1$

	/**
	 * Pattern to recognize identifiers.
	 */
	private static final Pattern IX = Pattern.compile("^([a-zA-Z_$][a-zA-Z0-9_$]*)$"); //$NON-NLS-1$

	/**
	 * Pattern to recognize unsafe comment or string
	 */
	private static final Pattern AX = Pattern.compile(
			"@cc|<\\/?|script|\\]\\s*\\]|<\\s*!|&lt", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$

	/**
	 * The unique ID of this validator/build participant.
	 */
	public static final String ID = "com.aptana.js.core.JSStyleValidator"; //$NON-NLS-1$

	private static final Pattern QUANTIFIER = Pattern.compile("(?<!\\\\)\\{([^\\}]*)\\}"); //$NON-NLS-1$

	private static final Set<String> NOT_CONSTRUCTOR = CollectionsUtil.newSet(
			"Number", "String", "Boolean", "Math", "JSON"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

	private static final Set<String> BANNED = CollectionsUtil.newSet("arguments", "callee", "caller", "constructor", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			"eval", "prototype", "stack", "unwatch", "valueOf", "watch"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

	/**
	 * The standard set of predefineds.
	 */
	private static final Set<String> STANDARD_PREDEFINEDS = CollectionsUtil.newSet("Array", "Boolean", "Date", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"decodeURI", "decodeURIComponent", "encodeURI", "encodeURIComponent", "Error", "eval", "EvalError", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
			"Function", "isFinite", "isNaN", "JSON", "Math", "Number", "Object", "parseInt", "parseFloat", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
			"RangeError", "ReferenceError", "RegExp", "String", "SyntaxError", "TypeError", "URIError"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$

	/**
	 * Our Appcelerator-specific predefineds.
	 */
	private static final Set<String> APPC_PREDEFINEDS = CollectionsUtil.newSet(
			"Ti", "Titanium", "alert", "require", "exports", "native", "implements"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$			;

	private static final Set<String> BROWSER_PREDEFINEDS = CollectionsUtil.newSet("clearInterval", "clearTimeout", //$NON-NLS-1$ //$NON-NLS-2$
			"document", "event", "FormData", "frames", "history", "Image", "localStorage", "location", "name", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
			"navigator", "Option", "parent", "screen", "sessionStorage", "setInterval", "setTimeout", "Storage", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
			"window", "XMLHttpRequest"); //$NON-NLS-1$ //$NON-NLS-2$

	private static final Set<String> DEVEL_PREDEFINEDS = CollectionsUtil.newSet(
			"alert", "confirm", "console", "Debug", "opera", "prompt", "WSH"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$

	private static final Set<String> NODE_PREDEFINEDS = CollectionsUtil.newSet("ActiveXObject", "CScript", "Debug", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"Enumerator", "System", "VBArray", "WScript", "WSH"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

	private static final Set<String> RHINO_PREDEFINEDS = CollectionsUtil.newSet("defineClass", "deserialize", "gc", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"help", "load", "loadClass", "print", "quit", "readFile", "readUrl", "runCommand", "seal", "serialize", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
			"spawn", "sync", "toint32", "version"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

	private static final Set<String> WINDOWS_PREDEFINEDS = CollectionsUtil.newSet("Buffer", "clearInterval", //$NON-NLS-1$ //$NON-NLS-2$
			"clearTimeout", "console", "exports", "global", "module", "process", "querystring", "require", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
			"setInterval", "setTimeout", "__dirname", "__filename"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

	/**
	 * The Set of reserved words in javascript
	 */
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
		"else", //$NON-NLS-1$
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
	 * An IDocument wrapping the current file's source, so we can easily look up line numbers and offsets quickly and
	 * efficiently.
	 */
	private IDocument doc;

	/**
	 * The URI of the current file as a String.
	 */
	private String sourcePath;

	/**
	 * Stack of scopes. Global scope is at bottom (index 0). TODO Replace with Dequeue once we move to Java 1.6+
	 */
	private Stack<Scope> scopeStack;

	/**
	 * Stack of functions. Global function is at bottom (index 0). TODO Replace with Dequeue once we move to Java 1.6+
	 */
	private Stack<Function> functionStack;

	/**
	 * Stack of options. Global options are at bottom (index 0). TODO Replace with Dequeue once we move to Java 1.6+
	 */
	private Stack<EnumMap<Option, Boolean>> optionStack;

	/**
	 * The initial set of global options we use when we start the file. This is intentionally a {@link EnumMap} so we
	 * can call {@link EnumMap#clone()} when we enter a new scope to copy the wrapping scope's map and modify it in the
	 * current scope.
	 */
	private EnumMap<Option, Boolean> initialOptions = new EnumMap<Option, Boolean>(Option.class);

	private Collection<IProblem> problems;

	/**
	 * Kind of name/label.
	 */
	enum Kind
	{
		LABEL, PARAMETER, UNPARAM, FUNCTION, BECOMING, UNCTION, EXCEPTION, VAR, UNDEF, UNUSED, CLOSURE, OUTER, GLOBAL;

		public String toString()
		{
			return name();
		}
	}

	// We have a bit of a hack here. We're mixing the notion of default values that we have overridden typically (the
	// true values), with assuming false for options that have no explicit value set.
	// aptanaOptions.laxLineEnd = true;
	// aptanaOptions.jscript = true;
	enum Option
	{
		// @formatter:off
		ADSAFE    (false),
		ANON      (false),
        BITWISE   (false),
        BROWSER   (true),
        CAP       (false),
        CONTINUE  (false),
        CSS       (false),
        DEBUG     (true),
        DEVEL     (false),
        EQEQ      (false),
        ES5       (false),
        EVIL      (false),
        FORIN     (false),
        FRAGMENT  (false),
//      INDENT    (  10),
//      MAXERR    (1000),
//      MAXLEN    ( 256),
        NEWCAP    (false),
        NODE      (false),
        NOMEN     (false),
        ON        (false),
        PASSFAIL  (false),
        PLUSPLUS  (false),
        PROPERTIES(false),
        REGEXP    (false),
        RHINO     (false),
        UNDEF     (true),
        UNPARAM   (false),
        SAFE	  (false),
        SLOPPY    (false),
        STUPID    (false),
        SUB       (false),
        VARS      (false),
        WHITE     (true),
        WINDOWS   (false);
		// @formatter:on

		private boolean defValue;

		Option(boolean defValue)
		{
			this.defValue = defValue;
		}

		// FIXME We actually need to be defining the type of value (Number/Boolean)
		public boolean defaultValue()
		{
			return this.defValue;
		}
	}

	/**
	 * A Function object to track data.
	 * 
	 * @author cwilliams
	 */
	private class Function
	{
		private String name;
		private Function context;
		private int breakage;
		private int loopage;
		private Scope scope;

		/**
		 * Have we already defined variables in this function?
		 */
		private boolean vars = false;

		private Map<String, Kind> map;
		private boolean argumentsAccessed;
		private Set<String> assignedVars;

		public Function(Scope scope)
		{
			this.scope = scope;
			this.breakage = 0;
			this.loopage = 0;
			this.map = new HashMap<String, Kind>(2);
			this.assignedVars = new HashSet<String>(2);
		}

		public Function(String name, Function oldFunct, Scope scope2)
		{
			this(scope2);
			this.name = name;
			this.context = oldFunct;
		}

		public void put(String varName, Kind undef)
		{
			map.put(varName, undef);
		}

		public Kind get(String varName)
		{
			Kind k = map.get(varName);
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

		public void argumentsAccessed(boolean b)
		{
			this.argumentsAccessed = b;
		}

		public boolean areArgumentsAccessed()
		{
			return this.argumentsAccessed;
		}

		public void varAssigned(String varName)
		{
			this.assignedVars.add(varName);
		}

		public Set<String> assignedVars()
		{
			return this.assignedVars;
		}
	}

	/**
	 * a Scope object. Holds a map of names/vars in the scope.
	 * 
	 * @author cwilliams
	 */
	private class Scope implements Cloneable
	{
		private String string;
		private boolean writeable;
		private Function funct;

		private HashMap<String, Scope> names;

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

		public synchronized void put(String name, Scope variable)
		{
			if (this.names == null)
			{
				this.names = new HashMap<String, Scope>(2);
			}
			this.names.put(name, variable);
		}

		public synchronized Scope get(String name)
		{
			if (this.names == null)
			{
				return null;
			}
			return this.names.get(name);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Object clone()
		{
			Scope s = new Scope(this.string, this.writeable, this.funct);
			if (this.names != null)
			{
				s.names = (HashMap<String, Scope>) this.names.clone();
			}
			return s;
		}

		public boolean isDisruptor(IParseNode child)
		{
			// TODO We need to bubble disrupts up to the containing statements/block
			return child instanceof JSBreakNode || child instanceof JSContinueNode || child instanceof JSReturnNode
					|| child instanceof JSThrowNode;
		}
	}

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
		this.problems = new ArrayList<IProblem>(10);
		try
		{
			if (ast != null)
			{
				// Wrap the source in an IDocument so we can easily look up line numbers/offsets.
				this.doc = new Document(context.getContents());

				ParseUtil.treeApply(ast, new ParseUtil.IASTVisitor()
				{

					public boolean exitNode(IParseNode node)
					{
						JSStyleValidator.this.exitNode(node);
						return true;
					}

					public boolean enterNode(IParseNode node)
					{
						JSStyleValidator.this.enterNode(node);
						return true;
					}
				});
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(JSCorePlugin.getDefault(),
					MessageFormat.format("Failed to parse {0} for JS Style Validation", sourcePath), e); //$NON-NLS-1$
		}

		// Add parse errors...
		Collection<IParseError> parseErrors = context.getParseErrors();
		if (!CollectionsUtil.isEmpty(parseErrors))
		{
			problems.addAll(CollectionsUtil.map(parseErrors, new IMap<IParseError, IProblem>()
			{

				public IProblem map(IParseError parseError)
				{
					int offset = parseError.getOffset();
					int lineno = parseError.getLineNumber();
					if (offset < 0 && lineno > 0)
					{
						try
						{
							offset = doc.getLineOffset(lineno - 1);
						}
						catch (BadLocationException e)
						{
							// ignore
						}
					}
					return new Problem(parseError.getSeverity().intValue(), parseError.getMessage(), offset,
							parseError.getLength(), parseError.getLineNumber(), sourcePath);
				}
			}));
		}
		
		// Wipe the intermediate fields
		this.doc = null;
		this.sourcePath = null;
		this.scopeStack = null;
		this.functionStack = null;
		this.optionStack = null;
		this.predefineds = null;
		this.initialOptions.clear();

		// Filter down the list of problems based on filter expressions.
		final List<String> filters = getFilters();
		List<IProblem> filtered = CollectionsUtil.filter(problems, new IFilter<IProblem>()
		{
			public boolean include(IProblem item)
			{
				return !isIgnored(item.getMessage(), filters);
			}
		});
		// Wipe the problems collection since we're done with it.
		this.problems = null;
		context.putProblems(IJSConstants.JSSTYLE_PROBLEM_MARKER_TYPE, filtered);
	}

	public void deleteFile(BuildContext context, IProgressMonitor monitor)
	{
		if (context == null)
		{
			return;
		}

		context.removeProblems(IJSConstants.JSSTYLE_PROBLEM_MARKER_TYPE);
	}

	/**
	 * Returns the global function (should be first in the stack).
	 * 
	 * @return
	 */
	private Function globalFunct()
	{
		if (CollectionsUtil.isEmpty(this.functionStack))
		{
			return null;
		}
		return this.functionStack.get(0);
	}

	/**
	 * Returns the global scope (should be first in the stack).
	 * 
	 * @return
	 */
	private Scope globalScope()
	{
		if (CollectionsUtil.isEmpty(this.scopeStack))
		{
			return null;
		}
		return this.scopeStack.get(0);
	}

	/**
	 * Returns the current function (peeks at top of stack).
	 * 
	 * @return
	 */
	private Function currentFunction()
	{
		if (CollectionsUtil.isEmpty(this.functionStack))
		{
			return null;
		}
		return this.functionStack.peek();
	}

	/**
	 * Return the current scope (peeks at top of stack).
	 * 
	 * @return
	 */
	private Scope currentScope()
	{
		if (CollectionsUtil.isEmpty(this.scopeStack))
		{
			return null;
		}
		return this.scopeStack.peek();
	}

	/**
	 * Returns the current options (peeks at top of stack).
	 * 
	 * @return
	 */
	private EnumMap<Option, Boolean> options()
	{
		if (CollectionsUtil.isEmpty(this.optionStack))
		{
			return null;
		}
		return this.optionStack.peek();
	}

	private void enterNode(IParseNode node)
	{
		if (node instanceof IParseRootNode)
		{
			enterRootNode((IParseRootNode) node);
			return;
		}
		if (!(node instanceof JSNode))
		{
			return;
		}

		if (node instanceof JSCommentNode)
		{
			enterComment((JSCommentNode) node);
			return;
		}

		switch (node.getNodeType())
		{
			case IJSNodeTypes.ASSIGN:
				enterAssignment((JSAssignmentNode) node);
				break;

			case IJSNodeTypes.CATCH:
				enterCatch((JSCatchNode) node);
				break;

			case IJSNodeTypes.CONDITIONAL:
				enterTernary((JSConditionalNode) node);
				break;

			case IJSNodeTypes.CONSTRUCT:
				enterConstruct((JSConstructNode) node);
				break;

			case IJSNodeTypes.DECLARATION:
				enterDeclaration((JSDeclarationNode) node);
				break;

			case IJSNodeTypes.DO:
				enterDo((JSDoNode) node);
				break;

			case IJSNodeTypes.FOR:
				enterFor((JSForNode) node);
				break;

			case IJSNodeTypes.FUNCTION:
				enterFunction((JSFunctionNode) node);
				break;

			case IJSNodeTypes.GET_ELEMENT:
				enterGetElement((JSGetElementNode) node);
				break;

			case IJSNodeTypes.GET_PROPERTY:
				enterGetProperty((JSGetPropertyNode) node);
				break;

			case IJSNodeTypes.GROUP:
				enterGroupNode((JSGroupNode) node);
				break;

			case IJSNodeTypes.IDENTIFIER:
				enterIdentifier((JSIdentifierNode) node);
				break;

			case IJSNodeTypes.IF:
				enterIf((JSIfNode) node);
				break;

			case IJSNodeTypes.INVOKE:
				enterFunctionCall((JSInvokeNode) node);
				break;

			case IJSNodeTypes.LOGICAL_AND:
				enterLogicalAnd((JSBinaryBooleanOperatorNode) node);
				break;

			case IJSNodeTypes.LOGICAL_OR:
				enterLogicalOr((JSBinaryBooleanOperatorNode) node);
				break;

			case IJSNodeTypes.NUMBER:
				enterNumber((JSNumberNode) node);
				break;

			case IJSNodeTypes.PARAMETERS:
				enterParameters((JSParametersNode) node);
				break;

			case IJSNodeTypes.REGEX:
				enterRegexp((JSRegexNode) node);
				break;

			case IJSNodeTypes.RETURN:
				enterReturn((JSReturnNode) node);
				break;

			case IJSNodeTypes.STATEMENTS:
				enterStatements((JSStatementsNode) node);
				break;

			case IJSNodeTypes.SWITCH:
				enterSwitch((JSSwitchNode) node);
				break;

			case IJSNodeTypes.VAR:
				enterVar((JSVarNode) node);
				break;

			case IJSNodeTypes.WHILE:
				enterWhile((JSWhileNode) node);
				break;

			default:
				break;
		}
	}

	private void enterVar(JSVarNode node)
	{
		// Check if we can combine var decls
		if (currentFunction().vars && !option(Option.VARS))
		{
			problems.add(createWarning(Messages.JSStyleValidator_CombineVar, node.getFirstChild()));
		}
		else if (!inGlobalFunct())
		{
			currentFunction().vars = true;
		}
	}

	private void enterReturn(JSReturnNode node)
	{
		IParseNode child = node.getFirstChild();
		while (child != null)
		{
			if (child instanceof JSRegexNode)
			{
				try
				{
					int returnLine = doc.getLineOfOffset(node.getStartingOffset()) + 1;
					int regexpLine = doc.getLineOfOffset(child.getStartingOffset()) + 1;
					if (returnLine == regexpLine)
					{
						problems.add(createWarning(Messages.JSStyleValidator_WrapRegexp, child));
					}
				}
				catch (BadLocationException e)
				{
					// ignore
				}
			}

			// Only delve into GetProperty and Invoke nodes
			if (!(child instanceof JSGetPropertyNode || child instanceof JSInvokeNode))
			{
				break;
			}

			child = child.getFirstChild();
		}
	}

	private void enterGroupNode(JSGroupNode node)
	{
		IParseNode expression = node.getExpression();
		if (expression instanceof JSFunctionNode)
		{
			IParseNode parent = node.getParent();
			if (parent instanceof JSInvokeNode)
			{
				// FIXME the offset/length here is a little wonky. May need to grab next sibling (argumenets node) and
				// mark on that.
				problems.add(createWarning(Messages.JSStyleValidator_MoveInvocation, parent.getEndingOffset() - 1, 1));
			}
			else
			{
				problems.add(createWarning(Messages.JSStyleValidator_BadWrap, node));
			}
		}
	}

	private void enterComment(JSCommentNode node)
	{
		try
		{
			String commentText = this.doc.get(node.getStartingOffset(), node.getLength());
			if (commentText.startsWith("/*jslint")) //$NON-NLS-1$
			{
				// TODO Parse out the directives!
				// if (option(Option.SAFE))
				// {
				// warn('adsafe_a', this);
				// }

				String values = commentText.substring(8, commentText.length() - 2).trim();
				String[] propertyPairs = values.split(","); //$NON-NLS-1$
				for (String pair : propertyPairs)
				{
					String[] nameValue = pair.split(":"); //$NON-NLS-1$
					Option option = Option.valueOf(nameValue[0].trim().toUpperCase());
					options().put(option, Boolean.parseBoolean(nameValue[1].trim()));
				}
			}
			else if (option(Option.SAFE))
			{
				if (AX.matcher(commentText).find())
				{
					int start = node.getStart() + 2;
					problems.add(createWarning(Messages.JSStyleValidator_DangerousComment, start, node.getEnd() - start
							+ 1));
				}
			}
		}
		catch (BadLocationException e)
		{
			// ignore
		}
	}

	private void enterTernary(JSConditionalNode node)
	{
		checkCondition(node.getTestExpression());
	}

	private void enterSwitch(JSSwitchNode node)
	{
		checkCondition(node.getExpression());
		int childrenCount = node.getChildCount();
		if (childrenCount <= 1)
		{
			this.problems
					.add(createWarning(
							MessageFormat.format(Messages.JSStyleValidator_MissingA, "case"), node.getRightBrace().getStart(), 1)); //$NON-NLS-1$
		}
	}

	private void enterLogicalOr(JSBinaryBooleanOperatorNode node)
	{
		checkAnd(node.getLeftHandSide());
		checkAnd(node.getRightHandSide());
		enterLogicalAnd(node);
	}

	private void checkAnd(IParseNode node)
	{
		if (node != null && node.getNodeType() == IJSNodeTypes.LOGICAL_AND)
		{
			JSBinaryBooleanOperatorNode andNode = (JSBinaryBooleanOperatorNode) node;
			Symbol operator = andNode.getOperator();
			int start = operator.getStart();
			this.problems.add(createWarning(Messages.JSStyleValidator_And, start, operator.getEnd() - start + 1));
		}
	}

	private void enterLogicalAnd(JSBinaryBooleanOperatorNode node)
	{
		checkCondition(node.getLeftHandSide());
		checkCondition(node.getRightHandSide());
	}

	private void enterRootNode(IParseRootNode node)
	{
		// Set up the JSLint fields
		this.predefineds = new HashSet<String>(CollectionsUtil.union(STANDARD_PREDEFINEDS, APPC_PREDEFINEDS));

		// Seed the stacks with the global funct/scope/options
		this.optionStack = new Stack<EnumMap<Option, Boolean>>();
		this.optionStack.push(initialOptions);

		this.scopeStack = new Stack<JSStyleValidator.Scope>();
		this.scopeStack.push(new Scope());

		this.functionStack = new Stack<JSStyleValidator.Function>();
		this.functionStack.push(new Function(currentScope()));

		if (option(Option.ADSAFE))
		{
			options().put(Option.SAFE, true);
		}
		if (option(Option.SAFE))
		{
			options().put(Option.BROWSER, false);
			options().put(Option.CONTINUE, false);
			options().put(Option.CSS, false);
			options().put(Option.DEBUG, false);
			options().put(Option.DEVEL, false);
			options().put(Option.EVIL, false);
			options().put(Option.FORIN, false);
			options().put(Option.NEWCAP, false);
			options().put(Option.NOMEN, false);
			options().put(Option.ON, false);
			options().put(Option.RHINO, false);
			options().put(Option.SLOPPY, false);
			options().put(Option.SUB, false);
			options().put(Option.UNDEF, false);
			options().put(Option.WINDOWS, false);

			predefineds.remove("Array"); //$NON-NLS-1$
			predefineds.remove("Date"); //$NON-NLS-1$
			predefineds.remove("Function"); //$NON-NLS-1$
			predefineds.remove("Object"); //$NON-NLS-1$
			predefineds.remove("eval"); //$NON-NLS-1$

			predefineds.add("ADSAFE"); //$NON-NLS-1$
			predefineds.add("lib"); //$NON-NLS-1$
		}

		assume();
	}

	private void exitRootNode(IParseRootNode node)
	{
		exitStatements(node);
	}

	private void enterStatements(JSStatementsNode node)
	{
		// Push new scope onto the stack!
		this.scopeStack.push((Scope) currentScope().clone());

		if (!node.hasChildren() && !(node.getParent() instanceof JSWhileNode))
		{
			problems.add(createWarning(Messages.JSStyleValidator_StatementBlock, node.getStartingOffset() + 1,
					node.getLength() - 1));
		}
	}

	private void exitStatements(IParseNode node)
	{
		Scope scope = this.scopeStack.pop();
		boolean markedStrangeLoop = false;
		IParseNode disruptor = null;
		IParseNode[] children = node.getChildren();
		int length = children.length;
		for (int i = 0; i < length; i++)
		{
			IParseNode child = children[i];
			if (disruptor != null)
			{
				problems.add(createWarning(
						MessageFormat.format(Messages.JSStyleValidator_UnreachableAB, string(child), string(disruptor)),
						child));
				disruptor = null;
			}
			if (scope.isDisruptor(child))
			{
				disruptor = child;
				if (!markedStrangeLoop)
				{
					if (node instanceof IParseRootNode)
					{
						problems.add(createWarning(Messages.JSStyleValidator_WeirdProgram, lastChild(child)));
						markedStrangeLoop = true;
					}
					else
					{
						IParseNode parent = node.getParent();
						if (parent instanceof JSDoNode || parent instanceof JSWhileNode || parent instanceof JSForNode)
						{
							problems.add(createWarning(Messages.JSStyleValidator_StrangeLoop, child.getEndingOffset(),
									1));
							markedStrangeLoop = true;
						}
					}
				}
			}
		}
	}

	private IParseNode lastChild(IParseNode child)
	{
		IParseNode[] children = child.getChildren();
		if (ArrayUtil.isEmpty(children))
		{
			if (child instanceof JSEmptyNode)
			{
				return child.getParent();
			}
			return child;
		}
		return lastChild(children[children.length - 1]);
	}

	private String string(IParseNode child)
	{
		switch (child.getNodeType())
		{
			case IJSNodeTypes.BREAK:
				return "break"; //$NON-NLS-1$

			case IJSNodeTypes.CONTINUE:
				return "continue"; //$NON-NLS-1$

			case IJSNodeTypes.DO:
				return "do"; //$NON-NLS-1$

			case IJSNodeTypes.FOR:
				return "for"; //$NON-NLS-1$

			case IJSNodeTypes.FUNCTION:
				return "function"; //$NON-NLS-1$

			case IJSNodeTypes.IF:
				return "if"; //$NON-NLS-1$

			case IJSNodeTypes.RETURN:
				return "return"; //$NON-NLS-1$

			case IJSNodeTypes.SWITCH:
				return "switch"; //$NON-NLS-1$

			case IJSNodeTypes.THROW:
				return "throw"; //$NON-NLS-1$

			case IJSNodeTypes.TRY:
				return "try"; //$NON-NLS-1$

			case IJSNodeTypes.VAR:
				return "var"; //$NON-NLS-1$

			case IJSNodeTypes.WHILE:
				return "while"; //$NON-NLS-1$

			case IJSNodeTypes.WITH:
				return "with"; //$NON-NLS-1$
		}
		return child.getText();
	}

	private void enterFor(JSForNode node)
	{
		checkCondition(node.getCondition());
		checkForEmptyBody(node.getBody());
	}

	private void checkForEmptyBody(IParseNode body)
	{
		checkForEmptyBody(body, 0, 1);
	}

	private void checkForEmptyBody(IParseNode body, int offset, int length)
	{
		if (body instanceof JSStatementsNode && body.getChildCount() == 0)
		{
			problems.add(createWarning(Messages.JSStyleValidator_EmptyBlock, body.getEndingOffset() + offset, length));
		}
	}

	private void enterWhile(JSWhileNode node)
	{
		checkCondition(node.getCondition());
		checkForEmptyBody(node.getBody());
	}

	private void enterDo(JSDoNode node)
	{
		checkCondition(node.getCondition());
		checkForEmptyBody(node.getBody(), 2, 5);
	}

	private void enterIf(JSIfNode node)
	{
		checkCondition(node.getCondition());
		checkForEmptyBody(node.getTrueBlock());
		checkForEmptyBody(node.getFalseBlock());
	}

	private void checkCondition(IParseNode condition)
	{
		if (condition instanceof JSGroupNode)
		{
			condition = ((JSGroupNode) condition).getExpression();
		}

		if (condition instanceof JSAssignmentNode)
		{
			JSAssignmentNode assign = (JSAssignmentNode) condition;
			Symbol operator = assign.getOperator();
			int start = operator.getStart();
			this.problems.add(createWarning(Messages.JSStyleValidator_ConditionalAssignment, start, operator.getEnd()
					- start + 1));
		}
	}

	private void exitNode(IParseNode node)
	{
		if (node instanceof IParseRootNode)
		{
			exitRootNode((IParseRootNode) node);
			return;
		}
		if (!(node instanceof JSNode))
		{
			return;
		}
		switch (node.getNodeType())
		{
			case IJSNodeTypes.FUNCTION:
				exitFunction((JSFunctionNode) node);
				break;

			case IJSNodeTypes.STATEMENTS:
				exitStatements(node);
				break;

			default:
				break;
		}
	}

	private void enterAssignment(JSAssignmentNode node)
	{
		IParseNode left = node.getLeftHandSide();
		if (left instanceof JSGetElementNode || left instanceof JSGetPropertyNode)
		{
			JSBinaryOperatorNode get = (JSBinaryOperatorNode) left;
			IParseNode first = get.getFirstChild();
			String name = first.getNameNode().getName();
			if ("arguments".equals(name)) //$NON-NLS-1$
			{
				problems.add(createWarning(Messages.JSStyleValidator_BadAssignment, get.getEndingOffset() + 2, 1));
			}
		}
		else if (left instanceof JSIdentifierNode)
		{
			String name = left.getNameNode().getName();
			if (!RESERVED.contains(name) && currentFunction().get(name) == Kind.EXCEPTION)
			{
				problems.add(createWarning(Messages.JSStyleValidator_AssignException, left));
			}

			if (predefineds.contains(name))
			{
				if (currentScope().get(name) != null)
				{
					problems.add(createWarning(Messages.JSStyleValidator_ReadOnly, node.getRightHandSide()));
				}
				else
				{
					problems.add(createError(Messages.JSStyleValidator_ReadOnly, node.getRightHandSide()));
				}
			}

			// Make a note that we're assigning to this variable.
			currentFunction().varAssigned(name);
		}
		else
		{
			problems.add(createWarning(Messages.JSStyleValidator_BadAssignment, node.getOperator().getStart(), 1));
		}
	}

	private void enterCatch(JSCatchNode node)
	{
		addLabel(Kind.EXCEPTION, node.getIdentifier().getNameNode().getName(), node);
	}

	private void enterDeclaration(JSDeclarationNode node)
	{
		IParseNode identifier = node.getIdentifier();
		String id = identifier.getNameNode().getName();

		// Make sure we have a valid var name
		checkIdentifier(identifier);

		// Record the variable name
		addLabel(Kind.BECOMING, id, node);

		IParseNode value = node.getValue();
		if (value instanceof JSAssignmentNode)
		{
			JSAssignmentNode assign = (JSAssignmentNode) value;
			IParseNode left = assign.getLeftHandSide();
			if (left instanceof JSIdentifierNode)
			{
				problems.add(createError(
						MessageFormat.format(Messages.JSStyleValidator_VarANot, left.getNameNode().getName()), left));
			}
		}
		// Check for assignment of 'undefined'
		else if ("undefined".equals(value.getNameNode().getName())) //$NON-NLS-1$
		{
			// FIXME JSLint marks the equal sign as start of warning. Should we mark there? Mark the value? Mark the
			// equals to the end of the value?
			int start = node.getEqualSign().getStart();
			problems.add(createWarning(MessageFormat.format(Messages.JSStyleValidator_UnnecessaryInitialize, id),
					start, 1));
		}
	}

	private void enterFunction(JSFunctionNode node)
	{
		String name = node.getNameNode().getName();
		if (!StringUtil.isEmpty(name))
		{
			addLabel(Kind.FUNCTION, name, node);
		}

		IParseNode nameNode = node.getName();
		checkIdentifier(nameNode);

		// Push new function/scope/options onto the stack!
		this.functionStack.push(new Function(name, currentFunction(), currentScope()));
		this.optionStack.push(options().clone());
		this.scopeStack.push((Scope) currentScope().clone());

		// FIXME Why do we do this in both places?
		if (!StringUtil.isEmpty(name))
		{
			addLabel(Kind.FUNCTION, name, node);
		}
	}

	private void checkIdentifier(IParseNode node)
	{
		String name = node.getNameNode().getName();
		if (option(Option.SAFE) && BANNED.contains(name))
		{
			problems.add(createWarning(MessageFormat.format(Messages.JSStyleValidator_AdsafeA, name), node));
		}
		// TODO Implement expected_identifier_a_reserved
		// else if (RESERVED.contains(name) && !option(Option.ES5))
		// {
		// problems.add(createWarning(
		// MessageFormat.format("Expected an identifier and instead saw ''{0}'' (a reserved word).", name),
		// node));
		// }
	}

	private void exitFunction(JSFunctionNode node)
	{
		Function currentFunction = currentFunction();
		if (currentFunction.areArgumentsAccessed())
		{
			Set<String> assigned = currentFunction.assignedVars();

			IParseNode params = node.getParameters();
			IParseNode[] children = params.getChildren();
			int length = children.length;
			for (int i = 0; i < length; i++)
			{
				String paramName = children[i].getNameNode().getName();
				if (assigned.contains(paramName))
				{
					problems.add(createWarning(
							MessageFormat.format(Messages.JSStyleValidator_ParameterArgumentsA, paramName), children[i]));
				}
			}
		}

		// pop the stacks!
		this.functionStack.pop();
		this.optionStack.pop();
		this.scopeStack.pop();
	}

	private void enterParameters(JSParametersNode node)
	{
		for (IParseNode child : node.getChildren())
		{
			identifier((JSIdentifierNode) child);
			addLabel(Kind.PARAMETER, child.getNameNode().getName(), child);
		}
	}

	private void enterRegexp(JSRegexNode node)
	{
		String rawRegexp = node.getText();

		Matcher m = QUANTIFIER.matcher(rawRegexp);
		while (m.find())
		{
			String insideQuantifier = m.group(1);
			String[] parts = insideQuantifier.split(","); //$NON-NLS-1$
			Integer low;
			try
			{
				low = Integer.parseInt(parts[0]);
				if (parts.length > 1)
				{
					try
					{
						Integer high = Integer.parseInt(parts[1]);
						if (low > high)
						{
							problems.add(createWarning(
									MessageFormat.format(Messages.JSStyleValidator_NotGreater, low, high),
									node.getStartingOffset() + m.end(1), 1));
						}
					}
					catch (NumberFormatException e)
					{
						problems.add(createWarning(
								MessageFormat.format(Messages.JSStyleValidator_ExpectedNumberA, parts[1]),
								node.getStartingOffset() + m.end(1) - 1, 1));
					}
				}
			}
			catch (NumberFormatException e)
			{
				problems.add(createWarning(MessageFormat.format(Messages.JSStyleValidator_ExpectedNumberA, parts[0]),
						node.getStartingOffset() + m.start(1) - 1, 1));
			}
		}

		// Look for empty classes in regexps
		int length = rawRegexp.length();
		for (int i = 0; i < length; i++)
		{
			char c = rawRegexp.charAt(i);
			switch (c)
			{
				case '\\': // skip next character
					i += 1;
					break;

				case '[':
					if ((i + 1) < length)
					{
						char d = rawRegexp.charAt(i + 1);
						if (d == '^')
						{
							if (!option(Option.REGEXP))
							{
								problems.add(createWarning(
										MessageFormat.format(Messages.JSStyleValidator_InsecureA, '^'),
										node.getStartingOffset() + i + 1, 1));
							}
						}
						else if (d == ']')
						{
							problems.add(createWarning(Messages.JSStyleValidator_EmptyClass, node.getStartingOffset()
									+ i - 1, 2));
						}
					}
					break;

				case '.':
					if (!option(Option.REGEXP))
					{
						problems.add(createWarning(MessageFormat.format(Messages.JSStyleValidator_InsecureA, '.'),
								node.getStartingOffset() + i, 1));
					}
					break;

				case ' ':
					int count = 1;
					while ((i + count) < length && rawRegexp.charAt(i + count) == ' ')
					{
						count++;
					}
					if (count > 1)
					{
						problems.add(createWarning(MessageFormat.format(Messages.JSStyleValidator_UseBraces, count),
								node.getStartingOffset() + i + count - 1, count));
					}
					break;

				default:
					break;
			}
		}
	}

	private void enterConstruct(JSConstructNode node)
	{
		JSNode expressionNode = (JSNode) node.getExpression();
		if (expressionNode instanceof JSIdentifierNode)
		{
			JSIdentifierNode identifierNode = (JSIdentifierNode) expressionNode;
			String name = identifierNode.getNameNode().getName();
			if (!option(Option.EVIL) && "Function".equals(name)) //$NON-NLS-1$
			{
				// FIXME JSLint reports the warning _after_ the node, we should just mark the node.
				problems.add(createWarningAtEndOfNode(Messages.JSStyleValidator_FunctionEval, identifierNode));
			}
			else if ("Object".equals(name)) //$NON-NLS-1$
			{
				problems.add(createWarning(Messages.JSStyleValidator_UseObject, identifierNode));
			}
			else if ("Array".equals(name)) //$NON-NLS-1$
			{
				// if there's no paren following, mark beginning of node.
				IParseNode next = identifierNode.getNextNode();
				if (next instanceof JSEmptyNode)
				{
					problems.add(createWarning(Messages.JSStyleValidator_UseArray, identifierNode));
				}
				else
				{
					// FIXME JSLint reports the warning _after_ the node, we should just mark the node.
					problems.add(createWarningAtEndOfNode(Messages.JSStyleValidator_UseArray, identifierNode));
				}
			}
			else if (NOT_CONSTRUCTOR.contains(name))
			{
				problems.add(createWarning(MessageFormat.format(Messages.JSStyleValidator_NotAConstructor, name),
						identifierNode));
			}
			else if (!option(Option.NEWCAP) && !Character.isUpperCase(name.charAt(0)))
			{
				problems.add(createWarning(MessageFormat.format(Messages.JSStyleValidator_ConstructorNameA, name),
						identifierNode));
			}

			// Check if this is part of an assignment, if not complain about bad_new!
			IParseNode parent = node.getParent();
			if (!(parent instanceof JSDeclarationNode || parent instanceof JSAssignmentNode
					|| parent instanceof JSThrowNode || parent instanceof JSReturnNode
					|| parent instanceof JSArgumentsNode || parent instanceof JSGetPropertyNode))
			{
				problems.add(createWarning(Messages.JSStyleValidator_BadNew, node.getEndingOffset(), 1));
			}
		}
		else
		{
			problems.add(createWarning(Messages.JSStyleValidator_WeirdNew, node));
		}

		// Check to see if the construct has no parens!
		IParseNode args = node.getArguments();
		if (args instanceof JSEmptyNode)
		{
			problems.add(createWarning(MessageFormat.format(Messages.JSStyleValidator_MissingA, "()"), expressionNode)); //$NON-NLS-1$
		}
	}

	private void enterGetElement(JSGetElementNode node)
	{
		IParseNode property = node.getRightHandSide();

		if (property instanceof JSNumberNode)
		{
			IParseNode left = node.getLeftHandSide();
			if (left instanceof JSIdentifierNode && "arguments".equals(left.getNameNode().getName())) //$NON-NLS-1$
			{
				problems.add(createWarning(Messages.JSStyleValidator_UseParam, node));
			}
		}
		else if (property instanceof JSStringNode)
		{
			String name = stripQuotes(property.getText());
			if (!option(Option.EVIL) && ("eval".equals(name) || "execScript".equals(name))) //$NON-NLS-1$ //$NON-NLS-2$
			{
				problems.add(createWarning(Messages.JSStyleValidator_Evil, property));
			}
			else if (!option(Option.SUB) && !RESERVED.contains(name) && IX.matcher(name).find())
			{
				problems.add(createWarning(MessageFormat.format(Messages.JSStyleValidator_Subscript, name), property));
			}
		}
	}

	private void enterGetProperty(JSGetPropertyNode getPropertyNode)
	{
		JSNode left = (JSNode) getPropertyNode.getLeftHandSide();
		String leftName = left.getNameNode().getName();

		JSNode right = (JSNode) getPropertyNode.getRightHandSide();
		String rightName = right.getNameNode().getName();

		if (("callee".equals(rightName) || "caller".equals(rightName)) && "arguments".equals(leftName)) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		{
			problems.add(createWarning(MessageFormat.format(Messages.JSStyleValidator_AvoidA, rightName),
					getPropertyNode));
		}

		if (!option(Option.STUPID) && rightName.contains("Sync")) //$NON-NLS-1$
		{
			problems.add(createWarning(MessageFormat.format(Messages.JSStyleValidator_Sync, rightName), right));
		}

		if (!option(Option.EVIL))
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
				problems.add(createWarning(Messages.JSStyleValidator_Evil, line, start, 1, sourcePath));
			}
			else if (("write".equals(rightName) || "writeln".equals(rightName)) && "document".equals(leftName)) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			{
				problems.add(createWarning(Messages.JSStyleValidator_WriteIsWrong, getPropertyNode));
			}
		}

		if ("split".equals(rightName) && left instanceof JSStringNode) //$NON-NLS-1$
		{
			problems.add(createWarning(Messages.JSStyleValidator_UseArray, right));
		}
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

	private void enterIdentifier(JSIdentifierNode node)
	{
		String name = node.getNameNode().getName();

		// Remember if we're accessing arguments in this function...
		if ("arguments".equals(name)) //$NON-NLS-1$
		{
			currentFunction().argumentsAccessed(true);
		}
		if ("__iterator__".equals(name) || "__proto__".equals(name)) //$NON-NLS-1$ //$NON-NLS-2$
		{
			problems.add(createError(MessageFormat.format(Messages.JSStyleValidator_ReservedA, name), node));
		}
		else if (!option(Option.NOMEN) && name.length() > 0
				&& (name.charAt(0) == '_' || name.charAt(name.length() - 1) == '_'))
		{
			problems.add(createWarning(MessageFormat.format(Messages.JSStyleValidator_DanglingA, name), node));
		}

		identifier(node);
	}

	// FIXME JSLint assumes this gets executed when we scan the identifier and then addLabel gets called after!
	private void identifier(JSIdentifierNode node)
	{
		String name = node.getNameNode().getName();
		Scope variable = currentScope().get(name);
		if (variable == null)
		{
			if (predefineds.contains(name))
			{
				boolean writeable = false; // FIXME Grab value from predefined.get(name)
				variable = new Scope(name, writeable, globalFunct());
				globalScope().put(name, variable);
				globalFunct().put(name, Kind.VAR);
			}
			else
			{
				if (!option(Option.UNDEF))
				{
					problems.add(createWarning(MessageFormat.format(Messages.JSStyleValidator_UsedBeforeA, name), node));
				}
				variable = new Scope(name, true, currentFunction());
				currentScope().put(name, variable);
				currentFunction().put(name, Kind.UNDEF);
			}
		}
		else
		{
			Function site = variable.funct;
			// The name is in scope and defined in the current function.
			if (currentFunction() == site)
			{
				// Change 'unused' to 'var', and reject labels.
				switch (currentFunction().get(name))
				{
					case BECOMING:
						if (!(node.getParent() instanceof JSDeclarationNode))
						{
							// FIXME I had to hack this extra if check since logic in JSLint seems wrong here.
							problems.add(createWarning(
									MessageFormat.format(Messages.JSStyleValidator_UnexpectedA, name), node));
						}
						currentFunction().put(name, Kind.VAR);
						break;
					case UNUSED:
						currentFunction().put(name, Kind.VAR);
						break;
					case UNPARAM:
						currentFunction().put(name, Kind.PARAMETER);
						break;
					case UNCTION:
						currentFunction().put(name, Kind.FUNCTION);
						break;
					case LABEL:
						// TODO Implement a_label!
						// problems.add(createWarning(
						// MessageFormat.format(Messages.JSStyleValidator_ALabel, name), node));
						break;
				}
				// If the name is already defined in the current
				// function, but not as outer, then there is a scope error.
			}
			else
			{
				switch (currentFunction().get(name))
				{
					case CLOSURE:
					case FUNCTION:
					case VAR:
					case UNUSED:
						// TODO Implement a_scope!
						// problems.add(createWarning(
						// MessageFormat.format(Messages.JSStyleValidator_AScope, name), node));
						break;
					case LABEL:
						// TODO Implement a_label!
						// problems.add(createWarning(
						// MessageFormat.format(Messages.JSStyleValidator_ALabel, name), node));
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
								currentFunction().put(name, site == globalFunct() ? Kind.GLOBAL : Kind.OUTER);
								break;
							case UNPARAM:
								site.put(name, Kind.PARAMETER);
								currentFunction().put(name, Kind.OUTER);
								break;
							case UNDEF:
								currentFunction().put(name, Kind.UNDEF);
								break;
							case LABEL:
								// TODO Implement a_label!
								// problems.add(createWarning(
								// MessageFormat.format(Messages.JSStyleValidator_ALabel, name), node));
								break;
						}
				}
			}
		}
	}

	private void enterFunctionCall(JSInvokeNode node)
	{
		JSNode expressionNode = (JSNode) node.getExpression();
		if (expressionNode instanceof JSIdentifierNode)
		{
			JSIdentifierNode identifierNode = (JSIdentifierNode) expressionNode;
			String name = identifierNode.getNameNode().getName();
			JSArgumentsNode args = (JSArgumentsNode) node.getArguments();
			if (!option(Option.EVIL))
			{
				if ("eval".equals(name) || "execScript".equals(name)) //$NON-NLS-1$ //$NON-NLS-2$
				{
					problems.add(createWarning(Messages.JSStyleValidator_Evil, expressionNode));
				}
				else if ("setTimeout".equals(name) || "setInterval".equals(name)) //$NON-NLS-1$ //$NON-NLS-2$
				{
					if (args.getChildCount() > 0 && args.getChild(0) instanceof JSStringNode)
					{
						problems.add(createWarning(Messages.JSStyleValidator_ImpliedEvil, expressionNode));
					}
				}
			}

			if ("parseInt".equals(name) && args.getChildCount() == 1) //$NON-NLS-1$
			{
				problems.add(createWarning(Messages.JSStyleValidator_Radix, expressionNode));
			}
			else if ("Object".equals(name)) //$NON-NLS-1$
			{
				// FIXME JSLint reports the warning _after_ the node, we should just mark the node.
				problems.add(createWarningAtEndOfNode(Messages.JSStyleValidator_UseObject, identifierNode));
			}
		}
		else if (expressionNode instanceof JSFunctionNode && !(node.getParent() instanceof JSGroupNode))
		{
			problems.add(createWarning(Messages.JSStyleValidator_WrapImmediate, node.getEndingOffset(), 1));
		}
	}

	private void enterNumber(JSNumberNode node)
	{
		String text = node.getText();
		if (text.length() > 0)
		{
			if (text.charAt(0) == '.')
			{
				problems.add(createWarning(MessageFormat.format(Messages.JSStyleValidator_LeadingDecimalA, text), node));
			}
			if (text.charAt(text.length() - 1) == '.')
			{
				// FIXME JSLint reports the warning _after_ the node, we should just mark the node.
				problems.add(createWarningAtEndOfNode(
						MessageFormat.format(Messages.JSStyleValidator_TrailingDecimalA, text), node));
			}
			try
			{
				BigDecimal value = parseNumber(text);
				if (value.compareTo(MAX_NUMBER) > 0)
				{
					// FIXME JSLint reports the warning _after_ the node, we should just mark the node.
					problems.add(createWarningAtEndOfNode(
							MessageFormat.format(Messages.JSStyleValidator_BadNumber, text), node));
				}
				else if (value.compareTo(MIN_NUMBER) < 0)
				{
					// FIXME JSLint reports the warning _after_ the node, we should just mark the node.
					problems.add(createWarningAtEndOfNode(
							MessageFormat.format(Messages.JSStyleValidator_BadNumber, text), node));
				}
			}
			catch (NumberFormatException e)
			{
				IdeLog.logInfo(JSCorePlugin.getDefault(),
						MessageFormat.format("Error trying to parse JS number: ''{0}''", text), e, null); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Parse a JS Number. Need to look out for octal and hex representations.
	 * 
	 * @param text
	 * @return
	 */
	private BigDecimal parseNumber(String text)
	{
		// Check for hex or octal strings
		String base = text;
		boolean negative = false;
		if (base.charAt(0) == '-')
		{
			negative = true;
			base = base.substring(1);
		}
		if (base.length() > 0 && base.charAt(0) == '0')
		{
			base = base.substring(1);
			// hex or octal number starts with 0 (octal just 0, hex 0x)
			int radix = 8;
			if (base.length() > 0 && base.charAt(0) == 'x')
			{
				base = base.substring(2);
				radix = 16;
			}
			if (negative)
			{
				base = '-' + base;
			}
			return new BigDecimal(new BigInteger(base, radix));
		}

		return new BigDecimal(text);
	}

	// Define the symbol in the current function in the current scope.
	private void addLabel(Kind kind, String name, IParseNode node)
	{
		// Global variables cannot be created in the safe subset. If a global variable
		// already exists, do nothing. If it is predefined, define it.

		if (inGlobalFunct())
		{
			if (option(Option.SAFE))
			{
				problems.add(createWarning(MessageFormat.format(Messages.JSStyleValidator_AdsafeA, name), node));

			}
			if (!globalFunct().containsKey(name))
			{
				// token.writeable = typeof predefined[name] === 'boolean'
				// ? predefined[name]
				// : true;
				Scope token = new Scope(null, true, currentFunction());
				globalScope().put(name, token);
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
			if (currentFunction().containsKey(name))
			{
				if (currentFunction().get(name) == Kind.UNDEF)
				{
					if (!option(Option.UNDEF))
					{
						problems.add(createWarning(MessageFormat.format(Messages.JSStyleValidator_UsedBeforeA, name),
								node));
					}
					kind = Kind.VAR;
				}
				else
				{
					problems.add(createWarning(MessageFormat.format(Messages.JSStyleValidator_AlreadyDefined, name),
							node));
				}
			}
			// Add the symbol to the current function.
			else
			{
				Scope token = new Scope(null, true, currentFunction());
				currentScope().put(name, token);
			}
			currentFunction().put(name, kind);
		}
	}

	private boolean inGlobalFunct()
	{
		// FIXME Just check if stack is size 1?
		return currentFunction() == globalFunct();
	}

	private boolean option(Option option)
	{
		if (options().containsKey(option))
		{
			return options().get(option);
		}
		// FIXME if we haven't stuck a value in the option list, it should be false!
		return option.defaultValue();
	}

	protected IProblem createWarning(String msg, IParseNode node)
	{
		return createWarning(msg, node.getStartingOffset(), node.getLength());
	}

	protected IProblem createError(String msg, IParseNode node)
	{
		return createError(msg, node.getStartingOffset(), node.getLength());
	}

	protected IProblem createWarningAtEndOfNode(String msg, IParseNode node)
	{
		return createWarning(msg, node.getEndingOffset() + 1, 1);
	}

	protected IProblem createWarning(String msg, int start, int length)
	{
		int line = -1;
		try
		{
			line = doc.getLineOfOffset(start) + 1;
		}
		catch (BadLocationException e)
		{
			// ignore
		}
		return createWarning(msg, line, start, length, sourcePath);
	}

	protected IProblem createError(String msg, int start, int length)
	{
		int line = -1;
		try
		{
			line = doc.getLineOfOffset(start) + 1;
		}
		catch (BadLocationException e)
		{
			// ignore
		}
		return createError(msg, line, start, length, sourcePath);
	}

	void setOption(String optionName, boolean value)
	{
		initialOptions.put(Option.valueOf(optionName.toUpperCase()), value);
	}

	private void assume()
	{
		if (!option(Option.SAFE))
		{
			if (option(Option.RHINO))
			{
				predefineds.addAll(RHINO_PREDEFINEDS);
				options().put(Option.RHINO, false);
			}
			if (option(Option.DEVEL))
			{
				predefineds.addAll(DEVEL_PREDEFINEDS);
				options().put(Option.DEVEL, false);
			}
			if (option(Option.BROWSER))
			{
				predefineds.addAll(BROWSER_PREDEFINEDS);
				options().put(Option.BROWSER, false);
			}
			if (option(Option.WINDOWS))
			{
				predefineds.addAll(WINDOWS_PREDEFINEDS);
				options().put(Option.WINDOWS, false);
			}
			if (option(Option.NODE))
			{
				predefineds.addAll(NODE_PREDEFINEDS);
				options().put(Option.NODE, false);
				// node_js = true;
			}
		}
	}
}
