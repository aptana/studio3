/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.explorer.internal.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.WorkbenchJob;

import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.PlatformUtil;
import com.aptana.explorer.ExplorerPlugin;
import com.aptana.explorer.ui.filter.AbstractResourceBasedViewerFilter;
import com.aptana.explorer.ui.filter.PathFilter;
import com.aptana.ui.util.UIUtils;
import com.aptana.ui.widgets.SearchComposite;

/**
 * Adds focus filtering and a free form text filter to the Project view.
 * 
 * @author cwilliams
 */
public class FilteringProjectView extends GitProjectView
{
	/**
	 * Attribute/element names for filter extensions.
	 */
	private static final String ELEMENT_PRIORITY = "priority"; //$NON-NLS-1$
	private static final String ELEMENT_CLASS = "class"; //$NON-NLS-1$
	private static final String ELEMENT_NATURE = "nature"; //$NON-NLS-1$
	private static final String FILTERS_EXT_PT_ID = "filters"; //$NON-NLS-1$
	private static final String ELEMENT_FILTER = "filter"; //$NON-NLS-1$

	/**
	 * Memento names for saving state of view and restoring it across launches.
	 */
	private static final String TAG_SELECTION = "selection"; //$NON-NLS-1$
	private static final String TAG_EXPANDED = "expanded"; //$NON-NLS-1$
	private static final String TAG_ELEMENT = "element"; //$NON-NLS-1$
	private static final String TAG_PATH = "path"; //$NON-NLS-1$
	private static final String TAG_PROJECT = "project"; //$NON-NLS-1$
	private static final String TAG_FILTER = "filter"; //$NON-NLS-1$
	private static final String KEY_NAME = "name"; //$NON-NLS-1$

	/**
	 * Maximum time spent expanding the tree after the filter text has been updated (this is only used if we were able
	 * to at least expand the visible nodes)
	 */
	private static final long SOFT_MAX_EXPAND_TIME = 200;

	private IResource currentFilter = null;
	/**
	 * Determine if we're searching by filename or content in the search text box.
	 */
	private boolean fFilenameSearchMode;
	private AbstractResourceBasedViewerFilter patternFilter;
	private WorkbenchJob refreshJob;

	/**
	 * Fields for the focus/eyeball filtering
	 */
	protected TreeItem hoveredItem;
	protected int lastDrawnX;
	protected Object[] fExpandedElements;
	private Image eyeball;

	private Composite customComposite;
	private IResourceChangeListener fResourceListener;

	// Since the IMemento model does not fit our 'live' project switching,
	// we maintain the states of all the projects in these data structures and
	// flush them into the IMemento when needed (in the saveState call).
	private Map<IProject, List<String>> projectExpansions;
	private Map<IProject, List<String>> projectSelections;
	private Map<IProject, String> projectFilters;
	private ArrayList<IConfigurationElement> fgElements;
	/**
	 * The special filter used to filter the view when search is done for filename.
	 */
	private PathFilter filenameFilter;
	/**
	 * Special boolean for us to tell whether we use our special filename filter or use the hover filter.
	 */
	private boolean filterViaSearch;
	private SearchComposite search;

	/**
	 * Constructs a new FilteringProjectView.
	 */
	public FilteringProjectView()
	{
		projectExpansions = new HashMap<IProject, List<String>>();
		projectSelections = new HashMap<IProject, List<String>>();
		projectFilters = new HashMap<IProject, String>();
	}

