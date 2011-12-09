/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

/**
 * Various options that can be passed on the command line to control background reconciliation
 * 
 * @author Ingo Muschenetz
 */
public interface ICommonEditorSystemProperties
{
	/**
	 * Configuration options for background reconciliation.
	 */
	public static String DISABLE_BACKGROUND_RECONCILER = "studio.disableBackgroundReconciler"; //$NON-NLS-1$;
	public static String RECONCILER_ITERATION_PARTITION_LIMIT = "studio.reconcilerIterationPartitionLimit"; //$NON-NLS-1$;
	public static String RECONCILER_BACKGROUND_DELAY = "studio.reconcilerBackgroundDelay"; //$NON-NLS-1$
	public static String RECONCILER_ITERATION_DELAY = "studio.reconcilerIterationDelay"; //$NON-NLS-1$
	public static String RECONCILER_MINIMAL_VISIBLE_LENGTH = "studio.reconcilerMinimalVisibleLength"; //$NON-NLS-1$
}
