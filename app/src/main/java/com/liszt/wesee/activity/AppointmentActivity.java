package com.liszt.wesee.activity;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.liszt.wesee.R;
import com.liszt.wesee.fragment.AppointmentFragment;
import com.liszt.wesee.fragment.InvitedFragment;
import com.liszt.wesee.fragment.ReceivedFragment;

public class AppointmentActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private TextView txt1,txt2,txt3;
    private FragmentTransaction fragmentTransaction;
    Fragment appointmentFragment, receivedFragment, invitedFragment;
    public static final int VIEW_APP_INDEX = 0;
    public static final int VIEW_REC_INDEX = 1;
    public static final int VIEW_INV_INDEX = 2;
    private int temp_position_index = -1;
    private String fragmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        initView();

        fragmentId= getIntent().getStringExtra("fragmentId");
        if (fragmentId!=null&&fragmentId.equals("1")) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.id_fragment_content,ReceivedFragment.getNewInstance())
                    .addToBackStack(null)
                    .commit();
            temp_position_index = VIEW_REC_INDEX;
            txt1.setTextColor(Color.parseColor("#707070"));
            txt3.setTextColor(Color.parseColor("#707070"));
            txt2.setTextColor(Color.parseColor("#00a628"));
        }
    }

    private void initView() {
        radioGroup = (RadioGroup) findViewById(R.id.id_navcontent);
        txt1 =((TextView) findViewById(R.id.txt_appointment));
        txt2 =((TextView) findViewById(R.id.txt_recived));
        txt3 = ((TextView) findViewById(R.id.txt_invited));
        txt1.setTextColor(Color.parseColor("#00a628"));
        appointmentFragment = AppointmentFragment.getNewInstance();
        receivedFragment = ReceivedFragment.getNewInstance();
        invitedFragment = InvitedFragment.getNewInstance();
        //显示
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.id_fragment_content, appointmentFragment);
        fragmentTransaction.commit();



    }

    public void switchView(View view) {
        txt1.setTextColor(Color.parseColor("#707070"));
       txt2.setTextColor(Color.parseColor("#707070"));
        txt3.setTextColor(Color.parseColor("#707070"));
        switch (view.getId()) {
            case R.id.txt_appointment:
                if (temp_position_index != VIEW_APP_INDEX) {
                    //显示
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.id_fragment_content, appointmentFragment);
                    fragmentTransaction.commit();
                }
                temp_position_index = VIEW_APP_INDEX;
               txt1.setTextColor(Color.parseColor("#00a628"));
                break;
            case R.id.txt_recived:
                if (temp_position_index != VIEW_REC_INDEX) {
                    //显示
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.id_fragment_content, receivedFragment);
                    fragmentTransaction.commit();

                }
                 temp_position_index = VIEW_REC_INDEX;
                 txt2.setTextColor(Color.parseColor("#00a628"));
                 break;
            case R.id.txt_invited:
                if (temp_position_index != VIEW_INV_INDEX) {
                    //显示
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.id_fragment_content, invitedFragment);
                    fragmentTransaction.commit();
                }
                temp_position_index = VIEW_INV_INDEX;
                txt3.setTextColor(Color.parseColor("#00a628"));
                break;

}

    }

}
