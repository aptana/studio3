package com.aptana.scripting.ui.views;

import java.util.LinkedList;
import java.util.List;

import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.SnippetElement;

public class SnippetsNode implements CollectionNode
{
	private SnippetElement[] _snippets;
	
	/**
	 * CommandsNode
	 * 
	 * @param bundle
	 */
	public SnippetsNode(BundleElement bundle)
	{
		List<CommandElement> snippets = new LinkedList<CommandElement>();
		
		for (CommandElement command : bundle.getCommands())
		{
			if (command instanceof SnippetElement)
			{
				snippets.add(command);
			}
		}
		
		this._snippets = snippets.toArray(new SnippetElement[snippets.size()]);
	}
	
	/**
	 * getChildren
	 * 
	 * @return
	 */
	public Object[] getChildren()
	{
		return this._snippets;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.CollectionNode#getLabel()
	 */
	public String getLabel()
	{
		return "Snippets";
	}
	
	/**
	 * hasChildren
	 * 
	 * @return
	 */
	public boolean hasChildren()
	{
		return this._snippets.length > 0;
	}
}
