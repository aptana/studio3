package com.aptana.editor.common.outline;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.actions.BaseToggleLinkingAction;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.parsing.lexer.ILexeme;

public class CommonOutlinePage extends ContentOutlinePage implements IPropertyChangeListener
{
	public class ToggleLinkingAction extends BaseToggleLinkingAction
	{
		public ToggleLinkingAction()
		{
			setChecked(isLinkedWithEditor());
		}

		@Override
		public void run()
		{
			fPrefs.setValue(IPreferenceConstants.LINK_OUTLINE_WITH_EDITOR, isChecked());
		}
	}

	private AbstractThemeableEditor fEditor;

	private ITreeContentProvider fContentProvider;
	private ILabelProvider fLabelProvider;

	private ToggleLinkingAction fToggleLinkingAction;

	private IPreferenceStore fPrefs;

	public CommonOutlinePage(AbstractThemeableEditor editor, IPreferenceStore prefs)
	{
		fEditor = editor;
		fPrefs = prefs;
		fContentProvider = new CommonOutlineContentProvider();
		fLabelProvider = new LabelProvider();

		// TODO: needs to be improved
		editor.getSourceViewerNonFinal().getTextWidget().addFocusListener(new FocusAdapter()
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
		viewer.setInput(fEditor);

		IActionBars actionBars = getSite().getActionBars();
		registerActions(actionBars);
		actionBars.updateActionBars();

		fPrefs.addPropertyChangeListener(this);
	}

	@Override
	public void dispose()
	{
		fPrefs.removePropertyChangeListener(this);
		super.dispose();
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event)
	{
		if (isLinkedWithEditor())
		{
			setEditorSelection((IStructuredSelection) event.getSelection(), true);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		if (event.getProperty().equals(IPreferenceConstants.LINK_OUTLINE_WITH_EDITOR))
		{
			boolean isLinked = ((Boolean) event.getNewValue()).booleanValue();

			fToggleLinkingAction.setChecked(isLinked);
			TreeViewer viewer = getTreeViewer();
			if (isLinked)
			{
				setEditorSelection((IStructuredSelection) viewer.getSelection(), false);
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

	public void select(Object element)
	{
		if (element != null && !isDisposed())
		{
			getTreeViewer().setSelection(new StructuredSelection(element));
		}
	}

	private boolean isDisposed()
	{
		TreeViewer viewer = getTreeViewer();
		return viewer == null || viewer.getControl() == null || viewer.getControl().isDisposed();
	}

	private void registerActions(IActionBars actionBars)
	{
		IMenuManager menu = actionBars.getMenuManager();

		fToggleLinkingAction = new ToggleLinkingAction();
		menu.add(fToggleLinkingAction);
	}

	private void setEditorSelection(IStructuredSelection selection, boolean checkIfActive)
	{
		if (selection.size() == 1)
		{
			Object element = selection.getFirstElement();
			if (element instanceof ILexeme)
			{
				// selects the range in the editor
				fEditor.select((ILexeme) element, checkIfActive);
			}
		}
	}

	private boolean isLinkedWithEditor()
	{
		return fPrefs.getBoolean(IPreferenceConstants.LINK_OUTLINE_WITH_EDITOR);
	}
}