	@Override
	public void createPartControl(Composite aParent)
	{
		customComposite = new Composite(aParent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		customComposite.setLayout(gridLayout);

		super.createPartControl(customComposite);
		createRefreshJob();

		// Add eyeball hover
		addFocusHover();
		addResourceListener();
	}

	@Override
	public void init(IViewSite aSite, IMemento aMemento) throws PartInitException
	{
		super.init(aSite, aMemento);
		loadMementoCache();

		eyeball = ExplorerPlugin.getImage("icons/full/obj16/eye.png"); //$NON-NLS-1$
	}

	/**
	 * Load the memento's relevant data into this view's internal memento data structure.
	 */
	protected void loadMementoCache()
	{
		if (this.memento == null)
		{
			return;
		}
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IMemento[] projectMementoes = memento.getChildren(TAG_PROJECT);
		for (IMemento projMemento : projectMementoes)
		{
			String projectName = projMemento.getString(KEY_NAME);
			IProject project = workspaceRoot.getProject(projectName);
			// Load only projects that exist and open in the workspace.
			if (project != null && project.isAccessible())
			{
				List<String> expanded = new ArrayList<String>();
				IMemento childMem = projMemento.getChild(TAG_EXPANDED);
				if (childMem != null)
				{
					IMemento[] elementMem = childMem.getChildren(TAG_ELEMENT);
					for (int i = 0; i < elementMem.length; i++)
					{
						expanded.add(elementMem[i].getString(TAG_PATH));
					}
				}
				List<String> selected = new ArrayList<String>();
				childMem = projMemento.getChild(TAG_SELECTION);
				if (childMem != null)
				{
					IMemento[] elementMem = childMem.getChildren(TAG_ELEMENT);
					for (int i = 0; i < elementMem.length; i++)
					{
						selected.add(elementMem[i].getString(TAG_PATH));
					}
				}
				// Cache the loaded mementoes
				projectExpansions.put(project, expanded);
				projectSelections.put(project, selected);

				childMem = projMemento.getChild(TAG_FILTER);
				if (childMem != null)
				{
					projectFilters.put(project, childMem.getString(TAG_PATH));
				}
			}
		}
	}

	/**
	 * Update the in-memory memento for a given project. This update method should be called before saving the memento
	 * and before every project switch.
	 * 
	 * @param project
	 */
	protected void updateProjectMementoCache(IProject project)
	{
		TreeViewer viewer = getCommonViewer();
		if (viewer == null || project == null)
		{
			return;
		}
		List<String> expanded = new ArrayList<String>();
		List<String> selected = new ArrayList<String>();

		// Save the expansion state
		Object expandedElements[] = viewer.getVisibleExpandedElements();
		if (expandedElements.length > 0)
		{
			for (int i = 0; i < expandedElements.length; i++)
			{
				if (expandedElements[i] instanceof IResource)
				{
					expanded.add(((IResource) expandedElements[i]).getFullPath().toString());
				}
			}
		}
		// Save the selection state
		Object selectedElements[] = ((IStructuredSelection) viewer.getSelection()).toArray();
		if (selectedElements.length > 0)
		{
			for (int i = 0; i < selectedElements.length; i++)
			{
				if (selectedElements[i] instanceof IResource)
				{
					selected.add(((IResource) selectedElements[i]).getFullPath().toString());
				}
			}
		}
		projectExpansions.put(project, expanded);
		projectSelections.put(project, selected);

		// FIXME Need to store filters in a way that we can store the filename search filter too!
		IResource filter = getFilterResource();
		if (filter != null)
		{
			projectFilters.put(project, filter.getLocation().toPortableString());
		}
		else
		{
			projectFilters.remove(project);
		}
	}

	private void addFocusHover()
	{
		final int IMAGE_MARGIN = 2;
		final Tree tree = getCommonViewer().getTree();

		/*
		 * NOTE: MeasureItem and PaintItem are called repeatedly. Therefore it is critical for performance that these
		 * methods be as efficient as possible.
		 */
		// Do our hover bg coloring
		tree.addListener(SWT.EraseItem, createHoverBGColorer());
		// Paint Eyeball
		tree.addListener(SWT.Paint, createEyeballPainter(eyeball, IMAGE_MARGIN, tree));
		// Track hovered item and force it's coloring
		getCommonViewer().getControl().addMouseMoveListener(createHoverTracker());
		// Remove hover on exit of tree
		getCommonViewer().getControl().addMouseTrackListener(createTreeExitHoverRemover());
		// handle Eyeball Focus action
		getCommonViewer().getTree().addMouseListener(createEyeballFocusClickHandler(eyeball));
	}

	protected MouseListener createEyeballFocusClickHandler(final Image eyeball)
	{
		return new MouseListener()
		{
			public void mouseUp(MouseEvent e)
			{
			}

			public void mouseDown(MouseEvent e)
			{
				if (hoveredItem == null)
					return;
				// If user clicks on the eyeball, we need to turn on the focus filter!
				Tree tree = (Tree) e.widget;
				TreeItem t = tree.getItem(new Point(e.x, e.y));
				if (t == null)
					return;
				if (!t.equals(hoveredItem))
					return;
				if (e.x >= lastDrawnX && e.x <= lastDrawnX + eyeball.getBounds().width)
				{
					// Ok, now we need to turn on the filter!
					IResource text = getResourceToFilterBy();
					if (text != null)
					{
						fExpandedElements = getCommonViewer().getExpandedElements();
						hoveredItem = null;
						setFilter(text);
					}
				}
			}

			public void mouseDoubleClick(MouseEvent e)
			{
			}
		};
	}

	protected MouseTrackAdapter createTreeExitHoverRemover()
	{
		return new MouseTrackAdapter()
		{
			@Override
			public void mouseExit(MouseEvent e)
			{
				super.mouseExit(e);
				if (hoveredItem == null)
					return;
				removeHoveredItem();
			}
		};
	}

	protected MouseMoveListener createHoverTracker()
	{
		return new MouseMoveListener()
		{

			public void mouseMove(MouseEvent e)
			{
				// If the filter is already on, we shouldn't do this stuff
				if (filterOn())
					return;
				final TreeItem t = getCommonViewer().getTree().getItem(new Point(e.x, e.y));
				// hovered item didn't change
				if (hoveredItem != null && hoveredItem.equals(t))
					return;
				// remove old hover
				removeHoveredItem();

				if (t == null)
					return;
				IResource data = getResource(t);
				if (data != null && (data.getType() == IResource.FILE))
				{
					hoveredItem = t;
					hoveredItem.setBackground(getHoverBackgroundColor());
					Display.getDefault().asyncExec(new Runnable()
					{
						public void run()
						{
							if (hoveredItem == null || getCommonViewer() == null || getCommonViewer().getTree() == null
									|| hoveredItem.getBounds() == null)
								return;
							getCommonViewer().getTree().redraw(hoveredItem.getBounds().x, hoveredItem.getBounds().y,
									hoveredItem.getBounds().width, hoveredItem.getBounds().height, true);
						}
					});
				}
			}
		};
	}

	protected Listener createEyeballPainter(final Image eyeball, final int IMAGE_MARGIN, final Tree tree)
	{
		return new Listener()
		{
			public void handleEvent(Event event)
			{
				if (hoveredItem == null || hoveredItem.isDisposed())
					return;
				if (eyeball != null)
				{
					int endOfClientAreaX = tree.getClientArea().width + tree.getClientArea().x;
					int endOfItemX = hoveredItem.getBounds().width + hoveredItem.getBounds().x;
					lastDrawnX = Math.max(endOfClientAreaX, endOfItemX) - (IMAGE_MARGIN + eyeball.getBounds().width);
					int itemHeight = tree.getItemHeight();
					int imageHeight = eyeball.getBounds().height;
					int y = hoveredItem.getBounds().y + (itemHeight - imageHeight) / 2;
					event.gc.drawImage(eyeball, lastDrawnX, y);
				}
			}
		};
	}

	protected Listener createHoverBGColorer()
	{
		return new Listener()
		{
			public void handleEvent(Event event)
			{
				if ((event.detail & SWT.BACKGROUND) != 0)
				{
					TreeItem item = (TreeItem) event.item;
					if (hoveredItem == null || !hoveredItem.equals(item))
						return;
					Tree tree = (Tree) event.widget;
					int clientWidth = tree.getClientArea().width;

					GC gc = event.gc;
					Color oldBackground = gc.getBackground();
					gc.setBackground(getHoverBackgroundColor());
					gc.fillRectangle(0, event.y, clientWidth, event.height);
					gc.setBackground(oldBackground);

					event.detail &= ~SWT.BACKGROUND;
				}
			}
		};
	}

	@Override
	protected void removeFilter()
	{
		clearFilter();
		super.removeFilter();
	}

	@Override
	public void saveState(IMemento memento)
	{
		super.saveState(memento);
		TreeViewer viewer = getCommonViewer();
		if (viewer == null)
		{
			if (this.memento != null)
			{
				memento.putMemento(this.memento);
			}
			return;
		}

		// Make sure we are up-to-date
		updateProjectMementoCache(selectedProject);
		// Collect all the projects in the cache
		Set<IProject> projects = new TreeSet<IProject>(new Comparator<IProject>()
		{
			public int compare(IProject o1, IProject o2)
			{
				return o1 == o2 ? 0 : o1.getName().compareTo(o2.getName());
			}
		});
		projects.addAll(projectExpansions.keySet());
		projects.addAll(projectSelections.keySet());

		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		// Create a memento for every accessible project that has expanded/selected paths.
		for (IProject project : projects)
		{
			if (project.isAccessible()
					&& !(projectExpansions.get(project).isEmpty() && projectSelections.get(project).isEmpty() && projectFilters
							.get(project) == null))
			{
				IMemento projectMemento = memento.createChild(TAG_PROJECT);
				projectMemento.putString(KEY_NAME, project.getName());
				// Create the expansions memento
				List<String> expanded = projectExpansions.get(project);
				if (!expanded.isEmpty())
				{
					IMemento expansionMem = projectMemento.createChild(TAG_EXPANDED);
					for (String expandedPath : expanded)
					{
						if (workspaceRoot.findMember(expandedPath) != null)
						{
							IMemento elementMem = expansionMem.createChild(TAG_ELEMENT);
							elementMem.putString(TAG_PATH, expandedPath);
						}
					}
				}
				// Create the selection memento
				List<String> selected = projectSelections.get(project);
				if (!selected.isEmpty())
				{
					IMemento selectionMem = projectMemento.createChild(TAG_SELECTION);
					for (String selectedPath : selected)
					{
						if (workspaceRoot.findMember(selectedPath) != null)
						{
							IMemento elementMem = selectionMem.createChild(TAG_ELEMENT);
							elementMem.putString(TAG_PATH, selectedPath);
						}
					}
				}

				String filter = projectFilters.get(project);
				if (filter != null)
				{
					IMemento filterMem = projectMemento.createChild(TAG_FILTER);
					filterMem.putString(TAG_PATH, filter);
				}
			}
		}
	}

	/**
	 * Restore the expansion and selection state in a job.
	 * 
	 * @param project
	 */
	protected void restoreStateJob(final IProject project)
	{
		Job job = new WorkbenchJob("Restoring State") {//$NON-NLS-1$
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				restoreState(project);
				return Status.OK_STATUS;
			}
		};
		EclipseUtil.setSystemForJob(job);
		// We have to delay it a bit, otherwise, the tree collapse back due
		// to other jobs.
		job.schedule(getRefreshJobDelay() * 2);
	}

