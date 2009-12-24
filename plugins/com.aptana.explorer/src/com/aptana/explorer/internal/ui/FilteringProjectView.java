package com.aptana.explorer.internal.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.navigator.framelist.FrameList;
import org.eclipse.ui.internal.navigator.framelist.TreeFrame;
import org.eclipse.ui.progress.WorkbenchJob;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.theme.ThemeUtil;
import com.aptana.explorer.ExplorerPlugin;

/**
 * Adds focus filtering and a free form text filter to the Project view.
 * 
 * @author cwilliams
 */
@SuppressWarnings("restriction")
public class FilteringProjectView extends GitProjectView
{

	/**
	 * Memento names for saving state of view and restoring it across launches.
	 */
	private static final String TAG_SELECTION = "selection"; //$NON-NLS-1$
	private static final String TAG_EXPANDED = "expanded"; //$NON-NLS-1$
	private static final String TAG_ELEMENT = "element"; //$NON-NLS-1$
	private static final String TAG_PATH = "path"; //$NON-NLS-1$
	private static final String TAG_CURRENT_FRAME = "currentFrame"; //$NON-NLS-1$

	/**
	 * Maximum time spent expanding the tree after the filter text has been updated (this is only used if we were able
	 * to at least expand the visible nodes)
	 */
	private static final long SOFT_MAX_EXPAND_TIME = 200;
	
	private String currentFilterText = "";

	private PathFilter patternFilter;
	private WorkbenchJob refreshJob;

	/**
	 * Fields for the focus/eyeball filtering
	 */
	protected TreeItem hoveredItem;
	protected int lastDrawnX;
	protected Object[] fExpandedElements;
	private Image eyeball;

	private Label filterLabel;
	private GridData filterLayoutData;
	
	private Composite customComposite;

