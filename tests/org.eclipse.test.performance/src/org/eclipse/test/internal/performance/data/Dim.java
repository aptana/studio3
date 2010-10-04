/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.test.internal.performance.data;

import org.eclipse.test.internal.performance.InternalDimensions;
import org.eclipse.test.internal.performance.PerformanceTestPlugin;
import org.eclipse.test.performance.Dimension;

/**
 * @since 3.1
 */
public class Dim implements Dimension {

    private static Dim[] fgRegisteredDimensions= new Dim[100];

    private final int fId;
	private final Unit fUnit;
	private final int fMultiplier;
	private String shortName;

	public static Dim getDimension(int id) {
        InternalDimensions.COMITTED.getId();	// trigger loading class InternalDimensions
	    if (id >= 0 && id < fgRegisteredDimensions.length)
	        return fgRegisteredDimensions[id];
	    return null;
	}

	public Dim(int id) {
		this(id, Unit.CARDINAL, 1);
	}

	public Dim(int id, Unit unit) {
		this(id, unit, 1);
	}

	public Dim(int id, Unit unit, int multiplier) {

	    if (id >= 0 && id < fgRegisteredDimensions.length) {
		    if (fgRegisteredDimensions[id] == null) {
		        fgRegisteredDimensions[id]= this;
		    } else
			    PerformanceTestPlugin.logError("dimension with id '" + id + "' already registered"); //$NON-NLS-1$ //$NON-NLS-2$
	    }
		this.fId= id;
		this.fUnit= unit;
		this.fMultiplier= multiplier;
	}

    public int getId() {
        return this.fId;
    }

	public Unit getUnit() {
		return this.fUnit;
	}

	public int getMultiplier() {
		return this.fMultiplier;
	}

	public String getName() {
		return DimensionMessages.getString(this.fId);
	}

	/**
	 * Returns the label for the current dimension.
	 * This label is done keeping only uppercase characters from the name.
	 * It's typically used for anchor references based on dimension.
	 *
	 * @return The short name of the dimension
	 */
	public String getLabel() {
		if (this.shortName == null) {
			String name = getName();
			StringBuffer buffer = new StringBuffer();
			int length = name.length();
			for (int i=0; i<length; i++) {
				if (Character.isUpperCase(name.charAt(i))) {
					buffer.append(name.charAt(i));
				}
			}
			this.shortName = buffer.toString();
		}
		return this.shortName;
	}

	public String getDescription() {
		return DimensionMessages.getString(this.fId);
	}

	public String toString() {
		return "Dimension [name=" + getName() + ", " + this.fUnit + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public String getDisplayValue(Scalar scalar) {
		return this.fUnit.getDisplayValue1(scalar.getMagnitude(), this.fMultiplier);
	}

	public String getDisplayValue(double scalar) {
		return this.fUnit.getDisplayValue1(scalar / this.fMultiplier);
	}
}
