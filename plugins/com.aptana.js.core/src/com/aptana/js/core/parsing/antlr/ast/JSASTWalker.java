package com.aptana.js.core.parsing.antlr.ast;

import java.util.Stack;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.aptana.js.core.parsing.JSTokenType;
import com.aptana.js.core.parsing.antlr.JSParser;
import com.aptana.js.core.parsing.antlr.JSParser.*;
import com.aptana.js.core.parsing.antlr.JSParserBaseListener;
import com.aptana.js.core.parsing.ast.JSArgumentsNode;
import com.aptana.js.core.parsing.ast.JSArrayNode;
import com.aptana.js.core.parsing.ast.JSArrowFunctionNode;
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
import com.aptana.js.core.parsing.ast.JSDestructuringNode;
import com.aptana.js.core.parsing.ast.JSDoNode;
import com.aptana.js.core.parsing.ast.JSElementsNode;
import com.aptana.js.core.parsing.ast.JSElisionNode;
import com.aptana.js.core.parsing.ast.JSEmptyNode;
import com.aptana.js.core.parsing.ast.JSErrorNode;
import com.aptana.js.core.parsing.ast.JSExportNode;
import com.aptana.js.core.parsing.ast.JSExportSpecifierNode;
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
import com.aptana.js.core.parsing.ast.JSNamedImportsNode;
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
import com.aptana.js.core.parsing.ast.JSThisNode;
import com.aptana.js.core.parsing.ast.JSThrowNode;
import com.aptana.js.core.parsing.ast.JSTrueNode;
import com.aptana.js.core.parsing.ast.JSTryNode;
import com.aptana.js.core.parsing.ast.JSVarNode;
import com.aptana.js.core.parsing.ast.JSWhileNode;
import com.aptana.js.core.parsing.ast.JSWithNode;
import com.aptana.js.core.parsing.ast.JSYieldNode;
import com.aptana.parsing.ast.IParseNode;

import beaver.Symbol;

public class JSASTWalker extends JSParserBaseListener
{
	private JSParseRootNode fRootNode;
	private Stack<IParseNode> fNodeStack = new Stack<IParseNode>();

	@Override
	public void enterProgram(ProgramContext ctx)
	{
		fRootNode = new JSParseRootNode();
		fNodeStack.push(fRootNode);
		super.enterProgram(ctx);
	}

	@Override
	public void enterBlock(BlockContext ctx)
	{
		addToParentAndPushNodeToStack(new JSStatementsNode());
		super.enterBlock(ctx);
	}

	@Override
	public void exitBlock(BlockContext ctx)
	{
		popNode();
		super.exitBlock(ctx);
	}

	@Override
	public void enterArguments(ArgumentsContext ctx)
	{
		addToParentAndPushNodeToStack(new JSArgumentsNode());
		super.enterArguments(ctx);
	}

	@Override
	public void exitArguments(ArgumentsContext ctx)
	{
		popNode();
		super.exitArguments(ctx);
	}

	@Override
	public void enterAssignmentExpression(AssignmentExpressionContext ctx)
	{
		// FIXME If we attempt to use this walker as a parse listener, the assign token won't be here yet!
		Symbol o = toSymbol(ctx.getToken(JSParser.Assign, 0));
		addToParentAndPushNodeToStack(new JSAssignmentNode(o));
		super.enterAssignmentExpression(ctx);
	}

	@Override
	public void exitAssignmentExpression(AssignmentExpressionContext ctx)
	{
		popNode();
		super.exitAssignmentExpression(ctx);
	}

	@Override
	public void enterThisExpression(ThisExpressionContext ctx)
	{
		// leaf node, no need to push to stack or pop later
		addChildToParent(new JSThisNode());
		super.enterThisExpression(ctx);
	}

	@Override
	public void enterSuperExpression(SuperExpressionContext ctx)
	{
		// leaf node, no need to push to stack or pop later
		// TODO Introduce JSSuperNode?
		// use start in preference to getToken()
		addChildToParent(new JSIdentifierNode(toSymbolWithText(ctx.start)));
		// addChildToParent(new JSIdentifierNode(toSymbol(ctx.getToken(JSParser.Super, 0))));
		super.enterSuperExpression(ctx);
	}

	@Override
	public void enterIdentifierExpression(IdentifierExpressionContext ctx)
	{
		// leaf node, no need to push to stack or pop later
		// start is available during parse, use in preference to calling for sub-context
		addChildToParent(new JSIdentifierNode(toSymbolWithText(ctx.start)));
		// addChildToParent(new JSIdentifierNode(toSymbol(ctx.Identifier())));
		super.enterIdentifierExpression(ctx);
	}

	@Override
	public void enterPreIncrementExpression(PreIncrementExpressionContext ctx)
	{
		handlePreUnaryOperator(ctx, JSParser.PlusPlus);
		super.enterPreIncrementExpression(ctx);
	}

	@Override
	public void exitPreIncrementExpression(PreIncrementExpressionContext ctx)
	{
		popNode();
		super.exitPreIncrementExpression(ctx);
	}

	@Override
	public void enterPreDecreaseExpression(PreDecreaseExpressionContext ctx)
	{
		handlePreUnaryOperator(ctx, JSParser.MinusMinus);
		super.enterPreDecreaseExpression(ctx);
	}

	@Override
	public void exitPreDecreaseExpression(PreDecreaseExpressionContext ctx)
	{
		popNode();
		super.exitPreDecreaseExpression(ctx);
	}

	@Override
	public void enterUnaryMinusExpression(UnaryMinusExpressionContext ctx)
	{
		handlePreUnaryOperator(ctx, JSParser.Minus);
		super.enterUnaryMinusExpression(ctx);
	}

	@Override
	public void exitUnaryMinusExpression(UnaryMinusExpressionContext ctx)
	{
		popNode();
		super.exitUnaryMinusExpression(ctx);
	}

	@Override
	public void enterUnaryPlusExpression(UnaryPlusExpressionContext ctx)
	{
		handlePreUnaryOperator(ctx, JSParser.Plus);
		super.enterUnaryPlusExpression(ctx);
	}

	@Override
	public void exitUnaryPlusExpression(UnaryPlusExpressionContext ctx)
	{
		popNode();
		super.exitUnaryPlusExpression(ctx);
	}

	@Override
	public void enterBitNotExpression(BitNotExpressionContext ctx)
	{
		handlePreUnaryOperator(ctx, JSParser.BitNot);
		super.enterBitNotExpression(ctx);
	}

	@Override
	public void exitBitNotExpression(BitNotExpressionContext ctx)
	{
		popNode();
		super.exitBitNotExpression(ctx);
	}

	@Override
	public void enterNotExpression(NotExpressionContext ctx)
	{
		handlePreUnaryOperator(ctx, JSParser.Not);
		super.enterNotExpression(ctx);
	}

	@Override
	public void exitNotExpression(NotExpressionContext ctx)
	{
		popNode();
		super.exitNotExpression(ctx);
	}

	@Override
	public void enterDeleteExpression(DeleteExpressionContext ctx)
	{
		handlePreUnaryOperator(ctx, JSParser.Delete);
		super.enterDeleteExpression(ctx);
	}

	@Override
	public void exitDeleteExpression(DeleteExpressionContext ctx)
	{
		popNode();
		super.exitDeleteExpression(ctx);
	}

	@Override
	public void enterVoidExpression(VoidExpressionContext ctx)
	{
		handlePreUnaryOperator(ctx, JSParser.Void);
		super.enterVoidExpression(ctx);
	}

	@Override
	public void exitVoidExpression(VoidExpressionContext ctx)
	{
		popNode();
		super.exitVoidExpression(ctx);
	}

	@Override
	public void enterTypeofExpression(TypeofExpressionContext ctx)
	{
		handlePreUnaryOperator(ctx, JSParser.Typeof);
		super.enterTypeofExpression(ctx);
	}

	@Override
	public void exitTypeofExpression(TypeofExpressionContext ctx)
	{
		popNode();
		super.exitTypeofExpression(ctx);
	}

