/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.internal.model;

import java.net.URI;
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
	public URI getLocation() {
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

	/* package */void insertElement(JSDebugScriptElement scriptElement) {
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

	/* package */void removeElement(JSDebugScriptElement scriptElement) {
		/* TODO: remove element */
	}

	/* package */boolean isScriptElementInside(JSDebugScriptElement element) {
		return baseLine < /* ! */element.getBaseLine()
				&& (element.getBaseLine() + element.getLineExtent()) <= (baseLine + lineExtent);
	}

	/* package */void setParent(IJSScriptElement parent) {
		this.parent = parent;
	}

	/* package */void setName(String name) {
		this.name = name;
	}
}
