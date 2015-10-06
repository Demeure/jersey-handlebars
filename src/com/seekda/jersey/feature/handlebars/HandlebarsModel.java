package com.seekda.jersey.feature.handlebars;

import com.github.jknack.handlebars.Handlebars;

public class HandlebarsModel<T> {
	private T data;
	public T getData () { return data; }
	public void setData (T data) { this.data = data; }

	private Handlebars handlebars;
	public Handlebars getHandlebars () { return this.handlebars; }
	public void setHandlebars (Handlebars handlebars) { this.handlebars = handlebars; }

	public HandlebarsModel () {
		this(null);
	}
	public HandlebarsModel (T data) {
		setData(data);
		setHandlebars(new Handlebars());
	}
}
