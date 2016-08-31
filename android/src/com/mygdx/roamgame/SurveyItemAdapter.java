package com.mygdx.roamgame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Vinu on 2016-03-19.
 */
public class SurveyItemAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<String> mFiles;

    public SurveyItemAdapter(Context context, ArrayList<String> files) {
        mContext = context;
        mFiles = files;
    }

    @Override
    public int getCount() {
        return mFiles.size();
    }

    @Override
    public Object getItem(int position) {
        return mFiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View rowView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.survey_item, parent, false);
        }
        else {
            rowView = convertView;
        }

        TextView questionName = (TextView) rowView.findViewById(R.id.question_name);
        questionName.setText(mFiles.get(position).toString());

        /*ImageView ivDelete = (ImageView) rowView.findViewById(R.id.delete_icon);
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String file_name = ((TextView) ((LinearLayout)v.getParent()).findViewById(R.id.file_name)).getText().toString();

            }
        });*/

        return rowView;
    }
}
