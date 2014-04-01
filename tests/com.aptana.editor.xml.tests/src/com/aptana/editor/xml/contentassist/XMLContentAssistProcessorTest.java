/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.contentassist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.swt.graphics.Point;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.ExtendedFastPartitioner;
import com.aptana.editor.common.IExtendedPartitioner;
import com.aptana.editor.common.NullPartitionerSwitchStrategy;
import com.aptana.editor.common.tests.BadDocument;
import com.aptana.editor.common.tests.util.AssertUtil;
import com.aptana.editor.common.text.rules.CompositePartitionScanner;
import com.aptana.editor.common.text.rules.NullSubPartitionScanner;
import com.aptana.editor.xml.XMLSourceConfiguration;
import com.aptana.editor.xml.tests.XMLEditorBasedTests;
import com.aptana.editor.xml.tests.XMLTestUtil;
import com.aptana.xml.core.index.XMLIndexQueryHelper;
import com.aptana.xml.core.model.AttributeElement;
import com.aptana.xml.core.model.ElementElement;

public class XMLContentAssistProcessorTest extends XMLEditorBasedTests
{

	private IDocument fDocument;
	private List<ElementElement> fElements;

	@Before
	public void setup()
	{
		ElementElement ee = new ElementElement();
		ee.setName("element");
		ee.addAttribute("id");
		ee.addAttribute("class");
		ee.addAttribute("attribute");
		fElements = CollectionsUtil.newList(ee);
	}

	@Override
	protected XMLContentAssistProcessor createContentAssistProcessor(AbstractThemeableEditor editor)
	{
		return new XMLContentAssistProcessor(editor)
		{
			@Override
			protected XMLIndexQueryHelper createQueryHelper()
			{
				return new XMLIndexQueryHelper()
				{
					@Override
					public List<ElementElement> getElements()
					{
						return fElements;
					}

					@Override
					public ElementElement getElement(String elementName)
					{
						return getElements().get(0);
					}

					@Override
					public AttributeElement getAttribute(String elementName, String attributeName)
					{
						AttributeElement attr = new AttributeElement();
						attr.setElement(elementName);
						attr.setName(attributeName);
						return attr;
					}
				};
			}
		};
	}

	@Override
	public void tearDown() throws Exception
	{
		fDocument = null;
		fElements = null;

		super.tearDown();
	}

