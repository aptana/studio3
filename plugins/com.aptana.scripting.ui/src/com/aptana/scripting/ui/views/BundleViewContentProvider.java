package com.aptana.scripting.ui.views;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.BundleEntry;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.MenuElement;

class BundleViewContentProvider implements ITreeContentProvider
{
	private static final Object[] NO_ELEMENTS = new Object[0];
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement)
	{
		Object[] children = NO_ELEMENTS;
		
		if (parentElement instanceof String)
		{
			String name = (String) parentElement;
			BundleEntry entry = BundleManager.getInstance().getBundleEntry(name);
			
			children = entry.getBundles();
		}
		else if (parentElement instanceof BundleElement)
		{
			BundleElement bundle = (BundleElement) parentElement;
			CommandsNode commands = new CommandsNode(bundle);
			SnippetsNode snippets = new SnippetsNode(bundle);
			MenusNode menus = new MenusNode(bundle);
			List<Object> items = new LinkedList<Object>();
			
			if (commands.hasChildren())
			{
				items.add(commands);
			}
			if (snippets.hasChildren())
			{
				items.add(snippets);
			}
			if (menus.hasChildren())
			{
				items.add(menus);
			}
			
			children = items.toArray(new Object[items.size()]);
		}
		else if (parentElement instanceof CollectionNode)
		{
			children = ((CollectionNode) parentElement).getChildren();
		}
		else if (parentElement instanceof MenuElement)
		{
			children = ((MenuElement) parentElement).getChildren();
		}
		
		return children;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element)
	{
		boolean result = false;
		
		if (element instanceof String)
		{
			String name = (String) element;
			BundleEntry entry = BundleManager.getInstance().getBundleEntry(name);
			
			result = (entry != null && entry.size() > 0);
		}
		else if (element instanceof BundleElement)
		{
			BundleElement bundle = (BundleElement) element;
			
			result = bundle.hasCommands() || bundle.hasMenus();
		}
		else if (element instanceof CollectionNode)
		{
			result = ((CollectionNode) element).hasChildren();
		}
		else if (element instanceof MenuElement)
		{
			result = ((MenuElement) element).hasChildren();
		}
		
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement)
	{
		Object[] elements = NO_ELEMENTS;
		
		if (inputElement instanceof BundleManager)
		{
			BundleManager manager = (BundleManager) inputElement;
			
			elements = manager.getBundleNames(); 
		}
		
		return elements;
	}

	public void dispose()
	{
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
	}
}
