/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.ToolBarManager;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.progress.WorkbenchJob;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.parsing.ast.IParseNode;

/**
 * @author Paul Colton
 * @author Kevin Sawicki
 * @author Kevin Lindsey
 */
public class CommonQuickOutlinePage extends ContentOutlinePage
{
	private static final int FILTER_REFRESH_DELAY = 200;
	private static final int REFRESH_DELAY = 500;

	private Composite _composite;
	private AbstractThemeableEditor _editor;
	private PatternFilter _filter;
	private String _pattern;
	private WorkbenchJob _filterRefreshJob;
	private WorkbenchJob _delayedRefreshJob;
	private Text _searchBox;
	private TreeViewer _treeViewer;
	private IDocumentListener _documentListener;
	private IDocument _document;

	private boolean _hide;

	// FIXME Re-enable sort/expand/collapse actions
	// private ActionContributionItem _sortItem;
	// private ActionContributionItem _collapseItem;
	// private ActionContributionItem _expandItem;
	// private ActionContributionItem _hidePrivateItem;
	// private ActionContributionItem _splitItem;

	private ActionContributionItem openAction;

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
		GridLayout contentAreaLayout = new GridLayout();

		contentAreaLayout.numColumns = 1;
		contentAreaLayout.makeColumnsEqualWidth = false;
		contentAreaLayout.marginHeight = 0;
		contentAreaLayout.marginWidth = 0;
		contentAreaLayout.verticalSpacing = 0;
		contentAreaLayout.horizontalSpacing = 0;

		Composite result = new Composite(parent, SWT.NONE);

