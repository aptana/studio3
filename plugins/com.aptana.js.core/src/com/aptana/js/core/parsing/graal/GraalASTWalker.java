package com.aptana.js.core.parsing.graal;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.aptana.js.core.parsing.JSTokenType;
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
import com.aptana.js.core.parsing.ast.JSConstructNode;
import com.aptana.js.core.parsing.ast.JSDeclarationNode;
import com.aptana.js.core.parsing.ast.JSDefaultNode;
import com.aptana.js.core.parsing.ast.JSEmptyNode;
import com.aptana.js.core.parsing.ast.JSFalseNode;
import com.aptana.js.core.parsing.ast.JSForInNode;
import com.aptana.js.core.parsing.ast.JSForNode;
import com.aptana.js.core.parsing.ast.JSForOfNode;
import com.aptana.js.core.parsing.ast.JSFunctionNode;
import com.aptana.js.core.parsing.ast.JSGetElementNode;
import com.aptana.js.core.parsing.ast.JSGetPropertyNode;
import com.aptana.js.core.parsing.ast.JSGroupNode;
import com.aptana.js.core.parsing.ast.JSIdentifierNode;
import com.aptana.js.core.parsing.ast.JSIfNode;
import com.aptana.js.core.parsing.ast.JSInvokeNode;
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
import com.aptana.js.core.parsing.ast.JSReturnNode;
import com.aptana.js.core.parsing.ast.JSStatementsNode;
import com.aptana.js.core.parsing.ast.JSStringNode;
import com.aptana.js.core.parsing.ast.JSSwitchNode;
import com.aptana.js.core.parsing.ast.JSTrueNode;
import com.aptana.js.core.parsing.ast.JSTryNode;
import com.aptana.js.core.parsing.ast.JSVarNode;
import com.aptana.js.core.parsing.ast.JSWhileNode;
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
import com.oracle.js.parser.ir.EmptyNode;
import com.oracle.js.parser.ir.Expression;
import com.oracle.js.parser.ir.ExpressionStatement;
import com.oracle.js.parser.ir.ForNode;
import com.oracle.js.parser.ir.FunctionNode;
import com.oracle.js.parser.ir.IdentNode;
import com.oracle.js.parser.ir.IfNode;
import com.oracle.js.parser.ir.IndexNode;
import com.oracle.js.parser.ir.LexicalContext;
import com.oracle.js.parser.ir.LiteralNode;
import com.oracle.js.parser.ir.Node;
import com.oracle.js.parser.ir.ObjectNode;
import com.oracle.js.parser.ir.PropertyNode;
import com.oracle.js.parser.ir.ReturnNode;
import com.oracle.js.parser.ir.SwitchNode;
import com.oracle.js.parser.ir.TryNode;
import com.oracle.js.parser.ir.UnaryNode;
import com.oracle.js.parser.ir.VarNode;
import com.oracle.js.parser.ir.WhileNode;
import com.oracle.js.parser.ir.visitor.NodeVisitor;

import beaver.Symbol;

public class GraalASTWalker extends NodeVisitor<LexicalContext>
{

	private IParseRootNode fRootNode;
	private Stack<IParseNode> fNodeStack = new Stack<IParseNode>();
	private boolean wipeNextIdent;
	private Map<Expression, JSNode> pushOnLeave;
	private final String source;

	public GraalASTWalker(String source, LexicalContext lc)
	{
		super(lc);
		this.source = source;
		fRootNode = new JSParseRootNode();
		fNodeStack.push(fRootNode);
		wipeNextIdent = false;
		pushOnLeave = new HashMap<Expression, JSNode>();
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
			addChildToParent(new JSNumberNode(toSymbol(literalNode)));
		}
		else if (literalNode.isNull())
		{
			addChildToParent(new JSNullNode(toSymbol(literalNode)));
		}
		else if (literalNode.isString())
		{
			// Peek at offset before literal to sniff the single/double quotes
			int start = literalNode.getStart() - 1;
			int finish = start + literalNode.getFinish() + 1;
			String value = literalNode.getString();
			char c = source.charAt(start);
			value = c + value + c;
			addChildToParent(new JSStringNode(toSymbol(JSTokenType.STRING, start, finish, value)));
		}
		else if (literalNode.isBoolean())
		{
			if (literalNode.getBoolean())
			{
				addChildToParent(new JSTrueNode(toSymbol(literalNode)));
			}
			else
			{
				addChildToParent(new JSFalseNode(toSymbol(literalNode)));
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
			addChildToParent(new JSIdentifierNode(toSymbol(identNode)));
		}
		return super.enterIdentNode(identNode);
	}

	private Symbol toSymbol(Node ident)
	{
		return new Symbol((short) 0, ident.getStart(), ident.getFinish() - 1, ident.toString());
	}

