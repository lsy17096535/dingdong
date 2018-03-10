package com.intexh.bidong.utils;

import java.util.Comparator;

import com.intexh.bidong.userentity.FriendItemEntity;

public class PinyinComparator implements Comparator{
	private static final String TAG = "PinyinComparator";
	@Override
	public int compare(Object o1, Object o2) {
		FriendItemEntity info1 = (FriendItemEntity) o1;
		FriendItemEntity info2 = (FriendItemEntity) o2;
		if (info1 == info2) {
			return 0;
		}
	    String str1 = null;
		if(null == info1.getSort_key()){
			str1 = PingYinHelpUtil.getPingYin((String) info1.getFans().getAlias());
			str1 = str1.toUpperCase();
			str1 = str1.replaceAll("[^A-Z]", "");
			info1.setSort_key(str1);
		}else{
			str1 = info1.getSort_key();
		}
	    String str2 = null;
	    if(null == info2.getSort_key()){
	    	str2 = PingYinHelpUtil.getPingYin((String) info2.getFans().getAlias());
	    	str2 = str2.toUpperCase();
	    	str2 = str2.replaceAll("[^A-Z]", "");
	    	info2.setSort_key(str2);
	    }else{
	    	str2 = info2.getSort_key();
	    }
	    return str1.compareTo(str2);
	}

}
