package com.aptana.git.ui.internal.actions;

import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.internal.dialogs.BranchDialog;

public class SwitchBranchAction implements IObjectActionDelegate, IMenuCreator
{

	private ISelection selection;
	private Menu fCreatedMenu;
	private IAction fDelegateAction;
	private boolean fFillMenu;

	/*
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 * org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
		this.selection = selection;
		// if the selection is an IResource, save it and enable our action
		if (selection instanceof IStructuredSelection)
		{
			fFillMenu = true;
			if (fDelegateAction != action)
			{
				fDelegateAction = action;
				fDelegateAction.setMenuCreator(this);
			}
			// enable our menu
			action.setEnabled(true);
			return;
		}
		action.setEnabled(false);
	}

	private IResource getSelectedResource()
	{
		if (this.selection == null)
			return null;
		if (!(this.selection instanceof IStructuredSelection))
			return null;

		IStructuredSelection structured = (IStructuredSelection) this.selection;
		Object element = structured.getFirstElement();
		if (element == null)
			return null;

		if (element instanceof IResource)
			return (IResource) element;

		if (element instanceof IAdaptable)
		{
			IAdaptable adapt = (IAdaptable) element;
			return (IResource) adapt.getAdapter(IResource.class);
		}
		return null;
	}

	public Menu getMenu(Control parent)
	{
		return null;
	}

	public Menu getMenu(Menu parent)
	{
		if (fCreatedMenu != null)
		{
			fCreatedMenu.dispose();
		}
		fCreatedMenu = new Menu(parent);
		initMenu();
		return fCreatedMenu;
	}

	/**
	 * Creates the menu for the action
	 */
	private void initMenu()
	{
		// Add listener to re-populate the menu each time
		// it is shown to reflect changes in selection or active perspective
		fCreatedMenu.addMenuListener(new MenuAdapter()
		{
			public void menuShown(MenuEvent e)
			{
				if (fFillMenu)
				{
					Menu m = (Menu) e.widget;
					MenuItem[] items = m.getItems();
					for (int i = 0; i < items.length; i++)
					{
						items[i].dispose();
					}
					fillMenu();
					fFillMenu = false;
				}
			}
		});
	}

	/**
	 * Fills the fly-out menu
	 */
	private void fillMenu()
	{
		IResource resource = getSelectedResource();
		if (resource == null)
			return;

		final GitRepository repo = GitRepository.getAttached(resource.getProject());
		if (repo == null)
			return;

		Set<String> localBranches = repo.localBranches();
		int index = 0;
		for (final String branchName : localBranches)
		{
			MenuItem menuItem = new MenuItem(fCreatedMenu, SWT.PUSH, index++);
			menuItem.setText(branchName);
			menuItem.setEnabled(!branchName.equals(repo.currentBranch()));
			menuItem.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					// what to do when menu is subsequently selected.
					repo.switchBranch(branchName);
				}
			});
		}
	}

	/**
	 * @see IMenuCreator#dispose()
	 */
	public void dispose()
	{
		if (fCreatedMenu != null)
		{
			fCreatedMenu.dispose();
		}
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
		// do nothing
	}

	public void run(IAction action)
	{
		// Called when keybinding is used
		IResource resource = getSelectedResource();
		if (resource == null)
			return;

		final GitRepository repo = GitRepository.getAttached(resource.getProject());
		if (repo == null)
			return;

		BranchDialog dialog = new BranchDialog(Display.getDefault().getActiveShell(), repo);
		if (dialog.open() == Window.OK)
			repo.switchBranch(dialog.getBranch());
	}

}
