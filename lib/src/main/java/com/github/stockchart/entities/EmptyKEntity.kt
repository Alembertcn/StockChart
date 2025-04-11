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

package com.github.stockchart.entities

/**
 * 代表一个空的K线数据点
 *
 * @author hai
 * @version 创建时间: 2021/1/29
 */
@Deprecated("")
open class EmptyKEntity : KEntity(0f, 0f, 0f, 0f, 0, 0, null, null,FLAG_EMPTY)