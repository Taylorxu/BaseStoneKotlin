package www.wisesign.com.cbm.model

/**
 * "unifiedMonitor":"1"=有权限 "0"=无权限 (old)，
 * "businessMonitor":"1"=有权限 "0"=无权限 （new）
 */
data class LoginResponse(
    var Reason: String,
    var Status: String,
    var unifiedMonitor: String,
    var businessMonitor: String
)

data class SystemWorkingCaseBean(
    var TradeSysCode: String = "", var TradeSysColour: String = "", var TradeSysVolume: String = "",
    var TradeSysName: String = "", var TradeSysSucRate: String = "", var TradeBankName: String = "",
    var TradeBankCode: String = ""
)

/**
 * RecoveryTime : 2018-02-08 10:06:00
 * AlarmTime : 2018-03-22 14:22:20
 * AlarmContent : 【IGA服务器】在【00:00:32至23:59:32】的时间里【1分钟】内无成功交易
 */
data class AlarmSystemBean(var RecoveryTime: String, var AlarmTime: String, var AlarmContent: String) {
    fun shuoldShow(): Int {
        return if (RecoveryTime == null) {
            8
        } else 0
    }
}

data class WrapMemberTradeBean(var Hour: String, var BankList: MutableList<MemberTradeBean>)

data class MemberTradeBean(var TradeBankName: String, var TradeBankCode: String, var TradeVolume: String)

data class WrapOnSystemBean(
    var current: OneSystemBean,
    var history: OneSystemBean,
    var Status: String,
    var Reason: String
)


/**
 * "TradeVolume":交易量，
 * "TradeSucRate":成功率，
 * "TradeDynamicVolume":动账量，
 * "TradeStaticVolume":非动账量,
 * "TradeDynamicSucRate":动账成功率，
 * "TradeStaticSucRate":非动账成功率,
 * "DateTime":当前日期时间+星期几
 */
data class OneSystemBean(
    var TradeVolume: String,
    var TradeSucRate: String,
    var DateTime: String,
    var TradeDynamicVolume: String,
    var TradeStaticVolume: String,
    var TradeDynamicSucRate: String,
    var TradeStaticSucRate: String
)


data class WarningBean(
    var warnDate: String,
    var warnContent: String,
    var warnSovledDate: String
) {


    fun shuoldShow(): Int {
        return if (warnSovledDate.isEmpty()) {
            8
        } else 0
    }
}
