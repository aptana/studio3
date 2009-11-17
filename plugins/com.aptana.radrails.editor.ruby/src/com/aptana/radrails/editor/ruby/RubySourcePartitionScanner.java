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
package com.aptana.radrails.editor.ruby;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.jruby.CompatVersion;
import org.jruby.ast.CommentNode;
import org.jruby.ast.Node;
import org.jruby.common.NullWarnings;
import org.jruby.lexer.yacc.ISourcePosition;
import org.jruby.lexer.yacc.LexerSource;
import org.jruby.lexer.yacc.RubyYaccLexer;
import org.jruby.lexer.yacc.SyntaxException;
import org.jruby.lexer.yacc.RubyYaccLexer.LexState;
import org.jruby.parser.ParserConfiguration;
import org.jruby.parser.ParserSupport;
import org.jruby.parser.RubyParserResult;
import org.jruby.parser.Tokens;
import org.jruby.util.KCode;

import com.aptana.radrails.editor.common.SourceConfigurationPartitionScanner;

public class RubySourcePartitionScanner extends SourceConfigurationPartitionScanner {

    private static final String BEGIN = "=begin"; //$NON-NLS-1$

    private static class QueuedToken {
        private IToken token;
        private int length;
        private int offset;

        QueuedToken(IToken token, int offset, int length) {
            this.token = token;
            this.length = length;
            this.offset = offset;
        }

        public int getLength() {
            return length;
        }

        public int getOffset() {
            return offset;
        }

        public IToken getToken() {
            return token;
        }

        @Override
        public String toString() {
            StringBuilder text = new StringBuilder();
            text.append(getToken().getData());
            text.append(": offset: "); //$NON-NLS-1$
            text.append(getOffset());
            text.append(", length: "); //$NON-NLS-1$
            text.append(getLength());
            return text.toString();
        }
    }

    private RubyYaccLexer lexer;
    private ParserSupport parserSupport;
    private RubyParserResult result;
    private String fContents;
    private LexerSource lexerSource;
    private int origOffset;
    private int origLength;
    private int fLength;
    private int fOffset;

    private List<QueuedToken> fQueue = new ArrayList<QueuedToken>();
    private String fContentType = RubySourceConfiguration.DEFAULT;
    private boolean inSingleQuote;
    private String fOpeningString;

    public RubySourcePartitionScanner() {
        super(RubySourceConfiguration.getDefault());
        lexer = new RubyYaccLexer();
        parserSupport = new ParserSupport();
        ParserConfiguration config = new ParserConfiguration(KCode.NIL, 0, false,
                CompatVersion.RUBY1_8);
        config.setExtraPositionInformation(true);
        parserSupport.setConfiguration(config);
        result = new RubyParserResult();
        parserSupport.setResult(result);
        lexer.setParserSupport(parserSupport);
        lexer.setWarnings(new NullWarnings());
        lexer.setEncoding(config.getKCode().getEncoding());
    }

    @Override
    public void setPartialRange(IDocument document, int offset, int length, String contentType,
            int partitionOffset) {
        reset();
        int myOffset = offset;
        if (contentType != null) {
            int diff = offset - partitionOffset;
            // backtrack to beginning of partition so we don't get in weird
            // state
            myOffset = partitionOffset;
            length += diff;
            this.fContentType = contentType;
        }
        if (myOffset == -1)
            myOffset = 0;
        ParserConfiguration config = new ParserConfiguration(KCode.NIL, 0, true, false,
                CompatVersion.RUBY1_8);
        try {
            fContents = document.get(myOffset, length);
            lexerSource = LexerSource.getSource("filename", new StringReader(fContents), null, //$NON-NLS-1$
                    config);
            lexer.setSource(lexerSource);
        } catch (BadLocationException e) {
            lexerSource = LexerSource.getSource("filename", new StringReader(""), null, config); //$NON-NLS-1$ //$NON-NLS-2$
            lexer.setSource(lexerSource);
        }
        origOffset = myOffset;
        origLength = length;
    }

    @Override
    public int getTokenLength() {
        return fLength;
    }

    @Override
    public int getTokenOffset() {
        return fOffset;
    }

