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

	public IRubyScript getScript()
	{
		return fScript;
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
		ERBScript other = (ERBScript) obj;
		return start == other.start && end == other.end && fScript.equals(other.fScript);
	}

	@Override
	public int hashCode()
	{
		int hash = start * 31 + end;
		hash = hash * 31 + fScript.hashCode();
		return hash;
	}
}
