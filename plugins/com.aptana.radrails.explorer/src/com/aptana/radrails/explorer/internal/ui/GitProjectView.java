package com.aptana.radrails.explorer.internal.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.navigator.framelist.FrameList;
import org.eclipse.ui.internal.navigator.framelist.TreeFrame;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.progress.WorkbenchJob;

import com.aptana.git.core.model.BranchChangedEvent;
import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryListener;
import com.aptana.git.core.model.IndexChangedEvent;
import com.aptana.git.core.model.RepositoryAddedEvent;
import com.aptana.git.ui.actions.CommitAction;
import com.aptana.git.ui.actions.PullAction;
import com.aptana.git.ui.actions.PushAction;
import com.aptana.git.ui.actions.StashAction;
import com.aptana.git.ui.actions.UnstashAction;
import com.aptana.radrails.explorer.ExplorerPlugin;

public class GitProjectView extends CommonNavigator implements IGitRepositoryListener
{
	private static final String BRANCH_SEPARATOR = "--------"; //$NON-NLS-1$

	private static final String CREATE_NEW_BRANCH_TEXT = Messages.GitProjectView_createNewBranchOption;

	/**
	 * Property we assign to a project to make it the active one that this view is filtered to.
	 */
	private static final String ACTIVE_PROJECT = "activeProject"; //$NON-NLS-1$

	/**
	 * Memento names for saving state of view and restoring it across launches.
	 */
	private static final String TAG_SELECTION = "selection"; //$NON-NLS-1$
	private static final String TAG_EXPANDED = "expanded"; //$NON-NLS-1$
	private static final String TAG_ELEMENT = "element"; //$NON-NLS-1$
	private static final String TAG_PATH = "path"; //$NON-NLS-1$
	private static final String TAG_CURRENT_FRAME = "currentFrame"; //$NON-NLS-1$

	private Combo projectCombo;
	protected IProject selectedProject;
	private Combo branchCombo;
	private StyledText summary;
	private Button pull;
	private Button push;
	private Button commit;
	private Button stash;
	private Button unstash;
	private Composite gitStuff;
	private Composite gitDetails;

	private FormData showGitDetailsData;
	private FormData hideGitDetailsData;

	private ResourceListener fResourceListener;

	/**
	 * Maximum time spent expanding the tree after the filter text has been updated (this is only used if we were able
	 * to at least expand the visible nodes)
	 */
	private static final long SOFT_MAX_EXPAND_TIME = 200;

	private Text filterText;
	/**
	 * The text to initially show in the filter text control.
	 */
	protected String initialText = Messages.GitProjectView_InitialFileFilterText;
	private String previousFilterText;

	private PathFilter patternFilter;
	private boolean narrowingDown;
	private WorkbenchJob refreshJob;

	private Button gitFilter;

	/**
	 * Fields for the focus/eyeball filtering
	 */
	protected TreeItem hoveredItem;
	protected int lastDrawnX;
	protected Object[] fExpandedElements;

	private Label expandCollapse;

	private Composite focus;

	@Override
	public void createPartControl(Composite aParent)
	{
		// Create our own parent
		Composite customComposite = new Composite(aParent, SWT.NONE);
		customComposite.setLayout(new FormLayout());
		// TODO Each composite we're hanging off here should probably be defined in it's own class and attached to this
		// view using an extension or something. that way we can mix and match and dynamically turn on and off the
		// various components (like filter, git actions, single project focus, etc)

		// Create our special git stuff
		gitStuff = new Composite(customComposite, SWT.NONE);
		gitStuff.setLayout(new FormLayout());
		FormData gitStuffLayoutData = new FormData();
		gitStuffLayoutData.top = new FormAttachment(0, 5);
		gitStuffLayoutData.left = new FormAttachment(0, 5);
		gitStuffLayoutData.right = new FormAttachment(100, -5);
		gitStuff.setLayoutData(gitStuffLayoutData);

		IProject[] projects = createProjectCombo(gitStuff); // TODO Attach project combo in it's own area, not in git
		// composite
		createGitDetailsComposite(gitStuff);
		// TODO Add a button/arrow to allow expanding/hiding the stuff below branch/commit widgets
		createExpandCollapseButton(gitDetails);
		createGitBranchCombo(gitDetails);
		createFilterButton(gitDetails);
		createCommitButton(gitDetails);
		createSummaryLabel(gitDetails);
		createPushButton(gitDetails);
		createPullButton(gitDetails);
		createStashButton(gitDetails);
		createUnstashButton(gitDetails);

		// focus filter stuff, attach top to bottom of 'gitStuff'
		focus = createFocusComposite(customComposite, gitStuff);

		// Now create the typical stuff for the navigator, attach top to bottom of 'focus'
		createNavigator(customComposite, focus);

		fResourceListener = new ResourceListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(fResourceListener, IResourceChangeEvent.POST_CHANGE);
		GitRepository.addListener(this);
		if (projects.length > 0)
			detectSelectedProject();

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
	}

