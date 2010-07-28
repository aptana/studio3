package com.aptana.editor.js.inferencing;

import java.util.List;

import com.aptana.editor.js.parsing.ast.JSInvokeNode;

public interface InvocationProcessor
{
	/**
	 * getInvocationPatterns
	 * 
	 * @return
	 */
	List<String> getInvocationPatterns();

	/**
	 * processInvocation
	 * 
	 * @param scope
	 * @param node
	 * @return
	 */
	boolean processInvocation(JSScope scope, JSInvokeNode node);
}
