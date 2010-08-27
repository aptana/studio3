package com.aptana.editor.common.scripting;


public interface IContentTypeTranslator
{

	/**
	 * Allows plugins to contribute a scope translation. This is used to give different scope names to various
	 * partitions based on their nesting within other languages.
	 * 
	 * @param left
	 * @param right
	 */
	public void addTranslation(QualifiedContentType left, QualifiedContentType right);

	public QualifiedContentType translate(QualifiedContentType contentType);
}
