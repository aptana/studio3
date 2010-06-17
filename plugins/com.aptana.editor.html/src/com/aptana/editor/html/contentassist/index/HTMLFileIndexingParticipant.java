package com.aptana.editor.html.contentassist.index;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;

import com.aptana.core.util.IOUtil;
import com.aptana.editor.common.resolver.IPathResolver;
import com.aptana.editor.common.resolver.URIResolver;
import com.aptana.editor.css.contentassist.index.CSSFileIndexingParticipant;
import com.aptana.editor.css.contentassist.index.CSSIndexConstants;
import com.aptana.editor.css.parsing.ICSSParserConstants;
import com.aptana.editor.html.Activator;
import com.aptana.editor.html.IHTMLConstants;
import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
import com.aptana.editor.html.parsing.ast.HTMLNode;
import com.aptana.editor.html.parsing.ast.HTMLSpecialNode;
import com.aptana.index.core.IFileStoreIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.parsing.IParser;
import com.aptana.parsing.IParserPool;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseNode;

public class HTMLFileIndexingParticipant implements IFileStoreIndexingParticipant
{
	private static final String[] HTML_EXTENSIONS = { "html", "htm" }; //$NON-NLS-1$ //$NON-NLS-2$

	private static final String ELEMENT_LINK = "link"; //$NON-NLS-1$
	private static final String ELEMENT_SCRIPT = "script"; //$NON-NLS-1$
	private static final String ATTRIBUTE_HREF = "href"; //$NON-NLS-1$
	private static final String ATTRIBUTE_SRC = "src"; //$NON-NLS-1$

	@Override
	public void index(Set<IFileStore> files, Index index, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, files.size());
		for (IFileStore file : files)
		{
			if (sub.isCanceled())
			{
				return;
			}
			try
			{
				if (file == null || !isHTMLFile(file))
				{
					continue;
				}
				sub.subTask(file.getName());
				try
				{
					IParserPool pool = ParserPoolFactory.getInstance().getParserPool(HTMLNode.LANGUAGE);
					if (pool != null)
					{
						String fileContents = IOUtil.read(file.openInputStream(EFS.NONE, sub.newChild(-1)));
						if (fileContents != null && fileContents.trim().length() > 0)
						{
							IParser htmlParser = pool.checkOut();
							if (htmlParser != null)
							{

								HTMLParseState parseState = new HTMLParseState();
								parseState.setEditState(fileContents, "", 0, 0); //$NON-NLS-1$
								IParseNode parseNode = htmlParser.parse(parseState);
								pool.checkIn(htmlParser);
								walkNode(index, file, parseNode);
							}
						}
					}
				}
				catch (Throwable e)
				{
					Activator.logError(
							MessageFormat.format(Messages.HTMLFileIndexingParticipant_Error_During_Indexing,
									file.getName()), e);
				}
			}
			finally
			{
				sub.worked(1);
			}
		}
		sub.done();
	}

	private boolean isHTMLFile(IFileStore file)
	{
		InputStream stream = null;
		IContentTypeManager manager = Platform.getContentTypeManager();
		try
		{
			stream = file.openInputStream(EFS.NONE, new NullProgressMonitor());
			IContentType[] types = manager.findContentTypesFor(stream, file.getName());
			for (IContentType type : types)
			{
				if (type.getId().equals(IHTMLConstants.CONTENT_TYPE_HTML))
					return true;
			}
		}
		catch (CoreException e)
		{
			Activator.logError(e);
		}
		catch (Exception e)
		{
			Activator.logError(e.getMessage(), e);
		}
		finally
		{
			try
			{
				if (stream != null)
					stream.close();
			}
			catch (IOException e)
			{
				// ignore
			}
		}
		// fall back to file extensions
		String fileExtension = new Path(file.getName()).getFileExtension();
		return (HTML_EXTENSIONS[0].equalsIgnoreCase(fileExtension) || HTML_EXTENSIONS[1]
				.equalsIgnoreCase(fileExtension));
	}

	public static void walkNode(Index index, IFileStore file, IParseNode parent)
	{
		if (parent == null)
			return;

		if (parent instanceof HTMLSpecialNode)
		{
			HTMLSpecialNode htmlSpecialNode = (HTMLSpecialNode) parent;
			IParseNode child = htmlSpecialNode.getChild(0);
			if (child != null)
			{
				String language = child.getLanguage();
				if (ICSSParserConstants.LANGUAGE.equals(language))
				{
					CSSFileIndexingParticipant.walkNode(index, file, child);
				}
			}
			if (htmlSpecialNode.getName().equalsIgnoreCase(ELEMENT_SCRIPT))
			{
				String jsSource = htmlSpecialNode.getAttributeValue(ATTRIBUTE_SRC);
				if (jsSource != null)
				{

					IPathResolver resolver = new URIResolver(file.toURI());
					URI resolved = resolver.resolveURI(jsSource);
					addIndex(index, file, HTMLIndexConstants.RESOURCE_JS, resolved.toString());
				}
			}
		}
		else if (parent instanceof HTMLElementNode)
		{
			HTMLElementNode element = (HTMLElementNode) parent;
			String cssClass = element.getCSSClass();
			if (cssClass != null && cssClass.trim().length() > 0)
			{
				StringTokenizer tokenizer = new StringTokenizer(cssClass);
				while (tokenizer.hasMoreTokens())
					addIndex(index, file, CSSIndexConstants.CLASS, tokenizer.nextToken());
			}
			String id = element.getID();
			if (id != null && id.trim().length() > 0)
			{
				addIndex(index, file, CSSIndexConstants.IDENTIFIER, id);
			}
			if (element.getName().equalsIgnoreCase(ELEMENT_LINK))
			{
				String cssLink = element.getAttributeValue(ATTRIBUTE_HREF);
				if (cssLink != null)
				{
					IPathResolver resolver = new URIResolver(file.toURI());
					URI resolved = resolver.resolveURI(cssLink);
					addIndex(index, file, HTMLIndexConstants.RESOURCE_CSS, resolved.toString());
				}
			}
		}

		for (IParseNode child : parent.getChildren())
		{
			walkNode(index, file, child);
		}
	}

	private static void addIndex(Index index, IFileStore file, String category, String word)
	{
		index.addEntry(category, word, file.toURI().getPath());
	}

}
