package com.aptana.editor.ruby.parsing.ast;

import java.util.HashSet;
import java.util.Set;

import com.aptana.editor.ruby.core.IRubyElement;
import com.aptana.editor.ruby.core.IRubyMethod;

public class RubyMethod extends NamedMember implements IRubyMethod
{

	private String[] fParameters;
	private Visibility fVisibility;
	private boolean isSingleton;
	private Set<String> blockVars;

	public RubyMethod(String name, String[] parameters, int start, int end)
	{
		super(name, start, end);
		fParameters = parameters;
		blockVars = new HashSet<String>();
	}

	public void addBlockVar(String name)
	{
		blockVars.add(name);
	}

	@Override
	public String[] getBlockVars()
	{
		return blockVars.toArray(new String[blockVars.size()]);
	}

	@Override
	public String[] getParameters()
	{
		return fParameters;
	}

	@Override
	public Visibility getVisibility()
	{
		return fVisibility;
	}

	@Override
	public short getNodeType()
	{
		return IRubyElement.METHOD;
	}

	@Override
	public boolean isSingleton()
	{
		return isSingleton;
	}

	public void setVisibility(Visibility visibility)
	{
		fVisibility = visibility;
	}

	public void setIsSingleton(boolean singleton)
	{
		isSingleton = singleton;
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		text.append(getName());
		text.append("("); //$NON-NLS-1$
		String[] params = getParameters();
		for (int i = 0; i < params.length; ++i)
		{
			text.append(params[i]);
			if (i < params.length - 1)
			{
				text.append(", "); //$NON-NLS-1$
			}
		}
		text.append(")"); //$NON-NLS-1$
		return text.toString();
	}
}
