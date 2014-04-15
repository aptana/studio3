/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.EnumSet;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Point;
import org.junit.Test;

import com.aptana.core.util.FileUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.css.core.parsing.CSSTokenType;
import com.aptana.editor.common.contentassist.ILexemeProvider;
import com.aptana.editor.common.tests.util.AssertUtil;
import com.aptana.editor.css.parsing.CSSTokenScanner;
import com.aptana.editor.css.parsing.lexer.CSSLexemeProvider;
import com.aptana.editor.css.tests.CSSEditorBasedTests;
import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Lexeme;
import com.aptana.parsing.lexer.Range;
import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.SnippetElement;

/**
 * CSSContentAssistProposalTests
 */
public class CSSContentAssistProposalTests extends CSSEditorBasedTests
{

	/*
	 * Universal selector. Matches any element
	 */
	@Test
	public void testUniversalSelectors()
	{
		// Should be in the list
		// assertCompletionCorrect("| {}", '\t', "*", "* {}");
	}

	/*
	 * Type selectors
	 */
	@Test
	public void testTypeSelectors()
	{
		assertCompletionCorrect("p| {}", '\t', "p", "p {}");
	}

	/*
	 * Type selectors
	 */
	@Test
	public void testTypeSelectorsMidElement()
	{
		assertCompletionCorrect("tab|le {}", '\t', "table", "table {}");
	}

	/*
	 * Type selectors
	 */
	@Test
	public void testTypeSelectorsListNoSpace()
	{
		assertCompletionCorrect("p,| {}", '\t', "table", "p,table {}");
	}

	/*
	 * Type selectors
	 */
	@Test
	public void testTypeSelectorsListSpace()
	{
		assertCompletionCorrect("p, | {}", '\t', "table", "p, table {}");
	}

	/*
	 * Type selectors
	 */
	@Test
	public void testTypeSelectorsPreCurlyNoSpace()
	{
		// I think this should test addOutsideRuleProposals.LCURLY, but does not because we reset the current
		// lexeme to null in getCoarseLocationType()
		assertCompletionCorrect("p |{}", '\t', "div", "p div{}");
	}

	/*
	 * Type selectors
	 */
	@Test
	public void testTypeSelectorsPreCurlySpace()
	{
		assertCompletionCorrect("p | {}", '\t', "div", "p div {}");
	}

	/*
	 * Type selectors
	 */
	@Test
	public void testTypeSelectorsPostCurlyNoSpace()
	{

		// I think this should test addOutsideRuleProposals.RCURLY, but does not because of how we select the
		// lexeme provider range now
		assertCompletionCorrect("p, table {}|", '\t', "div", "p, table {}div");
	}

	/*
	 * Type selectors
	 */
	@Test
	public void testTypeSelectorsPostCurlySpace()
	{
		assertCompletionCorrect("p, table {} |", '\t', "div", "p, table {} div");
	}

	/*
	 * E:lang(c) Matches element of type E if it is in (human) language c (the document language specifies how language
	 * is determined)
	 */
	@Test
	public void testTypeSelectorsPostRParenNoSpace()
	{
		// Currently inserting as html:lang()en, for example
		assertCompletionCorrect("html:lang()| { }", '\t', 0, null, "html:lang() { }", null);
	}

	/*
	 * E F Matches any F element that is a descendant of an E element. Descendant selectors
	 */
	@Test
	public void testDescendantSelectors()
	{
		assertCompletionCorrect("p | {}", '\t', "table", "p table {}");
	}

	/*
	 * E > F Matches any F element that is a child of an element E.
	 */
	@Test
	public void testChildSelectorsSpace()
	{
		assertCompletionCorrect("p > | {}", '\t', "table", "p > table {}");
	}

	@Test
	public void testChildSelectorsNoSpace()
	{
		assertCompletionCorrect("p>| {}", '\t', "table", "p>table {}");
	}