    @Override
    public IToken nextToken() {
        if (!fQueue.isEmpty()) {
            return popTokenOffQueue();
        }
        fOffset = getOffset();
        fLength = 0;
        IToken returnValue = new Token(RubySourceConfiguration.DEFAULT);
        boolean isEOF = false;
        try {
            isEOF = !lexer.advance();
            if (isEOF) {
                returnValue = Token.EOF;
            } else {
                int lexerToken = lexer.token();
                if (!inSingleQuote && lexerToken == Tokens.tSTRING_DVAR) {
                    // we hit a single dynamic variable
                    addPoundToken();
                    scanDynamicVariable();
                    setLexerPastDynamicSectionOfString();
                    return popTokenOffQueue();
                } else if (!inSingleQuote && lexerToken == Tokens.tSTRING_DBEG) {
                    // if we hit dynamic code inside a string
                    addPoundBraceToken();
                    scanTokensInsideDynamicPortion();
                    addClosingBraceToken();
                    setLexerPastDynamicSectionOfString();
                    return popTokenOffQueue();
                } else if (lexerToken == Tokens.tSTRING_BEG) {
                    String opening = getUntrimmedOpeningString();
                    int endOfMarker = indexOf(opening.trim(), ", +)"); //$NON-NLS-1$
                    if (opening.trim().startsWith("<<") && endOfMarker != -1) { //$NON-NLS-1$
                        adjustOffset(opening);
                        addHereDocStartToken(endOfMarker);
                        addCommaToken(endOfMarker);
                        scanRestOfLineAfterHeredocBegins(opening.trim(), endOfMarker);
                        setLexerPastHeredocBeginning(opening.trim());
                        return popTokenOffQueue();
                    }
                }
                returnValue = getToken(lexerToken);
            }
            List<CommentNode> comments = result.getCommentNodes();
            if (comments != null && !comments.isEmpty()) {
                parseOutComments(comments);
                // Queue the normal token we just ate up
                addQueuedToken(returnValue, isEOF);
                comments.clear();
                return popTokenOffQueue();
            }
        } catch (SyntaxException se) {
            if (se.getMessage().equals("embedded document meets end of file")) { //$NON-NLS-1$
                // Add to the queue (at end), then try to just do the rest of
                // the file...
                // TODO recover somehow by removing this chunk out of the
                // fContents?
                int start = se.getPosition().getStartOffset();
                int length = fContents.length() - start;
                QueuedToken qtoken = new QueuedToken(new Token(
                        RubySourceConfiguration.MULTI_LINE_COMMENT), start + origOffset, length);
                if (fOffset == origOffset) {
                    // If we never got to read in beginning contents
                    RubySourcePartitionScanner scanner = new RubySourcePartitionScanner();
                    String possible = new String(fContents.substring(0, start));
                    IDocument document = new Document(possible);
                    scanner.setRange(document, origOffset, possible.length());
                    IToken token;
                    while (!(token = scanner.nextToken()).isEOF()) {
                        push(new QueuedToken(token, scanner.getTokenOffset() + fOffset, scanner
                                .getTokenLength()));
                    }
                }
                push(qtoken);
                push(new QueuedToken(Token.EOF, start + origOffset + length, 0));
                return popTokenOffQueue();
            } else if (se.getMessage().equals("unterminated string meets end of file")) { //$NON-NLS-1$
                // Add to the queue (at end), then try to just do the rest of
                // the file...
                // TODO recover somehow by removing this chunk out of the
                // fContents?
                int start = se.getPosition().getStartOffset();
                int length = fContents.length() - start;
                QueuedToken qtoken = new QueuedToken(new Token(fContentType), start + origOffset,
                        length);
                if (fOffset == origOffset) {
                    // If we never got to read in beginning contents
                    RubySourcePartitionScanner scanner = new RubySourcePartitionScanner();
                    String possible = new String(fContents.substring(0, start));
                    IDocument document = new Document(possible);
                    scanner.setRange(document, origOffset, possible.length());
                    IToken token;
                    while (!(token = scanner.nextToken()).isEOF()) {
                        push(new QueuedToken(token, scanner.getTokenOffset() + fOffset, scanner
                                .getTokenLength()));
                    }
                }
                push(qtoken);
                push(new QueuedToken(Token.EOF, start + origOffset + length, 0));
                return popTokenOffQueue();
            }

            if (lexerSource.getOffset() - origLength == 0) {
                // return eof if we hit a problem found at end of parsing
                return Token.EOF;
            }
            fLength = getOffset() - fOffset;
            
            Assert.isTrue(fLength >= 0);
            return new Token(RubySourceConfiguration.DEFAULT);
        } catch (IOException e) {
            System.out.println(e);
        }
        if (!isEOF) {
            fLength = getOffset() - fOffset;
            Assert.isTrue(fLength >= 0);
        }
        return returnValue;
    }

