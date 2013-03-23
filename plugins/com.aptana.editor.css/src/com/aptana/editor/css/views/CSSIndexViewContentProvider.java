package com.aptana.editor.css.views;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.css.core.model.CSSElement;
import com.aptana.css.core.model.ClassGroupElement;
import com.aptana.css.core.model.ColorGroupElement;
import com.aptana.css.core.model.IdGroupElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;

public class CSSIndexViewContentProvider implements ITreeContentProvider
{
	private static final Object[] NO_ELEMENTS = new Object[0];

	public void dispose()
	{
		// do nothing
	}

	@SuppressWarnings("unchecked")
	public Object[] getChildren(Object parentElement)
	{
		List<?> result = Collections.emptyList();

		if (parentElement instanceof CSSElement)
		{
			CSSElement root = (CSSElement) parentElement;

			// @formatter:off
			result = CollectionsUtil.newList(
				new ClassGroupElement(root.getIndex()),
				new ColorGroupElement(root.getIndex()),
				new IdGroupElement(root.getIndex())
			);
			// @formatter:on
		}
		else if (parentElement instanceof ClassGroupElement)
		{
			result = ((ClassGroupElement) parentElement).getClasses();
		}
		else if (parentElement instanceof IdGroupElement)
		{
			result = ((IdGroupElement) parentElement).getIds();
		}
		else if (parentElement instanceof ColorGroupElement)
		{
			result = ((ColorGroupElement) parentElement).getColors();
		}

		return result.toArray(new Object[result.size()]);
	}

	public Object[] getElements(Object inputElement)
	{
		Object[] result;

		if (inputElement instanceof IProject)
		{
			IProject project = (IProject) inputElement;
			Index index = getIndexManager().getIndex(project.getLocationURI());

			result = new Object[] { new CSSElement(index) };
		}
		else
		{
			result = NO_ELEMENTS;
		}

		return result;
	}

	protected IndexManager getIndexManager()
	{
		return IndexPlugin.getDefault().getIndexManager();
	}

	public Object getParent(Object element)
	{
		return null;
	}

	public boolean hasChildren(Object element)
	{
		return getChildren(element).length > 0;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		// do nothing
	}
}
