package wb.lib.module_chart


/**
 * @author Hai
 * @date   2025/4/10 10:42
 * @desc
 */
enum class Period {
    DAY,                // 日K
    FIVE_DAYS,          // 5日K
    WEEK,               // 周K
    MONTH,              // 月K
    QUARTER,            // 季K
    YEAR,               // 年K
    FIVE_YEARS,         // 5年K
    YTD,                // 年初至今
    ONE_MINUTE,         // 一分
    FIVE_MINUTES,       // 5分
    THIRTY_MINUTES,       // 30分
    SIXTY_MINUTES,      // 60分
    DAY_TIME,           // 分时
}

enum class ChartType(num: Int) {
    UNKNOWN(0),

    ONE_DAY_MINI(66),  // 港股, 5.5*60+1
    ONE_DAY_SH(241),  // a股, 4*60+1
    ONE_DAY(241),  //沪深股票当日分时线总点数
    FIVE_DAY(1200),  //沪深股票五日分时线总点数
    DARK_DAY(135),  // 港股, 2.25*60+1

    HK_ONE_DAY(331),  //港股当日分时线总点数
    HK_FIVE_DAY(415),  //港股五日分时线总点数
    SH_FIVE_DAY(1200),  // 港股

    US_ONE_DAY(390),  //美股当日分时线总点数
    US_FIVE_DAY(1950),  //美股五日分时线总点数

    //    US_ONE_DAY(391),// 美股, 6.5*60+1
    US_ONE_DAY_MINI(78),  // 美股, 6.5*60+1
    US_ONE_DAY_PRE_MARKET(331),  //美股盘前 04:00 - 09:30, 5.5*60+1
    US_ONE_DAY_PRE_MARKET_MINI(66),  //美股盘前 04:00 - 09:30, 5.5*60+1
    US_ONE_DAY_AFTER_MARKET(241),  //美股盘后 16:00 - 20:00, 4*60+1
    US_ONE_DAY_AFTER_MARKET_MINI(48),  //美股盘后 16:00 - 20:00, 4*60+1

    US_OTC_ONE_DAY_PRE_MARKET(91),  //otc盘前 8：00-9：30 ，1.5*60+1
    US_OTC_ONE_DAY_AFTER_MARKET(121),  //otc盘后 16：00-18：00 ，2*60+1

    K_DAY_SMALL(100),  //竖屏日K展示的总数据条数
    K_WEEK_SMALL(100),  //竖屏周K展示的总数据条数
    K_MONTH_SMALL(100),  //竖屏月K展示的总数据条数
    K_DAY_BIG(1000),  //横屏日K展示的总数据条数
    K_WEEK_BIG(1000),  //横屏周K展示的总数据条数
    K_MONTH_BIG(1000),  //横屏月K展示的总数据条数

    K_MINUTE_1(1000), K_MINUTE_3(1000), K_MINUTE_5(1000), K_MINUTE_15(1000), K_MINUTE_30(1000), K_MINUTE_60(
        1000
    );

    var pointNum: Int = 0

    init {
        this.pointNum = num
    }


    val isKType: Boolean
        get() = (this == K_DAY_SMALL) || (this == K_WEEK_SMALL) || (this == K_MONTH_SMALL
                ) || (this == K_DAY_BIG) || (this == K_WEEK_BIG) || (this == K_MONTH_BIG) || (this == K_MINUTE_1
                ) || (this == K_MINUTE_3) || (this == K_MINUTE_5) || (this == K_MINUTE_15) || (this == K_MINUTE_30) || (this == K_MINUTE_60)

    val isMinuteKType: Boolean
        get() {
            return (this == K_MINUTE_1) || (this == K_MINUTE_3) || (this == K_MINUTE_5
                    ) || (this == K_MINUTE_15) || (this == K_MINUTE_30) || (this == K_MINUTE_60)
        }
    val isRelateTimeType: Boolean
        get() {
            return (this == ONE_DAY_SH) || (this == ONE_DAY) || (this == FIVE_DAY) || (this == HK_ONE_DAY
                    ) || (this == HK_FIVE_DAY) || (this == US_ONE_DAY) || (this == US_FIVE_DAY)
        }
}