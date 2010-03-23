package com.aptana.editor.common.outline;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.parsing.ast.IParseNode;

public class CommonOutlineContentProvider implements ITreeContentProvider
{

	private static final Object[] EMPTY = new Object[0];
	private IParseListener fListener;

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
		else if (parentElement instanceof IParseNode)
		{
			return filter(((IParseNode) parentElement).getChildren());
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
		boolean isCU = (newInput instanceof AbstractThemeableEditor);

		if (isCU && fListener == null)
		{
			final AbstractThemeableEditor editor = (AbstractThemeableEditor) newInput;
			fListener = new IParseListener()
			{
				@Override
				public void parseFinished()
				{
					Display.getDefault().asyncExec(new Runnable()
					{

						@Override
						public void run()
						{
							CommonOutlinePage page = editor.getOutlinePage();
							// FIXME What if the parse failed! We don't really want to wipe the existing results! This is just a hack!
							IParseNode node = editor.getFileService().getParseResult();
							if (node.getChildrenCount() > 0)
							{
								page.refresh();
								page.expandToLevel(2);
							}
						}
					});
				}
			};
			editor.getFileService().addListener(fListener);
		}
		else if (!isCU && fListener != null)
		{
			AbstractThemeableEditor editor = (AbstractThemeableEditor) oldInput;
			editor.getFileService().removeListener(fListener);
			fListener = null;
		}
	}

	/**
	 * Subclass could override to return a specific list from the result.
	 * 
	 * @param nodes
	 *            the array containing the parse result
	 * @return the specific top level objects to display
	 */
	protected Object[] filter(IParseNode[] nodes)
	{
		return nodes;
	}
}
