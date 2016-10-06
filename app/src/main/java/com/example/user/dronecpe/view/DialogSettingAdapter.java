package com.example.user.dronecpe.view;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.user.dronecpe.R;
import com.example.user.dronecpe.model.SettingModel;

import java.util.List;

/**
 * Created by Dev on 4/10/2559.
 */

public class DialogSettingAdapter extends RecyclerView.Adapter<DialogSettingAdapter.ViewHolder> {

    private Context context;
    private List<SettingModel> list;

    public DialogSettingAdapter(Context context,List<SettingModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public Object getItemObject(int position){
        return list.get(Math.min(list.size(),position));
    }

    /**
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_dialog_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SettingModel settingModel = list.get(position);
        holder.mEditText.setText(settingModel.getTextValue());
        holder.mEditText.addTextChangedListener(onTextWatcher(position));
        holder.txtTitle.setText(settingModel.getTitle());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private TextWatcher onTextWatcher(int position){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                SettingModel settingModel = (SettingModel) getItemObject(position);
                settingModel.setTextValue(charSequence.toString());
                list.set(position,settingModel);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    /**
     * ViewHolder
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        EditText mEditText;

        ViewHolder(View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            mEditText = (EditText) itemView.findViewById(R.id.editText);
        }
    }
}
