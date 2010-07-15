package com.aptana.editor.common.outline;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.resolver.IPathResolver;
import com.aptana.parsing.ast.IParseNode;

public class CommonOutlineContentProvider implements ITreeContentProvider
{

	protected static final Object[] EMPTY = new Object[0];
	private IParseListener fListener;
	protected IPathResolver resolver;

	public CommonOutlineItem getOutlineItem(IParseNode node)
	{
		if (node == null)
		{
			return null;
		}
		return new CommonOutlineItem(node.getNameNode().getNameRange(), node);
	}

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
		else if (parentElement instanceof CommonOutlineItem)
		{
			// delegates to the parse node it references to
			return getChildren(((CommonOutlineItem) parentElement).getReferenceNode());
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
		if (element instanceof CommonOutlineItem)
		{
			IParseNode node = ((CommonOutlineItem) element).getReferenceNode();
			return getOutlineItem(node.getParent());
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
							// FIXME What if the parse failed! We don't really want to wipe the existing results! This
							// is just a hack!
							IParseNode node = editor.getFileService().getParseResult();
							if (node != null)
							{
								page.refresh();
								page.expandToLevel(2);
							}
						}
					});
				}
			};
			editor.getFileService().addListener(fListener);
			this.resolver = PathResolverProvider.getResolver(editor.getEditorInput());
		}
		else if (!isCU && fListener != null)
		{
			AbstractThemeableEditor editor = (AbstractThemeableEditor) oldInput;
			editor.getFileService().removeListener(fListener);
			fListener = null;
			this.resolver = PathResolverProvider.getResolver(editor.getEditorInput());
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
