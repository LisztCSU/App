package watchers;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class confirmPasswordWatcher implements TextWatcher {
    private EditText confirmPassword;
    private EditText password;

    private TextView notsame;

    public confirmPasswordWatcher(EditText confirmPassword,EditText password, TextView notsame) {
        this.confirmPassword = confirmPassword;
        this.password = password;
        this.notsame = notsame;

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if(TextUtils.isEmpty(confirmPassword.getText())){
            notsame.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {


    }

    @Override
    public void afterTextChanged(Editable s) {
         if (!TextUtils.isEmpty(confirmPassword.getText()) && !TextUtils.isEmpty(password.getText()) && confirmPassword.getText().toString().equals(password.getText().toString())||TextUtils.isEmpty(confirmPassword.getText())){
             notsame.setVisibility(View.GONE);
         }
         else {
             notsame.setVisibility(View.VISIBLE);
         }


    }
}
