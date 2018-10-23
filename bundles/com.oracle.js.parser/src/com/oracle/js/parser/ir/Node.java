/*
 * Copyright (c) 2010, 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.oracle.js.parser.ir;

import java.util.ArrayList;
import java.util.List;

import com.oracle.js.parser.Token;
import com.oracle.js.parser.TokenType;
import com.oracle.js.parser.ir.visitor.NodeVisitor;
import com.oracle.js.parser.ir.visitor.TranslatorNodeVisitor;

/**
 * Nodes are used to compose Abstract Syntax Trees.
 */
public abstract class Node implements Cloneable {
    /** Start of source range. */
    protected final int start;

    /** End of source range. */
    protected final int finish;

    /** Token descriptor. */
    private final long token;

    /**
     * Constructor
     *
     * @param token token
     * @param finish finish
     */
    public Node(final long token, final int finish) {
        this.token = token;
        this.start = Token.descPosition(token);
        this.finish = finish;
    }

    /**
     * Constructor
     *
     * @param token token
     * @param start start
     * @param finish finish
     */
    protected Node(final long token, final int start, final int finish) {
        this.start = start;
        this.finish = finish;
        this.token = token;
    }

    /**
     * Copy constructor
     *
     * @param node source node
     */
    protected Node(final Node node) {
        this.token = node.token;
        this.start = node.start;
        this.finish = node.finish;
    }

    /**
     * Copy constructor that overrides finish
     *
     * @param node source node
     * @param finish Last character
     */
    protected Node(final Node node, final int finish) {
        this.token = node.token;
        this.start = node.start;
        this.finish = finish;
    }

    /**
     * Is this a loop node?
     *
     * @return true if atom
     */
    public boolean isLoop() {
        return false;
    }

    /**
     * Is this an assignment node - for example a var node with an init or a binary node that writes
     * to a destination
     *
     * @return true if assignment
     */
    public boolean isAssignment() {
        return false;
    }

    /**
     * Provides a means to navigate the IR.
     *
     * @param visitor Node visitor.
     * @return node the node or its replacement after visitation, null if no further visitations are
     *         required
     */
    public abstract Node accept(NodeVisitor<? extends LexicalContext> visitor);

    /**
     * Provides a means to navigate the IR.
     *
     * @param visitor Node visitor.
     * @return node the node or its replacement after visitation, null if no further visitations are
     *         required
     */
    public abstract <R> R accept(TranslatorNodeVisitor<? extends LexicalContext, R> visitor);

    @Override
    public final String toString() {
        return toString(true);
    }

    /**
     * Return String representation of this Node.
     *
     * @param includeTypeInfo include type information or not
     */
    public final String toString(final boolean includeTypeInfo) {
        final StringBuilder sb = new StringBuilder();
        toString(sb, includeTypeInfo);
        return sb.toString();
    }

    /**
     * String conversion helper. Fills a {@link StringBuilder} with the string version of this node
     *
     * @param sb a StringBuilder
     */
    public void toString(final StringBuilder sb) {
        toString(sb, true);
    }

    /**
     * Print logic that decides whether to show the optimistic type or not - for example it should
     * not be printed after just parse, when it hasn't been computed, or has been set to a trivially
     * provable value
     *
     * @param sb string builder
     * @param printType print type?
     */
    public abstract void toString(final StringBuilder sb, final boolean printType);

    /**
     * Get the finish position for this node in the source string
     *
     * @return finish
     */
    public int getFinish() {
        return finish;
    }

    /**
     * Get start position for node
     *
     * @return start position
     */
    public int getStart() {
        return start;
    }

    /**
     * Integer to sort nodes in source order. This order is used by parser API to sort statements in
     * correct order. By default, this is the start position of this node.
     *
     * @return int code to sort this node.
     */
    public int getSourceOrder() {
        return getStart();
    }

    @Override
    protected Object clone() {
        try {
            return super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public final boolean equals(final Object other) {
        return this == other;
    }

    @Override
    public final int hashCode() {
        /*
         * NOTE: we aren't delegating to Object.hashCode as it still requires trip to the VM for
         * initializing, it touches the object header and/or stores the identity hashcode somewhere.
         */
        return Long.hashCode(token);
    }

    /**
     * Return token position from a token descriptor.
     *
     * @return Start position of the token in the source.
     */
    public int position() {
        return Token.descPosition(token);
    }

    /**
     * Return token length from a token descriptor.
     *
     * @return Length of the token.
     */
    public int length() {
        return Token.descLength(token);
    }

    /**
     * Returns this node's token's type. If you want to check for the node having a specific token
     * type, consider using {@link #isTokenType(TokenType)} instead.
     *
     * @return type of token.
     */
    public TokenType tokenType() {
        return Token.descType(token);
    }

    /**
     * Tests if this node has the specific token type.
     *
     * @param type a token type to check this node's token type against
     * @return true if token types match.
     */
    public boolean isTokenType(final TokenType type) {
        return tokenType() == type;
    }

    /**
     * Get the token for this node. If you want to retrieve the token's type, consider using
     * {@link #tokenType()} or {@link #isTokenType(TokenType)} instead.
     *
     * @return the token
     */
    public long getToken() {
        return token;
    }

    // on change, we have to replace the entire list, that's we can't simple do ListIterator.set
    static <T extends Node> List<T> accept(final NodeVisitor<? extends LexicalContext> visitor, final List<T> list) {
        final int size = list.size();
        if (size == 0) {
            return list;
        }

        List<T> newList = null;

        for (int i = 0; i < size; i++) {
            final T node = list.get(i);
            @SuppressWarnings("unchecked")
            final T newNode = node == null ? null : (T) node.accept(visitor);
            if (newNode != node) {
                if (newList == null) {
                    newList = new ArrayList<>(size);
                    for (int j = 0; j < i; j++) {
                        newList.add(list.get(j));
                    }
                }
                newList.add(newNode);
            } else {
                if (newList != null) {
                    newList.add(node);
                }
            }
        }

        return newList == null ? list : newList;
    }

    static <T extends LexicalContextNode> T replaceInLexicalContext(final LexicalContext lc, final T oldNode, final T newNode) {
        if (lc != null) {
            lc.replace(oldNode, newNode);
        }
        return newNode;
    }
}
