/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.scripting.snippets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import com.aptana.scripting.model.SnippetElement;
import com.aptana.ui.util.UIUtils;

public class SnippetTemplateProposalTest
{

	@Test
	public void testReplacesCorrectRegionWhenPortionOfFullPrefixMatches()
	{
		Mockery context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
		// Create the snippet we want to apply
		SnippetElement se = new SnippetElement("");
		se.setDisplayName("something");
		se.setExpansion("Yahoo!");
		SnippetTemplate template = new SnippetTemplate(se, "echo", "whatever");

		// Set up the document we're operating on
		final IDocument document = new Document("<div>echo\n");
		final ITextViewer viewer = context.mock(ITextViewer.class);

		context.checking(new Expectations()
		{
			{
				oneOf(viewer).getDocument();
				will(returnValue(document));
			}
		});

		// Create snippet proposal, then apply it to the document
		DocumentSnippetTemplateContext tc = new DocumentSnippetTemplateContext(new SnippetTemplateContextType("scope"),
				document, 0, 9);
		SnippetTemplateProposal p = new SnippetTemplateProposal(template, tc, new Region(0, 0), null, 0);

		// Make sure the snippet validates
		DocumentEvent event = new DocumentEvent(document, 9, 0, "");
		assertTrue("Snippet proposal incorrectly failed validation!", p.validate(document, 9, event));

		// Now make sure the snippet got applied correctly
		p.apply(viewer, '\t', 0, 9);
		assertEquals("<div>Yahoo!\n", document.get());
		context.assertIsSatisfied();
	}

	@Test
	public void testTriggerNumberMatches() throws BadLocationException
	{
		Mockery context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};

		// Set up the document we're operating on
		final IDocument document = new Document("echo \n");
		final ITextViewer viewer = new TextViewer(UIUtils.getActiveShell(), 0);
		viewer.setDocument(document);

		final Template[] templates = new Template[3];

		SnippetsCompletionProcessor snippetsCompletionProcessor = new SnippetsCompletionProcessor()
		{
			protected org.eclipse.jface.text.templates.Template[] getTemplates(String contextTypeId)
			{
				String contextId = "com.aptana.contenttype.unknown __dftl_partition_content_type";
				// Create the snippet we want to apply
				SnippetElement se1 = new SnippetElement("");
				se1.setDisplayName("something1");
				se1.setExpansion("Yahoo1!");
				templates[0] = new SnippetTemplate(se1, "echo", contextId);

				SnippetElement se2 = new SnippetElement("");
				se2.setDisplayName("something1");
				se2.setExpansion("Yahoo2!");
				templates[1] = new SnippetTemplate(se2, "echo", contextId);

				SnippetElement se3 = new SnippetElement("");
				se3.setDisplayName("other");
				se3.setExpansion("Bingo!");
				templates[2] = new SnippetTemplate(se3, "ego", contextId);

				return templates;
			};
		};
		ICompletionProposal[] snippets = snippetsCompletionProcessor.computeCompletionProposals(viewer, 1);

		assertEquals("Expected snippets length do not match", 3, snippets.length);
		// Let's check the 1st and 2nd snippets
		for (int i = 0; i < snippets.length - 1; i++)
		{
			assertTrue(snippets[i] instanceof SnippetTemplateProposal);
			StyledString styledActivationString = ((SnippetTemplateProposal) snippets[i]).getStyledActivationString();
			String styleStr = "echo » " + (i + 1);
			assertEquals("Style string of snippets templates do not match", styleStr, styledActivationString
					.getString().trim());
		}

		// Create snippet proposal, then apply it to the document
		DocumentSnippetTemplateContext tc = new DocumentSnippetTemplateContext(new SnippetTemplateContextType("scope"),
				document, 0, 4);
		SnippetTemplateProposal p = new SnippetTemplateProposal(templates[1], tc, new Region(0, 0), null, 0);

		// Make sure the snippet validates
		DocumentEvent event = new DocumentEvent(document, 4, 0, "");
		assertTrue("Snippet proposal incorrectly failed validation!", p.validate(document, 4, event));

		// Now make sure the snippet got applied correctly
		p.apply(viewer, '2', 0, 4);
		assertEquals("Yahoo2! \n", document.get());

