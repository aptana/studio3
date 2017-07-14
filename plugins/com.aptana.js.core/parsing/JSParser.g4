/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 by Bart Kiers
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
parser grammar JSParser;

options {tokenVocab = JSLexer;}

@parser::members {
  
    /**
     * Returns {@code true} iff on the current index of the parser's
     * token stream a token of the given {@code type} exists on the
     * {@code HIDDEN} channel.
     *
     * @param type
     *         the type of the token on the {@code HIDDEN} channel
     *         to check.
     *
     * @return {@code true} iff on the current index of the parser's
     * token stream a token of the given {@code type} exists on the
     * {@code HIDDEN} channel.
     */
    private boolean here(final int type) {
        // This assumes use of a token stream with a buffer that does filtering (CommonTokenStream, but not UnbufferedTokenStream)
    	int possibleIndexEosToken = this.getCurrentToken().getTokenIndex() - 1;
    	if (possibleIndexEosToken < 0) {
    		return false;
    	}
    	Token ahead = _input.get(possibleIndexEosToken);

        // Check if the token resides on the HIDDEN channel and if it's of the
        // provided type.
        return (ahead.getChannel() == Lexer.HIDDEN) && (ahead.getType() == type);
    }

    /**
     * Returns {@code true} iff on the current index of the parser's
     * token stream a token exists on the {@code HIDDEN} channel which
     * either is a line terminator, or is a multi line comment that
     * contains a line terminator.
     *
     * @return {@code true} iff on the current index of the parser's
     * token stream a token exists on the {@code HIDDEN} channel which
     * either is a line terminator, or is a multi line comment that
     * contains a line terminator.
     */
    private boolean lineTerminatorAhead() {

        // This assumes use of a token stream with a buffer that does filtering (CommonTokenStream, but not UnbufferedTokenStream)
    	int possibleIndexEosToken = this.getCurrentToken().getTokenIndex() - 1;
    	if (possibleIndexEosToken < 0) {
    		return false;
    	}
    	Token ahead = _input.get(possibleIndexEosToken);

        if (ahead.getChannel() != Lexer.HIDDEN) {
            // We're only interested in tokens on the HIDDEN channel.
            return false;
        }

        if (ahead.getType() == LineTerminator) {
            // There is definitely a line terminator ahead.
            return true;
        }

        if (ahead.getType() == WhiteSpaces) {
            // Get the token ahead of the current whitespaces.
            possibleIndexEosToken = this.getCurrentToken().getTokenIndex() - 2;
            if (possibleIndexEosToken < 0) {
    			return false;
    		}
            ahead = _input.get(possibleIndexEosToken);
        }

        // Get the token's text and type.
        String text = ahead.getText();
        int type = ahead.getType();

        // Check if the token is, or contains a line terminator.
        return (type == MultiLineComment && (text.contains("\r") || text.contains("\n"))) ||
                (type == LineTerminator);
    }                                
}

/// Program :
///    Module
///    Script
program
 : module
 ;

/// Statement :
///     BlockStatement
///     VariableStatement
///     EmptyStatement
///     ExpressionStatement
///     IfStatement
///     BreakableStatement
///     ContinueStatement
///     BreakStatement
///     ReturnStatement
///     WithStatement
///     LabelledStatement
///     ThrowStatement
///     TryStatement
///     DebuggerStatement
statement
 : blockStatement
 | variableStatement
 | emptyStatement
 | expressionStatement
 | ifStatement
 | breakableStatement
 | continueStatement
 | breakStatement
 | returnStatement
 | withStatement
 | labelledStatement
 | throwStatement
 | tryStatement
 | debuggerStatement
 ;

/// Declaration :
///     HoistableDeclaration
///     ClassDeclaration
///     LexicalDeclaration
declaration
 : hoistableDeclaration
 | classDeclaration
 | lexicalDeclaration
 ;

/// HoistableDeclaration :
///     FunctionDeclaration
///     GeneratorDeclaration
hoistableDeclaration
 : functionDeclaration
 | generatorDeclaration
 ;

/// BreakableStatement :
///     IterationStatement
///     SwitchStatement
breakableStatement
 : iterationStatement
 | switchStatement
 ;

/// BlockStatement :
///     Block
blockStatement
 : block
 ;

/// Block :
///     { StatementList? }
block
 : '{' statementList? '}'
 ;

/// StatementList :
///     StatementListItem
///     StatementList StatementListItem
statementList
 : statementListItem+
 ;

