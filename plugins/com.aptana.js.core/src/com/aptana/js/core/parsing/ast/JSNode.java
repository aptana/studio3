/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing.ast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beaver.Symbol;

import com.aptana.core.util.ArrayUtil;
import com.aptana.js.core.IJSConstants;
import com.aptana.js.internal.core.parsing.VSDocReader;
import com.aptana.js.internal.core.parsing.sdoc.SDocParser;
import com.aptana.js.internal.core.parsing.sdoc.model.DocumentationBlock;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseNode;
import com.aptana.parsing.ast.ParseRootNode;

public class JSNode extends ParseNode
{
	private static final int NODE_TYPE_MASK = 255; // 8 bits
	private static final int DOC_TYPE_MASK = 0x3; // two bits

	protected static final short DEFAULT_TYPE = IJSNodeTypes.EMPTY;
	private static Map<Short, String> TYPE_NAME_MAP;

	/**
	 * Here we try to be smarter about storing docs. Since we can only have doc of one type at a time, we remove the
	 * pre/post/block fields and replace with a field to hold the doc and a field to hold the type. This should save us
	 * 4-bytes per-node.
	 */
	private static final short PRE_DOC = 0;
	private static final short POST_DOC = 1;
	private static final byte DOC_BLOCK = 2;

	private Symbol fDoc;

	/**
	 * A super-optimization. We compress the type of documentation we store in fDoc, the type of node we store for
	 * getNodeType, and the boolean flag for if a semicolon is included. First bit is used for determining if semicolon
	 * is included. Next 2 bits are for determining documentation node type. Next 8 bits are for node type.
	 */
	private int typeFlags = 0;

	/**
	 * We memoize the hashcode because it recursively asks for hash of children!
	 */
	private int fHash;

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
		super();

		// set node type
		setNodeType(type);

		// store children
		if (!ArrayUtil.isEmpty(children))
		{
			setChildren(children);
		}
	}

	public String getLanguage()
	{
		return IJSConstants.CONTENT_TYPE_JS;
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
		// no comment
		if (fDoc == null)
		{
			return null;
		}

		short docType = getDocType();
		// convert to DocumentationBlock lazily
		if (docType != DOC_BLOCK)
		{
			if (docType == PRE_DOC)
			{
				updateDocumentationFromSDoc(fDoc);
			}
			else
			{
				updateDocumentationFromVSDoc(fDoc);
			}
		}
		// If we were successful, return the block
		if (fDoc instanceof DocumentationBlock)
		{
			return (DocumentationBlock) fDoc;
		}

		// otherwise return null
		return null;
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
		// we need the 4th - 12th bits (8 bits)
		return (short) ((typeFlags >>> 3) & NODE_TYPE_MASK);
	}

	public short getDocType()
	{
		// we need the 2nd and 3rd bits
		return (short) ((typeFlags >>> 1) & DOC_TYPE_MASK);
	}

	/**
	 * getSemicolonIncluded
	 * 
	 * @return
	 */
	public boolean getSemicolonIncluded()
	{
		// we need the 1st bit
		return (typeFlags & 0x1) == 1;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseBaseNode#hashCode()
	 */
	@Override
	public int hashCode()
	{
		if (fHash == -1)
		{
			int hash = getNodeType();
			hash = 31 * hash + (getSemicolonIncluded() ? 1 : 0);
			hash = 31 * hash + Arrays.hashCode(getChildren());
			fHash = hash;
		}
		return fHash;
	}

	@Override
	public void addChild(IParseNode child)
	{
		super.addChild(child);
		fHash = -1;
	}

	@Override
	public void setChildren(IParseNode[] children)
	{
		super.setChildren(children);
		fHash = -1;
	}

	@Override
	public void replaceChild(int index, IParseNode child) throws IndexOutOfBoundsException
	{
		super.replaceChild(index, child);
		fHash = -1;
	}

	@Override
	public void trimToSize()
	{
		super.trimToSize();
		fHash = -1;
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
		this.fDoc = comment;
		setDocType(PRE_DOC);
	}

	/**
	 * Post documentation (i.e.: vsdoc format).
	 * 
	 * @param comment
	 */
	public void setPostDocumentation(Symbol comment)
	{
		this.fDoc = comment;
		setDocType(POST_DOC);
	}

	/**
	 * setDocumentation
	 * 
	 * @param block
	 */
	public void setDocumentation(DocumentationBlock block)
	{
		this.fDoc = block;
		setDocType(DOC_BLOCK);
	}

	private void setDocType(short docBlock)
	{
		typeFlags |= ((docBlock & DOC_TYPE_MASK) << 1);
	}

	/**
	 * setType
	 * 
	 * @param type
	 */
	protected void setNodeType(short type)
	{
		typeFlags |= (type << 3);
		fHash = -1;
	}

	/**
	 * setSemicolonIncluded
	 * 
	 * @param included
	 */
	public void setSemicolonIncluded(boolean included)
	{
		typeFlags |= (included ? 0x1 : 0x0);
		fHash = -1;
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
