package net.jeeeyul.eclipse.themes.rendering

import java.util.HashMap
import java.util.Map
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.custom.CTabFolderRenderer
import org.eclipse.swt.graphics.GC
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.graphics.Rectangle
import org.eclipse.swt.widgets.Control

class KTabRenderer extends CTabFolderRenderer {
	extension KTabRendererHelper = new KTabRendererHelper
	CTabFolder parent
	Map<Integer, Point> sizeCache = new HashMap<Integer, Point>
	int padding = 2
	int margin = 5

	new(CTabFolder parent) {
		super(parent)
		this.parent = parent
		parent.selectionForeground = parent.display.getSystemColor(SWT::COLOR_WHITE)
		updateBackgroundImages()
	}

	override protected draw(int part, int state, Rectangle bounds, GC gc) {
		updateBackgroundImages()
		println(part)
		switch(part) {
			case PART_BODY: {
				
			}
			case PART_HEADER: {
			}
			case PART_BACKGROUND: {
			}
			case PART_BORDER: {
			}
			case part >= 0: {
				gc.drawOval(bounds.x, bounds.y, bounds.width, bounds.height)
				gc.drawLine(margin, margin + sizeCache.get(PART_HEADER).y, parent.size.x-margin , margin + sizeCache.get(PART_HEADER).y);
				
				super.draw(part, state.removeFlag(SWT::BACKGROUND), bounds, gc)
			}
		default:{
				super.draw(part, state, bounds, gc)
			}
		}
	}

	override protected computeSize(int part, int state, GC gc, int wHint, int hHint) {
		var result = switch(part) {
			case PART_BODY: {
				super.computeSize(part, state, gc, wHint, hHint)
			}
		default:{
				super.computeSize(part, state, gc, wHint, hHint)
			}
		}
		sizeCache.put(part, result)
		return result
	}

	override protected computeTrim(int part, int state, int x, int y, int width, int height) {
		var result = switch(part) {
			case PART_BODY: {
				new Rectangle(x - padding - margin,
					y - sizeCache.get(PART_HEADER).y - padding - margin, 
					width + padding * 2 + margin * 2,
					height + padding * 2 + sizeCache.get(PART_HEADER).y + margin * 2
				)
			}
			case PART_HEADER: {
				new Rectangle(x - margin, y, width + margin * 2, height)
			}
			case PART_BORDER: {
				new Rectangle(x - margin, y - margin, width + margin * 2, height + margin * 2)
			}
			
			case part > 0:{
				new Rectangle(x , y - 6, width, height + 12)
			}
		default:{
				super.computeTrim(part, state, x, y, width, height)
			}
		}
		return result
	}

	def void updateBackgroundImages() {
		var updateTarget = parent.children.filter(typeof(Control)).filter[
			val eachToolbar = it
			!parent.items.exists[ it.control == eachToolbar ]
		]

		for(each : updateTarget){
			each.backgroundImage = parent.backgroundImage 
		}
	}

	def fillLines(GC gc, Rectangle area, int start){
		var oldClip = gc.clipping
		gc.setClipping(area)
		var x = area.x + start
		while(x < area.x + area.width + area.height){
			gc.drawLine(x, area.y, area.x, area.y + x)
			x = x + 10
		}
		gc.setClipping(oldClip)
	}

	def Rectangle expand(Rectangle rect, int dx, int dy){
		new Rectangle(rect.x, rect.y, rect.width + dx, rect.height + dy)
	}

	def Rectangle translate(Rectangle rect, int dx, int dy){
		new Rectangle(rect.x + dx, rect.y + dy, rect.width, rect.height)
	}
}