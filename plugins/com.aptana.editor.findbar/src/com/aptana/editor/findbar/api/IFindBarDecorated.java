package com.aptana.editor.findbar.api;

/**
 * A tag interface to indicate that the ITextEditor is capable of supporting
 * Find Bar.
 * 
 * @author schitale
 *
 */
public interface IFindBarDecorated {
	IFindBarDecorator getFindBarDecorator();
}
