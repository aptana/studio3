/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common;

import java.util.HashSet;
import java.util.Set;

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

	private final Set<String> legalContentTypes;

	/**
	 * @param scanner
	 * @param legalContentTypes
	 */
	public ExtendedFastPartitioner(IPartitionTokenScanner scanner, String[] legalContentTypes) {
		super(scanner, legalContentTypes);
		this.legalContentTypes = new HashSet<String>(legalContentTypes.length);
		for (String contentType : legalContentTypes) {
			this.legalContentTypes.add(contentType);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.FastPartitioner#isSupportedContentType(java.lang.String)
	 */
	@Override
	protected boolean isSupportedContentType(String contentType) {
		return legalContentTypes.contains(contentType);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.FastPartitioner#getPartition(int, boolean)
	 */
	@Override
	public ITypedRegion getPartition(int offset, boolean preferOpenPartitions) {
		// let the last offset partition be the same as preceding
		ITypedRegion region = super.getPartition(offset, preferOpenPartitions);
		if (region.getType().equals(IDocument.DEFAULT_CONTENT_TYPE)) {
			if (region.getOffset() == fDocument.getLength()) {
				region = new TypedRegion(region.getOffset(), region.getLength(), getPartition(region.getOffset()-1).getType());
			} else if (preferOpenPartitions && region.getLength() == 0) {
				region = super.getPartition(offset, false);
			}
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
