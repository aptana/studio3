/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.markdown.text.rules;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.common.tests.AbstractTokenScannerTestCase;

public class MarkdownScannerTest extends AbstractTokenScannerTestCase
{

	@Override
	protected ITokenScanner createTokenScanner()
	{
		return new MarkdownScanner();
	}

	public void testBasicTokenizing()
	{
		String src = "Everything else:";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken(""), 0, 10);
		assertToken(Token.WHITESPACE, 10, 1);
		assertToken(getToken(""), 11, 4);
		assertToken(getToken(""), 15, 1);
	}

	public void testItalic()
	{
		String src = "There is *italics* here";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken(""), 0, 5);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken(""), 6, 2);
		assertToken(Token.WHITESPACE, 8, 1);
		assertToken(getToken("markup.italic.markdown"), 9, 9);
		assertToken(Token.WHITESPACE, 18, 1);
		assertToken(getToken(""), 19, 4);
	}

	public void testEscapedAsteriskIsntItalic()
	{
		String src = "Aint no \\*italics\\* here";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken(""), 0, 4);
		assertToken(Token.WHITESPACE, 4, 1);
		assertToken(getToken(""), 5, 2);
		assertToken(Token.WHITESPACE, 7, 1);
		assertToken(getToken("constant.character.escape.markdown"), 8, 2);
		assertToken(getToken(""), 10, 7);
		assertToken(getToken("constant.character.escape.markdown"), 17, 2);
		assertToken(Token.WHITESPACE, 19, 1);
		assertToken(getToken(""), 20, 4);
	}

	public void testEscapedUnderscoreIsntItalic()
	{
		String src = "Aint no \\_italics\\_ here";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken(""), 0, 4);
		assertToken(Token.WHITESPACE, 4, 1);
		assertToken(getToken(""), 5, 2);
		assertToken(Token.WHITESPACE, 7, 1);
		assertToken(getToken("constant.character.escape.markdown"), 8, 2);
		assertToken(getToken(""), 10, 7);
		assertToken(getToken("constant.character.escape.markdown"), 17, 2);
		assertToken(Token.WHITESPACE, 19, 1);
		assertToken(getToken(""), 20, 4);
	}

	public void testItalicWithSpaceInside()
	{
		String src = "There is *ita lics* here";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken(""), 0, 5);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken(""), 6, 2);
		assertToken(Token.WHITESPACE, 8, 1);
		assertToken(getToken("markup.italic.markdown"), 9, 10);
		assertToken(Token.WHITESPACE, 19, 1);
		assertToken(getToken(""), 20, 4);
	}

	public void testItalicsUsingUnderscores()
	{
		String src = "There is _italics_ here";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken(""), 0, 5);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken(""), 6, 2);
		assertToken(Token.WHITESPACE, 8, 1);
		assertToken(getToken("markup.italic.markdown"), 9, 9);
		assertToken(Token.WHITESPACE, 18, 1);
		assertToken(getToken(""), 19, 4);
	}

	public void testItalicsUsingUnderscoresWithSpaceInside()
	{
		String src = "There is _ita lics_ here";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken(""), 0, 5);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken(""), 6, 2);
		assertToken(Token.WHITESPACE, 8, 1);
		assertToken(getToken("markup.italic.markdown"), 9, 10);
		assertToken(Token.WHITESPACE, 19, 1);
		assertToken(getToken(""), 20, 4);
	}

	// FIXME Fix this bug!
//	public void testUnderscoreSurroundedBySpaceIsLiteral()
//	{
//		String src = "There is _ no italics _ here";
//		IDocument document = new Document(src);
//		scanner.setRange(document, 0, src.length());
//
//		assertToken(getToken(""), 0, 5);
//		assertToken(Token.WHITESPACE, 5, 1);
//		assertToken(getToken(""), 6, 2);
//		assertToken(Token.WHITESPACE, 8, 1);
//		assertToken(getToken(""), 9, 1);
//		assertToken(Token.WHITESPACE, 10, 1);
//		assertToken(getToken(""), 11, 2);
//		assertToken(Token.WHITESPACE, 13, 1);
//		assertToken(getToken(""), 14, 7);
//		assertToken(Token.WHITESPACE, 21, 1);
//		assertToken(getToken(""), 22, 1);
//		assertToken(Token.WHITESPACE, 23, 1);
//		assertToken(getToken(""), 24, 4);
//	}

	// FIXME Fix this bug!
