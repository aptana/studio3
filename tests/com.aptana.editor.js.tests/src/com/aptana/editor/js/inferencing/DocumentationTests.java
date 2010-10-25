package com.aptana.editor.js.inferencing;

public class DocumentationTests extends InferencingTestsBase
{
	/**
	 * testFunctionReturnType
	 */
	public void testFunctionReturnType()
	{
		String source = this.getContent("inferencing/return-type-documentation.js");
		
		this.lastStatementTypeTests(source, "Function:Number");
	}
	
	/**
	 * testFunctionReturnTypes
	 */
	public void testFunctionReturnTypes()
	{
		String source = this.getContent("inferencing/return-types-documentation.js");
		
		this.lastStatementTypeTests(source, "Function:Number,RegExp");
	}
	
	/**
	 * testVarType
	 */
	public void testVarType()
	{
		String source = this.getContent("inferencing/type-documentation.js");
		
		this.lastStatementTypeTests(source, "Node");
	}
}
