package com.aptana.git.ui.internal.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.resource.ImageDescriptor;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

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

	protected CommitDialog(Shell parentShell, GitRepository gitRepository)
	{
		super(parentShell);
		this.gitRepository = gitRepository;
		newFileImage = ImageDescriptor.createFromURL(
				GitUIPlugin.getDefault().getBundle().getEntry("icons/obj16/new_file.png")).createImage(); //$NON-NLS-1$
		deletedFileImage = ImageDescriptor.createFromURL(
				GitUIPlugin.getDefault().getBundle().getEntry("icons/obj16/deleted_file.png")).createImage(); //$NON-NLS-1$
		emptyFileImage = ImageDescriptor.createFromURL(
				GitUIPlugin.getDefault().getBundle().getEntry("icons/obj16/empty_file.png")).createImage(); //$NON-NLS-1$
	}

	@Override
	public boolean close()
	{
		newFileImage.dispose();
		deletedFileImage.dispose();
		emptyFileImage.dispose();
		return super.close();
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
				if (TextTransfer.getInstance().isSupportedType(event.currentDataType))
				{
					// Get the dropped data
					DropTarget target = (DropTarget) event.widget;
					Table table = (Table) target.getControl();
					String data = (String) event.data;
					// Translate the comma delimited paths back into the matching ChangedFile objects
					Map<String, ChangedFile> draggedFiles = new HashMap<String, ChangedFile>();
					StringTokenizer tokenizer = new StringTokenizer(data, ","); //$NON-NLS-1$
					while (tokenizer.hasMoreTokens())
					{
						String path = tokenizer.nextToken();
						ChangedFile changedFile = findChangedFile(path);
						draggedFiles.put(path, changedFile);
						createTableItem(table, changedFile); // add it to our new table
					}
					packTable(table);
					table.redraw();

					// Actually stage or unstage the files
					Table sourceDragTable = null;
					if (staged)
					{
						gitRepository.index().stageFiles(draggedFiles.values());
						sourceDragTable = unstagedTable;
					}
					else
					{
						gitRepository.index().unstageFiles(draggedFiles.values());
						sourceDragTable = stagedTable;
					}
					removeDraggedFilesFromSource(sourceDragTable, draggedFiles);
					workaroundEmptyTableDropEffectBug(sourceDragTable);
				}
			}
		});

		table.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (e.item == null)
					return;
				String path = ((TableItem) e.item).getText(1);
				ChangedFile file = findChangedFile(path);
				if (file == null)
					return;
				String diff = gitRepository.index().diffForFile(file, staged, 3);
				updateDiff(diff);
				super.widgetSelected(e);
			}
		});
		return table;
	}

	protected void updateDiff(String diff)
	{
		diffArea.setText(DiffFormatter.toHTML(diff));
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
		if (gitRepository.hasMerges())
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
	protected void removeDraggedFilesFromSource(Table sourceTable, Map<String, ChangedFile> draggedFiles)
	{
		List<Integer> toRemove = new ArrayList<Integer>();
		TableItem[] items = sourceTable.getItems();
		for (int i = 0; i < items.length; i++)
		{
			TableItem item = items[i];
			if (draggedFiles.keySet().contains(item.getText(1)))
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
		sourceTable.remove(primitive);
		packTable(sourceTable);
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
