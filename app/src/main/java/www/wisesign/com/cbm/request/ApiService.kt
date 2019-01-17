package www.wisesign.com.cbm.request


import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.xuzhiguang.xzglibrary.http.ResultModel
import com.xuzhiguang.xzglibrary.http.XRequest
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import rx.Observable
import www.wisesign.com.cbm.model.*


/**
 * 请求接口发起
 */

interface ApiService {
    @FormUrlEncoded
    @POST("{path}")
    fun userLoginServlet(@Path("path") path: String, @FieldMap param: Map<String, String>): Observable<Response<LoginResponse>>

    @FormUrlEncoded
    @POST("DictDataServlet")
    fun dictDataServlet(@FieldMap param: Map<String, String>): Observable<Response<ResultModel<MutableList<SystemWorkingCaseBean>>>>

    @FormUrlEncoded
    @POST("SystemStatusServlet")
    fun systemStatusServlet(@Field("tradeBankCode") tradeBankCode: String): Observable<Response<ResultModel<MutableList<SystemWorkingCaseBean>>>>

    @FormUrlEncoded
    @POST("CurrentAlarmServlet")
    fun currentAlarmServlet(@Field("page") page: String): Observable<Response<ResultModel<MutableList<AlarmSystemBean>>>>

    @FormUrlEncoded
    @POST("HistoryAlarmServlet")
    fun historyAlarmServlet(@Field("page") page: Int): Observable<Response<ResultModel<MutableList<AlarmSystemBean>>>>


    @POST("TradeBankTopServlet")
    fun tradeBankTopServlet(): Observable<Response<ResultModel<WrapMemberTradeBean>>>

    @FormUrlEncoded
    @POST("OneSystemStatusServlet")
    fun oneSystemStatusServlet(@Field("tradeSysCode") tradeSysCode: String, @Field("tradeBankCode") tradeBankCode: String): Observable<Response<WrapOnSystemBean>>


    companion object {

        var gson: Gson = GsonBuilder().enableComplexMapKeySerialization().create()
        fun get(): ApiService {
            var retrofit: Retrofit? = Retrofit.Builder()
                .baseUrl(BaseUrl.host)
                .client(XRequest.build().mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            return retrofit!!.create(ApiService::class.java)
        }
    }

}
