/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples;

import java.util.List;

import com.aptana.samples.model.IProjectSample;
import com.aptana.samples.model.SampleCategory;
import com.aptana.scripting.model.ProjectSampleElement;

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
	public List<IProjectSample> getSamplesForCategory(String categoryId);

	/**
	 * Gets the sample with the specific id.
	 * 
	 * @param id
	 *            the id of the sample
	 * @return the sample with the id
	 */
	public IProjectSample getSample(String id);

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

	/**
	 * Adds a sample to the sample manager
	 * 
	 * @param sampleElement
	 *            sample defined in the bundles.
	 */
	public void addSample(ProjectSampleElement sampleElement);

	/**
	 * @param categoryId
	 * @return the category based on the provided categoryId
	 */
	public SampleCategory getCategory(String categoryId);

	/**
	 * Adds a sample to the sample manager
	 * 
	 * @param sample
	 */
	public void addSample(IProjectSample sample);
}
