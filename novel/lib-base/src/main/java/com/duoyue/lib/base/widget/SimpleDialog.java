package com.duoyue.lib.base.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import com.zydm.base.R;

public class SimpleDialog extends Dialog {

    private static final String TAG = "base#SimpleDialog";

    public SimpleDialog(Context context) {
        super(context);
    }

    public SimpleDialog(Context context, int theme) {
        super(context, theme);
    }


    public static class Builder {
        private Context context; // 上下文对象
        private String title; // 对话框标题
        private String message; // 对话框内容
        private int messageLines;//对话框内容显示行数
        private int messageGravity;//对话框内容居中方式
        private String confirm_btnText; // 按钮名称“确定”
        private String cancel_btnText; // 按钮名称“取消”
        private View contentView; // 对话框中间加载的其他布局界面
        private SimpleDialog dialog = null;
        /* 按钮监听事件 */
        private DialogInterface.OnClickListener confirm_btnClickListener;
        private DialogInterface.OnClickListener cancel_btnClickListener;

        public Builder(Context context) {
            this.context = context;
            dialog = new SimpleDialog(context, R.style.Dialog);
        }

        public Builder setCancelable(boolean flag) {
            dialog.setCancelable(flag);
            return this;
        }

        ;

        public Builder setCanceledOnTouchOutside(boolean cancel) {
            dialog.setCanceledOnTouchOutside(cancel);
            return this;
        }

        ;

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
         * 设置内容提示显示行数.
         * @param line 行数(-1:不限制)
         */
        public Builder setMessageMaxLines(int line)
        {
            messageLines = line;
            return this;
        }

        /**
         * 设置内容提示居中方式
         * @param gravity
         * @return
         */
        public Builder setMessageGravity(int gravity)
        {
            messageGravity = gravity;
            return this;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * 设置对话框界面
         *
         * @param v View
         * @return
         */
        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param confirm_btnText
         * @return
         */
        public Builder setPositiveButton(int confirm_btnText,
                                         DialogInterface.OnClickListener listener) {
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
                                         DialogInterface.OnClickListener listener) {
            this.confirm_btnText = confirm_btnText;
            this.confirm_btnClickListener = listener;
            return this;
        }

        /**
         * Set the negative button resource and it's listener
         */
        public Builder setNegativeButton(int cancel_btnText,
                                         DialogInterface.OnClickListener listener) {
            this.cancel_btnText = (String) context.getText(cancel_btnText);
            this.cancel_btnClickListener = listener;
            return this;
        }

        /**
         * Set the negative button and it's listener
         */
        public Builder setNegativeButton(String cancel_btnText,
                                         DialogInterface.OnClickListener listener) {
            this.cancel_btnText = cancel_btnText;
            this.cancel_btnClickListener = listener;
            return this;
        }

        public SimpleDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            View layout = inflater.inflate(R.layout.dialog_simple, null);
            dialog.addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

            // set the dialog title

            if (title == null || title.trim().length() == 0) {
                layout.findViewById(R.id.dialog_simple_title).setVisibility(View.GONE);
            } else {
                ((TextView) layout.findViewById(R.id.dialog_simple_title)).setText(title);
            }

            // set the confirm button
            if (confirm_btnText != null) {
                ((TextView) layout.findViewById(R.id.dialog_simple_positive)).setText(confirm_btnText);
                if (confirm_btnClickListener != null) {
                    layout.findViewById(R.id.dialog_simple_positive)
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    confirm_btnClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                } else {
                    layout.findViewById(R.id.dialog_simple_positive)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                }
            } else {
                layout.findViewById(R.id.dialog_simple_positive).setVisibility(View.GONE);
                layout.findViewById(R.id.dialog_simple_line_vertical).setVisibility(View.GONE);
            }
            // set the cancel button
            if (cancel_btnText != null) {
                ((TextView) layout.findViewById(R.id.dialog_simple_negative)).setText(cancel_btnText);
                if (cancel_btnClickListener != null) {
                    layout.findViewById(R.id.dialog_simple_negative)
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    cancel_btnClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                } else {
                    layout.findViewById(R.id.dialog_simple_negative)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                }
            } else {
                // if no cancel button just set the visibility to GONE
                layout.findViewById(R.id.dialog_simple_negative).setVisibility(View.GONE);
                layout.findViewById(R.id.dialog_simple_line_vertical).setVisibility(View.GONE);
            }

            if (confirm_btnText == null && cancel_btnText != null) {
                layout.findViewById(R.id.dialog_simple_line).setVisibility(View.GONE);
            }

            // set the content message
            if (message != null) {
                TextView messageTextView = layout.findViewById(R.id.dialog_simple_message);
                //设置显示行数.
                if (messageLines > 0)
                {
                    messageTextView.setMaxLines(messageLines);
                }
                //这是居中方式.
                if (messageGravity > 0)
                {
                    messageTextView.setGravity(messageGravity);
                }
                //设置显示内容.
                messageTextView.setText(message);
            } else {
                layout.findViewById(R.id.dialog_simple_message).setVisibility(View.GONE);
            }

            dialog.setContentView(layout);
            return dialog;
        }

    }
}
