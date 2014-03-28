/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common;

import com.aptana.editor.common.IPartitionScannerSwitchStrategy.ISequenceBypassHandler;

/**
 * @author Max Stepanov
 *
 */
public abstract class PartitionerSwitchStrategy implements IPartitionerSwitchStrategy {
	
	private final char[][][] switchSequences;
	private final ISequenceBypassHandler[] sequenceBypassHandlers;

	protected PartitionerSwitchStrategy(String[][] switchSequencePairs) {
		this(switchSequencePairs, null, null);
	}

	private PartitionerSwitchStrategy(String[][] switchSequencePairs, ISequenceBypassHandler startBypassHandler, ISequenceBypassHandler endBypassHandler) {
		char[][] startSequences = new char[switchSequencePairs.length][];
		char[][] endSequences = new char[switchSequencePairs.length][];
		for (int i = 0; i < switchSequencePairs.length; ++i) {
			startSequences[i] = switchSequencePairs[i][0].toCharArray();
			endSequences[i] = switchSequencePairs[i][1].toCharArray();
		}

		switchSequences = new char[][][] { TextUtils.removeDuplicates(startSequences), TextUtils.removeDuplicates(endSequences) };
		sequenceBypassHandlers = new ISequenceBypassHandler[] { startBypassHandler, endBypassHandler };
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitionerSwitchStrategy#getDefaultSwitchStrategy()
	 */
	public IPartitionScannerSwitchStrategy getDefaultSwitchStrategy() {
		return new IPartitionScannerSwitchStrategy() {
			public char[][] getSwitchSequences() {
				return switchSequences[0];
			}

			public ISequenceBypassHandler getSequenceBypassHandler() {
				return sequenceBypassHandlers[0];
			}
		};
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitionerSwitchStrategy#getPrimarySwitchStrategy()
	 */
	public IPartitionScannerSwitchStrategy getPrimarySwitchStrategy() {
		return new IPartitionScannerSwitchStrategy() {
			public char[][] getSwitchSequences() {
				return switchSequences[1];
			}

			public ISequenceBypassHandler getSequenceBypassHandler() {
				return sequenceBypassHandlers[1];
			}
		};
	}

}