//	public void testAsteriskSurroundedBySpaceIsLiteral()
//	{
//		String src = "There is * no italics * here";
//		IDocument document = new Document(src);
//		scanner.setRange(document, 0, src.length());
//
//		assertToken(getToken(""), 0, 5);
//		assertToken(Token.WHITESPACE, 5, 1);
//		assertToken(getToken(""), 6, 2);
//		assertToken(Token.WHITESPACE, 8, 1);
//		assertToken(getToken(""), 9, 1);
//		assertToken(Token.WHITESPACE, 10, 1);
//		assertToken(getToken(""), 11, 2);
//		assertToken(Token.WHITESPACE, 13, 1);
//		assertToken(getToken(""), 14, 7);
//		assertToken(Token.WHITESPACE, 21, 1);
//		assertToken(getToken(""), 22, 1);
//		assertToken(Token.WHITESPACE, 23, 1);
//		assertToken(getToken(""), 24, 4);
//	}

	public void testBold()
	{
		String src = "There is **bold** here";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken(""), 0, 5);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken(""), 6, 2);
		assertToken(Token.WHITESPACE, 8, 1);
		assertToken(getToken("markup.bold.markdown"), 9, 8);
		assertToken(Token.WHITESPACE, 17, 1);
		assertToken(getToken(""), 18, 4);
	}

	public void testBoldWithSpaceInside()
	{
		String src = "There is **bo ld** here";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken(""), 0, 5);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken(""), 6, 2);
		assertToken(Token.WHITESPACE, 8, 1);
		assertToken(getToken("markup.bold.markdown"), 9, 9);
		assertToken(Token.WHITESPACE, 18, 1);
		assertToken(getToken(""), 19, 4);
	}

	public void testBoldUsingUnderscores()
	{
		String src = "There is __bold__ here";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken(""), 0, 5);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken(""), 6, 2);
		assertToken(Token.WHITESPACE, 8, 1);
		assertToken(getToken("markup.bold.markdown"), 9, 8);
		assertToken(Token.WHITESPACE, 17, 1);
		assertToken(getToken(""), 18, 4);
	}

	public void testBoldUsingUnderscoresWithSpaceInside()
	{
		String src = "There is __bo ld__ here";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken(""), 0, 5);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken(""), 6, 2);
		assertToken(Token.WHITESPACE, 8, 1);
		assertToken(getToken("markup.bold.markdown"), 9, 9);
		assertToken(Token.WHITESPACE, 18, 1);
		assertToken(getToken(""), 19, 4);
	}

	public void testLinkWithTitle()
	{
		String src = "This is [an example](http://example.com/ \"Title\") inline link.";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken(""), 0, 4); //$NON-NLS-1$
		assertToken(Token.WHITESPACE, 4, 1);
		assertToken(getToken(""), 5, 2); //$NON-NLS-1$
		assertToken(Token.WHITESPACE, 7, 1);
		assertToken(getToken("string.other.link.title.markdown"), 8, 12); //$NON-NLS-1$
		assertToken(getToken("punctuation.definition.metadata.markdown"), 20, 1); //$NON-NLS-1$
		assertToken(getToken("markup.underline.link.markdown"), 21, 19); //$NON-NLS-1$
		assertToken(Token.WHITESPACE, 40, 1);
		assertToken(getToken("string.other.link.description.title.markdown"), 41, 7); //$NON-NLS-1$
		assertToken(getToken("punctuation.definition.metadata.markdown"), 48, 1); //$NON-NLS-1$
		assertToken(Token.WHITESPACE, 49, 1);
		assertToken(getToken(""), 50, 6); //$NON-NLS-1$
		assertToken(Token.WHITESPACE, 56, 1);
		assertToken(getToken(""), 57, 4); //$NON-NLS-1$
		assertToken(getToken(""), 61, 1); //$NON-NLS-1$
	}

	public void testLinkWithNoTitle()
	{
		String src = "[This link](http://example.net/) has no title attribute.";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("string.other.link.title.markdown"), 0, 11); //$NON-NLS-1$
		assertToken(getToken("punctuation.definition.metadata.markdown"), 11, 1); //$NON-NLS-1$
		assertToken(getToken("markup.underline.link.markdown"), 12, 19); //$NON-NLS-1$
		assertToken(getToken("punctuation.definition.metadata.markdown"), 31, 1); //$NON-NLS-1$
		assertToken(Token.WHITESPACE, 32, 1);
		assertToken(getToken(""), 33, 3); //$NON-NLS-1$
		assertToken(Token.WHITESPACE, 36, 1);
		assertToken(getToken(""), 37, 2); //$NON-NLS-1$
		assertToken(Token.WHITESPACE, 39, 1);
		assertToken(getToken(""), 40, 5); //$NON-NLS-1$
		assertToken(Token.WHITESPACE, 45, 1);
		assertToken(getToken(""), 46, 9); //$NON-NLS-1$
		assertToken(getToken(""), 55, 1); //$NON-NLS-1$
	}

	public void testLinkWithNoTitleRelativeURL()
	{
		String src = "See my [About](/about/) page for details.";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken(""), 0, 3); //$NON-NLS-1$
		assertToken(Token.WHITESPACE, 3, 1);
		assertToken(getToken(""), 4, 2); //$NON-NLS-1$
		assertToken(Token.WHITESPACE, 6, 1);
		assertToken(getToken("string.other.link.title.markdown"), 7, 7); //$NON-NLS-1$
		assertToken(getToken("punctuation.definition.metadata.markdown"), 14, 1); //$NON-NLS-1$
		assertToken(getToken("markup.underline.link.markdown"), 15, 7); //$NON-NLS-1$
		assertToken(getToken("punctuation.definition.metadata.markdown"), 22, 1); //$NON-NLS-1$
		assertToken(Token.WHITESPACE, 23, 1);
		assertToken(getToken(""), 24, 4); //$NON-NLS-1$
		assertToken(Token.WHITESPACE, 28, 1);
		assertToken(getToken(""), 29, 3); //$NON-NLS-1$
		assertToken(Token.WHITESPACE, 32, 1);
		assertToken(getToken(""), 33, 7); //$NON-NLS-1$
		assertToken(getToken(""), 40, 1); //$NON-NLS-1$
	}

	public void testReferenceLink()
	{
		String src = "This is [an example][id] reference-style link.";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken(""), 0, 4); //$NON-NLS-1$
		assertToken(Token.WHITESPACE, 4, 1);
		assertToken(getToken(""), 5, 2); //$NON-NLS-1$
		assertToken(Token.WHITESPACE, 7, 1);
		assertToken(getToken("string.other.link.title.markdown"), 8, 12); //$NON-NLS-1$
		assertToken(getToken("constant.other.reference.link.markdown"), 20, 4); //$NON-NLS-1$
		assertToken(Token.WHITESPACE, 24, 1);
		assertToken(getToken(""), 25, 9); //$NON-NLS-1$
		assertToken(getToken(""), 34, 1); //$NON-NLS-1$
		assertToken(getToken(""), 35, 5); //$NON-NLS-1$
		assertToken(Token.WHITESPACE, 40, 1);
		assertToken(getToken(""), 41, 4); //$NON-NLS-1$
	}

	public void testReferenceLinkWithSpaceBetweenBrackets()
	{
		String src = "This is [an example] [id] reference-style link.";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken(""), 0, 4); //$NON-NLS-1$
		assertToken(Token.WHITESPACE, 4, 1);
		assertToken(getToken(""), 5, 2); //$NON-NLS-1$
		assertToken(Token.WHITESPACE, 7, 1);
		assertToken(getToken("string.other.link.title.markdown"), 8, 12); //$NON-NLS-1$
		assertToken(Token.WHITESPACE, 20, 1);
		assertToken(getToken("constant.other.reference.link.markdown"), 21, 4); //$NON-NLS-1$
		assertToken(Token.WHITESPACE, 25, 1);
		assertToken(getToken(""), 26, 9); //$NON-NLS-1$
		assertToken(getToken(""), 35, 1); //$NON-NLS-1$
		assertToken(getToken(""), 36, 5); //$NON-NLS-1$
		assertToken(Token.WHITESPACE, 41, 1);
		assertToken(getToken(""), 42, 4); //$NON-NLS-1$
	}

	public void testReferenceDefinitionWithDoubleQuotes()
	{
		String src = "[foo]: http://example.com/  \"Optional Title Here\"";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("constant.other.reference.link.markdown"), 0, 5); //$NON-NLS-1$
		assertToken(getToken(""), 5, 1); //$NON-NLS-1$
		assertToken(Token.WHITESPACE, 6, 1);
		assertToken(getToken("markup.underline.link.markdown"), 7, 19); //$NON-NLS-1$
		assertToken(Token.WHITESPACE, 26, 2);
		assertToken(getToken("string.other.link.description.title.markdown"), 28, 21); //$NON-NLS-1$
	}

	public void testReferenceDefinitionWithSingleQuotes()
	{
		String src = "[foo]: http://example.com/  'Optional Title Here'";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("constant.other.reference.link.markdown"), 0, 5); //$NON-NLS-1$
		assertToken(getToken(""), 5, 1); //$NON-NLS-1$
		assertToken(Token.WHITESPACE, 6, 1);
		assertToken(getToken("markup.underline.link.markdown"), 7, 19); //$NON-NLS-1$
		assertToken(Token.WHITESPACE, 26, 2);
		assertToken(getToken("string.other.link.description.title.markdown"), 28, 21); //$NON-NLS-1$
	}

	// FIXME Fix this bug!
//	public void testReferenceDefinitionWithParentheses()
//	{
//		String src = "[foo]: http://example.com/  (Optional Title Here)";
//		IDocument document = new Document(src);
//		scanner.setRange(document, 0, src.length());
//
//		assertToken(getToken("constant.other.reference.link.markdown"), 0, 5); //$NON-NLS-1$
//		assertToken(getToken(""), 5, 1); //$NON-NLS-1$
//		assertToken(Token.WHITESPACE, 6, 1);
//		assertToken(getToken("markup.underline.link.markdown"), 7, 19); //$NON-NLS-1$
//		assertToken(Token.WHITESPACE, 26, 2);
//		assertToken(getToken("string.other.link.description.title.markdown"), 28, 21); //$NON-NLS-1$
//	}
}
