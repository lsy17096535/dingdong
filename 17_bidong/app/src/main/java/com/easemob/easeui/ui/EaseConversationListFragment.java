package com.easemob.easeui.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.easeui.utils.EaseCommonUtils;
import com.easemob.easeui.utils.EaseSmileUtils;
import com.easemob.easeui.widget.EaseConversationList;
import com.easemob.easeui.widget.EaseTitleBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import com.intexh.bidong.R;
import com.intexh.bidong.contact.ContactActivity;
import com.intexh.bidong.easemob.chat.ChatActivity;
import com.intexh.bidong.easemob.constants.Constant;

import uk.co.senab.photoview.log.Logger;

/**
 * 会话列表fragment
 *
 */
public class EaseConversationListFragment extends EaseBaseFragment{
    protected EaseTitleBar titleBar;
    protected EditText query;
    protected ImageButton clearSearch;
    protected boolean hidden;
    protected List<EMConversation> conversationList = new ArrayList<EMConversation>();
    protected EaseConversationList conversationListView;

    protected FrameLayout errorItemContainer;

    protected boolean isConflict;
    private TextView service_unread_msg_number;
    private TextView service_message;
    private RelativeLayout service_itease_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ease_fragment_conversation_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void initView() {
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //会话列表控件
        conversationListView = (EaseConversationList) getView().findViewById(R.id.list);
        // 搜索框
        query = (EditText) getView().findViewById(R.id.query);
        // 搜索框中清除button
        clearSearch = (ImageButton) getView().findViewById(R.id.search_clear);
        errorItemContainer = (FrameLayout) getView().findViewById(R.id.fl_error_item);

        //客服
        service_itease_layout = (RelativeLayout)getView().findViewById(R.id.service_itease_layout);
        service_unread_msg_number = (TextView)getView().findViewById(R.id.service_unread_msg_number);
        service_message = (TextView)getView().findViewById(R.id.service_message);
        service_itease_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转客服
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(Constant.EXTRA_USER_ID, "ppstar");
                startActivity(intent);
            }
        });

        titleBar = (EaseTitleBar) getView().findViewById(R.id.ease_title_bar);
        titleBar.setRightImageResource(R.drawable.icon_ban_friend);
        titleBar.setRightLayoutClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ContactActivity.class);
                startActivity(intent);
            }
        });
    }
    
    @Override
    protected void setUpView() {
        Log.e("frank","setUpView");
    	conversationList.clear();
        conversationList.addAll(loadConversationList());
        conversationListView.init(conversationList);
        
        if(listItemClickListener != null){
            conversationListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    EMConversation conversation = conversationListView.getItem(position);
                    listItemClickListener.onListItemClicked(conversation);
                }
            });
        }
        
        EMChatManager.getInstance().addConnectionListener(connectionListener);
        
        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                conversationListView.filter(s);
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        clearSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                hideSoftKeyboard();
            }
        });
        
        conversationListView.setOnTouchListener(new OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard();
                return false;
            }
        });
    }
    
    
    protected EMConnectionListener connectionListener = new EMConnectionListener() {
        
        @Override
        public void onDisconnected(int error) {
            if (error == EMError.USER_REMOVED || error == EMError.CONNECTION_CONFLICT) {
                isConflict = true;
            } else {
               handler.sendEmptyMessage(0);
            }
        }
        
        @Override
        public void onConnected() {
            handler.sendEmptyMessage(1);
        }
    };
    private EaseConversationListItemClickListener listItemClickListener;
    
    protected Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case 0:
                onConnectionDisconnected();
                break;
            case 1:
                onConnectionConnected();
                break;
                
            default:
                break;
            }
        }
    };
    
    /**
     * 连接到服务器
     */
    protected void onConnectionConnected(){
        errorItemContainer.setVisibility(View.GONE);
    }
    
    /**
     * 连接断开
     */
    protected void onConnectionDisconnected(){
        errorItemContainer.setVisibility(View.VISIBLE);
    }
    

    /**
     * 刷新页面
     */
    public void refresh() {
        conversationList.clear();
        conversationList.addAll(loadConversationList());
        conversationListView.refresh();
    }
    
    /**
     * 获取会话列表
     * @return
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        +    */
    protected List<EMConversation> loadConversationList(){
        // 获取所有会话，包括陌生人
        Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
        // 过滤掉messages size为0的conversation
        /**
         * 如果在排序过程中有新消息收到，lastMsgTime会发生变化
         * 影响排序过程，Collection.sort会产生异常
         * 保证Conversation在Sort过程中最后一条消息的时间不变 
         * 避免并发问题
         */
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    //if(conversation.getType() != EMConversationType.ChatRoom){
//                        sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                    //}
                    if(!conversation.getLastMessage().getUserName().equals("ppstar")){
                        sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                    }else{
                        //如果是客服
                        int unreadMsgCount = conversation.getUnreadMsgCount();
                        if(unreadMsgCount>0){
                            service_unread_msg_number.setVisibility(View.VISIBLE);
                            service_unread_msg_number.setText(unreadMsgCount+"");
                        }else{
                            service_unread_msg_number.setVisibility(View.GONE);
                        }
                        EMMessage lastMessage = conversation.getLastMessage();
                        if(lastMessage!=null &&getActivity()!=null){
                            service_message.setText(EaseSmileUtils.getSmiledText(getActivity(), EaseCommonUtils.getMessageDigest(lastMessage, (getActivity()))),
                                    TextView.BufferType.SPANNABLE);
                        }
                    }

                }


            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {

            list.add(sortItem.second);
        }
        return list;
    }

    /**
     * 根据最后一条消息的时间排序
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }
    
   protected void hideSoftKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden && !isConflict) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            refresh();
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        EMChatManager.getInstance().removeConnectionListener(connectionListener);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(isConflict){
            outState.putBoolean("isConflict", true);
        }
    }
    
    public interface EaseConversationListItemClickListener {
        /**
         * 会话listview item点击事件
         * @param conversation 被点击item所对应的会话
         */
        void onListItemClicked(EMConversation conversation);
    }
    
    /**
     * 设置listview item点击事件
     * @param listItemClickListener
     */
    public void setConversationListItemClickListener(EaseConversationListItemClickListener listItemClickListener){
        this.listItemClickListener = listItemClickListener;
    }

}
