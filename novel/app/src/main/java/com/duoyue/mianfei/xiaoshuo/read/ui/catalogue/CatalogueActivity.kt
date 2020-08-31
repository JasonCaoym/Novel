package com.duoyue.mianfei.xiaoshuo.read.ui.catalogue

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import com.duoyue.app.bean.BookDetailBean
import com.duoyue.app.presenter.CataloguePresenter
import com.duoyue.app.ui.view.DirectoryDialog
import com.duoyue.mianfei.xiaoshuo.R
import com.duoyue.mianfei.xiaoshuo.read.common.ActivityHelper
import com.duoyue.mianfei.xiaoshuo.read.utils.BookRecordHelper
import com.duoyue.mod.stats.common.PageNameConstants
import com.zydm.base.data.dao.BookRecordBean
import com.zydm.base.data.dao.ChapterBean
import com.zydm.base.data.dao.ChapterListBean
import com.zydm.base.ext.onClick
import com.zydm.base.ext.setVisible
import com.zydm.base.presenter.view.ISimplePageView
import com.zydm.base.statistics.umeng.StatisHelper
import com.zydm.base.ui.activity.BaseActivity
import com.zydm.base.ui.item.AdapterBuilder
import com.zydm.base.ui.item.ItemListenerAdapter
import com.zydm.base.ui.item.RecyclerAdapter
import com.zydm.base.utils.ViewUtils
import com.zydm.base.widgets.PromptLayoutHelper
import com.zydm.base.widgets.refreshview.PullToRefreshLayout
import com.zzdm.ad.router.BaseData
import kotlinx.android.synthetic.main.activity_catalogue.*

class CatalogueActivity : BaseActivity(), ISimplePageView<ChapterListBean>, BookRecordHelper.RecordDaoObserver {


    private lateinit var mAdapter: RecyclerAdapter

    private lateinit var mBookDetailBean: BookDetailBean

    private var mGroupCount = 0

    private var mReadGroup: Int = 0

    private lateinit var mGroups: ArrayList<GroupItem>

//    private var mDialog: Dialog? = null

//    private lateinit var mWheelView: WheelView

    private lateinit var mPresenter: CataloguePresenter

    private var mReadChapterSeqNum: Int = 0

    protected var mPromptLayoutHelper: PromptLayoutHelper? = null

