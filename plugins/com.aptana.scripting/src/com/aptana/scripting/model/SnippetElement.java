package com.aptana.scripting.model;

public class SnippetElement extends TriggerableElement
{
	private String _expansion;

	/**
	 * Snippet
	 * 
	 * @param name
	 */
	public SnippetElement(String path)
	{
		super(path);
	}

	/**
	 * getExpansion
	 * 
	 * @return
	 */
	public String getExpansion()
	{
		return this._expansion;
	}

	/**
	 * setExpansion
	 * 
	 * @param expansion
	 */
	public void setExpansion(String expansion)
	{
		this._expansion = expansion;
	}

	/**
	 * toSource
	 */
	protected void toSource(SourcePrinter printer)
	{
		printer.printWithIndent("snippet \"").print(this._displayName).println("\" {").increaseIndent(); //$NON-NLS-1$ //$NON-NLS-2$

		printer.printWithIndent("path: ").println(this._path); //$NON-NLS-1$
		printer.printWithIndent("scope: ").println(this._scope); //$NON-NLS-1$
		printer.printWithIndent("trigger: ").println(this._trigger); //$NON-NLS-1$

		printer.decreaseIndent().printlnWithIndent("}");
	}
}
