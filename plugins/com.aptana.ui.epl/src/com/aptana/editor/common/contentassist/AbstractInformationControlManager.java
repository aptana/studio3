/**
 * Copyright (c) 2005-2010 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.editor.common.contentassist;

import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public abstract class AbstractInformationControlManager extends
		org.eclipse.jface.text.AbstractInformationControlManager
{

	protected AbstractInformationControlManager(IInformationControlCreator creator)
	{
		super(creator);
	}

	@Override
	protected boolean updateLocation(Point location, Point size, Rectangle displayArea, Anchor anchor)
	{
		int displayLowerRightX = displayArea.x + displayArea.width;
		int displayLowerRightY = displayArea.y + displayArea.height;
		int lowerRightX = location.x + size.x;
		int lowerRightY = location.y + size.y;

		if (ANCHOR_BOTTOM == anchor || ANCHOR_TOP == anchor)
		{
			// [IM] Commented out to allow the window to go off the bottom edge of the screen.
			/*
			 * if (ANCHOR_BOTTOM == anchor) { if (lowerRightY > displayLowerRightY) return false; } else { if
			 * (location.y < displayArea.y) return false; }
			 */

			if (lowerRightX > displayLowerRightX)
			{
				location.x = location.x - (lowerRightX - displayLowerRightX);
			}

			return (location.x >= 0 && location.y >= 0);
		}
		if (ANCHOR_RIGHT == anchor || ANCHOR_LEFT == anchor)
		{
			if (ANCHOR_RIGHT == anchor)
			{
				// [PC]: Check to see that if we were to move the anchor to left, would it go off the screen anyway, if
				// so, let's just keep right
				// [PC]: The asssumption right now (hence the '* 3') is that the tooltip is as large as the completion
				// proposoal, and as of 7/23/07, that is the case
				if ((location.x - (size.x * 3)) < 0)
				{
					return true;
				}

				if (lowerRightX > displayLowerRightX)
				{
					return false;
				}
			}
			else
			{
				if (location.x < displayArea.x)
				{
					return false;
				}
			}

			if (lowerRightY > displayLowerRightY)
			{
				location.y = location.y - (lowerRightY - displayLowerRightY);
			}

			return (location.x >= 0 && location.y >= 0);
		}
		if (ANCHOR_GLOBAL == anchor)
		{
			if (lowerRightX > displayLowerRightX)
			{
				location.x = location.x - (lowerRightX - displayLowerRightX);
			}

			if (lowerRightY > displayLowerRightY)
			{
				location.y = location.y - (lowerRightY - displayLowerRightY);
			}

			return (location.x >= 0 && location.y >= 0);
		}

		return false;
	}
}
