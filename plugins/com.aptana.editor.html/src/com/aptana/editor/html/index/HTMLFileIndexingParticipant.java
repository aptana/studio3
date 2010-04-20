package com.aptana.editor.html.index;

import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import com.aptana.editor.css.index.CSSFileIndexingParticipant;
import com.aptana.editor.css.index.IIndexConstants;
import com.aptana.editor.css.parsing.ICSSParserConstants;
import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.editor.html.parsing.HTMLParser;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
import com.aptana.editor.html.parsing.ast.HTMLSpecialNode;
import com.aptana.index.core.IFileIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.util.IOUtil;

public class HTMLFileIndexingParticipant implements IFileIndexingParticipant
{
	private static final String[] HTML_EXTENSIONS = { "html", "htm" }; //$NON-NLS-1$ //$NON-NLS-2$

	private static final String ELEMENT_LINK = "link"; //$NON-NLS-1$
	private static final String ELEMENT_SCRIPT = "script"; //$NON-NLS-1$
	private static final String ATTRIBUTE_HREF = "href"; //$NON-NLS-1$
	private static final String ATTRIBUTE_SRC = "src"; //$NON-NLS-1$

	public HTMLFileIndexingParticipant()
	{
	}

	@Override
	public void index(Set<IFile> files, Index index, IProgressMonitor monitor)
	{
		for (IFile file : files)
		{
			if (monitor.isCanceled())
			{
				return;
			}
			String fileExtension = file.getFileExtension();
			if (HTML_EXTENSIONS[0].equalsIgnoreCase(fileExtension)
					|| HTML_EXTENSIONS[1].equalsIgnoreCase(fileExtension))
			{
				try
				{
					String fileContents = IOUtil.read(file.getContents());
					HTMLParseState parseState = new HTMLParseState();
					parseState.setEditState(fileContents, "", 0, 0); //$NON-NLS-1$
					HTMLParser htmlParser = new HTMLParser();
					IParseNode parseNode = htmlParser.parse(parseState);
					walkNode(index, file, parseNode);
				}
				catch (CoreException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static void walkNode(Index index, IFile file, IParseNode parent)
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
			if (htmlSpecialNode.getName().equals(ELEMENT_SCRIPT))
			{
				String jsSource = htmlSpecialNode.getAttributeValue(ATTRIBUTE_SRC);
				if (jsSource != null)
				{
					IFile jsFile = file.getParent().getFile(new Path(jsSource));
					if (jsFile.exists())
					{
						addIndex(index, file, IHTMLIndexConstants.RESOURCE_JS, jsFile.getProjectRelativePath()
								.toPortableString());
					}
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
					addIndex(index, file, IIndexConstants.CSS_CLASS, tokenizer.nextToken());
			}
			String id = element.getID();
			if (id != null && id.trim().length() > 0)
			{
				addIndex(index, file, IIndexConstants.CSS_IDENTIFIER, id);
			}
			if (element.getName().equals(ELEMENT_LINK))
			{
				String cssLink = element.getAttributeValue(ATTRIBUTE_HREF);
				if (cssLink != null)
				{
					IFile cssFile = file.getParent().getFile(new Path(cssLink));
					if (cssFile.exists())
					{
						addIndex(index, file, IHTMLIndexConstants.RESOURCE_CSS, cssFile.getProjectRelativePath()
								.toPortableString());
					}
				}
			}
		}

		for (IParseNode child : parent.getChildren())
		{
			walkNode(index, file, child);
		}
	}

	private static void addIndex(Index index, IFile file, String category, String word)
	{
		index.addEntry(category, word, file.getProjectRelativePath().toPortableString());
	}

}
