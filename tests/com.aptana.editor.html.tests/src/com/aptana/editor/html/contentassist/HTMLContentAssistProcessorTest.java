/**
 * Aptana Studio
 * Copyright (c) 2005-2014 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.swt.graphics.Point;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.tests.BadDocument;
import com.aptana.editor.common.tests.util.AssertUtil;
import com.aptana.editor.html.HTMLMetadataLoader;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.HTMLTestUtil;
import com.aptana.editor.html.core.preferences.IPreferenceConstants;
import com.aptana.editor.html.parsing.lexer.HTMLLexemeProvider;
import com.aptana.editor.html.parsing.lexer.HTMLTokenType;
import com.aptana.parsing.lexer.Lexeme;

public class HTMLContentAssistProcessorTest
{

	private static final int ELEMENT_PROPOSALS_COUNT = 135;
	private static final int DOCTYPE_PROPOSALS_COUNT = 11;
	private static final int CLOSE_TAG_PROPOSALS_COUNT = 121;
	private static final int ENTITY_PROPOSAL_COUNT = 252;

	private HTMLContentAssistProcessor fProcessor;
	private IDocument fDocument;
	private List<Integer> cursorOffsets;

	@BeforeClass
	public static void loadMetadata() throws Exception
	{
		HTMLMetadataLoader loader = new HTMLMetadataLoader();
		loader.schedule();
		loader.join();
	}

	@Before
	public void setUp() throws Exception
	{
		fProcessor = new HTMLContentAssistProcessor(null);
	}

	@After
	public void tearDown() throws Exception
	{
		HTMLPlugin.getDefault().getPreferenceStore().setValue(IPreferenceConstants.HTML_REMOTE_HREF_PROPOSALS, true);
		fProcessor = null;
		fDocument = null;
		cursorOffsets = null;
	}

	@Test
	public void testGetContextInformationValidator()
	{
		IContextInformationValidator validator = fProcessor.getContextInformationValidator();
		assertNotNull(validator);
		// should be the same object as we are caching them
		assertEquals(validator, fProcessor.getContextInformationValidator());
	}

	@Test
	public void testEmptyDocument()
	{
		assertCompletionCorrect("|", '\t', 0, null, StringUtil.EMPTY, null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testLinkProposal()
	{
		assertCompletionCorrect("<|", '\t', ELEMENT_PROPOSALS_COUNT, "a", "<a></a>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testDOCTYPEProposal1()
	{
		assertCompletionCorrect("<!|", '\t', ELEMENT_PROPOSALS_COUNT, "!DOCTYPE", "<!DOCTYPE >", new Point(10, 0)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testDOCTYPEProposal2()
	{
		assertCompletionCorrect("<!D|", '\t', ELEMENT_PROPOSALS_COUNT, "!DOCTYPE", "<!DOCTYPE >", new Point(10, 0)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testDOCTYPEProposal3()
	{
		assertCompletionCorrect(
				"<!D| html>", '\t', ELEMENT_PROPOSALS_COUNT, "!DOCTYPE", "<!DOCTYPE html>", new Point(10, 0)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testDOCTYPEProposal4()
	{
		assertCompletionCorrect("<|>", '\t', ELEMENT_PROPOSALS_COUNT, "!DOCTYPE", "<!DOCTYPE >", new Point(10, 0)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testDOCTYPEProposal5()
	{
		assertCompletionCorrect("<!|>", '\t', ELEMENT_PROPOSALS_COUNT, "!DOCTYPE", "<!DOCTYPE >", new Point(10, 0)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testDOCTYPEProposal6()
	{
		assertCompletionCorrect(
				"<!D|OCTYP >", '\t', ELEMENT_PROPOSALS_COUNT, "!DOCTYPE", "<!DOCTYPE >", new Point(10, 0)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testDOCTYPEProposal7()
	{
		assertCompletionCorrect(
				"<!D|OCTYPE >", '\t', ELEMENT_PROPOSALS_COUNT, "!DOCTYPE", "<!DOCTYPE >", new Point(10, 0)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testDOCTYPEValueReplacement1()
	{
		assertCompletionCorrect("<!DOCTYPE |html>", '\t', DOCTYPE_PROPOSALS_COUNT, "HTML 5", "<!DOCTYPE HTML>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testDOCTYPEValueReplacement2()
	{
		assertCompletionCorrect(
				"<!DOCTYPE |html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\"\n	\"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">", //$NON-NLS-1$
				'\t', DOCTYPE_PROPOSALS_COUNT, "HTML 5", "<!DOCTYPE HTML>", null); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testABBRProposal1()
	{
		assertCompletionCorrect("<a|>", '\t', ELEMENT_PROPOSALS_COUNT, "abbr", "<abbr></abbr>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testABBRProposal2()
	{
		assertCompletionCorrect("<A|>", '\t', ELEMENT_PROPOSALS_COUNT, "abbr", "<abbr></abbr>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testElementWhichIsClosedProposal()
	{
		assertCompletionCorrect("<|></a>", '\t', ELEMENT_PROPOSALS_COUNT, "a", "<a></a>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testElementWhichIsClosedProposal2()
	{
		assertCompletionCorrect("<|></a>", '\t', ELEMENT_PROPOSALS_COUNT, "abbr", "<abbr></abbr></a>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testElementWhichIsClosedProposal3()
	{
		assertCompletionCorrect("<|</a>", '\t', ELEMENT_PROPOSALS_COUNT, "abbr", "<abbr></abbr></a>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testElementWhichIsClosedProposal4()
	{
		assertCompletionCorrect("<b><a><|</b>", '\t', ELEMENT_PROPOSALS_COUNT + 1, "/a", "<b><a></a></b>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testElementWhichIsClosedProposal5()
	{
		assertCompletionCorrect("<b><a><|></b>", '\t', ELEMENT_PROPOSALS_COUNT + 1, "/a", "<b><a></a></b>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testElementWhichIsClosedProposal6()
	{
		assertCompletionCorrect("<b><a></|</b>", '\t', 1, "/a", "<b><a></a></b>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testSuggestOnlyUnclosedTagForCloseTagWithNoElementName()
	{
		assertCompletionCorrect("<b><a></|></b>", '\t', 1, "/a", "<b><a></a></b>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testSuggestOnlyUnclosedTagForCloseTagWithElementName()
	{
		assertCompletionCorrect("<b><a></a|</b>", '\t', 1, "/a", "<b><a></a></b>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testSuggestAllPossibleCloseTagsOnExistingCloseTagRegardlessOfPrefix()
	{
		assertCompletionCorrect("<b><a></a|></b>", '\t', CLOSE_TAG_PROPOSALS_COUNT, "/a", "<b><a></a></b>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testCloseTagWithNoUnclosedTagsProposal()
	{
		assertCompletionCorrect("</|>", '\t', CLOSE_TAG_PROPOSALS_COUNT, "/ul", "</ul>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testNoSuggestionsInTextAreaBetweenTags()
	{
		assertCompletionCorrect("<p>|</p>", '\t', 0, null, "<p></p>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testNoSuggestionsInTextAreaWithWhitespaceBetweenTags()
	{
		assertCompletionCorrect("<p>\n  |\n</p>", '\t', 0, null, "<p>\n  \n</p>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testOnlySuggestEntityIfPrecededByAmpersand()
	{
		assertCompletionCorrect("<p>\n  &|\n</p>", '\t', ENTITY_PROPOSAL_COUNT, "&amp;", "<p>\n  &amp;\n</p>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testEntitySuggestionWithNoSurroundingWhitespace()
	{
		assertCompletionCorrect("<div>&|</div>", '\t', ENTITY_PROPOSAL_COUNT, "&amp;", "<div>&amp;</div>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testExistingEntityGetsFullyReplaced1()
	{
		assertCompletionCorrect(
				"<body>\n  &a|acute;\n</body>", '\t', ENTITY_PROPOSAL_COUNT, "&acirc;", "<body>\n  &acirc;\n</body>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testExistingEntityGetsFullyReplaced2()
	{
		assertCompletionCorrect("<div>&a|acute;</div>", '\t', ENTITY_PROPOSAL_COUNT, "&amp;", "<div>&amp;</div>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testEntityDoesntReplaceNonEntityText()
	{
		assertCompletionCorrect(
				"<div>ind&u|stria</div>", '\t', ENTITY_PROPOSAL_COUNT, "&uacute;", "<div>ind&uacute;stria</div>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testProposalsBadLocation()
	{
		String document = "<body>&|";
		int offset = HTMLTestUtil.findCursorOffset(document);
		fDocument = HTMLTestUtil.createBadDocument(document, true);

		// offset is outside document size
		((BadDocument) fDocument).setThrowBadLocation(true);
		fProcessor.doComputeCompletionProposals(textViewer(), offset, '\t', false);
	}

	@Test
	public void testIMGProposal()
	{
		assertCompletionCorrect("<|", '\t', ELEMENT_PROPOSALS_COUNT, "img", "<img />", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testHTML5DoctypeProposal()
	{
		assertCompletionCorrect("<!doctype |>", '\t', DOCTYPE_PROPOSALS_COUNT, "HTML 5", "<!doctype HTML>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testHTML401StrictDoctypeProposal()
	{
		assertCompletionCorrect("<!DOCTYPE |", '\t', DOCTYPE_PROPOSALS_COUNT, "HTML 4.01 Strict", //$NON-NLS-1$ //$NON-NLS-2$
				"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\"\n\"http://www.w3.org/TR/html4/strict.dtd\"", null); //$NON-NLS-1$
	}

	@Test
	public void testStyleAttributeProposalWithNoPrefix()
	{
		assertCompletionCorrect("<div |></div>", '\t', 64, "style", "<div style=\"\"></div>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testStyleAttributeProposalOnSelfClosingTag()
	{
		assertCompletionCorrect("<br |/>", '\t', 8, "style", "<br style=\"\"/>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testStyleAttributeProposalOnSelfClosingTagWithTrailingSpaceAfterCursor()
	{
		assertCompletionCorrect("<br | />", '\t', 8, "style", "<br style=\"\" />", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testStyleAttributeProposalWithPrefix()
	{
		assertCompletionCorrect("<div sty|></div>", '\t', 64, "style", "<div style=\"\"></div>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testStyleAttributeProposalWithPrefixAndTrailingEquals()
	{
		assertCompletionCorrect("<div sty|=\"\"></div>", '\t', 64, "style", "<div style=\"\"></div>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testDontOverwriteTagEnd1()
	{
		assertCompletionCorrect("<div dir=\"|></div>", '\t', 2, "ltr", "<div dir=\"ltr\"></div>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testDontOverwriteTagEnd2()
	{
		assertCompletionCorrect("<br dir=\"|/>", '\t', 2, "ltr", "<br dir=\"ltr\"/>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testStyleAttributeProposalHasExitTabstopAfterQuotes()
	{
		assertCompletionCorrect("<div |>", '\t', 64, "style", "<div style=\"\">", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// Now test that we have two tabstops. One inside the quotes, one just after
		LinkedModeModel model = LinkedModeModel.getModel(fDocument, 12);
		assertNotNull(model);
		List list = model.getTabStopSequence();
		assertNotNull(list);
		assertEquals(2, list.size());
		Position pos = (Position) list.get(0);
		assertEquals(12, pos.getOffset());
		assertEquals(0, pos.getLength());

		pos = (Position) list.get(1);
		assertEquals(13, pos.getOffset());
		assertEquals(0, pos.getLength());
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testEventProposalHasExitTabstopAfterQuotes()
	{
		assertCompletionCorrect("<div |>", '\t', 64, "onmouseenter", "<div onmouseenter=\"\">", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// Now test that we have two tabstops. One inside the quotes, one just after
		LinkedModeModel model = LinkedModeModel.getModel(fDocument, 19);
		assertNotNull(model);
		List list = model.getTabStopSequence();
		assertNotNull(list);
		assertEquals(2, list.size());
		Position pos = (Position) list.get(0);
		assertEquals(19, pos.getOffset());
		assertEquals(0, pos.getLength());

		pos = (Position) list.get(1);
		assertEquals(20, pos.getOffset());
		assertEquals(0, pos.getLength());
	}

	// https://aptana.lighthouseapp.com/projects/35272/tickets/1719-html-code-completion-for-tag-attributes-goes-wrong
	@Test
	public void testIMGSrcContentAssistDoesntAutoInsertCloseTag()
	{
		String document = "<div><img src='|' />";
		ICompletionProposal[] proposals = complete(document, '\t');
		assertEquals(0, proposals.length);
	}

	/**
	 * Returns the first offset.
	 * 
	 * @return
	 */
	private int getOffset()
	{
		if (CollectionsUtil.isEmpty(this.cursorOffsets))
		{
			throw new IllegalStateException("Must have cursor offsets parsed out already!");
		}
		return this.cursorOffsets.get(0);
	}

	private ITextViewer textViewer()
	{
		if (fDocument == null)
		{
			throw new IllegalStateException("fDocument must be set first!");
		}
		return new com.aptana.editor.common.tests.TextViewer(fDocument);
	}

	@Test
	public void testAttributeNameAtSpace()
	{
		ICompletionProposal[] proposals = complete("<p | align=\"\"></p>");
		assertTrue(proposals.length > 0);
		AssertUtil.assertProposalFound("class", proposals);
	}

	@Test
	public void testAttributeNameAtSpace2()
	{
		ICompletionProposal[] proposals = complete("<p align=\"\" | ></p>");
		assertTrue(proposals.length > 0);
		AssertUtil.assertProposalFound("class", proposals);
	}

	@Test
	public void testAttributeAfterElementName()
	{
		ICompletionProposal[] proposals = complete("<body s|></body>");
		assertTrue(proposals.length > 0);
		AssertUtil.assertProposalFound("scroll", proposals);
	}

	@Test
	public void testAttributeValueProposals()
	{
		ICompletionProposal[] proposals = complete("<li><a class=|</li>");
		assertTrue(proposals.length == 0);
	}

	@Test
	public void testAttributeValueProposalsBeforeEquals()
	{
		ICompletionProposal[] proposals = complete("<li><a clas|s=</li>");
		assertTrue(proposals.length > 0);
	}

	@Test
	public void testAPSTUD5017()
	{
		ICompletionProposal[] proposals = complete("<video autoplay=|preload=\"none\"></video>");
		assertTrue(proposals.length >= 1); // "autoplay"
		// insert the "autoplay" proposal
		AssertUtil.assertProposalFound("autoplay", proposals);
		AssertUtil.assertProposalApplies("<video autoplay=\"autoplay\" preload=\"none\"></video>", fDocument,
				"autoplay", proposals, getOffset(), null);
	}

	@Test
	public void testAtrributeValueMidValueNoCloseQuoteAtEOF()
	{
		ICompletionProposal[] proposals = complete("<script type=\"te|");
		assertTrue(proposals.length >= 1); // "text/javascript"
		// insert the "text/javascript" proposal
		AssertUtil.assertProposalFound("text/javascript", proposals);
		AssertUtil.assertProposalApplies("<script type=\"text/javascript\" ", fDocument, "text/javascript", proposals,
				getOffset(), null);
	}

	@Test
	public void testAtrributeValueMidValueWithExistingQuotes()
	{
		ICompletionProposal[] proposals = complete("<script type=\"te|\"");
		assertTrue(proposals.length >= 1); // "text/javascript"
		// insert the "text/javascript" proposal
		AssertUtil.assertProposalFound("text/javascript", proposals);
		AssertUtil.assertProposalApplies("<script type=\"text/javascript\"", fDocument, "text/javascript", proposals,
				getOffset(), null);
	}

	@Test
	public void testAtrributeValueNoValueWithExistingQuotes()
	{
		ICompletionProposal[] proposals = complete("<script type=\"|\"");
		assertTrue(proposals.length >= 1); // "text/javascript"
		// insert the "text/javascript" proposal
		AssertUtil.assertProposalFound("text/javascript", proposals);
		AssertUtil.assertProposalApplies("<script type=\"text/javascript\"", fDocument, "text/javascript", proposals,
				getOffset(), null);
	}

	@Test
	public void testAtrributeValueNoValueNoQuotes()
	{
		ICompletionProposal[] proposals = complete("<script type=|");
		assertTrue(proposals.length >= 1); // "text/javascript"
		// insert the "text/javascript" proposal
		AssertUtil.assertProposalFound("text/javascript", proposals);
		AssertUtil.assertProposalApplies("<script type=\"text/javascript\"", fDocument, "text/javascript", proposals,
				getOffset(), null);
	}

	@Test
	public void testAtrributeValueNoValueLeadingSingleQuoteTrailingGT()
	{
		ICompletionProposal[] proposals = complete("<script type='|>");
		assertTrue(proposals.length >= 1); // "text/javascript"
		// insert the "text/javascript" proposal
		AssertUtil.assertProposalFound("text/javascript", proposals);
		AssertUtil.assertProposalApplies("<script type='text/javascript'>", fDocument, "text/javascript", proposals,
				getOffset(), null);
	}

	@Test
	public void testAtrributeValuePartialValueNoLeadingQuote()
	{
		ICompletionProposal[] proposals = complete("<link rel=alt| />");
		assertTrue(proposals.length >= 1); // "alternate"
		// insert the "alternate" proposal
		AssertUtil.assertProposalFound("alternate", proposals);
		AssertUtil.assertProposalApplies("<link rel=\"alternate\" />", fDocument, "alternate", proposals, getOffset(),
				new Point(21, 0));
	}

	@Test
	public void testAtrributeValueNoValueWrappingSingleQuoteTrailingAttributeName()
	{
		ICompletionProposal[] proposals = complete("<script type='|'id='1'>");
		assertTrue(proposals.length >= 1); // "text/javascript"
		// insert the "text/javascript" proposal
		AssertUtil.assertProposalFound("text/javascript", proposals);
		AssertUtil.assertProposalApplies("<script type='text/javascript'id='1'>", fDocument, "text/javascript",
				proposals, getOffset(), null);
	}

	// @Test public void testAtrributeValueNoValueLeadingSingleQuoteTrailingAttributeName()
	// {
	// String document = "<script type='|id='1'>";
	// int offset = HTMLTestUtil.findCursorOffset(document);
	// fDocument = HTMLTestUtil.createDocument(document, true);
	// ITextViewer viewer = createTextViewer(fDocument);
	//
	// ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, '\t', false);
	// assertTrue(proposals.length >= 1); // "text/javascript"
	// // insert the "text/javascript" proposal
	// AssertUtil.assertProposalFound("text/javascript", proposals);
	// AssertUtil.assertProposalApplies("<script type='text/javascript' id='1'>", fDocument, "text/javascript",
	// proposals, offset, null);
	// }

	@Test
	public void testIsValidIdentifier()
	{
		assertTrue(fProcessor.isValidIdentifier('a', 'a'));
		assertTrue(fProcessor.isValidIdentifier('z', 'z'));
		assertTrue(fProcessor.isValidIdentifier('A', 'A'));
		assertTrue(fProcessor.isValidIdentifier('Z', 'Z'));
		assertFalse(fProcessor.isValidIdentifier('_', '_'));
		assertFalse(fProcessor.isValidIdentifier('$', '$'));
		assertFalse(fProcessor.isValidIdentifier(' ', ' '));
	}

	private void assertCompletionCorrect(String source, char trigger, int proposalCount, String proposalToChoose,
			String postCompletion, Point point)
	{
		ICompletionProposal[] proposals = complete(source, trigger);
		assertEquals(proposalCount, proposals.length);
		if (proposalToChoose != null)
		{
			AssertUtil.assertProposalFound(proposalToChoose, proposals);
			AssertUtil
					.assertProposalApplies(postCompletion, fDocument, proposalToChoose, proposals, getOffset(), point);
		}
	}

	private ICompletionProposal[] complete(String source)
	{
		return complete(source, '\t');
	}

	private ICompletionProposal[] complete(String source, char trigger)
	{
		setupDocument(source);

		return fProcessor.doComputeCompletionProposals(textViewer(), getOffset(), trigger, false);
	}

	/**
	 * Given source, we will wrap it in an IDocument, strip out "cursor" offsets, and partition the document.
	 * 
	 * @param source
	 * @return
	 */
	private IDocument setupDocument(String source)
	{
		fDocument = new Document(source);
		handleCursorOffsets(fDocument);
		HTMLTestUtil.attachPartitioner(fDocument);
		return fDocument;
	}

	@Test
	public void testDoubleQuotedEventAttributeValueType() throws Exception
	{
		setupTestContext("contentAssist/js-event-attribute-double-quoted.html");

		for (int offset : cursorOffsets)
		{
			HTMLLexemeProvider lexemeProvider = fProcessor.createLexemeProvider(fDocument, offset);
			Lexeme<HTMLTokenType> lexeme = lexemeProvider.getLexemeFromOffset(offset);

			assertNotNull(lexeme);
			assertEquals(HTMLTokenType.DOUBLE_QUOTED_STRING, lexeme.getType());
			assertEquals(17, lexeme.getStartingOffset());
			assertEquals(63, lexeme.getEndingOffset());
		}
	}

	private void setupTestContext(String resource) throws IOException
	{
		String source = IOUtil.read(FileLocator.openStream(HTMLPlugin.getDefault().getBundle(),
				Path.fromPortableString(resource), false));
		setupDocument(source);
	}

	@Test
	public void testSingleQuotedEventAttributeValueType() throws Exception
	{
		setupTestContext("contentAssist/js-event-attribute-single-quoted.html");

		for (int offset : cursorOffsets)
		{
			HTMLLexemeProvider lexemeProvider = fProcessor.createLexemeProvider(fDocument, offset);
			Lexeme<HTMLTokenType> lexeme = lexemeProvider.getLexemeFromOffset(offset);

			assertNotNull(lexeme);
			assertEquals(HTMLTokenType.SINGLE_QUOTED_STRING, lexeme.getType());
			assertEquals(17, lexeme.getStartingOffset());
			assertEquals(63, lexeme.getEndingOffset());
		}
	}

	@Test
	public void testDoubleQuotedStyleAttributeValueType() throws Exception
	{
		setupTestContext("contentAssist/css-style-attribute-double-quoted.html");

		for (int offset : cursorOffsets)
		{
			HTMLLexemeProvider lexemeProvider = fProcessor.createLexemeProvider(fDocument, offset);
			Lexeme<HTMLTokenType> lexeme = lexemeProvider.getLexemeFromOffset(offset);

			assertNotNull(lexeme);
			assertEquals(HTMLTokenType.DOUBLE_QUOTED_STRING, lexeme.getType());
			assertEquals(12, lexeme.getStartingOffset());
			assertEquals(29, lexeme.getEndingOffset());
		}
	}

	@Test
	public void testSingleQuotedStyleAttributeValueType() throws Exception
	{
		setupTestContext("contentAssist/css-style-attribute-single-quoted.html");

		for (int offset : cursorOffsets)
		{
			HTMLLexemeProvider lexemeProvider = fProcessor.createLexemeProvider(fDocument, offset);
			Lexeme<HTMLTokenType> lexeme = lexemeProvider.getLexemeFromOffset(offset);

			assertNotNull(lexeme);
			assertEquals(HTMLTokenType.SINGLE_QUOTED_STRING, lexeme.getType());
			assertEquals(12, lexeme.getStartingOffset());
			assertEquals(29, lexeme.getEndingOffset());
		}
	}

	private void handleCursorOffsets(IDocument document)
	{
		String source = document.get();
		ArrayList<Integer> cursorOffsets = new ArrayList<Integer>();
		int offset = source.indexOf('|');

		while (offset != -1)
		{
			// NOTE: we have to account for the deletion of previous offsets
			cursorOffsets.add(offset - cursorOffsets.size());
			offset = source.indexOf('|', offset + 1);
		}

		if (cursorOffsets.isEmpty())
		{
			// use last position if we didn't find any cursors
			cursorOffsets.add(source.length());
		}
		else
		{
			// clean source
			source = CURSOR.matcher(source).replaceAll(StringUtil.EMPTY);

			// update document
			document.set(source);
		}
		cursorOffsets.trimToSize();
		this.cursorOffsets = cursorOffsets;
	}

	private static final Pattern CURSOR = Pattern.compile("\\|");
}
