/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import com.aptana.core.util.ImmutableTuple;
import com.aptana.scope.IScopeSelector;
import com.aptana.scope.ScopeSelector;

/**
 * Helper class used to get the text attribute for a given scope (given the related theme). Should not be manipulated
 * directly (only through the Theme class). Caches information based on the theme, so, if it changes, this instance
 * should be deleted and a new one created.
 */
/* default */class ThemeGetTextAttribute
{

	/**
	 * Used for recursion in getDelayedTextAttribute to avoid matching same rule on scope twice
	 */
	private IScopeSelector lastSelectorMatch;
	private final ColorManager colorManager;
	private final Theme theme;
	private final RGB defaultFG;
	private final RGB defaultBG;
	private final Collection<IScopeSelector> selectors;

	/**
	 * A cache to memoize the ultimate TextAttribute generated for a given fully qualified scope.
	 */
	private final Map<String, TextAttribute> cacheGetTextAttribute;
	private static volatile ImmutableTuple<ScopeSelector, DelayedTextAttribute>[] scopeToAttribute;
	private static volatile ImmutableTuple<ScopeSelector, DelayedTextAttribute>[] scopeToAttributeLight;
	private static volatile ImmutableTuple<ScopeSelector, DelayedTextAttribute>[] scopeToAttributeDark;

	/**
	 * A cache to memoize internally gotten delayed text attributes.
	 */
	private final Map<String, DelayedTextAttribute> cacheDelayedGetTextAttribute;

	public ThemeGetTextAttribute(Theme theme)
	{
		this.theme = theme;
		this.colorManager = theme.getColorManager();
		this.defaultFG = theme.getForeground();
		this.defaultBG = theme.getBackground();
		this.cacheGetTextAttribute = new HashMap<String, TextAttribute>();
		this.cacheDelayedGetTextAttribute = new HashMap<String, DelayedTextAttribute>();

		List<ThemeRule> tokens = theme.getTokens();
		selectors = new ArrayList<IScopeSelector>(tokens.size());

		for (ThemeRule rule : tokens)
		{
			if (rule.isSeparator())
			{
				continue;
			}
			selectors.add(rule.getScopeSelector());
		}
	}

	/* default */IScopeSelector findMatch(String scope)
	{
		return ScopeSelector.bestMatch(selectors, scope);
	}

	/* default */synchronized TextAttribute getTextAttribute(String scope)
	{
		TextAttribute ta = cacheGetTextAttribute.get(scope);
		if (ta != null)
		{
			return ta;
		}
		ta = internalGetTextAttribute(scope);
		cacheGetTextAttribute.put(scope, ta);
		return ta;
	}

	private TextAttribute internalGetTextAttribute(String scope)
	{
		lastSelectorMatch = null;
		TextAttribute ta = toTextAttribute(getDelayedTextAttribute(scope), true);
		return ta;
	}

	private DelayedTextAttribute getDelayedTextAttribute(String scope)
	{
		DelayedTextAttribute delayedTextAttribute = cacheDelayedGetTextAttribute.get(scope);
		if (delayedTextAttribute != null)
		{
			return delayedTextAttribute;
		}
		delayedTextAttribute = internalDelayedTextAttribute(scope);
		cacheDelayedGetTextAttribute.put(scope, delayedTextAttribute);
		return delayedTextAttribute;
	}

	/**
	 * @return DelayedTextAttribute or TextAttribute if already gotten from the cache.
	 */
	private DelayedTextAttribute getParent(String scope)
	{
		DelayedTextAttribute parentAttr = null;
		int index = scope.lastIndexOf(' ');
		if (index != -1)
		{
			String subType = scope.substring(0, index);
			parentAttr = getDelayedTextAttribute(subType);
		}
		if (parentAttr == null)
		{
			// If we never find a parent, use default bg
			parentAttr = new DelayedTextAttribute(new RGBa(defaultFG), new RGBa(defaultBG), 0);
		}
		return parentAttr;
	}

	/**
	 * @return DelayedTextAttribute or TextAttribute if already gotten from the cache.
	 */
	private DelayedTextAttribute internalDelayedTextAttribute(String scope)
	{
		IScopeSelector match = findMatch(scope);
		if (match != null)
		{
			// This is to avoid matching the same selector multiple times when recursing up the scope! Basically our
			// match may have been many steps up our scope, not at the end!
			if (lastSelectorMatch != null && lastSelectorMatch.equals(match))
			{
				// We just matched the same rule! We need to recurse from parent scope!
				return getParent(scope);
			}
			lastSelectorMatch = match;
			ThemeRule rule = theme.getRuleForSelector(match);
			DelayedTextAttribute attr = rule.getTextAttribute();

			// if our coloring has no background, we should use parent's. If it has some opacity (alpha != 255), we
			// need to alpha blend
			if (attr.background == null || !attr.background.isFullyOpaque())
			{
				// Need to merge bg color up the scope!
				DelayedTextAttribute parentAttr = getParent(scope);
				// Now do actual merge
				attr = merge(attr, parentAttr);
			}
			return attr;
		}
		if (scopeToAttribute == null)
		{
			initializeScopeToAttribute();
		}

		for (ImmutableTuple<ScopeSelector, DelayedTextAttribute> tup : scopeToAttribute)
		{
			if (tup.first.matches(scope))
			{
				return tup.second;
			}
		}
		if (theme.hasDarkBG())
		{
			for (ImmutableTuple<ScopeSelector, DelayedTextAttribute> tup : scopeToAttributeDark)
			{
				if (tup.first.matches(scope))
				{
					return tup.second;
				}
			}
		}
		else
		{
			for (ImmutableTuple<ScopeSelector, DelayedTextAttribute> tup : scopeToAttributeLight)
			{
				if (tup.first.matches(scope))
				{
					return tup.second;
				}
			}
		}

		return new DelayedTextAttribute(new RGBa(defaultFG));
	}

	@SuppressWarnings("unchecked")
	private void initializeScopeToAttribute()
	{
		// Some tokens are special. They have fallbacks even if not in the theme! Looks like bundles can contribute
		// them?

		scopeToAttribute = new ImmutableTuple[] {
				new ImmutableTuple<ScopeSelector, DelayedTextAttribute>(new ScopeSelector("markup.changed"), //$NON-NLS-1$
						new DelayedTextAttribute(new RGBa(255, 255, 255), new RGBa(248, 205, 14), SWT.NORMAL)),

				new ImmutableTuple<ScopeSelector, DelayedTextAttribute>(new ScopeSelector("markup.deleted"), //$NON-NLS-1$
						new DelayedTextAttribute(new RGBa(255, 255, 255), new RGBa(255, 86, 77), SWT.NORMAL)),

				new ImmutableTuple<ScopeSelector, DelayedTextAttribute>(new ScopeSelector("markup.inserted"), //$NON-NLS-1$
						new DelayedTextAttribute(new RGBa(0, 0, 0), new RGBa(128, 250, 120), SWT.NORMAL)),

				new ImmutableTuple<ScopeSelector, DelayedTextAttribute>(new ScopeSelector("markup.underline"), //$NON-NLS-1$
						new DelayedTextAttribute(null, null, TextAttribute.UNDERLINE)),

				new ImmutableTuple<ScopeSelector, DelayedTextAttribute>(new ScopeSelector("markup.bold"), //$NON-NLS-1$
						new DelayedTextAttribute(null, null, SWT.BOLD)),

				new ImmutableTuple<ScopeSelector, DelayedTextAttribute>(new ScopeSelector("markup.italic"), //$NON-NLS-1$
						new DelayedTextAttribute(null, null, SWT.ITALIC)),

				// note: meta.diff.index, meta.diff.range and meta.separator.diff return the same thing.
				new ImmutableTuple<ScopeSelector, DelayedTextAttribute>(new ScopeSelector("meta.diff.index"), //$NON-NLS-1$
						new DelayedTextAttribute(new RGBa(255, 255, 255), new RGBa(65, 126, 218), SWT.ITALIC)),

				new ImmutableTuple<ScopeSelector, DelayedTextAttribute>(new ScopeSelector("meta.diff.range"), //$NON-NLS-1$
						new DelayedTextAttribute(new RGBa(255, 255, 255), new RGBa(65, 126, 218), SWT.ITALIC)),

				new ImmutableTuple<ScopeSelector, DelayedTextAttribute>(new ScopeSelector("meta.separator.diff"), //$NON-NLS-1$
						new DelayedTextAttribute(new RGBa(255, 255, 255), new RGBa(65, 126, 218), SWT.ITALIC)),

				new ImmutableTuple<ScopeSelector, DelayedTextAttribute>(new ScopeSelector("meta.diff.header"), //$NON-NLS-1$
						new DelayedTextAttribute(new RGBa(255, 255, 255), new RGBa(103, 154, 233), SWT.NORMAL)),

				new ImmutableTuple<ScopeSelector, DelayedTextAttribute>(new ScopeSelector("meta.separator"), //$NON-NLS-1$
						new DelayedTextAttribute(new RGBa(255, 255, 255), new RGBa(52, 103, 209), SWT.NORMAL)) };

		scopeToAttributeDark = new ImmutableTuple[] {
				new ImmutableTuple<ScopeSelector, DelayedTextAttribute>(new ScopeSelector("console.error"), //$NON-NLS-1$
						new DelayedTextAttribute(new RGBa(255, 0, 0), null, SWT.NORMAL)),

				new ImmutableTuple<ScopeSelector, DelayedTextAttribute>(new ScopeSelector("console.input"), //$NON-NLS-1$
						new DelayedTextAttribute(new RGBa(95, 175, 176), null, SWT.NORMAL)),

				new ImmutableTuple<ScopeSelector, DelayedTextAttribute>(new ScopeSelector("console.prompt"), //$NON-NLS-1$
						new DelayedTextAttribute(new RGBa(131, 132, 161), null, SWT.NORMAL)),

				new ImmutableTuple<ScopeSelector, DelayedTextAttribute>(new ScopeSelector("console.warning"), //$NON-NLS-1$
						new DelayedTextAttribute(new RGBa(255, 215, 0), null, SWT.NORMAL)),

				new ImmutableTuple<ScopeSelector, DelayedTextAttribute>(new ScopeSelector("console.debug"), //$NON-NLS-1$
						new DelayedTextAttribute(new RGBa(255, 236, 139), null, SWT.NORMAL)),

				new ImmutableTuple<ScopeSelector, DelayedTextAttribute>(new ScopeSelector("hyperlink"), //$NON-NLS-1$
						new DelayedTextAttribute(new RGBa(84, 143, 160), null, SWT.NORMAL)) };

		scopeToAttributeLight = new ImmutableTuple[] {
				new ImmutableTuple<ScopeSelector, DelayedTextAttribute>(new ScopeSelector("console.error"), //$NON-NLS-1$
						new DelayedTextAttribute(new RGBa(255, 0, 0), null, SWT.NORMAL)),

				new ImmutableTuple<ScopeSelector, DelayedTextAttribute>(new ScopeSelector("console.input"), //$NON-NLS-1$
						new DelayedTextAttribute(new RGBa(63, 127, 95), null, SWT.NORMAL)),

				new ImmutableTuple<ScopeSelector, DelayedTextAttribute>(new ScopeSelector("console.prompt"), //$NON-NLS-1$
						new DelayedTextAttribute(new RGBa(42, 0, 255), null, SWT.NORMAL)),

				new ImmutableTuple<ScopeSelector, DelayedTextAttribute>(new ScopeSelector("console.warning"), //$NON-NLS-1$
						new DelayedTextAttribute(new RGBa(205, 102, 0), null, SWT.NORMAL)),

				new ImmutableTuple<ScopeSelector, DelayedTextAttribute>(new ScopeSelector("console.debug"), //$NON-NLS-1$
						new DelayedTextAttribute(new RGBa(93, 102, 102), null, SWT.NORMAL)),

				new ImmutableTuple<ScopeSelector, DelayedTextAttribute>(new ScopeSelector("hyperlink"), //$NON-NLS-1$
						new DelayedTextAttribute(new RGBa(13, 17, 113), null, SWT.NORMAL)) };
	}

	private TextAttribute toTextAttribute(DelayedTextAttribute delayedOrTextAttr, boolean forceColor)
	{
		DelayedTextAttribute attr = delayedOrTextAttr;
		Color fg = null;
		if (attr.foreground != null || forceColor)
		{
			fg = colorManager.getColor(merge(attr.foreground, null, defaultFG).toRGB());
		}
		Color bg = null;
		if (attr.background != null || forceColor)
		{
			bg = colorManager.getColor(merge(attr.background, null, defaultBG).toRGB());
		}
		return new TextAttribute(fg, bg, attr.style);
	}

	private DelayedTextAttribute merge(DelayedTextAttribute childAttr, DelayedTextAttribute parentAttr)
	{
		return new DelayedTextAttribute(merge(childAttr.foreground, parentAttr.foreground, defaultFG), merge(
				childAttr.background, parentAttr.background, defaultBG), childAttr.style | parentAttr.style);
	}

	private RGBa merge(RGBa top, RGBa bottom, RGB defaultParent)
	{
		if (top == null && bottom == null)
		{
			return new RGBa(defaultParent);
		}
		if (top == null) // for some reason there is no top.
		{
			return bottom;
		}
		if (top.isFullyOpaque()) // top has no transparency, just return it
		{
			return top;
		}
		if (bottom == null) // there is no parent, merge onto default FG/BG for theme
		{
			return new RGBa(Theme.alphaBlend(defaultParent, top.toRGB(), top.getAlpha()));
		}
		return new RGBa(Theme.alphaBlend(bottom.toRGB(), top.toRGB(), top.getAlpha()));
	}
}
