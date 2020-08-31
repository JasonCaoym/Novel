package com.zydm.base.data.bean

import android.os.Parcel
import android.os.Parcelable

class BookDetailBean() : Parcelable {

    var bookId: String = ""
    var bookName = ""
    var bookCover = ""
    var resume = ""
    var chapterCount = 0
    var wordCount: Long = 0
    var isFinish = false
    var updateTime: Long = 0
    var currTime: Long = 0
    var author = ""
    var mReadChapter: CharSequence = ""
    var mReadChapterTitle = ""
    var mReadChapterSeqNum = 1
    var mIsInShelf: Boolean = false

    constructor(parcel: Parcel) : this() {
        bookId = parcel.readString()
        bookName = parcel.readString()
        bookCover = parcel.readString()
        resume = parcel.readString()
        chapterCount = parcel.readInt()
        wordCount = parcel.readLong()
        isFinish = parcel.readByte() != 0.toByte()
        updateTime = parcel.readLong()
        currTime = parcel.readLong()
        author = parcel.readString()
        mReadChapter = parcel.readString()
        mReadChapterTitle = parcel.readString()
        mReadChapterSeqNum = parcel.readInt()
        mIsInShelf = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(bookId)
        parcel.writeString(bookName)
        parcel.writeString(bookCover)
        parcel.writeString(resume)
        parcel.writeInt(chapterCount)
        parcel.writeLong(wordCount)
        parcel.writeByte(if (isFinish) 1 else 0)
        parcel.writeLong(updateTime)
        parcel.writeLong(currTime)
        parcel.writeString(author)
        parcel.writeString(mReadChapter.toString())
        parcel.writeString(mReadChapterTitle)
        parcel.writeInt(mReadChapterSeqNum)
        parcel.writeByte(if (mIsInShelf) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BookDetailBean> {
        override fun createFromParcel(parcel: Parcel): BookDetailBean {
            return BookDetailBean(parcel)
        }

        override fun newArray(size: Int): Array<BookDetailBean?> {
            return arrayOfNulls(size)
        }
    }
}