/// StatementListItem :
///     Statement
///     Declaration
statementListItem
 : statement
 | declaration
 ;

/// LexicalDeclaration :
///     LetOrConst BindingList ;
lexicalDeclaration
 : letOrConst bindingList eos
 ;

/// LetOrConst :
///     let
///     const
letOrConst
 : Let
 | Const
 ;

/// BindingList :
///     LexicalBinding
///     BindingList , LexicalBinding
bindingList
 : lexicalBinding (',' lexicalBinding )*
 ;

/// LexicalBinding :
///     BindingIdentifier Initializer?
///     BindingPattern Initializer
lexicalBinding
 : bindingIdentifier initializer?
 | bindingPattern initializer
 ;

/// VariableStatement :
///     var VariableDeclarationList ;
variableStatement
 : variableDeclarationStatement eos
 ;

/// VariableDeclarationList :
///     VariableDeclaration
///     VariableDeclarationList , VariableDeclaration
variableDeclarationList
 : variableDeclaration ( ',' variableDeclaration )*
 ;

/// VariableDeclaration :
///     BindingIdentifier Initializer?
///     BindingPattern Initializer
variableDeclaration
 : bindingIdentifier initializer?
 | bindingPattern initializer
 ;

/// BindingPattern :
///     ObjectBindingPattern
///     ArrayBindingPattern
bindingPattern
 : objectBindingPattern
 | arrayBindingPattern
 ;

/// ObjectBindingPattern :
///     { }
///     { BindingPropertyList }
///     { BindingPropertyList , }
objectBindingPattern
 : '{' '}'
 | '{' bindingPropertyList '}'
 | '{' bindingPropertyList ',' '}'
 ;

/// ArrayBindingPattern :
///     [ Elision? BindingRestElement? ]
///     [ BindingElementList ]
///     [ BindingElementList , Elision? BindingRestElement? ]
arrayBindingPattern
 : '[' elision? bindingRestElement? ']'
 | '[' bindingElementList ']'
 | '[' bindingElementList ',' elision? bindingRestElement? ']'
 ;

/// BindingPropertyList :
///     BindingProperty
///     BindingPropertyList , BindingProperty
bindingPropertyList
 : bindingProperty ( ',' bindingProperty )*
 ;

/// BindingElementList :
///     BindingElisionElement
///     BindingElementList , BindingElisionElement
bindingElementList
 : bindingElisionElement ( ',' bindingElisionElement )*
 ;

/// BindingElisionElement :
///     Elision? BindingElement
bindingElisionElement
 : elision? bindingElement
 ;

/// BindingProperty :
///     SingleNameBinding
///     PropertyName : BindingElement
bindingProperty
 : singleNameBinding
 | propertyName ':' bindingElement
 ;

/// BindingElement :
///     SingleNameBinding
///     BindingPattern Initializer?
bindingElement
 : singleNameBinding
 | bindingPattern initializer?
 ;

/// SingleNameBinding :
///     BindingIdentifier Initializer?
singleNameBinding
 : bindingIdentifier initializer?
 ;

/// BindingRestElement :
///     ... BindingIdentifier
bindingRestElement
 : '...' bindingIdentifier
 ;

/// initializer :
///     = AssignmentExpression
initializer
 : '=' singleExpression
 ;

/// EmptyStatement :
///     ;
emptyStatement
 : SemiColon
 ;

/// ExpressionStatement :
///     [lookahead ∉ {{, function, class, let [}] Expression ;
expressionStatement
 : {(_input.LA(1) != OpenBrace) && (_input.LA(1) != Function) && (_input.LA(1) != Class) && ((_input.LA(1) == Let) ? _input.LA(2) != OpenBracket : true)}? expressionSequence eos
 ;

/// IfStatement :
///     if ( Expression ) Statement else Statement
///     if ( Expression ) Statement
ifStatement
 : If '(' expressionSequence ')' statement ( Else statement )?
 ;

