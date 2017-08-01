package com.aptana.js.core.parsing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.core.internal.utils.StringPool;

import com.aptana.core.util.StringUtil;
import com.aptana.js.core.JSLanguageConstants;
import com.aptana.js.core.parsing.ast.JSArgumentsNode;
import com.aptana.js.core.parsing.ast.JSArrayNode;
import com.aptana.js.core.parsing.ast.JSAssignmentNode;
import com.aptana.js.core.parsing.ast.JSBinaryArithmeticOperatorNode;
import com.aptana.js.core.parsing.ast.JSBinaryBooleanOperatorNode;
import com.aptana.js.core.parsing.ast.JSBreakNode;
import com.aptana.js.core.parsing.ast.JSCaseNode;
import com.aptana.js.core.parsing.ast.JSCatchNode;
import com.aptana.js.core.parsing.ast.JSClassNode;
import com.aptana.js.core.parsing.ast.JSCommaNode;
import com.aptana.js.core.parsing.ast.JSComputedPropertyNameNode;
import com.aptana.js.core.parsing.ast.JSConditionalNode;
import com.aptana.js.core.parsing.ast.JSConstructNode;
import com.aptana.js.core.parsing.ast.JSContinueNode;
import com.aptana.js.core.parsing.ast.JSDeclarationNode;
import com.aptana.js.core.parsing.ast.JSDefaultNode;
import com.aptana.js.core.parsing.ast.JSDoNode;
import com.aptana.js.core.parsing.ast.JSElisionNode;
import com.aptana.js.core.parsing.ast.JSEmptyNode;
import com.aptana.js.core.parsing.ast.JSExportNode;
import com.aptana.js.core.parsing.ast.JSFalseNode;
import com.aptana.js.core.parsing.ast.JSFinallyNode;
import com.aptana.js.core.parsing.ast.JSForInNode;
import com.aptana.js.core.parsing.ast.JSForNode;
import com.aptana.js.core.parsing.ast.JSForOfNode;
import com.aptana.js.core.parsing.ast.JSFunctionNode;
import com.aptana.js.core.parsing.ast.JSGeneratorFunctionNode;
import com.aptana.js.core.parsing.ast.JSGetElementNode;
import com.aptana.js.core.parsing.ast.JSGetPropertyNode;
import com.aptana.js.core.parsing.ast.JSGetterNode;
import com.aptana.js.core.parsing.ast.JSGroupNode;
import com.aptana.js.core.parsing.ast.JSIdentifierNode;
import com.aptana.js.core.parsing.ast.JSIfNode;
import com.aptana.js.core.parsing.ast.JSImportNode;
import com.aptana.js.core.parsing.ast.JSImportSpecifierNode;
import com.aptana.js.core.parsing.ast.JSInvokeNode;
import com.aptana.js.core.parsing.ast.JSLabelledNode;
import com.aptana.js.core.parsing.ast.JSNameValuePairNode;
import com.aptana.js.core.parsing.ast.JSNode;
import com.aptana.js.core.parsing.ast.JSNullNode;
import com.aptana.js.core.parsing.ast.JSNumberNode;
import com.aptana.js.core.parsing.ast.JSObjectNode;
import com.aptana.js.core.parsing.ast.JSParametersNode;
import com.aptana.js.core.parsing.ast.JSParseRootNode;
import com.aptana.js.core.parsing.ast.JSPostUnaryOperatorNode;
import com.aptana.js.core.parsing.ast.JSPreUnaryOperatorNode;
import com.aptana.js.core.parsing.ast.JSRegexNode;
import com.aptana.js.core.parsing.ast.JSRestElementNode;
import com.aptana.js.core.parsing.ast.JSReturnNode;
import com.aptana.js.core.parsing.ast.JSSetterNode;
import com.aptana.js.core.parsing.ast.JSSpreadElementNode;
import com.aptana.js.core.parsing.ast.JSStatementsNode;
import com.aptana.js.core.parsing.ast.JSStringNode;
import com.aptana.js.core.parsing.ast.JSSwitchNode;
import com.aptana.js.core.parsing.ast.JSThrowNode;
import com.aptana.js.core.parsing.ast.JSTrueNode;
import com.aptana.js.core.parsing.ast.JSTryNode;
import com.aptana.js.core.parsing.ast.JSVarNode;
import com.aptana.js.core.parsing.ast.JSWhileNode;
import com.aptana.js.core.parsing.ast.JSWithNode;
import com.aptana.js.core.parsing.ast.JSYieldNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.parsing.ast.ParseNode;
import com.oracle.js.parser.TokenType;
import com.oracle.js.parser.ir.AccessNode;
import com.oracle.js.parser.ir.BinaryNode;
import com.oracle.js.parser.ir.Block;
import com.oracle.js.parser.ir.BreakNode;
import com.oracle.js.parser.ir.CallNode;
import com.oracle.js.parser.ir.CaseNode;
import com.oracle.js.parser.ir.CatchNode;
import com.oracle.js.parser.ir.ClassNode;
import com.oracle.js.parser.ir.ContinueNode;
import com.oracle.js.parser.ir.EmptyNode;
import com.oracle.js.parser.ir.Expression;
import com.oracle.js.parser.ir.ExpressionStatement;
import com.oracle.js.parser.ir.ForNode;
import com.oracle.js.parser.ir.FunctionNode;
import com.oracle.js.parser.ir.IdentNode;
import com.oracle.js.parser.ir.IfNode;
import com.oracle.js.parser.ir.IndexNode;
import com.oracle.js.parser.ir.LabelNode;
import com.oracle.js.parser.ir.LexicalContext;
import com.oracle.js.parser.ir.LiteralNode;
import com.oracle.js.parser.ir.LiteralNode.ArrayLiteralNode;
import com.oracle.js.parser.ir.Module;
import com.oracle.js.parser.ir.Module.ExportEntry;
import com.oracle.js.parser.ir.Module.ImportEntry;
import com.oracle.js.parser.ir.Node;
import com.oracle.js.parser.ir.ObjectNode;
import com.oracle.js.parser.ir.PropertyNode;
import com.oracle.js.parser.ir.ReturnNode;
import com.oracle.js.parser.ir.Statement;
import com.oracle.js.parser.ir.SwitchNode;
import com.oracle.js.parser.ir.TernaryNode;
import com.oracle.js.parser.ir.ThrowNode;
import com.oracle.js.parser.ir.TryNode;
import com.oracle.js.parser.ir.UnaryNode;
import com.oracle.js.parser.ir.VarNode;
import com.oracle.js.parser.ir.WhileNode;
import com.oracle.js.parser.ir.WithNode;
import com.oracle.js.parser.ir.visitor.NodeVisitor;

