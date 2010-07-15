package com.aptana.editor.erb.html.parsing;

import com.aptana.editor.ruby.core.IRubyScript;
import com.aptana.editor.ruby.parsing.IRubyParserConstants;
import com.aptana.parsing.ast.ParseNode;

public class ERBScript extends ParseNode
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

		setChildren(fScript.getChildren());
		setLocation(script.getStartingOffset(), script.getEndingOffset());
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
