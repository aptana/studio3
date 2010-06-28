package com.aptana.editor.ruby.outline;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.editor.ruby.Activator;
import com.aptana.editor.ruby.core.IRubyElement;
import com.aptana.editor.ruby.core.IRubyMethod;
import com.aptana.editor.ruby.core.IRubyType;
import com.aptana.editor.ruby.core.IRubyMethod.Visibility;

public class RubyOutlineLabelProvider extends LabelProvider
{

	private static final Image CLASS = Activator.getImage("icons/class_obj.png"); //$NON-NLS-1$
	private static final Image MODULE = Activator.getImage("icons/module_obj.png"); //$NON-NLS-1$
	private static final Image METHOD_PUBLIC = Activator.getImage("icons/method_public_obj.png"); //$NON-NLS-1$
	private static final Image METHOD_PROTECTED = Activator.getImage("icons/method_protected_obj.png"); //$NON-NLS-1$
	private static final Image METHOD_PRIVATE = Activator.getImage("icons/method_private_obj.png"); //$NON-NLS-1$
	private static final Image CLASS_VAR = Activator.getImage("icons/class_var_obj.gif"); //$NON-NLS-1$
	private static final Image CONSTANT = Activator.getImage("icons/constant_obj.gif"); //$NON-NLS-1$
	private static final Image GLOBAL = Activator.getImage("icons/global_obj.png"); //$NON-NLS-1$
	private static final Image INSTANCE_VAR = Activator.getImage("icons/instance_var_obj.gif"); //$NON-NLS-1$
	private static final Image LOCAL_VAR = Activator.getImage("icons/local_var_obj.gif"); //$NON-NLS-1$
	private static final Image IMPORT_DECLARATION = Activator.getImage("icons/import_obj.gif"); //$NON-NLS-1$
	private static final Image IMPORT_CONTAINER = Activator.getImage("icons/import_container_obj.gif"); //$NON-NLS-1$

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
			Visibility visibility = ((IRubyMethod) element).getVisibility();
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