		result.setLayout(contentAreaLayout);
		result.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		return result;
	}

	/**
	 * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent)
	{
		createControl(parent, true);
	}

	/**
	 * Creates page control.
	 * 
	 * @param parent
	 *            - page parent.
	 * @param createSearchArea
	 *            - whether to create search area or it would be created separately through calling
	 *            {@link CommonQuickOutlinePage#createSearchArea(Composite)} by a third party.
	 */
	public void createControl(Composite parent, boolean createSearchArea)
	{
		// create main container
		this._composite = createComposite(parent);

		if (createSearchArea)
		{
			// create top strip and search area
			this.createSearchArea(this._composite, false);
		}

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
				// FIXME How come return/enter don't select the element?!
				gotoSelectedElement();
			}
		});

		// apply tree filters
		// this._treeViewer.addFilter(new UnifiedViewerFilter(this));
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
				// else if (element instanceof IParseNode)
				// {
				// label = UnifiedOutlineProvider.getInstance().getText(element);
				// }

				if (label != null)
				{
					result = this.wordMatches(label);
				}

				return result;
			}
		};
		this._treeViewer.addFilter(this._filter);

		// add filters
		// for (BaseFilter filter : this._filters)
		// {
		// this._treeViewer.addFilter(filter);
		// }

		// create filter refresh job
		this._filterRefreshJob = this.createRefreshJob();
		this._filterRefreshJob.setSystem(true);

		// create delayed update job
		this._delayedRefreshJob = this.createDelayedRefreshJob();
		this._delayedRefreshJob.setSystem(true);

		// create document change listener and add to editor
		this.createDocumentListener();
		this._document = this._editor.getDocumentProvider().getDocument(this._editor.getEditorInput());
		this._document.addDocumentListener(this._documentListener);

		// refresh tree
		this.refresh();
	}

	/**
	 * Reveals position.
	 * 
	 * @param documentPos
	 *            - document position.
	 */
	public void revealPosition(int documentPos)
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

		// TreePath path = new TreePath(new Object[]{elements.get(0), elements.get(4)});
		// _treeViewer.setSelection(new TreeSelection(path), true);

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
				start = refNodeStart < elementStart ? refNodeStart : elementStart;
				end = refNodeEnd > elementEnd ? refNodeEnd : elementEnd;
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
				catch (Exception e)
				{
					// IdeLog.logError(UnifiedEditorsPlugin.getDefault(),
					// Messages.UnifiedOutlinePage_ErrorRefreshingOutline, e);
				}
				catch (Error e)
				{
					// IdeLog.logError(UnifiedEditorsPlugin.getDefault(),
					// Messages.UnifiedOutlinePage_ErrorRefreshingOutline, e);
				}

				return Status.OK_STATUS;
			}
		};
	}

	/**
	 * createDocumentListener
	 */
	private void createDocumentListener()
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
	 * @param embedded
	 *            - whether to create embedded search area.
	 * @return Composite
	 */
	public Composite createSearchArea(Composite parent, boolean embedded)
	{
		GridLayout contentAreaLayout = new GridLayout();

		contentAreaLayout.numColumns = 1;
		contentAreaLayout.makeColumnsEqualWidth = false;
		contentAreaLayout.marginHeight = 3;
		contentAreaLayout.marginWidth = 0;
		contentAreaLayout.verticalSpacing = 0;
		contentAreaLayout.horizontalSpacing = 0;

		Composite top = new Composite(parent, SWT.NONE);

		top.setLayout(contentAreaLayout);
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		// create layout
		contentAreaLayout = new GridLayout();

		contentAreaLayout.numColumns = 3;
		contentAreaLayout.makeColumnsEqualWidth = false;
		contentAreaLayout.marginHeight = 0;
		contentAreaLayout.marginWidth = 0;
		contentAreaLayout.verticalSpacing = 0;
		contentAreaLayout.horizontalSpacing = 0;

		// create layout data
		GridData data = new GridData();

		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = 1;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;

		// create layout
		Composite result = new Composite(top, SWT.NONE);

		// assign layout and layout data
		result.setLayout(contentAreaLayout);
		result.setLayoutData(data);

		// create label
		if (!embedded)
		{
			Label searchLabel = new Label(result, SWT.NONE);
			searchLabel.setText(Messages.CommonQuickOutlinePage_FilterLabel);
		}

		// create text box
		int style = 0;
		if (embedded)
		{
			style = SWT.SINGLE | SWT.FOCUSED;
		}
		else
		{
			style = SWT.SINGLE | SWT.BORDER;
		}
		this._searchBox = new Text(result, style);
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

		// this._filters = filters.toArray(new BaseFilter[filters.size()]);

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

		result.setLabelProvider(_editor.getOutlinePage().getTreeViewer().getLabelProvider());
		result.setContentProvider(_editor.getOutlinePage().getTreeViewer().getContentProvider());
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
				// else if (a instanceof IParseNode && b instanceof IParseNode)
				// {
				// if (a == b)
				// {
				// result = true;
				// }
				// else
				// {
				// IParseNode node1 = (IParseNode) a;
				// IParseNode node2 = (IParseNode) b;
				// String path1 = node1.getUniquePath();
				// String path2 = node2.getUniquePath();
				//
				// result = path1.equals(path2);
				// }
				// }
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
		super.dispose();

		if (this._delayedRefreshJob != null && this._editor != null)
		{
			this._editor.getDocumentProvider().getDocument(this._editor.getEditorInput())
					.removeDocumentListener(this._documentListener);
		}
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
	 * hidePrivateMembers
	 * 
	 * @return boolean
	 */
	public boolean hidePrivateMembers()
	{
		return this._hide;
	}

	/**
	 * refresh
	 */
	public void refresh()
	{
		if (!_treeViewer.getControl().isDisposed())
		{
			this._treeViewer.refresh();
		}
	}

	/**
	 * removeOpenActionIfNeeded
	 */
	private void removeOpenActionIfNeeded()
	{
		if (this.openAction != null)
		{
			_toolbarManager.remove(openAction);
			openAction = null;
			_toolbarManager.update(false);
		}
	}

	/**
	 * Contibutes actions to quick outline menu.
	 * 
	 * @param manager
	 *            - menu manager.
	 */
	public void contributeToQuickOutlineMenu(IMenuManager manager)
	{
		// FIXME add actions for sorting/etc
		// add sort action
		// SortAction sortAction = new SortAction(this);
		// IPreferenceStore store = UnifiedEditorsPlugin.getDefault().getPreferenceStore();
		// boolean sort = store.getBoolean(IPreferenceConstants.SORT_OUTLINE_ALPHABETICALLY);
		// sortAction.setChecked(sort);
		// if (sort)
		// {
		// getTreeViewer().setSorter(SortAction.SORTER);
		// }
		// this._sortItem = new ActionContributionItem(sortAction);
		// manager.add(this._sortItem);
		//
		// // add hide private members action
		// this._hidePrivateAction = new HidePrivateAction(this);
		// this._hidePrivateItem = new ActionContributionItem(this._hidePrivateAction);
		// manager.add(this._hidePrivateItem);
		//
		// // add collapse all action
		// CollapseAction collapseAction = new CollapseAction(this);
		// this._collapseItem = new ActionContributionItem(collapseAction);
		// manager.add(this._collapseItem);
		//
		// Action expandAction = new Action(Messages.UnifiedOutlinePage_ExpandAll)
		// {
		// public void run()
		// {
		// getTreeViewer().expandAll();
		// }
		// };
		//        expandAction.setImageDescriptor(UnifiedEditorsPlugin.getImageDescriptor("icons/expandall.gif")); //$NON-NLS-1$
		// expandAction.setToolTipText(Messages.UnifiedOutlinePage_CollapseAll);
		// this._expandItem = new ActionContributionItem(expandAction);
		// manager.add(this._expandItem);
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
	public Control getSearchBox()
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

	/**
	 * togglePrivateMemberVisibility
	 */
	public void togglePrivateMemberVisibility()
	{
		this._hide = (this._hide == false);

		this.refresh();
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

		if (openAction != null)
		{
			_toolbarManager.remove(openAction);
			_toolbarManager.update(true);
			_toolbarManager.getControl().getParent().layout(true, true);
		}

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
					return;
				}
				else if (element instanceof IParseNode)
				{
					int position = ((IParseNode) element).getStartingOffset();
					this._editor.selectAndReveal(position, 0);
					closeDialog();
					return;
				}
				// removing item from toolbar
				removeOpenActionIfNeeded();
				return;
			}
		}

		removeOpenActionIfNeeded();
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
			ArrayList<Object> parentsList = new ArrayList<Object>();

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
