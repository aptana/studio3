/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;

/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlExtension;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public abstract class AbstractInformationControlManager extends
		org.eclipse.jface.text.AbstractInformationControlManager
{

	private int fVerticalOffset;

	/** The information to be presented */
	private String fInformation;

	/** Indicates whether the size constraints should be enforced as minimal control size */
	private boolean fEnforceAsMinimalSize = false;

	/** Indicates whether the size constraints should be enforced as maximal control size */
	private boolean fEnforceAsMaximalSize = false;

	/** The anchor for laying out the information control in relation to the subject control */
	private Anchor fAnchor = ANCHOR_BOTTOM;

	/** The anchor computed after laying out the control */
	private Anchor fComputedAnchor = fAnchor;

	protected AbstractInformationControlManager(IInformationControlCreator creator)
	{
		super(creator);
	}

	@Override
	public void setSizeConstraints(int widthInChar, int heightInChar, boolean enforceAsMinimalSize,
			boolean enforceAsMaximalSize)
	{
		super.setSizeConstraints(widthInChar, heightInChar, enforceAsMinimalSize, enforceAsMaximalSize);
		fEnforceAsMinimalSize = enforceAsMinimalSize;
		fEnforceAsMaximalSize = enforceAsMaximalSize;
	}

	@Override
	public void setAnchor(Anchor anchor)
	{
		super.setAnchor(anchor);
		fAnchor = anchor;
	}

	protected final void setInformation(String information, Rectangle subjectArea, int verticalOffset)
	{
		fVerticalOffset = verticalOffset;
		fInformation = information;
		setInformation(information, subjectArea);
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

	@Override
	protected void doShowInformation()
	{
		super.doShowInformation();
		fInformation = null;
	}

	@Override
	protected void hideInformationControl()
	{
		super.hideInformationControl();
		fInformation = null;
	}

	@Override
	protected Point computeInformationControlLocation(Rectangle subjectArea, Point controlSize)
	{
		Rectangle displayBounds = getSubjectControl().getDisplay().getClientArea();

		Point upperLeft;
		fComputedAnchor = fAnchor;
		do
		{
			upperLeft = computeLocation(subjectArea, controlSize, fComputedAnchor);
			if (updateLocation(upperLeft, controlSize, displayBounds, fComputedAnchor))
			{
				break;
			}
			fComputedAnchor = getNextFallbackAnchor(fComputedAnchor);
		}
		while (fComputedAnchor != fAnchor && fComputedAnchor != null);

		return upperLeft;
	}

	/**
	 * Opens the information control with the given information and the specified subject area. It also activates the
	 * information control closer.
	 * 
	 * @param subjectArea
	 *            the information area
	 * @param information
	 *            the information
	 */
	private void internalShowInformationControl(Rectangle subjectArea, String information)
	{
		IInformationControl informationControl = getInformationControl();
		if (informationControl != null)
		{
			Point sizeConstraints = computeSizeConstraints(getSubjectControl(), getSubjectArea(), informationControl);
			informationControl.setSizeConstraints(sizeConstraints.x, sizeConstraints.y);

			if (informationControl instanceof IInformationControlExtension2)
			{
				((IInformationControlExtension2) informationControl).setInput(information);
			}
			else
			{
				informationControl.setInformation(information.toString());
			}

			if (informationControl instanceof IInformationControlExtension)
			{
				IInformationControlExtension extension = (IInformationControlExtension) informationControl;
				if (!extension.hasContents())
				{
					return;
				}
			}

			Point size = null;
			Point location = null;
			Rectangle bounds = restoreInformationControlBounds();

			if (bounds != null)
			{
				if (bounds.x > -1 && bounds.y > -1)
				{
					location = new Point(bounds.x, bounds.y);
				}

				if (bounds.width > -1 && bounds.height > -1)
				{
					size = new Point(bounds.width, bounds.height);
				}
			}

			if (size == null)
			{
				size = informationControl.computeSizeHint();
			}

			if (fEnforceAsMinimalSize)
			{
				if (size.x < sizeConstraints.x)
				{
					size.x = sizeConstraints.x;
				}
				if (size.y < sizeConstraints.y)
				{
					size.y = sizeConstraints.y;
				}
			}

			if (fEnforceAsMaximalSize)
			{
				if (size.x > sizeConstraints.x)
				{
					size.x = sizeConstraints.x;
				}
				if (size.y > sizeConstraints.y)
				{
					size.y = sizeConstraints.y;
				}
			}

			informationControl.setSize(size.x + 5, size.y);

			if (location == null)
			{
				location = computeInformationControlLocation(subjectArea, size);
			}

			// TableItem[] selection = ((Table) fSubjectControl).getSelection();
			//
			// if (selection != null)
			// {
			// TableItem item = selection[0];
			// Rectangle parentBounds = fSubjectControl.getParent().getBounds();
			// Rectangle itemBounds = item.getBounds(0);
			//
			// fVerticalOffset = parentBounds.y + itemBounds.y;
			//
			// int parentBottom = parentBounds.y + parentBounds.height;
			//
			// TODO: replace "size.y" with this info control's height to get this code to work
			// int bottom = fVerticalOffset + size.y;
			//
			// if (bottom > parentBottom )
			// {
			// fVerticalOffset -= (bottom - parentBottom);
			// }
			// }

			if ((fComputedAnchor == ANCHOR_LEFT || fComputedAnchor == ANCHOR_RIGHT) && fVerticalOffset != 0)
			{
				location.y = fVerticalOffset;
			}

			informationControl.setLocation(location);

			showInformationControl(subjectArea);
		}
	}
}
