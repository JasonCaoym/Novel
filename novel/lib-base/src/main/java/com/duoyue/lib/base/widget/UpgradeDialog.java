package com.duoyue.lib.base.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import com.zydm.base.R;

public class UpgradeDialog extends Dialog {

    private static final String TAG = "base#UpgradeDialog";

    public UpgradeDialog(Context context) {
        super(context);
    }

    public UpgradeDialog(Context context, int theme) {
        super(context, theme);
    }


    public static class Builder {
        private Context context; // 上下文对象
        private String version; // 对话框标题
        private String message; // 对话框内容
        private String size; // app size
        private String confirm_btnText; // 按钮名称“确定”
        private String cancel_btnText; // 按钮名称“取消”
        private UpgradeDialog dialog = null;
        /* 按钮监听事件 */
        private OnClickListener confirm_btnClickListener;
        private OnClickListener cancel_btnClickListener;

        public Builder(Context context) {
            this.context = context;
            dialog = new UpgradeDialog(context, R.style.Dialog);
        }

        public void setCancelable(boolean flag) {
            dialog.setCancelable(flag);
        }


        public void setCanceledOnTouchOutside(boolean cancel) {
            dialog.setCanceledOnTouchOutside(cancel);
        }


        /* 设置对话框信息 */
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param version
         * @return
         */
        public Builder setVersion(String version) {
            this.version = version;
            return this;
        }

        public Builder setSize(String size) {
            this.size = size;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param confirm_btnText
         * @return
         */
        public Builder setPositiveButton(int confirm_btnText,
                                         OnClickListener listener) {
            this.confirm_btnText = (String) context.getText(confirm_btnText);
            this.confirm_btnClickListener = listener;
            return this;
        }

        /**
         * Set the positive button and it's listener
         *
         * @param confirm_btnText
         * @return
         */
        public Builder setPositiveButton(String confirm_btnText,
                                         OnClickListener listener) {
            this.confirm_btnText = confirm_btnText;
            this.confirm_btnClickListener = listener;
            return this;
        }

        /**
         * Set the negative button resource and it's listener
         */
        public Builder setNegativeButton(int cancel_btnText,
                                         OnClickListener listener) {
            this.cancel_btnText = (String) context.getText(cancel_btnText);
            this.cancel_btnClickListener = listener;
            return this;
        }

        /**
         * Set the negative button and it's listener
         */
        public Builder setNegativeButton(String cancel_btnText,
                                         OnClickListener listener) {
            this.cancel_btnText = cancel_btnText;
            this.cancel_btnClickListener = listener;
            return this;
        }

        public UpgradeDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            View layout = inflater.inflate(R.layout.dialog_upgrade, null);
            dialog.addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

            // set the dialog title

            if (version == null || version.trim().length() == 0) {
                layout.findViewById(R.id.dialog_upgrade_version).setVisibility(View.GONE);
            } else {
                ((TextView) layout.findViewById(R.id.dialog_upgrade_version)).setText(version);
            }

            // set the confirm button
            if (confirm_btnText != null) {
                ((TextView) layout.findViewById(R.id.dialog_upgrade_positive)).setText(confirm_btnText);
                if (confirm_btnClickListener != null) {
                    layout.findViewById(R.id.dialog_upgrade_positive)
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    confirm_btnClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                } else {
                    layout.findViewById(R.id.dialog_upgrade_positive)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                }
            } else {
                layout.findViewById(R.id.dialog_upgrade_positive).setVisibility(View.GONE);
                layout.findViewById(R.id.dialog_upgrade_divider).setVisibility(View.GONE);
            }
            // set the cancel button
            if (cancel_btnText != null) {
                ((TextView) layout.findViewById(R.id.dialog_upgrade_negative)).setText(cancel_btnText);
                if (cancel_btnClickListener != null) {
                    layout.findViewById(R.id.dialog_upgrade_negative)
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    cancel_btnClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                } else {
                    layout.findViewById(R.id.dialog_upgrade_negative)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                }
            } else {
                // if no cancel button just set the visibility to GONE
                layout.findViewById(R.id.dialog_upgrade_negative).setVisibility(View.GONE);
                layout.findViewById(R.id.dialog_upgrade_divider).setVisibility(View.GONE);
            }

            // set the content message
            if (message != null) {
                ((TextView) layout.findViewById(R.id.dialog_upgrade_message)).setText(message);
            } else {
                layout.findViewById(R.id.dialog_upgrade_message).setVisibility(View.GONE);
            }

            if (size != null) {
                ((TextView) layout.findViewById(R.id.dialog_upgrade_size)).setText(size + "M");
            } else {
                layout.findViewById(R.id.dialog_upgrade_size).setVisibility(View.GONE);
            }

            dialog.setContentView(layout);
            return dialog;
        }

    }
}