	@Test
	public void testChildSelectorsDescendant()
	{
		// matches a P element that is a descendant of an LI; the LI element must be the child of an OL element; the OL
		// element must be a descendant of a DIV.
		assertCompletionCorrect("div ol > | p {}", '\t', "li", "div ol > li p {}");
	}

	/*
	 * Matches element E when E is the first child of its parent.
	 */
	@Test
	public void testPseudoClass()
	{
		assertCompletionCorrect("p:| {}", '\t', "first-child", "p:first-child {}");
	}

	/*
	 * Matches element E when E is the first child of its parent.
	 */
	@Test
	public void testPseudoClassPrefix()
	{
		assertCompletionCorrect("p:f| {}", '\t', "first-child", "p:first-child {}");
	}

	/*
	 * Matches element E if E is the source anchor of a hyperlink of which the target is not yet visited (:link) or
	 * already visited (:visited).
	 */
	@Test
	public void testLinkPseudoClass()
	{
		assertCompletionCorrect("a:| {}", '\t', "link", "a:link {}");
	}

	/*
	 * Matches element E if E is the source anchor of a hyperlink of which the target is not yet visited (:link) or
	 * already visited (:visited).
	 */
	@Test
	public void testLinkPseudoClassPrefix()
	{
		assertCompletionCorrect("a:l| {}", '\t', "link", "a:link {}");
	}

	/*
	 * Matches element E if E is the source anchor of a hyperlink of which the target is not yet visited (:link) or
	 * already visited (:visited).
	 */
	@Test
	public void testVistedPseudoClass()
	{
		assertCompletionCorrect("a:| {}", '\t', "visited", "a:visited {}");
	}

	/*
	 * Matches element E if E is the source anchor of a hyperlink of which the target is not yet visited (:link) or
	 * already visited (:visited).
	 */
	@Test
	public void testVistedPseudoClassPrefix()
	{
		assertCompletionCorrect("a:v| {}", '\t', "visited", "a:visited {}");
	}

	/*
	 * testDynamicPseudoClassActivePrefix
	 */
	@Test
	public void testDynamicPseudoClassActive()
	{
		assertCompletionCorrect("a:| {}", '\t', "active", "a:active {}");
	}

	/*
	 * testDynamicPseudoClassActivePrefix
	 */
	@Test
	public void testDynamicPseudoClassActivePrefix()
	{
		assertCompletionCorrect("a:a| {}", '\t', "active", "a:active {}");
	}

	/*
	 * testDynamicPseudoClassHover
	 */
	@Test
	public void testDynamicPseudoClassHover()
	{
		assertCompletionCorrect("a:| {}", '\t', "hover", "a:hover {}");
	}

	/*
	 * testDynamicPseudoClassHoverPrefix
	 */
	@Test
	public void testDynamicPseudoClassHoverPrefix()
	{
		assertCompletionCorrect("a:h| {}", '\t', "hover", "a:hover {}");
	}

	/*
	 * testDynamicPseudoClassFocus
	 */
	@Test
	public void testDynamicPseudoClassFocus()
	{
		assertCompletionCorrect("a:| {}", '\t', "focus", "a:focus {}");
	}

	/*
	 * testDynamicPseudoClassFocusPrefix
	 */
	@Test
	public void testDynamicPseudoClassFocusPrefix()
	{
		assertCompletionCorrect("a:f| {}", '\t', "focus", "a:focus {}");
	}

	/*
	 * E:lang(c) Matches element of type E if it is in (human) language c (the document language specifies how language
	 * is determined)
	 */
	@Test
	public void testLangPseudoClass()
	{
		assertCompletionCorrect("html:| { quotes: '« ' ' »' }", '\t', "lang", "html:lang { quotes: '« ' ' »' }");
	}

	/*
	 * E:lang(c) Matches element of type E if it is in (human) language c (the document language specifies how language
	 * is determined)
	 */
	@Test
	public void testLangPseudoClassPrefix()
	{
		assertCompletionCorrect("html:l| { quotes: '« ' ' »' }", '\t', "lang", "html:lang { quotes: '« ' ' »' }");
	}

