package com.aptana.editor.common.scripting.snippets;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.events.VerifyEvent;
import org.junit.Test;
import org.osgi.framework.Bundle;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.FileUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.EditorBasedTests;
import com.aptana.scripting.model.CommandElement;

public class ExpandSnippetVerifyKeyListenerTest extends EditorBasedTests
{

	@Test
	public void testVerifyKeySpace() throws IOException
	{
		// try sending a space, nothing should happen
		assertVerifyKey("", "fun", ' ', true, true);
	}

	@Test
	public void testVerifyKeyFunctionListenerOn() throws IOException
	{
		// full prefix, should pop CA
		assertVerifyKey("fun", "fun", '\t', true, false);
	}

	@Test
	public void testVerifyKeyFunctionListenerOff() throws IOException
	{
		// full prefix, listener off, should not pop CA
		assertVerifyKey("fun", "fun", '\t', false, true);
	}

	@Test
	public void testVerifyKeyFunctionIncorrectPrefix() throws IOException
	{
		// Should not pop CA here as prefix is not == snippet
		assertVerifyKey("fu", "fun", '\t', true, true);
	}

	protected void assertVerifyKey(String documentSource, String snippetTrigger, char typedChar,
			boolean listenerEnabled, boolean expectedOutcome) throws IOException
	{

		File file = FileUtil.createTempFile("snippet", "rb");
		final CommandElement se = createSnippet(file.getAbsolutePath(), "FunctionTemplate", snippetTrigger, "", "text");

		IFileStore fileStore = createFileStore("proposal_tests", "txt", documentSource);
		this.setupTestContext(fileStore);

		ITextViewer viewer = (ITextViewer) editor.getAdapter(ITextOperationTarget.class);
		SnippetsContentAssistant snipContentAssistant = new SnippetsContentAssistant();
		snipContentAssistant.install(viewer);
		ExpandSnippetVerifyKeyListener listener = new ExpandSnippetVerifyKeyListener(editor, viewer,
				snipContentAssistant)
		{
			@Override
			protected List<CommandElement> getSnippetsInScope(int caretOffset)
			{
				return CollectionsUtil.newList(se);
			}
		};

		// modify snippet assistance
		listener.setEnabled(listenerEnabled);

		// test sending event
		VerifyEvent ve = createVerifyKeyEvent(typedChar, (int) typedChar, document.getLength());
		listener.verifyKey(ve);

		// doit == false means we've popped CA
		assertEquals(expectedOutcome, ve.doit);
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
}
