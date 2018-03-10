package com.intexh.bidong.me;

import android.os.Bundle;

import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseActivity;
import com.intexh.bidong.gift.MainGiftFragment;

public class MainGiftActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift2);
        getSupportFragmentManager().beginTransaction().replace(R.id.root,new MainGiftFragment()).commit();
    }
}
