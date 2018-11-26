package com.caverock.androidsvg;

import com.caverock.androidsvg.SVG.Colour;
import com.caverock.androidsvg.SVG.Style;
import com.caverock.androidsvg.SVG.SvgPaint;

public class ColorUpdaterInstanceType implements StyleUpdater {
	private int fill = -1;
	private int color = -1;
	private int stopColor = -1;
	private int stroke = -1;
	private int solidColor = -1;
	private int viewportFill = -1;

	public ColorUpdaterInstanceType() {
		super();
	}

	public ColorUpdaterInstanceType(int fill, int color, int stopColor,
			int stroke, int solidColor, int viewportFill) {
		super();
		this.fill = fill;
		this.color = color;
		this.stopColor = stopColor;
		this.stroke = stroke;
		this.solidColor = solidColor;
		this.viewportFill = viewportFill;
	}

	public int getFill() {
		return fill;
	}

	public void setFill(int fill) {
		this.fill = fill;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getStopColor() {
		return stopColor;
	}

	public void setStopColor(int stopColor) {
		this.stopColor = stopColor;
	}

	public int getStroke() {
		return stroke;
	}

	public void setStroke(int stroke) {
		this.stroke = stroke;
	}

	public int getSolidColor() {
		return solidColor;
	}

	public void setSolidColor(int solidColor) {
		this.solidColor = solidColor;
	}

	public int getViewportFill() {
		return viewportFill;
	}

	public void setViewportFill(int viewportFill) {
		this.viewportFill = viewportFill;
	}

	public void updateStyle(Style style) {
		if (style == null) {
			return;
		}
		style.fill = update(style.fill, fill);
		style.color = (Colour) update(style.color, color);
		style.stopColor = update(style.stopColor, stopColor);
		style.stroke = update(style.stroke, stroke);
		style.solidColor = update(style.solidColor, solidColor);
		style.viewportFill = update(style.viewportFill, viewportFill);
	}

	private SvgPaint update(SvgPaint svgPaint, int color) {
		if (svgPaint instanceof Colour && color != -1) {
			((Colour) svgPaint).colour = color;
		}
		return svgPaint;
	}

}
