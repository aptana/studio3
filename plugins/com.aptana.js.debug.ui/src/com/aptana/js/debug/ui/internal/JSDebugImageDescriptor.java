/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

/**
 * @author Max Stepanov
 */
public class JSDebugImageDescriptor extends CompositeImageDescriptor {
	public static final int ENABLED = 0x0001;
	public static final int CONDITIONAL = 0x0002;

	public static final int ENTRY = 0x0010;
	public static final int EXIT = 0x0020;

	protected int flags;
	private ImageDescriptor baseImage;
	private Point size;

	/**
	 * @param baseImage
	 * @param flags
	 */
	public JSDebugImageDescriptor(ImageDescriptor baseImage, int flags) {
		this.baseImage = baseImage;
		this.flags = flags;
	}

	private ImageDescriptor getBaseImage() {
		return baseImage;
	}

	private int getFlags() {
		return flags;
	}

	protected Point getSize() {
		if (size == null) {
			ImageData data = getBaseImage().getImageData();
			size = new Point(data.width, data.height);
		}
		return size;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object instanceof JSDebugImageDescriptor) {
			JSDebugImageDescriptor other = (JSDebugImageDescriptor) object;
			return getBaseImage().equals(other.getBaseImage()) && getFlags() == other.getFlags();
		}
		return false;
	}

	public int hashCode() {
		return getBaseImage().hashCode() | getFlags();
	}

	protected void drawCompositeImage(int width, int height) {
		ImageData bg = getBaseImage().getImageData();
		if (bg == null) {
			bg = DEFAULT_IMAGE_DATA;
		}
		drawImage(bg, 0, 0);
		drawOverlays();
	}

	private ImageData getImageData(String imageDescriptorKey) {
		return DebugUIImages.getImageDescriptor(imageDescriptorKey).getImageData();
	}

	/**
	 * Add any overlays to the image as specified in the flags.
	 */
	private void drawOverlays() {
		drawBreakpointOverlays();
	}

	/**
	 * drawBreakpointOverlays
	 */
	private void drawBreakpointOverlays() {
		int flags = getFlags();
		int x = 0;
		int y = 0;
		ImageData data = null;
		if ((flags & CONDITIONAL) != 0) {
			if ((flags & ENABLED) != 0) {
				data = getImageData(DebugUIImages.IMG_OVR_CONDITIONAL_BREAKPOINT);
			} else {
				data = getImageData(DebugUIImages.IMG_OVR_CONDITIONAL_BREAKPOINT_DISABLED);
			}
			drawImage(data, x, y);
		}
		if ((flags & ENTRY) != 0) {
			x = getSize().x;
			y = 0;
			if ((flags & ENABLED) != 0) {
				data = getImageData(DebugUIImages.IMG_OVR_METHOD_BREAKPOINT_ENTRY);
			} else {
				data = getImageData(DebugUIImages.IMG_OVR_METHOD_BREAKPOINT_ENTRY_DISABLED);
			}
			x -= data.width;
			drawImage(data, x, y);
		}
		if ((flags & EXIT) != 0) {
			x = getSize().x;
			y = getSize().y;
			if ((flags & ENABLED) != 0) {
				data = getImageData(DebugUIImages.IMG_OVR_METHOD_BREAKPOINT_EXIT);
			} else {
				data = getImageData(DebugUIImages.IMG_OVR_METHOD_BREAKPOINT_EXIT_DISABLED);
			}
			x -= data.width;
			y -= data.height;
			drawImage(data, x, y);
		}
	}
}