	private void handlePreUnaryOperator(ParserRuleContext ctx, int type)
	{
		// start is available during parse, so use in preference to getToken.
		// start should always be the operator in preUnary
		addToParentAndPushNodeToStack(new JSPreUnaryOperatorNode(toSymbol(ctx.start)));
		// addToParentAndPushNodeToStack(new JSPreUnaryOperatorNode(toSymbol(ctx.getToken(type, 0))));
	}

	@Override
	public void enterPostIncrementExpression(PostIncrementExpressionContext ctx)
	{
		addToParentAndPushNodeToStack(new JSPostUnaryOperatorNode(toSymbol(ctx.getToken(JSParser.PlusPlus, 0))));
		super.enterPostIncrementExpression(ctx);
	}

	@Override
	public void exitPostIncrementExpression(PostIncrementExpressionContext ctx)
	{
		popNode();
		super.exitPostIncrementExpression(ctx);
	}

	@Override
	public void enterPostDecreaseExpression(PostDecreaseExpressionContext ctx)
	{
		addToParentAndPushNodeToStack(new JSPostUnaryOperatorNode(toSymbol(ctx.getToken(JSParser.MinusMinus, 0))));
		super.enterPostDecreaseExpression(ctx);
	}

	@Override
	public void exitPostDecreaseExpression(PostDecreaseExpressionContext ctx)
	{
		popNode();
		super.exitPostDecreaseExpression(ctx);
	}

	@Override
	public void enterAssignmentOperatorExpression(AssignmentOperatorExpressionContext ctx)
	{
		Symbol o = toSymbol(ctx.assignmentOperator().getStart());

		addToParentAndPushNodeToStack(new JSAssignmentNode(o));
		super.enterAssignmentOperatorExpression(ctx);
	}

	@Override
	public void enterExpressionSequence(ExpressionSequenceContext ctx)
	{
		JSNode node = new JSCommaNode();
		if (ctx.parent instanceof ExpressionStatementContext)
		{
			node.setSemicolonIncluded(true);
		}
		addToParentAndPushNodeToStack(node);
		super.enterExpressionSequence(ctx);
	}

	@Override
	public void exitExpressionSequence(ExpressionSequenceContext ctx)
	{
		popNode();
		super.exitExpressionSequence(ctx);
	}

	@Override
	public void enterAdditiveExpression(AdditiveExpressionContext ctx)
	{
		Symbol o = pickSymbol(ctx, 0, JSParser.Plus, JSParser.Minus);
		addToParentAndPushNodeToStack(new JSBinaryArithmeticOperatorNode(o));
		super.enterAdditiveExpression(ctx);
	}

	@Override
	public void exitAdditiveExpression(AdditiveExpressionContext ctx)
	{
		popNode();
		super.exitAdditiveExpression(ctx);
	}

	@Override
	public void enterBitShiftExpression(BitShiftExpressionContext ctx)
	{
		Symbol o = pickSymbol(ctx, 0, JSParser.LeftShiftArithmetic, JSParser.RightShiftArithmetic,
				JSParser.RightShiftLogical);
		addToParentAndPushNodeToStack(new JSBinaryArithmeticOperatorNode(o));
		super.enterBitShiftExpression(ctx);
	}

	@Override
	public void exitBitShiftExpression(BitShiftExpressionContext ctx)
	{
		popNode();
		super.exitBitShiftExpression(ctx);
	}

	@Override
	public void enterMultiplicativeExpression(MultiplicativeExpressionContext ctx)
	{
		Symbol o = pickSymbol(ctx, 0, JSParser.Multiply, JSParser.Divide, JSParser.Modulus);
		addToParentAndPushNodeToStack(new JSBinaryArithmeticOperatorNode(o));
		super.enterMultiplicativeExpression(ctx);
	}

	@Override
	public void exitMultiplicativeExpression(MultiplicativeExpressionContext ctx)
	{
		popNode();
		super.exitMultiplicativeExpression(ctx);
	}

	@Override
	public void enterInstanceofExpression(InstanceofExpressionContext ctx)
	{
		Symbol instance = toSymbol(ctx.getToken(JSParser.Instanceof, 0));
		addToParentAndPushNodeToStack(new JSBinaryBooleanOperatorNode(instance));
		super.enterInstanceofExpression(ctx);
	}

	@Override
	public void exitInstanceofExpression(InstanceofExpressionContext ctx)
	{
		popNode();
		super.exitInstanceofExpression(ctx);
	}

	@Override
	public void enterRelationalExpression(RelationalExpressionContext ctx)
	{
		Symbol o = pickSymbol(ctx, 0, JSParser.LessThan, JSParser.MoreThan, JSParser.LessThanEquals,
				JSParser.GreaterThanEquals);
		addToParentAndPushNodeToStack(new JSBinaryBooleanOperatorNode(o));
		super.enterRelationalExpression(ctx);
	}

	@Override
	public void exitRelationalExpression(RelationalExpressionContext ctx)
	{
		popNode();
		super.exitRelationalExpression(ctx);
	}

	@Override
	public void enterEqualityExpression(EqualityExpressionContext ctx)
	{
		Symbol o = pickSymbol(ctx, 0, JSParser.Equals, JSParser.NotEquals, JSParser.IdentityEquals,
				JSParser.IdentityNotEquals);
		addToParentAndPushNodeToStack(new JSBinaryBooleanOperatorNode(o));
		super.enterEqualityExpression(ctx);
	}

	@Override
	public void exitEqualityExpression(EqualityExpressionContext ctx)
	{
		popNode();
		super.exitEqualityExpression(ctx);
	}

	@Override
	public void enterBitAndExpression(BitAndExpressionContext ctx)
	{
		Symbol o = toSymbol(ctx.getToken(JSParser.BitAnd, 0));
		addToParentAndPushNodeToStack(new JSBinaryArithmeticOperatorNode(o));
		super.enterBitAndExpression(ctx);
	}

	@Override
	public void exitBitAndExpression(BitAndExpressionContext ctx)
	{
		popNode();
		super.exitBitAndExpression(ctx);
	}

	@Override
	public void enterBitXOrExpression(BitXOrExpressionContext ctx)
	{
		Symbol o = toSymbol(ctx.getToken(JSParser.BitXOr, 0));
		addToParentAndPushNodeToStack(new JSBinaryArithmeticOperatorNode(o));
		super.enterBitXOrExpression(ctx);
	}

	@Override
	public void exitBitXOrExpression(BitXOrExpressionContext ctx)
	{
		popNode();
		super.exitBitXOrExpression(ctx);
	}

	@Override
	public void enterBitOrExpression(BitOrExpressionContext ctx)
	{
		Symbol o = toSymbol(ctx.getToken(JSParser.BitOr, 0));
		addToParentAndPushNodeToStack(new JSBinaryArithmeticOperatorNode(o));
		super.enterBitOrExpression(ctx);
	}

	@Override
	public void exitBitOrExpression(BitOrExpressionContext ctx)
	{
		popNode();
		super.exitBitOrExpression(ctx);
	}

	@Override
	public void enterLogicalAndExpression(LogicalAndExpressionContext ctx)
	{
		Symbol o = toSymbol(ctx.getToken(JSParser.And, 0));
		addToParentAndPushNodeToStack(new JSBinaryBooleanOperatorNode(o));
		super.enterLogicalAndExpression(ctx);
	}

	@Override
	public void exitLogicalAndExpression(LogicalAndExpressionContext ctx)
	{
		popNode();
		super.exitLogicalAndExpression(ctx);
	}

	@Override
	public void enterLogicalOrExpression(LogicalOrExpressionContext ctx)
	{
		Symbol o = toSymbol(ctx.getToken(JSParser.Or, 0));
		addToParentAndPushNodeToStack(new JSBinaryBooleanOperatorNode(o));
		super.enterLogicalOrExpression(ctx);
	}

	@Override
	public void exitLogicalOrExpression(LogicalOrExpressionContext ctx)
	{
		popNode();
		super.exitLogicalOrExpression(ctx);
	}

