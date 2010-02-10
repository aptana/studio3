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

	public String[] getBlockVars()
	{
		return blockVars.toArray(new String[blockVars.size()]);
	}

	public String[] getParameters()
	{
		return fParameters;
	}

	public Visibility getVisibility()
	{
		return fVisibility;
	}

	@Override
	public short getType()
	{
		return IRubyElement.METHOD;
	}

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
		for (int i = 0; i < fParameters.length; ++i)
		{
			text.append(fParameters[i]);
			if (i < fParameters.length - 1)
			{
				text.append(", "); //$NON-NLS-1$
			}
		}
		text.append(")"); //$NON-NLS-1$
		return text.toString();
	}
}
