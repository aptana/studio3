/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.debug.internal.core.sourcelookup;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;

import com.aptana.debug.core.model.IJSScriptElement;
import com.aptana.debug.core.model.IJSStackFrame;
import com.aptana.debug.core.model.ISourceLink;
import com.aptana.debug.internal.core.IFileContentRetriever;

/**
 * The source lookup participant knows how to translate a JS stack frame into a
 * source file name
 */
public class JSSourceLookupParticipant extends AbstractSourceLookupParticipant {
	/**
	 * JSSourceLookupParticipant
	 */
	public JSSourceLookupParticipant() {
		super();
		RemoteSourceCacheManager.getDefault();
	}

	/**
	 * @see org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant#getSourceName(java.lang.Object)
	 */
	public String getSourceName(Object object) throws CoreException {
		if (object instanceof IJSStackFrame) {
			return ((IJSStackFrame) object).getSourceFileName();
		} else if (object instanceof IJSScriptElement) {
			return ((IJSScriptElement) object).getLocation();
		} else if (object instanceof ISourceLink) {
			return ((ISourceLink) object).getLocation();
		} else if (object instanceof String) {
			// assume it's a file name
			return (String) object;
		}
		return null;
	}

	/**
	 * @see org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant#findSourceElements(java.lang.Object)
	 */
	public Object[] findSourceElements(Object object) throws CoreException {
		if (object instanceof IDebugElement && ((IDebugElement) object).getDebugTarget() != null) {
			IFileContentRetriever fileContentRetriever = (IFileContentRetriever) ((IDebugElement) object)
					.getDebugTarget().getAdapter(IFileContentRetriever.class);
			if (fileContentRetriever != null) {
				Object[] objects = super.findSourceElements(object);
				for (int i = 0; i < objects.length; ++i) {
					if (objects[i] instanceof RemoteFileStorage) {
						((RemoteFileStorage) objects[i]).setFileContentRetriever(fileContentRetriever);
					}
				}
				return objects;
			}
		}
		return super.findSourceElements(object);
	}
}