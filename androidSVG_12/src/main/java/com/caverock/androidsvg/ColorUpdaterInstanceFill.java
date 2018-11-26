package com.caverock.androidsvg;

import com.caverock.androidsvg.SVG.Colour;
import com.caverock.androidsvg.SVG.Style;
import com.caverock.androidsvg.SVG.SvgPaint;

public class ColorUpdaterInstanceFill extends ColorUpdater {

	private int toColor;

	public ColorUpdaterInstanceFill(int toColor) {
		super();
		this.toColor = toColor;
	}

	@Override
	public SvgPaint update(SvgPaint svgPaint) {
		if (svgPaint instanceof Colour) {
			Colour colour = (Colour) svgPaint;
			colour.colour = toColor;
		}
		return svgPaint;
	}

}