	private void addFocusHover()
	{
		final Image eyeball = ExplorerPlugin.getImage("icons/full/obj16/eye.png"); //$NON-NLS-1$
		final int IMAGE_MARGIN = 2;
		final Tree tree = getCommonViewer().getTree();

		/*
		 * NOTE: MeasureItem and PaintItem are called repeatedly. Therefore it is critical for performance that these
		 * methods be as efficient as possible.
		 */
		tree.addListener(SWT.MeasureItem, new Listener()
		{
			public void handleEvent(Event event)
			{
				TreeItem item = (TreeItem) event.item;
				if (hoveredItem == null || !hoveredItem.equals(item))
					return;
				if (eyeball != null)
				{
					event.width += eyeball.getBounds().width + IMAGE_MARGIN;
				}
			}
		});
		tree.addListener(SWT.PaintItem, new Listener()
		{
			public void handleEvent(Event event)
			{
				TreeItem item = (TreeItem) event.item;
				if (hoveredItem == null || !hoveredItem.equals(item))
					return;
				if (eyeball != null)
				{
					lastDrawnX = event.x + event.width + IMAGE_MARGIN;
					int itemHeight = tree.getItemHeight();
					int imageHeight = eyeball.getBounds().height;
					int y = event.y + (itemHeight - imageHeight) / 2;
					event.gc.drawImage(eyeball, lastDrawnX, y);
				}
			}
		});

		getCommonViewer().getControl().addMouseMoveListener(new MouseMoveListener()
		{

			public void mouseMove(MouseEvent e)
			{
				if (hoveredItem == null)
					return;

				final TreeItem t = getCommonViewer().getTree().getItem(new Point(e.x, e.y));
				if (!hoveredItem.equals(t))
					removeHoveredItem();
			}
		});
		getCommonViewer().getControl().addMouseTrackListener(new MouseTrackAdapter()
		{
			@Override
			public void mouseExit(MouseEvent e)
			{
				super.mouseExit(e);
				if (hoveredItem == null)
					return;
				removeHoveredItem();
			}

			@Override
			public void mouseHover(MouseEvent e)
			{
				super.mouseHover(e);
				// If the filter is already on, we shouldn't do this stuff
				if (getFilterString() != null && getFilterString().trim().length() > 0
						&& !getFilterString().equals(initialText))
					return;
				final TreeItem t = getCommonViewer().getTree().getItem(new Point(e.x, e.y));
				if (hoveredItem != null && hoveredItem.equals(t))
					return;
				final Rectangle oldBounds = hoveredItem == null ? null : hoveredItem.getBounds();
				hoveredItem = t;
				final Rectangle newBounds = hoveredItem == null ? null : hoveredItem.getBounds();
				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						if (oldBounds != null)
							getCommonViewer().getTree().redraw(oldBounds.x, oldBounds.y, oldBounds.width,
									oldBounds.height, true);
						if (newBounds != null)
							getCommonViewer().getTree().redraw(newBounds.x, newBounds.y, newBounds.width,
									newBounds.height, true);
					}
				});
			}
		});

		getCommonViewer().getTree().addMouseListener(new MouseListener()
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
						textChanged();
					}
				}
			}

			public void mouseDoubleClick(MouseEvent e)
			{
			}
		});
	}

	private Composite createFocusComposite(Composite myComposite, Composite top)
	{
		Composite focus = new Composite(myComposite, SWT.BORDER);
		focus.setLayout(new GridLayout(2, false));
		FormData data2 = new FormData();
		data2.top = new FormAttachment(top);
		data2.right = new FormAttachment(100, 0);
		data2.left = new FormAttachment(0, 0);
		focus.setLayoutData(data2);

		patternFilter = new PathFilter();
		filterText = new Text(focus, SWT.SINGLE | SWT.BORDER | SWT.SEARCH | SWT.ICON_CANCEL);
		filterText.setText(initialText);
		filterText.addModifyListener(new ModifyListener()
		{
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			public void modifyText(ModifyEvent e)
			{
				textChanged();
			}
		});

		// if we're using a field with built in cancel we need to listen for
		// default selection changes (which tell us the cancel button has been
		// pressed)
		if ((filterText.getStyle() & SWT.ICON_CANCEL) != 0)
		{
			filterText.addSelectionListener(new SelectionAdapter()
			{
				/*
				 * (non-Javadoc)
				 * @see
				 * org.eclipse.swt.events.SelectionAdapter#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				public void widgetDefaultSelected(SelectionEvent e)
				{
					if (e.detail == SWT.ICON_CANCEL)
						clearText();
				}
			});
		}

		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		// if the text widget supported cancel then it will have it's own
		// integrated button. We can take all of the space.
		if ((filterText.getStyle() & SWT.ICON_CANCEL) != 0)
			gridData.horizontalSpan = 2;
		filterText.setLayoutData(gridData);
		return focus;
	}

	private void createNavigator(Composite myComposite, Composite top)
	{
		Composite viewer = new Composite(myComposite, SWT.NONE);
		viewer.setLayout(new FillLayout());
		FormData data2 = new FormData();
		data2.top = new FormAttachment(top);
		data2.bottom = new FormAttachment(100, 0);
		data2.right = new FormAttachment(100, 0);
		data2.left = new FormAttachment(0, 0);
		viewer.setLayoutData(data2);
		super.createPartControl(viewer);
	}

	private void createExpandCollapseButton(Composite parent)
	{
		expandCollapse = new Label(parent, SWT.FLAT | SWT.CENTER);
		expandCollapse.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		expandCollapse.setImage(ExplorerPlugin.getImage("icons/full/obj16/bullet_toggle_minus.png")); //$NON-NLS-1$
		expandCollapse.setToolTipText("Collapse");
		expandCollapse.addMouseListener(new MouseListener()
		{
			private boolean expanded = true;

			public void mouseUp(MouseEvent e)
			{
			}

			public void mouseDown(MouseEvent e)
			{
				expanded = !expanded;
				summary.setVisible(expanded);
				stash.setVisible(expanded);
				push.setVisible(expanded);
				pull.setVisible(expanded);
				unstash.setVisible(expanded);

				if (!expanded)
				{
					expandCollapse.setImage(ExplorerPlugin.getImage("icons/full/obj16/bullet_toggle_plus.png")); //$NON-NLS-1$
					expandCollapse.setToolTipText("Expand");
					FormData data = (FormData) gitDetails.getLayoutData();
					data.height = branchCombo.getBounds().height + 3; // margin of 3
					gitDetails.setLayoutData(data);
				}
				else
				{
					expandCollapse.setImage(ExplorerPlugin.getImage("icons/full/obj16/bullet_toggle_minus.png")); //$NON-NLS-1$
					expandCollapse.setToolTipText("Collapse");
					FormData data = (FormData) gitDetails.getLayoutData();
					data.height = SWT.DEFAULT;
					gitDetails.setLayoutData(data);
				}
				gitStuff.getParent().layout();
			}

			public void mouseDoubleClick(MouseEvent e)
			{
			}
		});
	}

	private void createStashButton(Composite parent)
	{
		stash = new Button(parent, SWT.FLAT | SWT.PUSH | SWT.CENTER);
		stash.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		stash.setImage(ExplorerPlugin.getImage("icons/full/elcl16/arrow_down.png")); //$NON-NLS-1$
		stash.setToolTipText(Messages.GitProjectView_StashTooltip);
		stash.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final StashAction action = new StashAction();
				action.selectionChanged(null, new StructuredSelection(selectedProject));
				Job job = new Job(Messages.GitProjectView_StashJobTitle)
				{

					@Override
					protected IStatus run(IProgressMonitor monitor)
					{
						action.run(null);
						refreshUI(GitRepository.getAttached(selectedProject));
						return Status.OK_STATUS;
					}
				};
				job.setUser(true);
				job.setPriority(Job.LONG);
				job.schedule();
			}
		});
	}

	private void createUnstashButton(Composite parent)
	{
		unstash = new Button(parent, SWT.FLAT | SWT.PUSH | SWT.CENTER);
		unstash.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		unstash.setImage(ExplorerPlugin.getImage("icons/full/elcl16/arrow_up.png")); //$NON-NLS-1$
		unstash.setToolTipText(Messages.GitProjectView_UnstashTooltip);
		unstash.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final UnstashAction action = new UnstashAction();
				action.selectionChanged(null, new StructuredSelection(selectedProject));
				Job job = new Job(Messages.GitProjectView_UnstashJobTitle)
				{

					@Override
					protected IStatus run(IProgressMonitor monitor)
					{
						action.run(null);
						refreshUI(GitRepository.getAttached(selectedProject));
						return Status.OK_STATUS;
					}
				};
				job.setUser(true);
				job.setPriority(Job.LONG);
				job.schedule();
			}
		});
	}

	private void createPullButton(Composite parent)
	{
		pull = new Button(parent, SWT.FLAT | SWT.PUSH | SWT.CENTER);
		pull.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		pull.setImage(ExplorerPlugin.getImage("icons/full/elcl16/arrow_left.png")); //$NON-NLS-1$
		pull.setToolTipText(Messages.GitProjectView_PullTooltip);
		pull.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final PullAction action = new PullAction();
				action.selectionChanged(null, new StructuredSelection(selectedProject));
				Job job = new Job(Messages.GitProjectView_PullJobTitle)
				{

					@Override
					protected IStatus run(IProgressMonitor monitor)
					{
						action.run(null);
						return Status.OK_STATUS;
					}
				};
				job.setUser(true);
				job.setPriority(Job.LONG);
				job.schedule();
			}
		});
	}

	private void createPushButton(Composite parent)
	{
		push = new Button(parent, SWT.FLAT | SWT.PUSH | SWT.CENTER);
		push.setImage(ExplorerPlugin.getImage("icons/full/elcl16/arrow_right.png")); //$NON-NLS-1$
		push.setToolTipText(Messages.GitProjectView_PushTooltip);
		push.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		push.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final PushAction action = new PushAction();
				action.selectionChanged(null, new StructuredSelection(selectedProject));
				Job job = new Job(Messages.GitProjectView_PushJobTitle)
				{

					@Override
					protected IStatus run(IProgressMonitor monitor)
					{
						action.run(null);
						refreshUI(GitRepository.getAttached(selectedProject));
						return Status.OK_STATUS;
					}
				};
				job.setUser(true);
				job.setPriority(Job.LONG);
				job.schedule();
			}
		});
	}

	private void createSummaryLabel(Composite parent)
	{
		summary = new StyledText(parent, SWT.WRAP);
		summary.setText(""); //$NON-NLS-1$
		summary.setEditable(false);
		summary.setEnabled(false);
		summary.setBackground(parent.getBackground());
		GridData summaryData = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, true);
		summaryData.verticalSpan = 2;
		summaryData.horizontalSpan = 2;
		summary.setLayoutData(summaryData);
	}

	private void createFilterButton(Composite parent)
	{
		gitFilter = new Button(parent, SWT.FLAT | SWT.TOGGLE | SWT.CENTER);
		gitFilter.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		gitFilter.setImage(ExplorerPlugin.getImage("icons/full/elcl16/filter.png")); //$NON-NLS-1$
		gitFilter.setToolTipText(Messages.GitProjectView_ChangedFilesFilterTooltip);
		gitFilter.addSelectionListener(new SelectionAdapter()
		{
			private GitChangedFilesFilter fChangedFilesFilter;

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (fChangedFilesFilter == null)
				{
					fChangedFilesFilter = new GitChangedFilesFilter();
					getCommonViewer().addFilter(fChangedFilesFilter);
					getCommonViewer().expandAll();
				}
				else
				{
					getCommonViewer().removeFilter(fChangedFilesFilter);
					fChangedFilesFilter = null;
				}
			}
		});
	}

	private void createCommitButton(Composite parent)
	{
		commit = new Button(parent, SWT.FLAT | SWT.PUSH | SWT.CENTER);
		commit.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		commit.setImage(ExplorerPlugin.getImage("icons/full/elcl16/disk.png")); //$NON-NLS-1$
		commit.setToolTipText(Messages.GitProjectView_CommitTooltip);
		commit.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				CommitAction action = new CommitAction();
				ISelection selection = new StructuredSelection(selectedProject);
				action.selectionChanged(null, selection);
				action.run(null);
			}
		});
	}

	private void createGitBranchCombo(Composite parent)
	{
		branchCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData branchComboData = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		branchCombo.setLayoutData(branchComboData);
		branchCombo.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				setNewBranch(branchCombo.getText());
			}
		});
	}

	private void createGitDetailsComposite(Composite parent)
	{
		gitDetails = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(4, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		gitDetails.setLayout(layout);
		showGitDetailsData = new FormData();
		showGitDetailsData.top = new FormAttachment(projectCombo, 0);
		showGitDetailsData.bottom = new FormAttachment(100, 0);
		showGitDetailsData.right = new FormAttachment(100, 0);
		showGitDetailsData.left = new FormAttachment(0, 0);

		hideGitDetailsData = new FormData();
		hideGitDetailsData.top = new FormAttachment(0);
		hideGitDetailsData.bottom = new FormAttachment(0);
		hideGitDetailsData.right = new FormAttachment(0);
		hideGitDetailsData.left = new FormAttachment(0);

		gitDetails.setLayoutData(hideGitDetailsData);
	}

	private IProject[] createProjectCombo(Composite parent)
	{
		projectCombo = new Combo(parent, SWT.DROP_DOWN | SWT.MULTI | SWT.READ_ONLY);
		FormData projectData = new FormData();
		projectData.left = new FormAttachment(0, 0);
		projectData.top = new FormAttachment(0, 0);
		projectData.right = new FormAttachment(100, 0);
		projectCombo.setLayoutData(projectData);
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject iProject : projects)
		{
			projectCombo.add(iProject.getName());
		}
		projectCombo.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				setActiveProject(projectCombo.getText());
			}
		});
		return projects;
	}

	@Override
	public void dispose()
	{
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(fResourceListener);
		GitRepository.removeListener(this);
		super.dispose();
	}

	@Override
	public void saveState(IMemento memento)
	{
		// TODO Auto-generated method stub
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
				ArrayList elements = new ArrayList();
				IMemento[] elementMem = childMem.getChildren(TAG_ELEMENT);
				for (int i = 0; i < elementMem.length; i++)
				{
					Object element = container.findMember(elementMem[i].getString(TAG_PATH));
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
				ArrayList list = new ArrayList();
				IMemento[] elementMem = childMem.getChildren(TAG_ELEMENT);
				for (int i = 0; i < elementMem.length; i++)
				{
					Object element = container.findMember(elementMem[i].getString(TAG_PATH));
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
	String getFrameName(Object element)
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

	protected void reloadProjects()
	{
		Job job = new UIJob("Reload Projects") //$NON-NLS-1$
		{

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				// FIXME What if the active project was deleted or renamed?
				projectCombo.removeAll();
				IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
				for (IProject iProject : projects)
				{
					projectCombo.add(iProject.getName());
				}
				projectCombo.setText(selectedProject.getName());
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.setPriority(Job.INTERACTIVE);
		job.schedule();
	}

	protected boolean setNewBranch(String branchName)
	{
		if (branchName.endsWith("*")) //$NON-NLS-1$
			branchName = branchName.substring(0, branchName.length() - 1);

		final GitRepository repo = GitRepository.getAttached(selectedProject);
		if (repo == null)
			return false;
		if (branchName.equals(repo.currentBranch()))
			return false;
		// If user selected separator, revert
		if (branchName.equals(BRANCH_SEPARATOR))
		{
			revertToCurrentBranch(repo);
			return false;
		}
		// If user selected "Create New..." then pop a dialog to generate a new branch
		if (branchName.equals(CREATE_NEW_BRANCH_TEXT))
		{
			InputDialog dialog = new InputDialog(getSite().getShell(),
					Messages.GitProjectView_CreateBranchDialog_Title,
					Messages.GitProjectView_CreateBranchDialog_Message, "", //$NON-NLS-1$
					new IInputValidator()
					{

						public String isValid(String newText)
						{
							if (newText == null || newText.trim().length() == 0)
								return Messages.GitProjectView_NonEmptyBranchNameMessage;
							if (newText.trim().contains(" ") || newText.trim().contains("\t")) //$NON-NLS-1$ //$NON-NLS-2$
								return Messages.GitProjectView_NoWhitespaceBranchNameMessage;
							if (repo.localBranches().contains(newText.trim()))
								return Messages.GitProjectView_BranchAlreadyExistsMessage;
							if (!repo.validBranchName(newText.trim()))
								return Messages.GitProjectView_InvalidBranchNameMessage;
							return null;
						}
					});
			if (dialog.open() != Window.OK)
			{
				revertToCurrentBranch(repo);
				return false;
			}
			branchName = dialog.getValue().trim();
			if (!repo.createBranch(branchName))
			{
				revertToCurrentBranch(repo);
				return false;
			}
		}
		if (repo.switchBranch(branchName))
		{
			refreshViewer();
			return true;
		}
		else
		{
			revertToCurrentBranch(repo);
			return false;
		}
	}

	private void revertToCurrentBranch(final GitRepository repo)
	{
		Job job = new UIJob("") //$NON-NLS-1$
		{

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				String currentBranchName = repo.currentBranch();
				if (repo.isDirty())
					currentBranchName += "*"; //$NON-NLS-1$
				branchCombo.setText(currentBranchName);
				// TODO Pop a dialog saying we couldn't branches
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.setPriority(Job.INTERACTIVE);
		job.schedule();
	}

	private void detectSelectedProject()
	{
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		if (projects == null)
			return;
		for (IProject iProject : projects)
		{
			try
			{
				String value = iProject.getPersistentProperty(new QualifiedName(ExplorerPlugin.PLUGIN_ID,
						ACTIVE_PROJECT));
				if (value != null && value.equals(Boolean.TRUE.toString()))
				{
					projectCombo.setText(iProject.getName());
					setActiveProject(iProject.getName());
					return;
				}
			}
			catch (CoreException e)
			{
				ExplorerPlugin.logError(e);
			}
		}
	}

	protected void setActiveProject(String projectName)
	{
		IProject newSelectedProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (newSelectedProject == null || (selectedProject != null && newSelectedProject.equals(selectedProject)))
			return;
		try
		{
			if (selectedProject != null)
			{
				selectedProject
						.setPersistentProperty(new QualifiedName(ExplorerPlugin.PLUGIN_ID, ACTIVE_PROJECT), null);
			}
			selectedProject = newSelectedProject;
			selectedProject.setPersistentProperty(new QualifiedName(ExplorerPlugin.PLUGIN_ID, ACTIVE_PROJECT),
					Boolean.TRUE.toString());
			refreshUI(GitRepository.getAttached(newSelectedProject));
			// Refresh the view so our filter gets updated!
			clearText();
			refreshViewer();
		}
		catch (CoreException e)
		{
			ExplorerPlugin.logError(e);
		}
	}

	private void populateBranches(GitRepository repo)
	{
		branchCombo.removeAll();
		if (repo == null)
			return;
		// FIXME This doesn't seem to indicate proper dirty status and changed files on initial load!
		String currentBranchName = repo.currentBranch();
		for (String branchName : repo.localBranches())
		{
			if (branchName.equals(currentBranchName) && repo.isDirty())
				branchCombo.add(branchName + "*"); //$NON-NLS-1$
			else
				branchCombo.add(branchName);
		}
		branchCombo.add(BRANCH_SEPARATOR);
		branchCombo.add(CREATE_NEW_BRANCH_TEXT);
		if (repo.isDirty())
			currentBranchName += "*"; //$NON-NLS-1$
		branchCombo.setText(currentBranchName);
		branchCombo.pack(true);
	}

	private void refreshViewer()
	{
		if (getCommonViewer() == null)
			return;
		getCommonViewer().refresh();
	}

	public void indexChanged(final IndexChangedEvent e)
	{
		refreshUI(e.getRepository());
	}

	protected void refreshUI(final GitRepository repository)
	{
		Job job = new UIJob("update UI for index changes") //$NON-NLS-1$
		{

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				// Update the branch list so we can reset the dirty status on the branch
				populateBranches(repository);
				updateSummaryText(repository);
				if (repository == null)
				{
					push.setEnabled(false);
					pull.setEnabled(false);
					stash.setEnabled(false);
					commit.setEnabled(false);
					gitFilter.setEnabled(false);
					push.setVisible(false);
					pull.setVisible(false);
					commit.setVisible(false);
					stash.setVisible(false);
					unstash.setVisible(false);
					gitFilter.setVisible(false);
					branchCombo.setVisible(false);
					summary.setVisible(false);
					gitDetails.setLayoutData(hideGitDetailsData);
				}
				else
				{
					// Disable push unless there's a remote tracking branch and we have committed changes
					String[] commitsAhead = repository.commitsAhead(repository.currentBranch());
					push.setEnabled(commitsAhead != null && commitsAhead.length > 0);
					// Disable pull unless there's a remote tracking branch
					pull.setEnabled(repository.trackingRemote(repository.currentBranch()));
					// TODO Disable stash unless there are staged or unstaged (but not untracked) changes
					stash.setEnabled(true);
					// TODO Disable unstash unless there's a refs/stash ref
					unstash.setEnabled(true);
					// TODO Disable commit unless there are changes to commit
					commit.setEnabled(true);
					gitFilter.setEnabled(gitFilterEnabled(repository));
					push.setVisible(true);
					pull.setVisible(true);
					commit.setVisible(true);
					stash.setVisible(true);
					unstash.setVisible(true);
					summary.setVisible(true);
					gitFilter.setVisible(true);
					branchCombo.setVisible(true);
					gitDetails.setLayoutData(showGitDetailsData);
					// Make the summary as wide as the project combo, and as tall as the 3 icons
					GridData summaryData = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, true);
					summaryData.verticalSpan = 2;
					summaryData.horizontalSpan = 2;
					summaryData.widthHint = projectCombo.getBounds().width + expandCollapse.getBounds().width;
					// Minimum height should be to bottom of push, pull, stash icons ((2 * icon height) + (1 * space
					// between icons))
					summaryData.minimumHeight = (commit.getBounds().height * 2)
							+ ((GridLayout) gitDetails.getLayout()).verticalSpacing * 1;
					summary.setLayoutData(summaryData);
				}
				gitStuff.getParent().layout();
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.setPriority(Job.INTERACTIVE);
		job.schedule();
	}

	private void updateSummaryText(GitRepository repo)
	{
		if (repo == null)
		{
			summary.setText(""); //$NON-NLS-1$
			summary.setStyleRanges(new StyleRange[0]);
			return;
		}
		Set<StyleRange> ranges = new HashSet<StyleRange>();
		StringBuilder builder = new StringBuilder();
		if (repo.hasMerges())
		{
			builder.append(Messages.GitProjectView_UnresolvedMerges_msg);
			ranges.add(new StyleRange(0, builder.length(), getSite().getShell().getDisplay().getSystemColor(
					SWT.COLOR_RED), null));
		}
		int stagedCount = 0;
		int addedCount = 0;
		int unstagedCount = 0;
		if (repo.index().changedFiles() != null)
		{
			for (ChangedFile file : repo.index().changedFiles())
			{
				if (file == null)
					continue;
				if (file.hasStagedChanges())
				{
					stagedCount++;
				}
				else if (file.getStatus().equals(ChangedFile.Status.NEW))
				{
					addedCount++;
				}
				else
				{
					unstagedCount++;
				}
			}
		}
		String branch = repo.currentBranch();
		String[] commitsAhead = repo.commitsAhead(branch);
		if (commitsAhead != null && commitsAhead.length > 0)
		{
			builder.append(NLS.bind(Messages.GitProjectView_BranchAhead_msg, new Object[] {
					repo.remoteTrackingBranch(branch).shortName(), commitsAhead.length }));
		}
		builder.append(NLS.bind(Messages.GitProjectView_FileCounts, new Object[] { stagedCount, unstagedCount,
				addedCount }));
		int legendStart = builder.length();
		builder.append(Messages.GitProjectView_FileCountsLabel);
		ranges.add(new StyleRange(legendStart, builder.length() - legendStart, null, null, SWT.ITALIC));
		summary.setText(builder.toString());
		for (StyleRange range : ranges)
		{
			summary.setStyleRange(range);
		}
	}

	public void repositoryAdded(RepositoryAddedEvent e)
	{
		// TODO Someone may have just attached the current project to a repo! We need to update our UI if they did
		GitRepository repo = e.getRepository();
		GitRepository selectedRepo = GitRepository.getAttached(selectedProject);
		if (selectedRepo != null && selectedRepo.equals(repo))
			refreshUI(e.getRepository());
	}

	private class ResourceListener implements IResourceChangeListener
	{

		public void resourceChanged(IResourceChangeEvent event)
		{
			IResourceDelta delta = event.getDelta();
			if (delta == null)
				return;
			try
			{
				delta.accept(new IResourceDeltaVisitor()
				{

					public boolean visit(IResourceDelta delta) throws CoreException
					{
						IResource resource = delta.getResource();
						if (resource.getType() == IResource.FILE || resource.getType() == IResource.FOLDER)
							return false;
						if (resource.getType() == IResource.ROOT)
							return true;
						if (resource.getType() == IResource.PROJECT)
						{
							// a project was added, removed, or changed!
							reloadProjects();
						}
						return false;
					}
				});
			}
			catch (CoreException e)
			{
				ExplorerPlugin.logError(e);
			}
		}
	}

	public void branchChanged(BranchChangedEvent e)
	{
		GitRepository repo = e.getRepository();
		GitRepository selectedRepo = GitRepository.getAttached(selectedProject);
		if (selectedRepo != null && selectedRepo.equals(repo))
			refreshUI(e.getRepository());
	}

	/**
	 * Clears the text in the filter text widget.
	 */
	protected void clearText()
	{
		setFilterText(initialText);
		textChanged();
	}

	/**
	 * Set the text in the filter control.
	 * 
	 * @param string
	 */
	protected void setFilterText(String string)
	{
		if (filterText != null)
		{
			filterText.setText(string);
			selectAll();
		}
	}

	/**
	 * Select all text in the filter text field.
	 */
	protected void selectAll()
	{
		if (filterText != null)
		{
			filterText.selectAll();
		}
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
		narrowingDown = previousFilterText == null || getFilterString().startsWith(previousFilterText);
		previousFilterText = getFilterString();
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

				boolean initial = initialText != null && initialText.equals(text);
				if (initial)
				{
					patternFilter.setPattern(null);
				}
				else if (text != null)
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
					if (!narrowingDown)
					{
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
		return filterText != null ? filterText.getText() : null;
	}

	private boolean gitFilterEnabled(final GitRepository repository)
	{
		// TODO The files also have to be children of the active project!
		return !repository.index().changedFiles().isEmpty();
	}

	private String getResourceNameToFilterBy()
	{
		String text = hoveredItem.getText();
		Object data = hoveredItem.getData();
		if (data instanceof IResource)
		{
			IResource resource = (IResource) data;
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
			if (data instanceof IResource)
			{
				IResource resource = (IResource) data;
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
			if (data instanceof IResource)
			{
				IResource resource = (IResource) data;
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
		final Rectangle bounds = hoveredItem.getBounds();
		hoveredItem = null;
		Display.getDefault().asyncExec(new Runnable()
		{

			public void run()
			{
				getCommonViewer().getTree().redraw(bounds.x, bounds.y, bounds.width, bounds.height, true);
			}
		});
	}

}
