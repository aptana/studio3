/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

// copied directly from org.eclipse.jface.internal.text.link.contentassist.HTML2TextReader;

package com.aptana.editor.common.contentassist;


import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Reads the text contents from a reader of HTML contents and translates
 * the tags or cut them out.
 */
class HTML2TextReader extends SubstitutionTextReader {

	private static final String EMPTY_STRING = ""; 
	private static final Map<String, String> fgEntityLookup;
	private static final Set<String> fgTags;

	static {

		fgTags= new HashSet<String>();
		fgTags.add("b");  //$NON-NLS-1$
		fgTags.add("i"); //$NON-NLS-1$
		fgTags.add("br"); //$NON-NLS-1$
		fgTags.add("h2"); //$NON-NLS-1$
		fgTags.add("h3"); //$NON-NLS-1$
		fgTags.add("h5"); //$NON-NLS-1$
		fgTags.add("p"); //$NON-NLS-1$
		fgTags.add("dl"); //$NON-NLS-1$
		fgTags.add("dt"); //$NON-NLS-1$
		fgTags.add("dd"); //$NON-NLS-1$
		fgTags.add("li"); //$NON-NLS-1$
		fgTags.add("ul"); //$NON-NLS-1$
		fgTags.add("pre"); //$NON-NLS-1$
		fgTags.add("code"); //$NON-NLS-1$
		fgTags.add("script"); //$NON-NLS-1$
		fgTags.add("property"); //$NON-NLS-1$
		fgTags.add("var"); //$NON-NLS-1$
		fgTags.add("method"); //$NON-NLS-1$
		fgTags.add("specification"); //$NON-NLS-1$
		
		fgEntityLookup= new HashMap<String, String>(8);
		fgEntityLookup.put("lt", "<"); //$NON-NLS-1$ //$NON-NLS-2$
		fgEntityLookup.put("gt", ">"); //$NON-NLS-1$ //$NON-NLS-2$
		fgEntityLookup.put("nbsp", " "); //$NON-NLS-1$ //$NON-NLS-2$
		fgEntityLookup.put("amp", "&"); //$NON-NLS-1$ //$NON-NLS-2$
		fgEntityLookup.put("circ", "^"); //$NON-NLS-1$ //$NON-NLS-2$
		fgEntityLookup.put("tilde", "~"); //$NON-NLS-2$ //$NON-NLS-1$
		fgEntityLookup.put("quot", "\"");		 //$NON-NLS-1$ //$NON-NLS-2$
		fgEntityLookup.put("rarr", "?");		 //$NON-NLS-1$ //$NON-NLS-2$
	}

	private int fCounter= 0;
	private TextPresentation fTextPresentation;
	private int fBold= 0;
	private int fItalic = 0;
	private int fHeader = 0;
	private int fCode = 0;
	private int fPre = 0;
	private int fStartOffset= -1;
	private int fStartItalicOffset= -1;
	private int fStartHeaderOffset= -1;
	private int fStartCodeOffset= -1;
	private int fStartPreOffset= -1;

	private boolean fIsPreformattedText= false;
	private String fPreviousTag = "";

	private Color fLinkColor = null;
	private Color fPreColor = null;
	
	/**
	 * Transforms the HTML text from the reader to formatted text.
	 * 
	 * @param reader the reader
	 * @param presentation If not <code>null</code>, formattings will be applied to
	 * the presentation.
	 * @param display 
	*/
	public HTML2TextReader(Reader reader, TextPresentation presentation, Display display) {
		super(new PushbackReader(reader));
		fTextPresentation= presentation;
		
		RGB blackColor = new RGB(0, 0, 0);
		RGB linkColor = new RGB(93, 117, 215);
		RGB preColor = new RGB(204, 153, 51);

		ColorRegistry cm = JFaceResources.getColorRegistry();
		cm.put("black", blackColor); //$NON-NLS-1$
		cm.put("link", linkColor); //$NON-NLS-1$
		cm.put("pre", preColor); //$NON-NLS-1$
		
		fLinkColor = cm.get("link"); //$NON-NLS-1$
		fPreColor = cm.get("pre"); //$NON-NLS-1$
	}

	/**
	 * @see java.io.Reader#read()
	 */
	public int read() throws IOException {
		int c= super.read();
		if (c != -1)
		{
			++ fCounter;
		}
		return c;
	}

	/**
	 * startBold
	 */
	protected void startBold() {
		if (fBold == 0)
		{
			fStartOffset= fCounter;
		}
		++ fBold;
	}

	/**
	 * startItalic
	 */
	protected void startItalic() {
		if (fItalic == 0)
		{
			fStartItalicOffset = fCounter;
		}
		++ fItalic;
	}

	/**
	 * startHeader
	 */
	protected void startHeader() {
		if (fHeader == 0)
		{
			fStartHeaderOffset= fCounter;
		}
		++ fHeader;
	}

	/**
	 * startCode
	 */
	protected void startCode() {
		if (fCode == 0)
		{
			fStartCodeOffset= fCounter;
		}
		++ fCode;
	}

