/**
 * ISqlJetBinaryExpression.java
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
 * <p>
 * Binary expression.
 * </p>
 * 
 * <p>
 * Binary expression is compound expression which consists from two expressions
 * combined by one operation {@link Operation}.
 * </p>
 * 
 * <p>
 * Format:
 * </p>
 * 
 * <p>
 * &lt;leftExpression&gt; &lt;operation&gt; &lt;rightExpression&gt;
 * </p>
 * 
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public interface ISqlJetBinaryExpression extends ISqlJetExpression {

    /**
     * Operation which combine expressions in binary expression
     * {@link ISqlJetBinaryExpression}.
     * 
     * @author TMate Software Ltd.
     * @author Dmitry Stadnik (dtrace@seznam.cz)
     * 
     */
    public enum Operation {

        /**
         * Logical "or" : <code>OR</code>
         */
        OR,

        /**
         * Logical "and" : <code>AND</code>
         */
        AND,

        /**
         * Equals : <code>=</code> , <code>==</code>
         */
        EQUALS,

        /**
         * Not equals : <code>&lt;&gt;</code> , <code>!=</code>
         */
        NOT_EQUALS,

        /**
         * Less : <code>&lt;</code>
         */
        LESS,

        /**
         * Less or equals : <code>&lt;=</code>
         */
        LESS_OR_EQ,

        /**
         * Greater : <code>&gt;</code>
         */
        GREATER,

        /**
         * Greater or equals : <code>&gt;=</code>
         */
        GREATER_OR_EQ,

        /**
         * Bitwise left-shift : <code>&lt;&lt;</code>
         */
        SHIFT_LEFT,

        /**
         * Bitwise right-shift : <code>&gt;&gt;</code>
         */
        SHIFT_RIGHT,

        /**
         * Bitwise "and" : <code>&</code>
         */
        BIT_AND,

        /**
         * Bitwise "or" : <code>|</code>
         */
        BIT_OR,

        /**
         * Plus : <code>+</code>
         */
        PLUS,

        /**
         * Minus : <code>-</code>
         */
        MINUS,

        /**
         * Multiply : <code>*</code>
         */
        MULTIPLY,

        /**
         * Divide : <code>/</code>
         */
        DIVIDE,

        /**
         * Modulo (divide remainder) : <code>%</code>
         */
        MODULO,

        /**
         * Strings concatenation : <code>||</code>
         */
        CONCATENATE;

        /**
         * Decode operation from string.
         * 
         * @param s
         *            string to decode
         * @return decoded operation or null if string doesn't have known
         *         operation.
         */
        public static Operation decode(String s) {
            if ("or".equalsIgnoreCase(s)) {
                return OR;
            } else if ("and".equalsIgnoreCase(s)) {
                return AND;
            } else if ("=".equalsIgnoreCase(s) || "==".equalsIgnoreCase(s)) {
                return EQUALS;
            } else if ("!=".equalsIgnoreCase(s) || "<>".equalsIgnoreCase(s)) {
                return NOT_EQUALS;
            } else if ("<".equalsIgnoreCase(s)) {
                return LESS;
            } else if ("<=".equalsIgnoreCase(s)) {
                return LESS_OR_EQ;
            } else if (">".equalsIgnoreCase(s)) {
                return GREATER;
            } else if (">=".equalsIgnoreCase(s)) {
                return GREATER_OR_EQ;
            } else if ("<<".equalsIgnoreCase(s)) {
                return SHIFT_LEFT;
            } else if (">>".equalsIgnoreCase(s)) {
                return SHIFT_RIGHT;
            } else if ("&".equalsIgnoreCase(s)) {
                return BIT_AND;
            } else if ("|".equalsIgnoreCase(s)) {
                return BIT_OR;
            } else if ("+".equalsIgnoreCase(s)) {
                return PLUS;
            } else if ("-".equalsIgnoreCase(s)) {
                return MINUS;
            } else if ("*".equalsIgnoreCase(s)) {
                return MULTIPLY;
            } else if ("/".equalsIgnoreCase(s)) {
                return DIVIDE;
            } else if ("%".equalsIgnoreCase(s)) {
                return MODULO;
            } else if ("||".equalsIgnoreCase(s)) {
                return CONCATENATE;
            }
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Enum#toString()
         */
        public String toString() {
            switch (this) {
            case OR:
                return "OR";
            case AND:
                return "AND";
            case EQUALS:
                return "=";
            case NOT_EQUALS:
                return "!=";
            case LESS:
                return "<";
            case LESS_OR_EQ:
                return "<=";
            case GREATER:
                return ">";
            case GREATER_OR_EQ:
                return ">=";
            case SHIFT_LEFT:
                return "<<";
            case SHIFT_RIGHT:
                return ">>";
            case BIT_AND:
                return "&";
            case BIT_OR:
                return "|";
            case PLUS:
                return "+";
            case MINUS:
                return "-";
            case MULTIPLY:
                return "*";
            case DIVIDE:
                return "/";
            case MODULO:
                return "%";
            case CONCATENATE:
                return "||";
            }
            return "";
        }
    }

    /**
     * Operation.
     * 
     * @return operation
     */
    public Operation getOperation();

    /**
     * Left expression.
     * 
     * @return left expression
     */
    public ISqlJetExpression getLeftExpression();

    /**
     * Right expression.
     * 
     * @return right expression
     */
    public ISqlJetExpression getRightExpression();
}
