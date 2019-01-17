package com.xuzhiguang.xzglibrary.rx


import rx.Observable
import rx.Subscription
import rx.functions.Action1


class Notification(
    /**
     * 001  top 排名跳转到首页
     *
     * @return
     */
    var code: Int, var id: Long
) {
    private var extra: Any? = null

    fun getExtra(): Any? {
        return extra
    }

    fun setExtra(extra: Any): Notification {
        this.extra = extra
        return this
    }

    override fun toString(): String {
        return "Notification{" +
                "code=" + code +
                ", id=" + id +
                '}'.toString()
    }

    companion object {

        private var observable: Observable<Notification>? = null

        fun register(action1: Action1<Notification>): Subscription {
            if (observable == null) {
                observable = RxBus.default!!.toObservable(Notification::class.java)
            }
            return observable!!.subscribe(action1, Action1 { throwable -> throwable.printStackTrace() })
        }
    }
}