/// IterationStatement :
///     do Statement while ( Expression ) ;
///     while ( Expression ) Statement
///     for ( [lookahead ∉ {let [}] Expression? ; Expression? ; Expression? ) Statement
///     for ( var VariableDeclarationList ; Expression? ; Expression? ) Statement
///     for ( LexicalDeclaration Expression? ; Expression? ) Statement
///     for ( [lookahead ∉ {let [}] LeftHandSideExpression in Expression ) Statement
///     for ( var ForBinding in Expression ) Statement
///     for ( ForDeclaration in Expression ) Statement
///     for ( [lookahead ≠ let ] LeftHandSideExpression of AssignmentExpression ) Statement
///     for ( var ForBinding of AssignmentExpression ) Statement
///     for ( ForDeclaration of AssignmentExpression ) Statement
iterationStatement
 : Do statement While '(' expressionSequence ')' eos                                                                                  # DoWhileStatement
 | While '(' expressionSequence ')' statement                                                                                         # WhileStatement
 | For '(' {((_input.LA(1) == Let) ? _input.LA(2) != OpenBracket : true)}? expressionSequence? ';' expressionSequence? ';' expressionSequence? ')' statement # ForLoopStatement
 | For '(' variableDeclarationStatement ';' expressionSequence? ';' expressionSequence? ')' statement                                 # ForVarLoopStatement
 | For '(' lexicalDeclaration expressionSequence? ';' expressionSequence? ')' statement                                               # ForLexicalLoopStatement
 | For '(' {((_input.LA(1) == Let) ? _input.LA(2) != OpenBracket : true)}? singleExpression In expressionSequence ')' statement       # ForInStatement
 | For '(' varForDeclaration In expressionSequence ')' statement                                                                      # ForVarInStatement
 | For '(' forDeclaration In expressionSequence ')' statement                                                                         # ForLexicalInStatement
 | For '(' {(_input.LA(1) != Let)}? singleExpression {_input.LT(1).getText().equals("of")}? Identifier singleExpression ')' statement # ForOfStatement
 | For '(' varForDeclaration {_input.LT(1).getText().equals("of")}? Identifier singleExpression ')' statement                         # ForVarOfStatement
 | For '(' forDeclaration {_input.LT(1).getText().equals("of")}? Identifier singleExpression ')' statement                            # ForLexicalOfStatement
 ;


// I introduced this rule to combine VariableStatement and Var decls in a for loop
variableDeclarationStatement
 : Var variableDeclarationList
 ;
 
varForDeclaration
 : Var forBinding
 ;

/// ForDeclaration :
///     LetOrConst ForBinding
forDeclaration
 : letOrConst forBinding
 ;

/// ForBinding :
///     BindingIdentifier
///     BindingPattern
forBinding
 : bindingIdentifier
 | bindingPattern
 ;

/// ContinueStatement :
///     continue ;
///     continue [no LineTerminator here] Identifier ;
continueStatement
 : Continue ({!here(LineTerminator)}? Identifier)? eos
 ;

/// BreakStatement :
///     break ;
///     break [no LineTerminator here] Identifier ;
breakStatement
 : Break ({!here(LineTerminator)}? Identifier)? eos
 ;

/// ReturnStatement :
///     return ;
///     return [no LineTerminator here] Expression ;
returnStatement
 : Return ({!here(LineTerminator)}? expressionSequence)? eos
 ;

/// WithStatement :
///     with ( Expression ) Statement
withStatement
 : With '(' expressionSequence ')' statement
 ;

/// SwitchStatement :
///     switch ( Expression ) CaseBlock
switchStatement
 : Switch '(' expressionSequence ')' caseBlock
 ;

/// CaseBlock :
///     { CaseClauses? }
///     { CaseClauses? DefaultClause CaseClauses? }
caseBlock
 : '{' caseClauses? ( defaultClause caseClauses? )? '}'
 ;

/// CaseClauses :
///     CaseClause
///     CaseClauses CaseClause
caseClauses
 : caseClause+
 ;

/// CaseClause :
///     case Expression ':' StatementList?
caseClause
 : Case expressionSequence ':' statementList?
 ;

/// DefaultClause :
///     default ':' StatementList?
defaultClause
 : Default ':' statementList?
 ;

/// LabelledStatement :
///     Identifier ':' Statement
labelledStatement
 : Identifier ':' labelledItem
 ;

/// LabelledItem :
///     Statement
///     FunctionDeclaration
labelledItem
 : statement
 | functionDeclaration
 ;

/// ThrowStatement :
///     throw [no LineTerminator here] Expression ;
throwStatement
 : Throw {!here(LineTerminator)}? expressionSequence eos
 ;

/// TryStatement :
///     try Block Catch
///     try Block Finally
///     try Block Catch Finally
tryStatement
 : Try block catchProduction
 | Try block finallyProduction
 | Try block catchProduction finallyProduction
 ;

/// Catch :
///     catch ( CatchParameter ) Block
catchProduction
 : Catch '(' catchParameter ')' block
 ;

/// Finally :
///     finally Block
finallyProduction
 : Finally block
 ;