	/**
	 * Restores the expansion and selection state of given project.
	 * 
	 * @param project
	 *            the {@link IProject}
	 * @see #restoreStateJob(IProject)
	 * @see #saveState(IMemento)
	 * @since 2.0
	 */
	protected void restoreState(IProject project)
	{
		TreeViewer viewer = getCommonViewer();
		Control control = viewer.getControl();
		if (control == null || control.isDisposed())
		{
			return;
		}

		IContainer container = ResourcesPlugin.getWorkspace().getRoot();
		List<String> expansions = projectExpansions.get(project);
		List<String> selections = projectSelections.get(project);
		control.setRedraw(false);
		// FIXME Reconstruct filter into IResource
		String filter = projectFilters.get(project);
		if (filter == null || filter.length() == 0)
		{
			if (currentFilter != null)
			{
				clearFilter();
			}
		}
		else
		{
			IResource filterResource = project.getWorkspace().getRoot()
					.getFileForLocation(Path.fromPortableString(filter));
			setFilter(filterResource);
		}
		if (selections != null)
		{
			List<IResource> elements = new ArrayList<IResource>();
			for (String selectionPath : selections)
			{
				IResource element = container.findMember(selectionPath);
				if (element != null)
				{
					elements.add(element);
				}
			}
			viewer.setSelection(new StructuredSelection(elements), true);
		}
		if (expansions != null)
		{
			List<IResource> elements = new ArrayList<IResource>();
			for (String expansionPath : expansions)
			{
				IResource element = container.findMember(expansionPath);
				if (element != null)
				{
					elements.add(element);
				}
			}
			viewer.setExpandedElements(elements.toArray());
		}
		control.setRedraw(true);
	}

