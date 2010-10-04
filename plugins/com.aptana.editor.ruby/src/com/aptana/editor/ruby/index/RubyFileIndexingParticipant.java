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
package com.aptana.editor.ruby.index;

import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.jrubyparser.ast.Node;
import org.jrubyparser.parser.ParserResult;

import com.aptana.core.util.IOUtil;
import com.aptana.editor.ruby.RubyEditorPlugin;
import com.aptana.editor.ruby.parsing.IRubyParserConstants;
import com.aptana.editor.ruby.parsing.ISourceElementRequestor;
import com.aptana.editor.ruby.parsing.RubyParser;
import com.aptana.editor.ruby.parsing.RubySourceParser;
import com.aptana.editor.ruby.parsing.SourceElementVisitor;
import com.aptana.index.core.IFileStoreIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.parsing.IParserPool;
import com.aptana.parsing.ParserPoolFactory;

public class RubyFileIndexingParticipant implements IFileStoreIndexingParticipant
{

	public void index(Set<IFileStore> files, final Index index, IProgressMonitor monitor) throws CoreException
	{
		SubMonitor sub = SubMonitor.convert(monitor, files.size() * 100);
		for (final IFileStore store : files)
		{
			if (sub.isCanceled())
			{
				throw new CoreException(Status.CANCEL_STATUS);
			}
			Thread.yield(); // be nice to other threads, let them get in before each file...
			indexFileStore(index, store, sub.newChild(100));
		}
	}

	private void indexFileStore(final Index index, IFileStore store, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		if (store == null)
		{
			return;
		}
		try
		{
			sub.subTask(store.toString());

			// grab the source of the file we're going to parse
			String source = IOUtil.read(store.openInputStream(EFS.NONE, sub.newChild(20)));

			// minor optimization when creating a new empty file
			if (source == null || source.length() <= 0)
			{
				return;
			}

			// create parser and associated parse state
			IParserPool pool = ParserPoolFactory.getInstance().getParserPool(IRubyParserConstants.LANGUAGE);

			if (pool != null)
			{
				RubyParser parser = (RubyParser) pool.checkOut();

				RubySourceParser sourceParser = parser.getSourceParser();
				ParserResult result = sourceParser.parse(store.getName(), source);

				pool.checkIn(parser);
				sub.worked(50);

				Node root = result.getAST();
				ISourceElementRequestor builder = new RubySourceIndexer(index, store.toURI());
				SourceElementVisitor visitor = new SourceElementVisitor(builder);
				visitor.acceptNode(root);
			}
		}
		catch (Throwable e)
		{
			RubyEditorPlugin.log(e);
		}
		finally
		{
			sub.done();
		}
	}

}
