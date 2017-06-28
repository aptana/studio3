// Generated from /Users/cwilliams/repos/studio3/plugins/com.aptana.js.core/parsing/JS.g4 by ANTLR 4.7
package com.aptana.js.core.parsing.antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link JSParser}.
 */
public interface JSListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link JSParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(JSParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(JSParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(JSParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(JSParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(JSParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(JSParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#hoistableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterHoistableDeclaration(JSParser.HoistableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#hoistableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitHoistableDeclaration(JSParser.HoistableDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#breakableStatement}.
	 * @param ctx the parse tree
	 */
	void enterBreakableStatement(JSParser.BreakableStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#breakableStatement}.
	 * @param ctx the parse tree
	 */
	void exitBreakableStatement(JSParser.BreakableStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#blockStatement}.
	 * @param ctx the parse tree
	 */
	void enterBlockStatement(JSParser.BlockStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#blockStatement}.
	 * @param ctx the parse tree
	 */
	void exitBlockStatement(JSParser.BlockStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(JSParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(JSParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#statementList}.
	 * @param ctx the parse tree
	 */
	void enterStatementList(JSParser.StatementListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#statementList}.
	 * @param ctx the parse tree
	 */
	void exitStatementList(JSParser.StatementListContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#statementListItem}.
	 * @param ctx the parse tree
	 */
	void enterStatementListItem(JSParser.StatementListItemContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#statementListItem}.
	 * @param ctx the parse tree
	 */
	void exitStatementListItem(JSParser.StatementListItemContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#lexicalDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterLexicalDeclaration(JSParser.LexicalDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#lexicalDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitLexicalDeclaration(JSParser.LexicalDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#letOrConst}.
	 * @param ctx the parse tree
	 */
	void enterLetOrConst(JSParser.LetOrConstContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#letOrConst}.
	 * @param ctx the parse tree
	 */
	void exitLetOrConst(JSParser.LetOrConstContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#bindingList}.
	 * @param ctx the parse tree
	 */
	void enterBindingList(JSParser.BindingListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#bindingList}.
	 * @param ctx the parse tree
	 */
	void exitBindingList(JSParser.BindingListContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#lexicalBinding}.
	 * @param ctx the parse tree
	 */
	void enterLexicalBinding(JSParser.LexicalBindingContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#lexicalBinding}.
	 * @param ctx the parse tree
	 */
	void exitLexicalBinding(JSParser.LexicalBindingContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#variableStatement}.
	 * @param ctx the parse tree
	 */
	void enterVariableStatement(JSParser.VariableStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#variableStatement}.
	 * @param ctx the parse tree
	 */
	void exitVariableStatement(JSParser.VariableStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#variableDeclarationList}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclarationList(JSParser.VariableDeclarationListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#variableDeclarationList}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclarationList(JSParser.VariableDeclarationListContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaration(JSParser.VariableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaration(JSParser.VariableDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#bindingPattern}.
	 * @param ctx the parse tree
	 */
	void enterBindingPattern(JSParser.BindingPatternContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#bindingPattern}.
	 * @param ctx the parse tree
	 */
	void exitBindingPattern(JSParser.BindingPatternContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#objectBindingPattern}.
	 * @param ctx the parse tree
	 */
	void enterObjectBindingPattern(JSParser.ObjectBindingPatternContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#objectBindingPattern}.
	 * @param ctx the parse tree
	 */
	void exitObjectBindingPattern(JSParser.ObjectBindingPatternContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#arrayBindingPattern}.
	 * @param ctx the parse tree
	 */
	void enterArrayBindingPattern(JSParser.ArrayBindingPatternContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#arrayBindingPattern}.
	 * @param ctx the parse tree
	 */
	void exitArrayBindingPattern(JSParser.ArrayBindingPatternContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#bindingPropertyList}.
	 * @param ctx the parse tree
	 */
	void enterBindingPropertyList(JSParser.BindingPropertyListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#bindingPropertyList}.
	 * @param ctx the parse tree
	 */
	void exitBindingPropertyList(JSParser.BindingPropertyListContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#bindingElementList}.
	 * @param ctx the parse tree
	 */
	void enterBindingElementList(JSParser.BindingElementListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#bindingElementList}.
	 * @param ctx the parse tree
	 */
	void exitBindingElementList(JSParser.BindingElementListContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#bindingElisionElement}.
	 * @param ctx the parse tree
	 */
	void enterBindingElisionElement(JSParser.BindingElisionElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#bindingElisionElement}.
	 * @param ctx the parse tree
	 */
	void exitBindingElisionElement(JSParser.BindingElisionElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#bindingProperty}.
	 * @param ctx the parse tree
	 */
	void enterBindingProperty(JSParser.BindingPropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#bindingProperty}.
	 * @param ctx the parse tree
	 */
	void exitBindingProperty(JSParser.BindingPropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#bindingElement}.
	 * @param ctx the parse tree
	 */
	void enterBindingElement(JSParser.BindingElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#bindingElement}.
	 * @param ctx the parse tree
	 */
	void exitBindingElement(JSParser.BindingElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#singleNameBinding}.
	 * @param ctx the parse tree
	 */
	void enterSingleNameBinding(JSParser.SingleNameBindingContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#singleNameBinding}.
	 * @param ctx the parse tree
	 */
	void exitSingleNameBinding(JSParser.SingleNameBindingContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#bindingRestElement}.
	 * @param ctx the parse tree
	 */
	void enterBindingRestElement(JSParser.BindingRestElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#bindingRestElement}.
	 * @param ctx the parse tree
	 */
	void exitBindingRestElement(JSParser.BindingRestElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#initializer}.
	 * @param ctx the parse tree
	 */
	void enterInitializer(JSParser.InitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#initializer}.
	 * @param ctx the parse tree
	 */
	void exitInitializer(JSParser.InitializerContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#emptyStatement}.
	 * @param ctx the parse tree
	 */
	void enterEmptyStatement(JSParser.EmptyStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#emptyStatement}.
	 * @param ctx the parse tree
	 */
	void exitEmptyStatement(JSParser.EmptyStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#expressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterExpressionStatement(JSParser.ExpressionStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#expressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitExpressionStatement(JSParser.ExpressionStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void enterIfStatement(JSParser.IfStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void exitIfStatement(JSParser.IfStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#iterationStatement}.
	 * @param ctx the parse tree
	 */
	void enterIterationStatement(JSParser.IterationStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#iterationStatement}.
	 * @param ctx the parse tree
	 */
	void exitIterationStatement(JSParser.IterationStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#forDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterForDeclaration(JSParser.ForDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#forDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitForDeclaration(JSParser.ForDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#forBinding}.
	 * @param ctx the parse tree
	 */
	void enterForBinding(JSParser.ForBindingContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#forBinding}.
	 * @param ctx the parse tree
	 */
	void exitForBinding(JSParser.ForBindingContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#continueStatement}.
	 * @param ctx the parse tree
	 */
	void enterContinueStatement(JSParser.ContinueStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#continueStatement}.
	 * @param ctx the parse tree
	 */
	void exitContinueStatement(JSParser.ContinueStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#breakStatement}.
	 * @param ctx the parse tree
	 */
	void enterBreakStatement(JSParser.BreakStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#breakStatement}.
	 * @param ctx the parse tree
	 */
	void exitBreakStatement(JSParser.BreakStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStatement(JSParser.ReturnStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStatement(JSParser.ReturnStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#withStatement}.
	 * @param ctx the parse tree
	 */
	void enterWithStatement(JSParser.WithStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#withStatement}.
	 * @param ctx the parse tree
	 */
	void exitWithStatement(JSParser.WithStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#switchStatement}.
	 * @param ctx the parse tree
	 */
	void enterSwitchStatement(JSParser.SwitchStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#switchStatement}.
	 * @param ctx the parse tree
	 */
	void exitSwitchStatement(JSParser.SwitchStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#caseBlock}.
	 * @param ctx the parse tree
	 */
	void enterCaseBlock(JSParser.CaseBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#caseBlock}.
	 * @param ctx the parse tree
	 */
	void exitCaseBlock(JSParser.CaseBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#caseClauses}.
	 * @param ctx the parse tree
	 */
	void enterCaseClauses(JSParser.CaseClausesContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#caseClauses}.
	 * @param ctx the parse tree
	 */
	void exitCaseClauses(JSParser.CaseClausesContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#caseClause}.
	 * @param ctx the parse tree
	 */
	void enterCaseClause(JSParser.CaseClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#caseClause}.
	 * @param ctx the parse tree
	 */
	void exitCaseClause(JSParser.CaseClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#defaultClause}.
	 * @param ctx the parse tree
	 */
	void enterDefaultClause(JSParser.DefaultClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#defaultClause}.
	 * @param ctx the parse tree
	 */
	void exitDefaultClause(JSParser.DefaultClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#labelledStatement}.
	 * @param ctx the parse tree
	 */
	void enterLabelledStatement(JSParser.LabelledStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#labelledStatement}.
	 * @param ctx the parse tree
	 */
	void exitLabelledStatement(JSParser.LabelledStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#throwStatement}.
	 * @param ctx the parse tree
	 */
	void enterThrowStatement(JSParser.ThrowStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#throwStatement}.
	 * @param ctx the parse tree
	 */
	void exitThrowStatement(JSParser.ThrowStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#tryStatement}.
	 * @param ctx the parse tree
	 */
	void enterTryStatement(JSParser.TryStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#tryStatement}.
	 * @param ctx the parse tree
	 */
	void exitTryStatement(JSParser.TryStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#catchProduction}.
	 * @param ctx the parse tree
	 */
	void enterCatchProduction(JSParser.CatchProductionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#catchProduction}.
	 * @param ctx the parse tree
	 */
	void exitCatchProduction(JSParser.CatchProductionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#finallyProduction}.
	 * @param ctx the parse tree
	 */
	void enterFinallyProduction(JSParser.FinallyProductionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#finallyProduction}.
	 * @param ctx the parse tree
	 */
	void exitFinallyProduction(JSParser.FinallyProductionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#debuggerStatement}.
	 * @param ctx the parse tree
	 */
	void enterDebuggerStatement(JSParser.DebuggerStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#debuggerStatement}.
	 * @param ctx the parse tree
	 */
	void exitDebuggerStatement(JSParser.DebuggerStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDeclaration(JSParser.FunctionDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDeclaration(JSParser.FunctionDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#strictFormalParameters}.
	 * @param ctx the parse tree
	 */
	void enterStrictFormalParameters(JSParser.StrictFormalParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#strictFormalParameters}.
	 * @param ctx the parse tree
	 */
	void exitStrictFormalParameters(JSParser.StrictFormalParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#formalParameters}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameters(JSParser.FormalParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#formalParameters}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameters(JSParser.FormalParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#formalParameterList}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameterList(JSParser.FormalParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#formalParameterList}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameterList(JSParser.FormalParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#formalsList}.
	 * @param ctx the parse tree
	 */
	void enterFormalsList(JSParser.FormalsListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#formalsList}.
	 * @param ctx the parse tree
	 */
	void exitFormalsList(JSParser.FormalsListContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#functionRestParameter}.
	 * @param ctx the parse tree
	 */
	void enterFunctionRestParameter(JSParser.FunctionRestParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#functionRestParameter}.
	 * @param ctx the parse tree
	 */
	void exitFunctionRestParameter(JSParser.FunctionRestParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#formalParameter}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameter(JSParser.FormalParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#formalParameter}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameter(JSParser.FormalParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#functionBody}.
	 * @param ctx the parse tree
	 */
	void enterFunctionBody(JSParser.FunctionBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#functionBody}.
	 * @param ctx the parse tree
	 */
	void exitFunctionBody(JSParser.FunctionBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#functionStatementList}.
	 * @param ctx the parse tree
	 */
	void enterFunctionStatementList(JSParser.FunctionStatementListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#functionStatementList}.
	 * @param ctx the parse tree
	 */
	void exitFunctionStatementList(JSParser.FunctionStatementListContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#arrowFunction}.
	 * @param ctx the parse tree
	 */
	void enterArrowFunction(JSParser.ArrowFunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#arrowFunction}.
	 * @param ctx the parse tree
	 */
	void exitArrowFunction(JSParser.ArrowFunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#arrowParameters}.
	 * @param ctx the parse tree
	 */
	void enterArrowParameters(JSParser.ArrowParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#arrowParameters}.
	 * @param ctx the parse tree
	 */
	void exitArrowParameters(JSParser.ArrowParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#conciseBody}.
	 * @param ctx the parse tree
	 */
	void enterConciseBody(JSParser.ConciseBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#conciseBody}.
	 * @param ctx the parse tree
	 */
	void exitConciseBody(JSParser.ConciseBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#coverParenthesizedExpressionAndArrowParameterList}.
	 * @param ctx the parse tree
	 */
	void enterCoverParenthesizedExpressionAndArrowParameterList(JSParser.CoverParenthesizedExpressionAndArrowParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#coverParenthesizedExpressionAndArrowParameterList}.
	 * @param ctx the parse tree
	 */
	void exitCoverParenthesizedExpressionAndArrowParameterList(JSParser.CoverParenthesizedExpressionAndArrowParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#methodDefinition}.
	 * @param ctx the parse tree
	 */
	void enterMethodDefinition(JSParser.MethodDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#methodDefinition}.
	 * @param ctx the parse tree
	 */
	void exitMethodDefinition(JSParser.MethodDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#propertySetParameterList}.
	 * @param ctx the parse tree
	 */
	void enterPropertySetParameterList(JSParser.PropertySetParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#propertySetParameterList}.
	 * @param ctx the parse tree
	 */
	void exitPropertySetParameterList(JSParser.PropertySetParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#generatorMethod}.
	 * @param ctx the parse tree
	 */
	void enterGeneratorMethod(JSParser.GeneratorMethodContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#generatorMethod}.
	 * @param ctx the parse tree
	 */
	void exitGeneratorMethod(JSParser.GeneratorMethodContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#generatorDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterGeneratorDeclaration(JSParser.GeneratorDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#generatorDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitGeneratorDeclaration(JSParser.GeneratorDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#generatorExpression}.
	 * @param ctx the parse tree
	 */
	void enterGeneratorExpression(JSParser.GeneratorExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#generatorExpression}.
	 * @param ctx the parse tree
	 */
	void exitGeneratorExpression(JSParser.GeneratorExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#generatorBody}.
	 * @param ctx the parse tree
	 */
	void enterGeneratorBody(JSParser.GeneratorBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#generatorBody}.
	 * @param ctx the parse tree
	 */
	void exitGeneratorBody(JSParser.GeneratorBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassDeclaration(JSParser.ClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassDeclaration(JSParser.ClassDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#classExpression}.
	 * @param ctx the parse tree
	 */
	void enterClassExpression(JSParser.ClassExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#classExpression}.
	 * @param ctx the parse tree
	 */
	void exitClassExpression(JSParser.ClassExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#classTail}.
	 * @param ctx the parse tree
	 */
	void enterClassTail(JSParser.ClassTailContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#classTail}.
	 * @param ctx the parse tree
	 */
	void exitClassTail(JSParser.ClassTailContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#classHeritage}.
	 * @param ctx the parse tree
	 */
	void enterClassHeritage(JSParser.ClassHeritageContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#classHeritage}.
	 * @param ctx the parse tree
	 */
	void exitClassHeritage(JSParser.ClassHeritageContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#classBody}.
	 * @param ctx the parse tree
	 */
	void enterClassBody(JSParser.ClassBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#classBody}.
	 * @param ctx the parse tree
	 */
	void exitClassBody(JSParser.ClassBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#classElementList}.
	 * @param ctx the parse tree
	 */
	void enterClassElementList(JSParser.ClassElementListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#classElementList}.
	 * @param ctx the parse tree
	 */
	void exitClassElementList(JSParser.ClassElementListContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#classElement}.
	 * @param ctx the parse tree
	 */
	void enterClassElement(JSParser.ClassElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#classElement}.
	 * @param ctx the parse tree
	 */
	void exitClassElement(JSParser.ClassElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#arrayLiteral}.
	 * @param ctx the parse tree
	 */
	void enterArrayLiteral(JSParser.ArrayLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#arrayLiteral}.
	 * @param ctx the parse tree
	 */
	void exitArrayLiteral(JSParser.ArrayLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#elementList}.
	 * @param ctx the parse tree
	 */
	void enterElementList(JSParser.ElementListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#elementList}.
	 * @param ctx the parse tree
	 */
	void exitElementList(JSParser.ElementListContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#elision}.
	 * @param ctx the parse tree
	 */
	void enterElision(JSParser.ElisionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#elision}.
	 * @param ctx the parse tree
	 */
	void exitElision(JSParser.ElisionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#spreadElement}.
	 * @param ctx the parse tree
	 */
	void enterSpreadElement(JSParser.SpreadElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#spreadElement}.
	 * @param ctx the parse tree
	 */
	void exitSpreadElement(JSParser.SpreadElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#objectLiteral}.
	 * @param ctx the parse tree
	 */
	void enterObjectLiteral(JSParser.ObjectLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#objectLiteral}.
	 * @param ctx the parse tree
	 */
	void exitObjectLiteral(JSParser.ObjectLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#propertyDefinitionList}.
	 * @param ctx the parse tree
	 */
	void enterPropertyDefinitionList(JSParser.PropertyDefinitionListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#propertyDefinitionList}.
	 * @param ctx the parse tree
	 */
	void exitPropertyDefinitionList(JSParser.PropertyDefinitionListContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#propertyDefinition}.
	 * @param ctx the parse tree
	 */
	void enterPropertyDefinition(JSParser.PropertyDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#propertyDefinition}.
	 * @param ctx the parse tree
	 */
	void exitPropertyDefinition(JSParser.PropertyDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#propertyName}.
	 * @param ctx the parse tree
	 */
	void enterPropertyName(JSParser.PropertyNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#propertyName}.
	 * @param ctx the parse tree
	 */
	void exitPropertyName(JSParser.PropertyNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#literalPropertyName}.
	 * @param ctx the parse tree
	 */
	void enterLiteralPropertyName(JSParser.LiteralPropertyNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#literalPropertyName}.
	 * @param ctx the parse tree
	 */
	void exitLiteralPropertyName(JSParser.LiteralPropertyNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#computedPropertyName}.
	 * @param ctx the parse tree
	 */
	void enterComputedPropertyName(JSParser.ComputedPropertyNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#computedPropertyName}.
	 * @param ctx the parse tree
	 */
	void exitComputedPropertyName(JSParser.ComputedPropertyNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#coverInitializedName}.
	 * @param ctx the parse tree
	 */
	void enterCoverInitializedName(JSParser.CoverInitializedNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#coverInitializedName}.
	 * @param ctx the parse tree
	 */
	void exitCoverInitializedName(JSParser.CoverInitializedNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#arguments}.
	 * @param ctx the parse tree
	 */
	void enterArguments(JSParser.ArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#arguments}.
	 * @param ctx the parse tree
	 */
	void exitArguments(JSParser.ArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#argumentList}.
	 * @param ctx the parse tree
	 */
	void enterArgumentList(JSParser.ArgumentListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#argumentList}.
	 * @param ctx the parse tree
	 */
	void exitArgumentList(JSParser.ArgumentListContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#expressionSequence}.
	 * @param ctx the parse tree
	 */
	void enterExpressionSequence(JSParser.ExpressionSequenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#expressionSequence}.
	 * @param ctx the parse tree
	 */
	void exitExpressionSequence(JSParser.ExpressionSequenceContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TernaryExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterTernaryExpression(JSParser.TernaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TernaryExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitTernaryExpression(JSParser.TernaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LogicalAndExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterLogicalAndExpression(JSParser.LogicalAndExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LogicalAndExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitLogicalAndExpression(JSParser.LogicalAndExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PreIncrementExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterPreIncrementExpression(JSParser.PreIncrementExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PreIncrementExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitPreIncrementExpression(JSParser.PreIncrementExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ObjectLiteralExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterObjectLiteralExpression(JSParser.ObjectLiteralExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ObjectLiteralExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitObjectLiteralExpression(JSParser.ObjectLiteralExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SuperMemberIndexExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterSuperMemberIndexExpression(JSParser.SuperMemberIndexExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SuperMemberIndexExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitSuperMemberIndexExpression(JSParser.SuperMemberIndexExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NewTargetExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterNewTargetExpression(JSParser.NewTargetExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NewTargetExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitNewTargetExpression(JSParser.NewTargetExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code InExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterInExpression(JSParser.InExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code InExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitInExpression(JSParser.InExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LogicalOrExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterLogicalOrExpression(JSParser.LogicalOrExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LogicalOrExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitLogicalOrExpression(JSParser.LogicalOrExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NotExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterNotExpression(JSParser.NotExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NotExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitNotExpression(JSParser.NotExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SuperMemberDotExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterSuperMemberDotExpression(JSParser.SuperMemberDotExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SuperMemberDotExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitSuperMemberDotExpression(JSParser.SuperMemberDotExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PreDecreaseExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterPreDecreaseExpression(JSParser.PreDecreaseExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PreDecreaseExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitPreDecreaseExpression(JSParser.PreDecreaseExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ArgumentsExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterArgumentsExpression(JSParser.ArgumentsExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ArgumentsExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitArgumentsExpression(JSParser.ArgumentsExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ThisExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterThisExpression(JSParser.ThisExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ThisExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitThisExpression(JSParser.ThisExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunctionExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterFunctionExpression(JSParser.FunctionExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunctionExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitFunctionExpression(JSParser.FunctionExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code UnaryMinusExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterUnaryMinusExpression(JSParser.UnaryMinusExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code UnaryMinusExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitUnaryMinusExpression(JSParser.UnaryMinusExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AssignmentExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentExpression(JSParser.AssignmentExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AssignmentExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentExpression(JSParser.AssignmentExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PostDecreaseExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterPostDecreaseExpression(JSParser.PostDecreaseExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PostDecreaseExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitPostDecreaseExpression(JSParser.PostDecreaseExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TypeofExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterTypeofExpression(JSParser.TypeofExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TypeofExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitTypeofExpression(JSParser.TypeofExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code InstanceofExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterInstanceofExpression(JSParser.InstanceofExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code InstanceofExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitInstanceofExpression(JSParser.InstanceofExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code UnaryPlusExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterUnaryPlusExpression(JSParser.UnaryPlusExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code UnaryPlusExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitUnaryPlusExpression(JSParser.UnaryPlusExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DeleteExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterDeleteExpression(JSParser.DeleteExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DeleteExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitDeleteExpression(JSParser.DeleteExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ArrowFunctionExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterArrowFunctionExpression(JSParser.ArrowFunctionExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ArrowFunctionExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitArrowFunctionExpression(JSParser.ArrowFunctionExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code EqualityExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterEqualityExpression(JSParser.EqualityExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code EqualityExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitEqualityExpression(JSParser.EqualityExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BitXOrExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterBitXOrExpression(JSParser.BitXOrExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BitXOrExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitBitXOrExpression(JSParser.BitXOrExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MultiplicativeExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicativeExpression(JSParser.MultiplicativeExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MultiplicativeExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicativeExpression(JSParser.MultiplicativeExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BitShiftExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterBitShiftExpression(JSParser.BitShiftExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BitShiftExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitBitShiftExpression(JSParser.BitShiftExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ParenthesizedExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterParenthesizedExpression(JSParser.ParenthesizedExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ParenthesizedExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitParenthesizedExpression(JSParser.ParenthesizedExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AdditiveExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterAdditiveExpression(JSParser.AdditiveExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AdditiveExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitAdditiveExpression(JSParser.AdditiveExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code RelationalExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterRelationalExpression(JSParser.RelationalExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code RelationalExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitRelationalExpression(JSParser.RelationalExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PostIncrementExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterPostIncrementExpression(JSParser.PostIncrementExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PostIncrementExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitPostIncrementExpression(JSParser.PostIncrementExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code YieldExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterYieldExpression(JSParser.YieldExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code YieldExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitYieldExpression(JSParser.YieldExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BitNotExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterBitNotExpression(JSParser.BitNotExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BitNotExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitBitNotExpression(JSParser.BitNotExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NewExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterNewExpression(JSParser.NewExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NewExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitNewExpression(JSParser.NewExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LiteralExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterLiteralExpression(JSParser.LiteralExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LiteralExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitLiteralExpression(JSParser.LiteralExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ArrayLiteralExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterArrayLiteralExpression(JSParser.ArrayLiteralExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ArrayLiteralExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitArrayLiteralExpression(JSParser.ArrayLiteralExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MemberDotExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterMemberDotExpression(JSParser.MemberDotExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MemberDotExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitMemberDotExpression(JSParser.MemberDotExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MemberIndexExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterMemberIndexExpression(JSParser.MemberIndexExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MemberIndexExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitMemberIndexExpression(JSParser.MemberIndexExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IdentifierExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterIdentifierExpression(JSParser.IdentifierExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IdentifierExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitIdentifierExpression(JSParser.IdentifierExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BitAndExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterBitAndExpression(JSParser.BitAndExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BitAndExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitBitAndExpression(JSParser.BitAndExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BitOrExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterBitOrExpression(JSParser.BitOrExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BitOrExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitBitOrExpression(JSParser.BitOrExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AssignmentOperatorExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOperatorExpression(JSParser.AssignmentOperatorExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AssignmentOperatorExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOperatorExpression(JSParser.AssignmentOperatorExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SuperCallExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterSuperCallExpression(JSParser.SuperCallExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SuperCallExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitSuperCallExpression(JSParser.SuperCallExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code VoidExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterVoidExpression(JSParser.VoidExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code VoidExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitVoidExpression(JSParser.VoidExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CallTemplateLiteralExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterCallTemplateLiteralExpression(JSParser.CallTemplateLiteralExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CallTemplateLiteralExpression}
	 * labeled alternative in {@link JSParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitCallTemplateLiteralExpression(JSParser.CallTemplateLiteralExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#script}.
	 * @param ctx the parse tree
	 */
	void enterScript(JSParser.ScriptContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#script}.
	 * @param ctx the parse tree
	 */
	void exitScript(JSParser.ScriptContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#scriptBody}.
	 * @param ctx the parse tree
	 */
	void enterScriptBody(JSParser.ScriptBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#scriptBody}.
	 * @param ctx the parse tree
	 */
	void exitScriptBody(JSParser.ScriptBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#module}.
	 * @param ctx the parse tree
	 */
	void enterModule(JSParser.ModuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#module}.
	 * @param ctx the parse tree
	 */
	void exitModule(JSParser.ModuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#moduleBody}.
	 * @param ctx the parse tree
	 */
	void enterModuleBody(JSParser.ModuleBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#moduleBody}.
	 * @param ctx the parse tree
	 */
	void exitModuleBody(JSParser.ModuleBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#moduleItemList}.
	 * @param ctx the parse tree
	 */
	void enterModuleItemList(JSParser.ModuleItemListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#moduleItemList}.
	 * @param ctx the parse tree
	 */
	void exitModuleItemList(JSParser.ModuleItemListContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#moduleItem}.
	 * @param ctx the parse tree
	 */
	void enterModuleItem(JSParser.ModuleItemContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#moduleItem}.
	 * @param ctx the parse tree
	 */
	void exitModuleItem(JSParser.ModuleItemContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#importDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterImportDeclaration(JSParser.ImportDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#importDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitImportDeclaration(JSParser.ImportDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#importClause}.
	 * @param ctx the parse tree
	 */
	void enterImportClause(JSParser.ImportClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#importClause}.
	 * @param ctx the parse tree
	 */
	void exitImportClause(JSParser.ImportClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#importedDefaultBinding}.
	 * @param ctx the parse tree
	 */
	void enterImportedDefaultBinding(JSParser.ImportedDefaultBindingContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#importedDefaultBinding}.
	 * @param ctx the parse tree
	 */
	void exitImportedDefaultBinding(JSParser.ImportedDefaultBindingContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#nameSpaceImport}.
	 * @param ctx the parse tree
	 */
	void enterNameSpaceImport(JSParser.NameSpaceImportContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#nameSpaceImport}.
	 * @param ctx the parse tree
	 */
	void exitNameSpaceImport(JSParser.NameSpaceImportContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#namedImports}.
	 * @param ctx the parse tree
	 */
	void enterNamedImports(JSParser.NamedImportsContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#namedImports}.
	 * @param ctx the parse tree
	 */
	void exitNamedImports(JSParser.NamedImportsContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#fromClause}.
	 * @param ctx the parse tree
	 */
	void enterFromClause(JSParser.FromClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#fromClause}.
	 * @param ctx the parse tree
	 */
	void exitFromClause(JSParser.FromClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#importsList}.
	 * @param ctx the parse tree
	 */
	void enterImportsList(JSParser.ImportsListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#importsList}.
	 * @param ctx the parse tree
	 */
	void exitImportsList(JSParser.ImportsListContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#importSpecifier}.
	 * @param ctx the parse tree
	 */
	void enterImportSpecifier(JSParser.ImportSpecifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#importSpecifier}.
	 * @param ctx the parse tree
	 */
	void exitImportSpecifier(JSParser.ImportSpecifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#moduleSpecifier}.
	 * @param ctx the parse tree
	 */
	void enterModuleSpecifier(JSParser.ModuleSpecifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#moduleSpecifier}.
	 * @param ctx the parse tree
	 */
	void exitModuleSpecifier(JSParser.ModuleSpecifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#importedBinding}.
	 * @param ctx the parse tree
	 */
	void enterImportedBinding(JSParser.ImportedBindingContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#importedBinding}.
	 * @param ctx the parse tree
	 */
	void exitImportedBinding(JSParser.ImportedBindingContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#exportDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterExportDeclaration(JSParser.ExportDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#exportDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitExportDeclaration(JSParser.ExportDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#exportClause}.
	 * @param ctx the parse tree
	 */
	void enterExportClause(JSParser.ExportClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#exportClause}.
	 * @param ctx the parse tree
	 */
	void exitExportClause(JSParser.ExportClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#exportsList}.
	 * @param ctx the parse tree
	 */
	void enterExportsList(JSParser.ExportsListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#exportsList}.
	 * @param ctx the parse tree
	 */
	void exitExportsList(JSParser.ExportsListContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#exportSpecifier}.
	 * @param ctx the parse tree
	 */
	void enterExportSpecifier(JSParser.ExportSpecifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#exportSpecifier}.
	 * @param ctx the parse tree
	 */
	void exitExportSpecifier(JSParser.ExportSpecifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOperator(JSParser.AssignmentOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOperator(JSParser.AssignmentOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(JSParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(JSParser.LiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#numericLiteral}.
	 * @param ctx the parse tree
	 */
	void enterNumericLiteral(JSParser.NumericLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#numericLiteral}.
	 * @param ctx the parse tree
	 */
	void exitNumericLiteral(JSParser.NumericLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#identifierName}.
	 * @param ctx the parse tree
	 */
	void enterIdentifierName(JSParser.IdentifierNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#identifierName}.
	 * @param ctx the parse tree
	 */
	void exitIdentifierName(JSParser.IdentifierNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#identifierReference}.
	 * @param ctx the parse tree
	 */
	void enterIdentifierReference(JSParser.IdentifierReferenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#identifierReference}.
	 * @param ctx the parse tree
	 */
	void exitIdentifierReference(JSParser.IdentifierReferenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#bindingIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterBindingIdentifier(JSParser.BindingIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#bindingIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitBindingIdentifier(JSParser.BindingIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#labelIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterLabelIdentifier(JSParser.LabelIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#labelIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitLabelIdentifier(JSParser.LabelIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#reservedWord}.
	 * @param ctx the parse tree
	 */
	void enterReservedWord(JSParser.ReservedWordContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#reservedWord}.
	 * @param ctx the parse tree
	 */
	void exitReservedWord(JSParser.ReservedWordContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#keyword}.
	 * @param ctx the parse tree
	 */
	void enterKeyword(JSParser.KeywordContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#keyword}.
	 * @param ctx the parse tree
	 */
	void exitKeyword(JSParser.KeywordContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#futureReservedWord}.
	 * @param ctx the parse tree
	 */
	void enterFutureReservedWord(JSParser.FutureReservedWordContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#futureReservedWord}.
	 * @param ctx the parse tree
	 */
	void exitFutureReservedWord(JSParser.FutureReservedWordContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#getter}.
	 * @param ctx the parse tree
	 */
	void enterGetter(JSParser.GetterContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#getter}.
	 * @param ctx the parse tree
	 */
	void exitGetter(JSParser.GetterContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#setter}.
	 * @param ctx the parse tree
	 */
	void enterSetter(JSParser.SetterContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#setter}.
	 * @param ctx the parse tree
	 */
	void exitSetter(JSParser.SetterContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#eos}.
	 * @param ctx the parse tree
	 */
	void enterEos(JSParser.EosContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#eos}.
	 * @param ctx the parse tree
	 */
	void exitEos(JSParser.EosContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#eof}.
	 * @param ctx the parse tree
	 */
	void enterEof(JSParser.EofContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#eof}.
	 * @param ctx the parse tree
	 */
	void exitEof(JSParser.EofContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#templateLiteral}.
	 * @param ctx the parse tree
	 */
	void enterTemplateLiteral(JSParser.TemplateLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#templateLiteral}.
	 * @param ctx the parse tree
	 */
	void exitTemplateLiteral(JSParser.TemplateLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#templateSpans}.
	 * @param ctx the parse tree
	 */
	void enterTemplateSpans(JSParser.TemplateSpansContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#templateSpans}.
	 * @param ctx the parse tree
	 */
	void exitTemplateSpans(JSParser.TemplateSpansContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSParser#templateMiddleList}.
	 * @param ctx the parse tree
	 */
	void enterTemplateMiddleList(JSParser.TemplateMiddleListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSParser#templateMiddleList}.
	 * @param ctx the parse tree
	 */
	void exitTemplateMiddleList(JSParser.TemplateMiddleListContext ctx);
}