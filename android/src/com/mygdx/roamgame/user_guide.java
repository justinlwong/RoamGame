package com.mygdx.roamgame;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class user_guide extends Activity {

    int[] imageArray;
    Bundle bundle;
    final TypedArray imageArrayIcon=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_guide);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        ImagePagerAdapter adapter = new ImagePagerAdapter();
        viewPager.setAdapter(adapter);
        //imageArrayIcon = getResources().obtainTypedArray(R.array.imageArray);
    }

    private class ImagePagerAdapter extends PagerAdapter {
        //
        private int[] mImages = new int[]{R.drawable.i1, R.drawable.i2, R.drawable.i3, R.drawable.i4, R.drawable.i5, R.drawable.i6};

        @Override
        public int getCount() {
            return mImages.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Context context = user_guide.this;
            ImageView imageView = new ImageView(context);
            /*int padding = context.getResources().getDimensionPixelSize(
                    R.dimen.padding_large);
            imageView.setPadding(padding, padding, padding, padding);*/
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setImageResource(mImages[position]);

            //imageView.setImageResource(imageArrayIcon.getResourceId(mImages[position], -1));
            ((ViewPager) container).addView(imageView, 0);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((ImageView) object);
        }
    }
    }
