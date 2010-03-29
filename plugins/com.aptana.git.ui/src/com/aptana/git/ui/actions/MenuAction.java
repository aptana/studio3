package com.aptana.git.ui.actions;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.IGitRepositoryManager;

/**
 * An action that actually generates a dynamic sub-menu.
 * 
 * @author cwilliams
 */
public abstract class MenuAction implements IObjectActionDelegate, IMenuCreator
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

	protected IResource getSelectedResource()
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
					fillMenu(fCreatedMenu);
					fFillMenu = false;
				}
			}
		});
	}

	/**
	 * Fills the fly-out menu
	 */
	protected abstract void fillMenu(Menu menu);

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
		// generally do nothing. If can be activated by keybinding, subclasses should implement
	}

	protected IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}
}
