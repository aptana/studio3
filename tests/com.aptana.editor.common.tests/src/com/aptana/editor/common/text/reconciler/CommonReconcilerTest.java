package com.aptana.editor.common.text.reconciler;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TypedRegion;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

public class CommonReconcilerTest
{

	private Mockery context;
	private CommonReconcilingStrategy strategy;

	@Before
	public void setUp() throws Exception
	{
//		super.setUp();

		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
		strategy = context.mock(CommonReconcilingStrategy.class);
	}

	@After
	public void tearDown() throws Exception
	{
		try
		{
			strategy = null;
			context = null;
		}
		finally
		{
//			super.tearDown();
		}
	}

	@Test
	public void testCallsFullReconcileOnBatchReconcilingStrategiesAtEnd() throws Exception
	{
		final ITypedRegion[] partitions = new ITypedRegion[] { new TypedRegion(0, 4, "word"),
				new TypedRegion(4, 1, "whitespace"), new TypedRegion(5, 4, "word"),
				new TypedRegion(9, 1, "whitespace"), new TypedRegion(10, 8, "word") };
		context.checking(new Expectations()
		{
			{
				oneOf(strategy).setProgressMonitor(with(any(IProgressMonitor.class)));

				// Call incremental reconcile for each partition
				for (ITypedRegion partition : partitions)
				{
					oneOf(strategy).reconcile(partition);
				}

				// Make sure we call full reconcile once on the batch strategy
				oneOf(strategy).fullReconcile();
			}
		});
		final IDocument document = new Document("some fake contents");
		CommonReconciler reconciler = new CommonReconciler(strategy)
		{
			@Override
			protected ITypedRegion[] computePartitioning(int offset, int length)
			{
				return partitions;
			}

			@Override
			protected IDocument getDocument()
			{
				return document;
			}
		};
		reconciler.process(null);
		context.assertIsSatisfied();
	}

}
