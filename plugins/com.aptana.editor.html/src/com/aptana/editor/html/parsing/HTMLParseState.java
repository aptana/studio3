package com.aptana.editor.html.parsing;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aptana.parsing.ParseState;

/**
 * @author Kevin Lindsey
 * @author Michael Xia
 */
public class HTMLParseState extends ParseState
{

	private static final String HTML_2_0 = "-//IETF//DTD HTML//EN"; //$NON-NLS-1$
	private static final String HTML_3_2 = "-//W3C//DTD HTML 3.2 Final//EN"; //$NON-NLS-1$
	private static final String HTML_4_0_1_STRICT = "-//W3C//DTD HTML 4.01//EN"; //$NON-NLS-1$
	private static final String HTML_4_0_1_TRANSITIONAL = "-//W3C//DTD HTML 4.01 Transitional//EN"; //$NON-NLS-1$
	private static final String HTML_4_0_1_FRAMESET = "-//W3C//DTD HTML 4.01 Frameset//EN"; //$NON-NLS-1$
	private static final String XHTML_1_0_STRICT = "-//W3C//DTD XHTML 1.0 Strict//EN"; //$NON-NLS-1$
	private static final String XHTML_1_0_TRANSITIONAL = "-//W3C//DTD XHTML 1.0 Transitional//EN"; //$NON-NLS-1$
	private static final String XHTML_1_0_FRAMESET = "-//W3C//DTD XHTML 1.0 Frameset//EN"; //$NON-NLS-1$
	private static final String XHTML_1_1_STRICT = "-//W3C//DTD XHTML 1.1//EN"; //$NON-NLS-1$

	private static final Pattern DOCTYPE_PATTERN = Pattern
			.compile("<!DOCTYPE\\s+(\\S+)\\s+PUBLIC\\s+((?:'[^']+')|(?:\"[^\"]+\"))(?:\\s+((?:'[^']+')|(?:\"[^\"]+\")))?"); //$NON-NLS-1$
	@SuppressWarnings("nls")
	private static final String[] END_OPTIONAL_TAGS = { "body", "colgroup", "dd", "dt", "area", "html", "li", "option",
			"p", "tbody", "td", "tfoot", "th", "thead", "tr" };
	@SuppressWarnings("nls")
	private static final String[] END_FORBIDDEN_OR_EMPTY_TAGS = { "area", "base", "basefont", "br", "col", "frame",
			"hr", "img", "input", "isindex", "link", "meta", "param", };

	private static Map<String, Integer> fDocTypes;
	private static Map<String, Integer> fEndTagInfo;

	private int fDocumentType;
	private String fRootElement;
	private String fPubId;
	private String fSystem;

	/**
	 * static constructor
	 */
	static
	{
		fDocTypes = new HashMap<String, Integer>();
		fDocTypes.put(HTML_2_0, HTMLDocumentType.HTML_2_0);
		fDocTypes.put(HTML_3_2, HTMLDocumentType.HTML_3_2);
		fDocTypes.put(HTML_4_0_1_STRICT, HTMLDocumentType.HTML_4_0_1_STRICT);
		fDocTypes.put(HTML_4_0_1_TRANSITIONAL, HTMLDocumentType.HTML_4_0_1_TRANSITIONAL);
		fDocTypes.put(HTML_4_0_1_FRAMESET, HTMLDocumentType.HTML_4_0_1_FRAMESET);
		fDocTypes.put(XHTML_1_0_STRICT, HTMLDocumentType.XHTML_1_0_STRICT);
		fDocTypes.put(XHTML_1_0_TRANSITIONAL, HTMLDocumentType.XHTML_1_0_TRANSITIONAL);
		fDocTypes.put(XHTML_1_0_FRAMESET, HTMLDocumentType.XHTML_1_0_FRAMESET);
		fDocTypes.put(XHTML_1_1_STRICT, HTMLDocumentType.XHTML_1_1_STRICT);

		fEndTagInfo = new HashMap<String, Integer>();
		for (String tag : END_OPTIONAL_TAGS)
		{
			fEndTagInfo.put(tag, HTMLTagInfo.END_OPTIONAL);
		}
		for (String tag : END_FORBIDDEN_OR_EMPTY_TAGS)
		{
			fEndTagInfo.put(tag, HTMLTagInfo.END_FORBIDDEN | HTMLTagInfo.EMPTY);
		}
	}

	public HTMLParseState()
	{
	}

	public int getDocumentType()
	{
		return fDocumentType;
	}

	public String getRootElement()
	{
		return fRootElement;
	}

	public String getPubId()
	{
		return fPubId;
	}

	public String getSystem()
	{
		return fSystem;
	}

	/**
	 * @param tagName
	 *            the name of the tag
	 * @return the closing type that the tag has
	 */
	public int getCloseTagType(String tagName)
	{
		if (fDocumentType < HTMLDocumentType.XHTML_1_0_STRICT)
		{
			String key = tagName.toLowerCase();
			if (fEndTagInfo.containsKey(key))
			{
				return fEndTagInfo.get(key) & HTMLTagInfo.END_MASK;
			}
		}
		return HTMLTagInfo.END_REQUIRED;
	}

	/**
	 * @param tagName
	 *            the name of the tag
	 * @return true if the tag is of the empty type, false otherwise
	 */
	public boolean isEmptyTagType(String tagName)
	{
		String key = tagName.toLowerCase();
		if (fEndTagInfo.containsKey(key))
		{
			return (fEndTagInfo.get(key) & HTMLTagInfo.EMPTY) == HTMLTagInfo.EMPTY;
		}
		return false;
	}

	@Override
	public void setEditState(String source, String insertedText, int startingOffset, int removedLength)
	{
		super.setEditState(source, insertedText, startingOffset, removedLength);

		// assumes we don't know the document type
		int documentType = HTMLDocumentType.OTHER;
		int indexOf = source.indexOf("<!DOCTYPE");//$NON-NLS-1$
		if (indexOf > -1)
		{
			Matcher match = DOCTYPE_PATTERN.matcher(source.substring(indexOf));

			if (match.find())
			{
				// grabs various pieces of the doctype string
				fRootElement = match.group(1);
				fPubId = match.group(2);
				fSystem = match.group(3);

				// strips opening and closing quotes
				fPubId = fPubId.substring(1, fPubId.length() - 1);
				if (fSystem != null && fSystem.length() > 0)
				{
					fSystem = fSystem.substring(1, fSystem.length() - 1);
				}

				// sees if we could determine the document type
				if (fRootElement.equals("html") || fRootElement.equals("HTML")) //$NON-NLS-1$ //$NON-NLS-2$
				{
					if (fDocTypes.containsKey(fPubId))
					{
						documentType = fDocTypes.get(fPubId);
					}
				}
			}
		}
		fDocumentType = documentType;
	}
}
