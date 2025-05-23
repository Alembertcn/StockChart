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

package com.androidx.stockchart.entities

/**
 * @author hai
 * @version 创建时间: 2021/5/13
 */
open class GestureEvent(
    /**
     * 实际坐标x
     */
    var x: Float = 0f,

    /**
     * 实际坐标y
     */
    var y: Float = 0f,

    /**
     * 逻辑坐标x
     */
    var valueX: Float = 0f,

    /**
     * 逻辑坐标y
     */
    var valueY: Float = 0f
) {

    /**
     * 获取对应K线数据下标
     */
    fun getIdx() = (valueX+.5f).toInt()

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Highlight) return false
        return x == other.x && y == other.y && valueX == other.valueX && valueY == other.valueY
    }

    override fun toString(): String {
        return "GestureEvent(x=$x, y=$y, valueX=$valueX, valueY=$valueY)"
    }


}