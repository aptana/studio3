package com.aptana.editor.ruby.core;

public interface IRubyMethod extends IRubyMember
{
	public enum Visibility
	{
		PUBLIC, PROTECTED, PRIVATE
	}

	public Visibility getVisibility();
}
