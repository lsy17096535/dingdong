package com.intexh.bidong.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Created by testuser on 16/3/16.
 */
public class ConstactUtils {

    public static List<String> getContact(Context context) {
        List<String> lists= new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        while (cursor.moveToNext()) {
            String phoneNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNum = phoneNum.replaceFirst("086", "");
            phoneNum = phoneNum.replaceAll("-", "");
            phoneNum = phoneNum.replaceAll(" ", "");
            if(isMobile(phoneNum)){
                lists.add(phoneNum);
            }
        }
        return lists;
    }

    /**
     * 手机号验证
     *
     * @param  str
     * @return 验证通过返回true
     */
    public static boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }
}
