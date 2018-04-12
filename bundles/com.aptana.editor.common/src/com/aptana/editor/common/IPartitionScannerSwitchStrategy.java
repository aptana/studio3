/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common;

import org.eclipse.jface.text.rules.ICharacterScanner;

/**
 * @author Max Stepanov
 *
 */
public interface IPartitionScannerSwitchStrategy {

	public interface ISequenceBypassHandler {
		public boolean bypassSequence(ICharacterScanner characterScanner, char[] sequenceFound);
	}
	
	public char[][] getSwitchSequences();
	public ISequenceBypassHandler getSequenceBypassHandler();
}
