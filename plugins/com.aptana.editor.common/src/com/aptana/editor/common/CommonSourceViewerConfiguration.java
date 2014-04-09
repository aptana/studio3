/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.internal.text.html.HTMLTextPresenter;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.DefaultTextHover;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.hyperlink.IHyperlinkPresenter;
import org.eclipse.jface.text.hyperlink.MultipleHyperlinkPresenter;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.spelling.SpellingService;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.editor.common.contentassist.ContentAssistant;
import com.aptana.editor.common.contentassist.ICommonContentAssistProcessor;
import com.aptana.editor.common.contentassist.SimpleTextHover;
import com.aptana.editor.common.hover.AbstractCommonTextHover;
import com.aptana.editor.common.hover.ThemedInformationControl;
import com.aptana.editor.common.internal.formatter.CommonMultiPassContentFormatter;
import com.aptana.editor.common.internal.hover.TextHoverDescriptor;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.editor.common.scripting.IContentTypeTranslator;
import com.aptana.editor.common.scripting.QualifiedContentType;
import com.aptana.editor.common.spelling.MultiRegionSpellingReconcileStrategy;
import com.aptana.editor.common.text.CommonDoubleClickStrategy;
import com.aptana.editor.common.text.RubyRegexpAutoIndentStrategy;
import com.aptana.editor.common.text.reconciler.CommonPresentationReconciler;
import com.aptana.editor.common.text.reconciler.CommonReconciler;
import com.aptana.editor.common.text.reconciler.CommonReconcilingStrategy;
import com.aptana.editor.common.text.reconciler.CompositeReconcilingStrategy;
import com.aptana.editor.hover.CommonAnnotationHover;
import com.aptana.formatter.ScriptFormatterManager;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;

