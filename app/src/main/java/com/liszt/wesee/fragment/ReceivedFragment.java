package com.liszt.wesee.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liszt.wesee.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReceivedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReceivedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReceivedFragment extends Fragment {
    private View rootView;
    private static ReceivedFragment receivedFragment;
    public ReceivedFragment(){}
    public static ReceivedFragment getNewInstance(){
        if (receivedFragment ==null){
            receivedFragment =new ReceivedFragment();
        }
        return receivedFragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_received, container, false);
        }
        // 缓存的rootView需要判断是否已经被加过parent，
        // 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }

        return rootView;
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // The size for this PublisherAdView is defined in the XML layout as AdSize.FLUID. It could
        // also be set here by calling publisherAdView.setAdSizes(AdSize.FLUID).
        //
        // An ad with fluid size will automatically stretch or shrink to fit the height of its
        // content, which can help layout designers cut down on excess whitespace.



    }
    @Override
    public void onResume() {
        super.onResume();
    }
}

