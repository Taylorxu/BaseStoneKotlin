package com.xuzhiguang.xzglibrary.rx

import rx.Observable
import rx.subjects.PublishSubject
import rx.subjects.SerializedSubject
import rx.subjects.Subject

class RxBus {

    private val bus: Subject<Any, Any>

    init {
        bus = SerializedSubject(PublishSubject.create())
    }

    fun post(o: Any) {
        bus.onNext(o)
    }

    // 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
    fun <T> toObservable(eventType: Class<T>): Observable<T> {
        return bus.ofType(eventType)
    }

    companion object {
        @Volatile
        private var defaultInstance: RxBus? = null

        val default: RxBus?
            get() {
                if (defaultInstance == null) {
                    synchronized(RxBus::class.java) {
                        if (defaultInstance == null) {
                            defaultInstance = RxBus()
                        }
                    }
                }
                return defaultInstance
            }
    }
}