	/*
	 * E:lang(c) Matches element of type E if it is in (human) language c (the document language specifies how language
	 * is determined)
	 */
	@Test
	public void testLangPseudoClassNoElement()
	{
		assertCompletionCorrect(":|", '\t', "lang", ":lang");
	}

	/*
	 * E:lang(c) Matches element of type E if it is in (human) language c (the document language specifies how language
	 * is determined)
	 */
	@Test
	public void testLangPseudoClassNoElementPrefix()
	{
		assertCompletionCorrect(":l|", '\t', "lang", ":lang");
	}

	/*
	 * E:lang(c) Matches element of type E if it is in (human) language c (the document language specifies how language
	 * is determined)
	 */
	@Test
	public void testLangPseudoClassPreviousElement()
	{
		// currently reports pseduo-elements as proposals _before_ the colon
		assertCompletionCorrect("p {} |:", '\t', "div", "p {} div:");
	}

	/*
	 * E:lang(c) Matches element of type E if it is in (human) language c (the document language specifies how language
	 * is determined)
	 */
	@Test
	public void testLangPseudoClassProperties()
	{
		assertCompletionCorrect("html:lang(|) { quotes: '« ' ' »' }", '\t', "fr-ca",
				"html:lang(fr-ca) { quotes: '« ' ' »' }");
	}

	/*
	 * E:lang(c) Matches element of type E if it is in (human) language c (the document language specifies how language
	 * is determined)
	 */
	@Test
	public void testLangPseudoClassPropertiesPrefix()
	{
		assertCompletionCorrect("html:lang(f|) { quotes: '« ' ' »' }", '\t', "fr-ca",
				"html:lang(fr-ca) { quotes: '« ' ' »' }");
	}

	/*
	 * E:lang(c) Matches element of type E if it is in (human) language c (the document language specifies how language
	 * is determined)
	 */
	@Test
	public void testLangPseudoClassPropertiesPrefixSpace()
	{
		assertCompletionCorrect("html:lang(f| ) { quotes: '« ' ' »' }", '\t', "fr-ca",
				"html:lang(fr-ca ) { quotes: '« ' ' »' }");
	}

	/**
	 * testPseudoElements
	 */
	@Test
	public void testPseudoClassesAndElements()
	{
		this.checkProposals("contentAssist/pseduo-class-proposal.css", true, true, "active", "after", "before",
				"checked", "disabled", "empty", "enabled", "first-child", "first-letter", "first-line",
				"first-of-type", "focus", "hover", "indeterminate", "lang", "last-child", "last-of-type", "link",
				"not", "nth-child", "nth-last-child", "nth-last-of-type", "nth-of-type", "only-child", "only-of-type",
				"root", "target", "visited");
	}

	/**
	 * testPseudoElements
	 */
	@Test
	public void testPseudoElements()
	{
		this.checkProposals("contentAssist/pseduo-element-proposal.css", true, true, "after", "before", "first-letter",
				"first-line");
	}

	/**
	 * testPseudoElementsPrefix
	 */
	@Test
	public void testPseudoElementsPrefix()
	{
		this.checkProposals("contentAssist/pseduo-element-proposal-prefix.css", true, true, "first-letter",
				"first-line");
	}

	/**
	 * same pseduo-class test as above, but has a more complete list of the items being tested
	 */
	@Test
	public void testPseudoClassPrefixFull()
	{
		this.checkProposals("contentAssist/pseduo-class-proposal-prefix.css", true, true, "first-child",
				"first-letter", "first-line", "first-of-type", "focus");
	}

	/**
	 * testPseudoClassParen
	 */
	@Test
	public void testPseudoClassParen()
	{
		// @formatter:off
		this.checkProposals(
			"contentAssist/pseduo-class-paren-proposal.css",
			"even"
		);
		// @formatter:on
	}

	/*
	 * Matches any F element immediately preceded by a sibling element E.
	 */
	@Test
	public void testAdjacentSelector()
	{
		assertCompletionCorrect("p + |", '\t', "div", "p + div");
	}

