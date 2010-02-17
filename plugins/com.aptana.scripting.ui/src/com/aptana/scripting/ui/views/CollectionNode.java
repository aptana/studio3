package com.aptana.scripting.ui.views;

public interface CollectionNode
{
	String getLabel();
	
	Object[] getChildren();
	
	boolean hasChildren();
}
