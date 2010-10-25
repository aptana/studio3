package com.aptana.scripting.ui.views;

import java.util.Arrays;
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
	public CommandsNode(CommandElement[] elements)
	{
		List<CommandNode> commands = new LinkedList<CommandNode>();

		if (elements != null)
		{
			Arrays.sort(elements);

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
