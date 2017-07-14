// Generated from /Users/cwilliams/repos/studio3/plugins/com.aptana.js.core/parsing/JSParser.g4 by ANTLR 4.7
package com.aptana.js.core.parsing.antlr;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class JSParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		RegularExpressionLiteral=1, LineTerminator=2, OpenBracket=3, CloseBracket=4, 
		OpenParen=5, CloseParen=6, OpenBrace=7, CloseBrace=8, SemiColon=9, Comma=10, 
		Arrow=11, Assign=12, QuestionMark=13, Colon=14, Ellipsis=15, Dot=16, PlusPlus=17, 
		MinusMinus=18, Plus=19, Minus=20, BitNot=21, Not=22, Multiply=23, Divide=24, 
		Modulus=25, RightShiftArithmetic=26, LeftShiftArithmetic=27, RightShiftLogical=28, 
		LessThan=29, MoreThan=30, LessThanEquals=31, GreaterThanEquals=32, Equals=33, 
		NotEquals=34, IdentityEquals=35, IdentityNotEquals=36, BitAnd=37, BitXOr=38, 
		BitOr=39, And=40, Or=41, MultiplyAssign=42, DivideAssign=43, ModulusAssign=44, 
		PlusAssign=45, MinusAssign=46, LeftShiftArithmeticAssign=47, RightShiftArithmeticAssign=48, 
		RightShiftLogicalAssign=49, BitAndAssign=50, BitXorAssign=51, BitOrAssign=52, 
		NullLiteral=53, BooleanLiteral=54, DecimalLiteral=55, HexIntegerLiteral=56, 
		OctalIntegerLiteral=57, BinaryIntegerLiteral=58, Break=59, Do=60, Instanceof=61, 
		Typeof=62, Case=63, Else=64, New=65, Var=66, Catch=67, Finally=68, Return=69, 
		Void=70, Continue=71, For=72, Switch=73, While=74, Debugger=75, Function=76, 
		This=77, With=78, Default=79, If=80, Throw=81, Delete=82, In=83, Try=84, 
		Export=85, Class=86, Extends=87, Const=88, Super=89, Yield=90, Import=91, 
		Enum=92, Await=93, Implements=94, Private=95, Public=96, Interface=97, 
		Package=98, Protected=99, Static=100, Let=101, From=102, Get=103, Set=104, 
		As=105, Of=106, Target=107, Identifier=108, StringLiteral=109, WhiteSpaces=110, 
		MultiLineComment=111, SingleLineComment=112, UnexpectedCharacter=113, 
		NoSubstitutionTemplate=114, TemplateHead=115, TemplateMiddle=116, TemplateTail=117;
	public static final int
		RULE_program = 0, RULE_statement = 1, RULE_declaration = 2, RULE_hoistableDeclaration = 3, 
		RULE_breakableStatement = 4, RULE_blockStatement = 5, RULE_block = 6, 
		RULE_statementList = 7, RULE_statementListItem = 8, RULE_lexicalDeclaration = 9, 
		RULE_letOrConst = 10, RULE_bindingList = 11, RULE_lexicalBinding = 12, 
		RULE_variableStatement = 13, RULE_variableDeclarationList = 14, RULE_variableDeclaration = 15, 
		RULE_bindingPattern = 16, RULE_objectBindingPattern = 17, RULE_arrayBindingPattern = 18, 
		RULE_bindingPropertyList = 19, RULE_bindingElementList = 20, RULE_bindingElisionElement = 21, 
		RULE_bindingProperty = 22, RULE_bindingElement = 23, RULE_singleNameBinding = 24, 
		RULE_bindingRestElement = 25, RULE_initializer = 26, RULE_emptyStatement = 27, 
		RULE_expressionStatement = 28, RULE_ifStatement = 29, RULE_iterationStatement = 30, 
		RULE_variableDeclarationStatement = 31, RULE_varForDeclaration = 32, RULE_forDeclaration = 33, 
		RULE_forBinding = 34, RULE_continueStatement = 35, RULE_breakStatement = 36, 
		RULE_returnStatement = 37, RULE_withStatement = 38, RULE_switchStatement = 39, 
		RULE_caseBlock = 40, RULE_caseClauses = 41, RULE_caseClause = 42, RULE_defaultClause = 43, 
		RULE_labelledStatement = 44, RULE_labelledItem = 45, RULE_throwStatement = 46, 
		RULE_tryStatement = 47, RULE_catchProduction = 48, RULE_finallyProduction = 49, 
		RULE_catchParameter = 50, RULE_debuggerStatement = 51, RULE_functionDeclaration = 52, 
		RULE_functionExpression = 53, RULE_strictFormalParameters = 54, RULE_formalParameters = 55, 
		RULE_formalParameterList = 56, RULE_formalsList = 57, RULE_functionRestParameter = 58, 
		RULE_formalParameter = 59, RULE_functionBody = 60, RULE_functionStatementList = 61, 
		RULE_arrowFunction = 62, RULE_arrowParameters = 63, RULE_conciseBody = 64, 
		RULE_coverParenthesizedExpressionAndArrowParameterList = 65, RULE_methodDefinition = 66, 
		RULE_propertySetParameterList = 67, RULE_generatorMethod = 68, RULE_generatorDeclaration = 69, 
		RULE_generatorExpression = 70, RULE_generatorBody = 71, RULE_classDeclaration = 72, 
		RULE_classExpression = 73, RULE_classTail = 74, RULE_classHeritage = 75, 
		RULE_classBody = 76, RULE_classElementList = 77, RULE_classElement = 78, 
		RULE_arrayLiteral = 79, RULE_elementList = 80, RULE_elision = 81, RULE_spreadElement = 82, 
		RULE_objectLiteral = 83, RULE_propertyDefinitionList = 84, RULE_propertyDefinition = 85, 
		RULE_propertyName = 86, RULE_literalPropertyName = 87, RULE_computedPropertyName = 88, 
		RULE_coverInitializedName = 89, RULE_arguments = 90, RULE_argumentList = 91, 
		RULE_expressionSequence = 92, RULE_singleExpression = 93, RULE_script = 94, 
		RULE_scriptBody = 95, RULE_module = 96, RULE_moduleItem = 97, RULE_importDeclaration = 98, 
		RULE_importClause = 99, RULE_importedDefaultBinding = 100, RULE_nameSpaceImport = 101, 
		RULE_namedImports = 102, RULE_fromClause = 103, RULE_importsList = 104, 
		RULE_importSpecifier = 105, RULE_moduleSpecifier = 106, RULE_importedBinding = 107, 
		RULE_exportDeclaration = 108, RULE_exportClause = 109, RULE_exportsList = 110, 
		RULE_exportSpecifier = 111, RULE_assignmentOperator = 112, RULE_literal = 113, 
		RULE_numericLiteral = 114, RULE_identifier = 115, RULE_identifierName = 116, 
		RULE_identifierReference = 117, RULE_bindingIdentifier = 118, RULE_labelIdentifier = 119, 
		RULE_reservedWord = 120, RULE_keyword = 121, RULE_futureReservedWord = 122, 
		RULE_eos = 123, RULE_eof = 124, RULE_templateLiteral = 125, RULE_templateSpans = 126, 
		RULE_templateMiddleList = 127;
	public static final String[] ruleNames = {
		"program", "statement", "declaration", "hoistableDeclaration", "breakableStatement", 
		"blockStatement", "block", "statementList", "statementListItem", "lexicalDeclaration", 
		"letOrConst", "bindingList", "lexicalBinding", "variableStatement", "variableDeclarationList", 
		"variableDeclaration", "bindingPattern", "objectBindingPattern", "arrayBindingPattern", 
		"bindingPropertyList", "bindingElementList", "bindingElisionElement", 
		"bindingProperty", "bindingElement", "singleNameBinding", "bindingRestElement", 
		"initializer", "emptyStatement", "expressionStatement", "ifStatement", 
		"iterationStatement", "variableDeclarationStatement", "varForDeclaration", 
		"forDeclaration", "forBinding", "continueStatement", "breakStatement", 
		"returnStatement", "withStatement", "switchStatement", "caseBlock", "caseClauses", 
		"caseClause", "defaultClause", "labelledStatement", "labelledItem", "throwStatement", 
		"tryStatement", "catchProduction", "finallyProduction", "catchParameter", 
		"debuggerStatement", "functionDeclaration", "functionExpression", "strictFormalParameters", 
		"formalParameters", "formalParameterList", "formalsList", "functionRestParameter", 
		"formalParameter", "functionBody", "functionStatementList", "arrowFunction", 
		"arrowParameters", "conciseBody", "coverParenthesizedExpressionAndArrowParameterList", 
		"methodDefinition", "propertySetParameterList", "generatorMethod", "generatorDeclaration", 
		"generatorExpression", "generatorBody", "classDeclaration", "classExpression", 
		"classTail", "classHeritage", "classBody", "classElementList", "classElement", 
		"arrayLiteral", "elementList", "elision", "spreadElement", "objectLiteral", 
		"propertyDefinitionList", "propertyDefinition", "propertyName", "literalPropertyName", 
		"computedPropertyName", "coverInitializedName", "arguments", "argumentList", 
		"expressionSequence", "singleExpression", "script", "scriptBody", "module", 
		"moduleItem", "importDeclaration", "importClause", "importedDefaultBinding", 
		"nameSpaceImport", "namedImports", "fromClause", "importsList", "importSpecifier", 
		"moduleSpecifier", "importedBinding", "exportDeclaration", "exportClause", 
		"exportsList", "exportSpecifier", "assignmentOperator", "literal", "numericLiteral", 
		"identifier", "identifierName", "identifierReference", "bindingIdentifier", 
		"labelIdentifier", "reservedWord", "keyword", "futureReservedWord", "eos", 
		"eof", "templateLiteral", "templateSpans", "templateMiddleList"
	};

	private static final String[] _LITERAL_NAMES = {
		null, null, null, "'['", "']'", "'('", "')'", "'{'", "'}'", "';'", "','", 
		"'=>'", "'='", "'?'", "':'", "'...'", "'.'", "'++'", "'--'", "'+'", "'-'", 
		"'~'", "'!'", "'*'", "'/'", "'%'", "'>>'", "'<<'", "'>>>'", "'<'", "'>'", 
		"'<='", "'>='", "'=='", "'!='", "'==='", "'!=='", "'&'", "'^'", "'|'", 
		"'&&'", "'||'", "'*='", "'/='", "'%='", "'+='", "'-='", "'<<='", "'>>='", 
		"'>>>='", "'&='", "'^='", "'|='", "'null'", null, null, null, null, null, 
		"'break'", "'do'", "'instanceof'", "'typeof'", "'case'", "'else'", "'new'", 
		"'var'", "'catch'", "'finally'", "'return'", "'void'", "'continue'", "'for'", 
		"'switch'", "'while'", "'debugger'", "'function'", "'this'", "'with'", 
		"'default'", "'if'", "'throw'", "'delete'", "'in'", "'try'", "'export'", 
		"'class'", "'extends'", "'const'", "'super'", "'yield'", "'import'", "'enum'", 
		"'await'", "'implements'", "'private'", "'public'", "'interface'", "'package'", 
		"'protected'", "'static'", "'let'", "'from'", "'get'", "'set'", "'as'", 
		"'of'", "'target'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "RegularExpressionLiteral", "LineTerminator", "OpenBracket", "CloseBracket", 
		"OpenParen", "CloseParen", "OpenBrace", "CloseBrace", "SemiColon", "Comma", 
		"Arrow", "Assign", "QuestionMark", "Colon", "Ellipsis", "Dot", "PlusPlus", 
		"MinusMinus", "Plus", "Minus", "BitNot", "Not", "Multiply", "Divide", 
		"Modulus", "RightShiftArithmetic", "LeftShiftArithmetic", "RightShiftLogical", 
		"LessThan", "MoreThan", "LessThanEquals", "GreaterThanEquals", "Equals", 
		"NotEquals", "IdentityEquals", "IdentityNotEquals", "BitAnd", "BitXOr", 
		"BitOr", "And", "Or", "MultiplyAssign", "DivideAssign", "ModulusAssign", 
		"PlusAssign", "MinusAssign", "LeftShiftArithmeticAssign", "RightShiftArithmeticAssign", 
		"RightShiftLogicalAssign", "BitAndAssign", "BitXorAssign", "BitOrAssign", 
		"NullLiteral", "BooleanLiteral", "DecimalLiteral", "HexIntegerLiteral", 
		"OctalIntegerLiteral", "BinaryIntegerLiteral", "Break", "Do", "Instanceof", 
		"Typeof", "Case", "Else", "New", "Var", "Catch", "Finally", "Return", 
		"Void", "Continue", "For", "Switch", "While", "Debugger", "Function", 
		"This", "With", "Default", "If", "Throw", "Delete", "In", "Try", "Export", 
		"Class", "Extends", "Const", "Super", "Yield", "Import", "Enum", "Await", 
		"Implements", "Private", "Public", "Interface", "Package", "Protected", 
		"Static", "Let", "From", "Get", "Set", "As", "Of", "Target", "Identifier", 
		"StringLiteral", "WhiteSpaces", "MultiLineComment", "SingleLineComment", 
		"UnexpectedCharacter", "NoSubstitutionTemplate", "TemplateHead", "TemplateMiddle", 
		"TemplateTail"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "JSParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }


	  
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

	public JSParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ProgramContext extends ParserRuleContext {
		public ModuleContext module() {
			return getRuleContext(ModuleContext.class,0);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterProgram(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitProgram(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(256);
			module();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementContext extends ParserRuleContext {
		public BlockStatementContext blockStatement() {
			return getRuleContext(BlockStatementContext.class,0);
		}
		public VariableStatementContext variableStatement() {
			return getRuleContext(VariableStatementContext.class,0);
		}
		public EmptyStatementContext emptyStatement() {
			return getRuleContext(EmptyStatementContext.class,0);
		}
		public ExpressionStatementContext expressionStatement() {
			return getRuleContext(ExpressionStatementContext.class,0);
		}
		public IfStatementContext ifStatement() {
			return getRuleContext(IfStatementContext.class,0);
		}
		public BreakableStatementContext breakableStatement() {
			return getRuleContext(BreakableStatementContext.class,0);
		}
		public ContinueStatementContext continueStatement() {
			return getRuleContext(ContinueStatementContext.class,0);
		}
		public BreakStatementContext breakStatement() {
			return getRuleContext(BreakStatementContext.class,0);
		}
		public ReturnStatementContext returnStatement() {
			return getRuleContext(ReturnStatementContext.class,0);
		}
		public WithStatementContext withStatement() {
			return getRuleContext(WithStatementContext.class,0);
		}
		public LabelledStatementContext labelledStatement() {
			return getRuleContext(LabelledStatementContext.class,0);
		}
		public ThrowStatementContext throwStatement() {
			return getRuleContext(ThrowStatementContext.class,0);
		}
		public TryStatementContext tryStatement() {
			return getRuleContext(TryStatementContext.class,0);
		}
		public DebuggerStatementContext debuggerStatement() {
			return getRuleContext(DebuggerStatementContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitStatement(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_statement);
		try {
			setState(272);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(258);
				blockStatement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(259);
				variableStatement();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(260);
				emptyStatement();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(261);
				expressionStatement();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(262);
				ifStatement();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(263);
				breakableStatement();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(264);
				continueStatement();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(265);
				breakStatement();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(266);
				returnStatement();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(267);
				withStatement();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(268);
				labelledStatement();
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(269);
				throwStatement();
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(270);
				tryStatement();
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(271);
				debuggerStatement();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DeclarationContext extends ParserRuleContext {
		public HoistableDeclarationContext hoistableDeclaration() {
			return getRuleContext(HoistableDeclarationContext.class,0);
		}
		public ClassDeclarationContext classDeclaration() {
			return getRuleContext(ClassDeclarationContext.class,0);
		}
		public LexicalDeclarationContext lexicalDeclaration() {
			return getRuleContext(LexicalDeclarationContext.class,0);
		}
		public DeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitDeclaration(this);
		}
	}

	public final DeclarationContext declaration() throws RecognitionException {
		DeclarationContext _localctx = new DeclarationContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_declaration);
		try {
			setState(277);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Function:
				enterOuterAlt(_localctx, 1);
				{
				setState(274);
				hoistableDeclaration();
				}
				break;
			case Class:
				enterOuterAlt(_localctx, 2);
				{
				setState(275);
				classDeclaration();
				}
				break;
			case Const:
			case Let:
				enterOuterAlt(_localctx, 3);
				{
				setState(276);
				lexicalDeclaration();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class HoistableDeclarationContext extends ParserRuleContext {
		public FunctionDeclarationContext functionDeclaration() {
			return getRuleContext(FunctionDeclarationContext.class,0);
		}
		public GeneratorDeclarationContext generatorDeclaration() {
			return getRuleContext(GeneratorDeclarationContext.class,0);
		}
		public HoistableDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hoistableDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterHoistableDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitHoistableDeclaration(this);
		}
	}

	public final HoistableDeclarationContext hoistableDeclaration() throws RecognitionException {
		HoistableDeclarationContext _localctx = new HoistableDeclarationContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_hoistableDeclaration);
		try {
			setState(281);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(279);
				functionDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(280);
				generatorDeclaration();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BreakableStatementContext extends ParserRuleContext {
		public IterationStatementContext iterationStatement() {
			return getRuleContext(IterationStatementContext.class,0);
		}
		public SwitchStatementContext switchStatement() {
			return getRuleContext(SwitchStatementContext.class,0);
		}
		public BreakableStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_breakableStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterBreakableStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitBreakableStatement(this);
		}
	}

	public final BreakableStatementContext breakableStatement() throws RecognitionException {
		BreakableStatementContext _localctx = new BreakableStatementContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_breakableStatement);
		try {
			setState(285);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Do:
			case For:
			case While:
				enterOuterAlt(_localctx, 1);
				{
				setState(283);
				iterationStatement();
				}
				break;
			case Switch:
				enterOuterAlt(_localctx, 2);
				{
				setState(284);
				switchStatement();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BlockStatementContext extends ParserRuleContext {
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public BlockStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_blockStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterBlockStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitBlockStatement(this);
		}
	}

	public final BlockStatementContext blockStatement() throws RecognitionException {
		BlockStatementContext _localctx = new BlockStatementContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_blockStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(287);
			block();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BlockContext extends ParserRuleContext {
		public StatementListContext statementList() {
			return getRuleContext(StatementListContext.class,0);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitBlock(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_block);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(289);
			match(OpenBrace);
			setState(291);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				{
				setState(290);
				statementList();
				}
				break;
			}
			setState(293);
			match(CloseBrace);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementListContext extends ParserRuleContext {
		public List<StatementListItemContext> statementListItem() {
			return getRuleContexts(StatementListItemContext.class);
		}
		public StatementListItemContext statementListItem(int i) {
			return getRuleContext(StatementListItemContext.class,i);
		}
		public StatementListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statementList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterStatementList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitStatementList(this);
		}
	}

	public final StatementListContext statementList() throws RecognitionException {
		StatementListContext _localctx = new StatementListContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_statementList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(296); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(295);
					statementListItem();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(298); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementListItemContext extends ParserRuleContext {
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public DeclarationContext declaration() {
			return getRuleContext(DeclarationContext.class,0);
		}
		public StatementListItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statementListItem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterStatementListItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitStatementListItem(this);
		}
	}

	public final StatementListItemContext statementListItem() throws RecognitionException {
		StatementListItemContext _localctx = new StatementListItemContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_statementListItem);
		try {
			setState(302);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(300);
				statement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(301);
				declaration();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LexicalDeclarationContext extends ParserRuleContext {
		public LetOrConstContext letOrConst() {
			return getRuleContext(LetOrConstContext.class,0);
		}
		public BindingListContext bindingList() {
			return getRuleContext(BindingListContext.class,0);
		}
		public EosContext eos() {
			return getRuleContext(EosContext.class,0);
		}
		public LexicalDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lexicalDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterLexicalDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitLexicalDeclaration(this);
		}
	}

	public final LexicalDeclarationContext lexicalDeclaration() throws RecognitionException {
		LexicalDeclarationContext _localctx = new LexicalDeclarationContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_lexicalDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(304);
			letOrConst();
			setState(305);
			bindingList();
			setState(306);
			eos();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LetOrConstContext extends ParserRuleContext {
		public TerminalNode Let() { return getToken(JSParser.Let, 0); }
		public TerminalNode Const() { return getToken(JSParser.Const, 0); }
		public LetOrConstContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_letOrConst; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterLetOrConst(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitLetOrConst(this);
		}
	}

	public final LetOrConstContext letOrConst() throws RecognitionException {
		LetOrConstContext _localctx = new LetOrConstContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_letOrConst);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(308);
			_la = _input.LA(1);
			if ( !(_la==Const || _la==Let) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BindingListContext extends ParserRuleContext {
		public List<LexicalBindingContext> lexicalBinding() {
			return getRuleContexts(LexicalBindingContext.class);
		}
		public LexicalBindingContext lexicalBinding(int i) {
			return getRuleContext(LexicalBindingContext.class,i);
		}
		public BindingListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bindingList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterBindingList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitBindingList(this);
		}
	}

	public final BindingListContext bindingList() throws RecognitionException {
		BindingListContext _localctx = new BindingListContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_bindingList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(310);
			lexicalBinding();
			setState(315);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(311);
					match(Comma);
					setState(312);
					lexicalBinding();
					}
					} 
				}
				setState(317);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LexicalBindingContext extends ParserRuleContext {
		public BindingIdentifierContext bindingIdentifier() {
			return getRuleContext(BindingIdentifierContext.class,0);
		}
		public InitializerContext initializer() {
			return getRuleContext(InitializerContext.class,0);
		}
		public BindingPatternContext bindingPattern() {
			return getRuleContext(BindingPatternContext.class,0);
		}
		public LexicalBindingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lexicalBinding; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterLexicalBinding(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitLexicalBinding(this);
		}
	}

	public final LexicalBindingContext lexicalBinding() throws RecognitionException {
		LexicalBindingContext _localctx = new LexicalBindingContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_lexicalBinding);
		try {
			setState(325);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Static:
			case Let:
			case From:
			case Get:
			case Set:
			case As:
			case Of:
			case Target:
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(318);
				bindingIdentifier();
				setState(320);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
				case 1:
					{
					setState(319);
					initializer();
					}
					break;
				}
				}
				break;
			case OpenBracket:
			case OpenBrace:
				enterOuterAlt(_localctx, 2);
				{
				setState(322);
				bindingPattern();
				setState(323);
				initializer();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableStatementContext extends ParserRuleContext {
		public VariableDeclarationStatementContext variableDeclarationStatement() {
			return getRuleContext(VariableDeclarationStatementContext.class,0);
		}
		public EosContext eos() {
			return getRuleContext(EosContext.class,0);
		}
		public VariableStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterVariableStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitVariableStatement(this);
		}
	}

	public final VariableStatementContext variableStatement() throws RecognitionException {
		VariableStatementContext _localctx = new VariableStatementContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_variableStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(327);
			variableDeclarationStatement();
			setState(328);
			eos();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableDeclarationListContext extends ParserRuleContext {
		public List<VariableDeclarationContext> variableDeclaration() {
			return getRuleContexts(VariableDeclarationContext.class);
		}
		public VariableDeclarationContext variableDeclaration(int i) {
			return getRuleContext(VariableDeclarationContext.class,i);
		}
		public VariableDeclarationListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableDeclarationList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterVariableDeclarationList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitVariableDeclarationList(this);
		}
	}

	public final VariableDeclarationListContext variableDeclarationList() throws RecognitionException {
		VariableDeclarationListContext _localctx = new VariableDeclarationListContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_variableDeclarationList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(330);
			variableDeclaration();
			setState(335);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(331);
					match(Comma);
					setState(332);
					variableDeclaration();
					}
					} 
				}
				setState(337);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableDeclarationContext extends ParserRuleContext {
		public BindingIdentifierContext bindingIdentifier() {
			return getRuleContext(BindingIdentifierContext.class,0);
		}
		public InitializerContext initializer() {
			return getRuleContext(InitializerContext.class,0);
		}
		public BindingPatternContext bindingPattern() {
			return getRuleContext(BindingPatternContext.class,0);
		}
		public VariableDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterVariableDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitVariableDeclaration(this);
		}
	}

	public final VariableDeclarationContext variableDeclaration() throws RecognitionException {
		VariableDeclarationContext _localctx = new VariableDeclarationContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_variableDeclaration);
		try {
			setState(345);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Static:
			case Let:
			case From:
			case Get:
			case Set:
			case As:
			case Of:
			case Target:
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(338);
				bindingIdentifier();
				setState(340);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
				case 1:
					{
					setState(339);
					initializer();
					}
					break;
				}
				}
				break;
			case OpenBracket:
			case OpenBrace:
				enterOuterAlt(_localctx, 2);
				{
				setState(342);
				bindingPattern();
				setState(343);
				initializer();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BindingPatternContext extends ParserRuleContext {
		public ObjectBindingPatternContext objectBindingPattern() {
			return getRuleContext(ObjectBindingPatternContext.class,0);
		}
		public ArrayBindingPatternContext arrayBindingPattern() {
			return getRuleContext(ArrayBindingPatternContext.class,0);
		}
		public BindingPatternContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bindingPattern; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterBindingPattern(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitBindingPattern(this);
		}
	}

	public final BindingPatternContext bindingPattern() throws RecognitionException {
		BindingPatternContext _localctx = new BindingPatternContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_bindingPattern);
		try {
			setState(349);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case OpenBrace:
				enterOuterAlt(_localctx, 1);
				{
				setState(347);
				objectBindingPattern();
				}
				break;
			case OpenBracket:
				enterOuterAlt(_localctx, 2);
				{
				setState(348);
				arrayBindingPattern();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ObjectBindingPatternContext extends ParserRuleContext {
		public BindingPropertyListContext bindingPropertyList() {
			return getRuleContext(BindingPropertyListContext.class,0);
		}
		public ObjectBindingPatternContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_objectBindingPattern; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterObjectBindingPattern(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitObjectBindingPattern(this);
		}
	}

	public final ObjectBindingPatternContext objectBindingPattern() throws RecognitionException {
		ObjectBindingPatternContext _localctx = new ObjectBindingPatternContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_objectBindingPattern);
		try {
			setState(362);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(351);
				match(OpenBrace);
				setState(352);
				match(CloseBrace);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(353);
				match(OpenBrace);
				setState(354);
				bindingPropertyList();
				setState(355);
				match(CloseBrace);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(357);
				match(OpenBrace);
				setState(358);
				bindingPropertyList();
				setState(359);
				match(Comma);
				setState(360);
				match(CloseBrace);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArrayBindingPatternContext extends ParserRuleContext {
		public ElisionContext elision() {
			return getRuleContext(ElisionContext.class,0);
		}
		public BindingRestElementContext bindingRestElement() {
			return getRuleContext(BindingRestElementContext.class,0);
		}
		public BindingElementListContext bindingElementList() {
			return getRuleContext(BindingElementListContext.class,0);
		}
		public ArrayBindingPatternContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayBindingPattern; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterArrayBindingPattern(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitArrayBindingPattern(this);
		}
	}

	public final ArrayBindingPatternContext arrayBindingPattern() throws RecognitionException {
		ArrayBindingPatternContext _localctx = new ArrayBindingPatternContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_arrayBindingPattern);
		int _la;
		try {
			setState(387);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(364);
				match(OpenBracket);
				setState(366);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Comma) {
					{
					setState(365);
					elision();
					}
				}

				setState(369);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Ellipsis) {
					{
					setState(368);
					bindingRestElement();
					}
				}

				setState(371);
				match(CloseBracket);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(372);
				match(OpenBracket);
				setState(373);
				bindingElementList();
				setState(374);
				match(CloseBracket);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(376);
				match(OpenBracket);
				setState(377);
				bindingElementList();
				setState(378);
				match(Comma);
				setState(380);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Comma) {
					{
					setState(379);
					elision();
					}
				}

				setState(383);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Ellipsis) {
					{
					setState(382);
					bindingRestElement();
					}
				}

				setState(385);
				match(CloseBracket);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BindingPropertyListContext extends ParserRuleContext {
		public List<BindingPropertyContext> bindingProperty() {
			return getRuleContexts(BindingPropertyContext.class);
		}
		public BindingPropertyContext bindingProperty(int i) {
			return getRuleContext(BindingPropertyContext.class,i);
		}
		public BindingPropertyListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bindingPropertyList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterBindingPropertyList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitBindingPropertyList(this);
		}
	}

	public final BindingPropertyListContext bindingPropertyList() throws RecognitionException {
		BindingPropertyListContext _localctx = new BindingPropertyListContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_bindingPropertyList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(389);
			bindingProperty();
			setState(394);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(390);
					match(Comma);
					setState(391);
					bindingProperty();
					}
					} 
				}
				setState(396);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BindingElementListContext extends ParserRuleContext {
		public List<BindingElisionElementContext> bindingElisionElement() {
			return getRuleContexts(BindingElisionElementContext.class);
		}
		public BindingElisionElementContext bindingElisionElement(int i) {
			return getRuleContext(BindingElisionElementContext.class,i);
		}
		public BindingElementListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bindingElementList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterBindingElementList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitBindingElementList(this);
		}
	}

	public final BindingElementListContext bindingElementList() throws RecognitionException {
		BindingElementListContext _localctx = new BindingElementListContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_bindingElementList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(397);
			bindingElisionElement();
			setState(402);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(398);
					match(Comma);
					setState(399);
					bindingElisionElement();
					}
					} 
				}
				setState(404);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BindingElisionElementContext extends ParserRuleContext {
		public BindingElementContext bindingElement() {
			return getRuleContext(BindingElementContext.class,0);
		}
		public ElisionContext elision() {
			return getRuleContext(ElisionContext.class,0);
		}
		public BindingElisionElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bindingElisionElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterBindingElisionElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitBindingElisionElement(this);
		}
	}

	public final BindingElisionElementContext bindingElisionElement() throws RecognitionException {
		BindingElisionElementContext _localctx = new BindingElisionElementContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_bindingElisionElement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(406);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Comma) {
				{
				setState(405);
				elision();
				}
			}

			setState(408);
			bindingElement();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BindingPropertyContext extends ParserRuleContext {
		public SingleNameBindingContext singleNameBinding() {
			return getRuleContext(SingleNameBindingContext.class,0);
		}
		public PropertyNameContext propertyName() {
			return getRuleContext(PropertyNameContext.class,0);
		}
		public BindingElementContext bindingElement() {
			return getRuleContext(BindingElementContext.class,0);
		}
		public BindingPropertyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bindingProperty; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterBindingProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitBindingProperty(this);
		}
	}

	public final BindingPropertyContext bindingProperty() throws RecognitionException {
		BindingPropertyContext _localctx = new BindingPropertyContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_bindingProperty);
		try {
			setState(415);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(410);
				singleNameBinding();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(411);
				propertyName();
				setState(412);
				match(Colon);
				setState(413);
				bindingElement();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BindingElementContext extends ParserRuleContext {
		public SingleNameBindingContext singleNameBinding() {
			return getRuleContext(SingleNameBindingContext.class,0);
		}
		public BindingPatternContext bindingPattern() {
			return getRuleContext(BindingPatternContext.class,0);
		}
		public InitializerContext initializer() {
			return getRuleContext(InitializerContext.class,0);
		}
		public BindingElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bindingElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterBindingElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitBindingElement(this);
		}
	}

	public final BindingElementContext bindingElement() throws RecognitionException {
		BindingElementContext _localctx = new BindingElementContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_bindingElement);
		int _la;
		try {
			setState(422);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Static:
			case Let:
			case From:
			case Get:
			case Set:
			case As:
			case Of:
			case Target:
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(417);
				singleNameBinding();
				}
				break;
			case OpenBracket:
			case OpenBrace:
				enterOuterAlt(_localctx, 2);
				{
				setState(418);
				bindingPattern();
				setState(420);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Assign) {
					{
					setState(419);
					initializer();
					}
				}

				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SingleNameBindingContext extends ParserRuleContext {
		public BindingIdentifierContext bindingIdentifier() {
			return getRuleContext(BindingIdentifierContext.class,0);
		}
		public InitializerContext initializer() {
			return getRuleContext(InitializerContext.class,0);
		}
		public SingleNameBindingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleNameBinding; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterSingleNameBinding(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitSingleNameBinding(this);
		}
	}

	public final SingleNameBindingContext singleNameBinding() throws RecognitionException {
		SingleNameBindingContext _localctx = new SingleNameBindingContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_singleNameBinding);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(424);
			bindingIdentifier();
			setState(426);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Assign) {
				{
				setState(425);
				initializer();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BindingRestElementContext extends ParserRuleContext {
		public BindingIdentifierContext bindingIdentifier() {
			return getRuleContext(BindingIdentifierContext.class,0);
		}
		public BindingRestElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bindingRestElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterBindingRestElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitBindingRestElement(this);
		}
	}

	public final BindingRestElementContext bindingRestElement() throws RecognitionException {
		BindingRestElementContext _localctx = new BindingRestElementContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_bindingRestElement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(428);
			match(Ellipsis);
			setState(429);
			bindingIdentifier();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class InitializerContext extends ParserRuleContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public InitializerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_initializer; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterInitializer(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitInitializer(this);
		}
	}

	public final InitializerContext initializer() throws RecognitionException {
		InitializerContext _localctx = new InitializerContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_initializer);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(431);
			match(Assign);
			setState(432);
			singleExpression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EmptyStatementContext extends ParserRuleContext {
		public TerminalNode SemiColon() { return getToken(JSParser.SemiColon, 0); }
		public EmptyStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_emptyStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterEmptyStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitEmptyStatement(this);
		}
	}

	public final EmptyStatementContext emptyStatement() throws RecognitionException {
		EmptyStatementContext _localctx = new EmptyStatementContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_emptyStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(434);
			match(SemiColon);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionStatementContext extends ParserRuleContext {
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public EosContext eos() {
			return getRuleContext(EosContext.class,0);
		}
		public ExpressionStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterExpressionStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitExpressionStatement(this);
		}
	}

	public final ExpressionStatementContext expressionStatement() throws RecognitionException {
		ExpressionStatementContext _localctx = new ExpressionStatementContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_expressionStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(436);
			if (!((_input.LA(1) != OpenBrace) && (_input.LA(1) != Function) && (_input.LA(1) != Class) && ((_input.LA(1) == Let) ? _input.LA(2) != OpenBracket : true))) throw new FailedPredicateException(this, "(_input.LA(1) != OpenBrace) && (_input.LA(1) != Function) && (_input.LA(1) != Class) && ((_input.LA(1) == Let) ? _input.LA(2) != OpenBracket : true)");
			setState(437);
			expressionSequence();
			setState(438);
			eos();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IfStatementContext extends ParserRuleContext {
		public TerminalNode If() { return getToken(JSParser.If, 0); }
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public TerminalNode Else() { return getToken(JSParser.Else, 0); }
		public IfStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterIfStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitIfStatement(this);
		}
	}

	public final IfStatementContext ifStatement() throws RecognitionException {
		IfStatementContext _localctx = new IfStatementContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_ifStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(440);
			match(If);
			setState(441);
			match(OpenParen);
			setState(442);
			expressionSequence();
			setState(443);
			match(CloseParen);
			setState(444);
			statement();
			setState(447);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
			case 1:
				{
				setState(445);
				match(Else);
				setState(446);
				statement();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IterationStatementContext extends ParserRuleContext {
		public IterationStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_iterationStatement; }
	 
		public IterationStatementContext() { }
		public void copyFrom(IterationStatementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ForLexicalOfStatementContext extends IterationStatementContext {
		public TerminalNode For() { return getToken(JSParser.For, 0); }
		public ForDeclarationContext forDeclaration() {
			return getRuleContext(ForDeclarationContext.class,0);
		}
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public ForLexicalOfStatementContext(IterationStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterForLexicalOfStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitForLexicalOfStatement(this);
		}
	}
	public static class ForVarOfStatementContext extends IterationStatementContext {
		public TerminalNode For() { return getToken(JSParser.For, 0); }
		public VarForDeclarationContext varForDeclaration() {
			return getRuleContext(VarForDeclarationContext.class,0);
		}
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public ForVarOfStatementContext(IterationStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterForVarOfStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitForVarOfStatement(this);
		}
	}
	public static class ForVarInStatementContext extends IterationStatementContext {
		public TerminalNode For() { return getToken(JSParser.For, 0); }
		public VarForDeclarationContext varForDeclaration() {
			return getRuleContext(VarForDeclarationContext.class,0);
		}
		public TerminalNode In() { return getToken(JSParser.In, 0); }
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public ForVarInStatementContext(IterationStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterForVarInStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitForVarInStatement(this);
		}
	}
	public static class ForLexicalInStatementContext extends IterationStatementContext {
		public TerminalNode For() { return getToken(JSParser.For, 0); }
		public ForDeclarationContext forDeclaration() {
			return getRuleContext(ForDeclarationContext.class,0);
		}
		public TerminalNode In() { return getToken(JSParser.In, 0); }
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public ForLexicalInStatementContext(IterationStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterForLexicalInStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitForLexicalInStatement(this);
		}
	}
	public static class ForLoopStatementContext extends IterationStatementContext {
		public TerminalNode For() { return getToken(JSParser.For, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public List<ExpressionSequenceContext> expressionSequence() {
			return getRuleContexts(ExpressionSequenceContext.class);
		}
		public ExpressionSequenceContext expressionSequence(int i) {
			return getRuleContext(ExpressionSequenceContext.class,i);
		}
		public ForLoopStatementContext(IterationStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterForLoopStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitForLoopStatement(this);
		}
	}
	public static class ForVarLoopStatementContext extends IterationStatementContext {
		public TerminalNode For() { return getToken(JSParser.For, 0); }
		public VariableDeclarationStatementContext variableDeclarationStatement() {
			return getRuleContext(VariableDeclarationStatementContext.class,0);
		}
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public List<ExpressionSequenceContext> expressionSequence() {
			return getRuleContexts(ExpressionSequenceContext.class);
		}
		public ExpressionSequenceContext expressionSequence(int i) {
			return getRuleContext(ExpressionSequenceContext.class,i);
		}
		public ForVarLoopStatementContext(IterationStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterForVarLoopStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitForVarLoopStatement(this);
		}
	}
	public static class ForLexicalLoopStatementContext extends IterationStatementContext {
		public TerminalNode For() { return getToken(JSParser.For, 0); }
		public LexicalDeclarationContext lexicalDeclaration() {
			return getRuleContext(LexicalDeclarationContext.class,0);
		}
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public List<ExpressionSequenceContext> expressionSequence() {
			return getRuleContexts(ExpressionSequenceContext.class);
		}
		public ExpressionSequenceContext expressionSequence(int i) {
			return getRuleContext(ExpressionSequenceContext.class,i);
		}
		public ForLexicalLoopStatementContext(IterationStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterForLexicalLoopStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitForLexicalLoopStatement(this);
		}
	}
	public static class WhileStatementContext extends IterationStatementContext {
		public TerminalNode While() { return getToken(JSParser.While, 0); }
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public WhileStatementContext(IterationStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterWhileStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitWhileStatement(this);
		}
	}
	public static class DoWhileStatementContext extends IterationStatementContext {
		public TerminalNode Do() { return getToken(JSParser.Do, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public TerminalNode While() { return getToken(JSParser.While, 0); }
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public EosContext eos() {
			return getRuleContext(EosContext.class,0);
		}
		public DoWhileStatementContext(IterationStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterDoWhileStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitDoWhileStatement(this);
		}
	}
	public static class ForInStatementContext extends IterationStatementContext {
		public TerminalNode For() { return getToken(JSParser.For, 0); }
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public TerminalNode In() { return getToken(JSParser.In, 0); }
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public ForInStatementContext(IterationStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterForInStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitForInStatement(this);
		}
	}
	public static class ForOfStatementContext extends IterationStatementContext {
		public TerminalNode For() { return getToken(JSParser.For, 0); }
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public ForOfStatementContext(IterationStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterForOfStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitForOfStatement(this);
		}
	}

	public final IterationStatementContext iterationStatement() throws RecognitionException {
		IterationStatementContext _localctx = new IterationStatementContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_iterationStatement);
		int _la;
		try {
			setState(556);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,35,_ctx) ) {
			case 1:
				_localctx = new DoWhileStatementContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(449);
				match(Do);
				setState(450);
				statement();
				setState(451);
				match(While);
				setState(452);
				match(OpenParen);
				setState(453);
				expressionSequence();
				setState(454);
				match(CloseParen);
				setState(455);
				eos();
				}
				break;
			case 2:
				_localctx = new WhileStatementContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(457);
				match(While);
				setState(458);
				match(OpenParen);
				setState(459);
				expressionSequence();
				setState(460);
				match(CloseParen);
				setState(461);
				statement();
				}
				break;
			case 3:
				_localctx = new ForLoopStatementContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(463);
				match(For);
				setState(464);
				match(OpenParen);
				setState(465);
				if (!(((_input.LA(1) == Let) ? _input.LA(2) != OpenBracket : true))) throw new FailedPredicateException(this, "((_input.LA(1) == Let) ? _input.LA(2) != OpenBracket : true)");
				setState(467);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RegularExpressionLiteral) | (1L << OpenBracket) | (1L << OpenParen) | (1L << OpenBrace) | (1L << PlusPlus) | (1L << MinusMinus) | (1L << Plus) | (1L << Minus) | (1L << BitNot) | (1L << Not) | (1L << NullLiteral) | (1L << BooleanLiteral) | (1L << DecimalLiteral) | (1L << HexIntegerLiteral) | (1L << OctalIntegerLiteral) | (1L << BinaryIntegerLiteral) | (1L << Typeof))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (New - 65)) | (1L << (Void - 65)) | (1L << (Function - 65)) | (1L << (This - 65)) | (1L << (Delete - 65)) | (1L << (Class - 65)) | (1L << (Super - 65)) | (1L << (Yield - 65)) | (1L << (Static - 65)) | (1L << (Let - 65)) | (1L << (From - 65)) | (1L << (Get - 65)) | (1L << (Set - 65)) | (1L << (As - 65)) | (1L << (Of - 65)) | (1L << (Target - 65)) | (1L << (Identifier - 65)) | (1L << (StringLiteral - 65)) | (1L << (NoSubstitutionTemplate - 65)) | (1L << (TemplateHead - 65)))) != 0)) {
					{
					setState(466);
					expressionSequence();
					}
				}

				setState(469);
				match(SemiColon);
				setState(471);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RegularExpressionLiteral) | (1L << OpenBracket) | (1L << OpenParen) | (1L << OpenBrace) | (1L << PlusPlus) | (1L << MinusMinus) | (1L << Plus) | (1L << Minus) | (1L << BitNot) | (1L << Not) | (1L << NullLiteral) | (1L << BooleanLiteral) | (1L << DecimalLiteral) | (1L << HexIntegerLiteral) | (1L << OctalIntegerLiteral) | (1L << BinaryIntegerLiteral) | (1L << Typeof))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (New - 65)) | (1L << (Void - 65)) | (1L << (Function - 65)) | (1L << (This - 65)) | (1L << (Delete - 65)) | (1L << (Class - 65)) | (1L << (Super - 65)) | (1L << (Yield - 65)) | (1L << (Static - 65)) | (1L << (Let - 65)) | (1L << (From - 65)) | (1L << (Get - 65)) | (1L << (Set - 65)) | (1L << (As - 65)) | (1L << (Of - 65)) | (1L << (Target - 65)) | (1L << (Identifier - 65)) | (1L << (StringLiteral - 65)) | (1L << (NoSubstitutionTemplate - 65)) | (1L << (TemplateHead - 65)))) != 0)) {
					{
					setState(470);
					expressionSequence();
					}
				}

				setState(473);
				match(SemiColon);
				setState(475);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RegularExpressionLiteral) | (1L << OpenBracket) | (1L << OpenParen) | (1L << OpenBrace) | (1L << PlusPlus) | (1L << MinusMinus) | (1L << Plus) | (1L << Minus) | (1L << BitNot) | (1L << Not) | (1L << NullLiteral) | (1L << BooleanLiteral) | (1L << DecimalLiteral) | (1L << HexIntegerLiteral) | (1L << OctalIntegerLiteral) | (1L << BinaryIntegerLiteral) | (1L << Typeof))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (New - 65)) | (1L << (Void - 65)) | (1L << (Function - 65)) | (1L << (This - 65)) | (1L << (Delete - 65)) | (1L << (Class - 65)) | (1L << (Super - 65)) | (1L << (Yield - 65)) | (1L << (Static - 65)) | (1L << (Let - 65)) | (1L << (From - 65)) | (1L << (Get - 65)) | (1L << (Set - 65)) | (1L << (As - 65)) | (1L << (Of - 65)) | (1L << (Target - 65)) | (1L << (Identifier - 65)) | (1L << (StringLiteral - 65)) | (1L << (NoSubstitutionTemplate - 65)) | (1L << (TemplateHead - 65)))) != 0)) {
					{
					setState(474);
					expressionSequence();
					}
				}

				setState(477);
				match(CloseParen);
				setState(478);
				statement();
				}
				break;
			case 4:
				_localctx = new ForVarLoopStatementContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(479);
				match(For);
				setState(480);
				match(OpenParen);
				setState(481);
				variableDeclarationStatement();
				setState(482);
				match(SemiColon);
				setState(484);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RegularExpressionLiteral) | (1L << OpenBracket) | (1L << OpenParen) | (1L << OpenBrace) | (1L << PlusPlus) | (1L << MinusMinus) | (1L << Plus) | (1L << Minus) | (1L << BitNot) | (1L << Not) | (1L << NullLiteral) | (1L << BooleanLiteral) | (1L << DecimalLiteral) | (1L << HexIntegerLiteral) | (1L << OctalIntegerLiteral) | (1L << BinaryIntegerLiteral) | (1L << Typeof))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (New - 65)) | (1L << (Void - 65)) | (1L << (Function - 65)) | (1L << (This - 65)) | (1L << (Delete - 65)) | (1L << (Class - 65)) | (1L << (Super - 65)) | (1L << (Yield - 65)) | (1L << (Static - 65)) | (1L << (Let - 65)) | (1L << (From - 65)) | (1L << (Get - 65)) | (1L << (Set - 65)) | (1L << (As - 65)) | (1L << (Of - 65)) | (1L << (Target - 65)) | (1L << (Identifier - 65)) | (1L << (StringLiteral - 65)) | (1L << (NoSubstitutionTemplate - 65)) | (1L << (TemplateHead - 65)))) != 0)) {
					{
					setState(483);
					expressionSequence();
					}
				}

				setState(486);
				match(SemiColon);
				setState(488);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RegularExpressionLiteral) | (1L << OpenBracket) | (1L << OpenParen) | (1L << OpenBrace) | (1L << PlusPlus) | (1L << MinusMinus) | (1L << Plus) | (1L << Minus) | (1L << BitNot) | (1L << Not) | (1L << NullLiteral) | (1L << BooleanLiteral) | (1L << DecimalLiteral) | (1L << HexIntegerLiteral) | (1L << OctalIntegerLiteral) | (1L << BinaryIntegerLiteral) | (1L << Typeof))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (New - 65)) | (1L << (Void - 65)) | (1L << (Function - 65)) | (1L << (This - 65)) | (1L << (Delete - 65)) | (1L << (Class - 65)) | (1L << (Super - 65)) | (1L << (Yield - 65)) | (1L << (Static - 65)) | (1L << (Let - 65)) | (1L << (From - 65)) | (1L << (Get - 65)) | (1L << (Set - 65)) | (1L << (As - 65)) | (1L << (Of - 65)) | (1L << (Target - 65)) | (1L << (Identifier - 65)) | (1L << (StringLiteral - 65)) | (1L << (NoSubstitutionTemplate - 65)) | (1L << (TemplateHead - 65)))) != 0)) {
					{
					setState(487);
					expressionSequence();
					}
				}

				setState(490);
				match(CloseParen);
				setState(491);
				statement();
				}
				break;
			case 5:
				_localctx = new ForLexicalLoopStatementContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(493);
				match(For);
				setState(494);
				match(OpenParen);
				setState(495);
				lexicalDeclaration();
				setState(497);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RegularExpressionLiteral) | (1L << OpenBracket) | (1L << OpenParen) | (1L << OpenBrace) | (1L << PlusPlus) | (1L << MinusMinus) | (1L << Plus) | (1L << Minus) | (1L << BitNot) | (1L << Not) | (1L << NullLiteral) | (1L << BooleanLiteral) | (1L << DecimalLiteral) | (1L << HexIntegerLiteral) | (1L << OctalIntegerLiteral) | (1L << BinaryIntegerLiteral) | (1L << Typeof))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (New - 65)) | (1L << (Void - 65)) | (1L << (Function - 65)) | (1L << (This - 65)) | (1L << (Delete - 65)) | (1L << (Class - 65)) | (1L << (Super - 65)) | (1L << (Yield - 65)) | (1L << (Static - 65)) | (1L << (Let - 65)) | (1L << (From - 65)) | (1L << (Get - 65)) | (1L << (Set - 65)) | (1L << (As - 65)) | (1L << (Of - 65)) | (1L << (Target - 65)) | (1L << (Identifier - 65)) | (1L << (StringLiteral - 65)) | (1L << (NoSubstitutionTemplate - 65)) | (1L << (TemplateHead - 65)))) != 0)) {
					{
					setState(496);
					expressionSequence();
					}
				}

				setState(499);
				match(SemiColon);
				setState(501);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RegularExpressionLiteral) | (1L << OpenBracket) | (1L << OpenParen) | (1L << OpenBrace) | (1L << PlusPlus) | (1L << MinusMinus) | (1L << Plus) | (1L << Minus) | (1L << BitNot) | (1L << Not) | (1L << NullLiteral) | (1L << BooleanLiteral) | (1L << DecimalLiteral) | (1L << HexIntegerLiteral) | (1L << OctalIntegerLiteral) | (1L << BinaryIntegerLiteral) | (1L << Typeof))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (New - 65)) | (1L << (Void - 65)) | (1L << (Function - 65)) | (1L << (This - 65)) | (1L << (Delete - 65)) | (1L << (Class - 65)) | (1L << (Super - 65)) | (1L << (Yield - 65)) | (1L << (Static - 65)) | (1L << (Let - 65)) | (1L << (From - 65)) | (1L << (Get - 65)) | (1L << (Set - 65)) | (1L << (As - 65)) | (1L << (Of - 65)) | (1L << (Target - 65)) | (1L << (Identifier - 65)) | (1L << (StringLiteral - 65)) | (1L << (NoSubstitutionTemplate - 65)) | (1L << (TemplateHead - 65)))) != 0)) {
					{
					setState(500);
					expressionSequence();
					}
				}

				setState(503);
				match(CloseParen);
				setState(504);
				statement();
				}
				break;
			case 6:
				_localctx = new ForInStatementContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(506);
				match(For);
				setState(507);
				match(OpenParen);
				setState(508);
				if (!(((_input.LA(1) == Let) ? _input.LA(2) != OpenBracket : true))) throw new FailedPredicateException(this, "((_input.LA(1) == Let) ? _input.LA(2) != OpenBracket : true)");
				setState(509);
				singleExpression(0);
				setState(510);
				match(In);
				setState(511);
				expressionSequence();
				setState(512);
				match(CloseParen);
				setState(513);
				statement();
				}
				break;
			case 7:
				_localctx = new ForVarInStatementContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(515);
				match(For);
				setState(516);
				match(OpenParen);
				setState(517);
				varForDeclaration();
				setState(518);
				match(In);
				setState(519);
				expressionSequence();
				setState(520);
				match(CloseParen);
				setState(521);
				statement();
				}
				break;
			case 8:
				_localctx = new ForLexicalInStatementContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(523);
				match(For);
				setState(524);
				match(OpenParen);
				setState(525);
				forDeclaration();
				setState(526);
				match(In);
				setState(527);
				expressionSequence();
				setState(528);
				match(CloseParen);
				setState(529);
				statement();
				}
				break;
			case 9:
				_localctx = new ForOfStatementContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(531);
				match(For);
				setState(532);
				match(OpenParen);
				setState(533);
				if (!((_input.LA(1) != Let))) throw new FailedPredicateException(this, "(_input.LA(1) != Let)");
				setState(534);
				singleExpression(0);
				setState(535);
				match(Of);
				setState(536);
				singleExpression(0);
				setState(537);
				match(CloseParen);
				setState(538);
				statement();
				}
				break;
			case 10:
				_localctx = new ForVarOfStatementContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(540);
				match(For);
				setState(541);
				match(OpenParen);
				setState(542);
				varForDeclaration();
				setState(543);
				match(Of);
				setState(544);
				singleExpression(0);
				setState(545);
				match(CloseParen);
				setState(546);
				statement();
				}
				break;
			case 11:
				_localctx = new ForLexicalOfStatementContext(_localctx);
				enterOuterAlt(_localctx, 11);
				{
				setState(548);
				match(For);
				setState(549);
				match(OpenParen);
				setState(550);
				forDeclaration();
				setState(551);
				match(Of);
				setState(552);
				singleExpression(0);
				setState(553);
				match(CloseParen);
				setState(554);
				statement();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableDeclarationStatementContext extends ParserRuleContext {
		public TerminalNode Var() { return getToken(JSParser.Var, 0); }
		public VariableDeclarationListContext variableDeclarationList() {
			return getRuleContext(VariableDeclarationListContext.class,0);
		}
		public VariableDeclarationStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableDeclarationStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterVariableDeclarationStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitVariableDeclarationStatement(this);
		}
	}

	public final VariableDeclarationStatementContext variableDeclarationStatement() throws RecognitionException {
		VariableDeclarationStatementContext _localctx = new VariableDeclarationStatementContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_variableDeclarationStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(558);
			match(Var);
			setState(559);
			variableDeclarationList();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VarForDeclarationContext extends ParserRuleContext {
		public TerminalNode Var() { return getToken(JSParser.Var, 0); }
		public ForBindingContext forBinding() {
			return getRuleContext(ForBindingContext.class,0);
		}
		public VarForDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varForDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterVarForDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitVarForDeclaration(this);
		}
	}

	public final VarForDeclarationContext varForDeclaration() throws RecognitionException {
		VarForDeclarationContext _localctx = new VarForDeclarationContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_varForDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(561);
			match(Var);
			setState(562);
			forBinding();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ForDeclarationContext extends ParserRuleContext {
		public LetOrConstContext letOrConst() {
			return getRuleContext(LetOrConstContext.class,0);
		}
		public ForBindingContext forBinding() {
			return getRuleContext(ForBindingContext.class,0);
		}
		public ForDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterForDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitForDeclaration(this);
		}
	}

	public final ForDeclarationContext forDeclaration() throws RecognitionException {
		ForDeclarationContext _localctx = new ForDeclarationContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_forDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(564);
			letOrConst();
			setState(565);
			forBinding();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ForBindingContext extends ParserRuleContext {
		public BindingIdentifierContext bindingIdentifier() {
			return getRuleContext(BindingIdentifierContext.class,0);
		}
		public BindingPatternContext bindingPattern() {
			return getRuleContext(BindingPatternContext.class,0);
		}
		public ForBindingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forBinding; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterForBinding(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitForBinding(this);
		}
	}

	public final ForBindingContext forBinding() throws RecognitionException {
		ForBindingContext _localctx = new ForBindingContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_forBinding);
		try {
			setState(569);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Static:
			case Let:
			case From:
			case Get:
			case Set:
			case As:
			case Of:
			case Target:
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(567);
				bindingIdentifier();
				}
				break;
			case OpenBracket:
			case OpenBrace:
				enterOuterAlt(_localctx, 2);
				{
				setState(568);
				bindingPattern();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ContinueStatementContext extends ParserRuleContext {
		public TerminalNode Continue() { return getToken(JSParser.Continue, 0); }
		public EosContext eos() {
			return getRuleContext(EosContext.class,0);
		}
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public ContinueStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_continueStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterContinueStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitContinueStatement(this);
		}
	}

	public final ContinueStatementContext continueStatement() throws RecognitionException {
		ContinueStatementContext _localctx = new ContinueStatementContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_continueStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(571);
			match(Continue);
			setState(574);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
			case 1:
				{
				setState(572);
				if (!(!here(LineTerminator))) throw new FailedPredicateException(this, "!here(LineTerminator)");
				setState(573);
				identifier();
				}
				break;
			}
			setState(576);
			eos();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BreakStatementContext extends ParserRuleContext {
		public TerminalNode Break() { return getToken(JSParser.Break, 0); }
		public EosContext eos() {
			return getRuleContext(EosContext.class,0);
		}
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public BreakStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_breakStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterBreakStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitBreakStatement(this);
		}
	}

	public final BreakStatementContext breakStatement() throws RecognitionException {
		BreakStatementContext _localctx = new BreakStatementContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_breakStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(578);
			match(Break);
			setState(581);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
			case 1:
				{
				setState(579);
				if (!(!here(LineTerminator))) throw new FailedPredicateException(this, "!here(LineTerminator)");
				setState(580);
				identifier();
				}
				break;
			}
			setState(583);
			eos();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ReturnStatementContext extends ParserRuleContext {
		public TerminalNode Return() { return getToken(JSParser.Return, 0); }
		public EosContext eos() {
			return getRuleContext(EosContext.class,0);
		}
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public ReturnStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_returnStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterReturnStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitReturnStatement(this);
		}
	}

	public final ReturnStatementContext returnStatement() throws RecognitionException {
		ReturnStatementContext _localctx = new ReturnStatementContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_returnStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(585);
			match(Return);
			setState(588);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
			case 1:
				{
				setState(586);
				if (!(!here(LineTerminator))) throw new FailedPredicateException(this, "!here(LineTerminator)");
				setState(587);
				expressionSequence();
				}
				break;
			}
			setState(590);
			eos();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WithStatementContext extends ParserRuleContext {
		public TerminalNode With() { return getToken(JSParser.With, 0); }
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public WithStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_withStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterWithStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitWithStatement(this);
		}
	}

	public final WithStatementContext withStatement() throws RecognitionException {
		WithStatementContext _localctx = new WithStatementContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_withStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(592);
			match(With);
			setState(593);
			match(OpenParen);
			setState(594);
			expressionSequence();
			setState(595);
			match(CloseParen);
			setState(596);
			statement();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SwitchStatementContext extends ParserRuleContext {
		public TerminalNode Switch() { return getToken(JSParser.Switch, 0); }
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public CaseBlockContext caseBlock() {
			return getRuleContext(CaseBlockContext.class,0);
		}
		public SwitchStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_switchStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterSwitchStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitSwitchStatement(this);
		}
	}

	public final SwitchStatementContext switchStatement() throws RecognitionException {
		SwitchStatementContext _localctx = new SwitchStatementContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_switchStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(598);
			match(Switch);
			setState(599);
			match(OpenParen);
			setState(600);
			expressionSequence();
			setState(601);
			match(CloseParen);
			setState(602);
			caseBlock();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CaseBlockContext extends ParserRuleContext {
		public List<CaseClausesContext> caseClauses() {
			return getRuleContexts(CaseClausesContext.class);
		}
		public CaseClausesContext caseClauses(int i) {
			return getRuleContext(CaseClausesContext.class,i);
		}
		public DefaultClauseContext defaultClause() {
			return getRuleContext(DefaultClauseContext.class,0);
		}
		public CaseBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_caseBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterCaseBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitCaseBlock(this);
		}
	}

	public final CaseBlockContext caseBlock() throws RecognitionException {
		CaseBlockContext _localctx = new CaseBlockContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_caseBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(604);
			match(OpenBrace);
			setState(606);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Case) {
				{
				setState(605);
				caseClauses();
				}
			}

			setState(612);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Default) {
				{
				setState(608);
				defaultClause();
				setState(610);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Case) {
					{
					setState(609);
					caseClauses();
					}
				}

				}
			}

			setState(614);
			match(CloseBrace);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CaseClausesContext extends ParserRuleContext {
		public List<CaseClauseContext> caseClause() {
			return getRuleContexts(CaseClauseContext.class);
		}
		public CaseClauseContext caseClause(int i) {
			return getRuleContext(CaseClauseContext.class,i);
		}
		public CaseClausesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_caseClauses; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterCaseClauses(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitCaseClauses(this);
		}
	}

	public final CaseClausesContext caseClauses() throws RecognitionException {
		CaseClausesContext _localctx = new CaseClausesContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_caseClauses);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(617); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(616);
				caseClause();
				}
				}
				setState(619); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==Case );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CaseClauseContext extends ParserRuleContext {
		public TerminalNode Case() { return getToken(JSParser.Case, 0); }
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public StatementListContext statementList() {
			return getRuleContext(StatementListContext.class,0);
		}
		public CaseClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_caseClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterCaseClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitCaseClause(this);
		}
	}

	public final CaseClauseContext caseClause() throws RecognitionException {
		CaseClauseContext _localctx = new CaseClauseContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_caseClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(621);
			match(Case);
			setState(622);
			expressionSequence();
			setState(623);
			match(Colon);
			setState(625);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,44,_ctx) ) {
			case 1:
				{
				setState(624);
				statementList();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DefaultClauseContext extends ParserRuleContext {
		public TerminalNode Default() { return getToken(JSParser.Default, 0); }
		public StatementListContext statementList() {
			return getRuleContext(StatementListContext.class,0);
		}
		public DefaultClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defaultClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterDefaultClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitDefaultClause(this);
		}
	}

	public final DefaultClauseContext defaultClause() throws RecognitionException {
		DefaultClauseContext _localctx = new DefaultClauseContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_defaultClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(627);
			match(Default);
			setState(628);
			match(Colon);
			setState(630);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
			case 1:
				{
				setState(629);
				statementList();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LabelledStatementContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public LabelledItemContext labelledItem() {
			return getRuleContext(LabelledItemContext.class,0);
		}
		public LabelledStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_labelledStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterLabelledStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitLabelledStatement(this);
		}
	}

	public final LabelledStatementContext labelledStatement() throws RecognitionException {
		LabelledStatementContext _localctx = new LabelledStatementContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_labelledStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(632);
			identifier();
			setState(633);
			match(Colon);
			setState(634);
			labelledItem();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LabelledItemContext extends ParserRuleContext {
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public FunctionDeclarationContext functionDeclaration() {
			return getRuleContext(FunctionDeclarationContext.class,0);
		}
		public LabelledItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_labelledItem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterLabelledItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitLabelledItem(this);
		}
	}

	public final LabelledItemContext labelledItem() throws RecognitionException {
		LabelledItemContext _localctx = new LabelledItemContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_labelledItem);
		try {
			setState(638);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(636);
				statement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(637);
				functionDeclaration();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ThrowStatementContext extends ParserRuleContext {
		public TerminalNode Throw() { return getToken(JSParser.Throw, 0); }
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public EosContext eos() {
			return getRuleContext(EosContext.class,0);
		}
		public ThrowStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_throwStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterThrowStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitThrowStatement(this);
		}
	}

	public final ThrowStatementContext throwStatement() throws RecognitionException {
		ThrowStatementContext _localctx = new ThrowStatementContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_throwStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(640);
			match(Throw);
			setState(641);
			if (!(!here(LineTerminator))) throw new FailedPredicateException(this, "!here(LineTerminator)");
			setState(642);
			expressionSequence();
			setState(643);
			eos();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TryStatementContext extends ParserRuleContext {
		public TerminalNode Try() { return getToken(JSParser.Try, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public CatchProductionContext catchProduction() {
			return getRuleContext(CatchProductionContext.class,0);
		}
		public FinallyProductionContext finallyProduction() {
			return getRuleContext(FinallyProductionContext.class,0);
		}
		public TryStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tryStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterTryStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitTryStatement(this);
		}
	}

	public final TryStatementContext tryStatement() throws RecognitionException {
		TryStatementContext _localctx = new TryStatementContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_tryStatement);
		try {
			setState(658);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,47,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(645);
				match(Try);
				setState(646);
				block();
				setState(647);
				catchProduction();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(649);
				match(Try);
				setState(650);
				block();
				setState(651);
				finallyProduction();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(653);
				match(Try);
				setState(654);
				block();
				setState(655);
				catchProduction();
				setState(656);
				finallyProduction();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CatchProductionContext extends ParserRuleContext {
		public TerminalNode Catch() { return getToken(JSParser.Catch, 0); }
		public CatchParameterContext catchParameter() {
			return getRuleContext(CatchParameterContext.class,0);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public CatchProductionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_catchProduction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterCatchProduction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitCatchProduction(this);
		}
	}

	public final CatchProductionContext catchProduction() throws RecognitionException {
		CatchProductionContext _localctx = new CatchProductionContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_catchProduction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(660);
			match(Catch);
			setState(661);
			match(OpenParen);
			setState(662);
			catchParameter();
			setState(663);
			match(CloseParen);
			setState(664);
			block();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FinallyProductionContext extends ParserRuleContext {
		public TerminalNode Finally() { return getToken(JSParser.Finally, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public FinallyProductionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_finallyProduction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterFinallyProduction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitFinallyProduction(this);
		}
	}

	public final FinallyProductionContext finallyProduction() throws RecognitionException {
		FinallyProductionContext _localctx = new FinallyProductionContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_finallyProduction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(666);
			match(Finally);
			setState(667);
			block();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CatchParameterContext extends ParserRuleContext {
		public BindingIdentifierContext bindingIdentifier() {
			return getRuleContext(BindingIdentifierContext.class,0);
		}
		public BindingPatternContext bindingPattern() {
			return getRuleContext(BindingPatternContext.class,0);
		}
		public CatchParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_catchParameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterCatchParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitCatchParameter(this);
		}
	}

	public final CatchParameterContext catchParameter() throws RecognitionException {
		CatchParameterContext _localctx = new CatchParameterContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_catchParameter);
		try {
			setState(671);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Static:
			case Let:
			case From:
			case Get:
			case Set:
			case As:
			case Of:
			case Target:
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(669);
				bindingIdentifier();
				}
				break;
			case OpenBracket:
			case OpenBrace:
				enterOuterAlt(_localctx, 2);
				{
				setState(670);
				bindingPattern();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DebuggerStatementContext extends ParserRuleContext {
		public TerminalNode Debugger() { return getToken(JSParser.Debugger, 0); }
		public EosContext eos() {
			return getRuleContext(EosContext.class,0);
		}
		public DebuggerStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_debuggerStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterDebuggerStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitDebuggerStatement(this);
		}
	}

	public final DebuggerStatementContext debuggerStatement() throws RecognitionException {
		DebuggerStatementContext _localctx = new DebuggerStatementContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_debuggerStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(673);
			match(Debugger);
			setState(674);
			eos();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctionDeclarationContext extends ParserRuleContext {
		public TerminalNode Function() { return getToken(JSParser.Function, 0); }
		public BindingIdentifierContext bindingIdentifier() {
			return getRuleContext(BindingIdentifierContext.class,0);
		}
		public FormalParametersContext formalParameters() {
			return getRuleContext(FormalParametersContext.class,0);
		}
		public FunctionBodyContext functionBody() {
			return getRuleContext(FunctionBodyContext.class,0);
		}
		public FunctionDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterFunctionDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitFunctionDeclaration(this);
		}
	}

	public final FunctionDeclarationContext functionDeclaration() throws RecognitionException {
		FunctionDeclarationContext _localctx = new FunctionDeclarationContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_functionDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(676);
			match(Function);
			setState(677);
			bindingIdentifier();
			setState(678);
			match(OpenParen);
			setState(679);
			formalParameters();
			setState(680);
			match(CloseParen);
			setState(681);
			match(OpenBrace);
			setState(682);
			functionBody();
			setState(683);
			match(CloseBrace);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctionExpressionContext extends ParserRuleContext {
		public TerminalNode Function() { return getToken(JSParser.Function, 0); }
		public FormalParametersContext formalParameters() {
			return getRuleContext(FormalParametersContext.class,0);
		}
		public FunctionBodyContext functionBody() {
			return getRuleContext(FunctionBodyContext.class,0);
		}
		public FunctionDeclarationContext functionDeclaration() {
			return getRuleContext(FunctionDeclarationContext.class,0);
		}
		public FunctionExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterFunctionExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitFunctionExpression(this);
		}
	}

	public final FunctionExpressionContext functionExpression() throws RecognitionException {
		FunctionExpressionContext _localctx = new FunctionExpressionContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_functionExpression);
		try {
			setState(694);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,49,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(685);
				match(Function);
				setState(686);
				match(OpenParen);
				setState(687);
				formalParameters();
				setState(688);
				match(CloseParen);
				setState(689);
				match(OpenBrace);
				setState(690);
				functionBody();
				setState(691);
				match(CloseBrace);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(693);
				functionDeclaration();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StrictFormalParametersContext extends ParserRuleContext {
		public FormalParametersContext formalParameters() {
			return getRuleContext(FormalParametersContext.class,0);
		}
		public StrictFormalParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_strictFormalParameters; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterStrictFormalParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitStrictFormalParameters(this);
		}
	}

	public final StrictFormalParametersContext strictFormalParameters() throws RecognitionException {
		StrictFormalParametersContext _localctx = new StrictFormalParametersContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_strictFormalParameters);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(696);
			formalParameters();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FormalParametersContext extends ParserRuleContext {
		public FormalParameterListContext formalParameterList() {
			return getRuleContext(FormalParameterListContext.class,0);
		}
		public FormalParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formalParameters; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterFormalParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitFormalParameters(this);
		}
	}

	public final FormalParametersContext formalParameters() throws RecognitionException {
		FormalParametersContext _localctx = new FormalParametersContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_formalParameters);
		try {
			setState(700);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CloseParen:
				enterOuterAlt(_localctx, 1);
				{
				}
				break;
			case OpenBracket:
			case OpenBrace:
			case Ellipsis:
			case Static:
			case Let:
			case From:
			case Get:
			case Set:
			case As:
			case Of:
			case Target:
			case Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(699);
				formalParameterList();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FormalParameterListContext extends ParserRuleContext {
		public FunctionRestParameterContext functionRestParameter() {
			return getRuleContext(FunctionRestParameterContext.class,0);
		}
		public FormalsListContext formalsList() {
			return getRuleContext(FormalsListContext.class,0);
		}
		public FormalParameterListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formalParameterList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterFormalParameterList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitFormalParameterList(this);
		}
	}

	public final FormalParameterListContext formalParameterList() throws RecognitionException {
		FormalParameterListContext _localctx = new FormalParameterListContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_formalParameterList);
		int _la;
		try {
			setState(708);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Ellipsis:
				enterOuterAlt(_localctx, 1);
				{
				setState(702);
				functionRestParameter();
				}
				break;
			case OpenBracket:
			case OpenBrace:
			case Static:
			case Let:
			case From:
			case Get:
			case Set:
			case As:
			case Of:
			case Target:
			case Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(703);
				formalsList();
				setState(706);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Comma) {
					{
					setState(704);
					match(Comma);
					setState(705);
					functionRestParameter();
					}
				}

				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FormalsListContext extends ParserRuleContext {
		public List<FormalParameterContext> formalParameter() {
			return getRuleContexts(FormalParameterContext.class);
		}
		public FormalParameterContext formalParameter(int i) {
			return getRuleContext(FormalParameterContext.class,i);
		}
		public FormalsListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formalsList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterFormalsList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitFormalsList(this);
		}
	}

	public final FormalsListContext formalsList() throws RecognitionException {
		FormalsListContext _localctx = new FormalsListContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_formalsList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(710);
			formalParameter();
			setState(715);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,53,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(711);
					match(Comma);
					setState(712);
					formalParameter();
					}
					} 
				}
				setState(717);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,53,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctionRestParameterContext extends ParserRuleContext {
		public BindingRestElementContext bindingRestElement() {
			return getRuleContext(BindingRestElementContext.class,0);
		}
		public FunctionRestParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionRestParameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterFunctionRestParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitFunctionRestParameter(this);
		}
	}

	public final FunctionRestParameterContext functionRestParameter() throws RecognitionException {
		FunctionRestParameterContext _localctx = new FunctionRestParameterContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_functionRestParameter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(718);
			bindingRestElement();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FormalParameterContext extends ParserRuleContext {
		public BindingElementContext bindingElement() {
			return getRuleContext(BindingElementContext.class,0);
		}
		public FormalParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formalParameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterFormalParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitFormalParameter(this);
		}
	}

	public final FormalParameterContext formalParameter() throws RecognitionException {
		FormalParameterContext _localctx = new FormalParameterContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_formalParameter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(720);
			bindingElement();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctionBodyContext extends ParserRuleContext {
		public FunctionStatementListContext functionStatementList() {
			return getRuleContext(FunctionStatementListContext.class,0);
		}
		public FunctionBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionBody; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterFunctionBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitFunctionBody(this);
		}
	}

	public final FunctionBodyContext functionBody() throws RecognitionException {
		FunctionBodyContext _localctx = new FunctionBodyContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_functionBody);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(722);
			functionStatementList();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctionStatementListContext extends ParserRuleContext {
		public StatementListContext statementList() {
			return getRuleContext(StatementListContext.class,0);
		}
		public FunctionStatementListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionStatementList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterFunctionStatementList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitFunctionStatementList(this);
		}
	}

	public final FunctionStatementListContext functionStatementList() throws RecognitionException {
		FunctionStatementListContext _localctx = new FunctionStatementListContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_functionStatementList);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(725);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,54,_ctx) ) {
			case 1:
				{
				setState(724);
				statementList();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArrowFunctionContext extends ParserRuleContext {
		public ArrowParametersContext arrowParameters() {
			return getRuleContext(ArrowParametersContext.class,0);
		}
		public ConciseBodyContext conciseBody() {
			return getRuleContext(ConciseBodyContext.class,0);
		}
		public ArrowFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrowFunction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterArrowFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitArrowFunction(this);
		}
	}

	public final ArrowFunctionContext arrowFunction() throws RecognitionException {
		ArrowFunctionContext _localctx = new ArrowFunctionContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_arrowFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(727);
			arrowParameters();
			setState(728);
			if (!(!here(LineTerminator))) throw new FailedPredicateException(this, "!here(LineTerminator)");
			setState(729);
			match(Arrow);
			setState(730);
			conciseBody();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArrowParametersContext extends ParserRuleContext {
		public BindingIdentifierContext bindingIdentifier() {
			return getRuleContext(BindingIdentifierContext.class,0);
		}
		public CoverParenthesizedExpressionAndArrowParameterListContext coverParenthesizedExpressionAndArrowParameterList() {
			return getRuleContext(CoverParenthesizedExpressionAndArrowParameterListContext.class,0);
		}
		public ArrowParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrowParameters; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterArrowParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitArrowParameters(this);
		}
	}

	public final ArrowParametersContext arrowParameters() throws RecognitionException {
		ArrowParametersContext _localctx = new ArrowParametersContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_arrowParameters);
		try {
			setState(734);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Static:
			case Let:
			case From:
			case Get:
			case Set:
			case As:
			case Of:
			case Target:
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(732);
				bindingIdentifier();
				}
				break;
			case OpenParen:
				enterOuterAlt(_localctx, 2);
				{
				setState(733);
				coverParenthesizedExpressionAndArrowParameterList();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConciseBodyContext extends ParserRuleContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public FunctionBodyContext functionBody() {
			return getRuleContext(FunctionBodyContext.class,0);
		}
		public ConciseBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conciseBody; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterConciseBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitConciseBody(this);
		}
	}

	public final ConciseBodyContext conciseBody() throws RecognitionException {
		ConciseBodyContext _localctx = new ConciseBodyContext(_ctx, getState());
		enterRule(_localctx, 128, RULE_conciseBody);
		try {
			setState(742);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,56,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(736);
				if (!((_input.LA(1) != OpenBrace))) throw new FailedPredicateException(this, "(_input.LA(1) != OpenBrace)");
				setState(737);
				singleExpression(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(738);
				match(OpenBrace);
				setState(739);
				functionBody();
				setState(740);
				match(CloseBrace);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CoverParenthesizedExpressionAndArrowParameterListContext extends ParserRuleContext {
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public BindingRestElementContext bindingRestElement() {
			return getRuleContext(BindingRestElementContext.class,0);
		}
		public CoverParenthesizedExpressionAndArrowParameterListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_coverParenthesizedExpressionAndArrowParameterList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterCoverParenthesizedExpressionAndArrowParameterList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitCoverParenthesizedExpressionAndArrowParameterList(this);
		}
	}

	public final CoverParenthesizedExpressionAndArrowParameterListContext coverParenthesizedExpressionAndArrowParameterList() throws RecognitionException {
		CoverParenthesizedExpressionAndArrowParameterListContext _localctx = new CoverParenthesizedExpressionAndArrowParameterListContext(_ctx, getState());
		enterRule(_localctx, 130, RULE_coverParenthesizedExpressionAndArrowParameterList);
		try {
			setState(760);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,57,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(744);
				match(OpenParen);
				setState(745);
				expressionSequence();
				setState(746);
				match(CloseParen);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(748);
				match(OpenParen);
				setState(749);
				match(CloseParen);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(750);
				match(OpenParen);
				setState(751);
				bindingRestElement();
				setState(752);
				match(CloseParen);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(754);
				match(OpenParen);
				setState(755);
				expressionSequence();
				setState(756);
				match(Comma);
				setState(757);
				bindingRestElement();
				setState(758);
				match(CloseParen);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MethodDefinitionContext extends ParserRuleContext {
		public PropertyNameContext propertyName() {
			return getRuleContext(PropertyNameContext.class,0);
		}
		public StrictFormalParametersContext strictFormalParameters() {
			return getRuleContext(StrictFormalParametersContext.class,0);
		}
		public FunctionBodyContext functionBody() {
			return getRuleContext(FunctionBodyContext.class,0);
		}
		public GeneratorMethodContext generatorMethod() {
			return getRuleContext(GeneratorMethodContext.class,0);
		}
		public PropertySetParameterListContext propertySetParameterList() {
			return getRuleContext(PropertySetParameterListContext.class,0);
		}
		public MethodDefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodDefinition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterMethodDefinition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitMethodDefinition(this);
		}
	}

	public final MethodDefinitionContext methodDefinition() throws RecognitionException {
		MethodDefinitionContext _localctx = new MethodDefinitionContext(_ctx, getState());
		enterRule(_localctx, 132, RULE_methodDefinition);
		try {
			setState(788);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,58,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(762);
				propertyName();
				setState(763);
				match(OpenParen);
				setState(764);
				strictFormalParameters();
				setState(765);
				match(CloseParen);
				setState(766);
				match(OpenBrace);
				setState(767);
				functionBody();
				setState(768);
				match(CloseBrace);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(770);
				generatorMethod();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(771);
				match(Get);
				setState(772);
				propertyName();
				setState(773);
				match(OpenParen);
				setState(774);
				match(CloseParen);
				setState(775);
				match(OpenBrace);
				setState(776);
				functionBody();
				setState(777);
				match(CloseBrace);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(779);
				match(Set);
				setState(780);
				propertyName();
				setState(781);
				match(OpenParen);
				setState(782);
				propertySetParameterList();
				setState(783);
				match(CloseParen);
				setState(784);
				match(OpenBrace);
				setState(785);
				functionBody();
				setState(786);
				match(CloseBrace);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PropertySetParameterListContext extends ParserRuleContext {
		public FormalParameterContext formalParameter() {
			return getRuleContext(FormalParameterContext.class,0);
		}
		public PropertySetParameterListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_propertySetParameterList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterPropertySetParameterList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitPropertySetParameterList(this);
		}
	}

	public final PropertySetParameterListContext propertySetParameterList() throws RecognitionException {
		PropertySetParameterListContext _localctx = new PropertySetParameterListContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_propertySetParameterList);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(790);
			formalParameter();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GeneratorMethodContext extends ParserRuleContext {
		public PropertyNameContext propertyName() {
			return getRuleContext(PropertyNameContext.class,0);
		}
		public StrictFormalParametersContext strictFormalParameters() {
			return getRuleContext(StrictFormalParametersContext.class,0);
		}
		public GeneratorBodyContext generatorBody() {
			return getRuleContext(GeneratorBodyContext.class,0);
		}
		public GeneratorMethodContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_generatorMethod; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterGeneratorMethod(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitGeneratorMethod(this);
		}
	}

	public final GeneratorMethodContext generatorMethod() throws RecognitionException {
		GeneratorMethodContext _localctx = new GeneratorMethodContext(_ctx, getState());
		enterRule(_localctx, 136, RULE_generatorMethod);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(792);
			match(Multiply);
			setState(793);
			propertyName();
			setState(794);
			match(OpenParen);
			setState(795);
			strictFormalParameters();
			setState(796);
			match(CloseParen);
			setState(797);
			match(OpenBrace);
			setState(798);
			generatorBody();
			setState(799);
			match(CloseBrace);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GeneratorDeclarationContext extends ParserRuleContext {
		public TerminalNode Function() { return getToken(JSParser.Function, 0); }
		public BindingIdentifierContext bindingIdentifier() {
			return getRuleContext(BindingIdentifierContext.class,0);
		}
		public FormalParametersContext formalParameters() {
			return getRuleContext(FormalParametersContext.class,0);
		}
		public GeneratorBodyContext generatorBody() {
			return getRuleContext(GeneratorBodyContext.class,0);
		}
		public GeneratorDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_generatorDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterGeneratorDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitGeneratorDeclaration(this);
		}
	}

	public final GeneratorDeclarationContext generatorDeclaration() throws RecognitionException {
		GeneratorDeclarationContext _localctx = new GeneratorDeclarationContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_generatorDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(801);
			match(Function);
			setState(802);
			match(Multiply);
			setState(803);
			bindingIdentifier();
			setState(804);
			match(OpenParen);
			setState(805);
			formalParameters();
			setState(806);
			match(CloseParen);
			setState(807);
			match(OpenBrace);
			setState(808);
			generatorBody();
			setState(809);
			match(CloseBrace);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GeneratorExpressionContext extends ParserRuleContext {
		public TerminalNode Function() { return getToken(JSParser.Function, 0); }
		public FormalParametersContext formalParameters() {
			return getRuleContext(FormalParametersContext.class,0);
		}
		public GeneratorBodyContext generatorBody() {
			return getRuleContext(GeneratorBodyContext.class,0);
		}
		public BindingIdentifierContext bindingIdentifier() {
			return getRuleContext(BindingIdentifierContext.class,0);
		}
		public GeneratorExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_generatorExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterGeneratorExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitGeneratorExpression(this);
		}
	}

	public final GeneratorExpressionContext generatorExpression() throws RecognitionException {
		GeneratorExpressionContext _localctx = new GeneratorExpressionContext(_ctx, getState());
		enterRule(_localctx, 140, RULE_generatorExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(811);
			match(Function);
			setState(812);
			match(Multiply);
			setState(814);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 100)) & ~0x3f) == 0 && ((1L << (_la - 100)) & ((1L << (Static - 100)) | (1L << (Let - 100)) | (1L << (From - 100)) | (1L << (Get - 100)) | (1L << (Set - 100)) | (1L << (As - 100)) | (1L << (Of - 100)) | (1L << (Target - 100)) | (1L << (Identifier - 100)))) != 0)) {
				{
				setState(813);
				bindingIdentifier();
				}
			}

			setState(816);
			match(OpenParen);
			setState(817);
			formalParameters();
			setState(818);
			match(CloseParen);
			setState(819);
			match(OpenBrace);
			setState(820);
			generatorBody();
			setState(821);
			match(CloseBrace);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GeneratorBodyContext extends ParserRuleContext {
		public FunctionBodyContext functionBody() {
			return getRuleContext(FunctionBodyContext.class,0);
		}
		public GeneratorBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_generatorBody; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterGeneratorBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitGeneratorBody(this);
		}
	}

	public final GeneratorBodyContext generatorBody() throws RecognitionException {
		GeneratorBodyContext _localctx = new GeneratorBodyContext(_ctx, getState());
		enterRule(_localctx, 142, RULE_generatorBody);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(823);
			functionBody();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ClassDeclarationContext extends ParserRuleContext {
		public TerminalNode Class() { return getToken(JSParser.Class, 0); }
		public BindingIdentifierContext bindingIdentifier() {
			return getRuleContext(BindingIdentifierContext.class,0);
		}
		public ClassTailContext classTail() {
			return getRuleContext(ClassTailContext.class,0);
		}
		public ClassDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterClassDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitClassDeclaration(this);
		}
	}

	public final ClassDeclarationContext classDeclaration() throws RecognitionException {
		ClassDeclarationContext _localctx = new ClassDeclarationContext(_ctx, getState());
		enterRule(_localctx, 144, RULE_classDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(825);
			match(Class);
			setState(826);
			bindingIdentifier();
			setState(827);
			classTail();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ClassExpressionContext extends ParserRuleContext {
		public TerminalNode Class() { return getToken(JSParser.Class, 0); }
		public ClassTailContext classTail() {
			return getRuleContext(ClassTailContext.class,0);
		}
		public BindingIdentifierContext bindingIdentifier() {
			return getRuleContext(BindingIdentifierContext.class,0);
		}
		public ClassExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterClassExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitClassExpression(this);
		}
	}

	public final ClassExpressionContext classExpression() throws RecognitionException {
		ClassExpressionContext _localctx = new ClassExpressionContext(_ctx, getState());
		enterRule(_localctx, 146, RULE_classExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(829);
			match(Class);
			setState(831);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 100)) & ~0x3f) == 0 && ((1L << (_la - 100)) & ((1L << (Static - 100)) | (1L << (Let - 100)) | (1L << (From - 100)) | (1L << (Get - 100)) | (1L << (Set - 100)) | (1L << (As - 100)) | (1L << (Of - 100)) | (1L << (Target - 100)) | (1L << (Identifier - 100)))) != 0)) {
				{
				setState(830);
				bindingIdentifier();
				}
			}

			setState(833);
			classTail();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ClassTailContext extends ParserRuleContext {
		public ClassHeritageContext classHeritage() {
			return getRuleContext(ClassHeritageContext.class,0);
		}
		public ClassBodyContext classBody() {
			return getRuleContext(ClassBodyContext.class,0);
		}
		public ClassTailContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classTail; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterClassTail(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitClassTail(this);
		}
	}

	public final ClassTailContext classTail() throws RecognitionException {
		ClassTailContext _localctx = new ClassTailContext(_ctx, getState());
		enterRule(_localctx, 148, RULE_classTail);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(836);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Extends) {
				{
				setState(835);
				classHeritage();
				}
			}

			setState(838);
			match(OpenBrace);
			setState(840);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OpenBracket) | (1L << SemiColon) | (1L << Multiply) | (1L << NullLiteral) | (1L << BooleanLiteral) | (1L << DecimalLiteral) | (1L << HexIntegerLiteral) | (1L << OctalIntegerLiteral) | (1L << BinaryIntegerLiteral) | (1L << Break) | (1L << Do) | (1L << Instanceof) | (1L << Typeof) | (1L << Case))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (Else - 64)) | (1L << (New - 64)) | (1L << (Var - 64)) | (1L << (Catch - 64)) | (1L << (Finally - 64)) | (1L << (Return - 64)) | (1L << (Void - 64)) | (1L << (Continue - 64)) | (1L << (For - 64)) | (1L << (Switch - 64)) | (1L << (While - 64)) | (1L << (Debugger - 64)) | (1L << (Function - 64)) | (1L << (This - 64)) | (1L << (With - 64)) | (1L << (Default - 64)) | (1L << (If - 64)) | (1L << (Throw - 64)) | (1L << (Delete - 64)) | (1L << (In - 64)) | (1L << (Try - 64)) | (1L << (Export - 64)) | (1L << (Class - 64)) | (1L << (Extends - 64)) | (1L << (Const - 64)) | (1L << (Super - 64)) | (1L << (Yield - 64)) | (1L << (Import - 64)) | (1L << (Enum - 64)) | (1L << (Await - 64)) | (1L << (Implements - 64)) | (1L << (Private - 64)) | (1L << (Public - 64)) | (1L << (Interface - 64)) | (1L << (Package - 64)) | (1L << (Protected - 64)) | (1L << (Static - 64)) | (1L << (Let - 64)) | (1L << (From - 64)) | (1L << (Get - 64)) | (1L << (Set - 64)) | (1L << (As - 64)) | (1L << (Of - 64)) | (1L << (Target - 64)) | (1L << (Identifier - 64)) | (1L << (StringLiteral - 64)))) != 0)) {
				{
				setState(839);
				classBody();
				}
			}

			setState(842);
			match(CloseBrace);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ClassHeritageContext extends ParserRuleContext {
		public TerminalNode Extends() { return getToken(JSParser.Extends, 0); }
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public ClassHeritageContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classHeritage; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterClassHeritage(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitClassHeritage(this);
		}
	}

	public final ClassHeritageContext classHeritage() throws RecognitionException {
		ClassHeritageContext _localctx = new ClassHeritageContext(_ctx, getState());
		enterRule(_localctx, 150, RULE_classHeritage);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(844);
			match(Extends);
			setState(845);
			singleExpression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ClassBodyContext extends ParserRuleContext {
		public ClassElementListContext classElementList() {
			return getRuleContext(ClassElementListContext.class,0);
		}
		public ClassBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classBody; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterClassBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitClassBody(this);
		}
	}

	public final ClassBodyContext classBody() throws RecognitionException {
		ClassBodyContext _localctx = new ClassBodyContext(_ctx, getState());
		enterRule(_localctx, 152, RULE_classBody);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(847);
			classElementList();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ClassElementListContext extends ParserRuleContext {
		public List<ClassElementContext> classElement() {
			return getRuleContexts(ClassElementContext.class);
		}
		public ClassElementContext classElement(int i) {
			return getRuleContext(ClassElementContext.class,i);
		}
		public ClassElementListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classElementList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterClassElementList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitClassElementList(this);
		}
	}

	public final ClassElementListContext classElementList() throws RecognitionException {
		ClassElementListContext _localctx = new ClassElementListContext(_ctx, getState());
		enterRule(_localctx, 154, RULE_classElementList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(850); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(849);
				classElement();
				}
				}
				setState(852); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OpenBracket) | (1L << SemiColon) | (1L << Multiply) | (1L << NullLiteral) | (1L << BooleanLiteral) | (1L << DecimalLiteral) | (1L << HexIntegerLiteral) | (1L << OctalIntegerLiteral) | (1L << BinaryIntegerLiteral) | (1L << Break) | (1L << Do) | (1L << Instanceof) | (1L << Typeof) | (1L << Case))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (Else - 64)) | (1L << (New - 64)) | (1L << (Var - 64)) | (1L << (Catch - 64)) | (1L << (Finally - 64)) | (1L << (Return - 64)) | (1L << (Void - 64)) | (1L << (Continue - 64)) | (1L << (For - 64)) | (1L << (Switch - 64)) | (1L << (While - 64)) | (1L << (Debugger - 64)) | (1L << (Function - 64)) | (1L << (This - 64)) | (1L << (With - 64)) | (1L << (Default - 64)) | (1L << (If - 64)) | (1L << (Throw - 64)) | (1L << (Delete - 64)) | (1L << (In - 64)) | (1L << (Try - 64)) | (1L << (Export - 64)) | (1L << (Class - 64)) | (1L << (Extends - 64)) | (1L << (Const - 64)) | (1L << (Super - 64)) | (1L << (Yield - 64)) | (1L << (Import - 64)) | (1L << (Enum - 64)) | (1L << (Await - 64)) | (1L << (Implements - 64)) | (1L << (Private - 64)) | (1L << (Public - 64)) | (1L << (Interface - 64)) | (1L << (Package - 64)) | (1L << (Protected - 64)) | (1L << (Static - 64)) | (1L << (Let - 64)) | (1L << (From - 64)) | (1L << (Get - 64)) | (1L << (Set - 64)) | (1L << (As - 64)) | (1L << (Of - 64)) | (1L << (Target - 64)) | (1L << (Identifier - 64)) | (1L << (StringLiteral - 64)))) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ClassElementContext extends ParserRuleContext {
		public MethodDefinitionContext methodDefinition() {
			return getRuleContext(MethodDefinitionContext.class,0);
		}
		public TerminalNode Static() { return getToken(JSParser.Static, 0); }
		public TerminalNode SemiColon() { return getToken(JSParser.SemiColon, 0); }
		public ClassElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterClassElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitClassElement(this);
		}
	}

	public final ClassElementContext classElement() throws RecognitionException {
		ClassElementContext _localctx = new ClassElementContext(_ctx, getState());
		enterRule(_localctx, 156, RULE_classElement);
		try {
			setState(858);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,64,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(854);
				methodDefinition();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(855);
				match(Static);
				setState(856);
				methodDefinition();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(857);
				match(SemiColon);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArrayLiteralContext extends ParserRuleContext {
		public ElisionContext elision() {
			return getRuleContext(ElisionContext.class,0);
		}
		public ElementListContext elementList() {
			return getRuleContext(ElementListContext.class,0);
		}
		public ArrayLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterArrayLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitArrayLiteral(this);
		}
	}

	public final ArrayLiteralContext arrayLiteral() throws RecognitionException {
		ArrayLiteralContext _localctx = new ArrayLiteralContext(_ctx, getState());
		enterRule(_localctx, 158, RULE_arrayLiteral);
		int _la;
		try {
			setState(877);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,67,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(860);
				match(OpenBracket);
				setState(862);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Comma) {
					{
					setState(861);
					elision();
					}
				}

				setState(864);
				match(CloseBracket);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(865);
				match(OpenBracket);
				setState(866);
				elementList(0);
				setState(867);
				match(CloseBracket);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(869);
				match(OpenBracket);
				setState(870);
				elementList(0);
				setState(871);
				match(Comma);
				setState(873);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Comma) {
					{
					setState(872);
					elision();
					}
				}

				setState(875);
				match(CloseBracket);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ElementListContext extends ParserRuleContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public ElisionContext elision() {
			return getRuleContext(ElisionContext.class,0);
		}
		public SpreadElementContext spreadElement() {
			return getRuleContext(SpreadElementContext.class,0);
		}
		public ElementListContext elementList() {
			return getRuleContext(ElementListContext.class,0);
		}
		public ElementListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elementList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterElementList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitElementList(this);
		}
	}

	public final ElementListContext elementList() throws RecognitionException {
		return elementList(0);
	}

	private ElementListContext elementList(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ElementListContext _localctx = new ElementListContext(_ctx, _parentState);
		ElementListContext _prevctx = _localctx;
		int _startState = 160;
		enterRecursionRule(_localctx, 160, RULE_elementList, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(888);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,70,_ctx) ) {
			case 1:
				{
				setState(881);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Comma) {
					{
					setState(880);
					elision();
					}
				}

				setState(883);
				singleExpression(0);
				}
				break;
			case 2:
				{
				setState(885);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Comma) {
					{
					setState(884);
					elision();
					}
				}

				setState(887);
				spreadElement();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(904);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,74,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(902);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,73,_ctx) ) {
					case 1:
						{
						_localctx = new ElementListContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_elementList);
						setState(890);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(891);
						match(Comma);
						setState(893);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==Comma) {
							{
							setState(892);
							elision();
							}
						}

						setState(895);
						singleExpression(0);
						}
						break;
					case 2:
						{
						_localctx = new ElementListContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_elementList);
						setState(896);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(897);
						match(Comma);
						setState(899);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==Comma) {
							{
							setState(898);
							elision();
							}
						}

						setState(901);
						spreadElement();
						}
						break;
					}
					} 
				}
				setState(906);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,74,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ElisionContext extends ParserRuleContext {
		public ElisionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elision; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterElision(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitElision(this);
		}
	}

	public final ElisionContext elision() throws RecognitionException {
		ElisionContext _localctx = new ElisionContext(_ctx, getState());
		enterRule(_localctx, 162, RULE_elision);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(908); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(907);
				match(Comma);
				}
				}
				setState(910); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==Comma );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SpreadElementContext extends ParserRuleContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public SpreadElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_spreadElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterSpreadElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitSpreadElement(this);
		}
	}

	public final SpreadElementContext spreadElement() throws RecognitionException {
		SpreadElementContext _localctx = new SpreadElementContext(_ctx, getState());
		enterRule(_localctx, 164, RULE_spreadElement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(912);
			match(Ellipsis);
			setState(913);
			singleExpression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ObjectLiteralContext extends ParserRuleContext {
		public PropertyDefinitionListContext propertyDefinitionList() {
			return getRuleContext(PropertyDefinitionListContext.class,0);
		}
		public ObjectLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_objectLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterObjectLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitObjectLiteral(this);
		}
	}

	public final ObjectLiteralContext objectLiteral() throws RecognitionException {
		ObjectLiteralContext _localctx = new ObjectLiteralContext(_ctx, getState());
		enterRule(_localctx, 166, RULE_objectLiteral);
		int _la;
		try {
			setState(924);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,77,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(915);
				match(OpenBrace);
				setState(916);
				match(CloseBrace);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(917);
				match(OpenBrace);
				setState(918);
				propertyDefinitionList();
				setState(920);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Comma) {
					{
					setState(919);
					match(Comma);
					}
				}

				setState(922);
				match(CloseBrace);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PropertyDefinitionListContext extends ParserRuleContext {
		public List<PropertyDefinitionContext> propertyDefinition() {
			return getRuleContexts(PropertyDefinitionContext.class);
		}
		public PropertyDefinitionContext propertyDefinition(int i) {
			return getRuleContext(PropertyDefinitionContext.class,i);
		}
		public PropertyDefinitionListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_propertyDefinitionList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterPropertyDefinitionList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitPropertyDefinitionList(this);
		}
	}

	public final PropertyDefinitionListContext propertyDefinitionList() throws RecognitionException {
		PropertyDefinitionListContext _localctx = new PropertyDefinitionListContext(_ctx, getState());
		enterRule(_localctx, 168, RULE_propertyDefinitionList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(926);
			propertyDefinition();
			setState(931);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,78,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(927);
					match(Comma);
					setState(928);
					propertyDefinition();
					}
					} 
				}
				setState(933);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,78,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PropertyDefinitionContext extends ParserRuleContext {
		public IdentifierReferenceContext identifierReference() {
			return getRuleContext(IdentifierReferenceContext.class,0);
		}
		public CoverInitializedNameContext coverInitializedName() {
			return getRuleContext(CoverInitializedNameContext.class,0);
		}
		public PropertyNameContext propertyName() {
			return getRuleContext(PropertyNameContext.class,0);
		}
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public MethodDefinitionContext methodDefinition() {
			return getRuleContext(MethodDefinitionContext.class,0);
		}
		public PropertyDefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_propertyDefinition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterPropertyDefinition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitPropertyDefinition(this);
		}
	}

	public final PropertyDefinitionContext propertyDefinition() throws RecognitionException {
		PropertyDefinitionContext _localctx = new PropertyDefinitionContext(_ctx, getState());
		enterRule(_localctx, 170, RULE_propertyDefinition);
		try {
			setState(941);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,79,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(934);
				identifierReference();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(935);
				coverInitializedName();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(936);
				propertyName();
				setState(937);
				match(Colon);
				setState(938);
				singleExpression(0);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(940);
				methodDefinition();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PropertyNameContext extends ParserRuleContext {
		public LiteralPropertyNameContext literalPropertyName() {
			return getRuleContext(LiteralPropertyNameContext.class,0);
		}
		public ComputedPropertyNameContext computedPropertyName() {
			return getRuleContext(ComputedPropertyNameContext.class,0);
		}
		public PropertyNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_propertyName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterPropertyName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitPropertyName(this);
		}
	}

	public final PropertyNameContext propertyName() throws RecognitionException {
		PropertyNameContext _localctx = new PropertyNameContext(_ctx, getState());
		enterRule(_localctx, 172, RULE_propertyName);
		try {
			setState(945);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NullLiteral:
			case BooleanLiteral:
			case DecimalLiteral:
			case HexIntegerLiteral:
			case OctalIntegerLiteral:
			case BinaryIntegerLiteral:
			case Break:
			case Do:
			case Instanceof:
			case Typeof:
			case Case:
			case Else:
			case New:
			case Var:
			case Catch:
			case Finally:
			case Return:
			case Void:
			case Continue:
			case For:
			case Switch:
			case While:
			case Debugger:
			case Function:
			case This:
			case With:
			case Default:
			case If:
			case Throw:
			case Delete:
			case In:
			case Try:
			case Export:
			case Class:
			case Extends:
			case Const:
			case Super:
			case Yield:
			case Import:
			case Enum:
			case Await:
			case Implements:
			case Private:
			case Public:
			case Interface:
			case Package:
			case Protected:
			case Static:
			case Let:
			case From:
			case Get:
			case Set:
			case As:
			case Of:
			case Target:
			case Identifier:
			case StringLiteral:
				enterOuterAlt(_localctx, 1);
				{
				setState(943);
				literalPropertyName();
				}
				break;
			case OpenBracket:
				enterOuterAlt(_localctx, 2);
				{
				setState(944);
				computedPropertyName();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LiteralPropertyNameContext extends ParserRuleContext {
		public IdentifierNameContext identifierName() {
			return getRuleContext(IdentifierNameContext.class,0);
		}
		public TerminalNode StringLiteral() { return getToken(JSParser.StringLiteral, 0); }
		public NumericLiteralContext numericLiteral() {
			return getRuleContext(NumericLiteralContext.class,0);
		}
		public LiteralPropertyNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literalPropertyName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterLiteralPropertyName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitLiteralPropertyName(this);
		}
	}

	public final LiteralPropertyNameContext literalPropertyName() throws RecognitionException {
		LiteralPropertyNameContext _localctx = new LiteralPropertyNameContext(_ctx, getState());
		enterRule(_localctx, 174, RULE_literalPropertyName);
		try {
			setState(950);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NullLiteral:
			case BooleanLiteral:
			case Break:
			case Do:
			case Instanceof:
			case Typeof:
			case Case:
			case Else:
			case New:
			case Var:
			case Catch:
			case Finally:
			case Return:
			case Void:
			case Continue:
			case For:
			case Switch:
			case While:
			case Debugger:
			case Function:
			case This:
			case With:
			case Default:
			case If:
			case Throw:
			case Delete:
			case In:
			case Try:
			case Export:
			case Class:
			case Extends:
			case Const:
			case Super:
			case Yield:
			case Import:
			case Enum:
			case Await:
			case Implements:
			case Private:
			case Public:
			case Interface:
			case Package:
			case Protected:
			case Static:
			case Let:
			case From:
			case Get:
			case Set:
			case As:
			case Of:
			case Target:
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(947);
				identifierName();
				}
				break;
			case StringLiteral:
				enterOuterAlt(_localctx, 2);
				{
				setState(948);
				match(StringLiteral);
				}
				break;
			case DecimalLiteral:
			case HexIntegerLiteral:
			case OctalIntegerLiteral:
			case BinaryIntegerLiteral:
				enterOuterAlt(_localctx, 3);
				{
				setState(949);
				numericLiteral();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ComputedPropertyNameContext extends ParserRuleContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public ComputedPropertyNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_computedPropertyName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterComputedPropertyName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitComputedPropertyName(this);
		}
	}

	public final ComputedPropertyNameContext computedPropertyName() throws RecognitionException {
		ComputedPropertyNameContext _localctx = new ComputedPropertyNameContext(_ctx, getState());
		enterRule(_localctx, 176, RULE_computedPropertyName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(952);
			match(OpenBracket);
			setState(953);
			singleExpression(0);
			setState(954);
			match(CloseBracket);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CoverInitializedNameContext extends ParserRuleContext {
		public IdentifierReferenceContext identifierReference() {
			return getRuleContext(IdentifierReferenceContext.class,0);
		}
		public InitializerContext initializer() {
			return getRuleContext(InitializerContext.class,0);
		}
		public CoverInitializedNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_coverInitializedName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterCoverInitializedName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitCoverInitializedName(this);
		}
	}

	public final CoverInitializedNameContext coverInitializedName() throws RecognitionException {
		CoverInitializedNameContext _localctx = new CoverInitializedNameContext(_ctx, getState());
		enterRule(_localctx, 178, RULE_coverInitializedName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(956);
			identifierReference();
			setState(957);
			initializer();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArgumentsContext extends ParserRuleContext {
		public ArgumentListContext argumentList() {
			return getRuleContext(ArgumentListContext.class,0);
		}
		public ArgumentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arguments; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterArguments(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitArguments(this);
		}
	}

	public final ArgumentsContext arguments() throws RecognitionException {
		ArgumentsContext _localctx = new ArgumentsContext(_ctx, getState());
		enterRule(_localctx, 180, RULE_arguments);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(959);
			match(OpenParen);
			setState(961);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RegularExpressionLiteral) | (1L << OpenBracket) | (1L << OpenParen) | (1L << OpenBrace) | (1L << Ellipsis) | (1L << PlusPlus) | (1L << MinusMinus) | (1L << Plus) | (1L << Minus) | (1L << BitNot) | (1L << Not) | (1L << NullLiteral) | (1L << BooleanLiteral) | (1L << DecimalLiteral) | (1L << HexIntegerLiteral) | (1L << OctalIntegerLiteral) | (1L << BinaryIntegerLiteral) | (1L << Typeof))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (New - 65)) | (1L << (Void - 65)) | (1L << (Function - 65)) | (1L << (This - 65)) | (1L << (Delete - 65)) | (1L << (Class - 65)) | (1L << (Super - 65)) | (1L << (Yield - 65)) | (1L << (Static - 65)) | (1L << (Let - 65)) | (1L << (From - 65)) | (1L << (Get - 65)) | (1L << (Set - 65)) | (1L << (As - 65)) | (1L << (Of - 65)) | (1L << (Target - 65)) | (1L << (Identifier - 65)) | (1L << (StringLiteral - 65)) | (1L << (NoSubstitutionTemplate - 65)) | (1L << (TemplateHead - 65)))) != 0)) {
				{
				setState(960);
				argumentList();
				}
			}

			setState(963);
			match(CloseParen);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArgumentListContext extends ParserRuleContext {
		public List<SpreadElementContext> spreadElement() {
			return getRuleContexts(SpreadElementContext.class);
		}
		public SpreadElementContext spreadElement(int i) {
			return getRuleContext(SpreadElementContext.class,i);
		}
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public ArgumentListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argumentList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterArgumentList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitArgumentList(this);
		}
	}

	public final ArgumentListContext argumentList() throws RecognitionException {
		ArgumentListContext _localctx = new ArgumentListContext(_ctx, getState());
		enterRule(_localctx, 182, RULE_argumentList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(967);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Ellipsis:
				{
				setState(965);
				spreadElement();
				}
				break;
			case RegularExpressionLiteral:
			case OpenBracket:
			case OpenParen:
			case OpenBrace:
			case PlusPlus:
			case MinusMinus:
			case Plus:
			case Minus:
			case BitNot:
			case Not:
			case NullLiteral:
			case BooleanLiteral:
			case DecimalLiteral:
			case HexIntegerLiteral:
			case OctalIntegerLiteral:
			case BinaryIntegerLiteral:
			case Typeof:
			case New:
			case Void:
			case Function:
			case This:
			case Delete:
			case Class:
			case Super:
			case Yield:
			case Static:
			case Let:
			case From:
			case Get:
			case Set:
			case As:
			case Of:
			case Target:
			case Identifier:
			case StringLiteral:
			case NoSubstitutionTemplate:
			case TemplateHead:
				{
				setState(966);
				singleExpression(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(976);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(969);
				match(Comma);
				setState(972);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case Ellipsis:
					{
					setState(970);
					spreadElement();
					}
					break;
				case RegularExpressionLiteral:
				case OpenBracket:
				case OpenParen:
				case OpenBrace:
				case PlusPlus:
				case MinusMinus:
				case Plus:
				case Minus:
				case BitNot:
				case Not:
				case NullLiteral:
				case BooleanLiteral:
				case DecimalLiteral:
				case HexIntegerLiteral:
				case OctalIntegerLiteral:
				case BinaryIntegerLiteral:
				case Typeof:
				case New:
				case Void:
				case Function:
				case This:
				case Delete:
				case Class:
				case Super:
				case Yield:
				case Static:
				case Let:
				case From:
				case Get:
				case Set:
				case As:
				case Of:
				case Target:
				case Identifier:
				case StringLiteral:
				case NoSubstitutionTemplate:
				case TemplateHead:
					{
					setState(971);
					singleExpression(0);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				}
				setState(978);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionSequenceContext extends ParserRuleContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public ExpressionSequenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionSequence; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterExpressionSequence(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitExpressionSequence(this);
		}
	}

	public final ExpressionSequenceContext expressionSequence() throws RecognitionException {
		ExpressionSequenceContext _localctx = new ExpressionSequenceContext(_ctx, getState());
		enterRule(_localctx, 184, RULE_expressionSequence);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(979);
			singleExpression(0);
			setState(984);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,86,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(980);
					match(Comma);
					setState(981);
					singleExpression(0);
					}
					} 
				}
				setState(986);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,86,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SingleExpressionContext extends ParserRuleContext {
		public SingleExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleExpression; }
	 
		public SingleExpressionContext() { }
		public void copyFrom(SingleExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class TernaryExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public TernaryExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterTernaryExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitTernaryExpression(this);
		}
	}
	public static class LogicalAndExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public LogicalAndExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterLogicalAndExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitLogicalAndExpression(this);
		}
	}
	public static class PreIncrementExpressionContext extends SingleExpressionContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public PreIncrementExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterPreIncrementExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitPreIncrementExpression(this);
		}
	}
	public static class ObjectLiteralExpressionContext extends SingleExpressionContext {
		public ObjectLiteralContext objectLiteral() {
			return getRuleContext(ObjectLiteralContext.class,0);
		}
		public ObjectLiteralExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterObjectLiteralExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitObjectLiteralExpression(this);
		}
	}
	public static class NewTargetExpressionContext extends SingleExpressionContext {
		public TerminalNode New() { return getToken(JSParser.New, 0); }
		public NewTargetExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterNewTargetExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitNewTargetExpression(this);
		}
	}
	public static class InExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public TerminalNode In() { return getToken(JSParser.In, 0); }
		public InExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterInExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitInExpression(this);
		}
	}
	public static class LogicalOrExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public LogicalOrExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterLogicalOrExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitLogicalOrExpression(this);
		}
	}
	public static class GeneratorExpressionExpressionContext extends SingleExpressionContext {
		public GeneratorExpressionContext generatorExpression() {
			return getRuleContext(GeneratorExpressionContext.class,0);
		}
		public GeneratorExpressionExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterGeneratorExpressionExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitGeneratorExpressionExpression(this);
		}
	}
	public static class NotExpressionContext extends SingleExpressionContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public NotExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterNotExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitNotExpression(this);
		}
	}
	public static class PreDecreaseExpressionContext extends SingleExpressionContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public PreDecreaseExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterPreDecreaseExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitPreDecreaseExpression(this);
		}
	}
	public static class ArgumentsExpressionContext extends SingleExpressionContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public ArgumentsContext arguments() {
			return getRuleContext(ArgumentsContext.class,0);
		}
		public ArgumentsExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterArgumentsExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitArgumentsExpression(this);
		}
	}
	public static class ThisExpressionContext extends SingleExpressionContext {
		public TerminalNode This() { return getToken(JSParser.This, 0); }
		public ThisExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterThisExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitThisExpression(this);
		}
	}
	public static class RegularExpressionLiteralExpressionContext extends SingleExpressionContext {
		public TerminalNode RegularExpressionLiteral() { return getToken(JSParser.RegularExpressionLiteral, 0); }
		public RegularExpressionLiteralExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterRegularExpressionLiteralExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitRegularExpressionLiteralExpression(this);
		}
	}
	public static class UnaryMinusExpressionContext extends SingleExpressionContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public UnaryMinusExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterUnaryMinusExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitUnaryMinusExpression(this);
		}
	}
	public static class AssignmentExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public AssignmentExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterAssignmentExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitAssignmentExpression(this);
		}
	}
	public static class PostDecreaseExpressionContext extends SingleExpressionContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public PostDecreaseExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterPostDecreaseExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitPostDecreaseExpression(this);
		}
	}
	public static class TypeofExpressionContext extends SingleExpressionContext {
		public TerminalNode Typeof() { return getToken(JSParser.Typeof, 0); }
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public TypeofExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterTypeofExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitTypeofExpression(this);
		}
	}
	public static class InstanceofExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public TerminalNode Instanceof() { return getToken(JSParser.Instanceof, 0); }
		public InstanceofExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterInstanceofExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitInstanceofExpression(this);
		}
	}
	public static class UnaryPlusExpressionContext extends SingleExpressionContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public UnaryPlusExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterUnaryPlusExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitUnaryPlusExpression(this);
		}
	}
	public static class DeleteExpressionContext extends SingleExpressionContext {
		public TerminalNode Delete() { return getToken(JSParser.Delete, 0); }
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public DeleteExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterDeleteExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitDeleteExpression(this);
		}
	}
	public static class ArrowFunctionExpressionContext extends SingleExpressionContext {
		public ArrowFunctionContext arrowFunction() {
			return getRuleContext(ArrowFunctionContext.class,0);
		}
		public ArrowFunctionExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterArrowFunctionExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitArrowFunctionExpression(this);
		}
	}
	public static class EqualityExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public EqualityExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterEqualityExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitEqualityExpression(this);
		}
	}
	public static class BitXOrExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public BitXOrExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterBitXOrExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitBitXOrExpression(this);
		}
	}
	public static class SuperExpressionContext extends SingleExpressionContext {
		public TerminalNode Super() { return getToken(JSParser.Super, 0); }
		public SuperExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterSuperExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitSuperExpression(this);
		}
	}
	public static class MultiplicativeExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public MultiplicativeExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterMultiplicativeExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitMultiplicativeExpression(this);
		}
	}
	public static class BitShiftExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public BitShiftExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterBitShiftExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitBitShiftExpression(this);
		}
	}
	public static class ParenthesizedExpressionContext extends SingleExpressionContext {
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public ParenthesizedExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterParenthesizedExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitParenthesizedExpression(this);
		}
	}
	public static class AdditiveExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public AdditiveExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterAdditiveExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitAdditiveExpression(this);
		}
	}
	public static class RelationalExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public RelationalExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterRelationalExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitRelationalExpression(this);
		}
	}
	public static class PostIncrementExpressionContext extends SingleExpressionContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public PostIncrementExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterPostIncrementExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitPostIncrementExpression(this);
		}
	}
	public static class ClassExpressionExpressionContext extends SingleExpressionContext {
		public ClassExpressionContext classExpression() {
			return getRuleContext(ClassExpressionContext.class,0);
		}
		public ClassExpressionExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterClassExpressionExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitClassExpressionExpression(this);
		}
	}
	public static class YieldExpressionContext extends SingleExpressionContext {
		public TerminalNode Yield() { return getToken(JSParser.Yield, 0); }
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public YieldExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterYieldExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitYieldExpression(this);
		}
	}
	public static class BitNotExpressionContext extends SingleExpressionContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public BitNotExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterBitNotExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitBitNotExpression(this);
		}
	}
	public static class NewExpressionContext extends SingleExpressionContext {
		public TerminalNode New() { return getToken(JSParser.New, 0); }
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public ArgumentsContext arguments() {
			return getRuleContext(ArgumentsContext.class,0);
		}
		public NewExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterNewExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitNewExpression(this);
		}
	}
	public static class LiteralExpressionContext extends SingleExpressionContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public LiteralExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterLiteralExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitLiteralExpression(this);
		}
	}
	public static class ArrayLiteralExpressionContext extends SingleExpressionContext {
		public ArrayLiteralContext arrayLiteral() {
			return getRuleContext(ArrayLiteralContext.class,0);
		}
		public ArrayLiteralExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterArrayLiteralExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitArrayLiteralExpression(this);
		}
	}
	public static class MemberDotExpressionContext extends SingleExpressionContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public IdentifierNameContext identifierName() {
			return getRuleContext(IdentifierNameContext.class,0);
		}
		public MemberDotExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterMemberDotExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitMemberDotExpression(this);
		}
	}
	public static class MemberIndexExpressionContext extends SingleExpressionContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public MemberIndexExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterMemberIndexExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitMemberIndexExpression(this);
		}
	}
	public static class IdentifierExpressionContext extends SingleExpressionContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public IdentifierExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterIdentifierExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitIdentifierExpression(this);
		}
	}
	public static class BitAndExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public BitAndExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterBitAndExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitBitAndExpression(this);
		}
	}
	public static class BitOrExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public BitOrExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterBitOrExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitBitOrExpression(this);
		}
	}
	public static class FunctionExpressionExpressionContext extends SingleExpressionContext {
		public FunctionExpressionContext functionExpression() {
			return getRuleContext(FunctionExpressionContext.class,0);
		}
		public FunctionExpressionExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterFunctionExpressionExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitFunctionExpressionExpression(this);
		}
	}
	public static class AssignmentOperatorExpressionContext extends SingleExpressionContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public AssignmentOperatorContext assignmentOperator() {
			return getRuleContext(AssignmentOperatorContext.class,0);
		}
		public AssignmentOperatorExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterAssignmentOperatorExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitAssignmentOperatorExpression(this);
		}
	}
	public static class VoidExpressionContext extends SingleExpressionContext {
		public TerminalNode Void() { return getToken(JSParser.Void, 0); }
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public VoidExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterVoidExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitVoidExpression(this);
		}
	}
	public static class CallTemplateLiteralExpressionContext extends SingleExpressionContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public TemplateLiteralContext templateLiteral() {
			return getRuleContext(TemplateLiteralContext.class,0);
		}
		public CallTemplateLiteralExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterCallTemplateLiteralExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitCallTemplateLiteralExpression(this);
		}
	}
	public static class TemplateLiteralExpressionContext extends SingleExpressionContext {
		public TemplateLiteralContext templateLiteral() {
			return getRuleContext(TemplateLiteralContext.class,0);
		}
		public TemplateLiteralExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterTemplateLiteralExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitTemplateLiteralExpression(this);
		}
	}

	public final SingleExpressionContext singleExpression() throws RecognitionException {
		return singleExpression(0);
	}

	private SingleExpressionContext singleExpression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		SingleExpressionContext _localctx = new SingleExpressionContext(_ctx, _parentState);
		SingleExpressionContext _prevctx = _localctx;
		int _startState = 186;
		enterRecursionRule(_localctx, 186, RULE_singleExpression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1038);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,90,_ctx) ) {
			case 1:
				{
				_localctx = new FunctionExpressionExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(988);
				functionExpression();
				}
				break;
			case 2:
				{
				_localctx = new ClassExpressionExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(989);
				classExpression();
				}
				break;
			case 3:
				{
				_localctx = new GeneratorExpressionExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(990);
				generatorExpression();
				}
				break;
			case 4:
				{
				_localctx = new NewTargetExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(991);
				match(New);
				setState(992);
				match(Dot);
				setState(993);
				match(Target);
				}
				break;
			case 5:
				{
				_localctx = new NewExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(994);
				match(New);
				setState(995);
				singleExpression(0);
				setState(997);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,87,_ctx) ) {
				case 1:
					{
					setState(996);
					arguments();
					}
					break;
				}
				}
				break;
			case 6:
				{
				_localctx = new DeleteExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(999);
				match(Delete);
				setState(1000);
				singleExpression(35);
				}
				break;
			case 7:
				{
				_localctx = new VoidExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1001);
				match(Void);
				setState(1002);
				singleExpression(34);
				}
				break;
			case 8:
				{
				_localctx = new TypeofExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1003);
				match(Typeof);
				setState(1004);
				singleExpression(33);
				}
				break;
			case 9:
				{
				_localctx = new PreIncrementExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1005);
				match(PlusPlus);
				setState(1006);
				singleExpression(32);
				}
				break;
			case 10:
				{
				_localctx = new PreDecreaseExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1007);
				match(MinusMinus);
				setState(1008);
				singleExpression(31);
				}
				break;
			case 11:
				{
				_localctx = new UnaryPlusExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1009);
				match(Plus);
				setState(1010);
				singleExpression(30);
				}
				break;
			case 12:
				{
				_localctx = new UnaryMinusExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1011);
				match(Minus);
				setState(1012);
				singleExpression(29);
				}
				break;
			case 13:
				{
				_localctx = new BitNotExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1013);
				match(BitNot);
				setState(1014);
				singleExpression(28);
				}
				break;
			case 14:
				{
				_localctx = new NotExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1015);
				match(Not);
				setState(1016);
				singleExpression(27);
				}
				break;
			case 15:
				{
				_localctx = new YieldExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1017);
				match(Yield);
				setState(1023);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,89,_ctx) ) {
				case 1:
					{
					setState(1018);
					if (!(!here(LineTerminator))) throw new FailedPredicateException(this, "!here(LineTerminator)");
					setState(1020);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==Multiply) {
						{
						setState(1019);
						match(Multiply);
						}
					}

					setState(1022);
					singleExpression(0);
					}
					break;
				}
				}
				break;
			case 16:
				{
				_localctx = new ArrowFunctionExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1025);
				arrowFunction();
				}
				break;
			case 17:
				{
				_localctx = new ThisExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1026);
				match(This);
				}
				break;
			case 18:
				{
				_localctx = new SuperExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1027);
				match(Super);
				}
				break;
			case 19:
				{
				_localctx = new IdentifierExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1028);
				identifier();
				}
				break;
			case 20:
				{
				_localctx = new LiteralExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1029);
				literal();
				}
				break;
			case 21:
				{
				_localctx = new ArrayLiteralExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1030);
				arrayLiteral();
				}
				break;
			case 22:
				{
				_localctx = new ObjectLiteralExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1031);
				objectLiteral();
				}
				break;
			case 23:
				{
				_localctx = new RegularExpressionLiteralExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1032);
				match(RegularExpressionLiteral);
				}
				break;
			case 24:
				{
				_localctx = new TemplateLiteralExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1033);
				templateLiteral();
				}
				break;
			case 25:
				{
				_localctx = new ParenthesizedExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1034);
				match(OpenParen);
				setState(1035);
				expressionSequence();
				setState(1036);
				match(CloseParen);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(1109);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,92,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(1107);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,91,_ctx) ) {
					case 1:
						{
						_localctx = new MultiplicativeExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1040);
						if (!(precpred(_ctx, 26))) throw new FailedPredicateException(this, "precpred(_ctx, 26)");
						setState(1041);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Multiply) | (1L << Divide) | (1L << Modulus))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1042);
						singleExpression(27);
						}
						break;
					case 2:
						{
						_localctx = new AdditiveExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1043);
						if (!(precpred(_ctx, 25))) throw new FailedPredicateException(this, "precpred(_ctx, 25)");
						setState(1044);
						_la = _input.LA(1);
						if ( !(_la==Plus || _la==Minus) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1045);
						singleExpression(26);
						}
						break;
					case 3:
						{
						_localctx = new BitShiftExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1046);
						if (!(precpred(_ctx, 24))) throw new FailedPredicateException(this, "precpred(_ctx, 24)");
						setState(1047);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RightShiftArithmetic) | (1L << LeftShiftArithmetic) | (1L << RightShiftLogical))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1048);
						singleExpression(25);
						}
						break;
					case 4:
						{
						_localctx = new RelationalExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1049);
						if (!(precpred(_ctx, 23))) throw new FailedPredicateException(this, "precpred(_ctx, 23)");
						setState(1050);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LessThan) | (1L << MoreThan) | (1L << LessThanEquals) | (1L << GreaterThanEquals))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1051);
						singleExpression(24);
						}
						break;
					case 5:
						{
						_localctx = new InstanceofExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1052);
						if (!(precpred(_ctx, 22))) throw new FailedPredicateException(this, "precpred(_ctx, 22)");
						setState(1053);
						match(Instanceof);
						setState(1054);
						singleExpression(23);
						}
						break;
					case 6:
						{
						_localctx = new InExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1055);
						if (!(precpred(_ctx, 21))) throw new FailedPredicateException(this, "precpred(_ctx, 21)");
						setState(1056);
						match(In);
						setState(1057);
						singleExpression(22);
						}
						break;
					case 7:
						{
						_localctx = new EqualityExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1058);
						if (!(precpred(_ctx, 20))) throw new FailedPredicateException(this, "precpred(_ctx, 20)");
						setState(1059);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Equals) | (1L << NotEquals) | (1L << IdentityEquals) | (1L << IdentityNotEquals))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1060);
						singleExpression(21);
						}
						break;
					case 8:
						{
						_localctx = new BitAndExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1061);
						if (!(precpred(_ctx, 19))) throw new FailedPredicateException(this, "precpred(_ctx, 19)");
						setState(1062);
						match(BitAnd);
						setState(1063);
						singleExpression(20);
						}
						break;
					case 9:
						{
						_localctx = new BitXOrExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1064);
						if (!(precpred(_ctx, 18))) throw new FailedPredicateException(this, "precpred(_ctx, 18)");
						setState(1065);
						match(BitXOr);
						setState(1066);
						singleExpression(19);
						}
						break;
					case 10:
						{
						_localctx = new BitOrExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1067);
						if (!(precpred(_ctx, 17))) throw new FailedPredicateException(this, "precpred(_ctx, 17)");
						setState(1068);
						match(BitOr);
						setState(1069);
						singleExpression(18);
						}
						break;
					case 11:
						{
						_localctx = new LogicalAndExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1070);
						if (!(precpred(_ctx, 16))) throw new FailedPredicateException(this, "precpred(_ctx, 16)");
						setState(1071);
						match(And);
						setState(1072);
						singleExpression(17);
						}
						break;
					case 12:
						{
						_localctx = new LogicalOrExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1073);
						if (!(precpred(_ctx, 15))) throw new FailedPredicateException(this, "precpred(_ctx, 15)");
						setState(1074);
						match(Or);
						setState(1075);
						singleExpression(16);
						}
						break;
					case 13:
						{
						_localctx = new TernaryExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1076);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(1077);
						match(QuestionMark);
						setState(1078);
						singleExpression(0);
						setState(1079);
						match(Colon);
						setState(1080);
						singleExpression(15);
						}
						break;
					case 14:
						{
						_localctx = new AssignmentExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1082);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(1083);
						match(Assign);
						setState(1084);
						singleExpression(12);
						}
						break;
					case 15:
						{
						_localctx = new AssignmentOperatorExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1085);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(1086);
						assignmentOperator();
						setState(1087);
						singleExpression(11);
						}
						break;
					case 16:
						{
						_localctx = new MemberIndexExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1089);
						if (!(precpred(_ctx, 41))) throw new FailedPredicateException(this, "precpred(_ctx, 41)");
						setState(1090);
						match(OpenBracket);
						setState(1091);
						expressionSequence();
						setState(1092);
						match(CloseBracket);
						}
						break;
					case 17:
						{
						_localctx = new MemberDotExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1094);
						if (!(precpred(_ctx, 40))) throw new FailedPredicateException(this, "precpred(_ctx, 40)");
						setState(1095);
						match(Dot);
						setState(1096);
						identifierName();
						}
						break;
					case 18:
						{
						_localctx = new CallTemplateLiteralExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1097);
						if (!(precpred(_ctx, 39))) throw new FailedPredicateException(this, "precpred(_ctx, 39)");
						setState(1098);
						templateLiteral();
						}
						break;
					case 19:
						{
						_localctx = new ArgumentsExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1099);
						if (!(precpred(_ctx, 38))) throw new FailedPredicateException(this, "precpred(_ctx, 38)");
						setState(1100);
						arguments();
						}
						break;
					case 20:
						{
						_localctx = new PostIncrementExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1101);
						if (!(precpred(_ctx, 37))) throw new FailedPredicateException(this, "precpred(_ctx, 37)");
						setState(1102);
						if (!(!here(LineTerminator))) throw new FailedPredicateException(this, "!here(LineTerminator)");
						setState(1103);
						match(PlusPlus);
						}
						break;
					case 21:
						{
						_localctx = new PostDecreaseExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1104);
						if (!(precpred(_ctx, 36))) throw new FailedPredicateException(this, "precpred(_ctx, 36)");
						setState(1105);
						if (!(!here(LineTerminator))) throw new FailedPredicateException(this, "!here(LineTerminator)");
						setState(1106);
						match(MinusMinus);
						}
						break;
					}
					} 
				}
				setState(1111);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,92,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ScriptContext extends ParserRuleContext {
		public ScriptBodyContext scriptBody() {
			return getRuleContext(ScriptBodyContext.class,0);
		}
		public ScriptContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_script; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterScript(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitScript(this);
		}
	}

	public final ScriptContext script() throws RecognitionException {
		ScriptContext _localctx = new ScriptContext(_ctx, getState());
		enterRule(_localctx, 188, RULE_script);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1113);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,93,_ctx) ) {
			case 1:
				{
				setState(1112);
				scriptBody();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ScriptBodyContext extends ParserRuleContext {
		public StatementListContext statementList() {
			return getRuleContext(StatementListContext.class,0);
		}
		public ScriptBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_scriptBody; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterScriptBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitScriptBody(this);
		}
	}

	public final ScriptBodyContext scriptBody() throws RecognitionException {
		ScriptBodyContext _localctx = new ScriptBodyContext(_ctx, getState());
		enterRule(_localctx, 190, RULE_scriptBody);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1115);
			statementList();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModuleContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(JSParser.EOF, 0); }
		public List<ModuleItemContext> moduleItem() {
			return getRuleContexts(ModuleItemContext.class);
		}
		public ModuleItemContext moduleItem(int i) {
			return getRuleContext(ModuleItemContext.class,i);
		}
		public ModuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_module; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterModule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitModule(this);
		}
	}

	public final ModuleContext module() throws RecognitionException {
		ModuleContext _localctx = new ModuleContext(_ctx, getState());
		enterRule(_localctx, 192, RULE_module);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1120);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,94,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1117);
					moduleItem();
					}
					} 
				}
				setState(1122);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,94,_ctx);
			}
			setState(1123);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModuleItemContext extends ParserRuleContext {
		public ImportDeclarationContext importDeclaration() {
			return getRuleContext(ImportDeclarationContext.class,0);
		}
		public ExportDeclarationContext exportDeclaration() {
			return getRuleContext(ExportDeclarationContext.class,0);
		}
		public StatementListItemContext statementListItem() {
			return getRuleContext(StatementListItemContext.class,0);
		}
		public ModuleItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_moduleItem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterModuleItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitModuleItem(this);
		}
	}

	public final ModuleItemContext moduleItem() throws RecognitionException {
		ModuleItemContext _localctx = new ModuleItemContext(_ctx, getState());
		enterRule(_localctx, 194, RULE_moduleItem);
		try {
			setState(1128);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,95,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1125);
				importDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1126);
				exportDeclaration();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1127);
				statementListItem();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ImportDeclarationContext extends ParserRuleContext {
		public TerminalNode Import() { return getToken(JSParser.Import, 0); }
		public ImportClauseContext importClause() {
			return getRuleContext(ImportClauseContext.class,0);
		}
		public FromClauseContext fromClause() {
			return getRuleContext(FromClauseContext.class,0);
		}
		public EosContext eos() {
			return getRuleContext(EosContext.class,0);
		}
		public ModuleSpecifierContext moduleSpecifier() {
			return getRuleContext(ModuleSpecifierContext.class,0);
		}
		public ImportDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_importDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterImportDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitImportDeclaration(this);
		}
	}

	public final ImportDeclarationContext importDeclaration() throws RecognitionException {
		ImportDeclarationContext _localctx = new ImportDeclarationContext(_ctx, getState());
		enterRule(_localctx, 196, RULE_importDeclaration);
		try {
			setState(1139);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,96,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1130);
				match(Import);
				setState(1131);
				importClause();
				setState(1132);
				fromClause();
				setState(1133);
				eos();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1135);
				match(Import);
				setState(1136);
				moduleSpecifier();
				setState(1137);
				eos();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ImportClauseContext extends ParserRuleContext {
		public ImportedDefaultBindingContext importedDefaultBinding() {
			return getRuleContext(ImportedDefaultBindingContext.class,0);
		}
		public NameSpaceImportContext nameSpaceImport() {
			return getRuleContext(NameSpaceImportContext.class,0);
		}
		public NamedImportsContext namedImports() {
			return getRuleContext(NamedImportsContext.class,0);
		}
		public ImportClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_importClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterImportClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitImportClause(this);
		}
	}

	public final ImportClauseContext importClause() throws RecognitionException {
		ImportClauseContext _localctx = new ImportClauseContext(_ctx, getState());
		enterRule(_localctx, 198, RULE_importClause);
		try {
			setState(1152);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,97,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1141);
				importedDefaultBinding();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1142);
				nameSpaceImport();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1143);
				namedImports();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1144);
				importedDefaultBinding();
				setState(1145);
				match(Comma);
				setState(1146);
				nameSpaceImport();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1148);
				importedDefaultBinding();
				setState(1149);
				match(Comma);
				setState(1150);
				namedImports();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ImportedDefaultBindingContext extends ParserRuleContext {
		public ImportedBindingContext importedBinding() {
			return getRuleContext(ImportedBindingContext.class,0);
		}
		public ImportedDefaultBindingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_importedDefaultBinding; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterImportedDefaultBinding(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitImportedDefaultBinding(this);
		}
	}

	public final ImportedDefaultBindingContext importedDefaultBinding() throws RecognitionException {
		ImportedDefaultBindingContext _localctx = new ImportedDefaultBindingContext(_ctx, getState());
		enterRule(_localctx, 200, RULE_importedDefaultBinding);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1154);
			importedBinding();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NameSpaceImportContext extends ParserRuleContext {
		public ImportedBindingContext importedBinding() {
			return getRuleContext(ImportedBindingContext.class,0);
		}
		public NameSpaceImportContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nameSpaceImport; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterNameSpaceImport(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitNameSpaceImport(this);
		}
	}

	public final NameSpaceImportContext nameSpaceImport() throws RecognitionException {
		NameSpaceImportContext _localctx = new NameSpaceImportContext(_ctx, getState());
		enterRule(_localctx, 202, RULE_nameSpaceImport);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1156);
			match(Multiply);
			setState(1157);
			match(As);
			setState(1158);
			importedBinding();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NamedImportsContext extends ParserRuleContext {
		public ImportsListContext importsList() {
			return getRuleContext(ImportsListContext.class,0);
		}
		public NamedImportsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_namedImports; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterNamedImports(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitNamedImports(this);
		}
	}

	public final NamedImportsContext namedImports() throws RecognitionException {
		NamedImportsContext _localctx = new NamedImportsContext(_ctx, getState());
		enterRule(_localctx, 204, RULE_namedImports);
		try {
			setState(1171);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,98,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1160);
				match(OpenBrace);
				setState(1161);
				match(CloseBrace);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1162);
				match(OpenBrace);
				setState(1163);
				importsList();
				setState(1164);
				match(CloseBrace);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1166);
				match(OpenBrace);
				setState(1167);
				importsList();
				setState(1168);
				match(Comma);
				setState(1169);
				match(CloseBrace);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FromClauseContext extends ParserRuleContext {
		public ModuleSpecifierContext moduleSpecifier() {
			return getRuleContext(ModuleSpecifierContext.class,0);
		}
		public FromClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fromClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterFromClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitFromClause(this);
		}
	}

	public final FromClauseContext fromClause() throws RecognitionException {
		FromClauseContext _localctx = new FromClauseContext(_ctx, getState());
		enterRule(_localctx, 206, RULE_fromClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1173);
			match(From);
			setState(1174);
			moduleSpecifier();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ImportsListContext extends ParserRuleContext {
		public List<ImportSpecifierContext> importSpecifier() {
			return getRuleContexts(ImportSpecifierContext.class);
		}
		public ImportSpecifierContext importSpecifier(int i) {
			return getRuleContext(ImportSpecifierContext.class,i);
		}
		public ImportsListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_importsList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterImportsList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitImportsList(this);
		}
	}

	public final ImportsListContext importsList() throws RecognitionException {
		ImportsListContext _localctx = new ImportsListContext(_ctx, getState());
		enterRule(_localctx, 208, RULE_importsList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1176);
			importSpecifier();
			setState(1181);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,99,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1177);
					match(Comma);
					setState(1178);
					importSpecifier();
					}
					} 
				}
				setState(1183);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,99,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ImportSpecifierContext extends ParserRuleContext {
		public ImportedBindingContext importedBinding() {
			return getRuleContext(ImportedBindingContext.class,0);
		}
		public IdentifierNameContext identifierName() {
			return getRuleContext(IdentifierNameContext.class,0);
		}
		public ImportSpecifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_importSpecifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterImportSpecifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitImportSpecifier(this);
		}
	}

	public final ImportSpecifierContext importSpecifier() throws RecognitionException {
		ImportSpecifierContext _localctx = new ImportSpecifierContext(_ctx, getState());
		enterRule(_localctx, 210, RULE_importSpecifier);
		try {
			setState(1189);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,100,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1184);
				importedBinding();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1185);
				identifierName();
				setState(1186);
				match(As);
				setState(1187);
				importedBinding();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModuleSpecifierContext extends ParserRuleContext {
		public TerminalNode StringLiteral() { return getToken(JSParser.StringLiteral, 0); }
		public ModuleSpecifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_moduleSpecifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterModuleSpecifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitModuleSpecifier(this);
		}
	}

	public final ModuleSpecifierContext moduleSpecifier() throws RecognitionException {
		ModuleSpecifierContext _localctx = new ModuleSpecifierContext(_ctx, getState());
		enterRule(_localctx, 212, RULE_moduleSpecifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1191);
			match(StringLiteral);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ImportedBindingContext extends ParserRuleContext {
		public BindingIdentifierContext bindingIdentifier() {
			return getRuleContext(BindingIdentifierContext.class,0);
		}
		public ImportedBindingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_importedBinding; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterImportedBinding(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitImportedBinding(this);
		}
	}

	public final ImportedBindingContext importedBinding() throws RecognitionException {
		ImportedBindingContext _localctx = new ImportedBindingContext(_ctx, getState());
		enterRule(_localctx, 214, RULE_importedBinding);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1193);
			bindingIdentifier();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExportDeclarationContext extends ParserRuleContext {
		public TerminalNode Export() { return getToken(JSParser.Export, 0); }
		public FromClauseContext fromClause() {
			return getRuleContext(FromClauseContext.class,0);
		}
		public EosContext eos() {
			return getRuleContext(EosContext.class,0);
		}
		public ExportClauseContext exportClause() {
			return getRuleContext(ExportClauseContext.class,0);
		}
		public VariableStatementContext variableStatement() {
			return getRuleContext(VariableStatementContext.class,0);
		}
		public DeclarationContext declaration() {
			return getRuleContext(DeclarationContext.class,0);
		}
		public TerminalNode Default() { return getToken(JSParser.Default, 0); }
		public HoistableDeclarationContext hoistableDeclaration() {
			return getRuleContext(HoistableDeclarationContext.class,0);
		}
		public ClassDeclarationContext classDeclaration() {
			return getRuleContext(ClassDeclarationContext.class,0);
		}
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public ExportDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exportDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterExportDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitExportDeclaration(this);
		}
	}

	public final ExportDeclarationContext exportDeclaration() throws RecognitionException {
		ExportDeclarationContext _localctx = new ExportDeclarationContext(_ctx, getState());
		enterRule(_localctx, 216, RULE_exportDeclaration);
		try {
			setState(1225);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,101,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1195);
				match(Export);
				setState(1196);
				match(Multiply);
				setState(1197);
				fromClause();
				setState(1198);
				eos();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1200);
				match(Export);
				setState(1201);
				exportClause();
				setState(1202);
				fromClause();
				setState(1203);
				eos();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1205);
				match(Export);
				setState(1206);
				exportClause();
				setState(1207);
				eos();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1209);
				match(Export);
				setState(1210);
				variableStatement();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1211);
				match(Export);
				setState(1212);
				declaration();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1213);
				match(Export);
				setState(1214);
				match(Default);
				setState(1215);
				hoistableDeclaration();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1216);
				match(Export);
				setState(1217);
				match(Default);
				setState(1218);
				classDeclaration();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(1219);
				match(Export);
				setState(1220);
				match(Default);
				setState(1221);
				if (!((_input.LA(1) != Function) && (_input.LA(1) != Class))) throw new FailedPredicateException(this, "(_input.LA(1) != Function) && (_input.LA(1) != Class)");
				setState(1222);
				singleExpression(0);
				setState(1223);
				eos();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExportClauseContext extends ParserRuleContext {
		public ExportsListContext exportsList() {
			return getRuleContext(ExportsListContext.class,0);
		}
		public ExportClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exportClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterExportClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitExportClause(this);
		}
	}

	public final ExportClauseContext exportClause() throws RecognitionException {
		ExportClauseContext _localctx = new ExportClauseContext(_ctx, getState());
		enterRule(_localctx, 218, RULE_exportClause);
		try {
			setState(1238);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,102,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1227);
				match(OpenBrace);
				setState(1228);
				match(CloseBrace);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1229);
				match(OpenBrace);
				setState(1230);
				exportsList();
				setState(1231);
				match(CloseBrace);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1233);
				match(OpenBrace);
				setState(1234);
				exportsList();
				setState(1235);
				match(Comma);
				setState(1236);
				match(CloseBrace);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExportsListContext extends ParserRuleContext {
		public List<ExportSpecifierContext> exportSpecifier() {
			return getRuleContexts(ExportSpecifierContext.class);
		}
		public ExportSpecifierContext exportSpecifier(int i) {
			return getRuleContext(ExportSpecifierContext.class,i);
		}
		public ExportsListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exportsList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterExportsList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitExportsList(this);
		}
	}

	public final ExportsListContext exportsList() throws RecognitionException {
		ExportsListContext _localctx = new ExportsListContext(_ctx, getState());
		enterRule(_localctx, 220, RULE_exportsList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1240);
			exportSpecifier();
			setState(1245);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,103,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1241);
					match(Comma);
					setState(1242);
					exportSpecifier();
					}
					} 
				}
				setState(1247);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,103,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExportSpecifierContext extends ParserRuleContext {
		public List<IdentifierNameContext> identifierName() {
			return getRuleContexts(IdentifierNameContext.class);
		}
		public IdentifierNameContext identifierName(int i) {
			return getRuleContext(IdentifierNameContext.class,i);
		}
		public ExportSpecifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exportSpecifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterExportSpecifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitExportSpecifier(this);
		}
	}

	public final ExportSpecifierContext exportSpecifier() throws RecognitionException {
		ExportSpecifierContext _localctx = new ExportSpecifierContext(_ctx, getState());
		enterRule(_localctx, 222, RULE_exportSpecifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1248);
			identifierName();
			setState(1251);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==As) {
				{
				setState(1249);
				match(As);
				setState(1250);
				identifierName();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AssignmentOperatorContext extends ParserRuleContext {
		public AssignmentOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignmentOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterAssignmentOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitAssignmentOperator(this);
		}
	}

	public final AssignmentOperatorContext assignmentOperator() throws RecognitionException {
		AssignmentOperatorContext _localctx = new AssignmentOperatorContext(_ctx, getState());
		enterRule(_localctx, 224, RULE_assignmentOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1253);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MultiplyAssign) | (1L << DivideAssign) | (1L << ModulusAssign) | (1L << PlusAssign) | (1L << MinusAssign) | (1L << LeftShiftArithmeticAssign) | (1L << RightShiftArithmeticAssign) | (1L << RightShiftLogicalAssign) | (1L << BitAndAssign) | (1L << BitXorAssign) | (1L << BitOrAssign))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LiteralContext extends ParserRuleContext {
		public TerminalNode NullLiteral() { return getToken(JSParser.NullLiteral, 0); }
		public TerminalNode BooleanLiteral() { return getToken(JSParser.BooleanLiteral, 0); }
		public TerminalNode StringLiteral() { return getToken(JSParser.StringLiteral, 0); }
		public NumericLiteralContext numericLiteral() {
			return getRuleContext(NumericLiteralContext.class,0);
		}
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitLiteral(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 226, RULE_literal);
		int _la;
		try {
			setState(1257);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NullLiteral:
			case BooleanLiteral:
			case StringLiteral:
				enterOuterAlt(_localctx, 1);
				{
				setState(1255);
				_la = _input.LA(1);
				if ( !(((((_la - 53)) & ~0x3f) == 0 && ((1L << (_la - 53)) & ((1L << (NullLiteral - 53)) | (1L << (BooleanLiteral - 53)) | (1L << (StringLiteral - 53)))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case DecimalLiteral:
			case HexIntegerLiteral:
			case OctalIntegerLiteral:
			case BinaryIntegerLiteral:
				enterOuterAlt(_localctx, 2);
				{
				setState(1256);
				numericLiteral();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NumericLiteralContext extends ParserRuleContext {
		public TerminalNode DecimalLiteral() { return getToken(JSParser.DecimalLiteral, 0); }
		public TerminalNode HexIntegerLiteral() { return getToken(JSParser.HexIntegerLiteral, 0); }
		public TerminalNode OctalIntegerLiteral() { return getToken(JSParser.OctalIntegerLiteral, 0); }
		public TerminalNode BinaryIntegerLiteral() { return getToken(JSParser.BinaryIntegerLiteral, 0); }
		public NumericLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_numericLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterNumericLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitNumericLiteral(this);
		}
	}

	public final NumericLiteralContext numericLiteral() throws RecognitionException {
		NumericLiteralContext _localctx = new NumericLiteralContext(_ctx, getState());
		enterRule(_localctx, 228, RULE_numericLiteral);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1259);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << DecimalLiteral) | (1L << HexIntegerLiteral) | (1L << OctalIntegerLiteral) | (1L << BinaryIntegerLiteral))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IdentifierContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(JSParser.Identifier, 0); }
		public TerminalNode As() { return getToken(JSParser.As, 0); }
		public TerminalNode From() { return getToken(JSParser.From, 0); }
		public TerminalNode Get() { return getToken(JSParser.Get, 0); }
		public TerminalNode Set() { return getToken(JSParser.Set, 0); }
		public TerminalNode Of() { return getToken(JSParser.Of, 0); }
		public TerminalNode Static() { return getToken(JSParser.Static, 0); }
		public TerminalNode Let() { return getToken(JSParser.Let, 0); }
		public TerminalNode Target() { return getToken(JSParser.Target, 0); }
		public IdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitIdentifier(this);
		}
	}

	public final IdentifierContext identifier() throws RecognitionException {
		IdentifierContext _localctx = new IdentifierContext(_ctx, getState());
		enterRule(_localctx, 230, RULE_identifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1261);
			_la = _input.LA(1);
			if ( !(((((_la - 100)) & ~0x3f) == 0 && ((1L << (_la - 100)) & ((1L << (Static - 100)) | (1L << (Let - 100)) | (1L << (From - 100)) | (1L << (Get - 100)) | (1L << (Set - 100)) | (1L << (As - 100)) | (1L << (Of - 100)) | (1L << (Target - 100)) | (1L << (Identifier - 100)))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IdentifierNameContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public ReservedWordContext reservedWord() {
			return getRuleContext(ReservedWordContext.class,0);
		}
		public IdentifierNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifierName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterIdentifierName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitIdentifierName(this);
		}
	}

	public final IdentifierNameContext identifierName() throws RecognitionException {
		IdentifierNameContext _localctx = new IdentifierNameContext(_ctx, getState());
		enterRule(_localctx, 232, RULE_identifierName);
		try {
			setState(1265);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Static:
			case Let:
			case From:
			case Get:
			case Set:
			case As:
			case Of:
			case Target:
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(1263);
				identifier();
				}
				break;
			case NullLiteral:
			case BooleanLiteral:
			case Break:
			case Do:
			case Instanceof:
			case Typeof:
			case Case:
			case Else:
			case New:
			case Var:
			case Catch:
			case Finally:
			case Return:
			case Void:
			case Continue:
			case For:
			case Switch:
			case While:
			case Debugger:
			case Function:
			case This:
			case With:
			case Default:
			case If:
			case Throw:
			case Delete:
			case In:
			case Try:
			case Export:
			case Class:
			case Extends:
			case Const:
			case Super:
			case Yield:
			case Import:
			case Enum:
			case Await:
			case Implements:
			case Private:
			case Public:
			case Interface:
			case Package:
			case Protected:
				enterOuterAlt(_localctx, 2);
				{
				setState(1264);
				reservedWord();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IdentifierReferenceContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public IdentifierReferenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifierReference; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterIdentifierReference(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitIdentifierReference(this);
		}
	}

	public final IdentifierReferenceContext identifierReference() throws RecognitionException {
		IdentifierReferenceContext _localctx = new IdentifierReferenceContext(_ctx, getState());
		enterRule(_localctx, 234, RULE_identifierReference);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1267);
			identifier();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BindingIdentifierContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public BindingIdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bindingIdentifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterBindingIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitBindingIdentifier(this);
		}
	}

	public final BindingIdentifierContext bindingIdentifier() throws RecognitionException {
		BindingIdentifierContext _localctx = new BindingIdentifierContext(_ctx, getState());
		enterRule(_localctx, 236, RULE_bindingIdentifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1269);
			identifier();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LabelIdentifierContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public LabelIdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_labelIdentifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterLabelIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitLabelIdentifier(this);
		}
	}

	public final LabelIdentifierContext labelIdentifier() throws RecognitionException {
		LabelIdentifierContext _localctx = new LabelIdentifierContext(_ctx, getState());
		enterRule(_localctx, 238, RULE_labelIdentifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1271);
			identifier();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ReservedWordContext extends ParserRuleContext {
		public KeywordContext keyword() {
			return getRuleContext(KeywordContext.class,0);
		}
		public FutureReservedWordContext futureReservedWord() {
			return getRuleContext(FutureReservedWordContext.class,0);
		}
		public TerminalNode NullLiteral() { return getToken(JSParser.NullLiteral, 0); }
		public TerminalNode BooleanLiteral() { return getToken(JSParser.BooleanLiteral, 0); }
		public ReservedWordContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_reservedWord; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterReservedWord(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitReservedWord(this);
		}
	}

	public final ReservedWordContext reservedWord() throws RecognitionException {
		ReservedWordContext _localctx = new ReservedWordContext(_ctx, getState());
		enterRule(_localctx, 240, RULE_reservedWord);
		int _la;
		try {
			setState(1276);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Break:
			case Do:
			case Instanceof:
			case Typeof:
			case Case:
			case Else:
			case New:
			case Var:
			case Catch:
			case Finally:
			case Return:
			case Void:
			case Continue:
			case For:
			case Switch:
			case While:
			case Debugger:
			case Function:
			case This:
			case With:
			case Default:
			case If:
			case Throw:
			case Delete:
			case In:
			case Try:
			case Export:
			case Class:
			case Extends:
			case Const:
			case Super:
			case Yield:
			case Import:
				enterOuterAlt(_localctx, 1);
				{
				setState(1273);
				keyword();
				}
				break;
			case Enum:
			case Await:
			case Implements:
			case Private:
			case Public:
			case Interface:
			case Package:
			case Protected:
				enterOuterAlt(_localctx, 2);
				{
				setState(1274);
				futureReservedWord();
				}
				break;
			case NullLiteral:
			case BooleanLiteral:
				enterOuterAlt(_localctx, 3);
				{
				setState(1275);
				_la = _input.LA(1);
				if ( !(_la==NullLiteral || _la==BooleanLiteral) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class KeywordContext extends ParserRuleContext {
		public TerminalNode Break() { return getToken(JSParser.Break, 0); }
		public TerminalNode Do() { return getToken(JSParser.Do, 0); }
		public TerminalNode Instanceof() { return getToken(JSParser.Instanceof, 0); }
		public TerminalNode Typeof() { return getToken(JSParser.Typeof, 0); }
		public TerminalNode Case() { return getToken(JSParser.Case, 0); }
		public TerminalNode Else() { return getToken(JSParser.Else, 0); }
		public TerminalNode New() { return getToken(JSParser.New, 0); }
		public TerminalNode Var() { return getToken(JSParser.Var, 0); }
		public TerminalNode Catch() { return getToken(JSParser.Catch, 0); }
		public TerminalNode Export() { return getToken(JSParser.Export, 0); }
		public TerminalNode Const() { return getToken(JSParser.Const, 0); }
		public TerminalNode Finally() { return getToken(JSParser.Finally, 0); }
		public TerminalNode Super() { return getToken(JSParser.Super, 0); }
		public TerminalNode Return() { return getToken(JSParser.Return, 0); }
		public TerminalNode Void() { return getToken(JSParser.Void, 0); }
		public TerminalNode Class() { return getToken(JSParser.Class, 0); }
		public TerminalNode Extends() { return getToken(JSParser.Extends, 0); }
		public TerminalNode Continue() { return getToken(JSParser.Continue, 0); }
		public TerminalNode For() { return getToken(JSParser.For, 0); }
		public TerminalNode Switch() { return getToken(JSParser.Switch, 0); }
		public TerminalNode Yield() { return getToken(JSParser.Yield, 0); }
		public TerminalNode While() { return getToken(JSParser.While, 0); }
		public TerminalNode Debugger() { return getToken(JSParser.Debugger, 0); }
		public TerminalNode Function() { return getToken(JSParser.Function, 0); }
		public TerminalNode This() { return getToken(JSParser.This, 0); }
		public TerminalNode With() { return getToken(JSParser.With, 0); }
		public TerminalNode Default() { return getToken(JSParser.Default, 0); }
		public TerminalNode If() { return getToken(JSParser.If, 0); }
		public TerminalNode Throw() { return getToken(JSParser.Throw, 0); }
		public TerminalNode Delete() { return getToken(JSParser.Delete, 0); }
		public TerminalNode In() { return getToken(JSParser.In, 0); }
		public TerminalNode Import() { return getToken(JSParser.Import, 0); }
		public TerminalNode Try() { return getToken(JSParser.Try, 0); }
		public KeywordContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_keyword; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterKeyword(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitKeyword(this);
		}
	}

	public final KeywordContext keyword() throws RecognitionException {
		KeywordContext _localctx = new KeywordContext(_ctx, getState());
		enterRule(_localctx, 242, RULE_keyword);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1278);
			_la = _input.LA(1);
			if ( !(((((_la - 59)) & ~0x3f) == 0 && ((1L << (_la - 59)) & ((1L << (Break - 59)) | (1L << (Do - 59)) | (1L << (Instanceof - 59)) | (1L << (Typeof - 59)) | (1L << (Case - 59)) | (1L << (Else - 59)) | (1L << (New - 59)) | (1L << (Var - 59)) | (1L << (Catch - 59)) | (1L << (Finally - 59)) | (1L << (Return - 59)) | (1L << (Void - 59)) | (1L << (Continue - 59)) | (1L << (For - 59)) | (1L << (Switch - 59)) | (1L << (While - 59)) | (1L << (Debugger - 59)) | (1L << (Function - 59)) | (1L << (This - 59)) | (1L << (With - 59)) | (1L << (Default - 59)) | (1L << (If - 59)) | (1L << (Throw - 59)) | (1L << (Delete - 59)) | (1L << (In - 59)) | (1L << (Try - 59)) | (1L << (Export - 59)) | (1L << (Class - 59)) | (1L << (Extends - 59)) | (1L << (Const - 59)) | (1L << (Super - 59)) | (1L << (Yield - 59)) | (1L << (Import - 59)))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FutureReservedWordContext extends ParserRuleContext {
		public TerminalNode Enum() { return getToken(JSParser.Enum, 0); }
		public TerminalNode Await() { return getToken(JSParser.Await, 0); }
		public TerminalNode Implements() { return getToken(JSParser.Implements, 0); }
		public TerminalNode Private() { return getToken(JSParser.Private, 0); }
		public TerminalNode Public() { return getToken(JSParser.Public, 0); }
		public TerminalNode Interface() { return getToken(JSParser.Interface, 0); }
		public TerminalNode Package() { return getToken(JSParser.Package, 0); }
		public TerminalNode Protected() { return getToken(JSParser.Protected, 0); }
		public FutureReservedWordContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_futureReservedWord; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterFutureReservedWord(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitFutureReservedWord(this);
		}
	}

	public final FutureReservedWordContext futureReservedWord() throws RecognitionException {
		FutureReservedWordContext _localctx = new FutureReservedWordContext(_ctx, getState());
		enterRule(_localctx, 244, RULE_futureReservedWord);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1280);
			_la = _input.LA(1);
			if ( !(((((_la - 92)) & ~0x3f) == 0 && ((1L << (_la - 92)) & ((1L << (Enum - 92)) | (1L << (Await - 92)) | (1L << (Implements - 92)) | (1L << (Private - 92)) | (1L << (Public - 92)) | (1L << (Interface - 92)) | (1L << (Package - 92)) | (1L << (Protected - 92)))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EosContext extends ParserRuleContext {
		public TerminalNode SemiColon() { return getToken(JSParser.SemiColon, 0); }
		public TerminalNode EOF() { return getToken(JSParser.EOF, 0); }
		public EosContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eos; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterEos(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitEos(this);
		}
	}

	public final EosContext eos() throws RecognitionException {
		EosContext _localctx = new EosContext(_ctx, getState());
		enterRule(_localctx, 246, RULE_eos);
		try {
			setState(1286);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,108,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1282);
				match(SemiColon);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1283);
				match(EOF);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1284);
				if (!(lineTerminatorAhead())) throw new FailedPredicateException(this, "lineTerminatorAhead()");
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1285);
				if (!(_input.LT(1).getType() == CloseBrace)) throw new FailedPredicateException(this, "_input.LT(1).getType() == CloseBrace");
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EofContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(JSParser.EOF, 0); }
		public EofContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eof; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterEof(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitEof(this);
		}
	}

	public final EofContext eof() throws RecognitionException {
		EofContext _localctx = new EofContext(_ctx, getState());
		enterRule(_localctx, 248, RULE_eof);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1288);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TemplateLiteralContext extends ParserRuleContext {
		public TerminalNode NoSubstitutionTemplate() { return getToken(JSParser.NoSubstitutionTemplate, 0); }
		public TerminalNode TemplateHead() { return getToken(JSParser.TemplateHead, 0); }
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public TemplateSpansContext templateSpans() {
			return getRuleContext(TemplateSpansContext.class,0);
		}
		public TemplateLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_templateLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterTemplateLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitTemplateLiteral(this);
		}
	}

	public final TemplateLiteralContext templateLiteral() throws RecognitionException {
		TemplateLiteralContext _localctx = new TemplateLiteralContext(_ctx, getState());
		enterRule(_localctx, 250, RULE_templateLiteral);
		try {
			setState(1295);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NoSubstitutionTemplate:
				enterOuterAlt(_localctx, 1);
				{
				setState(1290);
				match(NoSubstitutionTemplate);
				}
				break;
			case TemplateHead:
				enterOuterAlt(_localctx, 2);
				{
				setState(1291);
				match(TemplateHead);
				setState(1292);
				expressionSequence();
				setState(1293);
				templateSpans();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TemplateSpansContext extends ParserRuleContext {
		public TerminalNode TemplateTail() { return getToken(JSParser.TemplateTail, 0); }
		public TemplateMiddleListContext templateMiddleList() {
			return getRuleContext(TemplateMiddleListContext.class,0);
		}
		public TemplateSpansContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_templateSpans; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterTemplateSpans(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitTemplateSpans(this);
		}
	}

	public final TemplateSpansContext templateSpans() throws RecognitionException {
		TemplateSpansContext _localctx = new TemplateSpansContext(_ctx, getState());
		enterRule(_localctx, 252, RULE_templateSpans);
		try {
			setState(1301);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TemplateTail:
				enterOuterAlt(_localctx, 1);
				{
				setState(1297);
				match(TemplateTail);
				}
				break;
			case TemplateMiddle:
				enterOuterAlt(_localctx, 2);
				{
				setState(1298);
				templateMiddleList(0);
				setState(1299);
				match(TemplateTail);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TemplateMiddleListContext extends ParserRuleContext {
		public TerminalNode TemplateMiddle() { return getToken(JSParser.TemplateMiddle, 0); }
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public TemplateMiddleListContext templateMiddleList() {
			return getRuleContext(TemplateMiddleListContext.class,0);
		}
		public TemplateMiddleListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_templateMiddleList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterTemplateMiddleList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitTemplateMiddleList(this);
		}
	}

	public final TemplateMiddleListContext templateMiddleList() throws RecognitionException {
		return templateMiddleList(0);
	}

	private TemplateMiddleListContext templateMiddleList(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		TemplateMiddleListContext _localctx = new TemplateMiddleListContext(_ctx, _parentState);
		TemplateMiddleListContext _prevctx = _localctx;
		int _startState = 254;
		enterRecursionRule(_localctx, 254, RULE_templateMiddleList, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(1304);
			match(TemplateMiddle);
			setState(1305);
			expressionSequence();
			}
			_ctx.stop = _input.LT(-1);
			setState(1312);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,111,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new TemplateMiddleListContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_templateMiddleList);
					setState(1307);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(1308);
					match(TemplateMiddle);
					setState(1309);
					expressionSequence();
					}
					} 
				}
				setState(1314);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,111,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 28:
			return expressionStatement_sempred((ExpressionStatementContext)_localctx, predIndex);
		case 30:
			return iterationStatement_sempred((IterationStatementContext)_localctx, predIndex);
		case 35:
			return continueStatement_sempred((ContinueStatementContext)_localctx, predIndex);
		case 36:
			return breakStatement_sempred((BreakStatementContext)_localctx, predIndex);
		case 37:
			return returnStatement_sempred((ReturnStatementContext)_localctx, predIndex);
		case 46:
			return throwStatement_sempred((ThrowStatementContext)_localctx, predIndex);
		case 62:
			return arrowFunction_sempred((ArrowFunctionContext)_localctx, predIndex);
		case 64:
			return conciseBody_sempred((ConciseBodyContext)_localctx, predIndex);
		case 80:
			return elementList_sempred((ElementListContext)_localctx, predIndex);
		case 93:
			return singleExpression_sempred((SingleExpressionContext)_localctx, predIndex);
		case 108:
			return exportDeclaration_sempred((ExportDeclarationContext)_localctx, predIndex);
		case 123:
			return eos_sempred((EosContext)_localctx, predIndex);
		case 127:
			return templateMiddleList_sempred((TemplateMiddleListContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expressionStatement_sempred(ExpressionStatementContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return (_input.LA(1) != OpenBrace) && (_input.LA(1) != Function) && (_input.LA(1) != Class) && ((_input.LA(1) == Let) ? _input.LA(2) != OpenBracket : true);
		}
		return true;
	}
	private boolean iterationStatement_sempred(IterationStatementContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return ((_input.LA(1) == Let) ? _input.LA(2) != OpenBracket : true);
		case 2:
			return ((_input.LA(1) == Let) ? _input.LA(2) != OpenBracket : true);
		case 3:
			return (_input.LA(1) != Let);
		}
		return true;
	}
	private boolean continueStatement_sempred(ContinueStatementContext _localctx, int predIndex) {
		switch (predIndex) {
		case 4:
			return !here(LineTerminator);
		}
		return true;
	}
	private boolean breakStatement_sempred(BreakStatementContext _localctx, int predIndex) {
		switch (predIndex) {
		case 5:
			return !here(LineTerminator);
		}
		return true;
	}
	private boolean returnStatement_sempred(ReturnStatementContext _localctx, int predIndex) {
		switch (predIndex) {
		case 6:
			return !here(LineTerminator);
		}
		return true;
	}
	private boolean throwStatement_sempred(ThrowStatementContext _localctx, int predIndex) {
		switch (predIndex) {
		case 7:
			return !here(LineTerminator);
		}
		return true;
	}
	private boolean arrowFunction_sempred(ArrowFunctionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 8:
			return !here(LineTerminator);
		}
		return true;
	}
	private boolean conciseBody_sempred(ConciseBodyContext _localctx, int predIndex) {
		switch (predIndex) {
		case 9:
			return (_input.LA(1) != OpenBrace);
		}
		return true;
	}
	private boolean elementList_sempred(ElementListContext _localctx, int predIndex) {
		switch (predIndex) {
		case 10:
			return precpred(_ctx, 2);
		case 11:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean singleExpression_sempred(SingleExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 12:
			return !here(LineTerminator);
		case 13:
			return precpred(_ctx, 26);
		case 14:
			return precpred(_ctx, 25);
		case 15:
			return precpred(_ctx, 24);
		case 16:
			return precpred(_ctx, 23);
		case 17:
			return precpred(_ctx, 22);
		case 18:
			return precpred(_ctx, 21);
		case 19:
			return precpred(_ctx, 20);
		case 20:
			return precpred(_ctx, 19);
		case 21:
			return precpred(_ctx, 18);
		case 22:
			return precpred(_ctx, 17);
		case 23:
			return precpred(_ctx, 16);
		case 24:
			return precpred(_ctx, 15);
		case 25:
			return precpred(_ctx, 14);
		case 26:
			return precpred(_ctx, 11);
		case 27:
			return precpred(_ctx, 10);
		case 28:
			return precpred(_ctx, 41);
		case 29:
			return precpred(_ctx, 40);
		case 30:
			return precpred(_ctx, 39);
		case 31:
			return precpred(_ctx, 38);
		case 32:
			return precpred(_ctx, 37);
		case 33:
			return !here(LineTerminator);
		case 34:
			return precpred(_ctx, 36);
		case 35:
			return !here(LineTerminator);
		}
		return true;
	}
	private boolean exportDeclaration_sempred(ExportDeclarationContext _localctx, int predIndex) {
		switch (predIndex) {
		case 36:
			return (_input.LA(1) != Function) && (_input.LA(1) != Class);
		}
		return true;
	}
	private boolean eos_sempred(EosContext _localctx, int predIndex) {
		switch (predIndex) {
		case 37:
			return lineTerminatorAhead();
		case 38:
			return _input.LT(1).getType() == CloseBrace;
		}
		return true;
	}
	private boolean templateMiddleList_sempred(TemplateMiddleListContext _localctx, int predIndex) {
		switch (predIndex) {
		case 39:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3w\u0526\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\tT"+
		"\4U\tU\4V\tV\4W\tW\4X\tX\4Y\tY\4Z\tZ\4[\t[\4\\\t\\\4]\t]\4^\t^\4_\t_\4"+
		"`\t`\4a\ta\4b\tb\4c\tc\4d\td\4e\te\4f\tf\4g\tg\4h\th\4i\ti\4j\tj\4k\t"+
		"k\4l\tl\4m\tm\4n\tn\4o\to\4p\tp\4q\tq\4r\tr\4s\ts\4t\tt\4u\tu\4v\tv\4"+
		"w\tw\4x\tx\4y\ty\4z\tz\4{\t{\4|\t|\4}\t}\4~\t~\4\177\t\177\4\u0080\t\u0080"+
		"\4\u0081\t\u0081\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\5\3\u0113\n\3\3\4\3\4\3\4\5\4\u0118\n\4\3\5\3\5\5\5\u011c\n"+
		"\5\3\6\3\6\5\6\u0120\n\6\3\7\3\7\3\b\3\b\5\b\u0126\n\b\3\b\3\b\3\t\6\t"+
		"\u012b\n\t\r\t\16\t\u012c\3\n\3\n\5\n\u0131\n\n\3\13\3\13\3\13\3\13\3"+
		"\f\3\f\3\r\3\r\3\r\7\r\u013c\n\r\f\r\16\r\u013f\13\r\3\16\3\16\5\16\u0143"+
		"\n\16\3\16\3\16\3\16\5\16\u0148\n\16\3\17\3\17\3\17\3\20\3\20\3\20\7\20"+
		"\u0150\n\20\f\20\16\20\u0153\13\20\3\21\3\21\5\21\u0157\n\21\3\21\3\21"+
		"\3\21\5\21\u015c\n\21\3\22\3\22\5\22\u0160\n\22\3\23\3\23\3\23\3\23\3"+
		"\23\3\23\3\23\3\23\3\23\3\23\3\23\5\23\u016d\n\23\3\24\3\24\5\24\u0171"+
		"\n\24\3\24\5\24\u0174\n\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24"+
		"\5\24\u017f\n\24\3\24\5\24\u0182\n\24\3\24\3\24\5\24\u0186\n\24\3\25\3"+
		"\25\3\25\7\25\u018b\n\25\f\25\16\25\u018e\13\25\3\26\3\26\3\26\7\26\u0193"+
		"\n\26\f\26\16\26\u0196\13\26\3\27\5\27\u0199\n\27\3\27\3\27\3\30\3\30"+
		"\3\30\3\30\3\30\5\30\u01a2\n\30\3\31\3\31\3\31\5\31\u01a7\n\31\5\31\u01a9"+
		"\n\31\3\32\3\32\5\32\u01ad\n\32\3\33\3\33\3\33\3\34\3\34\3\34\3\35\3\35"+
		"\3\36\3\36\3\36\3\36\3\37\3\37\3\37\3\37\3\37\3\37\3\37\5\37\u01c2\n\37"+
		"\3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \5 \u01d6\n \3 "+
		"\3 \5 \u01da\n \3 \3 \5 \u01de\n \3 \3 \3 \3 \3 \3 \3 \5 \u01e7\n \3 "+
		"\3 \5 \u01eb\n \3 \3 \3 \3 \3 \3 \3 \5 \u01f4\n \3 \3 \5 \u01f8\n \3 "+
		"\3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 "+
		"\3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 "+
		"\3 \3 \3 \3 \3 \3 \5 \u022f\n \3!\3!\3!\3\"\3\"\3\"\3#\3#\3#\3$\3$\5$"+
		"\u023c\n$\3%\3%\3%\5%\u0241\n%\3%\3%\3&\3&\3&\5&\u0248\n&\3&\3&\3\'\3"+
		"\'\3\'\5\'\u024f\n\'\3\'\3\'\3(\3(\3(\3(\3(\3(\3)\3)\3)\3)\3)\3)\3*\3"+
		"*\5*\u0261\n*\3*\3*\5*\u0265\n*\5*\u0267\n*\3*\3*\3+\6+\u026c\n+\r+\16"+
		"+\u026d\3,\3,\3,\3,\5,\u0274\n,\3-\3-\3-\5-\u0279\n-\3.\3.\3.\3.\3/\3"+
		"/\5/\u0281\n/\3\60\3\60\3\60\3\60\3\60\3\61\3\61\3\61\3\61\3\61\3\61\3"+
		"\61\3\61\3\61\3\61\3\61\3\61\3\61\5\61\u0295\n\61\3\62\3\62\3\62\3\62"+
		"\3\62\3\62\3\63\3\63\3\63\3\64\3\64\5\64\u02a2\n\64\3\65\3\65\3\65\3\66"+
		"\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\67\3\67\3\67\3\67\3\67\3\67"+
		"\3\67\3\67\3\67\5\67\u02b9\n\67\38\38\39\39\59\u02bf\n9\3:\3:\3:\3:\5"+
		":\u02c5\n:\5:\u02c7\n:\3;\3;\3;\7;\u02cc\n;\f;\16;\u02cf\13;\3<\3<\3="+
		"\3=\3>\3>\3?\5?\u02d8\n?\3@\3@\3@\3@\3@\3A\3A\5A\u02e1\nA\3B\3B\3B\3B"+
		"\3B\3B\5B\u02e9\nB\3C\3C\3C\3C\3C\3C\3C\3C\3C\3C\3C\3C\3C\3C\3C\3C\5C"+
		"\u02fb\nC\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D"+
		"\3D\3D\3D\3D\3D\3D\5D\u0317\nD\3E\3E\3F\3F\3F\3F\3F\3F\3F\3F\3F\3G\3G"+
		"\3G\3G\3G\3G\3G\3G\3G\3G\3H\3H\3H\5H\u0331\nH\3H\3H\3H\3H\3H\3H\3H\3I"+
		"\3I\3J\3J\3J\3J\3K\3K\5K\u0342\nK\3K\3K\3L\5L\u0347\nL\3L\3L\5L\u034b"+
		"\nL\3L\3L\3M\3M\3M\3N\3N\3O\6O\u0355\nO\rO\16O\u0356\3P\3P\3P\3P\5P\u035d"+
		"\nP\3Q\3Q\5Q\u0361\nQ\3Q\3Q\3Q\3Q\3Q\3Q\3Q\3Q\3Q\5Q\u036c\nQ\3Q\3Q\5Q"+
		"\u0370\nQ\3R\3R\5R\u0374\nR\3R\3R\5R\u0378\nR\3R\5R\u037b\nR\3R\3R\3R"+
		"\5R\u0380\nR\3R\3R\3R\3R\5R\u0386\nR\3R\7R\u0389\nR\fR\16R\u038c\13R\3"+
		"S\6S\u038f\nS\rS\16S\u0390\3T\3T\3T\3U\3U\3U\3U\3U\5U\u039b\nU\3U\3U\5"+
		"U\u039f\nU\3V\3V\3V\7V\u03a4\nV\fV\16V\u03a7\13V\3W\3W\3W\3W\3W\3W\3W"+
		"\5W\u03b0\nW\3X\3X\5X\u03b4\nX\3Y\3Y\3Y\5Y\u03b9\nY\3Z\3Z\3Z\3Z\3[\3["+
		"\3[\3\\\3\\\5\\\u03c4\n\\\3\\\3\\\3]\3]\5]\u03ca\n]\3]\3]\3]\5]\u03cf"+
		"\n]\7]\u03d1\n]\f]\16]\u03d4\13]\3^\3^\3^\7^\u03d9\n^\f^\16^\u03dc\13"+
		"^\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\5_\u03e8\n_\3_\3_\3_\3_\3_\3_\3_\3_\3"+
		"_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\5_\u03ff\n_\3_\5_\u0402\n_\3_\3"+
		"_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\5_\u0411\n_\3_\3_\3_\3_\3_\3_\3_\3"+
		"_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3"+
		"_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3"+
		"_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\7_\u0456\n_\f_\16_\u0459\13_"+
		"\3`\5`\u045c\n`\3a\3a\3b\7b\u0461\nb\fb\16b\u0464\13b\3b\3b\3c\3c\3c\5"+
		"c\u046b\nc\3d\3d\3d\3d\3d\3d\3d\3d\3d\5d\u0476\nd\3e\3e\3e\3e\3e\3e\3"+
		"e\3e\3e\3e\3e\5e\u0483\ne\3f\3f\3g\3g\3g\3g\3h\3h\3h\3h\3h\3h\3h\3h\3"+
		"h\3h\3h\5h\u0496\nh\3i\3i\3i\3j\3j\3j\7j\u049e\nj\fj\16j\u04a1\13j\3k"+
		"\3k\3k\3k\3k\5k\u04a8\nk\3l\3l\3m\3m\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n"+
		"\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n\5n\u04cc\nn"+
		"\3o\3o\3o\3o\3o\3o\3o\3o\3o\3o\3o\5o\u04d9\no\3p\3p\3p\7p\u04de\np\fp"+
		"\16p\u04e1\13p\3q\3q\3q\5q\u04e6\nq\3r\3r\3s\3s\5s\u04ec\ns\3t\3t\3u\3"+
		"u\3v\3v\5v\u04f4\nv\3w\3w\3x\3x\3y\3y\3z\3z\3z\5z\u04ff\nz\3{\3{\3|\3"+
		"|\3}\3}\3}\3}\5}\u0509\n}\3~\3~\3\177\3\177\3\177\3\177\3\177\5\177\u0512"+
		"\n\177\3\u0080\3\u0080\3\u0080\3\u0080\5\u0080\u0518\n\u0080\3\u0081\3"+
		"\u0081\3\u0081\3\u0081\3\u0081\3\u0081\3\u0081\7\u0081\u0521\n\u0081\f"+
		"\u0081\16\u0081\u0524\13\u0081\3\u0081\2\5\u00a2\u00bc\u0100\u0082\2\4"+
		"\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>@BDFHJLNP"+
		"RTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086\u0088\u008a\u008c\u008e"+
		"\u0090\u0092\u0094\u0096\u0098\u009a\u009c\u009e\u00a0\u00a2\u00a4\u00a6"+
		"\u00a8\u00aa\u00ac\u00ae\u00b0\u00b2\u00b4\u00b6\u00b8\u00ba\u00bc\u00be"+
		"\u00c0\u00c2\u00c4\u00c6\u00c8\u00ca\u00cc\u00ce\u00d0\u00d2\u00d4\u00d6"+
		"\u00d8\u00da\u00dc\u00de\u00e0\u00e2\u00e4\u00e6\u00e8\u00ea\u00ec\u00ee"+
		"\u00f0\u00f2\u00f4\u00f6\u00f8\u00fa\u00fc\u00fe\u0100\2\17\4\2ZZgg\3"+
		"\2\31\33\3\2\25\26\3\2\34\36\3\2\37\"\3\2#&\3\2,\66\4\2\678oo\3\29<\3"+
		"\2fn\3\2\678\3\2=]\3\2^e\2\u0570\2\u0102\3\2\2\2\4\u0112\3\2\2\2\6\u0117"+
		"\3\2\2\2\b\u011b\3\2\2\2\n\u011f\3\2\2\2\f\u0121\3\2\2\2\16\u0123\3\2"+
		"\2\2\20\u012a\3\2\2\2\22\u0130\3\2\2\2\24\u0132\3\2\2\2\26\u0136\3\2\2"+
		"\2\30\u0138\3\2\2\2\32\u0147\3\2\2\2\34\u0149\3\2\2\2\36\u014c\3\2\2\2"+
		" \u015b\3\2\2\2\"\u015f\3\2\2\2$\u016c\3\2\2\2&\u0185\3\2\2\2(\u0187\3"+
		"\2\2\2*\u018f\3\2\2\2,\u0198\3\2\2\2.\u01a1\3\2\2\2\60\u01a8\3\2\2\2\62"+
		"\u01aa\3\2\2\2\64\u01ae\3\2\2\2\66\u01b1\3\2\2\28\u01b4\3\2\2\2:\u01b6"+
		"\3\2\2\2<\u01ba\3\2\2\2>\u022e\3\2\2\2@\u0230\3\2\2\2B\u0233\3\2\2\2D"+
		"\u0236\3\2\2\2F\u023b\3\2\2\2H\u023d\3\2\2\2J\u0244\3\2\2\2L\u024b\3\2"+
		"\2\2N\u0252\3\2\2\2P\u0258\3\2\2\2R\u025e\3\2\2\2T\u026b\3\2\2\2V\u026f"+
		"\3\2\2\2X\u0275\3\2\2\2Z\u027a\3\2\2\2\\\u0280\3\2\2\2^\u0282\3\2\2\2"+
		"`\u0294\3\2\2\2b\u0296\3\2\2\2d\u029c\3\2\2\2f\u02a1\3\2\2\2h\u02a3\3"+
		"\2\2\2j\u02a6\3\2\2\2l\u02b8\3\2\2\2n\u02ba\3\2\2\2p\u02be\3\2\2\2r\u02c6"+
		"\3\2\2\2t\u02c8\3\2\2\2v\u02d0\3\2\2\2x\u02d2\3\2\2\2z\u02d4\3\2\2\2|"+
		"\u02d7\3\2\2\2~\u02d9\3\2\2\2\u0080\u02e0\3\2\2\2\u0082\u02e8\3\2\2\2"+
		"\u0084\u02fa\3\2\2\2\u0086\u0316\3\2\2\2\u0088\u0318\3\2\2\2\u008a\u031a"+
		"\3\2\2\2\u008c\u0323\3\2\2\2\u008e\u032d\3\2\2\2\u0090\u0339\3\2\2\2\u0092"+
		"\u033b\3\2\2\2\u0094\u033f\3\2\2\2\u0096\u0346\3\2\2\2\u0098\u034e\3\2"+
		"\2\2\u009a\u0351\3\2\2\2\u009c\u0354\3\2\2\2\u009e\u035c\3\2\2\2\u00a0"+
		"\u036f\3\2\2\2\u00a2\u037a\3\2\2\2\u00a4\u038e\3\2\2\2\u00a6\u0392\3\2"+
		"\2\2\u00a8\u039e\3\2\2\2\u00aa\u03a0\3\2\2\2\u00ac\u03af\3\2\2\2\u00ae"+
		"\u03b3\3\2\2\2\u00b0\u03b8\3\2\2\2\u00b2\u03ba\3\2\2\2\u00b4\u03be\3\2"+
		"\2\2\u00b6\u03c1\3\2\2\2\u00b8\u03c9\3\2\2\2\u00ba\u03d5\3\2\2\2\u00bc"+
		"\u0410\3\2\2\2\u00be\u045b\3\2\2\2\u00c0\u045d\3\2\2\2\u00c2\u0462\3\2"+
		"\2\2\u00c4\u046a\3\2\2\2\u00c6\u0475\3\2\2\2\u00c8\u0482\3\2\2\2\u00ca"+
		"\u0484\3\2\2\2\u00cc\u0486\3\2\2\2\u00ce\u0495\3\2\2\2\u00d0\u0497\3\2"+
		"\2\2\u00d2\u049a\3\2\2\2\u00d4\u04a7\3\2\2\2\u00d6\u04a9\3\2\2\2\u00d8"+
		"\u04ab\3\2\2\2\u00da\u04cb\3\2\2\2\u00dc\u04d8\3\2\2\2\u00de\u04da\3\2"+
		"\2\2\u00e0\u04e2\3\2\2\2\u00e2\u04e7\3\2\2\2\u00e4\u04eb\3\2\2\2\u00e6"+
		"\u04ed\3\2\2\2\u00e8\u04ef\3\2\2\2\u00ea\u04f3\3\2\2\2\u00ec\u04f5\3\2"+
		"\2\2\u00ee\u04f7\3\2\2\2\u00f0\u04f9\3\2\2\2\u00f2\u04fe\3\2\2\2\u00f4"+
		"\u0500\3\2\2\2\u00f6\u0502\3\2\2\2\u00f8\u0508\3\2\2\2\u00fa\u050a\3\2"+
		"\2\2\u00fc\u0511\3\2\2\2\u00fe\u0517\3\2\2\2\u0100\u0519\3\2\2\2\u0102"+
		"\u0103\5\u00c2b\2\u0103\3\3\2\2\2\u0104\u0113\5\f\7\2\u0105\u0113\5\34"+
		"\17\2\u0106\u0113\58\35\2\u0107\u0113\5:\36\2\u0108\u0113\5<\37\2\u0109"+
		"\u0113\5\n\6\2\u010a\u0113\5H%\2\u010b\u0113\5J&\2\u010c\u0113\5L\'\2"+
		"\u010d\u0113\5N(\2\u010e\u0113\5Z.\2\u010f\u0113\5^\60\2\u0110\u0113\5"+
		"`\61\2\u0111\u0113\5h\65\2\u0112\u0104\3\2\2\2\u0112\u0105\3\2\2\2\u0112"+
		"\u0106\3\2\2\2\u0112\u0107\3\2\2\2\u0112\u0108\3\2\2\2\u0112\u0109\3\2"+
		"\2\2\u0112\u010a\3\2\2\2\u0112\u010b\3\2\2\2\u0112\u010c\3\2\2\2\u0112"+
		"\u010d\3\2\2\2\u0112\u010e\3\2\2\2\u0112\u010f\3\2\2\2\u0112\u0110\3\2"+
		"\2\2\u0112\u0111\3\2\2\2\u0113\5\3\2\2\2\u0114\u0118\5\b\5\2\u0115\u0118"+
		"\5\u0092J\2\u0116\u0118\5\24\13\2\u0117\u0114\3\2\2\2\u0117\u0115\3\2"+
		"\2\2\u0117\u0116\3\2\2\2\u0118\7\3\2\2\2\u0119\u011c\5j\66\2\u011a\u011c"+
		"\5\u008cG\2\u011b\u0119\3\2\2\2\u011b\u011a\3\2\2\2\u011c\t\3\2\2\2\u011d"+
		"\u0120\5> \2\u011e\u0120\5P)\2\u011f\u011d\3\2\2\2\u011f\u011e\3\2\2\2"+
		"\u0120\13\3\2\2\2\u0121\u0122\5\16\b\2\u0122\r\3\2\2\2\u0123\u0125\7\t"+
		"\2\2\u0124\u0126\5\20\t\2\u0125\u0124\3\2\2\2\u0125\u0126\3\2\2\2\u0126"+
		"\u0127\3\2\2\2\u0127\u0128\7\n\2\2\u0128\17\3\2\2\2\u0129\u012b\5\22\n"+
		"\2\u012a\u0129\3\2\2\2\u012b\u012c\3\2\2\2\u012c\u012a\3\2\2\2\u012c\u012d"+
		"\3\2\2\2\u012d\21\3\2\2\2\u012e\u0131\5\4\3\2\u012f\u0131\5\6\4\2\u0130"+
		"\u012e\3\2\2\2\u0130\u012f\3\2\2\2\u0131\23\3\2\2\2\u0132\u0133\5\26\f"+
		"\2\u0133\u0134\5\30\r\2\u0134\u0135\5\u00f8}\2\u0135\25\3\2\2\2\u0136"+
		"\u0137\t\2\2\2\u0137\27\3\2\2\2\u0138\u013d\5\32\16\2\u0139\u013a\7\f"+
		"\2\2\u013a\u013c\5\32\16\2\u013b\u0139\3\2\2\2\u013c\u013f\3\2\2\2\u013d"+
		"\u013b\3\2\2\2\u013d\u013e\3\2\2\2\u013e\31\3\2\2\2\u013f\u013d\3\2\2"+
		"\2\u0140\u0142\5\u00eex\2\u0141\u0143\5\66\34\2\u0142\u0141\3\2\2\2\u0142"+
		"\u0143\3\2\2\2\u0143\u0148\3\2\2\2\u0144\u0145\5\"\22\2\u0145\u0146\5"+
		"\66\34\2\u0146\u0148\3\2\2\2\u0147\u0140\3\2\2\2\u0147\u0144\3\2\2\2\u0148"+
		"\33\3\2\2\2\u0149\u014a\5@!\2\u014a\u014b\5\u00f8}\2\u014b\35\3\2\2\2"+
		"\u014c\u0151\5 \21\2\u014d\u014e\7\f\2\2\u014e\u0150\5 \21\2\u014f\u014d"+
		"\3\2\2\2\u0150\u0153\3\2\2\2\u0151\u014f\3\2\2\2\u0151\u0152\3\2\2\2\u0152"+
		"\37\3\2\2\2\u0153\u0151\3\2\2\2\u0154\u0156\5\u00eex\2\u0155\u0157\5\66"+
		"\34\2\u0156\u0155\3\2\2\2\u0156\u0157\3\2\2\2\u0157\u015c\3\2\2\2\u0158"+
		"\u0159\5\"\22\2\u0159\u015a\5\66\34\2\u015a\u015c\3\2\2\2\u015b\u0154"+
		"\3\2\2\2\u015b\u0158\3\2\2\2\u015c!\3\2\2\2\u015d\u0160\5$\23\2\u015e"+
		"\u0160\5&\24\2\u015f\u015d\3\2\2\2\u015f\u015e\3\2\2\2\u0160#\3\2\2\2"+
		"\u0161\u0162\7\t\2\2\u0162\u016d\7\n\2\2\u0163\u0164\7\t\2\2\u0164\u0165"+
		"\5(\25\2\u0165\u0166\7\n\2\2\u0166\u016d\3\2\2\2\u0167\u0168\7\t\2\2\u0168"+
		"\u0169\5(\25\2\u0169\u016a\7\f\2\2\u016a\u016b\7\n\2\2\u016b\u016d\3\2"+
		"\2\2\u016c\u0161\3\2\2\2\u016c\u0163\3\2\2\2\u016c\u0167\3\2\2\2\u016d"+
		"%\3\2\2\2\u016e\u0170\7\5\2\2\u016f\u0171\5\u00a4S\2\u0170\u016f\3\2\2"+
		"\2\u0170\u0171\3\2\2\2\u0171\u0173\3\2\2\2\u0172\u0174\5\64\33\2\u0173"+
		"\u0172\3\2\2\2\u0173\u0174\3\2\2\2\u0174\u0175\3\2\2\2\u0175\u0186\7\6"+
		"\2\2\u0176\u0177\7\5\2\2\u0177\u0178\5*\26\2\u0178\u0179\7\6\2\2\u0179"+
		"\u0186\3\2\2\2\u017a\u017b\7\5\2\2\u017b\u017c\5*\26\2\u017c\u017e\7\f"+
		"\2\2\u017d\u017f\5\u00a4S\2\u017e\u017d\3\2\2\2\u017e\u017f\3\2\2\2\u017f"+
		"\u0181\3\2\2\2\u0180\u0182\5\64\33\2\u0181\u0180\3\2\2\2\u0181\u0182\3"+
		"\2\2\2\u0182\u0183\3\2\2\2\u0183\u0184\7\6\2\2\u0184\u0186\3\2\2\2\u0185"+
		"\u016e\3\2\2\2\u0185\u0176\3\2\2\2\u0185\u017a\3\2\2\2\u0186\'\3\2\2\2"+
		"\u0187\u018c\5.\30\2\u0188\u0189\7\f\2\2\u0189\u018b\5.\30\2\u018a\u0188"+
		"\3\2\2\2\u018b\u018e\3\2\2\2\u018c\u018a\3\2\2\2\u018c\u018d\3\2\2\2\u018d"+
		")\3\2\2\2\u018e\u018c\3\2\2\2\u018f\u0194\5,\27\2\u0190\u0191\7\f\2\2"+
		"\u0191\u0193\5,\27\2\u0192\u0190\3\2\2\2\u0193\u0196\3\2\2\2\u0194\u0192"+
		"\3\2\2\2\u0194\u0195\3\2\2\2\u0195+\3\2\2\2\u0196\u0194\3\2\2\2\u0197"+
		"\u0199\5\u00a4S\2\u0198\u0197\3\2\2\2\u0198\u0199\3\2\2\2\u0199\u019a"+
		"\3\2\2\2\u019a\u019b\5\60\31\2\u019b-\3\2\2\2\u019c\u01a2\5\62\32\2\u019d"+
		"\u019e\5\u00aeX\2\u019e\u019f\7\20\2\2\u019f\u01a0\5\60\31\2\u01a0\u01a2"+
		"\3\2\2\2\u01a1\u019c\3\2\2\2\u01a1\u019d\3\2\2\2\u01a2/\3\2\2\2\u01a3"+
		"\u01a9\5\62\32\2\u01a4\u01a6\5\"\22\2\u01a5\u01a7\5\66\34\2\u01a6\u01a5"+
		"\3\2\2\2\u01a6\u01a7\3\2\2\2\u01a7\u01a9\3\2\2\2\u01a8\u01a3\3\2\2\2\u01a8"+
		"\u01a4\3\2\2\2\u01a9\61\3\2\2\2\u01aa\u01ac\5\u00eex\2\u01ab\u01ad\5\66"+
		"\34\2\u01ac\u01ab\3\2\2\2\u01ac\u01ad\3\2\2\2\u01ad\63\3\2\2\2\u01ae\u01af"+
		"\7\21\2\2\u01af\u01b0\5\u00eex\2\u01b0\65\3\2\2\2\u01b1\u01b2\7\16\2\2"+
		"\u01b2\u01b3\5\u00bc_\2\u01b3\67\3\2\2\2\u01b4\u01b5\7\13\2\2\u01b59\3"+
		"\2\2\2\u01b6\u01b7\6\36\2\2\u01b7\u01b8\5\u00ba^\2\u01b8\u01b9\5\u00f8"+
		"}\2\u01b9;\3\2\2\2\u01ba\u01bb\7R\2\2\u01bb\u01bc\7\7\2\2\u01bc\u01bd"+
		"\5\u00ba^\2\u01bd\u01be\7\b\2\2\u01be\u01c1\5\4\3\2\u01bf\u01c0\7B\2\2"+
		"\u01c0\u01c2\5\4\3\2\u01c1\u01bf\3\2\2\2\u01c1\u01c2\3\2\2\2\u01c2=\3"+
		"\2\2\2\u01c3\u01c4\7>\2\2\u01c4\u01c5\5\4\3\2\u01c5\u01c6\7L\2\2\u01c6"+
		"\u01c7\7\7\2\2\u01c7\u01c8\5\u00ba^\2\u01c8\u01c9\7\b\2\2\u01c9\u01ca"+
		"\5\u00f8}\2\u01ca\u022f\3\2\2\2\u01cb\u01cc\7L\2\2\u01cc\u01cd\7\7\2\2"+
		"\u01cd\u01ce\5\u00ba^\2\u01ce\u01cf\7\b\2\2\u01cf\u01d0\5\4\3\2\u01d0"+
		"\u022f\3\2\2\2\u01d1\u01d2\7J\2\2\u01d2\u01d3\7\7\2\2\u01d3\u01d5\6 \3"+
		"\2\u01d4\u01d6\5\u00ba^\2\u01d5\u01d4\3\2\2\2\u01d5\u01d6\3\2\2\2\u01d6"+
		"\u01d7\3\2\2\2\u01d7\u01d9\7\13\2\2\u01d8\u01da\5\u00ba^\2\u01d9\u01d8"+
		"\3\2\2\2\u01d9\u01da\3\2\2\2\u01da\u01db\3\2\2\2\u01db\u01dd\7\13\2\2"+
		"\u01dc\u01de\5\u00ba^\2\u01dd\u01dc\3\2\2\2\u01dd\u01de\3\2\2\2\u01de"+
		"\u01df\3\2\2\2\u01df\u01e0\7\b\2\2\u01e0\u022f\5\4\3\2\u01e1\u01e2\7J"+
		"\2\2\u01e2\u01e3\7\7\2\2\u01e3\u01e4\5@!\2\u01e4\u01e6\7\13\2\2\u01e5"+
		"\u01e7\5\u00ba^\2\u01e6\u01e5\3\2\2\2\u01e6\u01e7\3\2\2\2\u01e7\u01e8"+
		"\3\2\2\2\u01e8\u01ea\7\13\2\2\u01e9\u01eb\5\u00ba^\2\u01ea\u01e9\3\2\2"+
		"\2\u01ea\u01eb\3\2\2\2\u01eb\u01ec\3\2\2\2\u01ec\u01ed\7\b\2\2\u01ed\u01ee"+
		"\5\4\3\2\u01ee\u022f\3\2\2\2\u01ef\u01f0\7J\2\2\u01f0\u01f1\7\7\2\2\u01f1"+
		"\u01f3\5\24\13\2\u01f2\u01f4\5\u00ba^\2\u01f3\u01f2\3\2\2\2\u01f3\u01f4"+
		"\3\2\2\2\u01f4\u01f5\3\2\2\2\u01f5\u01f7\7\13\2\2\u01f6\u01f8\5\u00ba"+
		"^\2\u01f7\u01f6\3\2\2\2\u01f7\u01f8\3\2\2\2\u01f8\u01f9\3\2\2\2\u01f9"+
		"\u01fa\7\b\2\2\u01fa\u01fb\5\4\3\2\u01fb\u022f\3\2\2\2\u01fc\u01fd\7J"+
		"\2\2\u01fd\u01fe\7\7\2\2\u01fe\u01ff\6 \4\2\u01ff\u0200\5\u00bc_\2\u0200"+
		"\u0201\7U\2\2\u0201\u0202\5\u00ba^\2\u0202\u0203\7\b\2\2\u0203\u0204\5"+
		"\4\3\2\u0204\u022f\3\2\2\2\u0205\u0206\7J\2\2\u0206\u0207\7\7\2\2\u0207"+
		"\u0208\5B\"\2\u0208\u0209\7U\2\2\u0209\u020a\5\u00ba^\2\u020a\u020b\7"+
		"\b\2\2\u020b\u020c\5\4\3\2\u020c\u022f\3\2\2\2\u020d\u020e\7J\2\2\u020e"+
		"\u020f\7\7\2\2\u020f\u0210\5D#\2\u0210\u0211\7U\2\2\u0211\u0212\5\u00ba"+
		"^\2\u0212\u0213\7\b\2\2\u0213\u0214\5\4\3\2\u0214\u022f\3\2\2\2\u0215"+
		"\u0216\7J\2\2\u0216\u0217\7\7\2\2\u0217\u0218\6 \5\2\u0218\u0219\5\u00bc"+
		"_\2\u0219\u021a\7l\2\2\u021a\u021b\5\u00bc_\2\u021b\u021c\7\b\2\2\u021c"+
		"\u021d\5\4\3\2\u021d\u022f\3\2\2\2\u021e\u021f\7J\2\2\u021f\u0220\7\7"+
		"\2\2\u0220\u0221\5B\"\2\u0221\u0222\7l\2\2\u0222\u0223\5\u00bc_\2\u0223"+
		"\u0224\7\b\2\2\u0224\u0225\5\4\3\2\u0225\u022f\3\2\2\2\u0226\u0227\7J"+
		"\2\2\u0227\u0228\7\7\2\2\u0228\u0229\5D#\2\u0229\u022a\7l\2\2\u022a\u022b"+
		"\5\u00bc_\2\u022b\u022c\7\b\2\2\u022c\u022d\5\4\3\2\u022d\u022f\3\2\2"+
		"\2\u022e\u01c3\3\2\2\2\u022e\u01cb\3\2\2\2\u022e\u01d1\3\2\2\2\u022e\u01e1"+
		"\3\2\2\2\u022e\u01ef\3\2\2\2\u022e\u01fc\3\2\2\2\u022e\u0205\3\2\2\2\u022e"+
		"\u020d\3\2\2\2\u022e\u0215\3\2\2\2\u022e\u021e\3\2\2\2\u022e\u0226\3\2"+
		"\2\2\u022f?\3\2\2\2\u0230\u0231\7D\2\2\u0231\u0232\5\36\20\2\u0232A\3"+
		"\2\2\2\u0233\u0234\7D\2\2\u0234\u0235\5F$\2\u0235C\3\2\2\2\u0236\u0237"+
		"\5\26\f\2\u0237\u0238\5F$\2\u0238E\3\2\2\2\u0239\u023c\5\u00eex\2\u023a"+
		"\u023c\5\"\22\2\u023b\u0239\3\2\2\2\u023b\u023a\3\2\2\2\u023cG\3\2\2\2"+
		"\u023d\u0240\7I\2\2\u023e\u023f\6%\6\2\u023f\u0241\5\u00e8u\2\u0240\u023e"+
		"\3\2\2\2\u0240\u0241\3\2\2\2\u0241\u0242\3\2\2\2\u0242\u0243\5\u00f8}"+
		"\2\u0243I\3\2\2\2\u0244\u0247\7=\2\2\u0245\u0246\6&\7\2\u0246\u0248\5"+
		"\u00e8u\2\u0247\u0245\3\2\2\2\u0247\u0248\3\2\2\2\u0248\u0249\3\2\2\2"+
		"\u0249\u024a\5\u00f8}\2\u024aK\3\2\2\2\u024b\u024e\7G\2\2\u024c\u024d"+
		"\6\'\b\2\u024d\u024f\5\u00ba^\2\u024e\u024c\3\2\2\2\u024e\u024f\3\2\2"+
		"\2\u024f\u0250\3\2\2\2\u0250\u0251\5\u00f8}\2\u0251M\3\2\2\2\u0252\u0253"+
		"\7P\2\2\u0253\u0254\7\7\2\2\u0254\u0255\5\u00ba^\2\u0255\u0256\7\b\2\2"+
		"\u0256\u0257\5\4\3\2\u0257O\3\2\2\2\u0258\u0259\7K\2\2\u0259\u025a\7\7"+
		"\2\2\u025a\u025b\5\u00ba^\2\u025b\u025c\7\b\2\2\u025c\u025d\5R*\2\u025d"+
		"Q\3\2\2\2\u025e\u0260\7\t\2\2\u025f\u0261\5T+\2\u0260\u025f\3\2\2\2\u0260"+
		"\u0261\3\2\2\2\u0261\u0266\3\2\2\2\u0262\u0264\5X-\2\u0263\u0265\5T+\2"+
		"\u0264\u0263\3\2\2\2\u0264\u0265\3\2\2\2\u0265\u0267\3\2\2\2\u0266\u0262"+
		"\3\2\2\2\u0266\u0267\3\2\2\2\u0267\u0268\3\2\2\2\u0268\u0269\7\n\2\2\u0269"+
		"S\3\2\2\2\u026a\u026c\5V,\2\u026b\u026a\3\2\2\2\u026c\u026d\3\2\2\2\u026d"+
		"\u026b\3\2\2\2\u026d\u026e\3\2\2\2\u026eU\3\2\2\2\u026f\u0270\7A\2\2\u0270"+
		"\u0271\5\u00ba^\2\u0271\u0273\7\20\2\2\u0272\u0274\5\20\t\2\u0273\u0272"+
		"\3\2\2\2\u0273\u0274\3\2\2\2\u0274W\3\2\2\2\u0275\u0276\7Q\2\2\u0276\u0278"+
		"\7\20\2\2\u0277\u0279\5\20\t\2\u0278\u0277\3\2\2\2\u0278\u0279\3\2\2\2"+
		"\u0279Y\3\2\2\2\u027a\u027b\5\u00e8u\2\u027b\u027c\7\20\2\2\u027c\u027d"+
		"\5\\/\2\u027d[\3\2\2\2\u027e\u0281\5\4\3\2\u027f\u0281\5j\66\2\u0280\u027e"+
		"\3\2\2\2\u0280\u027f\3\2\2\2\u0281]\3\2\2\2\u0282\u0283\7S\2\2\u0283\u0284"+
		"\6\60\t\2\u0284\u0285\5\u00ba^\2\u0285\u0286\5\u00f8}\2\u0286_\3\2\2\2"+
		"\u0287\u0288\7V\2\2\u0288\u0289\5\16\b\2\u0289\u028a\5b\62\2\u028a\u0295"+
		"\3\2\2\2\u028b\u028c\7V\2\2\u028c\u028d\5\16\b\2\u028d\u028e\5d\63\2\u028e"+
		"\u0295\3\2\2\2\u028f\u0290\7V\2\2\u0290\u0291\5\16\b\2\u0291\u0292\5b"+
		"\62\2\u0292\u0293\5d\63\2\u0293\u0295\3\2\2\2\u0294\u0287\3\2\2\2\u0294"+
		"\u028b\3\2\2\2\u0294\u028f\3\2\2\2\u0295a\3\2\2\2\u0296\u0297\7E\2\2\u0297"+
		"\u0298\7\7\2\2\u0298\u0299\5f\64\2\u0299\u029a\7\b\2\2\u029a\u029b\5\16"+
		"\b\2\u029bc\3\2\2\2\u029c\u029d\7F\2\2\u029d\u029e\5\16\b\2\u029ee\3\2"+
		"\2\2\u029f\u02a2\5\u00eex\2\u02a0\u02a2\5\"\22\2\u02a1\u029f\3\2\2\2\u02a1"+
		"\u02a0\3\2\2\2\u02a2g\3\2\2\2\u02a3\u02a4\7M\2\2\u02a4\u02a5\5\u00f8}"+
		"\2\u02a5i\3\2\2\2\u02a6\u02a7\7N\2\2\u02a7\u02a8\5\u00eex\2\u02a8\u02a9"+
		"\7\7\2\2\u02a9\u02aa\5p9\2\u02aa\u02ab\7\b\2\2\u02ab\u02ac\7\t\2\2\u02ac"+
		"\u02ad\5z>\2\u02ad\u02ae\7\n\2\2\u02aek\3\2\2\2\u02af\u02b0\7N\2\2\u02b0"+
		"\u02b1\7\7\2\2\u02b1\u02b2\5p9\2\u02b2\u02b3\7\b\2\2\u02b3\u02b4\7\t\2"+
		"\2\u02b4\u02b5\5z>\2\u02b5\u02b6\7\n\2\2\u02b6\u02b9\3\2\2\2\u02b7\u02b9"+
		"\5j\66\2\u02b8\u02af\3\2\2\2\u02b8\u02b7\3\2\2\2\u02b9m\3\2\2\2\u02ba"+
		"\u02bb\5p9\2\u02bbo\3\2\2\2\u02bc\u02bf\3\2\2\2\u02bd\u02bf\5r:\2\u02be"+
		"\u02bc\3\2\2\2\u02be\u02bd\3\2\2\2\u02bfq\3\2\2\2\u02c0\u02c7\5v<\2\u02c1"+
		"\u02c4\5t;\2\u02c2\u02c3\7\f\2\2\u02c3\u02c5\5v<\2\u02c4\u02c2\3\2\2\2"+
		"\u02c4\u02c5\3\2\2\2\u02c5\u02c7\3\2\2\2\u02c6\u02c0\3\2\2\2\u02c6\u02c1"+
		"\3\2\2\2\u02c7s\3\2\2\2\u02c8\u02cd\5x=\2\u02c9\u02ca\7\f\2\2\u02ca\u02cc"+
		"\5x=\2\u02cb\u02c9\3\2\2\2\u02cc\u02cf\3\2\2\2\u02cd\u02cb\3\2\2\2\u02cd"+
		"\u02ce\3\2\2\2\u02ceu\3\2\2\2\u02cf\u02cd\3\2\2\2\u02d0\u02d1\5\64\33"+
		"\2\u02d1w\3\2\2\2\u02d2\u02d3\5\60\31\2\u02d3y\3\2\2\2\u02d4\u02d5\5|"+
		"?\2\u02d5{\3\2\2\2\u02d6\u02d8\5\20\t\2\u02d7\u02d6\3\2\2\2\u02d7\u02d8"+
		"\3\2\2\2\u02d8}\3\2\2\2\u02d9\u02da\5\u0080A\2\u02da\u02db\6@\n\2\u02db"+
		"\u02dc\7\r\2\2\u02dc\u02dd\5\u0082B\2\u02dd\177\3\2\2\2\u02de\u02e1\5"+
		"\u00eex\2\u02df\u02e1\5\u0084C\2\u02e0\u02de\3\2\2\2\u02e0\u02df\3\2\2"+
		"\2\u02e1\u0081\3\2\2\2\u02e2\u02e3\6B\13\2\u02e3\u02e9\5\u00bc_\2\u02e4"+
		"\u02e5\7\t\2\2\u02e5\u02e6\5z>\2\u02e6\u02e7\7\n\2\2\u02e7\u02e9\3\2\2"+
		"\2\u02e8\u02e2\3\2\2\2\u02e8\u02e4\3\2\2\2\u02e9\u0083\3\2\2\2\u02ea\u02eb"+
		"\7\7\2\2\u02eb\u02ec\5\u00ba^\2\u02ec\u02ed\7\b\2\2\u02ed\u02fb\3\2\2"+
		"\2\u02ee\u02ef\7\7\2\2\u02ef\u02fb\7\b\2\2\u02f0\u02f1\7\7\2\2\u02f1\u02f2"+
		"\5\64\33\2\u02f2\u02f3\7\b\2\2\u02f3\u02fb\3\2\2\2\u02f4\u02f5\7\7\2\2"+
		"\u02f5\u02f6\5\u00ba^\2\u02f6\u02f7\7\f\2\2\u02f7\u02f8\5\64\33\2\u02f8"+
		"\u02f9\7\b\2\2\u02f9\u02fb\3\2\2\2\u02fa\u02ea\3\2\2\2\u02fa\u02ee\3\2"+
		"\2\2\u02fa\u02f0\3\2\2\2\u02fa\u02f4\3\2\2\2\u02fb\u0085\3\2\2\2\u02fc"+
		"\u02fd\5\u00aeX\2\u02fd\u02fe\7\7\2\2\u02fe\u02ff\5n8\2\u02ff\u0300\7"+
		"\b\2\2\u0300\u0301\7\t\2\2\u0301\u0302\5z>\2\u0302\u0303\7\n\2\2\u0303"+
		"\u0317\3\2\2\2\u0304\u0317\5\u008aF\2\u0305\u0306\7i\2\2\u0306\u0307\5"+
		"\u00aeX\2\u0307\u0308\7\7\2\2\u0308\u0309\7\b\2\2\u0309\u030a\7\t\2\2"+
		"\u030a\u030b\5z>\2\u030b\u030c\7\n\2\2\u030c\u0317\3\2\2\2\u030d\u030e"+
		"\7j\2\2\u030e\u030f\5\u00aeX\2\u030f\u0310\7\7\2\2\u0310\u0311\5\u0088"+
		"E\2\u0311\u0312\7\b\2\2\u0312\u0313\7\t\2\2\u0313\u0314\5z>\2\u0314\u0315"+
		"\7\n\2\2\u0315\u0317\3\2\2\2\u0316\u02fc\3\2\2\2\u0316\u0304\3\2\2\2\u0316"+
		"\u0305\3\2\2\2\u0316\u030d\3\2\2\2\u0317\u0087\3\2\2\2\u0318\u0319\5x"+
		"=\2\u0319\u0089\3\2\2\2\u031a\u031b\7\31\2\2\u031b\u031c\5\u00aeX\2\u031c"+
		"\u031d\7\7\2\2\u031d\u031e\5n8\2\u031e\u031f\7\b\2\2\u031f\u0320\7\t\2"+
		"\2\u0320\u0321\5\u0090I\2\u0321\u0322\7\n\2\2\u0322\u008b\3\2\2\2\u0323"+
		"\u0324\7N\2\2\u0324\u0325\7\31\2\2\u0325\u0326\5\u00eex\2\u0326\u0327"+
		"\7\7\2\2\u0327\u0328\5p9\2\u0328\u0329\7\b\2\2\u0329\u032a\7\t\2\2\u032a"+
		"\u032b\5\u0090I\2\u032b\u032c\7\n\2\2\u032c\u008d\3\2\2\2\u032d\u032e"+
		"\7N\2\2\u032e\u0330\7\31\2\2\u032f\u0331\5\u00eex\2\u0330\u032f\3\2\2"+
		"\2\u0330\u0331\3\2\2\2\u0331\u0332\3\2\2\2\u0332\u0333\7\7\2\2\u0333\u0334"+
		"\5p9\2\u0334\u0335\7\b\2\2\u0335\u0336\7\t\2\2\u0336\u0337\5\u0090I\2"+
		"\u0337\u0338\7\n\2\2\u0338\u008f\3\2\2\2\u0339\u033a\5z>\2\u033a\u0091"+
		"\3\2\2\2\u033b\u033c\7X\2\2\u033c\u033d\5\u00eex\2\u033d\u033e\5\u0096"+
		"L\2\u033e\u0093\3\2\2\2\u033f\u0341\7X\2\2\u0340\u0342\5\u00eex\2\u0341"+
		"\u0340\3\2\2\2\u0341\u0342\3\2\2\2\u0342\u0343\3\2\2\2\u0343\u0344\5\u0096"+
		"L\2\u0344\u0095\3\2\2\2\u0345\u0347\5\u0098M\2\u0346\u0345\3\2\2\2\u0346"+
		"\u0347\3\2\2\2\u0347\u0348\3\2\2\2\u0348\u034a\7\t\2\2\u0349\u034b\5\u009a"+
		"N\2\u034a\u0349\3\2\2\2\u034a\u034b\3\2\2\2\u034b\u034c\3\2\2\2\u034c"+
		"\u034d\7\n\2\2\u034d\u0097\3\2\2\2\u034e\u034f\7Y\2\2\u034f\u0350\5\u00bc"+
		"_\2\u0350\u0099\3\2\2\2\u0351\u0352\5\u009cO\2\u0352\u009b\3\2\2\2\u0353"+
		"\u0355\5\u009eP\2\u0354\u0353\3\2\2\2\u0355\u0356\3\2\2\2\u0356\u0354"+
		"\3\2\2\2\u0356\u0357\3\2\2\2\u0357\u009d\3\2\2\2\u0358\u035d\5\u0086D"+
		"\2\u0359\u035a\7f\2\2\u035a\u035d\5\u0086D\2\u035b\u035d\7\13\2\2\u035c"+
		"\u0358\3\2\2\2\u035c\u0359\3\2\2\2\u035c\u035b\3\2\2\2\u035d\u009f\3\2"+
		"\2\2\u035e\u0360\7\5\2\2\u035f\u0361\5\u00a4S\2\u0360\u035f\3\2\2\2\u0360"+
		"\u0361\3\2\2\2\u0361\u0362\3\2\2\2\u0362\u0370\7\6\2\2\u0363\u0364\7\5"+
		"\2\2\u0364\u0365\5\u00a2R\2\u0365\u0366\7\6\2\2\u0366\u0370\3\2\2\2\u0367"+
		"\u0368\7\5\2\2\u0368\u0369\5\u00a2R\2\u0369\u036b\7\f\2\2\u036a\u036c"+
		"\5\u00a4S\2\u036b\u036a\3\2\2\2\u036b\u036c\3\2\2\2\u036c\u036d\3\2\2"+
		"\2\u036d\u036e\7\6\2\2\u036e\u0370\3\2\2\2\u036f\u035e\3\2\2\2\u036f\u0363"+
		"\3\2\2\2\u036f\u0367\3\2\2\2\u0370\u00a1\3\2\2\2\u0371\u0373\bR\1\2\u0372"+
		"\u0374\5\u00a4S\2\u0373\u0372\3\2\2\2\u0373\u0374\3\2\2\2\u0374\u0375"+
		"\3\2\2\2\u0375\u037b\5\u00bc_\2\u0376\u0378\5\u00a4S\2\u0377\u0376\3\2"+
		"\2\2\u0377\u0378\3\2\2\2\u0378\u0379\3\2\2\2\u0379\u037b\5\u00a6T\2\u037a"+
		"\u0371\3\2\2\2\u037a\u0377\3\2\2\2\u037b\u038a\3\2\2\2\u037c\u037d\f\4"+
		"\2\2\u037d\u037f\7\f\2\2\u037e\u0380\5\u00a4S\2\u037f\u037e\3\2\2\2\u037f"+
		"\u0380\3\2\2\2\u0380\u0381\3\2\2\2\u0381\u0389\5\u00bc_\2\u0382\u0383"+
		"\f\3\2\2\u0383\u0385\7\f\2\2\u0384\u0386\5\u00a4S\2\u0385\u0384\3\2\2"+
		"\2\u0385\u0386\3\2\2\2\u0386\u0387\3\2\2\2\u0387\u0389\5\u00a6T\2\u0388"+
		"\u037c\3\2\2\2\u0388\u0382\3\2\2\2\u0389\u038c\3\2\2\2\u038a\u0388\3\2"+
		"\2\2\u038a\u038b\3\2\2\2\u038b\u00a3\3\2\2\2\u038c\u038a\3\2\2\2\u038d"+
		"\u038f\7\f\2\2\u038e\u038d\3\2\2\2\u038f\u0390\3\2\2\2\u0390\u038e\3\2"+
		"\2\2\u0390\u0391\3\2\2\2\u0391\u00a5\3\2\2\2\u0392\u0393\7\21\2\2\u0393"+
		"\u0394\5\u00bc_\2\u0394\u00a7\3\2\2\2\u0395\u0396\7\t\2\2\u0396\u039f"+
		"\7\n\2\2\u0397\u0398\7\t\2\2\u0398\u039a\5\u00aaV\2\u0399\u039b\7\f\2"+
		"\2\u039a\u0399\3\2\2\2\u039a\u039b\3\2\2\2\u039b\u039c\3\2\2\2\u039c\u039d"+
		"\7\n\2\2\u039d\u039f\3\2\2\2\u039e\u0395\3\2\2\2\u039e\u0397\3\2\2\2\u039f"+
		"\u00a9\3\2\2\2\u03a0\u03a5\5\u00acW\2\u03a1\u03a2\7\f\2\2\u03a2\u03a4"+
		"\5\u00acW\2\u03a3\u03a1\3\2\2\2\u03a4\u03a7\3\2\2\2\u03a5\u03a3\3\2\2"+
		"\2\u03a5\u03a6\3\2\2\2\u03a6\u00ab\3\2\2\2\u03a7\u03a5\3\2\2\2\u03a8\u03b0"+
		"\5\u00ecw\2\u03a9\u03b0\5\u00b4[\2\u03aa\u03ab\5\u00aeX\2\u03ab\u03ac"+
		"\7\20\2\2\u03ac\u03ad\5\u00bc_\2\u03ad\u03b0\3\2\2\2\u03ae\u03b0\5\u0086"+
		"D\2\u03af\u03a8\3\2\2\2\u03af\u03a9\3\2\2\2\u03af\u03aa\3\2\2\2\u03af"+
		"\u03ae\3\2\2\2\u03b0\u00ad\3\2\2\2\u03b1\u03b4\5\u00b0Y\2\u03b2\u03b4"+
		"\5\u00b2Z\2\u03b3\u03b1\3\2\2\2\u03b3\u03b2\3\2\2\2\u03b4\u00af\3\2\2"+
		"\2\u03b5\u03b9\5\u00eav\2\u03b6\u03b9\7o\2\2\u03b7\u03b9\5\u00e6t\2\u03b8"+
		"\u03b5\3\2\2\2\u03b8\u03b6\3\2\2\2\u03b8\u03b7\3\2\2\2\u03b9\u00b1\3\2"+
		"\2\2\u03ba\u03bb\7\5\2\2\u03bb\u03bc\5\u00bc_\2\u03bc\u03bd\7\6\2\2\u03bd"+
		"\u00b3\3\2\2\2\u03be\u03bf\5\u00ecw\2\u03bf\u03c0\5\66\34\2\u03c0\u00b5"+
		"\3\2\2\2\u03c1\u03c3\7\7\2\2\u03c2\u03c4\5\u00b8]\2\u03c3\u03c2\3\2\2"+
		"\2\u03c3\u03c4\3\2\2\2\u03c4\u03c5\3\2\2\2\u03c5\u03c6\7\b\2\2\u03c6\u00b7"+
		"\3\2\2\2\u03c7\u03ca\5\u00a6T\2\u03c8\u03ca\5\u00bc_\2\u03c9\u03c7\3\2"+
		"\2\2\u03c9\u03c8\3\2\2\2\u03ca\u03d2\3\2\2\2\u03cb\u03ce\7\f\2\2\u03cc"+
		"\u03cf\5\u00a6T\2\u03cd\u03cf\5\u00bc_\2\u03ce\u03cc\3\2\2\2\u03ce\u03cd"+
		"\3\2\2\2\u03cf\u03d1\3\2\2\2\u03d0\u03cb\3\2\2\2\u03d1\u03d4\3\2\2\2\u03d2"+
		"\u03d0\3\2\2\2\u03d2\u03d3\3\2\2\2\u03d3\u00b9\3\2\2\2\u03d4\u03d2\3\2"+
		"\2\2\u03d5\u03da\5\u00bc_\2\u03d6\u03d7\7\f\2\2\u03d7\u03d9\5\u00bc_\2"+
		"\u03d8\u03d6\3\2\2\2\u03d9\u03dc\3\2\2\2\u03da\u03d8\3\2\2\2\u03da\u03db"+
		"\3\2\2\2\u03db\u00bb\3\2\2\2\u03dc\u03da\3\2\2\2\u03dd\u03de\b_\1\2\u03de"+
		"\u0411\5l\67\2\u03df\u0411\5\u0094K\2\u03e0\u0411\5\u008eH\2\u03e1\u03e2"+
		"\7C\2\2\u03e2\u03e3\7\22\2\2\u03e3\u0411\7m\2\2\u03e4\u03e5\7C\2\2\u03e5"+
		"\u03e7\5\u00bc_\2\u03e6\u03e8\5\u00b6\\\2\u03e7\u03e6\3\2\2\2\u03e7\u03e8"+
		"\3\2\2\2\u03e8\u0411\3\2\2\2\u03e9\u03ea\7T\2\2\u03ea\u0411\5\u00bc_%"+
		"\u03eb\u03ec\7H\2\2\u03ec\u0411\5\u00bc_$\u03ed\u03ee\7@\2\2\u03ee\u0411"+
		"\5\u00bc_#\u03ef\u03f0\7\23\2\2\u03f0\u0411\5\u00bc_\"\u03f1\u03f2\7\24"+
		"\2\2\u03f2\u0411\5\u00bc_!\u03f3\u03f4\7\25\2\2\u03f4\u0411\5\u00bc_ "+
		"\u03f5\u03f6\7\26\2\2\u03f6\u0411\5\u00bc_\37\u03f7\u03f8\7\27\2\2\u03f8"+
		"\u0411\5\u00bc_\36\u03f9\u03fa\7\30\2\2\u03fa\u0411\5\u00bc_\35\u03fb"+
		"\u0401\7\\\2\2\u03fc\u03fe\6_\16\2\u03fd\u03ff\7\31\2\2\u03fe\u03fd\3"+
		"\2\2\2\u03fe\u03ff\3\2\2\2\u03ff\u0400\3\2\2\2\u0400\u0402\5\u00bc_\2"+
		"\u0401\u03fc\3\2\2\2\u0401\u0402\3\2\2\2\u0402\u0411\3\2\2\2\u0403\u0411"+
		"\5~@\2\u0404\u0411\7O\2\2\u0405\u0411\7[\2\2\u0406\u0411\5\u00e8u\2\u0407"+
		"\u0411\5\u00e4s\2\u0408\u0411\5\u00a0Q\2\u0409\u0411\5\u00a8U\2\u040a"+
		"\u0411\7\3\2\2\u040b\u0411\5\u00fc\177\2\u040c\u040d\7\7\2\2\u040d\u040e"+
		"\5\u00ba^\2\u040e\u040f\7\b\2\2\u040f\u0411\3\2\2\2\u0410\u03dd\3\2\2"+
		"\2\u0410\u03df\3\2\2\2\u0410\u03e0\3\2\2\2\u0410\u03e1\3\2\2\2\u0410\u03e4"+
		"\3\2\2\2\u0410\u03e9\3\2\2\2\u0410\u03eb\3\2\2\2\u0410\u03ed\3\2\2\2\u0410"+
		"\u03ef\3\2\2\2\u0410\u03f1\3\2\2\2\u0410\u03f3\3\2\2\2\u0410\u03f5\3\2"+
		"\2\2\u0410\u03f7\3\2\2\2\u0410\u03f9\3\2\2\2\u0410\u03fb\3\2\2\2\u0410"+
		"\u0403\3\2\2\2\u0410\u0404\3\2\2\2\u0410\u0405\3\2\2\2\u0410\u0406\3\2"+
		"\2\2\u0410\u0407\3\2\2\2\u0410\u0408\3\2\2\2\u0410\u0409\3\2\2\2\u0410"+
		"\u040a\3\2\2\2\u0410\u040b\3\2\2\2\u0410\u040c\3\2\2\2\u0411\u0457\3\2"+
		"\2\2\u0412\u0413\f\34\2\2\u0413\u0414\t\3\2\2\u0414\u0456\5\u00bc_\35"+
		"\u0415\u0416\f\33\2\2\u0416\u0417\t\4\2\2\u0417\u0456\5\u00bc_\34\u0418"+
		"\u0419\f\32\2\2\u0419\u041a\t\5\2\2\u041a\u0456\5\u00bc_\33\u041b\u041c"+
		"\f\31\2\2\u041c\u041d\t\6\2\2\u041d\u0456\5\u00bc_\32\u041e\u041f\f\30"+
		"\2\2\u041f\u0420\7?\2\2\u0420\u0456\5\u00bc_\31\u0421\u0422\f\27\2\2\u0422"+
		"\u0423\7U\2\2\u0423\u0456\5\u00bc_\30\u0424\u0425\f\26\2\2\u0425\u0426"+
		"\t\7\2\2\u0426\u0456\5\u00bc_\27\u0427\u0428\f\25\2\2\u0428\u0429\7\'"+
		"\2\2\u0429\u0456\5\u00bc_\26\u042a\u042b\f\24\2\2\u042b\u042c\7(\2\2\u042c"+
		"\u0456\5\u00bc_\25\u042d\u042e\f\23\2\2\u042e\u042f\7)\2\2\u042f\u0456"+
		"\5\u00bc_\24\u0430\u0431\f\22\2\2\u0431\u0432\7*\2\2\u0432\u0456\5\u00bc"+
		"_\23\u0433\u0434\f\21\2\2\u0434\u0435\7+\2\2\u0435\u0456\5\u00bc_\22\u0436"+
		"\u0437\f\20\2\2\u0437\u0438\7\17\2\2\u0438\u0439\5\u00bc_\2\u0439\u043a"+
		"\7\20\2\2\u043a\u043b\5\u00bc_\21\u043b\u0456\3\2\2\2\u043c\u043d\f\r"+
		"\2\2\u043d\u043e\7\16\2\2\u043e\u0456\5\u00bc_\16\u043f\u0440\f\f\2\2"+
		"\u0440\u0441\5\u00e2r\2\u0441\u0442\5\u00bc_\r\u0442\u0456\3\2\2\2\u0443"+
		"\u0444\f+\2\2\u0444\u0445\7\5\2\2\u0445\u0446\5\u00ba^\2\u0446\u0447\7"+
		"\6\2\2\u0447\u0456\3\2\2\2\u0448\u0449\f*\2\2\u0449\u044a\7\22\2\2\u044a"+
		"\u0456\5\u00eav\2\u044b\u044c\f)\2\2\u044c\u0456\5\u00fc\177\2\u044d\u044e"+
		"\f(\2\2\u044e\u0456\5\u00b6\\\2\u044f\u0450\f\'\2\2\u0450\u0451\6_#\2"+
		"\u0451\u0456\7\23\2\2\u0452\u0453\f&\2\2\u0453\u0454\6_%\2\u0454\u0456"+
		"\7\24\2\2\u0455\u0412\3\2\2\2\u0455\u0415\3\2\2\2\u0455\u0418\3\2\2\2"+
		"\u0455\u041b\3\2\2\2\u0455\u041e\3\2\2\2\u0455\u0421\3\2\2\2\u0455\u0424"+
		"\3\2\2\2\u0455\u0427\3\2\2\2\u0455\u042a\3\2\2\2\u0455\u042d\3\2\2\2\u0455"+
		"\u0430\3\2\2\2\u0455\u0433\3\2\2\2\u0455\u0436\3\2\2\2\u0455\u043c\3\2"+
		"\2\2\u0455\u043f\3\2\2\2\u0455\u0443\3\2\2\2\u0455\u0448\3\2\2\2\u0455"+
		"\u044b\3\2\2\2\u0455\u044d\3\2\2\2\u0455\u044f\3\2\2\2\u0455\u0452\3\2"+
		"\2\2\u0456\u0459\3\2\2\2\u0457\u0455\3\2\2\2\u0457\u0458\3\2\2\2\u0458"+
		"\u00bd\3\2\2\2\u0459\u0457\3\2\2\2\u045a\u045c\5\u00c0a\2\u045b\u045a"+
		"\3\2\2\2\u045b\u045c\3\2\2\2\u045c\u00bf\3\2\2\2\u045d\u045e\5\20\t\2"+
		"\u045e\u00c1\3\2\2\2\u045f\u0461\5\u00c4c\2\u0460\u045f\3\2\2\2\u0461"+
		"\u0464\3\2\2\2\u0462\u0460\3\2\2\2\u0462\u0463\3\2\2\2\u0463\u0465\3\2"+
		"\2\2\u0464\u0462\3\2\2\2\u0465\u0466\7\2\2\3\u0466\u00c3\3\2\2\2\u0467"+
		"\u046b\5\u00c6d\2\u0468\u046b\5\u00dan\2\u0469\u046b\5\22\n\2\u046a\u0467"+
		"\3\2\2\2\u046a\u0468\3\2\2\2\u046a\u0469\3\2\2\2\u046b\u00c5\3\2\2\2\u046c"+
		"\u046d\7]\2\2\u046d\u046e\5\u00c8e\2\u046e\u046f\5\u00d0i\2\u046f\u0470"+
		"\5\u00f8}\2\u0470\u0476\3\2\2\2\u0471\u0472\7]\2\2\u0472\u0473\5\u00d6"+
		"l\2\u0473\u0474\5\u00f8}\2\u0474\u0476\3\2\2\2\u0475\u046c\3\2\2\2\u0475"+
		"\u0471\3\2\2\2\u0476\u00c7\3\2\2\2\u0477\u0483\5\u00caf\2\u0478\u0483"+
		"\5\u00ccg\2\u0479\u0483\5\u00ceh\2\u047a\u047b\5\u00caf\2\u047b\u047c"+
		"\7\f\2\2\u047c\u047d\5\u00ccg\2\u047d\u0483\3\2\2\2\u047e\u047f\5\u00ca"+
		"f\2\u047f\u0480\7\f\2\2\u0480\u0481\5\u00ceh\2\u0481\u0483\3\2\2\2\u0482"+
		"\u0477\3\2\2\2\u0482\u0478\3\2\2\2\u0482\u0479\3\2\2\2\u0482\u047a\3\2"+
		"\2\2\u0482\u047e\3\2\2\2\u0483\u00c9\3\2\2\2\u0484\u0485\5\u00d8m\2\u0485"+
		"\u00cb\3\2\2\2\u0486\u0487\7\31\2\2\u0487\u0488\7k\2\2\u0488\u0489\5\u00d8"+
		"m\2\u0489\u00cd\3\2\2\2\u048a\u048b\7\t\2\2\u048b\u0496\7\n\2\2\u048c"+
		"\u048d\7\t\2\2\u048d\u048e\5\u00d2j\2\u048e\u048f\7\n\2\2\u048f\u0496"+
		"\3\2\2\2\u0490\u0491\7\t\2\2\u0491\u0492\5\u00d2j\2\u0492\u0493\7\f\2"+
		"\2\u0493\u0494\7\n\2\2\u0494\u0496\3\2\2\2\u0495\u048a\3\2\2\2\u0495\u048c"+
		"\3\2\2\2\u0495\u0490\3\2\2\2\u0496\u00cf\3\2\2\2\u0497\u0498\7h\2\2\u0498"+
		"\u0499\5\u00d6l\2\u0499\u00d1\3\2\2\2\u049a\u049f\5\u00d4k\2\u049b\u049c"+
		"\7\f\2\2\u049c\u049e\5\u00d4k\2\u049d\u049b\3\2\2\2\u049e\u04a1\3\2\2"+
		"\2\u049f\u049d\3\2\2\2\u049f\u04a0\3\2\2\2\u04a0\u00d3\3\2\2\2\u04a1\u049f"+
		"\3\2\2\2\u04a2\u04a8\5\u00d8m\2\u04a3\u04a4\5\u00eav\2\u04a4\u04a5\7k"+
		"\2\2\u04a5\u04a6\5\u00d8m\2\u04a6\u04a8\3\2\2\2\u04a7\u04a2\3\2\2\2\u04a7"+
		"\u04a3\3\2\2\2\u04a8\u00d5\3\2\2\2\u04a9\u04aa\7o\2\2\u04aa\u00d7\3\2"+
		"\2\2\u04ab\u04ac\5\u00eex\2\u04ac\u00d9\3\2\2\2\u04ad\u04ae\7W\2\2\u04ae"+
		"\u04af\7\31\2\2\u04af\u04b0\5\u00d0i\2\u04b0\u04b1\5\u00f8}\2\u04b1\u04cc"+
		"\3\2\2\2\u04b2\u04b3\7W\2\2\u04b3\u04b4\5\u00dco\2\u04b4\u04b5\5\u00d0"+
		"i\2\u04b5\u04b6\5\u00f8}\2\u04b6\u04cc\3\2\2\2\u04b7\u04b8\7W\2\2\u04b8"+
		"\u04b9\5\u00dco\2\u04b9\u04ba\5\u00f8}\2\u04ba\u04cc\3\2\2\2\u04bb\u04bc"+
		"\7W\2\2\u04bc\u04cc\5\34\17\2\u04bd\u04be\7W\2\2\u04be\u04cc\5\6\4\2\u04bf"+
		"\u04c0\7W\2\2\u04c0\u04c1\7Q\2\2\u04c1\u04cc\5\b\5\2\u04c2\u04c3\7W\2"+
		"\2\u04c3\u04c4\7Q\2\2\u04c4\u04cc\5\u0092J\2\u04c5\u04c6\7W\2\2\u04c6"+
		"\u04c7\7Q\2\2\u04c7\u04c8\6n&\2\u04c8\u04c9\5\u00bc_\2\u04c9\u04ca\5\u00f8"+
		"}\2\u04ca\u04cc\3\2\2\2\u04cb\u04ad\3\2\2\2\u04cb\u04b2\3\2\2\2\u04cb"+
		"\u04b7\3\2\2\2\u04cb\u04bb\3\2\2\2\u04cb\u04bd\3\2\2\2\u04cb\u04bf\3\2"+
		"\2\2\u04cb\u04c2\3\2\2\2\u04cb\u04c5\3\2\2\2\u04cc\u00db\3\2\2\2\u04cd"+
		"\u04ce\7\t\2\2\u04ce\u04d9\7\n\2\2\u04cf\u04d0\7\t\2\2\u04d0\u04d1\5\u00de"+
		"p\2\u04d1\u04d2\7\n\2\2\u04d2\u04d9\3\2\2\2\u04d3\u04d4\7\t\2\2\u04d4"+
		"\u04d5\5\u00dep\2\u04d5\u04d6\7\f\2\2\u04d6\u04d7\7\n\2\2\u04d7\u04d9"+
		"\3\2\2\2\u04d8\u04cd\3\2\2\2\u04d8\u04cf\3\2\2\2\u04d8\u04d3\3\2\2\2\u04d9"+
		"\u00dd\3\2\2\2\u04da\u04df\5\u00e0q\2\u04db\u04dc\7\f\2\2\u04dc\u04de"+
		"\5\u00e0q\2\u04dd\u04db\3\2\2\2\u04de\u04e1\3\2\2\2\u04df\u04dd\3\2\2"+
		"\2\u04df\u04e0\3\2\2\2\u04e0\u00df\3\2\2\2\u04e1\u04df\3\2\2\2\u04e2\u04e5"+
		"\5\u00eav\2\u04e3\u04e4\7k\2\2\u04e4\u04e6\5\u00eav\2\u04e5\u04e3\3\2"+
		"\2\2\u04e5\u04e6\3\2\2\2\u04e6\u00e1\3\2\2\2\u04e7\u04e8\t\b\2\2\u04e8"+
		"\u00e3\3\2\2\2\u04e9\u04ec\t\t\2\2\u04ea\u04ec\5\u00e6t\2\u04eb\u04e9"+
		"\3\2\2\2\u04eb\u04ea\3\2\2\2\u04ec\u00e5\3\2\2\2\u04ed\u04ee\t\n\2\2\u04ee"+
		"\u00e7\3\2\2\2\u04ef\u04f0\t\13\2\2\u04f0\u00e9\3\2\2\2\u04f1\u04f4\5"+
		"\u00e8u\2\u04f2\u04f4\5\u00f2z\2\u04f3\u04f1\3\2\2\2\u04f3\u04f2\3\2\2"+
		"\2\u04f4\u00eb\3\2\2\2\u04f5\u04f6\5\u00e8u\2\u04f6\u00ed\3\2\2\2\u04f7"+
		"\u04f8\5\u00e8u\2\u04f8\u00ef\3\2\2\2\u04f9\u04fa\5\u00e8u\2\u04fa\u00f1"+
		"\3\2\2\2\u04fb\u04ff\5\u00f4{\2\u04fc\u04ff\5\u00f6|\2\u04fd\u04ff\t\f"+
		"\2\2\u04fe\u04fb\3\2\2\2\u04fe\u04fc\3\2\2\2\u04fe\u04fd\3\2\2\2\u04ff"+
		"\u00f3\3\2\2\2\u0500\u0501\t\r\2\2\u0501\u00f5\3\2\2\2\u0502\u0503\t\16"+
		"\2\2\u0503\u00f7\3\2\2\2\u0504\u0509\7\13\2\2\u0505\u0509\7\2\2\3\u0506"+
		"\u0509\6}\'\2\u0507\u0509\6}(\2\u0508\u0504\3\2\2\2\u0508\u0505\3\2\2"+
		"\2\u0508\u0506\3\2\2\2\u0508\u0507\3\2\2\2\u0509\u00f9\3\2\2\2\u050a\u050b"+
		"\7\2\2\3\u050b\u00fb\3\2\2\2\u050c\u0512\7t\2\2\u050d\u050e\7u\2\2\u050e"+
		"\u050f\5\u00ba^\2\u050f\u0510\5\u00fe\u0080\2\u0510\u0512\3\2\2\2\u0511"+
		"\u050c\3\2\2\2\u0511\u050d\3\2\2\2\u0512\u00fd\3\2\2\2\u0513\u0518\7w"+
		"\2\2\u0514\u0515\5\u0100\u0081\2\u0515\u0516\7w\2\2\u0516\u0518\3\2\2"+
		"\2\u0517\u0513\3\2\2\2\u0517\u0514\3\2\2\2\u0518\u00ff\3\2\2\2\u0519\u051a"+
		"\b\u0081\1\2\u051a\u051b\7v\2\2\u051b\u051c\5\u00ba^\2\u051c\u0522\3\2"+
		"\2\2\u051d\u051e\f\3\2\2\u051e\u051f\7v\2\2\u051f\u0521\5\u00ba^\2\u0520"+
		"\u051d\3\2\2\2\u0521\u0524\3\2\2\2\u0522\u0520\3\2\2\2\u0522\u0523\3\2"+
		"\2\2\u0523\u0101\3\2\2\2\u0524\u0522\3\2\2\2r\u0112\u0117\u011b\u011f"+
		"\u0125\u012c\u0130\u013d\u0142\u0147\u0151\u0156\u015b\u015f\u016c\u0170"+
		"\u0173\u017e\u0181\u0185\u018c\u0194\u0198\u01a1\u01a6\u01a8\u01ac\u01c1"+
		"\u01d5\u01d9\u01dd\u01e6\u01ea\u01f3\u01f7\u022e\u023b\u0240\u0247\u024e"+
		"\u0260\u0264\u0266\u026d\u0273\u0278\u0280\u0294\u02a1\u02b8\u02be\u02c4"+
		"\u02c6\u02cd\u02d7\u02e0\u02e8\u02fa\u0316\u0330\u0341\u0346\u034a\u0356"+
		"\u035c\u0360\u036b\u036f\u0373\u0377\u037a\u037f\u0385\u0388\u038a\u0390"+
		"\u039a\u039e\u03a5\u03af\u03b3\u03b8\u03c3\u03c9\u03ce\u03d2\u03da\u03e7"+
		"\u03fe\u0401\u0410\u0455\u0457\u045b\u0462\u046a\u0475\u0482\u0495\u049f"+
		"\u04a7\u04cb\u04d8\u04df\u04e5\u04eb\u04f3\u04fe\u0508\u0511\u0517\u0522";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}