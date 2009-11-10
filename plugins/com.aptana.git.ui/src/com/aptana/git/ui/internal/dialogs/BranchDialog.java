package com.aptana.git.ui.internal.dialogs;

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
 * Dialog to pick a local branch.
 * 
 * @author cwilliams
 */
public class BranchDialog extends Dialog
{

	private GitRepository repository;
	private String branchName;

	public BranchDialog(Shell parentShell, GitRepository repository)
	{
		super(parentShell);
		this.repository = repository;
	}

	@Override
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		newShell.setText("Choose branch");
	}
	
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = (Composite) super.createDialogArea(parent);
		
		Label label = new Label(composite, SWT.WRAP);
		label.setText("Choose the local branch you'd like to use as your working tree.");
		
		Combo combo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		Set<String> localBranches = repository.localBranches();
		localBranches.remove(repository.currentBranch());
		combo.setItems(localBranches.toArray(new String[localBranches.size()]));
		combo.setText(localBranches.iterator().next());
		combo.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				branchName = e.text;
			}
		});
		return composite;
	}

	public String getBranch()
	{
		return branchName;
	}

}
