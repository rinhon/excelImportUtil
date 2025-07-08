package com.zhile.excelutil.dao.impl

import com.zhile.excelutil.dao.ImPurchaseInvoiceRepository
import com.zhile.excelutil.entity.ImPurchaseInvoice
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement

@Repository
class ImPurchaseInvoiceRepositoryImpl(private val jdbcTemplate: JdbcTemplate) : ImPurchaseInvoiceRepository {
    override fun deleteAllPurchaseInvoice() {
        jdbcTemplate.update("DELETE FROM IM_PURCHASE_INVOICE")
    }

    override fun batchInsertPurchaseInvoices(purchaseInvoices: List<ImPurchaseInvoice>) {
        if (purchaseInvoices.isEmpty()) {
            return
        }

        val sql = """
            INSERT INTO SPMG_ERP_159.IM_PURCHASE_INVOICE
            (F_INVOICE_BILL_NO, F_INVOICE_TYPE, F_INVOICE_NO, F_INVOICE_USER_CODE, F_INVOICE_USER_NAME, F_INVOICE_DEPARTMENT_CODE, F_INVOICE_DEPARTMENT_NAME, F_INVOICE_DATE, F_REMARKS, F_ORDER_BILL_NO, F_ORDER_DATE, F_ORDER_ROW_NO, F_IN_BILL_NO, F_IN_DATE, F_IN_ROW_NO, F_POSITION, F_CUSTOMER_CODE, F_CUSTOMER_NAME, F_AREA, F_SEND_ADDRESS, F_SEND_MAN, F_SEND_PHONE, F_GIST, F_ORDER_USER_CODE, F_ORDER_USER_NAME, F_ORDER_DEPARTMENT_CODE, F_ORDER_DEPARTMENT_NAME, F_ITEM_CODE, F_ITEM_NAME, F_PRODUCE_NUM, F_SET_PRICE, F_DISCOUNT, F_UNIT, F_QUANTITY, F_AVERAGE_DISCOUNT, F_NO_INVOICE_AMOUNT, F_TAX, F_TAX_AMOUNT, F_ACCOUNT_BANK_NAME, F_BANK_NAME, F_ACCOUNT_NAME, F_ACCOUNT_NO, F_ACCOUNT_DATE) 
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
        """.trimIndent()

        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                val invoice = purchaseInvoices[i]
                ps.setString(1, invoice.invoiceBillNo)
                ps.setString(2, invoice.invoiceType)
                ps.setString(3, invoice.invoiceNo)
                ps.setString(4, invoice.invoiceUserCode)
                ps.setString(5, invoice.invoiceUserName)
                ps.setString(6, invoice.invoiceDepartmentCode)
                ps.setString(7, invoice.invoiceDepartmentName)
                ps.setString(8, invoice.invoiceDate)
                ps.setString(9, invoice.remarks)
                ps.setString(10, invoice.orderBillNo)
                ps.setString(11, invoice.orderDate)
                ps.setString(12, invoice.orderRowNo)
                ps.setString(13, invoice.inBillNo)
                ps.setString(14, invoice.inDate)
                ps.setString(15, invoice.inRowNo)
                ps.setString(16, invoice.position)
                ps.setString(17, invoice.customerCode)
                ps.setString(18, invoice.customerName)
                ps.setString(19, invoice.area)
                ps.setString(20, invoice.sendAddress)
                ps.setString(21, invoice.sendMan)
                ps.setString(22, invoice.sendPhone)
                ps.setString(23, invoice.gist)
                ps.setString(24, invoice.orderUserCode)
                ps.setString(25, invoice.orderUserName)
                ps.setString(26, invoice.orderDepartmentCode)
                ps.setString(27, invoice.orderDepartmentName)
                ps.setString(28, invoice.itemCode)
                ps.setString(29, invoice.itemName)
                ps.setString(30, invoice.produceNum)
                ps.setString(31, invoice.setPrice)
                ps.setString(32, invoice.discount)
                ps.setString(33, invoice.unit)
                ps.setString(34, invoice.quantity)
                ps.setString(35, invoice.averageDiscount)
                ps.setString(36, invoice.noInvoiceAmount)
                ps.setString(37, invoice.tax)
                ps.setString(38, invoice.taxAmount)
                ps.setString(39, invoice.accountBankName)
                ps.setString(40, invoice.bankName)
                ps.setString(41, invoice.accountName)
                ps.setString(42, invoice.accountNo)
                ps.setString(43, invoice.accountDate)
            }

            override fun getBatchSize(): Int {
                return purchaseInvoices.size
            }

        })
    }
}