	/*
	 * Matches any F element immediately preceded by a sibling element E.
	 */
	@Test
	public void testAdjacentSelectorWithClass()
	{
		/* Special formatting only occurs when H1 has class="opener" */
		assertCompletionCorrect("h1.opener + | { margin-top: -5mm }", '\t', "h2", "h1.opener + h2 { margin-top: -5mm }");
	}

	/**
	 * Matches any E element with the "foo" attribute set (whatever the value)
	 */
	@Test
	public void testAttributeSelector()
	{

		// Left as a block of tests that will need to be split and uncommented when we actually support this
		// functionality

		// Matches any E element with the "foo" attribute set (whatever the value).
		// assertCompletionCorrect("E[foo]", '\t', "", ""); // custom attributes

		// Matches any E element whose "foo" attribute value is exactly equal to "warning".
		// assertCompletionCorrect("E[foo=\"warning\"]", '\t', "", ""); // custom attributes

		// Matches any E element whose "foo" attribute value is a list of space-separated values, one of which is
		// exactly equal to "warning".
		// assertCompletionCorrect("E[foo~=\"warning\"]", '\t', "", ""); // need to find actual attribute values

		// Matches any E element whose "lang" attribute has a hyphen-separated list of values beginning (from the left)
		// with "en".
		// assertCompletionCorrect("p[lang|=\"en\"]", '\t', "", ""); needs to be fixed as it has a pipe in the actual
		// text

		// *[lang=fr] and [lang=fr] are equivalent.
		// assertCompletionCorrect("[|]", '\t', "lang", "[lang]");
		// assertCompletionCorrect("[lang=|]", '\t', "fr", "[lang=fr]");
		// assertCompletionCorrect("*[|]", '\t', "lang", "*[lang]");
		// assertCompletionCorrect("*[lang=|]", '\t', "fr", "*[lang=fr]");

		// Matches any element that (1) has the "href" attribute set and (2) is inside a P that is itself inside a DIV
		// assertCompletionCorrect("div p *[href]", '\t', "div", "p + div");

		// Matches all SPAN elements whose "hello" attribute has exactly the value "Cleveland" and whose "goodbye"
		// attribute has exactly the value "Columbus"
		// assertCompletionCorrect("span[|hello=\"Cleveland\"][goodbye=\"Columbus\"] { color: blue; }", '\t', "", "");
	}

	/**
	 * testPropertyProposalPostLCurly
	 */
	@Test
	public void testPropertyProposalPostLCurly()
	{
		assertCompletionCorrect("p {| }", '\t', "background-position", "p {background-position:  }");
	}

	/**
	 * testPropertyProposalPostLCurly
	 */
	@Test
	public void testPropertyProposalPreRCurly()
	{
		assertCompletionCorrect("p { |}", '\t', "background-position", "p { background-position: }");
	}

	/**
	 * testPropertyProposalPreColon
	 */
	@Test
	public void testPropertyProposalPreColon()
	{
		assertCompletionCorrect("p {background-position|:}", '\t', "background-position", "p {background-position:}");
	}

	/**
	 * testPropertyProposalPreColon
	 */
	@Test
	public void testPropertyProposalPostSemicolon()
	{
		assertCompletionCorrect("p {background-position:top;| }", '\t', "background-color",
				"p {background-position:top;background-color:  }");
	}

	/**
	 * testPropertyProposalProperty
	 */
	@Test
	public void testPropertyProposalProperty()
	{
		assertCompletionCorrect("p {background-po|sition: }", '\t', "background-position", "p {background-position: }");
	}

	/**
	 * testPropertyProposalWithMinus
	 */
	@Test
	public void testPropertyProposalWithMinus()
	{
		assertCompletionCorrect("p {|-moz-binding: }", '\t', "-moz-binding", "p {-moz-binding: }");
	}

	/**
	 * testPropertyProposalPostColonNoSpace
	 */
	@Test
	public void testPropertyValueProposalPostColonNoSpace()
	{
		assertCompletionCorrect("p {background-position:|top;}", '\t', "top", "p {background-position:top;}");
	}

