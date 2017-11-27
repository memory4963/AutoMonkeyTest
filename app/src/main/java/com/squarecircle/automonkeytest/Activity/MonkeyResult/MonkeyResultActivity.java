package com.squarecircle.automonkeytest.Activity.MonkeyResult;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.squarecircle.automonkeytest.Activity.Email.MailActivity;
import com.squarecircle.automonkeytest.Base.App;
import com.squarecircle.automonkeytest.R;
import com.squarecircle.automonkeytest.Utils.ShellInputs;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MonkeyResultActivity extends AppCompatActivity {

    private static final String COMMAND = "command";

    public static final int MONKEY_CALLBACK = 1234;
    public static final int MONKEY_FILEPATH = 1235;

    @BindView(R.id.mon_result_tv)
    TextView resultTv;
    @BindView(R.id.mon_result_toolbar)
    Toolbar toolbar;
    @BindView(R.id.send_btn)
    Button sendBtn;

    String filePath = "";

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MONKEY_CALLBACK:
                    String input = (String) msg.obj;
                    if (input != null) {
                        resultTv.setText(input);
                        Toast.makeText(App.getContext(), "命令完成", Toast.LENGTH_SHORT).show();
                    } else {
                        resultTv.setText("input is null");
                        Toast.makeText(App.getContext(), "命令失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MONKEY_FILEPATH:
                    filePath = (String) msg.obj;
            }
        }
    };

    public static void start(Context context, String command) {
        Intent intent = new Intent(context, MonkeyResultActivity.class);
        intent.putExtra(COMMAND, command);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monkey_result);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Monkey测试结果");

        Intent intent = getIntent();
        String command = intent.getStringExtra(COMMAND);
        if (command != null) {
            ShellInputs.executeForResult(command, handler);
        }

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(filePath)) {
                    Toast.makeText(MonkeyResultActivity.this, "获取路径失败！", Toast.LENGTH_SHORT).show();
                    return;
                }
                MailActivity.start(MonkeyResultActivity.this, filePath);
            }
        });

        resultTv.setText("Hello World!");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return true;
    }
}
