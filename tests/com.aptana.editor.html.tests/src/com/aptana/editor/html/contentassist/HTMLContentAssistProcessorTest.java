/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

import com.aptana.core.util.FileUtil;
import com.aptana.editor.common.contentassist.LexemeProvider;
import com.aptana.editor.html.HTMLMetadataLoader;
import com.aptana.editor.html.HTMLTestUtil;
import com.aptana.editor.html.contentassist.HTMLContentAssistProcessor.LocationType;
import com.aptana.editor.html.parsing.lexer.HTMLTokenType;

public class HTMLContentAssistProcessorTest extends LocationTestCase
{

	private static final int ELEMENT_PROPOSALS_COUNT = 132;
	private static final int DOCTYPE_PROPOSALS_COUNT = 11;
	private static final int CLOSE_TAG_PROPOSALS_COUNT = 119;
	private static final int ENTITY_PROPOSAL_COUNT = 252;

	private HTMLContentAssistProcessor fProcessor;
	private IDocument fDocument;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		HTMLMetadataLoader loader = new HTMLMetadataLoader();
		loader.schedule();
		loader.join();

		fProcessor = new HTMLContentAssistProcessor(null);
	}

	@Override
	protected void tearDown() throws Exception
	{
		fProcessor = null;
		fDocument = null;
		super.tearDown();
	}

	public void testLinkProposal()
	{
		assertCompletionCorrect("<|", '\t', ELEMENT_PROPOSALS_COUNT, "a", "<a></a>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testDOCTYPEProposal1()
	{
		assertCompletionCorrect("<!|", '\t', ELEMENT_PROPOSALS_COUNT, "!DOCTYPE", "<!DOCTYPE >", new Point(10, 0)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testDOCTYPEProposal2()
	{
		assertCompletionCorrect("<!D|", '\t', ELEMENT_PROPOSALS_COUNT, "!DOCTYPE", "<!DOCTYPE >", new Point(10, 0)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testDOCTYPEProposal3()
	{
		assertCompletionCorrect(
				"<!D| html>", '\t', ELEMENT_PROPOSALS_COUNT, "!DOCTYPE", "<!DOCTYPE html>", new Point(10, 0)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testDOCTYPEProposal4()
	{
		assertCompletionCorrect("<|>", '\t', ELEMENT_PROPOSALS_COUNT, "!DOCTYPE", "<!DOCTYPE >", new Point(10, 0)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testDOCTYPEProposal5()
	{
		assertCompletionCorrect("<!|>", '\t', ELEMENT_PROPOSALS_COUNT, "!DOCTYPE", "<!DOCTYPE >", new Point(10, 0)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testDOCTYPEProposal6()
	{
		assertCompletionCorrect(
				"<!D|OCTYP >", '\t', ELEMENT_PROPOSALS_COUNT, "!DOCTYPE", "<!DOCTYPE >", new Point(10, 0)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testDOCTYPEProposal7()
	{
		assertCompletionCorrect(
				"<!D|OCTYPE >", '\t', ELEMENT_PROPOSALS_COUNT, "!DOCTYPE", "<!DOCTYPE >", new Point(10, 0)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testDOCTYPEValueReplacement1()
	{
		assertCompletionCorrect("<!DOCTYPE |html>", '\t', DOCTYPE_PROPOSALS_COUNT, "HTML 5", "<!DOCTYPE HTML>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testDOCTYPEValueReplacement2()
	{
		assertCompletionCorrect(
				"<!DOCTYPE |html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\"\n	\"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">", //$NON-NLS-1$
				'\t', DOCTYPE_PROPOSALS_COUNT, "HTML 5", "<!DOCTYPE HTML>", null); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testCloseTagProposal1()
	{
		ITextViewer viewer = createTextViewer(createDocument("<a>Test <b>Item</b>", false)); //$NON-NLS-1$

		// Should be no unclosed tags at this point
		fProcessor.doComputeCompletionProposals(viewer, 0, '\t', false);
		assertEquals(0, fProcessor.getUnclosedTagNames(0).size());
	}

	public void testCloseTagProposal2()
	{
		ITextViewer viewer = createTextViewer(createDocument("<a>Test <b>Item</b>", false)); //$NON-NLS-1$

		fProcessor.doComputeCompletionProposals(viewer, 1, '\t', false);
		assertEquals(0, fProcessor.getUnclosedTagNames(1).size());
	}

	public void testCloseTagProposal3()
	{
		ITextViewer viewer = createTextViewer(createDocument("<a>Test <b>Item</b>", false)); //$NON-NLS-1$

		fProcessor.doComputeCompletionProposals(viewer, 2, '\t', false);
		assertEquals(0, fProcessor.getUnclosedTagNames(2).size());
	}

	public void testCloseTagProposal4()
	{
		ITextViewer viewer = createTextViewer(createDocument("<a>Test <b>Item</b>", false)); //$NON-NLS-1$

		fProcessor.doComputeCompletionProposals(viewer, 3, '\t', false);
		assertEquals(1, fProcessor.getUnclosedTagNames(3).size());
	}

	public void testCloseTagProposal5()
	{
		ITextViewer viewer = createTextViewer(createDocument("<a>Test <b>Item</b>", false)); //$NON-NLS-1$
		// show unclosed tag once we get past the '>'
		fProcessor.doComputeCompletionProposals(viewer, 4, '\t', false);
		assertEquals(1, fProcessor.getUnclosedTagNames(4).size());
	}

	public void testABBRProposal1()
	{
		assertCompletionCorrect("<a|>", '\t', ELEMENT_PROPOSALS_COUNT, "abbr", "<abbr></abbr>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testABBRProposal2()
	{
		assertCompletionCorrect("<A|>", '\t', ELEMENT_PROPOSALS_COUNT, "abbr", "<abbr></abbr>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testElementWhichIsClosedProposal()
	{
		assertCompletionCorrect("<|></a>", '\t', ELEMENT_PROPOSALS_COUNT, "a", "<a></a>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testElementWhichIsClosedProposal2()
	{
		assertCompletionCorrect("<|></a>", '\t', ELEMENT_PROPOSALS_COUNT, "abbr", "<abbr></abbr></a>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testElementWhichIsClosedProposal3()
	{
		assertCompletionCorrect("<|</a>", '\t', ELEMENT_PROPOSALS_COUNT, "abbr", "<abbr></abbr></a>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testElementWhichIsClosedProposal4()
	{
		assertCompletionCorrect("<b><a><|</b>", '\t', ELEMENT_PROPOSALS_COUNT + 1, "/a", "<b><a></a></b>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testElementWhichIsClosedProposal5()
	{
		assertCompletionCorrect("<b><a><|></b>", '\t', ELEMENT_PROPOSALS_COUNT + 1, "/a", "<b><a></a></b>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testElementWhichIsClosedProposal6()
	{
		assertCompletionCorrect("<b><a></|</b>", '\t', 1, "/a", "<b><a></a></b>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testSuggestOnlyUnclosedTagForCloseTagWithNoElementName()
	{
		assertCompletionCorrect("<b><a></|></b>", '\t', 1, "/a", "<b><a></a></b>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testSuggestOnlyUnclosedTagForCloseTagWithElementName()
	{
		assertCompletionCorrect("<b><a></a|</b>", '\t', 1, "/a", "<b><a></a></b>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testSuggestAllPossibleCloseTagsOnExistingCloseTagRegardlessOfPrefix()
	{
		assertCompletionCorrect("<b><a></a|></b>", '\t', CLOSE_TAG_PROPOSALS_COUNT, "/a", "<b><a></a></b>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testCloseTagWithNoUnclosedTagsProposal()
	{
		assertCompletionCorrect("</|>", '\t', CLOSE_TAG_PROPOSALS_COUNT, "/ul", "</ul>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testNoSuggestionsInTextAreaBetweenTags()
	{
		assertCompletionCorrect("<p>|</p>", '\t', 0, null, "<p></p>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testNoSuggestionsInTextAreaWithWhitespaceBetweenTags()
	{
		assertCompletionCorrect("<p>\n  |\n</p>", '\t', 0, null, "<p>\n  \n</p>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testOnlySuggestEntityIfPrecededByAmpersand()
	{
		assertCompletionCorrect("<p>\n  &|\n</p>", '\t', ENTITY_PROPOSAL_COUNT, "&amp;", "<p>\n  &amp;\n</p>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testEntitySuggestionWithNoSurroundingWhitespace()
	{
		assertCompletionCorrect("<div>&|</div>", '\t', ENTITY_PROPOSAL_COUNT, "&amp;", "<div>&amp;</div>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testExistingEntityGetsFullyReplaced1()
	{
		assertCompletionCorrect(
				"<body>\n  &a|acute;\n</body>", '\t', ENTITY_PROPOSAL_COUNT, "&acirc;", "<body>\n  &acirc;\n</body>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testExistingEntityGetsFullyReplaced2()
	{
		assertCompletionCorrect("<div>&a|acute;</div>", '\t', ENTITY_PROPOSAL_COUNT, "&amp;", "<div>&amp;</div>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testIMGProposal()
	{
		assertCompletionCorrect("<|", '\t', ELEMENT_PROPOSALS_COUNT, "img", "<img />", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testHTML5DoctypeProposal()
	{
		assertCompletionCorrect("<!doctype |>", '\t', DOCTYPE_PROPOSALS_COUNT, "HTML 5", "<!doctype HTML>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testHTML401StrictDoctypeProposal()
	{
		assertCompletionCorrect("<!DOCTYPE |", '\t', DOCTYPE_PROPOSALS_COUNT, "HTML 4.01 Strict", //$NON-NLS-1$ //$NON-NLS-2$
				"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\"\n\"http://www.w3.org/TR/html4/strict.dtd\"", null); //$NON-NLS-1$
	}

	public void testStyleAttributeProposalWithNoPrefix()
	{
		assertCompletionCorrect("<div |></div>", '\t', 64, "style", "<div style=\"\"></div>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testStyleAttributeProposalOnSelfClosingTag()
	{
		assertCompletionCorrect("<br |/>", '\t', 8, "style", "<br style=\"\"/>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testStyleAttributeProposalOnSelfClosingTagWithTrailingSpaceAfterCursor()
	{
		assertCompletionCorrect("<br | />", '\t', 8, "style", "<br style=\"\" />", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testStyleAttributeProposalWithPrefix()
	{
		assertCompletionCorrect("<div sty|></div>", '\t', 64, "style", "<div style=\"\"></div>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testStyleAttributeProposalWithPrefixAndTrailingEquals()
	{
		assertCompletionCorrect("<div sty|=\"\"></div>", '\t', 64, "style", "<div style=\"\"></div>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testDontOverwriteTagEnd1()
	{
		assertCompletionCorrect("<div dir=\"|></div>", '\t', 2, "ltr", "<div dir=\"ltr></div>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void testDontOverwriteTagEnd2()
	{
		assertCompletionCorrect("<br dir=\"|/>", '\t', 2, "ltr", "<br dir=\"ltr/>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@SuppressWarnings("rawtypes")
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
	public void testIMGSrcContentAssistDoesntAutoInsertCloseTag()
	{
		String document = "<div><img src='|' />";
		int offset = HTMLTestUtil.findCursorOffset(document);
		fDocument = HTMLTestUtil.createDocument(document, true);
		ITextViewer viewer = createTextViewer(fDocument);

		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, '\t', false);
		assertEquals(0, proposals.length);
	}

	// TODO Add tests for src/href folder/filepath proposals
	public void testLinkHREFFolderProposal() throws Exception
	{
		String document = "<link rel=\"stylesheet\" href=\"|\" />";
		int offset = HTMLTestUtil.findCursorOffset(document);
		fDocument = HTMLTestUtil.createDocument(document, true);
		ITextViewer viewer = createTextViewer(fDocument);

		// Generate some folders/files to use as proposals
		String tmpDir = System.getProperty("java.io.tmpdir");
		final File dir = new File(tmpDir, "testLinkHREFFolderProposal" + System.currentTimeMillis());
		dir.mkdirs();

		File folder = new File(dir, "folder");
		folder.mkdirs();

		File underFolder = null;
		File rootFile = null;
		try
		{
			underFolder = new File(folder, "inside_folder.css");
			underFolder.createNewFile();

			rootFile = new File(dir, "root.css");
			rootFile.createNewFile();

			fProcessor = new HTMLContentAssistProcessor(null)
			{
				@Override
				protected URI getURI()
				{
					return new File(dir, "file.html").toURI();
				}
			};

			ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, '\t', false);
			assertEquals(2, proposals.length);
			assertNotNull(findProposal("folder/", proposals));
			assertNotNull(findProposal("root.css", proposals));
			assertApplyProposal(viewer, proposals, "folder/", offset);
			assertEquals("<link rel=\"stylesheet\" href=\"folder/\" />", fDocument.get());
		}
		finally
		{
			FileUtil.deleteRecursively(dir);
		}
	}

	protected void assertApplyProposal(ITextViewer viewer, ICompletionProposal[] proposals, String proposalToSelect,
			int offset)
	{
		ICompletionProposal closeProposal = findProposal(proposalToSelect, proposals);
		assertNotNull("Unable to find proposal you wanted to select: " + proposalToSelect, closeProposal);
		assertTrue("Selected proposal doesn't validate against document",
				((ICompletionProposalExtension2) closeProposal).validate(fDocument, offset, null));
		((ICompletionProposalExtension2) closeProposal).apply(viewer, '\t', SWT.NONE, offset);
	}

	public void testLinkHREFFileProposal() throws Exception
	{
		String document = "<link rel=\"stylesheet\" href=\"|\" />";
		int offset = HTMLTestUtil.findCursorOffset(document);
		fDocument = HTMLTestUtil.createDocument(document, true);
		ITextViewer viewer = createTextViewer(fDocument);

		// Generate some folders/files to use as proposals
		String tmpDir = System.getProperty("java.io.tmpdir");
		final File dir = new File(tmpDir, "testLinkHREFFileProposal" + System.currentTimeMillis());
		dir.mkdirs();

		File folder = new File(dir, "folder");
		folder.mkdirs();

		File underFolder = null;
		File rootFile = null;
		try
		{
			underFolder = new File(folder, "inside_folder.css");
			underFolder.createNewFile();

			rootFile = new File(dir, "root.css");
			rootFile.createNewFile();

			fProcessor = new HTMLContentAssistProcessor(null)
			{
				@Override
				protected URI getURI()
				{
					return new File(dir, "file.html").toURI();
				}
			};

			ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, '\t', false);
			assertEquals(2, proposals.length);
			assertNotNull(findProposal("folder/", proposals));
			assertNotNull(findProposal("root.css", proposals));
			assertApplyProposal(viewer, proposals, "root.css", offset);
			assertEquals("<link rel=\"stylesheet\" href=\"root.css\" />", fDocument.get());
		}
		finally
		{
			FileUtil.deleteRecursively(dir);
		}
	}

	public void testLinkHREFFileUnderFolderProposal() throws Exception
	{
		String document = "<link rel=\"stylesheet\" href=\"folder/|\" />";
		int offset = HTMLTestUtil.findCursorOffset(document);
		fDocument = HTMLTestUtil.createDocument(document, true);
		ITextViewer viewer = createTextViewer(fDocument);

		// Generate some folders/files to use as proposals
		String tmpDir = System.getProperty("java.io.tmpdir");
		final File dir = new File(tmpDir, "testLinkHREFFileUnderFolderProposal" + System.currentTimeMillis());
		dir.mkdirs();

		File folder = new File(dir, "folder");
		folder.mkdirs();

		File underFolder = null;
		File rootFile = null;
		try
		{
			underFolder = new File(folder, "inside_folder.css");
			underFolder.createNewFile();

			rootFile = new File(dir, "root.css");
			rootFile.createNewFile();

			fProcessor = new HTMLContentAssistProcessor(null)
			{
				@Override
				protected URI getURI()
				{
					return new File(dir, "file.html").toURI();
				}
			};

			ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, '\t', false);
			assertEquals(1, proposals.length);
			assertNotNull(findProposal("inside_folder.css", proposals));
			assertApplyProposal(viewer, proposals, "inside_folder.css", offset);
			assertEquals("<link rel=\"stylesheet\" href=\"folder/inside_folder.css\" />", fDocument.get());
		}
		finally
		{
			FileUtil.deleteRecursively(dir);
		}
	}

	public void testLinkHREFFileProposalWithPrefix() throws Exception
	{
		String document = "<link rel=\"stylesheet\" href=\"roo|\" />";
		int offset = HTMLTestUtil.findCursorOffset(document);
		fDocument = HTMLTestUtil.createDocument(document, true);
		ITextViewer viewer = createTextViewer(fDocument);

		// Generate some folders/files to use as proposals
		String tmpDir = System.getProperty("java.io.tmpdir");
		final File dir = new File(tmpDir, "testLinkHREFFileProposalWithPrefix" + System.currentTimeMillis());
		dir.mkdirs();

		File folder = new File(dir, "folder");
		folder.mkdirs();

		File underFolder = null;
		File rootFile = null;
		try
		{
			underFolder = new File(folder, "inside_folder.css");
			underFolder.createNewFile();

			rootFile = new File(dir, "root.css");
			rootFile.createNewFile();

			fProcessor = new HTMLContentAssistProcessor(null)
			{
				@Override
				protected URI getURI()
				{
					return new File(dir, "file.html").toURI();
				}
			};

			ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, '\t', false);
			assertEquals(1, proposals.length);
			assertNotNull(findProposal("root.css", proposals));
			assertApplyProposal(viewer, proposals, "root.css", offset);
			assertEquals("<link rel=\"stylesheet\" href=\"root.css\" />", fDocument.get());
		}
		finally
		{
			FileUtil.deleteRecursively(dir);
		}
	}

	public void testLinkHREFFolderProposalWithPrefix() throws Exception
	{
		String document = "<link rel=\"stylesheet\" href=\"fo|\" />";
		int offset = HTMLTestUtil.findCursorOffset(document);
		fDocument = HTMLTestUtil.createDocument(document, true);
		ITextViewer viewer = createTextViewer(fDocument);

		// Generate some folders/files to use as proposals
		String tmpDir = System.getProperty("java.io.tmpdir");
		final File dir = new File(tmpDir, "testLinkHREFFolderProposalWithPrefix" + System.currentTimeMillis());
		dir.mkdirs();

		File folder = new File(dir, "folder");
		folder.mkdirs();

		File underFolder = null;
		File rootFile = null;
		try
		{
			underFolder = new File(folder, "inside_folder.css");
			underFolder.createNewFile();

			rootFile = new File(dir, "root.css");
			rootFile.createNewFile();

			fProcessor = new HTMLContentAssistProcessor(null)
			{
				@Override
				protected URI getURI()
				{
					return new File(dir, "file.html").toURI();
				}
			};

			ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, '\t', false);
			assertEquals(1, proposals.length);
			assertNotNull(findProposal("folder/", proposals));
			assertApplyProposal(viewer, proposals, "folder/", offset);
			assertEquals("<link rel=\"stylesheet\" href=\"folder/\" />", fDocument.get());
		}
		finally
		{
			FileUtil.deleteRecursively(dir);
		}
	}

	public void testLinkHREFFileInsideFolderProposalWithPrefix() throws Exception
	{
		String document = "<link rel=\"stylesheet\" href=\"folder/in|\" />";
		int offset = HTMLTestUtil.findCursorOffset(document);
		fDocument = HTMLTestUtil.createDocument(document, true);
		ITextViewer viewer = createTextViewer(fDocument);

		// Generate some folders/files to use as proposals
		String tmpDir = System.getProperty("java.io.tmpdir");
		final File dir = new File(tmpDir, "testLinkHREFFileInsideFolderProposalWithPrefix" + System.currentTimeMillis());
		dir.mkdirs();

		File folder = new File(dir, "folder");
		folder.mkdirs();

		File underFolder = null;
		File rootFile = null;
		try
		{
			underFolder = new File(folder, "inside_folder.css");
			underFolder.createNewFile();

			rootFile = new File(dir, "root.css");
			rootFile.createNewFile();

			fProcessor = new HTMLContentAssistProcessor(null)
			{
				@Override
				protected URI getURI()
				{
					return new File(dir, "file.html").toURI();
				}
			};

			ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, '\t', false);
			assertEquals(1, proposals.length);
			assertNotNull(findProposal("inside_folder.css", proposals));
			assertApplyProposal(viewer, proposals, "inside_folder.css", offset);
			assertEquals("<link rel=\"stylesheet\" href=\"folder/inside_folder.css\" />", fDocument.get());
		}
		finally
		{
			FileUtil.deleteRecursively(dir);
		}
	}

	public void testAttributeAfterElementName()
	{
		String document = "<body s|></body>";
		int offset = HTMLTestUtil.findCursorOffset(document);
		fDocument = HTMLTestUtil.createDocument(document, true);
		ITextViewer viewer = createTextViewer(fDocument);

		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, '\t', false);
		assertTrue(proposals.length > 0);
		assertNotNull(findProposal("scroll", proposals));
	}

	private ICompletionProposal findProposal(String string, ICompletionProposal[] proposals)
	{
		for (ICompletionProposal proposal : proposals)
		{
			if (proposal.getDisplayString().equals(string))
			{
				return proposal;
			}
		}
		return null;
	}

	protected ITextViewer createTextViewer(IDocument fDocument)
	{
		ITextViewer viewer = new TextViewer(new Shell(), SWT.NONE);
		viewer.setDocument(fDocument);
		return viewer;
	}

	protected void assertLocation(String document, LocationType location)
	{
		int offset = HTMLTestUtil.findCursorOffset(document);
		fDocument = HTMLTestUtil.createDocument(document, true);

		LexemeProvider<HTMLTokenType> lexemeProvider = HTMLTestUtil.createLexemeProvider(fDocument, offset);
		LocationType l = fProcessor.getOpenTagLocationType(lexemeProvider, offset);

		assertEquals(location, l);
	}

	protected void assertCompletionCorrect(String document, char trigger, int proposalCount, String proposalToSelect,
			String postCompletion, Point point)
	{
		int offset = HTMLTestUtil.findCursorOffset(document);
		fDocument = HTMLTestUtil.createDocument(document, true);
		ITextViewer viewer = createTextViewer(fDocument);

		ICompletionProposal[] proposals = fProcessor.doComputeCompletionProposals(viewer, offset, trigger, false);
		assertEquals(proposalCount, proposals.length);

		if (proposalToSelect != null)
		{
			ICompletionProposal closeProposal = findProposal(proposalToSelect, proposals);
			assertNotNull("Unable to find proposal you wanted to select: " + proposalToSelect, closeProposal);
			assertTrue("Selected proposal doesn't validate against document",
					((ICompletionProposalExtension2) closeProposal).validate(fDocument, offset, null));
			((ICompletionProposalExtension2) closeProposal).apply(viewer, trigger, SWT.NONE, offset);
		}
		assertEquals(postCompletion, fDocument.get());

		if (point != null)
		{
			Point p = viewer.getSelectedRange();
			assertEquals(point.x, p.x);
			assertEquals(point.y, p.y);
		}
	}
}
