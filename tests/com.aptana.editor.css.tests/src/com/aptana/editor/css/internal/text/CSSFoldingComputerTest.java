/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.internal.text;

import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Collection;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;

import com.aptana.core.util.EclipseUtil;
import com.aptana.css.core.parsing.CSSParser;
import com.aptana.editor.css.CSSPlugin;
import com.aptana.editor.css.preferences.IPreferenceConstants;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseRootNode;

public class CSSFoldingComputerTest
{

//	@Override
	@After
	public void tearDown() throws Exception
	{
		try
		{
			EclipseUtil.instanceScope().getNode(CSSPlugin.PLUGIN_ID)
					.remove(IPreferenceConstants.INITIALLY_FOLD_COMMENTS);
		}
		finally
		{
//			super.tearDown();
		}
	}

	@Test
	public void testBasicCSSFolding() throws Exception
	{
		String src = "body {\n" + "	color: red;\n" + "}\n" + "\n" + "div p {\n" + "	background-color: green;\n" + "}\n"
				+ "\n" + ".one-liner { color: orange; }\n" + "\n" + "#id { \n" + "	font-family: monospace;\n" + "}";

		Map<ProjectionAnnotation, Position> annotations = emitFoldingRegions(false, new NullProgressMonitor(), src);
		Collection<Position> positions = annotations.values();
		assertEquals(3, positions.size());
		assertTrue(positions.contains(new Position(0, 22)));
		assertTrue(positions.contains(new Position(23, 36)));
		assertTrue(positions.contains(new Position(91, 33))); // only can go so far as EOF
	}

	private Map<ProjectionAnnotation, Position> emitFoldingRegions(boolean initialReconcile, IProgressMonitor monitor,
			String document)
	{
		IParseState parseState = new ParseState(document);
		IParseRootNode ast;
		try
		{
			ast = parse(parseState);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
			return null;
		}
		try
		{
			return new CSSFoldingComputer(null, new Document(document)).emitFoldingRegions(initialReconcile, monitor,
					ast);
		}
		catch (BadLocationException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testCSSCommentFolding() throws Exception
	{
		String src = "/*\n * This is a comment.\n */\n";

		Map<ProjectionAnnotation, Position> annotations = emitFoldingRegions(false, new NullProgressMonitor(), src);
		Collection<Position> positions = annotations.values();
		assertEquals(1, positions.size());
		assertTrue(positions.contains(new Position(0, src.length())));
	}

	@Test
	public void testCSSCommentInitiallyFolded() throws Exception
	{
		String src = "/*\n * This is a comment.\n */\n";

		// Turn on initially folding comments
		EclipseUtil.instanceScope().getNode(CSSPlugin.PLUGIN_ID)
				.putBoolean(IPreferenceConstants.INITIALLY_FOLD_COMMENTS, true);

		Map<ProjectionAnnotation, Position> annotations = emitFoldingRegions(true, new NullProgressMonitor(), src);
		assertTrue(annotations.keySet().iterator().next().isCollapsed());

		// After initial reconcile, don't mark any collapsed
		annotations = emitFoldingRegions(false, new NullProgressMonitor(), src);
		assertFalse(annotations.keySet().iterator().next().isCollapsed());
	}

	@Test
	public void testCSSRuleInitiallyFolded() throws Exception
	{
		String src = "body {\n	color: red;\n}\n";

		// Turn on initially folding rules
		EclipseUtil.instanceScope().getNode(CSSPlugin.PLUGIN_ID)
				.putBoolean(IPreferenceConstants.INITIALLY_FOLD_RULES, true);

		Map<ProjectionAnnotation, Position> annotations = emitFoldingRegions(true, new NullProgressMonitor(), src);
		assertTrue(annotations.keySet().iterator().next().isCollapsed());

		// After initial reconcile, don't mark any collapsed
		annotations = emitFoldingRegions(false, new NullProgressMonitor(), src);
		assertFalse(annotations.keySet().iterator().next().isCollapsed());
	}

	@Test
	public void testMediaFolding() throws Exception
	{
		String src = "@media print {\n  body {\n    color: red;\n  }\n}\n";

		Map<ProjectionAnnotation, Position> annotations = emitFoldingRegions(false, new NullProgressMonitor(), src);
		Collection<Position> positions = annotations.values();
		assertEquals(2, positions.size());
		assertTrue(positions.contains(new Position(0, src.length())));
		assertTrue(positions.contains(new Position(17, 27)));
	}

	@Test
	public void testPageFolding() throws Exception
	{
		String src = "@page {\n  margin: 3cm;\n}\n";

		Map<ProjectionAnnotation, Position> annotations = emitFoldingRegions(false, new NullProgressMonitor(), src);
		Collection<Position> positions = annotations.values();
		assertEquals(1, positions.size());
		assertTrue(positions.contains(new Position(0, src.length())));
	}

	@Test
	public void testFontFaceFolding() throws Exception
	{
		String src = "@font-face {\n  font-family: Gentium;\n  src: url(http://site/fonts/Gentium.ttf);\n}\n";

		Map<ProjectionAnnotation, Position> annotations = emitFoldingRegions(false, new NullProgressMonitor(), src);
		Collection<Position> positions = annotations.values();
		assertEquals(1, positions.size());
		assertTrue(positions.contains(new Position(0, src.length())));
	}

	private IParseRootNode parse(IParseState parseState) throws Exception
	{
		return new CSSParser().parse(parseState).getRootNode();
	}
}
