/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist;

import java.io.File;
import java.io.IOException;

import com.aptana.editor.css.tests.CSSEditorBasedTests;
import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.SnippetElement;

/**
 * JSContentAssistProposalTests
 */
public class CSSContentAssistProposalTests extends CSSEditorBasedTests
{

	/*
	 * Universal selector. Matches any element
	 */
	public void testUniversalSelectors()
	{
		// Should be in the list
		// assertCompletionCorrect("| {}", '\t', "*", "* {}");
	}

	/*
	 * Type selectors
	 */
	public void testTypeSelectors()
	{
		assertCompletionCorrect("p| {}", '\t', "p", "p {}");
	}

	/*
	 * Type selectors
	 */
	public void testTypeSelectorsMidElement()
	{
		assertCompletionCorrect("tab|le {}", '\t', "table", "table {}");
	}

	/*
	 * Type selectors
	 */
	public void testTypeSelectorsListNoSpace()
	{
		assertCompletionCorrect("p,| {}", '\t', "table", "p,table {}");
	}

	/*
	 * Type selectors
	 */
	public void testTypeSelectorsListSpace()
	{
		assertCompletionCorrect("p, | {}", '\t', "table", "p, table {}");
	}

	/*
	 * Type selectors
	 */
	public void testTypeSelectorsPreCurlyNoSpace()
	{
		// I think this should test addOutsideRuleProposals.LCURLY, but does not because we reset the current
		// lexeme to null in getCoarseLocationType()
		assertCompletionCorrect("p |{}", '\t', "div", "p div{}");
	}

	/*
	 * Type selectors
	 */
	public void testTypeSelectorsPreCurlySpace()
	{
		assertCompletionCorrect("p | {}", '\t', "div", "p div {}");
	}

	/*
	 * Type selectors
	 */
	public void testTypeSelectorsPostCurlyNoSpace()
	{

		// I think this should test addOutsideRuleProposals.RCURLY, but does not because of how we select the
		// lexeme provider range now
		assertCompletionCorrect("p, table {}|", '\t', "div", "p, table {}div");
	}

	/*
	 * Type selectors
	 */
	public void testTypeSelectorsPostCurlySpace()
	{
		assertCompletionCorrect("p, table {} |", '\t', "div", "p, table {} div");
	}

	/*
	 * E:lang(c) Matches element of type E if it is in (human) language c (the document language specifies how language
	 * is determined)
	 */
	public void testTypeSelectorsPostRParenNoSpace()
	{
		// FIXME: This should work. Currently inserting as html:lang()en, for example
		// assertCompletionCorrect("html:lang()| { }", '\t', 0, null, "html:lang() { }", null);
	}

	/*
	 * E F Matches any F element that is a descendant of an E element. Descendant selectors
	 */
	public void testDescendantSelectors()
	{
		assertCompletionCorrect("p | {}", '\t', "table", "p table {}");
	}

	/*
	 * E > F Matches any F element that is a child of an element E.
	 */
	public void testChildSelectorsSpace()
	{
		assertCompletionCorrect("p > | {}", '\t', "table", "p > table {}");
	}

	public void testChildSelectorsNoSpace()
	{
		// FIXME: This should work
		// assertCompletionCorrect("p>| {}", '\t', "table", "p>table {}");
	}

	public void testChildSelectorsDescendant()
	{
		// matches a P element that is a descendant of an LI; the LI element must be the child of an OL element; the OL
		// element must be a descendant of a DIV.
		assertCompletionCorrect("div ol > | p {}", '\t', "li", "div ol > li p {}");
	}

	/*
	 * Matches element E when E is the first child of its parent.
	 */
	public void testPseduoClass()
	{
		assertCompletionCorrect("p:| {}", '\t', "first-child", "p:first-child {}");
	}

	/*
	 * Matches element E if E is the source anchor of a hyperlink of which the target is not yet visited (:link) or
	 * already visited (:visited).
	 */
	public void testLinkPseduoClass()
	{
		assertCompletionCorrect("a:| {}", '\t', "link", "a:link {}");
	}

	/*
	 * Matches element E if E is the source anchor of a hyperlink of which the target is not yet visited (:link) or
	 * already visited (:visited).
	 */
	public void testVistedPseduoClass()
	{
		assertCompletionCorrect("a:| {}", '\t', "visited", "a:visited {}");
	}

	/*
	 * Matches E during certain user actions.
	 */
	public void testDynamicPseduoClassActive()
	{
		assertCompletionCorrect("a:| {}", '\t', "active", "a:active {}");
	}

	/*
	 * Matches E during certain user actions.
	 */
	public void testDynamicPseduoClassHover()
	{
		assertCompletionCorrect("a:| {}", '\t', "hover", "a:hover {}");
	}

	/*
	 * Matches E during certain user actions.
	 */
	public void testDynamicPseduoClassFocus()
	{
		assertCompletionCorrect("a:| {}", '\t', "focus", "a:focus {}");
	}

	/*
	 * E:lang(c) Matches element of type E if it is in (human) language c (the document language specifies how language
	 * is determined)
	 */
	public void testLangPseduoClass()
	{
		assertCompletionCorrect("html:| { quotes: '« ' ' »' }", '\t', "lang", "html:lang { quotes: '« ' ' »' }");
	}

	/*
	 * E:lang(c) Matches element of type E if it is in (human) language c (the document language specifies how language
	 * is determined)
	 */
	public void testLangPseduoClassNoElement()
	{
		assertCompletionCorrect(":|", '\t', "lang", ":lang");
	}

	/*
	 * E:lang(c) Matches element of type E if it is in (human) language c (the document language specifies how language
	 * is determined)
	 */
	public void testLangPseduoClassPreviousElement()
	{
		// FIXME: currently reports pseduo-elements as proposals _before_ the colon
		// assertCompletionCorrect("p {} |:", '\t', "div", "p {} div:");
	}

	/*
	 * E:lang(c) Matches element of type E if it is in (human) language c (the document language specifies how language
	 * is determined)
	 */
	public void testLangPseduoClassProperties()
	{
		assertCompletionCorrect("html:lang(|) { quotes: '« ' ' »' }", '\t', "fr-ca",
				"html:lang(fr-ca) { quotes: '« ' ' »' }");
	}

	/**
	 * testFailureAfterColon
	 */
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
	public void testBackgroundPoposals() throws IOException
	{
		File bundleFile = File.createTempFile("editor_unit_tests", "rb");
		bundleFile.deleteOnExit();

		BundleElement bundleElement = new BundleElement(bundleFile.getAbsolutePath());
		bundleElement.setDisplayName("Editor Unit Tests");

		File f = File.createTempFile("snippet", "rb");
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

	}
