package com.squarecircle.automonkeytest.Activity.Email;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.squarecircle.automonkeytest.R;

import javax.security.auth.Subject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MailActivity extends AppCompatActivity {
    @BindView(R.id.result_email_send)
    Button button;
    @BindView(R.id.text_email_address)
    TextView textOfEmailAddr;

    private static final String FILE_PATH = "file_path";
    private String filePath;

    public static void start(Context context, String filePath) {
        Intent intent = new Intent(context, MailActivity.class);
        intent.putExtra(FILE_PATH, filePath);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);
        ButterKnife.bind(this);

        filePath = getIntent().getStringExtra(FILE_PATH);
        if (TextUtils.isEmpty(filePath)) {
            return;
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailAddr = textOfEmailAddr.getText().toString();
                Intent intent1 = new Intent(Intent.ACTION_SEND);

                String[] tos={emailAddr};

                intent1.putExtra(Intent.EXTRA_EMAIL, tos);
                intent1.putExtra(Intent.EXTRA_SUBJECT, "分析结果");
                intent1.putExtra(Intent.EXTRA_TEXT, "分析结果如文件所示");
                intent1.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + filePath));

                intent1.setType("message/rfc822");
                Intent.createChooser(intent1, "Choose Email Client");
                startActivity(intent1);
            }
        });


    }
}