	@Override
	protected void projectChanged(IProject oldProject, IProject newProject)
	{
		// Update the memento cache when the project is changed.
		updateProjectMementoCache(oldProject);
		super.projectChanged(oldProject, newProject);
		// Restore the displayed project state.
		restoreStateJob(newProject);
	}

	@Override
	public void dispose()
	{
		removeResourceListener();
		super.dispose();
	}

	private void addResourceListener()
	{
		// Add a listener for add/remove/edits of files in this project!
		fResourceListener = new IResourceChangeListener()
		{

			public void resourceChanged(IResourceChangeEvent event)
			{
				if (selectedProject == null || !selectedProject.exists())
					return;
				IResourceDelta delta = event.getDelta();
				IResourceDelta[] children = delta.getAffectedChildren();
				for (IResourceDelta iResourceDelta : children)
				{
					IResource resource = iResourceDelta.getResource();
					if (resource == null)
						continue;
					if (resource.getProject().equals(selectedProject))
					{
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
						{

							public void run()
							{
								if (getCommonViewer() != null && getCommonViewer().getTree() != null
										&& !getCommonViewer().getTree().isDisposed())
								{
									getCommonViewer().refresh();
								}
							}
						});
						return;
					}
				}
			}
		};
		ResourcesPlugin.getWorkspace().addResourceChangeListener(fResourceListener, IResourceChangeEvent.POST_CHANGE);
	}

	private void removeResourceListener()
	{
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(fResourceListener);
	}

	/**
	 * Clears the filter.
	 */
	protected void clearFilter()
	{
		currentFilter = null;
		filterChanged();
	}

	/**
	 * Set the filter.
	 * 
	 * @param resource
	 */
	protected void setFilter(IResource resource)
	{
		currentFilter = resource;
		filterChanged();
	}

	/**
	 * Create the refresh job for the receiver.
	 */
	private void createRefreshJob()
	{
		refreshJob = doCreateRefreshJob();
		EclipseUtil.setSystemForJob(refreshJob);
	}

	/**
	 * Update the receiver after the filter has changed.
	 */
	protected void filterChanged()
	{
		// cancel currently running job first, to prevent unnecessary redraw
		if (refreshJob != null)
		{
			refreshJob.cancel();
			refreshJob.schedule(getRefreshJobDelay());
		}
	}

	/**
	 * Return the time delay that should be used when scheduling the filter refresh job. Subclasses may override.
	 * 
	 * @return a time delay in milliseconds before the job should run
	 * @since 3.5
	 */
	protected long getRefreshJobDelay()
	{
		return 100;
	}

	protected WorkbenchJob doCreateRefreshJob()
	{
		return new WorkbenchJob("Refresh Filter") {//$NON-NLS-1$
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				if (getCommonViewer().getControl().isDisposed())
				{
					return Status.CANCEL_STATUS;
				}

				Control redrawFalseControl = getCommonViewer().getControl();
				try
				{
					// don't want the user to see updates that will be made to
					// the tree
					// we are setting redraw(false) on the composite to avoid
					// dancing scrollbar
					redrawFalseControl.setRedraw(false);
					// collapse all
					getCommonViewer().collapseAll();
					// Now apply/remove the filter. This will trigger a refresh!
					IResource filterResource = getFilterResource();
					try
					{
						if (filterResource == null)
						{
							if (patternFilter != null)
							{
								getCommonViewer().removeFilter(patternFilter);
								patternFilter = null;
							}
						}
						else
						{
							if (patternFilter != null)
							{
								getCommonViewer().removeFilter(patternFilter);
							}
							patternFilter = createPatternFilter(filterResource);
							showFilterLabel(eyeball, NLS.bind(Messages.FilteringProjectView_LBL_FilteringFor,
									new Object[] { patternFilter.getPattern() }));
							getCommonViewer().addFilter(patternFilter);
						}
					}
					catch (Exception e)
					{
						// ignore. This seems to just happen on windows and appears to be benign
					}

					// Now set up expansion of elements
					if (filterResource != null)
					{
						/*
						 * Expand elements one at a time. After each is expanded, check to see if the filter text has
						 * been modified. If it has, then cancel the refresh job so the user doesn't have to endure
						 * expansion of all the nodes.
						 */
						TreeItem[] items = getCommonViewer().getTree().getItems();
						int treeHeight = getCommonViewer().getTree().getBounds().height;
						int numVisibleItems = treeHeight / getCommonViewer().getTree().getItemHeight();
						long stopTime = SOFT_MAX_EXPAND_TIME + System.currentTimeMillis();
						boolean cancel = false;
						if (items.length > 0
								&& recursiveExpand(items, monitor, stopTime, new int[] { numVisibleItems }))
						{
							cancel = true;
						}
						if (cancel)
						{
							return Status.CANCEL_STATUS;
						}
					}
					else
					{
						// to reset our expansion state back to what it was before user used the eyeball/focus filter!
						if (fExpandedElements != null)
						{
							getCommonViewer().collapseAll();
							getCommonViewer().setExpandedElements(fExpandedElements);
							fExpandedElements = null;
						}
					}
				}
				finally
				{
					// done updating the tree - set redraw back to true
					TreeItem[] items = getCommonViewer().getTree().getItems();
					if (items.length > 0 && getCommonViewer().getTree().getSelectionCount() == 0)
					{
						getCommonViewer().getTree().setTopItem(items[0]);
					}
					redrawFalseControl.setRedraw(true);
				}
				return Status.OK_STATUS;
			}

			/**
			 * Returns true if the job should be canceled (because of timeout or actual cancellation).
			 * 
			 * @param items
			 * @param monitor
			 * @param cancelTime
			 * @param numItemsLeft
			 * @return true if canceled
			 */
			private boolean recursiveExpand(TreeItem[] items, IProgressMonitor monitor, long cancelTime,
					int[] numItemsLeft)
			{
				boolean canceled = false;
				for (int i = 0; !canceled && i < items.length; i++)
				{
					TreeItem item = items[i];
					boolean visible = numItemsLeft[0]-- >= 0;
					if (monitor.isCanceled() || (!visible && System.currentTimeMillis() > cancelTime))
					{
						canceled = true;
					}
					else
					{
						Object itemData = item.getData();
						if (itemData != null)
						{
							if (!item.getExpanded())
							{
								// do the expansion through the viewer so that
								// it can refresh children appropriately.
								getCommonViewer().setExpandedState(itemData, true);
							}
							TreeItem[] children = item.getItems();
							if (items.length > 0)
							{
								canceled = recursiveExpand(children, monitor, cancelTime, numItemsLeft);
							}
						}
					}
				}
				return canceled;
			}

		};
	}

	/**
	 * Based on the registered filters, we grab the one that has the highest priority and matches one of the project
	 * natures. Defaults to {@link PathFilter} in case no match is made.
	 * 
	 * @param filterResource
	 * @return
	 */
	protected AbstractResourceBasedViewerFilter createPatternFilter(IResource filterResource)
	{
		if (fFilenameSearchMode && filterViaSearch)
		{
			filenameFilter.setResourceToFilterOn(selectedProject);
			filterViaSearch = false;
			return filenameFilter;
		}

		IProject project = filterResource.getProject();
		Set<String> natures = new HashSet<String>();
		try
		{
			for (String natureId : project.getDescription().getNatureIds())
			{
				natures.add(natureId);
			}
		}
		catch (CoreException e1)
		{
			// ignore
		}

		final List<AbstractResourceBasedViewerFilter> filters = new ArrayList<AbstractResourceBasedViewerFilter>();
		final List<Integer> priorities = new ArrayList<Integer>();
		for (IConfigurationElement element : getResourceBasedFilters())
		{
			if (natures.contains(element.getAttribute(ELEMENT_NATURE)))
			{
				try
				{
					AbstractResourceBasedViewerFilter participant = (AbstractResourceBasedViewerFilter) element
							.createExecutableExtension(ELEMENT_CLASS);
					String rawPriority = element.getAttribute(ELEMENT_PRIORITY);
					Integer priority;
					try
					{
						priority = Integer.parseInt(rawPriority);
					}
					catch (NumberFormatException e)
					{
						priority = 50;
					}
					filters.add(participant);
					priorities.add(priority);
				}
				catch (CoreException e)
				{
					ExplorerPlugin.logError(e);
				}
			}
		}

		AbstractResourceBasedViewerFilter patternFilter;
		if (filters.isEmpty())
		{
			patternFilter = new PathFilter();
		}
		else if (filters.size() > 1)
		{
			List<AbstractResourceBasedViewerFilter> copy = new ArrayList<AbstractResourceBasedViewerFilter>(filters);
			Collections.sort(copy, new Comparator<AbstractResourceBasedViewerFilter>()
			{
				public int compare(AbstractResourceBasedViewerFilter arg0, AbstractResourceBasedViewerFilter arg1)
				{
					return priorities.get(filters.indexOf(arg0)).compareTo(priorities.get(filters.indexOf(arg1)));
				}
			});
			patternFilter = copy.get(0);
		}
		else
		{
			patternFilter = filters.get(0);
		}
		patternFilter.setResourceToFilterOn(filterResource);
		return patternFilter;
	}

	private synchronized Collection<IConfigurationElement> getResourceBasedFilters()
	{
		if (fgElements == null)
		{
			fgElements = new ArrayList<IConfigurationElement>();
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			if (registry != null)
			{
				IExtensionPoint extensionPoint = registry
						.getExtensionPoint(ExplorerPlugin.PLUGIN_ID, FILTERS_EXT_PT_ID);
				if (extensionPoint != null)
				{
					for (IExtension extension : extensionPoint.getExtensions())
					{
						for (IConfigurationElement element : extension.getConfigurationElements())
						{
							if (ELEMENT_FILTER.equals(element.getName()))
							{
								fgElements.add(element);
							}
						}
					}
				}
			}
		}
		return fgElements;
	}

	protected IResource getFilterResource()
	{
		return currentFilter;
	}

	private IResource getResourceToFilterBy()
	{
		return getResource(hoveredItem);
	}

	private void removeHoveredItem()
	{
		if (hoveredItem == null)
			return;
		if (hoveredItem.isDisposed())
		{
			hoveredItem = null;
			return;
		}
		final Rectangle bounds = hoveredItem.getBounds();
		hoveredItem.setBackground(null);
		hoveredItem = null;
		Display.getDefault().asyncExec(new Runnable()
		{

			public void run()
			{
				getCommonViewer().getTree().redraw(bounds.x, bounds.y, bounds.width, bounds.height, true);
			}
		});
	}

	protected Color getHoverBackgroundColor()
	{
		return UIUtils.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
	}

	private boolean filterOn()
	{
		return getFilterResource() != null;
	}

	protected IResource getResource(final TreeItem t)
	{
		Object data = t.getData();
		if (data instanceof IResource)
			return (IResource) t.getData();
		if (data instanceof IAdaptable)
		{
			IAdaptable adapt = (IAdaptable) data;
			return (IResource) adapt.getAdapter(IResource.class);
		}
		return null;
	}

	@Override
	protected Composite createSearchComposite(Composite myComposite)
	{
		search = (SearchComposite) super.createSearchComposite(myComposite);

		final Menu modeMenu = new Menu(search);
		final MenuItem filenameItem = new MenuItem(modeMenu, SWT.RADIO);
		filenameItem.setText(Messages.FilteringProjectView_SearchByFilenameLabel);
		filenameItem.setSelection(fFilenameSearchMode);
		filenameItem.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				fFilenameSearchMode = true;
			}
		});

		final MenuItem contentItem = new MenuItem(modeMenu, SWT.RADIO);
		contentItem.setText(Messages.FilteringProjectView_SearchContentLabel);
		contentItem.setSelection(!fFilenameSearchMode);
		contentItem.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				fFilenameSearchMode = false;
			}
		});

		search.getTextControl().addListener(SWT.Paint, new Listener()
		{
			public void handleEvent(Event event)
			{
				// Paint the down arrow
				GC gc = event.gc;
				final int width = 7;
				int x = 15;
				int y = 10;
				if (Platform.getOS().equals(Platform.OS_WIN32)) // On windows, we need to draw on right side
				{
					x = event.width - 7;
					y = 10;
				}
				else if (Platform.getOS().equals(Platform.OS_LINUX)) // draw near bottom at far-left on Linux (still
																		// doesn't overlap magnifying glass)
				{
					// For Ubuntu, draw the down arrow below the text. That is better than right in the middle of the
					// text
					if (PlatformUtil.isOSName("Ubuntu")) //$NON-NLS-1$
					{
						x = 0;
						y = 17;
					}
					else
					{
						x = 0;
						y = 15;
					}
				}

				Color bg = gc.getBackground();
				gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
				gc.fillPolygon(new int[] { x, y, x + width - 1, y, x + ((width - 1) / 2), y + ((width - 1) / 2) });
				gc.setBackground(bg);
			}
		});

		search.getTextControl().addMouseListener(new MouseAdapter()
		{
			public void mouseDown(MouseEvent e)
			{
				boolean isOnPulldownSection = false;
				int shift = 0;
				// Because on windows we draw on right side, we need to check different click area
				if (Platform.getOS().equals(Platform.OS_WIN32))
				{
					Rectangle bounds = search.getTextControl().getBounds();
					if (e.x >= bounds.width - 20 && e.x <= bounds.width)
					{
						isOnPulldownSection = true;
						shift = bounds.width - 20;
					}
				}
				else if (e.x <= 18)
				{
					isOnPulldownSection = true;
				}
				if (isOnPulldownSection)
				{
					Point searchLocation = search.getLocation();
					searchLocation = search.getParent().toDisplay(searchLocation.x, searchLocation.y);
					Point searchSize = search.getSize();
					modeMenu.setLocation(searchLocation.x + shift, searchLocation.y + searchSize.y + 2);
					modeMenu.setVisible(true);
				}
			}
		});

		return search;
	}

	/**
	 * Override search to handle filename search mode, which is actually a filter
	 */
	public void search(final String text, final boolean isCaseSensitive, final boolean isRegularExpression)
	{
		if (selectedProject == null)
		{
			return;
		}

		if (fFilenameSearchMode)
		{
			clearFilter();
			try
			{
				final Pattern pattern = search.createSearchPattern();
				search.getTextControl().setForeground(search.getDisplay().getSystemColor(SWT.COLOR_BLACK));
				filenameFilter = new PathFilter()
				{
					@Override
					protected boolean match(String string)
					{
						if (pattern == null)
						{
							return false;
						}
						return pattern.matcher(string).find();
					}

					public String getPattern()
					{
						// This is what we display in the "filtering for ..." label
						return text;
					};
				};
				// We need some way to tell the job that uses the filter that this is the one to apply versus creating
				// one for the hover
				filterViaSearch = true;
				setFilter(selectedProject);
			}
			catch (PatternSyntaxException e)
			{
				// TODO Show some UI popup or something to say regexp is bad?
				search.getTextControl().setForeground(search.getDisplay().getSystemColor(SWT.COLOR_RED));
			}
		}
		else
		{
			super.search(text, isCaseSensitive, isRegularExpression);
		}
	}

}
