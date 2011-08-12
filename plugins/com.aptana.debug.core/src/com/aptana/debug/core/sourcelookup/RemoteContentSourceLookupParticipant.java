/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.debug.core.sourcelookup;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;

/**
 * @author Max Stepanov
 */
public abstract class RemoteContentSourceLookupParticipant extends AbstractSourceLookupParticipant {

	/*
	 * @see org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant#findSourceElements(java.lang.Object)
	 */
	public Object[] findSourceElements(Object object) throws CoreException {
		if (object instanceof IDebugElement && ((IDebugElement) object).getDebugTarget() != null) {
			IFileContentRetriever fileContentRetriever = (IFileContentRetriever) ((IDebugElement) object)
					.getDebugTarget().getAdapter(IFileContentRetriever.class);
			if (fileContentRetriever != null) {
				Object[] objects = super.findSourceElements(object);
				for (Object i : objects) {
					if (i instanceof RemoteFileStorage) {
						((RemoteFileStorage) i).setFileContentRetriever(fileContentRetriever);
					}
				}
				return objects;
			}
		}
		return super.findSourceElements(object);
	}

}
