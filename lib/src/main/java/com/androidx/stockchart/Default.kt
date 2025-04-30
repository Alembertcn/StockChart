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

package com.androidx.stockchart

import com.androidx.stock_chart.R
import com.androidx.stockchart.childchart.base.HighlightLabelConfig
import com.androidx.stockchart.childchart.kchart.KChartConfig
import com.androidx.stockchart.index.Index
import com.androidx.stockchart.util.NumberFormatUtil
import com.androidx.stockchart.util.ResourceUtil

/**
 * 默认配置
 * @author hai
 * @version 创建时间: 2021/1/28
 */

// 通用
const val DEFAULT_CHILD_CHART_HEIGHT = 500
const val DEFAULT_CHILD_CHART_MARGIN_TOP = 0
const val DEFAULT_CHILD_CHART_MARGIN_BOTTOM = 0
const val DEFAULT_OVER_SCROLL_DISTANCE = 300
const val DEFAULT_OVER_SCROLL_ON_LOAD_MORE_DISTANCE = 100
const val DEFAULT_SCROLL_ABLE = false
const val DEFAULT_SCROLL_SMOOTHLY = true
const val DEFAULT_OVER_SCROLL_ABLE = true
const val DEFAULT_SCALE_ABLE = false
const val DEFAULT_FRICTION_SCROLL_EXCEED_LIMIT = 0.3f
const val DEFAULT_SCALE_FACTOR_MAX = 5f
const val DEFAULT_SCALE_FACTOR_MIN = 0.5f
const val DEFAULT_HIGHLIGHT_HORIZONTAL_LINE_WIDTH = 2f
const val DEFAULT_HIGHLIGHT_VERTICAL_LINE_WIDTH = 2f
const val DEFAULT_SHOW_HIGHLIGHT_HORIZONTAL_LINE = true
const val DEFAULT_SHOW_HIGHLIGHT_VERTICAL_LINE = true
const val DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_TOP = 60f
const val DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_BOTTOM = 60f
const val DEFAULT_GRID_HORIZONTAL_LINE_COUNT = 0
const val DEFAULT_GRID_VERTICAL_LINE_COUNT = 0
const val DEFAULT_GRID_LINE_STROKE_WIDTH = 2f
const val DEFAULT_VALUE_TEND_TO_ZERO = 0.0001f
// K线图
const val DEFAULT_K_CHART_LINE_CHART_STROKE_WIDTH = 3f
const val DEFAULT_K_CHART_MOUNTAIN_CHART_STROKE_WIDTH = 3f
const val DEFAULT_K_CHART_CANDLE_CHART_LINE_STROKE_WIDTH = 1.5f
const val DEFAULT_K_CHART_HOLLOW_CHART_LINE_STROKE_WIDTH = 1.5f
const val DEFAULT_K_CHART_BAR_CHART_LINE_STROKE_WIDTH = 3f
const val DEFAULT_K_CHART_COST_PRICE_LINE_WIDTH = 3f
const val DEFAULT_K_CHART_INDEX_STROKE_WIDTH = 1f
const val DEFAULT_K_CHART_BAR_SPACE_RATIO = 0.3f
val DEFAULT_K_CHART_INDEX = Index.MA()
const val DEFAULT_K_CHART_HIGHEST_AND_LOWEST_LABEL_TEXT_SIZE = 24f
const val DEFAULT_K_CHART_HIGHEST_AND_LOWEST_LABEL_LINE_STROKE_WIDTH = 1f
const val DEFAULT_K_CHART_HIGHEST_AND_LOWEST_LABEL_LINE_LENGTH = 30f

val DEFAULT_MAIN_CHART_INDEX_TYPES = arrayOf(Index.MA::class, Index.EMA::class, Index.BOLL::class, Index.VWAP::class)


// 时间条
const val DEFAULT_TIME_BAR_HEIGHT = 60
const val DEFAULT_TIME_BAR_LABEL_TEXT_SIZE = 30f
const val DEFAULT_TIME_BAR_HIGHLIGHT_LABEL_TEXT_SIZE = 30f

val DEFAULT_K_CHART_LEFT_LABEL_CONFIG =
    KChartConfig.LabelConfig(
        3,
        { "${NumberFormatUtil.formatPrice(it)}" },
        DEFAULT_TIME_BAR_LABEL_TEXT_SIZE,
        ResourceUtil.getColor(R.color.stock_chart_axis_y_label),
        15f,
        15f,
        15f,
        null
    )
val DEFAULT_K_CHART_HIGHLIGHT_LABEL_LEFT = HighlightLabelConfig()
const val DEFAULT_AVG_LINE_WIDTH = 3f