/// CatchParameter :
///     BindingIdentifier
///     BindingPattern
catchParameter
 : bindingIdentifier
 | bindingPattern
 ;

/// DebuggerStatement :
///     debugger ;
debuggerStatement
 : Debugger eos
 ;

/// FunctionDeclaration :
///     function BindingIdentifier ( FormalParameters ) { FunctionBody }
///     [+Default] function ( FormalParameters ) { FunctionBody }
functionDeclaration
 : Function bindingIdentifier '(' formalParameters ')' '{' functionBody '}'
 ;

// Rolled into singleExpression
/// FunctionExpression :
///     function BindingIdentifier? ( FormalParameters ) { FunctionBody }
functionExpression
 : Function '(' formalParameters ')' '{' functionBody '}'
 | functionDeclaration
 ;

/// StrictFormalParameters :
///     FormalParameters
strictFormalParameters
 : formalParameters
 ;

/// FormalParameters :
///     [empty]
///     FormalParameterList
formalParameters
 :
 | formalParameterList
 ;

/// FormalParameterList :
///     FunctionRestParameter
///     FormalsList
///     FormalsList , FunctionRestParameter
formalParameterList
 : functionRestParameter
 | formalsList ( ',' functionRestParameter )?
 ;

/// FormalsList :
///     FormalParameter
///     FormalsList , FormalParameter
formalsList
 : formalParameter ( ',' formalParameter )*
 ;

/// FunctionRestParameter :
///     BindingRestElement
functionRestParameter
 : bindingRestElement
 ;

/// FormalParameter :
///     BindingElement
formalParameter
 : bindingElement
 ;

/// FunctionBody :
///     FunctionStatementList
functionBody
 : functionStatementList
 ;
  
/// FunctionStatementList :
///     StatementList?
functionStatementList
 : statementList?
 ;

/// ArrowFunction :
///     ArrowParameters [no LineTerminator here] => ConciseBody
arrowFunction
 : arrowParameters {!here(LineTerminator)}? '=>' conciseBody
 ;

/// ArrowParameters :
///     BindingIdentifier
///     CoverParenthesizedExpressionAndArrowParameterList
arrowParameters
 : bindingIdentifier
 | coverParenthesizedExpressionAndArrowParameterList
 ;

/// ConciseBody :
///     [lookahead ≠ { ] AssignmentExpression
///     { FunctionBody }
conciseBody
 : {(_input.LA(1) != OpenBrace)}? singleExpression
 | '{' functionBody '}'
 ;

/// CoverParenthesizedExpressionAndArrowParameterList :
///     ( Expression )
///     ( )
///     ( ... BindingIdentifier )
///     ( Expression , ... BindingIdentifier
coverParenthesizedExpressionAndArrowParameterList
 : '(' expressionSequence ')'
 | '(' ')'
 | '(' bindingRestElement ')'
 | '(' expressionSequence ',' bindingRestElement ')'
 ;

/// MethodDefinition :
///     PropertyName ( StrictFormalParameters ) { FunctionBody }
///     GeneratorMethod
///     get PropertyName ( ) { FunctionBody }
///     set PropertyName ( PropertySetParameterList ) { FunctionBody }
methodDefinition
 : propertyName '(' strictFormalParameters ')' '{' functionBody '}'
 | generatorMethod
 | {_input.LT(1).getText().equals("get")}? Identifier propertyName '(' ')' '{' functionBody '}'
 | {_input.LT(1).getText().equals("set")}? Identifier propertyName '(' propertySetParameterList ')' '{' functionBody '}'
;

/// PropertySetParameterList :
///     FormalParameter
propertySetParameterList
 : formalParameter
 ;

/// GeneratorMethod :
///     * PropertyName ( StrictFormalParameters ) { GeneratorBody }
generatorMethod
 : '*' propertyName '(' strictFormalParameters ')' '{' generatorBody '}'
 ;

/// GeneratorDeclaration :
///     function * BindingIdentifier ( FormalParameters ) { GeneratorBody }
///     [+Default] function * ( FormalParameters ) { GeneratorBody }
generatorDeclaration
 : Function '*' bindingIdentifier '(' formalParameters ')' '{' generatorBody '}'
///     [+Default] function * ( FormalParameters ) { GeneratorBody }
 ;

/// GeneratorExpression :
///     function * BindingIdentifier? ( FormalParameters ) { GeneratorBody }
generatorExpression
 : Function '*' bindingIdentifier? '(' formalParameters ')' '{' generatorBody '}'
 ;

