/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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
package com.aptana.editor.css.internal;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.aptana.editor.common.CommonAutoIndentStrategy;

/**
 * @author Ingo Muschenetz
 * @author Michael Xia (mxia@aptana.com)
 */
public class CSSCommentIndentStrategy extends CommonAutoIndentStrategy {

    private String fPartitioning;

    public CSSCommentIndentStrategy(String partitioning, String contentType,
            SourceViewerConfiguration configuration, ISourceViewer sourceViewer) {
        super(contentType, configuration, sourceViewer);
        fPartitioning = partitioning;
    }

    @Override
    public void customizeDocumentCommand(IDocument document, DocumentCommand command) {
        if (command.length == 0 && command.text != null && isLineDelimiter(document, command.text)) {
            autoIndentAfterNewLine(document, command);
        }
    }

    /**
     * Copies the indentation of the previous line and adds a star. If the
     * comment just started on this line, adds standard method tags and close
     * the comment.
     * 
     * @param d
     *            the document to work on
     * @param c
     *            the command to deal with
     */
    @Override
    protected void autoIndentAfterNewLine(IDocument d, DocumentCommand c) {
        int offset = c.offset;
        if (offset == -1 || d.getLength() == 0) {
            return;
        }

        try {
            int p = (offset == d.getLength() ? offset - 1 : offset);
            IRegion line = d.getLineInformationOfOffset(p);

            int lineOffset = line.getOffset();
            int firstNonWS = findEndOfWhiteSpace(d, lineOffset, offset);

            // find out if this is a return after a */ (in which case only add
            // an indent, not a *)
            if (indentCloseToken(d, c, offset, lineOffset, firstNonWS)) {
                return;
            }

            // find out if this is a // style single line comment
            if (d.getLength() > firstNonWS + 1 && d.getChar(firstNonWS + 1) == '/') {
                super.autoIndentAfterNewLine(d, c);
                return;
            }

            StringBuilder buf = new StringBuilder(c.text);

            IRegion prefix = findPrefixRange(d, line);
            String indentation = d.get(prefix.getOffset(), prefix.getLength());
            int lengthToAdd = Math.min(offset - prefix.getOffset(), prefix.getLength());
            buf.append(indentation.substring(0, lengthToAdd));

            if (firstNonWS < offset) {
                if (d.getChar(firstNonWS) == '/') {
                    // comment started on this line
                    buf.append(" * "); //$NON-NLS-1$

                    if (isNewComment(d, offset)) {
                        c.shiftsCaret = false;
                        c.caretOffset = c.offset + buf.length();
                        String lineDelimiter = TextUtilities.getDefaultLineDelimiter(d);
                        String endTag = lineDelimiter + indentation + " */"; //$NON-NLS-1$

                        // guard for end of doc (comment at the very end of doc)
                        if (d.getLength() > firstNonWS + 2 && d.getChar(firstNonWS + 1) == '*') {
                            // we need to close the comment
                            d.replace(offset, 0, endTag);
                        } else {
                            buf.append(endTag);
                        }
                    }
                }
            }

            // move the caret behind the prefix, even if we do not have to
            // insert it
            if (lengthToAdd < prefix.getLength()) {
                c.caretOffset = offset + prefix.getLength() - lengthToAdd;
            }
            c.text = buf.toString();
        } catch (BadLocationException excp) {
            // stops work
        }
    }

    private boolean indentCloseToken(IDocument doc, DocumentCommand c, int offset, int lineOffset,
            int firstNonWS) {
        boolean isClose = false;
        if (doc.getLength() < 2 || offset < 2) {
            isClose = true;
        } else {
            try {
                if (doc.getChar(offset - 1) == '/' && doc.getChar(offset - 2) == '*') {
                    isClose = true;
                }
            } catch (BadLocationException e) {
            }
        }

        if (isClose) {
            String append = getIndentationString(doc, lineOffset, firstNonWS);
            // multiline comments indent with "space *" after the first, so
            // trims that if it is there
            if (append.endsWith(" ")) { //$NON-NLS-1$
                append = append.substring(0, append.length() - 1);
            }
            c.text += append;
            return true;
        }
        return false;
    }

    /**
     * Returns the range of the multiline comment prefix on the given line in
     * <code>document</code>. The prefix greedily matches the following regex
     * pattern: <code>\w*\*\w*</code>, i.e, any number of whitespace characters,
     * followed by an asterix ('*'), followed by any number of whitespace
     * characters.
     * 
     * @param document
     *            the document to which <code>line</code> refers
     * @param line
     *            the line from which to extract the prefix range
     * 
     * @return an <code>IRegion</code> describing the range of the prefix on the
     *         given line
     * @throws BadLocationException
     *             if accessing the document fails
     */
    private IRegion findPrefixRange(IDocument document, IRegion line) throws BadLocationException {
        int lineOffset = line.getOffset();
        int lineEnd = lineOffset + line.getLength();
        int indentEnd = findEndOfWhiteSpace(document, lineOffset, lineEnd);
        if (indentEnd < lineEnd && document.getChar(indentEnd) == '*') {
            indentEnd++;
            while (indentEnd < lineEnd && document.getChar(indentEnd) == ' ')
                indentEnd++;
        }
        return new Region(lineOffset, indentEnd - lineOffset);
    }

    /**
     * Guesses if the command operates within a newly created comment or not. If
     * in doubt, it will assume that the comment is new.
     * 
     * @param document
     *            the document
     * @param commandOffset
     *            the command offset
     * 
     * @return <code>true</code> if the comment should be closed,
     *         <code>false</code> if not
     */
    private boolean isNewComment(IDocument document, int commandOffset) {
        try {
            int lineIndex = document.getLineOfOffset(commandOffset) + 1;
            if (lineIndex >= document.getNumberOfLines()) {
                return true;
            }

            IRegion line = document.getLineInformation(lineIndex);
            ITypedRegion partition = TextUtilities.getPartition(document, fPartitioning,
                    commandOffset, false);
            // uses "- 1" because partitions have overlaps in eclipse
            int partitionEnd = partition.getOffset() + partition.getLength() - 1;
            if (line.getOffset() >= partitionEnd) {
                return true;
            }

            String comment = document.get(partition.getOffset(), partition.getLength());

            // comments that don't end with */ are certainly not closed
            if (!comment.endsWith("*/")) { //$NON-NLS-1$
                return true;
            }
            // assume short comment always unclosed and guard for next test
            if (comment.length() < 4) {
                return true;
            }
            if (comment.startsWith("/**/")) { //$NON-NLS-1$
                return false;
            }

            int firstNewline = comment.indexOf('\n');
            if (firstNewline > -1 && firstNewline <= comment.length()) {
                // comments that have * as the first non-whitespace char on next
                // line are probably closed
                String subComment = comment.substring(firstNewline).trim();
                if (subComment.startsWith("*")) { //$NON-NLS-1$
                    return false;
                }

                // no extra lines means probably not closed (can be a */ line
                // due to previous test)
                if (subComment.indexOf("\n") == -1) { //$NON-NLS-1$
                    return true;
                }
            }

            if (comment.indexOf("/*", 2) != -1) { //$NON-NLS-1$
                // enclosed another comment -> probably a new comment
                return true;
            }
        } catch (BadLocationException e) {
        }
        return false;
    }
}
