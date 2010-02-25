package com.aptana.editor.erb.html.parsing;

import com.aptana.editor.ruby.core.IRubyScript;
import com.aptana.editor.ruby.parsing.IRubyParserConstants;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseBaseNode;

public class ERBScript extends ParseBaseNode
{

	private IRubyScript fScript;
	private String fStartTag;
	private String fEndTag;

	public ERBScript(IRubyScript script, String startTag, String endTag)
	{
		super(IRubyParserConstants.LANGUAGE);
		fScript = script;
		fStartTag = startTag;
		fEndTag = endTag;

		this.start = script.getStartingOffset();
		this.end = script.getEndingOffset();
	}

	public String getStartTag()
	{
		return fStartTag;
	}

	public String getEndTag()
	{
		return fEndTag;
	}

	@Override
	public IParseNode getChild(int index)
	{
		return fScript.getChild(index);
	}

	@Override
	public IParseNode[] getChildren()
	{
		return fScript.getChildren();
	}

	@Override
	public int getChildrenCount()
	{
		return fScript.getChildrenCount();
	}

	@Override
	public int getIndex(IParseNode child)
	{
		return fScript.getIndex(child);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!super.equals(obj))
		{
			return false;
		}
		if (!(obj instanceof ERBScript))
		{
			return false;
		}
		return fScript.equals(((ERBScript) obj).fScript);
	}

	@Override
	public int hashCode()
	{
		return fScript.hashCode();
	}
}
