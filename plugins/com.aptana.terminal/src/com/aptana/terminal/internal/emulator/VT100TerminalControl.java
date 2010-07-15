package com.aptana.terminal.internal.emulator;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.tm.internal.terminal.control.ITerminalListener;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalConnector;
import org.eclipse.tm.internal.terminal.textcanvas.ILinelRenderer;
import org.eclipse.tm.internal.terminal.textcanvas.ITextCanvasModel;

import com.aptana.theme.IThemeManager;
import com.aptana.theme.ThemePlugin;

public class VT100TerminalControl extends org.eclipse.tm.internal.terminal.emulator.VT100TerminalControl {

	private IPreferenceChangeListener preferenceChangeListener;
	
	public VT100TerminalControl(ITerminalListener target, Composite wndParent, ITerminalConnector[] connectors) {
		super(target, wndParent, connectors);
		getRootControl().setBackground(ThemedTextLineRenderer.getStyleMap().getBackgroundColor());
		preferenceChangeListener = new IPreferenceChangeListener() {
			@Override
			public void preferenceChange(PreferenceChangeEvent event) {
				if (IThemeManager.THEME_CHANGED.equals(event.getKey())) {
					Control control = getRootControl();
					if (!control.isDisposed()) {
						control.setBackground(ThemedTextLineRenderer.getStyleMap().getBackgroundColor());
						getCtlText().redraw();
					}
				}
			}
		};
		new InstanceScope().getNode(ThemePlugin.PLUGIN_ID).addPreferenceChangeListener(preferenceChangeListener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.VT100TerminalControl#disposeTerminal()
	 */
	@Override
	public void disposeTerminal() {
		new InstanceScope().getNode(ThemePlugin.PLUGIN_ID).removePreferenceChangeListener(preferenceChangeListener);
		super.disposeTerminal();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.VT100TerminalControl#createLineRenderer(org.eclipse.tm.internal.terminal.textcanvas.ITextCanvasModel)
	 */
	@Override
	protected ILinelRenderer createLineRenderer(ITextCanvasModel model) {
		return new ThemedTextLineRenderer(model);
	}

}
