package com.aptana.editor.ruby.parsing.ast;

import com.aptana.editor.ruby.core.IImportContainer;
import com.aptana.editor.ruby.core.IRubyElement;

public class RubyImportContainer extends RubyElement implements IImportContainer
{

	private static final String NAME = "require/load declarations"; //$NON-NLS-1$

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public short getType()
	{
		return IRubyElement.IMPORT_CONTAINER;
	}

	@Override
	public String toString()
	{
		return getName();
	}
}
