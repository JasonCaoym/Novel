package com.zydm.base.utils

import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import com.zydm.base.R
import com.zydm.base.common.BaseApplication.Companion.context

object ParagraphUtils {

    open fun convertChineseParagraphStyle(content: String, lineHeight: Int, paragraphSpacing: Int, lineSpacingExtra: Int): CharSequence {
        if (!content.contains("\n")) {
            return content
        }
        var newContent = content.replace("\n", "\n\r        ")
        newContent = "        ".plus(newContent)
        val previousIndex = newContent.indexOf("\n\r")
        val nextParagraphBeginIndexes = ArrayList<Int>()
        nextParagraphBeginIndexes.add(previousIndex)
        while (previousIndex != -1) {
            val nextIndex = newContent.indexOf("\n\r", previousIndex + 2)
            if (nextIndex != -1) {
                nextParagraphBeginIndexes.add(nextIndex)
            }
        }
        val lineHeight: Int = lineHeight
        val spanString = SpannableString(newContent)
        val drawable = ContextCompat.getDrawable(context.globalContext, R.drawable.paragraph_space);
        val density = context.globalContext.resources.displayMetrics.density;
        drawable?.setBounds(0, 0, 1, (((lineHeight - lineSpacingExtra * density) / 1.2 + (paragraphSpacing - lineSpacingExtra) * density).toInt()));
        for (index in nextParagraphBeginIndexes) {
            spanString.setSpan(ImageSpan(drawable), index + 1, index + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spanString
    }
}