    protected var mPullLayout: PullToRefreshLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalogue)
        if (intent == null) return
        mBookDetailBean = intent.getParcelableExtra(BaseActivity.DATA_KEY)
        initView()
        BookRecordHelper.getsInstance().addObserver(this)
        mPresenter = CataloguePresenter(mReadChapterSeqNum, mBookDetailBean, this)
        StatisHelper.onEvent().detailCatalog(mBookDetailBean.bookName)
    }

    private fun initView() {
        setToolBarLayout(ViewUtils.getString(R.string.catalogue))
        mAdapter = AdapterBuilder()
            .putItemClass(ChapterListHolder::class.java, getItemListener())
            .builderRecyclerAdapter(this)
        list_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        list_view.adapter = mAdapter
        chapter_count.text = ViewUtils.getString(R.string.chapter_count, mBookDetailBean.lastChapter)
        initGroup()
        var viewGrop = findView<ViewGroup>(R.id.chapter_banner_ad)
        viewGrop!!.setVisible(false)
        /*if (!AdManager.getInstance().showAd(AdConstants.Position.CATALOGUE)) {
            return
        }
        viewGrop!!.setVisible(true)
        AdManager.getInstance().createAdSource(this).loadBannerAd(null, viewGrop, object : ADListener {
            override fun pull(originBean: AdOriginConfigBean?) {
            }

            override fun pullFailed(originBean: AdOriginConfigBean?) {
                viewGrop!!.setVisible(false)
            }

            override fun onShow(originConfigBean: AdOriginConfigBean) {
                viewGrop!!.setVisible(true)
            }

            override fun onClick(originConfigBean: AdOriginConfigBean) {
            }

            override fun onError(originConfigBean: AdOriginConfigBean, msg: String?) {
                chapter_banner_ad.removeAllViews()
                viewGrop!!.setVisible(false)
            }

            override fun onDismiss(originConfigBean: AdOriginConfigBean) {
                viewGrop!!.setVisible(false)
            }
        })*/
    }

    override fun getCurrPageId(): String {
        return PageNameConstants.CATALOGUE
    }

    private fun getItemListener(): ItemListenerAdapter<ChapterListHolder> {
        return object : ItemListenerAdapter<ChapterListHolder>() {
            override fun onClick(readBgHolder: ChapterListHolder, v: View) {
                val position = readBgHolder.mPosition
//                ReadActivity.gBookId = 0;
                ActivityHelper.gotoRead(
                    activity, mBookDetailBean.bookId, (readBgHolder.mItemData as ChapterBean).seqNum,
                    BaseData("目录"), PageNameConstants.CATALOGUE, ""
                )
            }
        }
    }

    private fun initGroup() {
        val chapterCount = mBookDetailBean.lastChapter
        if (chapterCount <= 50) {
            current_group.setVisible(false)
            return
        }
        current_group.setVisible(true);
        mGroupCount = if (chapterCount % 50 == 0) chapterCount / 50 else chapterCount / 50 + 1
        val readSeqNum = mReadChapterSeqNum
        mReadGroup = if (readSeqNum % 50 == 0) readSeqNum / 50 else readSeqNum / 50 + 1
        mGroups = ArrayList(mGroupCount)
        val remain = if (chapterCount % 50 == 0) 50 else chapterCount % 50;
        for (i in 0 until mGroupCount) {
            val groupItem = GroupItem(i)
            groupItem.name = ViewUtils.getString(
                R.string.chapter_group,
                i * 50 + 1,
                if (i == mGroupCount - 1) i * 50 + remain else i * 50 + 50
            )
            if (i == mReadGroup) {
                groupItem.isReadGroup = true
            }
            mGroups.add(groupItem)
        }
        current_group.text = ViewUtils.getString(R.string.chapter_group, mReadGroup * 50 + 1, mReadGroup * 50 + 50)
        current_group.onClick(this)
    }

    override fun isVisibleToUser(): Boolean {
        return true
    }

    override fun getActivity(): Activity {
        return this
    }

    protected fun getPromptLayoutHelper(): PromptLayoutHelper? {
        var helper: PromptLayoutHelper? = null
        if (mPullLayout != null) {
            helper = mPullLayout?.getPromptLayoutHelper()
        }
        if (helper != null) {
            return helper
        }
        val promptView = findViewById<View>(R.id.load_prompt_layout)

        if (mPromptLayoutHelper == null) {
            mPromptLayoutHelper = PromptLayoutHelper(promptView!!)
        }
        return mPromptLayoutHelper
    }

    override fun showLoading() {
        getPromptLayoutHelper()?.showLoading()
    }

    override fun dismissLoading() {
        getPromptLayoutHelper()?.hide()
    }

    override fun showEmpty() {
        getPromptLayoutHelper()?.showPrompt(PromptLayoutHelper.TYPE_DEFAULT_EMPTY, null)
    }

    override fun showNetworkError() {
        getPromptLayoutHelper()?.showPrompt(PromptLayoutHelper.TYPE_NO_NET,
            View.OnClickListener {
                mPresenter.requestChapter()
            })
    }

    override fun showForceUpdateFinish(result: Int) {
        mPullLayout?.refreshFinish(result)
    }

    override fun showLoadMoreFinish(result: Int) {
        mPullLayout?.loadMoreFinish(result)
    }

    override fun showPage(data: ChapterListBean) {
        val readSeqNum = mReadChapterSeqNum
        var select = 0
        if (readSeqNum >= data.list[0].seqNum && readSeqNum <= data.list[data.list.size - 1].seqNum) {
            for (index in 0 until data.list.size - 1) {
                if (readSeqNum == data.list[index].seqNum) {
                    data.list[index].isSelect = true
                    select = index
                }
            }
        }
        mAdapter.setData(data.list)
        mAdapter.notifyDataSetChanged()
        list_view.scrollToPosition(select)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.current_group -> showGroupDialog()
        }
    }

    private fun updateCatalogue(postion: Int) {
        current_group.text = ViewUtils.getString(R.string.chapter_range, postion * 50 + 1, postion * 50 + 50)
        mPresenter.setGroup(postion)
        mReadGroup = postion
    }

//    private fun showGroupDialog() {
//        if (mDialog == null) {
//            mDialog = Dialog(this, R.style.ActionSheetDialogStyle)
//        }
//        val content = LayoutInflater.from(this).inflate(R.layout.group_dialog_layout, null)
//        val corner = ViewUtils.dp2px(5.0f).toFloat()
//        val drawable = CornerDrawable()
//        drawable.setRadius(corner, corner, corner, corner, 0.0f, 0.0f, 0.0f, 0.0f)
//        drawable.setColor(ViewUtils.getColor(R.color.white))
//        content.background = drawable
//        content.confirm_btn.onClick(this)
//        mWheelView = content.findViewById<WheelView>(R.id.wheel_view)
//        mWheelView.viewAdapter = GroupWheelAdapter(mGroups, this)
//        mWheelView.currentItem = mReadGroup
//        mDialog!!.setContentView(content)
//        val dialogWindow = mDialog!!.window
//        dialogWindow.setGravity(Gravity.BOTTOM)
//        val lp = dialogWindow.attributes
//        lp.height = (ViewUtils.getPhonePixels()[1] / 2.5f).toInt()
//        lp.width = ViewUtils.getPhonePixels()[0]
//        dialogWindow.attributes = lp
//        mDialog!!.show()
//    }

    private fun showGroupDialog() {
        val dialog = DirectoryDialog()
        dialog.setData(mGroups)
        dialog.setOnItemClickListener { view, postion -> updateCatalogue(postion) }
        dialog.show(supportFragmentManager, "directory")
    }


/*    private fun getGroupItemListener(): ItemListener<GroupItemView>? {
        return object : ItemListener<GroupItemView> {
            override fun onClick(itemView: GroupItemView, view: View) {

            }
        }
    }*/

    override fun onRecordChange(recordBean: BookRecordBean?) {
        if (recordBean == null || mBookDetailBean.bookId != recordBean.bookId) {
            return
        }
        mReadChapterSeqNum = recordBean.seqNum
        mPresenter.requestChapter()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.destroy()
        BookRecordHelper.getsInstance().removeObserver(this)
    }

    data class GroupItem(var groupId: Int) {
        public var name: String = ""
        public var isReadGroup: Boolean = false
    }
}
