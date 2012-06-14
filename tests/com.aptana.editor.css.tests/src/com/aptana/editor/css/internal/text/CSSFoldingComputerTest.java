/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.internal.text;

import java.util.Collection;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;

import com.aptana.core.util.EclipseUtil;
import com.aptana.editor.common.text.reconciler.IFoldingComputer;
import com.aptana.editor.css.CSSPlugin;
import com.aptana.editor.css.parsing.CSSParser;
import com.aptana.editor.css.preferences.IPreferenceConstants;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;

public class CSSFoldingComputerTest extends TestCase
{

	private IFoldingComputer folder;

	@Override
	protected void tearDown() throws Exception
	{
		try
		{
			EclipseUtil.instanceScope().getNode(CSSPlugin.PLUGIN_ID).remove(IPreferenceConstants.INITIALLY_FOLD_COMMENTS);
		}
		finally
		{
			folder = null;
			super.tearDown();
		}
	}

	public void testBasicCSSFolding() throws Exception
	{
		String src = "body {\n" + "	color: red;\n" + "}\n" + "\n" + "div p {\n" + "	background-color: green;\n" + "}\n"
				+ "\n" + ".one-liner { color: orange; }\n" + "\n" + "#id { \n" + "	font-family: monospace;\n" + "}";
		folder = new CSSFoldingComputer(null, new Document(src))
		{
			protected IParseNode getAST()
			{
				IParseState parseState = new ParseState(getDocument().get());
				try
				{
					return parse(parseState);
				}
				catch (Exception e)
				{
					fail(e.getMessage());
				}
				return null;
			};
		};
		Map<ProjectionAnnotation, Position> annotations = folder.emitFoldingRegions(false, new NullProgressMonitor());
		Collection<Position> positions = annotations.values();
		assertEquals(3, positions.size());
		assertTrue(positions.contains(new Position(0, 22)));
		assertTrue(positions.contains(new Position(23, 36)));
		assertTrue(positions.contains(new Position(91, 33))); // only can go so far as EOF
	}

	public void testCSSCommentFolding() throws Exception
	{
		String src = "/*\n * This is a comment.\n */\n";
		folder = new CSSFoldingComputer(null, new Document(src))
		{
			protected IParseNode getAST()
			{
				IParseState parseState = new ParseState(getDocument().get());
				try
				{
					return parse(parseState);
				}
				catch (Exception e)
				{
					fail(e.getMessage());
				}
				return null;
			};
		};
		Map<ProjectionAnnotation, Position> annotations = folder.emitFoldingRegions(false, new NullProgressMonitor());
		Collection<Position> positions = annotations.values();
		assertEquals(1, positions.size());
		assertTrue(positions.contains(new Position(0, src.length())));
	}
	
	public void testCSSCommentInitiallyFolded() throws Exception
	{
		String src = "/*\n * This is a comment.\n */\n";
		folder = new CSSFoldingComputer(null, new Document(src))
		{
			protected IParseNode getAST()
			{
				IParseState parseState = new ParseState(getDocument().get());
				try
				{
					return parse(parseState);
				}
				catch (Exception e)
				{
					fail(e.getMessage());
				}
				return null;
			};
		};
		// Turn on initially folding comments
		EclipseUtil.instanceScope().getNode(CSSPlugin.PLUGIN_ID).putBoolean(IPreferenceConstants.INITIALLY_FOLD_COMMENTS, true);

		Map<ProjectionAnnotation, Position> annotations = folder.emitFoldingRegions(true, new NullProgressMonitor());
		assertTrue(annotations.keySet().iterator().next().isCollapsed());

		// After initial reconcile, don't mark any collapsed
		annotations = folder.emitFoldingRegions(false, new NullProgressMonitor());
		assertFalse(annotations.keySet().iterator().next().isCollapsed());
	}

	public void testCSSRuleInitiallyFolded() throws Exception
	{
		String src = "body {\n	color: red;\n}\n";
		folder = new CSSFoldingComputer(null, new Document(src))
		{
			protected IParseNode getAST()
			{
				IParseState parseState = new ParseState(getDocument().get());
				try
				{
					return parse(parseState);
				}
				catch (Exception e)
				{
					fail(e.getMessage());
				}
				return null;
			};
		};
		// Turn on initially folding rules
		EclipseUtil.instanceScope().getNode(CSSPlugin.PLUGIN_ID).putBoolean(IPreferenceConstants.INITIALLY_FOLD_RULES, true);

		Map<ProjectionAnnotation, Position> annotations = folder.emitFoldingRegions(true, new NullProgressMonitor());
		assertTrue(annotations.keySet().iterator().next().isCollapsed());

		// After initial reconcile, don't mark any collapsed
		annotations = folder.emitFoldingRegions(false, new NullProgressMonitor());
		assertFalse(annotations.keySet().iterator().next().isCollapsed());
	}

	public void testMediaFolding() throws Exception
	{
		String src = "@media print {\n  body {\n    color: red;\n  }\n}\n";
		folder = new CSSFoldingComputer(null, new Document(src))
		{
			protected IParseNode getAST()
			{
				IParseState parseState = new ParseState(getDocument().get());
				try
				{
					return parse(parseState);
				}
				catch (Exception e)
				{
					fail(e.getMessage());
				}
				return null;
			};
		};
		Map<ProjectionAnnotation, Position> annotations = folder.emitFoldingRegions(false, new NullProgressMonitor());
		Collection<Position> positions = annotations.values();
		assertEquals(2, positions.size());
		assertTrue(positions.contains(new Position(0, src.length())));
		assertTrue(positions.contains(new Position(17, 27)));
	}
	
	public void testPageFolding() throws Exception
	{
		String src = "@page {\n  margin: 3cm;\n}\n";
		folder = new CSSFoldingComputer(null, new Document(src))
		{
			protected IParseNode getAST()
			{
				IParseState parseState = new ParseState(getDocument().get());
				try
				{
					return parse(parseState);
				}
				catch (Exception e)
				{
					fail(e.getMessage());
				}
				return null;
			};
		};
		Map<ProjectionAnnotation, Position> annotations = folder.emitFoldingRegions(false, new NullProgressMonitor());
		Collection<Position> positions = annotations.values();
		assertEquals(1, positions.size());
		assertTrue(positions.contains(new Position(0, src.length())));
	}
	
	public void testFontFaceFolding() throws Exception
	{
		String src = "@font-face {\n  font-family: Gentium;\n  src: url(http://site/fonts/Gentium.ttf);\n}\n";
		folder = new CSSFoldingComputer(null, new Document(src))
		{
			protected IParseNode getAST()
			{
				IParseState parseState = new ParseState(getDocument().get());
				try
				{
					return parse(parseState);
				}
				catch (Exception e)
				{
					fail(e.getMessage());
				}
				return null;
			};
		};
		Map<ProjectionAnnotation, Position> annotations = folder.emitFoldingRegions(false, new NullProgressMonitor());
		Collection<Position> positions = annotations.values();
		assertEquals(1, positions.size());
		assertTrue(positions.contains(new Position(0, src.length())));
	}

	private IParseRootNode parse(IParseState parseState) throws Exception
	{
		return new CSSParser().parse(parseState).getRootNode();
	}
}
