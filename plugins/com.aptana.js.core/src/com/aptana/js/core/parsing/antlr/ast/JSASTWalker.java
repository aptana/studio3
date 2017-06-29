package com.aptana.js.core.parsing.antlr.ast;

import java.util.Stack;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.aptana.js.core.parsing.antlr.JSBaseListener;
import com.aptana.js.core.parsing.antlr.JSParser;
import com.aptana.js.core.parsing.antlr.JSParser.AdditiveExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.ArgumentsContext;
import com.aptana.js.core.parsing.antlr.JSParser.ArgumentsExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.ArrayLiteralContext;
import com.aptana.js.core.parsing.antlr.JSParser.AssignmentExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.AssignmentOperatorExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.BindingElementContext;
import com.aptana.js.core.parsing.antlr.JSParser.BindingIdentifierContext;
import com.aptana.js.core.parsing.antlr.JSParser.BindingPatternContext;
import com.aptana.js.core.parsing.antlr.JSParser.BitAndExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.BitNotExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.BitOrExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.BitShiftExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.BitXOrExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.BlockContext;
import com.aptana.js.core.parsing.antlr.JSParser.BreakStatementContext;
import com.aptana.js.core.parsing.antlr.JSParser.CaseClauseContext;
import com.aptana.js.core.parsing.antlr.JSParser.CatchProductionContext;
import com.aptana.js.core.parsing.antlr.JSParser.ContinueStatementContext;
import com.aptana.js.core.parsing.antlr.JSParser.DefaultClauseContext;
import com.aptana.js.core.parsing.antlr.JSParser.DeleteExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.DoWhileStatementContext;
import com.aptana.js.core.parsing.antlr.JSParser.ElementListContext;
import com.aptana.js.core.parsing.antlr.JSParser.ElisionContext;
import com.aptana.js.core.parsing.antlr.JSParser.EmptyStatementContext;
import com.aptana.js.core.parsing.antlr.JSParser.EqualityExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.ExpressionSequenceContext;
import com.aptana.js.core.parsing.antlr.JSParser.ExpressionStatementContext;
import com.aptana.js.core.parsing.antlr.JSParser.FinallyProductionContext;
import com.aptana.js.core.parsing.antlr.JSParser.ForLoopStatementContext;
import com.aptana.js.core.parsing.antlr.JSParser.ForVarLoopStatementContext;
import com.aptana.js.core.parsing.antlr.JSParser.FormalParametersContext;
import com.aptana.js.core.parsing.antlr.JSParser.FunctionDeclarationContext;
import com.aptana.js.core.parsing.antlr.JSParser.FunctionStatementListContext;
import com.aptana.js.core.parsing.antlr.JSParser.IdentifierExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.IfStatementContext;
import com.aptana.js.core.parsing.antlr.JSParser.ImportDeclarationContext;
import com.aptana.js.core.parsing.antlr.JSParser.InitializerContext;
import com.aptana.js.core.parsing.antlr.JSParser.LexicalBindingContext;
import com.aptana.js.core.parsing.antlr.JSParser.LexicalDeclarationContext;
import com.aptana.js.core.parsing.antlr.JSParser.LiteralContext;
import com.aptana.js.core.parsing.antlr.JSParser.LogicalAndExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.LogicalOrExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.MemberDotExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.MemberIndexExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.ModuleSpecifierContext;
import com.aptana.js.core.parsing.antlr.JSParser.MultiplicativeExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.NewExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.NotExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.NumericLiteralContext;
import com.aptana.js.core.parsing.antlr.JSParser.PostDecreaseExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.PostIncrementExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.PreDecreaseExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.PreIncrementExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.ProgramContext;
import com.aptana.js.core.parsing.antlr.JSParser.RegularExpressionLiteralExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.RelationalExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.ReturnStatementContext;
import com.aptana.js.core.parsing.antlr.JSParser.SwitchStatementContext;
import com.aptana.js.core.parsing.antlr.JSParser.ThisExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.TryStatementContext;
import com.aptana.js.core.parsing.antlr.JSParser.TypeofExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.UnaryMinusExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.UnaryPlusExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.VariableDeclarationContext;
import com.aptana.js.core.parsing.antlr.JSParser.VariableDeclarationStatementContext;
import com.aptana.js.core.parsing.antlr.JSParser.VariableStatementContext;
import com.aptana.js.core.parsing.antlr.JSParser.VoidExpressionContext;
import com.aptana.js.core.parsing.antlr.JSParser.WhileStatementContext;
import com.aptana.js.core.parsing.antlr.JSParser.WithStatementContext;
import com.aptana.js.core.parsing.ast.JSArgumentsNode;
import com.aptana.js.core.parsing.ast.JSArrayNode;
import com.aptana.js.core.parsing.ast.JSAssignmentNode;
import com.aptana.js.core.parsing.ast.JSBinaryArithmeticOperatorNode;
import com.aptana.js.core.parsing.ast.JSBinaryBooleanOperatorNode;
import com.aptana.js.core.parsing.ast.JSBreakNode;
import com.aptana.js.core.parsing.ast.JSCaseNode;
import com.aptana.js.core.parsing.ast.JSCatchNode;
import com.aptana.js.core.parsing.ast.JSCommaNode;
import com.aptana.js.core.parsing.ast.JSConstructNode;
import com.aptana.js.core.parsing.ast.JSContinueNode;
import com.aptana.js.core.parsing.ast.JSDeclarationNode;
import com.aptana.js.core.parsing.ast.JSDefaultNode;
import com.aptana.js.core.parsing.ast.JSDestructuringNode;
import com.aptana.js.core.parsing.ast.JSDoNode;
import com.aptana.js.core.parsing.ast.JSElementsNode;
import com.aptana.js.core.parsing.ast.JSElisionNode;
import com.aptana.js.core.parsing.ast.JSEmptyNode;
import com.aptana.js.core.parsing.ast.JSFalseNode;
import com.aptana.js.core.parsing.ast.JSFinallyNode;
import com.aptana.js.core.parsing.ast.JSForNode;
import com.aptana.js.core.parsing.ast.JSFunctionNode;
import com.aptana.js.core.parsing.ast.JSGetElementNode;
import com.aptana.js.core.parsing.ast.JSGetPropertyNode;
import com.aptana.js.core.parsing.ast.JSIdentifierNode;
import com.aptana.js.core.parsing.ast.JSIfNode;
import com.aptana.js.core.parsing.ast.JSImportNode;
import com.aptana.js.core.parsing.ast.JSInvokeNode;
import com.aptana.js.core.parsing.ast.JSNode;
import com.aptana.js.core.parsing.ast.JSNullNode;
import com.aptana.js.core.parsing.ast.JSNumberNode;
import com.aptana.js.core.parsing.ast.JSParametersNode;
import com.aptana.js.core.parsing.ast.JSParseRootNode;
import com.aptana.js.core.parsing.ast.JSPostUnaryOperatorNode;
import com.aptana.js.core.parsing.ast.JSPreUnaryOperatorNode;
import com.aptana.js.core.parsing.ast.JSRegexNode;
import com.aptana.js.core.parsing.ast.JSReturnNode;
import com.aptana.js.core.parsing.ast.JSStatementsNode;
import com.aptana.js.core.parsing.ast.JSStringNode;
import com.aptana.js.core.parsing.ast.JSSwitchNode;
import com.aptana.js.core.parsing.ast.JSThisNode;
import com.aptana.js.core.parsing.ast.JSTrueNode;
import com.aptana.js.core.parsing.ast.JSTryNode;
import com.aptana.js.core.parsing.ast.JSVarNode;
import com.aptana.js.core.parsing.ast.JSWhileNode;
import com.aptana.js.core.parsing.ast.JSWithNode;
import com.aptana.parsing.ast.IParseNode;

