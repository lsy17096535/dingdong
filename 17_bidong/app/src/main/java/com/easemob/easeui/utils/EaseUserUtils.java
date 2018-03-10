package com.easemob.easeui.utils;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.intexh.bidong.PPStarApplication;
import com.intexh.bidong.R;
import com.intexh.bidong.user.FriendsManager;
import com.intexh.bidong.userentity.FriendItemEntity;
import com.intexh.bidong.utils.ImageUtils;
import com.intexh.bidong.utils.UserUtils;

import com.easemob.easeui.controller.EaseUI;
import com.easemob.easeui.controller.EaseUI.EaseUserProfileProvider;

public class EaseUserUtils {
    
    static EaseUserProfileProvider userProvider;
    
    static {
        userProvider = EaseUI.getInstance().getUserProfileProvider();
    }
    
    /**
     * 设置用户头像
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView){
    	FriendItemEntity entity = FriendsManager.getInstance().getFrinedInfo(username);
    	if(null != entity){
    		ImageUtils.loadAvatarDefaultImage(entity.getFans().getAvatar(), imageView);
    	}else{
    		if(UserUtils.getUserInfo().getUser().getHxId().equals(username)){
    			ImageUtils.loadAvatarDefaultImage(UserUtils.getUserInfo().getUser().getAvatar(), imageView);
    		}else{
    			if("ppstar".equals(username)){
    				imageView.setImageResource(R.drawable.pic_service);
    			}else{
    				imageView.setImageResource(R.drawable.ease_default_avatar);
    			}
    		}
    	}
    }
    
    /**
     * 设置用户昵称
     */
    public static void setUserNick(String username,TextView textView){
        if(textView != null){
        	FriendItemEntity entity = FriendsManager.getInstance().getFrinedInfo(username);
        	if(null != entity){
        		textView.setText(entity.getFans().getAlias());
        	}else{
        		if("ppstar".equals(username)){
        			textView.setText(PPStarApplication.getInstance().getString(R.string.app_kefu));
        		}else{
        			textView.setText("");
        		}
        	}
        }
    }
    
}
