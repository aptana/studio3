package com.aptana.terminal.internal.emulator;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.tm.internal.terminal.control.ITerminalListener;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalConnector;
import org.eclipse.tm.internal.terminal.textcanvas.ILinelRenderer;
import org.eclipse.tm.internal.terminal.textcanvas.ITextCanvasModel;


@SuppressWarnings("restriction")
public class VT100TerminalControl extends org.eclipse.tm.internal.terminal.emulator.VT100TerminalControl {

	public VT100TerminalControl(ITerminalListener target, Composite wndParent, ITerminalConnector[] connectors) {
		super(target, wndParent, connectors);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.VT100TerminalControl#createLineRenderer(org.eclipse.tm.internal.terminal.textcanvas.ITextCanvasModel)
	 */
	@Override
	protected ILinelRenderer createLineRenderer(ITextCanvasModel model) {
		return new ThemedTextLineRenderer(model);
	}

}
