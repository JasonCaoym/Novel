package com.zydm.base.data.net

import com.zydm.base.common.BaseApplication
import com.zydm.base.utils.SPUtils
import com.zydm.base.utils.StringUtils

object DomainConfig {
    /*private val SEARCH_URL_CONST_FOLDER = "/Api/Search"

    private val BASE_DOMAIN_NAME = "ebk.cm233.com"

    private val DEFAULT = arrayOf(
            "http://test.$BASE_DOMAIN_NAME",
            "http://$BASE_DOMAIN_NAME")

    private val SEARCH = arrayOf(
            "http://search.test.$BASE_DOMAIN_NAME",
            "http://search.$BASE_DOMAIN_NAME")

    private val STATISTICS = arrayOf(
            "http://statistics-test-ebk$BASE_DOMAIN_NAME",
            "http://statistics.$BASE_DOMAIN_NAME")

    private val H5_PAGE = arrayOf(
            "http://m.test.$BASE_DOMAIN_NAME",
            "http://m.$BASE_DOMAIN_NAME")*/

    //======================爱告预发布环境====================
    /*private val BASE_DOMAIN_NAME = "iadmob.com"

    private val DEFAULT = arrayOf(
        "http://test-ebk.$BASE_DOMAIN_NAME",
        "http://$BASE_DOMAIN_NAME")

    private val SEARCH = arrayOf(
        "http://search-test-ebk.$BASE_DOMAIN_NAME",
        "http://search.$BASE_DOMAIN_NAME")

    private val STATISTICS = arrayOf(
        "http://statistics-test-ebk.$BASE_DOMAIN_NAME",
        "http://statistics.$BASE_DOMAIN_NAME")

    private val H5_PAGE = arrayOf(
        "http://m-test-$BASE_DOMAIN_NAME",
        "http://m.$BASE_DOMAIN_NAME")*/


    //======================爱告正式环境====================
    private val BASE_DOMAIN_NAME = "yoko66.com"

    private val DEFAULT = arrayOf(
        "http://ebk.$BASE_DOMAIN_NAME",
        "http://ebk.$BASE_DOMAIN_NAME")

    private val SEARCH = arrayOf(
        "http://search-ebk.$BASE_DOMAIN_NAME",
        "http://search-ebk.$BASE_DOMAIN_NAME")

    private val STATISTICS = arrayOf(
        "http://statistics-ebk.$BASE_DOMAIN_NAME",
        "http://statistics-ebk.$BASE_DOMAIN_NAME")

    private val H5_PAGE = arrayOf(
        "http://ebk.$BASE_DOMAIN_NAME",
        "http://ebk.$BASE_DOMAIN_NAME")



    private val ALL_DOMAIN = arrayOf(
            DEFAULT,
            SEARCH,
            STATISTICS,
            H5_PAGE)

    private val KEY_BY_DOMAIN = arrayOf(
            "domain_name_default",
            "domain_name_search",
            "domain_name_statistics",
            "domain_name_h5_page")

    private val sCurDomain = arrayOfNulls<String>(ALL_DOMAIN.size)

//    fun fullUrl(urlConst: String): String {
//        var type = DomainType.DEFAULT
//        if (urlConst.startsWith(SEARCH_URL_CONST_FOLDER)) {
//            type = DomainType.SEARCH
//            //        } else if (urlConst.equals(ActionUrlConst.SPREAD_ACTIVATE) || urlConst.equals(DataUrlConst.DEVICE_SIGN_URL)) {
//            //            type = DomainType.STATISTICS;
//        }
//        return getDomainName(type) + urlConst
//    }

    /**
     * @return
     */
    fun getDomainName(type: Int): String {
        if (StringUtils.isBlank(sCurDomain[type])) {
            sCurDomain[type] = SPUtils.getString(KEY_BY_DOMAIN[type])
            if (StringUtils.isBlank(sCurDomain[type])) {
                val envIndex = if (BaseApplication.context.isTestEnv()) 0 else 1
                sCurDomain[type] = ALL_DOMAIN[type][envIndex]
            }
        }
        return sCurDomain[type]!!
    }

    fun getAboutH5(): String
    {
        //return getDomainName(DomainType.H5_PAGE) + "/about"
        return "http://dl.dwz0.net/app_novel/xieyi.html";
    }

    /*-------------------------------------------------开发者模式专用-------------------------------------------------*/
    fun getStaticDomain(type: Int, isTestEnv: Boolean): String {
        val envIndex = if (isTestEnv) 0 else 1
        return ALL_DOMAIN[type][envIndex]
    }

    fun setDomains(api: String, search: String, statistics: String, h5: String) {
        sCurDomain[DomainType.DEFAULT] = api
        sCurDomain[DomainType.SEARCH] = search
        sCurDomain[DomainType.STATISTICS] = statistics
        sCurDomain[DomainType.H5_PAGE] = h5
        for (i in KEY_BY_DOMAIN.indices) {
            SPUtils.putString(KEY_BY_DOMAIN[i], sCurDomain[i]!!)
        }
    }

}