// Volume图
const val DEFAULT_VOLUME_BAR_SPACE_RATIO = 0.3f
const val DEFAULT_VOLUME_CHART_HOLLOW_CHART_LINE_STROKE_WIDTH = 1.5f

// HighlightLabel
const val DEFAULT_HIGHLIGHT_LABEL_BG_CORNER = 6f
const val DEFAULT_HIGHLIGHT_LABEL_PADDING = 6f
const val DEFAULT_HIGHLIGHT_LABEL_TEXT_SIZE = 20f

// MACD指标图
const val DEFAULT_MACD_DIF_LINE_STROKE_WIDTH = 3f
const val DEFAULT_MACD_DEA_LINE_STROKE_WIDTH = 3f
const val DEFAULT_MACD_BAR_SPACE_RATIO = 0.8f

// KDJ指标图
const val DEFAULT_KDJ_K_LINE_STROKE_WIDTH = 3f

const val DEFAULT_KDJ_D_LINE_STROKE_WIDTH = 3f
const val DEFAULT_KDJ_J_LINE_STROKE_WIDTH = 3f

// 指标
const val DEFAULT_INDEX_TEXT_MARGIN_LEFT = 15f
const val DEFAULT_INDEX_TEXT_MARGIN_TOP = 0f
const val DEFAULT_INDEX_TEXT_SPACE = 15f
const val DEFAULT_INDEX_TEXT_SIZE = 24f

object DefaultIndexParams {
    const val MA = "5,10,20"
    const val EMA = "5,10,20"
    const val BOLL = "20,2"
    const val MACD = "12,26,9"
    const val KDJ = "9,3,3"
    const val RSI = "6,12,24"

    const val ATR = "14"
}

object DefaultIndexTextFormatter {
    val MA: (idx: Int, value: Float?) -> String = { idx, value ->
        "MA${
            com.androidx.stockchart.DefaultIndexParams.MA.split(",")
                .map { it.trim() }[idx]
        }:${value?.let { NumberFormatUtil.formatPrice(it) } ?: "——"}"
    }
    val EMA: (idx: Int, value: Float?) -> String = { idx, value ->
        "EMA${
            com.androidx.stockchart.DefaultIndexParams.EMA.split(",")
                .map { it.trim() }[idx]
        }:${value?.let { NumberFormatUtil.formatPrice(it) } ?: "——"}"
    }
    val BOLL: (idx: Int, value: Float?) -> String = { idx, value ->
        val prefix = when (idx) {
            0 -> "MID:"
            1 -> "UPPER:"
            2 -> "LOWER:"
            else -> ""
        }

        "$prefix${value?.let { NumberFormatUtil.formatPrice(it) } ?: "——"}"
    }
    val MACD: (idx: Int, value: Float?) -> String = { idx, value ->
        val prefix = when (idx) {
            0 -> "DIF:"
            1 -> "DEA:"
            2 -> "MACD:"
            else -> ""
        }
        "$prefix${value?.let { NumberFormatUtil.formatPrice(it) } ?: "——"}"
    }
    val KDJ: (idx: Int, value: Float?) -> String = { idx, value ->
        val prefix = when (idx) {
            0 -> "K:"
            1 -> "D:"
            2 -> "J:"
            else -> ""
        }
        "$prefix${value?.let { NumberFormatUtil.formatPrice(it) } ?: "——"}"
    }
    val RSI: (idx: Int, value: Float?) -> String = { idx, value ->
        "RSI${
            com.androidx.stockchart.DefaultIndexParams.RSI.split(",").map { it.trim() }[idx]
        }:${value?.let { NumberFormatUtil.formatPrice(it) } ?: "——"}"
    }
    val VOL: (idx: Int, value: Float?) -> String = { idx, value ->
        NumberFormatUtil.formatVolume(value?.toLong()) ?: "——"
    }
    val ATR: (idx: Int, value: Float?) -> String = { idx, value ->
        "${if(idx == 0)"TR1" else "ATR1"}:${value?.let { NumberFormatUtil.formatPrice(it) } ?: "——"}"
    }
    val OBV: (idx: Int, value: Float?) -> String = { idx, value ->
        "OBV:${value?.let { NumberFormatUtil.formatPrice(it) } ?: "——"}"
    }
    val VWAP: (idx: Int, value: Float?) -> String = { idx, value ->
        "VWAP:${value?.let { NumberFormatUtil.formatPrice(it) } ?: "——"}"
    }
}

object DefaultIndexStartText {
    const val MA = "MA"
    const val EMA = "EMA"
    const val BOLL = "BOLL(${com.androidx.stockchart.DefaultIndexParams.BOLL})"
    const val MACD = "MACD(${com.androidx.stockchart.DefaultIndexParams.MACD})"
    const val KDJ = "KDJ(${com.androidx.stockchart.DefaultIndexParams.KDJ})"
}



