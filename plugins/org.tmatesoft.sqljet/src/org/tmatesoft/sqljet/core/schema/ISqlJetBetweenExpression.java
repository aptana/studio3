/**
 * ISqlJetBetweenExpression.java
 * Copyright (C) 2009-2010 TMate Software Ltd
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package org.tmatesoft.sqljet.core.schema;

/**
 * <p>The BETWEEN operator.</p>
 * 
 * <p>The BETWEEN operator is equivalent to a pair of comparisons.
 * "a BETWEEN b AND c" is equivalent to "a&gt;=b AND a&lt;=c". The precedence of
 * the BETWEEN operator is the same as the precedence as operators == and != and
 * LIKE and groups left to right.</p>
 * 
 * <p>Format:</p>
 * 
 * <p>
 * &lt;expression&gt; [NOT] BETWEEN &lt;lowerBound&gt; AND &lt;upperBound&gt;
 * </p>
 * 
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public interface ISqlJetBetweenExpression extends ISqlJetExpression {

    /**
     * Expression.
     * 
     * @return expression
     */
    public ISqlJetExpression getExpression();

    /**
     * Checks is NOT used.
     * 
     * @return true if NOT is used
     */
    public boolean isNot();

    /**
     * Lower bound.
     * 
     * @return lower bound
     */
    public ISqlJetExpression getLowerBound();

    /**
     * Upper bound.
     * 
     * @return upper bound
     */
    public ISqlJetExpression getUpperBound();
}
