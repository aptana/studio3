/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.aptana.editor.epl.tests;

import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.test.performance.Performance;
import org.eclipse.test.performance.PerformanceMeter;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.junit.experimental.categories.Category;

import com.aptana.testing.categories.PerformanceTests;

@Category({ PerformanceTests.class })
public abstract class OpenEditorTest extends TextPerformanceTestCase
{

	protected void measureOpenInEditor(IFile[] files, PerformanceMeter performanceMeter, boolean closeEach)
			throws PartInitException
	{
		for (int i = 0, n = files.length; i < n; i++)
		{
			performanceMeter.start();
			AbstractTextEditor editor = (AbstractTextEditor) EditorTestHelper.openInEditor(files[i], true);
			performanceMeter.stop();
			EditorTestHelper.joinReconciler(EditorTestHelper.getSourceViewer(editor), 100, 10000, 100);
			if (closeEach)
			{
				EditorTestHelper.closeEditor(editor);
				EditorTestHelper.runEventQueue(100);
			}
		}
		performanceMeter.commit();
		assertPerformance(performanceMeter);
	}

	protected void measureOpenInEditor(IPath file, PerformanceMeter performanceMeter) throws PartInitException
	{
		measureOpenInEditor(arrayOf(file, getWarmUpRuns()), Performance.getDefault().getNullPerformanceMeter(), true);
		measureOpenInEditor(arrayOf(file, getMeasuredRuns()), performanceMeter, true);
	}

	private IFile[] arrayOf(IPath file, int n)
	{
		IFile[] files = new IFile[n];
		Arrays.fill(files, ResourceTestHelper.findFile(file));
		return files;
	}
}