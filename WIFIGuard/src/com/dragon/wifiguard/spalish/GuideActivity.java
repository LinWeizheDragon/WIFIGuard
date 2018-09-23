package com.dragon.wifiguard.spalish;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dragon.wifiguard.R;
import com.dragon.wifiguard.spalish.adapter.ViewPagerAdapter;

/**
 * ��������
 */
public class GuideActivity extends Activity implements OnPageChangeListener {

    private ViewPager vp;
    private ViewPagerAdapter vpAdapter;
    private List<View> views;

    // ��¼��ǰѡ��λ��
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide);

        // ��ʼ��ҳ��
        initViews();

    }

    private void initViews() {
        LayoutInflater inflater = LayoutInflater.from(this);

        views = new ArrayList<View>();
        // ��ʼ������ͼƬ�б�
        views.add(inflater.inflate(R.layout.what_new_one, null));
        views.add(inflater.inflate(R.layout.what_new_two, null));
        views.add(inflater.inflate(R.layout.what_new_three, null));

        // ��ʼ��Adapter
        vpAdapter = new ViewPagerAdapter(views, this);

        vp = (ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(vpAdapter);
        // �󶨻ص�
        vp.setOnPageChangeListener(this);
    }

    // ������״̬�ı�ʱ����
    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    // ����ǰҳ�汻����ʱ����
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    // ���µ�ҳ�汻ѡ��ʱ����
    @Override
    public void onPageSelected(int arg0) {
    }

}