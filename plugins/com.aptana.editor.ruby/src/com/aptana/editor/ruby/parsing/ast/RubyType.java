package com.aptana.editor.ruby.parsing.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.aptana.editor.ruby.core.IRubyElement;
import com.aptana.editor.ruby.core.IRubyField;
import com.aptana.editor.ruby.core.IRubyMethod;
import com.aptana.editor.ruby.core.IRubyType;

public class RubyType extends NamedMember implements IRubyType
{

	private static final String[] EMPTY_ARRAY = new String[0];

	/**
	 * the names of the module this type includes
	 */
	private String[] includedModuleNames;

	/**
	 * the name of the superclass for this type
	 */
	private String superclassName;

	public RubyType(String name, int start, int end)
	{
		super(name, start, end);
		includedModuleNames = EMPTY_ARRAY;
	}

	@Override
	public IRubyField[] getFields()
	{
		List<IRubyElement> elements = new ArrayList<IRubyElement>();
		elements.addAll(Arrays.asList(getChildrenOfType(IRubyElement.CONSTANT)));
		elements.addAll(Arrays.asList(getChildrenOfType(INSTANCE_VAR)));
		elements.addAll(Arrays.asList(getChildrenOfType(CLASS_VAR)));
		return elements.toArray(new IRubyField[elements.size()]);
	}

	@Override
	public IRubyMethod[] getMethods()
	{
		IRubyElement[] elements = getChildrenOfType(IRubyElement.METHOD);
		IRubyMethod[] methods = new IRubyMethod[elements.length];
		for (int i = 0; i < elements.length; ++i)
		{
			methods[i] = (IRubyMethod) elements[i];
		}
		return methods;
	}

	@Override
	public String[] getIncludedModuleNames()
	{
		if (isAnonymous())
		{
			return EMPTY_ARRAY;
		}
		return includedModuleNames;
	}

	@Override
	public String getSuperclassName()
	{
		if (isAnonymous())
		{
			if (includedModuleNames.length > 0)
			{
				return includedModuleNames[0];
			}
		}
		return superclassName;
	}

	@Override
	public short getNodeType()
	{
		return IRubyElement.TYPE;
	}

	@Override
	public boolean isClass()
	{
		return true;
	}

	@Override
	public boolean isModule()
	{
		return false;
	}

	/**
	 * Sets the names of the modules this type includes.
	 */
	public void setIncludedModuleNames(String[] includedModuleNames)
	{
		this.includedModuleNames = includedModuleNames;
	}

	/**
	 * Sets the name of this type's superclass.
	 */
	public void setSuperclassName(String superclassName)
	{
		this.superclassName = superclassName;
	}

	private boolean isAnonymous()
	{
		return getName() == null || getName().length() == 0;
	}
}