		context.assertIsSatisfied();
	}

	@Test
	public void testSnippetLinkedMode()
	{
		// Create the snippet we want to apply
		SnippetElement se = new SnippetElement("/some/fake/path.rb");
		se.setDisplayName("if...");
		se.setExpansion("if (${1:condition}) {\n\t${0:// code...}\n}");
		SnippetTemplate template = new SnippetTemplate(se, "if", "source.php");

		// Set up the document we're operating on
		final IDocument document = new Document("<?php\nif?>");

		// Create snippet proposal, then apply it to the document
		DocumentSnippetTemplateContext tc = new DocumentSnippetTemplateContext(new SnippetTemplateContextType(
				"source.php"), document, 2, 6);
		SnippetTemplateProposal p = new SnippetTemplateProposal(template, tc, new Region(6, 2), null, 0);

		// Make sure the snippet validates
		DocumentEvent event = new DocumentEvent(document, 8, 0, "");
		assertTrue("Snippet proposal incorrectly failed validation!", p.validate(document, 8, event));

		// Now make sure the snippet gets applied correctly
		ITextViewer viewer = new TextViewer(new Shell(), SWT.NONE);
		viewer.setDocument(document);

		p.apply(viewer, '\t', 0, 8);
		p.apply(viewer, '\t', 0, 26);
		assertEquals("<?php\nif (condition) {\n\t\n}?>", document.get());
	}

	@Test
	public void testAPSTUD2445()
	{
		Mockery context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
		// Create the snippet we want to apply
		SnippetElement se = new SnippetElement("/some/fake/path.rb");
		se.setDisplayName(".add");
		se.setExpansion(".add('selector')");
		SnippetTemplate template = new SnippetTemplate(se, ".add", "source.js");

		// Set up the document we're operating on
		final IDocument document = new Document("$(selector).add");
		final ITextViewer viewer = context.mock(ITextViewer.class);

		context.checking(new Expectations()
		{
			{
				oneOf(viewer).getDocument();
				will(returnValue(document));
			}
		});

		// Create snippet proposal, then apply it to the document
		DocumentSnippetTemplateContext tc = new DocumentSnippetTemplateContext(new SnippetTemplateContextType(
				"source.js"), document, 0, 15);
		SnippetTemplateProposal p = new SnippetTemplateProposal(template, tc, new Region(0, 15), null, 0);

		// Make sure the snippet validates
		DocumentEvent event = new DocumentEvent(document, 15, 0, "");
		assertTrue("Snippet proposal incorrectly failed validation!", p.validate(document, 15, event));

		// Now make sure the snippet gets applied correctly
		p.apply(viewer, '\t', 0, 15);
		assertEquals("$(selector).add('selector')", document.get());
		context.assertIsSatisfied();
	}

	@Test
	public void testDoublePrefix()
	{
		Mockery context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
		// Create the snippet we want to apply
		SnippetElement se = new SnippetElement("/some/fake/path.rb");
		se.setDisplayName(".add");
		se.setExpansion(".add('selector')");
		SnippetTemplate template = new SnippetTemplate(se, ".add", "source.js");

		// Set up the document we're operating on
		final IDocument document = new Document("$(selector).add.add");
		final ITextViewer viewer = context.mock(ITextViewer.class);

		context.checking(new Expectations()
		{
			{
				oneOf(viewer).getDocument();
				will(returnValue(document));
			}
		});

		// Create snippet proposal, then apply it to the document
		DocumentSnippetTemplateContext tc = new DocumentSnippetTemplateContext(new SnippetTemplateContextType(
				"source.js"), document, 0, 19);
		SnippetTemplateProposal p = new SnippetTemplateProposal(template, tc, new Region(0, 19), null, 0);

		// FIXME not working yet
		// // Make sure the snippet validates
		// DocumentEvent event = new DocumentEvent(document, 19, 0, "");
		// assertTrue("Snippet proposal incorrectly failed validation!", p.validate(document, 19, event));
		//
		// // Now make sure the snippet gets applied correctly. This is what TM would insert
		// p.apply(viewer, '\t', 0, 19);
		// assertEquals("$(selector).add.add('selector')", document.get());
		// context.assertIsSatisfied();

		// also consider case of two snippets with activation characters b.a and .a. in TM, b.a wins.
	}

	@Test
	public void testIsTriggerEnabled()
	{
		// Create the snippet we want to apply
		SnippetElement se = new SnippetElement("");
		se.setDisplayName("something");
		se.setExpansion("Yahoo!");
		SnippetTemplate template = new SnippetTemplate(se, "echo", "whatever");

		// Set up the document we're operating on
		final IDocument document = new Document("<div>ech\n");

		// Create snippet proposal, then apply it to the document
		DocumentSnippetTemplateContext tc = new DocumentSnippetTemplateContext(new SnippetTemplateContextType("scope"),
				document, 0, 8);
		SnippetTemplateProposal p = new SnippetTemplateProposal(template, tc, new Region(0, 0), null, 0);
		assertFalse(p.validateTrigger(document, 8, null));

		document.set("<div>echoo\n");
		assertFalse(p.validateTrigger(document, 10, null));

		document.set("<div>echo\n");
		assertTrue(p.validateTrigger(document, 9, null));

	}

}