import beaver.Symbol;

class GraalASTWalker extends NodeVisitor<LexicalContext>
{

	private IParseRootNode fRootNode;
	private Stack<IParseNode> fNodeStack = new Stack<IParseNode>();
	private boolean wipeNextIdent;
	private Map<Expression, JSNode> pushOnLeave;
	private final String source;

	private StringPool pool; // not sure right now if pooling helps our RAM usage mcuh (or maybe even makes it worse!)

	private Module module;

	public GraalASTWalker(String source, LexicalContext lc)
	{
		super(lc);
		this.source = source;
		fRootNode = new JSParseRootNode();
		fNodeStack.push(fRootNode);
		wipeNextIdent = false;
		pushOnLeave = new HashMap<Expression, JSNode>();
		pool = new StringPool();
	}

	@Override
	public boolean enterEmptyNode(EmptyNode emptyNode)
	{
		JSNode node = new JSEmptyNode(toSymbol(emptyNode));
		node.setSemicolonIncluded(true);
		addChildToParent(node);
		return super.enterEmptyNode(emptyNode);
	}

	@Override
	public boolean enterLiteralNode(LiteralNode<?> literalNode)
	{
		if (literalNode.isNumeric())
		{
			addChildToParent(
					new JSNumberNode(toSymbol(JSTokenType.NUMBER, literalNode, pool.add(literalNode.toString()))));
		}
		else if (literalNode.isNull())
		{
			addChildToParent(new JSNullNode(toSymbol(JSTokenType.NULL, literalNode)));
		}
		else if (literalNode.isString())
		{
			// Peek at offset before literal to sniff the single/double quotes
			int start = literalNode.getStart() - 1;
			int finish = start + literalNode.getFinish() + 1;
			String value = literalNode.getString();
			char c = source.charAt(start);
			value = c + value + c;
			addChildToParent(new JSStringNode(toSymbol(JSTokenType.STRING, start, finish, pool.add(value))));
		}
		else if (literalNode.isBoolean())
		{
			if (literalNode.getBoolean())
			{
				addChildToParent(new JSTrueNode(toSymbol(JSTokenType.TRUE, literalNode)));
			}
			else
			{
				addChildToParent(new JSFalseNode(toSymbol(JSTokenType.FALSE, literalNode)));
			}
		}
		else if (literalNode.isArray())
		{
			addToParentAndPushNodeToStack(new JSArrayNode(null, null));
		}
		else
		{
			addChildToParent(new JSRegexNode(literalNode.toString()));
		}
		return super.enterLiteralNode(literalNode);
	}

	@Override
	public Node leaveLiteralNode(LiteralNode<?> literalNode)
	{
		if (literalNode.isArray())
		{
			JSArrayNode arrayNode = (JSArrayNode) getCurrentNode();

			IParseNode[] nonElidedChildren = arrayNode.getChildren();

			ArrayLiteralNode aln = (ArrayLiteralNode) literalNode;
			final List<Expression> oldValue = aln.getElementExpressions();
			int childCount = oldValue.size();

			if (nonElidedChildren.length != childCount)
			{
				// Re-insert empty nodes for elided elements!
				IParseNode[] elidedChildren = new IParseNode[childCount];
				int j = 0;
				for (int i = 0; i < childCount; i++)
				{
					Expression e = oldValue.get(i);
					if (e == null)
					{
						elidedChildren[i] = new JSElisionNode(new JSNullNode());
					}
					else
					{
						elidedChildren[i] = nonElidedChildren[j];
						j++;
					}
				}
				arrayNode.setChildren(elidedChildren);
			}
			popNode();
		}
		return super.leaveLiteralNode(literalNode);
	}

	@Override
	public boolean enterReturnNode(ReturnNode returnNode)
	{
		JSNode node = new JSReturnNode();
		node.setSemicolonIncluded(true);
		addToParentAndPushNodeToStack(node);
		return super.enterReturnNode(returnNode);
	}

	@Override
	public Node leaveReturnNode(ReturnNode returnNode)
	{
		// If there's no child to return node, add JSEmptyNode!
		if (!returnNode.hasExpression())
		{
			Symbol r = toSymbol(JSTokenType.RETURN, returnNode);
			addChildToParent(new JSEmptyNode(r));
		}
		popNode();
		return super.leaveReturnNode(returnNode);
	}

	@Override
	public boolean enterIdentNode(IdentNode identNode)
	{
		if (wipeNextIdent)
		{
			wipeNextIdent = false;
		}
		else
		{
			if (identNode.isRestParameter())
			{
				addChildToParent(new JSRestElementNode(null, new JSIdentifierNode(identifierSymbol(identNode))));
			}
			else if (identNode.isDefaultParameter())
			{
				JSIdentifierNode ident = new JSIdentifierNode(identifierSymbol(identNode));
				// find first expression statement, holding a binary node, whose lhs has an identNode matching this.
				// rhs is ternarynode whose trueExpr is the default value!
				BinaryNode matchingInitializer = matchingInitializer(identNode);
				if (matchingInitializer != null)
				{
					TernaryNode tn = (TernaryNode) matchingInitializer.rhs();

					JSDeclarationNode declNode = new JSDeclarationNode(null);
					addToParentAndPushNodeToStack(declNode);
					addChildToParent(ident);
					tn.getTrueExpression().accept(this);
					popNode();
				}
				else
				{
					addChildToParent(ident);
				}
			}
			else
			{
				addChildToParent(new JSIdentifierNode(identifierSymbol(identNode)));
			}
		}
		return super.enterIdentNode(identNode);
	}

	private BinaryNode matchingInitializer(IdentNode identNode)
	{
		Block funcBody = lc.getCurrentFunction().getBody();
		List<Statement> statements = funcBody.getStatements();
		for (Statement stmt : statements)
		{
			if (stmt instanceof ExpressionStatement)
			{
				ExpressionStatement es = (ExpressionStatement) stmt;
				Expression e = es.getExpression();
				if (e instanceof BinaryNode)
				{
					BinaryNode possible = (BinaryNode) e;
					if (possible.lhs() instanceof IdentNode)
					{
						IdentNode lhs = (IdentNode) possible.lhs();
						if (lhs.getName().equals(identNode.getName()))
						{
							return possible;
						}
					}
				}
			}
		}
		return null;
	}

	private Symbol toSymbol(Node ident)
	{
		return new Symbol((short) 0, ident.getStart(), ident.getFinish() - 1, ident.toString());
	}

	private Symbol identifierSymbol(Node ident)
	{
		return identifierSymbol(ident, ident.toString());
	}

	private Symbol identifierSymbol(Node ident, String value)
	{
		return identifierSymbol(ident.getStart(), ident.getFinish() - 1, value);
	}

