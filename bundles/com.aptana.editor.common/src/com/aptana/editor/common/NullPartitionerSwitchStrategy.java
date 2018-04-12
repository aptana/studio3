/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common;


/**
 * @author Max Stepanov
 *
 */
public class NullPartitionerSwitchStrategy implements IPartitionerSwitchStrategy {

	private static final String[][] EMPTY = new String[0][];
	private static final char[][] EMPTY_SWITCHES = new char[0][];
	
	private static final IPartitionScannerSwitchStrategy EMPTY_STRATEGY = new IPartitionScannerSwitchStrategy() {

		public char[][] getSwitchSequences() {
			return EMPTY_SWITCHES;
		}

		public ISequenceBypassHandler getSequenceBypassHandler() {
			return null;
		}
	};
	
	/* (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitionerSwitchStrategy#getDefaultSwitchStrategy()
	 */
	public IPartitionScannerSwitchStrategy getDefaultSwitchStrategy() {
		return EMPTY_STRATEGY;
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitionerSwitchStrategy#getPrimarySwitchStrategy()
	 */
	public IPartitionScannerSwitchStrategy getPrimarySwitchStrategy() {
		return EMPTY_STRATEGY;
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitionerSwitchStrategy#getSwitchTagPairs()
	 */
	public String[][] getSwitchTagPairs() {
		return EMPTY;
	}

}
