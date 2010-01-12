package com.aptana.git.ui.internal.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchActionConstants;

import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.GitUIPlugin;
import com.aptana.git.ui.internal.DiffFormatter;

public class CommitDialog extends StatusDialog
{
	private GitRepository gitRepository;
	private Text commitMessage;
	private String fMessage;
	private Table unstagedTable;
	private Table stagedTable;

	private Image newFileImage;
	private Image deletedFileImage;
	private Image emptyFileImage;
	private Browser diffArea;

	public CommitDialog(Shell parentShell, GitRepository gitRepository)
	{
		super(parentShell);
		Assert.isNotNull(gitRepository, "Must have a non-null git repository!"); //$NON-NLS-1$
		this.gitRepository = gitRepository;
		newFileImage = GitUIPlugin.getImage("icons/obj16/new_file.png"); //$NON-NLS-1$
		deletedFileImage = GitUIPlugin.getImage("icons/obj16/deleted_file.png"); //$NON-NLS-1$
		emptyFileImage = GitUIPlugin.getImage("icons/obj16/empty_file.png"); //$NON-NLS-1$
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite container = (Composite) super.createDialogArea(parent);
		parent.getShell().setText(Messages.CommitDialog_3);

		container.setLayout(new GridLayout(1, true));

		createDiffArea(container);

		SashForm sashForm = new SashForm(container, SWT.HORIZONTAL);
		sashForm.setLayout(new FillLayout());

		createUnstagedFileArea(sashForm);
		createCommitMessageArea(sashForm);
		createStagedFileArea(sashForm);

		sashForm.setSashWidth(10);
		sashForm.setWeights(new int[] { 25, 50, 25 });

		validate();

		return container;
	}

	private void createDiffArea(Composite container)
	{
		diffArea = new Browser(container, SWT.BORDER);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 300;
		diffArea.setLayoutData(data);
		diffArea.setText(Messages.CommitDialog_4);
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
		composite.setLayout(new GridLayout(1, true));
		Label label = new Label(composite, SWT.NONE);
		String text = Messages.CommitDialog_5;
		if (!staged)
			text = Messages.CommitDialog_6;
		label.setText(text);
		Table table = createTable(composite, staged);
		if (staged)
			stagedTable = table;
		else
			unstagedTable = table;
	}

