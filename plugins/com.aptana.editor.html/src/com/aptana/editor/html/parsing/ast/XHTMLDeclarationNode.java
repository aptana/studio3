package com.aptana.editor.html.parsing.ast;

/**
 * This contains XHTML declaration tag.
 */
public class XHTMLDeclarationNode extends HTMLNode
{

	private String fVersion;
	private String fEncoding;
	private String fStandalone;

	private String fText;

	public XHTMLDeclarationNode(String version, int start, int end)
	{
		super(HTMLNodeTypes.DECLARATION, start, end);
		fVersion = version;
	}

	public XHTMLDeclarationNode(String version, String encoding, String standalone, int start, int end)
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
