/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.folding;

import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.FileStoreEditorInput;

import com.aptana.core.util.IOUtil;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.editor.common.text.reconciler.CommonReconciler;
import com.aptana.editor.common.text.reconciler.CommonReconcilingStrategy;
import com.aptana.editor.common.text.reconciler.CompositeReconcilingStrategy;
import com.aptana.editor.epl.tests.EditorTestHelper;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.JSSourceEditor;
import com.aptana.editor.js.JSSourceViewerConfiguration;
import com.aptana.js.core.parsing.ast.JSCommentNode;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ParsingEngine;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.parsing.util.ParseUtil;
import com.aptana.ui.util.UIUtils;

public class JSEditorFoldingTest
{
	private JSSourceEditor editor;

//	@Override
	@After
	public void tearDown() throws Exception
	{
		if (editor != null)
		{
			EditorTestHelper.closeEditor(editor);
			editor = null;
		}
//		super.tearDown();
	}

	@Test
	public void testExecute() throws Exception
	{
		IWorkbenchPage page = UIUtils.getActivePage();
		String contents = "/*comment\ncomment*/\nfunction myfunc(){\nvar a = 10\n};";
		editor = (JSSourceEditor) page.openEditor(new FileStoreEditorInput(getFileStore(contents)),
				"com.aptana.editor.js");
		JSSourceViewerConfiguration jsSourceViewerConfiguration = editor.getJSSourceViewerConfiguration();
		CommonReconciler reconciler = (CommonReconciler) jsSourceViewerConfiguration.getReconciler(editor
				.getISourceViewer());
		// Get the default strategy (i.e.: no match means default will be returned).
		CompositeReconcilingStrategy strategy = (CompositeReconcilingStrategy) reconciler
				.getReconcilingStrategy("default_strategy");

		// Accessing private field with the strategies in unit-test.
		Field field = CompositeReconcilingStrategy.class.getDeclaredField("fStrategies");
		field.setAccessible(true);
		IReconcilingStrategy[] fStrategies = (IReconcilingStrategy[]) field.get(strategy);
		CommonReconcilingStrategy commonStrategy = null;
		for (IReconcilingStrategy iReconcilingStrategy : fStrategies)
		{
			if (iReconcilingStrategy instanceof CommonReconcilingStrategy)
			{
				commonStrategy = (CommonReconcilingStrategy) iReconcilingStrategy;
				break;
			}
		}

		try
		{

			// Reconcile and check if we have 2 entries (for comments and for the function).
			commonStrategy.setDocument(editor.getDocumentProvider().getDocument(editor.getEditorInput()));
			commonStrategy.fullReconcile();
			Map<ProjectionAnnotation, Position> positions = commonStrategy.getPositions();
			setFoldingEnabled(true);
			assertTrue(editor.isFoldingEnabled());
			assertEquals(2, positions.size());
			Collection<Position> values = positions.values();
			checkFound(values, 0, 20);
			checkFound(values, 20, 32);
			assertComments(editor.getAST(), true);

			setFoldingEnabled(false);

			// Clear the ast cache (as we may match a version with comments when asking for a version
			// without comments).
			ParserPoolFactory instance = ParserPoolFactory.getInstance();
			field = instance.getClass().getDeclaredField("fParsingEngine");
			field.setAccessible(true);
			ParsingEngine engine = (ParsingEngine) field.get(instance);
			engine.clearCache();

			commonStrategy.setDocument(editor.getDocumentProvider().getDocument(editor.getEditorInput()));
			commonStrategy.fullReconcile();
			positions = commonStrategy.getPositions();
			assertEquals(0, positions.size());
			assertComments(editor.getAST(), true);
		}
		finally
		{
			// Set default value again.
			IPreferenceStore preferenceStore = JSPlugin.getDefault().getPreferenceStore();
			preferenceStore.setToDefault(IPreferenceConstants.EDITOR_ENABLE_FOLDING);
		}
	}

	private void assertComments(IParseRootNode reconcileAST, boolean expectComment)
	{
		final boolean foundComment[] = new boolean[] { false };
		ParseUtil.treeApply(reconcileAST, new ParseUtil.IASTVisitor()
		{

			public boolean exitNode(IParseNode node)
			{
				return false;
			}

			public boolean enterNode(IParseNode node)
			{
				if (node instanceof JSCommentNode)
				{
					foundComment[0] = true;
				}
				return true;
			}
		});
		if (expectComment != foundComment[0])
		{
			fail("Expected to find comments: " + expectComment + ". Found comment: " + foundComment[0]);
		}
	}

	private void setFoldingEnabled(boolean b)
	{
		IPreferenceStore preferenceStore = JSPlugin.getDefault().getPreferenceStore();
		preferenceStore.setValue(IPreferenceConstants.EDITOR_ENABLE_FOLDING, b);
	}

	private void checkFound(Collection<Position> values, int offset, int len)
	{
		for (Position position : values)
		{
			if (position.offset == offset && position.length == len)
			{
				return;
			}
		}
		fail("Did not find position at: offset: " + offset + " length: " + len + " available: " + values);
	}

	protected IFileStore getFileStore(String contents) throws Exception
	{
		File file = File.createTempFile("TempFile", ".js");
		FileOutputStream output = new FileOutputStream(file);
		try
		{
			IOUtil.write(output, contents);
		}
		finally
		{
			output.close();
		}
		file.deleteOnExit();
		return EFS.getStore((file).toURI());
	}

}
