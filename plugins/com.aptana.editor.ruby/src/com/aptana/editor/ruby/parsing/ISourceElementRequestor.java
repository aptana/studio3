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
package com.aptana.editor.ruby.parsing;

import com.aptana.editor.ruby.core.IRubyMethod.Visibility;

public interface ISourceElementRequestor
{

	public static class TypeInfo
	{
		public int declarationStart;
		public boolean isModule;
		public String name;
		public int nameSourceStart;
		public int nameSourceEnd;
		public String superclass;
		public String[] modules;
		public boolean secondary;
	}

	public static class MethodInfo
	{
		public boolean isConstructor;
		public boolean isClassLevel;
		public Visibility visibility;
		public int declarationStart;
		public String name;
		public int nameSourceStart;
		public int nameSourceEnd;
		public String[] parameterNames;
		public String[] blockVars;
	}

	public static class FieldInfo
	{
		public int declarationStart;
		public String name;
		public boolean isDynamic;
		public int nameSourceStart;
		public int nameSourceEnd;
	}

	public void enterMethod(MethodInfo methodInfo);

	public void enterConstructor(MethodInfo constructor);

	public void enterField(FieldInfo fieldInfo);

	public void enterType(TypeInfo typeInfo);

	public void enterScript();

	public void exitMethod(int endOffset);

	public void exitConstructor(int endOffset);

	public void exitField(int endOffset);

	public void exitType(int endOffset);

	public void exitScript(int endOffset);

	public void acceptMethodReference(String name, int argCount, int offset);

	public void acceptConstructorReference(String name, int argCount, int offset);

	public void acceptFieldReference(String name, int offset);

	public void acceptTypeReference(String name, int startOffset, int endOffset);

	public void acceptImport(String value, int startOffset, int endOffset);

	public void acceptMixin(String string);

	public void acceptModuleFunction(String function);

	public void acceptMethodVisibilityChange(String methodName, Visibility visibility);

	public void acceptYield(String name);

	public void enterBlock(int startOffset, int endOffset);

	public void exitBlock(int endOffset);
}
