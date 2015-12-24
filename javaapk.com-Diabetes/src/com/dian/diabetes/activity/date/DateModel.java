package com.dian.diabetes.activity.date;

import hirondelle.date4j.DateTime;

/**
 * 每天的状态以及提示的model，value是血糖值
 * @author hua
 *
 */
public class DateModel {

	private DateTime date;
	private String toast;
	private float value;

	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	public String getToast() {
		return toast;
	}

	public void setToast(String toast) {
		this.toast = toast;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

}
