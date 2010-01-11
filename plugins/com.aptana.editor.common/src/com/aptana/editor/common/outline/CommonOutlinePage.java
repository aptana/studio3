package com.aptana.editor.common.outline;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.parsing.lexer.ILexeme;

public class CommonOutlinePage extends ContentOutlinePage
{

	private AbstractThemeableEditor fEditor;

	private ITreeContentProvider fContentProvider;
	private ILabelProvider fLabelProvider;

	public CommonOutlinePage(AbstractThemeableEditor editor)
	{
		fEditor = editor;
		fContentProvider = new CommonOutlineContentProvider();
		fLabelProvider = new LabelProvider();

		// TODO: needs to be improved
		editor.getViewer().getTextWidget().addFocusListener(new FocusAdapter()
		{
			public void focusGained(FocusEvent e)
			{
				refresh();
			}
		});
	}

	@Override
	public void createControl(Composite parent)
	{
		super.createControl(parent);

		TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(fContentProvider);
		viewer.setLabelProvider(fLabelProvider);
		viewer.addSelectionChangedListener(this);
		viewer.setInput(fEditor);
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event)
	{
		IStructuredSelection selection = (IStructuredSelection) event.getSelection();
		if (selection.size() == 1)
		{
			Object element = selection.getFirstElement();
			if (element instanceof ILexeme)
			{
				// selects the range in the editor
				ILexeme symbol = (ILexeme) element;
				fEditor.selectAndReveal(symbol.getStartingOffset(), symbol.getLength());
			}
		}
	}

	public void refresh()
	{
		if (!isDisposed())
		{
			getTreeViewer().refresh();
		}
	}

	public void setContentProvider(ITreeContentProvider provider)
	{
		fContentProvider = provider;
		if (!isDisposed())
		{
			getTreeViewer().setContentProvider(fContentProvider);
		}
	}

	public void setLabelProvider(ILabelProvider provider)
	{
		fLabelProvider = provider;
		if (!isDisposed())
		{
			getTreeViewer().setLabelProvider(fLabelProvider);
		}
	}

	private boolean isDisposed()
	{
		TreeViewer viewer = getTreeViewer();
		return viewer == null || viewer.getControl() == null || viewer.getControl().isDisposed();
	}
}
