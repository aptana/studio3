package com.aptana.editor.ruby.core;

public interface IRubyType extends IRubyMember
{

	public IRubyField[] getFields();

	public IRubyMethod[] getMethods();

	public String[] getIncludedModuleNames();

	public String getSuperclassName();

	public boolean isClass();

	public boolean isModule();
}