	@Override
	public void enterRegularExpressionLiteralExpression(RegularExpressionLiteralExpressionContext ctx)
	{
		// leaf node
		addChildToParent(new JSRegexNode(ctx.getText()));
		super.enterRegularExpressionLiteralExpression(ctx);
	}

	@Override
	public void enterImportDeclaration(ImportDeclarationContext ctx)
	{
		if (ctx.exception == null)
		{
			ModuleSpecifierContext msc = ctx.moduleSpecifier();
			if (msc == null)
			{
				msc = ctx.fromClause().moduleSpecifier();
			}
			String s = msc.StringLiteral().getText();
			addToParentAndPushNodeToStack(new JSImportNode(s));
		}
		super.enterImportDeclaration(ctx);
	}

	@Override
	public void exitImportDeclaration(ImportDeclarationContext ctx)
	{
		if (ctx.exception == null)
		{
			popNode();
		}
		super.exitImportDeclaration(ctx);
	}

	@Override
	public void enterImportSpecifier(ImportSpecifierContext ctx)
	{
		addToParentAndPushNodeToStack(new JSImportSpecifierNode());
		super.enterImportSpecifier(ctx);
	}

	@Override
	public void exitImportSpecifier(ImportSpecifierContext ctx)
	{
		popNode();
		super.exitImportSpecifier(ctx);
	}

	@Override
	public void enterNameSpaceImport(NameSpaceImportContext ctx)
	{
		// Use start in preference to getToken()
		addToParentAndPushNodeToStack(new JSImportSpecifierNode(toSymbol(ctx.start)));
		// addToParentAndPushNodeToStack(new JSImportSpecifierNode(toSymbol(ctx.getToken(JSParser.Multiply, 0))));
		super.enterNameSpaceImport(ctx);
	}

	@Override
	public void exitNameSpaceImport(NameSpaceImportContext ctx)
	{
		popNode();
		super.exitNameSpaceImport(ctx);
	}

	@Override
	public void enterNamedImports(NamedImportsContext ctx)
	{
		addToParentAndPushNodeToStack(new JSNamedImportsNode());
		super.enterNamedImports(ctx);
	}

	@Override
	public void exitNamedImports(NamedImportsContext ctx)
	{
		popNode();
		super.exitNamedImports(ctx);
	}

	@Override
	public void enterEmptyStatement(EmptyStatementContext ctx)
	{
		// leaf node
		// use start in preference to getToken, should always just be a semicolon token
		JSNode node = new JSEmptyNode(toSymbol(ctx.start));
		// JSNode node = new JSEmptyNode(toSymbol(ctx.SemiColon()));
		node.setSemicolonIncluded(true);
		addChildToParent(node);
		super.enterEmptyStatement(ctx);
	}

	@Override
	public void enterArrayLiteral(ArrayLiteralContext ctx)
	{
		// Use start in preference to getToken for open bracket
		// faster, and shoudl always be the first token for this rule
		// JSArrayNode node = new JSArrayNode(toSymbol(ctx.getToken(JSParser.OpenBracket, 0)),
		JSArrayNode node = new JSArrayNode(toSymbol(ctx.start), toSymbol(ctx.getToken(JSParser.CloseBracket, 0)));
		addToParentAndPushNodeToStack(node);
		super.enterArrayLiteral(ctx);
	}

	@Override
	public void exitArrayLiteral(ArrayLiteralContext ctx)
	{
		// FIXME If there's a trailing elision, add it to the ElementList, not this array
		popNode();
		super.exitArrayLiteral(ctx);
	}

	@Override
	public void enterArrayBindingPattern(ArrayBindingPatternContext ctx)
	{
		// Use start in preference to getToken()
		JSArrayNode node = new JSArrayNode(toSymbol(ctx.start),
				// JSArrayNode node = new JSArrayNode(toSymbol(ctx.getToken(JSParser.OpenBracket, 0)),
				toSymbol(ctx.getToken(JSParser.CloseBracket, 0)));
		addToParentAndPushNodeToStack(node);
		super.enterArrayBindingPattern(ctx);
	}

	@Override
	public void exitArrayBindingPattern(ArrayBindingPatternContext ctx)
	{
		popNode();
		super.exitArrayBindingPattern(ctx);
	}

	@Override
	public void enterElementList(ElementListContext ctx)
	{
		addToParentAndPushNodeToStack(new JSElementsNode());
		super.enterElementList(ctx);
	}

	@Override
	public void exitElementList(ElementListContext ctx)
	{
		popNode();
		super.exitElementList(ctx);
	}

	@Override
	public void enterElision(ElisionContext ctx)
	{
		// Add a JSNullNode for each comma!
		JSElisionNode elisionNode = new JSElisionNode();
		for (int i = 0; i < ctx.getChildCount(); i++)
		{
			elisionNode.addChild(new JSNullNode());
		}
		addChildToParent(elisionNode);
		super.enterElision(ctx);
	}

	@Override
	public void enterLiteral(LiteralContext ctx)
	{
		// Always a leaf node, so just add to parent node, don't push to stack
		Token t = ctx.getStart();
		switch (t.getType())
		{
			case JSParser.NullLiteral:
				addChildToParent(new JSNullNode());
				break;
			case JSParser.StringLiteral:
				addChildToParent(new JSStringNode(t.getText()));
				break;
			case JSParser.BooleanLiteral:
				String text = t.getText();
				if ("true".equalsIgnoreCase(text)) //$NON-NLS-1$
				{
					addChildToParent(new JSTrueNode());
				}
				else
				{
					addChildToParent(new JSFalseNode());
				}
				break;
			default:
				// numericLiteral is handled by #enterNumericLiteral
				break;
		}
		super.enterLiteral(ctx);
	}

	@Override
	public void enterNumericLiteral(NumericLiteralContext ctx)
	{
		addChildToParent(new JSNumberNode(ctx.getText()));
		super.enterNumericLiteral(ctx);
	}

	@Override
	public void enterVariableDeclarationStatement(VariableDeclarationStatementContext ctx)
	{
		Symbol var = toSymbol(ctx.start);
		// start is available in post-parse or during parse, use that in preference to getToken(), which only works when
		// used as post-parse walker
		// Symbol var = toSymbol(ctx.getToken(JSParser.Var, 0));
		JSNode node = new JSVarNode(var);
		if (ctx.parent instanceof VariableStatementContext)
		{
			node.setSemicolonIncluded(true);
		}
		addToParentAndPushNodeToStack(node);
		super.enterVariableDeclarationStatement(ctx);
	}

	@Override
	public void exitVariableDeclarationStatement(VariableDeclarationStatementContext ctx)
	{
		popNode();
		super.exitVariableDeclarationStatement(ctx);
	}

	@Override
	public void enterVariableDeclaration(VariableDeclarationContext ctx)
	{
		enterVarDecl(ctx.initializer());
		super.enterVariableDeclaration(ctx);
	}

	@Override
	public void exitVariableDeclaration(VariableDeclarationContext ctx)
	{
		popNode();
		super.exitVariableDeclaration(ctx);
	}

	/**
	 * For LexicalBinding and VariableDeclaration
	 * 
	 * @param ic
	 */
	private void enterVarDecl(InitializerContext ic)
	{
		Symbol equalSign = null;
		if (ic != null)
		{
			TerminalNode t = ic.getToken(JSParser.Assign, 0);
			if (t != null)
			{
				equalSign = toSymbol(t);
			}
		}
		addToParentAndPushNodeToStack(new JSDeclarationNode(equalSign));
	}

	@Override
	public void enterLexicalBinding(LexicalBindingContext ctx)
	{
		enterVarDecl(ctx.initializer());
		super.enterLexicalBinding(ctx);
	}

	@Override
	public void exitLexicalBinding(LexicalBindingContext ctx)
	{
		popNode();
		super.exitLexicalBinding(ctx);
	}

	@Override
	public void enterBindingPattern(BindingPatternContext ctx)
	{
		addToParentAndPushNodeToStack(new JSDestructuringNode());
		super.enterBindingPattern(ctx);
	}

	@Override
	public void exitBindingPattern(BindingPatternContext ctx)
	{
		popNode();
		super.exitBindingPattern(ctx);
	}

