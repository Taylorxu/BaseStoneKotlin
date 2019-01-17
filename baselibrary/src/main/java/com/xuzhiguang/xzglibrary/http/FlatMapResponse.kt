package com.xuzhiguang.xzglibrary.http

import retrofit2.Response
import rx.Observable
import java.io.IOException

/**
 * Created by 徐志广 on 2018/4/12.
 */
object FlatMapResponse {
    /**
     * * Response retorfit 接口 返回的第一层 response
     * 通过Observable在将返回的结果发送到下一层
     */
    fun <T> callResponse(t: Response<T>): Observable<T> {
        return if (t.isSuccessful) {
            Observable.just(t.body())
        } else {
            try {
                Observable.error<T>(Error(t.code(), t.errorBody().string()))
            } catch (e: IOException) {
                Observable.error<T>(Error(t.code(), e.message))
            }

        }
    }

    /**
     * 最后返回的是resultModel<T>层
     * ResultModel 将返回结果 转换成对象 T 也就是对应 数据集合对象
     * Observable<T> 泛型 决定subscriber中的泛型
     *
     */
    fun <T> callResult(t: ResultModel<T>): Observable<T> {
        return if (0 == t.Status) {
            Observable.just(t.rows)
        } else {
            Observable.error(Error(t.Status, t.Reason))
        }
    }


}