	private Symbol identifierSymbol(int start, int finish, String value)
	{
		return toSymbol(JSTokenType.IDENTIFIER, start, finish, pool.add(value));
	}

	private Symbol identifierSymbol(String value)
	{
		return identifierSymbol(-1, -1, value);
	}

	private Symbol toSymbol(JSTokenType type, Node ident)
	{
		return toSymbol(type, ident.getStart(), ident.getFinish() - 1);
	}

	private Symbol toSymbol(JSTokenType type, Node ident, Object value)
	{
		return toSymbol(type, ident.getStart(), ident.getFinish() - 1, value);
	}

	private Symbol toSymbol(JSTokenType type, int start, int finish)
	{
		return toSymbol(type, start, finish, type.getName());
	}

	private Symbol toSymbol(JSTokenType type, int start, int finish, Object value)
	{
		return new Symbol(type.getIndex(), start, finish, value);
	}

	@Override
	public boolean enterVarNode(VarNode varNode)
	{
		if (!varNode.isFunctionDeclaration() && !(varNode.getInit() instanceof ClassNode))
		{
			JSTokenType type = JSTokenType.VAR;
			if (varNode.isConst())
			{
				type = JSTokenType.CONST;
			}
			else if (varNode.isLet())
			{
				type = JSTokenType.LET;
			}
			Symbol var = toSymbol(type, varNode);

			ExportedStatus exportStatus = getExportStatus(varNode.getName());
			JSNode node = new JSVarNode(var);
			node.setSemicolonIncluded(true);
			if (exportStatus.isExported)
			{
				// push export node
				addToParentAndPushNodeToStack(new JSExportNode(exportStatus.isDefault, node));
				fNodeStack.push(node); // now push var node to top of stack (it's already hooked as child)
			}
			else
			{
				addToParentAndPushNodeToStack(node);
			}
			addToParentAndPushNodeToStack(new JSDeclarationNode(null));
		}
		else if (varNode.getInit() instanceof ClassNode)
		{
			// class decl
			IdentNode name = varNode.getName();
			ClassNode classNode = (ClassNode) varNode.getInit();
			if (name != null && name.getName().equals(Module.DEFAULT_EXPORT_BINDING_NAME))
			{
				JSClassNode jsClassNode = new JSClassNode(false, classNode.getClassHeritage() != null);
				addChildToParent(new JSExportNode(true, jsClassNode));
				fNodeStack.push(jsClassNode);
			}
			else
			{
				addToParentAndPushNodeToStack(new JSClassNode(name != null, classNode.getClassHeritage() != null));
			}

		}
		return super.enterVarNode(varNode);
	}

	@Override
	public Node leaveVarNode(VarNode varNode)
	{
		// assignment is right associative, so we end up visiting the value before the name. We have to invert the
		// children
		if (!varNode.isFunctionDeclaration() && !(varNode.getInit() instanceof ClassNode))
		{
			// Invert the two children of the declaration!
			IParseNode node = getCurrentNode();
			if (node.getChildCount() > 1)
			{
				IParseNode value = node.getChild(0);
				IParseNode name = node.getChild(1);
				((JSNode) node).setChildren(new IParseNode[] { name, value });
			}
			else
			{
				// If we only have name, add an empty node for value
				node.addChild(new JSEmptyNode(node.getEndingOffset()));
			}
			popNode(); // decl node
			popNode(); // var node

			ExportedStatus exportStatus = getExportStatus(varNode.getName());
			if (exportStatus.isExported)
			{
				popNode(); // export node
			}
		}
		else if (varNode.getInit() instanceof ClassNode)
		{
			// Swap order of body and name
			// FIXME What if no name?!
			IParseNode node = getCurrentNode();
			int childCount = node.getChildCount(); // optional first child is superclass/heritage
			IParseNode body = node.getChild(childCount - 2); // second-last child should be class body
			IParseNode name = node.getChild(childCount - 1); // last child should be the class name
			if (childCount == 2)
			{
				((JSNode) node).setChildren(new IParseNode[] { name, body });
			}
			else
			{
				IParseNode heritage = node.getFirstChild();
				((JSNode) node).setChildren(new IParseNode[] { name, heritage, body });
			}

			popNode(); // class node
		}

		return super.leaveVarNode(varNode);
	}

	@Override
	public boolean enterFunctionNode(FunctionNode functionNode)
	{
		if (!functionNode.isProgram())
		{
			JSFunctionNode funcNode;
			if (functionNode.getKind() == FunctionNode.Kind.GENERATOR)
			{
				funcNode = new JSGeneratorFunctionNode();
			}
			else
			{
				funcNode = new JSFunctionNode();
			}

			IdentNode ident = functionNode.getIdent();
			ExportedStatus exportStatus = getExportStatus(ident);
			if (exportStatus.isExported)
			{
				addToParentAndPushNodeToStack(new JSExportNode(exportStatus.isDefault, funcNode));
				fNodeStack.push(funcNode); // make function node top of stack
			}
			else
			{
				addToParentAndPushNodeToStack(funcNode);
			}
			// Visit the name
			addChildToParent(new JSIdentifierNode(identifierSymbol(ident)));
			// Need to explicitly visit the params
			addToParentAndPushNodeToStack(new JSParametersNode());
			functionNode.visitParameters(this);
			popNode(); // parameters
		}
		else
		{
			module = functionNode.getModule();
			if (module != null)
			{
				handleImportsAndExports();
			}
		}
		return super.enterFunctionNode(functionNode);
	}

	private void handleImportsAndExports()
	{
		handleImports();
		handleExports();
	}

	protected void handleImports()
	{
		List<ImportEntry> imports = module.getImportEntries();
		for (ImportEntry entry : imports)
		{
			String from = entry.getModuleRequest(); // FIXME If we have multiple from same module, need to make
													// them
													// JSNamedImportsNode!
			String as = entry.getLocalName();
			String name = entry.getImportName();

			JSImportNode importNode = new JSImportNode("'" + from + "'");

			JSImportSpecifierNode node;
			JSIdentifierNode alias = null;
			if (!StringUtil.isEmpty(as))
			{
				alias = new JSIdentifierNode(identifierSymbol(as));
			}

			if (Module.STAR_NAME.equals(name))
			{
				node = new JSImportSpecifierNode(new Symbol(JSLanguageConstants.STAR), alias);
			}
			else
			{
				JSIdentifierNode importedName = new JSIdentifierNode(identifierSymbol(name));
				if (alias == null)
				{
					node = new JSImportSpecifierNode(importedName);
				}
				else
				{
					node = new JSImportSpecifierNode(importedName, alias);
				}
			}
			if (node != null)
			{
				importNode.addChild(node);
			}
			addChildToParent(importNode);
		}
	}

