package com.aptana.editor.common.text.reconciler;

import junit.framework.TestCase;

import org.eclipse.jface.text.TypedRegion;
import org.eclipse.ui.IPropertyListener;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

import com.aptana.editor.common.AbstractThemeableEditor;

public class CommonReconcilingStrategyTest extends TestCase
{

	private Mockery context;
	private AbstractThemeableEditor editor;

	protected void setUp() throws Exception
	{
		super.setUp();

		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
		editor = context.mock(AbstractThemeableEditor.class);
	}

	protected void tearDown() throws Exception
	{
		try
		{
			editor = null;
			context = null;
		}
		finally
		{
			super.tearDown();
		}
	}

	public void testNoOpOnIncrementalReconcile() throws Exception
	{
		context.checking(new Expectations()
		{
			{
				oneOf(editor).addPropertyListener(with(any(IPropertyListener.class)));
				// Make sure we never even try to get file service to parse/check folding/etc
				never(editor).isFoldingEnabled();
			}
		});

		CommonReconcilingStrategy strategy = new CommonReconcilingStrategy(editor);
		strategy.reconcile(new TypedRegion(0, 100, "fake_content_type"));
		context.assertIsSatisfied();
	}

}
