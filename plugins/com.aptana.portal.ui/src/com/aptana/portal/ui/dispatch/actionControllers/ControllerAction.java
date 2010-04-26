package com.aptana.portal.ui.dispatch.actionControllers;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Describes an 'ControllerAction' annotation. Mark all action methods with this annotation.
 */
@Target( { METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ControllerAction
{
	String name() default "";
}