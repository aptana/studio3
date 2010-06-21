package com.aptana.editor.ruby.parsing.ast;

import com.aptana.editor.ruby.core.IImportContainer;
import com.aptana.editor.ruby.core.IRubyElement;
import com.aptana.editor.ruby.core.IRubyScript;

public class RubyScript extends RubyElement implements IRubyScript
{

	private RubyImportContainer fImportContainer;

	public RubyScript(int start, int end)
	{
		super(start, end);
	}

	@Override
	public IImportContainer getImportContainer()
	{
		if (fImportContainer == null)
		{
			fImportContainer = new RubyImportContainer();
			addChild(fImportContainer);
		}
		return fImportContainer;
	}

	@Override
	public short getNodeType()
	{
		return IRubyElement.SCRIPT;
	}
}
