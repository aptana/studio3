/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.internal.peer;

import java.net.URI;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.ui.ide.IDE;
import org.junit.experimental.categories.Category;

import com.aptana.core.tests.GlobalTimePerformanceTestCase;
import com.aptana.core.util.ResourceUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.testing.categories.PerformanceTests;
import com.aptana.ui.util.UIUtils;

@Category({PerformanceTests.class})
public class CharacterPairMatcherPerfTest extends GlobalTimePerformanceTestCase
{
	private static final char[] pairs = new char[] { '(', ')', '{', '}', '[', ']', '`', '`', '\'', '\'', '"', '"' };
	private ICharacterPairMatcher matcher;

	@Override
	protected void setUp() throws Exception
	{
		matcher = new CharacterPairMatcher(pairs);
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception
	{
		if (matcher != null)
		{
			matcher.dispose();
		}
		matcher = null;
		super.tearDown();
	}

	public void testPairMatching() throws Exception
	{
		URL url = FileLocator.find(Platform.getBundle("com.aptana.editor.common.tests"),
				Path.fromPortableString("performance/jquery-1.6.4.js"), null);
		URI uri = ResourceUtil.resourcePathToURI(url);
		AbstractThemeableEditor editorPart = (AbstractThemeableEditor) IDE.openEditor(UIUtils.getActivePage(), uri,
				"com.aptana.editor.js", true);
		IDocument document = editorPart.getDocumentProvider().getDocument(editorPart.getEditorInput());
		for (int i = 0; i < 6400; i++)
		{
			startMeasuring();
			IRegion match = matcher.match(document, 367); // match the opening paren just before function.
			matcher.match(document, 368); // match the opening paren just before function.
			matcher.match(document, match.getOffset() + match.getLength());
			stopMeasuring();
		}
		commitMeasurements();
		assertPerformance();
	}
}
