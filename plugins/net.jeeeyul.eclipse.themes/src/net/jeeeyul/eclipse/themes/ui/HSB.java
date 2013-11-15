package net.jeeeyul.eclipse.themes.ui;

import java.util.Locale;
import java.util.Scanner;

import org.eclipse.swt.graphics.RGB;

public class HSB {
	public static HSB deserialize(String literal) {
		HSB result = new HSB();

		try {
			Scanner scanner = new Scanner(literal);
			scanner.useDelimiter("\\|");
			scanner.useLocale(Locale.US);

			result.hue = scanner.nextFloat();
			result.saturation = scanner.nextFloat();
			result.brightness = scanner.nextFloat();
		} catch (Exception e) {
			// FIXME report to user.
			result = new HSB(0f, 1f, 1f);
		}
		return result;
	}

	public static void main(String[] args) {
		HSB hsb = new HSB(0, 1f, 1f);
		System.out.println(hsb.toHTMLCode());
	}

	public float hue;
	public float saturation;

	public float brightness;

	public HSB() {

	}

	public HSB(float hue, float saturation, float brightness) {
		this.hue = hue;
		this.saturation = saturation;
		this.brightness = brightness;
	}

	public HSB(float[] hsb) {
		this(hsb[0], hsb[1], hsb[2]);
	}

	public HSB(int red, int green, int blue) {
		this(new RGB(red, green, blue).getHSB());
	}

	public HSB(RGB rgb) {
		this(rgb.getHSB());
	}

	public HSB(String htmlCode) {
		if (htmlCode.startsWith("#")) {
			htmlCode = htmlCode.substring(1);
		}

		RGB rgb = new RGB(0, 0, 0);

		if (htmlCode.matches("[0-9a-fA-F]{6}")) {
			rgb.red = Integer.parseInt(htmlCode.substring(0, 2), 16);
			rgb.green = Integer.parseInt(htmlCode.substring(2, 4), 16);
			rgb.blue = Integer.parseInt(htmlCode.substring(4, 6), 16);
		} else if (htmlCode.matches("[0-9a-fA-F]{3}")) {
			rgb.red = Integer.parseInt(htmlCode.substring(0, 1) + htmlCode.substring(0, 1), 16);
			rgb.green = Integer.parseInt(htmlCode.substring(1, 2) + htmlCode.substring(1, 2), 16);
			rgb.blue = Integer.parseInt(htmlCode.substring(2, 3) + htmlCode.substring(2, 3), 16);
		}

		else {
			throw new UnsupportedOperationException(htmlCode + " is not supported color code.");
		}

		float[] hsb = rgb.getHSB();
		this.hue = hsb[0];
		this.saturation = hsb[1];
		this.brightness = hsb[2];
	}

	public HSB ampBrightness(float amp) {
		return new HSB(hue, saturation, limit(this.brightness * amp, 0f, 1f));
	}

	public HSB ampSaturation(float amp) {
		return new HSB(hue, limit(this.saturation * amp, 0f, 1f), brightness);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof HSB) {
			HSB other = (HSB) obj;
			return this.hue == other.hue && this.saturation == other.saturation && this.brightness == other.brightness;
		}
		return super.equals(obj);
	}

	public HSB getCopy() {
		return new HSB(hue, saturation, brightness);
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	private float limit(float original, float min, float max) {
		return Math.min(Math.max(original, min), max);
	}

	public HSB mixWith(HSB color, float strength) {
		RGB thisRGB = toRGB();
		RGB otherRGB = color.toRGB();
		RGB newRGB = new RGB(0, 0, 0);

		newRGB.red = (int) (thisRGB.red * (1f - strength) + otherRGB.red * strength);
		newRGB.green = (int) (thisRGB.green * (1f - strength) + otherRGB.green * strength);
		newRGB.blue = (int) (thisRGB.blue * (1f - strength) + otherRGB.blue * strength);

		float[] hsb = newRGB.getHSB();

		this.hue = hsb[0];
		this.saturation = hsb[1];
		this.brightness = hsb[2];

		return this;
	}

	public HSB rewriteHue(float newHue) {
		return new HSB(newHue, saturation, brightness);
	}

	public String serialize() {
		return String.format(Locale.ENGLISH, "%f|%f|%f", hue, saturation, brightness);
	}

	public float[] toArray() {
		return new float[] { hue, saturation, brightness };
	}

	public String toHTMLCode() {
		RGB rgb = toRGB();
		return String.format("#%02x%02x%02x", rgb.red, rgb.green, rgb.blue);
	}

	public RGB toRGB() {
		return new RGB(hue, saturation, brightness);
	}

	@Override
	public String toString() {
		return String.format("%f|%f|%f", hue, saturation, brightness);
	}
}
