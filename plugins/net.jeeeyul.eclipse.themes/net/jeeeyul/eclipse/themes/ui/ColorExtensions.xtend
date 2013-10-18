package net.jeeeyul.eclipse.themes.ui

import org.eclipse.swt.graphics.RGB

class ColorExtensions {
	def ampSaturation(RGB rgb, float amp) { 
		var hsb = rgb.HSB
		return new RGB(hsb.get(0), (hsb.get(1) * amp).limit(0f, 1f), hsb.get(2))
	}
	
	def float limit(float original, float min, float max){
		var result = Math::min(Math::max(original, min), max)
		return result
	}
	
	def ampBrightness(RGB rgb, float amp) { 
		var hsb = rgb.HSB
		return new RGB(hsb.get(0),hsb.get(1) ,(hsb.get(2) * amp).limit(0f, 1f))
	}
	
	def RGB rewriteHue(RGB original, float hue){
		var hsb = original.HSB
		new RGB(hue, hsb.get(1), hsb.get(2))
	}
	
	def float getHue(RGB rgb) {
		return rgb.HSB.get(0)
	}
	
	def float getSaturation(RGB rgb) {
		return rgb.HSB.get(1)
	}
	
	def float getBrightness(RGB rgb) {
		return rgb.HSB.get(2)
	}
}