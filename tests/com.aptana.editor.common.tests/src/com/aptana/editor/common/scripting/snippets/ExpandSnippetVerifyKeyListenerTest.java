package com.aptana.editor.common.scripting.snippets;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.events.VerifyEvent;
import org.junit.Test;
import org.osgi.framework.Bundle;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.EditorBasedTests;
import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.SnippetElement;

public class ExpandSnippetVerifyKeyListenerTest extends EditorBasedTests
{

	@Test
	public void testExpandSnippetVerifyKeyListener()
	{
	}

	@Test
	public void testVerifyKey() throws IOException
	{
		File bundleFile = File.createTempFile("common_projection_viewer_unit_tests", "rb");
		bundleFile.deleteOnExit();

		BundleElement bundleElement = new BundleElement(bundleFile.getAbsolutePath());
		bundleElement.setDisplayName("CommonProjectionViewerTest Unit Tests");

		File file = File.createTempFile("snippet", "rb");
		SnippetElement se = createSnippet(file.getAbsolutePath(), "FunctionTemplate", "fun", "function",
				"text __dftl_partition_content_type");
		bundleElement.addChild(se);
		BundleManager.getInstance().addBundle(bundleElement);

		IFileStore fileStore = createFileStore("proposal_tests", "txt", "");
		this.setupTestContext(fileStore);

		ITextViewer viewer = (ITextViewer) editor.getAdapter(ITextOperationTarget.class);
		SnippetsContentAssistant snipContentAssistant = new SnippetsContentAssistant();
		snipContentAssistant.install(viewer);
		ExpandSnippetVerifyKeyListener listener = new ExpandSnippetVerifyKeyListener(editor, viewer,
				snipContentAssistant);

		// test sending something besides a \t
		listener.verifyKey(createVerifyKeyEvent(' ', 32, 1));

		// turn on snippet assistance
		listener.setEnabled(true);
		document.set("fun");
		VerifyEvent ve = createVerifyKeyEvent('\t', 9, 3);
		listener.verifyKey(ve);

		try
		{
			// doit == false means we've popped CA
			assertFalse(ve.doit);

			// reset document. Should not pop CA here as prefix is not == snippet
			document.set("fu");

			ve = createVerifyKeyEvent('\t', 9, 2);
			listener.verifyKey(ve);

			// doit == true means we've not popped CA
			assertTrue(ve.doit);

			// reset document
			document.set("fun");

			// turn off content assist
			listener.setEnabled(false);

			ve = createVerifyKeyEvent('\t', 9, 3);
			listener.verifyKey(ve);

			// doit == true means we've not popped CA
			assertTrue(ve.doit);

		}
		finally
		{
			BundleManager.getInstance().unloadScript(file);
		}
	}

	@Test
	public void testHasMatchingSnippet()
	{
	}

	@Test
	public void testSetEnabled()
	{
	}

	@Test
	public void testGetDocumentScopeManager()
	{
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
	protected String getPluginId()
	{
		// straight text editor
		return "com.aptana.editor.text";
	}
}
