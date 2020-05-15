package prj.cognitive.func.content.comparison;

import java.awt.Color;

import com.google.gson.annotations.Expose;

public class DiffRequest {
	@Expose
	private String action;
	@Expose
	private String color;
	@Expose
	private double opacity = 0.4;

	public DiffRequest() {
		this.action = "annotate";
		this.color = "Yellow";
		this.opacity = 0.4;
	}

	public DiffRequest(String action, String color, double opacity) {
		this.action = action;
		this.color = color;
		this.opacity = opacity;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public double getOpacity() {
		return opacity;
	}

	public void setOpacity(double opacity) {
		this.opacity = opacity;
	}

}
