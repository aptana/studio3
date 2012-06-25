/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.yaml.internal.text;

import java.util.Collection;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;

import com.aptana.editor.common.text.reconciler.IFoldingComputer;
import com.aptana.editor.yaml.parsing.YAMLParser;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseRootNode;

public class YAMLFoldingComputerTest extends TestCase
{

	private IFoldingComputer folder;

	@Override
	protected void tearDown() throws Exception
	{
		folder = null;
		super.tearDown();
	}

	public void testBasicYAMLFolding() throws Exception
	{
		String src = "development:\n  adapter: mysql\n\ntest:\n  adapter: sqlite3\n"; //$NON-NLS-1$
		folder = new YAMLFoldingComputer(null, new Document(src));
		IParseState parseState = new ParseState(src);
		IParseRootNode ast = new YAMLParser().parse(parseState).getRootNode();
		Map<ProjectionAnnotation, Position> annotations = folder.emitFoldingRegions(false, new NullProgressMonitor(),
				ast);
		Collection<Position> positions = annotations.values();
		assertEquals(2, positions.size());
		assertTrue(positions.contains(new Position(0, 30)));
		assertTrue(positions.contains(new Position(31, 25)));
	}
}