	/**
	 * testPropertyProposalPostColonSpace
	 */
	@Test
	public void testPropertyValueProposalPostColonSpace()
	{
		assertCompletionCorrect("p {background-position: |top;}", '\t', "top", "p {background-position: top;}");
	}

	/**
	 * testPropertyValueProposalPreSemiColonNoSpace
	 */
	@Test
	public void testPropertyValueProposalPreSemiColonNoSpace()
	{
		assertCompletionCorrect("p {background-position:top|;}", '\t', "top", "p {background-position:top;}");
	}

	/**
	 * testPropertyValueProposalPreSemiColonSpace
	 */
	@Test
	public void testPropertyValueProposalPreSemiColonSpace()
	{
		assertCompletionCorrect("p {background-position:top| ;}", '\t', "top", "p {background-position:top ;}");
	}

	/**
	 * testPropertyValueProposalPreLCurlyNoSpace
	 */
	@Test
	public void testPropertyValueProposalPreLCurlyNoSpace()
	{
		assertCompletionCorrect("p {background-position:top|}", '\t', "top", "p {background-position:top}");
	}

	/**
	 * testPropertyValueProposalPreLCurlySpace
	 */
	@Test
	public void testPropertyValueProposalPreLCurlySpace()
	{
		assertCompletionCorrect("p {background-position:top| }", '\t', "top", "p {background-position:top }");
	}

	/**
	 * testClasses
	 */
	@Test
	public void testClasses()
	{
		this.setupTestContext("contentAssist/class-proposal.css");
		int offset = this.cursorOffsets.get(0);
		ITextViewer viewer = AssertUtil.createTextViewer(document);

		// add on "." to test class proposals
		document.set(document.get() + ".");
		offset++;

		ICompletionProposal[] proposals = processor.doComputeCompletionProposals(viewer, offset, '\t', false);
		AssertUtil.assertProposalFound(".testclass", proposals);
	}

	/**
	 * testClasses
	 */
	@Test
	public void testClassesChild()
	{
		this.setupTestContext("contentAssist/class-proposal.css");
		int offset = this.cursorOffsets.get(0);
		ITextViewer viewer = AssertUtil.createTextViewer(document);

		// add on "p." to test class proposals
		document.set(document.get() + "p.");
		offset += 2;

		ICompletionProposal[] proposals = processor.doComputeCompletionProposals(viewer, offset, '\t', false);

		AssertUtil.assertProposalFound(".testclass", proposals);
	}

	/**
	 * testIds
	 */
	@Test
	public void testIds()
	{
		this.setupTestContext("contentAssist/id-proposal.css");
		int offset = this.cursorOffsets.get(0);
		ITextViewer viewer = AssertUtil.createTextViewer(document);

		// add on "#" to test id proposals
		document.set(document.get() + "#");
		offset++;

		ICompletionProposal[] proposals = processor.doComputeCompletionProposals(viewer, offset, '\t', false);
		AssertUtil.assertProposalFound("#testid", proposals);
	}

	/**
	 * testIds
	 */
	@Test
	public void testIdsChild()
	{
		this.setupTestContext("contentAssist/id-proposal.css");
		int offset = this.cursorOffsets.get(0);
		ITextViewer viewer = AssertUtil.createTextViewer(document);

		// add on "p#" to test id proposals
		document.set(document.get() + "p#");
		offset += 2;

		ICompletionProposal[] proposals = processor.doComputeCompletionProposals(viewer, offset, '\t', false);

		AssertUtil.assertProposalFound("#testid", proposals);
	}

