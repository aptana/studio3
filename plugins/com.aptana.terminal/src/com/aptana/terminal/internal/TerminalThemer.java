package com.aptana.terminal.internal;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.RGB;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.theme.Theme;

@SuppressWarnings("nls")
public class TerminalThemer
{

	public static void apply()
	{
		Theme theme = CommonEditorPlugin.getDefault().getThemeManager().getCurrentTheme();
		setColor(0, 0, 0, theme.getForeground()); // black
		setColor(255, 255, 255, theme.getBackground()); // white
		setColor(229, 229, 229, "ansi.white"); // white fg
		setColor(255, 128, 128, "ansi.red"); // RED
		setColor(128, 255, 128, "ansi.green"); // GREEN
		setColor(128, 128, 255, "ansi.blue"); // BLUE
		setColor(255, 255, 0, "ansi.yellow"); // YELLOW
		setColor(0, 255, 255, "ansi.cyan"); // CYAN
		setColor(255, 255, 0, "ansi.magenta"); // MAGENTA
	}

	private static void setColor(int r, int g, int b, String tokenName)
	{
		Theme theme = CommonEditorPlugin.getDefault().getThemeManager().getCurrentTheme();
		RGB rgb = new RGB(r, g, b);
		if (theme.hasEntry(tokenName))
		{
			rgb = theme.getForegroundAsRGB(tokenName);
		}
		setColor(r, g, b, rgb);
	}

	private static void setColor(int r, int g, int b, RGB rgb)
	{
		JFaceResources.getColorRegistry().put("org.eclipse.tm.internal." + r + "-" + g + "-" + b, rgb);
	}

}
