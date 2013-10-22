/*******************************************************************************
 *  Copyright (c) 2008, 2010 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      IBM Corporation - initial API and implementation
 *      Cloudsmith Inc. - converted into expression based query
 *******************************************************************************/
package org.eclipse.equinox.internal.p2.director;

import org.eclipse.equinox.p2.query.ExpressionMatchQuery;

import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.IInstallableUnitPatch;
import org.eclipse.equinox.p2.metadata.expression.ExpressionUtil;
import org.eclipse.equinox.p2.metadata.expression.IExpression;

/**
 * A query that accepts any patch that applies to a given installable unit.
 */
public class ApplicablePatchQuery extends ExpressionMatchQuery<IInstallableUnit> {
	private static final IExpression applicablePatches = ExpressionUtil.parse(//
			"applicabilityScope.empty || applicabilityScope.exists(rqArr | rqArr.all(rq | $0 ~= rq))"); //$NON-NLS-1$

	/**
	 * Creates a new patch query on the given installable unit. Patches that can
	 * be applied to this unit will be accepted as matches by the query.
	 * @param iu The unit to compare patches against
	 */
	public ApplicablePatchQuery(IInstallableUnit iu) {
		super(IInstallableUnitPatch.class, applicablePatches, iu);
	}
}