    @Override
    public void setRange(IDocument document, int offset, int length) {
        setPartialRange(document, offset, length, RubySourceConfiguration.DEFAULT, 0);
    }

    private void reset() {
        lexer.reset();
        lexer.setState(LexState.EXPR_BEG);
        parserSupport.initTopLocalVariables();
        fQueue.clear();
        inSingleQuote = false;
    }

    private void setLexerPastHeredocBeginning(String rawBeginning) throws IOException {
        StringBuffer fakeContents = new StringBuffer();
        int toAdd = 1;
        if (rawBeginning.startsWith("<<-")) { //$NON-NLS-1$
            toAdd = 2;
        }
        int start = fOffset - (fOpeningString.length() + toAdd);
        for (int i = 0; i < start; i++) {
            fakeContents.append(" "); //$NON-NLS-1$
        }
        fakeContents.append("<<"); //$NON-NLS-1$
        if (rawBeginning.startsWith("<<-")) { //$NON-NLS-1$
            fakeContents.append("-"); //$NON-NLS-1$
        }
        fakeContents.append(fOpeningString.trim());
        if ((fOffset - origOffset) < origLength) {
            // BLAH removed + 1 from end here
            fakeContents.append(new String(fContents.substring((fOffset - origOffset))));
        }
        IDocument document = new Document(fakeContents.toString());
        List<QueuedToken> queueCopy = new ArrayList<QueuedToken>(fQueue);
        setPartialRange(document, start, fakeContents.length() - start, null, start);
        fQueue = new ArrayList<QueuedToken>(queueCopy);
        lexer.advance();
    }

    private void adjustOffset(String opening) {
        int index = opening.indexOf("<<"); //$NON-NLS-1$
        if (index > 0)
            setOffset(fOffset + index);
    }

