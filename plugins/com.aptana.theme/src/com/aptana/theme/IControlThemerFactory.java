package com.aptana.theme;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;

public interface IControlThemerFactory
{

	public abstract void apply(Control control);

	public abstract void dispose(Control control);
	
	public abstract void apply(Viewer viewer);

	public abstract void dispose(Viewer viewer);

	public abstract void dispose();

}