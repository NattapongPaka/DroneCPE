package com.example.user.dronecpe.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.user.dronecpe.R;
import com.example.user.dronecpe.model.SettingModel;
import com.example.user.dronecpe.preference.UtilPreference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Dev on 3/10/2559.
 */

public class DialogSetting extends AppCompatDialogFragment {

    private RecyclerView mRecyclerView;
    private DialogSettingAdapter mDialogSettingAdapter;
    private Button btnOK;
    public static final String TAG = DialogSetting.class.getSimpleName();

    public static final String HOST_IP_ID = "0";
    public static final String HOST_PORT_ID = "1";
    public static final String CAMERA_IP_ID = "2";
    public static final String CAMERA_PORT_ID = "3";

    public IDialogSetting iDialogSetting;

    public interface IDialogSetting{
        void onSettingSuccess();
    }

    public void setOnDialogSettingClickListener(IDialogSetting iDialogSetting){
        this.iDialogSetting = iDialogSetting;
    }

    public static DialogSetting newInstance() {
        return new DialogSetting();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.layout_dialog_setting, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);
        btnOK = (Button) v.findViewById(R.id.btnOKSetting);
        btnOK.setOnClickListener(onClickListener);
        Log.d(TAG, "onCreateView: ");
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
        List<SettingModel> settingModels = new ArrayList<>();
        Map<String, ?> mapSettingModel = UtilPreference.getInstance().getAllSharedPreference();
        if (mapSettingModel != null && !mapSettingModel.isEmpty()) {
            for (int i = 0; i < mapSettingModel.size(); i++) {
                if (mapSettingModel.containsKey(String.valueOf(i))) {
                    String result = (String) mapSettingModel.get(String.valueOf(i));
                    SettingModel settingModel = new Gson().fromJson(result, SettingModel.class);
                    settingModels.add(new SettingModel(String.valueOf(i), settingModel.getTitle(), settingModel.getTextValue()));
                }
            }
        } else {
            settingModels.add(new SettingModel(HOST_IP_ID, "Host IP", ""));
            settingModels.add(new SettingModel(HOST_PORT_ID, "Host Port", ""));
            settingModels.add(new SettingModel(CAMERA_IP_ID, "Camera IP", ""));
            settingModels.add(new SettingModel(CAMERA_PORT_ID, "Camera Port", ""));
        }

        mDialogSettingAdapter = new DialogSettingAdapter(getActivity(), settingModels);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(mDialogSettingAdapter);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mDialogSettingAdapter != null) {
                for (int i = 0; i < mDialogSettingAdapter.getItemCount(); i++) {
                    SettingModel settingModel = (SettingModel) mDialogSettingAdapter.getItemObject(i);
                    String settingObject = new Gson().toJson(settingModel);
                    UtilPreference.getInstance().setSharedPreference(settingModel.getId(), settingObject);
                    Log.d(TAG, "onClick : " + settingObject);
                }
            }
            if(iDialogSetting != null){
                iDialogSetting.onSettingSuccess();
            }
            dismiss();
        }
    };


}