	/**
	 * testClasses
	 */
	@Test
	public void testClassesIds()
	{
		this.setupTestContext("contentAssist/class-id-proposal.css");
		int offset = this.cursorOffsets.get(0);
		ITextViewer viewer = AssertUtil.createTextViewer(document);

		// add on "." to test class proposals
		document.set(document.get() + ".");
		offset++;

		// looking for .testclass#a#b
		ICompletionProposal[] proposals = processor.doComputeCompletionProposals(viewer, offset, '\t', false);
		AssertUtil.assertProposalApplies(document, ".testclass", proposals, offset);

		// test for #a
		offset = document.getLength() - 1;
		document.set(document.get() + "#");
		offset++;

		proposals = processor.doComputeCompletionProposals(viewer, offset, '\t', false);
		AssertUtil.assertProposalApplies(document, "#a", proposals, offset);

		// test for #b
		offset = document.getLength() - 1;
		document.set(document.get() + "#");
		offset++;

		proposals = processor.doComputeCompletionProposals(viewer, offset, '\t', false);
		AssertUtil.assertProposalApplies(document, "#b", proposals, offset);

	}

	/**
	 * testFailureAfterColon
	 */
	@Test
	public void testFailureAfterColon()
	{
		// @formatter:off
		this.checkProposals(
			"contentAssist/failure-after-colon.css",
			"center",
			"inherit",
			"justify",
			"left",
			"right"
		);
		// @formatter:on
	}

	/**
	 * testStringFunction
	 * 
	 * @throws IOException
	 */
	@Test
	public void testBackgroundProposals() throws IOException
	{
		File bundleFile = FileUtil.createTempFile("editor_unit_tests", "rb");

		BundleElement bundleElement = new BundleElement(bundleFile.getAbsolutePath());
		bundleElement.setDisplayName("Editor Unit Tests");

		File f = FileUtil.createTempFile("snippet", "rb");
		SnippetElement se = createSnippet(f.getAbsolutePath(), "background-color-template", "background", "source.css");
		bundleElement.addChild(se);
		BundleManager.getInstance().addBundle(bundleElement);

		// note template is interleaved into proposals
		this.checkProposals("contentAssist/background.css", true, true, "backface-visibility", "background",
				"background-attachment", "background-clip", "background-color", "background-color-template",
				"background-image", "background-origin", "background-position", "background-position-x",
				"background-position-y", "background-repeat", "background-size");

		BundleManager.getInstance().unloadScript(f);

	}

	/**
	 * testClasses
	 */
	@Test
	public void testColorProposals()
	{
		this.setupTestContext("contentAssist/color-proposal.css");
		int offset = this.cursorOffsets.get(0);
		ITextViewer viewer = AssertUtil.createTextViewer(document);

		// add on "#" to test color proposals
		String toInsert = "p {color:#}";
		document.set(document.get() + toInsert);
		offset += toInsert.length() - 1; // cursor before }

		ICompletionProposal[] proposals = processor.doComputeCompletionProposals(viewer, offset, '\t', false);
		AssertUtil.assertProposalApplies(document, "#CCCCCC", proposals, offset);
	}

	/**
	 * testClasses
	 */
	@Test
	public void testColorProposalsBackground()
	{
		this.setupTestContext("contentAssist/color-proposal.css");
		int offset = this.cursorOffsets.get(0);
		ITextViewer viewer = AssertUtil.createTextViewer(document);

		// add on "#" to test color proposals
		String toInsert = "p {background:#}";
		document.set(document.get() + toInsert);
		offset += toInsert.length() - 1; // cursor before }

		ICompletionProposal[] proposals = processor.doComputeCompletionProposals(viewer, offset, '\t', false);
		AssertUtil.assertProposalApplies(document, "#CCCCCC", proposals, offset);
	}

	@Test
	public void testCreateLexemeProviderEmptyDocument()
	{

		CSSContentAssistProcessor proc = new CSSContentAssistProcessor(null)
		{
			public IRange getLexemeRange(IDocument document, int offset)
			{
				return super.getLexemeRange(document, offset);
			}
		};

		String source = "";
		IFileStore fileStore = createFileStore("proposal_tests", "css", source);
		this.setupTestContext(fileStore);

		IRange actualRange = proc.getLexemeRange(document, this.cursorOffsets.get(0));
		IRange expectedRange = new Range(0, 0);
		assertEquals(expectedRange, actualRange);
	}

