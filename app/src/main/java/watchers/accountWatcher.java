package watchers;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import android.widget.EditText;
import android.widget.TextView;


import java.util.regex.Pattern;


public class accountWatcher implements TextWatcher {
    private EditText account;
    private TextView wrong;

    public accountWatcher(EditText account, TextView wrong) {
        this.account = account;
        this.wrong = wrong;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if(TextUtils.isEmpty(account.getText())){
            wrong.setText("");
            wrong.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(TextUtils.isEmpty(account.getText())){
            wrong.setText("用户名为空！");
            wrong.setVisibility(View.VISIBLE);
        }
       else if(!TextUtils.isEmpty(account.getText())&& !Pattern.matches("^[a-zA-][0-9a-zA-Z_]{5,10}$", account.getText().toString())){
            wrong.setText("用户名不合要求");
            wrong.setVisibility(View.VISIBLE);
        }
        else {
            wrong.setVisibility(View.GONE);
        }
    }
}
