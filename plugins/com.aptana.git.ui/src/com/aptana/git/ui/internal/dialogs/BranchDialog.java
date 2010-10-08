/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
