package com.zydm.base.ext

fun ArrayList<Any>.addSubListNotEmpty(list: List<*>, start: Int, end: Int): Boolean {
    val subList = list.subList1(start, end)
    if (subList.isEmpty()) {
        return false
    }
    this.add(subList)
    return true
}

fun <E> List<E>.subList1(start: Int, end: Int): List<E>  {
    val result = ArrayList<E>()
    for (i in start until end) {
        if (i < this.size) {
            result.add(this[i])
        } else {
            break
        }
    }
    return result
}

fun <E> ArrayList<E>.addSingleTop(value: E) {
    remove(value)
    add(0, value)
}

fun ArrayList<out Any>.trimToSize(size: Int) {
    while (this.size > size) {
        removeAt(this.size - 1)
    }
}