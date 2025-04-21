/*
 * Copyright 2025 hai
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 *
 */

package com.androidx.stockchart.index

import android.graphics.Color
import com.androidx.stockchart.entities.IKEntity
import com.androidx.stockchart.util.ResourceUtil
import com.androidx.stock_chart.R

/**
 * @author hai
 * @version 创建时间: 2021/2/18
 */
open abstract class Index(
    var param: String,
    var startText: String,
    var startTextColor: Int,
    var textFormatter: (idx: Int, value: Float?) -> String,
    var textMarginLeft: Float,
    var textMarginTop: Float,
    var textSpace: Float,
    var textSize: Float,
    var preFixText:String?=null,
    var preFixTextColor: Int = Color.GRAY
) {

    abstract fun calculate(input: List<IKEntity>): List<List<Float?>>

    class MA(
        param: String = com.androidx.stockchart.DefaultIndexParams.MA,
        startText: String = com.androidx.stockchart.DefaultIndexStartText.MA,
        startTextColor: Int = ResourceUtil.getColor(R.color.stock_chart_index_start_text),
        textFormatter: (idx: Int, value: Float?) -> String = com.androidx.stockchart.DefaultIndexTextFormatter.MA,
        textMarginLeft: Float = com.androidx.stockchart.DEFAULT_INDEX_TEXT_MARGIN_LEFT,
        textMarginTop: Float = com.androidx.stockchart.DEFAULT_INDEX_TEXT_MARGIN_TOP,
        textSpace: Float = com.androidx.stockchart.DEFAULT_INDEX_TEXT_SPACE,
        textSize: Float = com.androidx.stockchart.DEFAULT_INDEX_TEXT_SIZE,
        preFixText: String? =null
    ) : Index(
        param,
        startText,
        startTextColor,
        textFormatter,
        textMarginLeft,
        textMarginTop,
        textSpace,
        textSize,
        preFixText
    ) {
        override fun calculate(input: List<IKEntity>) = MACalculator.calculate(param, input)
    }

    class EMA(
        param: String = com.androidx.stockchart.DefaultIndexParams.EMA,
        startText: String = com.androidx.stockchart.DefaultIndexStartText.EMA,
        startTextColor: Int = ResourceUtil.getColor(R.color.stock_chart_index_start_text),
        textFormatter: (idx: Int, value: Float?) -> String = com.androidx.stockchart.DefaultIndexTextFormatter.EMA,
        textMarginLeft: Float = com.androidx.stockchart.DEFAULT_INDEX_TEXT_MARGIN_LEFT,
        textMarginTop: Float = com.androidx.stockchart.DEFAULT_INDEX_TEXT_MARGIN_TOP,
        textSpace: Float = com.androidx.stockchart.DEFAULT_INDEX_TEXT_SPACE,
        textSize: Float = com.androidx.stockchart.DEFAULT_INDEX_TEXT_SIZE,
        preFixText: String? =null
    ) : Index(
        param,
        startText,
        startTextColor, textFormatter, textMarginLeft, textMarginTop, textSpace, textSize,preFixText
    ) {
        override fun calculate(input: List<IKEntity>) = EMACalculator.calculate(param, input)
    }

    class BOLL(
        param: String = com.androidx.stockchart.DefaultIndexParams.BOLL,
        startText: String = com.androidx.stockchart.DefaultIndexStartText.BOLL,
        startTextColor: Int = ResourceUtil.getColor(R.color.stock_chart_index_start_text),
        textFormatter: (idx: Int, value: Float?) -> String = com.androidx.stockchart.DefaultIndexTextFormatter.BOLL,
        textMarginLeft: Float = com.androidx.stockchart.DEFAULT_INDEX_TEXT_MARGIN_LEFT,
        textMarginTopDp: Float = com.androidx.stockchart.DEFAULT_INDEX_TEXT_MARGIN_TOP,
        textSpace: Float = com.androidx.stockchart.DEFAULT_INDEX_TEXT_SPACE,
        textSize: Float = com.androidx.stockchart.DEFAULT_INDEX_TEXT_SIZE,
        preFixText: String? =null
    ) : Index(
        param,
        startText,
        startTextColor,
        textFormatter,
        textMarginLeft,
        textMarginTopDp,
        textSpace,
        textSize,
        preFixText
    ) {
        override fun calculate(input: List<IKEntity>) = BollCalculator.calculate(param, input)
    }

    class MACD(
        param: String = com.androidx.stockchart.DefaultIndexParams.MACD,
        startText: String = com.androidx.stockchart.DefaultIndexStartText.MACD,
        startTextColor: Int = ResourceUtil.getColor(R.color.stock_chart_index_start_text),
        textFormatter: (idx: Int, value: Float?) -> String = com.androidx.stockchart.DefaultIndexTextFormatter.MACD,
        textMarginLeft: Float = com.androidx.stockchart.DEFAULT_INDEX_TEXT_MARGIN_LEFT,
        textMarginTop: Float = com.androidx.stockchart.DEFAULT_INDEX_TEXT_MARGIN_TOP,
        textSpace: Float = com.androidx.stockchart.DEFAULT_INDEX_TEXT_SPACE,
        textSize: Float = com.androidx.stockchart.DEFAULT_INDEX_TEXT_SIZE
    ) : Index(
        param,
        startText,
        startTextColor,
        textFormatter,
        textMarginLeft,
        textMarginTop,
        textSpace,
        textSize
    ) {
        override fun calculate(input: List<IKEntity>) = MACDCalculator.calculate(param, input)
    }

    class KDJ(
        param: String = com.androidx.stockchart.DefaultIndexParams.KDJ,
        startText: String = com.androidx.stockchart.DefaultIndexStartText.KDJ,
        startTextColor: Int = ResourceUtil.getColor(R.color.stock_chart_index_start_text),
        textFormatter: (idx: Int, value: Float?) -> String = com.androidx.stockchart.DefaultIndexTextFormatter.KDJ,
        textMarginLeft: Float = com.androidx.stockchart.DEFAULT_INDEX_TEXT_MARGIN_LEFT,
        textMarginTop: Float = com.androidx.stockchart.DEFAULT_INDEX_TEXT_MARGIN_TOP,
        textSpace: Float = com.androidx.stockchart.DEFAULT_INDEX_TEXT_SPACE,
        textSize: Float = com.androidx.stockchart.DEFAULT_INDEX_TEXT_SIZE
    ) : Index(
        param,
        startText,
        startTextColor,
        textFormatter,
        textMarginLeft,
        textMarginTop,
        textSpace,
        textSize
    ) {
        override fun calculate(input: List<IKEntity>) = KDJCalculator.calculate(param, input)
    }

    class RSI(
        param: String = com.androidx.stockchart.DefaultIndexParams.RSI,
        startText: String = "RSI",
        startTextColor: Int = ResourceUtil.getColor(R.color.stock_chart_index_start_text),
        textFormatter: (idx: Int, value: Float?) -> String = com.androidx.stockchart.DefaultIndexTextFormatter.RSI,
        textMarginLeft: Float = com.androidx.stockchart.DEFAULT_INDEX_TEXT_MARGIN_LEFT,
        textMarginTopDp: Float = com.androidx.stockchart.DEFAULT_INDEX_TEXT_MARGIN_TOP,
        textSpace: Float = com.androidx.stockchart.DEFAULT_INDEX_TEXT_SPACE,
        textSize: Float = com.androidx.stockchart.DEFAULT_INDEX_TEXT_SIZE
    ) : Index(
        param,
        startText,
        startTextColor,
        textFormatter,
        textMarginLeft,
        textMarginTopDp,
        textSpace,
        textSize
    ) {
        override fun calculate(input: List<IKEntity>) = RSICalculator.calculate(param, input)
    }

    class VOL(
        param: String = "",
        startText: String = "VOL",
        startTextColor: Int = ResourceUtil.getColor(R.color.stock_chart_index_start_text),
        textFormatter: (idx: Int, value: Float?) -> String =  com.androidx.stockchart.DefaultIndexTextFormatter.VOL,
        textMarginLeft: Float = com.androidx.stockchart.DEFAULT_INDEX_TEXT_MARGIN_LEFT,
        textMarginTopDp: Float = com.androidx.stockchart.DEFAULT_INDEX_TEXT_MARGIN_TOP,
        textSpace: Float = com.androidx.stockchart.DEFAULT_INDEX_TEXT_SPACE,
        textSize: Float = com.androidx.stockchart.DEFAULT_INDEX_TEXT_SIZE
    ) : Index(
        param,
        startText,
        startTextColor,
        textFormatter,
        textMarginLeft,
        textMarginTopDp,
        textSpace,
        textSize
    ) {
        override fun calculate(input: List<IKEntity>): List<List<Float?>>{
            return  mutableListOf(input.map { it.getVolume().toFloat() }.toList())
        }
    }
}

