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
 * IR representation for an IF statement.
 */
public final class IfNode extends Statement {
    /** Test expression. */
    private final Expression test;

    /** Pass statements. */
    private final Block pass;

    /** Fail statements. */
    private final Block fail;

    /**
     * Constructor
     *
     * @param lineNumber line number
     * @param token      token
     * @param finish     finish
     * @param test       test
     * @param pass       block to execute when test passes
     * @param fail       block to execute when test fails or null
     */
    public IfNode(final int lineNumber, final long token, final int finish, final Expression test, final Block pass, final Block fail) {
        super(lineNumber, token, finish);
        this.test = test;
        this.pass = pass;
        this.fail = fail;
    }

    private IfNode(final IfNode ifNode, final Expression test, final Block pass, final Block fail) {
        super(ifNode);
        this.test = test;
        this.pass = pass;
        this.fail = fail;
    }

    @Override
    public boolean isTerminal() {
        return pass.isTerminal() && fail != null && fail.isTerminal();
    }

    @Override
    public Node accept(final NodeVisitor<? extends LexicalContext> visitor) {
        if (visitor.enterIfNode(this)) {
            return visitor.leaveIfNode(
                setTest((Expression)test.accept(visitor)).
                setPass((Block)pass.accept(visitor)).
                setFail(fail == null ? null : (Block)fail.accept(visitor)));
        }

        return this;
    }

    @Override
    public <R> R accept(TranslatorNodeVisitor<? extends LexicalContext, R> visitor) {
        return visitor.enterIfNode(this);
    }

    @Override
    public void toString(final StringBuilder sb, final boolean printTypes) {
        sb.append("if (");
        test.toString(sb, printTypes);
        sb.append(')');
    }

    /**
     * Get the else block of this IfNode
     * @return the else block, or null if none exists
     */
    public Block getFail() {
        return fail;
    }

    private IfNode setFail(final Block fail) {
        if (this.fail == fail) {
            return this;
        }
        return new IfNode(this, test, pass, fail);
    }

    /**
     * Get the then block for this IfNode
     * @return the then block
     */
    public Block getPass() {
        return pass;
    }

    private IfNode setPass(final Block pass) {
        if (this.pass == pass) {
            return this;
        }
        return new IfNode(this, test, pass, fail);
    }

    /**
     * Get the test expression for this IfNode
     * @return the test expression
     */
    public Expression getTest() {
        return test;
    }

    /**
     * Reset the test expression for this IfNode
     * @param test a new test expression
     * @return new or same IfNode
     */
    public IfNode setTest(final Expression test) {
        if (this.test == test) {
            return this;
        }
        return new IfNode(this, test, pass, fail);
    }
}