/// GeneratorBody :
///     FunctionBody
generatorBody
 : functionBody
 ;

/// ClassDeclaration :
///     class BindingIdentifier ClassTail
///     [+Default] class ClassTail
classDeclaration
 : Class bindingIdentifier classTail
///     [+Default] class ClassTail
 ;

/// ClassExpression :
///     class BindingIdentifier? ClassTail
classExpression
 : Class bindingIdentifier? classTail
 ;

/// ClassTail :
///     ClassHeritage? { ClassBody? }
classTail
 : classHeritage? '{' classBody? '}'
 ;

/// ClassHeritage :
///     extends LeftHandSideExpression
classHeritage
 : Extends singleExpression
 ;

/// ClassBody :
///     ClassElementList
classBody
 : classElementList
 ;

/// ClassElementList :
///     ClassElement
///     ClassElementList ClassElement
classElementList
 : classElement+
 ;

/// ClassElement :
///     MethodDefinition
///     static MethodDefinition
///     ;
classElement
 : methodDefinition
 | Static methodDefinition
 | SemiColon
 ;

/// ArrayLiteral :
///     [ Elision? ]
///     [ ElementList ]
///     [ ElementList , Elision? ]
arrayLiteral
// : '[' elementList? ','? elision? ']'
 : '[' elision? ']'
 | '[' elementList ']'
 | '[' elementList ',' elision? ']'
 ;


/// ElementList :
///     Elision? AssignmentExpression
///     Elision? SpreadElement
///     ElementList , Elision? AssignmentExpression
///     ElementList , Elision? SpreadElement
elementList
 : elision? singleExpression
 | elision? spreadElement
 | elementList ',' elision? singleExpression
 | elementList ',' elision? spreadElement
 ;

/// Elision :
///     ,
///     Elision ,
elision
 : ','+
 ;

/// SpreadElement :
///     ... AssignmentExpression
spreadElement
 : '...' singleExpression
 ;

/// ObjectLiteral :
///     { }
///     { PropertyDefinitionList }
///     { PropertyDefinitionList , }
objectLiteral
 : '{' '}'
 | '{' propertyDefinitionList ','? '}'
 ;

/// PropertyDefinitionList :
///     PropertyDefinition
///     PropertyDefinitionList , PropertyDefinition
propertyDefinitionList
 : propertyDefinition ( ',' propertyDefinition )*
 ;

/// PropertyDefinition :
///     IdentifierReference
///     CoverInitializedName
///     PropertyName : AssignmentExpression
///     MethodDefinition
propertyDefinition
 : identifierReference
 | coverInitializedName
 | propertyName ':' singleExpression
 | methodDefinition
 ;

/// PropertyName :
///     LiteralPropertyName
///     ComputedPropertyName
propertyName
 : literalPropertyName
 | computedPropertyName
 ;

/// LiteralPropertyName :
///     IdentifierName
///     StringLiteral
///     NumericLiteral
literalPropertyName
 : identifierName
 | StringLiteral
 | numericLiteral
 ;

/// ComputedPropertyName :
///     [ AssignmentExpression ]
computedPropertyName
 : '[' singleExpression ']'
 ;


/// CoverInitializedName :
///     IdentifierReference Initializer
coverInitializedName
 : identifierReference initializer
 ;

/// Arguments :
///     ( )
///     ( ArgumentList )
arguments
 : '(' argumentList? ')'
 ;
 
/// ArgumentList :
///     AssignmentExpression
///     ... AssignmentExpression
///     ArgumentList , AssignmentExpression
///     ArgumentList , ... AssignmentExpression
argumentList
 : ( spreadElement | singleExpression) ( ',' ( spreadElement | singleExpression) )*
 ;
    
