package com.xuzhiguang.xzglibrary.http

import rx.Subscriber

open class MySubscriber<T> : Subscriber<T>() {
    override fun onNext(t: T?) {
    }

    override fun onCompleted() {
    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
        e.message?.also { NetErrorToastHelper.newInstance().selectWitch(it) }
    }
}