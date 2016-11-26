package me.ziningzhu.basicchattingbot;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText mInputText;
    private Button mSendButton;
    private Button mClearButton;
    private String mSentence;
    private ArrayList<String> mMessages;
    private ListView mListView;
    private MyMessagesAdapter mListAdapter;
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInputText = (EditText)findViewById(R.id.user_input);
        mInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSentence = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mMessages = new ArrayList<String>();
        mListView = (ListView)findViewById(R.id.my_list_view);

        mListAdapter = new MyMessagesAdapter(getApplicationContext(), mMessages);
        mListView.setAdapter(mListAdapter);

        mSendButton = (Button)findViewById(R.id.submit_button);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // read file, append to end of its arrayList, and write back
                if (mMessages == null)
                    mMessages = new ArrayList<String>();
                mMessages.add("me:"+mSentence);


                writeMessagesToMemory();

                updateAdapterDataAndView();
                mInputText.setText("");

                sendRequestUpdateResult(mSentence);
            }
        });

        mClearButton = (Button)findViewById(R.id.clear_messages_button);
        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanUpPreviousMessages();
            }
        });

    }

    private void cleanUpPreviousMessages() {

        // Starts appending to the new file.
        File file = new File(getFilesDir(), "message_history.ser");
        boolean deleted = file.delete();
        mMessages.clear();

        updateAdapterDataAndView();

    }

    private void updateAdapterDataAndView() {
        //mListView.setAdapter(new MyMessagesAdapter(getApplicationContext(), mMessages));
        mListAdapter.updateData(mMessages);
        writeMessagesToMemory();

    }

    private void writeMessagesToMemory() {
        try {
            // Starts appending to the new file.
            File file = new File(getFilesDir(), "message_history.ser");
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(mMessages);
            out.close();
            fos.close();
        } catch (IOException i) {
            i.printStackTrace();
            Log.e(TAG, "IOException when writing memory file.");
        }
    }

    private void sendRequestUpdateResult(String mSentence) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://54.147.200.46/basicRequest")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code "+response);
                } else {
                    final String responseText = "server:"+response.body().string();

                    Log.d(TAG, responseText);


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMessages.add(responseText);
                            updateAdapterDataAndView();
                        }
                    });

                }
            }
        });
    }

    @Override
    protected void onResume() {
        String TAG = "onResume";
        super.onResume();
        // Read file to get mMessages. Should be placed in onResume()
        try {
            File file = new File(getFilesDir(), "message_history.ser");
            FileInputStream fin = new FileInputStream(file);
            ObjectInputStream oin = new ObjectInputStream(fin);
            mMessages = (ArrayList<String>)oin.readObject();
        } catch(IOException i) {
            i.printStackTrace();
            Log.e(TAG, "Error when reading memory!");
        } catch(ClassNotFoundException c) {
            c.printStackTrace();
            Log.e(TAG, "ClassNotFoundException when reading memory!");
        }
        if (mMessages != null && mMessages.size() > 0) {
            Log.d(TAG, "mMessages length: " + mMessages.size());
            Log.d(TAG, "mMessages: " + mMessages.toString());

            updateAdapterDataAndView();

        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences settings = getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("EditArea", mSentence);
        editor.apply();

    }
}