import beaver.Symbol;

public class JSASTWalker extends JSBaseListener
{
	// TODO Construct the AST from the callbacks!
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
	public void enterIdentifierExpression(IdentifierExpressionContext ctx)
	{
		// leaf node, no need to push to stack or pop later
		addChildToParent(new JSIdentifierNode(toSymbol(ctx.Identifier())));
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
		addToParentAndPushNodeToStack(new JSPreUnaryOperatorNode(toSymbol(ctx.getToken(type, 0))));
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
		ModuleSpecifierContext msc = ctx.moduleSpecifier();
		if (msc == null)
		{
			msc = ctx.fromClause().moduleSpecifier();
		}
		String s = msc.StringLiteral().getText();
		addToParentAndPushNodeToStack(new JSImportNode(s));
		super.enterImportDeclaration(ctx);
	}

	@Override
	public void exitImportDeclaration(ImportDeclarationContext ctx)
	{
		popNode();
		super.exitImportDeclaration(ctx);
	}

	@Override
	public void enterEmptyStatement(EmptyStatementContext ctx)
	{
		// leaf node
		JSNode node = new JSEmptyNode(toSymbol(ctx.SemiColon()));
		node.setSemicolonIncluded(true);
		addChildToParent(node);
		super.enterEmptyStatement(ctx);
	}

