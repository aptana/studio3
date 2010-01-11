package com.aptana.parsing.ast;

import beaver.spec.ast.Node;
import beaver.spec.ast.TreeWalker;

public class ParseBaseNode extends Node implements IParseNode {

    private IParseNode[] fChildren;

    public ParseBaseNode() {
        fChildren = new IParseNode[0];
    }

    @Override
    public void accept(TreeWalker walker) {
    }

    @Override
    public IParseNode[] getChildren() {
        return fChildren;
    }

    @Override
    public int getEndingOffset() {
        return getEnd();
    }

    @Override
    public int getLength() {
        return getEnd() - getStart() + 1;
    }

    @Override
    public int getStartingOffset() {
        return getStart();
    }

    @Override
    public String getText() {
        return toString();
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        for (IParseNode node : fChildren) {
            text.append(node);
        }
        return text.toString();
    }

    protected void setChildren(IParseNode[] children) {
        fChildren = children;
    }
}
