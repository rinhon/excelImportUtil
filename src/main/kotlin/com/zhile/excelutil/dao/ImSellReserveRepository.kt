package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImSellReserve

interface ImSellReserveRepository {

    fun deleteAllImSellReserve()

    fun batchInsertSellReserves(sellReserves: List<ImSellReserve>)
}