	private Symbol toSymbol(JSTokenType type, Node ident)
	{
		return toSymbol(type, ident.getStart(), ident.getFinish() - 1);
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
			JSNode node = new JSVarNode(var);
			node.setSemicolonIncluded(true);
			addToParentAndPushNodeToStack(node);
			addToParentAndPushNodeToStack(new JSDeclarationNode(null));
		}
		else if (varNode.getInit() instanceof ClassNode)
		{
			// class decl
			IdentNode name = varNode.getName();
			ClassNode classNode = (ClassNode) varNode.getInit();
			addToParentAndPushNodeToStack(new JSClassNode(name != null, classNode.getClassHeritage() != null));
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
		}
		else if (varNode.getInit() instanceof ClassNode)
		{
			// Swap order of body and name
			// FIXME handle when there's a superclass! Should be "middle" child
			IParseNode node = getCurrentNode();
			IParseNode body = node.getChild(0);
			IParseNode name = node.getChild(1);
			((JSNode) node).setChildren(new IParseNode[] { name, body });

			popNode(); // class node
		}

		return super.leaveVarNode(varNode);
	}

	@Override
	public boolean enterFunctionNode(FunctionNode functionNode)
	{
		if (!functionNode.isProgram())
		{
			addToParentAndPushNodeToStack(new JSFunctionNode());
			// Visit the name
			IdentNode ident = functionNode.getIdent();
			addChildToParent(new JSIdentifierNode(toSymbol(ident)));
			// Need to explicitly visit the params
			addToParentAndPushNodeToStack(new JSParametersNode());
			functionNode.visitParameters(this);
			popNode();
		}
		return super.enterFunctionNode(functionNode);
	}

	@Override
	public Node leaveFunctionNode(FunctionNode functionNode)
	{
		if (!functionNode.isProgram())
		{

			popNode();
			// when the function node is the "init" of a parent VarNode, we need to avoid hitting the "name" IdentNode.
			if (!functionNode.isAnonymous())
			{
				wipeNextIdent = true;
			}
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
			case ELLIPSIS:
				break;
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
		}
		addToParentAndPushNodeToStack(theNode);
		return super.enterUnaryNode(unaryNode);
	}

	@Override
	public Node leaveUnaryNode(UnaryNode unaryNode)
	{
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
			// case ASSIGN_EXP:
			// type = JSTokenType.STAR_STAR_EQUAL;
			// theNode = new JSAssignmentNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
			// break;
			//
			default:
				type = JSTokenType.UNDEFINED;
				theNode = new JSBinaryBooleanOperatorNode(new Symbol(type.getIndex(), 0, 0, type.getName()));
				break;
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
	public boolean enterExpressionStatement(ExpressionStatement expressionStatement)
	{
		JSNode node = new JSCommaNode();
		node.setSemicolonIncluded(true);
		addToParentAndPushNodeToStack(node);
		return super.enterExpressionStatement(expressionStatement);
	}

	@Override
	public Node leaveExpressionStatement(ExpressionStatement expressionStatement)
	{
		popNode();
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

		if (!block.isSynthetic())
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
			if (fN.getInit() == null)
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
		addToParentAndPushNodeToStack(new JSStatementsNode()); // push statements node for body
		return super.enterClassNode(classNode);
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
		if (theNode.getChildCount() != 4)
		{
			// Inject empty nodes for missing test/increment expressions!
			IParseNode[] newChildren = new IParseNode[4];
			newChildren[0] = theNode.getFirstChild(); // we always inject an empty node if necessary for init expression
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
			((JSNode) theNode).setChildren(newChildren);
		}
		popNode();
		return super.leaveForNode(forNode);

	}

	@Override
	public boolean enterPropertyNode(PropertyNode propertyNode)
	{
		int offset = propertyNode.getKey().getFinish() + 2;
		Symbol colon = toSymbol(JSTokenType.COLON, offset, offset + 1);
		addToParentAndPushNodeToStack(new JSNameValuePairNode(colon));
		return super.enterPropertyNode(propertyNode);
	}

	@Override
	public Node leavePropertyNode(PropertyNode propertyNode)
	{
		popNode();
		return super.leavePropertyNode(propertyNode);
	}

	@Override
	public boolean enterTryNode(TryNode tryNode)
	{
		addToParentAndPushNodeToStack(new JSTryNode());
		return super.enterTryNode(tryNode);
	}

	@Override
	public Node leaveTryNode(TryNode tryNode)
	{
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
		addToParentAndPushNodeToStack(new JSWhileNode(null, null));
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
		if (pushOnLeave.containsKey(node))
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
		addChildToParent(new JSIdentifierNode(toSymbol(JSTokenType.IDENTIFIER, start, finish, propertyName)));
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
	protected boolean enterDefault(Node node)
	{
		System.out.println("Entering node: " + node.getClass().getName() + ": " + node);
		return super.enterDefault(node);
	}

	@Override
	public Node leaveIndexNode(IndexNode indexNode)
	{
		popNode();
		return super.leaveIndexNode(indexNode);
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

}
