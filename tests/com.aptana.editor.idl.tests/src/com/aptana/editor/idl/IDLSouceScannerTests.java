package com.aptana.editor.idl;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.idl.parsing.lexer.IDLTokenType;

public class IDLSouceScannerTests extends TestCase
{
	private IDLSourceScanner _scanner;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		this._scanner = new IDLSourceScanner()
		{
			/*
			 * (non-Javadoc)
			 * @see com.aptana.editor.dtd.DTDSourceScanner#createToken(com.aptana.editor.dtd.parsing.lexer.DTDTokenType)
			 */
			@Override
			protected IToken createToken(IDLTokenType type)
			{
				// TODO Auto-generated method stub
				return new Token(type);
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		this._scanner = null;

		super.tearDown();
	}

	/**
	 * typeTests
	 * 
	 * @param source
	 * @param types
	 */
	public void typeTests(String source, IDLTokenType... types)
	{
		IDocument document = new Document(source);

		this._scanner.setRange(document, 0, source.length());

		for (IDLTokenType type : types)
		{
			IToken token = this._scanner.nextToken();
			Object data = token.getData();

			assertEquals(type, data);
		}
	}

	public void testString()
	{
		String source = "\"This is a string\""; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.STRING);
	}

	public void testDocumentationComment()
	{
		String source = "/** this is a documentation comment */"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.DOC_COMMENT);
	}

	public void testMultilineComment()
	{
		String source = "/* this is a multi-line comment */"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.MULTILINE_COMMENT);
	}

	public void testDoubleColon()
	{
		String source = "::"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.DOUBLE_COLON);
	}

	public void testEllipsis()
	{
		String source = "..."; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.ELLIPSIS);
	}

	public void testAny()
	{
		String source = "any"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.ANY);
	}

	public void testAttribute()
	{
		String source = "attribute"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.ATTRIBUTE);
	}

	public void testBoolean()
	{
		String source = "boolean"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.BOOLEAN);
	}

	public void testCaller()
	{
		String source = "caller"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.CALLER);
	}

	public void testConst()
	{
		String source = "const"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.CONST);
	}

	public void testCreator()
	{
		String source = "creator"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.CREATOR);
	}

	public void testDeleter()
	{
		String source = "deleter"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.DELETER);
	}

	public void testDOMString()
	{
		String source = "DOMString"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.DOMSTRING);
	}

	public void testDouble()
	{
		String source = "double"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.DOUBLE);
	}

	public void testException()
	{
		String source = "exception"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.EXCEPTION);
	}

	public void testFalse()
	{
		String source = "false"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.FALSE);
	}

	public void testFloat()
	{
		String source = "float"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.FLOAT);
	}

	public void testGetRaises()
	{
		String source = "getraises"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.GETRAISES);
	}

	public void testGetter()
	{
		String source = "getter"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.GETTER);
	}

	public void testImplements()
	{
		String source = "implements"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.IMPLEMENTS);
	}

	public void testIn()
	{
		String source = "in"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.IN);
	}

	public void testInterface()
	{
		String source = "interface"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.INTERFACE);
	}

	public void testLong()
	{
		String source = "long"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.LONG);
	}

	public void testModule()
	{
		String source = "module"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.MODULE);
	}

	public void testObject()
	{
		String source = "Object"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.OBJECT);
	}

	public void testOctet()
	{
		String source = "octet"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.OCTET);
	}

	public void testOmittable()
	{
		String source = "omittable"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.OMITTABLE);
	}

	public void testOptional()
	{
		String source = "optional"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.OPTIONAL);
	}

	public void testRaises()
	{
		String source = "raises"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.RAISES);
	}

	public void testReadOnly()
	{
		String source = "readonly"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.READONLY);
	}

	public void testSequence()
	{
		String source = "sequence"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.SEQUENCE);
	}

	public void testSetRaises()
	{
		String source = "setraises"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.SETRAISES);
	}

	public void testSetter()
	{
		String source = "setter"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.SETTER);
	}

	public void testShort()
	{
		String source = "short"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.SHORT);
	}

	public void testStringifier()
	{
		String source = "stringifier"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.STRINGIFIER);
	}

	public void testTrue()
	{
		String source = "true"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.TRUE);
	}

	public void testTypeDef()
	{
		String source = "typedef"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.TYPEDEF);
	}

	public void testUnsigned()
	{
		String source = "unsigned"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.UNSIGNED);
	}

	public void testVoid()
	{
		String source = "void"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.VOID);
	}

	public void testLeftCurly()
	{
		String source = "{"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.LCURLY);
	}

	public void testRightCurly()
	{
		String source = "}"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.RCURLY);
	}

	public void testSemicolon()
	{
		String source = ";"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.SEMICOLON);
	}

	public void testColon()
	{
		String source = ":"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.COLON);
	}

	public void testLessThan()
	{
		String source = "<"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.LESS_THAN);
	}

	public void testGreaterThan()
	{
		String source = ">"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.GREATER_THAN);
	}

	public void testEqual()
	{
		String source = "="; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.EQUAL);
	}

	public void testLeftParen()
	{
		String source = "("; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.LPAREN);
	}

	public void testRightParen()
	{
		String source = ")"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.RPAREN);
	}

	public void testComma()
	{
		String source = ","; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.COMMA);
	}

	public void testLeftBracket()
	{
		String source = "["; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.LBRACKET);
	}

	public void testRightBracket()
	{
		String source = "]"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.RBRACKET);
	}

	public void testQuestion()
	{
		String source = "?"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.QUESTION);
	}

	public void testIntegerNumber()
	{
		String source = "10"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.NUMBER);
	}
	
	public void testNegativeIntegerNumber()
	{
		String source = "-10"; //$NON-NLS-1$
		
		this.typeTests(source, IDLTokenType.NUMBER);
	}
	
	public void testHexNumber()
	{
		String source = "0x10"; //$NON-NLS-1$
		
		this.typeTests(source, IDLTokenType.NUMBER);
	}
	
	public void testNegativeHexNumber()
	{
		String source = "-0x10"; //$NON-NLS-1$
		
		this.typeTests(source, IDLTokenType.NUMBER);
	}
	
	public void testOctalNumber()
	{
		String source = "007"; //$NON-NLS-1$
		
		this.typeTests(source, IDLTokenType.NUMBER);
	}
	
	public void testOctalHexNumber()
	{
		String source = "-007"; //$NON-NLS-1$
		
		this.typeTests(source, IDLTokenType.NUMBER);
	}

	public void testDoubleNumber()
	{
		String source = "10.10"; //$NON-NLS-1$

		this.typeTests(source, IDLTokenType.NUMBER);
	}
	
	public void testDoubleNumber2()
	{
		String source = ".10"; //$NON-NLS-1$
		
		this.typeTests(source, IDLTokenType.NUMBER);
	}
	
	public void testNegativeDoubleNumber()
	{
		String source = "-10.10"; //$NON-NLS-1$
		
		this.typeTests(source, IDLTokenType.NUMBER);
	}
	
	public void testNegativeDoubleNumber2()
	{
		String source = "-.10"; //$NON-NLS-1$
		
		this.typeTests(source, IDLTokenType.NUMBER);
	}
	
	public void testScientificNotation()
	{
		String source = "1.10e10"; //$NON-NLS-1$
		
		this.typeTests(source, IDLTokenType.NUMBER);
	}
	
	public void testScientificNotation2()
	{
		String source = "1.10e-10"; //$NON-NLS-1$
		
		this.typeTests(source, IDLTokenType.NUMBER);
	}
	
	public void testScientificNotation3()
	{
		String source = "1.10e+10"; //$NON-NLS-1$
		
		this.typeTests(source, IDLTokenType.NUMBER);
	}
	
	public void testNegativeScientificNotation()
	{
		String source = "1.10e10"; //$NON-NLS-1$
		
		this.typeTests(source, IDLTokenType.NUMBER);
	}
	
	public void testNegativeScientificNotation2()
	{
		String source = "1.10e-10"; //$NON-NLS-1$
		
		this.typeTests(source, IDLTokenType.NUMBER);
	}
	
	public void testNegativeScientificNotation3()
	{
		String source = "1.10e+10"; //$NON-NLS-1$
		
		this.typeTests(source, IDLTokenType.NUMBER);
	}
}
