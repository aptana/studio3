package com.aptana.editor.js.navigate.selection;

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Event;
import org.junit.Before;
import org.junit.Test;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.js.tests.JSEditorBasedTestCase;

public class JSSubWordSelectTest extends JSEditorBasedTestCase
{
	private ISourceViewer viewer;
	private StyledText textWidget;
	private Event selectNextEvent;
	private Event selectPreviousEvent;

	@Before
	public void setUp()
	{
		this.setupTestContext("locations/camelCaseIdentifiers.js");
		viewer = ((AbstractThemeableEditor) editor).getISourceViewer();
		textWidget = viewer.getTextWidget();
		selectNextEvent = new Event();
		selectNextEvent.detail = SWT.TRAVERSE_ARROW_NEXT;
		selectNextEvent.keyCode = 16777220;
		selectNextEvent.stateMask = SWT.ALT | SWT.SHIFT;
		selectNextEvent.widget = textWidget;

		selectPreviousEvent = new Event();
		selectPreviousEvent.detail = SWT.TRAVERSE_ARROW_PREVIOUS;
		selectPreviousEvent.keyCode = 16777219;
		selectPreviousEvent.stateMask = SWT.ALT | SWT.SHIFT;
		selectPreviousEvent.widget = textWidget;
	}

	@Test
	public void testCamelCaseNavigation()
	{
		textWidget.setCaretOffset(1);
		textWidget.traverse(SWT.TRAVERSE_ARROW_NEXT, new KeyEvent(selectNextEvent));
		org.junit.Assert.assertEquals(5, viewer.getTextWidget().getCaretOffset());
		textWidget.notifyListeners(SWT.KeyDown, selectNextEvent);
		org.junit.Assert.assertEquals(9, viewer.getTextWidget().getCaretOffset());

		textWidget.traverse(SWT.TRAVERSE_ARROW_PREVIOUS, new KeyEvent(selectPreviousEvent));
		org.junit.Assert.assertEquals(5, viewer.getTextWidget().getCaretOffset());
		textWidget.notifyListeners(SWT.KeyDown, selectPreviousEvent);
		org.junit.Assert.assertEquals(0, viewer.getTextWidget().getCaretOffset());
	}

	@Test
	public void testChildMembersNavigation()
	{
		textWidget.setCaretOffset(20);
		textWidget.traverse(SWT.TRAVERSE_ARROW_NEXT, new KeyEvent(selectNextEvent));
		org.junit.Assert.assertEquals(22, viewer.getTextWidget().getCaretOffset());
		textWidget.notifyListeners(SWT.KeyDown, selectNextEvent);
		org.junit.Assert.assertEquals(23, viewer.getTextWidget().getCaretOffset());

		textWidget.traverse(SWT.TRAVERSE_ARROW_PREVIOUS, new KeyEvent(selectPreviousEvent));
		org.junit.Assert.assertEquals(22, viewer.getTextWidget().getCaretOffset());
		textWidget.notifyListeners(SWT.KeyDown, selectPreviousEvent);
		org.junit.Assert.assertEquals(19, viewer.getTextWidget().getCaretOffset());
	}
}
