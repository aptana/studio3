/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples;

import java.util.List;

import com.aptana.samples.model.SampleCategory;
import com.aptana.samples.model.SamplesReference;

public interface ISamplesManager
{

	/**
	 * Returns the list of contributed sample categories.
	 * 
	 * @return the list of sample categories
	 */
	public List<SampleCategory> getCategories();

	/**
	 * Returns the list of samples belonging to a specific category.
	 * 
	 * @param categoryId
	 *            the id of the category
	 * @return the list of samples that belongs to the category
	 */
	public List<SamplesReference> getSamplesForCategory(String categoryId);

	/**
	 * Gets the sample with the specific id.
	 * 
	 * @param id
	 *            the id of the sample
	 * @return the sample with the id
	 */
	public SamplesReference getSample(String id);

	/**
	 * Adds a listener to get notified when a sample is added or removed.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addSampleListener(ISampleListener listener);

	/**
	 * Removes a listener from getting notified when a sample is added or removed.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void removeSampleListener(ISampleListener listener);
}
