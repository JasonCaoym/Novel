package com.zydm.base.utils;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.text.*;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.widget.TextView;
import com.zydm.base.R;
import com.zydm.base.common.Constants;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;

public class StringUtils {

    private static DecimalFormat DECIMAL_ONE_FORMAT = new DecimalFormat(".#");

    private static DecimalFormat DECIMAL_TWO_FORMAT = new DecimalFormat(".00");//格式化小数

    public static boolean isBlank(String str) {
        return (str == null || str.trim().length() == 0);
    }

    public static boolean isEmpty(CharSequence str) {
        return (str == null || str.length() == 0);
    }

    public static int length(CharSequence str) {
        return str == null ? 0 : str.length();
    }

    public static boolean equalsIgnoreCase(String str, String str2) {
        if (str == null) {
            return str2 == null;
        }
        return str.equalsIgnoreCase(str2);
    }

    public static String getRandomStr(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; ++i) {
            int number = random.nextInt(str.length());

            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static String getRandomStr() {
        return getRandomStr(16);
    }

    /**
     * 100,000   加逗号的格式化
     *
     * @param num
     * @return
     */
    public static String numberFormat(int num) {
        NumberFormat cFormat = NumberFormat.getCurrencyInstance();
        cFormat.setMaximumFractionDigits(0);
        return cFormat.format(num).substring(1);
    }

    public static String numFormat(int num) {
        if (num < Constants.TEN_THOUSAND) {
            return num + Constants.EMPTY;
        } else if (num < Constants.HUNDRED_MILLION) {
            return DECIMAL_TWO_FORMAT.format(num / Constants.TEN_THOUSAND) + Constants.MONEY_W;
        } else {
            return DECIMAL_TWO_FORMAT.format(num / Constants.HUNDRED_MILLION) + Constants.UNIT_YI;
        }
    }

    public static String numberFormatF2(double num) {
        NumberFormat cFormat = NumberFormat.getCurrencyInstance();
        cFormat.setMaximumFractionDigits(2);
        return cFormat.format(num).substring(1);
    }

    public static String moneyFormatMdou(int mdou) {
        double m = mdou;
        if (mdou < Constants.TEN_THOUSAND) {
            return mdou + Constants.EMPTY;
        } else if (mdou < Constants.HUNDRED_MILLION) {
            return DECIMAL_ONE_FORMAT.format(m / Constants.TEN_THOUSAND) + Constants.MONEY_W;
        } else {
            return DECIMAL_ONE_FORMAT.format(m / Constants.HUNDRED_MILLION) + Constants.UNIT_YI;
        }
    }

    public static String moneyFormatMdouContainThousand(int mdou) {
        double m = mdou;
        if (mdou < Constants.THOUSAND) {
            return mdou + Constants.EMPTY;
        } else if (mdou < Constants.TEN_THOUSAND) {
            return DECIMAL_ONE_FORMAT.format(m / Constants.THOUSAND) + Constants.MONEY_K;
        } else if (mdou < Constants.HUNDRED_MILLION) {
            return DECIMAL_ONE_FORMAT.format(m / Constants.TEN_THOUSAND) + Constants.MONEY_W;
        } else {
            return DECIMAL_ONE_FORMAT.format(m / Constants.HUNDRED_MILLION) + Constants.UNIT_YI;
        }
    }

    public static void setAutoSplit(final TextView textView) {
        if (null == textView) {
            return;
        }
        textView.setGravity(Gravity.LEFT);
        textView.post(new Runnable() {
            @Override
            public void run() {
                final String rawText = textView.getText().toString(); //原始文本
                final Paint tvPaint = textView.getPaint(); //paint，包含字体等信息
                final float tvWidth = textView.getWidth() - textView.getPaddingLeft() - textView.getPaddingRight(); //控件可用宽度

                LogUtils.d("textTest", "tvWidth : " + tvWidth);
                LogUtils.d("textTest", "rawText : " + rawText);

                //将原始文本按行拆分
                String[] rawTextLines = rawText.replaceAll("\r", "").split("\n");
                StringBuilder sbNewText = new StringBuilder();
                for (String rawTextLine : rawTextLines) {
                    LogUtils.d("textTest", "rawTextLine : " + rawTextLine);
                    if (tvPaint.measureText(rawTextLine) <= tvWidth) {
                        //如果整行宽度在控件可用宽度之内，就不处理了
                        sbNewText.append(rawTextLine);
                    } else {
                        //如果整行宽度超过控件可用宽度，则按字符测量，在超过可用宽度的前一个字符处手动换行
                        float lineWidth = 0;
                        for (int cnt = 0; cnt < rawTextLine.length(); cnt++) {
                            char ch = rawTextLine.charAt(cnt);

                            lineWidth += tvPaint.measureText(String.valueOf(ch));
                            if (lineWidth <= tvWidth) {
                                sbNewText.append(ch);
                            } else {
                                lineWidth = 0;
                                sbNewText.append("\n");
                                sbNewText.append(Constants.BLANK_THREE);
                                sbNewText.append(ch);
                                lineWidth += tvPaint.measureText(Constants.BLANK_THREE);
                            }
                        }
                    }
                    sbNewText.append("\n");
                }

                //把结尾多余的\n去掉
                if (!rawText.endsWith("\n")) {
                    sbNewText.deleteCharAt(sbNewText.length() - 1);
                }

                LogUtils.d("textTest", "sbNewText : " + sbNewText);
                textView.setText(sbNewText);
            }
        });
    }


    public static int parseInt(String s) {
        if (StringUtils.isBlank(s)) {
            return 0;
        }

        try {
            int i = Integer.parseInt(s);
            return i;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getString(String str) {
        return str == null ? Constants.EMPTY : str;
    }

    public static String getString(String str, @NonNull String defStr) {
        return StringUtils.isBlank(str) ? defStr : str;
    }

    /**
     * @param msg         输入的待处理的字符串
     * @param lengthLimit 要求转换的字符串的最大长度
     * @return 如果输入的字符串的长度大于长度的上限，则省略中间字符并用“……”代替
     */
    public static String getSubStr(String msg, int lengthLimit) {
        if (isBlank(msg)) {
            return Constants.EMPTY;
        }
        if (msg.length() <= lengthLimit) {
            return msg;
        }
        int length = (int) Math.floor(lengthLimit / 2) - 1;
        String preStr = msg.substring(0, length);
        String lastStr = msg.substring(msg.length() - length);
        StringBuilder builder = new StringBuilder();
        builder.append(preStr).append(Constants.ELLIPSIS).append(lastStr);
        return builder.toString();
    }

    public static boolean equalsExcludeNull(CharSequence a, CharSequence b) {
        if (StringUtils.isEmpty(a) || StringUtils.isEmpty(b)) {
            return false;
        }
        String a1 = a.toString().trim();
        String b1 = b.toString().trim();
        if (StringUtils.isBlank(a1) || StringUtils.isBlank(b1)) {
            return false;
        }
        return TextUtils.equals(a1, b1);
    }

    public static boolean differsExcludeNull(CharSequence a, CharSequence b) {
        if (StringUtils.isEmpty(a) || StringUtils.isEmpty(b)) {
            return false;
        }
        String a1 = a.toString().trim();
        String b1 = b.toString().trim();
        if (StringUtils.isBlank(a1) || StringUtils.isBlank(b1)) {
            return false;
        }
        return !TextUtils.equals(a1, b1);
    }

    /**
     * 字符串处理： 暴烈<font color='red'>少女</font>   --->   暴烈少女
     * @param bookName
     * @return
     */
    public static String resetBookName(String bookName) {
        String newBookName = bookName;
        int start = newBookName.indexOf("<");
        while (start >= 0) {
            int end = newBookName.indexOf(">");
            if (end > start) {
                newBookName = newBookName.replace(newBookName.substring(start, end+1), "");
                start = newBookName.indexOf("<");
            } else {
                break;
            }
        }
        return newBookName;
    }

    public static String asString(Object obj) {
        return obj == null ? Constants.EMPTY : obj.toString();
    }

    public static SpannableString getHighlightText(CharSequence text, int start, int length, int color) {
        SpannableString ss = new SpannableString(text);
        ss.setSpan(new ForegroundColorSpan(color), start, start + length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    /**
     * @param text
     * @param highLightArgs it looks like ArrayList{[colorResId,start,length],[colorResId,start,length]}>
     * @return
     */
    public static SpannableString getMultiHighLightText(CharSequence text,
                                                        ArrayList<Integer[]> highLightArgs) {
        SpannableString ss = new SpannableString(text);
        for (Integer[] args : highLightArgs) {
            int color = args[0];
            int start = args[1];
            int length = args[2];
            ss.setSpan(new ForegroundColorSpan(color), start, start + length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return ss;
    }

    public static String formatUnixTime(long unixTime) {
        long millisTimeStamp = unixTime * Constants.SECOND_1;
        long curTimeStamp = System.currentTimeMillis();
        String timeFormat = "";
        long gapTime = curTimeStamp - millisTimeStamp;
        if (gapTime > -Constants.MINUTE_1 && gapTime < Constants.DAY_1) {
            if (gapTime < Constants.MINUTE_1) {
                timeFormat = ViewUtils.getResources().getString(R.string.post_time_now);
            } else if (gapTime < Constants.HOUR_1) {
                timeFormat = ViewUtils.getResources().getString(R.string.post_time_minute,
                        gapTime / Constants.MINUTE_1);
            } else {
                timeFormat = ViewUtils.getResources()
                        .getString(R.string.post_time_hour, gapTime / Constants.HOUR_1);
            }
        } else {
//            Calendar calendar = Calendar.getInstance();
//            int curYear = calendar.get(Calendar.YEAR);
//            calendar.setTimeInMillis(millisTimeStamp);
//            if (calendar.get(Calendar.YEAR) == curYear) {
//                timeFormat = MD_FORMAT.format(identity_new Date(millisTimeStamp));
//            } else {
//                timeFormat = YMD_FORMAT.format(identity_new Date(millisTimeStamp));
//            }
            timeFormat = TimeUtils.formatDate(unixTime);
        }
        return timeFormat;
    }

    public static String getPercentage(int cur, int total) {
        String baifenbi = "";// 接受百分比的值
        double baiy = cur * 1.0;
        double baiz = total * 1.0;
        double fen = baiy / baiz;
        DecimalFormat df1 = new DecimalFormat("##.00%"); // ##.00%
        baifenbi = df1.format(fen);
        return baifenbi;
    }

    public static String htmlToString(String html) {
        Spanned fromHtml = Html.fromHtml(html);
        return fromHtml.toString();
    }
}
