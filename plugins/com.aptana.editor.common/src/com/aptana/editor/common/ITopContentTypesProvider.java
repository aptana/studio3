package com.aptana.editor.common;

/**
 * This returns the top level content types that are possible.
 * 
 * @author schitale
 *
 */
public interface ITopContentTypesProvider {
	// TODO Use generic collections instead of arrays
	String[][] getTopContentTypes();
}
