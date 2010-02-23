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

	public XMLDeclarationNode(String version, int start, int end)
	{
		super(XMLNodeTypes.DECLARATION.getIndex(), start, end);
		fVersion = version;
	}

	public XMLDeclarationNode(String version, String encoding, String standalone, int start, int end)
	{
		this(version, start, end);
		fEncoding = encoding;
		fStandalone = standalone;
	}

	public String getVersion()
	{
		return fVersion;
	}

	public String getEncoding()
	{
		return fEncoding;
	}

	public String getStandalone()
	{
		return fStandalone;
	}

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
		return fVersion.equals(other.fVersion) && fEncoding.equals(other.fEncoding)
				&& fStandalone.equals(other.fStandalone);
	}

	@Override
	public int hashCode()
	{
		int hash = super.hashCode();
		hash = 31 * hash + fVersion == null ? 0 : fVersion.hashCode();
		hash = 31 * hash + fEncoding == null ? 0 : fEncoding.hashCode();
		hash = 31 * hash + fStandalone == null ? 0 : fStandalone.hashCode();
		return hash;
	}

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
