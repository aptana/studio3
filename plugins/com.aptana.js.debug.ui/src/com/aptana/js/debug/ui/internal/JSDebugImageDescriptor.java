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
	 * 
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
		if (object instanceof JSDebugImageDescriptor) {
			JSDebugImageDescriptor other = (JSDebugImageDescriptor) object;
			return (getBaseImage().equals(other.getBaseImage()) && getFlags() == other.getFlags());
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
