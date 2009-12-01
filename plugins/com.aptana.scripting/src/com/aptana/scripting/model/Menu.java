package com.aptana.scripting.model;

import java.util.ArrayList;
import java.util.List;

import org.jruby.anno.JRubyMethod;

import com.aptana.scope.ScopeSelector;

public class Menu extends AbstractModel
{
	private static final String SEPARATOR_TEXT = "-";
	
	private Menu _parent;
	private List<Menu> _children;
	private String _commandName;
	
	/**
	 * Snippet
	 * 
	 * @param path
	 */
	public Menu(String path)
	{
		super(path);
	}

	/**
	 * addMenu
	 * 
	 * @param menu
	 */
	@JRubyMethod(name = "add_menu")
	public void addMenu(Menu menu)
	{
		if (menu != null)
		{
			if (this._children == null)
			{
				this._children = new ArrayList<Menu>();
			}
			
			// set parent
			menu._parent = this;
			
			// add to our list
			this._children.add(menu);
		}
	}
	
	/**
	 * getCommand
	 * 
	 * @return
	 */
	public Command getCommand()
	{
		Command result = null;
		
		if (this.hasChildren() == false)
		{
			// TODO: lookup command in this menu's bundle
		}
		
		return result;
	}
	
	/**
	 * getCommandName
	 * 
	 * @param name
	 * @return
	 */
	@JRubyMethod(name = "command")
	public String getCommandName(String name)
	{
		return this._commandName;
	}
	
	/**
	 * getParent
	 * 
	 * @return
	 */
	public Menu getParent()
	{
		return this._parent;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.AbstractModel#getScopeSelector()
	 */
	public ScopeSelector getScopeSelector()
	{
		ScopeSelector result = null;
		
		if (this._scope != null)
		{
			result = new ScopeSelector(this._scope);
		}
		else
		{
			Menu parent = this._parent;
			
			while (parent != null)
			{
				if (parent._scope != null)
				{
					result = new ScopeSelector(parent._scope);
					break;
				}
				else
				{
					parent = parent._parent;
				}
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
	 * isSeparator
	 * 
	 * @return
	 */
	public boolean isSeparator()
	{
		return this._displayName != null && this._displayName.startsWith(SEPARATOR_TEXT);
	}
	
	/**
	 * setCommandName
	 * 
	 * @param name
	 */
	@JRubyMethod(name = "command=")
	public void setCommandName(String name)
	{
		this.addMenu(new Menu(name));
	}
	
	/**
	 * toSource
	 */
	protected void toSource(SourcePrinter printer)
	{
		printer.printWithIndent("menu \"").print(this._displayName).println("\" {").increaseIndent(); //$NON-NLS-1$ //$NON-NLS-2$
		
		printer.printWithIndent("path: ").println(this._path); //$NON-NLS-1$
		printer.printWithIndent("scope: ").println(this.getScopeSelector().toString()); //$NON-NLS-1$
		
		if (this.hasChildren())
		{
			for (Menu menu : this._children)
			{
				menu.toSource(printer);
			}
		}
		
		printer.decreaseIndent().printlnWithIndent("}"); //$NON-NLS-1$
	}
}
