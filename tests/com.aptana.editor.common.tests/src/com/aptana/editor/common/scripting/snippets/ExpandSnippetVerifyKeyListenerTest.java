package com.aptana.editor.common.scripting.snippets;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.events.VerifyEvent;
import org.osgi.framework.Bundle;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.EditorBasedTests;
import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.SnippetElement;

public class ExpandSnippetVerifyKeyListenerTest extends EditorBasedTests
{

	public void testVerifyKeySpace() throws IOException
	{
		// try sending a space, nothing should happen
		assertVerifyKey("", "fun", ' ', true, true);
	}

	public void testVerifyKeyFunctionListenerOn() throws IOException
	{
		// full prefix, should pop CA
		assertVerifyKey("fun", "fun", '\t', true, false);
	}

	public void testVerifyKeyFunctionListenerOff() throws IOException
	{
		// full prefix, listener off, should not pop CA
		assertVerifyKey("fun", "fun", '\t', false, true);
	}

	public void testVerifyKeyFunctionIncorrectPrefix() throws IOException
	{
		// Should not pop CA here as prefix is not == snippet
		assertVerifyKey("fu", "fun", '\t', true, true);
	}

	protected void assertVerifyKey(String documentSource, String snippetTrigger, char typedChar,
			boolean listenerEnabled, boolean expectedOutcome) throws IOException
	{
		File bundleFile = File.createTempFile("common_projection_viewer_unit_tests", "rb");
		bundleFile.deleteOnExit();

		BundleElement bundleElement = new BundleElement(bundleFile.getAbsolutePath());
		bundleElement.setDisplayName("CommonProjectionViewerTest Unit Tests");

		File file = File.createTempFile("snippet", "rb");
		SnippetElement se = createSnippet(file.getAbsolutePath(), "FunctionTemplate", snippetTrigger, "",
				"text __dftl_partition_content_type");
		bundleElement.addChild(se);
		BundleManager.getInstance().addBundle(bundleElement);

		IFileStore fileStore = createFileStore("proposal_tests", "txt", documentSource);
		this.setupTestContext(fileStore);

		ITextViewer viewer = (ITextViewer) editor.getAdapter(ITextOperationTarget.class);
		SnippetsContentAssistant snipContentAssistant = new SnippetsContentAssistant();
		snipContentAssistant.install(viewer);
		ExpandSnippetVerifyKeyListener listener = new ExpandSnippetVerifyKeyListener(editor, viewer,
				snipContentAssistant);

		// modify snippet assistance
		listener.setEnabled(listenerEnabled);

		// test sending event
		VerifyEvent ve = createVerifyKeyEvent(typedChar, (int) typedChar, document.getLength());
		listener.verifyKey(ve);

		// doit == false means we've popped CA
		try
		{
			assertEquals(expectedOutcome, ve.doit);
		}
		finally
		{
			BundleManager.getInstance().unloadScript(file);
		}
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