	protected void handleExports()
	{
		List<ExportEntry> starExports = module.getStarExportEntries();
		for (ExportEntry entry : starExports)
		{
			addChildToParent(new JSExportNode(false, (Symbol) null, "'" + entry.getModuleRequest() + "'"));
		}
	}

	@Override
	public Node leaveFunctionNode(FunctionNode functionNode)
	{
		if (!functionNode.isProgram())
		{

			popNode(); // func node
			ExportedStatus exportStatus = getExportStatus(functionNode.getIdent());
			if (exportStatus.isExported)
			{
				popNode(); // export node
			}
			// when the function node is the "init" of a parent VarNode, we need to avoid hitting the "name" IdentNode.
			if (!functionNode.isAnonymous() && !functionNode.isMethod())
			{
				wipeNextIdent = true;
			}
		}
		else
		{
			module = null;
			pool = null;
			pushOnLeave = null;
			// FIXME If nodestack is not empty, spit out an error message
			fNodeStack = null;
		}
		return super.leaveFunctionNode(functionNode);
	}

	@Override
	public boolean enterUnaryNode(UnaryNode unaryNode)
	{
		JSNode theNode = null;
		JSTokenType type;
		TokenType tokenType = unaryNode.tokenType();
		switch (tokenType)
		{
			case NOT:
				type = JSTokenType.EXCLAMATION;
				theNode = new JSPreUnaryOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case INCPREFIX:
				type = JSTokenType.PLUS_PLUS;
				theNode = new JSPreUnaryOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case DECPREFIX:
				type = JSTokenType.MINUS_MINUS;
				theNode = new JSPreUnaryOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case INCPOSTFIX:
				type = JSTokenType.PLUS_PLUS;
				theNode = new JSPostUnaryOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case DECPOSTFIX:
				type = JSTokenType.MINUS_MINUS;
				theNode = new JSPostUnaryOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case BIT_NOT:
				type = JSTokenType.TILDE;
				theNode = new JSPreUnaryOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			// case ELLIPSIS:
			// break;
			case DELETE:
				type = JSTokenType.DELETE;
				theNode = new JSPreUnaryOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case NEW:
				type = JSTokenType.NEW;
				theNode = new JSConstructNode();
				break;
			case TYPEOF:
				type = JSTokenType.TYPEOF;
				theNode = new JSPreUnaryOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case VOID:
				type = JSTokenType.VOID;
				theNode = new JSPreUnaryOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case ADD:
				type = JSTokenType.PLUS;
				theNode = new JSPreUnaryOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case SUB:
				type = JSTokenType.MINUS;
				theNode = new JSPreUnaryOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;

			case YIELD:
				type = JSTokenType.YIELD;
				theNode = new JSYieldNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;

			case SPREAD_ARRAY:
				type = JSTokenType.DOT_DOT_DOT;
				theNode = new JSSpreadElementNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;

			case SPREAD_ARGUMENT:
				type = JSTokenType.DOT_DOT_DOT;
				theNode = new JSSpreadElementNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;

			default:
				throw new IllegalStateException("Reached unhandled unary node type! " + unaryNode);
		}
		addToParentAndPushNodeToStack(theNode);
		return super.enterUnaryNode(unaryNode);
	}

	@Override
	public Node leaveUnaryNode(UnaryNode unaryNode)
	{
		TokenType tokenType = unaryNode.tokenType();
		if (tokenType == TokenType.NEW)
		{
			JSConstructNode cn = (JSConstructNode) getCurrentNode();
			// FIXME If this is a construct node and it's child is an invoke node:
			// Grab the first child of invoke node and make that the construct first child
			// grab the second child of invoke node (args) and make that construct second child (or empty node)
			JSNode firstChild = (JSNode) cn.getFirstChild();
			if (firstChild instanceof JSInvokeNode)
			{
				JSNode expression = (JSNode) firstChild.getChild(0);
				JSNode arguments = (JSNode) firstChild.getChild(1);
				IParseNode[] newChildren = new IParseNode[] { expression, arguments };
				cn.setChildren(newChildren);
			}
		}
		popNode();
		return super.leaveUnaryNode(unaryNode);
	}

