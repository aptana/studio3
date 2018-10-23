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

import com.oracle.js.parser.ir.visitor.NodeVisitor;
import com.oracle.js.parser.ir.visitor.TranslatorNodeVisitor;

// @formatter:off
/**
 * IR representing a FOR statement.
 */
public final class ForNode extends LoopNode {
    /** Initialize expression for an ordinary for statement, or the LHS expression receiving iterated-over values in a
     * for-in statement. */
    private final Expression init;

    /** Modify expression for an ordinary statement, or the source of the iterator in the for-in statement. */
    private final JoinPredecessorExpression modify;

    /** Iterator symbol. */
    private Symbol iterator;

    /** Is this a normal for in loop? */
    public static final int IS_FOR_IN           = 1 << 0;

    /** Is this a normal for each in loop? */
    public static final int IS_FOR_EACH         = 1 << 1;

    /** Does this loop need a per-iteration scope because its init contain a LET declaration? */
    public static final int PER_ITERATION_SCOPE = 1 << 2;

    public static final int IS_FOR_OF = 1 << 3;

    private final int flags;

    /**
     * Constructor
     *
     * @param lineNumber The line number of header
     * @param token      The for token
     * @param finish     The last character of the for node
     * @param body       The body of the for node
     * @param flags      The flags
     * @param init       The initial expression
     * @param test       The test expression
     * @param modify     The modify expression
     */
    public ForNode(final int lineNumber, final long token, final int finish, final Block body, final int flags, final Expression init, final JoinPredecessorExpression test, final JoinPredecessorExpression modify) {
        super(lineNumber, token, finish, body, test, false);
        this.flags  = flags;
        this.init = init;
        this.modify = modify;

    }

    private ForNode(final ForNode forNode, final Expression init, final JoinPredecessorExpression test,
            final Block body, final JoinPredecessorExpression modify, final int flags, final boolean controlFlowEscapes) {
        super(forNode, test, body, controlFlowEscapes);
        this.init   = init;
        this.modify = modify;
        this.flags  = flags;
        // Even if the for node gets cloned in try/finally, the symbol can be shared as only one branch of the finally
        // is executed.
        this.iterator = forNode.iterator;
    }

    @Override
    public Node accept(final LexicalContext lc, final NodeVisitor<? extends LexicalContext> visitor) {
        if (visitor.enterForNode(this)) {
            return visitor.leaveForNode(
                setInit(lc, init == null ? null : (Expression)init.accept(visitor)).
                setTest(lc, test == null ? null : (JoinPredecessorExpression)test.accept(visitor)).
                setModify(lc, modify == null ? null : (JoinPredecessorExpression)modify.accept(visitor)).
                setBody(lc, (Block)body.accept(visitor)));
        }

        return this;
    }

    @Override
    public <R> R accept(LexicalContext lc, TranslatorNodeVisitor<? extends LexicalContext, R> visitor) {
        return visitor.enterForNode(this);
    }

    @Override
    public void toString(final StringBuilder sb, final boolean printTypes) {
        sb.append("for");
        sb.append(' ');
        if (isForEach()) {
            sb.append("each ");
        }
        sb.append('(');

        if (isForIn()) {
            init.toString(sb, printTypes);
            sb.append(" in ");
            modify.toString(sb, printTypes);
        } else if (isForOf()) {
            init.toString(sb, printTypes);
            sb.append(" of ");
            modify.toString(sb, printTypes);
        } else {
            if (init != null) {
                init.toString(sb, printTypes);
            }
            sb.append("; ");
            if (test != null) {
                test.toString(sb, printTypes);
            }
            sb.append("; ");
            if (modify != null) {
                modify.toString(sb, printTypes);
            }
        }

        sb.append(')');
    }

    @Override
    public boolean hasGoto() {
        return !isForInOrOf() && test == null;
    }

    @Override
    public boolean mustEnter() {
        if (isForInOrOf()) {
            return false; //may be an empty set to iterate over, then we skip the loop
        }
        return test == null;
    }

    /**
     * Get the initialization expression for this for loop
     * @return the initialization expression
     */
    public Expression getInit() {
        return init;
    }

    /**
     * Reset the initialization expression for this for loop
     * @param lc lexical context
     * @param init new initialization expression
     * @return new for node if changed or existing if not
     */
    public ForNode setInit(final LexicalContext lc, final Expression init) {
        if (this.init == init) {
            return this;
        }
        return Node.replaceInLexicalContext(lc, this, new ForNode(this, init, test, body, modify, flags, controlFlowEscapes));
    }

    /**
     * Is this a for in construct rather than a standard init;condition;modification one
     * @return true if this is a for in construct
     */
    public boolean isForIn() {
        return (flags & IS_FOR_IN) != 0;
    }

    /**
     * Is this a (non-standard) for each construct, known from e.g. Rhino.
     * @return true if this is a for each construct
     */
    public boolean isForEach() {
        return (flags & IS_FOR_EACH) != 0;
    }

    /**
     * Is this an ECMAScript 6 for of construct.
     * @return true if this is a for of construct
     */
    public boolean isForOf() {
        return (flags & IS_FOR_OF) != 0;
    }

    /**
     * Is this a for-in or for-of statement?
     * @return true if this is a for-in or for-of loop
     */
    public boolean isForInOrOf() {
        return isForIn() || isForOf();
    }

    /**
     * If this is a for in or for each construct, there is an iterator symbol
     * @return the symbol for the iterator to be used, or null if none exists
     */
    public Symbol getIterator() {
        return iterator;
    }

    /**
     * Assign an iterator symbol to this ForNode. Used for for in and for each constructs
     * @param iterator the iterator symbol
     */
    public void setIterator(final Symbol iterator) {
        this.iterator = iterator;
    }

    /**
     * Get the modification expression for this ForNode
     * @return the modification expression
     */
    public JoinPredecessorExpression getModify() {
        return modify;
    }

    /**
     * Reset the modification expression for this ForNode
     * @param lc lexical context
     * @param modify new modification expression
     * @return new for node if changed or existing if not
     */
    public ForNode setModify(final LexicalContext lc, final JoinPredecessorExpression modify) {
        if (this.modify == modify) {
            return this;
        }
        return Node.replaceInLexicalContext(lc, this, new ForNode(this, init, test, body, modify, flags, controlFlowEscapes));
    }

    @Override
    public ForNode setTest(final LexicalContext lc, final JoinPredecessorExpression test) {
        if (this.test == test) {
            return this;
        }
        return Node.replaceInLexicalContext(lc, this, new ForNode(this, init, test, body, modify, flags, controlFlowEscapes));
    }

    @Override
    public Block getBody() {
        return body;
    }

    @Override
    public ForNode setBody(final LexicalContext lc, final Block body) {
        if (this.body == body) {
            return this;
        }
        return Node.replaceInLexicalContext(lc, this, new ForNode(this, init, test, body, modify, flags, controlFlowEscapes));
    }

    @Override
    public ForNode setControlFlowEscapes(final LexicalContext lc, final boolean controlFlowEscapes) {
        if (this.controlFlowEscapes == controlFlowEscapes) {
            return this;
        }
        return Node.replaceInLexicalContext(lc, this, new ForNode(this, init, test, body, modify, flags, controlFlowEscapes));
    }

    @Override
    public boolean hasPerIterationScope() {
        return (flags & PER_ITERATION_SCOPE) != 0;
    }
}
