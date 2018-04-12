/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.internal.sourcelookup;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant;

/**
 * JS source lookup director. For JS source lookup there is one source lookup participant.
 */
public class JSSourceLookupDirector extends AbstractSourceLookupDirector {

	/*
	 * @see org.eclipse.debug.core.sourcelookup.ISourceLookupDirector#initializeParticipants()
	 */
	public void initializeParticipants() {
		addParticipants(new ISourceLookupParticipant[] { new JSSourceLookupParticipant() });
	}

	/*
	 * @see org.eclipse.debug.core.model.IPersistableSourceLocator#getMemento()
	 */
	public String getMemento() throws CoreException {
		return null;
	}
}
