package adapter;



import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleAdapter;
import com.liszt.wesee.R;
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
        communicate.setText("交流");
        String str = map.get("operation").toString();
        communicate.setTag(str);

        communicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return view;
    }
}

