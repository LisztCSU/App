
package watchers;

import android.text.Editable;
        import android.text.TextUtils;
        import android.text.TextWatcher;
        import android.widget.Button;
        import android.widget.EditText;

import java.util.regex.Pattern;

public class registerWatcher implements TextWatcher {
    private EditText EditList[];
    private Button  regitster;

    public registerWatcher( EditText editList[],Button regitster) {
        EditList = editList;
       this.regitster = regitster;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(!TextUtils.isEmpty(EditList[0].getText())&&!TextUtils.isEmpty(EditList[1].getText())&&!TextUtils.isEmpty(EditList[2].getText())&&!TextUtils.isEmpty(EditList[3].getText())&&!TextUtils.isEmpty(EditList[4].getText())
                &&Pattern.matches("^[a-zA-][0-9a-zA-Z_]{5,10}$",EditList[0].getText().toString())
                &&Pattern.matches("^(?=.*[0-9])(?=.*[A-Za-z])[\\x20-\\x7e]{6,20}$",EditList[1].getText().toString())
                &&EditList[1].getText().toString().equals(EditList[2].getText().toString()))
               {
                   regitster.setEnabled(true);

        }
        else {
            regitster.setEnabled(false);
        }

    }

    @Override
    public void afterTextChanged(Editable s) {


    }
}