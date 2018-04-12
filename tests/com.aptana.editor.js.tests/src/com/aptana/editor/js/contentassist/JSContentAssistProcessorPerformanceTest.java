/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.test.performance.Performance;
import org.eclipse.test.performance.PerformanceMeter;
import org.eclipse.test.performance.PerformanceTestCase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;

import com.aptana.editor.js.tests.JSEditorBasedTestCase;
import com.aptana.index.core.IFileStoreIndexingParticipant;
import com.aptana.js.core.index.JSFileIndexingParticipant;
import com.aptana.testing.categories.PerformanceTests;
import com.aptana.ui.util.UIUtils;

@Category({ PerformanceTests.class })
public class JSContentAssistProcessorPerformanceTest extends JSEditorBasedTestCase
{
	@Rule
	public TestName name = new TestName();
	private PerformanceMeter fPerformanceMeter;

	@Before
	public void setUp() throws Exception
	{

		Performance performance = Performance.getDefault();
		fPerformanceMeter = performance
				.createPerformanceMeter(getClass().getName() + '#' + name.getMethodName() + "()");
	}

	@Override
	public void tearDown() throws Exception
	{
		try
		{
			fPerformanceMeter.dispose();
		}
		finally
		{
			super.tearDown();
		}
	}

	@Test
	public void testMathFunctions()
	{
		setupTestContext("contentAssist/math.js");

		ITextViewer viewer = new TextViewer(UIUtils.getActiveShell(), SWT.NONE);
		viewer.setDocument(this.document);

		for (int i = 0; i < 500; i++)
		{
			startMeasuring();
			processor.computeCompletionProposals(viewer, 5, '\0', false);
			stopMeasuring();
		}

		commitMeasurements();
		assertPerformance();
	}

	@Test
	public void testDocumentFunctions()
	{
		setupTestContext("contentAssist/document.js");

		ITextViewer viewer = new TextViewer(UIUtils.getActiveShell(), SWT.NONE);
		viewer.setDocument(this.document);

		for (int i = 0; i < 500; i++)
		{
			startMeasuring();
			processor.computeCompletionProposals(viewer, 5, '\0', false);
			stopMeasuring();
		}

		commitMeasurements();
		assertPerformance();
	}

	@Test
	public void testWindowFunctions()
	{
		setupTestContext("contentAssist/window.js");

		ITextViewer viewer = new TextViewer(UIUtils.getActiveShell(), SWT.NONE);
		viewer.setDocument(this.document);

		for (int i = 0; i < 500; i++)
		{
			startMeasuring();
			processor.computeCompletionProposals(viewer, 5, '\0', false);
			stopMeasuring();
		}

		commitMeasurements();
		assertPerformance();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.EditorBasedTests#createIndexer()
	 */
	@Override
	protected IFileStoreIndexingParticipant createIndexer()
	{
		return new JSFileIndexingParticipant();
	}

	/**
	 * Called from within a test case immediately before the code to measure is run. It starts capturing of performance
	 * data. Must be followed by a call to {@link PerformanceTestCase#stopMeasuring()} before subsequent calls to this
	 * method or {@link PerformanceTestCase#commitMeasurements()}.
	 * 
	 * @see PerformanceMeter#start()
	 */
	protected void startMeasuring()
	{
		fPerformanceMeter.start();
	}

	/**
	 * Called from within a test case immediately after the operation to measure. Must be preceded by a call to
	 * {@link PerformanceTestCase#startMeasuring()}, that follows any previous call to this method.
	 * 
	 * @see PerformanceMeter#stop()
	 */
	protected void stopMeasuring()
	{
		fPerformanceMeter.stop();
	}

	/**
	 * Called exactly once after repeated measurements are done and before their analysis. Afterwards
	 * {@link PerformanceTestCase#startMeasuring()} and {@link PerformanceTestCase#stopMeasuring()} must not be called.
	 * 
	 * @see PerformanceMeter#commit()
	 */
	protected void commitMeasurements()
	{
		fPerformanceMeter.commit();
	}

	/**
	 * Asserts default properties of the measurements captured for this test case.
	 * 
	 * @throws RuntimeException
	 *             if the properties do not hold
	 */
	protected void assertPerformance()
	{
		Performance.getDefault().assertPerformance(fPerformanceMeter);
	}
}
