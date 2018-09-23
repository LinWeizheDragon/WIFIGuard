package com.dragon.wifiguard.spalish.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.dragon.wifiguard.spalish.HelpActivity;
import com.dragon.wifiguard.MainActivity;
import com.dragon.wifiguard.MainUI;
import com.dragon.wifiguard.R;

/**
 *     class desc: ����ҳ��������
 */
public class ViewPagerAdapter extends PagerAdapter
{

	// �����б�
	private List<View> mViews;
	private Activity mActivity;

	private static final String SHAREDPREFERENCES_NAME = "first_pref";

	public ViewPagerAdapter(List<View> mViews, Activity mActivity)
	{
		this.mViews = mViews;
		this.mActivity = mActivity;
	}

	// ����arg1λ�õĽ���
	@Override
	public void destroyItem(View arg0, int arg1, Object arg2)
	{
		((ViewPager) arg0).removeView(mViews.get(arg1));
	}

	@Override
	public void finishUpdate(View arg0)
	{
	}

	// ��õ�ǰ������
	@Override
	public int getCount()
	{
		if (mViews != null)
		{
			return mViews.size();
		}
		return 0;
	}

	// ��ʼ��arg1λ�õĽ���
	@Override
	public Object instantiateItem(View arg0, int arg1)
	{
		((ViewPager) arg0).addView(mViews.get(arg1), 0);
		if (mActivity instanceof HelpActivity)
		{
			if (arg1 == mViews.size() - 1)
			{
				Button btn = (Button) arg0.findViewById(R.id.help_back);
				btn.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						mActivity.finish();
					}
				});
			}
			return mViews.get(arg1);
		}

		if (arg1 == mViews.size() - 1)
		{
			Button mStartWeiboImageButton = (Button) arg0
					.findViewById(R.id.iv_start_weibo);
			mStartWeiboImageButton.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					// �����Ѿ�����
					setGuided();
					goHome();

				}

			});
		}
		return mViews.get(arg1);
	}

	private void goHome()
	{
		// ��ת
		Intent intent = new Intent(mActivity, MainUI.class);
		mActivity.startActivity(intent);
		mActivity.finish();
	}

	/**
	 * 
	 * method desc�������Ѿ��������ˣ��´����������ٴ�����
	 */
	private void setGuided()
	{
		SharedPreferences preferences = mActivity.getSharedPreferences(
				SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		// ��������
		editor.putBoolean("isFirstIn", false);
		// �ύ�޸�
		editor.commit();
	}

	// �ж��Ƿ��ɶ������ɽ���
	@Override
	public boolean isViewFromObject(View arg0, Object arg1)
	{
		return (arg0 == arg1);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1)
	{
	}

	@Override
	public Parcelable saveState()
	{
		return null;
	}

	@Override
	public void startUpdate(View arg0)
	{
	}

}