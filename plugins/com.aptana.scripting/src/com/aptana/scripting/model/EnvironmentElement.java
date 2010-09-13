package com.aptana.scripting.model;

import org.jruby.RubyProc;

import com.aptana.parsing.io.SourcePrinter;

public class EnvironmentElement extends AbstractBundleElement
{

	private RubyProc _invokeBlock;

	public EnvironmentElement(String path)
	{
		super(path);
	}

	@Override
	protected String getElementName()
	{
		return "environment"; //$NON-NLS-1$
	}

	@Override
	protected void printBody(SourcePrinter printer)
	{
		// output path and scope
		printer.printWithIndent("path: ").println(this.getPath()); //$NON-NLS-1$
		printer.printWithIndent("scope: ").println(this.getScope()); //$NON-NLS-1$
	}

	public void setInvokeBlock(RubyProc block)
	{
		this._invokeBlock = block;
	}

	public RubyProc getInvokeBlock()
	{
		return _invokeBlock;
	}

}
