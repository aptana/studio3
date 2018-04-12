/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class ScopeSelectorTests
{
	@Test
	public void testParseSimpleName()
	{
		String scope = "source.ruby";
		ScopeSelector selector = new ScopeSelector(scope);

		// make sure we parsed successfully
		assertNotNull(selector);

		// convert selector back to source and compare
		assertEquals(scope, selector.toString());

		// make sure we have the right selector type
		ISelectorNode root = selector.getRoot();
		assertTrue(root instanceof NameSelector);
	}

	@Test
	public void testParseSimpleAndSelector()
	{
		String scope = "text.html source.ruby";
		ScopeSelector selector = new ScopeSelector(scope);

		// make sure we parsed successfully
		assertNotNull(selector);

		// convert selector back to source and compare
		assertEquals(scope, selector.toString());

		// make sure we have the right selector type
		ISelectorNode root = selector.getRoot();
		assertTrue(root instanceof DescendantSelector);

		// check children
		DescendantSelector andSelector = (DescendantSelector) root;
		assertTrue(andSelector.getLeftChild() instanceof NameSelector);
		assertTrue(andSelector.getRightChild() instanceof NameSelector);
	}

	@Test
	public void testParseSimpleOrSelector()
	{
		String scope = "text.html, source.ruby";
		ScopeSelector selector = new ScopeSelector(scope);

		// make sure we parsed successfully
		assertNotNull(selector);

		// convert selector back to source and compare
		assertEquals(scope, selector.toString());

		// make sure we have the right selector type
		ISelectorNode root = selector.getRoot();
		assertTrue(root instanceof OrSelector);

		// check children
		OrSelector orSelector = (OrSelector) root;
		assertTrue(orSelector.getLeftChild() instanceof NameSelector);
		assertTrue(orSelector.getRightChild() instanceof NameSelector);
	}

	@Test
	public void testParseMultiAndSelector()
	{
		String scope = "text.html source.ruby string.ruby";
		ScopeSelector selector = new ScopeSelector(scope);

		// make sure we parsed successfully
		assertNotNull(selector);

		// convert selector back to source and compare
		assertEquals(scope, selector.toString());

		// make sure we have the right selector type
		ISelectorNode root = selector.getRoot();
		assertTrue(root instanceof DescendantSelector);

		// check children
		DescendantSelector andSelector = (DescendantSelector) root;
		assertTrue(andSelector.getLeftChild() instanceof DescendantSelector);
		assertTrue(andSelector.getRightChild() instanceof NameSelector);
	}

	@Test
	public void testParseMultiOrSelector()
	{
		String scope = "text.html, source.ruby, string.ruby";
		ScopeSelector selector = new ScopeSelector(scope);

		// make sure we parsed successfully
		assertNotNull(selector);

		// convert selector back to source and compare
		assertEquals(scope, selector.toString());

		// make sure we have the right selector type
		ISelectorNode root = selector.getRoot();
		assertTrue(root instanceof OrSelector);

		// check children
		OrSelector orSelector = (OrSelector) root;
		assertTrue(orSelector.getLeftChild() instanceof OrSelector);
		assertTrue(orSelector.getRightChild() instanceof NameSelector);
	}

	@Test
	public void testParseMultiMixedSelector()
	{
		String scope = "text.html source.ruby, text.erb source.ruby, source.ruby string.ruby";
		ScopeSelector selector = new ScopeSelector(scope);

		// make sure we parsed successfully
		assertNotNull(selector);

		// convert selector back to source and compare
		assertEquals(scope, selector.toString());

		// make sure we have the right selector type
		ISelectorNode root = selector.getRoot();
		assertTrue(root instanceof OrSelector);

		// check children
		OrSelector orSelector = (OrSelector) root;
		assertTrue(orSelector.getLeftChild() instanceof OrSelector);
		assertTrue(orSelector.getRightChild() instanceof DescendantSelector);
	}

	// Match the element deepest down in the scope e.g. string wins over source.php when the
	// scope is source.php string.quoted.
	//
	// Match most of the deepest element e.g. string.quoted wins over string.
	//
	// Rules 1 and 2 applied again to the scope selector when removing the deepest element
	// (in the case of a tie), e.g. text source string wins over source string.
	//
	// In the case of tab triggers, key equivalents and dropped files (drag commands), a menu is
	// presented for the best matches when these are identical in rank (which would mean the scope
	// selector in that case was identical).
	//
	// For themes and preference items, the winner is undefined when multiple items use the same scope
	// selector, though this is on a per-property basis. So for example if one theme item sets the
	// background to blue for string.quoted and another theme item sets the foreground to white,
	// again for string.quoted, the result would be that the foreground was taken from the latter
	// item and background from the former.
	@Test
	public void testBestMatch()
	{
		ScopeSelector entity = new ScopeSelector("entity");
		ScopeSelector metaTagEntity = new ScopeSelector("meta.tag entity");
		List<IScopeSelector> selectors = new ArrayList<IScopeSelector>();
		selectors.add(entity);
		selectors.add(metaTagEntity);
		assertEquals(metaTagEntity, ScopeSelector.bestMatch(selectors,
				"text.html.markdown meta.disable-markdown meta.tag.block.any.html entity.name.tag.block.any.html"));
	}

	@Test
	public void testMatchResults()
	{
		ScopeSelector entity = new ScopeSelector("entity");
		ScopeSelector metaTagEntity = new ScopeSelector("meta.tag entity");

		String scope = "text.html.markdown meta.disable-markdown meta.tag.block.any.html entity.name.tag.block.any.html";

		assertTrue(entity.matches(scope));
		assertEquals(Arrays.asList(0, 0, 0, 6), entity.getMatchResults());

		assertTrue(metaTagEntity.matches(scope));
		assertEquals(Arrays.asList(0, 0, 8, 6), metaTagEntity.getMatchResults());
	}

	@Test
	public void testBestMatchExample()
	{
		ScopeSelector string = new ScopeSelector(
				"string - string.unquoted.old-plist - string.unquoted.heredoc, string.unquoted.heredoc string");
		ScopeSelector metaTag = new ScopeSelector("meta.tag");
		List<IScopeSelector> selectors = new ArrayList<IScopeSelector>();
		selectors.add(string);
		selectors.add(metaTag);
		assertEquals(string, ScopeSelector.bestMatch(selectors,
				"text.html.markdown meta.disable-markdown meta.tag.block.any.html meta.attribute-with-value.id.html string.quoted.double.html meta.toc-list.id.html"));
		assertEquals(string, ScopeSelector.bestMatch(selectors,
				"text.html.markdown meta.disable-markdown meta.tag.block.any.html string.quoted.double.html"));
	}

	@Test
	public void testBestMatchDeepestElementWins()
	{
		ScopeSelector string = new ScopeSelector("string");
		ScopeSelector source = new ScopeSelector("source.php");
		List<IScopeSelector> selectors = new ArrayList<IScopeSelector>();
		selectors.add(string);
		selectors.add(source);
		assertEquals(string, ScopeSelector.bestMatch(selectors, "source.php string.quoted"));
	}

	@Test
	public void testMatchResultsDeepestElementWins()
	{
		ScopeSelector string = new ScopeSelector("string");
		ScopeSelector source = new ScopeSelector("source.php");

		String scope = "source.php string.quoted";

		assertTrue(string.matches(scope));
		assertEquals(Arrays.asList(0, 6), string.getMatchResults());

		assertTrue(source.matches(scope));
		assertEquals(Arrays.asList(10, 0), source.getMatchResults());
	}

	@Test
	public void testBestMatchLengthOfDeepestElementWins()
	{
		ScopeSelector string = new ScopeSelector("string");
		ScopeSelector quoted = new ScopeSelector("string.quoted");
		List<IScopeSelector> selectors = new ArrayList<IScopeSelector>();
		selectors.add(string);
		selectors.add(quoted);
		assertEquals(quoted, ScopeSelector.bestMatch(selectors, "source.php string.quoted"));
	}

	@Test
	public void test2357()
	{
		ScopeSelector jsStorage = new ScopeSelector(
				"source.js storage - storage.type.function - source.php, source.js constant - source.php, source.js keyword - source.php, source.js variable.language, source.js meta.brace, source.js punctuation.definition.parameters.begin, source.js punctuation.definition.parameters.end");
		ScopeSelector constantNumeric = new ScopeSelector("constant.numeric");
		ScopeSelector jsString = new ScopeSelector("source.js string - source.php, source.js keyword.operator");

		assertTrue(new ScopeSelector("source.css string").matches("source.css string.quoted.single.css"));
		assertTrue(new ScopeSelector("string").matches("source.css string.quoted.single.css"));
		assertTrue(constantNumeric.matches("source.js constant.numeric.js"));
		assertTrue(jsStorage.matches("source.js constant.numeric.js"));
		assertTrue(jsString.matches("source.js string.quoted.single.js"));

		List<IScopeSelector> selectors = new ArrayList<IScopeSelector>();
		selectors.add(jsStorage);
		selectors.add(constantNumeric);
		selectors.add(jsString);
		assertEquals(constantNumeric, ScopeSelector.bestMatch(selectors, "source.js constant.numeric.js"));
		assertEquals(jsString, ScopeSelector.bestMatch(selectors, "source.js string.quoted.single.js"));
	}

	/**
	 * Test that ensures we move up the scope chain when deepest level match is of same length.
	 */
	@Test
	public void testBestMatchAdvanced()
	{
		ScopeSelector metaTagBlockEntity = new ScopeSelector("meta.tag.block entity");
		ScopeSelector metaTagEntity = new ScopeSelector("meta.tag entity");
		// Order is important in inserting into list here!
		List<IScopeSelector> selectors = new ArrayList<IScopeSelector>();
		selectors.add(metaTagEntity);
		selectors.add(metaTagBlockEntity);
		assertEquals(metaTagBlockEntity, ScopeSelector.bestMatch(selectors,
				"text.html.markdown meta.disable-markdown meta.tag.block.any.html entity.name.tag.block.any.html"));
	}

	@Test
	public void testAPSTUD2790()
	{
		IScopeSelector entityName = new ScopeSelector("entity.name.tag");
		IScopeSelector doctype = new ScopeSelector(
				"entity.name.tag.doctype.html, meta.tag.sgml.html, string.quoted.double.doctype.identifiers-and-DTDs.html");

		String scope = "text.html.basic meta.tag.sgml.html meta.tag.sgml.doctype.html entity.name.tag.doctype.html";

		assertTrue(entityName.matches(scope));
		assertEquals(Arrays.asList(0, 0, 0, 15), entityName.getMatchResults());

		assertTrue(doctype.matches(scope));
		assertEquals(Arrays.asList(0, 0, 0, 28), doctype.getMatchResults());

		assertEquals(doctype, ScopeSelector.bestMatch(Arrays.asList(entityName, doctype), scope));
	}

	@Test
	public void testMatchWhenScopeHasSegmentsBetweenScopeSelectorSegments()
	{
		ScopeSelector textSourceSelector = new ScopeSelector("text source");
		assertTrue("Selector should match, but doesn't", textSourceSelector
				.matches("text.haml meta.line.ruby.haml source.ruby.embedded.haml comment.line.number-sign.ruby"));
		assertTrue("Selector should match, but doesn't",
				textSourceSelector.matches("text.haml meta.line.ruby.haml source.ruby.embedded.haml"));
	}

	@Test
	public void testBestMatchWhenScopeHasSegmentsBetweenScopeSelectorSegments()
	{
		String scope = "text.haml meta.line.ruby.haml source.ruby.embedded.haml";

		IScopeSelector textSourceSelector = new ScopeSelector("text source");
		IScopeSelector metaSourceSelector = new ScopeSelector("meta source");

		assertTrue("Selector should match, but doesn't", textSourceSelector.matches(scope));
		assertEquals(Arrays.asList(4, 0, 6), textSourceSelector.getMatchResults());

		assertTrue("Selector should match, but doesn't", metaSourceSelector.matches(scope));
		assertEquals(Arrays.asList(0, 4, 6), metaSourceSelector.getMatchResults());

		assertEquals(metaSourceSelector,
				ScopeSelector.bestMatch(Arrays.asList(textSourceSelector, metaSourceSelector), scope));
	}

	@Test
	public void testNegativeLookaheadAppliesAsANDToTrailingScopes()
	{
		String scope = "text.haml";

		IScopeSelector textMinusMetaSelector = new ScopeSelector("text -meta");
		IScopeSelector textMinusMetaSourceSelector = new ScopeSelector("text -meta source");
		IScopeSelector textSourceSelector = new ScopeSelector("text source");

		assertTrue("Selector should match, but doesn't", textMinusMetaSelector.matches(scope));
		assertEquals(Arrays.asList(4), textMinusMetaSelector.getMatchResults());

		assertTrue("Selector should match, but doesn't", textMinusMetaSourceSelector.matches(scope));
		assertEquals(Arrays.asList(4), textMinusMetaSourceSelector.getMatchResults());

		assertFalse("Selector shouldn't match, but does", textSourceSelector.matches(scope));

		// Last one wins
		assertEquals(textMinusMetaSourceSelector, ScopeSelector.bestMatch(
				Arrays.asList(textSourceSelector, textMinusMetaSelector, textMinusMetaSourceSelector), scope));
		assertEquals(textMinusMetaSelector, ScopeSelector.bestMatch(
				Arrays.asList(textMinusMetaSourceSelector, textSourceSelector, textMinusMetaSelector), scope));
	}

	@Test
	public void testAdvancedScenario()
	{
		String scope = "text.haml meta.line.ruby.haml source.ruby.embedded.haml comment.line.number-sign.ruby";

		IScopeSelector textSourceSelector = new ScopeSelector("text source");
		IScopeSelector metaSourceSelector = new ScopeSelector("meta source");
		IScopeSelector textMinusMetaSelector = new ScopeSelector("text -meta");
		IScopeSelector textMinusMetaSourceSelector = new ScopeSelector("text -meta source");

		assertTrue("Selector should match, but doesn't", textSourceSelector.matches(scope));
		assertEquals(Arrays.asList(4, 0, 6, 0), textSourceSelector.getMatchResults());

		assertTrue("Selector should match, but doesn't", metaSourceSelector.matches(scope));
		assertEquals(Arrays.asList(0, 4, 6, 0), metaSourceSelector.getMatchResults());

		assertFalse("Selector shouldn't match, but does", textMinusMetaSelector.matches(scope));
		assertFalse("Selector shouldn't match, but does", textMinusMetaSourceSelector.matches(scope));

		assertEquals(metaSourceSelector, ScopeSelector.bestMatch(Arrays.asList(textSourceSelector, metaSourceSelector,
				textMinusMetaSelector, textMinusMetaSourceSelector), scope));

		// Scope 2
		String scope2 = "text.haml meta.line.ruby.haml";

		assertFalse("Selector shouldn't match, but does", textSourceSelector.matches(scope2));
		assertFalse("Selector shouldn't match but does", metaSourceSelector.matches(scope2));
		assertFalse("Selector shouldn't match, but does", textMinusMetaSelector.matches(scope2));

		assertTrue("Selector should match, but doesn't", textMinusMetaSourceSelector.matches(scope2));
		assertEquals(Arrays.asList(4, 0), textMinusMetaSourceSelector.getMatchResults());

		assertEquals(textMinusMetaSourceSelector, ScopeSelector.bestMatch(Arrays.asList(textSourceSelector,
				metaSourceSelector, textMinusMetaSelector, textMinusMetaSourceSelector), scope2));
	}

	@Test
	public void testSortAfterMatching()
	{
		String scope = "text.haml meta.line.ruby.haml source.ruby.embedded.haml";

		IScopeSelector textSourceSelector = new ScopeSelector("text source");
		IScopeSelector metaSourceSelector = new ScopeSelector("meta source");
		IScopeSelector sourceSelector = new ScopeSelector("source");
		IScopeSelector sourceRubySelector = new ScopeSelector("source.ruby");

		assertTrue("Selector should match, but doesn't", textSourceSelector.matches(scope));
		assertEquals(Arrays.asList(4, 0, 6), textSourceSelector.getMatchResults());

		assertTrue("Selector should match, but doesn't", metaSourceSelector.matches(scope));
		assertEquals(Arrays.asList(0, 4, 6), metaSourceSelector.getMatchResults());

		assertTrue("Selector should match, but doesn't", sourceSelector.matches(scope));
		assertEquals(Arrays.asList(0, 0, 6), sourceSelector.getMatchResults());

		assertTrue("Selector should match, but doesn't", sourceRubySelector.matches(scope));
		assertEquals(Arrays.asList(0, 0, 11), sourceRubySelector.getMatchResults());

		// Generate a list of matches
		List<IScopeSelector> matches = Arrays.asList(metaSourceSelector, textSourceSelector, sourceRubySelector,
				sourceSelector);
		assertEquals(metaSourceSelector, matches.get(0));
		assertEquals(textSourceSelector, matches.get(1));
		assertEquals(sourceRubySelector, matches.get(2));
		assertEquals(sourceSelector, matches.get(3));

		// Now sort them...
		Collections.sort(matches);

		// Make sure they get sorted into "worst to best" match order.
		assertEquals(sourceSelector, matches.get(0));
		assertEquals(textSourceSelector, matches.get(1));
		assertEquals(metaSourceSelector, matches.get(2));
		assertEquals(sourceRubySelector, matches.get(3));
	}

	@Test
	public void testNegativeOr()
	{
		IScopeSelector textSourceSelector = new ScopeSelector("text.html - (source | string)");

		// test inside attribute string
		assertFalse("Selector shouldn't match, but does", textSourceSelector.matches(
				"text.html.basic meta.tag.any.html meta.attribute-with-value.id.html string.quoted.double.html punctuation.definition.string.end.html"));

		// test inside body of html
		assertTrue("Selector should match, but doesn't", textSourceSelector.matches("text.html.basic"));
		assertEquals(Arrays.asList(9), textSourceSelector.getMatchResults());

		// Test inside erb tag
		assertFalse("Selector shouldn't match, but does",
				textSourceSelector.matches("text.html.basic source.ruby.embedded.html"));
	}
}