	@Override
	public void enterBindingIdentifier(BindingIdentifierContext ctx)
	{
		Symbol i = toSymbolWithText(ctx.start);
		// start is available in post-parse or during parse, use that in preference to getToken(), which only works when
		// used as post-parse walker
		// Symbol i = toSymbol(ctx.Identifier());
		addChildToParent(new JSIdentifierNode(i));
		super.enterBindingIdentifier(ctx);
	}

	@Override
	public void enterLexicalDeclaration(LexicalDeclarationContext ctx)
	{
		Symbol v = toSymbol(ctx.letOrConst().getStart());
		JSNode node = new JSVarNode(v);
		if (!(ctx.getParent() instanceof ForLexicalLoopStatementContext))
		{
			node.setSemicolonIncluded(true);
		}
		addToParentAndPushNodeToStack(node);
		super.enterLexicalDeclaration(ctx);
	}

	@Override
	public void exitLexicalDeclaration(LexicalDeclarationContext ctx)
	{
		popNode();
		super.exitLexicalDeclaration(ctx);
	}

	@Override
	public void enterIfStatement(IfStatementContext ctx)
	{
		Symbol leftParenthesis = toSymbol(ctx.getToken(JSParser.OpenParen, 0));
		Symbol rightParenthesis = toSymbol(ctx.getToken(JSParser.CloseParen, 0));

		addToParentAndPushNodeToStack(new JSIfNode(leftParenthesis, rightParenthesis));
		super.enterIfStatement(ctx);
	}

	@Override
	public void exitIfStatement(IfStatementContext ctx)
	{
		// If no else, add JSEmptyNode as child to JSIfNode!
		TerminalNode els = ctx.Else();
		if (els == null)
		{
			addChildToParent(new JSEmptyNode(ctx.statement(0).getStart().getStartIndex()));
		}
		popNode();
		super.exitIfStatement(ctx);
	}

	@Override
	public void enterWithStatement(WithStatementContext ctx)
	{
		Symbol l = toSymbol(ctx.getToken(JSParser.OpenParen, 0));
		Symbol r = toSymbol(ctx.getToken(JSParser.CloseParen, 0));
		addToParentAndPushNodeToStack(new JSWithNode(l, r));
		super.enterWithStatement(ctx);
	}

	@Override
	public void exitWithStatement(WithStatementContext ctx)
	{
		popNode();
		super.exitWithStatement(ctx);
	}

	public void enterSwitchStatement(SwitchStatementContext ctx)
	{
		Symbol lp = toSymbol(ctx.getToken(JSParser.OpenParen, 0));
		// If no expression is inside the parens, this may return null (it got assigned to the
		// expressionSequenceContext)
		Symbol rp = toSymbolOrNull(ctx.getToken(JSParser.CloseParen, 0));
		Symbol lb = null;
		Symbol rb = null;
		if (ctx.caseBlock() != null)
		{
			lb = toSymbolOrNull(ctx.caseBlock().getToken(JSParser.OpenBrace, 0));
			rb = toSymbolOrNull(ctx.caseBlock().getToken(JSParser.CloseBrace, 0));
		}
		addToParentAndPushNodeToStack(new JSSwitchNode(lp, rp, lb, rb));
		super.enterSwitchStatement(ctx);
	}

	@Override
	public void exitSwitchStatement(SwitchStatementContext ctx)
	{
		popNode();
		super.exitSwitchStatement(ctx);
	}

	@Override
	public void enterDefaultClause(DefaultClauseContext ctx)
	{
		Symbol c = toSymbol(ctx.getToken(JSParser.Colon, 0));
		addToParentAndPushNodeToStack(new JSDefaultNode(c));
		super.enterDefaultClause(ctx);
	}

	@Override
	public void exitDefaultClause(DefaultClauseContext ctx)
	{
		popNode();
		super.exitDefaultClause(ctx);
	}

	@Override
	public void enterCaseClause(CaseClauseContext ctx)
	{
		Symbol c = toSymbol(ctx.getToken(JSParser.Colon, 0));
		addToParentAndPushNodeToStack(new JSCaseNode(c));
		super.enterCaseClause(ctx);
	}

	@Override
	public void exitCaseClause(CaseClauseContext ctx)
	{
		popNode();
		super.exitCaseClause(ctx);
	}

	@Override
	public void enterReturnStatement(ReturnStatementContext ctx)
	{
		JSNode node = new JSReturnNode();
		node.setSemicolonIncluded(true);
		addToParentAndPushNodeToStack(node);
		super.enterReturnStatement(ctx);
	}

	@Override
	public void exitReturnStatement(ReturnStatementContext ctx)
	{
		// If there's no child to return node, add JSEmptyNode!
		if (getCurrentNode().getChildCount() == 0)
		{
			// Use start because it's faster, should always be return keyword
			Symbol r = toSymbol(ctx.start);
			// Symbol r = toSymbol(ctx.getToken(JSParser.Return, 0));
			addChildToParent(new JSEmptyNode(r));
		}
		popNode();
		super.exitReturnStatement(ctx);
	}

	@Override
	public void enterFunctionDeclaration(FunctionDeclarationContext ctx)
	{
		addToParentAndPushNodeToStack(new JSFunctionNode());
		super.enterFunctionDeclaration(ctx);
	}

	@Override
	public void exitFunctionDeclaration(FunctionDeclarationContext ctx)
	{
		popNode();
		super.exitFunctionDeclaration(ctx);
	}

	@Override
	public void enterBindingElement(BindingElementContext ctx)
	{
		enterVarDecl(ctx.initializer());
		super.enterBindingElement(ctx);
	}

	@Override
	public void exitBindingElement(BindingElementContext ctx)
	{
		popNode();
		super.exitBindingElement(ctx);
	}

	@Override
	public void enterFormalParameters(FormalParametersContext ctx)
	{
		addToParentAndPushNodeToStack(new JSParametersNode());
		super.enterFormalParameters(ctx);
	}

	@Override
	public void exitFormalParameters(FormalParametersContext ctx)
	{
		popNode();
		super.exitFormalParameters(ctx);
	}

	@Override
	public void enterFunctionStatementList(FunctionStatementListContext ctx)
	{
		addToParentAndPushNodeToStack(new JSStatementsNode());
		super.enterFunctionStatementList(ctx);
	}

	@Override
	public void exitFunctionStatementList(FunctionStatementListContext ctx)
	{
		popNode();
		super.exitFunctionStatementList(ctx);
	}

	@Override
	public void enterArgumentsExpression(ArgumentsExpressionContext ctx)
	{
		addToParentAndPushNodeToStack(new JSInvokeNode());
		super.enterArgumentsExpression(ctx);
	}

	@Override
	public void exitArgumentsExpression(ArgumentsExpressionContext ctx)
	{
		popNode();
		super.exitArgumentsExpression(ctx);
	}

	@Override
	public void enterNewExpression(NewExpressionContext ctx)
	{
		addToParentAndPushNodeToStack(new JSConstructNode());
		super.enterNewExpression(ctx);
	}

	@Override
	public void exitNewExpression(NewExpressionContext ctx)
	{
		popNode();
		super.exitNewExpression(ctx);
	}

	@Override
	public void enterMemberDotExpression(MemberDotExpressionContext ctx)
	{
		Symbol o = toSymbol(ctx.getToken(JSParser.Dot, 0));
		addToParentAndPushNodeToStack(new JSGetPropertyNode(o));
		super.enterMemberDotExpression(ctx);
	}

	@Override
	public void exitMemberDotExpression(MemberDotExpressionContext ctx)
	{
		popNode();
		super.exitMemberDotExpression(ctx);
	}

	@Override
	public void enterIdentifierName(IdentifierNameContext ctx)
	{
		// leaf node
		IdentifierContext ident = ctx.identifier();
		if (ident == null)
		{
			// Could be a reservedWord!
			ReservedWordContext rwc = ctx.reservedWord();
			if (rwc != null)
			{
				addChildToParent(new JSIdentifierNode(toSymbol(ctx.reservedWord().getStart())));
			}
			else
			{
				// assume it was supposed to be an identifier name here, but was empty!
				addChildToParent(new JSEmptyNode(ctx.getStart().getStartIndex()));
			}
		}
		else
		{
			addChildToParent(new JSIdentifierNode(toSymbolWithText(ident.start)));
		}
		super.enterIdentifierName(ctx);
	}

