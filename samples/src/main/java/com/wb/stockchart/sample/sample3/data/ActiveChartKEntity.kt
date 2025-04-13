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

package com.wb.stockchart.sample.sample3.data

import com.wb.stockchart.entities.FLAG_DEFAULT
import com.wb.stockchart.entities.IKEntity
import java.math.BigDecimal

/**
 * @author hai
 * @version 创建时间: 2021/5/14
 */
class ActiveChartKEntity(
    private var price: Float,
    private var avgPrice: Float?,
    private var time: Long,
    private var volume: Long,
    private var active: ActiveInfo?,
    private var flag: Int = FLAG_DEFAULT,
    private var amount: BigDecimal?=null,
) : com.wb.stockchart.entities.IKEntity, IActiveChartKEntity {
    override fun getAvgPrice() = avgPrice

    override fun setAvgPrice(price: Float?) {
        this.avgPrice = price
    }

    override fun getClosePrice() = price

    override fun setClosePrice(price: Float) {
        this.price = price
    }

    override fun getHighPrice() = price

    override fun setHighPrice(price: Float) {
        this.price = price
    }

    override fun getLowPrice() = price

    override fun setLowPrice(price: Float) {
        this.price = price
    }

    override fun getOpenPrice() = price

    override fun setOpenPrice(price: Float) {
        this.price = price
    }

    override fun getTime() = time

    override fun setTime(time: Long) {
        this.time = time
    }

    override fun getVolume() = volume

    override fun setVolume(volume: Long) {
        this.volume = volume
    }

    override fun getAmount()=amount

    override fun setAmount(amount: BigDecimal?) {
       this.amount = amount
    }

    override fun getActiveInfo() = active

    override fun setFlag(flag: Int) {
        this.flag = flag
    }

    override fun getFlag() = flag
}