@SuppressWarnings("restriction")
public abstract class CommonSourceViewerConfiguration extends TextSourceViewerConfiguration implements
		ITopContentTypesProvider
{

	public static final int CONTENT_ASSIST_OFF_DELAY = -1;
	public static final int NO_CONTENT_ASSIST_DELAY = 0;
	public static final int DEFAULT_CONTENT_ASSIST_DELAY = 200;
	public static final int LONG_CONTENT_ASSIST_DELAY = 1000;
	private boolean disableBackgroundReconciler = false;

	public static final String CONTENTTYPE_HTML_PREFIX = "com.aptana.contenttype.html"; //$NON-NLS-1$

	private AbstractThemeableEditor fTextEditor;
	private CommonDoubleClickStrategy fDoubleClickStrategy;
	private IPreferenceChangeListener fThemeChangeListener;
	private IPreferenceChangeListener fAutoActivationListener;
	protected CommonReconciler fReconciler;
	private List<IContentAssistProcessor> fCAProcessors = new ArrayList<IContentAssistProcessor>();

	/**
	 * CommonSourceViewerConfiguration
	 * 
	 * @param preferenceStore
	 * @param editor
	 */
	protected CommonSourceViewerConfiguration(IPreferenceStore preferenceStore, AbstractThemeableEditor editor)
	{
		super(preferenceStore);
		fTextEditor = editor;
		disableBackgroundReconciler = Boolean.parseBoolean(EclipseUtil
				.getSystemProperty(ICommonEditorSystemProperties.DISABLE_BACKGROUND_RECONCILER));
	}

	/**
	 * dispose
	 */
	void dispose()
	{
		fTextEditor = null;
		fDoubleClickStrategy = null;
		if (fReconciler != null)
		{
			fReconciler.dispose();
			fReconciler = null;
		}
		if (fAutoActivationListener != null)
		{
			EclipseUtil.instanceScope().getNode(CommonEditorPlugin.PLUGIN_ID)
					.removePreferenceChangeListener(fAutoActivationListener);
			fAutoActivationListener = null;
		}
		if (fThemeChangeListener != null)
		{
			EclipseUtil.instanceScope().getNode(ThemePlugin.PLUGIN_ID)
					.removePreferenceChangeListener(fThemeChangeListener);
			fThemeChangeListener = null;
		}

		if (fCAProcessors != null)
		{
			for (IContentAssistProcessor cap : fCAProcessors)
			{
				// disposes of unused resources, particularly preference change listeners
				if (cap instanceof ICommonContentAssistProcessor)
				{
					((ICommonContentAssistProcessor) cap).dispose();
				}
			}
			fCAProcessors = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.TextSourceViewerConfiguration#getAnnotationHover(org.eclipse.jface.text.source.
	 * ISourceViewer)
	 */
	@Override
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer)
	{
		return new CommonAnnotationHover(false)
		{
			protected boolean isIncluded(Annotation annotation)
			{
				return isShowInVerticalRuler(annotation);
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getAutoEditStrategies(org.eclipse.jface.text.source.
	 * ISourceViewer, java.lang.String)
	 */
	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType)
	{
		return new IAutoEditStrategy[] { new RubyRegexpAutoIndentStrategy(contentType, this, sourceViewer,
				CommonEditorPlugin.getDefault().getPreferenceStore()) };
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getContentAssistant(org.eclipse.jface.text.source.
	 * ISourceViewer)
	 */
	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer)
	{
		final ContentAssistant assistant = new ContentAssistant();
		assistant.setProposalSelectorBackground(getThemeBackground());
		assistant.setProposalSelectorForeground(getThemeForeground());
		assistant.setProposalSelectorSelectionColor(getThemeSelection());

		assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

		String[] contentTypes = getConfiguredContentTypes(sourceViewer);

		for (String type : contentTypes)
		{
			IContentAssistProcessor processor = getContentAssistProcessor(sourceViewer, type);

			if (processor != null)
			{
				assistant.setContentAssistProcessor(processor, type);
				fCAProcessors.add(processor);
			}
		}

		assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));

		if (fPreferenceStore != null)
		{
			setAutoActivationOptions(assistant);
			// Auto-insert single proposals
			boolean autoInsert = fPreferenceStore.getBoolean(IPreferenceConstants.CONTENT_ASSIST_AUTO_INSERT);
			assistant.enableAutoInsert(autoInsert);
		}

		fAutoActivationListener = new IPreferenceChangeListener()
		{
			public void preferenceChange(PreferenceChangeEvent event)
			{
				setAutoActivationOptions(assistant);
			}
		};
		EclipseUtil.instanceScope().getNode(CommonEditorPlugin.PLUGIN_ID)
				.addPreferenceChangeListener(fAutoActivationListener);

		assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_BELOW);
		assistant.setContextInformationPopupBackground(getThemeBackground());
		assistant.setContextInformationPopupForeground(getThemeForeground());

		fThemeChangeListener = new IPreferenceChangeListener()
		{

			public void preferenceChange(PreferenceChangeEvent event)
			{
				if (event.getKey().equals(IThemeManager.THEME_CHANGED))
				{
					assistant.setProposalSelectorBackground(getThemeBackground());
					assistant.setProposalSelectorForeground(getThemeForeground());
					assistant.setProposalSelectorSelectionColor(getThemeSelection());
					assistant.setContextInformationPopupBackground(getThemeBackground());
					assistant.setContextInformationPopupForeground(getThemeForeground());
				}
			}
		};
		EclipseUtil.instanceScope().getNode(ThemePlugin.PLUGIN_ID).addPreferenceChangeListener(fThemeChangeListener);

		return assistant;
	}

	private void setAutoActivationOptions(final ContentAssistant assistant)
	{
		int delay = fPreferenceStore.getInt(IPreferenceConstants.CONTENT_ASSIST_DELAY);
		if (delay >= 0)
		{
			assistant.enableAutoActivation(true);
			assistant.setAutoActivationDelay(delay);
		}
		else
		{
			assistant.enableAutoActivation(false);
		}
	}

	/**
	 * Returns the content assist processor that will be used for content assist in the given source viewer and for the
	 * given partition type.
	 * 
	 * @param sourceViewer
	 *            the source viewer to be configured by this configuration
	 * @param contentType
	 *            the partition type for which the content assist processor is applicable
	 * @return IContentAssistProcessor or null if the content type is not supported
	 */
	protected IContentAssistProcessor getContentAssistProcessor(ISourceViewer sourceViewer, String contentType)
	{
		return new CommonContentAssistProcessor(getEditor());
	}

	/**
	 * Collects the code formatters by the supported content-types and returns a new {@link import
	 * org.eclipse.jface.text.formatter.MultiPassContentFormatter} that holds them.<br>
	 * The returned content formatter is computed from the result of {@link #getTopContentTypes()}. The first element in
	 * the returned array should define the 'master' formatter. While the rest of the elements should contain the
	 * 'slave' formatter. <br>
	 * Note that each slave formatter is located in the last element of each inner-array that was returned from the
	 * getTopContentTypes call.
	 */
	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer)
	{
		final String[][] contentTypes = getTopContentTypes();
		final CommonMultiPassContentFormatter formatter = new CommonMultiPassContentFormatter(
				getConfiguredDocumentPartitioning(sourceViewer), IDocument.DEFAULT_CONTENT_TYPE);
		boolean masterSet = false;
		Set<String> addedFormatters = new HashSet<String>();
		for (String contentTypeArr[] : contentTypes)
		{
			// The first item in the array should contain the master formatter strategy
			// In case it starts with the HTML prefix (like in PHP, ERB etc.), we try to set
			// the master to the HTML formatter.
			if (!masterSet && contentTypeArr[0].startsWith(CONTENTTYPE_HTML_PREFIX))
			{
				if (ScriptFormatterManager.hasFormatterFor(CONTENTTYPE_HTML_PREFIX))
				{
					formatter.setMasterStrategy(CONTENTTYPE_HTML_PREFIX);
					masterSet = true;
					addedFormatters.add(CONTENTTYPE_HTML_PREFIX);
				}
				else
				{
					IdeLog.logWarning(CommonEditorPlugin.getDefault(), MessageFormat.format(
							"Could not located an expected code formatter for ''{0}''", CONTENTTYPE_HTML_PREFIX)); //$NON-NLS-1$
				}
			}
			String contentType = contentTypeArr[contentTypeArr.length - 1];
			if (!addedFormatters.contains(contentType) && ScriptFormatterManager.hasFormatterFor(contentType))
			{
				if (!masterSet)
				{
					formatter.setMasterStrategy(contentType);
					masterSet = true;
				}
				else
				{
					formatter.setSlaveStrategy(contentType);
				}
			}
		}
		return formatter;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.text.source.SourceViewerConfiguration#getDoubleClickStrategy(org.eclipse.jface.text.source.
	 * ISourceViewer, java.lang.String)
	 */
	@Override
	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType)
	{
		if (fDoubleClickStrategy == null)
		{
			fDoubleClickStrategy = new CommonDoubleClickStrategy();
		}

		return fDoubleClickStrategy;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.editors.text.TextSourceViewerConfiguration#getHyperlinkDetectorTargets(org.eclipse.jface.text.
	 * source.ISourceViewer)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Map getHyperlinkDetectorTargets(ISourceViewer sourceViewer)
	{
		Map targets = super.getHyperlinkDetectorTargets(sourceViewer);

		targets.put("com.aptana.editor.ui.hyperlinkTarget", fTextEditor); //$NON-NLS-1$

		return targets;
	}

	/**
	 * @return the default indentation string (either tab or spaces which represents a tab)
	 */
	public String getIndent()
	{
		boolean useSpaces = fPreferenceStore
				.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS);

		if (useSpaces)
		{
			int tabWidth = fPreferenceStore.getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
			StringBuilder buf = new StringBuilder();

			for (int i = 0; i < tabWidth; ++i)
			{
				buf.append(' ');
			}

			return buf.toString();
		}

		return "\t"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.text.source.SourceViewerConfiguration#getPresentationReconciler(org.eclipse.jface.text.source
	 * .ISourceViewer)
	 */
	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer)
	{
		if (disableBackgroundReconciler)
		{
			return super.getPresentationReconciler(sourceViewer);
		}
		else
		{
			PresentationReconciler reconciler = new CommonPresentationReconciler();
			reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
			return reconciler;
		}
	}

	protected final Collection<String> getSpellingContentTypes(ISourceViewer sourceViewer)
	{
		Set<String> set = new HashSet<String>();
		IContentTypeTranslator contentTypeTranslator = CommonEditorPlugin.getDefault().getContentTypeTranslator();
		String topContentType = getTopContentTypes()[0][0];
		for (String contentType : getConfiguredContentTypes(sourceViewer))
		{
			if (CommonEditorPlugin
					.getDefault()
					.getSpellingPreferences()
					.isSpellingEnabledFor(
							contentTypeTranslator.translate(new QualifiedContentType(topContentType, contentType))))
			{
				set.add(contentType);
			}
		}
		return Collections.unmodifiableCollection(set);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.text.source.SourceViewerConfiguration#getInformationControlCreator(org.eclipse.jface.text.source
	 * .ISourceViewer)
	 */
	@Override
	public IInformationControlCreator getInformationControlCreator(ISourceViewer sourceViewer)
	{
		return new IInformationControlCreator()
		{
			public IInformationControl createInformationControl(Shell parent)
			{
				return new ThemedInformationControl(parent, SWT.NONE, new HTMLTextPresenter(true));
			}
		};
	}

	private Color getThemeBackground()
	{
		RGB bg = getCurrentTheme().getBackground();
		return ThemePlugin.getDefault().getColorManager().getColor(bg);
	}

	private Color getThemeForeground()
	{
		RGB bg = getCurrentTheme().getForeground();
		return ThemePlugin.getDefault().getColorManager().getColor(bg);
	}

	private Color getThemeSelection()
	{
		RGB bg = getCurrentTheme().getSelectionAgainstBG();
		return ThemePlugin.getDefault().getColorManager().getColor(bg);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.editors.text.TextSourceViewerConfiguration#getTextHover(org.eclipse.jface.text.source.ISourceViewer
	 * , java.lang.String)
	 */
	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType)
	{
		return new TextHover(sourceViewer);
	}

	private IInformationControl createTextHoverInformationControl(Shell parent, String statusFieldText)
	{
		return new ThemedInformationControl(parent, SWT.NONE, new HTMLTextPresenter(true), statusFieldText);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.text.source.SourceViewerConfiguration#getInformationPresenter(org.eclipse.jface.text.source
	 * .ISourceViewer)
	 */
	@Override
	public IInformationPresenter getInformationPresenter(ISourceViewer sourceViewer)
	{
		InformationPresenter presenter = new InformationPresenter(getInformationPresenterControlCreator(sourceViewer));

		presenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		presenter.setSizeConstraints(60, 10, true, true);

		return presenter;
	}

	/**
	 * getInformationPresenterControlCreator
	 * 
	 * @param sourceViewer
	 * @return
	 */
	private IInformationControlCreator getInformationPresenterControlCreator(ISourceViewer sourceViewer)
	{
		return new IInformationControlCreator()
		{
			public IInformationControl createInformationControl(Shell parent)
			{
				return new DefaultInformationControl(parent, true);
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.editors.text.TextSourceViewerConfiguration#getOverviewRulerAnnotationHover(org.eclipse.jface.text
	 * .source.ISourceViewer)
	 */
	@Override
	public IAnnotationHover getOverviewRulerAnnotationHover(ISourceViewer sourceViewer)
	{
		return new CommonAnnotationHover(true)
		{
			protected boolean isIncluded(Annotation annotation)
			{
				return isShowInOverviewRuler(annotation);
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.editors.text.TextSourceViewerConfiguration#getReconciler(org.eclipse.jface.text.source.ISourceViewer
	 * )
	 */
	@Override
	public IReconciler getReconciler(ISourceViewer sourceViewer)
	{
		if (fTextEditor != null && fTextEditor.isEditable())
		{
			IReconcilingStrategy reconcilingStrategy = new CommonReconcilingStrategy(fTextEditor);
			if (EditorsUI.getPreferenceStore().getBoolean(SpellingService.PREFERENCE_SPELLING_ENABLED))
			{
				SpellingService spellingService = EditorsUI.getSpellingService();
				Collection<String> spellingContentTypes = getSpellingContentTypes(sourceViewer);
				if (spellingService.getActiveSpellingEngineDescriptor(fPreferenceStore) != null
						&& !spellingContentTypes.isEmpty())
				{
					reconcilingStrategy = new CompositeReconcilingStrategy(reconcilingStrategy,
							new MultiRegionSpellingReconcileStrategy(sourceViewer, spellingService,
									getConfiguredDocumentPartitioning(sourceViewer), spellingContentTypes));
				}
			}
			CommonReconciler reconciler = new CommonReconciler(reconcilingStrategy);
			reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
			reconciler.setIsIncrementalReconciler(false);
			reconciler.setIsAllowedToModifyDocument(false);
			reconciler.setProgressMonitor(new NullProgressMonitor());
			reconciler.setDelay(500);
			return fReconciler = reconciler;
		}
		return null;
	}

	protected AbstractThemeableEditor getEditor()
	{
		return fTextEditor;
	}

	/**
	 * Force the current reconciler to reconcile immediately, rather than on delay
	 */
	public void forceReconcile()
	{
		if (fReconciler != null)
		{
			fReconciler.forceReconciling();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.editors.text.TextSourceViewerConfiguration#getHyperlinkPresenter(org.eclipse.jface.text.source
	 * .ISourceViewer)
	 */
	public IHyperlinkPresenter getHyperlinkPresenter(ISourceViewer sourceViewer)
	{
		RGB rgb = getCurrentTheme().getForegroundAsRGB("hyperlink"); //$NON-NLS-1$
		return new MultipleHyperlinkPresenter(rgb);
	}

	private Theme getCurrentTheme()
	{
		return ThemePlugin.getDefault().getThemeManager().getCurrentTheme();
	}

	private List<TextHoverDescriptor> getEnabledTextHoverDescriptors(ITextViewer textViewer, int offset)
	{
		List<TextHoverDescriptor> result = new ArrayList<TextHoverDescriptor>();
		if (fTextEditor == null)
		{
			return result;
		}
		try
		{
			QualifiedContentType contentType = CommonEditorPlugin.getDefault().getDocumentScopeManager()
					.getContentType(textViewer.getDocument(), offset);
			IEvaluationContext context = new EvaluationContext(null, textViewer);
			IWorkbenchPartSite site = fTextEditor.getSite();
			if (site != null)
			{
				context.addVariable(ISources.ACTIVE_EDITOR_ID_NAME, site.getId());
			}
			for (TextHoverDescriptor descriptor : TextHoverDescriptor.getContributedHovers())
			{
				if (descriptor.isEnabledFor(contentType, context))
				{
					result.add(descriptor);
				}
			}
		}
		catch (BadLocationException e)
		{
		}
		return result;
	}

	private class TextHover extends DefaultTextHover implements ITextHoverExtension, ITextHoverExtension2
	{

		private ITextHover activeTextHover;

		public TextHover(ISourceViewer sourceViewer)
		{
			super(sourceViewer);
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.text.ITextHoverExtension2#getHoverInfo2(org.eclipse.jface.text.ITextViewer,
		 * org.eclipse.jface.text.IRegion)
		 */
		@SuppressWarnings("deprecation")
		public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion)
		{
			if (activeTextHover != null)
			{
				Object info = null;
				if (activeTextHover instanceof AbstractCommonTextHover)
				{
					AbstractCommonTextHover commonHover = (AbstractCommonTextHover) activeTextHover;
					commonHover.setEditor(getEditor());
				}
				if (activeTextHover instanceof ITextHoverExtension2)
				{
					info = ((ITextHoverExtension2) activeTextHover).getHoverInfo2(textViewer, hoverRegion);
				}
				else
				{
					info = activeTextHover.getHoverInfo(textViewer, hoverRegion);
				}
				if (info != null)
				{
					return info;
				}
			}
			String defaultInfo = super.getHoverInfo(textViewer, hoverRegion);
			if (defaultInfo != null)
			{
				// wrap it in a SimpleTextHover so it will look the same as other hovers (i.e. use the theme colors,
				// etc.)
				return new SimpleTextHover(defaultInfo, null).getHoverInfo2(textViewer, hoverRegion);
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.text.DefaultTextHover#getHoverRegion(org.eclipse.jface.text.ITextViewer, int)
		 */
		@SuppressWarnings("deprecation")
		@Override
		public IRegion getHoverRegion(ITextViewer textViewer, int offset)
		{
			activeTextHover = null;
			List<TextHoverDescriptor> descriptors = getEnabledTextHoverDescriptors(textViewer, offset);
			for (TextHoverDescriptor descriptor : descriptors)
			{
				ITextHover textHover = descriptor.createTextHover();
				IRegion region = null;
				if (textHover != null)
				{
					region = textHover.getHoverRegion(textViewer, offset);
				}
				if (region != null)
				{
					if (descriptors.size() > 1)
					{
						if (textHover instanceof ITextHoverExtension2)
						{
							if (((ITextHoverExtension2) textHover).getHoverInfo2(textViewer, region) == null)
							{
								continue;
							}
						}
						else if (textHover.getHoverInfo(textViewer, region) == null)
						{
							continue;
						}
					}
					activeTextHover = textHover;
					return region;
				}
			}
			return super.getHoverRegion(textViewer, offset);
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.text.ITextHoverExtension#getHoverControlCreator()
		 */
		public IInformationControlCreator getHoverControlCreator()
		{
			if (activeTextHover instanceof ITextHoverExtension)
			{
				return ((ITextHoverExtension) activeTextHover).getHoverControlCreator();
			}
			return new IInformationControlCreator()
			{
				public IInformationControl createInformationControl(Shell parent)
				{
					return createTextHoverInformationControl(parent, EditorsUI.getTooltipAffordanceString());
				}
			};
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.text.DefaultTextHover#isIncluded(org.eclipse.jface.text.source.Annotation)
		 */
		@Override
		protected boolean isIncluded(Annotation annotation)
		{
			return isShownInText(annotation);
		}
	}
}
