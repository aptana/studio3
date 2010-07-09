package com.aptana.editor.js.contentassist;

import java.util.List;

public interface IReference
{
	/**
	 * Returns the name of the property this reference represents
	 * 
	 * @return String
	 */
	String getPropertyName();

	/**
	 * Returns a list of types that this reference represents
	 * 
	 * @return List<TypeElement>
	 */
	List<String> getTypes();

	/**
	 * This dereferences the current reference. A new reference is created using the resulting types and the specified
	 * propertyName
	 * 
	 * @param propertyName
	 * @return IReference
	 */
	IReference getPropertyReference(String propertyName);
}
