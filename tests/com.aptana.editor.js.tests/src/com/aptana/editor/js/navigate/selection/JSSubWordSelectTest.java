package com.aptana.editor.js.navigate.selection;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.junit.Before;
import org.junit.Test;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.tests.JSEditorBasedTestCase;

public class JSSubWordSelectTest extends JSEditorBasedTestCase
{
	private ISourceViewer viewer;
	private StyledText textWidget;

	@Before
	public void setUp()
	{
		this.setupTestContext("locations/camelCaseIdentifiers.js");
		viewer = ((AbstractThemeableEditor) editor).getISourceViewer();
		textWidget = viewer.getTextWidget();

		InstanceScope.INSTANCE.getNode(JSPlugin.PLUGIN_ID).putBoolean(IPreferenceConstants.EDITOR_SUB_WORD_NAVIGATION,
				true);
	}

	@Test
	public void testCamelCaseNavigation() throws Exception
	{
		textWidget.setCaretOffset(1);

		IAction action = editor.getAction(ITextEditorActionDefinitionIds.SELECT_WORD_NEXT);
		action.run();
		org.junit.Assert.assertEquals(5, viewer.getTextWidget().getCaretOffset());
		action.run();
		org.junit.Assert.assertEquals(9, viewer.getTextWidget().getCaretOffset());

		action = editor.getAction(ITextEditorActionDefinitionIds.SELECT_WORD_PREVIOUS);
		action.run();
		org.junit.Assert.assertEquals(5, viewer.getTextWidget().getCaretOffset());
		action.run();
		org.junit.Assert.assertEquals(0, viewer.getTextWidget().getCaretOffset());
	}

	@Test
	public void testFullWordNavigation() throws Exception
	{
		InstanceScope.INSTANCE.getNode(JSPlugin.PLUGIN_ID).putBoolean(IPreferenceConstants.EDITOR_SUB_WORD_NAVIGATION,
				false);

		textWidget.setCaretOffset(1);

		IAction action = editor.getAction(ITextEditorActionDefinitionIds.SELECT_WORD_NEXT);
		action.run();
		org.junit.Assert.assertEquals(13, viewer.getTextWidget().getCaretOffset());
	}

	@Test
	public void testChildMembersNavigation()
	{
		textWidget.setCaretOffset(20);
		IAction action = editor.getAction(ITextEditorActionDefinitionIds.SELECT_WORD_NEXT);
		action.run();
		org.junit.Assert.assertEquals(22, viewer.getTextWidget().getCaretOffset());
		action.run();
		org.junit.Assert.assertEquals(23, viewer.getTextWidget().getCaretOffset());

		action = editor.getAction(ITextEditorActionDefinitionIds.SELECT_WORD_PREVIOUS);
		action.run();
		org.junit.Assert.assertEquals(22, viewer.getTextWidget().getCaretOffset());
		action.run();
		org.junit.Assert.assertEquals(19, viewer.getTextWidget().getCaretOffset());
	}
}
