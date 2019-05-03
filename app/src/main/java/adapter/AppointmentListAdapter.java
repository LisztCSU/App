package adapter;



import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.liszt.wesee.R;
import com.liszt.wesee.activity.ChatActivity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentListAdapter extends SimpleAdapter {
    private Context mcontext = null;


    public AppointmentListAdapter(Context context,
                                  List<? extends Map<String, ?>> data, int resource,
                                  String[] from, int[] to) {
        super(context, data, resource, from, to);
        // TODO Auto-generated constructor stub
        mcontext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view = super.getView(position, convertView, parent);
        HashMap<String, Object> map = (HashMap<String, Object>) getItem(position);
        Button communicate = (Button) view.findViewById(R.id.bt_operation);
        TextView object = (TextView) view.findViewById(R.id.txt_objectname);
        String arr[] = map.get("object").toString().split("#");
        String str = map.get("operation").toString();
        String arr2[] = arr[1].split("&&");
        if (arr[0].equals("0")){
            object.setText(arr2[0]+"邀请看");
            str = str+"&&"+arr2[1];
        }
        else {
            object.setText("我邀请"+arr2[1]+"看");
            str= str + "&&"+arr2[0];
        }
        communicate.setText("交流");
        communicate.setTag(str);
        communicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent =  new Intent(mcontext, ChatActivity.class);
              String arr3[]= v.getTag().toString().split("&&");
              intent.putExtra("chatId",arr3[0]);
              intent.putExtra("account",arr3[1]);
              mcontext.startActivity(intent);
            }
        });


        return view;
    }
}