	private void createCommitMessageArea(SashForm sashForm)
	{
		Composite msgComp = new Composite(sashForm, SWT.NONE);
		msgComp.setLayout(new GridLayout(1, true));
		Label messageLabel = new Label(msgComp, SWT.NONE);
		messageLabel.setText(Messages.CommitDialog_MessageLabel);
		commitMessage = new Text(msgComp, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
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
	}

	private Table createTable(Composite composite, final boolean staged)
	{
		// TODO Make list entries be able to be truncated when too long to fit, like GitX does
		// TODO Sort list entries
		Table table = new Table(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 200;
		data.widthHint = 250;
		table.setLayoutData(data);
		String[] titles = { " ", Messages.CommitDialog_PathColumnLabel }; //$NON-NLS-1$
		for (int i = 0; i < titles.length; i++)
		{
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(titles[i]);
		}
		for (ChangedFile file : gitRepository.index().changedFiles())
		{
			boolean match = false;
			if (staged && file.hasStagedChanges())
				match = true;
			else if (!staged && file.hasUnstagedChanges())
				match = true;

			if (match)
			{
				createTableItem(table, file);
			}
		}
		packTable(table);

		// Drag and Drop
		// FIXME If user drags and drops while we're still crunching on last drag/drop then we end up hanging
		// Seems to be related to manipulating the table here before we receive the index changed callback
		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };

		// Drag Source
		DragSource source = new DragSource(table, DND.DROP_MOVE);
		source.setTransfer(types);
		source.addDragListener(new DragSourceAdapter()
		{
			public void dragSetData(DragSourceEvent event)
			{
				// Get the selected items in the drag source
				DragSource ds = (DragSource) event.widget;
				Table table = (Table) ds.getControl();
				TableItem[] selection = table.getSelection();
				// Create a comma separated string of the paths of the changed files we're dragging
				StringBuffer buff = new StringBuffer();
				for (int i = 0, n = selection.length; i < n; i++)
				{
					buff.append(selection[i].getText(1)).append(","); //$NON-NLS-1$
				}
				if (buff.length() > 0)
					buff.deleteCharAt(buff.length() - 1);
				event.data = buff.toString();
			}
		});

		// Create the drop target
		DropTarget target = new DropTarget(table, DND.DROP_MOVE);
		target.setTransfer(types);
		if (table.getItemCount() == 0)
			target.setDropTargetEffect(null);
		target.addDropListener(new DropTargetAdapter()
		{

			public void dragEnter(DropTargetEvent event)
			{
				// Allow dropping text only
				for (int i = 0, n = event.dataTypes.length; i < n; i++)
				{
					if (TextTransfer.getInstance().isSupportedType(event.dataTypes[i]))
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

			public void drop(DropTargetEvent event)
			{
				if (!TextTransfer.getInstance().isSupportedType(event.currentDataType))
					return;
				// Get the dropped data
				String data = (String) event.data;
				// Translate the comma delimited paths back into the matching ChangedFile objects
				Map<String, ChangedFile> draggedFiles = new HashMap<String, ChangedFile>();
				StringTokenizer tokenizer = new StringTokenizer(data, ","); //$NON-NLS-1$
				while (tokenizer.hasMoreTokens())
				{
					String path = tokenizer.nextToken();
					ChangedFile changedFile = findChangedFile(path);
					draggedFiles.put(path, changedFile);
				}

				// Actually stage or unstage the files
				if (staged)
				{
					stageFiles(draggedFiles);
				}
				else
				{
					unstageFiles(draggedFiles);
				}
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
				String path = ((TableItem) e.item).getText(1);
				ChangedFile file = findChangedFile(path);
				if (file == null)
					return;
				if (gitRepository.index().hasBinaryAttributes(file)
						&& !file.getStatus().equals(ChangedFile.Status.DELETED))
				{
					// Special code to draw the image if the binary file is an image
					String[] imageExtensions = new String[] { ".png", ".gif", ".jpeg", ".jpg", ".ico" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					for (String extension : imageExtensions)
					{
						if (file.getPath().endsWith(extension))
						{
							String fullPath = gitRepository.workingDirectory() + File.separator + file.getPath();
							updateDiff("<img src=\"" + fullPath + "\" />"); //$NON-NLS-1$ //$NON-NLS-2$
							return;
						}
					}
				}
				String diff = gitRepository.index().diffForFile(file, staged, 3);
				try
				{
					diff = DiffFormatter.toHTML(diff);
				}
				catch (Throwable t)
				{
					GitUIPlugin.logError("Failed to turn diff into HTML", t); //$NON-NLS-1$
				}
				updateDiff(diff);
			}
		});
		// Allow double-clicking to toggle staged/unstaged
		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDoubleClick(MouseEvent e)
			{
				if (e.getSource() == null)
					return;
				Table table = (Table) e.getSource();
				TableItem[] selected = table.getSelection();
				Map<String, ChangedFile> selectedFiles = new HashMap<String, ChangedFile>();
				for (TableItem item : selected)
				{
					String path = item.getText(1);
					ChangedFile file = findChangedFile(path);
					if (file == null)
						continue;
					selectedFiles.put(path, file);
				}
				if (selectedFiles.isEmpty())
					return;

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
					final List<String> filePaths = new ArrayList<String>();
					for (TableItem item : selected)
					{
						String filePath = item.getText(1);

						String workingDirectory = gitRepository.workingDirectory();

						IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(
								new Path(workingDirectory).append(filePath));
						if (file != null)
						{
							files.add(file);
							filePaths.add(filePath);
						}
					}
					RevertAction revertAction = new RevertAction()
					{
						// need to remove the file(s) from staged table once action runs
						@Override
						protected void doOperation(GitRepository repo, List<ChangedFile> changedFiles)
						{
							super.doOperation(repo, changedFiles);
							Display.getCurrent().asyncExec(new Runnable()
							{

								@Override
								public void run()
								{
									removeDraggedFilesFromSource(unstagedTable, filePaths);
								}
							});
						}
					};
					revertAction.selectionChanged(null, new StructuredSelection(files));
					manager.add(revertAction);
					// Other plug-ins can contribute there actions here
					manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
				}
			});
			Menu menu = menuMgr.createContextMenu(table);
			table.setMenu(menu);
		}

		return table;
	}

	protected synchronized void unstageFiles(final Map<String, ChangedFile> files)
	{
		toggleStageStatus(files, false);
		gitRepository.index().unstageFiles(files.values());
	}

	protected synchronized void stageFiles(final Map<String, ChangedFile> files)
	{
		toggleStageStatus(files, true);
		gitRepository.index().stageFiles(files.values());
	}

	private void toggleStageStatus(Map<String, ChangedFile> files, boolean stage)
	{
		Table to = stagedTable;
		Table from = unstagedTable;
		if (!stage)
		{
			from = stagedTable;
			to = unstagedTable;
		}
		to.setRedraw(false);
		for (ChangedFile changedFile : files.values())
		{
			createTableItem(to, changedFile); // add it to our new table
		}
		packTable(to);
		to.setRedraw(true);
		to.redraw();
		removeDraggedFilesFromSource(from, files.keySet());
		workaroundEmptyTableDropEffectBug(from);
		validate();
	}

	protected void updateDiff(String diff)
	{
		if (diffArea != null && !diffArea.isDisposed())
			diffArea.setText(diff);
	}

	protected ChangedFile findChangedFile(String path)
	{
		List<ChangedFile> changedFiles = gitRepository.index().changedFiles();
		for (ChangedFile changedFile : changedFiles)
		{
			if (changedFile.getPath().equals(path))
			{
				return changedFile;
			}
		}
		return null;
	}

	/**
	 * Creates a table item for a ChangedFile in Git
	 * 
	 * @param table
	 * @param file
	 */
	protected void createTableItem(Table table, ChangedFile file)
	{
		TableItem item = new TableItem(table, SWT.NONE);
		Image image = emptyFileImage;
		String text = Messages.CommitDialog_modified;
		if (file.getStatus() == ChangedFile.Status.DELETED)
		{
			image = deletedFileImage;
			text = Messages.CommitDialog_deleted;
		}
		else if (file.getStatus() == ChangedFile.Status.NEW)
		{
			image = newFileImage;
			text = Messages.CommitDialog_new;
		}
		item.setText(0, text);
		item.setImage(0, image);
		item.setText(1, file.getPath());
	}

	private void packTable(Table table)
	{
		for (int i = 0; i < table.getColumnCount(); i++)
		{
			table.getColumn(i).pack();
		}
	}

	protected void validate()
	{
		if (commitMessage.getText().length() < 3)
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
		updateStatus(Status.OK_STATUS);
	}

	// TODO Change way dialog is composed to push buttons into commit message area like GitX?

	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton)
	{
		if (id == IDialogConstants.OK_ID)
			label = Messages.CommitDialog_CommitButton_Label;
		return super.createButton(parent, id, label, defaultButton);
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
	protected void removeDraggedFilesFromSource(Table sourceTable, Collection<String> draggedFiles)
	{
		List<Integer> toRemove = new ArrayList<Integer>();
		TableItem[] items = sourceTable.getItems();
		for (int i = 0; i < items.length; i++)
		{
			TableItem item = items[i];
			if (draggedFiles.contains(item.getText(1)))
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
}
