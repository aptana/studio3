/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import org.jruby.RubyProc;

import com.aptana.core.util.SourcePrinter;

public class EnvironmentElement extends AbstractBundleElement
{
	private RubyProc _invokeBlock;

	/**
	 * EnvironmentElement
	 * 
	 * @param path
	 */
	public EnvironmentElement(String path)
	{
		super(path);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.AbstractElement#getElementName()
	 */
	@Override
	protected String getElementName()
	{
		return "environment"; //$NON-NLS-1$
	}

	/**
	 * getInvokeBlock
	 * 
	 * @return
	 */
	public RubyProc getInvokeBlock()
	{
		return _invokeBlock;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.AbstractElement#printBody(com.aptana.core.util.SourcePrinter)
	 */
	@Override
	protected void printBody(SourcePrinter printer, boolean includeBlocks)
	{
		// output path and scope
		printer.printWithIndent("path: ").println(this.getPath()); //$NON-NLS-1$
		printer.printWithIndent("scope: ").println(this.getScope()); //$NON-NLS-1$

		// output invoke block, if it is defined
		if (includeBlocks && this.getInvokeBlock() != null)
		{
			// Spit out something repeatable. block type for now
			printer.printWithIndent("block: ").println(this.getInvokeBlock().getBlock().type.toString()); //$NON-NLS-1$
		}
	}

	/**
	 * setInvokeBlock
	 * 
	 * @param block
	 */
	public void setInvokeBlock(RubyProc block)
	{
		this._invokeBlock = block;
	}
}
