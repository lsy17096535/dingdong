package com.intexh.bidong.constants;

public class Constants {
//	public static final String BASE_URL = "http://192.168.31.66:8888/api/v1";

	public static final String LOG_TAG_CACHE = "VideoCache";
	
	public static class MessageType{
		public static final int MessageType_AvatarOk = 9;		    //头像审核通过
		public static final int MessageType_AvatarNo = 10;		    //头像审核不过
		public static final int MessageType_VideoAudio = 11;		//视频审核通知
		public static final int MessageType_CashApply = 12;			//体现处理通知
		public static final int MessageType_GiftAsk = 21;			//要礼物
		public static final int MessageType_GiftReceive = 22;		//收到礼物
		public static final int MessageType_OfferNew = 31;			//新邀请
		public static final int MessageType_OfferAccept = 32;		//邀约被接受
		public static final int MessageType_OfferReject = 33;		//邀约被拒绝
		public static final int MessageType_OffsetCancel = 34;		//取消
		public static final int MessageType_FANS_ADD = 41;			//增加粉丝
		public static final int MessageType_FANS_ACTIVE = 42;		//激活粉丝
		public static final int MessageType_FANS_BLOCK = 43;		//屏蔽粉丝
		public static final int MessageType_FANS_DELETE = 44;		//删除粉丝
		public static final int MessageType_COMMENT = 51;			//评论
		public static final int MessageType_VIDEO_OK = 52;			//认证视频审核通过
		public static final int MessageType_OrderNew = 61;			//新约单
		public static final int MessageType_OrderAccept = 62;		//约单被接受
		public static final int MessageType_OrderReject = 63;		//约单被拒绝
		public static final int MessageType_OrderCancel = 64;		//约单被取消
		public static final int MessageType_OrderAsk = 65;		    //约单收款
		public static final int MessageType_OrderPay = 66;		    //约单付款
		public static final int MessageType_OpporCommend = 71;		//模特需求、娱乐场所推荐
		public static final int MessageType_USER_VISIT = 81;		//来访
		public static final int MessageType_USER_LIKE = 82;		    //被喜欢
	}

	//appid
	//请同时修改  androidmanifest.xml里面，.PayActivityd里的属性<data android:scheme="wxb4ba3c02aa476ea1"/>为新设置的appid
	public static final String APP_ID = "wx1a145aa869523a4d";
}
