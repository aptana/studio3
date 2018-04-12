/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.outline;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.progress.WorkbenchJob;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.ui.util.UIUtils;

/**
 * @author Paul Colton
 * @author Kevin Sawicki
 * @author Kevin Lindsey
 * @author Chris Williams
 */
class CommonQuickOutlinePage extends ContentOutlinePage
{
	private static final int FILTER_REFRESH_DELAY = 200;
	private static final int REFRESH_DELAY = 500;

	/**
	 * Main container for the page.
	 */
	private Composite _composite;
	/**
	 * The active editor for this outline.
	 */
	private AbstractThemeableEditor _editor;
	/**
	 * The filter used to narrow down the {@link #_treeViewer}
	 */
	private PatternFilter _filter;
	/**
	 * The pattern used to generate the {@link #_filter}
	 */
	private String _pattern;
	/**
	 * Jobs used to refresh the tree after the filter changes or the document content changes.
	 */
	private WorkbenchJob _filterRefreshJob;
	private WorkbenchJob _delayedRefreshJob;
	/**
	 * The actual text field where the filter/search pattern is entered.
	 */
	private Text _searchBox;
	/**
	 * The Tree that holds the outline.
	 */
	private TreeViewer _treeViewer;
	/**
	 * Listener for document content changes to trigger {@link #_delayedRefreshJob}.
	 */
	private IDocumentListener _documentListener;

	private ToolBarManager _toolbarManager;

	/**
	 * UnifiedOutlinePage
	 * 
	 * @param editor
	 */
	public CommonQuickOutlinePage(AbstractThemeableEditor editor)
	{
		this._editor = editor;
	}

	/**
	 * createComposite
	 * 
	 * @param parent
	 * @return Composite
	 */
	private Composite createComposite(Composite parent)
	{
		GridLayoutFactory factory = GridLayoutFactory.fillDefaults().spacing(0, 0);
		Composite result = new Composite(parent, SWT.NONE);
		factory.applyTo(result);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(result);

		return result;
	}

