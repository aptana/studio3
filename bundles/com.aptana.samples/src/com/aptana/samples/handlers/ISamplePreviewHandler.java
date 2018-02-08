/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.handlers;

import com.aptana.samples.model.SamplesReference;

public interface ISamplePreviewHandler
{

	/**
	 * Handles the preview request on a sample.
	 * 
	 * @param sample
	 *            the sample entry
	 */
	public void previewRequested(SamplesReference sample);
}
