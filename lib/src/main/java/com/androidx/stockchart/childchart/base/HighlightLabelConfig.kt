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

package com.androidx.stockchart.childchart.base

import com.androidx.stock_chart.R
import com.androidx.stockchart.util.NumberFormatUtil
import com.androidx.stockchart.util.ResourceUtil

/**
 * 高亮线配置
 *
 * @author hai
 * @version 创建时间: 2021/2/8
 */
class HighlightLabelConfig(
    // 背景色
    var bgColor: Int = ResourceUtil.getColor(R.color.stock_chart_highlight_label_bg),
    // 背景圆角
    var bgCorner: Float = com.androidx.stockchart.DEFAULT_HIGHLIGHT_LABEL_BG_CORNER,
    // 内间距
    var padding: Float = com.androidx.stockchart.DEFAULT_HIGHLIGHT_LABEL_PADDING,
    // 文字大小
    var textSize: Float = com.androidx.stockchart.DEFAULT_HIGHLIGHT_LABEL_TEXT_SIZE,
    // 文字颜色
    var textColor: Int = ResourceUtil.getColor(R.color.stock_chart_highlight_label),
    // 显示的内容格式化
    var textFormat: (value: Float) -> String = { value -> "${NumberFormatUtil.formatPrice(value)}" }
)