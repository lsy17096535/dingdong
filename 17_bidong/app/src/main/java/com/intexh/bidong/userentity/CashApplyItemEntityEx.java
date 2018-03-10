package com.intexh.bidong.userentity;

public class CashApplyItemEntityEx extends CashApplyItemEntity{
	private int status;	//0待处理		1已打款	2已取消	3处理中

	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status){
		this.status = status;
	}
}
