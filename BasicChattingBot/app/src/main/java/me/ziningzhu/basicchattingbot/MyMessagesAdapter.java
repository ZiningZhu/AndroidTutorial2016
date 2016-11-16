package me.ziningzhu.basicchattingbot;

import android.app.ActionBar;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by zining on 15/11/16.
 */

public class MyMessagesAdapter extends ArrayAdapter<String> {
    private final Context mContext;
    private final String TAG = "MyMessageAdapter";
    private ArrayList<String> mMessages;

    public MyMessagesAdapter(Context context, ArrayList<String> messages) {
        super(context, R.layout.message_row, messages);
        mContext = context;
        mMessages = messages;
        //Log.d(TAG, "mMessages: " + mMessages.toString());
    }

    @Override
    public int getCount() {
        if (mMessages == null) {
            return 0;
        } else {
            return mMessages.size();
        }
    }

    @Override
    public String getItem(int i) {
        try{
            return mMessages.get(i);
        } catch(IndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.message_row, parent, false);
        }

        // Put messages at position i to the convertView
        // Decide whether this message is sent by me or the server. Adjust positions and bg colors.
        String txt = mMessages.get(position);
        TextView messageTV = (TextView)convertView.findViewById(R.id.message_text);
        TextView leftFill = (TextView)convertView.findViewById(R.id.left_fill);
        TextView rightFill = (TextView)convertView.findViewById(R.id.right_fill);
        if (txt.length() < 3) {
            Log.d(TAG, "Illegal String at position " + position);
            messageTV.setText(txt);
            return convertView;
        }
        if (txt.substring(0, 3).equals("me:")) {
            messageTV.setText(txt.substring(3));
            messageTV.setBackgroundColor(mContext.getResources().getColor(R.color.myMessageBackground));
            Log.d(TAG, "position="+position +"; msgTV text: " + txt.substring(3));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1);
            leftFill.setLayoutParams(params);

        }
        else if (txt.length() < 7) {
            Log.d(TAG, "Illegal String at position " + position);
            messageTV.setText(txt);
            return convertView;
        }
        else if (txt.substring(0, 7).equals("server:")) {
            messageTV.setText(txt.substring(7));
            messageTV.setBackgroundColor(mContext.getResources().getColor(R.color.serverMessageBackground));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1);
            leftFill.setLayoutParams(params);
        }



        return convertView;
    }


}
