package com.aptana.parsing;

import com.aptana.parsing.ast.IParseNode;

public interface IParser {

    public IParseNode parse(String source) throws Exception;
}