	@Override
	public void enterIdentifierReference(IdentifierReferenceContext ctx)
	{
		// leaf node
		// Use start in preference to getting sub-content
		addChildToParent(new JSIdentifierNode(toSymbolWithText(ctx.start)));
		// addChildToParent(new JSIdentifierNode(toSymbol(ctx.Identifier())));
		super.enterIdentifierReference(ctx);
	}

	@Override
	public void enterMemberIndexExpression(MemberIndexExpressionContext ctx)
	{
		Symbol lb = toSymbol(ctx.getToken(JSParser.OpenBracket, 0));
		Symbol rb = toSymbol(ctx.getToken(JSParser.CloseBracket, 0));
		addToParentAndPushNodeToStack(new JSGetElementNode(lb, rb));
		super.enterMemberIndexExpression(ctx);
	}

	@Override
	public void exitMemberIndexExpression(MemberIndexExpressionContext ctx)
	{
		popNode();
		super.exitMemberIndexExpression(ctx);
	}

	@Override
	public void enterThrowStatement(ThrowStatementContext ctx)
	{
		JSNode node = new JSThrowNode();
		node.setSemicolonIncluded(true);
		addToParentAndPushNodeToStack(node);
		super.enterThrowStatement(ctx);
	}

	@Override
	public void exitThrowStatement(ThrowStatementContext ctx)
	{
		popNode();
		super.exitThrowStatement(ctx);
	}

	@Override
	public void enterTryStatement(TryStatementContext ctx)
	{
		addToParentAndPushNodeToStack(new JSTryNode());
		super.enterTryStatement(ctx);
	}

	@Override
	public void exitTryStatement(TryStatementContext ctx)
	{
		popNode();
		super.exitTryStatement(ctx);
	}

	@Override
	public void enterCatchProduction(CatchProductionContext ctx)
	{
		addToParentAndPushNodeToStack(new JSCatchNode());
		super.enterCatchProduction(ctx);
	}

	@Override
	public void exitCatchProduction(CatchProductionContext ctx)
	{
		popNode();
		super.exitCatchProduction(ctx);
	}

	@Override
	public void enterFinallyProduction(FinallyProductionContext ctx)
	{
		addToParentAndPushNodeToStack(new JSFinallyNode());
		super.enterFinallyProduction(ctx);
	}

	@Override
	public void exitFinallyProduction(FinallyProductionContext ctx)
	{
		popNode();
		super.exitFinallyProduction(ctx);
	}

	@Override
	public void enterBreakStatement(BreakStatementContext ctx)
	{
		JSNode breakNode = new JSBreakNode();
		breakNode.setSemicolonIncluded(true);
		addToParentAndPushNodeToStack(breakNode);
		super.enterBreakStatement(ctx);
	}

	@Override
	public void exitBreakStatement(BreakStatementContext ctx)
	{
		popNode();
		super.exitBreakStatement(ctx);
	}

	@Override
	public void enterContinueStatement(ContinueStatementContext ctx)
	{
		JSNode node = new JSContinueNode();
		node.setSemicolonIncluded(true);
		addToParentAndPushNodeToStack(node);
		super.enterContinueStatement(ctx);
	}

	@Override
	public void exitContinueStatement(ContinueStatementContext ctx)
	{
		popNode();
		super.exitContinueStatement(ctx);
	}

	@Override
	public void enterDoWhileStatement(DoWhileStatementContext ctx)
	{
		Symbol l = toSymbol(ctx.getToken(JSParser.OpenParen, 0));
		Symbol r = toSymbol(ctx.getToken(JSParser.CloseParen, 0));
		JSNode node = new JSDoNode(l, r);
		node.setSemicolonIncluded(true);
		addToParentAndPushNodeToStack(node);
		super.enterDoWhileStatement(ctx);
	}

	@Override
	public void exitDoWhileStatement(DoWhileStatementContext ctx)
	{
		popNode();
		super.exitDoWhileStatement(ctx);
	}

	@Override
	public void enterWhileStatement(WhileStatementContext ctx)
	{
		Symbol l = toSymbol(ctx.getToken(JSParser.OpenParen, 0));
		Symbol r = toSymbol(ctx.getToken(JSParser.CloseParen, 0));
		addToParentAndPushNodeToStack(new JSWhileNode(l, r));
		super.enterWhileStatement(ctx);
	}

	@Override
	public void exitWhileStatement(WhileStatementContext ctx)
	{
		popNode();
		super.exitWhileStatement(ctx);
	}

	@Override
	public void enterForLoopStatement(ForLoopStatementContext ctx)
	{
		// JSNode expr1 = e1;
		// if (expr1 == null) {
		// expr1 = new JSEmptyNode(l);
		// }
		// JSNode expr2 = e2;
		// if (expr2 == null) {
		// expr2 = new JSEmptyNode(s1);
		// }
		// JSNode expr3 = e3;
		// if (expr3 == null) {
		// expr3 = new JSEmptyNode(s2);
		// }
		// return new JSForNode(l, expr1, s1, expr2, s2, expr3, r, s);

		Symbol l = toSymbol(ctx.getToken(JSParser.OpenParen, 0));
		Symbol r = toSymbol(ctx.getToken(JSParser.CloseParen, 0));
		Symbol s1 = toSymbol(ctx.getToken(JSParser.SemiColon, 0));
		Symbol s2 = toSymbol(ctx.getToken(JSParser.SemiColon, 1));
		JSNode node = new JSForNode(l, s1, s2, r);
		addToParentAndPushNodeToStack(node);

		// if first expression is empty, add an empty node in it's place
		if (!(ctx.getChild(2) instanceof ExpressionSequenceContext))
		{
			// missing first expression sequence (after var decl;)
			node.addChild(new JSEmptyNode(l)); // use location of l
		}

		super.enterForLoopStatement(ctx);
	}

	@Override
	public void exitForLoopStatement(ForLoopStatementContext ctx)
	{
		boolean missingFirstExpr = (!(ctx.getChild(2) instanceof ExpressionSequenceContext));
		boolean missingSecondExpr = false;
		if (missingFirstExpr)
		{
			missingSecondExpr = (!(ctx.getChild(3) instanceof ExpressionSequenceContext));
		}
		else
		{
			missingSecondExpr = (!(ctx.getChild(4) instanceof ExpressionSequenceContext));
		}
		boolean missingThirdExpr = (!(ctx.getChild(ctx.getChildCount() - 3) instanceof ExpressionSequenceContext));

		// Fill in missing expressions with JSEmptyNodes!
		if (missingSecondExpr || missingThirdExpr)
		{
			JSForNode forNode = (JSForNode) getCurrentNode();
			IParseNode[] children = forNode.getChildren();
			IParseNode[] newChildren = new IParseNode[4];
			newChildren[0] = children[0]; // we always have first expression, because we inject in enter method if empty
			if (missingSecondExpr)
			{
				Symbol s1 = toSymbol(ctx.getToken(JSParser.SemiColon, 0));
				newChildren[1] = new JSEmptyNode(s1); // use location of s1
			}
			else
			{
				newChildren[1] = children[1];
			}
			if (missingThirdExpr)
			{
				Symbol s2 = toSymbol(ctx.getToken(JSParser.SemiColon, 1));
				newChildren[2] = new JSEmptyNode(s2); // use location of s2
			}
			else
			{
				newChildren[2] = children[children.length - 2];
			}
			newChildren[3] = children[children.length - 1]; // always have body statement
			forNode.setChildren(newChildren);
		}

		popNode();
		super.exitForLoopStatement(ctx);
	}

