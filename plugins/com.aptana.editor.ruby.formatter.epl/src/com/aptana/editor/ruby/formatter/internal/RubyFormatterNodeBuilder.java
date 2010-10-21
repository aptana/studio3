/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *     Aptana Inc. - Modify it to work with org.jrubyparser.parser AST (Shalom Gibly)
 *******************************************************************************/
package com.aptana.editor.ruby.formatter.internal;

import org.jrubyparser.parser.ParserResult;

import com.aptana.editor.ruby.formatter.internal.nodes.FormatterRootNode;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.nodes.AbstractFormatterNodeBuilder;
import com.aptana.formatter.nodes.IFormatterContainerNode;

/**
 * Ruby Formatter node builder.
 * 
 * @author Xored, Shalom Gibly [Aptana]
 */
public class RubyFormatterNodeBuilder extends AbstractFormatterNodeBuilder
{

	public IFormatterContainerNode build(final ParserResult result, final IFormatterDocument document)
	{
		final IFormatterContainerNode root = new FormatterRootNode(document);
		start(root);
		result.getAST().accept(new RubyFormatterNodeBuilderVisitor(document, this));
		checkedPop(root, document.getLength());
		return root;
	}
}
