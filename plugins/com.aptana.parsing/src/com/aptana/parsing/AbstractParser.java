package com.aptana.parsing;


public abstract class AbstractParser implements IParser
{

	public synchronized ParseResult parse(IParseState parseState) throws java.lang.Exception
	{
		WorkingParseResult working = new WorkingParseResult();
		parse(parseState, working);
		return working.getImmutableResult();
	}
	
	protected abstract void parse(IParseState parseState, WorkingParseResult working) throws Exception; // $codepro.audit.disable declaredExceptions

}
