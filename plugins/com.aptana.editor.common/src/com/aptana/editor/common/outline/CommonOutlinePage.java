package com.aptana.editor.common.outline;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.actions.BaseToggleLinkingAction;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.editor.common.theme.IThemeManager;
import com.aptana.editor.common.theme.ThemedDelegatingLabelProvider;
import com.aptana.editor.common.theme.TreeThemer;
import com.aptana.parsing.lexer.IRange;

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

	private class SortingAction extends Action
	{
		private static final String ICON_PATH = "icons/sort_alphab.gif"; //$NON-NLS-1$

		public SortingAction()
		{
			setText(Messages.CommonOutlinePage_Sorting_LBL);
			setToolTipText(Messages.CommonOutlinePage_Sorting_TTP);
			setDescription(Messages.CommonOutlinePage_Sorting_Description);
			setImageDescriptor(CommonEditorPlugin.getImageDescriptor(ICON_PATH));

			setChecked(isSortingEnabled());
		}

		public void run()
		{
			fPrefs.setValue(IPreferenceConstants.SORT_OUTLINE_ALPHABETIC, isChecked());
		}
	}

	private static final String OUTLINE_CONTEXT = "com.aptana.editor.common.outline"; //$NON-NLS-1$

	private AbstractThemeableEditor fEditor;

	private ITreeContentProvider fContentProvider;
	private ILabelProvider fLabelProvider;

	private ToggleLinkingAction fToggleLinkingAction;

	private IPreferenceStore fPrefs;

	private TreeThemer treeThemer;

	public CommonOutlinePage(AbstractThemeableEditor editor, IPreferenceStore prefs)
	{
		fEditor = editor;
		fPrefs = prefs;
		fContentProvider = new CommonOutlineContentProvider();
		fLabelProvider = new ThemedDelegatingLabelProvider(new LabelProvider());
	}

	@Override
	public void createControl(Composite parent)
	{
		super.createControl(parent);
		((IContextService) getSite().getService(IContextService.class)).activateContext(OUTLINE_CONTEXT);

		final TreeViewer viewer = getTreeViewer();
		viewer.setAutoExpandLevel(2);
		viewer.setUseHashlookup(true);
		viewer.setContentProvider(fContentProvider);
		viewer.setLabelProvider(fLabelProvider);
		viewer.setInput(fEditor);
		viewer.setComparator(isSortingEnabled() ? new ViewerComparator() : null);
		viewer.addDoubleClickListener(new IDoubleClickListener()
		{

			@Override
			public void doubleClick(DoubleClickEvent event)
			{
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				// expands the selection one level if applicable
				viewer.expandToLevel(selection.getFirstElement(), 1);
				// selects the corresponding text in editor
				if (!isLinkedWithEditor())
				{
					setEditorSelection(selection, true);
				}
			}
		});

		hookToThemes();

		IActionBars actionBars = getSite().getActionBars();
		registerActions(actionBars);
		actionBars.updateActionBars();

		fPrefs.addPropertyChangeListener(this);
	}

	private void hookToThemes()
	{
		treeThemer = new TreeThemer(getTreeViewer());
		treeThemer.apply();
	}

	protected IThemeManager getThemeManager()
	{
		return CommonEditorPlugin.getDefault().getThemeManager();
	}

	@Override
	public void dispose()
	{
		treeThemer.dispose();
		treeThemer = null;
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
		String property = event.getProperty();

		if (property.equals(IPreferenceConstants.LINK_OUTLINE_WITH_EDITOR))
		{
			boolean isLinked = ((Boolean) event.getNewValue()).booleanValue();

			fToggleLinkingAction.setChecked(isLinked);
			TreeViewer viewer = getTreeViewer();
			if (isLinked)
			{
				setEditorSelection((IStructuredSelection) viewer.getSelection(), false);
			}
		}
		else if (property.equals(IPreferenceConstants.SORT_OUTLINE_ALPHABETIC))
		{
			boolean sort = ((Boolean) event.getNewValue()).booleanValue();
			getTreeViewer().setComparator(sort ? new ViewerComparator() : null);
		}
	}

	public void collapseAll()
	{
		if (!isDisposed())
		{
			getTreeViewer().collapseAll();
		}
	}

	public void expandAll()
	{
		if (!isDisposed())
		{
			getTreeViewer().expandAll();
		}
	}

	public void expandToLevel(int level)
	{
		if (!isDisposed())
		{
			getTreeViewer().expandToLevel(level);
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
		fLabelProvider = new ThemedDelegatingLabelProvider(provider);
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
		IToolBarManager toolBarManager = actionBars.getToolBarManager();
		if (toolBarManager != null)
		{
			toolBarManager.add(new SortingAction());
		}

		IMenuManager menu = actionBars.getMenuManager();

		fToggleLinkingAction = new ToggleLinkingAction();
		menu.add(fToggleLinkingAction);
	}

	private void setEditorSelection(IStructuredSelection selection, boolean checkIfActive)
	{
		if (selection.size() == 1)
		{
			Object element = selection.getFirstElement();
			if (element instanceof IRange)
			{
				// selects the range in the editor
				fEditor.select((IRange) element, checkIfActive);
			}
		}
	}

	private boolean isLinkedWithEditor()
	{
		return fPrefs.getBoolean(IPreferenceConstants.LINK_OUTLINE_WITH_EDITOR);
	}

	private boolean isSortingEnabled()
	{
		return fPrefs.getBoolean(IPreferenceConstants.SORT_OUTLINE_ALPHABETIC);
	}
}
