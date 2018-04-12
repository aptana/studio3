/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.actions;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TableDropTargetEffect;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ActiveShellExpression;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.git.core.IDebugScopes;
import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.DiffFormatter;
import com.aptana.git.ui.GitUIPlugin;
import com.aptana.ui.util.UIUtils;

class CommitDialog extends StatusDialog
{
	/**
	 * Context specific to this dialog.
	 */
	private static final String COMMIT_DIALOG_CONTEXT_ID = "com.aptana.git.ui.context.dialog.commit"; //$NON-NLS-1$

	/**
	 * Command specific to this dialog to perform a commit (just like hitting Ok/Commit button), so we can bind a
	 * keybinding to perform this.
	 */
	private static final String PERFORM_COMMIT_COMMAND_ID = "com.aptana.git.ui.command.commit.dialog"; //$NON-NLS-1$

	private static final String CHANGED_FILE_DATA_KEY = "changedFile"; //$NON-NLS-1$

	private GitRepository gitRepository;
	private Text commitMessage;
	private String fMessage;
	private Table unstagedTable;
	private Table stagedTable;
	private Control draggingFromTable;

	private Image newFileImage;
	private Image deletedFileImage;
	private Image emptyFileImage;
	private Scrollable diffArea;
	private ChangedFile fLastDiffFile;

	private StagingButtons unstageButtons;
	private StagingButtons stageButtons;

	private IContextActivation contextActivation;

	private IHandlerActivation commitHandler;

	protected CommitDialog(Shell parentShell, GitRepository gitRepository)
	{
		super(parentShell);
		Assert.isNotNull(gitRepository, "Must have a non-null git repository!"); //$NON-NLS-1$
		this.gitRepository = gitRepository;
		newFileImage = GitUIPlugin.getImage("icons/obj16/new_file.png"); //$NON-NLS-1$
		deletedFileImage = GitUIPlugin.getImage("icons/obj16/deleted_file.png"); //$NON-NLS-1$
		emptyFileImage = GitUIPlugin.getImage("icons/obj16/empty_file.png"); //$NON-NLS-1$
		fLastDiffFile = null;
	}

