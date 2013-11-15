package net.jeeeyul.eclipse.themes.css;

import net.jeeeyul.eclipse.themes.rendering.ChromeTabRendering;
import net.jeeeyul.eclipse.themes.ui.HSB;

import org.eclipse.e4.ui.css.core.dom.properties.ICSSPropertyHandler;
import org.eclipse.e4.ui.css.core.engine.CSSEngine;
import org.eclipse.e4.ui.css.swt.dom.CTabFolderElement;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolderRenderer;
import org.eclipse.swt.graphics.Color;
import org.w3c.dom.css.CSSValue;

@SuppressWarnings("restriction")
public class ChromeCSSPropertyHandler implements ICSSPropertyHandler {

	public ChromeCSSPropertyHandler() {
	}

	@Override
	public boolean applyCSSProperty(Object element, String property, CSSValue value, String pseudo, CSSEngine engine) throws Exception {
		CTabFolderElement model = (CTabFolderElement) element;
		CTabFolder folder = (CTabFolder) model.getNativeWidget();
		CTabFolderRenderer renderer = folder.getRenderer();

		if (!(renderer instanceof ChromeTabRendering)) {
			return false;
		}

		if (property.equals("chrome-selected-tab-color")) {
			ChromeTabRendering rendering = (ChromeTabRendering) renderer;
			Color color = (Color) engine.convert(value, Color.class, folder.getDisplay());
			rendering.setSelectedTabItemColor(color);
		} else if (property.equals("chrome-unselected-tab-color")) {
			ChromeTabRendering rendering = (ChromeTabRendering) renderer;
			Color color = (Color) engine.convert(value, Color.class, folder.getDisplay());
			rendering.setUnselectedTabItemColor(color);
		} else if (property.equals("chrome-shiney-shadow")) {
			ChromeTabRendering rendering = (ChromeTabRendering) renderer;
			Boolean use = (Boolean) engine.convert(value, Boolean.class, folder.getDisplay());
			rendering.setShowShineyShadow(use);
		} else if (property.equals("chrome-selected-tab-fill-highlight")) {
			ChromeTabRendering rendering = (ChromeTabRendering) renderer;
			Color color = (Color) engine.convert(value, Color.class, folder.getDisplay());
			rendering.setSelectedTabFillHighlightColor(color);
		}

		return true;
	}

	@Override
	public String retrieveCSSProperty(Object element, String property, String pseudo, CSSEngine engine) throws Exception {
		CTabFolderElement model = (CTabFolderElement) element;
		CTabFolder folder = (CTabFolder) model.getNativeWidget();
		CTabFolderRenderer renderer = folder.getRenderer();

		if (!(renderer instanceof ChromeTabRendering)) {
			return null;
		}

		ChromeTabRendering rendering = (ChromeTabRendering) renderer;
		if (property.equals("chrome-selected-tab-color")) {
			Color color = rendering.getSelectedTabItemColor();
			if (color == null) {
				return null;
			}
			return new HSB(color.getRGB()).toHTMLCode();
		} else if (property.equals("chrome-unselected-tab-color")) {
			Color color = rendering.getUnselectedTabItemColor();
			if (color == null) {
				return null;
			}
			return new HSB(color.getRGB()).toHTMLCode();
		} else if (property.equals("chrome-shiney-shadow")) {
			return Boolean.toString(rendering.isShowShineyShadow());
		} else if (property.equals("chrome-selected-tab-fill-highlight")) {
			Color color = rendering.getSelectedTabFillHighlightColor();
			if (color == null) {
				return null;
			}
			return new HSB(color.getRGB()).toHTMLCode();
		}
		return null;
	}

}