	@Override
	public void enterForVarLoopStatement(ForVarLoopStatementContext ctx)
	{
		Symbol l = toSymbol(ctx.getToken(JSParser.OpenParen, 0));
		Symbol r = toSymbol(ctx.getToken(JSParser.CloseParen, 0));
		Symbol s1 = toSymbol(ctx.getToken(JSParser.SemiColon, 0));
		Symbol s2 = toSymbol(ctx.getToken(JSParser.SemiColon, 1));
		JSNode forNode = new JSForNode(l, s1, s2, r);
		addToParentAndPushNodeToStack(forNode);
		super.enterForVarLoopStatement(ctx);
	}

	@Override
	public void exitForVarLoopStatement(ForVarLoopStatementContext ctx)
	{
		// Fill in missing expressions with JSEmptyNodes!
		boolean missingFirstExpr = false;
		boolean missingSecondExpr = false;
		if (!(ctx.getChild(4) instanceof ExpressionSequenceContext))
		{
			// missing first expression sequence (after var decl;)
			missingFirstExpr = true;
		}

		if (!(ctx.getChild(ctx.getChildCount() - 3) instanceof ExpressionSequenceContext))
		{
			// missing second expression sequence (before ')' statement)
			missingSecondExpr = true;
		}

		if (missingFirstExpr || missingSecondExpr)
		{
			JSForNode forNode = (JSForNode) getCurrentNode();
			IParseNode[] children = forNode.getChildren();
			IParseNode[] newChildren = new IParseNode[4];
			newChildren[0] = children[0]; // we always have first var decl
			if (missingFirstExpr)
			{
				Symbol s1 = toSymbol(ctx.getToken(JSParser.SemiColon, 0));
				newChildren[1] = new JSEmptyNode(s1); // use location of s1
			}
			else
			{
				newChildren[1] = children[1];
			}
			if (missingSecondExpr)
			{
				Symbol s2 = toSymbol(ctx.getToken(JSParser.SemiColon, 1));
				newChildren[2] = new JSEmptyNode(s2); // use location of s2
			}
			else
			{
				newChildren[2] = children[children.length - 2];
			}
			newChildren[3] = children[children.length - 1]; // always have body
			forNode.setChildren(newChildren);
		}

		popNode();
		super.exitForVarLoopStatement(ctx);
	}

	@Override
	public void enterForLexicalLoopStatement(ForLexicalLoopStatementContext ctx)
	{
		Symbol l = toSymbol(ctx.getToken(JSParser.OpenParen, 0));
		Symbol r = toSymbol(ctx.getToken(JSParser.CloseParen, 0));
		Symbol s1 = toSymbol(ctx.getToken(JSParser.SemiColon, 0));
		// TODO Can we grab the semicolon from the lexical declaration to inject here?
		JSNode forNode = new JSForNode(l, null, s1, r);
		addToParentAndPushNodeToStack(forNode);
		super.enterForLexicalLoopStatement(ctx);
	}

	@Override
	public void exitForLexicalLoopStatement(ForLexicalLoopStatementContext ctx)
	{
		// Fill in missing expressions with JSEmptyNodes!
		boolean missingFirstExpr = false;
		boolean missingSecondExpr = false;
		if (!(ctx.getChild(3) instanceof ExpressionSequenceContext))
		{
			// missing first expression sequence (after lexicalDecl)
			missingFirstExpr = true;
		}

		if (!(ctx.getChild(ctx.getChildCount() - 3) instanceof ExpressionSequenceContext))
		{
			// missing second expression sequence (before ')' statement)
			missingSecondExpr = true;
		}

		if (missingFirstExpr || missingSecondExpr)
		{
			JSForNode forNode = (JSForNode) getCurrentNode();
			IParseNode[] children = forNode.getChildren();
			IParseNode[] newChildren = new IParseNode[4];
			newChildren[0] = children[0]; // we always have first var decl
			if (missingFirstExpr)
			{
				Symbol s1 = toSymbol(ctx.lexicalDeclaration().getStop());
				newChildren[1] = new JSEmptyNode(s1); // use end location of lexical declaration
			}
			else
			{
				newChildren[1] = children[1];
			}
			if (missingSecondExpr)
			{
				Symbol s1 = toSymbol(ctx.getToken(JSParser.SemiColon, 0));
				newChildren[2] = new JSEmptyNode(s1); // use location of s1
			}
			else
			{
				newChildren[2] = children[children.length - 2];
			}
			newChildren[3] = children[children.length - 1]; // always have body
			forNode.setChildren(newChildren);
		}

		popNode();
		super.exitForLexicalLoopStatement(ctx);
	}

	@Override
	public void enterForInStatement(ForInStatementContext ctx)
	{
		Symbol l = toSymbol(ctx.getToken(JSParser.OpenParen, 0));
		Symbol in = toSymbol(ctx.getToken(JSParser.In, 0));
		Symbol r = toSymbol(ctx.getToken(JSParser.CloseParen, 0));
		addToParentAndPushNodeToStack(new JSForInNode(l, in, r));
		super.enterForInStatement(ctx);
	}

	@Override
	public void exitForInStatement(ForInStatementContext ctx)
	{
		popNode();
		super.exitForInStatement(ctx);
	}

	@Override
	public void enterForVarInStatement(ForVarInStatementContext ctx)
	{
		Symbol l = toSymbol(ctx.getToken(JSParser.OpenParen, 0));
		Symbol in = toSymbol(ctx.getToken(JSParser.In, 0));
		Symbol r = toSymbol(ctx.getToken(JSParser.CloseParen, 0));
		addToParentAndPushNodeToStack(new JSForInNode(l, in, r));
		super.enterForVarInStatement(ctx);
	}

	@Override
	public void exitForVarInStatement(ForVarInStatementContext ctx)
	{
		popNode();
		super.exitForVarInStatement(ctx);
	}

	@Override
	public void enterForOfStatement(ForOfStatementContext ctx)
	{
		Symbol l = toSymbol(ctx.getToken(JSParser.OpenParen, 0));
		Symbol r = toSymbol(ctx.getToken(JSParser.CloseParen, 0));
		addToParentAndPushNodeToStack(new JSForOfNode(l, r));
		super.enterForOfStatement(ctx);
	}

	@Override
	public void exitForOfStatement(ForOfStatementContext ctx)
	{
		popNode();
		super.exitForOfStatement(ctx);
	}

	@Override
	public void enterForVarOfStatement(ForVarOfStatementContext ctx)
	{
		Symbol l = toSymbol(ctx.getToken(JSParser.OpenParen, 0));
		Symbol r = toSymbol(ctx.getToken(JSParser.CloseParen, 0));
		addToParentAndPushNodeToStack(new JSForOfNode(l, r));
		super.enterForVarOfStatement(ctx);
	}

	@Override
	public void exitForVarOfStatement(ForVarOfStatementContext ctx)
	{
		popNode();
		super.exitForVarOfStatement(ctx);
	}

	@Override
	public void enterForLexicalOfStatement(ForLexicalOfStatementContext ctx)
	{
		Symbol l = toSymbol(ctx.getToken(JSParser.OpenParen, 0));
		Symbol r = toSymbol(ctx.getToken(JSParser.CloseParen, 0));
		addToParentAndPushNodeToStack(new JSForOfNode(l, r));
		super.enterForLexicalOfStatement(ctx);
	}

	@Override
	public void exitForLexicalOfStatement(ForLexicalOfStatementContext ctx)
	{
		popNode();
		super.exitForLexicalOfStatement(ctx);
	}

	@Override
	public void enterForDeclaration(ForDeclarationContext ctx)
	{
		Symbol v = toSymbol(ctx.letOrConst().getStart());
		JSNode node = new JSVarNode(v);
		addToParentAndPushNodeToStack(node);
		super.enterForDeclaration(ctx);
	}

	@Override
	public void exitForDeclaration(ForDeclarationContext ctx)
	{
		popNode();
		super.exitForDeclaration(ctx);
	}

	@Override
	public void enterVarForDeclaration(VarForDeclarationContext ctx)
	{
		// Use start because it's available during parse, is faster than getToken, should always be var keyword
		Symbol var = toSymbol(ctx.start);
		// Symbol var = toSymbol(ctx.getToken(JSParser.Var, 0));
		JSNode node = new JSVarNode(var);
		addToParentAndPushNodeToStack(node);
		super.enterVarForDeclaration(ctx);
	}

