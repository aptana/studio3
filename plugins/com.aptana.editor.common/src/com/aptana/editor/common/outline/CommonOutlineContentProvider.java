package com.aptana.editor.common.outline;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.parsing.ast.IParseNode;

public class CommonOutlineContentProvider implements ITreeContentProvider
{

	private static final Object[] EMPTY = new Object[0];

	@Override
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof AbstractThemeableEditor)
		{
			IParseNode root = ((AbstractThemeableEditor) parentElement).getFileService().getParseResult();
			if (root != null)
			{
				return filter(root.getChildren());
			}
		}
		return EMPTY;
	}

	@Override
	public Object getParent(Object element)
	{
		if (element instanceof IParseNode)
		{
			return ((IParseNode) element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element)
	{
		return getChildren(element).length > 0;
	}

	@Override
	public Object[] getElements(Object inputElement)
	{
		return getChildren(inputElement);
	}

	@Override
	public void dispose()
	{
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
	}

	/**
	 * Subclass could override to filter out specific nodes.
	 * 
	 * @param nodes
	 *            the array containing the parse result
	 * @return the nodes that should be displayed
	 */
	protected IParseNode[] filter(IParseNode[] nodes)
	{
		return nodes;
	}
}
