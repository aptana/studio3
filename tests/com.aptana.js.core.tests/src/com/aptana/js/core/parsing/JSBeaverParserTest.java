package com.aptana.js.core.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.aptana.parsing.IParser;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;

import beaver.Symbol;

public class JSBeaverParserTest extends JSParserTest
{

	@Override
	protected IParser createParser()
	{
		return new JSParser();
	}

	@Override
	protected boolean isANTLR()
	{
		return false;
	}

	@Override
	protected boolean isBeaver()
	{
		return true;
	}

	protected String unexpectedToken(String token)
	{
		return "Syntax Error: unexpected token \"" + token + "\"";
	}

	@Override
	protected String mismatchedToken(int line, int offset, String token)
	{
		return unexpectedToken(token);
	}

	@Test
	public void testSDocComment() throws Exception
	{
		JSFlexScanner scanner = new JSFlexScanner();
		scanner.setSource("/**/");
		new JSParser().parse(scanner);

		List<Symbol> comments = scanner.getMultiLineComments();

		assertEquals(1, comments.size());
	}

	/**
	 * Test APSTUD-4072
	 *
	 * @throws IOException
	 * @throws beaver.Parser.Exception
	 */
	@Test
	public void testNodeOffsetsAtEOF() throws Exception
	{
		String source = "a.foo()\n// this is a comment";
		IParseNode result = parse(source);

		assertNotNull(result);
		assertEquals(1, result.getChildCount());

		IParseNode invokeNode = result.getFirstChild();
		assertNotNull(invokeNode);
		assertEquals(0, invokeNode.getStartingOffset());
		assertEquals(6, invokeNode.getEndingOffset());
	}

	@Test
	public void testSingleLineComment() throws Exception
	{
		String source = "// this is a single-line comment";

		IParseRootNode root = parse(source);
		IParseNode[] comments = root.getCommentNodes();
		assertNotNull(comments);
		assertEquals(1, comments.length);
		IParseNode comment = comments[0];
		assertEquals(0, comment.getStartingOffset());
		assertEquals(source.length() - 1, comment.getEndingOffset());
	}

}