	@Override
	public void exitVarForDeclaration(VarForDeclarationContext ctx)
	{
		popNode();
		super.exitVarForDeclaration(ctx);
	}

	@Override
	public void enterObjectLiteral(ObjectLiteralContext ctx)
	{
		Symbol l = toSymbol(ctx.getToken(JSParser.OpenBrace, 0));
		Symbol r = toSymbol(ctx.getToken(JSParser.CloseBrace, 0));
		addToParentAndPushNodeToStack(new JSObjectNode(l, r));
		super.enterObjectLiteral(ctx);
	}

	@Override
	public void exitObjectLiteral(ObjectLiteralContext ctx)
	{
		popNode();
		super.exitObjectLiteral(ctx);
	}

	@Override
	public void enterObjectBindingPattern(ObjectBindingPatternContext ctx)
	{
		Symbol l = toSymbol(ctx.getToken(JSParser.OpenBrace, 0));
		Symbol r = toSymbol(ctx.getToken(JSParser.CloseBrace, 0));
		addToParentAndPushNodeToStack(new JSObjectNode(l, r));
		super.enterObjectBindingPattern(ctx);
	}

	@Override
	public void exitObjectBindingPattern(ObjectBindingPatternContext ctx)
	{
		popNode();
		super.exitObjectBindingPattern(ctx);
	}

	@Override
	public void enterPropertyDefinition(PropertyDefinitionContext ctx)
	{
		PropertyNameContext pnc = ctx.propertyName();
		if (pnc != null)
		{
			Symbol c = toSymbol(ctx.getToken(JSParser.Colon, 0));
			addToParentAndPushNodeToStack(new JSNameValuePairNode(c));
		}
		super.enterPropertyDefinition(ctx);
	}

	@Override
	public void exitPropertyDefinition(PropertyDefinitionContext ctx)
	{
		PropertyNameContext pnc = ctx.propertyName();
		if (pnc != null)
		{
			popNode();
		}
		super.exitPropertyDefinition(ctx);
	}

	@Override
	public void enterExportDeclaration(ExportDeclarationContext ctx)
	{
		TerminalNode d = ctx.getToken(JSParser.Default, 0);
		boolean isDefault = (d != null);

		FromClauseContext fcc = ctx.fromClause();
		String from = null;
		if (fcc != null)
		{
			from = fcc.moduleSpecifier().StringLiteral().getText();
		}

		TerminalNode s = ctx.getToken(JSParser.Multiply, 0);
		Symbol star = null;
		if (s != null)
		{
			star = toSymbol(s);
		}

		addToParentAndPushNodeToStack(new JSExportNode(isDefault, star, from));
		super.enterExportDeclaration(ctx);
	}

	@Override
	public void exitExportDeclaration(ExportDeclarationContext ctx)
	{
		popNode();
		super.exitExportDeclaration(ctx);
	}

	@Override
	public void enterExportSpecifier(ExportSpecifierContext ctx)
	{
		addToParentAndPushNodeToStack(new JSExportSpecifierNode());
		super.enterExportSpecifier(ctx);
	}

	@Override
	public void exitExportSpecifier(ExportSpecifierContext ctx)
	{
		popNode();
		super.exitExportSpecifier(ctx);
	}

	@Override
	public void enterParenthesizedExpression(ParenthesizedExpressionContext ctx)
	{
		Symbol l = toSymbol(ctx.getToken(JSParser.OpenParen, 0));
		Symbol r = toSymbol(ctx.getToken(JSParser.CloseParen, 0));
		addToParentAndPushNodeToStack(new JSGroupNode(l, r));
		super.enterParenthesizedExpression(ctx);
	}

	@Override
	public void exitParenthesizedExpression(ParenthesizedExpressionContext ctx)
	{
		popNode();
		super.exitParenthesizedExpression(ctx);
	}

	@Override
	public void enterInExpression(InExpressionContext ctx)
	{
		Symbol o = toSymbol(ctx.getToken(JSParser.In, 0));
		addToParentAndPushNodeToStack(new JSBinaryBooleanOperatorNode(o));
		super.enterInExpression(ctx);
	}

	@Override
	public void exitInExpression(InExpressionContext ctx)
	{
		popNode();
		super.exitInExpression(ctx);
	}

	@Override
	public void enterTernaryExpression(TernaryExpressionContext ctx)
	{
		Symbol questionMark = toSymbol(ctx.getToken(JSParser.QuestionMark, 0));
		Symbol colon = toSymbol(ctx.getToken(JSParser.Colon, 0));
		addToParentAndPushNodeToStack(new JSConditionalNode(questionMark, colon));
		super.enterTernaryExpression(ctx);
	}

	@Override
	public void exitTernaryExpression(TernaryExpressionContext ctx)
	{
		popNode();
		super.exitTernaryExpression(ctx);
	}

	@Override
	public void enterLabelledStatement(LabelledStatementContext ctx)
	{
		JSNode id = new JSIdentifierNode(toSymbolWithText(ctx.identifier().start));
		Symbol colon = toSymbol(ctx.getToken(JSParser.Colon, 0));
		addToParentAndPushNodeToStack(new JSLabelledNode(id, colon));
		super.enterLabelledStatement(ctx);
	}

	@Override
	public void exitLabelledStatement(LabelledStatementContext ctx)
	{
		popNode();
		super.exitLabelledStatement(ctx);
	}

	@Override
	public void enterSpreadElement(SpreadElementContext ctx)
	{
		// Use start in preference to getToken()
		Symbol d = toSymbol(ctx.start);
		// Symbol d = toSymbol(ctx.getToken(JSParser.Ellipsis, 0));
		addToParentAndPushNodeToStack(new JSSpreadElementNode(d));
		super.enterSpreadElement(ctx);
	}

	@Override
	public void exitSpreadElement(SpreadElementContext ctx)
	{
		popNode();
		super.exitSpreadElement(ctx);
	}

	@Override
	public void enterBindingRestElement(BindingRestElementContext ctx)
	{
		// Use start in preference to getToken()
		Symbol d = toSymbol(ctx.start);
		// Symbol d = toSymbol(ctx.getToken(JSParser.Ellipsis, 0));
		addToParentAndPushNodeToStack(new JSRestElementNode(d));
		super.enterBindingRestElement(ctx);
	}

	@Override
	public void exitBindingRestElement(BindingRestElementContext ctx)
	{
		popNode();
		super.exitBindingRestElement(ctx);
	}

	@Override
	public void enterBindingProperty(BindingPropertyContext ctx)
	{
		if (ctx.propertyName() != null)
		{
			Symbol colon = toSymbol(ctx.getToken(JSParser.Colon, 0));
			addToParentAndPushNodeToStack(new JSNameValuePairNode(colon));
		}
		super.enterBindingProperty(ctx);
	}

	@Override
	public void exitBindingProperty(BindingPropertyContext ctx)
	{
		if (ctx.propertyName() != null)
		{
			popNode();
		}
		super.exitBindingProperty(ctx);
	}

	@Override
	public void enterClassDeclaration(ClassDeclarationContext ctx)
	{
		ClassHeritageContext chc = ctx.classTail().classHeritage();
		addToParentAndPushNodeToStack(new JSClassNode(true, chc != null));
		super.enterClassDeclaration(ctx);
	}

	@Override
	public void exitClassDeclaration(ClassDeclarationContext ctx)
	{
		// if no class body, add an empty statements node
		if (ctx.classTail().classBody() == null)
		{
			addChildToParent(new JSStatementsNode());
		}
		popNode();
		super.exitClassDeclaration(ctx);
	}

	@Override
	public void enterClassBody(ClassBodyContext ctx)
	{
		addToParentAndPushNodeToStack(new JSStatementsNode());
		super.enterClassBody(ctx);
	}

	@Override
	public void exitClassBody(ClassBodyContext ctx)
	{
		popNode();
		super.exitClassBody(ctx);
	}

