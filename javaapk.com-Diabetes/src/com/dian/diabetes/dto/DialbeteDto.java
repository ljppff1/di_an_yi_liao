package com.dian.diabetes.dto;

public class DialbeteDto {

	private String id;
	private String value;
	private int dinOrder;
	private int dinStage;
	private int level;
	private String note;
	private String signs;
	private long createTime;
	private long updateTime;
	private int status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getDinOrder() {
		return dinOrder;
	}

	public void setDinOrder(int dinOrder) {
		this.dinOrder = dinOrder;
	}

	public int getDinStage() {
		return dinStage;
	}

	public void setDinStage(int dinStage) {
		this.dinStage = dinStage;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getSigns() {
		return signs;
	}

	public void setSigns(String signs) {
		this.signs = signs;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

}