	@Override
	public boolean enterBinaryNode(BinaryNode binaryNode)
	{
		JSNode theNode;
		JSTokenType type;
		TokenType tokenType = binaryNode.tokenType();
		switch (tokenType)
		{
			// JSBinaryBooleanOperatorNode
			case INSTANCEOF:
				type = JSTokenType.INSTANCEOF;
				theNode = new JSBinaryBooleanOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case IN:
				type = JSTokenType.IN;
				theNode = new JSBinaryBooleanOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case EQ:
				type = JSTokenType.EQUAL_EQUAL;
				theNode = new JSBinaryBooleanOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case EQ_STRICT:
				type = JSTokenType.EQUAL_EQUAL_EQUAL;
				theNode = new JSBinaryBooleanOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case NE:
				type = JSTokenType.EXCLAMATION_EQUAL;
				theNode = new JSBinaryBooleanOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case NE_STRICT:
				type = JSTokenType.EXCLAMATION_EQUAL_EQUAL;
				theNode = new JSBinaryBooleanOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case LE:
				type = JSTokenType.LESS_EQUAL;
				theNode = new JSBinaryBooleanOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case LT:
				type = JSTokenType.LESS;
				theNode = new JSBinaryBooleanOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case GE:
				type = JSTokenType.GREATER_EQUAL;
				theNode = new JSBinaryBooleanOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case GT:
				type = JSTokenType.GREATER;
				theNode = new JSBinaryBooleanOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case AND:
				type = JSTokenType.AMPERSAND_AMPERSAND;
				theNode = new JSBinaryBooleanOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case OR:
				type = JSTokenType.PIPE_PIPE;
				theNode = new JSBinaryBooleanOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;

			// JSBinaryArithmeticOperatorNode
			case ADD:
				type = JSTokenType.PLUS;
				theNode = new JSBinaryArithmeticOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case SUB:
				type = JSTokenType.MINUS;
				theNode = new JSBinaryArithmeticOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			// shift operators
			case SHL:
				type = JSTokenType.LESS_LESS;
				theNode = new JSBinaryArithmeticOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case SAR:
				type = JSTokenType.GREATER_GREATER;
				theNode = new JSBinaryArithmeticOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case SHR:
				type = JSTokenType.GREATER_GREATER_GREATER;
				theNode = new JSBinaryArithmeticOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case BIT_AND:
				type = JSTokenType.AMPERSAND;
				theNode = new JSBinaryArithmeticOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case BIT_XOR:
				type = JSTokenType.CARET;
				theNode = new JSBinaryArithmeticOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case BIT_OR:
				type = JSTokenType.PIPE;
				theNode = new JSBinaryArithmeticOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case MUL:
				type = JSTokenType.STAR;
				theNode = new JSBinaryArithmeticOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case DIV:
				type = JSTokenType.FORWARD_SLASH;
				theNode = new JSBinaryArithmeticOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case MOD:
				type = JSTokenType.PERCENT;
				theNode = new JSBinaryArithmeticOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case EXP:
				type = JSTokenType.STAR_STAR;
				theNode = new JSBinaryArithmeticOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;

			// Assignment
			case ASSIGN:
				type = JSTokenType.EQUAL;
				theNode = new JSAssignmentNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case ASSIGN_BIT_AND:
				type = JSTokenType.AMPERSAND_EQUAL;
				theNode = new JSAssignmentNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case ASSIGN_ADD:
				type = JSTokenType.PLUS_EQUAL;
				theNode = new JSAssignmentNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case ASSIGN_SHR:
				type = JSTokenType.GREATER_GREATER_GREATER_EQUAL;
				theNode = new JSAssignmentNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case ASSIGN_BIT_OR:
				type = JSTokenType.PIPE_EQUAL;
				theNode = new JSAssignmentNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case ASSIGN_BIT_XOR:
				type = JSTokenType.CARET_EQUAL;
				theNode = new JSAssignmentNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case ASSIGN_DIV:
				type = JSTokenType.FORWARD_SLASH_EQUAL;
				theNode = new JSAssignmentNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case ASSIGN_MOD:
				type = JSTokenType.PERCENT_EQUAL;
				theNode = new JSAssignmentNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case ASSIGN_MUL:
				type = JSTokenType.STAR_EQUAL;
				theNode = new JSAssignmentNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case ASSIGN_SHL:
				type = JSTokenType.LESS_LESS_EQUAL;
				theNode = new JSAssignmentNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case ASSIGN_SAR:
				type = JSTokenType.GREATER_GREATER_EQUAL;
				theNode = new JSAssignmentNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case ASSIGN_SUB:
				type = JSTokenType.MINUS_EQUAL;
				theNode = new JSAssignmentNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
			case ASSIGN_EXP:
				type = JSTokenType.STAR_STAR_EQUAL;
				theNode = new JSAssignmentNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;

			// comma...
			case COMMARIGHT:
				type = JSTokenType.COMMA;
				theNode = new JSCommaNode();
				break;
			default:
				throw new IllegalStateException("Reached unhandled binary node type! " + binaryNode);
		}
		// push operator node to stack
		addToParentAndPushNodeToStack(theNode);

		// if lhs needs parens, add group node
		Expression lhs = binaryNode.lhs();
		boolean lhsNeedsParens = lhs != null && tokenType.needsParens(lhs.tokenType(), true);
		if (lhsNeedsParens)
		{
			addToParentAndPushNodeToStack(new JSGroupNode(null, null));
		}
		return super.enterBinaryNode(binaryNode);
	}

	@Override
	public Node leaveBinaryNode(BinaryNode binaryNode)
	{
		TokenType tokenType = binaryNode.tokenType();

		Expression lhs = binaryNode.lhs();
		boolean lhsNeedsParens = lhs != null && tokenType.needsParens(lhs.tokenType(), true);
		boolean rhsNeedsParens = tokenType.needsParens(binaryNode.rhs().tokenType(), false);
		JSNode parentNode = (JSNode) getCurrentNode();
		JSNode lastChild = (JSNode) parentNode.getLastChild();
		if (lhsNeedsParens || rhsNeedsParens)
		{
			// remove the last child, it'll need to be placed under new parent
			IParseNode[] children = parentNode.getChildren();
			IParseNode[] newChildren = new IParseNode[children.length - 1];
			System.arraycopy(children, 0, newChildren, 0, children.length - 1);
			parentNode.setChildren(newChildren);
		}

		if (lhsNeedsParens)
		{
			popNode(); // remove the LHS group node, now current node is operator
		}

		// where does the last child go now?
		if (rhsNeedsParens)
		{
			// to new group node for RHS parens
			addToParentAndPushNodeToStack(new JSGroupNode(null, null));
			addChildToParent(lastChild);
			popNode(); // pop RHS parens node, current node is operator again
		}
		else if (lhsNeedsParens)
		{
			// add last child to operator node (moved out from incorrect LHS grouping)
			addChildToParent(lastChild);
		}

		popNode(); // pop operator node
		return super.leaveBinaryNode(binaryNode);
	}

	@Override
	public Node leaveExpressionStatement(ExpressionStatement expressionStatement)
	{
		JSNode node = (JSNode) getLastNode();
		node.setSemicolonIncluded(true);
		return super.leaveExpressionStatement(expressionStatement);
	}

	@Override
	public boolean enterObjectNode(ObjectNode objectNode)
	{
		addToParentAndPushNodeToStack(new JSObjectNode(null, null));
		return super.enterObjectNode(objectNode);
	}

	@Override
	public Node leaveObjectNode(ObjectNode objectNode)
	{
		popNode();
		return super.leaveObjectNode(objectNode);
	}

	@Override
	public boolean enterBlock(Block block)
	{
		if (block.isParameterBlock())
		{
			// when we have default parameters, the parser generates a special "parameter block"
			// that is filled with generated expressions to initialize argument values
			// The last statement is a block statement holding the real function body/block.
			super.enterBlock(block); // print our current node
			// don't create a statements node. We basically need to ignore until last BlockStatement and go into that
			block.getLastStatement().accept(this);
			return false; // don't go into this fake node. We already manaully went into the real function body
		}
		else if (!block.isSynthetic())
		{
			addToParentAndPushNodeToStack(new JSStatementsNode());
		}
		else if (block.getLastStatement() instanceof ForNode)
		{
			// Handle synthetic block that holds var/let/const declarations in for loops
			addToParentAndPushNodeToStack(new JSStatementsNode());
		}
		return super.enterBlock(block);
	}

