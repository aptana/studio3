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
import java.util.Map;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

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

		selectors = new ArrayList<IScopeSelector>();

		for (ThemeRule rule : theme.getTokens())
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
				attr = merge(attr, (DelayedTextAttribute) parentAttr);
			}
			return attr;
		}

		// Some tokens are special. They have fallbacks even if not in the theme! Looks like bundles can contribute
		// them?
		if (new ScopeSelector("markup.changed").matches(scope)) //$NON-NLS-1$
		{
			return new DelayedTextAttribute(new RGBa(255, 255, 255), new RGBa(248, 205, 14), SWT.NORMAL);
		}
		if (new ScopeSelector("markup.deleted").matches(scope)) //$NON-NLS-1$
		{
			return new DelayedTextAttribute(new RGBa(255, 255, 255), new RGBa(255, 86, 77), SWT.NORMAL);
		}
		if (new ScopeSelector("markup.inserted").matches(scope)) //$NON-NLS-1$
		{
			return new DelayedTextAttribute(new RGBa(0, 0, 0), new RGBa(128, 250, 120), SWT.NORMAL);
		}
		if (new ScopeSelector("markup.underline").matches(scope)) //$NON-NLS-1$
		{
			return new DelayedTextAttribute(null, null, TextAttribute.UNDERLINE);
		}
		if (new ScopeSelector("markup.bold").matches(scope)) //$NON-NLS-1$
		{
			return new DelayedTextAttribute(null, null, SWT.BOLD);
		}
		if (new ScopeSelector("markup.italic").matches(scope)) //$NON-NLS-1$
		{
			return new DelayedTextAttribute(null, null, SWT.ITALIC);
		}
		if (new ScopeSelector("meta.diff.index").matches(scope) || new ScopeSelector("meta.diff.range").matches(scope) || new ScopeSelector("meta.separator.diff").matches(scope)) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		{
			return new DelayedTextAttribute(new RGBa(255, 255, 255), new RGBa(65, 126, 218), SWT.ITALIC);
		}
		if (new ScopeSelector("meta.diff.header").matches(scope)) //$NON-NLS-1$
		{
			return new DelayedTextAttribute(new RGBa(255, 255, 255), new RGBa(103, 154, 233), SWT.NORMAL);
		}
		if (new ScopeSelector("meta.separator").matches(scope)) //$NON-NLS-1$
		{
			return new DelayedTextAttribute(new RGBa(255, 255, 255), new RGBa(52, 103, 209), SWT.NORMAL);
		}
		if (theme.hasDarkBG())
		{
			if (new ScopeSelector("console.error").matches(scope)) //$NON-NLS-1$
			{
				return new DelayedTextAttribute(new RGBa(255, 0, 0), null, SWT.NORMAL);
			}
			if (new ScopeSelector("console.input").matches(scope)) //$NON-NLS-1$
			{
				return new DelayedTextAttribute(new RGBa(95, 175, 176), null, SWT.NORMAL);
			}
			if (new ScopeSelector("console.prompt").matches(scope)) //$NON-NLS-1$
			{
				return new DelayedTextAttribute(new RGBa(131, 132, 161), null, SWT.NORMAL);
			}
			if (new ScopeSelector("console.warning").matches(scope)) //$NON-NLS-1$
			{
				return new DelayedTextAttribute(new RGBa(255, 215, 0), null, SWT.NORMAL);
			}
			if (new ScopeSelector("console.debug").matches(scope)) //$NON-NLS-1$
			{
				return new DelayedTextAttribute(new RGBa(255, 236, 139), null, SWT.NORMAL);
			}
			if (new ScopeSelector("hyperlink").matches(scope)) //$NON-NLS-1$
			{
				return new DelayedTextAttribute(new RGBa(84, 143, 160), null, SWT.NORMAL);
			}
		}
		else
		{
			if (new ScopeSelector("console.error").matches(scope)) //$NON-NLS-1$
			{
				return new DelayedTextAttribute(new RGBa(255, 0, 0), null, SWT.NORMAL);
			}
			if (new ScopeSelector("console.input").matches(scope)) //$NON-NLS-1$
			{
				return new DelayedTextAttribute(new RGBa(63, 127, 95), null, SWT.NORMAL);
			}
			if (new ScopeSelector("console.prompt").matches(scope)) //$NON-NLS-1$
			{
				return new DelayedTextAttribute(new RGBa(42, 0, 255), null, SWT.NORMAL);
			}
			if (new ScopeSelector("console.warning").matches(scope)) //$NON-NLS-1$
			{
				return new DelayedTextAttribute(new RGBa(205, 102, 0), null, SWT.NORMAL);
			}
			if (new ScopeSelector("console.debug").matches(scope)) //$NON-NLS-1$
			{
				return new DelayedTextAttribute(new RGBa(93, 102, 102), null, SWT.NORMAL);
			}
			if (new ScopeSelector("hyperlink").matches(scope)) //$NON-NLS-1$
			{
				return new DelayedTextAttribute(new RGBa(13, 17, 113), null, SWT.NORMAL);
			}
		}
		return new DelayedTextAttribute(new RGBa(defaultFG));
	}

	private TextAttribute toTextAttribute(DelayedTextAttribute delayedOrTextAttr, boolean forceColor)
	{
		DelayedTextAttribute attr = (DelayedTextAttribute) delayedOrTextAttr;
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
