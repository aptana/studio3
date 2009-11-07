package com.aptana.scope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ScopeSelectorTests
{
	@Test()
	public void parseSimpleName()
	{
		String scope = "source.ruby";
		ScopeSelector selector = new ScopeSelector(scope);

		// make sure we parsed successfully
		assertNotNull(selector);

		// convert selector back to source and compare
		assertEquals(scope, selector.toString());

		// make sure we have the right selector type
		ISelectorNode root = selector.getRoot();
		assertTrue(root instanceof NameSelector);
	}

	@Test()
	public void parseSimpleAndSelector()
	{
		String scope = "text.html source.ruby";
		ScopeSelector selector = new ScopeSelector(scope);

		// make sure we parsed successfully
		assertNotNull(selector);
		
		// convert selector back to source and compare
		assertEquals(scope, selector.toString());
		
		// make sure we have the right selector type
		ISelectorNode root = selector.getRoot();
		assertTrue(root instanceof AndSelector);
		
		// check children
		AndSelector andSelector = (AndSelector) root;
		assertTrue(andSelector.getLeftChild() instanceof NameSelector);
		assertTrue(andSelector.getRightChild() instanceof NameSelector);
	}
	
	@Test()
	public void parseSimpleOrSelector()
	{
		String scope = "text.html, source.ruby";
		ScopeSelector selector = new ScopeSelector(scope);
		
		// make sure we parsed successfully
		assertNotNull(selector);
		
		// convert selector back to source and compare
		assertEquals(scope, selector.toString());
		
		// make sure we have the right selector type
		ISelectorNode root = selector.getRoot();
		assertTrue(root instanceof OrSelector);
		
		// check children
		OrSelector orSelector = (OrSelector) root;
		assertTrue(orSelector.getLeftChild() instanceof NameSelector);
		assertTrue(orSelector.getRightChild() instanceof NameSelector);
	}

	@Test()
	public void parseMultiAndSelector()
	{
		String scope = "text.html source.ruby string.ruby";
		ScopeSelector selector = new ScopeSelector(scope);

		// make sure we parsed successfully
		assertNotNull(selector);
		
		// convert selector back to source and compare
		assertEquals(scope, selector.toString());
		
		// make sure we have the right selector type
		ISelectorNode root = selector.getRoot();
		assertTrue(root instanceof AndSelector);
		
		// check children
		AndSelector andSelector = (AndSelector) root;
		assertTrue(andSelector.getLeftChild() instanceof AndSelector);
		assertTrue(andSelector.getRightChild() instanceof NameSelector);
	}
	
	@Test()
	public void parseMultiOrSelector()
	{
		String scope = "text.html, source.ruby, string.ruby";
		ScopeSelector selector = new ScopeSelector(scope);
		
		// make sure we parsed successfully
		assertNotNull(selector);
		
		// convert selector back to source and compare
		assertEquals(scope, selector.toString());
		
		// make sure we have the right selector type
		ISelectorNode root = selector.getRoot();
		assertTrue(root instanceof OrSelector);
		
		// check children
		OrSelector orSelector = (OrSelector) root;
		assertTrue(orSelector.getLeftChild() instanceof OrSelector);
		assertTrue(orSelector.getRightChild() instanceof NameSelector);
	}
	

	@Test()
	public void parseMultiMixedSelector()
	{
		String scope = "text.html source.ruby, text.erb source.ruby, source.ruby string.ruby";
		ScopeSelector selector = new ScopeSelector(scope);

		// make sure we parsed successfully
		assertNotNull(selector);
		
		// convert selector back to source and compare
		assertEquals(scope, selector.toString());
		
		// make sure we have the right selector type
		ISelectorNode root = selector.getRoot();
		assertTrue(root instanceof OrSelector);
		
		// check children
		OrSelector orSelector = (OrSelector) root;
		assertTrue(orSelector.getLeftChild() instanceof OrSelector);
		assertTrue(orSelector.getRightChild() instanceof AndSelector);
	}
}
