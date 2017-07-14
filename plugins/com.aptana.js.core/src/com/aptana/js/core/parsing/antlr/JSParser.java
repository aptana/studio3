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
		Static=92, Let=93, Enum=94, Await=95, Implements=96, Private=97, Public=98, 
		Interface=99, Package=100, Protected=101, Identifier=102, StringLiteral=103, 
		WhiteSpaces=104, MultiLineComment=105, SingleLineComment=106, UnexpectedCharacter=107, 
		NoSubstitutionTemplate=108, TemplateHead=109, TemplateMiddle=110, TemplateTail=111;
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
		RULE_numericLiteral = 114, RULE_identifierName = 115, RULE_identifierReference = 116, 
		RULE_bindingIdentifier = 117, RULE_labelIdentifier = 118, RULE_reservedWord = 119, 
		RULE_keyword = 120, RULE_futureReservedWord = 121, RULE_eos = 122, RULE_eof = 123, 
		RULE_templateLiteral = 124, RULE_templateSpans = 125, RULE_templateMiddleList = 126;
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
		"identifierName", "identifierReference", "bindingIdentifier", "labelIdentifier", 
		"reservedWord", "keyword", "futureReservedWord", "eos", "eof", "templateLiteral", 
		"templateSpans", "templateMiddleList"
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
		"'class'", "'extends'", "'const'", "'super'", "'yield'", "'import'", "'static'", 
		"'let'", "'enum'", "'await'", "'implements'", "'private'", "'public'", 
		"'interface'", "'package'", "'protected'"
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
			setState(254);
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
			setState(270);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(256);
				blockStatement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(257);
				variableStatement();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(258);
				emptyStatement();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(259);
				expressionStatement();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(260);
				ifStatement();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(261);
				breakableStatement();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(262);
				continueStatement();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(263);
				breakStatement();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(264);
				returnStatement();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(265);
				withStatement();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(266);
				labelledStatement();
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(267);
				throwStatement();
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(268);
				tryStatement();
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(269);
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
			setState(275);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Function:
				enterOuterAlt(_localctx, 1);
				{
				setState(272);
				hoistableDeclaration();
				}
				break;
			case Class:
				enterOuterAlt(_localctx, 2);
				{
				setState(273);
				classDeclaration();
				}
				break;
			case Const:
			case Let:
				enterOuterAlt(_localctx, 3);
				{
				setState(274);
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
			setState(279);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(277);
				functionDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(278);
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
			setState(283);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Do:
			case For:
			case While:
				enterOuterAlt(_localctx, 1);
				{
				setState(281);
				iterationStatement();
				}
				break;
			case Switch:
				enterOuterAlt(_localctx, 2);
				{
				setState(282);
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
			setState(285);
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
			setState(287);
			match(OpenBrace);
			setState(289);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				{
				setState(288);
				statementList();
				}
				break;
			}
			setState(291);
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
			setState(294); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(293);
					statementListItem();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(296); 
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
			setState(300);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(298);
				statement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(299);
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
			setState(302);
			letOrConst();
			setState(303);
			bindingList();
			setState(304);
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
			setState(306);
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
			setState(308);
			lexicalBinding();
			setState(313);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(309);
					match(Comma);
					setState(310);
					lexicalBinding();
					}
					} 
				}
				setState(315);
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
			setState(323);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(316);
				bindingIdentifier();
				setState(318);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
				case 1:
					{
					setState(317);
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
				setState(320);
				bindingPattern();
				setState(321);
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
			setState(325);
			variableDeclarationStatement();
			setState(326);
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
			setState(328);
			variableDeclaration();
			setState(333);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(329);
					match(Comma);
					setState(330);
					variableDeclaration();
					}
					} 
				}
				setState(335);
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
			setState(343);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(336);
				bindingIdentifier();
				setState(338);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
				case 1:
					{
					setState(337);
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
				setState(340);
				bindingPattern();
				setState(341);
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
			setState(347);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case OpenBrace:
				enterOuterAlt(_localctx, 1);
				{
				setState(345);
				objectBindingPattern();
				}
				break;
			case OpenBracket:
				enterOuterAlt(_localctx, 2);
				{
				setState(346);
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
			setState(360);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(349);
				match(OpenBrace);
				setState(350);
				match(CloseBrace);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(351);
				match(OpenBrace);
				setState(352);
				bindingPropertyList();
				setState(353);
				match(CloseBrace);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(355);
				match(OpenBrace);
				setState(356);
				bindingPropertyList();
				setState(357);
				match(Comma);
				setState(358);
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
			setState(385);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(362);
				match(OpenBracket);
				setState(364);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Comma) {
					{
					setState(363);
					elision();
					}
				}

				setState(367);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Ellipsis) {
					{
					setState(366);
					bindingRestElement();
					}
				}

				setState(369);
				match(CloseBracket);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(370);
				match(OpenBracket);
				setState(371);
				bindingElementList();
				setState(372);
				match(CloseBracket);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(374);
				match(OpenBracket);
				setState(375);
				bindingElementList();
				setState(376);
				match(Comma);
				setState(378);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Comma) {
					{
					setState(377);
					elision();
					}
				}

				setState(381);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Ellipsis) {
					{
					setState(380);
					bindingRestElement();
					}
				}

				setState(383);
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
			setState(387);
			bindingProperty();
			setState(392);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(388);
					match(Comma);
					setState(389);
					bindingProperty();
					}
					} 
				}
				setState(394);
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
			setState(395);
			bindingElisionElement();
			setState(400);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(396);
					match(Comma);
					setState(397);
					bindingElisionElement();
					}
					} 
				}
				setState(402);
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
			setState(404);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Comma) {
				{
				setState(403);
				elision();
				}
			}

			setState(406);
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
			setState(413);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(408);
				singleNameBinding();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(409);
				propertyName();
				setState(410);
				match(Colon);
				setState(411);
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
			setState(420);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(415);
				singleNameBinding();
				}
				break;
			case OpenBracket:
			case OpenBrace:
				enterOuterAlt(_localctx, 2);
				{
				setState(416);
				bindingPattern();
				setState(418);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Assign) {
					{
					setState(417);
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
			setState(422);
			bindingIdentifier();
			setState(424);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Assign) {
				{
				setState(423);
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
			setState(426);
			match(Ellipsis);
			setState(427);
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
			setState(429);
			match(Assign);
			setState(430);
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
			setState(432);
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
			setState(434);
			if (!((_input.LA(1) != OpenBrace) && (_input.LA(1) != Function) && (_input.LA(1) != Class) && ((_input.LA(1) == Let) ? _input.LA(2) != OpenBracket : true))) throw new FailedPredicateException(this, "(_input.LA(1) != OpenBrace) && (_input.LA(1) != Function) && (_input.LA(1) != Class) && ((_input.LA(1) == Let) ? _input.LA(2) != OpenBracket : true)");
			setState(435);
			expressionSequence();
			setState(436);
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
			setState(438);
			match(If);
			setState(439);
			match(OpenParen);
			setState(440);
			expressionSequence();
			setState(441);
			match(CloseParen);
			setState(442);
			statement();
			setState(445);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
			case 1:
				{
				setState(443);
				match(Else);
				setState(444);
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
		public TerminalNode Identifier() { return getToken(JSParser.Identifier, 0); }
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
		public TerminalNode Identifier() { return getToken(JSParser.Identifier, 0); }
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
		public TerminalNode Identifier() { return getToken(JSParser.Identifier, 0); }
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
			setState(557);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,35,_ctx) ) {
			case 1:
				_localctx = new DoWhileStatementContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(447);
				match(Do);
				setState(448);
				statement();
				setState(449);
				match(While);
				setState(450);
				match(OpenParen);
				setState(451);
				expressionSequence();
				setState(452);
				match(CloseParen);
				setState(453);
				eos();
				}
				break;
			case 2:
				_localctx = new WhileStatementContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(455);
				match(While);
				setState(456);
				match(OpenParen);
				setState(457);
				expressionSequence();
				setState(458);
				match(CloseParen);
				setState(459);
				statement();
				}
				break;
			case 3:
				_localctx = new ForLoopStatementContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(461);
				match(For);
				setState(462);
				match(OpenParen);
				setState(463);
				if (!(((_input.LA(1) == Let) ? _input.LA(2) != OpenBracket : true))) throw new FailedPredicateException(this, "((_input.LA(1) == Let) ? _input.LA(2) != OpenBracket : true)");
				setState(465);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RegularExpressionLiteral) | (1L << OpenBracket) | (1L << OpenParen) | (1L << OpenBrace) | (1L << PlusPlus) | (1L << MinusMinus) | (1L << Plus) | (1L << Minus) | (1L << BitNot) | (1L << Not) | (1L << NullLiteral) | (1L << BooleanLiteral) | (1L << DecimalLiteral) | (1L << HexIntegerLiteral) | (1L << OctalIntegerLiteral) | (1L << BinaryIntegerLiteral) | (1L << Typeof))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (New - 65)) | (1L << (Void - 65)) | (1L << (Function - 65)) | (1L << (This - 65)) | (1L << (Delete - 65)) | (1L << (Class - 65)) | (1L << (Super - 65)) | (1L << (Yield - 65)) | (1L << (Identifier - 65)) | (1L << (StringLiteral - 65)) | (1L << (NoSubstitutionTemplate - 65)) | (1L << (TemplateHead - 65)))) != 0)) {
					{
					setState(464);
					expressionSequence();
					}
				}

				setState(467);
				match(SemiColon);
				setState(469);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RegularExpressionLiteral) | (1L << OpenBracket) | (1L << OpenParen) | (1L << OpenBrace) | (1L << PlusPlus) | (1L << MinusMinus) | (1L << Plus) | (1L << Minus) | (1L << BitNot) | (1L << Not) | (1L << NullLiteral) | (1L << BooleanLiteral) | (1L << DecimalLiteral) | (1L << HexIntegerLiteral) | (1L << OctalIntegerLiteral) | (1L << BinaryIntegerLiteral) | (1L << Typeof))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (New - 65)) | (1L << (Void - 65)) | (1L << (Function - 65)) | (1L << (This - 65)) | (1L << (Delete - 65)) | (1L << (Class - 65)) | (1L << (Super - 65)) | (1L << (Yield - 65)) | (1L << (Identifier - 65)) | (1L << (StringLiteral - 65)) | (1L << (NoSubstitutionTemplate - 65)) | (1L << (TemplateHead - 65)))) != 0)) {
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
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RegularExpressionLiteral) | (1L << OpenBracket) | (1L << OpenParen) | (1L << OpenBrace) | (1L << PlusPlus) | (1L << MinusMinus) | (1L << Plus) | (1L << Minus) | (1L << BitNot) | (1L << Not) | (1L << NullLiteral) | (1L << BooleanLiteral) | (1L << DecimalLiteral) | (1L << HexIntegerLiteral) | (1L << OctalIntegerLiteral) | (1L << BinaryIntegerLiteral) | (1L << Typeof))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (New - 65)) | (1L << (Void - 65)) | (1L << (Function - 65)) | (1L << (This - 65)) | (1L << (Delete - 65)) | (1L << (Class - 65)) | (1L << (Super - 65)) | (1L << (Yield - 65)) | (1L << (Identifier - 65)) | (1L << (StringLiteral - 65)) | (1L << (NoSubstitutionTemplate - 65)) | (1L << (TemplateHead - 65)))) != 0)) {
					{
					setState(472);
					expressionSequence();
					}
				}

				setState(475);
				match(CloseParen);
				setState(476);
				statement();
				}
				break;
			case 4:
				_localctx = new ForVarLoopStatementContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(477);
				match(For);
				setState(478);
				match(OpenParen);
				setState(479);
				variableDeclarationStatement();
				setState(480);
				match(SemiColon);
				setState(482);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RegularExpressionLiteral) | (1L << OpenBracket) | (1L << OpenParen) | (1L << OpenBrace) | (1L << PlusPlus) | (1L << MinusMinus) | (1L << Plus) | (1L << Minus) | (1L << BitNot) | (1L << Not) | (1L << NullLiteral) | (1L << BooleanLiteral) | (1L << DecimalLiteral) | (1L << HexIntegerLiteral) | (1L << OctalIntegerLiteral) | (1L << BinaryIntegerLiteral) | (1L << Typeof))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (New - 65)) | (1L << (Void - 65)) | (1L << (Function - 65)) | (1L << (This - 65)) | (1L << (Delete - 65)) | (1L << (Class - 65)) | (1L << (Super - 65)) | (1L << (Yield - 65)) | (1L << (Identifier - 65)) | (1L << (StringLiteral - 65)) | (1L << (NoSubstitutionTemplate - 65)) | (1L << (TemplateHead - 65)))) != 0)) {
					{
					setState(481);
					expressionSequence();
					}
				}

				setState(484);
				match(SemiColon);
				setState(486);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RegularExpressionLiteral) | (1L << OpenBracket) | (1L << OpenParen) | (1L << OpenBrace) | (1L << PlusPlus) | (1L << MinusMinus) | (1L << Plus) | (1L << Minus) | (1L << BitNot) | (1L << Not) | (1L << NullLiteral) | (1L << BooleanLiteral) | (1L << DecimalLiteral) | (1L << HexIntegerLiteral) | (1L << OctalIntegerLiteral) | (1L << BinaryIntegerLiteral) | (1L << Typeof))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (New - 65)) | (1L << (Void - 65)) | (1L << (Function - 65)) | (1L << (This - 65)) | (1L << (Delete - 65)) | (1L << (Class - 65)) | (1L << (Super - 65)) | (1L << (Yield - 65)) | (1L << (Identifier - 65)) | (1L << (StringLiteral - 65)) | (1L << (NoSubstitutionTemplate - 65)) | (1L << (TemplateHead - 65)))) != 0)) {
					{
					setState(485);
					expressionSequence();
					}
				}

				setState(488);
				match(CloseParen);
				setState(489);
				statement();
				}
				break;
			case 5:
				_localctx = new ForLexicalLoopStatementContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(491);
				match(For);
				setState(492);
				match(OpenParen);
				setState(493);
				lexicalDeclaration();
				setState(495);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RegularExpressionLiteral) | (1L << OpenBracket) | (1L << OpenParen) | (1L << OpenBrace) | (1L << PlusPlus) | (1L << MinusMinus) | (1L << Plus) | (1L << Minus) | (1L << BitNot) | (1L << Not) | (1L << NullLiteral) | (1L << BooleanLiteral) | (1L << DecimalLiteral) | (1L << HexIntegerLiteral) | (1L << OctalIntegerLiteral) | (1L << BinaryIntegerLiteral) | (1L << Typeof))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (New - 65)) | (1L << (Void - 65)) | (1L << (Function - 65)) | (1L << (This - 65)) | (1L << (Delete - 65)) | (1L << (Class - 65)) | (1L << (Super - 65)) | (1L << (Yield - 65)) | (1L << (Identifier - 65)) | (1L << (StringLiteral - 65)) | (1L << (NoSubstitutionTemplate - 65)) | (1L << (TemplateHead - 65)))) != 0)) {
					{
					setState(494);
					expressionSequence();
					}
				}

				setState(497);
				match(SemiColon);
				setState(499);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RegularExpressionLiteral) | (1L << OpenBracket) | (1L << OpenParen) | (1L << OpenBrace) | (1L << PlusPlus) | (1L << MinusMinus) | (1L << Plus) | (1L << Minus) | (1L << BitNot) | (1L << Not) | (1L << NullLiteral) | (1L << BooleanLiteral) | (1L << DecimalLiteral) | (1L << HexIntegerLiteral) | (1L << OctalIntegerLiteral) | (1L << BinaryIntegerLiteral) | (1L << Typeof))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (New - 65)) | (1L << (Void - 65)) | (1L << (Function - 65)) | (1L << (This - 65)) | (1L << (Delete - 65)) | (1L << (Class - 65)) | (1L << (Super - 65)) | (1L << (Yield - 65)) | (1L << (Identifier - 65)) | (1L << (StringLiteral - 65)) | (1L << (NoSubstitutionTemplate - 65)) | (1L << (TemplateHead - 65)))) != 0)) {
					{
					setState(498);
					expressionSequence();
					}
				}

				setState(501);
				match(CloseParen);
				setState(502);
				statement();
				}
				break;
			case 6:
				_localctx = new ForInStatementContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(504);
				match(For);
				setState(505);
				match(OpenParen);
				setState(506);
				if (!(((_input.LA(1) == Let) ? _input.LA(2) != OpenBracket : true))) throw new FailedPredicateException(this, "((_input.LA(1) == Let) ? _input.LA(2) != OpenBracket : true)");
				setState(507);
				singleExpression(0);
				setState(508);
				match(In);
				setState(509);
				expressionSequence();
				setState(510);
				match(CloseParen);
				setState(511);
				statement();
				}
				break;
			case 7:
				_localctx = new ForVarInStatementContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(513);
				match(For);
				setState(514);
				match(OpenParen);
				setState(515);
				varForDeclaration();
				setState(516);
				match(In);
				setState(517);
				expressionSequence();
				setState(518);
				match(CloseParen);
				setState(519);
				statement();
				}
				break;
			case 8:
				_localctx = new ForLexicalInStatementContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(521);
				match(For);
				setState(522);
				match(OpenParen);
				setState(523);
				forDeclaration();
				setState(524);
				match(In);
				setState(525);
				expressionSequence();
				setState(526);
				match(CloseParen);
				setState(527);
				statement();
				}
				break;
			case 9:
				_localctx = new ForOfStatementContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(529);
				match(For);
				setState(530);
				match(OpenParen);
				setState(531);
				if (!((_input.LA(1) != Let))) throw new FailedPredicateException(this, "(_input.LA(1) != Let)");
				setState(532);
				singleExpression(0);
				setState(533);
				if (!(_input.LT(1).getText().equals("of"))) throw new FailedPredicateException(this, "_input.LT(1).getText().equals(\"of\")");
				setState(534);
				match(Identifier);
				setState(535);
				singleExpression(0);
				setState(536);
				match(CloseParen);
				setState(537);
				statement();
				}
				break;
			case 10:
				_localctx = new ForVarOfStatementContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(539);
				match(For);
				setState(540);
				match(OpenParen);
				setState(541);
				varForDeclaration();
				setState(542);
				if (!(_input.LT(1).getText().equals("of"))) throw new FailedPredicateException(this, "_input.LT(1).getText().equals(\"of\")");
				setState(543);
				match(Identifier);
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
				if (!(_input.LT(1).getText().equals("of"))) throw new FailedPredicateException(this, "_input.LT(1).getText().equals(\"of\")");
				setState(552);
				match(Identifier);
				setState(553);
				singleExpression(0);
				setState(554);
				match(CloseParen);
				setState(555);
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
			setState(559);
			match(Var);
			setState(560);
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
			setState(562);
			match(Var);
			setState(563);
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
			setState(565);
			letOrConst();
			setState(566);
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
			setState(570);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(568);
				bindingIdentifier();
				}
				break;
			case OpenBracket:
			case OpenBrace:
				enterOuterAlt(_localctx, 2);
				{
				setState(569);
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
			setState(572);
			match(Continue);
			setState(575);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
			case 1:
				{
				setState(573);
				if (!(!here(LineTerminator))) throw new FailedPredicateException(this, "!here(LineTerminator)");
				setState(574);
				match(Identifier);
				}
				break;
			}
			setState(577);
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
			setState(579);
			match(Break);
			setState(582);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
			case 1:
				{
				setState(580);
				if (!(!here(LineTerminator))) throw new FailedPredicateException(this, "!here(LineTerminator)");
				setState(581);
				match(Identifier);
				}
				break;
			}
			setState(584);
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
			setState(586);
			match(Return);
			setState(589);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
			case 1:
				{
				setState(587);
				if (!(!here(LineTerminator))) throw new FailedPredicateException(this, "!here(LineTerminator)");
				setState(588);
				expressionSequence();
				}
				break;
			}
			setState(591);
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
			setState(593);
			match(With);
			setState(594);
			match(OpenParen);
			setState(595);
			expressionSequence();
			setState(596);
			match(CloseParen);
			setState(597);
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
			setState(599);
			match(Switch);
			setState(600);
			match(OpenParen);
			setState(601);
			expressionSequence();
			setState(602);
			match(CloseParen);
			setState(603);
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
			setState(605);
			match(OpenBrace);
			setState(607);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Case) {
				{
				setState(606);
				caseClauses();
				}
			}

			setState(613);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Default) {
				{
				setState(609);
				defaultClause();
				setState(611);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Case) {
					{
					setState(610);
					caseClauses();
					}
				}

				}
			}

			setState(615);
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
			setState(618); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(617);
				caseClause();
				}
				}
				setState(620); 
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
			setState(622);
			match(Case);
			setState(623);
			expressionSequence();
			setState(624);
			match(Colon);
			setState(626);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,44,_ctx) ) {
			case 1:
				{
				setState(625);
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
			setState(628);
			match(Default);
			setState(629);
			match(Colon);
			setState(631);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
			case 1:
				{
				setState(630);
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
			setState(633);
			match(Identifier);
			setState(634);
			match(Colon);
			setState(635);
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
			setState(639);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(637);
				statement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(638);
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
			setState(641);
			match(Throw);
			setState(642);
			if (!(!here(LineTerminator))) throw new FailedPredicateException(this, "!here(LineTerminator)");
			setState(643);
			expressionSequence();
			setState(644);
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
			setState(659);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,47,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(646);
				match(Try);
				setState(647);
				block();
				setState(648);
				catchProduction();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(650);
				match(Try);
				setState(651);
				block();
				setState(652);
				finallyProduction();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(654);
				match(Try);
				setState(655);
				block();
				setState(656);
				catchProduction();
				setState(657);
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
			setState(661);
			match(Catch);
			setState(662);
			match(OpenParen);
			setState(663);
			catchParameter();
			setState(664);
			match(CloseParen);
			setState(665);
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
			setState(667);
			match(Finally);
			setState(668);
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
			setState(672);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(670);
				bindingIdentifier();
				}
				break;
			case OpenBracket:
			case OpenBrace:
				enterOuterAlt(_localctx, 2);
				{
				setState(671);
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
			setState(674);
			match(Debugger);
			setState(675);
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
			setState(677);
			match(Function);
			setState(678);
			bindingIdentifier();
			setState(679);
			match(OpenParen);
			setState(680);
			formalParameters();
			setState(681);
			match(CloseParen);
			setState(682);
			match(OpenBrace);
			setState(683);
			functionBody();
			setState(684);
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
			setState(695);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,49,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(686);
				match(Function);
				setState(687);
				match(OpenParen);
				setState(688);
				formalParameters();
				setState(689);
				match(CloseParen);
				setState(690);
				match(OpenBrace);
				setState(691);
				functionBody();
				setState(692);
				match(CloseBrace);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(694);
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
			setState(697);
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
			setState(701);
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
			case Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(700);
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
			setState(709);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Ellipsis:
				enterOuterAlt(_localctx, 1);
				{
				setState(703);
				functionRestParameter();
				}
				break;
			case OpenBracket:
			case OpenBrace:
			case Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(704);
				formalsList();
				setState(707);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Comma) {
					{
					setState(705);
					match(Comma);
					setState(706);
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
			setState(711);
			formalParameter();
			setState(716);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,53,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(712);
					match(Comma);
					setState(713);
					formalParameter();
					}
					} 
				}
				setState(718);
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
			setState(719);
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
			setState(721);
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
			setState(723);
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
			setState(726);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,54,_ctx) ) {
			case 1:
				{
				setState(725);
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
			setState(728);
			arrowParameters();
			setState(729);
			if (!(!here(LineTerminator))) throw new FailedPredicateException(this, "!here(LineTerminator)");
			setState(730);
			match(Arrow);
			setState(731);
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
			setState(735);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(733);
				bindingIdentifier();
				}
				break;
			case OpenParen:
				enterOuterAlt(_localctx, 2);
				{
				setState(734);
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
			setState(743);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,56,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(737);
				if (!((_input.LA(1) != OpenBrace))) throw new FailedPredicateException(this, "(_input.LA(1) != OpenBrace)");
				setState(738);
				singleExpression(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(739);
				match(OpenBrace);
				setState(740);
				functionBody();
				setState(741);
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
			setState(761);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,57,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(745);
				match(OpenParen);
				setState(746);
				expressionSequence();
				setState(747);
				match(CloseParen);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(749);
				match(OpenParen);
				setState(750);
				match(CloseParen);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(751);
				match(OpenParen);
				setState(752);
				bindingRestElement();
				setState(753);
				match(CloseParen);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(755);
				match(OpenParen);
				setState(756);
				expressionSequence();
				setState(757);
				match(Comma);
				setState(758);
				bindingRestElement();
				setState(759);
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
			setState(791);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,58,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(763);
				propertyName();
				setState(764);
				match(OpenParen);
				setState(765);
				strictFormalParameters();
				setState(766);
				match(CloseParen);
				setState(767);
				match(OpenBrace);
				setState(768);
				functionBody();
				setState(769);
				match(CloseBrace);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(771);
				generatorMethod();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(772);
				if (!(_input.LT(1).getText().equals("get"))) throw new FailedPredicateException(this, "_input.LT(1).getText().equals(\"get\")");
				setState(773);
				match(Identifier);
				setState(774);
				propertyName();
				setState(775);
				match(OpenParen);
				setState(776);
				match(CloseParen);
				setState(777);
				match(OpenBrace);
				setState(778);
				functionBody();
				setState(779);
				match(CloseBrace);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(781);
				if (!(_input.LT(1).getText().equals("set"))) throw new FailedPredicateException(this, "_input.LT(1).getText().equals(\"set\")");
				setState(782);
				match(Identifier);
				setState(783);
				propertyName();
				setState(784);
				match(OpenParen);
				setState(785);
				propertySetParameterList();
				setState(786);
				match(CloseParen);
				setState(787);
				match(OpenBrace);
				setState(788);
				functionBody();
				setState(789);
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
			setState(793);
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
			setState(795);
			match(Multiply);
			setState(796);
			propertyName();
			setState(797);
			match(OpenParen);
			setState(798);
			strictFormalParameters();
			setState(799);
			match(CloseParen);
			setState(800);
			match(OpenBrace);
			setState(801);
			generatorBody();
			setState(802);
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
			setState(804);
			match(Function);
			setState(805);
			match(Multiply);
			setState(806);
			bindingIdentifier();
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
			setState(814);
			match(Function);
			setState(815);
			match(Multiply);
			setState(817);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Identifier) {
				{
				setState(816);
				bindingIdentifier();
				}
			}

			setState(819);
			match(OpenParen);
			setState(820);
			formalParameters();
			setState(821);
			match(CloseParen);
			setState(822);
			match(OpenBrace);
			setState(823);
			generatorBody();
			setState(824);
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
			setState(826);
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
			setState(828);
			match(Class);
			setState(829);
			bindingIdentifier();
			setState(830);
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
			setState(832);
			match(Class);
			setState(834);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Identifier) {
				{
				setState(833);
				bindingIdentifier();
				}
			}

			setState(836);
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
			setState(839);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Extends) {
				{
				setState(838);
				classHeritage();
				}
			}

			setState(841);
			match(OpenBrace);
			setState(843);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,62,_ctx) ) {
			case 1:
				{
				setState(842);
				classBody();
				}
				break;
			}
			setState(845);
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
			setState(847);
			match(Extends);
			setState(848);
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
			setState(850);
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
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(853); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(852);
					classElement();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(855); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,63,_ctx);
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
			setState(861);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,64,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(857);
				methodDefinition();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(858);
				match(Static);
				setState(859);
				methodDefinition();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(860);
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
			setState(880);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,67,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(863);
				match(OpenBracket);
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
				match(CloseBracket);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(868);
				match(OpenBracket);
				setState(869);
				elementList(0);
				setState(870);
				match(CloseBracket);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(872);
				match(OpenBracket);
				setState(873);
				elementList(0);
				setState(874);
				match(Comma);
				setState(876);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Comma) {
					{
					setState(875);
					elision();
					}
				}

				setState(878);
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
			setState(891);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,70,_ctx) ) {
			case 1:
				{
				setState(884);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Comma) {
					{
					setState(883);
					elision();
					}
				}

				setState(886);
				singleExpression(0);
				}
				break;
			case 2:
				{
				setState(888);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Comma) {
					{
					setState(887);
					elision();
					}
				}

				setState(890);
				spreadElement();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(907);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,74,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(905);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,73,_ctx) ) {
					case 1:
						{
						_localctx = new ElementListContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_elementList);
						setState(893);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(894);
						match(Comma);
						setState(896);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==Comma) {
							{
							setState(895);
							elision();
							}
						}

						setState(898);
						singleExpression(0);
						}
						break;
					case 2:
						{
						_localctx = new ElementListContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_elementList);
						setState(899);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(900);
						match(Comma);
						setState(902);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==Comma) {
							{
							setState(901);
							elision();
							}
						}

						setState(904);
						spreadElement();
						}
						break;
					}
					} 
				}
				setState(909);
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
			setState(911); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(910);
				match(Comma);
				}
				}
				setState(913); 
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
			setState(915);
			match(Ellipsis);
			setState(916);
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
			setState(927);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,77,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(918);
				match(OpenBrace);
				setState(919);
				match(CloseBrace);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(920);
				match(OpenBrace);
				setState(921);
				propertyDefinitionList();
				setState(923);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Comma) {
					{
					setState(922);
					match(Comma);
					}
				}

				setState(925);
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
			setState(929);
			propertyDefinition();
			setState(934);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,78,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(930);
					match(Comma);
					setState(931);
					propertyDefinition();
					}
					} 
				}
				setState(936);
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
			setState(944);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,79,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(937);
				identifierReference();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(938);
				coverInitializedName();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(939);
				propertyName();
				setState(940);
				match(Colon);
				setState(941);
				singleExpression(0);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(943);
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
			setState(948);
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
				setState(946);
				literalPropertyName();
				}
				break;
			case OpenBracket:
				enterOuterAlt(_localctx, 2);
				{
				setState(947);
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
			setState(953);
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
				setState(950);
				identifierName();
				}
				break;
			case StringLiteral:
				enterOuterAlt(_localctx, 2);
				{
				setState(951);
				match(StringLiteral);
				}
				break;
			case DecimalLiteral:
			case HexIntegerLiteral:
			case OctalIntegerLiteral:
			case BinaryIntegerLiteral:
				enterOuterAlt(_localctx, 3);
				{
				setState(952);
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
			setState(955);
			match(OpenBracket);
			setState(956);
			singleExpression(0);
			setState(957);
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
			setState(959);
			identifierReference();
			setState(960);
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
			setState(962);
			match(OpenParen);
			setState(964);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RegularExpressionLiteral) | (1L << OpenBracket) | (1L << OpenParen) | (1L << OpenBrace) | (1L << Ellipsis) | (1L << PlusPlus) | (1L << MinusMinus) | (1L << Plus) | (1L << Minus) | (1L << BitNot) | (1L << Not) | (1L << NullLiteral) | (1L << BooleanLiteral) | (1L << DecimalLiteral) | (1L << HexIntegerLiteral) | (1L << OctalIntegerLiteral) | (1L << BinaryIntegerLiteral) | (1L << Typeof))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (New - 65)) | (1L << (Void - 65)) | (1L << (Function - 65)) | (1L << (This - 65)) | (1L << (Delete - 65)) | (1L << (Class - 65)) | (1L << (Super - 65)) | (1L << (Yield - 65)) | (1L << (Identifier - 65)) | (1L << (StringLiteral - 65)) | (1L << (NoSubstitutionTemplate - 65)) | (1L << (TemplateHead - 65)))) != 0)) {
				{
				setState(963);
				argumentList();
				}
			}

			setState(966);
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
			setState(970);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Ellipsis:
				{
				setState(968);
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
			case Identifier:
			case StringLiteral:
			case NoSubstitutionTemplate:
			case TemplateHead:
				{
				setState(969);
				singleExpression(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(979);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(972);
				match(Comma);
				setState(975);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case Ellipsis:
					{
					setState(973);
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
				case Identifier:
				case StringLiteral:
				case NoSubstitutionTemplate:
				case TemplateHead:
					{
					setState(974);
					singleExpression(0);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				}
				setState(981);
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
			setState(982);
			singleExpression(0);
			setState(987);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,86,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(983);
					match(Comma);
					setState(984);
					singleExpression(0);
					}
					} 
				}
				setState(989);
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
		public TerminalNode Identifier() { return getToken(JSParser.Identifier, 0); }
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
		public TerminalNode Identifier() { return getToken(JSParser.Identifier, 0); }
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
			setState(1042);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,90,_ctx) ) {
			case 1:
				{
				_localctx = new FunctionExpressionExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(991);
				functionExpression();
				}
				break;
			case 2:
				{
				_localctx = new ClassExpressionExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(992);
				classExpression();
				}
				break;
			case 3:
				{
				_localctx = new GeneratorExpressionExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(993);
				generatorExpression();
				}
				break;
			case 4:
				{
				_localctx = new NewTargetExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(994);
				match(New);
				setState(995);
				match(Dot);
				setState(996);
				if (!(_input.LT(1).getText().equals("target"))) throw new FailedPredicateException(this, "_input.LT(1).getText().equals(\"target\")");
				setState(997);
				match(Identifier);
				}
				break;
			case 5:
				{
				_localctx = new NewExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(998);
				match(New);
				setState(999);
				singleExpression(0);
				setState(1001);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,87,_ctx) ) {
				case 1:
					{
					setState(1000);
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
				setState(1003);
				match(Delete);
				setState(1004);
				singleExpression(35);
				}
				break;
			case 7:
				{
				_localctx = new VoidExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1005);
				match(Void);
				setState(1006);
				singleExpression(34);
				}
				break;
			case 8:
				{
				_localctx = new TypeofExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1007);
				match(Typeof);
				setState(1008);
				singleExpression(33);
				}
				break;
			case 9:
				{
				_localctx = new PreIncrementExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1009);
				match(PlusPlus);
				setState(1010);
				singleExpression(32);
				}
				break;
			case 10:
				{
				_localctx = new PreDecreaseExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1011);
				match(MinusMinus);
				setState(1012);
				singleExpression(31);
				}
				break;
			case 11:
				{
				_localctx = new UnaryPlusExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1013);
				match(Plus);
				setState(1014);
				singleExpression(30);
				}
				break;
			case 12:
				{
				_localctx = new UnaryMinusExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1015);
				match(Minus);
				setState(1016);
				singleExpression(29);
				}
				break;
			case 13:
				{
				_localctx = new BitNotExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1017);
				match(BitNot);
				setState(1018);
				singleExpression(28);
				}
				break;
			case 14:
				{
				_localctx = new NotExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1019);
				match(Not);
				setState(1020);
				singleExpression(27);
				}
				break;
			case 15:
				{
				_localctx = new YieldExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1021);
				match(Yield);
				setState(1027);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,89,_ctx) ) {
				case 1:
					{
					setState(1022);
					if (!(!here(LineTerminator))) throw new FailedPredicateException(this, "!here(LineTerminator)");
					setState(1024);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==Multiply) {
						{
						setState(1023);
						match(Multiply);
						}
					}

					setState(1026);
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
				setState(1029);
				arrowFunction();
				}
				break;
			case 17:
				{
				_localctx = new ThisExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1030);
				match(This);
				}
				break;
			case 18:
				{
				_localctx = new SuperExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1031);
				match(Super);
				}
				break;
			case 19:
				{
				_localctx = new IdentifierExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1032);
				match(Identifier);
				}
				break;
			case 20:
				{
				_localctx = new LiteralExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1033);
				literal();
				}
				break;
			case 21:
				{
				_localctx = new ArrayLiteralExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1034);
				arrayLiteral();
				}
				break;
			case 22:
				{
				_localctx = new ObjectLiteralExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1035);
				objectLiteral();
				}
				break;
			case 23:
				{
				_localctx = new RegularExpressionLiteralExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1036);
				match(RegularExpressionLiteral);
				}
				break;
			case 24:
				{
				_localctx = new TemplateLiteralExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1037);
				templateLiteral();
				}
				break;
			case 25:
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
						if (!(precpred(_ctx, 26))) throw new FailedPredicateException(this, "precpred(_ctx, 26)");
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
						singleExpression(27);
						}
						break;
					case 2:
						{
						_localctx = new AdditiveExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1047);
						if (!(precpred(_ctx, 25))) throw new FailedPredicateException(this, "precpred(_ctx, 25)");
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
						singleExpression(26);
						}
						break;
					case 3:
						{
						_localctx = new BitShiftExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1050);
						if (!(precpred(_ctx, 24))) throw new FailedPredicateException(this, "precpred(_ctx, 24)");
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
						singleExpression(25);
						}
						break;
					case 4:
						{
						_localctx = new RelationalExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1053);
						if (!(precpred(_ctx, 23))) throw new FailedPredicateException(this, "precpred(_ctx, 23)");
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
						singleExpression(24);
						}
						break;
					case 5:
						{
						_localctx = new InstanceofExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1056);
						if (!(precpred(_ctx, 22))) throw new FailedPredicateException(this, "precpred(_ctx, 22)");
						setState(1057);
						match(Instanceof);
						setState(1058);
						singleExpression(23);
						}
						break;
					case 6:
						{
						_localctx = new InExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1059);
						if (!(precpred(_ctx, 21))) throw new FailedPredicateException(this, "precpred(_ctx, 21)");
						setState(1060);
						match(In);
						setState(1061);
						singleExpression(22);
						}
						break;
					case 7:
						{
						_localctx = new EqualityExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1062);
						if (!(precpred(_ctx, 20))) throw new FailedPredicateException(this, "precpred(_ctx, 20)");
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
						singleExpression(21);
						}
						break;
					case 8:
						{
						_localctx = new BitAndExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1065);
						if (!(precpred(_ctx, 19))) throw new FailedPredicateException(this, "precpred(_ctx, 19)");
						setState(1066);
						match(BitAnd);
						setState(1067);
						singleExpression(20);
						}
						break;
					case 9:
						{
						_localctx = new BitXOrExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1068);
						if (!(precpred(_ctx, 18))) throw new FailedPredicateException(this, "precpred(_ctx, 18)");
						setState(1069);
						match(BitXOr);
						setState(1070);
						singleExpression(19);
						}
						break;
					case 10:
						{
						_localctx = new BitOrExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1071);
						if (!(precpred(_ctx, 17))) throw new FailedPredicateException(this, "precpred(_ctx, 17)");
						setState(1072);
						match(BitOr);
						setState(1073);
						singleExpression(18);
						}
						break;
					case 11:
						{
						_localctx = new LogicalAndExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1074);
						if (!(precpred(_ctx, 16))) throw new FailedPredicateException(this, "precpred(_ctx, 16)");
						setState(1075);
						match(And);
						setState(1076);
						singleExpression(17);
						}
						break;
					case 12:
						{
						_localctx = new LogicalOrExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1077);
						if (!(precpred(_ctx, 15))) throw new FailedPredicateException(this, "precpred(_ctx, 15)");
						setState(1078);
						match(Or);
						setState(1079);
						singleExpression(16);
						}
						break;
					case 13:
						{
						_localctx = new TernaryExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1080);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(1081);
						match(QuestionMark);
						setState(1082);
						singleExpression(0);
						setState(1083);
						match(Colon);
						setState(1084);
						singleExpression(15);
						}
						break;
					case 14:
						{
						_localctx = new AssignmentExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1086);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(1087);
						match(Assign);
						setState(1088);
						singleExpression(12);
						}
						break;
					case 15:
						{
						_localctx = new AssignmentOperatorExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1089);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(1090);
						assignmentOperator();
						setState(1091);
						singleExpression(11);
						}
						break;
					case 16:
						{
						_localctx = new MemberIndexExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1093);
						if (!(precpred(_ctx, 41))) throw new FailedPredicateException(this, "precpred(_ctx, 41)");
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
						if (!(precpred(_ctx, 40))) throw new FailedPredicateException(this, "precpred(_ctx, 40)");
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
						if (!(precpred(_ctx, 39))) throw new FailedPredicateException(this, "precpred(_ctx, 39)");
						setState(1102);
						templateLiteral();
						}
						break;
					case 19:
						{
						_localctx = new ArgumentsExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1103);
						if (!(precpred(_ctx, 38))) throw new FailedPredicateException(this, "precpred(_ctx, 38)");
						setState(1104);
						arguments();
						}
						break;
					case 20:
						{
						_localctx = new PostIncrementExpressionContext(new SingleExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_singleExpression);
						setState(1105);
						if (!(precpred(_ctx, 37))) throw new FailedPredicateException(this, "precpred(_ctx, 37)");
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
						if (!(precpred(_ctx, 36))) throw new FailedPredicateException(this, "precpred(_ctx, 36)");
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
			setState(1124);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,94,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1121);
					moduleItem();
					}
					} 
				}
				setState(1126);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,94,_ctx);
			}
			setState(1127);
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
			setState(1132);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,95,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1129);
				importDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1130);
				exportDeclaration();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1131);
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
			setState(1143);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,96,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1134);
				match(Import);
				setState(1135);
				importClause();
				setState(1136);
				fromClause();
				setState(1137);
				eos();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1139);
				match(Import);
				setState(1140);
				moduleSpecifier();
				setState(1141);
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
			setState(1156);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,97,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1145);
				importedDefaultBinding();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1146);
				nameSpaceImport();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1147);
				namedImports();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1148);
				importedDefaultBinding();
				setState(1149);
				match(Comma);
				setState(1150);
				nameSpaceImport();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1152);
				importedDefaultBinding();
				setState(1153);
				match(Comma);
				setState(1154);
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

	public static class NameSpaceImportContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(JSParser.Identifier, 0); }
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
			setState(1160);
			match(Multiply);
			setState(1161);
			if (!(_input.LT(1).getText().equals("as"))) throw new FailedPredicateException(this, "_input.LT(1).getText().equals(\"as\")");
			setState(1162);
			match(Identifier);
			setState(1163);
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
			setState(1176);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,98,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1165);
				match(OpenBrace);
				setState(1166);
				match(CloseBrace);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1167);
				match(OpenBrace);
				setState(1168);
				importsList();
				setState(1169);
				match(CloseBrace);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1171);
				match(OpenBrace);
				setState(1172);
				importsList();
				setState(1173);
				match(Comma);
				setState(1174);
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
		public TerminalNode Identifier() { return getToken(JSParser.Identifier, 0); }
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
			setState(1178);
			if (!(_input.LT(1).getText().equals("from"))) throw new FailedPredicateException(this, "_input.LT(1).getText().equals(\"from\")");
			setState(1179);
			match(Identifier);
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
			setState(1182);
			importSpecifier();
			setState(1187);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,99,_ctx);
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
		public TerminalNode Identifier() { return getToken(JSParser.Identifier, 0); }
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
			setState(1196);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,100,_ctx) ) {
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
				if (!(_input.LT(1).getText().equals("as"))) throw new FailedPredicateException(this, "_input.LT(1).getText().equals(\"as\")");
				setState(1193);
				match(Identifier);
				setState(1194);
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
			setState(1198);
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
			setState(1200);
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
			setState(1232);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,101,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1202);
				match(Export);
				setState(1203);
				match(Multiply);
				setState(1204);
				fromClause();
				setState(1205);
				eos();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1207);
				match(Export);
				setState(1208);
				exportClause();
				setState(1209);
				fromClause();
				setState(1210);
				eos();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1212);
				match(Export);
				setState(1213);
				exportClause();
				setState(1214);
				eos();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1216);
				match(Export);
				setState(1217);
				variableStatement();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1218);
				match(Export);
				setState(1219);
				declaration();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1220);
				match(Export);
				setState(1221);
				match(Default);
				setState(1222);
				hoistableDeclaration();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1223);
				match(Export);
				setState(1224);
				match(Default);
				setState(1225);
				classDeclaration();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(1226);
				match(Export);
				setState(1227);
				match(Default);
				setState(1228);
				if (!((_input.LA(1) != Function) && (_input.LA(1) != Class))) throw new FailedPredicateException(this, "(_input.LA(1) != Function) && (_input.LA(1) != Class)");
				setState(1229);
				singleExpression(0);
				setState(1230);
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
			setState(1245);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,102,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1234);
				match(OpenBrace);
				setState(1235);
				match(CloseBrace);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1236);
				match(OpenBrace);
				setState(1237);
				exportsList();
				setState(1238);
				match(CloseBrace);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1240);
				match(OpenBrace);
				setState(1241);
				exportsList();
				setState(1242);
				match(Comma);
				setState(1243);
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
			setState(1247);
			exportSpecifier();
			setState(1252);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,103,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1248);
					match(Comma);
					setState(1249);
					exportSpecifier();
					}
					} 
				}
				setState(1254);
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
		public TerminalNode Identifier() { return getToken(JSParser.Identifier, 0); }
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
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1255);
			identifierName();
			setState(1259);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,104,_ctx) ) {
			case 1:
				{
				setState(1256);
				if (!(_input.LT(1).getText().equals("as"))) throw new FailedPredicateException(this, "_input.LT(1).getText().equals(\"as\")");
				setState(1257);
				match(Identifier);
				setState(1258);
				identifierName();
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
			setState(1261);
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
			setState(1265);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NullLiteral:
			case BooleanLiteral:
			case StringLiteral:
				enterOuterAlt(_localctx, 1);
				{
				setState(1263);
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
				setState(1264);
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
			setState(1267);
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
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterIdentifierName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitIdentifierName(this);
		}
	}

	public final IdentifierNameContext identifierName() throws RecognitionException {
		IdentifierNameContext _localctx = new IdentifierNameContext(_ctx, getState());
		enterRule(_localctx, 230, RULE_identifierName);
		try {
			setState(1271);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(1269);
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
				setState(1270);
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
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterIdentifierReference(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitIdentifierReference(this);
		}
	}

	public final IdentifierReferenceContext identifierReference() throws RecognitionException {
		IdentifierReferenceContext _localctx = new IdentifierReferenceContext(_ctx, getState());
		enterRule(_localctx, 232, RULE_identifierReference);
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

	public static class BindingIdentifierContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(JSParser.Identifier, 0); }
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
		enterRule(_localctx, 234, RULE_bindingIdentifier);
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

	public static class LabelIdentifierContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(JSParser.Identifier, 0); }
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
		enterRule(_localctx, 236, RULE_labelIdentifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1277);
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
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).enterReservedWord(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JSParserListener ) ((JSParserListener)listener).exitReservedWord(this);
		}
	}

	public final ReservedWordContext reservedWord() throws RecognitionException {
		ReservedWordContext _localctx = new ReservedWordContext(_ctx, getState());
		enterRule(_localctx, 238, RULE_reservedWord);
		int _la;
		try {
			setState(1282);
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
				setState(1279);
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
				setState(1280);
				futureReservedWord();
				}
				break;
			case NullLiteral:
			case BooleanLiteral:
				enterOuterAlt(_localctx, 3);
				{
				setState(1281);
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
		enterRule(_localctx, 240, RULE_keyword);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1284);
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
		enterRule(_localctx, 242, RULE_futureReservedWord);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1286);
			_la = _input.LA(1);
			if ( !(((((_la - 94)) & ~0x3f) == 0 && ((1L << (_la - 94)) & ((1L << (Enum - 94)) | (1L << (Await - 94)) | (1L << (Implements - 94)) | (1L << (Private - 94)) | (1L << (Public - 94)) | (1L << (Interface - 94)) | (1L << (Package - 94)) | (1L << (Protected - 94)))) != 0)) ) {
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
		enterRule(_localctx, 244, RULE_eos);
		try {
			setState(1292);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,108,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1288);
				match(SemiColon);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1289);
				match(EOF);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1290);
				if (!(lineTerminatorAhead())) throw new FailedPredicateException(this, "lineTerminatorAhead()");
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1291);
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
		enterRule(_localctx, 246, RULE_eof);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1294);
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
		enterRule(_localctx, 248, RULE_templateLiteral);
		try {
			setState(1301);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NoSubstitutionTemplate:
				enterOuterAlt(_localctx, 1);
				{
				setState(1296);
				match(NoSubstitutionTemplate);
				}
				break;
			case TemplateHead:
				enterOuterAlt(_localctx, 2);
				{
				setState(1297);
				match(TemplateHead);
				setState(1298);
				expressionSequence();
				setState(1299);
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
		enterRule(_localctx, 250, RULE_templateSpans);
		try {
			setState(1307);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TemplateTail:
				enterOuterAlt(_localctx, 1);
				{
				setState(1303);
				match(TemplateTail);
				}
				break;
			case TemplateMiddle:
				enterOuterAlt(_localctx, 2);
				{
				setState(1304);
				templateMiddleList(0);
				setState(1305);
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
		int _startState = 252;
		enterRecursionRule(_localctx, 252, RULE_templateMiddleList, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(1310);
			match(TemplateMiddle);
			setState(1311);
			expressionSequence();
			}
			_ctx.stop = _input.LT(-1);
			setState(1318);
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
					setState(1313);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(1314);
					match(TemplateMiddle);
					setState(1315);
					expressionSequence();
					}
					} 
				}
				setState(1320);
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
		case 66:
			return methodDefinition_sempred((MethodDefinitionContext)_localctx, predIndex);
		case 80:
			return elementList_sempred((ElementListContext)_localctx, predIndex);
		case 93:
			return singleExpression_sempred((SingleExpressionContext)_localctx, predIndex);
		case 101:
			return nameSpaceImport_sempred((NameSpaceImportContext)_localctx, predIndex);
		case 103:
			return fromClause_sempred((FromClauseContext)_localctx, predIndex);
		case 105:
			return importSpecifier_sempred((ImportSpecifierContext)_localctx, predIndex);
		case 108:
			return exportDeclaration_sempred((ExportDeclarationContext)_localctx, predIndex);
		case 111:
			return exportSpecifier_sempred((ExportSpecifierContext)_localctx, predIndex);
		case 122:
			return eos_sempred((EosContext)_localctx, predIndex);
		case 126:
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
		case 4:
			return _input.LT(1).getText().equals("of");
		case 5:
			return _input.LT(1).getText().equals("of");
		case 6:
			return _input.LT(1).getText().equals("of");
		}
		return true;
	}
	private boolean continueStatement_sempred(ContinueStatementContext _localctx, int predIndex) {
		switch (predIndex) {
		case 7:
			return !here(LineTerminator);
		}
		return true;
	}
	private boolean breakStatement_sempred(BreakStatementContext _localctx, int predIndex) {
		switch (predIndex) {
		case 8:
			return !here(LineTerminator);
		}
		return true;
	}
	private boolean returnStatement_sempred(ReturnStatementContext _localctx, int predIndex) {
		switch (predIndex) {
		case 9:
			return !here(LineTerminator);
		}
		return true;
	}
	private boolean throwStatement_sempred(ThrowStatementContext _localctx, int predIndex) {
		switch (predIndex) {
		case 10:
			return !here(LineTerminator);
		}
		return true;
	}
	private boolean arrowFunction_sempred(ArrowFunctionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 11:
			return !here(LineTerminator);
		}
		return true;
	}
	private boolean conciseBody_sempred(ConciseBodyContext _localctx, int predIndex) {
		switch (predIndex) {
		case 12:
			return (_input.LA(1) != OpenBrace);
		}
		return true;
	}
	private boolean methodDefinition_sempred(MethodDefinitionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 13:
			return _input.LT(1).getText().equals("get");
		case 14:
			return _input.LT(1).getText().equals("set");
		}
		return true;
	}
	private boolean elementList_sempred(ElementListContext _localctx, int predIndex) {
		switch (predIndex) {
		case 15:
			return precpred(_ctx, 2);
		case 16:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean singleExpression_sempred(SingleExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 17:
			return _input.LT(1).getText().equals("target");
		case 18:
			return !here(LineTerminator);
		case 19:
			return precpred(_ctx, 26);
		case 20:
			return precpred(_ctx, 25);
		case 21:
			return precpred(_ctx, 24);
		case 22:
			return precpred(_ctx, 23);
		case 23:
			return precpred(_ctx, 22);
		case 24:
			return precpred(_ctx, 21);
		case 25:
			return precpred(_ctx, 20);
		case 26:
			return precpred(_ctx, 19);
		case 27:
			return precpred(_ctx, 18);
		case 28:
			return precpred(_ctx, 17);
		case 29:
			return precpred(_ctx, 16);
		case 30:
			return precpred(_ctx, 15);
		case 31:
			return precpred(_ctx, 14);
		case 32:
			return precpred(_ctx, 11);
		case 33:
			return precpred(_ctx, 10);
		case 34:
			return precpred(_ctx, 41);
		case 35:
			return precpred(_ctx, 40);
		case 36:
			return precpred(_ctx, 39);
		case 37:
			return precpred(_ctx, 38);
		case 38:
			return precpred(_ctx, 37);
		case 39:
			return !here(LineTerminator);
		case 40:
			return precpred(_ctx, 36);
		case 41:
			return !here(LineTerminator);
		}
		return true;
	}
	private boolean nameSpaceImport_sempred(NameSpaceImportContext _localctx, int predIndex) {
		switch (predIndex) {
		case 42:
			return _input.LT(1).getText().equals("as");
		}
		return true;
	}
	private boolean fromClause_sempred(FromClauseContext _localctx, int predIndex) {
		switch (predIndex) {
		case 43:
			return _input.LT(1).getText().equals("from");
		}
		return true;
	}
	private boolean importSpecifier_sempred(ImportSpecifierContext _localctx, int predIndex) {
		switch (predIndex) {
		case 44:
			return _input.LT(1).getText().equals("as");
		}
		return true;
	}
	private boolean exportDeclaration_sempred(ExportDeclarationContext _localctx, int predIndex) {
		switch (predIndex) {
		case 45:
			return (_input.LA(1) != Function) && (_input.LA(1) != Class);
		}
		return true;
	}
	private boolean exportSpecifier_sempred(ExportSpecifierContext _localctx, int predIndex) {
		switch (predIndex) {
		case 46:
			return _input.LT(1).getText().equals("as");
		}
		return true;
	}
	private boolean eos_sempred(EosContext _localctx, int predIndex) {
		switch (predIndex) {
		case 47:
			return lineTerminatorAhead();
		case 48:
			return _input.LT(1).getType() == CloseBrace;
		}
		return true;
	}
	private boolean templateMiddleList_sempred(TemplateMiddleListContext _localctx, int predIndex) {
		switch (predIndex) {
		case 49:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3q\u052c\4\2\t\2\4"+
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
		"\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3\u0111"+
		"\n\3\3\4\3\4\3\4\5\4\u0116\n\4\3\5\3\5\5\5\u011a\n\5\3\6\3\6\5\6\u011e"+
		"\n\6\3\7\3\7\3\b\3\b\5\b\u0124\n\b\3\b\3\b\3\t\6\t\u0129\n\t\r\t\16\t"+
		"\u012a\3\n\3\n\5\n\u012f\n\n\3\13\3\13\3\13\3\13\3\f\3\f\3\r\3\r\3\r\7"+
		"\r\u013a\n\r\f\r\16\r\u013d\13\r\3\16\3\16\5\16\u0141\n\16\3\16\3\16\3"+
		"\16\5\16\u0146\n\16\3\17\3\17\3\17\3\20\3\20\3\20\7\20\u014e\n\20\f\20"+
		"\16\20\u0151\13\20\3\21\3\21\5\21\u0155\n\21\3\21\3\21\3\21\5\21\u015a"+
		"\n\21\3\22\3\22\5\22\u015e\n\22\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23"+
		"\3\23\3\23\3\23\5\23\u016b\n\23\3\24\3\24\5\24\u016f\n\24\3\24\5\24\u0172"+
		"\n\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\5\24\u017d\n\24\3\24"+
		"\5\24\u0180\n\24\3\24\3\24\5\24\u0184\n\24\3\25\3\25\3\25\7\25\u0189\n"+
		"\25\f\25\16\25\u018c\13\25\3\26\3\26\3\26\7\26\u0191\n\26\f\26\16\26\u0194"+
		"\13\26\3\27\5\27\u0197\n\27\3\27\3\27\3\30\3\30\3\30\3\30\3\30\5\30\u01a0"+
		"\n\30\3\31\3\31\3\31\5\31\u01a5\n\31\5\31\u01a7\n\31\3\32\3\32\5\32\u01ab"+
		"\n\32\3\33\3\33\3\33\3\34\3\34\3\34\3\35\3\35\3\36\3\36\3\36\3\36\3\37"+
		"\3\37\3\37\3\37\3\37\3\37\3\37\5\37\u01c0\n\37\3 \3 \3 \3 \3 \3 \3 \3"+
		" \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \5 \u01d4\n \3 \3 \5 \u01d8\n \3 \3 \5"+
		" \u01dc\n \3 \3 \3 \3 \3 \3 \3 \5 \u01e5\n \3 \3 \5 \u01e9\n \3 \3 \3"+
		" \3 \3 \3 \3 \5 \u01f2\n \3 \3 \5 \u01f6\n \3 \3 \3 \3 \3 \3 \3 \3 \3"+
		" \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3"+
		" \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3"+
		" \3 \5 \u0230\n \3!\3!\3!\3\"\3\"\3\"\3#\3#\3#\3$\3$\5$\u023d\n$\3%\3"+
		"%\3%\5%\u0242\n%\3%\3%\3&\3&\3&\5&\u0249\n&\3&\3&\3\'\3\'\3\'\5\'\u0250"+
		"\n\'\3\'\3\'\3(\3(\3(\3(\3(\3(\3)\3)\3)\3)\3)\3)\3*\3*\5*\u0262\n*\3*"+
		"\3*\5*\u0266\n*\5*\u0268\n*\3*\3*\3+\6+\u026d\n+\r+\16+\u026e\3,\3,\3"+
		",\3,\5,\u0275\n,\3-\3-\3-\5-\u027a\n-\3.\3.\3.\3.\3/\3/\5/\u0282\n/\3"+
		"\60\3\60\3\60\3\60\3\60\3\61\3\61\3\61\3\61\3\61\3\61\3\61\3\61\3\61\3"+
		"\61\3\61\3\61\3\61\5\61\u0296\n\61\3\62\3\62\3\62\3\62\3\62\3\62\3\63"+
		"\3\63\3\63\3\64\3\64\5\64\u02a3\n\64\3\65\3\65\3\65\3\66\3\66\3\66\3\66"+
		"\3\66\3\66\3\66\3\66\3\66\3\67\3\67\3\67\3\67\3\67\3\67\3\67\3\67\3\67"+
		"\5\67\u02ba\n\67\38\38\39\39\59\u02c0\n9\3:\3:\3:\3:\5:\u02c6\n:\5:\u02c8"+
		"\n:\3;\3;\3;\7;\u02cd\n;\f;\16;\u02d0\13;\3<\3<\3=\3=\3>\3>\3?\5?\u02d9"+
		"\n?\3@\3@\3@\3@\3@\3A\3A\5A\u02e2\nA\3B\3B\3B\3B\3B\3B\5B\u02ea\nB\3C"+
		"\3C\3C\3C\3C\3C\3C\3C\3C\3C\3C\3C\3C\3C\3C\3C\5C\u02fc\nC\3D\3D\3D\3D"+
		"\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D\3D"+
		"\3D\5D\u031a\nD\3E\3E\3F\3F\3F\3F\3F\3F\3F\3F\3F\3G\3G\3G\3G\3G\3G\3G"+
		"\3G\3G\3G\3H\3H\3H\5H\u0334\nH\3H\3H\3H\3H\3H\3H\3H\3I\3I\3J\3J\3J\3J"+
		"\3K\3K\5K\u0345\nK\3K\3K\3L\5L\u034a\nL\3L\3L\5L\u034e\nL\3L\3L\3M\3M"+
		"\3M\3N\3N\3O\6O\u0358\nO\rO\16O\u0359\3P\3P\3P\3P\5P\u0360\nP\3Q\3Q\5"+
		"Q\u0364\nQ\3Q\3Q\3Q\3Q\3Q\3Q\3Q\3Q\3Q\5Q\u036f\nQ\3Q\3Q\5Q\u0373\nQ\3"+
		"R\3R\5R\u0377\nR\3R\3R\5R\u037b\nR\3R\5R\u037e\nR\3R\3R\3R\5R\u0383\n"+
		"R\3R\3R\3R\3R\5R\u0389\nR\3R\7R\u038c\nR\fR\16R\u038f\13R\3S\6S\u0392"+
		"\nS\rS\16S\u0393\3T\3T\3T\3U\3U\3U\3U\3U\5U\u039e\nU\3U\3U\5U\u03a2\n"+
		"U\3V\3V\3V\7V\u03a7\nV\fV\16V\u03aa\13V\3W\3W\3W\3W\3W\3W\3W\5W\u03b3"+
		"\nW\3X\3X\5X\u03b7\nX\3Y\3Y\3Y\5Y\u03bc\nY\3Z\3Z\3Z\3Z\3[\3[\3[\3\\\3"+
		"\\\5\\\u03c7\n\\\3\\\3\\\3]\3]\5]\u03cd\n]\3]\3]\3]\5]\u03d2\n]\7]\u03d4"+
		"\n]\f]\16]\u03d7\13]\3^\3^\3^\7^\u03dc\n^\f^\16^\u03df\13^\3_\3_\3_\3"+
		"_\3_\3_\3_\3_\3_\3_\3_\5_\u03ec\n_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3"+
		"_\3_\3_\3_\3_\3_\3_\3_\3_\3_\5_\u0403\n_\3_\5_\u0406\n_\3_\3_\3_\3_\3"+
		"_\3_\3_\3_\3_\3_\3_\3_\3_\5_\u0415\n_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3"+
		"_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3"+
		"_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3"+
		"_\3_\3_\3_\3_\3_\3_\3_\3_\3_\3_\7_\u045a\n_\f_\16_\u045d\13_\3`\5`\u0460"+
		"\n`\3a\3a\3b\7b\u0465\nb\fb\16b\u0468\13b\3b\3b\3c\3c\3c\5c\u046f\nc\3"+
		"d\3d\3d\3d\3d\3d\3d\3d\3d\5d\u047a\nd\3e\3e\3e\3e\3e\3e\3e\3e\3e\3e\3"+
		"e\5e\u0487\ne\3f\3f\3g\3g\3g\3g\3g\3h\3h\3h\3h\3h\3h\3h\3h\3h\3h\3h\5"+
		"h\u049b\nh\3i\3i\3i\3i\3j\3j\3j\7j\u04a4\nj\fj\16j\u04a7\13j\3k\3k\3k"+
		"\3k\3k\3k\5k\u04af\nk\3l\3l\3m\3m\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n"+
		"\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n\5n\u04d3\nn\3o"+
		"\3o\3o\3o\3o\3o\3o\3o\3o\3o\3o\5o\u04e0\no\3p\3p\3p\7p\u04e5\np\fp\16"+
		"p\u04e8\13p\3q\3q\3q\3q\5q\u04ee\nq\3r\3r\3s\3s\5s\u04f4\ns\3t\3t\3u\3"+
		"u\5u\u04fa\nu\3v\3v\3w\3w\3x\3x\3y\3y\3y\5y\u0505\ny\3z\3z\3{\3{\3|\3"+
		"|\3|\3|\5|\u050f\n|\3}\3}\3~\3~\3~\3~\3~\5~\u0518\n~\3\177\3\177\3\177"+
		"\3\177\5\177\u051e\n\177\3\u0080\3\u0080\3\u0080\3\u0080\3\u0080\3\u0080"+
		"\3\u0080\7\u0080\u0527\n\u0080\f\u0080\16\u0080\u052a\13\u0080\3\u0080"+
		"\2\5\u00a2\u00bc\u00fe\u0081\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \""+
		"$&(*,.\60\62\64\668:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084"+
		"\u0086\u0088\u008a\u008c\u008e\u0090\u0092\u0094\u0096\u0098\u009a\u009c"+
		"\u009e\u00a0\u00a2\u00a4\u00a6\u00a8\u00aa\u00ac\u00ae\u00b0\u00b2\u00b4"+
		"\u00b6\u00b8\u00ba\u00bc\u00be\u00c0\u00c2\u00c4\u00c6\u00c8\u00ca\u00cc"+
		"\u00ce\u00d0\u00d2\u00d4\u00d6\u00d8\u00da\u00dc\u00de\u00e0\u00e2\u00e4"+
		"\u00e6\u00e8\u00ea\u00ec\u00ee\u00f0\u00f2\u00f4\u00f6\u00f8\u00fa\u00fc"+
		"\u00fe\2\16\4\2ZZ__\3\2\31\33\3\2\25\26\3\2\34\36\3\2\37\"\3\2#&\3\2,"+
		"\66\4\2\678ii\3\29<\3\2\678\3\2=]\3\2`g\2\u0577\2\u0100\3\2\2\2\4\u0110"+
		"\3\2\2\2\6\u0115\3\2\2\2\b\u0119\3\2\2\2\n\u011d\3\2\2\2\f\u011f\3\2\2"+
		"\2\16\u0121\3\2\2\2\20\u0128\3\2\2\2\22\u012e\3\2\2\2\24\u0130\3\2\2\2"+
		"\26\u0134\3\2\2\2\30\u0136\3\2\2\2\32\u0145\3\2\2\2\34\u0147\3\2\2\2\36"+
		"\u014a\3\2\2\2 \u0159\3\2\2\2\"\u015d\3\2\2\2$\u016a\3\2\2\2&\u0183\3"+
		"\2\2\2(\u0185\3\2\2\2*\u018d\3\2\2\2,\u0196\3\2\2\2.\u019f\3\2\2\2\60"+
		"\u01a6\3\2\2\2\62\u01a8\3\2\2\2\64\u01ac\3\2\2\2\66\u01af\3\2\2\28\u01b2"+
		"\3\2\2\2:\u01b4\3\2\2\2<\u01b8\3\2\2\2>\u022f\3\2\2\2@\u0231\3\2\2\2B"+
		"\u0234\3\2\2\2D\u0237\3\2\2\2F\u023c\3\2\2\2H\u023e\3\2\2\2J\u0245\3\2"+
		"\2\2L\u024c\3\2\2\2N\u0253\3\2\2\2P\u0259\3\2\2\2R\u025f\3\2\2\2T\u026c"+
		"\3\2\2\2V\u0270\3\2\2\2X\u0276\3\2\2\2Z\u027b\3\2\2\2\\\u0281\3\2\2\2"+
		"^\u0283\3\2\2\2`\u0295\3\2\2\2b\u0297\3\2\2\2d\u029d\3\2\2\2f\u02a2\3"+
		"\2\2\2h\u02a4\3\2\2\2j\u02a7\3\2\2\2l\u02b9\3\2\2\2n\u02bb\3\2\2\2p\u02bf"+
		"\3\2\2\2r\u02c7\3\2\2\2t\u02c9\3\2\2\2v\u02d1\3\2\2\2x\u02d3\3\2\2\2z"+
		"\u02d5\3\2\2\2|\u02d8\3\2\2\2~\u02da\3\2\2\2\u0080\u02e1\3\2\2\2\u0082"+
		"\u02e9\3\2\2\2\u0084\u02fb\3\2\2\2\u0086\u0319\3\2\2\2\u0088\u031b\3\2"+
		"\2\2\u008a\u031d\3\2\2\2\u008c\u0326\3\2\2\2\u008e\u0330\3\2\2\2\u0090"+
		"\u033c\3\2\2\2\u0092\u033e\3\2\2\2\u0094\u0342\3\2\2\2\u0096\u0349\3\2"+
		"\2\2\u0098\u0351\3\2\2\2\u009a\u0354\3\2\2\2\u009c\u0357\3\2\2\2\u009e"+
		"\u035f\3\2\2\2\u00a0\u0372\3\2\2\2\u00a2\u037d\3\2\2\2\u00a4\u0391\3\2"+
		"\2\2\u00a6\u0395\3\2\2\2\u00a8\u03a1\3\2\2\2\u00aa\u03a3\3\2\2\2\u00ac"+
		"\u03b2\3\2\2\2\u00ae\u03b6\3\2\2\2\u00b0\u03bb\3\2\2\2\u00b2\u03bd\3\2"+
		"\2\2\u00b4\u03c1\3\2\2\2\u00b6\u03c4\3\2\2\2\u00b8\u03cc\3\2\2\2\u00ba"+
		"\u03d8\3\2\2\2\u00bc\u0414\3\2\2\2\u00be\u045f\3\2\2\2\u00c0\u0461\3\2"+
		"\2\2\u00c2\u0466\3\2\2\2\u00c4\u046e\3\2\2\2\u00c6\u0479\3\2\2\2\u00c8"+
		"\u0486\3\2\2\2\u00ca\u0488\3\2\2\2\u00cc\u048a\3\2\2\2\u00ce\u049a\3\2"+
		"\2\2\u00d0\u049c\3\2\2\2\u00d2\u04a0\3\2\2\2\u00d4\u04ae\3\2\2\2\u00d6"+
		"\u04b0\3\2\2\2\u00d8\u04b2\3\2\2\2\u00da\u04d2\3\2\2\2\u00dc\u04df\3\2"+
		"\2\2\u00de\u04e1\3\2\2\2\u00e0\u04e9\3\2\2\2\u00e2\u04ef\3\2\2\2\u00e4"+
		"\u04f3\3\2\2\2\u00e6\u04f5\3\2\2\2\u00e8\u04f9\3\2\2\2\u00ea\u04fb\3\2"+
		"\2\2\u00ec\u04fd\3\2\2\2\u00ee\u04ff\3\2\2\2\u00f0\u0504\3\2\2\2\u00f2"+
		"\u0506\3\2\2\2\u00f4\u0508\3\2\2\2\u00f6\u050e\3\2\2\2\u00f8\u0510\3\2"+
		"\2\2\u00fa\u0517\3\2\2\2\u00fc\u051d\3\2\2\2\u00fe\u051f\3\2\2\2\u0100"+
		"\u0101\5\u00c2b\2\u0101\3\3\2\2\2\u0102\u0111\5\f\7\2\u0103\u0111\5\34"+
		"\17\2\u0104\u0111\58\35\2\u0105\u0111\5:\36\2\u0106\u0111\5<\37\2\u0107"+
		"\u0111\5\n\6\2\u0108\u0111\5H%\2\u0109\u0111\5J&\2\u010a\u0111\5L\'\2"+
		"\u010b\u0111\5N(\2\u010c\u0111\5Z.\2\u010d\u0111\5^\60\2\u010e\u0111\5"+
		"`\61\2\u010f\u0111\5h\65\2\u0110\u0102\3\2\2\2\u0110\u0103\3\2\2\2\u0110"+
		"\u0104\3\2\2\2\u0110\u0105\3\2\2\2\u0110\u0106\3\2\2\2\u0110\u0107\3\2"+
		"\2\2\u0110\u0108\3\2\2\2\u0110\u0109\3\2\2\2\u0110\u010a\3\2\2\2\u0110"+
		"\u010b\3\2\2\2\u0110\u010c\3\2\2\2\u0110\u010d\3\2\2\2\u0110\u010e\3\2"+
		"\2\2\u0110\u010f\3\2\2\2\u0111\5\3\2\2\2\u0112\u0116\5\b\5\2\u0113\u0116"+
		"\5\u0092J\2\u0114\u0116\5\24\13\2\u0115\u0112\3\2\2\2\u0115\u0113\3\2"+
		"\2\2\u0115\u0114\3\2\2\2\u0116\7\3\2\2\2\u0117\u011a\5j\66\2\u0118\u011a"+
		"\5\u008cG\2\u0119\u0117\3\2\2\2\u0119\u0118\3\2\2\2\u011a\t\3\2\2\2\u011b"+
		"\u011e\5> \2\u011c\u011e\5P)\2\u011d\u011b\3\2\2\2\u011d\u011c\3\2\2\2"+
		"\u011e\13\3\2\2\2\u011f\u0120\5\16\b\2\u0120\r\3\2\2\2\u0121\u0123\7\t"+
		"\2\2\u0122\u0124\5\20\t\2\u0123\u0122\3\2\2\2\u0123\u0124\3\2\2\2\u0124"+
		"\u0125\3\2\2\2\u0125\u0126\7\n\2\2\u0126\17\3\2\2\2\u0127\u0129\5\22\n"+
		"\2\u0128\u0127\3\2\2\2\u0129\u012a\3\2\2\2\u012a\u0128\3\2\2\2\u012a\u012b"+
		"\3\2\2\2\u012b\21\3\2\2\2\u012c\u012f\5\4\3\2\u012d\u012f\5\6\4\2\u012e"+
		"\u012c\3\2\2\2\u012e\u012d\3\2\2\2\u012f\23\3\2\2\2\u0130\u0131\5\26\f"+
		"\2\u0131\u0132\5\30\r\2\u0132\u0133\5\u00f6|\2\u0133\25\3\2\2\2\u0134"+
		"\u0135\t\2\2\2\u0135\27\3\2\2\2\u0136\u013b\5\32\16\2\u0137\u0138\7\f"+
		"\2\2\u0138\u013a\5\32\16\2\u0139\u0137\3\2\2\2\u013a\u013d\3\2\2\2\u013b"+
		"\u0139\3\2\2\2\u013b\u013c\3\2\2\2\u013c\31\3\2\2\2\u013d\u013b\3\2\2"+
		"\2\u013e\u0140\5\u00ecw\2\u013f\u0141\5\66\34\2\u0140\u013f\3\2\2\2\u0140"+
		"\u0141\3\2\2\2\u0141\u0146\3\2\2\2\u0142\u0143\5\"\22\2\u0143\u0144\5"+
		"\66\34\2\u0144\u0146\3\2\2\2\u0145\u013e\3\2\2\2\u0145\u0142\3\2\2\2\u0146"+
		"\33\3\2\2\2\u0147\u0148\5@!\2\u0148\u0149\5\u00f6|\2\u0149\35\3\2\2\2"+
		"\u014a\u014f\5 \21\2\u014b\u014c\7\f\2\2\u014c\u014e\5 \21\2\u014d\u014b"+
		"\3\2\2\2\u014e\u0151\3\2\2\2\u014f\u014d\3\2\2\2\u014f\u0150\3\2\2\2\u0150"+
		"\37\3\2\2\2\u0151\u014f\3\2\2\2\u0152\u0154\5\u00ecw\2\u0153\u0155\5\66"+
		"\34\2\u0154\u0153\3\2\2\2\u0154\u0155\3\2\2\2\u0155\u015a\3\2\2\2\u0156"+
		"\u0157\5\"\22\2\u0157\u0158\5\66\34\2\u0158\u015a\3\2\2\2\u0159\u0152"+
		"\3\2\2\2\u0159\u0156\3\2\2\2\u015a!\3\2\2\2\u015b\u015e\5$\23\2\u015c"+
		"\u015e\5&\24\2\u015d\u015b\3\2\2\2\u015d\u015c\3\2\2\2\u015e#\3\2\2\2"+
		"\u015f\u0160\7\t\2\2\u0160\u016b\7\n\2\2\u0161\u0162\7\t\2\2\u0162\u0163"+
		"\5(\25\2\u0163\u0164\7\n\2\2\u0164\u016b\3\2\2\2\u0165\u0166\7\t\2\2\u0166"+
		"\u0167\5(\25\2\u0167\u0168\7\f\2\2\u0168\u0169\7\n\2\2\u0169\u016b\3\2"+
		"\2\2\u016a\u015f\3\2\2\2\u016a\u0161\3\2\2\2\u016a\u0165\3\2\2\2\u016b"+
		"%\3\2\2\2\u016c\u016e\7\5\2\2\u016d\u016f\5\u00a4S\2\u016e\u016d\3\2\2"+
		"\2\u016e\u016f\3\2\2\2\u016f\u0171\3\2\2\2\u0170\u0172\5\64\33\2\u0171"+
		"\u0170\3\2\2\2\u0171\u0172\3\2\2\2\u0172\u0173\3\2\2\2\u0173\u0184\7\6"+
		"\2\2\u0174\u0175\7\5\2\2\u0175\u0176\5*\26\2\u0176\u0177\7\6\2\2\u0177"+
		"\u0184\3\2\2\2\u0178\u0179\7\5\2\2\u0179\u017a\5*\26\2\u017a\u017c\7\f"+
		"\2\2\u017b\u017d\5\u00a4S\2\u017c\u017b\3\2\2\2\u017c\u017d\3\2\2\2\u017d"+
		"\u017f\3\2\2\2\u017e\u0180\5\64\33\2\u017f\u017e\3\2\2\2\u017f\u0180\3"+
		"\2\2\2\u0180\u0181\3\2\2\2\u0181\u0182\7\6\2\2\u0182\u0184\3\2\2\2\u0183"+
		"\u016c\3\2\2\2\u0183\u0174\3\2\2\2\u0183\u0178\3\2\2\2\u0184\'\3\2\2\2"+
		"\u0185\u018a\5.\30\2\u0186\u0187\7\f\2\2\u0187\u0189\5.\30\2\u0188\u0186"+
		"\3\2\2\2\u0189\u018c\3\2\2\2\u018a\u0188\3\2\2\2\u018a\u018b\3\2\2\2\u018b"+
		")\3\2\2\2\u018c\u018a\3\2\2\2\u018d\u0192\5,\27\2\u018e\u018f\7\f\2\2"+
		"\u018f\u0191\5,\27\2\u0190\u018e\3\2\2\2\u0191\u0194\3\2\2\2\u0192\u0190"+
		"\3\2\2\2\u0192\u0193\3\2\2\2\u0193+\3\2\2\2\u0194\u0192\3\2\2\2\u0195"+
		"\u0197\5\u00a4S\2\u0196\u0195\3\2\2\2\u0196\u0197\3\2\2\2\u0197\u0198"+
		"\3\2\2\2\u0198\u0199\5\60\31\2\u0199-\3\2\2\2\u019a\u01a0\5\62\32\2\u019b"+
		"\u019c\5\u00aeX\2\u019c\u019d\7\20\2\2\u019d\u019e\5\60\31\2\u019e\u01a0"+
		"\3\2\2\2\u019f\u019a\3\2\2\2\u019f\u019b\3\2\2\2\u01a0/\3\2\2\2\u01a1"+
		"\u01a7\5\62\32\2\u01a2\u01a4\5\"\22\2\u01a3\u01a5\5\66\34\2\u01a4\u01a3"+
		"\3\2\2\2\u01a4\u01a5\3\2\2\2\u01a5\u01a7\3\2\2\2\u01a6\u01a1\3\2\2\2\u01a6"+
		"\u01a2\3\2\2\2\u01a7\61\3\2\2\2\u01a8\u01aa\5\u00ecw\2\u01a9\u01ab\5\66"+
		"\34\2\u01aa\u01a9\3\2\2\2\u01aa\u01ab\3\2\2\2\u01ab\63\3\2\2\2\u01ac\u01ad"+
		"\7\21\2\2\u01ad\u01ae\5\u00ecw\2\u01ae\65\3\2\2\2\u01af\u01b0\7\16\2\2"+
		"\u01b0\u01b1\5\u00bc_\2\u01b1\67\3\2\2\2\u01b2\u01b3\7\13\2\2\u01b39\3"+
		"\2\2\2\u01b4\u01b5\6\36\2\2\u01b5\u01b6\5\u00ba^\2\u01b6\u01b7\5\u00f6"+
		"|\2\u01b7;\3\2\2\2\u01b8\u01b9\7R\2\2\u01b9\u01ba\7\7\2\2\u01ba\u01bb"+
		"\5\u00ba^\2\u01bb\u01bc\7\b\2\2\u01bc\u01bf\5\4\3\2\u01bd\u01be\7B\2\2"+
		"\u01be\u01c0\5\4\3\2\u01bf\u01bd\3\2\2\2\u01bf\u01c0\3\2\2\2\u01c0=\3"+
		"\2\2\2\u01c1\u01c2\7>\2\2\u01c2\u01c3\5\4\3\2\u01c3\u01c4\7L\2\2\u01c4"+
		"\u01c5\7\7\2\2\u01c5\u01c6\5\u00ba^\2\u01c6\u01c7\7\b\2\2\u01c7\u01c8"+
		"\5\u00f6|\2\u01c8\u0230\3\2\2\2\u01c9\u01ca\7L\2\2\u01ca\u01cb\7\7\2\2"+
		"\u01cb\u01cc\5\u00ba^\2\u01cc\u01cd\7\b\2\2\u01cd\u01ce\5\4\3\2\u01ce"+
		"\u0230\3\2\2\2\u01cf\u01d0\7J\2\2\u01d0\u01d1\7\7\2\2\u01d1\u01d3\6 \3"+
		"\2\u01d2\u01d4\5\u00ba^\2\u01d3\u01d2\3\2\2\2\u01d3\u01d4\3\2\2\2\u01d4"+
		"\u01d5\3\2\2\2\u01d5\u01d7\7\13\2\2\u01d6\u01d8\5\u00ba^\2\u01d7\u01d6"+
		"\3\2\2\2\u01d7\u01d8\3\2\2\2\u01d8\u01d9\3\2\2\2\u01d9\u01db\7\13\2\2"+
		"\u01da\u01dc\5\u00ba^\2\u01db\u01da\3\2\2\2\u01db\u01dc\3\2\2\2\u01dc"+
		"\u01dd\3\2\2\2\u01dd\u01de\7\b\2\2\u01de\u0230\5\4\3\2\u01df\u01e0\7J"+
		"\2\2\u01e0\u01e1\7\7\2\2\u01e1\u01e2\5@!\2\u01e2\u01e4\7\13\2\2\u01e3"+
		"\u01e5\5\u00ba^\2\u01e4\u01e3\3\2\2\2\u01e4\u01e5\3\2\2\2\u01e5\u01e6"+
		"\3\2\2\2\u01e6\u01e8\7\13\2\2\u01e7\u01e9\5\u00ba^\2\u01e8\u01e7\3\2\2"+
		"\2\u01e8\u01e9\3\2\2\2\u01e9\u01ea\3\2\2\2\u01ea\u01eb\7\b\2\2\u01eb\u01ec"+
		"\5\4\3\2\u01ec\u0230\3\2\2\2\u01ed\u01ee\7J\2\2\u01ee\u01ef\7\7\2\2\u01ef"+
		"\u01f1\5\24\13\2\u01f0\u01f2\5\u00ba^\2\u01f1\u01f0\3\2\2\2\u01f1\u01f2"+
		"\3\2\2\2\u01f2\u01f3\3\2\2\2\u01f3\u01f5\7\13\2\2\u01f4\u01f6\5\u00ba"+
		"^\2\u01f5\u01f4\3\2\2\2\u01f5\u01f6\3\2\2\2\u01f6\u01f7\3\2\2\2\u01f7"+
		"\u01f8\7\b\2\2\u01f8\u01f9\5\4\3\2\u01f9\u0230\3\2\2\2\u01fa\u01fb\7J"+
		"\2\2\u01fb\u01fc\7\7\2\2\u01fc\u01fd\6 \4\2\u01fd\u01fe\5\u00bc_\2\u01fe"+
		"\u01ff\7U\2\2\u01ff\u0200\5\u00ba^\2\u0200\u0201\7\b\2\2\u0201\u0202\5"+
		"\4\3\2\u0202\u0230\3\2\2\2\u0203\u0204\7J\2\2\u0204\u0205\7\7\2\2\u0205"+
		"\u0206\5B\"\2\u0206\u0207\7U\2\2\u0207\u0208\5\u00ba^\2\u0208\u0209\7"+
		"\b\2\2\u0209\u020a\5\4\3\2\u020a\u0230\3\2\2\2\u020b\u020c\7J\2\2\u020c"+
		"\u020d\7\7\2\2\u020d\u020e\5D#\2\u020e\u020f\7U\2\2\u020f\u0210\5\u00ba"+
		"^\2\u0210\u0211\7\b\2\2\u0211\u0212\5\4\3\2\u0212\u0230\3\2\2\2\u0213"+
		"\u0214\7J\2\2\u0214\u0215\7\7\2\2\u0215\u0216\6 \5\2\u0216\u0217\5\u00bc"+
		"_\2\u0217\u0218\6 \6\2\u0218\u0219\7h\2\2\u0219\u021a\5\u00bc_\2\u021a"+
		"\u021b\7\b\2\2\u021b\u021c\5\4\3\2\u021c\u0230\3\2\2\2\u021d\u021e\7J"+
		"\2\2\u021e\u021f\7\7\2\2\u021f\u0220\5B\"\2\u0220\u0221\6 \7\2\u0221\u0222"+
		"\7h\2\2\u0222\u0223\5\u00bc_\2\u0223\u0224\7\b\2\2\u0224\u0225\5\4\3\2"+
		"\u0225\u0230\3\2\2\2\u0226\u0227\7J\2\2\u0227\u0228\7\7\2\2\u0228\u0229"+
		"\5D#\2\u0229\u022a\6 \b\2\u022a\u022b\7h\2\2\u022b\u022c\5\u00bc_\2\u022c"+
		"\u022d\7\b\2\2\u022d\u022e\5\4\3\2\u022e\u0230\3\2\2\2\u022f\u01c1\3\2"+
		"\2\2\u022f\u01c9\3\2\2\2\u022f\u01cf\3\2\2\2\u022f\u01df\3\2\2\2\u022f"+
		"\u01ed\3\2\2\2\u022f\u01fa\3\2\2\2\u022f\u0203\3\2\2\2\u022f\u020b\3\2"+
		"\2\2\u022f\u0213\3\2\2\2\u022f\u021d\3\2\2\2\u022f\u0226\3\2\2\2\u0230"+
		"?\3\2\2\2\u0231\u0232\7D\2\2\u0232\u0233\5\36\20\2\u0233A\3\2\2\2\u0234"+
		"\u0235\7D\2\2\u0235\u0236\5F$\2\u0236C\3\2\2\2\u0237\u0238\5\26\f\2\u0238"+
		"\u0239\5F$\2\u0239E\3\2\2\2\u023a\u023d\5\u00ecw\2\u023b\u023d\5\"\22"+
		"\2\u023c\u023a\3\2\2\2\u023c\u023b\3\2\2\2\u023dG\3\2\2\2\u023e\u0241"+
		"\7I\2\2\u023f\u0240\6%\t\2\u0240\u0242\7h\2\2\u0241\u023f\3\2\2\2\u0241"+
		"\u0242\3\2\2\2\u0242\u0243\3\2\2\2\u0243\u0244\5\u00f6|\2\u0244I\3\2\2"+
		"\2\u0245\u0248\7=\2\2\u0246\u0247\6&\n\2\u0247\u0249\7h\2\2\u0248\u0246"+
		"\3\2\2\2\u0248\u0249\3\2\2\2\u0249\u024a\3\2\2\2\u024a\u024b\5\u00f6|"+
		"\2\u024bK\3\2\2\2\u024c\u024f\7G\2\2\u024d\u024e\6\'\13\2\u024e\u0250"+
		"\5\u00ba^\2\u024f\u024d\3\2\2\2\u024f\u0250\3\2\2\2\u0250\u0251\3\2\2"+
		"\2\u0251\u0252\5\u00f6|\2\u0252M\3\2\2\2\u0253\u0254\7P\2\2\u0254\u0255"+
		"\7\7\2\2\u0255\u0256\5\u00ba^\2\u0256\u0257\7\b\2\2\u0257\u0258\5\4\3"+
		"\2\u0258O\3\2\2\2\u0259\u025a\7K\2\2\u025a\u025b\7\7\2\2\u025b\u025c\5"+
		"\u00ba^\2\u025c\u025d\7\b\2\2\u025d\u025e\5R*\2\u025eQ\3\2\2\2\u025f\u0261"+
		"\7\t\2\2\u0260\u0262\5T+\2\u0261\u0260\3\2\2\2\u0261\u0262\3\2\2\2\u0262"+
		"\u0267\3\2\2\2\u0263\u0265\5X-\2\u0264\u0266\5T+\2\u0265\u0264\3\2\2\2"+
		"\u0265\u0266\3\2\2\2\u0266\u0268\3\2\2\2\u0267\u0263\3\2\2\2\u0267\u0268"+
		"\3\2\2\2\u0268\u0269\3\2\2\2\u0269\u026a\7\n\2\2\u026aS\3\2\2\2\u026b"+
		"\u026d\5V,\2\u026c\u026b\3\2\2\2\u026d\u026e\3\2\2\2\u026e\u026c\3\2\2"+
		"\2\u026e\u026f\3\2\2\2\u026fU\3\2\2\2\u0270\u0271\7A\2\2\u0271\u0272\5"+
		"\u00ba^\2\u0272\u0274\7\20\2\2\u0273\u0275\5\20\t\2\u0274\u0273\3\2\2"+
		"\2\u0274\u0275\3\2\2\2\u0275W\3\2\2\2\u0276\u0277\7Q\2\2\u0277\u0279\7"+
		"\20\2\2\u0278\u027a\5\20\t\2\u0279\u0278\3\2\2\2\u0279\u027a\3\2\2\2\u027a"+
		"Y\3\2\2\2\u027b\u027c\7h\2\2\u027c\u027d\7\20\2\2\u027d\u027e\5\\/\2\u027e"+
		"[\3\2\2\2\u027f\u0282\5\4\3\2\u0280\u0282\5j\66\2\u0281\u027f\3\2\2\2"+
		"\u0281\u0280\3\2\2\2\u0282]\3\2\2\2\u0283\u0284\7S\2\2\u0284\u0285\6\60"+
		"\f\2\u0285\u0286\5\u00ba^\2\u0286\u0287\5\u00f6|\2\u0287_\3\2\2\2\u0288"+
		"\u0289\7V\2\2\u0289\u028a\5\16\b\2\u028a\u028b\5b\62\2\u028b\u0296\3\2"+
		"\2\2\u028c\u028d\7V\2\2\u028d\u028e\5\16\b\2\u028e\u028f\5d\63\2\u028f"+
		"\u0296\3\2\2\2\u0290\u0291\7V\2\2\u0291\u0292\5\16\b\2\u0292\u0293\5b"+
		"\62\2\u0293\u0294\5d\63\2\u0294\u0296\3\2\2\2\u0295\u0288\3\2\2\2\u0295"+
		"\u028c\3\2\2\2\u0295\u0290\3\2\2\2\u0296a\3\2\2\2\u0297\u0298\7E\2\2\u0298"+
		"\u0299\7\7\2\2\u0299\u029a\5f\64\2\u029a\u029b\7\b\2\2\u029b\u029c\5\16"+
		"\b\2\u029cc\3\2\2\2\u029d\u029e\7F\2\2\u029e\u029f\5\16\b\2\u029fe\3\2"+
		"\2\2\u02a0\u02a3\5\u00ecw\2\u02a1\u02a3\5\"\22\2\u02a2\u02a0\3\2\2\2\u02a2"+
		"\u02a1\3\2\2\2\u02a3g\3\2\2\2\u02a4\u02a5\7M\2\2\u02a5\u02a6\5\u00f6|"+
		"\2\u02a6i\3\2\2\2\u02a7\u02a8\7N\2\2\u02a8\u02a9\5\u00ecw\2\u02a9\u02aa"+
		"\7\7\2\2\u02aa\u02ab\5p9\2\u02ab\u02ac\7\b\2\2\u02ac\u02ad\7\t\2\2\u02ad"+
		"\u02ae\5z>\2\u02ae\u02af\7\n\2\2\u02afk\3\2\2\2\u02b0\u02b1\7N\2\2\u02b1"+
		"\u02b2\7\7\2\2\u02b2\u02b3\5p9\2\u02b3\u02b4\7\b\2\2\u02b4\u02b5\7\t\2"+
		"\2\u02b5\u02b6\5z>\2\u02b6\u02b7\7\n\2\2\u02b7\u02ba\3\2\2\2\u02b8\u02ba"+
		"\5j\66\2\u02b9\u02b0\3\2\2\2\u02b9\u02b8\3\2\2\2\u02bam\3\2\2\2\u02bb"+
		"\u02bc\5p9\2\u02bco\3\2\2\2\u02bd\u02c0\3\2\2\2\u02be\u02c0\5r:\2\u02bf"+
		"\u02bd\3\2\2\2\u02bf\u02be\3\2\2\2\u02c0q\3\2\2\2\u02c1\u02c8\5v<\2\u02c2"+
		"\u02c5\5t;\2\u02c3\u02c4\7\f\2\2\u02c4\u02c6\5v<\2\u02c5\u02c3\3\2\2\2"+
		"\u02c5\u02c6\3\2\2\2\u02c6\u02c8\3\2\2\2\u02c7\u02c1\3\2\2\2\u02c7\u02c2"+
		"\3\2\2\2\u02c8s\3\2\2\2\u02c9\u02ce\5x=\2\u02ca\u02cb\7\f\2\2\u02cb\u02cd"+
		"\5x=\2\u02cc\u02ca\3\2\2\2\u02cd\u02d0\3\2\2\2\u02ce\u02cc\3\2\2\2\u02ce"+
		"\u02cf\3\2\2\2\u02cfu\3\2\2\2\u02d0\u02ce\3\2\2\2\u02d1\u02d2\5\64\33"+
		"\2\u02d2w\3\2\2\2\u02d3\u02d4\5\60\31\2\u02d4y\3\2\2\2\u02d5\u02d6\5|"+
		"?\2\u02d6{\3\2\2\2\u02d7\u02d9\5\20\t\2\u02d8\u02d7\3\2\2\2\u02d8\u02d9"+
		"\3\2\2\2\u02d9}\3\2\2\2\u02da\u02db\5\u0080A\2\u02db\u02dc\6@\r\2\u02dc"+
		"\u02dd\7\r\2\2\u02dd\u02de\5\u0082B\2\u02de\177\3\2\2\2\u02df\u02e2\5"+
		"\u00ecw\2\u02e0\u02e2\5\u0084C\2\u02e1\u02df\3\2\2\2\u02e1\u02e0\3\2\2"+
		"\2\u02e2\u0081\3\2\2\2\u02e3\u02e4\6B\16\2\u02e4\u02ea\5\u00bc_\2\u02e5"+
		"\u02e6\7\t\2\2\u02e6\u02e7\5z>\2\u02e7\u02e8\7\n\2\2\u02e8\u02ea\3\2\2"+
		"\2\u02e9\u02e3\3\2\2\2\u02e9\u02e5\3\2\2\2\u02ea\u0083\3\2\2\2\u02eb\u02ec"+
		"\7\7\2\2\u02ec\u02ed\5\u00ba^\2\u02ed\u02ee\7\b\2\2\u02ee\u02fc\3\2\2"+
		"\2\u02ef\u02f0\7\7\2\2\u02f0\u02fc\7\b\2\2\u02f1\u02f2\7\7\2\2\u02f2\u02f3"+
		"\5\64\33\2\u02f3\u02f4\7\b\2\2\u02f4\u02fc\3\2\2\2\u02f5\u02f6\7\7\2\2"+
		"\u02f6\u02f7\5\u00ba^\2\u02f7\u02f8\7\f\2\2\u02f8\u02f9\5\64\33\2\u02f9"+
		"\u02fa\7\b\2\2\u02fa\u02fc\3\2\2\2\u02fb\u02eb\3\2\2\2\u02fb\u02ef\3\2"+
		"\2\2\u02fb\u02f1\3\2\2\2\u02fb\u02f5\3\2\2\2\u02fc\u0085\3\2\2\2\u02fd"+
		"\u02fe\5\u00aeX\2\u02fe\u02ff\7\7\2\2\u02ff\u0300\5n8\2\u0300\u0301\7"+
		"\b\2\2\u0301\u0302\7\t\2\2\u0302\u0303\5z>\2\u0303\u0304\7\n\2\2\u0304"+
		"\u031a\3\2\2\2\u0305\u031a\5\u008aF\2\u0306\u0307\6D\17\2\u0307\u0308"+
		"\7h\2\2\u0308\u0309\5\u00aeX\2\u0309\u030a\7\7\2\2\u030a\u030b\7\b\2\2"+
		"\u030b\u030c\7\t\2\2\u030c\u030d\5z>\2\u030d\u030e\7\n\2\2\u030e\u031a"+
		"\3\2\2\2\u030f\u0310\6D\20\2\u0310\u0311\7h\2\2\u0311\u0312\5\u00aeX\2"+
		"\u0312\u0313\7\7\2\2\u0313\u0314\5\u0088E\2\u0314\u0315\7\b\2\2\u0315"+
		"\u0316\7\t\2\2\u0316\u0317\5z>\2\u0317\u0318\7\n\2\2\u0318\u031a\3\2\2"+
		"\2\u0319\u02fd\3\2\2\2\u0319\u0305\3\2\2\2\u0319\u0306\3\2\2\2\u0319\u030f"+
		"\3\2\2\2\u031a\u0087\3\2\2\2\u031b\u031c\5x=\2\u031c\u0089\3\2\2\2\u031d"+
		"\u031e\7\31\2\2\u031e\u031f\5\u00aeX\2\u031f\u0320\7\7\2\2\u0320\u0321"+
		"\5n8\2\u0321\u0322\7\b\2\2\u0322\u0323\7\t\2\2\u0323\u0324\5\u0090I\2"+
		"\u0324\u0325\7\n\2\2\u0325\u008b\3\2\2\2\u0326\u0327\7N\2\2\u0327\u0328"+
		"\7\31\2\2\u0328\u0329\5\u00ecw\2\u0329\u032a\7\7\2\2\u032a\u032b\5p9\2"+
		"\u032b\u032c\7\b\2\2\u032c\u032d\7\t\2\2\u032d\u032e\5\u0090I\2\u032e"+
		"\u032f\7\n\2\2\u032f\u008d\3\2\2\2\u0330\u0331\7N\2\2\u0331\u0333\7\31"+
		"\2\2\u0332\u0334\5\u00ecw\2\u0333\u0332\3\2\2\2\u0333\u0334\3\2\2\2\u0334"+
		"\u0335\3\2\2\2\u0335\u0336\7\7\2\2\u0336\u0337\5p9\2\u0337\u0338\7\b\2"+
		"\2\u0338\u0339\7\t\2\2\u0339\u033a\5\u0090I\2\u033a\u033b\7\n\2\2\u033b"+
		"\u008f\3\2\2\2\u033c\u033d\5z>\2\u033d\u0091\3\2\2\2\u033e\u033f\7X\2"+
		"\2\u033f\u0340\5\u00ecw\2\u0340\u0341\5\u0096L\2\u0341\u0093\3\2\2\2\u0342"+
		"\u0344\7X\2\2\u0343\u0345\5\u00ecw\2\u0344\u0343\3\2\2\2\u0344\u0345\3"+
		"\2\2\2\u0345\u0346\3\2\2\2\u0346\u0347\5\u0096L\2\u0347\u0095\3\2\2\2"+
		"\u0348\u034a\5\u0098M\2\u0349\u0348\3\2\2\2\u0349\u034a\3\2\2\2\u034a"+
		"\u034b\3\2\2\2\u034b\u034d\7\t\2\2\u034c\u034e\5\u009aN\2\u034d\u034c"+
		"\3\2\2\2\u034d\u034e\3\2\2\2\u034e\u034f\3\2\2\2\u034f\u0350\7\n\2\2\u0350"+
		"\u0097\3\2\2\2\u0351\u0352\7Y\2\2\u0352\u0353\5\u00bc_\2\u0353\u0099\3"+
		"\2\2\2\u0354\u0355\5\u009cO\2\u0355\u009b\3\2\2\2\u0356\u0358\5\u009e"+
		"P\2\u0357\u0356\3\2\2\2\u0358\u0359\3\2\2\2\u0359\u0357\3\2\2\2\u0359"+
		"\u035a\3\2\2\2\u035a\u009d\3\2\2\2\u035b\u0360\5\u0086D\2\u035c\u035d"+
		"\7^\2\2\u035d\u0360\5\u0086D\2\u035e\u0360\7\13\2\2\u035f\u035b\3\2\2"+
		"\2\u035f\u035c\3\2\2\2\u035f\u035e\3\2\2\2\u0360\u009f\3\2\2\2\u0361\u0363"+
		"\7\5\2\2\u0362\u0364\5\u00a4S\2\u0363\u0362\3\2\2\2\u0363\u0364\3\2\2"+
		"\2\u0364\u0365\3\2\2\2\u0365\u0373\7\6\2\2\u0366\u0367\7\5\2\2\u0367\u0368"+
		"\5\u00a2R\2\u0368\u0369\7\6\2\2\u0369\u0373\3\2\2\2\u036a\u036b\7\5\2"+
		"\2\u036b\u036c\5\u00a2R\2\u036c\u036e\7\f\2\2\u036d\u036f\5\u00a4S\2\u036e"+
		"\u036d\3\2\2\2\u036e\u036f\3\2\2\2\u036f\u0370\3\2\2\2\u0370\u0371\7\6"+
		"\2\2\u0371\u0373\3\2\2\2\u0372\u0361\3\2\2\2\u0372\u0366\3\2\2\2\u0372"+
		"\u036a\3\2\2\2\u0373\u00a1\3\2\2\2\u0374\u0376\bR\1\2\u0375\u0377\5\u00a4"+
		"S\2\u0376\u0375\3\2\2\2\u0376\u0377\3\2\2\2\u0377\u0378\3\2\2\2\u0378"+
		"\u037e\5\u00bc_\2\u0379\u037b\5\u00a4S\2\u037a\u0379\3\2\2\2\u037a\u037b"+
		"\3\2\2\2\u037b\u037c\3\2\2\2\u037c\u037e\5\u00a6T\2\u037d\u0374\3\2\2"+
		"\2\u037d\u037a\3\2\2\2\u037e\u038d\3\2\2\2\u037f\u0380\f\4\2\2\u0380\u0382"+
		"\7\f\2\2\u0381\u0383\5\u00a4S\2\u0382\u0381\3\2\2\2\u0382\u0383\3\2\2"+
		"\2\u0383\u0384\3\2\2\2\u0384\u038c\5\u00bc_\2\u0385\u0386\f\3\2\2\u0386"+
		"\u0388\7\f\2\2\u0387\u0389\5\u00a4S\2\u0388\u0387\3\2\2\2\u0388\u0389"+
		"\3\2\2\2\u0389\u038a\3\2\2\2\u038a\u038c\5\u00a6T\2\u038b\u037f\3\2\2"+
		"\2\u038b\u0385\3\2\2\2\u038c\u038f\3\2\2\2\u038d\u038b\3\2\2\2\u038d\u038e"+
		"\3\2\2\2\u038e\u00a3\3\2\2\2\u038f\u038d\3\2\2\2\u0390\u0392\7\f\2\2\u0391"+
		"\u0390\3\2\2\2\u0392\u0393\3\2\2\2\u0393\u0391\3\2\2\2\u0393\u0394\3\2"+
		"\2\2\u0394\u00a5\3\2\2\2\u0395\u0396\7\21\2\2\u0396\u0397\5\u00bc_\2\u0397"+
		"\u00a7\3\2\2\2\u0398\u0399\7\t\2\2\u0399\u03a2\7\n\2\2\u039a\u039b\7\t"+
		"\2\2\u039b\u039d\5\u00aaV\2\u039c\u039e\7\f\2\2\u039d\u039c\3\2\2\2\u039d"+
		"\u039e\3\2\2\2\u039e\u039f\3\2\2\2\u039f\u03a0\7\n\2\2\u03a0\u03a2\3\2"+
		"\2\2\u03a1\u0398\3\2\2\2\u03a1\u039a\3\2\2\2\u03a2\u00a9\3\2\2\2\u03a3"+
		"\u03a8\5\u00acW\2\u03a4\u03a5\7\f\2\2\u03a5\u03a7\5\u00acW\2\u03a6\u03a4"+
		"\3\2\2\2\u03a7\u03aa\3\2\2\2\u03a8\u03a6\3\2\2\2\u03a8\u03a9\3\2\2\2\u03a9"+
		"\u00ab\3\2\2\2\u03aa\u03a8\3\2\2\2\u03ab\u03b3\5\u00eav\2\u03ac\u03b3"+
		"\5\u00b4[\2\u03ad\u03ae\5\u00aeX\2\u03ae\u03af\7\20\2\2\u03af\u03b0\5"+
		"\u00bc_\2\u03b0\u03b3\3\2\2\2\u03b1\u03b3\5\u0086D\2\u03b2\u03ab\3\2\2"+
		"\2\u03b2\u03ac\3\2\2\2\u03b2\u03ad\3\2\2\2\u03b2\u03b1\3\2\2\2\u03b3\u00ad"+
		"\3\2\2\2\u03b4\u03b7\5\u00b0Y\2\u03b5\u03b7\5\u00b2Z\2\u03b6\u03b4\3\2"+
		"\2\2\u03b6\u03b5\3\2\2\2\u03b7\u00af\3\2\2\2\u03b8\u03bc\5\u00e8u\2\u03b9"+
		"\u03bc\7i\2\2\u03ba\u03bc\5\u00e6t\2\u03bb\u03b8\3\2\2\2\u03bb\u03b9\3"+
		"\2\2\2\u03bb\u03ba\3\2\2\2\u03bc\u00b1\3\2\2\2\u03bd\u03be\7\5\2\2\u03be"+
		"\u03bf\5\u00bc_\2\u03bf\u03c0\7\6\2\2\u03c0\u00b3\3\2\2\2\u03c1\u03c2"+
		"\5\u00eav\2\u03c2\u03c3\5\66\34\2\u03c3\u00b5\3\2\2\2\u03c4\u03c6\7\7"+
		"\2\2\u03c5\u03c7\5\u00b8]\2\u03c6\u03c5\3\2\2\2\u03c6\u03c7\3\2\2\2\u03c7"+
		"\u03c8\3\2\2\2\u03c8\u03c9\7\b\2\2\u03c9\u00b7\3\2\2\2\u03ca\u03cd\5\u00a6"+
		"T\2\u03cb\u03cd\5\u00bc_\2\u03cc\u03ca\3\2\2\2\u03cc\u03cb\3\2\2\2\u03cd"+
		"\u03d5\3\2\2\2\u03ce\u03d1\7\f\2\2\u03cf\u03d2\5\u00a6T\2\u03d0\u03d2"+
		"\5\u00bc_\2\u03d1\u03cf\3\2\2\2\u03d1\u03d0\3\2\2\2\u03d2\u03d4\3\2\2"+
		"\2\u03d3\u03ce\3\2\2\2\u03d4\u03d7\3\2\2\2\u03d5\u03d3\3\2\2\2\u03d5\u03d6"+
		"\3\2\2\2\u03d6\u00b9\3\2\2\2\u03d7\u03d5\3\2\2\2\u03d8\u03dd\5\u00bc_"+
		"\2\u03d9\u03da\7\f\2\2\u03da\u03dc\5\u00bc_\2\u03db\u03d9\3\2\2\2\u03dc"+
		"\u03df\3\2\2\2\u03dd\u03db\3\2\2\2\u03dd\u03de\3\2\2\2\u03de\u00bb\3\2"+
		"\2\2\u03df\u03dd\3\2\2\2\u03e0\u03e1\b_\1\2\u03e1\u0415\5l\67\2\u03e2"+
		"\u0415\5\u0094K\2\u03e3\u0415\5\u008eH\2\u03e4\u03e5\7C\2\2\u03e5\u03e6"+
		"\7\22\2\2\u03e6\u03e7\6_\23\2\u03e7\u0415\7h\2\2\u03e8\u03e9\7C\2\2\u03e9"+
		"\u03eb\5\u00bc_\2\u03ea\u03ec\5\u00b6\\\2\u03eb\u03ea\3\2\2\2\u03eb\u03ec"+
		"\3\2\2\2\u03ec\u0415\3\2\2\2\u03ed\u03ee\7T\2\2\u03ee\u0415\5\u00bc_%"+
		"\u03ef\u03f0\7H\2\2\u03f0\u0415\5\u00bc_$\u03f1\u03f2\7@\2\2\u03f2\u0415"+
		"\5\u00bc_#\u03f3\u03f4\7\23\2\2\u03f4\u0415\5\u00bc_\"\u03f5\u03f6\7\24"+
		"\2\2\u03f6\u0415\5\u00bc_!\u03f7\u03f8\7\25\2\2\u03f8\u0415\5\u00bc_ "+
		"\u03f9\u03fa\7\26\2\2\u03fa\u0415\5\u00bc_\37\u03fb\u03fc\7\27\2\2\u03fc"+
		"\u0415\5\u00bc_\36\u03fd\u03fe\7\30\2\2\u03fe\u0415\5\u00bc_\35\u03ff"+
		"\u0405\7\\\2\2\u0400\u0402\6_\24\2\u0401\u0403\7\31\2\2\u0402\u0401\3"+
		"\2\2\2\u0402\u0403\3\2\2\2\u0403\u0404\3\2\2\2\u0404\u0406\5\u00bc_\2"+
		"\u0405\u0400\3\2\2\2\u0405\u0406\3\2\2\2\u0406\u0415\3\2\2\2\u0407\u0415"+
		"\5~@\2\u0408\u0415\7O\2\2\u0409\u0415\7[\2\2\u040a\u0415\7h\2\2\u040b"+
		"\u0415\5\u00e4s\2\u040c\u0415\5\u00a0Q\2\u040d\u0415\5\u00a8U\2\u040e"+
		"\u0415\7\3\2\2\u040f\u0415\5\u00fa~\2\u0410\u0411\7\7\2\2\u0411\u0412"+
		"\5\u00ba^\2\u0412\u0413\7\b\2\2\u0413\u0415\3\2\2\2\u0414\u03e0\3\2\2"+
		"\2\u0414\u03e2\3\2\2\2\u0414\u03e3\3\2\2\2\u0414\u03e4\3\2\2\2\u0414\u03e8"+
		"\3\2\2\2\u0414\u03ed\3\2\2\2\u0414\u03ef\3\2\2\2\u0414\u03f1\3\2\2\2\u0414"+
		"\u03f3\3\2\2\2\u0414\u03f5\3\2\2\2\u0414\u03f7\3\2\2\2\u0414\u03f9\3\2"+
		"\2\2\u0414\u03fb\3\2\2\2\u0414\u03fd\3\2\2\2\u0414\u03ff\3\2\2\2\u0414"+
		"\u0407\3\2\2\2\u0414\u0408\3\2\2\2\u0414\u0409\3\2\2\2\u0414\u040a\3\2"+
		"\2\2\u0414\u040b\3\2\2\2\u0414\u040c\3\2\2\2\u0414\u040d\3\2\2\2\u0414"+
		"\u040e\3\2\2\2\u0414\u040f\3\2\2\2\u0414\u0410\3\2\2\2\u0415\u045b\3\2"+
		"\2\2\u0416\u0417\f\34\2\2\u0417\u0418\t\3\2\2\u0418\u045a\5\u00bc_\35"+
		"\u0419\u041a\f\33\2\2\u041a\u041b\t\4\2\2\u041b\u045a\5\u00bc_\34\u041c"+
		"\u041d\f\32\2\2\u041d\u041e\t\5\2\2\u041e\u045a\5\u00bc_\33\u041f\u0420"+
		"\f\31\2\2\u0420\u0421\t\6\2\2\u0421\u045a\5\u00bc_\32\u0422\u0423\f\30"+
		"\2\2\u0423\u0424\7?\2\2\u0424\u045a\5\u00bc_\31\u0425\u0426\f\27\2\2\u0426"+
		"\u0427\7U\2\2\u0427\u045a\5\u00bc_\30\u0428\u0429\f\26\2\2\u0429\u042a"+
		"\t\7\2\2\u042a\u045a\5\u00bc_\27\u042b\u042c\f\25\2\2\u042c\u042d\7\'"+
		"\2\2\u042d\u045a\5\u00bc_\26\u042e\u042f\f\24\2\2\u042f\u0430\7(\2\2\u0430"+
		"\u045a\5\u00bc_\25\u0431\u0432\f\23\2\2\u0432\u0433\7)\2\2\u0433\u045a"+
		"\5\u00bc_\24\u0434\u0435\f\22\2\2\u0435\u0436\7*\2\2\u0436\u045a\5\u00bc"+
		"_\23\u0437\u0438\f\21\2\2\u0438\u0439\7+\2\2\u0439\u045a\5\u00bc_\22\u043a"+
		"\u043b\f\20\2\2\u043b\u043c\7\17\2\2\u043c\u043d\5\u00bc_\2\u043d\u043e"+
		"\7\20\2\2\u043e\u043f\5\u00bc_\21\u043f\u045a\3\2\2\2\u0440\u0441\f\r"+
		"\2\2\u0441\u0442\7\16\2\2\u0442\u045a\5\u00bc_\16\u0443\u0444\f\f\2\2"+
		"\u0444\u0445\5\u00e2r\2\u0445\u0446\5\u00bc_\r\u0446\u045a\3\2\2\2\u0447"+
		"\u0448\f+\2\2\u0448\u0449\7\5\2\2\u0449\u044a\5\u00ba^\2\u044a\u044b\7"+
		"\6\2\2\u044b\u045a\3\2\2\2\u044c\u044d\f*\2\2\u044d\u044e\7\22\2\2\u044e"+
		"\u045a\5\u00e8u\2\u044f\u0450\f)\2\2\u0450\u045a\5\u00fa~\2\u0451\u0452"+
		"\f(\2\2\u0452\u045a\5\u00b6\\\2\u0453\u0454\f\'\2\2\u0454\u0455\6_)\2"+
		"\u0455\u045a\7\23\2\2\u0456\u0457\f&\2\2\u0457\u0458\6_+\2\u0458\u045a"+
		"\7\24\2\2\u0459\u0416\3\2\2\2\u0459\u0419\3\2\2\2\u0459\u041c\3\2\2\2"+
		"\u0459\u041f\3\2\2\2\u0459\u0422\3\2\2\2\u0459\u0425\3\2\2\2\u0459\u0428"+
		"\3\2\2\2\u0459\u042b\3\2\2\2\u0459\u042e\3\2\2\2\u0459\u0431\3\2\2\2\u0459"+
		"\u0434\3\2\2\2\u0459\u0437\3\2\2\2\u0459\u043a\3\2\2\2\u0459\u0440\3\2"+
		"\2\2\u0459\u0443\3\2\2\2\u0459\u0447\3\2\2\2\u0459\u044c\3\2\2\2\u0459"+
		"\u044f\3\2\2\2\u0459\u0451\3\2\2\2\u0459\u0453\3\2\2\2\u0459\u0456\3\2"+
		"\2\2\u045a\u045d\3\2\2\2\u045b\u0459\3\2\2\2\u045b\u045c\3\2\2\2\u045c"+
		"\u00bd\3\2\2\2\u045d\u045b\3\2\2\2\u045e\u0460\5\u00c0a\2\u045f\u045e"+
		"\3\2\2\2\u045f\u0460\3\2\2\2\u0460\u00bf\3\2\2\2\u0461\u0462\5\20\t\2"+
		"\u0462\u00c1\3\2\2\2\u0463\u0465\5\u00c4c\2\u0464\u0463\3\2\2\2\u0465"+
		"\u0468\3\2\2\2\u0466\u0464\3\2\2\2\u0466\u0467\3\2\2\2\u0467\u0469\3\2"+
		"\2\2\u0468\u0466\3\2\2\2\u0469\u046a\7\2\2\3\u046a\u00c3\3\2\2\2\u046b"+
		"\u046f\5\u00c6d\2\u046c\u046f\5\u00dan\2\u046d\u046f\5\22\n\2\u046e\u046b"+
		"\3\2\2\2\u046e\u046c\3\2\2\2\u046e\u046d\3\2\2\2\u046f\u00c5\3\2\2\2\u0470"+
		"\u0471\7]\2\2\u0471\u0472\5\u00c8e\2\u0472\u0473\5\u00d0i\2\u0473\u0474"+
		"\5\u00f6|\2\u0474\u047a\3\2\2\2\u0475\u0476\7]\2\2\u0476\u0477\5\u00d6"+
		"l\2\u0477\u0478\5\u00f6|\2\u0478\u047a\3\2\2\2\u0479\u0470\3\2\2\2\u0479"+
		"\u0475\3\2\2\2\u047a\u00c7\3\2\2\2\u047b\u0487\5\u00caf\2\u047c\u0487"+
		"\5\u00ccg\2\u047d\u0487\5\u00ceh\2\u047e\u047f\5\u00caf\2\u047f\u0480"+
		"\7\f\2\2\u0480\u0481\5\u00ccg\2\u0481\u0487\3\2\2\2\u0482\u0483\5\u00ca"+
		"f\2\u0483\u0484\7\f\2\2\u0484\u0485\5\u00ceh\2\u0485\u0487\3\2\2\2\u0486"+
		"\u047b\3\2\2\2\u0486\u047c\3\2\2\2\u0486\u047d\3\2\2\2\u0486\u047e\3\2"+
		"\2\2\u0486\u0482\3\2\2\2\u0487\u00c9\3\2\2\2\u0488\u0489\5\u00d8m\2\u0489"+
		"\u00cb\3\2\2\2\u048a\u048b\7\31\2\2\u048b\u048c\6g,\2\u048c\u048d\7h\2"+
		"\2\u048d\u048e\5\u00d8m\2\u048e\u00cd\3\2\2\2\u048f\u0490\7\t\2\2\u0490"+
		"\u049b\7\n\2\2\u0491\u0492\7\t\2\2\u0492\u0493\5\u00d2j\2\u0493\u0494"+
		"\7\n\2\2\u0494\u049b\3\2\2\2\u0495\u0496\7\t\2\2\u0496\u0497\5\u00d2j"+
		"\2\u0497\u0498\7\f\2\2\u0498\u0499\7\n\2\2\u0499\u049b\3\2\2\2\u049a\u048f"+
		"\3\2\2\2\u049a\u0491\3\2\2\2\u049a\u0495\3\2\2\2\u049b\u00cf\3\2\2\2\u049c"+
		"\u049d\6i-\2\u049d\u049e\7h\2\2\u049e\u049f\5\u00d6l\2\u049f\u00d1\3\2"+
		"\2\2\u04a0\u04a5\5\u00d4k\2\u04a1\u04a2\7\f\2\2\u04a2\u04a4\5\u00d4k\2"+
		"\u04a3\u04a1\3\2\2\2\u04a4\u04a7\3\2\2\2\u04a5\u04a3\3\2\2\2\u04a5\u04a6"+
		"\3\2\2\2\u04a6\u00d3\3\2\2\2\u04a7\u04a5\3\2\2\2\u04a8\u04af\5\u00d8m"+
		"\2\u04a9\u04aa\5\u00e8u\2\u04aa\u04ab\6k.\2\u04ab\u04ac\7h\2\2\u04ac\u04ad"+
		"\5\u00d8m\2\u04ad\u04af\3\2\2\2\u04ae\u04a8\3\2\2\2\u04ae\u04a9\3\2\2"+
		"\2\u04af\u00d5\3\2\2\2\u04b0\u04b1\7i\2\2\u04b1\u00d7\3\2\2\2\u04b2\u04b3"+
		"\5\u00ecw\2\u04b3\u00d9\3\2\2\2\u04b4\u04b5\7W\2\2\u04b5\u04b6\7\31\2"+
		"\2\u04b6\u04b7\5\u00d0i\2\u04b7\u04b8\5\u00f6|\2\u04b8\u04d3\3\2\2\2\u04b9"+
		"\u04ba\7W\2\2\u04ba\u04bb\5\u00dco\2\u04bb\u04bc\5\u00d0i\2\u04bc\u04bd"+
		"\5\u00f6|\2\u04bd\u04d3\3\2\2\2\u04be\u04bf\7W\2\2\u04bf\u04c0\5\u00dc"+
		"o\2\u04c0\u04c1\5\u00f6|\2\u04c1\u04d3\3\2\2\2\u04c2\u04c3\7W\2\2\u04c3"+
		"\u04d3\5\34\17\2\u04c4\u04c5\7W\2\2\u04c5\u04d3\5\6\4\2\u04c6\u04c7\7"+
		"W\2\2\u04c7\u04c8\7Q\2\2\u04c8\u04d3\5\b\5\2\u04c9\u04ca\7W\2\2\u04ca"+
		"\u04cb\7Q\2\2\u04cb\u04d3\5\u0092J\2\u04cc\u04cd\7W\2\2\u04cd\u04ce\7"+
		"Q\2\2\u04ce\u04cf\6n/\2\u04cf\u04d0\5\u00bc_\2\u04d0\u04d1\5\u00f6|\2"+
		"\u04d1\u04d3\3\2\2\2\u04d2\u04b4\3\2\2\2\u04d2\u04b9\3\2\2\2\u04d2\u04be"+
		"\3\2\2\2\u04d2\u04c2\3\2\2\2\u04d2\u04c4\3\2\2\2\u04d2\u04c6\3\2\2\2\u04d2"+
		"\u04c9\3\2\2\2\u04d2\u04cc\3\2\2\2\u04d3\u00db\3\2\2\2\u04d4\u04d5\7\t"+
		"\2\2\u04d5\u04e0\7\n\2\2\u04d6\u04d7\7\t\2\2\u04d7\u04d8\5\u00dep\2\u04d8"+
		"\u04d9\7\n\2\2\u04d9\u04e0\3\2\2\2\u04da\u04db\7\t\2\2\u04db\u04dc\5\u00de"+
		"p\2\u04dc\u04dd\7\f\2\2\u04dd\u04de\7\n\2\2\u04de\u04e0\3\2\2\2\u04df"+
		"\u04d4\3\2\2\2\u04df\u04d6\3\2\2\2\u04df\u04da\3\2\2\2\u04e0\u00dd\3\2"+
		"\2\2\u04e1\u04e6\5\u00e0q\2\u04e2\u04e3\7\f\2\2\u04e3\u04e5\5\u00e0q\2"+
		"\u04e4\u04e2\3\2\2\2\u04e5\u04e8\3\2\2\2\u04e6\u04e4\3\2\2\2\u04e6\u04e7"+
		"\3\2\2\2\u04e7\u00df\3\2\2\2\u04e8\u04e6\3\2\2\2\u04e9\u04ed\5\u00e8u"+
		"\2\u04ea\u04eb\6q\60\2\u04eb\u04ec\7h\2\2\u04ec\u04ee\5\u00e8u\2\u04ed"+
		"\u04ea\3\2\2\2\u04ed\u04ee\3\2\2\2\u04ee\u00e1\3\2\2\2\u04ef\u04f0\t\b"+
		"\2\2\u04f0\u00e3\3\2\2\2\u04f1\u04f4\t\t\2\2\u04f2\u04f4\5\u00e6t\2\u04f3"+
		"\u04f1\3\2\2\2\u04f3\u04f2\3\2\2\2\u04f4\u00e5\3\2\2\2\u04f5\u04f6\t\n"+
		"\2\2\u04f6\u00e7\3\2\2\2\u04f7\u04fa\7h\2\2\u04f8\u04fa\5\u00f0y\2\u04f9"+
		"\u04f7\3\2\2\2\u04f9\u04f8\3\2\2\2\u04fa\u00e9\3\2\2\2\u04fb\u04fc\7h"+
		"\2\2\u04fc\u00eb\3\2\2\2\u04fd\u04fe\7h\2\2\u04fe\u00ed\3\2\2\2\u04ff"+
		"\u0500\7h\2\2\u0500\u00ef\3\2\2\2\u0501\u0505\5\u00f2z\2\u0502\u0505\5"+
		"\u00f4{\2\u0503\u0505\t\13\2\2\u0504\u0501\3\2\2\2\u0504\u0502\3\2\2\2"+
		"\u0504\u0503\3\2\2\2\u0505\u00f1\3\2\2\2\u0506\u0507\t\f\2\2\u0507\u00f3"+
		"\3\2\2\2\u0508\u0509\t\r\2\2\u0509\u00f5\3\2\2\2\u050a\u050f\7\13\2\2"+
		"\u050b\u050f\7\2\2\3\u050c\u050f\6|\61\2\u050d\u050f\6|\62\2\u050e\u050a"+
		"\3\2\2\2\u050e\u050b\3\2\2\2\u050e\u050c\3\2\2\2\u050e\u050d\3\2\2\2\u050f"+
		"\u00f7\3\2\2\2\u0510\u0511\7\2\2\3\u0511\u00f9\3\2\2\2\u0512\u0518\7n"+
		"\2\2\u0513\u0514\7o\2\2\u0514\u0515\5\u00ba^\2\u0515\u0516\5\u00fc\177"+
		"\2\u0516\u0518\3\2\2\2\u0517\u0512\3\2\2\2\u0517\u0513\3\2\2\2\u0518\u00fb"+
		"\3\2\2\2\u0519\u051e\7q\2\2\u051a\u051b\5\u00fe\u0080\2\u051b\u051c\7"+
		"q\2\2\u051c\u051e\3\2\2\2\u051d\u0519\3\2\2\2\u051d\u051a\3\2\2\2\u051e"+
		"\u00fd\3\2\2\2\u051f\u0520\b\u0080\1\2\u0520\u0521\7p\2\2\u0521\u0522"+
		"\5\u00ba^\2\u0522\u0528\3\2\2\2\u0523\u0524\f\3\2\2\u0524\u0525\7p\2\2"+
		"\u0525\u0527\5\u00ba^\2\u0526\u0523\3\2\2\2\u0527\u052a\3\2\2\2\u0528"+
		"\u0526\3\2\2\2\u0528\u0529\3\2\2\2\u0529\u00ff\3\2\2\2\u052a\u0528\3\2"+
		"\2\2r\u0110\u0115\u0119\u011d\u0123\u012a\u012e\u013b\u0140\u0145\u014f"+
		"\u0154\u0159\u015d\u016a\u016e\u0171\u017c\u017f\u0183\u018a\u0192\u0196"+
		"\u019f\u01a4\u01a6\u01aa\u01bf\u01d3\u01d7\u01db\u01e4\u01e8\u01f1\u01f5"+
		"\u022f\u023c\u0241\u0248\u024f\u0261\u0265\u0267\u026e\u0274\u0279\u0281"+
		"\u0295\u02a2\u02b9\u02bf\u02c5\u02c7\u02ce\u02d8\u02e1\u02e9\u02fb\u0319"+
		"\u0333\u0344\u0349\u034d\u0359\u035f\u0363\u036e\u0372\u0376\u037a\u037d"+
		"\u0382\u0388\u038b\u038d\u0393\u039d\u03a1\u03a8\u03b2\u03b6\u03bb\u03c6"+
		"\u03cc\u03d1\u03d5\u03dd\u03eb\u0402\u0405\u0414\u0459\u045b\u045f\u0466"+
		"\u046e\u0479\u0486\u049a\u04a5\u04ae\u04d2\u04df\u04e6\u04ed\u04f3\u04f9"+
		"\u0504\u050e\u0517\u051d\u0528";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}