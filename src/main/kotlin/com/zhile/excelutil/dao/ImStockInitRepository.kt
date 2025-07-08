package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImStockInit


interface ImStockInitRepository {

    fun deleteAllImStockInit()

    fun batchInsertStockInits(stockInits: List<ImStockInit>)
}
