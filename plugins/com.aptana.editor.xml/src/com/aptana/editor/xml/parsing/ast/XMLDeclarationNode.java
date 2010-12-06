/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