	/**
	 * startPre
	 */
	protected void startPre() {
		if (fPre == 0)
		{
			fStartPreOffset= fCounter;
		}
		++ fPre;
	}

	/**
	 * startPreformattedText
	 */
	protected void startPreformattedText() {
		fIsPreformattedText= true;
		setSkipWhitespace(false);
	}

	/**
	 * stopPreformattedText
	 */
	protected void stopPreformattedText() {
		fIsPreformattedText= false;
		setSkipWhitespace(true);
	}

	/**
	 * stopBold
	 */
	protected void stopBold() {
		-- fBold;
		if (fBold == 0) {
			if (fTextPresentation != null) {
				fTextPresentation.addStyleRange(new StyleRange(fStartOffset, fCounter - fStartOffset, null, null, SWT.BOLD));
			}
			fStartOffset= -1;
		}
	}
	
	/**
	 * stopItalic
	 */
	protected void stopItalic() {
		-- fItalic;
		if (fItalic == 0) {
			if (fTextPresentation != null) {
				fTextPresentation.addStyleRange(new StyleRange(fStartItalicOffset, fCounter - fStartItalicOffset, null, null, SWT.ITALIC));
			}
			fStartItalicOffset= -1;
		}
	}

	/**
	 * stopHeader
	 */
	protected void stopHeader() {
		-- fHeader;
		if (fHeader == 0) {
			if (fTextPresentation != null) {
				fTextPresentation.addStyleRange(new StyleRange(fStartHeaderOffset, fCounter - fStartHeaderOffset, null, null, SWT.BOLD));
			}
			fStartHeaderOffset= -1;
		}
	}

	/**
	 * stopCode
	 */
	protected void stopCode() {
		-- fCode;
		if (fHeader == 0) {
			if (fTextPresentation != null) {
				fTextPresentation.addStyleRange(new StyleRange(fStartCodeOffset, fCounter - fStartCodeOffset, fLinkColor, null, SWT.BOLD));
			}
			fStartCodeOffset= -1;
		}
	}

	/**
	 * stopPre
	 */
	protected void stopPre() {
		-- fPre;
		if (fPre == 0) {
			if (fTextPresentation != null) {
				fTextPresentation.addStyleRange(new StyleRange(fStartPreOffset, fCounter - fStartPreOffset, fPreColor, null, SWT.NORMAL));
			}
			fStartCodeOffset= -1;
		}
	}
	
	/**
	 * @see com.aptana.ide.editors.unified.utils.SubstitutionTextReader#computeSubstitution(int)
	 */
	protected String computeSubstitution(int c) throws IOException {

		if (c == '<')
		{
			return  processHTMLTag();
		}
		else if (c == '&')
		{
			return processEntity();
		}
		else if (fIsPreformattedText)
		{
			return processPreformattedText(c);
		}

		return null;
	}

