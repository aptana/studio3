/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.parsing.ast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beaver.Symbol;

import com.aptana.editor.js.IJSConstants;
import com.aptana.editor.js.sdoc.model.DocumentationBlock;
import com.aptana.editor.js.sdoc.parsing.SDocParser;
import com.aptana.editor.js.vsdoc.parsing.VSDocReader;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseNode;
import com.aptana.parsing.ast.ParseRootNode;

public class JSNode extends ParseNode
{
	protected static final short DEFAULT_TYPE = IJSNodeTypes.EMPTY;
	private static Map<Short, String> TYPE_NAME_MAP;

	private short fType;
	private boolean fSemicolonIncluded;

	private DocumentationBlock fDocumentation;
	private Symbol fPreDocumentationComment;
	private Symbol fPostDocumentationComment;

	/**
	 * static initializer
	 */
	static
	{
		TYPE_NAME_MAP = new HashMap<Short, String>();

		Class<?> klass = IJSNodeTypes.class;

		for (Field field : klass.getFields())
		{
			String name = field.getName().toLowerCase();

			try
			{
				Short value = field.getShort(klass);

				TYPE_NAME_MAP.put(value, name);
			}
			catch (IllegalArgumentException e) // $codepro.audit.disable emptyCatchClause
			{
			}
			catch (IllegalAccessException e) // $codepro.audit.disable emptyCatchClause
			{
			}
		}
	}

	/**
	 * JSNode
	 */
	protected JSNode()
	{
		this(DEFAULT_TYPE);
	}

	/**
	 * JSNode
	 * 
	 * @param type
	 * @param start
	 * @param end
	 * @param children
	 */
	protected JSNode(short type, JSNode... children)
	{
		super(IJSConstants.CONTENT_TYPE_JS);

		// set node type
		this.fType = type;

		// store children
		this.setChildren(children);
	}

	/**
	 * accept
	 * 
	 * @param walker
	 */
	public void accept(JSTreeWalker walker)
	{
		// sub-classes must override this method so their types will be
		// recognized properly
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseBaseNode#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		boolean result = false;

		if (this == obj)
		{
			result = true;
		}
		else if (obj instanceof JSNode)
		{
			JSNode other = (JSNode) obj;

			// @formatter:off
			result = 
					getNodeType() == other.getNodeType()
				&&	getSemicolonIncluded() == other.getSemicolonIncluded()
				&&	Arrays.equals(getChildren(), other.getChildren());
			// @formatter:on
		}

		return result;
	}

	/**
	 * getContainingStatementNode
	 * 
	 * @return
	 */
	public IParseNode getContainingStatementNode()
	{
		// move up to nearest statement
		IParseNode result = this;
		IParseNode parent = result.getParent();

		while (parent != null)
		{
			if (parent instanceof ParseRootNode || parent.getNodeType() == IJSNodeTypes.STATEMENTS)
			{
				break;
			}
			else
			{
				result = parent;
				parent = parent.getParent();
			}
		}

		return result;
	}

	/**
	 * getDocumentation: lazily computes the documentation from the attached node if it's still not computed.
	 * 
	 * @return
	 */
	public DocumentationBlock getDocumentation()
	{
		if (this.fDocumentation == null)
		{
			Symbol comment = fPreDocumentationComment;
			if (comment != null)
			{
				updateDocumentationFromSDoc(comment);
			}
			else
			{
				Symbol doc = fPostDocumentationComment;
				if (doc != null)
				{
					updateDocumentationFromVSDoc(doc);
				}
			}
		}
		return this.fDocumentation;
	}

	private void updateDocumentationFromVSDoc(Symbol doc)
	{
		VSDocReader parser = new VSDocReader();

		ByteArrayInputStream input = null;

		try
		{
			List<Symbol> lines = (List<Symbol>) doc.value;
			String source = this.buildVSDocXML(lines);

			input = new ByteArrayInputStream(source.getBytes());

			parser.loadXML(input);

			DocumentationBlock result = parser.getBlock();

			if (result != null)
			{
				if (lines.size() > 0)
				{
					result.setRange(lines.get(0).getStart(), lines.get(lines.size() - 1).getEnd());
				}

				setDocumentation(result);
			}
		}
		catch (java.lang.Exception e)
		{
		}
		finally
		{
			try
			{
				if (input != null)
				{
					input.close();
				}
			}
			catch (IOException e)
			{
			}
		}
	}

	private void updateDocumentationFromSDoc(Symbol comment)
	{
		SDocParser parser = new SDocParser();
		try
		{
			Object result = parser.parse((String) comment.value, comment.getStart());

			if (result instanceof DocumentationBlock)
			{
				setDocumentation((DocumentationBlock) result);
			}
		}
		catch (java.lang.Exception e)
		{
		}
	}

	/**
	 * buildVSDocXML
	 * 
	 * @param lines
	 * @return
	 */
	private String buildVSDocXML(List<Symbol> lines)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("<docs>\n"); //$NON-NLS-1$

		for (Symbol line : lines)
		{
			String text = (String) line.value;

			buffer.append(text.substring(3));
		}

		buffer.append("</docs>"); //$NON-NLS-1$

		return buffer.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseBaseNode#getElementName()
	 */
	@Override
	public String getElementName()
	{
		String result = TYPE_NAME_MAP.get(this.getNodeType());

		return (result == null) ? super.getElementName() : result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseBaseNode#getType()
	 */
	public short getNodeType()
	{
		return fType;
	}

	/**
	 * getSemicolonIncluded
	 * 
	 * @return
	 */
	public boolean getSemicolonIncluded()
	{
		return fSemicolonIncluded;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseBaseNode#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = getNodeType();
		hash = 31 * hash + (getSemicolonIncluded() ? 1 : 0);
		hash = 31 * hash + Arrays.hashCode(getChildren());
		return hash;
	}

	/**
	 * isEmpty
	 * 
	 * @return
	 */
	public boolean isEmpty()
	{
		return getNodeType() == IJSNodeTypes.EMPTY;
	}

	/**
	 * Pre documentation (i.e.: sdoc format).
	 */
	public void setPreDocumentation(Symbol comment)
	{
		this.fPreDocumentationComment = comment;
		this.fPostDocumentationComment = null;
		this.fDocumentation = null;
	}

	/**
	 * Post documentation (i.e.: vsdoc format).
	 * 
	 * @param comment
	 */
	public void setPostDocumentation(Symbol comment)
	{
		this.fPreDocumentationComment = null;
		this.fPostDocumentationComment = comment;
		this.fDocumentation = null;
	}

	/**
	 * setDocumentation
	 * 
	 * @param block
	 */
	public void setDocumentation(DocumentationBlock block)
	{
		this.fDocumentation = block;
		this.fPreDocumentationComment = null;
		this.fPostDocumentationComment = null;
	}

	/**
	 * setType
	 * 
	 * @param type
	 */
	protected void setNodeType(short type)
	{
		fType = type;
	}

	/**
	 * setSemicolonIncluded
	 * 
	 * @param included
	 */
	public void setSemicolonIncluded(boolean included)
	{
		fSemicolonIncluded = included;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#toString()
	 */
	public String toString()
	{
		JSFormatWalker walker = new JSFormatWalker();

		this.accept(walker);

		return walker.getText();
	}

}