	@Override
	protected IDialogSettings getDialogBoundsSettings()
	{
		IDialogSettings compareSettings = GitUIPlugin.getDefault().getDialogSettings();
		String sectionName = this.getClass().getName();
		IDialogSettings dialogSettings = compareSettings.getSection(sectionName);
		if (dialogSettings == null)
		{
			dialogSettings = compareSettings.addNewSection(sectionName);
		}
		return dialogSettings;
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite container = (Composite) super.createDialogArea(parent);
		parent.getShell().setText(
				MessageFormat.format(Messages.CommitDialog_Changes, this.gitRepository.currentBranch()));

		container.setLayout(new GridLayout(1, true));

		createDiffArea(container);

		SashForm sashForm = new SashForm(container, SWT.HORIZONTAL);
		// Make it fill grid, so when we resize it still does...
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		sashForm.setLayout(new FillLayout());

		createUnstagedFileArea(sashForm);
		createCommitMessageArea(sashForm);
		createStagedFileArea(sashForm);

		sashForm.setSashWidth(5);
		sashForm.setWeights(new int[] { 35, 30, 35 });

		validate();
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
		{
			public void run()
			{
				packTable(stagedTable);
				packTable(unstagedTable);
				// Select the first item in staged if there is one
				if (unstagedTable.getItemCount() > 0)
				{
					unstagedTable.select(0);
					ChangedFile file = getChangedFile(unstagedTable.getItem(0));
					updateDiff(false, file);
					stageButtons.updateSelectionButton();
				}
				else if (stagedTable.getItemCount() > 0)
				{
					stagedTable.select(0);
					ChangedFile file = getChangedFile(stagedTable.getItem(0));
					updateDiff(true, file);
					unstageButtons.updateSelectionButton();
				}
			}
		});
		return container;
	}

	@Override
	public void create()
	{
		super.create();
		// forces initial validation when the dialog first comes up
		validate();
	}

	/**
	 * Override the default implementation to get a bigger commit dialog on large monitors.
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#getInitialSize()
	 */
	protected Point getInitialSize()
	{
		IDialogSettings dialogSettings = getDialogBoundsSettings();
		try
		{
			dialogSettings.getInt("DIALOG_WIDTH"); //$NON-NLS-1$
		}
		catch (NumberFormatException e)
		{
			// The dialog settings are empty, so we need to compute the initial
			// size according to the size of the monitor. Large monitors will get a bigger commit dialog.
			Composite parent = getShell().getParent();
			Monitor monitor = getShell().getDisplay().getPrimaryMonitor();
			if (parent != null)
			{
				monitor = parent.getMonitor();
			}
			Rectangle monitorBounds = monitor.getClientArea();
			return new Point((int) (0.618 * monitorBounds.width), (int) (0.618 * monitorBounds.height));
		}
		return super.getInitialSize();
	}

	private void createDiffArea(Composite container)
	{
		try
		{
			diffArea = new Browser(container, SWT.BORDER);
		}
		catch (SWTError e)
		{
			// most likely cause is that browser stuff isn't set up on Linux. We provide a warning message in validate()
			diffArea = new Text(container, SWT.BORDER | SWT.MULTI);
		}
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 300;
		diffArea.setLayoutData(data);
		setDiffText(Messages.CommitDialog_NoFileSelected);
	}

	private void setDiffText(String msg)
	{
		if (diffArea instanceof Browser)
		{
			((Browser) diffArea).setText(msg);
		}
		else
		{
			((Text) diffArea).setText(msg);
		}
	}

	private void createUnstagedFileArea(SashForm sashForm)
	{
		createFileArea(sashForm, false);
	}

	private void createStagedFileArea(SashForm sashForm)
	{
		createFileArea(sashForm, true);
	}

	private void createFileArea(SashForm sashForm, boolean staged)
	{
		Composite composite = new Composite(sashForm, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		StagingButtons buttons = null;
		if (staged)
		{
			buttons = new StagingButtons(composite, Messages.CommitDialog_UnstageAllMarker,
					Messages.CommitDialog_UnstageAll, Messages.CommitDialog_UnstageSelectedMarker,
					Messages.CommitDialog_UnstageSelected);
			createTableComposite(composite, staged);
			buttons.setTable(stagedTable, staged);
			unstageButtons = buttons;
		}
		else
		{
			createTableComposite(composite, staged);
			buttons = new StagingButtons(composite, Messages.CommitDialog_StageAllMarker,
					Messages.CommitDialog_StageAll, Messages.CommitDialog_StageSelectedMarker,
					Messages.CommitDialog_StageSelected);
			buttons.setTable(unstagedTable, staged);
			stageButtons = buttons;
		}
	}

	// Creates a table with a title label on top of it.
	private Composite createTableComposite(Composite parent, boolean staged)
	{
		Composite tableComp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 1;
		layout.marginWidth = 1;
		tableComp.setLayout(layout);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 200;
		data.widthHint = 250;
		tableComp.setLayoutData(data);
		Label label = new Label(tableComp, SWT.NONE);
		Table table = null;
		if (staged)
		{
			label.setText(Messages.CommitDialog_StagedChanges);
			table = createTable(tableComp, true);
		}
		else
		{
			label.setText(Messages.CommitDialog_UnstagedChanges);
			table = createTable(tableComp, false);
		}
		if (staged)
		{
			stagedTable = table;
		}
		else
		{
			unstagedTable = table;
		}
		return tableComp;
	}

	private void createCommitMessageArea(SashForm sashForm)
	{
		Composite msgComp = new Composite(sashForm, SWT.NONE);
		GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = 0;
		layout.marginWidth = 0;
		msgComp.setLayout(layout);
		Label messageLabel = new Label(msgComp, SWT.NONE);
		messageLabel.setText(Messages.CommitDialog_MessageLabel);
		commitMessage = new Text(msgComp, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
		commitMessage.setText(this.gitRepository.getPrepopulatedCommitMessage());
		commitMessage.addKeyListener(new KeyListener()
		{

			public void keyReleased(KeyEvent e)
			{
				validate();
			}

			public void keyPressed(KeyEvent e)
			{
			}
		});
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 200;
		data.widthHint = 300;
		commitMessage.setLayoutData(data);

		IContextService service = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);
		contextActivation = service.activateContext(COMMIT_DIALOG_CONTEXT_ID);

		IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
		IHandler handler = new AbstractHandler()
		{
			public Object execute(ExecutionEvent event)
			{
				okPressed();
				return null;
			}
		};
		commitHandler = handlerService.activateHandler(PERFORM_COMMIT_COMMAND_ID, handler, new ActiveShellExpression(
				getShell()));
	}

	@Override
	public boolean close()
	{
		if (contextActivation != null)
		{
			IContextService service = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);
			service.deactivateContext(contextActivation);
			contextActivation = null;
		}
		if (commitHandler != null)
		{
			IHandlerService service = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
			service.deactivateHandler(commitHandler);
			commitHandler.getHandler().dispose();
			commitHandler = null;
		}
		return super.close();
	}

	private Table createTable(Composite composite, final boolean staged)
	{
		Table table = new Table(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 200;
		data.widthHint = 250;
		table.setLayoutData(data);
		String[] titles = { " ", Messages.CommitDialog_PathColumnLabel }; //$NON-NLS-1$
		int[] widths = new int[] { 20, 250 };
		for (int i = 0; i < titles.length; i++)
		{
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(titles[i]);
			column.setWidth(widths[i]);
		}
		List<ChangedFile> changedFiles = gitRepository.index().changedFiles();
		Collections.sort(changedFiles);
		for (ChangedFile file : changedFiles)
		{
			boolean match = false;
			if (staged && file.hasStagedChanges())
			{
				match = true;
			}
			else if (!staged && file.hasUnstagedChanges())
			{
				match = true;
			}

			if (match)
			{
				createTableItem(table, file, false);
			}
		}

		// Drag and Drop
		// FIXME If user drags and drops while we're still crunching on last drag/drop then we end up hanging
		// Seems to be related to manipulating the table here before we receive the index changed callback
		Transfer[] types = new Transfer[] { LocalSelectionTransfer.getTransfer() };

		// Drag Source
		DragSource source = new DragSource(table, DND.DROP_MOVE);
		source.setTransfer(types);
		source.addDragListener(new DragSourceAdapter()
		{
			public void dragStart(DragSourceEvent event)
			{
				DragSource ds = (DragSource) event.widget;
				draggingFromTable = ds.getControl();

				LocalSelectionTransfer.getTransfer().setSelection(
						new StructuredSelection(((Table) draggingFromTable).getSelection()));
				LocalSelectionTransfer.getTransfer().setSelectionSetTime(event.time & 0xFFFFFFFFL);
			}

			public void dragSetData(DragSourceEvent event)
			{
				// do nothing
			}
		});

		// Create the drop target
		DropTarget target = new DropTarget(table, DND.DROP_MOVE);
		target.setTransfer(types);
		if (table.getItemCount() == 0)
			target.setDropTargetEffect(null);
		target.addDropListener(new DropTargetAdapter()
		{
			public void dropAccept(DropTargetEvent event)
			{
				DropTarget dp = (DropTarget) event.widget;
				if (dp.getControl() == draggingFromTable)
				{
					event.detail = DND.DROP_NONE;
				}
			}

			public void dragEnter(DropTargetEvent event)
			{
				// Allow dropping text only
				for (int i = 0, n = event.dataTypes.length; i < n; i++)
				{
					if (LocalSelectionTransfer.getTransfer().isSupportedType(event.dataTypes[i]))
					{
						event.currentDataType = event.dataTypes[i];
					}
				}
				event.operations = DND.DROP_MOVE;
			}

			public void dragOver(DropTargetEvent event)
			{
				event.feedback = DND.FEEDBACK_SCROLL;
			}

			@SuppressWarnings("unchecked")
			public void drop(DropTargetEvent event)
			{
				if (!LocalSelectionTransfer.getTransfer().isSupportedType(event.currentDataType))
					return;
				// Get the dropped data
				IStructuredSelection selection = (IStructuredSelection) event.data;
				moveItems(!staged, ((List<TableItem>) selection.toList()).toArray(new TableItem[selection.size()]));
			}
		});

		table.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				super.widgetSelected(e);
				if (e.item == null)
					return;
				TableItem item = (TableItem) e.item;
				updateDiff(staged, getChangedFile(item));
			}
		});
		// Allow double-clicking to toggle staged/unstaged
		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				if (e.getSource() == null)
					return;
				Table table = (Table) e.getSource();
				Point point = new Point(e.x, e.y);
				TableItem item = table.getItem(point);
				if (item == null)
				{
					return;
				}
				// did user click on file image? If so, toggle staged/unstage
				Rectangle imageBounds = item.getBounds(0);
				if (imageBounds.contains(point))
				{
					moveItems(staged, new TableItem[] { item });
				}
			}

			@Override
			public void mouseDoubleClick(MouseEvent e)
			{
				if (e.getSource() == null)
					return;
				Table table = (Table) e.getSource();
				TableItem[] selected = table.getSelection();
				moveItems(staged, selected);
			}
		});
		// Custom drawing so we can truncate filepaths in middle...
		table.addListener(SWT.EraseItem, new Listener()
		{

			public void handleEvent(Event event)
			{
				// Only draw the text custom
				if (event.index != 1)
					return;

				event.detail &= ~SWT.FOREGROUND;
			}
		});
		table.addListener(SWT.PaintItem, new Listener()
		{

			public void handleEvent(Event event)
			{
				// Only draw the text custom
				if (event.index != 1)
					return;
				TableItem item = (TableItem) event.item;
				String text = item.getText(event.index);

				// Truncate middle of string
				Table theTable = (Table) event.widget;
				int width = theTable.getColumn(event.index).getWidth();
				Point p = event.gc.stringExtent(text); // is text wider than available width?
				if (p.x > width)
				{
					text = UIUtils.shortenText(text, width);
				}
				event.gc.drawText(text, event.x, event.y, true);

				event.detail &= ~SWT.FOREGROUND;
			}
		});

		if (!staged)
		{
			final Table myTable = table;
			MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
			menuMgr.setRemoveAllWhenShown(true);
			menuMgr.addMenuListener(new IMenuListener()
			{
				public void menuAboutToShow(IMenuManager manager)
				{
					TableItem[] selected = myTable.getSelection();
					List<IResource> files = new ArrayList<IResource>();
					final List<ChangedFile> changedFiles = new ArrayList<ChangedFile>();
					for (TableItem item : selected)
					{
						ChangedFile file = getChangedFile(item);
						if (file != null)
						{
							changedFiles.add(file);
							IFile iFile = gitRepository.getFileForChangedFile(file);
							if (iFile != null)
							{
								files.add(iFile);
							}
						}
					}

					ContributionItem ci = new ContributionItem()
					{
						public void fill(Menu menu, int index)
						{
							MenuItem item = new MenuItem(menu, SWT.NONE);
							item.setText(Messages.CommitDialog_RevertLabel);
							// need to remove the file(s) from staged table once action runs
							item.addSelectionListener(new SelectionAdapter()
							{
								@Override
								public void widgetSelected(SelectionEvent e)
								{
									// need to make a copy because operation will actually change input files.
									final List<ChangedFile> copy = new ArrayList<ChangedFile>(changedFiles);
									for (ChangedFile cf : changedFiles)
									{
										copy.add(cf.clone());
									}

									gitRepository.index().discardChangesForFiles(changedFiles);

									PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
									{

										public void run()
										{
											// If this file was shown in diff area, we need to blank the diff area!
											if (fLastDiffFile != null)
											{
												for (ChangedFile file : copy)
												{
													if (file != null && file.equals(fLastDiffFile))
													{
														updateDiff(null, Messages.CommitDialog_NoFileSelected);
													}
												}
											}
											removeDraggedFilesFromSource(unstagedTable, copy);
										}
									});
								}
							});
						}
					};
					manager.add(ci);
					// Other plug-ins can contribute there actions here
					manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
				}
			});
			Menu menu = menuMgr.createContextMenu(table);
			table.setMenu(menu);
		}

		return table;
	}

	private synchronized void unstageFiles(final Collection<ChangedFile> files)
	{
		// TODO Add a listener to the repo on creation and have toggleStageStatus get invoked with diff!
		// Temporarily disable the tables
		stagedTable.setEnabled(false);
		unstagedTable.setEnabled(false);
		// make a copy so we can erase from original table correctly since their flags get changed by operation
		final List<ChangedFile> copy = new ArrayList<ChangedFile>(files);
		Collections.copy(copy, new ArrayList<ChangedFile>(files));
		IStatus status = gitRepository.index().unstageFiles(files);
		if (status.isOK())
		{
			getParentShell().getDisplay().asyncExec(new Runnable()
			{

				public void run()
				{
					toggleStageStatus(copy, false);
					stagedTable.setEnabled(true);
					unstagedTable.setEnabled(true);
				}
			});
		}
		else
		{
			stagedTable.setEnabled(true);
			unstagedTable.setEnabled(true);
		}
	}

	private synchronized void stageFiles(final Collection<ChangedFile> files)
	{
		// Temporarily disable the tables
		stagedTable.setEnabled(false);
		unstagedTable.setEnabled(false);
		// make a copy so we can erase from original table correctly since their flags get changed by operation
		final List<ChangedFile> copy = new ArrayList<ChangedFile>(files);
		Collections.copy(copy, new ArrayList<ChangedFile>(files));
		IStatus status = gitRepository.index().stageFiles(files);
		if (status.isOK())
		{
			getParentShell().getDisplay().asyncExec(new Runnable()
			{

				public void run()
				{
					toggleStageStatus(copy, true);
					stagedTable.setEnabled(true);
					unstagedTable.setEnabled(true);
				}
			});
		}
		else
		{
			stagedTable.setEnabled(true);
			unstagedTable.setEnabled(true);
		}
	}

	private void toggleStageStatus(Collection<ChangedFile> files, boolean stage)
	{
		Table to = stagedTable;
		Table from = unstagedTable;
		if (!stage)
		{
			from = stagedTable;
			to = unstagedTable;
		}
		to.setRedraw(false);
		for (ChangedFile changedFile : files)
		{
			createTableItem(to, changedFile, true); // add it to our new table
		}
		packTable(to);
		to.setRedraw(true);
		// to.redraw();
		removeDraggedFilesFromSource(from, files);
		workaroundEmptyTableDropEffectBug(from);
		validate();
	}

	/**
	 * Update the diff area.
	 * 
	 * @param staged
	 * @param file
	 * @see #updateDiff(ChangedFile, String)
	 */
	private void updateDiff(final boolean staged, ChangedFile file)
	{
		if (file == null)
		{
			return;
		}
		boolean isBrowser = (diffArea instanceof Browser);

		if (isBrowser && gitRepository.index().hasBinaryAttributes(file)
				&& !file.getStatus().equals(ChangedFile.Status.DELETED))
		{
			// Special code to draw the image if the binary file is an image
			String fileExtension = file.getRelativePath().getFileExtension();
			if (fileExtension != null)
			{
				String[] imageExtensions = new String[] { "png", "gif", "jpeg", "jpg", "ico" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				for (String extension : imageExtensions)
				{
					if (fileExtension.equalsIgnoreCase(extension))
					{
						IPath fullPath = gitRepository.workingDirectory().append(file.getRelativePath());
						updateDiff(file, "<img src=\"" + fullPath.toOSString() + "\" />"); //$NON-NLS-1$ //$NON-NLS-2$
						return;
					}
				}
			}
		}
		// Don't recalc if it's the same file as we are already showing
		if (fLastDiffFile != null && file.equals(fLastDiffFile))
		{
			return;
		}

		String diff = gitRepository.index().diffForFile(file, staged, 3);
		if (isBrowser)
		{
			try
			{
				diff = DiffFormatter.toHTML(file.getRelativePath().toPortableString(), diff);
			}
			catch (Throwable t)
			{
				IdeLog.logWarning(GitUIPlugin.getDefault(), "Failed to turn diff into HTML", t, IDebugScopes.DEBUG); //$NON-NLS-1$
			}
		}
		updateDiff(file, diff);
	}

	/**
	 * Update the diff area.
	 * 
	 * @param file
	 * @param diff
	 * @see #updateDiff(boolean, String)
	 */
	private void updateDiff(ChangedFile file, String diff)
	{
		if (diffArea != null && !diffArea.isDisposed())
		{
			setDiffText(diff);
			fLastDiffFile = file;
		}
	}

	/**
	 * Creates a table item for a ChangedFile in Git
	 * 
	 * @param table
	 * @param file
	 */
	private void createTableItem(Table table, ChangedFile file, boolean sort)
	{
		TableItem item = null;
		if (sort)
		{
			// insert into sorted table
			TableItem[] items = table.getItems();
			int index = 0;
			for (TableItem existing : items)
			{
				String path = existing.getText(1);
				if (file.getRelativePath().toOSString().compareTo(path) < 0)
				{
					break;
				}
				index++;
			}
			item = new TableItem(table, SWT.NONE, index);
		}
		else
		{
			// Just insert at end
			item = new TableItem(table, SWT.NONE);
		}
		Image image = emptyFileImage;
		// String text = Messages.CommitDialog_modified;
		if (file.getStatus() == ChangedFile.Status.DELETED)
		{
			image = deletedFileImage;
			// text = Messages.CommitDialog_deleted;
		}
		else if (file.getStatus() == ChangedFile.Status.NEW)
		{
			image = newFileImage;
			// text = Messages.CommitDialog_new;
		}
		// item.setText(0, text);
		item.setImage(0, image);
		item.setText(1, file.getRelativePath().toOSString());
		item.setData(CHANGED_FILE_DATA_KEY, file);
	}

	private void packTable(Table table)
	{
		// pack first column (image)
		table.getColumn(0).pack();
		// Make the second column take all the available width!
		int totalWidth = table.getClientArea().width;
		if (totalWidth > 0)
		{
			totalWidth -= table.getColumn(0).getWidth();
			table.getColumn(1).setWidth(totalWidth);
		}
	}

	private void validate()
	{
		if (commitMessage.getText().length() < 1)
		{
			updateStatus(new Status(IStatus.ERROR, GitUIPlugin.getPluginId(), Messages.CommitDialog_EnterMessage_Error));
			return;
		}
		if (stagedTable.getItemCount() == 0)
		{
			updateStatus(new Status(IStatus.ERROR, GitUIPlugin.getPluginId(),
					Messages.CommitDialog_StageFilesFirst_Error));
			return;
		}
		if (gitRepository.hasUnresolvedMergeConflicts())
		{
			updateStatus(new Status(IStatus.ERROR, GitUIPlugin.getPluginId(), Messages.CommitDialog_CannotMerge_Error));
			return;
		}
		fMessage = commitMessage.getText();

		if (!(diffArea instanceof Browser))
		{
			updateStatus(new Status(IStatus.WARNING, GitUIPlugin.getPluginId(),
					Messages.CommitDialog_BrowserWidgetFailedMsg));
		}
		else
		{
			updateStatus(Status.OK_STATUS);
		}
	}

	// TODO Change way dialog is composed to push buttons into commit message area like GitX?

	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton)
	{
		if (id == IDialogConstants.OK_ID)
		{
			label = Messages.CommitDialog_CommitButton_Label;
		}
		else if (id == IDialogConstants.CANCEL_ID)
		{
			label = Messages.CommitDialog_CloseButton_Label;
		}
		return super.createButton(parent, id, label, defaultButton);
	}

	@Override
	protected void okPressed()
	{
		// disable the buttons until commit is done
		getButton(IDialogConstants.CANCEL_ID).setEnabled(false);
		getButton(IDialogConstants.OK_ID).setEnabled(false);

		// try the commit
		IStatus status = gitRepository.index().commit(getCommitMessage());
		if (status.isOK())
		{
			// commit worked, wipe commit message
			commitMessage.setText(StringUtil.EMPTY);
		}

		// Force a reload of the staged/unstaged file listing
		List<ChangedFile> changedFiles = gitRepository.index().changedFiles();
		// If there are no more staged/unstaged files and the commit went OK, close the dialog.
		if (CollectionsUtil.isEmpty(changedFiles) && status.isOK())
		{
			super.okPressed();
			return;
		}

		Collections.sort(changedFiles);
		stagedTable.removeAll();
		unstagedTable.removeAll();
		for (ChangedFile file : changedFiles)
		{
			if (file.hasStagedChanges())
			{
				createTableItem(stagedTable, file, false);
			}
			if (file.hasUnstagedChanges())
			{
				createTableItem(unstagedTable, file, false);
			}
		}
		// Update our status
		updateStatus(status);

		// Re-enable buttons
		getButton(IDialogConstants.CANCEL_ID).setEnabled(true);
		getButton(IDialogConstants.OK_ID).setEnabled(true);
	}

	public String getCommitMessage()
	{
		return fMessage;
	}

	/**
	 * Find the table items we just dragged over and remove them from the source table
	 * 
	 * @param sourceTable
	 * @param draggedFiles
	 */
	private void removeDraggedFilesFromSource(Table sourceTable, Collection<ChangedFile> draggedFiles)
	{
		if (draggedFiles == null || draggedFiles.isEmpty())
		{
			return;
		}
		TableItem[] items = sourceTable.getItems();
		if (draggedFiles.size() == items.length)
		{
			// shortcut for moving all
			sourceTable.setRedraw(false);
			sourceTable.removeAll();
			packTable(sourceTable);
			sourceTable.setRedraw(true);
			return;
		}

		List<Integer> toRemove = new ArrayList<Integer>();
		for (int i = 0; i < items.length; i++)
		{
			TableItem item = items[i];
			if (draggedFiles.contains(getChangedFile(item)))
			{
				toRemove.add(i);
			}
		}
		int[] primitive = new int[toRemove.size()];
		int x = 0;
		for (Integer object : toRemove)
		{
			primitive[x++] = object.intValue();
		}
		sourceTable.setRedraw(false);
		sourceTable.remove(primitive);
		packTable(sourceTable);
		sourceTable.setRedraw(true);
		sourceTable.redraw();
	}

	private ChangedFile getChangedFile(TableItem item)
	{
		return (ChangedFile) item.getData(CHANGED_FILE_DATA_KEY);
	}

	/**
	 * HACK Workaround bug where drag and drop breaks horribly on a table when it's empty
	 * 
	 * @param sourceDragTable
	 */
	private void workaroundEmptyTableDropEffectBug(Table sourceDragTable)
	{
		DropTarget dtarget = (DropTarget) sourceDragTable.getData(DND.DROP_TARGET_KEY);
		if (dtarget == null)
			return;
		if (sourceDragTable.getItemCount() == 0)
		{
			dtarget.setDropTargetEffect(null);
		}
		else if (dtarget.getDropTargetEffect() == null)
		{
			dtarget.setDropTargetEffect(new TableDropTargetEffect(sourceDragTable));
		}
	}

	@Override
	protected boolean isResizable()
	{
		return true;
	}

	/*
	 * Stage or Un-Stage the selected items (files).
	 * @param staged
	 * @param selected
	 */
	private void moveItems(final boolean staged, TableItem... selected)
	{
		Set<ChangedFile> selectedFiles = new HashSet<ChangedFile>(selected.length);
		for (TableItem item : selected)
		{
			ChangedFile file = getChangedFile(item);
			if (file == null)
			{
				continue;
			}
			selectedFiles.add(file);
		}
		if (selectedFiles.isEmpty())
		{
			return;
		}

		// Actually stage or unstage the files
		if (staged)
		{
			unstageFiles(selectedFiles);
		}
		else
		{
			stageFiles(selectedFiles);
		}
	}

	/*
	 * This class will create buttons next to the tables in order to allow staging and un-staging operations.
	 */
	private class StagingButtons implements SelectionListener
	{
		private String doAllLabel;
		private String doAllTooltip;
		private String doSelectionLabel;
		private String doSelectionTooltip;
		private Table table;
		private boolean staged;
		private Button doSelectionBt;
		private Button doAllBt;

		/**
		 * Constructs a new StagingButtons instance.
		 * 
		 * @param parent
		 * @param doAllLabel
		 * @param doAllTooltip
		 * @param doSelectionLabel
		 * @param doSelectionTooltip
		 */
		protected StagingButtons(Composite parent, String doAllLabel, String doAllTooltip, String doSelectionLabel,
				String doSelectionTooltip)
		{
			this.doAllLabel = doAllLabel;
			this.doAllTooltip = doAllTooltip;
			this.doSelectionLabel = doSelectionLabel;
			this.doSelectionTooltip = doSelectionTooltip;
			createComponent(parent);
		}

		/**
		 * Set the table (staged or un-staged) that this component controls.
		 * 
		 * @param table
		 * @param staged
		 */
		protected void setTable(Table table, boolean staged)
		{
			this.table = table;
			this.staged = staged;
			table.addSelectionListener(this);
			updateSelectionButton();
		}

		protected void createComponent(Composite parent)
		{
			Composite comp = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout(1, true);
			layout.horizontalSpacing = 0;
			layout.marginWidth = 1;
			comp.setLayout(layout);

			doAllBt = new Button(comp, SWT.PUSH | SWT.FLAT);
			doAllBt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			doAllBt.setText(doAllLabel);
			doAllBt.setToolTipText(doAllTooltip);
			doAllBt.addSelectionListener(this);
			doSelectionBt = new Button(comp, SWT.PUSH | SWT.FLAT);
			doSelectionBt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			doSelectionBt.setText(doSelectionLabel);
			doSelectionBt.setToolTipText(doSelectionTooltip);
			doSelectionBt.addSelectionListener(this);

			// minimize the width of this component
			Point doAllSize = doAllBt.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			Point doSelectionSize = doSelectionBt.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			Point bigger = null;
			if (doAllSize.x > doSelectionSize.x)
			{
				bigger = doAllSize;
			}
			else
			{
				bigger = doSelectionSize;
			}
			GridData data = new GridData(SWT.CENTER, SWT.CENTER, false, false);
			data.widthHint = bigger.x + 10;
			comp.setLayoutData(data);
		}

		public void widgetDefaultSelected(SelectionEvent e)
		{
		}

		public void widgetSelected(SelectionEvent e)
		{
			Object source = e.getSource();
			if (source instanceof Table)
			{
				updateSelectionButton();
			}
			else if (source == doAllBt)
			{
				if (table != null)
				{
					moveItems(staged, table.getItems());
					updateSelectionDiff(-1);
					updateSelectionButton();
				}
			}
			else if (source == doSelectionBt)
			{
				if (table != null)
				{
					int selectionIndex = table.getSelectionIndex();
					TableItem[] selection = table.getSelection();
					moveItems(staged, selection);
					updateSelectionDiff(selectionIndex);
					updateSelectionButton();
				}
			}
		}

		// Enable the doSelectionBt when the table has a selection
		private void updateSelectionButton()
		{
			doSelectionBt.setEnabled(table.getSelectionCount() > 0);
		}

		private void updateSelectionDiff(int previousSelectionIndex)
		{
			// Select the next file in line (if exists)
			if (table.getItemCount() > 0 && previousSelectionIndex > -1)
			{
				if (table.getItemCount() > previousSelectionIndex)
				{
					table.select(previousSelectionIndex);
				}
				else
				{
					table.select(table.getItemCount() - 1);
				}
				ChangedFile file = getChangedFile(table.getSelection()[0]);
				updateDiff(staged, file);
			}
			else
			{
				updateDiff(null, Messages.CommitDialog_NoFileSelected);
			}
		}
	}
}