	/**
	 * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent)
	{
		// create main container
		this._composite = createComposite(parent);

		// create tree view
		this._treeViewer = this.createTreeViewer(this._composite);

		final Tree tree = this._treeViewer.getTree();
		tree.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e)
			{
				if (e.character == 0x1B) // ESC
					dispose();
			}

			public void keyReleased(KeyEvent e)
			{
				// do nothing
			}
		});
		tree.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				// do nothing
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
				gotoSelectedElement();
			}
		});

		// apply tree filters
		this._filter = new PatternFilter()
		{
			/**
			 * @see org.eclipse.ui.dialogs.PatternFilter#isLeafMatch(org.eclipse.jface.viewers.Viewer, java.lang.Object)
			 */
			protected boolean isLeafMatch(Viewer viewer, Object element)
			{
				boolean result = true;
				String label = null;

				if (element instanceof CommonOutlineItem)
				{
					label = ((CommonOutlineItem) element).getLabel();
				}
				else if (element instanceof IParseNode)
				{
					label = ((IParseNode) element).getText();
				}

				if (label != null)
				{
					result = this.wordMatches(label);
				}

				return result;
			}
		};
		this._filter.setIncludeLeadingWildcard(true);
		this._treeViewer.addFilter(this._filter);

		// create filter refresh job
		this._filterRefreshJob = this.createRefreshJob();
		EclipseUtil.setSystemForJob(this._filterRefreshJob);

		// create delayed update job
		this._delayedRefreshJob = this.createDelayedRefreshJob();
		EclipseUtil.setSystemForJob(this._delayedRefreshJob);

		// create document change listener and add to editor
		this.createDocumentListener();
		IDocument document = getDocument();
		if (document != null)
		{
			document.addDocumentListener(this._documentListener);
		}

		// refresh tree
		this.refresh();
	}

	/**
	 * Reveals position.
	 * 
	 * @param documentPos
	 *            - document position.
	 */
	void revealPosition(int documentPos)
	{
		IStructuredContentProvider provider = (IStructuredContentProvider) getTreeViewer().getContentProvider();
		final Object[] originalElements = provider.getElements(_treeViewer.getInput());
		if (originalElements == null || originalElements.length == 0)
		{
			return;
		}

		// list of outline elements
		List<Object> elements = new ArrayList<Object>();

		// map from outline elements to the list of its parent elements
		// we need such a construction due to the fact that OutlineItem has no information about its parent
		final Map<Object, List<Object>> parents = new IdentityHashMap<Object, List<Object>>();

		for (Object el : originalElements)
		{
			elements.add(el);
		}

		if (provider instanceof ITreeContentProvider)
		{
			ITreeContentProvider treeContentProvider = (ITreeContentProvider) provider;
			for (Object element : originalElements)
			{
				expandElement(element, treeContentProvider, elements, parents);
			}
		}

		Object bestElement = null;
		int bestElementStartingOffset = -1;
		int bestElementEndingOffset = -1;

		for (Object element : elements)
		{
			int start = -1;
			int end = -1;
			if (element instanceof IParseNode)
			{
				IParseNode node = (IParseNode) element;
				start = node.getStartingOffset();
				end = node.getEndingOffset();
			}
			else if (element instanceof CommonOutlineItem)
			{
				int refNodeStart = ((CommonOutlineItem) element).getReferenceNode().getStartingOffset();
				int refNodeEnd = ((CommonOutlineItem) element).getReferenceNode().getEndingOffset();
				int elementStart = ((CommonOutlineItem) element).getStartingOffset();
				int elementEnd = ((CommonOutlineItem) element).getEndingOffset();
				start = (refNodeStart < elementStart) ? refNodeStart : elementStart;
				end = (refNodeEnd > elementEnd) ? refNodeEnd : elementEnd;
			}

			if (start != -1)
			{
				if (start <= documentPos && end >= documentPos)
				{
					// choosing the node having the least length
					if (bestElement == null || bestElementEndingOffset - bestElementStartingOffset > end - start)
					{
						bestElement = element;
						bestElementStartingOffset = start;
						bestElementEndingOffset = end;
					}
				}
			}
		}
		final Object toReveal = bestElement;
		WorkbenchJob job = new WorkbenchJob("Initial reveal") {//$NON-NLS-1$
			/**
			 * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
			 */
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				if (_treeViewer.getControl().isDisposed())
				{
					return Status.CANCEL_STATUS;
				}

				try
				{
					// don't want the user to see updates that will be made to the tree
					_treeViewer.getControl().setRedraw(false);
					_treeViewer.refresh(true);

					// _treeViewer.setSelection(new StructuredSelection(toReveal), true);
					List<Object> path = new ArrayList<Object>();
					List<Object> p = parents.get(toReveal);
					if (p != null)
					{
						path.addAll(p);
					}
					path.add(toReveal);
					TreePath treePath = new TreePath(path.toArray());
					_treeViewer.setSelection(new TreeSelection(treePath), true);
				}
				finally
				{
					// done updating the tree - set redraw back to true
					_treeViewer.getControl().setRedraw(true);
				}

				return Status.OK_STATUS;
			}
		};

		if (bestElement != null)
		{
			job.schedule(FILTER_REFRESH_DELAY);
		}
	}

	/**
	 * createDelayedRefreshJob
	 * 
	 * @return workbench job
	 */
	private WorkbenchJob createDelayedRefreshJob()
	{
		return new WorkbenchJob("Refresh Content") { //$NON-NLS-1$
			/**
			 * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
			 */
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				try
				{
					if (_treeViewer.getControl().isDisposed())
					{
						return Status.CANCEL_STATUS;
					}

					_treeViewer.setSelection(null);
					_treeViewer.refresh();
				}
				// SWT errors may be thrown here and will show as an error box since this is done on the UI thread
				// Catch everything and log it so that the dialog doesn't annoy the user since they may be typing into
				// the editor when this code throws errors and will impact them severely
				catch (Throwable e)
				{
					IdeLog.logError(CommonEditorPlugin.getDefault(), e);
				}

				return Status.OK_STATUS;
			}
		};
	}

	/**
	 * createDocumentListener
	 */
	private synchronized void createDocumentListener()
	{
		if (this._documentListener == null)
		{
			this._documentListener = new IDocumentListener()
			{
				/**
				 * @see org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
				 */
				public void documentAboutToBeChanged(DocumentEvent event)
				{
				}

				/**
				 * @see org.eclipse.jface.text.IDocumentListener#documentChanged(org.eclipse.jface.text.DocumentEvent)
				 */
				public void documentChanged(DocumentEvent event)
				{
					// cancel currently running job first, to prevent unnecessary redraw
					if (_delayedRefreshJob != null)
					{
						_delayedRefreshJob.cancel();
						_delayedRefreshJob.schedule(REFRESH_DELAY);
					}
				}
			};
		}
	}

	/**
	 * createRefreshJob
	 * 
	 * @return Workbench job
	 */
	private WorkbenchJob createRefreshJob()
	{
		return new WorkbenchJob("Refresh Filter") {//$NON-NLS-1$
			/**
			 * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
			 */
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				if (_treeViewer.getControl().isDisposed())
				{
					return Status.CANCEL_STATUS;
				}

				if (_pattern == null)
				{
					return Status.OK_STATUS;
				}

				_filter.setPattern(_pattern);

				try
				{
					// don't want the user to see updates that will be made to the tree
					_treeViewer.getControl().setRedraw(false);
					_treeViewer.refresh(true);

					if (_pattern.length() > 0)
					{
						/*
						 * Expand elements one at a time. After each is expanded, check to see if the filter text has
						 * been modified. If it has, then cancel the refresh job so the user doesn't have to endure
						 * expansion of all the nodes.
						 */
						IStructuredContentProvider provider = (IStructuredContentProvider) _treeViewer
								.getContentProvider();
						Object[] elements = provider.getElements(_treeViewer.getInput());

						for (int i = 0; i < elements.length; i++)
						{
							if (monitor.isCanceled())
							{
								return Status.CANCEL_STATUS;
							}

							_treeViewer.expandToLevel(elements[i], AbstractTreeViewer.ALL_LEVELS);
						}

						TreeItem[] items = _treeViewer.getTree().getItems();

						if (items.length > 0)
						{
							// to prevent scrolling
							_treeViewer.getTree().showItem(items[0]);
						}
					}
				}
				finally
				{
					// done updating the tree - set redraw back to true
					_treeViewer.getControl().setRedraw(true);
				}

				return Status.OK_STATUS;
			}

		};
	}

	/**
	 * Create search area
	 * 
	 * @param parent
	 *            - parent
	 * @return Composite
	 */
	Composite createSearchArea(Composite parent)
	{
		Composite top = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().spacing(0, 0).extendedMargins(0, 0, 3, 0).applyTo(top);
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		// create layout
		Composite result = new Composite(top, SWT.NONE);

		// assign layout and layout data
		GridLayoutFactory.fillDefaults().spacing(0, 0).numColumns(3).applyTo(result);
		GridDataFactory.fillDefaults().align(GridData.BEGINNING, GridData.FILL).grab(true, false).applyTo(result);

		// create text box
		this._searchBox = new Text(result, SWT.SINGLE | SWT.FOCUSED);
		this._searchBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		this._searchBox.setEditable(true);
		this._searchBox.addModifyListener(new ModifyListener()
		{
			/**
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			public void modifyText(ModifyEvent e)
			{
				textChanged();
			}
		});
		this._searchBox.addKeyListener(new KeyListener()
		{

			public void keyPressed(KeyEvent e)
			{
				if (e.keyCode == SWT.ARROW_UP || e.keyCode == SWT.ARROW_DOWN)
				{
					_treeViewer.getControl().setFocus();
				}
			}

			public void keyReleased(KeyEvent e)
			{
			}
		});

		ToolBar filtersToolBar = new ToolBar(result, SWT.HORIZONTAL);
		filtersToolBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		_toolbarManager = new ToolBarManager(filtersToolBar);

		_toolbarManager.update(false);
		_toolbarManager.getControl().update();

		return result;
	}

	/**
	 * createTreeViewer
	 * 
	 * @param parent
	 * @return TreeViewer
	 */
	private TreeViewer createTreeViewer(Composite parent)
	{
		TreeViewer result = new TreeViewer(new Tree(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL));
		Tree tree = result.getTree();
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = tree.getItemHeight() * 12;
		tree.setLayoutData(gd);

		result.setLabelProvider(_editor.getOutlineLabelProvider());
		result.setContentProvider(_editor.getOutlineContentProvider());
		result.setInput(this._editor);

		result.setComparer(new IElementComparer()
		{
			/**
			 * @see org.eclipse.jface.viewers.IElementComparer#equals(java.lang.Object, java.lang.Object)
			 */
			public boolean equals(Object a, Object b)
			{
				boolean result = false;

				if (a instanceof CommonOutlineItem && b instanceof CommonOutlineItem)
				{
					CommonOutlineItem item1 = (CommonOutlineItem) a;
					CommonOutlineItem item2 = (CommonOutlineItem) b;

					result = item1.equals(item2);
				}
				else if (a instanceof IParseNode && b instanceof IParseNode)
				{
					if (a == b)
					{
						result = true;
					}
					else
					{
						IParseNode node1 = (IParseNode) a;
						IParseNode node2 = (IParseNode) b;
						result = node1.equals(node2);
					}
				}
				else
				{
					result = (a == b);
				}

				return result;
			}

			/**
			 * @see org.eclipse.jface.viewers.IElementComparer#hashCode(java.lang.Object)
			 */
			public int hashCode(Object element)
			{
				return 0;
			}
		});

		return result;
	}

	/**
	 * @see org.eclipse.ui.part.Page#dispose()
	 */
	public void dispose()
	{
		if (this._documentListener != null)
		{
			IDocument document = getDocument();
			if (document != null)
			{
				document.removeDocumentListener(this._documentListener);
			}
			this._documentListener = null;
		}

		if (this._delayedRefreshJob != null)
		{
			this._delayedRefreshJob.cancel();
			this._delayedRefreshJob = null;
		}

		if (this._filterRefreshJob != null)
		{
			this._filterRefreshJob.cancel();
			this._filterRefreshJob = null;
		}

		if (this._toolbarManager != null)
		{
			this._toolbarManager.dispose();
			this._toolbarManager = null;
		}

		super.dispose();
	}

	private IDocument getDocument()
	{
		return this._editor.getDocumentProvider().getDocument(this._editor.getEditorInput());
	}

	/**
	 * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#getControl()
	 */
	public Control getControl()
	{
		return this._composite;
	}

	/**
	 * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#getTreeViewer()
	 */
	public TreeViewer getTreeViewer()
	{
		return this._treeViewer;
	}

	/**
	 * refresh
	 */
	private void refresh()
	{
		if (!_treeViewer.getControl().isDisposed())
		{
			this._treeViewer.refresh();
		}
	}

	/**
	 * Contributes actions to quick outline menu.
	 * 
	 * @param manager
	 *            - menu manager.
	 */
	void contributeToQuickOutlineMenu(IMenuManager manager)
	{
		// add sort action
		Action sortAction = new Action(Messages.CommonQuickOutlinePage_SortAlphabetically, Action.AS_CHECK_BOX)
		{
			public void run()
			{
				// Hide tree control during redraw
				getTreeViewer().getControl().setVisible(false);

				// Set the sorting according to whether this Action is checked/unchecked
				// TODO Store this persistently across quick outlines per-language?
				if (this.isChecked())
				{
					getTreeViewer().setComparator(new ViewerComparator());
				}
				else
				{
					getTreeViewer().setComparator(null);
				}

				// Show the tree control
				getTreeViewer().getControl().setVisible(true);
			}
		};
		sortAction.setImageDescriptor(UIUtils.getImageDescriptor(CommonEditorPlugin.PLUGIN_ID, "icons/sort.gif")); //$NON-NLS-1$
		sortAction.setToolTipText(Messages.CommonQuickOutlinePage_SortAlphabetically);
		// this._sortItem = new ActionContributionItem(sortAction);
		manager.add(new ActionContributionItem(sortAction));

		// add Collapse All action
		Action collapseAction = new Action(Messages.CommonQuickOutlinePage_CollapseAll, Action.AS_PUSH_BUTTON)
		{
			public void run()
			{
				getTreeViewer().collapseAll();
			}
		};
		collapseAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL));
		collapseAction.setToolTipText(Messages.CommonQuickOutlinePage_CollapseAll);
		manager.add(new ActionContributionItem(collapseAction));

		// Expand All action
		Action expandAction = new Action(Messages.CommonQuickOutlinePage_ExpandAll)
		{
			public void run()
			{
				getTreeViewer().expandAll();
			}
		};
		expandAction
				.setImageDescriptor(UIUtils.getImageDescriptor(CommonEditorPlugin.PLUGIN_ID, "icons/expandall.gif")); //$NON-NLS-1$
		expandAction.setToolTipText(Messages.CommonQuickOutlinePage_ExpandAll);
		manager.add(new ActionContributionItem(expandAction));
	}

	/**
	 * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#setFocus()
	 */
	public void setFocus()
	{
		this._treeViewer.getControl().setFocus();
	}

	/**
	 * Gets searhbox.
	 */
	Control getSearchBox()
	{
		return _searchBox;
	}

	/**
	 * textChanged
	 */
	private void textChanged()
	{
		// cancel currently running job first, to prevent unnecessary redraw
		this._filterRefreshJob.cancel();
		this._filterRefreshJob.schedule(FILTER_REFRESH_DELAY);

		// set current filter pattern
		this._pattern = this._searchBox.getText();
		this._filter.setPattern(this._pattern);
	}

	@Override
	public ISelection getSelection()
	{
		if (getTreeViewer() == null)
		{
			return StructuredSelection.EMPTY;
		}
		return getTreeViewer().getSelection();
	}

	private void gotoSelectedElement()
	{
		ISelection selection = getSelection();
		if (selection instanceof IStructuredSelection)
		{
			IStructuredSelection structured = (IStructuredSelection) selection;
			// If a node in the outline view is selected
			if (structured.size() == 1)
			{
				Object element = structured.getFirstElement();

				if (element instanceof CommonOutlineItem)
				{
					CommonOutlineItem item = (CommonOutlineItem) element;
					this._editor.selectAndReveal(item.getStartingOffset(), item.getLength());
					closeDialog();
				}
				else if (element instanceof IParseNode)
				{
					int position = ((IParseNode) element).getStartingOffset();
					this._editor.selectAndReveal(position, 0);
					closeDialog();
				}
				return;
			}
		}

		this._editor.getISourceViewer().removeRangeIndication();
	}

	private void closeDialog()
	{
		getControl().getParent().getShell().dispose();
	}

	/**
	 * Expands a tree element, also feels parents map.
	 * 
	 * @param element
	 *            - element to expand.
	 * @param treeContentProvider
	 *            - tree content provider.
	 * @param elements
	 *            - elements.
	 */
	private void expandElement(Object element, ITreeContentProvider treeContentProvider, List<Object> elements,
			Map<Object, List<Object>> parents)
	{
		// getting children
		Object[] children = treeContentProvider.getChildren(element);

		List<Object> elementParentsList = parents.get(element);

		for (Object child : children)
		{
			// adding child to the elements list
			elements.add(child);

			// filling parents list for the child
			List<Object> parentsList = new ArrayList<Object>();

			// adding list of parent's parents, if exists
			if (elementParentsList != null)
			{
				parentsList.addAll(elementParentsList);
			}

			// adding parent
			parentsList.add(element);
			parents.put(child, parentsList);
		}

		// expanding children
		for (Object child : children)
		{
			expandElement(child, treeContentProvider, elements, parents);
		}
	}
}