/// Expression :
///     AssignmentExpression
///     Expression , AssignmentExpression
///
/// AssignmentExpression :
///     ConditionalExpression
///     [+Yield] YieldExpression
///     ArrowFunction
///     LeftHandSideExpression = AssignmentExpression
///     LeftHandSideExpression AssignmentOperator AssignmentExpression
///
/// ConditionalExpression :
///     LogicalORExpression
///     LogicalORExpression ? AssignmentExpression : AssignmentExpression
///
/// LogicalORExpression :
///     LogicalANDExpression
///     LogicalORExpression || LogicalANDExpression
///
/// LogicalANDExpression :
///     BitwiseORExpression
///     LogicalANDExpression && BitwiseORExpression
///
/// BitwiseORExpression :
///     BitwiseXORExpression
///     BitwiseORExpression | BitwiseXORExpression
///
/// BitwiseXORExpression :
///     BitwiseANDExpression
///     BitwiseXORExpression ^ BitwiseANDExpression
///
/// BitwiseANDExpression :
///     EqualityExpression
///     BitwiseANDExpression & EqualityExpression
///
/// EqualityExpression :
///     RelationalExpression
///     EqualityExpression == RelationalExpression
///     EqualityExpression != RelationalExpression
///     EqualityExpression === RelationalExpression
///     EqualityExpression !== RelationalExpression
///
/// RelationalExpression :
///     ShiftExpression
///     RelationalExpression < ShiftExpression
///     RelationalExpression > ShiftExpression
///     RelationalExpression <= ShiftExpression
///     RelationalExpression >= ShiftExpression
///     RelationalExpression instanceof ShiftExpression 
///     RelationalExpression in ShiftExpression
///
/// ShiftExpression :
///     AdditiveExpression
///     ShiftExpression << AdditiveExpression
///     ShiftExpression >> AdditiveExpression
///     ShiftExpression >>> AdditiveExpression
/// 
/// AdditiveExpression :
///     MultiplicativeExpression
///     AdditiveExpression + MultiplicativeExpression
///     AdditiveExpression - MultiplicativeExpression
///
/// MultiplicativeExpression :
///     UnaryExpression
///     MultiplicativeExpression * UnaryExpression
///     MultiplicativeExpression / UnaryExpression
///     MultiplicativeExpression % UnaryExpression
///
/// UnaryExpression :
///     PostfixExpression
///     delete UnaryExpression
///     void UnaryExpression
///     typeof UnaryExpression
///     ++ UnaryExpression
///     -- UnaryExpression
///     + UnaryExpression
///     - UnaryExpression
///     ~ UnaryExpression
///     ! UnaryExpression
///
/// PostfixExpression :
///     LeftHandSideExpression
///     LeftHandSideExpression [no LineTerminator here] ++
///     LeftHandSideExpression [no LineTerminator here] --
///
/// LeftHandSideExpression :
///     NewExpression
///     CallExpression
///
/// CallExpression :
///     MemberExpression Arguments
///     SuperCall
///     CallExpression Arguments
///     CallExpression [ Expression ]
///     CallExpression . IdentifierName
///
/// SuperCall :
///     super Arguments
///
/// NewExpression :
///     MemberExpression
///     new NewExpression
///
/// MemberExpression :
///     PrimaryExpression
///     FunctionExpression
///     MemberExpression [ Expression ]
///     MemberExpression . IdentifierName
///     new MemberExpression Arguments
///
/// FunctionExpression :
///     function BindingIdentifier? ( FormalParameters ) { FunctionBody }
///
/// PrimaryExpression :
///     this
///     Identifier
///     Literal
///     ArrayLiteral
///     ObjectLiteral
///     ( Expression )
///
expressionSequence
 : singleExpression ( ',' singleExpression )*
 ;

