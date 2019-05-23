package com.aptana.editor.common.text.rules;

import static org.junit.Assert.assertEquals;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.junit.Test;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.editor.common.tests.AbstractTokenScannerTestCase;

public class CommentScannerTest extends AbstractTokenScannerTestCase
{

	private IToken defaultToken = new Token("default");;

	@Test
	public void testBasicTokenizing()
	{
		String src = "// THIS is some long text, man TODO something or other\n";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(defaultToken, 0, 31);
		assertToken(getToken(CommentScanner.TASK_TAG_SCOPE), 31, 4);
		assertToken(defaultToken, 35, 20);
		assertEOF();
	}
	
	@Test
	public void testNoTags()
	{
		String src = "// THIS is some long text, man test something or other\n";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(defaultToken, 0, 55);
		assertEOF();
	}

	@Test
	public void testNonZeroOffset()
	{
		String src = "var i - 0;\n// THIS is some long text, man test something or other\n";
		IDocument document = new Document(src);
		scanner.setRange(document, 11, 55);

		assertToken(defaultToken, 11, 55);
		assertEOF();
	}

	protected void assertEOF()
	{
		IToken next = scanner.nextToken();
		assertEquals(next, Token.EOF);
	}
	
	@Test
	public void testBasicTokenizingCaseInsensitive()
	{
		scanner = new CommentScanner(defaultToken, CollectionsUtil.newList("TODO"), false);

		String src = "// THIS is some long text, man todo something or other\n";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(defaultToken, 0, 31);
		assertToken(getToken(CommentScanner.TASK_TAG_SCOPE), 31, 4);
		assertToken(defaultToken, 35, 20);
		assertEOF();
	}
	
	@Test
	public void testMultipleTagsWithMixedCaseAndCaseInsensitive()
	{
		scanner = new CommentScanner(defaultToken, CollectionsUtil.newList("TODO", "fixme"), false);

		String src = "// FixMe is some long text, man toDO something or other\n";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(defaultToken, 0, 3);
		assertToken(getToken(CommentScanner.TASK_TAG_SCOPE), 3, 5);
		assertToken(defaultToken, 8, 24);
		assertToken(getToken(CommentScanner.TASK_TAG_SCOPE), 32, 4);
		assertToken(defaultToken, 36, 20);
		assertEOF();
	}
	
	@Test
	public void testMultipleTagsWithMixedCaseAndCaseSensitiveNotMatching()
	{
		String src = "// FixMe is some long text, man toDO something or other\n";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(defaultToken, 0, 56);
		assertEOF();
	}
	
	@Test
	public void testMultipleTagsWithMixedCaseAndCaseSensitiveMatching()
	{
		String src = "// fixme is some long text, man TODO something or other\n";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(defaultToken, 0, 3);
		assertToken(getToken(CommentScanner.TASK_TAG_SCOPE), 3, 5);
		assertToken(defaultToken, 8, 24);
		assertToken(getToken(CommentScanner.TASK_TAG_SCOPE), 32, 4);
		assertToken(defaultToken, 36, 20);
		assertEOF();
	}

	@Override
	protected ITokenScanner createTokenScanner()
	{
		return new CommentScanner(defaultToken, CollectionsUtil.newList("TODO", "fixme"), true);
	}

}