	@Override
	public Node leaveBlock(Block block)
	{
		if (!block.isSynthetic())
		{
			popNode();
		}
		else if (block.getLastStatement() instanceof ForNode)
		{
			ForNode fN = (ForNode) block.getLastStatement();

			// if block is synthetic and last child is for node, gather all the preceding var nodes and assign them
			// as
			// init values for the for loop!
			JSStatementsNode statements = (JSStatementsNode) getCurrentNode();
			IParseNode statementsParent = statements.getParent();
			JSNode forNode = (JSNode) statements.getLastChild();

			// remove for node from statements block
			IParseNode[] children = statements.getChildren();
			IParseNode[] newCHildren = new IParseNode[children.length - 1];
			System.arraycopy(children, 0, newCHildren, 0, children.length - 1);
			statements.setChildren(newCHildren);

			// convert statements node (block) into the initializer of the for loop
			if (children.length > 1 || fN.getInit() == null)
			{
				JSNode combinedVarDecls = combineVarDeclarations(statements.getStartingOffset(),
						statements.getChildren());
				forNode.replaceChild(0, combinedVarDecls);
			}

			// remove statements from it's original parent
			IParseNode[] parentChildren = statementsParent.getChildren();
			IParseNode[] parentNewCHildren = new IParseNode[parentChildren.length];
			System.arraycopy(parentChildren, 0, parentNewCHildren, 0, parentChildren.length - 1);
			parentNewCHildren[parentChildren.length - 1] = forNode; // move for node up to parent of statements as
																	// last
																	// child, replacing the statements node
			((ParseNode) statementsParent).setChildren(parentNewCHildren);

			popNode(); // statements node
		}
		return super.leaveBlock(block);
	}

	private JSNode combineVarDeclarations(int offset, IParseNode[] children)
	{
		if (children == null || children.length == 0)
		{
			return new JSEmptyNode(offset);
		}
		JSVarNode firstVarNode = (JSVarNode) children[0];
		firstVarNode.setSemicolonIncluded(false);
		JSDeclarationNode declNode = (JSDeclarationNode) firstVarNode.getFirstChild();
		declNode.setSemicolonIncluded(false);

		// loop through the rest and grab the single JSDeclarationNode child and append to first var node!
		for (int i = 1; i < children.length; i++)
		{
			declNode = (JSDeclarationNode) children[i].getFirstChild();
			// We need to set semicolon included to false on each decl node!
			declNode.setSemicolonIncluded(false);

			firstVarNode.addChild(declNode);
		}
		return firstVarNode;
	}

	@Override
	public boolean enterClassNode(ClassNode classNode)
	{
		// if we extend something, visit that first
		Expression heritage = classNode.getClassHeritage();
		if (heritage != null)
		{
			heritage.accept(this);
		}
		// then wrap body in a statements node
		addToParentAndPushNodeToStack(new JSStatementsNode());
		// manually walk constructor
		PropertyNode constructorNode = classNode.getConstructor();
		if (constructorNode != null)
		{
			boolean visitConstructor = !isSyntheticConstructor(classNode, constructorNode);

			if (visitConstructor)
			{
				constructorNode.accept(this);
			}
		}
		// manually walk the properties
		List<PropertyNode> classElements = classNode.getClassElements();
		for (PropertyNode prop : classElements)
		{
			prop.accept(this);
		}
		return super.enterClassNode(classNode);
	}

	private boolean isSyntheticConstructor(ClassNode classNode, PropertyNode constructorNode)
	{
		// constructor may be synthetic from parser
		Expression value = constructorNode.getValue();
		if (!(value instanceof FunctionNode))
		{
			return false;
		}

		String keyName = constructorNode.getKeyName();
		IdentNode nameNode = classNode.getIdent();
		String constructorName = "constructor"; //$NON-NLS-1$
		if (nameNode != null)
		{
			constructorName = nameNode.getName();
		}
		if (!constructorName.equals(keyName))
		{
			return false;
		}

		FunctionNode constructorFunction = (FunctionNode) value;
		Block body = constructorFunction.getBody();
		int numParams = constructorFunction.getNumOfParams();
		boolean isSubclass = (classNode.getClassHeritage() != null);

		// not a subclass
		if (!isSubclass)
		{
			return numParams == 0 && body.getStatementCount() == 0;
		}

		// TODO do we need to check the statement is an ExpressionStatement holding a CallNode with function "super" and
		// single "args" argument?
		return numParams == 1 && constructorFunction.hasDirectSuper()
				&& constructorFunction.getParameter(0).isRestParameter() && body.getStatementCount() == 1;
	}

	@Override
	public Node leaveClassNode(ClassNode classNode)
	{
		popNode();
		return super.leaveClassNode(classNode);
	}

	@Override
	public boolean enterForNode(ForNode forNode)
	{
		JSNode blah;
		if (forNode.isForIn())
		{
			blah = new JSForInNode(null, null, null);
		}
		else if (forNode.isForOf())
		{
			blah = new JSForOfNode(null, null);
		}
		else
		{
			blah = new JSForNode(null, null, null, null);
		}
		addToParentAndPushNodeToStack(blah);
		if (forNode.getInit() == null)
		{
			// if current parent
			addChildToParent(new JSEmptyNode(forNode.getStart()));
			if (forNode.getTest() == null)
			{
				addChildToParent(new JSEmptyNode(forNode.getStart()));
			}
		}
		return super.enterForNode(forNode);
	}

	@Override
	public Node leaveForNode(ForNode forNode)
	{
		IParseNode theNode = getCurrentNode();
		if (!forNode.isForInOrOf() && theNode.getChildCount() != 4)
		{
			// if the body is empty, we need to add an empty JSStatementsNode as body!
			if (forNode.getBody().getStatementCount() == 0)
			{
				theNode.addChild(new JSStatementsNode());
			}

			// we may have added the 4th child now...
			if (theNode.getChildCount() != 4)
			{
				// Inject empty nodes for missing test/increment expressions!
				IParseNode[] newChildren = new IParseNode[4];
				newChildren[0] = theNode.getFirstChild(); // we always inject an empty node if necessary for init
															// expression
				newChildren[3] = theNode.getLastChild(); // body is always last and should be non-null/empty

				// if we didn't already inject an empty test node...
				if (forNode.getTest() == null && forNode.getInit() != null)
				{
					// inject empty node for "test" expression
					newChildren[1] = new JSEmptyNode(newChildren[0].getEndingOffset());
					if (forNode.getModify() == null)
					{
						// if modify expression is null too, add empty node
						newChildren[2] = new JSEmptyNode(newChildren[0].getEndingOffset());
					}
					else
					{
						// copy over modify expression to right place
						newChildren[2] = theNode.getChild(1);
					}
				}
				// init and test expressions are set, just need to inject empty node for modify
				else if (forNode.getModify() == null)
				{
					// init is good, test is good, body is good.
					newChildren[1] = theNode.getChild(1);
					newChildren[2] = new JSEmptyNode(newChildren[1].getEndingOffset());
				}
				else
				{
					throw new IllegalStateException("Failed to set second and thrid children on for loop node!");
				}
				((JSNode) theNode).setChildren(newChildren);
			}
		}
		popNode();
		return super.leaveForNode(forNode);

	}