	@Test
	public void testCreateLexemeProvider()
	{

		CSSContentAssistProcessor proc = new CSSContentAssistProcessor(null)
		{
			public IRange getLexemeRange(IDocument document, int offset)
			{
				return super.getLexemeRange(document, offset);
			}
		};

		String source = "body { font-family: \"Helvetica Neue\", Arial, Helvetica, sans-serif; color:#000|; background: none; font-size: 10pt; }";
		IFileStore fileStore = createFileStore("proposal_tests", "css", source);
		this.setupTestContext(fileStore);

		IRange actualRange = proc.getLexemeRange(document, this.cursorOffsets.get(0));
		IRange expectedRange = new Range(0, 115);
		assertEquals(expectedRange, actualRange);
	}

	@Test
	public void testCreateLexemeProviderMedia()
	{

		CSSContentAssistProcessor proc = new CSSContentAssistProcessor(null)
		{
			public IRange getLexemeRange(IDocument document, int offset)
			{
				return super.getLexemeRange(document, offset);
			}
		};

		String source = "@media screen, print { body { line-height: 1.2; ; color:#000|; } }";
		IFileStore fileStore = createFileStore("proposal_tests", "css", source);
		this.setupTestContext(fileStore);

		IRange actualRange = proc.getLexemeRange(document, this.cursorOffsets.get(0));
		IRange expectedRange = new Range(0, 64);
		assertEquals(expectedRange, actualRange);
	}

	@Test
	public void testCreateLexemeProviderEndingBrace()
	{

		CSSContentAssistProcessor proc = new CSSContentAssistProcessor(null)
		{
			public IRange getLexemeRange(IDocument document, int offset)
			{
				return super.getLexemeRange(document, offset);
			}
		};

		String source = "body {}|";
		IFileStore fileStore = createFileStore("proposal_tests", "css", source);
		this.setupTestContext(fileStore);

		IRange actualRange = proc.getLexemeRange(document, this.cursorOffsets.get(0));
		IRange expectedRange = new Range(7, 7);
		assertEquals(expectedRange, actualRange);
	}

	@Test
	public void testIsValidAutoActivationLocation()
	{
		EnumSet<CSSTokenType> validTokenTypes = EnumSet.of(CSSTokenType.LCURLY, CSSTokenType.COMMA, CSSTokenType.COLON,
				CSSTokenType.SEMICOLON, CSSTokenType.CLASS, CSSTokenType.ID);

		// create a document with a lot of different token types
		IFileStore fileStore = this.getFileStore("contentAssist/blueprint.css");
		this.setupTestContext(fileStore);

		assertFalse("Negative offset is not a valid activation location",
				processor.isValidAutoActivationLocation('\t', '\t', document, -1));

		// tokenize the document
		IRange range = new Range(0, document.getLength() - 1);
		ILexemeProvider<CSSTokenType> lexemeProvider = new CSSLexemeProvider(document, range, new CSSTokenScanner());

		// based on the particular token we are testing, is this a valid activation location?
		for (Lexeme<CSSTokenType> lexeme : lexemeProvider)
		{
			int offset = lexeme.getEndingOffset() + 1;

			// ignore last lexeme in document as offset will be past document length
			if (offset >= document.getLength())
			{
				break;
			}

			if (validTokenTypes.contains(lexeme.getType()))
			{
				assertTrue(
						MessageFormat.format(
								"Lexeme {0} ending at offset {1} should be a valid activation location. Surrounding text is ...{2}...",
								lexeme.getType().name(), offset, getSurroundingText(document, offset, 20)),
						processor.isValidAutoActivationLocation('\t', '\t', document, offset));
			}
			else
			{
				assertFalse(
						MessageFormat.format(
								"Lexeme {0} ending at offset {1} should not be a valid activation location. Surrounding text is ...{2}...",
								lexeme.getType().name(), offset, getSurroundingText(document, offset, 20)),
						processor.isValidAutoActivationLocation('\t', '\t', document, offset));
			}

		}

	}

