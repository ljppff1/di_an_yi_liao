package com.dian.diabetes.activity.user;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * 类/接口注释
 * 
 * @author Chanjc@ifenguo.com
 * @createDate 2014年7月31日
 * 
 */
public class AreaDialogFragment extends DialogFragment {

    private OnListDialogItemSelect listener;
    private String[] list;
    private String title;

    public AreaDialogFragment(OnListDialogItemSelect listener, String[] list, String title) {
        this.listener = listener;
        this.list = list;
        this.title = title;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
            .setTitle(this.title)
            .setItems(list, new DialogInterface.OnClickListener() {
                
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listener.onListItemSelected(list[which]);
                    getDialog().dismiss();
                }
            })
            .create();

    }

    public interface OnListDialogItemSelect {
        public void onListItemSelected(String selection);
    }
}