    private int indexOf(String opening, String string) {
        String trimmed = opening.trim();
        int diff;
        if (trimmed.length() == 0) {
            diff = opening.length();
        } else {
            // Count leading whitespace
            diff = opening.indexOf(trimmed.charAt(0));
        }
        int lowest = -1;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            int value = trimmed.indexOf(c);
            if (value == -1)
                continue;
            value += diff;
            if (lowest == -1) {
                lowest = value;
                continue;
            }
            if (value < lowest)
                lowest = value;
        }
        return lowest;
    }

    private void scanRestOfLineAfterHeredocBegins(String opening, int index) {
        String possible = new String(opening.substring(index + 1));
        RubySourcePartitionScanner scanner = new RubySourcePartitionScanner();
        IDocument document = new Document(possible);
        scanner.setRange(document, 0, possible.length());
        IToken token;
        while (!(token = scanner.nextToken()).isEOF()) {
            push(new QueuedToken(token, scanner.getTokenOffset() + fOffset + index + 1, scanner
                    .getTokenLength()));
        }
        setOffset(fOffset + index + 1 + possible.length());
        if (scanner.fOpeningString != null && scanner.fOpeningString.endsWith("\n")) { //$NON-NLS-1$
            fOpeningString = scanner.fOpeningString;
        } else {
            String marker = new String(opening.substring(0, index).trim());
            fOpeningString = generateHeredocMarker(marker);
        }
        fContentType = RubySourceConfiguration.STRING;
    }

    private void addCommaToken(int index) {
        push(new QueuedToken(new Token(RubySourceConfiguration.DEFAULT), fOffset + index, 1));
    }

    private void addHereDocStartToken(int index) {
        push(new QueuedToken(new Token(RubySourceConfiguration.STRING), fOffset, index));
    }

    private void setOffset(int offset) {
        fOffset = offset;
    }

    private void addPoundToken() {
        addStringToken(1);// add token for the #
    }

    private void scanDynamicVariable() {
        // read until whitespace or '"'
        int whitespace = fContents.indexOf(' ', fOffset - origOffset);
        if (whitespace == -1)
            whitespace = Integer.MAX_VALUE;
        int doubleQuote = fContents.indexOf('"', fOffset - origOffset);
        if (doubleQuote == -1)
            doubleQuote = Integer.MAX_VALUE;
        int end = Math.min(whitespace, doubleQuote);
        // FIXME If we can't find whitespace or doubleQuote, we are pretty
        // screwed.
        String possible = null;
        if (end == -1) {
            possible = new String(fContents.substring(fOffset - origOffset));
        } else {
            possible = new String(fContents.substring(fOffset - origOffset, end));
        }
        RubySourcePartitionScanner scanner = new RubySourcePartitionScanner();
        IDocument document = new Document(possible);
        scanner.setRange(document, 0, possible.length());
        IToken token;
        while (!(token = scanner.nextToken()).isEOF()) {
            push(new QueuedToken(token, scanner.getTokenOffset() + (fOffset), scanner
                    .getTokenLength()));
        }
        setOffset(fOffset + possible.length());
    }

    private void scanTokensInsideDynamicPortion() {
        String possible = new String(fContents.substring(fOffset - origOffset));
        int end = findEnd(possible);
        if (end != -1) {
            possible = new String(possible.substring(0, end));
        }
        RubySourcePartitionScanner scanner = new RubySourcePartitionScanner();
        IDocument document = new Document(possible);
        scanner.setRange(document, 0, possible.length());
        IToken token;
        while (!(token = scanner.nextToken()).isEOF()) {
            push(new QueuedToken(token, scanner.getTokenOffset() + fOffset, scanner
                    .getTokenLength()));
        }
        setOffset(fOffset + possible.length());
    }

    private int findEnd(String possible) {
        return new EndBraceFinder(possible).find();
    }

    private void addPoundBraceToken() {
        addStringToken(2); // add token for the #{
    }

    private void addStringToken(int length) {
        push(new QueuedToken(new Token(fContentType), fOffset, length));
        setOffset(fOffset + length); // move past token
    }

    private void addClosingBraceToken() {
        addStringToken(1);
    }

    private void setLexerPastDynamicSectionOfString() throws IOException {
        StringBuffer fakeContents = new StringBuffer();
        String opening = fOpeningString;
        if (opening.endsWith("\n")) { //$NON-NLS-1$
            // What about When it should remain <<-!
            // try searching backwards from fOffset in fContents for <<-opening
            // or <<opening and take whichever we find
            // first. If we fail to find, assume <<
            String heredocStart = "<<"; //$NON-NLS-1$
            int lastIndent = fContents.lastIndexOf("<<-" + opening, fOffset); //$NON-NLS-1$
            if (lastIndent != -1) {
                if (lastIndent > fContents.lastIndexOf("<<" + opening, fOffset)) //$NON-NLS-1$
                    heredocStart = "<<-"; //$NON-NLS-1$
            }
            opening = heredocStart + opening;
        }
        int start = fOffset - opening.length();
        for (int i = 0; i < start; i++) {
            fakeContents.append(" "); //$NON-NLS-1$
        }
        fakeContents.append(opening);
        if ((fOffset - origOffset) < origLength) {
            // BLAH removed + 1 from end here
            fakeContents.append(new String(fContents.substring((fOffset - origOffset))));
        }
        IDocument document = new Document(fakeContents.toString());
        List<QueuedToken> queueCopy = new ArrayList<QueuedToken>(fQueue);
        setPartialRange(document, start, fakeContents.length() - start, null, start);
        fQueue = new ArrayList<QueuedToken>(queueCopy);
        lexer.advance();
    }

    private void parseOutComments(List<CommentNode> comments) {
        for (CommentNode comment : comments) {
            int offset = correctOffset(comment);
            int length = comment.getContent().length();
            if (isCommentMultiLine(comment)) {
                length = (origOffset + comment.getPosition().getEndOffset()) - offset;
                if (comment.getContent().charAt(0) != '=') {
                    length++;
                }
            }
            Token token = new Token(getContentType(comment));
            push(new QueuedToken(token, offset, length));
        }
    }

    private IToken popTokenOffQueue() {
        QueuedToken token = fQueue.remove(0);
        setOffset(token.getOffset());
        Assert.isTrue(token.getLength() >= 0);
        fLength = token.getLength();
        return token.getToken();
    }

    private IToken getToken(int i) {
        // If we hit a 32 (space) inside a qword, just return string content
        // type (not default)
        // FIXME IF we're in qwords, we should inspect the contents because it
        // may be a variable
        if (i == 32) {
            return new Token(fContentType);
        }
        switch (i) {
        case Tokens.tSTRING_CONTENT:
            return new Token(fContentType);
        case Tokens.tSTRING_BEG:
            fOpeningString = getOpeningString();
            if (fOpeningString.equals("'") || fOpeningString.startsWith("%q")) { //$NON-NLS-1$//$NON-NLS-2$
                inSingleQuote = true;
            } else if (fOpeningString.startsWith("<<")) { // here-doc //$NON-NLS-1$
                fOpeningString = generateHeredocMarker(fOpeningString);
            }
            fContentType = RubySourceConfiguration.STRING;
            return new Token(RubySourceConfiguration.STRING);
        case Tokens.tXSTRING_BEG:
            fOpeningString = getOpeningString();
            fContentType = RubySourceConfiguration.COMMAND;
            return new Token(RubySourceConfiguration.COMMAND);
        case Tokens.tQWORDS_BEG:
        case Tokens.tWORDS_BEG:
            fOpeningString = getOpeningString();
            fContentType = RubySourceConfiguration.STRING;
            return new Token(RubySourceConfiguration.STRING);
        case Tokens.tSTRING_END:
            String oldContentType = fContentType;
            fContentType = RubySourceConfiguration.DEFAULT;
            inSingleQuote = false;
            return new Token(oldContentType);
        case Tokens.tREGEXP_BEG:
            fOpeningString = getOpeningString();
            fContentType = RubySourceConfiguration.REGULAR_EXPRESSION;
            return new Token(RubySourceConfiguration.REGULAR_EXPRESSION);
        case Tokens.tREGEXP_END:
            fContentType = RubySourceConfiguration.DEFAULT;
            return new Token(RubySourceConfiguration.REGULAR_EXPRESSION);
        case Tokens.tSYMBEG:
            // Sometimes we need to add 1, sometimes two. Depends on if there's
            // a space preceding the ':'
            int nextCharOffset = (fOffset + 1);
            int charAt = nextCharOffset - origOffset;
            if (fContents.length() <= charAt) {
                return new Token(RubySourceConfiguration.DEFAULT);
            }
            char c = fContents.charAt(charAt);
            if (c == ':') {
                if (fContents.length() <= charAt + 1) {
                    return new Token(RubySourceConfiguration.DEFAULT);
                }
                nextCharOffset++;
                c = fContents.charAt(charAt + 1);
            }
            if (c == '"') {
                fOpeningString = "\""; //$NON-NLS-1$
                push(new QueuedToken(new Token(RubySourceConfiguration.STRING), nextCharOffset, 1));
                fContentType = RubySourceConfiguration.STRING;
            }
            return new Token(RubySourceConfiguration.DEFAULT);
        default:
            return new Token(RubySourceConfiguration.DEFAULT);
        }
    }

    private String generateHeredocMarker(String marker) {
        if (marker.startsWith("<<")) { //$NON-NLS-1$
            marker = marker.substring(2);
        }
        if (marker.startsWith("-")) { //$NON-NLS-1$
            marker = marker.substring(1);
        }
        return marker + "\n"; //$NON-NLS-1$
    }

    private String getOpeningString() {
        return getUntrimmedOpeningString().trim();
    }

    private String getUntrimmedOpeningString() {
        int start = fOffset - origOffset;
        List<CommentNode> comments = result.getCommentNodes();
        if (comments != null && !comments.isEmpty()) {
            Node comment = comments.get(comments.size() - 1);
            int end = comment.getPosition().getEndOffset();
            start = end;
        }
        return new String(fContents.substring(start, lexerSource.getOffset()));
    }

    /**
     * correct start offset, since when a line with nothing but spaces on it
     * appears before comment, we get messed up positions
     */
    private int correctOffset(CommentNode comment) {
        return origOffset + comment.getPosition().getStartOffset();
    }

    private boolean isCommentMultiLine(CommentNode comment) {
        String src = getSource(fContents, comment);
        return (src != null && src.startsWith(BEGIN));
    }

    private String getContentType(CommentNode comment) {
        if (isCommentMultiLine(comment))
            return RubySourceConfiguration.MULTI_LINE_COMMENT;
        return RubySourceConfiguration.SINGLE_LINE_COMMENT;
    }

    private void addQueuedToken(IToken returnValue, boolean isEOF) {
        // grab end of last comment (last thing in queue)
        QueuedToken token = peek();
        setOffset(token.getOffset() + token.getLength());
        int length = getOffset() - fOffset;
        if (length < 0) {
            length = 0;
        }
        push(new QueuedToken(returnValue, fOffset, length));
    }

    private QueuedToken peek() {
        return fQueue.get(fQueue.size() - 1);
    }

    private void push(QueuedToken token) {
        Assert.isTrue(token.getLength() >= 0);
        fQueue.add(token);
    }

    private int getOffset() {
        return lexerSource.getOffset() + origOffset;
    }

    private static String getSource(String contents, Node node) {
        if (node == null || contents == null)
            return null;
        ISourcePosition pos = node.getPosition();
        if (pos == null)
            return null;
        if (pos.getStartOffset() >= contents.length())
            return null; // position is past end of our source
        if (pos.getEndOffset() > contents.length())
            return null; // end is past end of source
        return new String(contents.substring(pos.getStartOffset(), pos.getEndOffset()));
    }

    private static class EndBraceFinder {
        private String input;
        private List<String> stack;

        public EndBraceFinder(String possible) {
            this.input = possible;
            stack = new ArrayList<String>();
        }

        public int find() {
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                switch (c) {
                case '\\':
                case '$':
                    // skip next character
                    i++;
                    break;
                case '"':
                    if (topEquals("\"")) { //$NON-NLS-1$
                        pop();
                    } else {
                        if (!topEquals("'")) //$NON-NLS-1$
                            push("\""); //$NON-NLS-1$
                    }
                    break;
                case '/':
                    if (topEquals("/")) { //$NON-NLS-1$
                        pop();
                    } else {
                        push("/"); //$NON-NLS-1$
                    }
                    break;
                case '\'':
                    if (topEquals("'")) { //$NON-NLS-1$
                        pop();
                    } else if (!topEquals("\"") && !topEquals("/")) { //$NON-NLS-1$ //$NON-NLS-2$
                        // not inside a double quoted string or a regex
                        push("'"); //$NON-NLS-1$
                    }
                    break;
                case '{':
                    // Only if we're not inside a string
                    if (!topEquals("'") && !topEquals("\"")) { //$NON-NLS-1$ //$NON-NLS-2$
                        push("{"); //$NON-NLS-1$
                    }
                    break;
                case '#':
                    // Only add if we're inside a double quote string
                    if (topEquals("\"")) { //$NON-NLS-1$
                        c = input.charAt(i + 1);
                        if (c == '{')
                            push("#{"); //$NON-NLS-1$
                    }
                    break;
                case '}':
                    if (stack.isEmpty()) { // if not in open state
                        return i;
                    }
                    if (topEquals("#{") || topEquals("{")) { //$NON-NLS-1$ //$NON-NLS-2$
                        pop();
                    }
                    break;
                default:
                    break;
                }
            }
            return -1;
        }

        private boolean topEquals(String string) {
            String open = peek();
            return open != null && open.equals(string);
        }

        private boolean push(String string) {
            return stack.add(string);
        }

        private String pop() {
            return stack.remove(stack.size() - 1);
        }

        private String peek() {
            if (stack.isEmpty())
                return null;
            return stack.get(stack.size() - 1);
        }
    }
}
