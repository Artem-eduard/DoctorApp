package com.cb.softwares.doctorapp.activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.cb.softwares.doctorapp.R;
import com.cb.softwares.doctorapp.fragment.ChatFragment;
import com.cb.softwares.doctorapp.fragment.UserFragment;
import com.cb.softwares.doctorapp.util.UtilActivity;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatMainActivity extends UtilActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.viewPager)
    ViewPager viewpager;

    ChatFragment chatFragment;
    UserFragment userFragment;


    public Uri sharedDataUri= null;
    public boolean isUriAvailable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);
        ButterKnife.bind(this);


        setup_toolbar_with_back(toolbar, "Chats");
        setUpViewpager();

        if (getIntent().getExtras() != null){

            isUriAvailable = getIntent().getExtras().getBoolean("isUriAvailable");
            if (isUriAvailable){
                sharedDataUri = Uri.parse(getIntent().getExtras().getString("uri"));
            }
        }


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


    private void setUpViewpager() {

        ViewpagerAdapter adapter = new ViewpagerAdapter(getSupportFragmentManager());


        chatFragment = new ChatFragment();
        userFragment = new UserFragment();
        adapter.addFragment(chatFragment, "Chat");
        adapter.addFragment(userFragment, "Users");

        viewpager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewpager);

    }


    class ViewpagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragmentsList;
        private ArrayList<String> titleList;

        public ViewpagerAdapter(FragmentManager fm) {
            super(fm);

            fragmentsList = new ArrayList<>();
            titleList = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int i) {
            return fragmentsList.get(i);
        }

        @Override
        public int getCount() {
            return fragmentsList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentsList.add(fragment);
            titleList.add(title);
        }


        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }
    }
}
