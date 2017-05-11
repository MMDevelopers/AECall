package com.magnetimarelli.aecall.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.magnetimarelli.aecall.R;
import com.magnetimarelli.aecall.adaptor.DialerAdaptor;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link Dialer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Dialer extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public EditText contactEdit = null;
    ImageButton backSpaceBtn = null, callBtn = null;
    GridView dialPad = null;
    DialerAdaptor dialPadAdaptor = null;
    public static final int REQUEST_PHONE_CALL = 1;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dialer, container, false);
        contactEdit = (EditText) view.findViewById(R.id.contact_edit);
        backSpaceBtn = (ImageButton) view.findViewById(R.id.backspace_btn);
        callBtn = (ImageButton) view.findViewById(R.id.call_button);
        dialPad = (GridView) view.findViewById(R.id.number_pad);
        dialPadAdaptor = new DialerAdaptor(getContext(), this);
        dialPad.setAdapter(dialPadAdaptor);
        backSpaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = contactEdit.getSelectionEnd();
                int index2 = contactEdit.getSelectionStart();
                if (index == 0 && index2 == 0) {
                    contactEdit.setSelection(contactEdit.getText().length());
                    contactEdit.setCursorVisible(true);
                }
                contactEdit.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            }
        });
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactEdit.setCursorVisible(false);
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + contactEdit.getText()));
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                    return;
                }
                getContext().startActivity(intent);
            }
        });
        contactEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactEdit.setCursorVisible(true);
            }
        });
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + contactEdit.getText()));
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    getContext().startActivity(intent);
                }
                return;
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public Dialer() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Dialer.
     */
    // TODO: Rename and change types and number of parameters
    public static Dialer newInstance(String param1, String param2) {
        Dialer fragment = new Dialer();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
}