	protected String getSurroundingText(IDocument document, int offset, int context)
	{
		int start = (offset - context < 0) ? 0 : offset - context;
		int end = (offset + context > document.getLength() - 1) ? document.getLength() - 1 : offset + context;
		try
		{
			return document.get(start, offset - start) + "|" + document.get(offset, end - (offset + 1));
		}
		catch (BadLocationException e)
		{
			return StringUtil.EMPTY;
		}
	}

	@Test
	public void testIsValidActivationCharacter_space()
	{
		processor = createContentAssistProcessor(null);
		assertTrue(processor.isValidActivationCharacter(' ', ' '));
	}

	@Test
	public void testIsValidActivationCharacter_a()
	{
		processor = createContentAssistProcessor(null);
		assertFalse(processor.isValidActivationCharacter('a', 'a'));
	}

	@Test
	public void testIsValidIdentifier_a()
	{
		processor = createContentAssistProcessor(null);
		assertTrue(processor.isValidIdentifier('a', 'a'));
	}

	@Test
	public void testIsValidIdentifier_z()
	{
		processor = createContentAssistProcessor(null);
		assertTrue(processor.isValidIdentifier('z', 'z'));
	}

	@Test
	public void testIsValidIdentifier_A()
	{
		processor = createContentAssistProcessor(null);
		assertTrue(processor.isValidIdentifier('A', 'A'));
	}

	@Test
	public void testIsValidIdentifier_Z()
	{
		processor = createContentAssistProcessor(null);
		assertTrue(processor.isValidIdentifier('Z', 'Z'));
	}

	@Test
	public void testIsValidIdentifier_underscore()
	{
		processor = createContentAssistProcessor(null);
		assertTrue(processor.isValidIdentifier('_', '_'));
	}

	@Test
	public void testIsValidIdentifier_hash()
	{
		processor = createContentAssistProcessor(null);
		assertTrue(processor.isValidIdentifier('#', '#'));
	}

	@Test
	public void testIsValidIdentifier_dot()
	{
		processor = createContentAssistProcessor(null);
		assertTrue(processor.isValidIdentifier('.', '.'));
	}

	@Test
	public void testIsValidIdentifier_dash()
	{
		processor = createContentAssistProcessor(null);
		assertTrue(processor.isValidIdentifier('-', '-'));
	}

	@Test
	public void testIsValidIdentifier_dollar()
	{
		processor = createContentAssistProcessor(null);
		assertFalse(processor.isValidIdentifier('$', '$'));
	}

	@Test
	public void testIsValidIdentifier_space()
	{
		processor = createContentAssistProcessor(null);
		assertFalse(processor.isValidIdentifier(' ', ' '));
	}

	/**
	 * @param source
	 * @param trigger
	 * @param proposalToChoose
	 * @param postCompletion
	 */
	protected void assertCompletionCorrect(String source, char trigger, String proposalToChoose, String postCompletion)
	{
		assertCompletionCorrect(source, trigger, -1, proposalToChoose, postCompletion, null);
	}

	/**
	 * @param source
	 * @param trigger
	 * @param proposalCount
	 * @param proposalToChoose
	 * @param postCompletion
	 * @param point
	 */
	protected void assertCompletionCorrect(String source, char trigger, int proposalCount, String proposalToChoose,
			String postCompletion, Point point)
	{
		IFileStore fileStore = createFileStore("proposal_tests", "css", source);
		this.setupTestContext(fileStore);

		int offset = this.cursorOffsets.get(0);
		ITextViewer viewer = AssertUtil.createTextViewer(document);
		ICompletionProposal[] proposals = processor.doComputeCompletionProposals(viewer, offset, trigger, false);

		if (proposalCount >= 0)
		{
			assertEquals(proposalCount, proposals.length);
		}
		if (proposalToChoose != null)
		{
			AssertUtil.assertProposalFound(proposalToChoose, proposals);
			AssertUtil.assertProposalApplies(postCompletion, document, proposalToChoose, proposals, offset, point);
		}
	}

}
