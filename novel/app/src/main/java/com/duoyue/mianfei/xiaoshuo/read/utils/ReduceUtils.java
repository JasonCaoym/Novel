package com.duoyue.mianfei.xiaoshuo.read.utils;

import com.duoyue.app.bean.*;

import java.util.*;


public class ReduceUtils {

    /**
     * 书城去重
     *
     * @param allData 原数据
     * @param list    新获取的数据
     * @return 新增的非重复书籍
     */
//    public static List<BookCityItemBean> booksCityToRepeat(List<Object> allData, final List<BookCityItemBean> list) {
//        List<BookCityItemBean> bookCityItemBeanList = new ArrayList<>();
//        for (int i = 0; i < allData.size(); i++) {
//            Object o = allData.get(i);
//            if (o instanceof BookCityListBean.BookOne2FourBean) {
//                List<BookCityItemBean> books = ((BookCityListBean.BookOne2FourBean) o).moduleBean.getBooks();
//                bookCityItemBeanList.addAll(books);
//            } else if (o instanceof BookCityListBean.BookThreeBean) {
//                List<BookCityItemBean> books = ((BookCityListBean.BookThreeBean) o).moduleBean.getBooks();
//                bookCityItemBeanList.addAll(books);
//            } else if (o instanceof BookCityListBean.BookOne2DoubleBean) {
//                List<BookCityItemBean> books = ((BookCityListBean.BookOne2DoubleBean) o).moduleBean.getBooks();
//                bookCityItemBeanList.addAll(books);
//            } else if (o instanceof BookCityItemBean) {
//                bookCityItemBeanList.add((BookCityItemBean) o);
//            }
//        }
//
//        List<Long> bookIds = new ArrayList<>();
//        for (int i = 0; i < bookCityItemBeanList.size(); i++) {
//            BookCityItemBean bookCityItemBean = bookCityItemBeanList.get(i);
//            bookIds.add(bookCityItemBean.getId());
//        }
//
//        for (int i = 0; i < list.size(); i++) {
//            BookCityItemBean bookCityItemBean = list.get(i);
//            if (bookIds.contains(bookCityItemBean.getId())) {
//                list.remove(i);
//                i--;
//                continue;
//            }
//        }
//
//        return list;
//    }

    /**
     * 书城更多书籍去重
     *
     * @param allData 原数据
     * @param list    新数据
     */
    public static List<Object> booksToRepeat(List<Object> allData, final List<Object> list) {
        List<BookCityItemBean> bookCityItemBeanList = new ArrayList<>();
        for (int i = 0; i < allData.size(); i++) {
            Object o = allData.get(i);
            if (o instanceof BookCityItemBean) {
                bookCityItemBeanList.add((BookCityItemBean) o);
            }
        }

        List<Long> bookIds = new ArrayList<>();
        for (int i = 0; i < bookCityItemBeanList.size(); i++) {
            BookCityItemBean bookCityItemBean = bookCityItemBeanList.get(i);
            bookIds.add(bookCityItemBean.getId());
        }

        for (int i = 0; i < list.size(); i++) {
            Object o = list.get(i);
            if (o instanceof BookCityItemBean && bookIds.contains(((BookCityItemBean) o).getId())) {
                list.remove(i);
                i--;
                continue;
            }
        }

        return list;
    }

    /**
     * 书城榜单书籍去重
     *
     * @param allData 原数据
     * @param list    新数据
     */
    public static List<Object> booksRankToRepeat(List<Object> allData, final List<Object> list) {

        List<BookRankItemBean> bookRankItemBeans = new ArrayList<>();
        for (int i = 0; i < allData.size(); i++) {
            Object o = allData.get(i);
            if (o instanceof BookRankItemBean) {
                bookRankItemBeans.add((BookRankItemBean) o);
            }
        }

        List<Long> bookIds = new ArrayList<>();
        for (int i = 0; i < bookRankItemBeans.size(); i++) {
            BookRankItemBean bookRankItemBean = bookRankItemBeans.get(i);
            bookIds.add(bookRankItemBean.getId());
        }

        for (int i = 0; i < list.size(); i++) {
            Object o = list.get(i);
            if (o instanceof BookRankItemBean && bookIds.contains(((BookRankItemBean) o).getId())) {
                list.remove(i);
                i--;
                continue;
            }
        }
        return list;
    }

    /**
     * 分类书籍去重
     *
     * @param allData 原数据
     * @param list    新数据
     */
    public static List<Object> booksCategoryToRepeat(List<Object> allData, final List<Object> list) {
        List<CategoryBookBean> categoryBookBeans = new ArrayList<>();
        for (int i = 0; i < allData.size(); i++) {
            Object o = allData.get(i);
            if (o instanceof CategoryBookBean) {
                categoryBookBeans.add((CategoryBookBean) o);
            }
        }

        List<Long> bookIds = new ArrayList<>();
        for (int i = 0; i < categoryBookBeans.size(); i++) {
            CategoryBookBean categoryBookBean = categoryBookBeans.get(i);
            bookIds.add(categoryBookBean.getBookId());
        }

        for (int i = 0; i < list.size(); i++) {
            Object o = list.get(i);
            if (o instanceof CategoryBookBean && bookIds.contains(((CategoryBookBean) o).getBookId())) {
                list.remove(i);
                i--;
                continue;
            }
        }


        return list;
    }

    /**
     * 搜索书籍去重
     *  @param allData 原数据
     * @param list    新数据
     */
    public static List<SearchResultBean> booksSearchToRepeat(List<SearchResultBean> allData, final List<SearchResultBean> list) {
        List<Integer> bookIds = new ArrayList<>();
        for (int i = 0; i < allData.size(); i++) {
            SearchResultBean searchResultBean = allData.get(i);
            bookIds.add(searchResultBean.getBookId());
        }

        for (int i = 0; i < list.size(); i++) {
            SearchResultBean searchResultBean = list.get(i);
            if (bookIds.contains(searchResultBean.getBookId())) {
                list.remove(i);
                i--;
                continue;
            }
        }

        return list;
    }
}
