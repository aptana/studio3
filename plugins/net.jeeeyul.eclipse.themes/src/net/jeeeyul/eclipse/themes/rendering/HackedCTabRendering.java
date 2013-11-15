package net.jeeeyul.eclipse.themes.rendering;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.inject.Inject;

import net.jeeeyul.eclipse.themes.SharedImages;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolderRenderer;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

public class HackedCTabRendering extends CTabFolderRenderer {
	public static Field HACK_CTabItem_closeRect;
	public static Field HACK_CTabItem_shortenText;
	public static Field HACK_CTabItem_shortenTextWidth;
	public static Field HACK_CTabItem_closeImageState;
	public static Field HACK_CTabFolder_curveWidth;
	public static Field HACK_CTabFolder_curveIndent;

	public static Method HACK_CTabFolder_getRightItemEdge;
	public static Method HACK_CTabFolder_updateItems;

	static {
		try {
			HACK_CTabItem_closeRect = CTabItem.class.getDeclaredField("closeRect");
			HACK_CTabItem_closeRect.setAccessible(true);

			HACK_CTabItem_shortenText = CTabItem.class.getDeclaredField("shortenedText");
			HACK_CTabItem_shortenText.setAccessible(true);

			HACK_CTabItem_shortenTextWidth = CTabItem.class.getDeclaredField("shortenedTextWidth");
			HACK_CTabItem_shortenTextWidth.setAccessible(true);

			HACK_CTabItem_closeImageState = CTabItem.class.getDeclaredField("closeImageState");
			HACK_CTabItem_closeImageState.setAccessible(true);

			HACK_CTabFolder_curveWidth = CTabFolderRenderer.class.getDeclaredField("curveWidth");
			HACK_CTabFolder_curveWidth.setAccessible(true);

			HACK_CTabFolder_curveIndent = CTabFolderRenderer.class.getDeclaredField("curveIndent");
			HACK_CTabFolder_curveIndent.setAccessible(true);

			HACK_CTabFolder_getRightItemEdge = CTabFolder.class.getDeclaredMethod("getRightItemEdge", GC.class);
			HACK_CTabFolder_getRightItemEdge.setAccessible(true);

			HACK_CTabFolder_updateItems = CTabFolder.class.getDeclaredMethod("updateItems");
			HACK_CTabFolder_updateItems.setAccessible(true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Constants for circle drawing
	final static int LEFT_TOP = 0;

	final static int LEFT_BOTTOM = 1;

	final static int RIGHT_TOP = 2;

	final static int RIGHT_BOTTOM = 3;

	// drop shadow constants
	final static int SIDE_DROP_WIDTH = 3;

	final static int BOTTOM_DROP_WIDTH = 4;

	// keylines
	final static int OUTER_KEYLINE = 1;

	final static int INNER_KEYLINE = 0;

	final static int TOP_KEYLINE = 0;

	// Item Constants
	static final int ITEM_TOP_MARGIN = 2;

	static final int ITEM_BOTTOM_MARGIN = 6;
	static final int ITEM_LEFT_MARGIN = 4;
	static final int ITEM_RIGHT_MARGIN = 4;
	static final int INTERNAL_SPACING = 4;

	static final String E4_SHADOW_IMAGE = "org.eclipse.e4.renderer.shadow_image"; //$NON-NLS-1$
	static final String E4_TOOLBAR_ACTIVE_IMAGE = "org.eclipse.e4.renderer.toolbar_background_active_image"; //$NON-NLS-1$

	static final String E4_TOOLBAR_INACTIVE_IMAGE = "org.eclipse.e4.renderer.toolbar_background_inactive_image"; //$NON-NLS-1$

	static int blend(int v1, int v2, int ratio) {
		int b = (ratio * v1 + (100 - ratio) * v2) / 100;
		return Math.min(255, b);
	}

	static RGB blend(RGB c1, RGB c2, int ratio) {
		int r = blend(c1.red, c2.red, ratio);
		int g = blend(c1.green, c2.green, ratio);
		int b = blend(c1.blue, c2.blue, ratio);
		return new RGB(r, g, b);
	}

	static int[] drawCircle(int xC, int yC, int r, int circlePart) {
		int x = 0, y = r, u = 1, v = 2 * r - 1, e = 0;
		int[] points = new int[1024];
		int[] pointsMirror = new int[1024];
		int loop = 0;
		int loopMirror = 0;
		while (x < y) {
			if (circlePart == RIGHT_BOTTOM) {
				points[loop++] = xC + x;
				points[loop++] = yC + y;
			}
			if (circlePart == RIGHT_TOP) {
				points[loop++] = xC + y;
				points[loop++] = yC - x;
			}
			if (circlePart == LEFT_TOP) {
				points[loop++] = xC - x;
				points[loop++] = yC - y;
			}
			if (circlePart == LEFT_BOTTOM) {
				points[loop++] = xC - y;
				points[loop++] = yC + x;
			}
			x++;
			e += u;
			u += 2;
			if (v < 2 * e) {
				y--;
				e -= v;
				v -= 2;
			}
			if (x > y)
				break;
			if (circlePart == RIGHT_BOTTOM) {
				pointsMirror[loopMirror++] = xC + y;
				pointsMirror[loopMirror++] = yC + x;
			}
			if (circlePart == RIGHT_TOP) {
				pointsMirror[loopMirror++] = xC + x;
				pointsMirror[loopMirror++] = yC - y;
			}
			if (circlePart == LEFT_TOP) {
				pointsMirror[loopMirror++] = xC - y;
				pointsMirror[loopMirror++] = yC - x;
			}
			if (circlePart == LEFT_BOTTOM) {
				pointsMirror[loopMirror++] = xC - x;
				pointsMirror[loopMirror++] = yC + y;
			}
			// grow?
			if ((loop + 1) > points.length) {
				int length = points.length * 2;
				int[] newPointTable = new int[length];
				int[] newPointTableMirror = new int[length];
				System.arraycopy(points, 0, newPointTable, 0, points.length);
				points = newPointTable;
				System.arraycopy(pointsMirror, 0, newPointTableMirror, 0, pointsMirror.length);
				pointsMirror = newPointTableMirror;
			}
		}
		int[] finalArray = new int[loop + loopMirror];
		System.arraycopy(points, 0, finalArray, 0, loop);
		for (int i = loopMirror - 1, j = loop; i > 0; i = i - 2, j = j + 2) {
			int tempY = pointsMirror[i];
			int tempX = pointsMirror[i - 1];
			finalArray[j] = tempX;
			finalArray[j + 1] = tempY;
		}
		return finalArray;
	}

	int[] shape;
	Image shadowImage, toolbarActiveImage, toolbarInactiveImage;
	int cornerSize = 14;
	boolean shadowEnabled = true;

	Color shadowColor;
	Color outerKeyline, innerKeyline;
	Color[] activeToolbar;

	int[] activePercents;

	Color[] inactiveToolbar;

	int[] inactivePercents;

	boolean active;
	Color selectedTabFillColor;
	Color tabOutlineColor;
	int paddingLeft = 0, paddingRight = 0, paddingTop = 0, paddingBottom = 0;

	protected Color selectedTabItemColor;
	protected Color unselectedTabItemColor;
	protected Color selectedTabFillHighlightColor;

	@Inject
	public HackedCTabRendering(CTabFolder parent) {
		super(parent);
	}

	void _drawClose(GC gc, Rectangle closeRect, int closeImageState) {
		if (closeRect.width == 0 || closeRect.height == 0)
			return;

		// draw X 9x9
		int x = closeRect.x + Math.max(1, (closeRect.width - 9) / 2);
		int y = closeRect.y + Math.max(1, (closeRect.height - 9) / 2);
		y += parent.getTabPosition() == SWT.BOTTOM ? -1 : 1;

		switch (closeImageState & (SWT.HOT | SWT.SELECTED | SWT.BACKGROUND)) {
		case SWT.NONE: {
			gc.drawImage(SharedImages.getImage(SharedImages.CLOSE_NORMAL), x, y);
			break;
		}
		case SWT.HOT: {
			gc.drawImage(SharedImages.getImage(SharedImages.CLOSE_ACTIVE), x, y);
			break;
		}
		case SWT.SELECTED: {
			gc.drawImage(SharedImages.getImage(SharedImages.CLOSE_ACTIVE), x + 1, y + 1);
			break;
		}
		case SWT.BACKGROUND: {
			gc.drawImage(SharedImages.getImage(SharedImages.CLOSE_NORMAL), x, y);
			break;
		}
		}
	}

	String _shortenText(GC gc, String text, int width) {
		return parent.getSimple() ? _shortenText(gc, text, width, "...") : _shortenText(gc, text, width, ""); //$NON-NLS-1$
	}

	String _shortenText(GC gc, String text, int width, String ellipses) {
		if (gc.textExtent(text, SWT.DRAW_TRANSPARENT | SWT.DRAW_MNEMONIC).x <= width)
			return text;
		int ellipseWidth = gc.textExtent(ellipses, SWT.DRAW_TRANSPARENT | SWT.DRAW_MNEMONIC).x;
		int length = text.length();
		TextLayout layout = new TextLayout(parent.getDisplay());
		layout.setText(text);
		int end = layout.getPreviousOffset(length, SWT.MOVEMENT_CLUSTER);
		while (end > 0) {
			text = text.substring(0, end);
			int l = gc.textExtent(text, SWT.DRAW_TRANSPARENT | SWT.DRAW_MNEMONIC).x;
			if (l + ellipseWidth <= width) {
				break;
			}
			end = layout.getPreviousOffset(end, SWT.MOVEMENT_CLUSTER);
		}
		layout.dispose();
		return end == 0 ? text.substring(0, 1) : text + ellipses;
	}

	public ImageData blur(Image src, int radius, int sigma) {
		float[] kernel = create1DKernel(radius, sigma);

		ImageData imgPixels = src.getImageData();
		int width = imgPixels.width;
		int height = imgPixels.height;

		int[] inPixels = new int[width * height];
		int[] outPixels = new int[width * height];
		int offset = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				RGB rgb = imgPixels.palette.getRGB(imgPixels.getPixel(x, y));
				if (rgb.red == 255 && rgb.green == 255 && rgb.blue == 255) {
					inPixels[offset] = (rgb.red << 16) | (rgb.green << 8) | rgb.blue;
				} else {
					inPixels[offset] = (imgPixels.getAlpha(x, y) << 24) | (rgb.red << 16) | (rgb.green << 8) | rgb.blue;
				}
				offset++;
			}
		}

		convolve(kernel, inPixels, outPixels, width, height, true);
		convolve(kernel, outPixels, inPixels, height, width, true);

		ImageData dst = new ImageData(imgPixels.width, imgPixels.height, 24, new PaletteData(0xff0000, 0xff00, 0xff));

		dst.setPixels(0, 0, inPixels.length, inPixels, 0);
		offset = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (inPixels[offset] == -1) {
					dst.setAlpha(x, y, 0);
				} else {
					int a = (inPixels[offset] >> 24) & 0xff;
					// if (a < 150) a = 0;
					dst.setAlpha(x, y, a);
				}
				offset++;
			}
		}
		return dst;
	}

	private int clamp(int value) {
		if (value > 255)
			return 255;
		if (value < 0)
			return 0;
		return value;
	}

	protected Point computeSize(int part, int state, GC gc, int wHint, int hHint) {
		wHint += paddingLeft + paddingRight;
		hHint += paddingTop + paddingBottom;
		if (0 <= part && part < parent.getItemCount()) {
			gc.setAdvanced(true);
			Point result = super.computeSize(part, state, gc, wHint, hHint);
			gc.setAdvanced(false);
			return result;
		}
		return super.computeSize(part, state, gc, wHint, hHint);
	}

	protected Rectangle computeTrim(int part, int state, int x, int y, int width, int height) {
		int borderTop = TOP_KEYLINE + OUTER_KEYLINE;
		int borderBottom = INNER_KEYLINE + OUTER_KEYLINE;
		int marginHeight = parent.marginHeight;
		int sideDropWidth = shadowEnabled ? SIDE_DROP_WIDTH : 0;
		switch (part) {
		case PART_BODY:
			x = -1 - paddingLeft;
			int tabHeight = parent.getTabHeight() + 1;
			y = y - paddingTop - marginHeight - tabHeight - borderTop - (cornerSize / 4);
			width = 2 + paddingLeft + paddingRight;
			height += paddingTop + paddingBottom;
			height += tabHeight + (cornerSize / 4) + borderBottom + borderTop;
			break;
		case PART_HEADER:
			x = x - (INNER_KEYLINE + OUTER_KEYLINE) - sideDropWidth;
			width = width + 2 * (INNER_KEYLINE + OUTER_KEYLINE + sideDropWidth);
			break;
		case PART_BORDER:
			x = x - INNER_KEYLINE - OUTER_KEYLINE - sideDropWidth - (cornerSize / 4);
			width = width + 2 * (INNER_KEYLINE + OUTER_KEYLINE + sideDropWidth) + cornerSize / 2;
			y = y - borderTop;
			height = height + borderTop + borderBottom;
			break;
		default:
			if (0 <= part && part < parent.getItemCount()) {
				x = x - ITEM_LEFT_MARGIN;// - (CORNER_SIZE/2);
				width = width + ITEM_LEFT_MARGIN + ITEM_RIGHT_MARGIN + 1;
				y = y - ITEM_TOP_MARGIN;
				height = height + ITEM_TOP_MARGIN + ITEM_BOTTOM_MARGIN;
			}
			break;
		}
		return new Rectangle(x, y, width, height);
	}

	private void convolve(float[] kernel, int[] inPixels, int[] outPixels, int width, int height, boolean alpha) {
		int kernelWidth = kernel.length;
		int kernelMid = kernelWidth / 2;
		for (int y = 0; y < height; y++) {
			int index = y;
			int currentLine = y * width;
			for (int x = 0; x < width; x++) {
				// do point
				float a = 0, r = 0, g = 0, b = 0;
				for (int k = -kernelMid; k <= kernelMid; k++) {
					float val = kernel[k + kernelMid];
					int xcoord = x + k;
					if (xcoord < 0)
						xcoord = 0;
					if (xcoord >= width)
						xcoord = width - 1;
					int pixel = inPixels[currentLine + xcoord];
					// float alp = ((pixel >> 24) & 0xff);
					a += val * ((pixel >> 24) & 0xff);
					r += val * (((pixel >> 16) & 0xff));
					g += val * (((pixel >> 8) & 0xff));
					b += val * (((pixel) & 0xff));
				}
				int ia = alpha ? clamp((int) (a + 0.5)) : 0xff;
				int ir = clamp((int) (r + 0.5));
				int ig = clamp((int) (g + 0.5));
				int ib = clamp((int) (b + 0.5));
				outPixels[index] = (ia << 24) | (ir << 16) | (ig << 8) | ib;
				index += height;
			}
		}

	}

	private float[] create1DKernel(int radius, int sigma) {
		// guideline: 3*sigma should be the radius
		int size = radius * 2 + 1;
		float[] kernel = new float[size];
		int radiusSquare = radius * radius;
		float sigmaSquare = 2 * sigma * sigma;
		float piSigma = 2 * (float) Math.PI * sigma;
		float sqrtSigmaPi2 = (float) Math.sqrt(piSigma);
		int start = size / 2;
		int index = 0;
		float total = 0;
		for (int i = -start; i <= start; i++) {
			float d = i * i;
			if (d > radiusSquare) {
				kernel[index] = 0;
			} else {
				kernel[index] = (float) Math.exp(-(d) / sigmaSquare) / sqrtSigmaPi2;
			}
			total += kernel[index];
			index++;
		}
		for (int i = 0; i < size; i++) {
			kernel[i] /= total;
		}
		return kernel;
	}

	void createShadow(final Display display, boolean recreate) {
		if (shadowImage != null && !shadowImage.isDisposed()) {
			shadowImage.dispose();
			shadowImage = null;
		}

		ImageData data = new ImageData(60, 60, 32, new PaletteData(0xFF0000, 0xFF00, 0xFF));
		Image tmpImage = shadowImage = new Image(display, data);
		GC gc = new GC(tmpImage);
		if (shadowColor == null)
			shadowColor = gc.getDevice().getSystemColor(SWT.COLOR_GRAY);
		gc.setBackground(shadowColor);
		drawTabBody(gc, new Rectangle(0, 0, 60, 60), SWT.None);
		ImageData blured = blur(tmpImage, 5, 25);
		shadowImage = new Image(display, blured);
		display.setData(E4_SHADOW_IMAGE, shadowImage);
		tmpImage.dispose();
	}

	protected void dispose() {
		if (shadowImage != null && !shadowImage.isDisposed()) {
			shadowImage.dispose();
			shadowImage = null;
		}
		super.dispose();
	}

	protected void draw(int part, int state, Rectangle bounds, GC gc) {
		switch (part) {

		case PART_BODY:
			this.drawTabBody(gc, bounds, state);
			return;
		case PART_HEADER:
			this.drawTabHeader(gc, bounds, state);
			return;
		default:
			if (0 <= part && part < parent.getItemCount()) {
				if (bounds.width == 0 || bounds.height == 0)
					return;
				gc.setAdvanced(true);

				if ((state & SWT.SELECTED) != 0) {
					drawSelectedTabItemBackground(part, gc, bounds, state);
					state &= ~SWT.BACKGROUND;
					drawSelectedTabItem(part, gc, bounds, state);
				} else {
					drawUnselectedTabItemBackground(part, gc, bounds, state);

					if ((state & SWT.HOT) == 0 && !active) {
						state &= ~SWT.BACKGROUND;
						drawUnselectedTabItem(part, gc, bounds, state);
					} else {
						state &= ~SWT.BACKGROUND;
						drawUnselectedTabItem(part, gc, bounds, state);
					}
				}

				gc.setAdvanced(false);
				return;
			}
		}
		super.draw(part, state, bounds, gc);
	}

	protected void drawSelectedTabItem(int itemIndex, GC gc, Rectangle bounds, int state) {
		CTabItem item = parent.getItem(itemIndex);
		int x = bounds.x;
		int y = bounds.y;
		int height = bounds.height;
		int width = bounds.width;
		if (!parent.getSimple() && !parent.getSingle())
			width -= (getCurveWidth() - getCurveIndent());

		int rightEdge = Math.min(x + width, getRightItemEdge(parent, gc));
		// Draw selection border across all tabs

		// draw Image
		Rectangle trim = computeTrim(itemIndex, SWT.NONE, 0, 0, 0, 0);
		int xDraw = x - trim.x;
		if (parent.getSingle() && (hasStyle(parent, SWT.CLOSE) || hasStyle(item, SWT.CLOSE)))
			xDraw += getCloseRect(item).width;
		Image image = item.getImage();
		if (image != null && !image.isDisposed()) {
			Rectangle imageBounds = image.getBounds();
			// only draw image if it won't overlap with close button
			int maxImageWidth = rightEdge - xDraw - (trim.width + trim.x);
			if (!parent.getSingle() && getCloseRect(item).width > 0)
				maxImageWidth -= getCloseRect(item).width + INTERNAL_SPACING;
			if (imageBounds.width < maxImageWidth) {
				int imageX = xDraw;
				int imageY = y + (height - imageBounds.height) / 2;
				imageY += parent.getTabPosition() == SWT.BOTTOM ? -1 : 1;
				gc.drawImage(image, imageX, imageY);
				xDraw += imageBounds.width + INTERNAL_SPACING;
			}
		}

		// draw Text
		int textWidth = rightEdge - xDraw - (trim.width + trim.x);
		if (!parent.getSingle() && getCloseRect(item).width > 0)
			textWidth -= getCloseRect(item).width + INTERNAL_SPACING;
		if (textWidth > 0) {
			Font gcFont = gc.getFont();
			gc.setFont(item.getFont() == null ? parent.getFont() : item.getFont());

			if (getShortenText(item) == null || getShortenTextWidth(item) != textWidth) {
				setShortenText(item, _shortenText(gc, item.getText(), textWidth));
				setShortenTextWidth(item, textWidth);
			}
			Point extent = gc.textExtent(getShortenText(item), SWT.DRAW_TRANSPARENT | SWT.DRAW_MNEMONIC);
			int textY = y + (height - extent.y) / 2;
			textY += parent.getTabPosition() == SWT.BOTTOM ? -1 : 1;

			if (selectedTabItemColor != null) {
				gc.setForeground(selectedTabItemColor);
			} else {
				gc.setForeground(parent.getSelectionForeground());
			}

			gc.drawText(getShortenText(item), xDraw, textY, SWT.DRAW_TRANSPARENT | SWT.DRAW_MNEMONIC);
			gc.setFont(gcFont);

			// draw a Focus rectangle
			if (parent.isFocusControl()) {
				Display display = parent.getDisplay();
				if (parent.getSimple() || parent.getSingle()) {
					gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
					gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
					gc.drawFocus(xDraw - 1, textY - 1, extent.x + 2, extent.y + 2);
				} else {
					gc.setForeground(display.getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));
					gc.drawLine(xDraw, textY + extent.y + 1, xDraw + extent.x + 1, textY + extent.y + 1);
				}
			}
		}
		if (hasStyle(parent, SWT.CLOSE) || hasStyle(item, SWT.CLOSE))
			_drawClose(gc, getCloseRect(item), getCloseImageState(item));
	}

	protected void drawSelectedTabItemBackground(int itemIndex, GC gc, Rectangle bounds, int state) {
		if (parent.getSingle() && parent.getItem(itemIndex).isShowing())
			return;

		int width = bounds.width;
		int[] points = new int[1024];
		int index = 0;
		int radius = cornerSize / 2;
		int circX = bounds.x + radius;
		int circY = bounds.y - 1 + radius;
		int selectionX1, selectionY1, selectionX2, selectionY2;
		if ((itemIndex == 0 || bounds.x <= 5) && bounds.x == -computeTrim(CTabFolderRenderer.PART_HEADER, SWT.NONE, 0, 0, 0, 0).x) {
			circX -= 1;
			points[index++] = circX - radius;
			points[index++] = bounds.y + bounds.height;

			points[index++] = selectionX1 = circX - radius;
			points[index++] = selectionY1 = bounds.y + bounds.height;
		} else {
			if (active) {
				points[index++] = shadowEnabled ? SIDE_DROP_WIDTH : 0 + INNER_KEYLINE + OUTER_KEYLINE;
				points[index++] = bounds.y + bounds.height;
			}
			points[index++] = selectionX1 = bounds.x;
			points[index++] = selectionY1 = bounds.y + bounds.height;
		}
		int[] ltt = drawCircle(circX, circY, radius, LEFT_TOP);
		int startX = ltt[6];
		for (int i = 0; i < ltt.length / 2; i += 2) {
			int tmp = ltt[i];
			ltt[i] = ltt[ltt.length - i - 2];
			ltt[ltt.length - i - 2] = tmp;
			tmp = ltt[i + 1];
			ltt[i + 1] = ltt[ltt.length - i - 1];
			ltt[ltt.length - i - 1] = tmp;
		}
		System.arraycopy(ltt, 0, points, index, ltt.length);
		index += ltt.length;

		int[] rt = drawCircle(circX + width - (radius * 2), circY, radius, RIGHT_TOP);
		int endX = rt[rt.length - 4];
		for (int i = 0; i < rt.length / 2; i += 2) {
			int tmp = rt[i];
			rt[i] = rt[rt.length - i - 2];
			rt[rt.length - i - 2] = tmp;
			tmp = rt[i + 1];
			rt[i + 1] = rt[rt.length - i - 1];
			rt[rt.length - i - 1] = tmp;
		}
		System.arraycopy(rt, 0, points, index, rt.length);
		index += rt.length;

		points[index++] = selectionX2 = bounds.width + circX - radius;
		points[index++] = selectionY2 = bounds.y + bounds.height;

		if (active) {
			points[index++] = parent.getSize().x - (shadowEnabled ? SIDE_DROP_WIDTH : 0 + INNER_KEYLINE + OUTER_KEYLINE);
			points[index++] = bounds.y + bounds.height;
		}
		gc.setClipping(0, bounds.y, parent.getSize().x - (shadowEnabled ? SIDE_DROP_WIDTH : 0 + INNER_KEYLINE + OUTER_KEYLINE), bounds.y + bounds.height);// bounds.height
																																							// +
																																							// 4);
		if (selectedTabFillColor == null)
			selectedTabFillColor = gc.getDevice().getSystemColor(SWT.COLOR_WHITE);
		gc.setBackground(selectedTabFillColor);
		gc.setForeground(selectedTabFillColor);
		Pattern backgroundPattern = null;

		if (selectedTabFillHighlightColor != null) {
			backgroundPattern = new Pattern(gc.getDevice(), 0, 0, 0, bounds.height + 1, selectedTabFillHighlightColor, selectedTabFillColor);
			gc.setBackgroundPattern(backgroundPattern);
		}

		int[] tmpPoints = new int[index];
		System.arraycopy(points, 0, tmpPoints, 0, index);

		if (Platform.getOS().equals(Platform.OS_LINUX)) {
			gc.fillPolygon(tmpPoints);
		} else {
			gc.fillPolygon(translate(tmpPoints, 1, 1));
		}

		gc.drawLine(selectionX1, selectionY1, selectionX2, selectionY2);
		if (tabOutlineColor == null)
			tabOutlineColor = gc.getDevice().getSystemColor(SWT.COLOR_BLACK);
		gc.setForeground(tabOutlineColor);
		Color gradientLineTop = null;
		Pattern foregroundPattern = null;
		if (!active) {
			RGB blendColor = gc.getDevice().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW).getRGB();
			RGB topGradient = blend(blendColor, tabOutlineColor.getRGB(), 40);
			gradientLineTop = new Color(gc.getDevice(), topGradient);
			foregroundPattern = new Pattern(gc.getDevice(), 0, 0, 0, bounds.height + 1, gradientLineTop, gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
			gc.setForegroundPattern(foregroundPattern);
		}
		gc.drawPolyline(tmpPoints);
		Rectangle rect = null;
		gc.setClipping(rect);

		if (active) {
			if (outerKeyline == null)
				outerKeyline = gc.getDevice().getSystemColor(SWT.COLOR_RED);
			gc.setForeground(outerKeyline);
			gc.drawPolyline(shape);
		} else {
			gc.drawLine(startX, 0, endX, 0);
		}

		if (backgroundPattern != null)
			backgroundPattern.dispose();
		if (gradientLineTop != null)
			gradientLineTop.dispose();
		if (foregroundPattern != null)
			foregroundPattern.dispose();
	}

	void drawShadow(final Display display, Rectangle bounds, GC gc) {
		if (shadowImage == null) {
			createShadow(display, true);
		}

		int x = bounds.x;
		int y = bounds.y;
		int SIZE = shadowImage.getBounds().width / 3;

		int height = Math.max(bounds.height, SIZE * 2);
		int width = Math.max(bounds.width, SIZE * 2);
		// top left
		gc.drawImage(shadowImage, 0, 0, SIZE, SIZE, 2, 10, SIZE, 20);
		int fillHeight = height - SIZE * 2;
		int fillWidth = width + 5 - SIZE * 2;

		int xFill = 0;
		for (int i = SIZE; i < fillHeight; i += SIZE) {
			xFill = i;
			gc.drawImage(shadowImage, 0, SIZE, SIZE, SIZE, 2, i, SIZE, SIZE);
		}

		// Pad the rest of the shadow
		gc.drawImage(shadowImage, 0, SIZE, SIZE, fillHeight - xFill, 2, xFill + SIZE, SIZE, fillHeight - xFill);

		// bl
		gc.drawImage(shadowImage, 0, 40, 20, 20, 2, y + height - SIZE, 20, 20);

		int yFill = 0;
		for (int i = SIZE; i <= fillWidth; i += SIZE) {
			yFill = i;
			gc.drawImage(shadowImage, SIZE, SIZE * 2, SIZE, SIZE, i, y + height - SIZE, SIZE, SIZE);
		}
		// Pad the rest of the shadow
		gc.drawImage(shadowImage, SIZE, SIZE * 2, fillWidth - yFill, SIZE, yFill + SIZE, y + height - SIZE, fillWidth - yFill, SIZE);

		// br
		gc.drawImage(shadowImage, SIZE * 2, SIZE * 2, SIZE, SIZE, x + width - SIZE - 1, y + height - SIZE, SIZE, SIZE);

		// tr
		gc.drawImage(shadowImage, (SIZE * 2), 0, SIZE, SIZE, x + width - SIZE - 1, 10, SIZE, SIZE);

		xFill = 0;
		for (int i = SIZE; i < fillHeight; i += SIZE) {
			xFill = i;
			gc.drawImage(shadowImage, SIZE * 2, SIZE, SIZE, SIZE, x + width - SIZE - 1, i, SIZE, SIZE);
		}

		// Pad the rest of the shadow
		gc.drawImage(shadowImage, SIZE * 2, SIZE, SIZE, fillHeight - xFill, x + width - SIZE - 1, xFill + SIZE, SIZE, fillHeight - xFill);
	}

	void drawTabBody(GC gc, Rectangle bounds, int state) {
		int[] points = new int[1024];
		int index = 0;
		int radius = cornerSize / 2;
		int marginWidth = parent.marginWidth;
		int marginHeight = parent.marginHeight;
		int delta = INNER_KEYLINE + OUTER_KEYLINE + 2 * (shadowEnabled ? SIDE_DROP_WIDTH : 0) + 2 * marginWidth;
		int width = bounds.width - delta;
		int height = Math.max(parent.getTabHeight() + INNER_KEYLINE + OUTER_KEYLINE + (shadowEnabled ? BOTTOM_DROP_WIDTH : 0), bounds.height - INNER_KEYLINE
				- OUTER_KEYLINE - 2 * marginHeight - (shadowEnabled ? BOTTOM_DROP_WIDTH : 0));

		int circX = bounds.x + delta / 2 + radius;
		int circY = bounds.y + radius;

		// Body
		index = 0;
		int[] ltt = drawCircle(circX, circY, radius, LEFT_TOP);
		System.arraycopy(ltt, 0, points, index, ltt.length);
		index += ltt.length;

		int[] lbb = drawCircle(circX, circY + height - (radius * 2), radius, LEFT_BOTTOM);
		System.arraycopy(lbb, 0, points, index, lbb.length);
		index += lbb.length;

		int[] rb = drawCircle(circX + width - (radius * 2), circY + height - (radius * 2), radius, RIGHT_BOTTOM);
		System.arraycopy(rb, 0, points, index, rb.length);
		index += rb.length;

		int[] rt = drawCircle(circX + width - (radius * 2), circY, radius, RIGHT_TOP);
		System.arraycopy(rt, 0, points, index, rt.length);
		index += rt.length;
		points[index++] = circX;
		points[index++] = circY - radius;

		int[] tempPoints = new int[index];
		System.arraycopy(points, 0, tempPoints, 0, index);
		gc.fillPolygon(tempPoints);

		// Fill in parent background for non-rectangular shape
		Region r = new Region();
		r.add(bounds);
		r.subtract(tempPoints);
		gc.setBackground(parent.getParent().getBackground());
		Display display = parent.getDisplay();
		Region clipping = new Region();
		gc.getClipping(clipping);
		r.intersect(clipping);
		gc.setClipping(r);
		Rectangle mappedBounds = display.map(parent, parent.getParent(), bounds);

		parent.getParent().drawBackground(gc, bounds.x, bounds.y, bounds.width, bounds.height, mappedBounds.x, mappedBounds.y);

		// Shadow
		if (shadowEnabled)
			drawShadow(display, bounds, gc);

		gc.setClipping(clipping);
		clipping.dispose();
		r.dispose();

		// Remember for use in header drawing
		shape = tempPoints;
	}

	void drawTabHeader(GC gc, Rectangle bounds, int state) {
		int[] points = new int[1024];
		int index = 0;
		int radius = cornerSize / 2;
		int marginWidth = parent.marginWidth;
		int marginHeight = parent.marginHeight;
		int delta = INNER_KEYLINE + OUTER_KEYLINE + 2 * (shadowEnabled ? SIDE_DROP_WIDTH : 0) + 2 * marginWidth;
		int width = bounds.width - delta;
		int height = bounds.height - INNER_KEYLINE - OUTER_KEYLINE - 2 * marginHeight - (shadowEnabled ? BOTTOM_DROP_WIDTH : 0);
		int circX = bounds.x + delta / 2 + radius;
		int circY = bounds.y + radius;

		// Fill in background
		Region clipping = new Region();
		gc.getClipping(clipping);
		Region region = new Region();
		region.add(shape);
		region.intersect(clipping);
		gc.setClipping(region);

		int header = 3; // TODO: this needs to be added to computeTrim for
						// HEADER
		Rectangle trim = computeTrim(PART_HEADER, state, 0, 0, 0, 0);
		trim.width = bounds.width - trim.width;
		trim.height = (parent.getTabHeight() + 1 + header) - trim.height;
		trim.x = -trim.x;
		trim.y = -trim.y;

		draw(PART_BACKGROUND, SWT.NONE, trim, gc);

		gc.setClipping(clipping);
		clipping.dispose();
		region.dispose();

		int[] ltt = drawCircle(circX + 1, circY + 1, radius, LEFT_TOP);
		System.arraycopy(ltt, 0, points, index, ltt.length);
		index += ltt.length;

		int[] lbb = drawCircle(circX + 1, circY + height - (radius * 2) - 2, radius, LEFT_BOTTOM);
		System.arraycopy(lbb, 0, points, index, lbb.length);
		index += lbb.length;

		int[] rb = drawCircle(circX + width - (radius * 2) - 2, circY + height - (radius * 2) - 2, radius, RIGHT_BOTTOM);
		System.arraycopy(rb, 0, points, index, rb.length);
		index += rb.length;

		int[] rt = drawCircle(circX + width - (radius * 2) - 2, circY + 1, radius, RIGHT_TOP);
		System.arraycopy(rt, 0, points, index, rt.length);
		index += rt.length;
		points[index++] = points[0];
		points[index++] = points[1];

		int[] tempPoints = new int[index];
		System.arraycopy(points, 0, tempPoints, 0, index);

		if (outerKeyline == null)
			outerKeyline = gc.getDevice().getSystemColor(SWT.COLOR_BLACK);
		gc.setForeground(outerKeyline);
		gc.drawPolyline(shape);
	}

	protected void drawUnselectedTabItem(int index, GC gc, Rectangle bounds, int state) {
		try {
			CTabItem item = parent.getItem(index);
			int x = bounds.x;
			int y = bounds.y;
			int height = bounds.height;
			int width = bounds.width;

			// Do not draw partial items
			if (!item.isShowing())
				return;

			Rectangle clipping = gc.getClipping();
			if (!clipping.intersects(bounds))
				return;

			if ((state & SWT.FOREGROUND) != 0) {
				// draw Image
				Rectangle trim = computeTrim(index, SWT.NONE, 0, 0, 0, 0);
				int xDraw = x - trim.x;
				Image image = item.getImage();
				if (image != null && !image.isDisposed() && parent.getUnselectedImageVisible()) {
					Rectangle imageBounds = image.getBounds();
					// only draw image if it won't overlap with close button
					int maxImageWidth = x + width - xDraw - (trim.width + trim.x);
					if (parent.getUnselectedCloseVisible() && ((parent.getStyle() & SWT.CLOSE) != 0 || item.getShowClose())) {
						maxImageWidth -= ((Rectangle) HACK_CTabItem_closeRect.get(item)).width + 4;
					}
					if (imageBounds.width < maxImageWidth) {
						int imageX = xDraw;
						int imageHeight = imageBounds.height;
						int imageY = y + (height - imageHeight) / 2;
						imageY += parent.getTabPosition() == SWT.BOTTOM ? -1 : 1;
						int imageWidth = imageBounds.width * imageHeight / imageBounds.height;
						gc.drawImage(image, imageBounds.x, imageBounds.y, imageBounds.width, imageBounds.height, imageX, imageY, imageWidth, imageHeight);
						xDraw += imageWidth + 4;
					}
				}

				// draw Text
				int textWidth = x + width - xDraw - (trim.width + trim.x);
				if (parent.getUnselectedCloseVisible() && ((parent.getStyle() & SWT.CLOSE) != 0 || item.getShowClose())) {
					textWidth -= ((Rectangle) HACK_CTabItem_closeRect.get(item)).width + 4;
				}
				if (textWidth > 0) {
					Font gcFont = gc.getFont();
					gc.setFont(item.getFont() == null ? parent.getFont() : item.getFont());
					if (HACK_CTabItem_shortenText.get(item) == null || HACK_CTabItem_shortenTextWidth.getInt(item) != textWidth) {
						HACK_CTabItem_shortenText.set(item, _shortenText(gc, item.getText(), textWidth));
						HACK_CTabItem_shortenTextWidth.setInt(item, textWidth);
					}
					Point extent = gc.textExtent((String) HACK_CTabItem_shortenText.get(item), SWT.DRAW_TRANSPARENT | SWT.DRAW_MNEMONIC);
					int textY = y + (height - extent.y) / 2;
					textY += parent.getTabPosition() == SWT.BOTTOM ? -1 : 1;

					if (showUnselectedTabItemShadow()) {
						gc.setAlpha(180);
						gc.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
						gc.drawText((String) HACK_CTabItem_shortenText.get(item), xDraw, textY + 1, SWT.DRAW_TRANSPARENT | SWT.DRAW_MNEMONIC);
					}

					gc.setAlpha(255);

					if (unselectedTabItemColor != null) {
						gc.setForeground(unselectedTabItemColor);
					} else {
						gc.setForeground(parent.getForeground());
					}
					gc.drawText((String) HACK_CTabItem_shortenText.get(item), xDraw, textY, SWT.DRAW_TRANSPARENT | SWT.DRAW_MNEMONIC);
					gc.setFont(gcFont);
				}

				if ((state & SWT.HOT) != 0 && parent.getUnselectedCloseVisible() && (hasStyle(parent, SWT.CLOSE) || hasStyle(item, SWT.CLOSE)))
					_drawClose(gc, getCloseRect(item), getCloseImageState(item));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void drawUnselectedTabItemBackground(int itemIndex, GC gc, Rectangle bounds, int state) {
		if ((state & SWT.HOT) != 0) {
			int width = bounds.width;
			int[] points = new int[1024];
			int[] inactive = new int[8];
			int index = 0, inactive_index = 0;
			int radius = cornerSize / 2;
			int circX = bounds.x + radius;
			int circY = bounds.y - 1 + radius;

			int leftIndex = circX;
			if (itemIndex == 0 || bounds.x <= 5) {
				if (parent.getSelectionIndex() != 0)
					leftIndex -= 1;
				points[index++] = leftIndex - radius;
				points[index++] = bounds.y + bounds.height;
			} else {
				points[index++] = bounds.x;
				points[index++] = bounds.y + bounds.height;
			}

			if (!active) {
				System.arraycopy(points, 0, inactive, 0, index);
				inactive_index += 2;
			}

			int[] ltt = drawCircle(leftIndex, circY, radius, LEFT_TOP);
			for (int i = 0; i < ltt.length / 2; i += 2) {
				int tmp = ltt[i];
				ltt[i] = ltt[ltt.length - i - 2];
				ltt[ltt.length - i - 2] = tmp;
				tmp = ltt[i + 1];
				ltt[i + 1] = ltt[ltt.length - i - 1];
				ltt[ltt.length - i - 1] = tmp;
			}
			System.arraycopy(ltt, 0, points, index, ltt.length);
			index += ltt.length;

			if (!active) {
				System.arraycopy(ltt, 0, inactive, inactive_index, 2);
				inactive_index += 2;
			}

			int rightIndex = circX - 1;
			int[] rt = drawCircle(rightIndex + width - (radius * 2), circY, radius, RIGHT_TOP);
			for (int i = 0; i < rt.length / 2; i += 2) {
				int tmp = rt[i];
				rt[i] = rt[rt.length - i - 2];
				rt[rt.length - i - 2] = tmp;
				tmp = rt[i + 1];
				rt[i + 1] = rt[rt.length - i - 1];
				rt[rt.length - i - 1] = tmp;
			}
			System.arraycopy(rt, 0, points, index, rt.length);
			index += rt.length;
			if (!active) {
				System.arraycopy(rt, rt.length - 4, inactive, inactive_index, 2);
				inactive[inactive_index] -= 1;
				inactive_index += 2;
			}

			points[index++] = bounds.width + rightIndex - radius;
			points[index++] = bounds.y + bounds.height;

			if (!active) {
				System.arraycopy(points, index - 2, inactive, inactive_index, 2);
				inactive[inactive_index] -= 1;
				inactive_index += 2;
			}

			gc.setClipping(points[0], bounds.y, parent.getSize().x - (shadowEnabled ? SIDE_DROP_WIDTH : 0 + INNER_KEYLINE + OUTER_KEYLINE), bounds.y
					+ bounds.height);

			gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
			int[] tmpPoints = new int[index];
			System.arraycopy(points, 0, tmpPoints, 0, index);

			gc.setAlpha(120);

			if (Platform.getOS().equals(Platform.OS_LINUX)) {
				gc.fillPolygon(tmpPoints);
			} else {
				gc.fillPolygon(translate(tmpPoints, 1, 1));
			}

			Color tempBorder = new Color(gc.getDevice(), 182, 188, 204);
			gc.setForeground(tempBorder);
			gc.drawPolygon(tmpPoints);
			tempBorder.dispose();
			gc.setAlpha(255);
		}
	}

	private Integer getCloseImageState(CTabItem item) {
		try {
			return (Integer) HACK_CTabItem_closeImageState.get(item);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private Rectangle getCloseRect(CTabItem item) {
		try {
			return (Rectangle) HACK_CTabItem_closeRect.get(item);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private Integer getCurveIndent() {
		try {
			return (Integer) HACK_CTabFolder_curveIndent.get(this);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private Integer getCurveWidth() {
		try {
			return (Integer) HACK_CTabFolder_curveWidth.get(this);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Rectangle getPadding() {
		return new Rectangle(paddingTop, paddingRight, paddingBottom, paddingLeft);
	}

	private Integer getRightItemEdge(CTabFolder folder, GC gc) {
		try {
			return (Integer) HACK_CTabFolder_getRightItemEdge.invoke(folder, gc);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Color getSelectedTabFillHighlightColor() {
		return selectedTabFillHighlightColor;
	}

	public Color getSelectedTabItemColor() {
		return selectedTabItemColor;
	}

	private String getShortenText(CTabItem item) {
		try {
			return (String) HACK_CTabItem_shortenText.get(item);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private Integer getShortenTextWidth(CTabItem item) {
		try {
			return (Integer) HACK_CTabItem_shortenTextWidth.get(item);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Color getUnselectedTabItemColor() {
		return unselectedTabItemColor;
	}

	private boolean hasStyle(Widget w, int flag) {
		return (w.getStyle() & flag) != 0;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setActiveToolbarGradient(Color[] color, int[] percents) {
		activeToolbar = color;
		activePercents = percents;
	}

	public void setCornerRadius(int radius) {
		cornerSize = radius;
		parent.redraw();
	}

	public int getCornerRadius() {
		return cornerSize;
	}

	public void setInactiveToolbarGradient(Color[] color, int[] percents) {
		inactiveToolbar = color;
		inactivePercents = percents;
	}

	public void setInnerKeyline(Color color) {
		this.innerKeyline = color;
		parent.redraw();
	}

	public void setOuterKeyline(Color color) {
		this.outerKeyline = color;
		// TODO: HACK! Should be set based on pseudo-state.
		setActive(!(color.getRed() == 255 && color.getGreen() == 255 && color.getBlue() == 255));
		parent.redraw();
	}

	public void setPadding(int paddingLeft, int paddingRight, int paddingTop, int paddingBottom) {
		this.paddingLeft = paddingLeft;
		this.paddingRight = paddingRight;
		this.paddingTop = paddingTop;
		this.paddingBottom = paddingBottom;
		parent.redraw();
	}

	public void setSelectedTabFill(Color color) {
		this.selectedTabFillColor = color;
		parent.redraw();
	}

	public void setSelectedTabFillHighlightColor(Color selectedTabFillHighlightColor) {
		this.selectedTabFillHighlightColor = selectedTabFillHighlightColor;
	}

	public void setSelectedTabItemColor(Color selectedTabItemColor) {
		this.selectedTabItemColor = selectedTabItemColor;
	}

	public void setShadowColor(Color color) {
		this.shadowColor = color;
		createShadow(parent.getDisplay(), true);
		parent.redraw();
	}

	public void setShadowVisible(boolean visible) {
		this.shadowEnabled = visible;
		parent.redraw();
	}

	private void setShortenText(CTabItem item, String shortenText) {
		try {
			HACK_CTabItem_shortenText.set(item, shortenText);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setShortenTextWidth(CTabItem item, int width) {
		try {
			HACK_CTabItem_shortenTextWidth.set(item, width);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setTabOutline(Color color) {
		this.tabOutlineColor = color;
		parent.redraw();
	}

	public void setUnselectedTabItemColor(Color unselectedTabItemColor) {
		this.unselectedTabItemColor = unselectedTabItemColor;
	}

	protected boolean showUnselectedTabItemShadow() {
		return true;
	}

	private int[] translate(int[] pointArray, int dx, int dy) {
		int[] result = new int[pointArray.length];
		System.arraycopy(pointArray, 0, result, 0, pointArray.length);
		for (int i = 0; i < result.length; i += 2) {
			result[i] += dx;
			result[i + 1] += dy;
		}
		return result;
	}

	protected void updateItems() {
		try {
			HACK_CTabFolder_updateItems.invoke(parent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
