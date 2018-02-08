/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme.internal;

import java.lang.reflect.Method;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.hyperlink.DefaultHyperlinkPresenter;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.osgi.framework.Bundle;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.theme.ConsoleThemer;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;
import com.aptana.theme.preferences.IPreferenceConstants;

/**
 * This is a UIJob that tries to expand the influence of our themes to common 3rd-party editors, like: JDT, WST, Ant,
 * PDE.
 * 
 * @author cwilliams
 */
public class InvasiveThemeHijacker extends UIJob implements IPreferenceChangeListener
{

	/**
	 * Constants of eclipse plugin/bundle ids and their root preference nodes.
	 */
	private static final String ORG_ECLIPSE_WST_JSDT_UI = "org.eclipse.wst.jsdt.ui"; //$NON-NLS-1$
	private static final String ORG_ECLIPSE_JDT_UI = "org.eclipse.jdt.ui"; //$NON-NLS-1$
	private static final String ORG_ECLIPSE_ANT_UI = "org.eclipse.ant.ui"; //$NON-NLS-1$
	private static final String ORG_ECLIPSE_PDE_UI = "org.eclipse.pde.ui"; //$NON-NLS-1$

	public InvasiveThemeHijacker()
	{
		super("Installing Studio theme hijacker"); //$NON-NLS-1$
		EclipseUtil.setSystemForJob(this);
	}

	@Override
	public synchronized IStatus runInUIThread(IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 10);
		if (sub.isCanceled())
		{
			return Status.CANCEL_STATUS;
		}

		// Apply to editors
		applyThemeToEclipseEditors(getCurrentTheme(), !applyToAllEditors(), sub.newChild(7));
		if (sub.isCanceled())
		{
			return Status.CANCEL_STATUS;
		}

		// Apply to consoles
		applyThemeToConsole(getCurrentTheme(), sub.newChild(1));
		if (sub.isCanceled())
		{
			return Status.CANCEL_STATUS;
		}

		// Apply to open editors
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		hijackCurrentEditors(window, sub.newChild(2));

