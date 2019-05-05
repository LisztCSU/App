package watchers;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Pattern;


public class passwordWatcher implements TextWatcher {
    private EditText password;
    private TextView wrong;

    public passwordWatcher(EditText password, TextView wrong) {
        this.password = password;
        this.wrong = wrong;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if(TextUtils.isEmpty(password.getText())){
            wrong.setText("");
            wrong.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(TextUtils.isEmpty(password.getText())){
            wrong.setText("密码为空！");
            wrong.setVisibility(View.VISIBLE);
        }
        else if(!Pattern.matches("[\\x20-\\x7e]{6,}$", password.getText().toString())){
            wrong.setText("密码太短！");
            wrong.setVisibility(View.VISIBLE);
        }
        else if(Pattern.matches("^[0-9]{6,20}$", password.getText().toString())||Pattern.matches("^[a-zA-Z]{6,20}$", password.getText().toString())){
            wrong.setText("密码不能为纯数字或字母!");
            wrong.setVisibility(View.VISIBLE);
        }
        else {
            wrong.setVisibility(View.GONE);
        }
    }
}