	@Override
	public void enterNumericLiteral(NumericLiteralContext ctx)
	{
		// leaf node
		addChildToParent(new JSNumberNode(ctx.getText()));
		super.enterNumericLiteral(ctx);
	}

	@Override
	public void enterArrayLiteral(ArrayLiteralContext ctx)
	{
		JSArrayNode node = new JSArrayNode(toSymbol(ctx.getToken(JSParser.OpenBracket, 0)),
				toSymbol(ctx.getToken(JSParser.CloseBracket, 0)));

		addToParentAndPushNodeToStack(node);
		// {:
		// if (e == null) {
		// return new JSArrayNode(l, r);
		// }
		// return new JSArrayNode(l, r, e);
		// :}
		// | LBRACKET.l ElementList.e RBRACKET.r
		// {:
		// return new JSArrayNode(l, r, e);
		// :}
		// | LBRACKET.l ElementList.e COMMA Elision.n? RBRACKET.r
		// {:
		// if (n == null) {
		// return new JSArrayNode(l, r, e);
		// }
		// e.addChild(n);
		// return new JSArrayNode(l, r, e);
		// :}

		super.enterArrayLiteral(ctx);
	}

	@Override
	public void exitArrayLiteral(ArrayLiteralContext ctx)
	{
		popNode();
		super.exitArrayLiteral(ctx);
	}

	@Override
	public void enterElementList(ElementListContext ctx)
	{
		addToParentAndPushNodeToStack(new JSElementsNode());

		// if (n == null) {
		// return new JSElementsNode(e);
		// }
		// return new JSElementsNode(n, e);
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
		// Elision.e COMMA
		// {:
		// e.addChild(new JSNullNode());
		//
		// return e;
		// :}
		// | COMMA
		// {:
		// return new JSElisionNode(new JSNullNode());
		// :}
		// TODO Add a JSNullNode for each comma!
		addChildToParent(new JSElisionNode(new JSNullNode()));
		super.enterElision(ctx);
	}

	@Override
	public void enterLiteral(LiteralContext ctx)
	{
		// Always a leaf node, so just add to parent node, don't push to stack
		Token t = ctx.getStart();
		switch (t.getType())
		{
			case JSParser.DecimalLiteral:
			case JSParser.HexIntegerLiteral:
			case JSParser.OctalIntegerLiteral:
			case JSParser.BinaryIntegerLiteral:
				addChildToParent(new JSNumberNode(t.getText()));
				break;
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
				break;
		}
		super.enterLiteral(ctx);
	}

	@Override
	public void enterVariableDeclarationStatement(VariableDeclarationStatementContext ctx)
	{
		Symbol var = toSymbol(ctx.getToken(JSParser.Var, 0));
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
		Symbol i = toSymbol(ctx.Identifier());
		addChildToParent(new JSIdentifierNode(i));
		super.enterBindingIdentifier(ctx);
	}

	@Override
	public void enterLexicalDeclaration(LexicalDeclarationContext ctx)
	{
		Symbol v = toSymbol(ctx.letOrConst().getStart());
		JSNode node = new JSVarNode(v);
		node.setSemicolonIncluded(true);
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
		Symbol rp = toSymbol(ctx.getToken(JSParser.CloseParen, 0));
		Symbol lb = toSymbol(ctx.caseBlock().getToken(JSParser.OpenBrace, 0));
		Symbol rb = toSymbol(ctx.caseBlock().getToken(JSParser.CloseBrace, 0));
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
			Symbol r = toSymbol(ctx.getToken(JSParser.Return, 0));
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
		getCurrentNode().addChild(node);
	}

	private void popNode()
	{
		fNodeStack.pop();
	}

	private IParseNode getCurrentNode()
	{
		return fNodeStack.peek();
	}

	private void addToParentAndPushNodeToStack(JSNode node)
	{
		addChildToParent(node);
		fNodeStack.push(node);
	}

	private Symbol toSymbol(TerminalNode terminal)
	{
		return toSymbol(terminal.getSymbol());
	}

	private Symbol toSymbol(Token token)
	{
		return new Symbol((short) token.getType(), token.getStartIndex(), token.getStopIndex(), token.getText());
	}

	public JSParseRootNode getRootNode()
	{
		return fRootNode;
	}

}
