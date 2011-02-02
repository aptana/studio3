/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TypedPosition;
import org.eclipse.jface.text.TypedRegion;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;

/**
 * @author Max Stepanov
 *
 */
public class ExtendedFastPartitioner extends FastPartitioner implements IExtendedPartitioner {

	/**
	 * @param scanner
	 * @param legalContentTypes
	 */
	public ExtendedFastPartitioner(IPartitionTokenScanner scanner, String[] legalContentTypes) {
		super(scanner, legalContentTypes);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.FastPartitioner#getPartition(int, boolean)
	 */
	@Override
	public ITypedRegion getPartition(int offset, boolean preferOpenPartitions) {
		// let the last offset partition be the same as preceding
		ITypedRegion region = super.getPartition(offset, preferOpenPartitions);
		if (region.getOffset() == fDocument.getLength() && region.getType().equals(IDocument.DEFAULT_CONTENT_TYPE)) {
			region = new TypedRegion(region.getOffset(), region.getLength(), getPartition(region.getOffset()-1).getType());
		}
		return region;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.FastPartitioner#findClosestPosition(int)
	 */
	@Override
	public TypedPosition findClosestPosition(int offset) {
		return super.findClosestPosition(offset);
	}

}
