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
package com.aptana.scripting.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.aptana.core.util.SourcePrinter;
import com.aptana.scope.ScopeSelector;

public class MenuElement extends AbstractBundleElement
{
	private static final String SEPARATOR_TEXT = "-"; //$NON-NLS-1$

	private MenuElement _parent;
	private List<MenuElement> _children;
	private String _commandName;

	private Object childrenLock = new Object();

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
					this._children = new ArrayList<MenuElement>();
				}

				// set parent
				menu._parent = this;

				// add to our list
				this._children.add(menu);
			}
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
	public synchronized MenuElement[] getChildren()
	{
		MenuElement[] result = BundleManager.NO_MENUS;

		synchronized (childrenLock)
		{
			if (this._children != null && this._children.size() > 0)
			{
				result = this._children.toArray(new MenuElement[this._children.size()]);
			}
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
				synchronized (childrenLock)
				{
					stack.addAll(menu._children);
				}
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
		boolean result = false;

		synchronized (childrenLock)
		{
			if (this._children != null)
			{
				result = this._children.size() > 0;
			}
		}

		return result;
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
					menu.toSource(printer);
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
		this._commandName = name;
	}
}
