package com.intexh.bidong.gift;

import com.easemob.easeui.utils.Settings;
import com.google.gson.reflect.TypeToken;
import com.intexh.bidong.net.GsonUtils;
import com.intexh.bidong.utils.DateUtil;
import com.intexh.bidong.utils.UserUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Frank on 2018/1/2.
 */

public class GiftCheckUtils {

    public static void saveChatid(String chatid){
        //记录礼物赠送好友的日期 用于判断每天与好友聊天需要赠送一次礼物
        Map<String,String> chatIds = GsonUtils.deSerializedFromJson(Settings.getString("chat_ids","")
                ,new TypeToken<Map<String,String>>(){}.getType());
        if(chatIds==null){
            chatIds = new HashMap<>();
        }
        String date = DateUtil.timestampToYMDay(System.currentTimeMillis());
        chatIds.put(chatid,date);
        Settings.setString("chat_ids",GsonUtils.serializedToJson(chatIds));
    }

    public static boolean checkChatidHex(String hexChatid){
        //记录礼物赠送好友的日期 用于判断每天与好友聊天需要赠送一次礼物
        Map<String,String> chatIds = GsonUtils.deSerializedFromJson(Settings.getString("chat_ids","")
                ,new TypeToken<Map<String,String>>(){}.getType());
        if(chatIds==null){
            return false;
        }
        if (!chatIds.containsKey(hexChatid)) {    //没有赠送过礼物 需要赠送
            return false;
        }

        if (!chatIds.get(hexChatid).equals(DateUtil.timestampToYMDay(System.currentTimeMillis()))) {
            //对比今天没有送过礼物 需要赠送
            return false;
        }
        return true;
    }
}