	@Override
	public void createPartControl(Composite aParent)
	{
		customComposite = new Composite(aParent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		customComposite.setLayout(gridLayout);
		
		super.createPartControl(customComposite);
		
		createFilterComposite(customComposite);
		
		patternFilter = new PathFilter();
		getCommonViewer().addFilter(patternFilter);
		createRefreshJob();

		// Add eyeball hover
		addFocusHover();
		if (memento != null)
		{
			restoreState(memento);
		}
		memento = null;
	}

	@Override
	public void init(IViewSite aSite, IMemento aMemento) throws PartInitException
	{
		this.memento = aMemento;
		super.init(aSite, aMemento);

		eyeball = ExplorerPlugin.getImage("icons/full/obj16/eye.png");

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
		tree.addListener(SWT.PaintItem, createEyeballPainter(eyeball, IMAGE_MARGIN, tree));
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
					String text = getResourceNameToFilterBy();
					if (text != null)
					{
						fExpandedElements = getCommonViewer().getExpandedElements();
						hoveredItem = null;
						setFilterText(text);
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
				TreeItem item = (TreeItem) event.item;
				if (hoveredItem == null || !hoveredItem.equals(item))
					return;
				if (eyeball != null)
				{
					int itemWidth = item.getParent().getClientArea().width;
					lastDrawnX = itemWidth - (IMAGE_MARGIN + eyeball.getBounds().width);
					int itemHeight = tree.getItemHeight();
					int imageHeight = eyeball.getBounds().height;
					int y = event.y + (itemHeight - imageHeight) / 2;
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

	private Composite createFilterComposite(final Composite myComposite)
	{
		Composite filter = new Composite(myComposite, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 2;
		gridLayout.marginHeight = 0;
		gridLayout.marginBottom = 2;
		filter.setLayout(gridLayout);

		filterLayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		filterLayoutData.exclude = true;
		filter.setLayoutData(filterLayoutData);

		filterLabel = new Label(filter, SWT.NONE);
		filterLabel.setText("");

		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		filterLabel.setLayoutData(gridData);
		
		ToolBar toolBar = new ToolBar(filter, SWT.FLAT);
		toolBar.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		
		ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setImage(ExplorerPlugin.getImage("icons/full/elcl16/close.png"));
		toolItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearText();
				hideFilterLable();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		return filter;
	}
	
	private void hideFilterLable() {
		filterLabel.setImage(null);
		filterLabel.setText("");
		filterLayoutData.exclude = true;
		customComposite.layout();
	}
	
	private void showFilterLabel(Image image, String text) {
		filterLabel.setImage(image);
		filterLabel.setText(text);
		filterLayoutData.exclude = false;
		customComposite.layout();
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

		FrameList frameList = getCommonViewer().getFrameList();
		if (frameList.getCurrentIndex() > 0)
		{
			// save frame, it's not the "home"/workspace frame
			TreeFrame currentFrame = (TreeFrame) frameList.getCurrentFrame();
			IMemento frameMemento = memento.createChild(TAG_CURRENT_FRAME);
			currentFrame.saveState(frameMemento);
		}
		else
		{
			// save visible expanded elements
			Object expandedElements[] = viewer.getVisibleExpandedElements();
			if (expandedElements.length > 0)
			{
				IMemento expandedMem = memento.createChild(TAG_EXPANDED);
				for (int i = 0; i < expandedElements.length; i++)
				{
					if (expandedElements[i] instanceof IResource)
					{
						IMemento elementMem = expandedMem.createChild(TAG_ELEMENT);
						elementMem.putString(TAG_PATH, ((IResource) expandedElements[i]).getFullPath().toString());
					}
				}
			}
			// save selection
			Object elements[] = ((IStructuredSelection) viewer.getSelection()).toArray();
			if (elements.length > 0)
			{
				IMemento selectionMem = memento.createChild(TAG_SELECTION);
				for (int i = 0; i < elements.length; i++)
				{
					if (elements[i] instanceof IResource)
					{
						IMemento elementMem = selectionMem.createChild(TAG_ELEMENT);
						elementMem.putString(TAG_PATH, ((IResource) elements[i]).getFullPath().toString());
					}
				}
			}
		}
	}

	/**
	 * Restores the state of the receiver to the state described in the specified memento.
	 * 
	 * @param memento
	 *            the memento
	 * @since 2.0
	 */
	protected void restoreState(IMemento memento)
	{
		TreeViewer viewer = getCommonViewer();
		IMemento frameMemento = memento.getChild(TAG_CURRENT_FRAME);

		if (frameMemento != null)
		{
			TreeFrame frame = new TreeFrame(viewer);
			frame.restoreState(frameMemento);
			frame.setName(getFrameName(frame.getInput()));
			frame.setToolTipText(getFrameToolTipText(frame.getInput()));
			viewer.setSelection(new StructuredSelection(frame.getInput()));
			getCommonViewer().getFrameList().gotoFrame(frame);
		}
		else
		{
			IContainer container = ResourcesPlugin.getWorkspace().getRoot();
			IMemento childMem = memento.getChild(TAG_EXPANDED);
			if (childMem != null)
			{
				List<IResource> elements = new ArrayList<IResource>();
				IMemento[] elementMem = childMem.getChildren(TAG_ELEMENT);
				for (int i = 0; i < elementMem.length; i++)
				{
					IResource element = container.findMember(elementMem[i].getString(TAG_PATH));
					if (element != null)
					{
						elements.add(element);
					}
				}
				viewer.setExpandedElements(elements.toArray());
			}
			childMem = memento.getChild(TAG_SELECTION);
			if (childMem != null)
			{
				List<IResource> list = new ArrayList<IResource>();
				IMemento[] elementMem = childMem.getChildren(TAG_ELEMENT);
				for (int i = 0; i < elementMem.length; i++)
				{
					IResource element = container.findMember(elementMem[i].getString(TAG_PATH));
					if (element != null)
					{
						list.add(element);
					}
				}
				viewer.setSelection(new StructuredSelection(list));
			}
		}
	}

	/**
	 * Returns the name for the given element. Used as the name for the current frame.
	 */
	private String getFrameName(Object element)
	{
		if (element instanceof IResource)
		{
			return ((IResource) element).getName();
		}
		String text = ((ILabelProvider) getCommonViewer().getLabelProvider()).getText(element);
		if (text == null)
		{
			return "";//$NON-NLS-1$
		}
		return text;
	}
	
	@Override
	protected void projectChanged(IProject oldProject, IProject newProject)
	{
		super.projectChanged(oldProject, newProject);
		clearText();
	}

	/**
	 * Clears the text in the filter text widget.
	 */
	protected void clearText()
	{
		currentFilterText = "";
		textChanged();
	}

	/**
	 * Set the text in the filter control.
	 * 
	 * @param string
	 */
	protected void setFilterText(String string)
	{
		currentFilterText = string;
		showFilterLabel(eyeball, "Filtering for '" + currentFilterText + "'");
		textChanged();
	}

	/**
	 * Create the refresh job for the receiver.
	 */
	private void createRefreshJob()
	{
		refreshJob = doCreateRefreshJob();
		refreshJob.setSystem(true);
	}

	/**
	 * Update the receiver after the text has changed.
	 */
	protected void textChanged()
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
		return 200;
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

				String text = getFilterString();
				if (text == null)
				{
					return Status.OK_STATUS;
				}

				boolean initial = currentFilterText == null || currentFilterText.equals("");
				if (initial)
				{
					patternFilter.setPattern(null);
				}
				else
				{
					patternFilter.setPattern(text);
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
					TreeItem[] is = getCommonViewer().getTree().getItems();
					for (int i = 0; i < is.length; i++)
					{
						TreeItem item = is[i];
						if (item.getExpanded())
						{
							getCommonViewer().setExpandedState(item.getData(), false);
						}
					}
					getCommonViewer().refresh(true);

					if (text.length() > 0 && !initial)
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

	protected String getFilterString()
	{
		return currentFilterText;
	}

	private String getResourceNameToFilterBy()
	{
		String text = hoveredItem.getText();
		IResource resource = getResource(hoveredItem);
		if (resource != null)
		{
			text = resource.getName(); // if we can, use the raw filename so we don't pick up decorators added
		}
		// Try and strip filename down to the resource name!
		if (text.endsWith("_controller.rb")) //$NON-NLS-1$
		{
			text = text.substring(0, text.indexOf("_controller")); //$NON-NLS-1$
			text = Inflector.singularize(text);
		}
		else if (text.endsWith("_controller_test.rb")) //$NON-NLS-1$
		{
			text = text.substring(0, text.indexOf("_controller_test.rb")); //$NON-NLS-1$
			text = Inflector.singularize(text);
		}
		else if (text.endsWith("_helper.rb")) //$NON-NLS-1$
		{
			text = text.substring(0, text.indexOf("_helper")); //$NON-NLS-1$
			text = Inflector.singularize(text);
		}
		else if (text.endsWith("_helper_test.rb")) //$NON-NLS-1$
		{
			text = text.substring(0, text.indexOf("_helper_test.rb")); //$NON-NLS-1$
			text = Inflector.singularize(text);
		}
		else if (text.endsWith("_test.rb")) //$NON-NLS-1$
		{
			text = text.substring(0, text.indexOf("_test.rb")); //$NON-NLS-1$
		}
		else if (text.endsWith("_spec.rb")) //$NON-NLS-1$
		{
			text = text.substring(0, text.indexOf("_spec.rb")); //$NON-NLS-1$
		}
		else if (text.endsWith(".yml")) //$NON-NLS-1$
		{
			if (resource != null)
			{
				IPath path = resource.getProjectRelativePath();
				if (path.segmentCount() >= 3 && path.segment(1).equals("fixtures")) //$NON-NLS-1$
				{
					text = text.substring(0, text.indexOf(".yml")); //$NON-NLS-1$
					text = Inflector.singularize(text);
				}
			}
		}
		else if (text.endsWith(".rb")) //$NON-NLS-1$
		{
			text = text.substring(0, text.indexOf(".rb")); //$NON-NLS-1$
		}
		else
		{
			// We need to grab the full path, so we can determine the resource name!
			if (resource != null)
			{
				IPath path = resource.getProjectRelativePath();
				if (path.segmentCount() >= 3 && path.segment(1).equals("views")) //$NON-NLS-1$
				{
					text = path.segment(2);
					text = Inflector.singularize(text);
				}
			}
		}
		return text;
	}

	private void removeHoveredItem()
	{
		if (hoveredItem == null)
			return;
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
		return CommonEditorPlugin.getDefault().getColorManager()
				.getColor(ThemeUtil.getActiveTheme().getLineHighlight());
	}

	private boolean filterOn()
	{
		return getFilterString() != null && getFilterString().trim().length() > 0
				&& !getFilterString().equals(initialText);
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
	
}
