package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImSellBill

/**
 * @author Rinhon
 * @date 2025/6/20 11:08
 * @description:
 */

interface ImSellBillRepository {

    suspend fun batchInsertSellBills(sellBills: List<ImSellBill>)

    suspend fun deleteAllImSellBill()

}
