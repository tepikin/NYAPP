package com.caverock.androidsvg;

import com.caverock.androidsvg.SVG.Colour;
import com.caverock.androidsvg.SVG.Style;
import com.caverock.androidsvg.SVG.SvgPaint;

public abstract class ColorUpdater implements StyleUpdater {
	public abstract SvgPaint update(SvgPaint svgPaint);

	public void updateStyle(Style style) {
		if (style == null) {
			return;
		}
		style.fill = update(style.fill);
		style.color = (Colour) update(style.color);
		style.stopColor = update(style.stopColor);
		style.stroke = update(style.stroke);
		style.solidColor = update(style.solidColor);
		style.viewportFill = update(style.viewportFill);
	}
}
