package com.aptana.git.ui.internal.dialogs;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.aptana.git.core.model.GitRepository;

/**
 * Dialog to pick a branch.
 * 
 * @author cwilliams
 */
public class BranchDialog extends Dialog
{

	private GitRepository repository;
	private String branchName;
	private Combo combo;
	private boolean local;
	private boolean remote;

	public BranchDialog(Shell parentShell, GitRepository repository, boolean local, boolean remote)
	{
		super(parentShell);
		this.repository = repository;
		this.local = local;
		this.remote = remote;
	}

	@Override
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		newShell.setText(Messages.BranchDialog_title);
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = (Composite) super.createDialogArea(parent);

		Label label = new Label(composite, SWT.WRAP);
		label.setText(Messages.BranchDialog_msg);

		combo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		Set<String> branchNames = new HashSet<String>();
		if (local && remote)
		{
			branchNames = repository.allBranches();
			branchNames.remove(repository.currentBranch());
		}
		else if (local)
		{
			branchNames = repository.localBranches();
			branchNames.remove(repository.currentBranch());
		}
		else if (remote)
		{
			branchNames.addAll(repository.remoteBranches());
		}
		combo.setItems(branchNames.toArray(new String[branchNames.size()]));
		String first = branchNames.iterator().next();
		branchName = first;
		combo.setText(first);
		combo.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				branchName = combo.getText();
			}
		});
		return composite;
	}

	public String getBranch()
	{
		return branchName;
	}

}