	@Override
	public boolean enterPropertyNode(PropertyNode propertyNode)
	{
		if (propertyNode.getGetter() != null)
		{
			addToParentAndPushNodeToStack(new JSGetterNode());
		}
		else if (propertyNode.getSetter() != null)
		{
			addToParentAndPushNodeToStack(new JSSetterNode());
		}
		else if (propertyNode.getValue() instanceof FunctionNode)
		{
			addToParentAndPushNodeToStack(new JSNameValuePairNode());
		}
		else
		{
			Expression key = propertyNode.getKey();
			int offset = key.getFinish() + 2;
			Symbol colon = toSymbol(JSTokenType.COLON, offset, offset + 1);
			addToParentAndPushNodeToStack(new JSNameValuePairNode(colon));
			// if the property name is computed, manually traverse
			if (!(key instanceof LiteralNode) && !(key instanceof IdentNode))
			{
				addToParentAndPushNodeToStack(new JSComputedPropertyNameNode());
				propertyNode.getKey().accept(this);
				popNode(); // computed property name node
				propertyNode.getValue().accept(this);
				popNode(); // name value pair node
				return false;
			}
		}
		return super.enterPropertyNode(propertyNode);
	}

	@Override
	public Node leavePropertyNode(PropertyNode propertyNode)
	{
		if (propertyNode.getGetter() != null)
		{
			// if getter, grab "value", which should be a function node
			// Grab function node's body. Replace our value with that body
			JSGetterNode getterNode = (JSGetterNode) getCurrentNode();
			JSFunctionNode funcValue = (JSFunctionNode) getterNode.getValue();
			JSStatementsNode bodyNode = (JSStatementsNode) funcValue.getBody();
			getterNode.replaceChild(1, bodyNode);
		}
		else if (propertyNode.getSetter() != null)
		{
			JSSetterNode setterNode = (JSSetterNode) getCurrentNode();
			JSFunctionNode funcValue = (JSFunctionNode) setterNode.getValue();
			JSStatementsNode bodyNode = (JSStatementsNode) funcValue.getBody();
			JSParametersNode paramsNode = (JSParametersNode) funcValue.getParameters();
			setterNode.replaceChild(1, paramsNode);
			setterNode.addChild(bodyNode);
		}
		// FIXME If value is a function, drop the name value pair node and just add the function to the parent
		// JSObjectNode or JSStatementsNode?
		else if (propertyNode.getValue() instanceof FunctionNode)
		{
			JSNameValuePairNode pairNode = (JSNameValuePairNode) getCurrentNode();
			JSFunctionNode funcValue = (JSFunctionNode) pairNode.getValue();
			if (propertyNode.isStatic())
			{
				funcValue.setStatic();
			}
			IParseNode parent = pairNode.getParent();
			int numChildren = parent.getChildCount();
			parent.replaceChild(numChildren - 1, funcValue);
		}
		popNode();
		return super.leavePropertyNode(propertyNode);
	}

	@Override
	public boolean enterTryNode(TryNode tryNode)
	{
		addToParentAndPushNodeToStack(new JSTryNode());
		// if finally block is empty, push empty node for it. We rely on the fact that finally block would typically be
		// first visited child here. see leave for more
		if (tryNode.getFinallyBody() == null)
		{
			addChildToParent(new JSEmptyNode(tryNode.getBody().getFinish()));
		}
		return super.enterTryNode(tryNode);
	}

	@Override
	public Node leaveTryNode(TryNode tryNode)
	{
		// Add empty catch
		if (tryNode.getCatches().isEmpty())
		{
			addChildToParent(new JSEmptyNode(tryNode.getBody().getFinish()));
		}

		// Fix the ordering of the children!
		// this visits in order:
		// - finally body
		// - body
		// - catch blocks FIXME Do these get added as multiple children?
		JSTryNode ourTryNode = (JSTryNode) getCurrentNode();
		IParseNode[] children = ourTryNode.getChildren();
		IParseNode[] orderedChildren = new IParseNode[children.length];
		orderedChildren[0] = children[1];
		orderedChildren[1] = children[2];
		JSNode firstChild = (JSNode) children[0];
		// if finally block is not empty, wrap in JSFinallyNode
		if (!(firstChild instanceof JSEmptyNode))
		{
			firstChild = new JSFinallyNode(firstChild);
		}
		orderedChildren[2] = firstChild;
		ourTryNode.setChildren(orderedChildren);

		popNode();
		return super.leaveTryNode(tryNode);
	}

	@Override
	public boolean enterCatchNode(CatchNode catchNode)
	{
		addToParentAndPushNodeToStack(new JSCatchNode());
		return super.enterCatchNode(catchNode);
	}

	@Override
	public Node leaveCatchNode(CatchNode catchNode)
	{
		popNode();
		return super.leaveCatchNode(catchNode);
	}

	@Override
	public boolean enterSwitchNode(SwitchNode switchNode)
	{
		addToParentAndPushNodeToStack(new JSSwitchNode(null, null, null, null));
		return super.enterSwitchNode(switchNode);
	}

	@Override
	public Node leaveSwitchNode(SwitchNode switchNode)
	{
		popNode();
		return super.leaveSwitchNode(switchNode);
	}

	@Override
	public boolean enterCaseNode(CaseNode caseNode)
	{
		int offset = caseNode.getFinish() + 1;
		Symbol colon = toSymbol(JSTokenType.COLON, offset, offset + 1);
		if (caseNode.getTest() != null)
		{
			addToParentAndPushNodeToStack(new JSCaseNode(colon));
		}
		else
		{
			addToParentAndPushNodeToStack(new JSDefaultNode(colon));
		}
		return super.enterCaseNode(caseNode);
	}

	@Override
	public Node leaveCaseNode(CaseNode caseNode)
	{
		popNode();
		return super.leaveCaseNode(caseNode);
	}

	@Override
	public boolean enterWhileNode(WhileNode whileNode)
	{
		if (whileNode.isDoWhile())
		{
			addToParentAndPushNodeToStack(new JSDoNode(null, null));
		}
		else
		{
			addToParentAndPushNodeToStack(new JSWhileNode(null, null));
		}
		return super.enterWhileNode(whileNode);
	}

	@Override
	public Node leaveWhileNode(WhileNode whileNode)
	{
		popNode();
		return super.leaveWhileNode(whileNode);
	}

	@Override
	public boolean enterBreakNode(BreakNode breakNode)
	{
		String labelName = breakNode.getLabelName();
		JSBreakNode bn;
		if (labelName != null)
		{
			int start = breakNode.getFinish() + 2;
			int finish = start + labelName.length();
			bn = new JSBreakNode(toSymbol(JSTokenType.IDENTIFIER, start, finish));
		}
		else
		{
			bn = new JSBreakNode();
		}
		bn.setSemicolonIncluded(true);
		addToParentAndPushNodeToStack(bn);
		return super.enterBreakNode(breakNode);
	}

