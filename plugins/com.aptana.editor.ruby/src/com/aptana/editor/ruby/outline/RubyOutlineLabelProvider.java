/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
package com.aptana.editor.ruby.outline;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.editor.ruby.RubyEditorPlugin;
import com.aptana.editor.ruby.core.IRubyElement;
import com.aptana.editor.ruby.core.IRubyMethod;
import com.aptana.editor.ruby.core.IRubyMethod.Visibility;
import com.aptana.editor.ruby.core.IRubyType;

public class RubyOutlineLabelProvider extends LabelProvider
{

	static final Image CLASS = RubyEditorPlugin.getImage("icons/class_obj.png"); //$NON-NLS-1$
	private static final Image MODULE = RubyEditorPlugin.getImage("icons/module_obj.png"); //$NON-NLS-1$
	private static final Image METHOD_PUBLIC = RubyEditorPlugin.getImage("icons/method_public_obj.png"); //$NON-NLS-1$
	private static final Image METHOD_PROTECTED = RubyEditorPlugin.getImage("icons/method_protected_obj.png"); //$NON-NLS-1$
	private static final Image METHOD_PRIVATE = RubyEditorPlugin.getImage("icons/method_private_obj.png"); //$NON-NLS-1$
	private static final Image METHOD_SINGLETON = RubyEditorPlugin.getImage("icons/class_method.png"); //$NON-NLS-1$
	static final Image METHOD_CONSTRUCTOR = RubyEditorPlugin.getImage("icons/constructor.png"); //$NON-NLS-1$
	private static final Image CLASS_VAR = RubyEditorPlugin.getImage("icons/class_var_obj.png"); //$NON-NLS-1$
	private static final Image CONSTANT = RubyEditorPlugin.getImage("icons/constant_obj.png"); //$NON-NLS-1$
	private static final Image GLOBAL = RubyEditorPlugin.getImage("icons/global_obj.png"); //$NON-NLS-1$
	static final Image INSTANCE_VAR = RubyEditorPlugin.getImage("icons/instance_var_obj.png"); //$NON-NLS-1$
	static final Image LOCAL_VAR = RubyEditorPlugin.getImage("icons/local_var_obj.png"); //$NON-NLS-1$
	private static final Image IMPORT_DECLARATION = RubyEditorPlugin.getImage("icons/import_obj.png"); //$NON-NLS-1$
	private static final Image IMPORT_CONTAINER = RubyEditorPlugin.getImage("icons/import_container_obj.png"); //$NON-NLS-1$

	@Override
	public Image getImage(Object element)
	{
		if (element instanceof CommonOutlineItem)
		{
			return getImage(((CommonOutlineItem) element).getReferenceNode());
		}
		if (element instanceof IRubyType)
		{
			return ((IRubyType) element).isModule() ? MODULE : CLASS;
		}
		else if (element instanceof IRubyMethod)
		{
			IRubyMethod method = (IRubyMethod) element;
			if (method.isSingleton())
			{
				return METHOD_SINGLETON;
			}
			if (method.isConstructor())
			{
				return METHOD_CONSTRUCTOR;
			}
			Visibility visibility = method.getVisibility();
			switch (visibility)
			{
				case PUBLIC:
					return METHOD_PUBLIC;
				case PROTECTED:
					return METHOD_PROTECTED;
				case PRIVATE:
					return METHOD_PRIVATE;
			}
		}
		else if (element instanceof IRubyElement)
		{
			short type = ((IRubyElement) element).getNodeType();
			switch (type)
			{
				case IRubyElement.CLASS_VAR:
					return CLASS_VAR;
				case IRubyElement.CONSTANT:
					return CONSTANT;
				case IRubyElement.GLOBAL:
					return GLOBAL;
				case IRubyElement.INSTANCE_VAR:
					return INSTANCE_VAR;
				case IRubyElement.LOCAL_VAR:
				case IRubyElement.DYNAMIC_VAR: // TODO Make dynamic variable have its own image
					return LOCAL_VAR;
				case IRubyElement.IMPORT_DECLARATION:
					return IMPORT_DECLARATION;
				case IRubyElement.IMPORT_CONTAINER:
					return IMPORT_CONTAINER;
			}
		}
		return super.getImage(element);
	}

	@Override
	public String getText(Object element)
	{
		if (element instanceof CommonOutlineItem)
		{
			return getText(((CommonOutlineItem) element).getReferenceNode());
		}
		return super.getText(element);
	}
}
