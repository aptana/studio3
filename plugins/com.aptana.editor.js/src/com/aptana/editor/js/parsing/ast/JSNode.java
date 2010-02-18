package com.aptana.editor.js.parsing.ast;

import com.aptana.editor.js.parsing.IJSParserConstants;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseBaseNode;

public class JSNode extends ParseBaseNode
{

	protected static final short DEFAULT_TYPE = JSNodeTypes.EMPTY;

	private short fType;

	private boolean fSemicolonIncluded;

	public JSNode()
	{
		this(DEFAULT_TYPE, 0, 0);
	}

	public JSNode(short type, int start, int end)
	{
		super(IJSParserConstants.LANGUAGE);
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
			case JSNodeTypes.ASSIGN:
				text.append(children[0]).append(" = ").append(children[1]); //$NON-NLS-1$
				break;
			case JSNodeTypes.INVOKE:
				text.append(children[0]).append("(").append(children[1]).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
				break;
			case JSNodeTypes.DECLARATION:
				text.append(children[0]);
				if (!((JSNode) children[1]).isEmpty())
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
			case JSNodeTypes.CONSTRUCT:
				text.append("new ").append(children[0]).append("(").append(children[1]).append(")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				break;
			case JSNodeTypes.NAME_VALUE_PAIR:
			case JSNodeTypes.LABELLED:
				text.append(children[0]).append(": ").append(children[1]); //$NON-NLS-1$
				break;
			case JSNodeTypes.WHILE:
				text.append("while (").append(children[0]).append(") ").append(children[1]); //$NON-NLS-1$ //$NON-NLS-2$
				break;
			case JSNodeTypes.WITH:
				text.append("with (").append(children[0]).append(") ").append(children[1]); //$NON-NLS-1$ //$NON-NLS-2$
				break;
			case JSNodeTypes.IF:
				text.append("if (").append(children[0]).append(") "); //$NON-NLS-1$ //$NON-NLS-2$
				text.append(children[1]);
				if (!((JSNode) children[2]).isEmpty())
				{
					if (children[1].getType() != JSNodeTypes.STATEMENTS)
					{
						text.append(";"); //$NON-NLS-1$
					}
					text.append(" else ").append(children[2]); //$NON-NLS-1$
				}
				break;
			case JSNodeTypes.DO:
				text.append("do ").append(children[0]); //$NON-NLS-1$
				if (children[0].getType() != JSNodeTypes.STATEMENTS)
				{
					text.append(";"); //$NON-NLS-1$
				}
				text.append(" while (").append(children[1]).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
				break;
			case JSNodeTypes.FOR:
				text.append("for ("); //$NON-NLS-1$
				if (!((JSNode) children[0]).isEmpty())
				{
					text.append(children[0]);
				}
				text.append(";"); //$NON-NLS-1$
				if (!((JSNode) children[1]).isEmpty())
				{
					text.append(" ").append(children[1]); //$NON-NLS-1$
				}
				text.append(";"); //$NON-NLS-1$
				if (!((JSNode) children[2]).isEmpty())
				{
					text.append(" ").append(children[2]); //$NON-NLS-1$
				}
				text.append(") ").append(children[3]); //$NON-NLS-1$
				break;
			case JSNodeTypes.FOR_IN:
				text.append("for (").append(children[0]).append(" in ").append(children[1]).append(") ").append( //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						children[2]);
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
