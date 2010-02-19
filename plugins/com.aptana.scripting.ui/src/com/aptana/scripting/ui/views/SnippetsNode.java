package com.aptana.scripting.ui.views;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.Image;

import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.SnippetElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class SnippetsNode extends BaseNode
{
	private static final Image SNIPPETS_ICON = ScriptingUIPlugin.getImage("icons/folder.png"); //$NON-NLS-1$
	private SnippetNode[] _snippets;

	/**
	 * CommandsNode
	 * 
	 * @param bundle
	 */
	public SnippetsNode(BundleElement bundle)
	{
		this(bundle.getCommands());
	}

	/**
	 * SnippetNode
	 * 
	 * @param elements
	 */
	public SnippetsNode(CommandElement[] elements)
	{
		List<SnippetNode> snippets = new LinkedList<SnippetNode>();

		if (elements != null)
		{
			Arrays.sort(elements);

			for (CommandElement command : elements)
			{
				if (command instanceof SnippetElement)
				{
					snippets.add(new SnippetNode((SnippetElement) command));
				}
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
		return SNIPPETS_ICON;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getLabel()
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