singleExpression
 : functionExpression                                                        # FunctionExpressionExpression
 | classExpression                                                           # ClassExpressionExpression
 | generatorExpression                                                       # GeneratorExpressionExpression
 | New '.' {_input.LT(1).getText().equals("target")}? Identifier             # NewTargetExpression
 | New singleExpression arguments?                                           # NewExpression
 | singleExpression '[' expressionSequence ']'                               # MemberIndexExpression
 | singleExpression '.' identifierName                                       # MemberDotExpression
 | singleExpression templateLiteral                                          # CallTemplateLiteralExpression
 | singleExpression arguments                                                # ArgumentsExpression
 | singleExpression {!here(LineTerminator)}? '++'                            # PostIncrementExpression
 | singleExpression {!here(LineTerminator)}? '--'                            # PostDecreaseExpression
 | Delete singleExpression                                                   # DeleteExpression
 | Void singleExpression                                                     # VoidExpression
 | Typeof singleExpression                                                   # TypeofExpression
 | '++' singleExpression                                                     # PreIncrementExpression
 | '--' singleExpression                                                     # PreDecreaseExpression
 | '+' singleExpression                                                      # UnaryPlusExpression
 | '-' singleExpression                                                      # UnaryMinusExpression
 | '~' singleExpression                                                      # BitNotExpression
 | '!' singleExpression                                                      # NotExpression
 | singleExpression ( '*' | '/' | '%' ) singleExpression                     # MultiplicativeExpression
 | singleExpression ( '+' | '-' ) singleExpression                           # AdditiveExpression
 | singleExpression ( '<<' | '>>' | '>>>' ) singleExpression                 # BitShiftExpression
 | singleExpression ( '<' | '>' | '<=' | '>=' ) singleExpression             # RelationalExpression
 | singleExpression Instanceof singleExpression                              # InstanceofExpression
 | singleExpression In singleExpression                                      # InExpression
 | singleExpression ( '==' | '!=' | '===' | '!==' ) singleExpression         # EqualityExpression
 | singleExpression '&' singleExpression                                     # BitAndExpression
 | singleExpression '^' singleExpression                                     # BitXOrExpression
 | singleExpression '|' singleExpression                                     # BitOrExpression
 | singleExpression '&&' singleExpression                                    # LogicalAndExpression
 | singleExpression '||' singleExpression                                    # LogicalOrExpression
 | singleExpression '?' singleExpression ':' singleExpression                # TernaryExpression
 | Yield ({!here(LineTerminator)}? '*'? singleExpression)?                   # YieldExpression
 | arrowFunction                                                             # ArrowFunctionExpression
 | singleExpression '=' singleExpression                                     # AssignmentExpression
 | singleExpression assignmentOperator singleExpression                      # AssignmentOperatorExpression
 | This                                                                      # ThisExpression
 | Super                                                                     # SuperExpression
 | Identifier                                                                # IdentifierExpression
 | literal                                                                   # LiteralExpression
 | arrayLiteral                                                              # ArrayLiteralExpression
 | objectLiteral                                                             # ObjectLiteralExpression
 | RegularExpressionLiteral                                                  # RegularExpressionLiteralExpression
 | templateLiteral                                                           # TemplateLiteralExpression
// | coverParenthesizedExpressionAndArrowParameterList                         # CoverParenthesizedExpressionAndArrowParameterListExpression
 | '(' expressionSequence ')'                                                # ParenthesizedExpression
 ;


// A.5 Scripts and Modules

/// Script :
///     ScriptBody?
script
 : scriptBody?
 ;
 
/// ScriptBody :
///     StatementList
scriptBody
 : statementList
 ;

/// Module :
///     ModuleBody?
module
 : moduleItem* EOF
 ;

/// ModuleBody :
///     ModuleItemList
//moduleBody
// : moduleItemList
// ;

/// ModuleItemList :
///     ModuleItem
///     ModuleItemList ModuleItem
//moduleItemList
// : moduleItem+
// ;

/// ModuleItem :
///     ImportDeclaration
///     ExportDeclaration
///     StatementListItem
moduleItem
 : importDeclaration
 | exportDeclaration
 | statementListItem
 ;

/// ImportDeclaration :
///     import ImportClause FromClause ;
///     import ModuleSpecifier ;
importDeclaration
 : Import importClause fromClause eos
 | Import moduleSpecifier eos
 ;

/// ImportClause :
///     ImportedDefaultBinding
///     NameSpaceImport
///     NamedImports
///     ImportedDefaultBinding , NameSpaceImport
///     ImportedDefaultBinding , NamedImports
importClause
 : importedDefaultBinding
 | nameSpaceImport
 | namedImports
 | importedDefaultBinding ',' nameSpaceImport
 | importedDefaultBinding ',' namedImports
 ;

/// ImportedDefaultBinding :
///     ImportedBinding
importedDefaultBinding
 : importedBinding
 ;

/// NameSpaceImport :
///     * as ImportedBinding
nameSpaceImport
 : '*' {_input.LT(1).getText().equals("as")}? Identifier importedBinding
 ;

/// NamedImports :
///     { }
///     { ImportsList }
///     { ImportsList , }
namedImports
 : '{' '}'
 | '{' importsList '}'
 | '{' importsList ',' '}'
 ;

/// FromClause :
///     from ModuleSpecifier
fromClause
 : {_input.LT(1).getText().equals("from")}? Identifier moduleSpecifier
 ;

/// ImportsList :
///     ImportSpecifier
///     ImportsList , ImportSpecifier
importsList
 : importSpecifier ( ',' importSpecifier )*
 ;

/// ImportSpecifier :
///     ImportedBinding
///     IdentifierName as ImportedBinding
importSpecifier
 : importedBinding
 | identifierName {_input.LT(1).getText().equals("as")}? Identifier importedBinding
 ;

/// ModuleSpecifier :
///     StringLiteral
moduleSpecifier
 : StringLiteral
 ;

