/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.outline;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.progress.WorkbenchJob;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.actions.BaseToggleLinkingAction;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.parsing.lexer.IRange;
import com.aptana.theme.ThemedDelegatingLabelProvider;
import com.aptana.ui.util.UIUtils;

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
			setImageDescriptor(UIUtils.getImageDescriptor(CommonEditorPlugin.PLUGIN_ID, ICON_PATH));

			setChecked(isSortingEnabled());
		}

		public void run()
		{
			fPrefs.setValue(IPreferenceConstants.SORT_OUTLINE_ALPHABETIC, isChecked());
		}
	}

	private static final String OUTLINE_CONTEXT = "com.aptana.editor.common.outline"; //$NON-NLS-1$
	private static final String INITIAL_FILTER_TEXT = Messages.CommonOutlinePage_InitialFilterText;
	private static final int FILTER_REFRESH_DELAY = 200;

	private AbstractThemeableEditor fEditor;

	private Composite fMainControl;
	private Text fSearchBox;
	private TreeViewer fTreeViewer;
	private ITreeContentProvider fContentProvider;
	private ILabelProvider fLabelProvider;

	private PatternFilter fFilter;
	private WorkbenchJob fFilterRefreshJob;
	private ToggleLinkingAction fToggleLinkingAction;
	private CommonOutlinePageInput fInput;

	private IPreferenceStore fPrefs;
	private ModifyListener fSearchModifyListener = new ModifyListener()
	{

		public void modifyText(ModifyEvent e)
		{
			String text = fSearchBox.getText();

			if (INITIAL_FILTER_TEXT.equals(text))
			{
				fFilter.setPattern(null);
			}
			else
			{
				fFilter.setPattern(text);
			}
			// refresh the content on a delay
			fFilterRefreshJob.cancel();
			fFilterRefreshJob.schedule(FILTER_REFRESH_DELAY);
		}
	};

	public CommonOutlinePage(AbstractThemeableEditor editor, IPreferenceStore prefs)
	{
		fEditor = editor;
		fPrefs = prefs;
		fContentProvider = new CommonOutlineContentProvider();
		fLabelProvider = new ThemedDelegatingLabelProvider(new CommonOutlineLabelProvider());
	}

	@Override
	public void createControl(Composite parent)
	{
		fMainControl = new Composite(parent, SWT.NONE);
		fMainControl.setLayout(GridLayoutFactory.fillDefaults().spacing(0, 2).create());
		fMainControl.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		fSearchBox = new Text(fMainControl, SWT.SINGLE | SWT.BORDER | SWT.SEARCH);
		fSearchBox.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).indent(0, 3).create());
		fSearchBox.setText(INITIAL_FILTER_TEXT);
		fSearchBox.setForeground(fSearchBox.getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
		fSearchBox.addModifyListener(fSearchModifyListener);
		fSearchBox.addFocusListener(new FocusListener()
		{

			public void focusLost(FocusEvent e)
			{
				if (fSearchBox.getText().length() == 0)
				{
					fSearchBox.removeModifyListener(fSearchModifyListener);
					fSearchBox.setText(INITIAL_FILTER_TEXT);
					fSearchBox.addModifyListener(fSearchModifyListener);
				}
				fSearchBox.setForeground(fSearchBox.getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
			}

			public void focusGained(FocusEvent e)
			{
				if (fSearchBox.getText().equals(INITIAL_FILTER_TEXT))
				{
					fSearchBox.removeModifyListener(fSearchModifyListener);
					fSearchBox.setText(StringUtil.EMPTY);
					fSearchBox.addModifyListener(fSearchModifyListener);
				}
				fSearchBox.setForeground(null);
			}
		});

		fTreeViewer = new TreeViewer(fMainControl, SWT.VIRTUAL | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		fTreeViewer.addSelectionChangedListener(this);
		fTreeViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		((IContextService) getSite().getService(IContextService.class)).activateContext(OUTLINE_CONTEXT);

		final TreeViewer viewer = getTreeViewer();
		viewer.setUseHashlookup(true);
		viewer.setContentProvider(fContentProvider);
		viewer.setLabelProvider(fLabelProvider);
		fInput = new CommonOutlinePageInput(fEditor.getAST());
		// Note: the input remains the same (we change its internal contents with a new ast and call refresh,
		// so that the outline structure is maintained).
		viewer.setInput(fInput);
		viewer.setComparator(isSortingEnabled() ? new ViewerComparator() : null);
		fFilter = new PatternFilter()
		{

			@Override
			protected boolean isLeafMatch(Viewer viewer, Object element)
			{
				String label = null;
				if (element instanceof CommonOutlineItem)
				{
					label = ((CommonOutlineItem) element).getLabel();
				}
				else if (element instanceof IParseNode)
				{
					label = ((IParseNode) element).getText();
				}

				if (label == null)
				{
					return true;
				}
				return wordMatches(label);
			}
		};
		fFilter.setIncludeLeadingWildcard(true);
		viewer.addFilter(fFilter);
		viewer.addDoubleClickListener(new IDoubleClickListener()
		{

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
		viewer.getTree().addKeyListener(new KeyListener()
		{

			public void keyPressed(KeyEvent e)
			{
			}

			public void keyReleased(KeyEvent e)
			{
				if (e.keyCode == '\r' && isLinkedWithEditor())
				{
					ISelection selection = viewer.getSelection();
					if (!selection.isEmpty() && selection instanceof IStructuredSelection)
					{
						IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
						if (page != null)
						{
							// brings editor to focus
							page.activate(fEditor);
							// deselects the current selection but keeps the cursor position
							Object widget = fEditor.getAdapter(Control.class);
							if (widget instanceof StyledText)
								fEditor.selectAndReveal(((StyledText) widget).getCaretOffset(), 0);
						}
					}
				}
			}
		});

		IActionBars actionBars = getSite().getActionBars();
		registerActions(actionBars);
		actionBars.updateActionBars();

		fPrefs.addPropertyChangeListener(this);
		fFilterRefreshJob = new WorkbenchJob("Refresh Filter") //$NON-NLS-1$
		{

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				if (isDisposed())
				{
					return Status.CANCEL_STATUS;
				}

				fTreeViewer.refresh();
				String text = fSearchBox.getText();
				if (!StringUtil.isEmpty(text) && !INITIAL_FILTER_TEXT.equals(text))
				{
					fTreeViewer.expandAll();
				}
				return Status.OK_STATUS;
			}
		};
		EclipseUtil.setSystemForJob(fFilterRefreshJob);
	}

	public void refresh(IParseRootNode ast)
	{
		// Just change the internal ast and call refresh, that way we keep
		// the expanded state of items.
		if (!isDisposed())
		{
			fInput.ast = ast;
			getTreeViewer().refresh();
		}
	}

	@Override
	public Control getControl()
	{
		if (fMainControl == null)
		{
			return null;
		}
		return fMainControl;
	}

	@Override
	public ISelection getSelection()
	{
		if (fTreeViewer == null)
		{
			return StructuredSelection.EMPTY;
		}
		return fTreeViewer.getSelection();
	}

	@Override
	public TreeViewer getTreeViewer()
	{
		return fTreeViewer;
	}

	@Override
	public void setFocus()
	{
		getControl().setFocus();
	}

	@Override
	public void setSelection(ISelection selection)
	{
		if (fTreeViewer != null)
		{
			fTreeViewer.setSelection(selection);
		}
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

	public void propertyChange(PropertyChangeEvent event)
	{
		String property = event.getProperty();

		if (property.equals(IPreferenceConstants.LINK_OUTLINE_WITH_EDITOR))
		{
			boolean isLinked = Boolean.parseBoolean(StringUtil.getStringValue(event.getNewValue()));

			fToggleLinkingAction.setChecked(isLinked);
			TreeViewer viewer = getTreeViewer();
			if (isLinked)
			{
				setEditorSelection((IStructuredSelection) viewer.getSelection(), false);
			}
		}
		else if (property.equals(IPreferenceConstants.SORT_OUTLINE_ALPHABETIC))
		{
			boolean sort = Boolean.parseBoolean(StringUtil.getStringValue(event.getNewValue()));
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

	public Object getOutlineItem(IParseNode node)
	{
		if (fContentProvider instanceof CommonOutlineContentProvider)
		{
			return ((CommonOutlineContentProvider) fContentProvider).getOutlineItem(node);
		}
		else
		{
			return null;
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
		Control control = getControl();
		return control == null || control.isDisposed();
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

	public IParseRootNode getCurrentAst()
	{
		if (fInput == null)
		{
			return null;
		}
		return fInput.ast;
	}

}