	private String html2Text(String html) {

		if (html == null || html.length() == 0)
		{
			return EMPTY_STRING;
		}

		String tag = html;
		String lastTag = fPreviousTag;
		fPreviousTag = tag;
		
		if ('/' == tag.charAt(0))
		{
			tag= tag.substring(1);
		}

		if (!fgTags.contains(tag))
		{
			return EMPTY_STRING;
		}


		if ("pre".equals(html)) { //$NON-NLS-1$
			startPreformattedText();
			startPre();
			return LINE_DELIM;
		}

		if ("/pre".equals(html)) { //$NON-NLS-1$
			stopPreformattedText();
			stopPre();
			return LINE_DELIM;
		}

		if ("script".equals(html)) { //$NON-NLS-1$
			return "<script>"; //$NON-NLS-1$
		}

		if ("/script".equals(html)) { //$NON-NLS-1$
			return "</script>"; //$NON-NLS-1$
		}
		
		if (fIsPreformattedText)
		{
			return EMPTY_STRING;
		}
		
		if ("b".equals(html)) { //$NON-NLS-1$
			startBold();
			return EMPTY_STRING;
		}

		if ("code".equals(html) || "property".equals(html) || "var".equals(html) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				|| "method".equals(html) || "specification".equals(html)) { //$NON-NLS-1$ //$NON-NLS-2$
			startCode();
			return EMPTY_STRING;
		}
		
		if ("i".equals(html)) { //$NON-NLS-1$
			startItalic();
			return EMPTY_STRING;
		}

		if ("h5".equals(html) || "dt".equals(html)) { //$NON-NLS-1$ //$NON-NLS-2$
			startBold();
			return EMPTY_STRING;
		}

		if ("h2".equals(html)) { //$NON-NLS-1$ 
			startBold();
			if("/p".equals(lastTag) || "/pre".equals(lastTag)) //$NON-NLS-1$ //$NON-NLS-2$
			{
				return LINE_DELIM;
			}
			else
			{
				return LINE_DELIM + LINE_DELIM;
			}
		}

		if ("h3".equals(html)) { //$NON-NLS-1$ 
			startHeader();
			if("/p".equals(lastTag) || "/pre".equals(lastTag)) //$NON-NLS-1$ //$NON-NLS-2$
			{
				return LINE_DELIM;
			}
			else
			{
				return LINE_DELIM + LINE_DELIM;
			}
		}

		if ("dl".equals(html)) //$NON-NLS-1$
		{
			return LINE_DELIM;
		}

		if ("dd".equals(html)) //$NON-NLS-1$
		{
			return "\t"; //$NON-NLS-1$
		}

		if ("li".equals(html)) {  //$NON-NLS-1$
			// FIXME: this hard-coded prefix does not work for RTL languages, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=91682
			return LINE_DELIM + "\t"; //$NON-NLS-1$ 
		}

		if ("/b".equals(html)) { //$NON-NLS-1$
			stopBold();
			return EMPTY_STRING;
		}

		if ("/code".equals(html) || "/property".equals(html) || "/var".equals(html) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				|| "/method".equals(html) || "/specification".equals(html)) { //$NON-NLS-1$ //$NON-NLS-2$
			stopCode();
			return EMPTY_STRING;
		}

		if ("/i".equals(html)) { //$NON-NLS-1$
			stopItalic();
			return EMPTY_STRING;
		}

		if ("p".equals(html))  { //$NON-NLS-1$
			return LINE_DELIM;
		}

		if ("br".equals(html)) //$NON-NLS-1$
		{
			return LINE_DELIM;
		}

		if ("/p".equals(html))  { //$NON-NLS-1$
			if("/pre".equals(lastTag)) //$NON-NLS-1$
			{
				return EMPTY_STRING;
			}
			else
			{
				return LINE_DELIM;
			}
		}

		if ("/h5".equals(html) || "/dt".equals(html)) { //$NON-NLS-1$ //$NON-NLS-2$
			stopBold();
			return LINE_DELIM;
		}

		if ("/h2".equals(html)) { //$NON-NLS-1$ 
			stopBold();
			return LINE_DELIM;
		}
		
		if ("/h3".equals(html)) { //$NON-NLS-1$ 
			stopHeader();
			return EMPTY_STRING;
		}
		
		if ("/dd".equals(html)) //$NON-NLS-1$
		{
			return LINE_DELIM;
		}

		return EMPTY_STRING;
	}

	/*
	 * A '<' has been read. Process a HTML tag
	 */
	private String processHTMLTag() throws IOException {

		StringBuffer buf= new StringBuffer();
		int ch;
		do {

			ch= nextChar();

			while (ch != -1 && ch != '>') {
				buf.append(Character.toLowerCase((char) ch));
				ch= nextChar();
				if (ch == '"'){
					buf.append(Character.toLowerCase((char) ch));
					ch= nextChar();
					while (ch != -1 && ch != '"'){
						buf.append(Character.toLowerCase((char) ch));
						ch= nextChar();
					}
				}
				if (ch == '<'){
					unread(ch);
					return '<' + buf.toString();
				}
			}

			if (ch == -1)
			{
				return null;
			}

			int tagLen= buf.length();
			// needs special treatment for comments
			if ((tagLen >= 3 && "!--".equals(buf.substring(0, 3))) //$NON-NLS-1$
				&& !(tagLen >= 5 && "--".equals(buf.substring(tagLen - 2)))) { //$NON-NLS-1$
				// unfinished comment
				buf.append(ch);
			} else {
				break;
			}
		} while (true);

		return html2Text(buf.toString());
	}

	private String processPreformattedText(int c) {
		// [IM] Line delimiter is two chars on windows. We only appear to need to do
		// this step on windows.
		if  ((c == '\r' || c == '\n') && LINE_DELIM.length() == 2)
		{
			fCounter++;
		}
		return null;
	}


	private void unread(int ch) throws IOException {
		((PushbackReader) getReader()).unread(ch);
	}

	/**
	 * entity2Text
	 *
	 * @param symbol
	 * @return String
	 */
	protected String entity2Text(String symbol) {
		if (symbol.length() > 1 && symbol.charAt(0) == '#') {
			int ch;
			try {
				if (symbol.charAt(1) == 'x') {
					ch= Integer.parseInt(symbol.substring(2), 16);
				} else {
					ch= Integer.parseInt(symbol.substring(1), 10);
				}
				return EMPTY_STRING + (char)ch;
			} catch (NumberFormatException e) {
			}
		} else {
			String str= fgEntityLookup.get(symbol);
			if (str != null) {
				return str;
			}
		}
		return "&" + symbol; // not found //$NON-NLS-1$
	}

	/*
	 * A '&' has been read. Process a entity
	 */
	private String processEntity() throws IOException {
		StringBuffer buf= new StringBuffer();
		int ch= nextChar();
		while (Character.isLetterOrDigit((char)ch) || ch == '#') {
			buf.append((char) ch);
			ch= nextChar();
		}

		if (ch == ';')
		{
			return entity2Text(buf.toString());
		}

		buf.insert(0, '&');
		if (ch != -1)
		{
			buf.append((char) ch);
		}
		return buf.toString();
	}
}