/// ImportedBinding :
///     BindingIdentifier
importedBinding
 : bindingIdentifier
 ;

/// ExportDeclaration :
///     export * FromClause ;
///     export ExportClause FromClause ;
///     export ExportClause ;
///     export VariableStatement
///     export Declaration
///     export default HoistableDeclaration[Default]
///     export default ClassDeclaration[Default]
///     export default [lookahead ∉ {function, class}] AssignmentExpression[In] ;
exportDeclaration
 : Export '*' fromClause eos
 | Export exportClause fromClause eos
 | Export exportClause eos
 | Export variableStatement
 | Export declaration
 | Export Default hoistableDeclaration
 | Export Default classDeclaration
 | Export Default {(_input.LA(1) != Function) && (_input.LA(1) != Class)}? singleExpression eos
 ;

/// ExportClause :
///     { }
///     { ExportsList }
///     { ExportsList , }
exportClause
 : '{' '}'
 | '{' exportsList '}'
 | '{' exportsList ',' '}'
 ;

/// ExportsList :
///     ExportSpecifier
///     ExportsList , ExportSpecifier
exportsList
 : exportSpecifier ( ',' exportSpecifier )*
 ;

/// ExportSpecifier :
///     IdentifierName
///     IdentifierName as IdentifierName
exportSpecifier
 : identifierName ( {_input.LT(1).getText().equals("as")}? Identifier identifierName )?
 ;

/// AssignmentOperator : one of
///     *=	/=	%=	+=	-=	<<=	>>=	>>>=	&=	^=	|=
assignmentOperator
 : '*=' 
 | '/=' 
 | '%=' 
 | '+=' 
 | '-=' 
 | '<<=' 
 | '>>=' 
 | '>>>=' 
 | '&=' 
 | '^=' 
 | '|='
 ;

literal
 : ( NullLiteral 
   | BooleanLiteral
   | StringLiteral
   )
 | numericLiteral
 ;

numericLiteral
 : DecimalLiteral
 | HexIntegerLiteral
 | OctalIntegerLiteral
 | BinaryIntegerLiteral
 ;

identifierName
 : Identifier
 | reservedWord
 ;

/// IdentifierReference :
///     Identifier
///     [~Yield] yield
identifierReference
 : Identifier
 ;

/// BindingIdentifier :
///     Identifier
///     [~Yield] yield
bindingIdentifier
 : Identifier
 ;

/// LabelIdentifier :
///     Identifier
///     [~Yield] yield
labelIdentifier
 : Identifier
 ;

reservedWord
 : keyword
 | futureReservedWord
 | ( NullLiteral
   | BooleanLiteral
   )
 ;

keyword
 : Break
 | Do
 | Instanceof
 | Typeof
 | Case
 | Else
 | New
 | Var
 | Catch
 | Export
 | Const
 | Finally
 | Super
 | Return
 | Void
 | Class
 | Extends
 | Continue
 | For
 | Switch
 | Yield
 | While
 | Debugger
 | Function
 | This
 | With
 | Default
 | If
 | Throw
 | Delete
 | In
 | Import
 | Try
 ;
 
// FIXME What about let and static?
//  | Let
//  | Static

futureReservedWord
 : Enum
 | Await
 | Implements
 | Private
 | Public
 | Interface
 | Package
 | Protected
 ;

eos
 : SemiColon
 | EOF
 | {lineTerminatorAhead()}?
 | {_input.LT(1).getType() == CloseBrace}?
 ;

eof
 : EOF
 ;

// V8 Handles template literals as:
/// TemplateLiteral
/// :    (TemplateSpan Expression)* TemplateTail
/// ;
///
/// TemplateTail
/// : '`' templateChar* '`'
/// | '}' templateChar* '`'
/// ;
///
/// TemplateSpan
/// : '`' templateChar* '${'
/// | '}' templateChar* '`'
/// ;

/// TemplateLiteral :
///     NoSubstitutionTemplate
///     TemplateHead Expression TemplateSpans
templateLiteral
 : NoSubstitutionTemplate
 | TemplateHead expressionSequence templateSpans
 ;

/// TemplateSpans :
///     TemplateTail
///     TemplateMiddleList TemplateTail
templateSpans
 : TemplateTail
 | templateMiddleList TemplateTail
 ;

/// TemplateMiddleList :
///     TemplateMiddle Expression
///     TemplateMiddleList TemplateMiddle Expression
templateMiddleList
 : TemplateMiddle expressionSequence
 | templateMiddleList TemplateMiddle expressionSequence
 ;
