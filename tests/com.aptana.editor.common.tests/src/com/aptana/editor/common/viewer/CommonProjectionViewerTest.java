package com.aptana.editor.common.viewer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.formatter.FormattingContextProperties;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.Test;
import org.osgi.framework.Bundle;

import com.aptana.core.util.EclipseUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.CommonSourceViewerConfiguration;
import com.aptana.editor.common.EditorBasedTests;
import com.aptana.editor.common.ICommonConstants;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.formatter.ui.ScriptFormattingContextProperties;

/**
 * The class <code>CommonProjectionViewerTest</code> contains tests for the class
 * <code>{@link CommonProjectionViewer}</code>.
 */
public class CommonProjectionViewerTest extends EditorBasedTests
{

	/**
	 * testSnippetProposalActivation
	 * 
	 * @throws IOException
	 */
	@Test
	public void testSnippetProposalActivation() throws IOException
	{

		IFileStore fileStore = createFileStore("proposal_tests", "html", "");
		this.setupTestContext(fileStore);

		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(CommonEditorPlugin.PLUGIN_ID);
		int delay = prefs.getInt(IPreferenceConstants.CONTENT_ASSIST_DELAY,
				CommonSourceViewerConfiguration.DEFAULT_CONTENT_ASSIST_DELAY);

		// turn on content assist
		prefs.putInt(IPreferenceConstants.CONTENT_ASSIST_DELAY,
				CommonSourceViewerConfiguration.DEFAULT_CONTENT_ASSIST_DELAY);

		ITextViewer textViewer = (ITextViewer) editor.getAdapter(ITextOperationTarget.class);
		TestCommonSourceViewer csv = new TestCommonSourceViewer(CommonEditorPlugin.getDefault().getPreferenceStore(),
				editor);
		TestCommonProjectionViewer cpv = new TestCommonProjectionViewer(textViewer.getTextWidget().getParent(), null,
				null, false, 0);
		cpv.configure(csv);

		assertTrue(cpv.snippetsEnabled());

		// turn off content assist
		prefs.putInt(IPreferenceConstants.CONTENT_ASSIST_DELAY,
				CommonSourceViewerConfiguration.CONTENT_ASSIST_OFF_DELAY);

		assertFalse(cpv.snippetsEnabled());

		// reset content assist
		prefs.putInt(IPreferenceConstants.CONTENT_ASSIST_DELAY, delay);
	}

	@Test
	public void testCreateFormattingContextText()
	{
		IFileStore fileStore = createFileStore("proposal_tests", "txt", "");
		this.setupTestContext(fileStore);

		CommonProjectionViewer textViewer = (CommonProjectionViewer) editor.getAdapter(ITextOperationTarget.class);
		IFormattingContext context = textViewer.createFormattingContext();
		assertNotNull(context);
		assertNull(context.getProperty(ScriptFormattingContextProperties.CONTEXT_FORMATTER_ID));
		assertNull(context.getProperty(FormattingContextProperties.CONTEXT_PREFERENCES));
		context.dispose();
	}

	@Test
	public void testCreateFormattingContextHtml()
	{
		IFileStore fileStore = createFileStore("proposal_tests", "html", "");
		FileStoreEditorInput editorInput = new FileStoreEditorInput(fileStore);
		editor = this.createEditor(editorInput, "com.aptana.editor.html");

		CommonProjectionViewer textViewer = (CommonProjectionViewer) editor.getAdapter(ITextOperationTarget.class);
		IFormattingContext context = textViewer.createFormattingContext();
		assertNotNull(context);
		assertNotNull(context.getProperty(ScriptFormattingContextProperties.CONTEXT_FORMATTER_ID));
		assertNotNull(context.getProperty(FormattingContextProperties.CONTEXT_PREFERENCES));
		context.dispose();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.EditorBasedTests#getBundle()
	 */
	@Override
	protected Bundle getBundle()
	{
		return CommonEditorPlugin.getDefault().getBundle();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.EditorBasedTests#getPluginId()
	 */
	@Override
	protected String getEditorId()
	{
		// straight text editor
		return "com.aptana.editor.text";
	}

	protected class TestCommonProjectionViewer extends CommonProjectionViewer
	{

		public TestCommonProjectionViewer(Composite parent, IVerticalRuler ruler, IOverviewRuler overviewRuler,
				boolean showsAnnotationOverview, int styles)
		{
			super(parent, ruler, overviewRuler, showsAnnotationOverview, styles);
		}

		public boolean snippetsEnabled()
		{
			return fKeyListener.isEnabled();
		}

	}

	protected class TestCommonSourceViewer extends CommonSourceViewerConfiguration
	{

		protected TestCommonSourceViewer(IPreferenceStore preferenceStore, ITextEditor editor)
		{
			super(preferenceStore, (AbstractThemeableEditor) editor);
		}

		public String[][] getTopContentTypes()
		{
			return new String[][] { { ICommonConstants.CONTENT_TYPE_UKNOWN } };
		}

	}
}