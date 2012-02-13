/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.reconciler;

/**
 * A reconciling strategy that has content to dispose when it is no longer used.
 * 
 * @author Michael Xia (mxia@appcelerator.com)
 */
public interface IDisposableReconcilingStrategy
{

	public void dispose();
}
