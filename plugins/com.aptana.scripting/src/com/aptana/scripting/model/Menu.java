package com.aptana.scripting.model;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Menu extends AbstractModel
{
	/**
	 * Snippet
	 * 
	 * @param path
	 */
	public Menu(String path)
	{
		super(path);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);

		// open snippet
		writer.append("  menu \"").append(this._displayName).println("\" {"); //$NON-NLS-1$ //$NON-NLS-2$

		// show body
		writer.append("    path:  ").println(this._path); //$NON-NLS-1$
		writer.append("    scope: ").println(this._scope); //$NON-NLS-1$

		// close snippet
		writer.println("  }"); //$NON-NLS-1$

		return sw.toString();
	}
}
