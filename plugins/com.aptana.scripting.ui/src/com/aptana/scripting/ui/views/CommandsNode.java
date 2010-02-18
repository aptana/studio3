package com.aptana.scripting.ui.views;

import java.util.LinkedList;
import java.util.List;

import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.SnippetElement;

class CommandsNode implements CollectionNode
{
	private CommandElement[] _commands;
	
	/**
	 * CommandsNode
	 * 
	 * @param bundle
	 */
	public CommandsNode(BundleElement bundle)
	{
		List<CommandElement> commands = new LinkedList<CommandElement>();
		
		for (CommandElement command : bundle.getCommands())
		{
			if ((command instanceof SnippetElement) == false)
			{
				commands.add(command);
			}
		}
		
		this._commands = commands.toArray(new CommandElement[commands.size()]);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.CollectionNode#getChildren()
	 */
	public Object[] getChildren()
	{
		return this._commands;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.CollectionNode#getLabel()
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