	@Override
	public void enterMethodDefinition(MethodDefinitionContext ctx)
	{
		// let enterGenerateMethod handle generater method definitions
		GeneratorMethodContext gmc = ctx.generatorMethod();
		if (gmc == null)
		{
			TerminalNode get = ctx.getToken(JSParser.Get, 0);
			if (get != null)
			{
				addToParentAndPushNodeToStack(new JSGetterNode());
			}
			else
			{
				TerminalNode set = ctx.getToken(JSParser.Set, 0);
				if (set != null)
				{
					addToParentAndPushNodeToStack(new JSSetterNode());
				}
				else
				{
					// FIXME Use NameValuePairNode to wrap this? This is a method declared as a property of a
					// class/object
					addToParentAndPushNodeToStack(new JSFunctionNode());
				}
			}
		}
		super.enterMethodDefinition(ctx);
	}

	@Override
	public void exitMethodDefinition(MethodDefinitionContext ctx)
	{
		// if preceded by 'static', mark the property as static
		if (ctx.getParent() instanceof ClassElementContext)
		{
			ClassElementContext cec = (ClassElementContext) ctx.getParent();
			TerminalNode stat = cec.Static();
			if (stat != null)
			{
				IParseNode d = getCurrentNode();
				if (d instanceof JSFunctionNode)
				{
					((JSFunctionNode) d).setStatic();
				}
				else if (d instanceof JSNameValuePairNode)
				{
					((JSNameValuePairNode) d).setStatic();
				}
			}
		}
		popNode();
		super.exitMethodDefinition(ctx);
	}

	@Override
	public void enterPropertySetParameterList(PropertySetParameterListContext ctx)
	{
		// this wraps the single argument for setter methods
		addToParentAndPushNodeToStack(new JSParametersNode());
		super.enterPropertySetParameterList(ctx);
	}

	@Override
	public void exitPropertySetParameterList(PropertySetParameterListContext ctx)
	{
		popNode();
		super.exitPropertySetParameterList(ctx);
	}

	@Override
	public void enterGeneratorMethod(GeneratorMethodContext ctx)
	{
		addToParentAndPushNodeToStack(new JSGeneratorFunctionNode());
		super.enterGeneratorMethod(ctx);
	}

	@Override
	public void exitGeneratorMethod(GeneratorMethodContext ctx)
	{
		popNode();
		super.exitGeneratorMethod(ctx);
	}

	@Override
	public void enterGeneratorDeclaration(GeneratorDeclarationContext ctx)
	{
		addToParentAndPushNodeToStack(new JSGeneratorFunctionNode());
		super.enterGeneratorDeclaration(ctx);
	}

	@Override
	public void exitGeneratorDeclaration(GeneratorDeclarationContext ctx)
	{
		popNode();
		super.exitGeneratorDeclaration(ctx);
	}

	@Override
	public void enterGeneratorExpression(GeneratorExpressionContext ctx)
	{
		addToParentAndPushNodeToStack(new JSGeneratorFunctionNode());
		// if no name, insert empty node for it
		if (ctx.bindingIdentifier() == null)
		{
			addChildToParent(new JSEmptyNode(toSymbol(ctx.getToken(JSParser.Function, 0))));
		}
		super.enterGeneratorExpression(ctx);
	}

	@Override
	public void exitGeneratorExpression(GeneratorExpressionContext ctx)
	{
		popNode();
		super.exitGeneratorExpression(ctx);
	}

	@Override
	public void enterYieldExpression(YieldExpressionContext ctx)
	{
		// Use start in preference to getToken: faster, available during parse, yield should always be first token
		Symbol y = toSymbol(ctx.start);
		// Symbol y = toSymbol(ctx.getToken(JSParser.Yield, 0));
		addToParentAndPushNodeToStack(new JSYieldNode(y));
		super.enterYieldExpression(ctx);
	}

	@Override
	public void exitYieldExpression(YieldExpressionContext ctx)
	{
		popNode();
		super.exitYieldExpression(ctx);
	}

	@Override
	public void enterArrowFunction(ArrowFunctionContext ctx)
	{
		addToParentAndPushNodeToStack(new JSArrowFunctionNode());
		super.enterArrowFunction(ctx);
	}

	@Override
	public void exitArrowFunction(ArrowFunctionContext ctx)
	{
		popNode();
		super.exitArrowFunction(ctx);
	}

	@Override
	public void enterArrowParameters(ArrowParametersContext ctx)
	{
		addToParentAndPushNodeToStack(new JSParametersNode());
		super.enterArrowParameters(ctx);
	}

	@Override
	public void exitArrowParameters(ArrowParametersContext ctx)
	{
		popNode();
		super.exitArrowParameters(ctx);
	}

	@Override
	public void enterComputedPropertyName(ComputedPropertyNameContext ctx)
	{
		addToParentAndPushNodeToStack(new JSComputedPropertyNameNode());
		super.enterComputedPropertyName(ctx);
	}

	@Override
	public void exitComputedPropertyName(ComputedPropertyNameContext ctx)
	{
		popNode();
		super.exitComputedPropertyName(ctx);
	}

	private Symbol pickSymbol(ParserRuleContext ctx, int position, int... types)
	{
		for (int i = 0; i < types.length; i++)
		{
			TerminalNode n = ctx.getToken(types[i], position);
			if (n != null)
			{
				return toSymbol(n);
			}
		}
		return null;
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

	private Symbol toSymbolOrNull(TerminalNode terminal)
	{
		if (terminal == null)
		{
			return null;
		}
		return toSymbol(terminal);
	}

	private Symbol toSymbol(TerminalNode terminal)
	{
		// TODO Most terminal nodes have essentially hard-coded string values baed on type
		// Can we just point to the pre-defined strings for these?
		return toSymbol(terminal.getSymbol());
	}

	private Symbol toSymbolWithText(TerminalNode terminal)
	{
		return toSymbolWithText(terminal.getSymbol());
	}

	private Symbol toSymbol(Token token)
	{
		JSTokenType t = JSTokenType.getFromANTLRType(token.getType());
		return toSymbol(token, t.getName());
	}

	private Symbol toSymbolWithText(Token token)
	{
		return toSymbol(token, token.getText());
	}

	private Symbol toSymbol(Token token, String text)
	{
		return new Symbol((short) token.getType(), token.getStartIndex(), token.getStopIndex(), text);
	}

	public JSParseRootNode getRootNode()
	{
		return fRootNode;
	}

	@Override
	public void visitErrorNode(ErrorNode node)
	{
		addChildToParent(new JSErrorNode());
		super.visitErrorNode(node);
	}

	@Override
	public void enterForLexicalInStatement(ForLexicalInStatementContext ctx)
	{
		Symbol l = toSymbol(ctx.getToken(JSParser.OpenParen, 0));
		Symbol in = toSymbol(ctx.getToken(JSParser.In, 0));
		Symbol r = toSymbol(ctx.getToken(JSParser.CloseParen, 0));
		addToParentAndPushNodeToStack(new JSForInNode(l, in, r));
		super.enterForLexicalInStatement(ctx);
	}

	@Override
	public void exitForLexicalInStatement(ForLexicalInStatementContext ctx)
	{
		popNode();
		super.exitForLexicalInStatement(ctx);

	}

	@Override
	public void enterClassExpression(ClassExpressionContext ctx)
	{
		BindingIdentifierContext bic = ctx.bindingIdentifier();
		ClassHeritageContext chc = ctx.classTail().classHeritage();
		addToParentAndPushNodeToStack(new JSClassNode(bic != null, chc != null));
		super.enterClassExpression(ctx);
	}

	@Override
	public void exitClassExpression(ClassExpressionContext ctx)
	{
		// if no class body, add an empty statements node
		if (ctx.classTail().classBody() == null)
		{
			addChildToParent(new JSStatementsNode());
		}
		popNode();
		super.exitClassExpression(ctx);
	}

	@Override
	public void enterFunctionExpression(FunctionExpressionContext ctx)
	{
		addToParentAndPushNodeToStack(new JSFunctionNode());
		super.enterFunctionExpression(ctx);
	}

	@Override
	public void exitFunctionExpression(FunctionExpressionContext ctx)
	{
		popNode();
		super.exitFunctionExpression(ctx);
	}
}
