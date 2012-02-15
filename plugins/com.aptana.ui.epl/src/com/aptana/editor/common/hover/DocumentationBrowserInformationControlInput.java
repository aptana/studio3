package com.aptana.editor.common.hover;

import org.eclipse.jface.internal.text.html.BrowserInformationControlInput;

import com.aptana.core.util.StringUtil;

/**
 * Browser input for documentation hovers.
 */
@SuppressWarnings("restriction")
public class DocumentationBrowserInformationControlInput extends BrowserInformationControlInput
{

	private final Object fElement;
	private final String fHtml;
	private final int fLeadingImageWidth;

	/**
	 * Creates a new browser information control input.
	 * 
	 * @param previous
	 *            previous input, or <code>null</code> if none available
	 * @param element
	 *            the element, or <code>null</code> if none available
	 * @param html
	 *            HTML contents, must not be null
	 * @param leadingImageWidth
	 *            the indent required for the element image
	 */
	public DocumentationBrowserInformationControlInput(DocumentationBrowserInformationControlInput previous,
			Object element, String html, int leadingImageWidth)
	{
		super(previous);
		fElement = element;
		fHtml = html;
		fLeadingImageWidth = leadingImageWidth;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.internal.text.html.BrowserInformationControlInput#getLeadingImageWidth()
	 */
	public int getLeadingImageWidth()
	{
		return fLeadingImageWidth;
	}

	/**
	 * Returns the Java element.
	 * 
	 * @return the element or <code>null</code> if none available
	 */
	public Object getElement()
	{
		return fElement;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.internal.text.html.BrowserInformationControlInput#getHtml()
	 */
	public String getHtml()
	{
		return fHtml;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.internal.text.html.BrowserInput#getInputElement()
	 */
	public Object getInputElement()
	{
		return fElement == null ? (Object) fHtml : fElement;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.internal.text.html.BrowserInput#getInputName()
	 */
	public String getInputName()
	{
		return StringUtil.EMPTY;
	}

}
