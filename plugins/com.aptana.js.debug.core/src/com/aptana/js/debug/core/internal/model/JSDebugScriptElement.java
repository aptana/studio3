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
package com.aptana.js.debug.core.internal.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.model.IDebugTarget;

import com.aptana.js.debug.core.internal.model.JSDebugElement;
import com.aptana.js.debug.core.model.IJSScriptElement;

/**
 * @author Max Stepanov
 */
public class JSDebugScriptElement extends JSDebugElement implements IJSScriptElement {

	private static final IJSScriptElement[] EMPTY = new IJSScriptElement[0];

	private IJSScriptElement parent;
	private String name;
	private final int baseLine;
	private final int lineExtent;
	private List<IJSScriptElement> children = new ArrayList<IJSScriptElement>();

	/**
	 * JSDebugScriptElement
	 * 
	 * @param target
	 * @param name
	 * @param baseLine
	 * @param lineExtent
	 */
	public JSDebugScriptElement(IDebugTarget target, String name, int baseLine, int lineExtent) {
		super(target);
		this.name = name;
		this.baseLine = baseLine;
		this.lineExtent = lineExtent;
	}

	/*
	 * @see com.aptana.js.debug.core.model.IJSScriptElement#getName()
	 */
	public String getName() {
		return name;
	}

	/*
	 * @see com.aptana.js.debug.core.model.IJSScriptElement#getLocation()
	 */
	public String getLocation() {
		return getParent().getLocation();
	}

	/*
	 * @see com.aptana.js.debug.core.model.IJSScriptElement#getChildren()
	 */
	public IJSScriptElement[] getChildren() {
		if (children.size() > 0) {
			return (IJSScriptElement[]) children.toArray(new IJSScriptElement[children.size()]);
		}
		return EMPTY;
	}

	/*
	 * @see com.aptana.js.debug.core.model.IJSScriptElement#getParent()
	 */
	public IJSScriptElement getParent() {
		return parent;
	}

	/*
	 * @see com.aptana.js.debug.core.model.IJSScriptElement#getBaseLine()
	 */
	public int getBaseLine() {
		return baseLine;
	}

	/*
	 * @see com.aptana.js.debug.core.model.IJSScriptElement#getLineExtent()
	 */
	public int getLineExtent() {
		return lineExtent;
	}

	/* package */ void insertElement(JSDebugScriptElement scriptElement) {
		for (IJSScriptElement i : children) {
			JSDebugScriptElement element = (JSDebugScriptElement) i; 
			if (element.isScriptElementInside(scriptElement)) {
				element.insertElement(scriptElement);
				return;
			} else if (scriptElement.isScriptElementInside(element)) {
				children.remove(element);
				scriptElement.insertElement(element);
				break;
			}
		}
		children.add(scriptElement);
		scriptElement.setParent(this);
	}

	/* package */ void removeElement(JSDebugScriptElement scriptElement) {
		/* TODO: remove element */
	}

	/* package */ boolean isScriptElementInside(JSDebugScriptElement element) {
		return baseLine < /* ! */element.getBaseLine()
				&& (element.getBaseLine() + element.getLineExtent()) <= (baseLine + lineExtent);
	}

	/* package */ void setParent(IJSScriptElement parent) {
		this.parent = parent;
	}

	/* package */ void setName(String name) {
		this.name = name;
	}
}
