/*
 * Copyright (c) 2014, 2016, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.js.parser;

import java.util.ArrayList;
import java.util.List;

import com.oracle.js.parser.ir.Statement;

/**
 * Base class for parser context nodes
 */
abstract class ParserContextBaseNode implements ParserContextNode {
    /**
     * Flags for this node
     */
    protected int flags;

    private List<Statement> statements;

    /**
     * Constructor
     */
    ParserContextBaseNode() {
        this.statements = new ArrayList<>();
    }

    /**
     * @return The flags for this node
     */
    @Override
    public int getFlags() {
        return flags;
    }

    /**
     * Returns a single flag
     *
     * @param flag
     * @return A single flag
     */
    protected int getFlag(final int flag) {
        return (flags & flag);
    }

    /**
     * @param flag
     * @return the new flags
     */
    @Override
    public int setFlag(final int flag) {
        flags |= flag;
        return flags;
    }

    /**
     * @return The list of statements that belongs to this node
     */
    @Override
    public List<Statement> getStatements() {
        return statements;
    }

    /**
     * @param statements
     */
    @Override
    public void setStatements(final List<Statement> statements) {
        this.statements = statements;
    }

    /**
     * Adds a Statement at the end of the Statementlist
     *
     * @param statement The statement to add
     */
    @Override
    public void appendStatement(final Statement statement) {
        this.statements.add(statement);
    }

    /**
     * Adds a statement at the begining of the statementlist
     *
     * @param statement The statement to add
     */
    @Override
    public void prependStatement(final Statement statement) {
        this.statements.add(0, statement);
    }
}
