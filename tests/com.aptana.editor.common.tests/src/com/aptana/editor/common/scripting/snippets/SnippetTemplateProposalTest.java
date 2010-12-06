/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.scripting.snippets;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

import com.aptana.scripting.model.SnippetElement;

public class SnippetTemplateProposalTest extends TestCase
{

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
		p.apply(viewer, '\t', 0, 9);

		// Now make sure the snippet got applied correctly
		assertEquals("<div>Yahoo!\n", document.get());
		context.assertIsSatisfied();
	}
}