	@Test
	public void testEmptyDocumentYieldsNoProposals()
	{
		assertCompletionCorrect("|", '\t', 0, null, StringUtil.EMPTY, null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testElementProposalTagUnclosedNoPrefix()
	{
		assertCompletionCorrect("<|", '\t', 1, "element", "<element></element>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testNoDuplicateElementsProposed()
	{
		// Add another element with same name
		ElementElement ee = new ElementElement();
		ee.setName("element");
		ee.addAttribute("something");
		ee.addAttribute("other");
		ee.addAttribute("yeah");
		fElements.add(ee);

		// Verify only one proposal returned since we enforce uniques
		assertCompletionCorrect("<|", '\t', 1, "element", "<element></element>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testElementProposalTagClosedNoPrefix()
	{
		assertCompletionCorrect("<|>", '\t', 1, "element", "<element></element>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testElementProposalTagUnclosedWithPrefix()
	{
		assertCompletionCorrect("<e|", '\t', 1, "element", "<element></element>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testElementProposalTagClosedWithPrefix()
	{
		assertCompletionCorrect("<e|>", '\t', 1, "element", "<element></element>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testElementWhichIsClosedProposal()
	{
		assertCompletionCorrect("<|></element>", '\t', 1, "element", "<element></element>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testNoSuggestionsInTextAreaBetweenTags()
	{
		assertCompletionCorrect("<element>|</element>", '\t', 0, null, "<element></element>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testNoSuggestionsInTextAreaWithWhitespaceBetweenTags()
	{
		assertCompletionCorrect("<element>\n  |\n</element>", '\t', 0, null, "<element>\n  \n</element>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testProposalsBadLocation()
	{
		String document = "<body>&|";
		int offset = XMLTestUtil.findCursorOffset(document);
		fDocument = XMLTestUtil.createBadDocument(document, true);
		ITextViewer viewer = createTextViewer(fDocument);

		// offset is outside document size
		((BadDocument) fDocument).setThrowBadLocation(true);
		getProcessor().doComputeCompletionProposals(viewer, offset, '\t', false);
	}

	private synchronized XMLContentAssistProcessor getProcessor()
	{
		if (processor == null)
		{
			processor = createContentAssistProcessor(null);
		}
		return processor;
	}

	@Test
	public void testAttributeProposalWithNoPrefix()
	{
		assertCompletionCorrect(
				"<element |></element>", '\t', 3, "attribute", "<element attribute=\"\"></element>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testAttributeProposalWithTrailingSpaceAfterCursor()
	{
		assertCompletionCorrect("<element | />", '\t', 3, "attribute", "<element attribute=\"\" />", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testAttributeProposalWithPrefix()
	{
		// FIXME Here we "expect" 3 proposals, but really only one is valid
		assertCompletionCorrect(
				"<element att|></element>", '\t', 3, "attribute", "<element attribute=\"\"></element>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testAttributeProposalWithPrefixAndTrailingEquals()
	{
		// FIXME Here we "expect" 3 proposals, but really only one is valid
		assertCompletionCorrect(
				"<element att|=\"\"></element>", '\t', 3, "attribute", "<element attribute=\"\"></element>", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testAttributeProposalHasExitTabstopAfterQuotes()
	{
		assertCompletionCorrect("<element |>", '\t', 3, "attribute", "<element attribute=\"\">", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// Now test that we have two tabstops. One inside the quotes, one just after
		LinkedModeModel model = LinkedModeModel.getModel(document, 20);
		assertNotNull(model);
		List list = model.getTabStopSequence();
		assertNotNull(list);
		assertEquals(2, list.size());
		Position pos = (Position) list.get(0);
		assertEquals(20, pos.getOffset());
		assertEquals(0, pos.getLength());

		pos = (Position) list.get(1);
		assertEquals(21, pos.getOffset());
		assertEquals(0, pos.getLength());
	}

	@Test
	public void testAttributeNameAtSpace()
	{
		String document = "<element | align=\"\"></p>";
		int offset = XMLTestUtil.findCursorOffset(document);
		fDocument = XMLTestUtil.createDocument(document, true);
		ITextViewer viewer = createTextViewer(fDocument);

		ICompletionProposal[] proposals = getProcessor().doComputeCompletionProposals(viewer, offset, '\t', false);
		assertTrue(proposals.length > 0);
		AssertUtil.assertProposalFound("attribute", proposals);
	}

	@Test
	public void testAttributeNameAtSpace2()
	{
		String document = "<element align=\"\" | ></p>";
		int offset = XMLTestUtil.findCursorOffset(document);
		fDocument = XMLTestUtil.createDocument(document, true);
		ITextViewer viewer = createTextViewer(fDocument);

		ICompletionProposal[] proposals = getProcessor().doComputeCompletionProposals(viewer, offset, '\t', false);
		assertTrue(proposals.length > 0);
		AssertUtil.assertProposalFound("attribute", proposals);
	}

	@Test
	public void testAttributeAfterElementName()
	{
		String document = "<element a|></body>";
		int offset = XMLTestUtil.findCursorOffset(document);
		fDocument = XMLTestUtil.createDocument(document, true);
		ITextViewer viewer = createTextViewer(fDocument);

		ICompletionProposal[] proposals = getProcessor().doComputeCompletionProposals(viewer, offset, '\t', false);
		assertTrue(proposals.length > 0);
		AssertUtil.assertProposalFound("attribute", proposals);
	}

	@Test
	public void testAttributeValueProposalsBeforeEquals()
	{
		String document = "<li><element clas|s=</li>";
		int offset = XMLTestUtil.findCursorOffset(document);
		fDocument = XMLTestUtil.createDocument(document, true);
		ITextViewer viewer = createTextViewer(fDocument);

		ICompletionProposal[] proposals = getProcessor().doComputeCompletionProposals(viewer, offset, '\t', false);
		assertTrue(proposals.length > 0);
	}

	// @Test public void testIsValidAutoActivationLocationElement()
	// {
	// String source = "<e|>";
	//
	// IFileStore fileStore = createFileStore("proposal_tests", "html", source);
	// this.setupTestContext(fileStore);
	// int offset = this.cursorOffsets.get(0);
	//
	// // space, beginning to type attribute name
	// assertTrue("Should have auto-popped CA when hitting space after tag name",
	// processor.isValidAutoActivationLocation(' ', ' ', document, offset));
	// assertTrue("Should have auto-popped CA when hitting space after tag name",
	// processor.isValidAutoActivationLocation('\t', '\t', document, offset));
	// // continue typing element name
	// assertFalse("Shouldn't have auto-popped CA typing second or later character in tag name",
	// processor.isValidAutoActivationLocation('l', 'l', document, offset));
	// }

	// @Test public void testIsValidAutoActivationLocationAttribute()
	// {
	// String source = "<element |>";
	// IFileStore fileStore = createFileStore("proposal_tests", "html", source);
	// this.setupTestContext(fileStore);
	// int offset = this.cursorOffsets.get(0);
	//
	// // starting to type an attribute name
	// assertTrue("Auto-pop CA when typing first character of attribute name",
	// processor.isValidAutoActivationLocation('a', 'a', document, offset));
	//
	// }

	// @Test public void testIsValidAutoActivationLocationAttributeValue()
	// {
	// String source = "<a class=\"|\"|>";
	// IFileStore fileStore = createFileStore("proposal_tests", "html", source);
	// this.setupTestContext(fileStore);
	// int offset = this.cursorOffsets.get(0);
	//
	// // starting to type an attribute value
	// assertTrue(processor.isValidAutoActivationLocation('f', 'f', document, offset));
	// }

	@Test
	public void testIsValidAutoActivationLocationText()
	{
		String source = "<element>|";

		document = new Document(source);
		handleCursorOffsets(document);

		attachPartitioner(document);

		int offset = this.cursorOffsets.get(0);

		assertFalse("Don't auto-pop CA when typing text between tags", createContentAssistProcessor(null)
				.isValidAutoActivationLocation('t', 't', document, offset));
	}

	protected ITextViewer createTextViewer(IDocument fDocument)
	{
		return new com.aptana.editor.common.tests.TextViewer(fDocument);
	}

	protected void assertCompletionCorrect(String source, char trigger, int proposalCount, String proposalToChoose,
			String postCompletion, Point point)
	{
		document = new Document(source);
		handleCursorOffsets(document);

		attachPartitioner(document);

		int offset = this.cursorOffsets.get(0);

		ITextViewer viewer = createTextViewer(document);
		ICompletionProposal[] proposals = createContentAssistProcessor(null).doComputeCompletionProposals(viewer,
				offset, trigger, false);

		assertEquals("Received proposal count we didn't expect: " + StringUtil.join(",", proposals), proposalCount,
				proposals.length);
		if (proposalToChoose != null)
		{
			AssertUtil.assertProposalFound(proposalToChoose, proposals);
			AssertUtil.assertProposalApplies(postCompletion, document, proposalToChoose, proposals, offset, point);
		}
	}

	protected void attachPartitioner(IDocument document)
	{
		CompositePartitionScanner partitionScanner = new CompositePartitionScanner(XMLSourceConfiguration.getDefault()
				.createSubPartitionScanner(), new NullSubPartitionScanner(), new NullPartitionerSwitchStrategy());
		IDocumentPartitioner partitioner = new ExtendedFastPartitioner(partitionScanner, XMLSourceConfiguration
				.getDefault().getContentTypes());
		partitionScanner.setPartitioner((IExtendedPartitioner) partitioner);
		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
		CommonEditorPlugin.getDefault().getDocumentScopeManager()
				.registerConfiguration(document, XMLSourceConfiguration.getDefault());
	}
}
