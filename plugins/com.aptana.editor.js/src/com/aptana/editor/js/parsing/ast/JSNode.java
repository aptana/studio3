package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseBaseNode;

public class JSNode extends ParseBaseNode
{

	protected static final short DEFAULT_TYPE = JSNodeTypes.EMPTY;

	private short fType;

	private boolean fSemicolonIncluded;

	public JSNode()
	{
		fType = DEFAULT_TYPE;
	}

	public JSNode(short type, int start, int end)
	{
		fType = type;
		this.start = start;
		this.end = end;
	}

	public JSNode(short type, int start, int end, boolean semicolon)
	{
		this(type, start, end);
		fSemicolonIncluded = semicolon;
	}

	public JSNode(short type, JSNode[] children, int start, int end)
	{
		this(type, start, end);
		setChildren(children);
	}

	public short getType()
	{
		return fType;
	}

	public boolean getSemicolonIncluded()
	{
		return fSemicolonIncluded;
	}

	public void setSemicolonIncluded(boolean included)
	{
		fSemicolonIncluded = included;
	}

	public boolean isEmpty()
	{
		return getType() == JSNodeTypes.EMPTY;
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		IParseNode[] children = getChildren();
		int type = getType();
		switch (type)
		{
			case JSNodeTypes.EMPTY:
				break;
			case JSNodeTypes.ASSIGN:
				text.append(children[0]).append(" = ").append(children[1]); //$NON-NLS-1$
				break;
			case JSNodeTypes.INVOKE:
				text.append(children[0]).append("(").append(children[1]).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
				break;
			case JSNodeTypes.DECLARATION:
				text.append(children[0]);
				if (children.length > 1 && !((JSNode) children[1]).isEmpty())
				{
					text.append(" = ").append(children[1]); //$NON-NLS-1$
				}
				break;
			case JSNodeTypes.TRY:
				text.append("try "); //$NON-NLS-1$
				text.append(children[0]);
				if (!((JSNode) children[1]).isEmpty())
				{
					text.append(" ").append(children[1]); //$NON-NLS-1$
				}
				if (!((JSNode) children[2]).isEmpty())
				{
					text.append(" ").append(children[2]); //$NON-NLS-1$
				}
				break;
			case JSNodeTypes.CATCH:
				text.append("catch (").append(children[0]).append(") ").append(children[1]); //$NON-NLS-1$ //$NON-NLS-2$
				break;
			case JSNodeTypes.FINALLY:
				text.append("finally ").append(children[0]); //$NON-NLS-1$
				break;
			case JSNodeTypes.CONDITIONAL:
				text.append(children[0]).append(" ? ").append(children[1]).append(" : ").append(children[2]); //$NON-NLS-1$ //$NON-NLS-2$
				break;
			default:
				text.append(super.toString());
		}

		return appendSemicolon(text.toString());
	}

	protected void setType(short type)
	{
		fType = type;
	}

	protected String appendSemicolon(String text)
	{
		if (getSemicolonIncluded())
		{
			return text + ";"; //$NON-NLS-1$
		}
		return text;
	}
}
