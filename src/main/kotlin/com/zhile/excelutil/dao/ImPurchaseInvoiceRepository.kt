package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImPurchaseInvoice

/**
 * @author zlhp
 * @date 2025/7/1 16:11
 * @description:
 */
interface ImPurchaseInvoiceRepository {

    fun deleteAllPurchaseInvoice()

    fun batchInsertPurchaseInvoices(purchaseInvoices: List<ImPurchaseInvoice>)
}
