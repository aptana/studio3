package com.aptana.scripting.ui.views;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.Image;

import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.SnippetElement;

class SnippetsNode extends BaseNode
{
	private SnippetNode[] _snippets;

	/**
	 * CommandsNode
	 * 
	 * @param bundle
	 */
	public SnippetsNode(BundleElement bundle)
	{
		List<SnippetNode> snippets = new LinkedList<SnippetNode>();

		for (CommandElement command : bundle.getCommands())
		{
			if (command instanceof SnippetElement)
			{
				snippets.add(new SnippetNode((SnippetElement) command));
			}
		}

		this._snippets = snippets.toArray(new SnippetNode[snippets.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getChildren()
	 */
	public Object[] getChildren()
	{
		return this._snippets;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getImage()
	 */
	public Image getImage()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.CollectionNode#getLabel()
	 */
	public String getLabel()
	{
		return Messages.SnippetsNode_Snippets_Node;
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
