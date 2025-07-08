package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImSellInvoice

/**
 * @author zlhp
 * @date 2025/6/30 15:39
 * @description:
 */
interface ImSellInvoiceRepository {

    fun deleteAllImSellInvoice()

    fun batchInsertSellInvoices(sellInvoices: List<ImSellInvoice>)
}