		sub.done();
		return Status.OK_STATUS;
	}

	private void applyThemeToConsole(Theme currentTheme, IProgressMonitor monitor)
	{
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode("org.eclipse.debug.ui"); //$NON-NLS-1$
		setColor(prefs, "org.eclipse.debug.ui.errorColor", currentTheme, ConsoleThemer.CONSOLE_ERROR, //$NON-NLS-1$
				currentTheme.getForegroundAsRGB("console.error")); //$NON-NLS-1$
		setColor(prefs, "org.eclipse.debug.ui.outColor", currentTheme, ConsoleThemer.CONSOLE_OUTPUT, //$NON-NLS-1$
				currentTheme.getForeground());
		setColor(prefs, "org.eclipse.debug.ui.inColor", currentTheme, ConsoleThemer.CONSOLE_INPUT, //$NON-NLS-1$
				currentTheme.getForegroundAsRGB("console.input")); //$NON-NLS-1$
		prefs.put("org.eclipse.debug.ui.consoleBackground", StringConverter.asString(currentTheme.getBackground())); //$NON-NLS-1$
		prefs.put("org.eclipse.debug.ui.PREF_CHANGED_VALUE_BACKGROUND", //$NON-NLS-1$
				StringConverter.asString(currentTheme.getBackgroundAsRGB("markup.changed.variable"))); //$NON-NLS-1$

		if (monitor.isCanceled())
		{
			return;
		}
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(ThemePlugin.getDefault(), e);
		}
	}

	protected void setColor(IEclipsePreferences prefs, String prefKey, Theme currentTheme, String tokenName,
			RGB defaultColor)
	{
		RGB rgb = defaultColor;
		if (currentTheme.hasEntry(tokenName))
		{
			rgb = currentTheme.getForegroundAsRGB(tokenName);
		}
		prefs.put(prefKey, StringConverter.asString(rgb));
	}

	private boolean applyToAllEditors()
	{
		return ThemePlugin.applyToAllEditors();
	}

	/**
	 * Grabs all open editors and changes their selection color to match theme.
	 * 
	 * @param window
	 * @param monitor
	 */
	private void hijackCurrentEditors(IWorkbenchWindow window, IProgressMonitor monitor)
	{
		if (window == null || window.getActivePage() == null)
		{
			return;
		}
		IEditorReference[] editorRefs = window.getActivePage().getEditorReferences();
		for (IEditorReference ref : editorRefs)
		{
			if (monitor.isCanceled())
			{
				return;
			}
			hijackEditor(ref.getEditor(false));
		}
	}

	private Theme getCurrentTheme()
	{
		return ThemePlugin.getDefault().getThemeManager().getCurrentTheme();
	}

	private void applyThemeToEclipseEditors(Theme theme, boolean revertToDefaults, IProgressMonitor monitor)
	{
		// Set prefs for all editors
		IScopeContext instance = InstanceScope.INSTANCE;
		setHyperlinkValues(theme, instance.getNode("org.eclipse.ui.workbench"), revertToDefaults); //$NON-NLS-1$
		setHyperlinkValues(theme, instance.getNode(ThemePlugin.PLUGIN_ID), revertToDefaults);

		// FIXME only set these if egit or mercurial are installed!
		setGitAndMercurialValues(theme, instance.getNode("org.eclipse.ui.workbench"), revertToDefaults); //$NON-NLS-1$

		setGeneralEditorValues(theme, instance.getNode("org.eclipse.ui.texteditor"), revertToDefaults); //$NON-NLS-1$
		setEditorValues(theme, instance.getNode("org.eclipse.ui.editors"), revertToDefaults); //$NON-NLS-1$

		if (monitor.isCanceled())
		{
			return;
		}

		// PDE
		Bundle pde = Platform.getBundle(ORG_ECLIPSE_PDE_UI);
		if (pde != null)
		{
			IEclipsePreferences pdePrefs = instance.getNode(ORG_ECLIPSE_PDE_UI);
			setGeneralEditorValues(theme, pdePrefs, revertToDefaults);
			setPDEEditorValues(theme, pdePrefs, revertToDefaults);
		}

		if (monitor.isCanceled())
		{
			return;
		}

		// Ant
		Bundle ant = Platform.getBundle(ORG_ECLIPSE_ANT_UI);
		if (ant != null)
		{
			IEclipsePreferences antPrefs = instance.getNode(ORG_ECLIPSE_ANT_UI);
			setGeneralEditorValues(theme, antPrefs, revertToDefaults);
			setAntEditorValues(theme, antPrefs, revertToDefaults);
		}
		if (monitor.isCanceled())
		{
			return;
		}

		// JDT
		Bundle jdt = Platform.getBundle(ORG_ECLIPSE_JDT_UI);
		if (jdt != null)
		{
			applyThemetoJDT(theme, revertToDefaults);
		}

		// WST
		Bundle wstBundle = Platform.getBundle(ORG_ECLIPSE_WST_JSDT_UI);
		if (wstBundle != null)
		{
			applyThemetoWST(theme, revertToDefaults);
		}
	}

	private void applyThemetoWST(Theme theme, boolean revertToDefaults)
	{
		// Adapted from
		// https://github.com/eclipse-color-theme/eclipse-color-theme/blob/master/com.github.eclipsecolortheme/mappings
		applyToWST_JSDTEditor(theme, revertToDefaults);
		applyToWST_CSSEditor(theme, revertToDefaults);
		applyToWST_HTMLEditor(theme, revertToDefaults);
		applyToWST_XMLEditor(theme, revertToDefaults);
	}

	private void applyToWST_HTMLEditor(Theme theme, boolean revertToDefaults)
	{
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode("org.eclipse.wst.html.ui"); //$NON-NLS-1$
		setGeneralEditorValues(theme, prefs, revertToDefaults);
		// TODO Add SCRIPT_AREA and SCRIPT_AREA_BORDER
		setWSTToken(prefs, theme, "punctuation.definition.tag.html", "tagBorder", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "entity.name.tag.html", "tagName", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "entity.other.attribute-name.html", "tagAttributeName", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "punctuation.separator.key-value.html", "tagAttributeEquals", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "string.quoted.html", "tagAttributeValue", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "text.html", "xmlContent", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "comment.block.html", "commentBorder", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "comment.block.html", "commentText", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "constant.character.entity.html", "entityReference", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "entity.name.tag.doctype.html", "doctypeName", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "text.html", "doctypeExternalPubref", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "text.html", "doctypeExtrenalSysref", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "text.html", "doctypeExternalId", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "punctuation.definition.tag.html", "declBoder", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$

		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(ThemePlugin.getDefault(), e);
		}
	}

	private void applyToWST_JSDTEditor(Theme theme, boolean revertToDefaults)
	{
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ORG_ECLIPSE_WST_JSDT_UI);
		setGeneralEditorValues(theme, prefs, revertToDefaults);

		// TODO Add mapping for parameter variables, "functions" (which might be function calls)?
		setToken(prefs, theme, "source.js", "java_default", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "comment.line.double-slash.js", "java_single_line_comment", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "comment.block", "java_multi_line_comment", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "string.quoted.double.js", "java_string", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "keyword", "java_keyword", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "keyword.operator", "java_operator", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "keyword.control.js", "java_keyword_return", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "punctuation.bracket.js", "java_bracket", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "keyword.other.documentation.task", "commentTaskTag", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$

		// JSdoc
		setToken(prefs, theme, "keyword.other.documentation.js", "java_doc_keyword", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "entity.name.tag.inline.any.html", "java_doc_tag", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "markup.underline.link.javadoc", "java_doc_link", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "comment.block.documentation.javadoc", "java_doc_default", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "meta.tag.documentation.js", "tagName", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$

		// Semantic
		setSemanticToken(prefs, theme, "entity.name.function.js", "methodDeclarationName", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "source.js", "localVariable", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "source.js", "localVariableDeclaration", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$

		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(ThemePlugin.getDefault(), e);
		}
	}

	private void applyToWST_CSSEditor(Theme theme, boolean revertToDefaults)
	{
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode("org.eclipse.wst.css.ui"); //$NON-NLS-1$
		setGeneralEditorValues(theme, prefs, revertToDefaults);

		setWSTToken(prefs, theme,
				"meta.property-name.css support.type.property-name.css", "PROPERTY_NAME", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "entity.other.attribute-name.class.css", "CLASS", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "source.css", "ATTRIBUTE_VALUE", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "source.css", "UNIVERSAL", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "punctuation.css", "COMBINATOR", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "punctuation.terminator.rule.css", "SEMI_COLON", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "punctuation.bracket.css", "ATTRIBUTE_DELIM", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "entity.name.tag.css", "SELECTOR", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "source.css", "URI", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "source.css", "ATTRIBUTE_NAME", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "punctuation.separator.key-value.css", "COLON", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "entity.other.attribute-name.id.css", "ID", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "source.css", "NORMAL", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "string.quoted.css", "STRING", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "punctuation.section.property-list.css", "CURLY_BRACE", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "source.css", "PSEUDO", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "comment.block.css", "COMMENT", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "support.constant.property-value.css", "PROPERTY_VALUE", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "keyword.control.at-rule.css", "ATMARK_RULE", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "source.css", "ATTRIBUTE_OPERATOR", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "support.constant.media.css", "MEDIA", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$

		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(ThemePlugin.getDefault(), e);
		}
	}

	private void applyToWST_XMLEditor(Theme theme, boolean revertToDefaults)
	{
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode("org.eclipse.wst.xml.ui"); //$NON-NLS-1$
		setGeneralEditorValues(theme, prefs, revertToDefaults);

		// FIXME These were adapted from our scopes for DTD, but those don't appear correct to me!
		setWSTToken(prefs, theme, "punctuation.definition.tag.xml", "tagBorder", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "entity.name.tag.xml", "tagName", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "entity.other.attribute-name.xml", "tagAttributeName", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "punctuation.separator.key-value.xml", "tagAttributeEquals", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "string.quoted.xml", "tagAttributeValue", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "text.xml", "xmlContent", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "comment.block.xml", "commentBorder", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "comment.block.xml", "commentText", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "constant.character.entity.xml", "entityReference", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "source.dtd", "doctypeName", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "keyword.operator.dtd", "doctypeExternalPubref", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "keyword.operator.dtd", "doctypeExtrenalSysref", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "keyword.operator.dtd", "doctypeExternalId", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "source.dtd", "declBorder", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "string.unquoted.cdata.xml", "cdataBorder", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "string.unquoted.cdata.xml", "cdataText", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "meta.tag.preprocessor.xml", "piBorder", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setWSTToken(prefs, theme, "meta.tag.preprocessor.xml", "piContent", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$

		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(ThemePlugin.getDefault(), e);
		}
	}

	private void applyThemetoJDT(Theme theme, boolean revertToDefaults)
	{
		// Now set for JDT...
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ORG_ECLIPSE_JDT_UI);
		setGeneralEditorValues(theme, prefs, revertToDefaults);

		// Set prefs for JDT so it's various tokens get colors that match up to our theme!
		// prefs = EclipseUtil.instanceScope().getNode("org.eclipse.jdt.ui");
		setToken(prefs, theme, "string.quoted.double.java", "java_string", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "source.java", "java_default", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "keyword", "java_keyword", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "keyword.operator", "java_operator", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "keyword.control.java", "java_keyword_return", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "comment.line.double-slash.java", "java_single_line_comment", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "comment.block", "java_multi_line_comment", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "punctuation.bracket.java", "java_bracket", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		// Javadoc
		//String TASK_TAG= "java_comment_task_tag"; //$NON-NLS-1$
		setToken(prefs, theme, "keyword.other.documentation.java", "java_doc_keyword", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "entity.name.tag.inline.any.html", "java_doc_tag", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "markup.underline.link.javadoc", "java_doc_link", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "comment.block.documentation.javadoc", "java_doc_default", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$

		// deprecated
		// setToken(prefs, theme, "entity.name.function.java", "java_method_name", revertToDefaults);
		setToken(prefs, theme, "entity.name.type.class.java", "java_type", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "storage.type.annotation.java", "java_annotation", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$

		// Semantic
		setSemanticToken(prefs, theme, "entity.name.type.class.java", "class", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "entity.name.type.enum.java", "enum", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "entity.name.type.interface.java", "interface", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "constant.numeric.java", "number", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "variable.parameter.java", "parameterVariable", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "constant.other.java", "staticField", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "constant.other.java", "staticFinalField", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "entity.name.function.java", "methodDeclarationName", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "invalid.deprecated.java", "deprecatedMember", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "storage.type.annotation.java", "annotation", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "constant.other.key.java", "annotationElementReference", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "source.java", "localVariable", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "source.java", "field", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "source.java", "staticMethodInvocation", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "source.java", "inheritedMethodInvocation", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "source.java", "abstractMethodInvocation", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "source.java", "localVariableDeclaration", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "source.java", "method", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "source.java", "typeParameter", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "source.java", "autoboxing", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "source.java", "typeArgument", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$

		// Java *.properties files
		setToken(prefs, theme, "keyword.other.java-props", "pf_coloring_key", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "comment.line.number-sign.java-props", "pf_coloring_comment", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "string.java-props", "pf_coloring_value", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "punctuation.separator.key-value.java-props", "pf_coloring_assignment", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "string.interpolated.java-props", "pf_coloring_argument", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$

		// Override pair matching colors
		if (!revertToDefaults)
		{
			// FIXME Revert back to default value if revertToDefaults!
			prefs.put("matchingBracketsColor", StringConverter.asString(theme.getCharacterPairColor())); //$NON-NLS-1$
		}

		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(ThemePlugin.getDefault(), e);
		}

		// Override JDT editor font
		Font fFont = JFaceResources.getFontRegistry().get(JFaceResources.TEXT_FONT);
		JFaceResources.getFontRegistry().put("org.eclipse.jdt.ui.editors.textfont", fFont.getFontData()); //$NON-NLS-1$
	}

	private void setGitAndMercurialValues(Theme theme, IEclipsePreferences prefs, boolean revertToDefaults)
	{
		if (prefs == null)
		{
			return;
		}

		if (revertToDefaults)
		{
			// EGit colors
			prefs.remove("org.eclipse.egit.ui.UncommittedChangeBackgroundColor"); //$NON-NLS-1$
			prefs.remove("org.eclipse.egit.ui.UncommittedChangeForegroundColor"); //$NON-NLS-1$

			// Mercurial colors
			prefs.remove("com.vectrace.mercurialeclipse.ui.colorsandfonts.addedForegroundColor"); //$NON-NLS-1$
			prefs.remove("com.vectrace.mercurialeclipse.ui.colorsandfonts.addedBackgroundColor"); //$NON-NLS-1$

			prefs.remove("com.vectrace.mercurialeclipse.ui.colorsandfonts.changedForegroundColor"); //$NON-NLS-1$
			prefs.remove("com.vectrace.mercurialeclipse.ui.colorsandfonts.changedBackgroundColor"); //$NON-NLS-1$

			prefs.remove("com.vectrace.mercurialeclipse.ui.colorsandfonts.removedForegroundColor"); //$NON-NLS-1$
			prefs.remove("com.vectrace.mercurialeclipse.ui.colorsandfonts.removedBackgroundColor"); //$NON-NLS-1$

			prefs.remove("com.vectrace.mercurialeclipse.ui.colorsandfonts.deletedForegroundColor"); //$NON-NLS-1$
			prefs.remove("com.vectrace.mercurialeclipse.ui.colorsandfonts.deletedBackgroundColor"); //$NON-NLS-1$

			prefs.remove("com.vectrace.mercurialeclipse.ui.colorsandfonts.unknownForegroundColor"); //$NON-NLS-1$
			prefs.remove("com.vectrace.mercurialeclipse.ui.colorsandfonts.unknownBackgroundColor"); //$NON-NLS-1$

			prefs.remove("com.vectrace.mercurialeclipse.ui.colorsandfonts.conflictForegroundColor"); //$NON-NLS-1$
			prefs.remove("com.vectrace.mercurialeclipse.ui.colorsandfonts.conflictBackgroundColor"); //$NON-NLS-1$

			prefs.remove("com.vectrace.mercurialeclipse.ui.colorsandfonts.IgnoredForegroundColor"); //$NON-NLS-1$
			prefs.remove("com.vectrace.mercurialeclipse.ui.colorsandfonts.IgnoredBackgroundColor"); //$NON-NLS-1$

		}
		else
		{
			TextAttribute changedFile = theme.getTextAttribute("markup.changed"); //$NON-NLS-1$
			TextAttribute addedFile = theme.getTextAttribute("markup.inserted"); //$NON-NLS-1$
			TextAttribute deletedFile = theme.getTextAttribute("markup.deleted"); //$NON-NLS-1$
			TextAttribute conflictFile = theme.getTextAttribute("invalid"); //$NON-NLS-1$

			// FIXME Grab colors from GitColors class, which should be moved to theme plugin and called something like
			// SCMColors or added to Theme class

			// EGit colors
			// TODO do we walso need to override font?
			prefs.put("org.eclipse.egit.ui.UncommittedChangeForegroundColor", //$NON-NLS-1$
					StringConverter.asString(addedFile.getForeground().getRGB()));
			prefs.put("org.eclipse.egit.ui.UncommittedChangeBackgroundColor", //$NON-NLS-1$
					StringConverter.asString(addedFile.getBackground().getRGB()));

			// Mercurial colors
			// TODO Do we also need to override fonts?
			prefs.put("com.vectrace.mercurialeclipse.ui.colorsandfonts.addedForegroundColor", //$NON-NLS-1$
					StringConverter.asString(addedFile.getForeground().getRGB()));
			prefs.put("com.vectrace.mercurialeclipse.ui.colorsandfonts.addedBackgroundColor", //$NON-NLS-1$
					StringConverter.asString(addedFile.getBackground().getRGB()));

			prefs.put("com.vectrace.mercurialeclipse.ui.colorsandfonts.changedForegroundColor", //$NON-NLS-1$
					StringConverter.asString(changedFile.getForeground().getRGB()));
			prefs.put("com.vectrace.mercurialeclipse.ui.colorsandfonts.changedBackgroundColor", //$NON-NLS-1$
					StringConverter.asString(changedFile.getBackground().getRGB()));

			prefs.put("com.vectrace.mercurialeclipse.ui.colorsandfonts.deletedForegroundColor", //$NON-NLS-1$
					StringConverter.asString(deletedFile.getForeground().getRGB()));
			prefs.put("com.vectrace.mercurialeclipse.ui.colorsandfonts.deletedBackgroundColor", //$NON-NLS-1$
					StringConverter.asString(deletedFile.getBackground().getRGB()));

			prefs.put("com.vectrace.mercurialeclipse.ui.colorsandfonts.removedForegroundColor", //$NON-NLS-1$
					StringConverter.asString(deletedFile.getForeground().getRGB()));
			prefs.put("com.vectrace.mercurialeclipse.ui.colorsandfonts.removedBackgroundColor", //$NON-NLS-1$
					StringConverter.asString(deletedFile.getBackground().getRGB()));

			prefs.put("com.vectrace.mercurialeclipse.ui.colorsandfonts.unknownForegroundColor", //$NON-NLS-1$
					StringConverter.asString(theme.getForeground()));
			prefs.put("com.vectrace.mercurialeclipse.ui.colorsandfonts.unknownBackgroundColor", //$NON-NLS-1$
					StringConverter.asString(theme.getBackground()));

			prefs.put("com.vectrace.mercurialeclipse.ui.colorsandfonts.conflictForegroundColor", //$NON-NLS-1$
					StringConverter.asString(conflictFile.getForeground().getRGB()));
			prefs.put("com.vectrace.mercurialeclipse.ui.colorsandfonts.conflictBackgroundColor", //$NON-NLS-1$
					StringConverter.asString(conflictFile.getBackground().getRGB()));

			prefs.put("com.vectrace.mercurialeclipse.ui.colorsandfonts.IgnoredForegroundColor", //$NON-NLS-1$
					StringConverter.asString(theme.getForeground()));
			prefs.put("com.vectrace.mercurialeclipse.ui.colorsandfonts.IgnoredBackgroundColor", //$NON-NLS-1$
					StringConverter.asString(theme.getBackground()));
		}
	}

	private void setPDEEditorValues(Theme theme, IEclipsePreferences pdePrefs, boolean revertToDefaults)
	{
		if (pdePrefs == null)
		{
			return;
		}

		if (revertToDefaults)
		{
			pdePrefs.remove("editor.color.xml_comment"); //$NON-NLS-1$
			pdePrefs.remove("editor.color.instr"); //$NON-NLS-1$
			pdePrefs.remove("editor.color.string"); //$NON-NLS-1$
			pdePrefs.remove("editor.color.externalized_string"); //$NON-NLS-1$
			pdePrefs.remove("editor.color.default"); //$NON-NLS-1$
			pdePrefs.remove("editor.color.tag"); //$NON-NLS-1$
			pdePrefs.remove("editor.color.header_key"); //$NON-NLS-1$
			pdePrefs.remove("editor.color.header_value"); //$NON-NLS-1$
			pdePrefs.remove("editor.color.header_assignment"); //$NON-NLS-1$
			pdePrefs.remove("editor.color.header_osgi"); //$NON-NLS-1$
			pdePrefs.remove("editor.color.header_attributes"); //$NON-NLS-1$
		}
		else
		{
			// plugin.xml
			setToken(pdePrefs, theme, "comment.block.xml", "editor.color.xml_comment", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
			setToken(pdePrefs, theme, "meta.tag.preprocessor.xml", "editor.color.instr", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
			setToken(pdePrefs, theme, "string.quoted.double.xml", "editor.color.string", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
			setToken(pdePrefs, theme, "string.interpolated.xml", "editor.color.externalized_string", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
			setToken(pdePrefs, theme, "text.xml", "editor.color.default", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
			setToken(pdePrefs, theme, "entity.name.tag.xml", "editor.color.tag", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
			// manifest.mf
			setToken(pdePrefs, theme, "keyword.other.manifest", "editor.color.header_key", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
			setToken(pdePrefs, theme, "source.manifest", "editor.color.header_value", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
			setToken(pdePrefs, theme, "punctuation.separator.key-value.manifest", "editor.color.header_assignment", //$NON-NLS-1$ //$NON-NLS-2$
					revertToDefaults);
			setToken(pdePrefs, theme, "keyword.other.manifest.osgi", "editor.color.header_osgi", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
			setToken(pdePrefs, theme, "string.manifest", "editor.color.header_attributes", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		}
		try
		{
			pdePrefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(ThemePlugin.getDefault(), e);
		}
	}

	private void setHyperlinkValues(Theme theme, IEclipsePreferences prefs, boolean revertToDefaults)
	{
		if (prefs == null || theme == null)
		{
			return;
		}

		if (revertToDefaults)
		{
			// Console preferences
			prefs.remove(JFacePreferences.HYPERLINK_COLOR);
			prefs.remove(JFacePreferences.ACTIVE_HYPERLINK_COLOR);

			// Editor preferences
			prefs.remove(DefaultHyperlinkPresenter.HYPERLINK_COLOR_SYSTEM_DEFAULT);
			prefs.remove(DefaultHyperlinkPresenter.HYPERLINK_COLOR);

		}
		else
		{
			TextAttribute editorHyperlink = theme.getTextAttribute("hyperlink"); //$NON-NLS-1$

			prefs.put(JFacePreferences.HYPERLINK_COLOR,
					StringConverter.asString(editorHyperlink.getForeground().getRGB()));
			JFaceResources.getColorRegistry().put(JFacePreferences.HYPERLINK_COLOR,
					editorHyperlink.getForeground().getRGB());
			prefs.put(JFacePreferences.ACTIVE_HYPERLINK_COLOR,
					StringConverter.asString(editorHyperlink.getForeground().getRGB()));
			JFaceResources.getColorRegistry().put(JFacePreferences.ACTIVE_HYPERLINK_COLOR,
					editorHyperlink.getForeground().getRGB());
			prefs.putBoolean(DefaultHyperlinkPresenter.HYPERLINK_COLOR_SYSTEM_DEFAULT, false);
			prefs.put(DefaultHyperlinkPresenter.HYPERLINK_COLOR,
					StringConverter.asString(editorHyperlink.getForeground().getRGB()));

		}

	}

	private void setGeneralEditorValues(Theme theme, IEclipsePreferences prefs, boolean revertToDefaults)
	{
		if (prefs == null)
		{
			return;
		}

		if (revertToDefaults)
		{
			prefs.remove(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_BACKGROUND);
			prefs.remove(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND);
			prefs.remove(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND);
			prefs.remove(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND);
			prefs.remove(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR);
		}
		else
		{
			prefs.put(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_BACKGROUND,
					StringConverter.asString(theme.getSelectionAgainstBG()));
			prefs.put(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND,
					StringConverter.asString(theme.getForeground()));
			prefs.put(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND, StringConverter.asString(theme.getBackground()));
			prefs.put(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND, StringConverter.asString(theme.getForeground()));
			prefs.put(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR,
					StringConverter.asString(theme.getLineHighlightAgainstBG()));
		}

		prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND_SYSTEM_DEFAULT, revertToDefaults);
		prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT, revertToDefaults);
		prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND_SYSTEM_DEFAULT, revertToDefaults);

		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(ThemePlugin.getDefault(), e);
		}
	}

	private void setEditorValues(Theme theme, IEclipsePreferences prefs, boolean revertToDefaults)
	{
		// FIXME Check for overrides in theme
		if (revertToDefaults)
		{
			prefs.remove("occurrenceIndicationColor"); //$NON-NLS-1$
			prefs.remove("writeOccurrenceIndicationColor"); //$NON-NLS-1$
			prefs.remove("currentIPColor"); //$NON-NLS-1$
			prefs.remove("secondaryIPColor"); //$NON-NLS-1$

			prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT, true);
			prefs.remove(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND);
			prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND_SYSTEM_DEFAULT, true);
			prefs.remove(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND);
			prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_BACKGROUND_SYSTEM_DEFAULT, true);
			prefs.remove(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_BACKGROUND);
			prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND_SYSTEM_DEFAULT, true);
			prefs.remove(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND);
			prefs.remove(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR);
		}
		else
		{
			prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT, false);
			prefs.put(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND, StringConverter.asString(theme.getBackground()));
			prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND_SYSTEM_DEFAULT, false);
			prefs.put(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND, StringConverter.asString(theme.getForeground()));
			prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_BACKGROUND_SYSTEM_DEFAULT, false);
			prefs.put(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_BACKGROUND,
					StringConverter.asString(theme.getSelectionAgainstBG()));
			prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND_SYSTEM_DEFAULT, false);
			prefs.put(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND,
					StringConverter.asString(theme.getForeground()));

			prefs.put(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR,
					StringConverter.asString(theme.getLineHighlightAgainstBG()));

			prefs.put("occurrenceIndicationColor", StringConverter.asString(theme.getSelectionAgainstBG())); //$NON-NLS-1$
			prefs.put("writeOccurrenceIndicationColor", StringConverter.asString(theme.getSelectionAgainstBG())); //$NON-NLS-1$
			// Override the debug line highlight colors
			prefs.put("currentIPColor", StringConverter.asString(theme.getBackgroundAsRGB("meta.diff.header"))); //$NON-NLS-1$ //$NON-NLS-2$
			prefs.put("secondaryIPColor", StringConverter.asString(theme.getBackgroundAsRGB("meta.diff.header"))); //$NON-NLS-1$ //$NON-NLS-2$
		}

		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(ThemePlugin.getDefault(), e);
		}
	}

	private void setAntEditorValues(Theme theme, IEclipsePreferences prefs, boolean revertToDefaults)
	{
		if (prefs == null)
		{
			return;
		}

		if (revertToDefaults)
		{
			prefs.remove("org.eclipse.ant.ui.commentsColor"); //$NON-NLS-1$
			prefs.remove("org.eclipse.ant.ui.processingInstructionsColor"); //$NON-NLS-1$
			prefs.remove("org.eclipse.ant.ui.constantStringsColor"); //$NON-NLS-1$
			prefs.remove("org.eclipse.ant.ui.textColor"); //$NON-NLS-1$
			prefs.remove("org.eclipse.ant.ui.tagsColor"); //$NON-NLS-1$
			prefs.remove("org.eclipse.ant.ui.dtdColor"); //$NON-NLS-1$
		}
		else
		{
			setToken(prefs, theme, "comment.block.xml.ant", "org.eclipse.ant.ui.commentsColor", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
			setToken(prefs, theme,
					"meta.tag.preprocessor.xml.ant", "org.eclipse.ant.ui.processingInstructionsColor", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
			setToken(prefs, theme,
					"string.quoted.double.xml.ant", "org.eclipse.ant.ui.constantStringsColor", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
			setToken(prefs, theme, "text.xml.ant", "org.eclipse.ant.ui.textColor", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
			setToken(prefs, theme, "entity.name.tag.target.xml.ant", "org.eclipse.ant.ui.tagsColor", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
			setToken(prefs, theme, "meta.tag.preprocessor.xml.ant", "org.eclipse.ant.ui.dtdColor", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		}

		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(ThemePlugin.getDefault(), e);
		}
	}

	private void setToken(IEclipsePreferences prefs, Theme theme, String ourTokenType, String jdtToken,
			boolean revertToDefaults)
	{
		if (revertToDefaults)
		{
			prefs.remove(jdtToken);
			prefs.remove(jdtToken + "_bold"); //$NON-NLS-1$
			prefs.remove(jdtToken + "_italic"); //$NON-NLS-1$
			prefs.remove(jdtToken + "_underline"); //$NON-NLS-1$
			prefs.remove(jdtToken + "_strikethrough"); //$NON-NLS-1$
		}
		else
		{
			TextAttribute attr = theme.getTextAttribute(ourTokenType);
			prefs.put(jdtToken, StringConverter.asString(attr.getForeground().getRGB()));
			prefs.putBoolean(jdtToken + "_bold", (attr.getStyle() & SWT.BOLD) != 0); //$NON-NLS-1$
			prefs.putBoolean(jdtToken + "_italic", (attr.getStyle() & SWT.ITALIC) != 0); //$NON-NLS-1$
			prefs.putBoolean(jdtToken + "_underline", (attr.getStyle() & TextAttribute.UNDERLINE) != 0); //$NON-NLS-1$
			prefs.putBoolean(jdtToken + "_strikethrough", (attr.getStyle() & TextAttribute.STRIKETHROUGH) != 0); //$NON-NLS-1$
		}
	}

	private void setWSTToken(IEclipsePreferences prefs, Theme theme, String ourEquivalentScope, String prefKey,
			boolean revertToDefaults)
	{
		if (revertToDefaults)
		{
			prefs.remove(prefKey);
		}
		else
		{
			TextAttribute attr = theme.getTextAttribute(ourEquivalentScope);
			boolean bold = (attr.getStyle() & SWT.BOLD) != 0;
			boolean italic = (attr.getStyle() & SWT.ITALIC) != 0;
			boolean strikethrough = (attr.getStyle() & TextAttribute.STRIKETHROUGH) != 0;
			boolean underline = (attr.getStyle() & TextAttribute.UNDERLINE) != 0;
			StringBuilder value = new StringBuilder();
			value.append(Theme.toHex(attr.getForeground().getRGB()));
			value.append('|');
			value.append(Theme.toHex(attr.getBackground().getRGB()));
			value.append('|');
			value.append(bold);
			value.append('|');
			value.append(italic);
			value.append('|');
			value.append(strikethrough);
			value.append('|');
			value.append(underline);
			prefs.put(prefKey, value.toString());
		}
	}

	private void setSemanticToken(IEclipsePreferences prefs, Theme theme, String ourTokenType, String jdtToken,
			boolean revertToDefaults)
	{
		String prefix = "semanticHighlighting."; //$NON-NLS-1$
		jdtToken = prefix + jdtToken;
		if (revertToDefaults)
		{
			prefs.remove(jdtToken + ".color"); //$NON-NLS-1$
			prefs.remove(jdtToken + ".bold"); //$NON-NLS-1$
			prefs.remove(jdtToken + ".italic"); //$NON-NLS-1$
			prefs.remove(jdtToken + ".underline"); //$NON-NLS-1$
			prefs.remove(jdtToken + ".strikethrough"); //$NON-NLS-1$
			prefs.remove(jdtToken + ".enabled"); //$NON-NLS-1$
		}
		else
		{
			TextAttribute attr = theme.getTextAttribute(ourTokenType);
			prefs.put(jdtToken + ".color", StringConverter.asString(attr.getForeground().getRGB())); //$NON-NLS-1$
			prefs.putBoolean(jdtToken + ".bold", (attr.getStyle() & SWT.BOLD) != 0); //$NON-NLS-1$
			prefs.putBoolean(jdtToken + ".italic", (attr.getStyle() & SWT.ITALIC) != 0); //$NON-NLS-1$
			prefs.putBoolean(jdtToken + ".underline", (attr.getStyle() & TextAttribute.UNDERLINE) != 0); //$NON-NLS-1$
			prefs.putBoolean(jdtToken + ".strikethrough", (attr.getStyle() & TextAttribute.STRIKETHROUGH) != 0); //$NON-NLS-1$
			prefs.putBoolean(jdtToken + ".enabled", true); //$NON-NLS-1$
		}
	}

	// IPreferenceChangeListener
	public void preferenceChange(PreferenceChangeEvent event)
	{
		// If invasive themes are on and we changed the theme, schedule. Also schedule if we toggled invasive theming.
		if (event.getKey().equals(IPreferenceConstants.APPLY_TO_ALL_EDITORS)
				|| event.getKey().equals(IThemeManager.THEME_CHANGED))
		{
			cancel();
			schedule();
		}
	}

	private void overrideSelectionColor(AbstractTextEditor editor)
	{
		try
		{
			if (Class.forName("com.aptana.editor.common.extensions.IThemeableEditor").isInstance(editor)) // we already handle our own editors //$NON-NLS-1$
			{
				return;
			}
		}
		catch (ClassNotFoundException e1)
		{
			// ignore
		}

		ISourceViewer sourceViewer = null;
		try
		{
			Method m = AbstractTextEditor.class.getDeclaredMethod("getSourceViewer"); //$NON-NLS-1$
			m.setAccessible(true);
			sourceViewer = (ISourceViewer) m.invoke(editor);
		}
		catch (Exception e)
		{
			// ignore
		}
		if (sourceViewer == null || sourceViewer.getTextWidget() == null)
		{
			return;
		}

		new TextViewerThemer(sourceViewer).apply();
	}

	/**
	 * Overrides the selection color of the editor.
	 * 
	 * @param part
	 */
	private void hijackEditor(IEditorPart part)
	{
		if (!applyToAllEditors())
		{
			return;
		}

		// FIXME This doesn't work on CompareEditor!
		if (part instanceof AbstractTextEditor)
		{
			overrideSelectionColor((AbstractTextEditor) part);
		}
	}

	public void apply()
	{
		if (applyToAllEditors()) // we have something to do, so schedule job to run
		{
			schedule();
		}
		// Listen for changes to prefs
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ThemePlugin.PLUGIN_ID);
		prefs.addPreferenceChangeListener(this);
	}

	public void dispose()
	{
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ThemePlugin.PLUGIN_ID);
		prefs.removePreferenceChangeListener(this);
	}
}
