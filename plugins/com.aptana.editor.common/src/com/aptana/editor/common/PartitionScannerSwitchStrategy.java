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
public class PartitionScannerSwitchStrategy implements IPartitionScannerSwitchStrategy {

	private char[][] switchSequences;
	private SequenceBypassHandler sequenceBypassHandler;

	/**
	 * 
	 */
	public PartitionScannerSwitchStrategy(String[] switchSequences) {
		this(switchSequences, null);
	}

	/**
	 * 
	 */
	public PartitionScannerSwitchStrategy(String[] switchSequences, SequenceBypassHandler sequenceBypassHandler) {
		this.switchSequences = new char[switchSequences.length][];
		for (int i = 0 ; i < switchSequences.length; ++i) {
			this.switchSequences[i] = switchSequences[i].toCharArray();
		}
		this.sequenceBypassHandler = sequenceBypassHandler;
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitionScannerSwitchStrategy#getSwitchSequences()
	 */
	public char[][] getSwitchSequences() {
		return switchSequences;
	}

	public SequenceBypassHandler getSequenceBypassHandler() {
		return sequenceBypassHandler;
	}

}
