package com.aptana.editor.js.inferencing;

public class GeneralInferencingTests extends InferencingTestsBase
{
	/**
	 * testIdentifierCycle
	 */
	public void testIdentifierCycle()
	{
		this.varTypeTests("var a = b, b = a;", "b");
	}
}
