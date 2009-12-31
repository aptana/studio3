package com.aptana.scripting.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.aptana.scope.ScopeSelector;

public class MenuElement extends AbstractBundleElement
{
	private static final String SEPARATOR_TEXT = "-"; //$NON-NLS-1$
	
	private MenuElement _parent;
	private List<MenuElement> _children;
	private String _commandName;

	/**
	 * Snippet
	 * 
	 * @param path
	 */
	public MenuElement(String path)
	{
		super(path);
	}

	/**
	 * addMenu
	 * 
	 * @param menu
	 */
	public void addMenu(MenuElement menu)
	{
		if (menu != null)
		{
			if (this._children == null)
			{
				this._children = new ArrayList<MenuElement>();
			}
			
			// set parent
			menu._parent = this;
			
			// add to our list
			this._children.add(menu);
		}
	}

	/**
	 * cloneByScope
	 * 
	 * @param scope
	 * @return
	 */
	public MenuElement cloneByScope(String scope)
	{
		MenuElement result = null;
		
		// find all menus in the specified scope
		List<MenuElement> matches = new ArrayList<MenuElement>();
		
		for (MenuElement menu : this.getLeafMenus())
		{
			if (menu.matches(scope))
			{
				matches.add(menu);
			}
		}
		
		// TODO: collect into one tree
		
		
		return result;
	}
	
	/**
	 * getChildren
	 * 
	 * @return
	 */
	public MenuElement[] getChildren()
	{
		MenuElement[] result = BundleManager.NO_MENUS;
		
		if (this._children != null && this._children.size() > 0)
		{
			result = this._children.toArray(new MenuElement[this._children.size()]);
		}
		
		return result;
	}
	
	/**
	 * getCommand
	 * 
	 * @return
	 */
	public CommandElement getCommand()
	{
		BundleElement owningBundle = this.getOwningBundle();
		CommandElement result = null;
		
		if (this.isLeafMenu() && owningBundle != null)
		{
			result = owningBundle.getCommandByName(this._commandName);
		}
		
		return result;
	}
	
	/**
	 * getCommandName
	 * 
	 * @return
	 */
	public String getCommandName()
	{
		return this._commandName;
	}
	
	/**
	 * getElementName
	 */
	protected String getElementName()
	{
		return "menu"; //$NON-NLS-1$
	}
	
	/**
	 * getLeafMenus
	 * 
	 * @return
	 */
	protected MenuElement[] getLeafMenus()
	{
		Stack<MenuElement> stack = new Stack<MenuElement>();
		List<MenuElement> result = new ArrayList<MenuElement>();
		
		// prime stack
		stack.push(this);
		
		while (stack.size() > 0)
		{
			MenuElement menu = stack.pop();
			
			if (menu.isHierarchicalMenu())
			{
				stack.addAll(menu._children);
			}
			else if (menu.isLeafMenu())
			{
				result.add(menu);
			}
			
			// NOTE: we ignore separators
		}
		
		return result.toArray(new MenuElement[result.size()]);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.AbstractBundleElement#getOwningBundle()
	 */
	public BundleElement getOwningBundle()
	{
		MenuElement currentMenu = this;
		BundleElement result = null;
		
		while (currentMenu != null)
		{
			BundleElement bundle = currentMenu.owningBundle;
			
			if (bundle != null)
			{
				result = bundle;
				break;
			}
			else
			{
				currentMenu = currentMenu._parent;
			}
		}
		
		return result;
	}

	/**
	 * getParent
	 * 
	 * @return
	 */
	public MenuElement getParent()
	{
		return this._parent;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.AbstractModel#getScopeSelector()
	 */
	public ScopeSelector getScopeSelector()
	{
		MenuElement currentMenu = this;
		ScopeSelector result = null;
		
		while (currentMenu != null)
		{
			String scope = currentMenu.getScope();
			
			if (scope != null && scope.length() > 0)
			{
				result = new ScopeSelector(scope);
				break;
			}
			else
			{
				currentMenu = currentMenu.getParent();
			}
		}
		
		return result;
	}

	/**
	 * hasChildren
	 * 
	 * @return
	 */
	public boolean hasChildren()
	{
		return this._children != null && this._children.size() > 0;
	}
	
	/**
	 * isHierarchical
	 * 
	 * @return
	 */
	public boolean isHierarchicalMenu()
	{
		return this.isSeparator() == false && this.hasChildren();
	}
	
	/**
	 * isLeafMenu
	 * 
	 * @return
	 */
	public boolean isLeafMenu()
	{
		return this.isSeparator() == false && this.hasChildren() == false;
	}
	
	/**
	 * isSeparator
	 * 
	 * @return
	 */
	public boolean isSeparator()
	{
		String displayName = this.getDisplayName();
		
		return displayName != null && displayName.startsWith(SEPARATOR_TEXT);
	}
	
	/**
	 * printBody
	 */
	protected void printBody(SourcePrinter printer)
	{
		printer.printWithIndent("path: ").println(this.getPath()); //$NON-NLS-1$
		printer.printWithIndent("scope: ").println(this.getScopeSelector().toString()); //$NON-NLS-1$
		printer.printWithIndent("command: ").println(this.getCommandName()); //$NON-NLS-1$
		
		if (this.hasChildren())
		{
			for (MenuElement menu : this._children)
			{
				menu.toSource(printer);
			}
		}
	}
	
	/**
	 * removeChildren
	 */
	public void removeChildren()
	{
		for (MenuElement child : this.getChildren())
		{
			this.removeMenu(child);
		}
	}
	
	/**
	 * removeMenu
	 * 
	 * @param menu
	 */
	public void removeMenu(MenuElement menu)
	{
		if (this._children != null && this._children.remove(menu))
		{
			AbstractElement.unregisterElement(menu);
			
			menu.removeChildren();
		}
	}
	
	/**
	 * setCommandName
	 * 
	 * @param name
	 */
	public void setCommandName(String name)
	{
		this._commandName = name;
	}
}
