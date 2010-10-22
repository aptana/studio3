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
package com.aptana.scripting.ui.views;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.BundleEntry;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.MenuElement;
import com.aptana.scripting.model.SnippetElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class BundleEntryNode extends BaseNode
{
	private enum Property
	{
		NAME, CONTRIBUTOR_COUNT
	}

	private static final Image BUNDLE_ENTRY_ICON = ScriptingUIPlugin.getImage("icons/bundle_entry.png"); //$NON-NLS-1$
	private BundleEntry _entry;
	
	private Action reloadAction;
	

	/**
	 * BundleEntryNode
	 * 
	 * @param entry
	 */
	public BundleEntryNode(BundleEntry entry)
	{
		this._entry = entry;
		
		this.makeActions();
	}

	/**
	 * getActions
	 */
	public Action[] getActions()
	{
		return new Action[] { reloadAction };
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getChildren()
	 */
	public Object[] getChildren()
	{
		List<Object> result = new LinkedList<Object>();

		// add bundle element that contribute to this bundle
		for (BundleElement bundle : this._entry.getBundles())
		{
			result.add(new BundleNode(bundle));
		}

		// divide commands into commands and snippets
		List<CommandElement> commands = new LinkedList<CommandElement>();
		List<SnippetElement> snippets = new LinkedList<SnippetElement>();

		for (CommandElement element : this._entry.getCommands())
		{
			if (element instanceof SnippetElement)
			{
				snippets.add((SnippetElement) element);
			}
			else
			{
				commands.add(element);
			}
		}

		// add visible commands
		if (commands.size() > 0)
		{
			result.add(new CommandsNode(commands.toArray(new CommandElement[commands.size()])));
		}

		// add visible snippets
		if (snippets.size() > 0)
		{
			result.add(new SnippetsNode(snippets.toArray(new CommandElement[snippets.size()])));
		}

		// add visible menus
		MenuElement[] menus = this._entry.getMenus();

		if (menus != null && menus.length > 0)
		{
			result.add(new MenusNode(menus));
		}

		return result.toArray();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getImage()
	 */
	public Image getImage()
	{
		return BUNDLE_ENTRY_ICON;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getLabel()
	 */
	public String getLabel()
	{
		return this._entry.getName();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		PropertyDescriptor nameProperty = new PropertyDescriptor(Property.NAME, "Name"); //$NON-NLS-1$
		PropertyDescriptor contributorCountProperty = new PropertyDescriptor(Property.CONTRIBUTOR_COUNT, "Contributors"); //$NON-NLS-1$

		return new IPropertyDescriptor[] { nameProperty, contributorCountProperty };
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id)
	{
		Object result = null;

		if (id instanceof Property)
		{
			switch ((Property) id)
			{
				case NAME:
					result = this._entry.getName();
					break;
					
				case CONTRIBUTOR_COUNT:
					BundleElement[] bundles = this._entry.getBundles();
					
					result = (bundles != null) ? bundles.length : 0;
					break;
			}
		}

		return result;
	}


	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#hasChildren()
	 */
	public boolean hasChildren()
	{
		boolean result = false;

		for (BundleElement bundle : this._entry.getBundles())
		{
			if (bundle.hasCommands() || bundle.hasMenus())
			{
				result = true;
				break;
			}
		}

		return result;
	}
	
	/**
	 * makeActions
	 */
	private void makeActions()
	{
		reloadAction = new Action()
		{
			public void run()
			{
				_entry.reload();
			}
		};
		reloadAction.setText(Messages.BundleEntryNode_TXT_Reload);
		//reloadAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_));
	}
}