	@Override
	public Node leaveBreakNode(BreakNode breakNode)
	{
		popNode();
		return super.leaveBreakNode(breakNode);
	}

	@Override
	public boolean enterCallNode(CallNode callNode)
	{
		addToParentAndPushNodeToStack(new JSInvokeNode());
		// We need to visit the expression first, then push the arguments node...
		pushOnLeave.put(callNode.getFunction(), new JSArgumentsNode());
		return super.enterCallNode(callNode);
	}

	@Override
	public Node leaveCallNode(CallNode callNode)
	{
		popNode(); // arguments node
		popNode(); // invoke node
		return super.leaveCallNode(callNode);
	}

	@Override
	protected Node leaveDefault(Node node)
	{
		// System.out.println("Leaving node: " + node.getClass().getName() + ": " + node);
		if (pushOnLeave != null && pushOnLeave.containsKey(node))
		{
			JSNode toPush = pushOnLeave.remove(node);
			addToParentAndPushNodeToStack(toPush);
		}
		return super.leaveDefault(node);
	}

	@Override
	public boolean enterIfNode(IfNode ifNode)
	{
		addToParentAndPushNodeToStack(new JSIfNode(null, null));
		return super.enterIfNode(ifNode);
	}

	@Override
	public Node leaveIfNode(IfNode ifNode)
	{
		Block elseBlock = ifNode.getFail();
		if (elseBlock == null)
		{
			JSIfNode jsIfNode = (JSIfNode) getCurrentNode();
			int offset = jsIfNode.getTrueBlock().getEndingOffset();
			jsIfNode.addChild(new JSEmptyNode(offset));
		}
		popNode();
		return super.leaveIfNode(ifNode);
	}

	@Override
	public boolean enterAccessNode(AccessNode accessNode)
	{
		addToParentAndPushNodeToStack(new JSGetPropertyNode(null));
		return super.enterAccessNode(accessNode);
	}

	@Override
	public Node leaveAccessNode(AccessNode accessNode)
	{
		// manually add the property as an identifier
		int finish = accessNode.getFinish();
		String propertyName = accessNode.getProperty();
		int start = finish - propertyName.length();
		addChildToParent(new JSIdentifierNode(identifierSymbol(start, finish, propertyName)));
		popNode();
		return super.leaveAccessNode(accessNode);
	}

	@Override
	public boolean enterIndexNode(IndexNode indexNode)
	{
		addToParentAndPushNodeToStack(new JSGetElementNode(null, null));
		return super.enterIndexNode(indexNode);
	}

	@Override
	public Node leaveIndexNode(IndexNode indexNode)
	{
		popNode();
		return super.leaveIndexNode(indexNode);
	}

	@Override
	public boolean enterTernaryNode(TernaryNode ternaryNode)
	{
		addToParentAndPushNodeToStack(new JSConditionalNode(null, null));
		return super.enterTernaryNode(ternaryNode);
	}

	@Override
	public Node leaveTernaryNode(TernaryNode ternaryNode)
	{
		popNode();
		return super.leaveTernaryNode(ternaryNode);
	}

	@Override
	public boolean enterContinueNode(ContinueNode continueNode)
	{
		JSContinueNode cn = new JSContinueNode();
		String label = continueNode.getLabelName();
		if (label != null)
		{
			int start = continueNode.getFinish();
			int finish = start + label.length();
			cn = new JSContinueNode(toSymbol(JSTokenType.IDENTIFIER, start, finish, label));
		}
		cn.setSemicolonIncluded(true);
		addToParentAndPushNodeToStack(cn);
		return super.enterContinueNode(continueNode);
	}

	@Override
	public Node leaveContinueNode(ContinueNode continueNode)
	{
		popNode();
		return super.leaveContinueNode(continueNode);
	}

	@Override
	public boolean enterThrowNode(ThrowNode throwNode)
	{
		JSNode tn = new JSThrowNode();
		tn.setSemicolonIncluded(true);
		addToParentAndPushNodeToStack(tn);
		return super.enterThrowNode(throwNode);
	}

	@Override
	public Node leaveThrowNode(ThrowNode throwNode)
	{
		popNode();
		return super.leaveThrowNode(throwNode);
	}

	@Override
	public boolean enterLabelNode(LabelNode labelNode)
	{
		addToParentAndPushNodeToStack(
				new JSLabelledNode(new JSIdentifierNode(identifierSymbol(labelNode, labelNode.getLabelName())), null));
		return super.enterLabelNode(labelNode);
	}

	@Override
	public Node leaveLabelNode(LabelNode labelNode)
	{
		popNode();
		return super.leaveLabelNode(labelNode);
	}

	// @Override
	// protected boolean enterDefault(Node node)
	// {
	// System.out.println("Entering node: " + node.getClass().getName() + ": " + node);
	// return super.enterDefault(node);
	// }

	@Override
	public boolean enterWithNode(WithNode withNode)
	{
		addToParentAndPushNodeToStack(new JSWithNode(null, null));
		return super.enterWithNode(withNode);
	}

	@Override
	public Node leaveWithNode(WithNode withNode)
	{
		popNode();
		return super.leaveWithNode(withNode);
	}

	public IParseRootNode getRootNode()
	{
		return fRootNode;
	}

	private void addChildToParent(JSNode node)
	{
		IParseNode parent = getCurrentNode();
		if (parent != null)
		{
			parent.addChild(node);
		}
	}

	private void popNode()
	{
		fNodeStack.pop();
	}

	private IParseNode getLastNode()
	{
		IParseNode currentNode = getCurrentNode();
		if (currentNode == null)
		{
			return null;
		}
		return currentNode.getLastChild();
	}

	private IParseNode getCurrentNode()
	{
		if (fNodeStack.isEmpty())
		{
			return null;
		}
		return fNodeStack.peek();
	}

	private void addToParentAndPushNodeToStack(JSNode node)
	{
		addChildToParent(node);
		fNodeStack.push(node);
	}

	private ExportedStatus getExportStatus(IdentNode ident)
	{
		if (module != null)
		{
			List<ExportEntry> exports = module.getLocalExportEntries();
			for (ExportEntry entry : exports)
			{
				if (entry.getLocalName().equals(ident.getName()))
				{
					return new ExportedStatus(true, entry.getExportName().equals(Module.DEFAULT_NAME));
				}
			}
		}
		return new ExportedStatus(false, false);
	}

	private static class ExportedStatus
	{
		final boolean isExported;
		final boolean isDefault;

		public ExportedStatus(boolean isExported, boolean isDefault)
		{
			this.isExported = isExported;
			this.isDefault = isDefault;
		}
	}
}
