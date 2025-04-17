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

package com.wb.stockchart.index

import com.wb.stockchart.entities.FLAG_EMPTY
import com.wb.stockchart.entities.IKEntity
import com.wb.stockchart.entities.containFlag
import kotlin.math.max
import kotlin.math.min

/**
 * KDJ 随机指标
 * @author hai
 * @version 创建时间: 2021/2/18
 */
object KDJCalculator : ICalculator {

    override fun calculate(param: String, input: List<IKEntity>): List<List<Float?>> {
        val paramList = param.split(",")
        val n = try { paramList[0].toInt() } catch (tr: Throwable) { return emptyList() }
        val kn = try { paramList[1].toInt() } catch (tr: Throwable) { return emptyList() }
        val dn = try { paramList[2].toInt() } catch (tr: Throwable) { return emptyList() }

        // 参数校验
        if (n <= 0 || kn <= 1 || dn <= 1) return emptyList()

        val result = MutableList(3) { MutableList<Float?>(input.size) { null } }
        val kIdx = 0; val dIdx = 1; val jIdx = 2

        input.forEachIndexed { kEntityIdx, kEntity ->
            if (kEntity.containFlag(FLAG_EMPTY)) {
                result[kIdx][kEntityIdx] = null
                result[dIdx][kEntityIdx] = null
                result[jIdx][kEntityIdx] = null
                return@forEachIndexed
            }

            val c = kEntity.getClosePrice()
            var l = kEntity.getLowPrice()
            var h = kEntity.getHighPrice()

            // 计算n日内的最低价和最高价
            for (i in kEntityIdx - 1 downTo max(0, kEntityIdx - n + 1)) {
                l = min(l, input[i].getLowPrice())
                h = max(h, input[i].getHighPrice())
            }

            // 处理RSV
            val rsv = if (h == l) 0.5f else (c - l) / (h - l)  // 默认50%避免除零

            // 计算K值（初始值为50）
            val preK = if (kEntityIdx == 0 || result[kIdx][kEntityIdx - 1] == null) 50f
            else result[kIdx][kEntityIdx - 1]!!
            val k = max(0f, min(100f, (kn - 1f) / kn * preK + 1f / kn * rsv * 100f))

            // 计算D值（初始值为50）
            val preD = if (kEntityIdx == 0 || result[dIdx][kEntityIdx - 1] == null) 50f
            else result[dIdx][kEntityIdx - 1]!!
            val d = max(0f, min(100f, (dn - 1f) / dn * preD + 1f / dn * k))

            // 计算J值（允许超出0~100）
            val j = 3f * k - 2f * d

            result[kIdx][kEntityIdx] = k
            result[dIdx][kEntityIdx] = d
            result[jIdx][kEntityIdx] = j
        }
        return result
    }
}