// Generated from /Users/cwilliams/repos/studio3/plugins/com.aptana.js.core/parsing/JS.g4 by ANTLR 4.7
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
		T__0=1, T__1=2, T__2=3, T__3=4, RegularExpressionLiteral=5, LineTerminator=6, 
		OpenBracket=7, CloseBracket=8, OpenParen=9, CloseParen=10, OpenBrace=11, 
		CloseBrace=12, SemiColon=13, Comma=14, Arrow=15, Assign=16, QuestionMark=17, 
		Colon=18, Dot=19, PlusPlus=20, MinusMinus=21, Plus=22, Minus=23, BitNot=24, 
		Not=25, Multiply=26, Divide=27, Modulus=28, RightShiftArithmetic=29, LeftShiftArithmetic=30, 
		RightShiftLogical=31, LessThan=32, MoreThan=33, LessThanEquals=34, GreaterThanEquals=35, 
		Equals=36, NotEquals=37, IdentityEquals=38, IdentityNotEquals=39, BitAnd=40, 
		BitXOr=41, BitOr=42, And=43, Or=44, MultiplyAssign=45, DivideAssign=46, 
		ModulusAssign=47, PlusAssign=48, MinusAssign=49, LeftShiftArithmeticAssign=50, 
		RightShiftArithmeticAssign=51, RightShiftLogicalAssign=52, BitAndAssign=53, 
		BitXorAssign=54, BitOrAssign=55, NullLiteral=56, BooleanLiteral=57, DecimalLiteral=58, 
		HexIntegerLiteral=59, OctalIntegerLiteral=60, BinaryIntegerLiteral=61, 
		Break=62, Do=63, Instanceof=64, Typeof=65, Case=66, Else=67, New=68, Var=69, 
		Catch=70, Finally=71, Return=72, Void=73, Continue=74, For=75, Switch=76, 
		While=77, Debugger=78, Function=79, This=80, With=81, Default=82, If=83, 
		Throw=84, Delete=85, In=86, Try=87, Export=88, Class=89, Extends=90, Const=91, 
		Super=92, Yield=93, Import=94, Static=95, Let=96, Enum=97, Await=98, Implements=99, 
		Private=100, Public=101, Interface=102, Package=103, Protected=104, Identifier=105, 
		StringLiteral=106, WhiteSpaces=107, MultiLineComment=108, SingleLineComment=109, 
		UnexpectedCharacter=110, NoSubstitutionTemplate=111, TemplateHead=112, 
		TemplateMiddle=113, TemplateTail=114;
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
		RULE_forDeclaration = 31, RULE_forBinding = 32, RULE_continueStatement = 33, 
		RULE_breakStatement = 34, RULE_returnStatement = 35, RULE_withStatement = 36, 
		RULE_switchStatement = 37, RULE_caseBlock = 38, RULE_caseClauses = 39, 
		RULE_caseClause = 40, RULE_defaultClause = 41, RULE_labelledStatement = 42, 
		RULE_labelledItem = 43, RULE_throwStatement = 44, RULE_tryStatement = 45, 
		RULE_catchProduction = 46, RULE_finallyProduction = 47, RULE_catchParameter = 48, 
		RULE_debuggerStatement = 49, RULE_functionDeclaration = 50, RULE_strictFormalParameters = 51, 
		RULE_formalParameters = 52, RULE_formalParameterList = 53, RULE_formalsList = 54, 
		RULE_functionRestParameter = 55, RULE_formalParameter = 56, RULE_functionBody = 57, 
		RULE_functionStatementList = 58, RULE_arrowFunction = 59, RULE_arrowParameters = 60, 
		RULE_conciseBody = 61, RULE_coverParenthesizedExpressionAndArrowParameterList = 62, 
		RULE_methodDefinition = 63, RULE_propertySetParameterList = 64, RULE_generatorMethod = 65, 
		RULE_generatorDeclaration = 66, RULE_generatorExpression = 67, RULE_generatorBody = 68, 
		RULE_classDeclaration = 69, RULE_classExpression = 70, RULE_classTail = 71, 
		RULE_classHeritage = 72, RULE_classBody = 73, RULE_classElementList = 74, 
		RULE_classElement = 75, RULE_arrayLiteral = 76, RULE_elementList = 77, 
		RULE_elision = 78, RULE_spreadElement = 79, RULE_objectLiteral = 80, RULE_propertyDefinitionList = 81, 
		RULE_propertyDefinition = 82, RULE_propertyName = 83, RULE_literalPropertyName = 84, 
		RULE_computedPropertyName = 85, RULE_coverInitializedName = 86, RULE_arguments = 87, 
		RULE_argumentList = 88, RULE_expressionSequence = 89, RULE_singleExpression = 90, 
		RULE_script = 91, RULE_scriptBody = 92, RULE_module = 93, RULE_moduleBody = 94, 
		RULE_moduleItemList = 95, RULE_moduleItem = 96, RULE_importDeclaration = 97, 
		RULE_importClause = 98, RULE_importedDefaultBinding = 99, RULE_nameSpaceImport = 100, 
		RULE_namedImports = 101, RULE_fromClause = 102, RULE_importsList = 103, 
		RULE_importSpecifier = 104, RULE_moduleSpecifier = 105, RULE_importedBinding = 106, 
		RULE_exportDeclaration = 107, RULE_exportClause = 108, RULE_exportsList = 109, 
		RULE_exportSpecifier = 110, RULE_assignmentOperator = 111, RULE_literal = 112, 
		RULE_numericLiteral = 113, RULE_identifierName = 114, RULE_identifierReference = 115, 
		RULE_bindingIdentifier = 116, RULE_labelIdentifier = 117, RULE_reservedWord = 118, 
		RULE_keyword = 119, RULE_futureReservedWord = 120, RULE_getter = 121, 
		RULE_setter = 122, RULE_eos = 123, RULE_eof = 124, RULE_templateLiteral = 125, 
		RULE_templateSpans = 126, RULE_templateMiddleList = 127;
	public static final String[] ruleNames = {
		"program", "statement", "declaration", "hoistableDeclaration", "breakableStatement", 
		"blockStatement", "block", "statementList", "statementListItem", "lexicalDeclaration", 
		"letOrConst", "bindingList", "lexicalBinding", "variableStatement", "variableDeclarationList", 
		"variableDeclaration", "bindingPattern", "objectBindingPattern", "arrayBindingPattern", 
		"bindingPropertyList", "bindingElementList", "bindingElisionElement", 
		"bindingProperty", "bindingElement", "singleNameBinding", "bindingRestElement", 
		"initializer", "emptyStatement", "expressionStatement", "ifStatement", 
		"iterationStatement", "forDeclaration", "forBinding", "continueStatement", 
		"breakStatement", "returnStatement", "withStatement", "switchStatement", 
		"caseBlock", "caseClauses", "caseClause", "defaultClause", "labelledStatement", 
		"labelledItem", "throwStatement", "tryStatement", "catchProduction", "finallyProduction", 
		"catchParameter", "debuggerStatement", "functionDeclaration", "strictFormalParameters", 
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
		"moduleBody", "moduleItemList", "moduleItem", "importDeclaration", "importClause", 
		"importedDefaultBinding", "nameSpaceImport", "namedImports", "fromClause", 
		"importsList", "importSpecifier", "moduleSpecifier", "importedBinding", 
		"exportDeclaration", "exportClause", "exportsList", "exportSpecifier", 
		"assignmentOperator", "literal", "numericLiteral", "identifierName", "identifierReference", 
		"bindingIdentifier", "labelIdentifier", "reservedWord", "keyword", "futureReservedWord", 
		"getter", "setter", "eos", "eof", "templateLiteral", "templateSpans", 
		"templateMiddleList"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'...'", "'of'", "'as'", "'from'", null, null, "'['", "']'", "'('", 
		"')'", "'{'", "'}'", "';'", "','", "'=>'", "'='", "'?'", "':'", "'.'", 
		"'++'", "'--'", "'+'", "'-'", "'~'", "'!'", "'*'", "'/'", "'%'", "'>>'", 
		"'<<'", "'>>>'", "'<'", "'>'", "'<='", "'>='", "'=='", "'!='", "'==='", 
		"'!=='", "'&'", "'^'", "'|'", "'&&'", "'||'", "'*='", "'/='", "'%='", 
		"'+='", "'-='", "'<<='", "'>>='", "'>>>='", "'&='", "'^='", "'|='", "'null'", 
		null, null, null, null, null, "'break'", "'do'", "'instanceof'", "'typeof'", 
		"'case'", "'else'", "'new'", "'var'", "'catch'", "'finally'", "'return'", 
		"'void'", "'continue'", "'for'", "'switch'", "'while'", "'debugger'", 
		"'function'", "'this'", "'with'", "'default'", "'if'", "'throw'", "'delete'", 
		"'in'", "'try'", "'export'", "'class'", "'extends'", "'const'", "'super'", 
		"'yield'", "'import'", "'static'", "'let'", "'enum'", "'await'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, "RegularExpressionLiteral", "LineTerminator", 
		"OpenBracket", "CloseBracket", "OpenParen", "CloseParen", "OpenBrace", 
		"CloseBrace", "SemiColon", "Comma", "Arrow", "Assign", "QuestionMark", 
		"Colon", "Dot", "PlusPlus", "MinusMinus", "Plus", "Minus", "BitNot", "Not", 
		"Multiply", "Divide", "Modulus", "RightShiftArithmetic", "LeftShiftArithmetic", 
		"RightShiftLogical", "LessThan", "MoreThan", "LessThanEquals", "GreaterThanEquals", 
		"Equals", "NotEquals", "IdentityEquals", "IdentityNotEquals", "BitAnd", 
		"BitXOr", "BitOr", "And", "Or", "MultiplyAssign", "DivideAssign", "ModulusAssign", 
		"PlusAssign", "MinusAssign", "LeftShiftArithmeticAssign", "RightShiftArithmeticAssign", 
		"RightShiftLogicalAssign", "BitAndAssign", "BitXorAssign", "BitOrAssign", 
		"NullLiteral", "BooleanLiteral", "DecimalLiteral", "HexIntegerLiteral", 
		"OctalIntegerLiteral", "BinaryIntegerLiteral", "Break", "Do", "Instanceof", 
		"Typeof", "Case", "Else", "New", "Var", "Catch", "Finally", "Return", 
		"Void", "Continue", "For", "Switch", "While", "Debugger", "Function", 
		"This", "With", "Default", "If", "Throw", "Delete", "In", "Try", "Export", 
		"Class", "Extends", "Const", "Super", "Yield", "Import", "Static", "Let", 
		"Enum", "Await", "Implements", "Private", "Public", "Interface", "Package", 
		"Protected", "Identifier", "StringLiteral", "WhiteSpaces", "MultiLineComment", 
		"SingleLineComment", "UnexpectedCharacter", "NoSubstitutionTemplate", 
		"TemplateHead", "TemplateMiddle", "TemplateTail"
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
	public String getGrammarFileName() { return "JS.g4"; }

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

	        // Get the token ahead of the current index.
	        int possibleIndexEosToken = this.getCurrentToken().getTokenIndex() - 1;
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

	        // Get the token ahead of the current index.
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
		public TerminalNode EOF() { return getToken(JSParser.EOF, 0); }
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterProgram(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitProgram(this);
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
			setState(257);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitStatement(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_statement);
		try {
			setState(273);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(259);
				blockStatement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(260);
				variableStatement();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(261);
				emptyStatement();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(262);
				expressionStatement();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(263);
				ifStatement();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(264);
				breakableStatement();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(265);
				continueStatement();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(266);
				breakStatement();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(267);
				returnStatement();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(268);
				withStatement();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(269);
				labelledStatement();
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(270);
				throwStatement();
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(271);
				tryStatement();
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(272);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitDeclaration(this);
		}
	}

	public final DeclarationContext declaration() throws RecognitionException {
		DeclarationContext _localctx = new DeclarationContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_declaration);
		try {
			setState(278);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Function:
				enterOuterAlt(_localctx, 1);
				{
				setState(275);
				hoistableDeclaration();
				}
				break;
			case Class:
				enterOuterAlt(_localctx, 2);
				{
				setState(276);
				classDeclaration();
				}
				break;
			case Const:
			case Let:
				enterOuterAlt(_localctx, 3);
				{
				setState(277);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterHoistableDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitHoistableDeclaration(this);
		}
	}

	public final HoistableDeclarationContext hoistableDeclaration() throws RecognitionException {
		HoistableDeclarationContext _localctx = new HoistableDeclarationContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_hoistableDeclaration);
		try {
			setState(282);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(280);
				functionDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(281);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterBreakableStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitBreakableStatement(this);
		}
	}

	public final BreakableStatementContext breakableStatement() throws RecognitionException {
		BreakableStatementContext _localctx = new BreakableStatementContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_breakableStatement);
		try {
			setState(286);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Do:
			case For:
			case While:
				enterOuterAlt(_localctx, 1);
				{
				setState(284);
				iterationStatement();
				}
				break;
			case Switch:
				enterOuterAlt(_localctx, 2);
				{
				setState(285);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterBlockStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitBlockStatement(this);
		}
	}

	public final BlockStatementContext blockStatement() throws RecognitionException {
		BlockStatementContext _localctx = new BlockStatementContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_blockStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(288);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitBlock(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_block);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(290);
			match(OpenBrace);
			setState(292);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				{
				setState(291);
				statementList();
				}
				break;
			}
			setState(294);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterStatementList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitStatementList(this);
		}
	}

	public final StatementListContext statementList() throws RecognitionException {
		StatementListContext _localctx = new StatementListContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_statementList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(297); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(296);
					statementListItem();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(299); 
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterStatementListItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitStatementListItem(this);
		}
	}

	public final StatementListItemContext statementListItem() throws RecognitionException {
		StatementListItemContext _localctx = new StatementListItemContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_statementListItem);
		try {
			setState(303);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(301);
				statement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(302);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterLexicalDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitLexicalDeclaration(this);
		}
	}

	public final LexicalDeclarationContext lexicalDeclaration() throws RecognitionException {
		LexicalDeclarationContext _localctx = new LexicalDeclarationContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_lexicalDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(305);
			letOrConst();
			setState(306);
			bindingList();
			setState(307);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterLetOrConst(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitLetOrConst(this);
		}
	}

	public final LetOrConstContext letOrConst() throws RecognitionException {
		LetOrConstContext _localctx = new LetOrConstContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_letOrConst);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(309);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterBindingList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitBindingList(this);
		}
	}

	public final BindingListContext bindingList() throws RecognitionException {
		BindingListContext _localctx = new BindingListContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_bindingList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(311);
			lexicalBinding();
			setState(316);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(312);
					match(Comma);
					setState(313);
					lexicalBinding();
					}
					} 
				}
				setState(318);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterLexicalBinding(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitLexicalBinding(this);
		}
	}

	public final LexicalBindingContext lexicalBinding() throws RecognitionException {
		LexicalBindingContext _localctx = new LexicalBindingContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_lexicalBinding);
		try {
			setState(326);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(319);
				bindingIdentifier();
				setState(321);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
				case 1:
					{
					setState(320);
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
				setState(323);
				bindingPattern();
				setState(324);
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
		public TerminalNode Var() { return getToken(JSParser.Var, 0); }
		public VariableDeclarationListContext variableDeclarationList() {
			return getRuleContext(VariableDeclarationListContext.class,0);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterVariableStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitVariableStatement(this);
		}
	}

	public final VariableStatementContext variableStatement() throws RecognitionException {
		VariableStatementContext _localctx = new VariableStatementContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_variableStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(328);
			match(Var);
			setState(329);
			variableDeclarationList();
			setState(330);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterVariableDeclarationList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitVariableDeclarationList(this);
		}
	}

	public final VariableDeclarationListContext variableDeclarationList() throws RecognitionException {
		VariableDeclarationListContext _localctx = new VariableDeclarationListContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_variableDeclarationList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(332);
			variableDeclaration();
			setState(337);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(333);
					match(Comma);
					setState(334);
					variableDeclaration();
					}
					} 
				}
				setState(339);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterVariableDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitVariableDeclaration(this);
		}
	}

	public final VariableDeclarationContext variableDeclaration() throws RecognitionException {
		VariableDeclarationContext _localctx = new VariableDeclarationContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_variableDeclaration);
		try {
			setState(347);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(340);
				bindingIdentifier();
				setState(342);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
				case 1:
					{
					setState(341);
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
				setState(344);
				bindingPattern();
				setState(345);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterBindingPattern(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitBindingPattern(this);
		}
	}

	public final BindingPatternContext bindingPattern() throws RecognitionException {
		BindingPatternContext _localctx = new BindingPatternContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_bindingPattern);
		try {
			setState(351);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case OpenBrace:
				enterOuterAlt(_localctx, 1);
				{
				setState(349);
				objectBindingPattern();
				}
				break;
			case OpenBracket:
				enterOuterAlt(_localctx, 2);
				{
				setState(350);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterObjectBindingPattern(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitObjectBindingPattern(this);
		}
	}

	public final ObjectBindingPatternContext objectBindingPattern() throws RecognitionException {
		ObjectBindingPatternContext _localctx = new ObjectBindingPatternContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_objectBindingPattern);
		try {
			setState(364);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(353);
				match(OpenBrace);
				setState(354);
				match(CloseBrace);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(355);
				match(OpenBrace);
				setState(356);
				bindingPropertyList();
				setState(357);
				match(CloseBrace);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(359);
				match(OpenBrace);
				setState(360);
				bindingPropertyList();
				setState(361);
				match(Comma);
				setState(362);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterArrayBindingPattern(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitArrayBindingPattern(this);
		}
	}

	public final ArrayBindingPatternContext arrayBindingPattern() throws RecognitionException {
		ArrayBindingPatternContext _localctx = new ArrayBindingPatternContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_arrayBindingPattern);
		int _la;
		try {
			setState(389);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(366);
				match(OpenBracket);
				setState(368);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Comma) {
					{
					setState(367);
					elision();
					}
				}

				setState(371);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__0) {
					{
					setState(370);
					bindingRestElement();
					}
				}

				setState(373);
				match(CloseBracket);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(374);
				match(OpenBracket);
				setState(375);
				bindingElementList();
				setState(376);
				match(CloseBracket);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(378);
				match(OpenBracket);
				setState(379);
				bindingElementList();
				setState(380);
				match(Comma);
				setState(382);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Comma) {
					{
					setState(381);
					elision();
					}
				}

				setState(385);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__0) {
					{
					setState(384);
					bindingRestElement();
					}
				}

				setState(387);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterBindingPropertyList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitBindingPropertyList(this);
		}
	}

	public final BindingPropertyListContext bindingPropertyList() throws RecognitionException {
		BindingPropertyListContext _localctx = new BindingPropertyListContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_bindingPropertyList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(391);
			bindingProperty();
			setState(396);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(392);
					match(Comma);
					setState(393);
					bindingProperty();
					}
					} 
				}
				setState(398);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterBindingElementList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitBindingElementList(this);
		}
	}

	public final BindingElementListContext bindingElementList() throws RecognitionException {
		BindingElementListContext _localctx = new BindingElementListContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_bindingElementList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(399);
			bindingElisionElement();
			setState(404);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(400);
					match(Comma);
					setState(401);
					bindingElisionElement();
					}
					} 
				}
				setState(406);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterBindingElisionElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitBindingElisionElement(this);
		}
	}

	public final BindingElisionElementContext bindingElisionElement() throws RecognitionException {
		BindingElisionElementContext _localctx = new BindingElisionElementContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_bindingElisionElement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(408);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Comma) {
				{
				setState(407);
				elision();
				}
			}

			setState(410);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterBindingProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitBindingProperty(this);
		}
	}

	public final BindingPropertyContext bindingProperty() throws RecognitionException {
		BindingPropertyContext _localctx = new BindingPropertyContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_bindingProperty);
		try {
			setState(417);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(412);
				singleNameBinding();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(413);
				propertyName();
				setState(414);
				match(Colon);
				setState(415);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterBindingElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitBindingElement(this);
		}
	}

	public final BindingElementContext bindingElement() throws RecognitionException {
		BindingElementContext _localctx = new BindingElementContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_bindingElement);
		int _la;
		try {
			setState(424);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(419);
				singleNameBinding();
				}
				break;
			case OpenBracket:
			case OpenBrace:
				enterOuterAlt(_localctx, 2);
				{
				setState(420);
				bindingPattern();
				setState(422);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Assign) {
					{
					setState(421);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterSingleNameBinding(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitSingleNameBinding(this);
		}
	}

	public final SingleNameBindingContext singleNameBinding() throws RecognitionException {
		SingleNameBindingContext _localctx = new SingleNameBindingContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_singleNameBinding);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(426);
			bindingIdentifier();
			setState(428);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Assign) {
				{
				setState(427);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterBindingRestElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitBindingRestElement(this);
		}
	}

	public final BindingRestElementContext bindingRestElement() throws RecognitionException {
		BindingRestElementContext _localctx = new BindingRestElementContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_bindingRestElement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(430);
			match(T__0);
			setState(431);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterInitializer(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitInitializer(this);
		}
	}

	public final InitializerContext initializer() throws RecognitionException {
		InitializerContext _localctx = new InitializerContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_initializer);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(433);
			match(Assign);
			setState(434);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterEmptyStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitEmptyStatement(this);
		}
	}

	public final EmptyStatementContext emptyStatement() throws RecognitionException {
		EmptyStatementContext _localctx = new EmptyStatementContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_emptyStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(436);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterExpressionStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitExpressionStatement(this);
		}
	}

	public final ExpressionStatementContext expressionStatement() throws RecognitionException {
		ExpressionStatementContext _localctx = new ExpressionStatementContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_expressionStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(438);
			if (!((_input.LA(1) != OpenBrace) && (_input.LA(1) != Function) && (_input.LA(1) != Class) && ((_input.LA(1) == Let) ? _input.LA(2) != OpenBracket : true))) throw new FailedPredicateException(this, "(_input.LA(1) != OpenBrace) && (_input.LA(1) != Function) && (_input.LA(1) != Class) && ((_input.LA(1) == Let) ? _input.LA(2) != OpenBracket : true)");
			setState(439);
			expressionSequence();
			setState(440);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterIfStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitIfStatement(this);
		}
	}

	public final IfStatementContext ifStatement() throws RecognitionException {
		IfStatementContext _localctx = new IfStatementContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_ifStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(442);
			match(If);
			setState(443);
			match(OpenParen);
			setState(444);
			expressionSequence();
			setState(445);
			match(CloseParen);
			setState(446);
			statement();
			setState(449);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
			case 1:
				{
				setState(447);
				match(Else);
				setState(448);
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
		public TerminalNode Do() { return getToken(JSParser.Do, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public TerminalNode While() { return getToken(JSParser.While, 0); }
		public List<ExpressionSequenceContext> expressionSequence() {
			return getRuleContexts(ExpressionSequenceContext.class);
		}
		public ExpressionSequenceContext expressionSequence(int i) {
			return getRuleContext(ExpressionSequenceContext.class,i);
		}
		public EosContext eos() {
			return getRuleContext(EosContext.class,0);
		}
		public TerminalNode For() { return getToken(JSParser.For, 0); }
		public TerminalNode Var() { return getToken(JSParser.Var, 0); }
		public VariableDeclarationListContext variableDeclarationList() {
			return getRuleContext(VariableDeclarationListContext.class,0);
		}
		public LexicalDeclarationContext lexicalDeclaration() {
			return getRuleContext(LexicalDeclarationContext.class,0);
		}
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public TerminalNode In() { return getToken(JSParser.In, 0); }
		public ForBindingContext forBinding() {
			return getRuleContext(ForBindingContext.class,0);
		}
		public ForDeclarationContext forDeclaration() {
			return getRuleContext(ForDeclarationContext.class,0);
		}
		public IterationStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_iterationStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterIterationStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitIterationStatement(this);
		}
	}

	public final IterationStatementContext iterationStatement() throws RecognitionException {
		IterationStatementContext _localctx = new IterationStatementContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_iterationStatement);
		int _la;
		try {
			setState(561);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,35,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(451);
				match(Do);
				setState(452);
				statement();
				setState(453);
				match(While);
				setState(454);
				match(OpenParen);
				setState(455);
				expressionSequence();
				setState(456);
				match(CloseParen);
				setState(457);
				eos();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(459);
				match(While);
				setState(460);
				match(OpenParen);
				setState(461);
				expressionSequence();
				setState(462);
				match(CloseParen);
				setState(463);
				statement();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(465);
				match(For);
				setState(466);
				match(OpenParen);
				setState(467);
				if (!(((_input.LA(1) == Let) ? _input.LA(2) != OpenBracket : true))) throw new FailedPredicateException(this, "((_input.LA(1) == Let) ? _input.LA(2) != OpenBracket : true)");
				setState(469);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RegularExpressionLiteral) | (1L << OpenBracket) | (1L << OpenParen) | (1L << OpenBrace) | (1L << PlusPlus) | (1L << MinusMinus) | (1L << Plus) | (1L << Minus) | (1L << BitNot) | (1L << Not) | (1L << NullLiteral) | (1L << BooleanLiteral) | (1L << DecimalLiteral) | (1L << HexIntegerLiteral) | (1L << OctalIntegerLiteral) | (1L << BinaryIntegerLiteral))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (Typeof - 65)) | (1L << (New - 65)) | (1L << (Void - 65)) | (1L << (Function - 65)) | (1L << (This - 65)) | (1L << (Delete - 65)) | (1L << (Class - 65)) | (1L << (Super - 65)) | (1L << (Yield - 65)) | (1L << (Identifier - 65)) | (1L << (StringLiteral - 65)) | (1L << (NoSubstitutionTemplate - 65)) | (1L << (TemplateHead - 65)))) != 0)) {
					{
					setState(468);
					expressionSequence();
					}
				}

				setState(471);
				match(SemiColon);
				setState(473);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RegularExpressionLiteral) | (1L << OpenBracket) | (1L << OpenParen) | (1L << OpenBrace) | (1L << PlusPlus) | (1L << MinusMinus) | (1L << Plus) | (1L << Minus) | (1L << BitNot) | (1L << Not) | (1L << NullLiteral) | (1L << BooleanLiteral) | (1L << DecimalLiteral) | (1L << HexIntegerLiteral) | (1L << OctalIntegerLiteral) | (1L << BinaryIntegerLiteral))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (Typeof - 65)) | (1L << (New - 65)) | (1L << (Void - 65)) | (1L << (Function - 65)) | (1L << (This - 65)) | (1L << (Delete - 65)) | (1L << (Class - 65)) | (1L << (Super - 65)) | (1L << (Yield - 65)) | (1L << (Identifier - 65)) | (1L << (StringLiteral - 65)) | (1L << (NoSubstitutionTemplate - 65)) | (1L << (TemplateHead - 65)))) != 0)) {
					{
					setState(472);
					expressionSequence();
					}
				}

				setState(475);
				match(SemiColon);
				setState(477);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RegularExpressionLiteral) | (1L << OpenBracket) | (1L << OpenParen) | (1L << OpenBrace) | (1L << PlusPlus) | (1L << MinusMinus) | (1L << Plus) | (1L << Minus) | (1L << BitNot) | (1L << Not) | (1L << NullLiteral) | (1L << BooleanLiteral) | (1L << DecimalLiteral) | (1L << HexIntegerLiteral) | (1L << OctalIntegerLiteral) | (1L << BinaryIntegerLiteral))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (Typeof - 65)) | (1L << (New - 65)) | (1L << (Void - 65)) | (1L << (Function - 65)) | (1L << (This - 65)) | (1L << (Delete - 65)) | (1L << (Class - 65)) | (1L << (Super - 65)) | (1L << (Yield - 65)) | (1L << (Identifier - 65)) | (1L << (StringLiteral - 65)) | (1L << (NoSubstitutionTemplate - 65)) | (1L << (TemplateHead - 65)))) != 0)) {
					{
					setState(476);
					expressionSequence();
					}
				}

				setState(479);
				match(CloseParen);
				setState(480);
				statement();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(481);
				match(For);
				setState(482);
				match(OpenParen);
				setState(483);
				match(Var);
				setState(484);
				variableDeclarationList();
				setState(485);
				match(SemiColon);
				setState(487);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RegularExpressionLiteral) | (1L << OpenBracket) | (1L << OpenParen) | (1L << OpenBrace) | (1L << PlusPlus) | (1L << MinusMinus) | (1L << Plus) | (1L << Minus) | (1L << BitNot) | (1L << Not) | (1L << NullLiteral) | (1L << BooleanLiteral) | (1L << DecimalLiteral) | (1L << HexIntegerLiteral) | (1L << OctalIntegerLiteral) | (1L << BinaryIntegerLiteral))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (Typeof - 65)) | (1L << (New - 65)) | (1L << (Void - 65)) | (1L << (Function - 65)) | (1L << (This - 65)) | (1L << (Delete - 65)) | (1L << (Class - 65)) | (1L << (Super - 65)) | (1L << (Yield - 65)) | (1L << (Identifier - 65)) | (1L << (StringLiteral - 65)) | (1L << (NoSubstitutionTemplate - 65)) | (1L << (TemplateHead - 65)))) != 0)) {
					{
					setState(486);
					expressionSequence();
					}
				}

				setState(489);
				match(SemiColon);
				setState(491);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RegularExpressionLiteral) | (1L << OpenBracket) | (1L << OpenParen) | (1L << OpenBrace) | (1L << PlusPlus) | (1L << MinusMinus) | (1L << Plus) | (1L << Minus) | (1L << BitNot) | (1L << Not) | (1L << NullLiteral) | (1L << BooleanLiteral) | (1L << DecimalLiteral) | (1L << HexIntegerLiteral) | (1L << OctalIntegerLiteral) | (1L << BinaryIntegerLiteral))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (Typeof - 65)) | (1L << (New - 65)) | (1L << (Void - 65)) | (1L << (Function - 65)) | (1L << (This - 65)) | (1L << (Delete - 65)) | (1L << (Class - 65)) | (1L << (Super - 65)) | (1L << (Yield - 65)) | (1L << (Identifier - 65)) | (1L << (StringLiteral - 65)) | (1L << (NoSubstitutionTemplate - 65)) | (1L << (TemplateHead - 65)))) != 0)) {
					{
					setState(490);
					expressionSequence();
					}
				}

				setState(493);
				match(CloseParen);
				setState(494);
				statement();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(496);
				match(For);
				setState(497);
				match(OpenParen);
				setState(498);
				lexicalDeclaration();
				setState(500);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RegularExpressionLiteral) | (1L << OpenBracket) | (1L << OpenParen) | (1L << OpenBrace) | (1L << PlusPlus) | (1L << MinusMinus) | (1L << Plus) | (1L << Minus) | (1L << BitNot) | (1L << Not) | (1L << NullLiteral) | (1L << BooleanLiteral) | (1L << DecimalLiteral) | (1L << HexIntegerLiteral) | (1L << OctalIntegerLiteral) | (1L << BinaryIntegerLiteral))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (Typeof - 65)) | (1L << (New - 65)) | (1L << (Void - 65)) | (1L << (Function - 65)) | (1L << (This - 65)) | (1L << (Delete - 65)) | (1L << (Class - 65)) | (1L << (Super - 65)) | (1L << (Yield - 65)) | (1L << (Identifier - 65)) | (1L << (StringLiteral - 65)) | (1L << (NoSubstitutionTemplate - 65)) | (1L << (TemplateHead - 65)))) != 0)) {
					{
					setState(499);
					expressionSequence();
					}
				}

				setState(502);
				match(SemiColon);
				setState(504);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RegularExpressionLiteral) | (1L << OpenBracket) | (1L << OpenParen) | (1L << OpenBrace) | (1L << PlusPlus) | (1L << MinusMinus) | (1L << Plus) | (1L << Minus) | (1L << BitNot) | (1L << Not) | (1L << NullLiteral) | (1L << BooleanLiteral) | (1L << DecimalLiteral) | (1L << HexIntegerLiteral) | (1L << OctalIntegerLiteral) | (1L << BinaryIntegerLiteral))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (Typeof - 65)) | (1L << (New - 65)) | (1L << (Void - 65)) | (1L << (Function - 65)) | (1L << (This - 65)) | (1L << (Delete - 65)) | (1L << (Class - 65)) | (1L << (Super - 65)) | (1L << (Yield - 65)) | (1L << (Identifier - 65)) | (1L << (StringLiteral - 65)) | (1L << (NoSubstitutionTemplate - 65)) | (1L << (TemplateHead - 65)))) != 0)) {
					{
					setState(503);
					expressionSequence();
					}
				}

				setState(506);
				match(CloseParen);
				setState(507);
				statement();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(509);
				match(For);
				setState(510);
				match(OpenParen);
				setState(511);
				if (!(((_input.LA(1) == Let) ? _input.LA(2) != OpenBracket : true))) throw new FailedPredicateException(this, "((_input.LA(1) == Let) ? _input.LA(2) != OpenBracket : true)");
				setState(512);
				singleExpression(0);
				setState(513);
				match(In);
				setState(514);
				expressionSequence();
				setState(515);
				match(CloseParen);
				setState(516);
				statement();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(518);
				match(For);
				setState(519);
				match(OpenParen);
				setState(520);
				match(Var);
				setState(521);
				forBinding();
				setState(522);
				match(In);
				setState(523);
				expressionSequence();
				setState(524);
				match(CloseParen);
				setState(525);
				statement();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(527);
				match(For);
				setState(528);
				match(OpenParen);
				setState(529);
				forDeclaration();
				setState(530);
				match(In);
				setState(531);
				expressionSequence();
				setState(532);
				match(CloseParen);
				setState(533);
				statement();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(535);
				match(For);
				setState(536);
				match(OpenParen);
				setState(537);
				if (!((_input.LA(1) != Let))) throw new FailedPredicateException(this, "(_input.LA(1) != Let)");
				setState(538);
				singleExpression(0);
				setState(539);
				match(T__1);
				setState(540);
				singleExpression(0);
				setState(541);
				match(CloseParen);
				setState(542);
				statement();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(544);
				match(For);
				setState(545);
				match(OpenParen);
				setState(546);
				match(Var);
				setState(547);
				forBinding();
				setState(548);
				match(T__1);
				setState(549);
				singleExpression(0);
				setState(550);
				match(CloseParen);
				setState(551);
				statement();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(553);
				match(For);
				setState(554);
				match(OpenParen);
				setState(555);
				forDeclaration();
				setState(556);
				match(T__1);
				setState(557);
				singleExpression(0);
				setState(558);
				match(CloseParen);
				setState(559);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterForDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitForDeclaration(this);
		}
	}

	public final ForDeclarationContext forDeclaration() throws RecognitionException {
		ForDeclarationContext _localctx = new ForDeclarationContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_forDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(563);
			letOrConst();
			setState(564);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterForBinding(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitForBinding(this);
		}
	}

	public final ForBindingContext forBinding() throws RecognitionException {
		ForBindingContext _localctx = new ForBindingContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_forBinding);
		try {
			setState(568);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(566);
				bindingIdentifier();
				}
				break;
			case OpenBracket:
			case OpenBrace:
				enterOuterAlt(_localctx, 2);
				{
				setState(567);
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
		public TerminalNode Identifier() { return getToken(JSParser.Identifier, 0); }
		public ContinueStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_continueStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterContinueStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitContinueStatement(this);
		}
	}

	public final ContinueStatementContext continueStatement() throws RecognitionException {
		ContinueStatementContext _localctx = new ContinueStatementContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_continueStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(570);
			match(Continue);
			setState(573);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
			case 1:
				{
				setState(571);
				if (!(!here(LineTerminator))) throw new FailedPredicateException(this, "!here(LineTerminator)");
				setState(572);
				match(Identifier);
				}
				break;
			}
			setState(575);
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
		public TerminalNode Identifier() { return getToken(JSParser.Identifier, 0); }
		public BreakStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_breakStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterBreakStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitBreakStatement(this);
		}
	}

	public final BreakStatementContext breakStatement() throws RecognitionException {
		BreakStatementContext _localctx = new BreakStatementContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_breakStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(577);
			match(Break);
			setState(580);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
			case 1:
				{
				setState(578);
				if (!(!here(LineTerminator))) throw new FailedPredicateException(this, "!here(LineTerminator)");
				setState(579);
				match(Identifier);
				}
				break;
			}
			setState(582);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterReturnStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitReturnStatement(this);
		}
	}

	public final ReturnStatementContext returnStatement() throws RecognitionException {
		ReturnStatementContext _localctx = new ReturnStatementContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_returnStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(584);
			match(Return);
			setState(587);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
			case 1:
				{
				setState(585);
				if (!(!here(LineTerminator))) throw new FailedPredicateException(this, "!here(LineTerminator)");
				setState(586);
				expressionSequence();
				}
				break;
			}
			setState(589);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterWithStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitWithStatement(this);
		}
	}

	public final WithStatementContext withStatement() throws RecognitionException {
		WithStatementContext _localctx = new WithStatementContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_withStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(591);
			match(With);
			setState(592);
			match(OpenParen);
			setState(593);
			expressionSequence();
			setState(594);
			match(CloseParen);
			setState(595);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterSwitchStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitSwitchStatement(this);
		}
	}

	public final SwitchStatementContext switchStatement() throws RecognitionException {
		SwitchStatementContext _localctx = new SwitchStatementContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_switchStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(597);
			match(Switch);
			setState(598);
			match(OpenParen);
			setState(599);
			expressionSequence();
			setState(600);
			match(CloseParen);
			setState(601);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterCaseBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitCaseBlock(this);
		}
	}

	public final CaseBlockContext caseBlock() throws RecognitionException {
		CaseBlockContext _localctx = new CaseBlockContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_caseBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(603);
			match(OpenBrace);
			setState(605);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Case) {
				{
				setState(604);
				caseClauses();
				}
			}

			setState(611);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Default) {
				{
				setState(607);
				defaultClause();
				setState(609);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Case) {
					{
					setState(608);
					caseClauses();
					}
				}

				}
			}

			setState(613);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterCaseClauses(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitCaseClauses(this);
		}
	}

	public final CaseClausesContext caseClauses() throws RecognitionException {
		CaseClausesContext _localctx = new CaseClausesContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_caseClauses);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(616); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(615);
				caseClause();
				}
				}
				setState(618); 
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterCaseClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitCaseClause(this);
		}
	}

	public final CaseClauseContext caseClause() throws RecognitionException {
		CaseClauseContext _localctx = new CaseClauseContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_caseClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(620);
			match(Case);
			setState(621);
			expressionSequence();
			setState(622);
			match(Colon);
			setState(624);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,44,_ctx) ) {
			case 1:
				{
				setState(623);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterDefaultClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitDefaultClause(this);
		}
	}

	public final DefaultClauseContext defaultClause() throws RecognitionException {
		DefaultClauseContext _localctx = new DefaultClauseContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_defaultClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(626);
			match(Default);
			setState(627);
			match(Colon);
			setState(629);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
			case 1:
				{
				setState(628);
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
		public TerminalNode Identifier() { return getToken(JSParser.Identifier, 0); }
		public LabelledItemContext labelledItem() {
			return getRuleContext(LabelledItemContext.class,0);
		}
		public LabelledStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_labelledStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterLabelledStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitLabelledStatement(this);
		}
	}

	public final LabelledStatementContext labelledStatement() throws RecognitionException {
		LabelledStatementContext _localctx = new LabelledStatementContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_labelledStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(631);
			match(Identifier);
			setState(632);
			match(Colon);
			setState(633);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterLabelledItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitLabelledItem(this);
		}
	}

	public final LabelledItemContext labelledItem() throws RecognitionException {
		LabelledItemContext _localctx = new LabelledItemContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_labelledItem);
		try {
			setState(637);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(635);
				statement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(636);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterThrowStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitThrowStatement(this);
		}
	}

	public final ThrowStatementContext throwStatement() throws RecognitionException {
		ThrowStatementContext _localctx = new ThrowStatementContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_throwStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(639);
			match(Throw);
			setState(640);
			if (!(!here(LineTerminator))) throw new FailedPredicateException(this, "!here(LineTerminator)");
			setState(641);
			expressionSequence();
			setState(642);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterTryStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitTryStatement(this);
		}
	}

	public final TryStatementContext tryStatement() throws RecognitionException {
		TryStatementContext _localctx = new TryStatementContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_tryStatement);
		try {
			setState(657);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,47,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(644);
				match(Try);
				setState(645);
				block();
				setState(646);
				catchProduction();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(648);
				match(Try);
				setState(649);
				block();
				setState(650);
				finallyProduction();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(652);
				match(Try);
				setState(653);
				block();
				setState(654);
				catchProduction();
				setState(655);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterCatchProduction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitCatchProduction(this);
		}
	}

	public final CatchProductionContext catchProduction() throws RecognitionException {
		CatchProductionContext _localctx = new CatchProductionContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_catchProduction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(659);
			match(Catch);
			setState(660);
			match(OpenParen);
			setState(661);
			catchParameter();
			setState(662);
			match(CloseParen);
			setState(663);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterFinallyProduction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitFinallyProduction(this);
		}
	}

	public final FinallyProductionContext finallyProduction() throws RecognitionException {
		FinallyProductionContext _localctx = new FinallyProductionContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_finallyProduction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(665);
			match(Finally);
			setState(666);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterCatchParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitCatchParameter(this);
		}
	}

	public final CatchParameterContext catchParameter() throws RecognitionException {
		CatchParameterContext _localctx = new CatchParameterContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_catchParameter);
		try {
			setState(670);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(668);
				bindingIdentifier();
				}
				break;
			case OpenBracket:
			case OpenBrace:
				enterOuterAlt(_localctx, 2);
				{
				setState(669);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterDebuggerStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitDebuggerStatement(this);
		}
	}

	public final DebuggerStatementContext debuggerStatement() throws RecognitionException {
		DebuggerStatementContext _localctx = new DebuggerStatementContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_debuggerStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(672);
			match(Debugger);
			setState(673);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterFunctionDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitFunctionDeclaration(this);
		}
	}

	public final FunctionDeclarationContext functionDeclaration() throws RecognitionException {
		FunctionDeclarationContext _localctx = new FunctionDeclarationContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_functionDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(675);
			match(Function);
			setState(676);
			bindingIdentifier();
			setState(677);
			match(OpenParen);
			setState(678);
			formalParameters();
			setState(679);
			match(CloseParen);
			setState(680);
			match(OpenBrace);
			setState(681);
			functionBody();
			setState(682);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterStrictFormalParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitStrictFormalParameters(this);
		}
	}

	public final StrictFormalParametersContext strictFormalParameters() throws RecognitionException {
		StrictFormalParametersContext _localctx = new StrictFormalParametersContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_strictFormalParameters);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(684);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterFormalParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitFormalParameters(this);
		}
	}

	public final FormalParametersContext formalParameters() throws RecognitionException {
		FormalParametersContext _localctx = new FormalParametersContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_formalParameters);
		try {
			setState(688);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CloseParen:
				enterOuterAlt(_localctx, 1);
				{
				}
				break;
			case T__0:
			case OpenBracket:
			case OpenBrace:
			case Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(687);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterFormalParameterList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitFormalParameterList(this);
		}
	}

	public final FormalParameterListContext formalParameterList() throws RecognitionException {
		FormalParameterListContext _localctx = new FormalParameterListContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_formalParameterList);
		int _la;
		try {
			setState(696);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
				enterOuterAlt(_localctx, 1);
				{
				setState(690);
				functionRestParameter();
				}
				break;
			case OpenBracket:
			case OpenBrace:
			case Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(691);
				formalsList();
				setState(694);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Comma) {
					{
					setState(692);
					match(Comma);
					setState(693);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterFormalsList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitFormalsList(this);
		}
	}

	public final FormalsListContext formalsList() throws RecognitionException {
		FormalsListContext _localctx = new FormalsListContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_formalsList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(698);
			formalParameter();
			setState(703);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,52,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(699);
					match(Comma);
					setState(700);
					formalParameter();
					}
					} 
				}
				setState(705);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,52,_ctx);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterFunctionRestParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitFunctionRestParameter(this);
		}
	}

	public final FunctionRestParameterContext functionRestParameter() throws RecognitionException {
		FunctionRestParameterContext _localctx = new FunctionRestParameterContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_functionRestParameter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(706);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterFormalParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitFormalParameter(this);
		}
	}

	public final FormalParameterContext formalParameter() throws RecognitionException {
		FormalParameterContext _localctx = new FormalParameterContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_formalParameter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(708);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterFunctionBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitFunctionBody(this);
		}
	}

	public final FunctionBodyContext functionBody() throws RecognitionException {
		FunctionBodyContext _localctx = new FunctionBodyContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_functionBody);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(710);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterFunctionStatementList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitFunctionStatementList(this);
		}
	}

	public final FunctionStatementListContext functionStatementList() throws RecognitionException {
		FunctionStatementListContext _localctx = new FunctionStatementListContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_functionStatementList);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(713);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,53,_ctx) ) {
			case 1:
				{
				setState(712);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterArrowFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitArrowFunction(this);
		}
	}

	public final ArrowFunctionContext arrowFunction() throws RecognitionException {
		ArrowFunctionContext _localctx = new ArrowFunctionContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_arrowFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(715);
			arrowParameters();
			setState(716);
			if (!(!here(LineTerminator))) throw new FailedPredicateException(this, "!here(LineTerminator)");
			setState(717);
			match(Arrow);
			setState(718);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterArrowParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitArrowParameters(this);
		}
	}

	public final ArrowParametersContext arrowParameters() throws RecognitionException {
		ArrowParametersContext _localctx = new ArrowParametersContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_arrowParameters);
		try {
			setState(722);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(720);
				bindingIdentifier();
				}
				break;
			case OpenParen:
				enterOuterAlt(_localctx, 2);
				{
				setState(721);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterConciseBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitConciseBody(this);
		}
	}

	public final ConciseBodyContext conciseBody() throws RecognitionException {
		ConciseBodyContext _localctx = new ConciseBodyContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_conciseBody);
		try {
			setState(730);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,55,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(724);
				if (!((_input.LA(1) != OpenBrace))) throw new FailedPredicateException(this, "(_input.LA(1) != OpenBrace)");
				setState(725);
				singleExpression(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(726);
				match(OpenBrace);
				setState(727);
				functionBody();
				setState(728);
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
		public BindingIdentifierContext bindingIdentifier() {
			return getRuleContext(BindingIdentifierContext.class,0);
		}
		public CoverParenthesizedExpressionAndArrowParameterListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_coverParenthesizedExpressionAndArrowParameterList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterCoverParenthesizedExpressionAndArrowParameterList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitCoverParenthesizedExpressionAndArrowParameterList(this);
		}
	}

	public final CoverParenthesizedExpressionAndArrowParameterListContext coverParenthesizedExpressionAndArrowParameterList() throws RecognitionException {
		CoverParenthesizedExpressionAndArrowParameterListContext _localctx = new CoverParenthesizedExpressionAndArrowParameterListContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_coverParenthesizedExpressionAndArrowParameterList);
		try {
			setState(749);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,56,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(732);
				match(OpenParen);
				setState(733);
				expressionSequence();
				setState(734);
				match(CloseParen);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(736);
				match(OpenParen);
				setState(737);
				match(CloseParen);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(738);
				match(OpenParen);
				setState(739);
				match(T__0);
				setState(740);
				bindingIdentifier();
				setState(741);
				match(CloseParen);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(743);
				match(OpenParen);
				setState(744);
				expressionSequence();
				setState(745);
				match(Comma);
				setState(746);
				match(T__0);
				setState(747);
				bindingIdentifier();
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
		public TerminalNode Identifier() { return getToken(JSParser.Identifier, 0); }
		public PropertySetParameterListContext propertySetParameterList() {
			return getRuleContext(PropertySetParameterListContext.class,0);
		}
		public MethodDefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodDefinition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterMethodDefinition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitMethodDefinition(this);
		}
	}

	public final MethodDefinitionContext methodDefinition() throws RecognitionException {
		MethodDefinitionContext _localctx = new MethodDefinitionContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_methodDefinition);
		try {
			setState(779);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,57,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(751);
				propertyName();
				setState(752);
				match(OpenParen);
				setState(753);
				strictFormalParameters();
				setState(754);
				match(CloseParen);
				setState(755);
				match(OpenBrace);
				setState(756);
				functionBody();
				setState(757);
				match(CloseBrace);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(759);
				generatorMethod();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(760);
				if (!(_input.LT(1).getText().equals("get"))) throw new FailedPredicateException(this, "_input.LT(1).getText().equals(\"get\")");
				setState(761);
				match(Identifier);
				setState(762);
				propertyName();
				setState(763);
				match(OpenParen);
				setState(764);
				match(CloseParen);
				setState(765);
				match(OpenBrace);
				setState(766);
				functionBody();
				setState(767);
				match(CloseBrace);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(769);
				if (!(_input.LT(1).getText().equals("set"))) throw new FailedPredicateException(this, "_input.LT(1).getText().equals(\"set\")");
				setState(770);
				match(Identifier);
				setState(771);
				propertyName();
				setState(772);
				match(OpenParen);
				setState(773);
				propertySetParameterList();
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterPropertySetParameterList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitPropertySetParameterList(this);
		}
	}

	public final PropertySetParameterListContext propertySetParameterList() throws RecognitionException {
		PropertySetParameterListContext _localctx = new PropertySetParameterListContext(_ctx, getState());
		enterRule(_localctx, 128, RULE_propertySetParameterList);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(781);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterGeneratorMethod(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitGeneratorMethod(this);
		}
	}

	public final GeneratorMethodContext generatorMethod() throws RecognitionException {
		GeneratorMethodContext _localctx = new GeneratorMethodContext(_ctx, getState());
		enterRule(_localctx, 130, RULE_generatorMethod);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(783);
			match(Multiply);
			setState(784);
			propertyName();
			setState(785);
			match(OpenParen);
			setState(786);
			strictFormalParameters();
			setState(787);
			match(CloseParen);
			setState(788);
			match(OpenBrace);
			setState(789);
			generatorBody();
			setState(790);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterGeneratorDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitGeneratorDeclaration(this);
		}
	}

	public final GeneratorDeclarationContext generatorDeclaration() throws RecognitionException {
		GeneratorDeclarationContext _localctx = new GeneratorDeclarationContext(_ctx, getState());
		enterRule(_localctx, 132, RULE_generatorDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(792);
			match(Function);
			setState(793);
			match(Multiply);
			setState(794);
			bindingIdentifier();
			setState(795);
			match(OpenParen);
			setState(796);
			formalParameters();
			setState(797);
			match(CloseParen);
			setState(798);
			match(OpenBrace);
			setState(799);
			generatorBody();
			setState(800);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterGeneratorExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitGeneratorExpression(this);
		}
	}

	public final GeneratorExpressionContext generatorExpression() throws RecognitionException {
		GeneratorExpressionContext _localctx = new GeneratorExpressionContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_generatorExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(802);
			match(Function);
			setState(803);
			match(Multiply);
			setState(805);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Identifier) {
				{
				setState(804);
				bindingIdentifier();
				}
			}

			setState(807);
			match(OpenParen);
			setState(808);
			formalParameters();
			setState(809);
			match(CloseParen);
			setState(810);
			match(OpenBrace);
			setState(811);
			generatorBody();
			setState(812);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterGeneratorBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitGeneratorBody(this);
		}
	}

	public final GeneratorBodyContext generatorBody() throws RecognitionException {
		GeneratorBodyContext _localctx = new GeneratorBodyContext(_ctx, getState());
		enterRule(_localctx, 136, RULE_generatorBody);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(814);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterClassDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitClassDeclaration(this);
		}
	}

	public final ClassDeclarationContext classDeclaration() throws RecognitionException {
		ClassDeclarationContext _localctx = new ClassDeclarationContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_classDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(816);
			match(Class);
			setState(817);
			bindingIdentifier();
			setState(818);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterClassExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitClassExpression(this);
		}
	}

	public final ClassExpressionContext classExpression() throws RecognitionException {
		ClassExpressionContext _localctx = new ClassExpressionContext(_ctx, getState());
		enterRule(_localctx, 140, RULE_classExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(820);
			match(Class);
			setState(822);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Identifier) {
				{
				setState(821);
				bindingIdentifier();
				}
			}

			setState(824);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterClassTail(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitClassTail(this);
		}
	}

	public final ClassTailContext classTail() throws RecognitionException {
		ClassTailContext _localctx = new ClassTailContext(_ctx, getState());
		enterRule(_localctx, 142, RULE_classTail);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(827);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Extends) {
				{
				setState(826);
				classHeritage();
				}
			}

			setState(829);
			match(OpenBrace);
			setState(831);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,61,_ctx) ) {
			case 1:
				{
				setState(830);
				classBody();
				}
				break;
			}
			setState(833);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterClassHeritage(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitClassHeritage(this);
		}
	}

	public final ClassHeritageContext classHeritage() throws RecognitionException {
		ClassHeritageContext _localctx = new ClassHeritageContext(_ctx, getState());
		enterRule(_localctx, 144, RULE_classHeritage);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(835);
			match(Extends);
			setState(836);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterClassBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitClassBody(this);
		}
	}

	public final ClassBodyContext classBody() throws RecognitionException {
		ClassBodyContext _localctx = new ClassBodyContext(_ctx, getState());
		enterRule(_localctx, 146, RULE_classBody);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(838);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterClassElementList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitClassElementList(this);
		}
	}

	public final ClassElementListContext classElementList() throws RecognitionException {
		ClassElementListContext _localctx = new ClassElementListContext(_ctx, getState());
		enterRule(_localctx, 148, RULE_classElementList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(841); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(840);
					classElement();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(843); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,62,_ctx);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterClassElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitClassElement(this);
		}
	}

	public final ClassElementContext classElement() throws RecognitionException {
		ClassElementContext _localctx = new ClassElementContext(_ctx, getState());
		enterRule(_localctx, 150, RULE_classElement);
		try {
			setState(849);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,63,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(845);
				methodDefinition();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(846);
				match(Static);
				setState(847);
				methodDefinition();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(848);
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
		public ElementListContext elementList() {
			return getRuleContext(ElementListContext.class,0);
		}
		public ElisionContext elision() {
			return getRuleContext(ElisionContext.class,0);
		}
		public ArrayLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterArrayLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitArrayLiteral(this);
		}
	}

	public final ArrayLiteralContext arrayLiteral() throws RecognitionException {
		ArrayLiteralContext _localctx = new ArrayLiteralContext(_ctx, getState());
		enterRule(_localctx, 152, RULE_arrayLiteral);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(851);
			match(OpenBracket);
			setState(853);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,64,_ctx) ) {
			case 1:
				{
				setState(852);
				elementList(0);
				}
				break;
			}
			setState(856);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,65,_ctx) ) {
			case 1:
				{
				setState(855);
				match(Comma);
				}
				break;
			}
			setState(859);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Comma) {
				{
				setState(858);
				elision();
				}
			}

			setState(861);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterElementList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitElementList(this);
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
		int _startState = 154;
		enterRecursionRule(_localctx, 154, RULE_elementList, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(872);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,69,_ctx) ) {
			case 1:
				{
				setState(865);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Comma) {
					{
					setState(864);
					elision();
					}
				}

				setState(867);
				singleExpression(0);
				}
				break;
			case 2:
				{
				setState(869);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Comma) {
					{
					setState(868);
					elision();
					}
				}

				setState(871);
				spreadElement();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(888);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,73,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(886);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,72,_ctx) ) {
					case 1:
						{
						_localctx = new ElementListContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_elementList);
						setState(874);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(875);
						match(Comma);
						setState(877);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==Comma) {
							{
							setState(876);
							elision();
							}
						}

						setState(879);
						singleExpression(0);
						}
						break;
					case 2:
						{
						_localctx = new ElementListContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_elementList);
						setState(880);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(881);
						match(Comma);
						setState(883);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==Comma) {
							{
							setState(882);
							elision();
							}
						}

						setState(885);
						spreadElement();
						}
						break;
					}
					} 
				}
				setState(890);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,73,_ctx);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterElision(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitElision(this);
		}
	}

	public final ElisionContext elision() throws RecognitionException {
		ElisionContext _localctx = new ElisionContext(_ctx, getState());
		enterRule(_localctx, 156, RULE_elision);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(892); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(891);
				match(Comma);
				}
				}
				setState(894); 
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterSpreadElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitSpreadElement(this);
		}
	}

	public final SpreadElementContext spreadElement() throws RecognitionException {
		SpreadElementContext _localctx = new SpreadElementContext(_ctx, getState());
		enterRule(_localctx, 158, RULE_spreadElement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(896);
			match(T__0);
			setState(897);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterObjectLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitObjectLiteral(this);
		}
	}

	public final ObjectLiteralContext objectLiteral() throws RecognitionException {
		ObjectLiteralContext _localctx = new ObjectLiteralContext(_ctx, getState());
		enterRule(_localctx, 160, RULE_objectLiteral);
		int _la;
		try {
			setState(908);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,76,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(899);
				match(OpenBrace);
				setState(900);
				match(CloseBrace);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(901);
				match(OpenBrace);
				setState(902);
				propertyDefinitionList();
				setState(904);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Comma) {
					{
					setState(903);
					match(Comma);
					}
				}

				setState(906);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterPropertyDefinitionList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitPropertyDefinitionList(this);
		}
	}

	public final PropertyDefinitionListContext propertyDefinitionList() throws RecognitionException {
		PropertyDefinitionListContext _localctx = new PropertyDefinitionListContext(_ctx, getState());
		enterRule(_localctx, 162, RULE_propertyDefinitionList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(910);
			propertyDefinition();
			setState(915);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,77,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(911);
					match(Comma);
					setState(912);
					propertyDefinition();
					}
					} 
				}
				setState(917);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,77,_ctx);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterPropertyDefinition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitPropertyDefinition(this);
		}
	}

	public final PropertyDefinitionContext propertyDefinition() throws RecognitionException {
		PropertyDefinitionContext _localctx = new PropertyDefinitionContext(_ctx, getState());
		enterRule(_localctx, 164, RULE_propertyDefinition);
		try {
			setState(925);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,78,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(918);
				identifierReference();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(919);
				coverInitializedName();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(920);
				propertyName();
				setState(921);
				match(Colon);
				setState(922);
				singleExpression(0);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(924);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterPropertyName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitPropertyName(this);
		}
	}

	public final PropertyNameContext propertyName() throws RecognitionException {
		PropertyNameContext _localctx = new PropertyNameContext(_ctx, getState());
		enterRule(_localctx, 166, RULE_propertyName);
		try {
			setState(929);
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
			case Identifier:
			case StringLiteral:
				enterOuterAlt(_localctx, 1);
				{
				setState(927);
				literalPropertyName();
				}
				break;
			case OpenBracket:
				enterOuterAlt(_localctx, 2);
				{
				setState(928);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterLiteralPropertyName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitLiteralPropertyName(this);
		}
	}

	public final LiteralPropertyNameContext literalPropertyName() throws RecognitionException {
		LiteralPropertyNameContext _localctx = new LiteralPropertyNameContext(_ctx, getState());
		enterRule(_localctx, 168, RULE_literalPropertyName);
		try {
			setState(934);
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
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(931);
				identifierName();
				}
				break;
			case StringLiteral:
				enterOuterAlt(_localctx, 2);
				{
				setState(932);
				match(StringLiteral);
				}
				break;
			case DecimalLiteral:
			case HexIntegerLiteral:
			case OctalIntegerLiteral:
			case BinaryIntegerLiteral:
				enterOuterAlt(_localctx, 3);
				{
				setState(933);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterComputedPropertyName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitComputedPropertyName(this);
		}
	}

	public final ComputedPropertyNameContext computedPropertyName() throws RecognitionException {
		ComputedPropertyNameContext _localctx = new ComputedPropertyNameContext(_ctx, getState());
		enterRule(_localctx, 170, RULE_computedPropertyName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(936);
			match(OpenBracket);
			setState(937);
			singleExpression(0);
			setState(938);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterCoverInitializedName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitCoverInitializedName(this);
		}
	}

	public final CoverInitializedNameContext coverInitializedName() throws RecognitionException {
		CoverInitializedNameContext _localctx = new CoverInitializedNameContext(_ctx, getState());
		enterRule(_localctx, 172, RULE_coverInitializedName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(940);
			identifierReference();
			setState(941);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterArguments(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitArguments(this);
		}
	}

	public final ArgumentsContext arguments() throws RecognitionException {
		ArgumentsContext _localctx = new ArgumentsContext(_ctx, getState());
		enterRule(_localctx, 174, RULE_arguments);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(943);
			match(OpenParen);
			setState(945);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << RegularExpressionLiteral) | (1L << OpenBracket) | (1L << OpenParen) | (1L << OpenBrace) | (1L << PlusPlus) | (1L << MinusMinus) | (1L << Plus) | (1L << Minus) | (1L << BitNot) | (1L << Not) | (1L << NullLiteral) | (1L << BooleanLiteral) | (1L << DecimalLiteral) | (1L << HexIntegerLiteral) | (1L << OctalIntegerLiteral) | (1L << BinaryIntegerLiteral))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (Typeof - 65)) | (1L << (New - 65)) | (1L << (Void - 65)) | (1L << (Function - 65)) | (1L << (This - 65)) | (1L << (Delete - 65)) | (1L << (Class - 65)) | (1L << (Super - 65)) | (1L << (Yield - 65)) | (1L << (Identifier - 65)) | (1L << (StringLiteral - 65)) | (1L << (NoSubstitutionTemplate - 65)) | (1L << (TemplateHead - 65)))) != 0)) {
				{
				setState(944);
				argumentList();
				}
			}

			setState(947);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterArgumentList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitArgumentList(this);
		}
	}

	public final ArgumentListContext argumentList() throws RecognitionException {
		ArgumentListContext _localctx = new ArgumentListContext(_ctx, getState());
		enterRule(_localctx, 176, RULE_argumentList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(950);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(949);
				match(T__0);
				}
			}

			setState(952);
			singleExpression(0);
			setState(960);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(953);
				match(Comma);
				setState(955);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__0) {
					{
					setState(954);
					match(T__0);
					}
				}

				setState(957);
				singleExpression(0);
				}
				}
				setState(962);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterExpressionSequence(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitExpressionSequence(this);
		}
	}

	public final ExpressionSequenceContext expressionSequence() throws RecognitionException {
		ExpressionSequenceContext _localctx = new ExpressionSequenceContext(_ctx, getState());
		enterRule(_localctx, 178, RULE_expressionSequence);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(963);
			singleExpression(0);
			setState(968);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,85,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(964);
					match(Comma);
					setState(965);
					singleExpression(0);
					}
					} 
				}
				setState(970);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,85,_ctx);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterTernaryExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitTernaryExpression(this);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterLogicalAndExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitLogicalAndExpression(this);
		}
	}
	public static class PreIncrementExpressionContext extends SingleExpressionContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public PreIncrementExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterPreIncrementExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitPreIncrementExpression(this);
		}
	}
	public static class ObjectLiteralExpressionContext extends SingleExpressionContext {
		public ObjectLiteralContext objectLiteral() {
			return getRuleContext(ObjectLiteralContext.class,0);
		}
		public ObjectLiteralExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterObjectLiteralExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitObjectLiteralExpression(this);
		}
	}
	public static class SuperMemberIndexExpressionContext extends SingleExpressionContext {
		public TerminalNode Super() { return getToken(JSParser.Super, 0); }
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public SuperMemberIndexExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterSuperMemberIndexExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitSuperMemberIndexExpression(this);
		}
	}
	public static class NewTargetExpressionContext extends SingleExpressionContext {
		public TerminalNode New() { return getToken(JSParser.New, 0); }
		public TerminalNode Identifier() { return getToken(JSParser.Identifier, 0); }
		public NewTargetExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterNewTargetExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitNewTargetExpression(this);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterInExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitInExpression(this);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterLogicalOrExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitLogicalOrExpression(this);
		}
	}
	public static class NotExpressionContext extends SingleExpressionContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public NotExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterNotExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitNotExpression(this);
		}
	}
	public static class GeneratorExpressionExpressionContext extends SingleExpressionContext {
		public GeneratorExpressionContext generatorExpression() {
			return getRuleContext(GeneratorExpressionContext.class,0);
		}
		public GeneratorExpressionExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterGeneratorExpressionExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitGeneratorExpressionExpression(this);
		}
	}
	public static class SuperMemberDotExpressionContext extends SingleExpressionContext {
		public TerminalNode Super() { return getToken(JSParser.Super, 0); }
		public IdentifierNameContext identifierName() {
			return getRuleContext(IdentifierNameContext.class,0);
		}
		public SuperMemberDotExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterSuperMemberDotExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitSuperMemberDotExpression(this);
		}
	}
	public static class PreDecreaseExpressionContext extends SingleExpressionContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public PreDecreaseExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterPreDecreaseExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitPreDecreaseExpression(this);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterArgumentsExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitArgumentsExpression(this);
		}
	}
	public static class ThisExpressionContext extends SingleExpressionContext {
		public TerminalNode This() { return getToken(JSParser.This, 0); }
		public ThisExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterThisExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitThisExpression(this);
		}
	}
	public static class RegularExpressionLiteralExpressionContext extends SingleExpressionContext {
		public TerminalNode RegularExpressionLiteral() { return getToken(JSParser.RegularExpressionLiteral, 0); }
		public RegularExpressionLiteralExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterRegularExpressionLiteralExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitRegularExpressionLiteralExpression(this);
		}
	}
	public static class FunctionExpressionContext extends SingleExpressionContext {
		public TerminalNode Function() { return getToken(JSParser.Function, 0); }
		public FormalParametersContext formalParameters() {
			return getRuleContext(FormalParametersContext.class,0);
		}
		public FunctionBodyContext functionBody() {
			return getRuleContext(FunctionBodyContext.class,0);
		}
		public BindingIdentifierContext bindingIdentifier() {
			return getRuleContext(BindingIdentifierContext.class,0);
		}
		public FunctionExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterFunctionExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitFunctionExpression(this);
		}
	}
	public static class UnaryMinusExpressionContext extends SingleExpressionContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public UnaryMinusExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterUnaryMinusExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitUnaryMinusExpression(this);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterAssignmentExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitAssignmentExpression(this);
		}
	}
	public static class PostDecreaseExpressionContext extends SingleExpressionContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public PostDecreaseExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterPostDecreaseExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitPostDecreaseExpression(this);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterTypeofExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitTypeofExpression(this);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterInstanceofExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitInstanceofExpression(this);
		}
	}
	public static class UnaryPlusExpressionContext extends SingleExpressionContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public UnaryPlusExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterUnaryPlusExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitUnaryPlusExpression(this);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterDeleteExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitDeleteExpression(this);
		}
	}
	public static class ArrowFunctionExpressionContext extends SingleExpressionContext {
		public ArrowFunctionContext arrowFunction() {
			return getRuleContext(ArrowFunctionContext.class,0);
		}
		public ArrowFunctionExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterArrowFunctionExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitArrowFunctionExpression(this);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterEqualityExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitEqualityExpression(this);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterBitXOrExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitBitXOrExpression(this);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterMultiplicativeExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitMultiplicativeExpression(this);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterBitShiftExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitBitShiftExpression(this);
		}
	}
	public static class ParenthesizedExpressionContext extends SingleExpressionContext {
		public ExpressionSequenceContext expressionSequence() {
			return getRuleContext(ExpressionSequenceContext.class,0);
		}
		public ParenthesizedExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterParenthesizedExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitParenthesizedExpression(this);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterAdditiveExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitAdditiveExpression(this);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterRelationalExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitRelationalExpression(this);
		}
	}
	public static class PostIncrementExpressionContext extends SingleExpressionContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public PostIncrementExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterPostIncrementExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitPostIncrementExpression(this);
		}
	}
	public static class ClassExpressionExpressionContext extends SingleExpressionContext {
		public ClassExpressionContext classExpression() {
			return getRuleContext(ClassExpressionContext.class,0);
		}
		public ClassExpressionExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterClassExpressionExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitClassExpressionExpression(this);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterYieldExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitYieldExpression(this);
		}
	}
	public static class BitNotExpressionContext extends SingleExpressionContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public BitNotExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterBitNotExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitBitNotExpression(this);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterNewExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitNewExpression(this);
		}
	}
	public static class LiteralExpressionContext extends SingleExpressionContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public LiteralExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterLiteralExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitLiteralExpression(this);
		}
	}
	public static class ArrayLiteralExpressionContext extends SingleExpressionContext {
		public ArrayLiteralContext arrayLiteral() {
			return getRuleContext(ArrayLiteralContext.class,0);
		}
		public ArrayLiteralExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterArrayLiteralExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitArrayLiteralExpression(this);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterMemberDotExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitMemberDotExpression(this);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterMemberIndexExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitMemberIndexExpression(this);
		}
	}
	public static class IdentifierExpressionContext extends SingleExpressionContext {
		public TerminalNode Identifier() { return getToken(JSParser.Identifier, 0); }
		public IdentifierExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterIdentifierExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitIdentifierExpression(this);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterBitAndExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitBitAndExpression(this);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterBitOrExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitBitOrExpression(this);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterAssignmentOperatorExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitAssignmentOperatorExpression(this);
		}
	}
	public static class SuperCallExpressionContext extends SingleExpressionContext {
		public TerminalNode Super() { return getToken(JSParser.Super, 0); }
		public ArgumentsContext arguments() {
			return getRuleContext(ArgumentsContext.class,0);
		}
		public SuperCallExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterSuperCallExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitSuperCallExpression(this);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterVoidExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitVoidExpression(this);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterCallTemplateLiteralExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitCallTemplateLiteralExpression(this);
		}
	}
	public static class TemplateLiteralExpressionContext extends SingleExpressionContext {
		public TemplateLiteralContext templateLiteral() {
			return getRuleContext(TemplateLiteralContext.class,0);
		}
		public TemplateLiteralExpressionContext(SingleExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterTemplateLiteralExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitTemplateLiteralExpression(this);
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
		int _startState = 180;
		enterRecursionRule(_localctx, 180, RULE_singleExpression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1042);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,90,_ctx) ) {
			case 1:
				{
				_localctx = new FunctionExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(972);
				match(Function);
				setState(974);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Identifier) {
					{
					setState(973);
					bindingIdentifier();
					}
				}

				setState(976);
				match(OpenParen);
				setState(977);
				formalParameters();
				setState(978);
				match(CloseParen);
				setState(979);
				match(OpenBrace);
				setState(980);
				functionBody();
				setState(981);
				match(CloseBrace);
				}
				break;
			case 2:
				{
				_localctx = new SuperMemberIndexExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(983);
				match(Super);
				setState(984);
				match(OpenBracket);
				setState(985);
				expressionSequence();
				setState(986);
				match(CloseBracket);
				}
				break;
			case 3:
				{
				_localctx = new SuperMemberDotExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(988);
				match(Super);
				setState(989);
				match(Dot);
				setState(990);
				identifierName();
				}
				break;
			case 4:
				{
				_localctx = new SuperCallExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(991);
				match(Super);
				setState(992);
				arguments();
				}
				break;
			case 5:
				{
				_localctx = new NewTargetExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(993);
				match(New);
				setState(994);
				match(Dot);
				setState(995);
				if (!(_input.LT(1).getText().equals("target"))) throw new FailedPredicateException(this, "_input.LT(1).getText().equals(\"target\")");
				setState(996);
				match(Identifier);
				}
				break;
			case 6:
				{
				_localctx = new NewExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(997);
				match(New);
				setState(998);
				singleExpression(0);
				setState(1000);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,87,_ctx) ) {
				case 1:
					{
					setState(999);
					arguments();
					}
					break;
				}
				}
				break;
			case 7:
				{
				_localctx = new DeleteExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1002);
				match(Delete);
				setState(1003);
				singleExpression(36);
				}
				break;
			case 8:
				{
				_localctx = new VoidExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1004);
				match(Void);
				setState(1005);
				singleExpression(35);
				}
				break;
			case 9:
				{
				_localctx = new TypeofExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1006);
				match(Typeof);
				setState(1007);
				singleExpression(34);
				}
				break;
			case 10:
				{
				_localctx = new PreIncrementExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1008);
				match(PlusPlus);
				setState(1009);
				singleExpression(33);
				}
				break;
			case 11:
				{
				_localctx = new PreDecreaseExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1010);
				match(MinusMinus);
				setState(1011);
				singleExpression(32);
				}
				break;
			case 12:
				{
				_localctx = new UnaryPlusExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1012);
				match(Plus);
				setState(1013);
				singleExpression(31);
				}
				break;
			case 13:
				{
				_localctx = new UnaryMinusExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1014);
				match(Minus);
				setState(1015);
				singleExpression(30);
				}
				break;
			case 14:
				{
				_localctx = new BitNotExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1016);
				match(BitNot);
				setState(1017);
				singleExpression(29);
				}
				break;
			case 15:
				{
				_localctx = new NotExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1018);
				match(Not);
				setState(1019);
				singleExpression(28);
				}
				break;
			case 16:
				{
				_localctx = new YieldExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1020);
				match(Yield);
				setState(1026);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,89,_ctx) ) {
				case 1:
					{
					setState(1021);
					if (!(!here(LineTerminator))) throw new FailedPredicateException(this, "!here(LineTerminator)");
					setState(1023);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==Multiply) {
						{
						setState(1022);
						match(Multiply);
						}
					}

					setState(1025);
					singleExpression(0);
					}
					break;
				}
				}
				break;
			case 17:
				{
				_localctx = new ArrowFunctionExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1028);
				arrowFunction();
				}
				break;
			case 18:
				{
				_localctx = new ThisExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1029);
				match(This);
				}
				break;
			case 19:
				{
				_localctx = new IdentifierExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1030);
				match(Identifier);
				}
				break;
			case 20:
				{
				_localctx = new LiteralExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1031);
				literal();
				}
				break;
			case 21:
				{
				_localctx = new ArrayLiteralExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1032);
				arrayLiteral();
				}
				break;
			case 22:
				{
				_localctx = new ObjectLiteralExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1033);
				objectLiteral();
				}
				break;
			case 23:
				{
				_localctx = new ClassExpressionExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1034);
				classExpression();
				}
				break;
			case 24:
				{
				_localctx = new GeneratorExpressionExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1035);
				generatorExpression();
				}
				break;
			case 25:
				{
				_localctx = new RegularExpressionLiteralExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1036);
				match(RegularExpressionLiteral);
				}
				break;
			case 26:
				{
				_localctx = new TemplateLiteralExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1037);
				templateLiteral();
				}
				break;
			case 27:
				{
				_localctx = new ParenthesizedExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1038);
				match(OpenParen);
				setState(1039);
				expressionSequence();
				setState(1040);
				match(CloseParen);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(1113);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,92,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(1111);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,91,_ctx) ) {
					case 1:
						{
						_localctx = new MultiplicativeExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1044);
						if (!(precpred(_ctx, 27))) throw new FailedPredicateException(this, "precpred(_ctx, 27)");
						setState(1045);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Multiply) | (1L << Divide) | (1L << Modulus))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1046);
						singleExpression(28);
						}
						break;
					case 2:
						{
						_localctx = new AdditiveExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1047);
						if (!(precpred(_ctx, 26))) throw new FailedPredicateException(this, "precpred(_ctx, 26)");
						setState(1048);
						_la = _input.LA(1);
						if ( !(_la==Plus || _la==Minus) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1049);
						singleExpression(27);
						}
						break;
					case 3:
						{
						_localctx = new BitShiftExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1050);
						if (!(precpred(_ctx, 25))) throw new FailedPredicateException(this, "precpred(_ctx, 25)");
						setState(1051);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RightShiftArithmetic) | (1L << LeftShiftArithmetic) | (1L << RightShiftLogical))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1052);
						singleExpression(26);
						}
						break;
					case 4:
						{
						_localctx = new RelationalExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1053);
						if (!(precpred(_ctx, 24))) throw new FailedPredicateException(this, "precpred(_ctx, 24)");
						setState(1054);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LessThan) | (1L << MoreThan) | (1L << LessThanEquals) | (1L << GreaterThanEquals))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1055);
						singleExpression(25);
						}
						break;
					case 5:
						{
						_localctx = new InstanceofExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1056);
						if (!(precpred(_ctx, 23))) throw new FailedPredicateException(this, "precpred(_ctx, 23)");
						setState(1057);
						match(Instanceof);
						setState(1058);
						singleExpression(24);
						}
						break;
					case 6:
						{
						_localctx = new InExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1059);
						if (!(precpred(_ctx, 22))) throw new FailedPredicateException(this, "precpred(_ctx, 22)");
						setState(1060);
						match(In);
						setState(1061);
						singleExpression(23);
						}
						break;
					case 7:
						{
						_localctx = new EqualityExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1062);
						if (!(precpred(_ctx, 21))) throw new FailedPredicateException(this, "precpred(_ctx, 21)");
						setState(1063);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Equals) | (1L << NotEquals) | (1L << IdentityEquals) | (1L << IdentityNotEquals))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1064);
						singleExpression(22);
						}
						break;
					case 8:
						{
						_localctx = new BitAndExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1065);
						if (!(precpred(_ctx, 20))) throw new FailedPredicateException(this, "precpred(_ctx, 20)");
						setState(1066);
						match(BitAnd);
						setState(1067);
						singleExpression(21);
						}
						break;
					case 9:
						{
						_localctx = new BitXOrExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1068);
						if (!(precpred(_ctx, 19))) throw new FailedPredicateException(this, "precpred(_ctx, 19)");
						setState(1069);
						match(BitXOr);
						setState(1070);
						singleExpression(20);
						}
						break;
					case 10:
						{
						_localctx = new BitOrExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1071);
						if (!(precpred(_ctx, 18))) throw new FailedPredicateException(this, "precpred(_ctx, 18)");
						setState(1072);
						match(BitOr);
						setState(1073);
						singleExpression(19);
						}
						break;
					case 11:
						{
						_localctx = new LogicalAndExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1074);
						if (!(precpred(_ctx, 17))) throw new FailedPredicateException(this, "precpred(_ctx, 17)");
						setState(1075);
						match(And);
						setState(1076);
						singleExpression(18);
						}
						break;
					case 12:
						{
						_localctx = new LogicalOrExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1077);
						if (!(precpred(_ctx, 16))) throw new FailedPredicateException(this, "precpred(_ctx, 16)");
						setState(1078);
						match(Or);
						setState(1079);
						singleExpression(17);
						}
						break;
					case 13:
						{
						_localctx = new TernaryExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1080);
						if (!(precpred(_ctx, 15))) throw new FailedPredicateException(this, "precpred(_ctx, 15)");
						setState(1081);
						match(QuestionMark);
						setState(1082);
						singleExpression(0);
						setState(1083);
						match(Colon);
						setState(1084);
						singleExpression(16);
						}
						break;
					case 14:
						{
						_localctx = new AssignmentExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1086);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(1087);
						match(Assign);
						setState(1088);
						singleExpression(13);
						}
						break;
					case 15:
						{
						_localctx = new AssignmentOperatorExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1089);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(1090);
						assignmentOperator();
						setState(1091);
						singleExpression(12);
						}
						break;
					case 16:
						{
						_localctx = new MemberIndexExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1093);
						if (!(precpred(_ctx, 47))) throw new FailedPredicateException(this, "precpred(_ctx, 47)");
						setState(1094);
						match(OpenBracket);
						setState(1095);
						expressionSequence();
						setState(1096);
						match(CloseBracket);
						}
						break;
					case 17:
						{
						_localctx = new MemberDotExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1098);
						if (!(precpred(_ctx, 46))) throw new FailedPredicateException(this, "precpred(_ctx, 46)");
						setState(1099);
						match(Dot);
						setState(1100);
						identifierName();
						}
						break;
					case 18:
						{
						_localctx = new CallTemplateLiteralExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1101);
						if (!(precpred(_ctx, 45))) throw new FailedPredicateException(this, "precpred(_ctx, 45)");
						setState(1102);
						templateLiteral();
						}
						break;
					case 19:
						{
						_localctx = new ArgumentsExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1103);
						if (!(precpred(_ctx, 41))) throw new FailedPredicateException(this, "precpred(_ctx, 41)");
						setState(1104);
						arguments();
						}
						break;
					case 20:
						{
						_localctx = new PostIncrementExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1105);
						if (!(precpred(_ctx, 38))) throw new FailedPredicateException(this, "precpred(_ctx, 38)");
						setState(1106);
						if (!(!here(LineTerminator))) throw new FailedPredicateException(this, "!here(LineTerminator)");
						setState(1107);
						match(PlusPlus);
						}
						break;
					case 21:
						{
						_localctx = new PostDecreaseExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1108);
						if (!(precpred(_ctx, 37))) throw new FailedPredicateException(this, "precpred(_ctx, 37)");
						setState(1109);
						if (!(!here(LineTerminator))) throw new FailedPredicateException(this, "!here(LineTerminator)");
						setState(1110);
						match(MinusMinus);
						}
						break;
					}
					} 
				}
				setState(1115);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterScript(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitScript(this);
		}
	}

	public final ScriptContext script() throws RecognitionException {
		ScriptContext _localctx = new ScriptContext(_ctx, getState());
		enterRule(_localctx, 182, RULE_script);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1117);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,93,_ctx) ) {
			case 1:
				{
				setState(1116);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterScriptBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitScriptBody(this);
		}
	}

	public final ScriptBodyContext scriptBody() throws RecognitionException {
		ScriptBodyContext _localctx = new ScriptBodyContext(_ctx, getState());
		enterRule(_localctx, 184, RULE_scriptBody);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1119);
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
		public ModuleBodyContext moduleBody() {
			return getRuleContext(ModuleBodyContext.class,0);
		}
		public ModuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_module; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterModule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitModule(this);
		}
	}

	public final ModuleContext module() throws RecognitionException {
		ModuleContext _localctx = new ModuleContext(_ctx, getState());
		enterRule(_localctx, 186, RULE_module);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1122);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,94,_ctx) ) {
			case 1:
				{
				setState(1121);
				moduleBody();
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

	public static class ModuleBodyContext extends ParserRuleContext {
		public ModuleItemListContext moduleItemList() {
			return getRuleContext(ModuleItemListContext.class,0);
		}
		public ModuleBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_moduleBody; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterModuleBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitModuleBody(this);
		}
	}

	public final ModuleBodyContext moduleBody() throws RecognitionException {
		ModuleBodyContext _localctx = new ModuleBodyContext(_ctx, getState());
		enterRule(_localctx, 188, RULE_moduleBody);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1124);
			moduleItemList();
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

	public static class ModuleItemListContext extends ParserRuleContext {
		public List<ModuleItemContext> moduleItem() {
			return getRuleContexts(ModuleItemContext.class);
		}
		public ModuleItemContext moduleItem(int i) {
			return getRuleContext(ModuleItemContext.class,i);
		}
		public ModuleItemListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_moduleItemList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterModuleItemList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitModuleItemList(this);
		}
	}

	public final ModuleItemListContext moduleItemList() throws RecognitionException {
		ModuleItemListContext _localctx = new ModuleItemListContext(_ctx, getState());
		enterRule(_localctx, 190, RULE_moduleItemList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1127); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(1126);
					moduleItem();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(1129); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,95,_ctx);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterModuleItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitModuleItem(this);
		}
	}

	public final ModuleItemContext moduleItem() throws RecognitionException {
		ModuleItemContext _localctx = new ModuleItemContext(_ctx, getState());
		enterRule(_localctx, 192, RULE_moduleItem);
		try {
			setState(1134);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,96,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1131);
				importDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1132);
				exportDeclaration();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1133);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterImportDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitImportDeclaration(this);
		}
	}

	public final ImportDeclarationContext importDeclaration() throws RecognitionException {
		ImportDeclarationContext _localctx = new ImportDeclarationContext(_ctx, getState());
		enterRule(_localctx, 194, RULE_importDeclaration);
		try {
			setState(1145);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,97,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1136);
				match(Import);
				setState(1137);
				importClause();
				setState(1138);
				fromClause();
				setState(1139);
				eos();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1141);
				match(Import);
				setState(1142);
				moduleSpecifier();
				setState(1143);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterImportClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitImportClause(this);
		}
	}

	public final ImportClauseContext importClause() throws RecognitionException {
		ImportClauseContext _localctx = new ImportClauseContext(_ctx, getState());
		enterRule(_localctx, 196, RULE_importClause);
		try {
			setState(1158);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,98,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1147);
				importedDefaultBinding();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1148);
				nameSpaceImport();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1149);
				namedImports();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1150);
				importedDefaultBinding();
				setState(1151);
				match(Comma);
				setState(1152);
				nameSpaceImport();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1154);
				importedDefaultBinding();
				setState(1155);
				match(Comma);
				setState(1156);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterImportedDefaultBinding(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitImportedDefaultBinding(this);
		}
	}

	public final ImportedDefaultBindingContext importedDefaultBinding() throws RecognitionException {
		ImportedDefaultBindingContext _localctx = new ImportedDefaultBindingContext(_ctx, getState());
		enterRule(_localctx, 198, RULE_importedDefaultBinding);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1160);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterNameSpaceImport(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitNameSpaceImport(this);
		}
	}

	public final NameSpaceImportContext nameSpaceImport() throws RecognitionException {
		NameSpaceImportContext _localctx = new NameSpaceImportContext(_ctx, getState());
		enterRule(_localctx, 200, RULE_nameSpaceImport);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1162);
			match(Multiply);
			setState(1163);
			match(T__2);
			setState(1164);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterNamedImports(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitNamedImports(this);
		}
	}

	public final NamedImportsContext namedImports() throws RecognitionException {
		NamedImportsContext _localctx = new NamedImportsContext(_ctx, getState());
		enterRule(_localctx, 202, RULE_namedImports);
		try {
			setState(1177);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,99,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1166);
				match(OpenBrace);
				setState(1167);
				match(CloseBrace);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1168);
				match(OpenBrace);
				setState(1169);
				importsList();
				setState(1170);
				match(CloseBrace);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1172);
				match(OpenBrace);
				setState(1173);
				importsList();
				setState(1174);
				match(Comma);
				setState(1175);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterFromClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitFromClause(this);
		}
	}

	public final FromClauseContext fromClause() throws RecognitionException {
		FromClauseContext _localctx = new FromClauseContext(_ctx, getState());
		enterRule(_localctx, 204, RULE_fromClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1179);
			match(T__3);
			setState(1180);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterImportsList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitImportsList(this);
		}
	}

	public final ImportsListContext importsList() throws RecognitionException {
		ImportsListContext _localctx = new ImportsListContext(_ctx, getState());
		enterRule(_localctx, 206, RULE_importsList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1182);
			importSpecifier();
			setState(1187);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,100,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1183);
					match(Comma);
					setState(1184);
					importSpecifier();
					}
					} 
				}
				setState(1189);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,100,_ctx);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterImportSpecifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitImportSpecifier(this);
		}
	}

	public final ImportSpecifierContext importSpecifier() throws RecognitionException {
		ImportSpecifierContext _localctx = new ImportSpecifierContext(_ctx, getState());
		enterRule(_localctx, 208, RULE_importSpecifier);
		try {
			setState(1195);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,101,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1190);
				importedBinding();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1191);
				identifierName();
				setState(1192);
				match(T__2);
				setState(1193);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterModuleSpecifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitModuleSpecifier(this);
		}
	}

	public final ModuleSpecifierContext moduleSpecifier() throws RecognitionException {
		ModuleSpecifierContext _localctx = new ModuleSpecifierContext(_ctx, getState());
		enterRule(_localctx, 210, RULE_moduleSpecifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1197);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterImportedBinding(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitImportedBinding(this);
		}
	}

	public final ImportedBindingContext importedBinding() throws RecognitionException {
		ImportedBindingContext _localctx = new ImportedBindingContext(_ctx, getState());
		enterRule(_localctx, 212, RULE_importedBinding);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1199);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterExportDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitExportDeclaration(this);
		}
	}

	public final ExportDeclarationContext exportDeclaration() throws RecognitionException {
		ExportDeclarationContext _localctx = new ExportDeclarationContext(_ctx, getState());
		enterRule(_localctx, 214, RULE_exportDeclaration);
		try {
			setState(1231);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,102,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1201);
				match(Export);
				setState(1202);
				match(Multiply);
				setState(1203);
				fromClause();
				setState(1204);
				match(SemiColon);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1206);
				match(Export);
				setState(1207);
				exportClause();
				setState(1208);
				fromClause();
				setState(1209);
				match(SemiColon);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1211);
				match(Export);
				setState(1212);
				exportClause();
				setState(1213);
				match(SemiColon);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1215);
				match(Export);
				setState(1216);
				variableStatement();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1217);
				match(Export);
				setState(1218);
				declaration();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1219);
				match(Export);
				setState(1220);
				match(Default);
				setState(1221);
				hoistableDeclaration();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1222);
				match(Export);
				setState(1223);
				match(Default);
				setState(1224);
				classDeclaration();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(1225);
				match(Export);
				setState(1226);
				match(Default);
				setState(1227);
				if (!((_input.LA(1) != Function) && (_input.LA(1) != Class))) throw new FailedPredicateException(this, "(_input.LA(1) != Function) && (_input.LA(1) != Class)");
				setState(1228);
				singleExpression(0);
				setState(1229);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterExportClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitExportClause(this);
		}
	}

	public final ExportClauseContext exportClause() throws RecognitionException {
		ExportClauseContext _localctx = new ExportClauseContext(_ctx, getState());
		enterRule(_localctx, 216, RULE_exportClause);
		try {
			setState(1244);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,103,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1233);
				match(OpenBrace);
				setState(1234);
				match(CloseBrace);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1235);
				match(OpenBrace);
				setState(1236);
				exportsList();
				setState(1237);
				match(CloseBrace);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1239);
				match(OpenBrace);
				setState(1240);
				exportsList();
				setState(1241);
				match(Comma);
				setState(1242);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterExportsList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitExportsList(this);
		}
	}

	public final ExportsListContext exportsList() throws RecognitionException {
		ExportsListContext _localctx = new ExportsListContext(_ctx, getState());
		enterRule(_localctx, 218, RULE_exportsList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1246);
			exportSpecifier();
			setState(1251);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,104,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1247);
					match(Comma);
					setState(1248);
					exportSpecifier();
					}
					} 
				}
				setState(1253);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,104,_ctx);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterExportSpecifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitExportSpecifier(this);
		}
	}

	public final ExportSpecifierContext exportSpecifier() throws RecognitionException {
		ExportSpecifierContext _localctx = new ExportSpecifierContext(_ctx, getState());
		enterRule(_localctx, 220, RULE_exportSpecifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1254);
			identifierName();
			setState(1257);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__2) {
				{
				setState(1255);
				match(T__2);
				setState(1256);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterAssignmentOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitAssignmentOperator(this);
		}
	}

	public final AssignmentOperatorContext assignmentOperator() throws RecognitionException {
		AssignmentOperatorContext _localctx = new AssignmentOperatorContext(_ctx, getState());
		enterRule(_localctx, 222, RULE_assignmentOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1259);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitLiteral(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 224, RULE_literal);
		int _la;
		try {
			setState(1263);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NullLiteral:
			case BooleanLiteral:
			case StringLiteral:
				enterOuterAlt(_localctx, 1);
				{
				setState(1261);
				_la = _input.LA(1);
				if ( !(((((_la - 56)) & ~0x3f) == 0 && ((1L << (_la - 56)) & ((1L << (NullLiteral - 56)) | (1L << (BooleanLiteral - 56)) | (1L << (StringLiteral - 56)))) != 0)) ) {
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
				setState(1262);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterNumericLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitNumericLiteral(this);
		}
	}

	public final NumericLiteralContext numericLiteral() throws RecognitionException {
		NumericLiteralContext _localctx = new NumericLiteralContext(_ctx, getState());
		enterRule(_localctx, 226, RULE_numericLiteral);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1265);
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

	public static class IdentifierNameContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(JSParser.Identifier, 0); }
		public ReservedWordContext reservedWord() {
			return getRuleContext(ReservedWordContext.class,0);
		}
		public IdentifierNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifierName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterIdentifierName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitIdentifierName(this);
		}
	}

	public final IdentifierNameContext identifierName() throws RecognitionException {
		IdentifierNameContext _localctx = new IdentifierNameContext(_ctx, getState());
		enterRule(_localctx, 228, RULE_identifierName);
		try {
			setState(1269);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(1267);
				match(Identifier);
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
				setState(1268);
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
		public TerminalNode Identifier() { return getToken(JSParser.Identifier, 0); }
		public IdentifierReferenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifierReference; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterIdentifierReference(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitIdentifierReference(this);
		}
	}

	public final IdentifierReferenceContext identifierReference() throws RecognitionException {
		IdentifierReferenceContext _localctx = new IdentifierReferenceContext(_ctx, getState());
		enterRule(_localctx, 230, RULE_identifierReference);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1271);
			match(Identifier);
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
		public TerminalNode Identifier() { return getToken(JSParser.Identifier, 0); }
		public BindingIdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bindingIdentifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterBindingIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitBindingIdentifier(this);
		}
	}

	public final BindingIdentifierContext bindingIdentifier() throws RecognitionException {
		BindingIdentifierContext _localctx = new BindingIdentifierContext(_ctx, getState());
		enterRule(_localctx, 232, RULE_bindingIdentifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1273);
			match(Identifier);
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
		public TerminalNode Identifier() { return getToken(JSParser.Identifier, 0); }
		public LabelIdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_labelIdentifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterLabelIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitLabelIdentifier(this);
		}
	}

	public final LabelIdentifierContext labelIdentifier() throws RecognitionException {
		LabelIdentifierContext _localctx = new LabelIdentifierContext(_ctx, getState());
		enterRule(_localctx, 234, RULE_labelIdentifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1275);
			match(Identifier);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterReservedWord(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitReservedWord(this);
		}
	}

	public final ReservedWordContext reservedWord() throws RecognitionException {
		ReservedWordContext _localctx = new ReservedWordContext(_ctx, getState());
		enterRule(_localctx, 236, RULE_reservedWord);
		int _la;
		try {
			setState(1280);
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
				setState(1277);
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
				setState(1278);
				futureReservedWord();
				}
				break;
			case NullLiteral:
			case BooleanLiteral:
				enterOuterAlt(_localctx, 3);
				{
				setState(1279);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterKeyword(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitKeyword(this);
		}
	}

	public final KeywordContext keyword() throws RecognitionException {
		KeywordContext _localctx = new KeywordContext(_ctx, getState());
		enterRule(_localctx, 238, RULE_keyword);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1282);
			_la = _input.LA(1);
			if ( !(((((_la - 62)) & ~0x3f) == 0 && ((1L << (_la - 62)) & ((1L << (Break - 62)) | (1L << (Do - 62)) | (1L << (Instanceof - 62)) | (1L << (Typeof - 62)) | (1L << (Case - 62)) | (1L << (Else - 62)) | (1L << (New - 62)) | (1L << (Var - 62)) | (1L << (Catch - 62)) | (1L << (Finally - 62)) | (1L << (Return - 62)) | (1L << (Void - 62)) | (1L << (Continue - 62)) | (1L << (For - 62)) | (1L << (Switch - 62)) | (1L << (While - 62)) | (1L << (Debugger - 62)) | (1L << (Function - 62)) | (1L << (This - 62)) | (1L << (With - 62)) | (1L << (Default - 62)) | (1L << (If - 62)) | (1L << (Throw - 62)) | (1L << (Delete - 62)) | (1L << (In - 62)) | (1L << (Try - 62)) | (1L << (Export - 62)) | (1L << (Class - 62)) | (1L << (Extends - 62)) | (1L << (Const - 62)) | (1L << (Super - 62)) | (1L << (Yield - 62)) | (1L << (Import - 62)))) != 0)) ) {
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterFutureReservedWord(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitFutureReservedWord(this);
		}
	}

	public final FutureReservedWordContext futureReservedWord() throws RecognitionException {
		FutureReservedWordContext _localctx = new FutureReservedWordContext(_ctx, getState());
		enterRule(_localctx, 240, RULE_futureReservedWord);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1284);
			_la = _input.LA(1);
			if ( !(((((_la - 97)) & ~0x3f) == 0 && ((1L << (_la - 97)) & ((1L << (Enum - 97)) | (1L << (Await - 97)) | (1L << (Implements - 97)) | (1L << (Private - 97)) | (1L << (Public - 97)) | (1L << (Interface - 97)) | (1L << (Package - 97)) | (1L << (Protected - 97)))) != 0)) ) {
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

	public static class GetterContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(JSParser.Identifier, 0); }
		public PropertyNameContext propertyName() {
			return getRuleContext(PropertyNameContext.class,0);
		}
		public GetterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_getter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterGetter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitGetter(this);
		}
	}

	public final GetterContext getter() throws RecognitionException {
		GetterContext _localctx = new GetterContext(_ctx, getState());
		enterRule(_localctx, 242, RULE_getter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1286);
			if (!(_input.LT(1).getText().equals("get"))) throw new FailedPredicateException(this, "_input.LT(1).getText().equals(\"get\")");
			setState(1287);
			match(Identifier);
			setState(1288);
			propertyName();
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

	public static class SetterContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(JSParser.Identifier, 0); }
		public PropertyNameContext propertyName() {
			return getRuleContext(PropertyNameContext.class,0);
		}
		public SetterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_setter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).enterSetter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitSetter(this);
		}
	}

	public final SetterContext setter() throws RecognitionException {
		SetterContext _localctx = new SetterContext(_ctx, getState());
		enterRule(_localctx, 244, RULE_setter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1290);
			if (!(_input.LT(1).getText().equals("set"))) throw new FailedPredicateException(this, "_input.LT(1).getText().equals(\"set\")");
			setState(1291);
			match(Identifier);
			setState(1292);
			propertyName();
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterEos(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitEos(this);
		}
	}

	public final EosContext eos() throws RecognitionException {
		EosContext _localctx = new EosContext(_ctx, getState());
		enterRule(_localctx, 246, RULE_eos);
		try {
			setState(1298);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,109,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1294);
				match(SemiColon);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1295);
				match(EOF);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1296);
				if (!(lineTerminatorAhead())) throw new FailedPredicateException(this, "lineTerminatorAhead()");
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1297);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterEof(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitEof(this);
		}
	}

	public final EofContext eof() throws RecognitionException {
		EofContext _localctx = new EofContext(_ctx, getState());
		enterRule(_localctx, 248, RULE_eof);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1300);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterTemplateLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitTemplateLiteral(this);
		}
	}

	public final TemplateLiteralContext templateLiteral() throws RecognitionException {
		TemplateLiteralContext _localctx = new TemplateLiteralContext(_ctx, getState());
		enterRule(_localctx, 250, RULE_templateLiteral);
		try {
			setState(1307);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NoSubstitutionTemplate:
				enterOuterAlt(_localctx, 1);
				{
				setState(1302);
				match(NoSubstitutionTemplate);
				}
				break;
			case TemplateHead:
				enterOuterAlt(_localctx, 2);
				{
				setState(1303);
				match(TemplateHead);
				setState(1304);
				expressionSequence();
				setState(1305);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterTemplateSpans(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitTemplateSpans(this);
		}
	}

	public final TemplateSpansContext templateSpans() throws RecognitionException {
		TemplateSpansContext _localctx = new TemplateSpansContext(_ctx, getState());
		enterRule(_localctx, 252, RULE_templateSpans);
		try {
			setState(1313);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TemplateTail:
				enterOuterAlt(_localctx, 1);
				{
				setState(1309);
				match(TemplateTail);
				}
				break;
			case TemplateMiddle:
				enterOuterAlt(_localctx, 2);
				{
				setState(1310);
				templateMiddleList(0);
				setState(1311);
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
			if ( listener instanceof JSListener ) ((JSListener)listener).enterTemplateMiddleList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSListener ) ((JSListener)listener).exitTemplateMiddleList(this);
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
			setState(1316);
			match(TemplateMiddle);
			setState(1317);
			expressionSequence();
			}
			_ctx.stop = _input.LT(-1);
			setState(1324);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,112,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new TemplateMiddleListContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_templateMiddleList);
					setState(1319);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(1320);
					match(TemplateMiddle);
					setState(1321);
					expressionSequence();
					}
					} 
				}
				setState(1326);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,112,_ctx);
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
		case 33:
			return continueStatement_sempred((ContinueStatementContext)_localctx, predIndex);
		case 34:
			return breakStatement_sempred((BreakStatementContext)_localctx, predIndex);
		case 35:
			return returnStatement_sempred((ReturnStatementContext)_localctx, predIndex);
		case 44:
			return throwStatement_sempred((ThrowStatementContext)_localctx, predIndex);
		case 59:
			return arrowFunction_sempred((ArrowFunctionContext)_localctx, predIndex);
		case 61:
			return conciseBody_sempred((ConciseBodyContext)_localctx, predIndex);
		case 63:
			return methodDefinition_sempred((MethodDefinitionContext)_localctx, predIndex);
		case 77:
			return elementList_sempred((ElementListContext)_localctx, predIndex);
		case 90:
			return singleExpression_sempred((SingleExpressionContext)_localctx, predIndex);
		case 107:
			return exportDeclaration_sempred((ExportDeclarationContext)_localctx, predIndex);
		case 121:
			return getter_sempred((GetterContext)_localctx, predIndex);
		case 122:
			return setter_sempred((SetterContext)_localctx, predIndex);
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
	private boolean methodDefinition_sempred(MethodDefinitionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 10:
			return _input.LT(1).getText().equals("get");
		case 11:
			return _input.LT(1).getText().equals("set");
		}
		return true;
	}
	private boolean elementList_sempred(ElementListContext _localctx, int predIndex) {
		switch (predIndex) {
		case 12:
			return precpred(_ctx, 2);
		case 13:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean singleExpression_sempred(SingleExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 14:
			return _input.LT(1).getText().equals("target");
		case 15:
			return !here(LineTerminator);
		case 16:
			return precpred(_ctx, 27);
		case 17:
			return precpred(_ctx, 26);
		case 18:
			return precpred(_ctx, 25);
		case 19:
			return precpred(_ctx, 24);
		case 20:
			return precpred(_ctx, 23);
		case 21:
			return precpred(_ctx, 22);
		case 22:
			return precpred(_ctx, 21);
		case 23:
			return precpred(_ctx, 20);
		case 24:
			return precpred(_ctx, 19);
		case 25:
			return precpred(_ctx, 18);
		case 26:
			return precpred(_ctx, 17);
		case 27:
			return precpred(_ctx, 16);
		case 28:
			return precpred(_ctx, 15);
		case 29:
			return precpred(_ctx, 12);
		case 30:
			return precpred(_ctx, 11);
		case 31:
			return precpred(_ctx, 47);
		case 32:
			return precpred(_ctx, 46);
		case 33:
			return precpred(_ctx, 45);
		case 34:
			return precpred(_ctx, 41);
		case 35:
			return precpred(_ctx, 38);
		case 36:
			return !here(LineTerminator);
		case 37:
			return precpred(_ctx, 37);
		case 38:
			return !here(LineTerminator);
		}
		return true;
	}
	private boolean exportDeclaration_sempred(ExportDeclarationContext _localctx, int predIndex) {
		switch (predIndex) {
		case 39:
			return (_input.LA(1) != Function) && (_input.LA(1) != Class);
		}
		return true;
	}
	private boolean getter_sempred(GetterContext _localctx, int predIndex) {
		switch (predIndex) {
		case 40:
			return _input.LT(1).getText().equals("get");
		}
		return true;
	}
	private boolean setter_sempred(SetterContext _localctx, int predIndex) {
		switch (predIndex) {
		case 41:
			return _input.LT(1).getText().equals("set");
		}
		return true;
	}
	private boolean eos_sempred(EosContext _localctx, int predIndex) {
		switch (predIndex) {
		case 42:
			return lineTerminatorAhead();
		case 43:
			return _input.LT(1).getType() == CloseBrace;
		}
		return true;
	}
	private boolean templateMiddleList_sempred(TemplateMiddleListContext _localctx, int predIndex) {
		switch (predIndex) {
		case 44:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3t\u0532\4\2\t\2\4"+
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
		"\4\u0081\t\u0081\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\5\3\u0114\n\3\3\4\3\4\3\4\5\4\u0119\n\4\3\5\3\5\5\5\u011d"+
		"\n\5\3\6\3\6\5\6\u0121\n\6\3\7\3\7\3\b\3\b\5\b\u0127\n\b\3\b\3\b\3\t\6"+
		"\t\u012c\n\t\r\t\16\t\u012d\3\n\3\n\5\n\u0132\n\n\3\13\3\13\3\13\3\13"+
		"\3\f\3\f\3\r\3\r\3\r\7\r\u013d\n\r\f\r\16\r\u0140\13\r\3\16\3\16\5\16"+
		"\u0144\n\16\3\16\3\16\3\16\5\16\u0149\n\16\3\17\3\17\3\17\3\17\3\20\3"+
		"\20\3\20\7\20\u0152\n\20\f\20\16\20\u0155\13\20\3\21\3\21\5\21\u0159\n"+
		"\21\3\21\3\21\3\21\5\21\u015e\n\21\3\22\3\22\5\22\u0162\n\22\3\23\3\23"+
		"\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\5\23\u016f\n\23\3\24\3\24"+
		"\5\24\u0173\n\24\3\24\5\24\u0176\n\24\3\24\3\24\3\24\3\24\3\24\3\24\3"+
		"\24\3\24\3\24\5\24\u0181\n\24\3\24\5\24\u0184\n\24\3\24\3\24\5\24\u0188"+
		"\n\24\3\25\3\25\3\25\7\25\u018d\n\25\f\25\16\25\u0190\13\25\3\26\3\26"+
		"\3\26\7\26\u0195\n\26\f\26\16\26\u0198\13\26\3\27\5\27\u019b\n\27\3\27"+
		"\3\27\3\30\3\30\3\30\3\30\3\30\5\30\u01a4\n\30\3\31\3\31\3\31\5\31\u01a9"+
		"\n\31\5\31\u01ab\n\31\3\32\3\32\5\32\u01af\n\32\3\33\3\33\3\33\3\34\3"+
		"\34\3\34\3\35\3\35\3\36\3\36\3\36\3\36\3\37\3\37\3\37\3\37\3\37\3\37\3"+
		"\37\5\37\u01c4\n\37\3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3"+
		" \3 \5 \u01d8\n \3 \3 \5 \u01dc\n \3 \3 \5 \u01e0\n \3 \3 \3 \3 \3 \3"+
		" \3 \3 \5 \u01ea\n \3 \3 \5 \u01ee\n \3 \3 \3 \3 \3 \3 \3 \5 \u01f7\n"+
		" \3 \3 \5 \u01fb\n \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3"+
		" \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3"+
		" \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \5 \u0234\n \3!\3!\3!\3"+
		"\"\3\"\5\"\u023b\n\"\3#\3#\3#\5#\u0240\n#\3#\3#\3$\3$\3$\5$\u0247\n$\3"+
		"$\3$\3%\3%\3%\5%\u024e\n%\3%\3%\3&\3&\3&\3&\3&\3&\3\'\3\'\3\'\3\'\3\'"+
		"\3\'\3(\3(\5(\u0260\n(\3(\3(\5(\u0264\n(\5(\u0266\n(\3(\3(\3)\6)\u026b"+
		"\n)\r)\16)\u026c\3*\3*\3*\3*\5*\u0273\n*\3+\3+\3+\5+\u0278\n+\3,\3,\3"+
		",\3,\3-\3-\5-\u0280\n-\3.\3.\3.\3.\3.\3/\3/\3/\3/\3/\3/\3/\3/\3/\3/\3"+
		"/\3/\3/\5/\u0294\n/\3\60\3\60\3\60\3\60\3\60\3\60\3\61\3\61\3\61\3\62"+
		"\3\62\5\62\u02a1\n\62\3\63\3\63\3\63\3\64\3\64\3\64\3\64\3\64\3\64\3\64"+
		"\3\64\3\64\3\65\3\65\3\66\3\66\5\66\u02b3\n\66\3\67\3\67\3\67\3\67\5\67"+
		"\u02b9\n\67\5\67\u02bb\n\67\38\38\38\78\u02c0\n8\f8\168\u02c3\138\39\3"+
		"9\3:\3:\3;\3;\3<\5<\u02cc\n<\3=\3=\3=\3=\3=\3>\3>\5>\u02d5\n>\3?\3?\3"+
		"?\3?\3?\3?\5?\u02dd\n?\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3@\3"+
		"@\3@\5@\u02f0\n@\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3"+
		"A\3A\3A\3A\3A\3A\3A\3A\3A\3A\3A\5A\u030e\nA\3B\3B\3C\3C\3C\3C\3C\3C\3"+
		"C\3C\3C\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3E\3E\3E\5E\u0328\nE\3E\3E\3E\3"+
		"E\3E\3E\3E\3F\3F\3G\3G\3G\3G\3H\3H\5H\u0339\nH\3H\3H\3I\5I\u033e\nI\3"+
		"I\3I\5I\u0342\nI\3I\3I\3J\3J\3J\3K\3K\3L\6L\u034c\nL\rL\16L\u034d\3M\3"+
		"M\3M\3M\5M\u0354\nM\3N\3N\5N\u0358\nN\3N\5N\u035b\nN\3N\5N\u035e\nN\3"+
		"N\3N\3O\3O\5O\u0364\nO\3O\3O\5O\u0368\nO\3O\5O\u036b\nO\3O\3O\3O\5O\u0370"+
		"\nO\3O\3O\3O\3O\5O\u0376\nO\3O\7O\u0379\nO\fO\16O\u037c\13O\3P\6P\u037f"+
		"\nP\rP\16P\u0380\3Q\3Q\3Q\3R\3R\3R\3R\3R\5R\u038b\nR\3R\3R\5R\u038f\n"+
		"R\3S\3S\3S\7S\u0394\nS\fS\16S\u0397\13S\3T\3T\3T\3T\3T\3T\3T\5T\u03a0"+
		"\nT\3U\3U\5U\u03a4\nU\3V\3V\3V\5V\u03a9\nV\3W\3W\3W\3W\3X\3X\3X\3Y\3Y"+
		"\5Y\u03b4\nY\3Y\3Y\3Z\5Z\u03b9\nZ\3Z\3Z\3Z\5Z\u03be\nZ\3Z\7Z\u03c1\nZ"+
		"\fZ\16Z\u03c4\13Z\3[\3[\3[\7[\u03c9\n[\f[\16[\u03cc\13[\3\\\3\\\3\\\5"+
		"\\\u03d1\n\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3"+
		"\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\5\\\u03eb\n\\\3\\\3\\\3\\\3\\\3"+
		"\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\5\\"+
		"\u0402\n\\\3\\\5\\\u0405\n\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3"+
		"\\\3\\\3\\\3\\\5\\\u0415\n\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3"+
		"\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\"+
		"\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3"+
		"\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\"+
		"\3\\\3\\\3\\\3\\\7\\\u045a\n\\\f\\\16\\\u045d\13\\\3]\5]\u0460\n]\3^\3"+
		"^\3_\5_\u0465\n_\3`\3`\3a\6a\u046a\na\ra\16a\u046b\3b\3b\3b\5b\u0471\n"+
		"b\3c\3c\3c\3c\3c\3c\3c\3c\3c\5c\u047c\nc\3d\3d\3d\3d\3d\3d\3d\3d\3d\3"+
		"d\3d\5d\u0489\nd\3e\3e\3f\3f\3f\3f\3g\3g\3g\3g\3g\3g\3g\3g\3g\3g\3g\5"+
		"g\u049c\ng\3h\3h\3h\3i\3i\3i\7i\u04a4\ni\fi\16i\u04a7\13i\3j\3j\3j\3j"+
		"\3j\5j\u04ae\nj\3k\3k\3l\3l\3m\3m\3m\3m\3m\3m\3m\3m\3m\3m\3m\3m\3m\3m"+
		"\3m\3m\3m\3m\3m\3m\3m\3m\3m\3m\3m\3m\3m\3m\3m\3m\5m\u04d2\nm\3n\3n\3n"+
		"\3n\3n\3n\3n\3n\3n\3n\3n\5n\u04df\nn\3o\3o\3o\7o\u04e4\no\fo\16o\u04e7"+
		"\13o\3p\3p\3p\5p\u04ec\np\3q\3q\3r\3r\5r\u04f2\nr\3s\3s\3t\3t\5t\u04f8"+
		"\nt\3u\3u\3v\3v\3w\3w\3x\3x\3x\5x\u0503\nx\3y\3y\3z\3z\3{\3{\3{\3{\3|"+
		"\3|\3|\3|\3}\3}\3}\3}\5}\u0515\n}\3~\3~\3\177\3\177\3\177\3\177\3\177"+
		"\5\177\u051e\n\177\3\u0080\3\u0080\3\u0080\3\u0080\5\u0080\u0524\n\u0080"+
		"\3\u0081\3\u0081\3\u0081\3\u0081\3\u0081\3\u0081\3\u0081\7\u0081\u052d"+
		"\n\u0081\f\u0081\16\u0081\u0530\13\u0081\3\u0081\2\5\u009c\u00b6\u0100"+
		"\u0082\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<"+
		">@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086\u0088\u008a"+
		"\u008c\u008e\u0090\u0092\u0094\u0096\u0098\u009a\u009c\u009e\u00a0\u00a2"+
		"\u00a4\u00a6\u00a8\u00aa\u00ac\u00ae\u00b0\u00b2\u00b4\u00b6\u00b8\u00ba"+
		"\u00bc\u00be\u00c0\u00c2\u00c4\u00c6\u00c8\u00ca\u00cc\u00ce\u00d0\u00d2"+
		"\u00d4\u00d6\u00d8\u00da\u00dc\u00de\u00e0\u00e2\u00e4\u00e6\u00e8\u00ea"+
		"\u00ec\u00ee\u00f0\u00f2\u00f4\u00f6\u00f8\u00fa\u00fc\u00fe\u0100\2\16"+
		"\4\2]]bb\3\2\34\36\3\2\30\31\3\2\37!\3\2\"%\3\2&)\3\2/9\4\2:;ll\3\2<?"+
		"\3\2:;\3\2@`\3\2cj\2\u057e\2\u0102\3\2\2\2\4\u0113\3\2\2\2\6\u0118\3\2"+
		"\2\2\b\u011c\3\2\2\2\n\u0120\3\2\2\2\f\u0122\3\2\2\2\16\u0124\3\2\2\2"+
		"\20\u012b\3\2\2\2\22\u0131\3\2\2\2\24\u0133\3\2\2\2\26\u0137\3\2\2\2\30"+
		"\u0139\3\2\2\2\32\u0148\3\2\2\2\34\u014a\3\2\2\2\36\u014e\3\2\2\2 \u015d"+
		"\3\2\2\2\"\u0161\3\2\2\2$\u016e\3\2\2\2&\u0187\3\2\2\2(\u0189\3\2\2\2"+
		"*\u0191\3\2\2\2,\u019a\3\2\2\2.\u01a3\3\2\2\2\60\u01aa\3\2\2\2\62\u01ac"+
		"\3\2\2\2\64\u01b0\3\2\2\2\66\u01b3\3\2\2\28\u01b6\3\2\2\2:\u01b8\3\2\2"+
		"\2<\u01bc\3\2\2\2>\u0233\3\2\2\2@\u0235\3\2\2\2B\u023a\3\2\2\2D\u023c"+
		"\3\2\2\2F\u0243\3\2\2\2H\u024a\3\2\2\2J\u0251\3\2\2\2L\u0257\3\2\2\2N"+
		"\u025d\3\2\2\2P\u026a\3\2\2\2R\u026e\3\2\2\2T\u0274\3\2\2\2V\u0279\3\2"+
		"\2\2X\u027f\3\2\2\2Z\u0281\3\2\2\2\\\u0293\3\2\2\2^\u0295\3\2\2\2`\u029b"+
		"\3\2\2\2b\u02a0\3\2\2\2d\u02a2\3\2\2\2f\u02a5\3\2\2\2h\u02ae\3\2\2\2j"+
		"\u02b2\3\2\2\2l\u02ba\3\2\2\2n\u02bc\3\2\2\2p\u02c4\3\2\2\2r\u02c6\3\2"+
		"\2\2t\u02c8\3\2\2\2v\u02cb\3\2\2\2x\u02cd\3\2\2\2z\u02d4\3\2\2\2|\u02dc"+
		"\3\2\2\2~\u02ef\3\2\2\2\u0080\u030d\3\2\2\2\u0082\u030f\3\2\2\2\u0084"+
		"\u0311\3\2\2\2\u0086\u031a\3\2\2\2\u0088\u0324\3\2\2\2\u008a\u0330\3\2"+
		"\2\2\u008c\u0332\3\2\2\2\u008e\u0336\3\2\2\2\u0090\u033d\3\2\2\2\u0092"+
		"\u0345\3\2\2\2\u0094\u0348\3\2\2\2\u0096\u034b\3\2\2\2\u0098\u0353\3\2"+
		"\2\2\u009a\u0355\3\2\2\2\u009c\u036a\3\2\2\2\u009e\u037e\3\2\2\2\u00a0"+
		"\u0382\3\2\2\2\u00a2\u038e\3\2\2\2\u00a4\u0390\3\2\2\2\u00a6\u039f\3\2"+
		"\2\2\u00a8\u03a3\3\2\2\2\u00aa\u03a8\3\2\2\2\u00ac\u03aa\3\2\2\2\u00ae"+
		"\u03ae\3\2\2\2\u00b0\u03b1\3\2\2\2\u00b2\u03b8\3\2\2\2\u00b4\u03c5\3\2"+
		"\2\2\u00b6\u0414\3\2\2\2\u00b8\u045f\3\2\2\2\u00ba\u0461\3\2\2\2\u00bc"+
		"\u0464\3\2\2\2\u00be\u0466\3\2\2\2\u00c0\u0469\3\2\2\2\u00c2\u0470\3\2"+
		"\2\2\u00c4\u047b\3\2\2\2\u00c6\u0488\3\2\2\2\u00c8\u048a\3\2\2\2\u00ca"+
		"\u048c\3\2\2\2\u00cc\u049b\3\2\2\2\u00ce\u049d\3\2\2\2\u00d0\u04a0\3\2"+
		"\2\2\u00d2\u04ad\3\2\2\2\u00d4\u04af\3\2\2\2\u00d6\u04b1\3\2\2\2\u00d8"+
		"\u04d1\3\2\2\2\u00da\u04de\3\2\2\2\u00dc\u04e0\3\2\2\2\u00de\u04e8\3\2"+
		"\2\2\u00e0\u04ed\3\2\2\2\u00e2\u04f1\3\2\2\2\u00e4\u04f3\3\2\2\2\u00e6"+
		"\u04f7\3\2\2\2\u00e8\u04f9\3\2\2\2\u00ea\u04fb\3\2\2\2\u00ec\u04fd\3\2"+
		"\2\2\u00ee\u0502\3\2\2\2\u00f0\u0504\3\2\2\2\u00f2\u0506\3\2\2\2\u00f4"+
		"\u0508\3\2\2\2\u00f6\u050c\3\2\2\2\u00f8\u0514\3\2\2\2\u00fa\u0516\3\2"+
		"\2\2\u00fc\u051d\3\2\2\2\u00fe\u0523\3\2\2\2\u0100\u0525\3\2\2\2\u0102"+
		"\u0103\5\u00bc_\2\u0103\u0104\7\2\2\3\u0104\3\3\2\2\2\u0105\u0114\5\f"+
		"\7\2\u0106\u0114\5\34\17\2\u0107\u0114\58\35\2\u0108\u0114\5:\36\2\u0109"+
		"\u0114\5<\37\2\u010a\u0114\5\n\6\2\u010b\u0114\5D#\2\u010c\u0114\5F$\2"+
		"\u010d\u0114\5H%\2\u010e\u0114\5J&\2\u010f\u0114\5V,\2\u0110\u0114\5Z"+
		".\2\u0111\u0114\5\\/\2\u0112\u0114\5d\63\2\u0113\u0105\3\2\2\2\u0113\u0106"+
		"\3\2\2\2\u0113\u0107\3\2\2\2\u0113\u0108\3\2\2\2\u0113\u0109\3\2\2\2\u0113"+
		"\u010a\3\2\2\2\u0113\u010b\3\2\2\2\u0113\u010c\3\2\2\2\u0113\u010d\3\2"+
		"\2\2\u0113\u010e\3\2\2\2\u0113\u010f\3\2\2\2\u0113\u0110\3\2\2\2\u0113"+
		"\u0111\3\2\2\2\u0113\u0112\3\2\2\2\u0114\5\3\2\2\2\u0115\u0119\5\b\5\2"+
		"\u0116\u0119\5\u008cG\2\u0117\u0119\5\24\13\2\u0118\u0115\3\2\2\2\u0118"+
		"\u0116\3\2\2\2\u0118\u0117\3\2\2\2\u0119\7\3\2\2\2\u011a\u011d\5f\64\2"+
		"\u011b\u011d\5\u0086D\2\u011c\u011a\3\2\2\2\u011c\u011b\3\2\2\2\u011d"+
		"\t\3\2\2\2\u011e\u0121\5> \2\u011f\u0121\5L\'\2\u0120\u011e\3\2\2\2\u0120"+
		"\u011f\3\2\2\2\u0121\13\3\2\2\2\u0122\u0123\5\16\b\2\u0123\r\3\2\2\2\u0124"+
		"\u0126\7\r\2\2\u0125\u0127\5\20\t\2\u0126\u0125\3\2\2\2\u0126\u0127\3"+
		"\2\2\2\u0127\u0128\3\2\2\2\u0128\u0129\7\16\2\2\u0129\17\3\2\2\2\u012a"+
		"\u012c\5\22\n\2\u012b\u012a\3\2\2\2\u012c\u012d\3\2\2\2\u012d\u012b\3"+
		"\2\2\2\u012d\u012e\3\2\2\2\u012e\21\3\2\2\2\u012f\u0132\5\4\3\2\u0130"+
		"\u0132\5\6\4\2\u0131\u012f\3\2\2\2\u0131\u0130\3\2\2\2\u0132\23\3\2\2"+
		"\2\u0133\u0134\5\26\f\2\u0134\u0135\5\30\r\2\u0135\u0136\5\u00f8}\2\u0136"+
		"\25\3\2\2\2\u0137\u0138\t\2\2\2\u0138\27\3\2\2\2\u0139\u013e\5\32\16\2"+
		"\u013a\u013b\7\20\2\2\u013b\u013d\5\32\16\2\u013c\u013a\3\2\2\2\u013d"+
		"\u0140\3\2\2\2\u013e\u013c\3\2\2\2\u013e\u013f\3\2\2\2\u013f\31\3\2\2"+
		"\2\u0140\u013e\3\2\2\2\u0141\u0143\5\u00eav\2\u0142\u0144\5\66\34\2\u0143"+
		"\u0142\3\2\2\2\u0143\u0144\3\2\2\2\u0144\u0149\3\2\2\2\u0145\u0146\5\""+
		"\22\2\u0146\u0147\5\66\34\2\u0147\u0149\3\2\2\2\u0148\u0141\3\2\2\2\u0148"+
		"\u0145\3\2\2\2\u0149\33\3\2\2\2\u014a\u014b\7G\2\2\u014b\u014c\5\36\20"+
		"\2\u014c\u014d\5\u00f8}\2\u014d\35\3\2\2\2\u014e\u0153\5 \21\2\u014f\u0150"+
		"\7\20\2\2\u0150\u0152\5 \21\2\u0151\u014f\3\2\2\2\u0152\u0155\3\2\2\2"+
		"\u0153\u0151\3\2\2\2\u0153\u0154\3\2\2\2\u0154\37\3\2\2\2\u0155\u0153"+
		"\3\2\2\2\u0156\u0158\5\u00eav\2\u0157\u0159\5\66\34\2\u0158\u0157\3\2"+
		"\2\2\u0158\u0159\3\2\2\2\u0159\u015e\3\2\2\2\u015a\u015b\5\"\22\2\u015b"+
		"\u015c\5\66\34\2\u015c\u015e\3\2\2\2\u015d\u0156\3\2\2\2\u015d\u015a\3"+
		"\2\2\2\u015e!\3\2\2\2\u015f\u0162\5$\23\2\u0160\u0162\5&\24\2\u0161\u015f"+
		"\3\2\2\2\u0161\u0160\3\2\2\2\u0162#\3\2\2\2\u0163\u0164\7\r\2\2\u0164"+
		"\u016f\7\16\2\2\u0165\u0166\7\r\2\2\u0166\u0167\5(\25\2\u0167\u0168\7"+
		"\16\2\2\u0168\u016f\3\2\2\2\u0169\u016a\7\r\2\2\u016a\u016b\5(\25\2\u016b"+
		"\u016c\7\20\2\2\u016c\u016d\7\16\2\2\u016d\u016f\3\2\2\2\u016e\u0163\3"+
		"\2\2\2\u016e\u0165\3\2\2\2\u016e\u0169\3\2\2\2\u016f%\3\2\2\2\u0170\u0172"+
		"\7\t\2\2\u0171\u0173\5\u009eP\2\u0172\u0171\3\2\2\2\u0172\u0173\3\2\2"+
		"\2\u0173\u0175\3\2\2\2\u0174\u0176\5\64\33\2\u0175\u0174\3\2\2\2\u0175"+
		"\u0176\3\2\2\2\u0176\u0177\3\2\2\2\u0177\u0188\7\n\2\2\u0178\u0179\7\t"+
		"\2\2\u0179\u017a\5*\26\2\u017a\u017b\7\n\2\2\u017b\u0188\3\2\2\2\u017c"+
		"\u017d\7\t\2\2\u017d\u017e\5*\26\2\u017e\u0180\7\20\2\2\u017f\u0181\5"+
		"\u009eP\2\u0180\u017f\3\2\2\2\u0180\u0181\3\2\2\2\u0181\u0183\3\2\2\2"+
		"\u0182\u0184\5\64\33\2\u0183\u0182\3\2\2\2\u0183\u0184\3\2\2\2\u0184\u0185"+
		"\3\2\2\2\u0185\u0186\7\n\2\2\u0186\u0188\3\2\2\2\u0187\u0170\3\2\2\2\u0187"+
		"\u0178\3\2\2\2\u0187\u017c\3\2\2\2\u0188\'\3\2\2\2\u0189\u018e\5.\30\2"+
		"\u018a\u018b\7\20\2\2\u018b\u018d\5.\30\2\u018c\u018a\3\2\2\2\u018d\u0190"+
		"\3\2\2\2\u018e\u018c\3\2\2\2\u018e\u018f\3\2\2\2\u018f)\3\2\2\2\u0190"+
		"\u018e\3\2\2\2\u0191\u0196\5,\27\2\u0192\u0193\7\20\2\2\u0193\u0195\5"+
		",\27\2\u0194\u0192\3\2\2\2\u0195\u0198\3\2\2\2\u0196\u0194\3\2\2\2\u0196"+
		"\u0197\3\2\2\2\u0197+\3\2\2\2\u0198\u0196\3\2\2\2\u0199\u019b\5\u009e"+
		"P\2\u019a\u0199\3\2\2\2\u019a\u019b\3\2\2\2\u019b\u019c\3\2\2\2\u019c"+
		"\u019d\5\60\31\2\u019d-\3\2\2\2\u019e\u01a4\5\62\32\2\u019f\u01a0\5\u00a8"+
		"U\2\u01a0\u01a1\7\24\2\2\u01a1\u01a2\5\60\31\2\u01a2\u01a4\3\2\2\2\u01a3"+
		"\u019e\3\2\2\2\u01a3\u019f\3\2\2\2\u01a4/\3\2\2\2\u01a5\u01ab\5\62\32"+
		"\2\u01a6\u01a8\5\"\22\2\u01a7\u01a9\5\66\34\2\u01a8\u01a7\3\2\2\2\u01a8"+
		"\u01a9\3\2\2\2\u01a9\u01ab\3\2\2\2\u01aa\u01a5\3\2\2\2\u01aa\u01a6\3\2"+
		"\2\2\u01ab\61\3\2\2\2\u01ac\u01ae\5\u00eav\2\u01ad\u01af\5\66\34\2\u01ae"+
		"\u01ad\3\2\2\2\u01ae\u01af\3\2\2\2\u01af\63\3\2\2\2\u01b0\u01b1\7\3\2"+
		"\2\u01b1\u01b2\5\u00eav\2\u01b2\65\3\2\2\2\u01b3\u01b4\7\22\2\2\u01b4"+
		"\u01b5\5\u00b6\\\2\u01b5\67\3\2\2\2\u01b6\u01b7\7\17\2\2\u01b79\3\2\2"+
		"\2\u01b8\u01b9\6\36\2\2\u01b9\u01ba\5\u00b4[\2\u01ba\u01bb\5\u00f8}\2"+
		"\u01bb;\3\2\2\2\u01bc\u01bd\7U\2\2\u01bd\u01be\7\13\2\2\u01be\u01bf\5"+
		"\u00b4[\2\u01bf\u01c0\7\f\2\2\u01c0\u01c3\5\4\3\2\u01c1\u01c2\7E\2\2\u01c2"+
		"\u01c4\5\4\3\2\u01c3\u01c1\3\2\2\2\u01c3\u01c4\3\2\2\2\u01c4=\3\2\2\2"+
		"\u01c5\u01c6\7A\2\2\u01c6\u01c7\5\4\3\2\u01c7\u01c8\7O\2\2\u01c8\u01c9"+
		"\7\13\2\2\u01c9\u01ca\5\u00b4[\2\u01ca\u01cb\7\f\2\2\u01cb\u01cc\5\u00f8"+
		"}\2\u01cc\u0234\3\2\2\2\u01cd\u01ce\7O\2\2\u01ce\u01cf\7\13\2\2\u01cf"+
		"\u01d0\5\u00b4[\2\u01d0\u01d1\7\f\2\2\u01d1\u01d2\5\4\3\2\u01d2\u0234"+
		"\3\2\2\2\u01d3\u01d4\7M\2\2\u01d4\u01d5\7\13\2\2\u01d5\u01d7\6 \3\2\u01d6"+
		"\u01d8\5\u00b4[\2\u01d7\u01d6\3\2\2\2\u01d7\u01d8\3\2\2\2\u01d8\u01d9"+
		"\3\2\2\2\u01d9\u01db\7\17\2\2\u01da\u01dc\5\u00b4[\2\u01db\u01da\3\2\2"+
		"\2\u01db\u01dc\3\2\2\2\u01dc\u01dd\3\2\2\2\u01dd\u01df\7\17\2\2\u01de"+
		"\u01e0\5\u00b4[\2\u01df\u01de\3\2\2\2\u01df\u01e0\3\2\2\2\u01e0\u01e1"+
		"\3\2\2\2\u01e1\u01e2\7\f\2\2\u01e2\u0234\5\4\3\2\u01e3\u01e4\7M\2\2\u01e4"+
		"\u01e5\7\13\2\2\u01e5\u01e6\7G\2\2\u01e6\u01e7\5\36\20\2\u01e7\u01e9\7"+
		"\17\2\2\u01e8\u01ea\5\u00b4[\2\u01e9\u01e8\3\2\2\2\u01e9\u01ea\3\2\2\2"+
		"\u01ea\u01eb\3\2\2\2\u01eb\u01ed\7\17\2\2\u01ec\u01ee\5\u00b4[\2\u01ed"+
		"\u01ec\3\2\2\2\u01ed\u01ee\3\2\2\2\u01ee\u01ef\3\2\2\2\u01ef\u01f0\7\f"+
		"\2\2\u01f0\u01f1\5\4\3\2\u01f1\u0234\3\2\2\2\u01f2\u01f3\7M\2\2\u01f3"+
		"\u01f4\7\13\2\2\u01f4\u01f6\5\24\13\2\u01f5\u01f7\5\u00b4[\2\u01f6\u01f5"+
		"\3\2\2\2\u01f6\u01f7\3\2\2\2\u01f7\u01f8\3\2\2\2\u01f8\u01fa\7\17\2\2"+
		"\u01f9\u01fb\5\u00b4[\2\u01fa\u01f9\3\2\2\2\u01fa\u01fb\3\2\2\2\u01fb"+
		"\u01fc\3\2\2\2\u01fc\u01fd\7\f\2\2\u01fd\u01fe\5\4\3\2\u01fe\u0234\3\2"+
		"\2\2\u01ff\u0200\7M\2\2\u0200\u0201\7\13\2\2\u0201\u0202\6 \4\2\u0202"+
		"\u0203\5\u00b6\\\2\u0203\u0204\7X\2\2\u0204\u0205\5\u00b4[\2\u0205\u0206"+
		"\7\f\2\2\u0206\u0207\5\4\3\2\u0207\u0234\3\2\2\2\u0208\u0209\7M\2\2\u0209"+
		"\u020a\7\13\2\2\u020a\u020b\7G\2\2\u020b\u020c\5B\"\2\u020c\u020d\7X\2"+
		"\2\u020d\u020e\5\u00b4[\2\u020e\u020f\7\f\2\2\u020f\u0210\5\4\3\2\u0210"+
		"\u0234\3\2\2\2\u0211\u0212\7M\2\2\u0212\u0213\7\13\2\2\u0213\u0214\5@"+
		"!\2\u0214\u0215\7X\2\2\u0215\u0216\5\u00b4[\2\u0216\u0217\7\f\2\2\u0217"+
		"\u0218\5\4\3\2\u0218\u0234\3\2\2\2\u0219\u021a\7M\2\2\u021a\u021b\7\13"+
		"\2\2\u021b\u021c\6 \5\2\u021c\u021d\5\u00b6\\\2\u021d\u021e\7\4\2\2\u021e"+
		"\u021f\5\u00b6\\\2\u021f\u0220\7\f\2\2\u0220\u0221\5\4\3\2\u0221\u0234"+
		"\3\2\2\2\u0222\u0223\7M\2\2\u0223\u0224\7\13\2\2\u0224\u0225\7G\2\2\u0225"+
		"\u0226\5B\"\2\u0226\u0227\7\4\2\2\u0227\u0228\5\u00b6\\\2\u0228\u0229"+
		"\7\f\2\2\u0229\u022a\5\4\3\2\u022a\u0234\3\2\2\2\u022b\u022c\7M\2\2\u022c"+
		"\u022d\7\13\2\2\u022d\u022e\5@!\2\u022e\u022f\7\4\2\2\u022f\u0230\5\u00b6"+
		"\\\2\u0230\u0231\7\f\2\2\u0231\u0232\5\4\3\2\u0232\u0234\3\2\2\2\u0233"+
		"\u01c5\3\2\2\2\u0233\u01cd\3\2\2\2\u0233\u01d3\3\2\2\2\u0233\u01e3\3\2"+
		"\2\2\u0233\u01f2\3\2\2\2\u0233\u01ff\3\2\2\2\u0233\u0208\3\2\2\2\u0233"+
		"\u0211\3\2\2\2\u0233\u0219\3\2\2\2\u0233\u0222\3\2\2\2\u0233\u022b\3\2"+
		"\2\2\u0234?\3\2\2\2\u0235\u0236\5\26\f\2\u0236\u0237\5B\"\2\u0237A\3\2"+
		"\2\2\u0238\u023b\5\u00eav\2\u0239\u023b\5\"\22\2\u023a\u0238\3\2\2\2\u023a"+
		"\u0239\3\2\2\2\u023bC\3\2\2\2\u023c\u023f\7L\2\2\u023d\u023e\6#\6\2\u023e"+
		"\u0240\7k\2\2\u023f\u023d\3\2\2\2\u023f\u0240\3\2\2\2\u0240\u0241\3\2"+
		"\2\2\u0241\u0242\5\u00f8}\2\u0242E\3\2\2\2\u0243\u0246\7@\2\2\u0244\u0245"+
		"\6$\7\2\u0245\u0247\7k\2\2\u0246\u0244\3\2\2\2\u0246\u0247\3\2\2\2\u0247"+
		"\u0248\3\2\2\2\u0248\u0249\5\u00f8}\2\u0249G\3\2\2\2\u024a\u024d\7J\2"+
		"\2\u024b\u024c\6%\b\2\u024c\u024e\5\u00b4[\2\u024d\u024b\3\2\2\2\u024d"+
		"\u024e\3\2\2\2\u024e\u024f\3\2\2\2\u024f\u0250\5\u00f8}\2\u0250I\3\2\2"+
		"\2\u0251\u0252\7S\2\2\u0252\u0253\7\13\2\2\u0253\u0254\5\u00b4[\2\u0254"+
		"\u0255\7\f\2\2\u0255\u0256\5\4\3\2\u0256K\3\2\2\2\u0257\u0258\7N\2\2\u0258"+
		"\u0259\7\13\2\2\u0259\u025a\5\u00b4[\2\u025a\u025b\7\f\2\2\u025b\u025c"+
		"\5N(\2\u025cM\3\2\2\2\u025d\u025f\7\r\2\2\u025e\u0260\5P)\2\u025f\u025e"+
		"\3\2\2\2\u025f\u0260\3\2\2\2\u0260\u0265\3\2\2\2\u0261\u0263\5T+\2\u0262"+
		"\u0264\5P)\2\u0263\u0262\3\2\2\2\u0263\u0264\3\2\2\2\u0264\u0266\3\2\2"+
		"\2\u0265\u0261\3\2\2\2\u0265\u0266\3\2\2\2\u0266\u0267\3\2\2\2\u0267\u0268"+
		"\7\16\2\2\u0268O\3\2\2\2\u0269\u026b\5R*\2\u026a\u0269\3\2\2\2\u026b\u026c"+
		"\3\2\2\2\u026c\u026a\3\2\2\2\u026c\u026d\3\2\2\2\u026dQ\3\2\2\2\u026e"+
		"\u026f\7D\2\2\u026f\u0270\5\u00b4[\2\u0270\u0272\7\24\2\2\u0271\u0273"+
		"\5\20\t\2\u0272\u0271\3\2\2\2\u0272\u0273\3\2\2\2\u0273S\3\2\2\2\u0274"+
		"\u0275\7T\2\2\u0275\u0277\7\24\2\2\u0276\u0278\5\20\t\2\u0277\u0276\3"+
		"\2\2\2\u0277\u0278\3\2\2\2\u0278U\3\2\2\2\u0279\u027a\7k\2\2\u027a\u027b"+
		"\7\24\2\2\u027b\u027c\5X-\2\u027cW\3\2\2\2\u027d\u0280\5\4\3\2\u027e\u0280"+
		"\5f\64\2\u027f\u027d\3\2\2\2\u027f\u027e\3\2\2\2\u0280Y\3\2\2\2\u0281"+
		"\u0282\7V\2\2\u0282\u0283\6.\t\2\u0283\u0284\5\u00b4[\2\u0284\u0285\5"+
		"\u00f8}\2\u0285[\3\2\2\2\u0286\u0287\7Y\2\2\u0287\u0288\5\16\b\2\u0288"+
		"\u0289\5^\60\2\u0289\u0294\3\2\2\2\u028a\u028b\7Y\2\2\u028b\u028c\5\16"+
		"\b\2\u028c\u028d\5`\61\2\u028d\u0294\3\2\2\2\u028e\u028f\7Y\2\2\u028f"+
		"\u0290\5\16\b\2\u0290\u0291\5^\60\2\u0291\u0292\5`\61\2\u0292\u0294\3"+
		"\2\2\2\u0293\u0286\3\2\2\2\u0293\u028a\3\2\2\2\u0293\u028e\3\2\2\2\u0294"+
		"]\3\2\2\2\u0295\u0296\7H\2\2\u0296\u0297\7\13\2\2\u0297\u0298\5b\62\2"+
		"\u0298\u0299\7\f\2\2\u0299\u029a\5\16\b\2\u029a_\3\2\2\2\u029b\u029c\7"+
		"I\2\2\u029c\u029d\5\16\b\2\u029da\3\2\2\2\u029e\u02a1\5\u00eav\2\u029f"+
		"\u02a1\5\"\22\2\u02a0\u029e\3\2\2\2\u02a0\u029f\3\2\2\2\u02a1c\3\2\2\2"+
		"\u02a2\u02a3\7P\2\2\u02a3\u02a4\5\u00f8}\2\u02a4e\3\2\2\2\u02a5\u02a6"+
		"\7Q\2\2\u02a6\u02a7\5\u00eav\2\u02a7\u02a8\7\13\2\2\u02a8\u02a9\5j\66"+
		"\2\u02a9\u02aa\7\f\2\2\u02aa\u02ab\7\r\2\2\u02ab\u02ac\5t;\2\u02ac\u02ad"+
		"\7\16\2\2\u02adg\3\2\2\2\u02ae\u02af\5j\66\2\u02afi\3\2\2\2\u02b0\u02b3"+
		"\3\2\2\2\u02b1\u02b3\5l\67\2\u02b2\u02b0\3\2\2\2\u02b2\u02b1\3\2\2\2\u02b3"+
		"k\3\2\2\2\u02b4\u02bb\5p9\2\u02b5\u02b8\5n8\2\u02b6\u02b7\7\20\2\2\u02b7"+
		"\u02b9\5p9\2\u02b8\u02b6\3\2\2\2\u02b8\u02b9\3\2\2\2\u02b9\u02bb\3\2\2"+
		"\2\u02ba\u02b4\3\2\2\2\u02ba\u02b5\3\2\2\2\u02bbm\3\2\2\2\u02bc\u02c1"+
		"\5r:\2\u02bd\u02be\7\20\2\2\u02be\u02c0\5r:\2\u02bf\u02bd\3\2\2\2\u02c0"+
		"\u02c3\3\2\2\2\u02c1\u02bf\3\2\2\2\u02c1\u02c2\3\2\2\2\u02c2o\3\2\2\2"+
		"\u02c3\u02c1\3\2\2\2\u02c4\u02c5\5\64\33\2\u02c5q\3\2\2\2\u02c6\u02c7"+
		"\5\60\31\2\u02c7s\3\2\2\2\u02c8\u02c9\5v<\2\u02c9u\3\2\2\2\u02ca\u02cc"+
		"\5\20\t\2\u02cb\u02ca\3\2\2\2\u02cb\u02cc\3\2\2\2\u02ccw\3\2\2\2\u02cd"+
		"\u02ce\5z>\2\u02ce\u02cf\6=\n\2\u02cf\u02d0\7\21\2\2\u02d0\u02d1\5|?\2"+
		"\u02d1y\3\2\2\2\u02d2\u02d5\5\u00eav\2\u02d3\u02d5\5~@\2\u02d4\u02d2\3"+
		"\2\2\2\u02d4\u02d3\3\2\2\2\u02d5{\3\2\2\2\u02d6\u02d7\6?\13\2\u02d7\u02dd"+
		"\5\u00b6\\\2\u02d8\u02d9\7\r\2\2\u02d9\u02da\5t;\2\u02da\u02db\7\16\2"+
		"\2\u02db\u02dd\3\2\2\2\u02dc\u02d6\3\2\2\2\u02dc\u02d8\3\2\2\2\u02dd}"+
		"\3\2\2\2\u02de\u02df\7\13\2\2\u02df\u02e0\5\u00b4[\2\u02e0\u02e1\7\f\2"+
		"\2\u02e1\u02f0\3\2\2\2\u02e2\u02e3\7\13\2\2\u02e3\u02f0\7\f\2\2\u02e4"+
		"\u02e5\7\13\2\2\u02e5\u02e6\7\3\2\2\u02e6\u02e7\5\u00eav\2\u02e7\u02e8"+
		"\7\f\2\2\u02e8\u02f0\3\2\2\2\u02e9\u02ea\7\13\2\2\u02ea\u02eb\5\u00b4"+
		"[\2\u02eb\u02ec\7\20\2\2\u02ec\u02ed\7\3\2\2\u02ed\u02ee\5\u00eav\2\u02ee"+
		"\u02f0\3\2\2\2\u02ef\u02de\3\2\2\2\u02ef\u02e2\3\2\2\2\u02ef\u02e4\3\2"+
		"\2\2\u02ef\u02e9\3\2\2\2\u02f0\177\3\2\2\2\u02f1\u02f2\5\u00a8U\2\u02f2"+
		"\u02f3\7\13\2\2\u02f3\u02f4\5h\65\2\u02f4\u02f5\7\f\2\2\u02f5\u02f6\7"+
		"\r\2\2\u02f6\u02f7\5t;\2\u02f7\u02f8\7\16\2\2\u02f8\u030e\3\2\2\2\u02f9"+
		"\u030e\5\u0084C\2\u02fa\u02fb\6A\f\2\u02fb\u02fc\7k\2\2\u02fc\u02fd\5"+
		"\u00a8U\2\u02fd\u02fe\7\13\2\2\u02fe\u02ff\7\f\2\2\u02ff\u0300\7\r\2\2"+
		"\u0300\u0301\5t;\2\u0301\u0302\7\16\2\2\u0302\u030e\3\2\2\2\u0303\u0304"+
		"\6A\r\2\u0304\u0305\7k\2\2\u0305\u0306\5\u00a8U\2\u0306\u0307\7\13\2\2"+
		"\u0307\u0308\5\u0082B\2\u0308\u0309\7\f\2\2\u0309\u030a\7\r\2\2\u030a"+
		"\u030b\5t;\2\u030b\u030c\7\16\2\2\u030c\u030e\3\2\2\2\u030d\u02f1\3\2"+
		"\2\2\u030d\u02f9\3\2\2\2\u030d\u02fa\3\2\2\2\u030d\u0303\3\2\2\2\u030e"+
		"\u0081\3\2\2\2\u030f\u0310\5r:\2\u0310\u0083\3\2\2\2\u0311\u0312\7\34"+
		"\2\2\u0312\u0313\5\u00a8U\2\u0313\u0314\7\13\2\2\u0314\u0315\5h\65\2\u0315"+
		"\u0316\7\f\2\2\u0316\u0317\7\r\2\2\u0317\u0318\5\u008aF\2\u0318\u0319"+
		"\7\16\2\2\u0319\u0085\3\2\2\2\u031a\u031b\7Q\2\2\u031b\u031c\7\34\2\2"+
		"\u031c\u031d\5\u00eav\2\u031d\u031e\7\13\2\2\u031e\u031f\5j\66\2\u031f"+
		"\u0320\7\f\2\2\u0320\u0321\7\r\2\2\u0321\u0322\5\u008aF\2\u0322\u0323"+
		"\7\16\2\2\u0323\u0087\3\2\2\2\u0324\u0325\7Q\2\2\u0325\u0327\7\34\2\2"+
		"\u0326\u0328\5\u00eav\2\u0327\u0326\3\2\2\2\u0327\u0328\3\2\2\2\u0328"+
		"\u0329\3\2\2\2\u0329\u032a\7\13\2\2\u032a\u032b\5j\66\2\u032b\u032c\7"+
		"\f\2\2\u032c\u032d\7\r\2\2\u032d\u032e\5\u008aF\2\u032e\u032f\7\16\2\2"+
		"\u032f\u0089\3\2\2\2\u0330\u0331\5t;\2\u0331\u008b\3\2\2\2\u0332\u0333"+
		"\7[\2\2\u0333\u0334\5\u00eav\2\u0334\u0335\5\u0090I\2\u0335\u008d\3\2"+
		"\2\2\u0336\u0338\7[\2\2\u0337\u0339\5\u00eav\2\u0338\u0337\3\2\2\2\u0338"+
		"\u0339\3\2\2\2\u0339\u033a\3\2\2\2\u033a\u033b\5\u0090I\2\u033b\u008f"+
		"\3\2\2\2\u033c\u033e\5\u0092J\2\u033d\u033c\3\2\2\2\u033d\u033e\3\2\2"+
		"\2\u033e\u033f\3\2\2\2\u033f\u0341\7\r\2\2\u0340\u0342\5\u0094K\2\u0341"+
		"\u0340\3\2\2\2\u0341\u0342\3\2\2\2\u0342\u0343\3\2\2\2\u0343\u0344\7\16"+
		"\2\2\u0344\u0091\3\2\2\2\u0345\u0346\7\\\2\2\u0346\u0347\5\u00b6\\\2\u0347"+
		"\u0093\3\2\2\2\u0348\u0349\5\u0096L\2\u0349\u0095\3\2\2\2\u034a\u034c"+
		"\5\u0098M\2\u034b\u034a\3\2\2\2\u034c\u034d\3\2\2\2\u034d\u034b\3\2\2"+
		"\2\u034d\u034e\3\2\2\2\u034e\u0097\3\2\2\2\u034f\u0354\5\u0080A\2\u0350"+
		"\u0351\7a\2\2\u0351\u0354\5\u0080A\2\u0352\u0354\7\17\2\2\u0353\u034f"+
		"\3\2\2\2\u0353\u0350\3\2\2\2\u0353\u0352\3\2\2\2\u0354\u0099\3\2\2\2\u0355"+
		"\u0357\7\t\2\2\u0356\u0358\5\u009cO\2\u0357\u0356\3\2\2\2\u0357\u0358"+
		"\3\2\2\2\u0358\u035a\3\2\2\2\u0359\u035b\7\20\2\2\u035a\u0359\3\2\2\2"+
		"\u035a\u035b\3\2\2\2\u035b\u035d\3\2\2\2\u035c\u035e\5\u009eP\2\u035d"+
		"\u035c\3\2\2\2\u035d\u035e\3\2\2\2\u035e\u035f\3\2\2\2\u035f\u0360\7\n"+
		"\2\2\u0360\u009b\3\2\2\2\u0361\u0363\bO\1\2\u0362\u0364\5\u009eP\2\u0363"+
		"\u0362\3\2\2\2\u0363\u0364\3\2\2\2\u0364\u0365\3\2\2\2\u0365\u036b\5\u00b6"+
		"\\\2\u0366\u0368\5\u009eP\2\u0367\u0366\3\2\2\2\u0367\u0368\3\2\2\2\u0368"+
		"\u0369\3\2\2\2\u0369\u036b\5\u00a0Q\2\u036a\u0361\3\2\2\2\u036a\u0367"+
		"\3\2\2\2\u036b\u037a\3\2\2\2\u036c\u036d\f\4\2\2\u036d\u036f\7\20\2\2"+
		"\u036e\u0370\5\u009eP\2\u036f\u036e\3\2\2\2\u036f\u0370\3\2\2\2\u0370"+
		"\u0371\3\2\2\2\u0371\u0379\5\u00b6\\\2\u0372\u0373\f\3\2\2\u0373\u0375"+
		"\7\20\2\2\u0374\u0376\5\u009eP\2\u0375\u0374\3\2\2\2\u0375\u0376\3\2\2"+
		"\2\u0376\u0377\3\2\2\2\u0377\u0379\5\u00a0Q\2\u0378\u036c\3\2\2\2\u0378"+
		"\u0372\3\2\2\2\u0379\u037c\3\2\2\2\u037a\u0378\3\2\2\2\u037a\u037b\3\2"+
		"\2\2\u037b\u009d\3\2\2\2\u037c\u037a\3\2\2\2\u037d\u037f\7\20\2\2\u037e"+
		"\u037d\3\2\2\2\u037f\u0380\3\2\2\2\u0380\u037e\3\2\2\2\u0380\u0381\3\2"+
		"\2\2\u0381\u009f\3\2\2\2\u0382\u0383\7\3\2\2\u0383\u0384\5\u00b6\\\2\u0384"+
		"\u00a1\3\2\2\2\u0385\u0386\7\r\2\2\u0386\u038f\7\16\2\2\u0387\u0388\7"+
		"\r\2\2\u0388\u038a\5\u00a4S\2\u0389\u038b\7\20\2\2\u038a\u0389\3\2\2\2"+
		"\u038a\u038b\3\2\2\2\u038b\u038c\3\2\2\2\u038c\u038d\7\16\2\2\u038d\u038f"+
		"\3\2\2\2\u038e\u0385\3\2\2\2\u038e\u0387\3\2\2\2\u038f\u00a3\3\2\2\2\u0390"+
		"\u0395\5\u00a6T\2\u0391\u0392\7\20\2\2\u0392\u0394\5\u00a6T\2\u0393\u0391"+
		"\3\2\2\2\u0394\u0397\3\2\2\2\u0395\u0393\3\2\2\2\u0395\u0396\3\2\2\2\u0396"+
		"\u00a5\3\2\2\2\u0397\u0395\3\2\2\2\u0398\u03a0\5\u00e8u\2\u0399\u03a0"+
		"\5\u00aeX\2\u039a\u039b\5\u00a8U\2\u039b\u039c\7\24\2\2\u039c\u039d\5"+
		"\u00b6\\\2\u039d\u03a0\3\2\2\2\u039e\u03a0\5\u0080A\2\u039f\u0398\3\2"+
		"\2\2\u039f\u0399\3\2\2\2\u039f\u039a\3\2\2\2\u039f\u039e\3\2\2\2\u03a0"+
		"\u00a7\3\2\2\2\u03a1\u03a4\5\u00aaV\2\u03a2\u03a4\5\u00acW\2\u03a3\u03a1"+
		"\3\2\2\2\u03a3\u03a2\3\2\2\2\u03a4\u00a9\3\2\2\2\u03a5\u03a9\5\u00e6t"+
		"\2\u03a6\u03a9\7l\2\2\u03a7\u03a9\5\u00e4s\2\u03a8\u03a5\3\2\2\2\u03a8"+
		"\u03a6\3\2\2\2\u03a8\u03a7\3\2\2\2\u03a9\u00ab\3\2\2\2\u03aa\u03ab\7\t"+
		"\2\2\u03ab\u03ac\5\u00b6\\\2\u03ac\u03ad\7\n\2\2\u03ad\u00ad\3\2\2\2\u03ae"+
		"\u03af\5\u00e8u\2\u03af\u03b0\5\66\34\2\u03b0\u00af\3\2\2\2\u03b1\u03b3"+
		"\7\13\2\2\u03b2\u03b4\5\u00b2Z\2\u03b3\u03b2\3\2\2\2\u03b3\u03b4\3\2\2"+
		"\2\u03b4\u03b5\3\2\2\2\u03b5\u03b6\7\f\2\2\u03b6\u00b1\3\2\2\2\u03b7\u03b9"+
		"\7\3\2\2\u03b8\u03b7\3\2\2\2\u03b8\u03b9\3\2\2\2\u03b9\u03ba\3\2\2\2\u03ba"+
		"\u03c2\5\u00b6\\\2\u03bb\u03bd\7\20\2\2\u03bc\u03be\7\3\2\2\u03bd\u03bc"+
		"\3\2\2\2\u03bd\u03be\3\2\2\2\u03be\u03bf\3\2\2\2\u03bf\u03c1\5\u00b6\\"+
		"\2\u03c0\u03bb\3\2\2\2\u03c1\u03c4\3\2\2\2\u03c2\u03c0\3\2\2\2\u03c2\u03c3"+
		"\3\2\2\2\u03c3\u00b3\3\2\2\2\u03c4\u03c2\3\2\2\2\u03c5\u03ca\5\u00b6\\"+
		"\2\u03c6\u03c7\7\20\2\2\u03c7\u03c9\5\u00b6\\\2\u03c8\u03c6\3\2\2\2\u03c9"+
		"\u03cc\3\2\2\2\u03ca\u03c8\3\2\2\2\u03ca\u03cb\3\2\2\2\u03cb\u00b5\3\2"+
		"\2\2\u03cc\u03ca\3\2\2\2\u03cd\u03ce\b\\\1\2\u03ce\u03d0\7Q\2\2\u03cf"+
		"\u03d1\5\u00eav\2\u03d0\u03cf\3\2\2\2\u03d0\u03d1\3\2\2\2\u03d1\u03d2"+
		"\3\2\2\2\u03d2\u03d3\7\13\2\2\u03d3\u03d4\5j\66\2\u03d4\u03d5\7\f\2\2"+
		"\u03d5\u03d6\7\r\2\2\u03d6\u03d7\5t;\2\u03d7\u03d8\7\16\2\2\u03d8\u0415"+
		"\3\2\2\2\u03d9\u03da\7^\2\2\u03da\u03db\7\t\2\2\u03db\u03dc\5\u00b4[\2"+
		"\u03dc\u03dd\7\n\2\2\u03dd\u0415\3\2\2\2\u03de\u03df\7^\2\2\u03df\u03e0"+
		"\7\25\2\2\u03e0\u0415\5\u00e6t\2\u03e1\u03e2\7^\2\2\u03e2\u0415\5\u00b0"+
		"Y\2\u03e3\u03e4\7F\2\2\u03e4\u03e5\7\25\2\2\u03e5\u03e6\6\\\20\2\u03e6"+
		"\u0415\7k\2\2\u03e7\u03e8\7F\2\2\u03e8\u03ea\5\u00b6\\\2\u03e9\u03eb\5"+
		"\u00b0Y\2\u03ea\u03e9\3\2\2\2\u03ea\u03eb\3\2\2\2\u03eb\u0415\3\2\2\2"+
		"\u03ec\u03ed\7W\2\2\u03ed\u0415\5\u00b6\\&\u03ee\u03ef\7K\2\2\u03ef\u0415"+
		"\5\u00b6\\%\u03f0\u03f1\7C\2\2\u03f1\u0415\5\u00b6\\$\u03f2\u03f3\7\26"+
		"\2\2\u03f3\u0415\5\u00b6\\#\u03f4\u03f5\7\27\2\2\u03f5\u0415\5\u00b6\\"+
		"\"\u03f6\u03f7\7\30\2\2\u03f7\u0415\5\u00b6\\!\u03f8\u03f9\7\31\2\2\u03f9"+
		"\u0415\5\u00b6\\ \u03fa\u03fb\7\32\2\2\u03fb\u0415\5\u00b6\\\37\u03fc"+
		"\u03fd\7\33\2\2\u03fd\u0415\5\u00b6\\\36\u03fe\u0404\7_\2\2\u03ff\u0401"+
		"\6\\\21\2\u0400\u0402\7\34\2\2\u0401\u0400\3\2\2\2\u0401\u0402\3\2\2\2"+
		"\u0402\u0403\3\2\2\2\u0403\u0405\5\u00b6\\\2\u0404\u03ff\3\2\2\2\u0404"+
		"\u0405\3\2\2\2\u0405\u0415\3\2\2\2\u0406\u0415\5x=\2\u0407\u0415\7R\2"+
		"\2\u0408\u0415\7k\2\2\u0409\u0415\5\u00e2r\2\u040a\u0415\5\u009aN\2\u040b"+
		"\u0415\5\u00a2R\2\u040c\u0415\5\u008eH\2\u040d\u0415\5\u0088E\2\u040e"+
		"\u0415\7\7\2\2\u040f\u0415\5\u00fc\177\2\u0410\u0411\7\13\2\2\u0411\u0412"+
		"\5\u00b4[\2\u0412\u0413\7\f\2\2\u0413\u0415\3\2\2\2\u0414\u03cd\3\2\2"+
		"\2\u0414\u03d9\3\2\2\2\u0414\u03de\3\2\2\2\u0414\u03e1\3\2\2\2\u0414\u03e3"+
		"\3\2\2\2\u0414\u03e7\3\2\2\2\u0414\u03ec\3\2\2\2\u0414\u03ee\3\2\2\2\u0414"+
		"\u03f0\3\2\2\2\u0414\u03f2\3\2\2\2\u0414\u03f4\3\2\2\2\u0414\u03f6\3\2"+
		"\2\2\u0414\u03f8\3\2\2\2\u0414\u03fa\3\2\2\2\u0414\u03fc\3\2\2\2\u0414"+
		"\u03fe\3\2\2\2\u0414\u0406\3\2\2\2\u0414\u0407\3\2\2\2\u0414\u0408\3\2"+
		"\2\2\u0414\u0409\3\2\2\2\u0414\u040a\3\2\2\2\u0414\u040b\3\2\2\2\u0414"+
		"\u040c\3\2\2\2\u0414\u040d\3\2\2\2\u0414\u040e\3\2\2\2\u0414\u040f\3\2"+
		"\2\2\u0414\u0410\3\2\2\2\u0415\u045b\3\2\2\2\u0416\u0417\f\35\2\2\u0417"+
		"\u0418\t\3\2\2\u0418\u045a\5\u00b6\\\36\u0419\u041a\f\34\2\2\u041a\u041b"+
		"\t\4\2\2\u041b\u045a\5\u00b6\\\35\u041c\u041d\f\33\2\2\u041d\u041e\t\5"+
		"\2\2\u041e\u045a\5\u00b6\\\34\u041f\u0420\f\32\2\2\u0420\u0421\t\6\2\2"+
		"\u0421\u045a\5\u00b6\\\33\u0422\u0423\f\31\2\2\u0423\u0424\7B\2\2\u0424"+
		"\u045a\5\u00b6\\\32\u0425\u0426\f\30\2\2\u0426\u0427\7X\2\2\u0427\u045a"+
		"\5\u00b6\\\31\u0428\u0429\f\27\2\2\u0429\u042a\t\7\2\2\u042a\u045a\5\u00b6"+
		"\\\30\u042b\u042c\f\26\2\2\u042c\u042d\7*\2\2\u042d\u045a\5\u00b6\\\27"+
		"\u042e\u042f\f\25\2\2\u042f\u0430\7+\2\2\u0430\u045a\5\u00b6\\\26\u0431"+
		"\u0432\f\24\2\2\u0432\u0433\7,\2\2\u0433\u045a\5\u00b6\\\25\u0434\u0435"+
		"\f\23\2\2\u0435\u0436\7-\2\2\u0436\u045a\5\u00b6\\\24\u0437\u0438\f\22"+
		"\2\2\u0438\u0439\7.\2\2\u0439\u045a\5\u00b6\\\23\u043a\u043b\f\21\2\2"+
		"\u043b\u043c\7\23\2\2\u043c\u043d\5\u00b6\\\2\u043d\u043e\7\24\2\2\u043e"+
		"\u043f\5\u00b6\\\22\u043f\u045a\3\2\2\2\u0440\u0441\f\16\2\2\u0441\u0442"+
		"\7\22\2\2\u0442\u045a\5\u00b6\\\17\u0443\u0444\f\r\2\2\u0444\u0445\5\u00e0"+
		"q\2\u0445\u0446\5\u00b6\\\16\u0446\u045a\3\2\2\2\u0447\u0448\f\61\2\2"+
		"\u0448\u0449\7\t\2\2\u0449\u044a\5\u00b4[\2\u044a\u044b\7\n\2\2\u044b"+
		"\u045a\3\2\2\2\u044c\u044d\f\60\2\2\u044d\u044e\7\25\2\2\u044e\u045a\5"+
		"\u00e6t\2\u044f\u0450\f/\2\2\u0450\u045a\5\u00fc\177\2\u0451\u0452\f+"+
		"\2\2\u0452\u045a\5\u00b0Y\2\u0453\u0454\f(\2\2\u0454\u0455\6\\&\2\u0455"+
		"\u045a\7\26\2\2\u0456\u0457\f\'\2\2\u0457\u0458\6\\(\2\u0458\u045a\7\27"+
		"\2\2\u0459\u0416\3\2\2\2\u0459\u0419\3\2\2\2\u0459\u041c\3\2\2\2\u0459"+
		"\u041f\3\2\2\2\u0459\u0422\3\2\2\2\u0459\u0425\3\2\2\2\u0459\u0428\3\2"+
		"\2\2\u0459\u042b\3\2\2\2\u0459\u042e\3\2\2\2\u0459\u0431\3\2\2\2\u0459"+
		"\u0434\3\2\2\2\u0459\u0437\3\2\2\2\u0459\u043a\3\2\2\2\u0459\u0440\3\2"+
		"\2\2\u0459\u0443\3\2\2\2\u0459\u0447\3\2\2\2\u0459\u044c\3\2\2\2\u0459"+
		"\u044f\3\2\2\2\u0459\u0451\3\2\2\2\u0459\u0453\3\2\2\2\u0459\u0456\3\2"+
		"\2\2\u045a\u045d\3\2\2\2\u045b\u0459\3\2\2\2\u045b\u045c\3\2\2\2\u045c"+
		"\u00b7\3\2\2\2\u045d\u045b\3\2\2\2\u045e\u0460\5\u00ba^\2\u045f\u045e"+
		"\3\2\2\2\u045f\u0460\3\2\2\2\u0460\u00b9\3\2\2\2\u0461\u0462\5\20\t\2"+
		"\u0462\u00bb\3\2\2\2\u0463\u0465\5\u00be`\2\u0464\u0463\3\2\2\2\u0464"+
		"\u0465\3\2\2\2\u0465\u00bd\3\2\2\2\u0466\u0467\5\u00c0a\2\u0467\u00bf"+
		"\3\2\2\2\u0468\u046a\5\u00c2b\2\u0469\u0468\3\2\2\2\u046a\u046b\3\2\2"+
		"\2\u046b\u0469\3\2\2\2\u046b\u046c\3\2\2\2\u046c\u00c1\3\2\2\2\u046d\u0471"+
		"\5\u00c4c\2\u046e\u0471\5\u00d8m\2\u046f\u0471\5\22\n\2\u0470\u046d\3"+
		"\2\2\2\u0470\u046e\3\2\2\2\u0470\u046f\3\2\2\2\u0471\u00c3\3\2\2\2\u0472"+
		"\u0473\7`\2\2\u0473\u0474\5\u00c6d\2\u0474\u0475\5\u00ceh\2\u0475\u0476"+
		"\5\u00f8}\2\u0476\u047c\3\2\2\2\u0477\u0478\7`\2\2\u0478\u0479\5\u00d4"+
		"k\2\u0479\u047a\5\u00f8}\2\u047a\u047c\3\2\2\2\u047b\u0472\3\2\2\2\u047b"+
		"\u0477\3\2\2\2\u047c\u00c5\3\2\2\2\u047d\u0489\5\u00c8e\2\u047e\u0489"+
		"\5\u00caf\2\u047f\u0489\5\u00ccg\2\u0480\u0481\5\u00c8e\2\u0481\u0482"+
		"\7\20\2\2\u0482\u0483\5\u00caf\2\u0483\u0489\3\2\2\2\u0484\u0485\5\u00c8"+
		"e\2\u0485\u0486\7\20\2\2\u0486\u0487\5\u00ccg\2\u0487\u0489\3\2\2\2\u0488"+
		"\u047d\3\2\2\2\u0488\u047e\3\2\2\2\u0488\u047f\3\2\2\2\u0488\u0480\3\2"+
		"\2\2\u0488\u0484\3\2\2\2\u0489\u00c7\3\2\2\2\u048a\u048b\5\u00d6l\2\u048b"+
		"\u00c9\3\2\2\2\u048c\u048d\7\34\2\2\u048d\u048e\7\5\2\2\u048e\u048f\5"+
		"\u00d6l\2\u048f\u00cb\3\2\2\2\u0490\u0491\7\r\2\2\u0491\u049c\7\16\2\2"+
		"\u0492\u0493\7\r\2\2\u0493\u0494\5\u00d0i\2\u0494\u0495\7\16\2\2\u0495"+
		"\u049c\3\2\2\2\u0496\u0497\7\r\2\2\u0497\u0498\5\u00d0i\2\u0498\u0499"+
		"\7\20\2\2\u0499\u049a\7\16\2\2\u049a\u049c\3\2\2\2\u049b\u0490\3\2\2\2"+
		"\u049b\u0492\3\2\2\2\u049b\u0496\3\2\2\2\u049c\u00cd\3\2\2\2\u049d\u049e"+
		"\7\6\2\2\u049e\u049f\5\u00d4k\2\u049f\u00cf\3\2\2\2\u04a0\u04a5\5\u00d2"+
		"j\2\u04a1\u04a2\7\20\2\2\u04a2\u04a4\5\u00d2j\2\u04a3\u04a1\3\2\2\2\u04a4"+
		"\u04a7\3\2\2\2\u04a5\u04a3\3\2\2\2\u04a5\u04a6\3\2\2\2\u04a6\u00d1\3\2"+
		"\2\2\u04a7\u04a5\3\2\2\2\u04a8\u04ae\5\u00d6l\2\u04a9\u04aa\5\u00e6t\2"+
		"\u04aa\u04ab\7\5\2\2\u04ab\u04ac\5\u00d6l\2\u04ac\u04ae\3\2\2\2\u04ad"+
		"\u04a8\3\2\2\2\u04ad\u04a9\3\2\2\2\u04ae\u00d3\3\2\2\2\u04af\u04b0\7l"+
		"\2\2\u04b0\u00d5\3\2\2\2\u04b1\u04b2\5\u00eav\2\u04b2\u00d7\3\2\2\2\u04b3"+
		"\u04b4\7Z\2\2\u04b4\u04b5\7\34\2\2\u04b5\u04b6\5\u00ceh\2\u04b6\u04b7"+
		"\7\17\2\2\u04b7\u04d2\3\2\2\2\u04b8\u04b9\7Z\2\2\u04b9\u04ba\5\u00dan"+
		"\2\u04ba\u04bb\5\u00ceh\2\u04bb\u04bc\7\17\2\2\u04bc\u04d2\3\2\2\2\u04bd"+
		"\u04be\7Z\2\2\u04be\u04bf\5\u00dan\2\u04bf\u04c0\7\17\2\2\u04c0\u04d2"+
		"\3\2\2\2\u04c1\u04c2\7Z\2\2\u04c2\u04d2\5\34\17\2\u04c3\u04c4\7Z\2\2\u04c4"+
		"\u04d2\5\6\4\2\u04c5\u04c6\7Z\2\2\u04c6\u04c7\7T\2\2\u04c7\u04d2\5\b\5"+
		"\2\u04c8\u04c9\7Z\2\2\u04c9\u04ca\7T\2\2\u04ca\u04d2\5\u008cG\2\u04cb"+
		"\u04cc\7Z\2\2\u04cc\u04cd\7T\2\2\u04cd\u04ce\6m)\2\u04ce\u04cf\5\u00b6"+
		"\\\2\u04cf\u04d0\7\17\2\2\u04d0\u04d2\3\2\2\2\u04d1\u04b3\3\2\2\2\u04d1"+
		"\u04b8\3\2\2\2\u04d1\u04bd\3\2\2\2\u04d1\u04c1\3\2\2\2\u04d1\u04c3\3\2"+
		"\2\2\u04d1\u04c5\3\2\2\2\u04d1\u04c8\3\2\2\2\u04d1\u04cb\3\2\2\2\u04d2"+
		"\u00d9\3\2\2\2\u04d3\u04d4\7\r\2\2\u04d4\u04df\7\16\2\2\u04d5\u04d6\7"+
		"\r\2\2\u04d6\u04d7\5\u00dco\2\u04d7\u04d8\7\16\2\2\u04d8\u04df\3\2\2\2"+
		"\u04d9\u04da\7\r\2\2\u04da\u04db\5\u00dco\2\u04db\u04dc\7\20\2\2\u04dc"+
		"\u04dd\7\16\2\2\u04dd\u04df\3\2\2\2\u04de\u04d3\3\2\2\2\u04de\u04d5\3"+
		"\2\2\2\u04de\u04d9\3\2\2\2\u04df\u00db\3\2\2\2\u04e0\u04e5\5\u00dep\2"+
		"\u04e1\u04e2\7\20\2\2\u04e2\u04e4\5\u00dep\2\u04e3\u04e1\3\2\2\2\u04e4"+
		"\u04e7\3\2\2\2\u04e5\u04e3\3\2\2\2\u04e5\u04e6\3\2\2\2\u04e6\u00dd\3\2"+
		"\2\2\u04e7\u04e5\3\2\2\2\u04e8\u04eb\5\u00e6t\2\u04e9\u04ea\7\5\2\2\u04ea"+
		"\u04ec\5\u00e6t\2\u04eb\u04e9\3\2\2\2\u04eb\u04ec\3\2\2\2\u04ec\u00df"+
		"\3\2\2\2\u04ed\u04ee\t\b\2\2\u04ee\u00e1\3\2\2\2\u04ef\u04f2\t\t\2\2\u04f0"+
		"\u04f2\5\u00e4s\2\u04f1\u04ef\3\2\2\2\u04f1\u04f0\3\2\2\2\u04f2\u00e3"+
		"\3\2\2\2\u04f3\u04f4\t\n\2\2\u04f4\u00e5\3\2\2\2\u04f5\u04f8\7k\2\2\u04f6"+
		"\u04f8\5\u00eex\2\u04f7\u04f5\3\2\2\2\u04f7\u04f6\3\2\2\2\u04f8\u00e7"+
		"\3\2\2\2\u04f9\u04fa\7k\2\2\u04fa\u00e9\3\2\2\2\u04fb\u04fc\7k\2\2\u04fc"+
		"\u00eb\3\2\2\2\u04fd\u04fe\7k\2\2\u04fe\u00ed\3\2\2\2\u04ff\u0503\5\u00f0"+
		"y\2\u0500\u0503\5\u00f2z\2\u0501\u0503\t\13\2\2\u0502\u04ff\3\2\2\2\u0502"+
		"\u0500\3\2\2\2\u0502\u0501\3\2\2\2\u0503\u00ef\3\2\2\2\u0504\u0505\t\f"+
		"\2\2\u0505\u00f1\3\2\2\2\u0506\u0507\t\r\2\2\u0507\u00f3\3\2\2\2\u0508"+
		"\u0509\6{*\2\u0509\u050a\7k\2\2\u050a\u050b\5\u00a8U\2\u050b\u00f5\3\2"+
		"\2\2\u050c\u050d\6|+\2\u050d\u050e\7k\2\2\u050e\u050f\5\u00a8U\2\u050f"+
		"\u00f7\3\2\2\2\u0510\u0515\7\17\2\2\u0511\u0515\7\2\2\3\u0512\u0515\6"+
		"},\2\u0513\u0515\6}-\2\u0514\u0510\3\2\2\2\u0514\u0511\3\2\2\2\u0514\u0512"+
		"\3\2\2\2\u0514\u0513\3\2\2\2\u0515\u00f9\3\2\2\2\u0516\u0517\7\2\2\3\u0517"+
		"\u00fb\3\2\2\2\u0518\u051e\7q\2\2\u0519\u051a\7r\2\2\u051a\u051b\5\u00b4"+
		"[\2\u051b\u051c\5\u00fe\u0080\2\u051c\u051e\3\2\2\2\u051d\u0518\3\2\2"+
		"\2\u051d\u0519\3\2\2\2\u051e\u00fd\3\2\2\2\u051f\u0524\7t\2\2\u0520\u0521"+
		"\5\u0100\u0081\2\u0521\u0522\7t\2\2\u0522\u0524\3\2\2\2\u0523\u051f\3"+
		"\2\2\2\u0523\u0520\3\2\2\2\u0524\u00ff\3\2\2\2\u0525\u0526\b\u0081\1\2"+
		"\u0526\u0527\7s\2\2\u0527\u0528\5\u00b4[\2\u0528\u052e\3\2\2\2\u0529\u052a"+
		"\f\3\2\2\u052a\u052b\7s\2\2\u052b\u052d\5\u00b4[\2\u052c\u0529\3\2\2\2"+
		"\u052d\u0530\3\2\2\2\u052e\u052c\3\2\2\2\u052e\u052f\3\2\2\2\u052f\u0101"+
		"\3\2\2\2\u0530\u052e\3\2\2\2s\u0113\u0118\u011c\u0120\u0126\u012d\u0131"+
		"\u013e\u0143\u0148\u0153\u0158\u015d\u0161\u016e\u0172\u0175\u0180\u0183"+
		"\u0187\u018e\u0196\u019a\u01a3\u01a8\u01aa\u01ae\u01c3\u01d7\u01db\u01df"+
		"\u01e9\u01ed\u01f6\u01fa\u0233\u023a\u023f\u0246\u024d\u025f\u0263\u0265"+
		"\u026c\u0272\u0277\u027f\u0293\u02a0\u02b2\u02b8\u02ba\u02c1\u02cb\u02d4"+
		"\u02dc\u02ef\u030d\u0327\u0338\u033d\u0341\u034d\u0353\u0357\u035a\u035d"+
		"\u0363\u0367\u036a\u036f\u0375\u0378\u037a\u0380\u038a\u038e\u0395\u039f"+
		"\u03a3\u03a8\u03b3\u03b8\u03bd\u03c2\u03ca\u03d0\u03ea\u0401\u0404\u0414"+
		"\u0459\u045b\u045f\u0464\u046b\u0470\u047b\u0488\u049b\u04a5\u04ad\u04d1"+
		"\u04de\u04e5\u04eb\u04f1\u04f7\u0502\u0514\u051d\u0523\u052e";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}