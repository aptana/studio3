/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.parsing.ast;

/**
 * This contains XML declaration tag.
 */
public class XMLDeclarationNode extends XMLNode
{
	private String fVersion;
	private String fEncoding;
	private String fStandalone;
	private String fText;

	/**
	 * XMLDeclarationNode
	 * 
	 * @param version
	 * @param start
	 * @param end
	 */
	public XMLDeclarationNode(String version, int start, int end)
	{
		super(XMLNodeType.DECLARATION, start, end);

		fVersion = version;
	}

	/**
	 * XMLDeclarationNode
	 * 
	 * @param version
	 * @param encoding
	 * @param standalone
	 * @param start
	 * @param end
	 */
	public XMLDeclarationNode(String version, String encoding, String standalone, int start, int end)
	{
		this(version, start, end);

		fEncoding = encoding;
		fStandalone = standalone;
	}

	/**
	 * getVersion
	 * 
	 * @return
	 */
	public String getVersion()
	{
		return fVersion;
	}

	/**
	 * getEncoding
	 * 
	 * @return
	 */
	public String getEncoding()
	{
		return fEncoding;
	}

	/**
	 * getStandalong
	 * 
	 * @return
	 */
	public String getStandalone()
	{
		return fStandalone;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (!super.equals(obj))
		{
			return false;
		}

		if (!(obj instanceof XMLDeclarationNode))
		{
			return false;
		}

		XMLDeclarationNode other = (XMLDeclarationNode) obj;

		if (fVersion == null && other.fVersion != null)
		{
			return false;
		}
		if (fEncoding == null && other.fEncoding != null)
		{
			return false;
		}
		if (fStandalone == null && other.fStandalone != null)
		{
			return false;
		}

		return fVersion.equals(other.fVersion) && fEncoding.equals(other.fEncoding) && fStandalone.equals(other.fStandalone);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = super.hashCode();

		hash = 31 * hash + fVersion == null ? 0 : fVersion.hashCode();
		hash = 31 * hash + fEncoding == null ? 0 : fEncoding.hashCode();
		hash = 31 * hash + fStandalone == null ? 0 : fStandalone.hashCode();

		return hash;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#toString()
	 */
	@Override
	public String toString()
	{
		if (fText == null)
		{
			StringBuilder text = new StringBuilder();

			text.append("<?xml"); //$NON-NLS-1$

			if (fVersion != null && fVersion.length() > 0)
			{
				text.append(" ").append(fVersion); //$NON-NLS-1$
			}

			if (fEncoding != null && fEncoding.length() > 0)
			{
				text.append(" ").append(fEncoding); //$NON-NLS-1$
			}

			if (fStandalone != null && fStandalone.length() > 0)
			{
				text.append(" ").append(fStandalone); //$NON-NLS-1$
			}

			text.append("?>"); //$NON-NLS-1$

			fText = text.toString();
		}

		return fText;
	}
}
