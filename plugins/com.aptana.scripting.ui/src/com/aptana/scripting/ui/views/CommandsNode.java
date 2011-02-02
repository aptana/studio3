/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.ui.views;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.Image;

import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.SnippetElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class CommandsNode extends BaseNode
{
	private static final Image COMMANDS_ICON = ScriptingUIPlugin.getImage("icons/folder.png"); //$NON-NLS-1$
	private CommandNode[] _commands;

	/**
	 * CommandsNode
	 * 
	 * @param bundle
	 */
	public CommandsNode(BundleElement bundle)
	{
		this(bundle.getCommands());
	}

	/**
	 * CommandsNode
	 * 
	 * @param elements
	 */
	public CommandsNode(List<CommandElement> elements)
	{
		List<CommandNode> commands = new LinkedList<CommandNode>();

		if (elements != null)
		{
			Collections.sort(elements);

			for (CommandElement command : elements)
			{
				if ((command instanceof SnippetElement) == false)
				{
					commands.add(new CommandNode(command));
				}
			}
		}

		this._commands = commands.toArray(new CommandNode[commands.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getChildren()
	 */
	public Object[] getChildren()
	{
		return this._commands;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getImage()
	 */
	public Image getImage()
	{
		return COMMANDS_ICON;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getLabel()
	 */
	public String getLabel()
	{
		return Messages.CommandsNode_Commands_Node;
	}

	/**
	 * hasChildren
	 * 
	 * @return
	 */
	public boolean hasChildren()
	{
		return this._commands.length > 0;
	}
}
