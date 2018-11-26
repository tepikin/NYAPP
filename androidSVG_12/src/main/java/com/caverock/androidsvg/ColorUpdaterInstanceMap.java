package com.caverock.androidsvg;

import com.caverock.androidsvg.SVG.Colour;
import com.caverock.androidsvg.SVG.SvgPaint;

public class ColorUpdaterInstanceMap extends ColorUpdater {
	private int fromColor;
	private int toColor;

	public ColorUpdaterInstanceMap(int fromColor, int toColor) {
		super();
		this.fromColor = fromColor;
		this.toColor = toColor;
	}

	@Override
	public SvgPaint update(SvgPaint svgPaint) {
		if (svgPaint instanceof Colour) {
			Colour colour = (Colour) svgPaint;
			if (colour.colour == fromColor) {
				colour.colour = toColor;
			}
		}
		return svgPaint;
	}

}
