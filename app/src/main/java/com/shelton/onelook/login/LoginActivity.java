package com.shelton.onelook.login;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shelton.onelook.R;
import com.shelton.onelook.util.MyUtil;
import com.shelton.onelook.widget.LoadingDialog;
import com.shelton.onelook.widget.SwipeBackActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LoginActivity extends SwipeBackActivity implements LoginRegisterContract.View {
    @BindView(R.id.account)
    EditText account;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.login)
    Button login;
    @BindView(R.id.register)
    TextView registerButton;
    @BindView(R.id.forget_password)
    TextView forgetButton;
    @BindView(R.id.login_back)
    Button close;
    @BindView(R.id.login_bar_theme)
    View loginBarTheme;

    private InputMethodManager inputMethodManager;
    private LoadingDialog dialog;
    private LoginRegisterPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        dialog = new LoadingDialog(this, "登录中");
        presenter = new LoginRegisterPresenter(this);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        close.setOnClickListener(v -> finish());

        login.setOnClickListener(v -> {
            if (TextUtils.isEmpty(account.getText()) || TextUtils.isEmpty(password.getText())) {
                Toast.makeText(LoginActivity.this, "账户或密码不能为空！", Toast.LENGTH_SHORT).show();
                return;
            }
//                dialog.show();
            presenter.login(account.getText().toString(), password.getText().toString());
        });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            intent.putExtra("type", 0);
            startActivity(intent);
        });

        forgetButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            intent.putExtra("type", 1);
            startActivity(intent);
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        loginBarTheme.setBackgroundColor(Color.parseColor(preferences.getString("theme_color", "#fb7299")));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unsubscribe();
        if (MyUtil.isSoftInputMethodShowing(this)) {
            inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
        if (msg.contains("成功")) {
            Intent intent = new Intent();
            intent.putExtra("username", account.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}