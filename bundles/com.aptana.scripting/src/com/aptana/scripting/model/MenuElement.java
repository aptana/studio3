/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.SourcePrinter;
import com.aptana.core.util.StringUtil;
import com.aptana.scope.ScopeSelector;

public class MenuElement extends AbstractBundleElement
{
	private static final String SEPARATOR_TEXT = "-"; //$NON-NLS-1$

	private MenuElement _parent;
	private List<MenuElement> _children;
	private String _commandName;

	private Object childrenLock = new Object();

	// FIXME this is needed for YAML serializing because I can't figure out how to get it to use my special representer
	// when loading a collection of them...
	public MenuElement()
	{
		this(null);
	}

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
			synchronized (childrenLock)
			{
				if (this._children == null)
				{
					this._children = new ArrayList<MenuElement>(1);
				}

				// set parent
				menu._parent = this;

				// add to our list
				this._children.add(menu);
			}
		}
	}

	/**
	 * getChildren
	 * 
	 * @return
	 */
	public synchronized List<MenuElement> getChildren()
	{
		List<MenuElement> result;

		synchronized (childrenLock)
		{
			if (!CollectionsUtil.isEmpty(_children))
			{
				result = new ArrayList<MenuElement>(this._children);
			}
			else
			{
				result = Collections.emptyList();
			}
		}

		return result;
	}

	public synchronized void setChildren(List<MenuElement> children)
	{
		if (children != null)
		{
			synchronized (childrenLock)
			{
				this._children = new ArrayList<MenuElement>(children);

				for (MenuElement child : children)
				{
					child._parent = this;
				}
			}
		}
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
			BundleEntry be = BundleManager.getInstance().getBundleEntry(owningBundle.getDisplayName());

			if (be != null)
			{
				for (CommandElement cmd : be.getCommands())
				{
					if (cmd.getDisplayName().equals(this._commandName))
					{
						result = cmd;
						break;
					}
				}
			}
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
		synchronized (childrenLock)
		{
			return !CollectionsUtil.isEmpty(_children);
		}
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

	@Override
	public void setDisplayName(String displayName)
	{
		if (StringUtil.startsWith(displayName, SEPARATOR_TEXT))
		{
			displayName = SEPARATOR_TEXT;
		}
		super.setDisplayName(displayName);
	}

	/**
	 * isSeparator
	 * 
	 * @return
	 */
	public boolean isSeparator()
	{
		String displayName = this.getDisplayName();

		return StringUtil.startsWith(displayName, SEPARATOR_TEXT);
	}

	/**
	 * printBody
	 */
	protected void printBody(SourcePrinter printer, boolean includeBlocks)
	{
		printer.printWithIndent("path: ").println(this.getPath()); //$NON-NLS-1$

		if (this.getScopeSelector() != null)
		{
			printer.printWithIndent("scope: ").println(this.getScopeSelector().toString()); //$NON-NLS-1$
		}

		printer.printWithIndent("command: ").println(this.getCommandName()); //$NON-NLS-1$

		synchronized (childrenLock)
		{
			if (this.hasChildren())
			{
				for (MenuElement menu : this._children)
				{
					menu.toSource(printer, includeBlocks);
				}
			}
		}
	}

	/**
	 * removeChildren
	 */
	public void removeChildren()
	{
		synchronized (childrenLock)
		{
			for (MenuElement child : this.getChildren())
			{
				this.removeMenu(child);
			}
		}
	}

	/**
	 * removeMenu
	 * 
	 * @param menu
	 */
	public void removeMenu(MenuElement menu)
	{
		synchronized (childrenLock)
		{
			if (this._children != null && this._children.remove(menu))
			{
				AbstractElement.unregisterElement(menu);

				menu.removeChildren();
			}
		}
	}

	/**
	 * setCommandName
	 * 
	 * @param name
	 */
	public void setCommandName(String name)
	{
		if (name != null && name.startsWith(SEPARATOR_TEXT))
		{
			name = SEPARATOR_TEXT;
		}
		this._commandName = name;
	}
}
