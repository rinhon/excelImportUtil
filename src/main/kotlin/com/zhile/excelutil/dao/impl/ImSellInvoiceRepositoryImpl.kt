package com.zhile.excelutil.dao.impl

import com.zhile.excelutil.dao.ImSellInvoiceRepository
import com.zhile.excelutil.entity.ImSellInvoice
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement

@Repository
class ImSellInvoiceRepositoryImpl(private val jdbcTemplate: JdbcTemplate) : ImSellInvoiceRepository {
    override fun deleteAllImSellInvoice() {
        jdbcTemplate.update("DELETE FROM IM_SELL_INVOICE")

    }

    override fun batchInsertSellInvoices(sellInvoices: List<ImSellInvoice>) {
        if (sellInvoices.isEmpty()) {
            return
        }

        val sql = """
            INSERT INTO IM_SELL_INVOICE
            (F_INVOICE_NO, F_INVOICE_TYPE, F_INVOICE_NUMBER, F_INVOICE_USER_CODE, F_INVOICE_USER_NAME, F_INVOICE_DEPARTMENT_CODE, F_INVOICE_DEPARTMENT_NAME, F_INVOICE_DATE, F_INVOICE_CUSTOMER, F_REMARKS, F_ADJUST_AMOUNT, F_ADJUST_TAX_AMOUNT, F_ORDER_BILL_NO, F_ORDER_BILL_DATE, F_ORADER_BILL_ROW_NO, F_OUT_BILL_NO, F_OUT_BILL_DATE, F_OUT_BILL_ROW_NO, F_POSITION_CODE, F_POSITION_NAME, F_CUSTOMER_CODE, F_CUSTOMER_NAME, F_AREA, F_RECEIVE_ADDRESS, F_RECEIVE_MAN, F_RECEIVE_PHONE, F_GIST, F_ORDER_USER_CODE, F_ORDER_USER_NAME, F_ORDER_DEPARTMENT_CODE, F_ORDER_DEPARTMENT_NAME, F_ITEM_CODE, F_ITEM_NAME, F_PRODUCE_NUM, F_SET_PRICE, F_UNIT, F_QUANTITY, F_DISCOUNT, F_AMOUNT, F_TAX, F_TAX_AMOUNT, F_COST_PRICE, F_COST_AMOUNT, F_ACCOUNT_DATE, F_ALLOCATED_NO_TAX_AMOUNT )
            VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
        """.trimIndent()

        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                val sellInvoice = sellInvoices[i]
                ps.setString(1, sellInvoice.invoiceNo)
                ps.setString(2, sellInvoice.invoiceType)
                ps.setString(3, sellInvoice.invoiceNumber)
                ps.setString(4, sellInvoice.invoiceUserCode)
                ps.setString(5, sellInvoice.invoiceUserName)
                ps.setString(6, sellInvoice.invoiceDepartmentCode)
                ps.setString(7, sellInvoice.invoiceDepartmentName)
                ps.setString(8, sellInvoice.invoiceDate) // Date object's string representation
                ps.setString(9, sellInvoice.invoiceCustomer)
                ps.setString(10, sellInvoice.remarks)
                ps.setString(11, sellInvoice.adjustAmount)
                ps.setString(12, sellInvoice.adjustTaxAmount)
                ps.setString(13, sellInvoice.orderBillNo)
                ps.setString(14, sellInvoice.orderBillDate) // Date object's string representation
                ps.setString(15, sellInvoice.orderBillRowNo)
                ps.setString(16, sellInvoice.outBillNo)
                ps.setString(17, sellInvoice.outBillDate) // Date object's string representation
                ps.setString(18, sellInvoice.outBillRowNo)
                ps.setString(19, sellInvoice.positionCode)
                ps.setString(20, sellInvoice.positionName)
                ps.setString(21, sellInvoice.customerCode)
                ps.setString(22, sellInvoice.customerName)
                ps.setString(23, sellInvoice.area)
                ps.setString(24, sellInvoice.receiveAddress)
                ps.setString(25, sellInvoice.receiveMan)
                ps.setString(26, sellInvoice.receivePhone)
                ps.setString(27, sellInvoice.gist)
                ps.setString(28, sellInvoice.orderUserCode)
                ps.setString(29, sellInvoice.orderUserName)
                ps.setString(30, sellInvoice.orderDepartmentCode)
                ps.setString(31, sellInvoice.orderDepartmentName)
                ps.setString(32, sellInvoice.itemCode)
                ps.setString(33, sellInvoice.itemName)
                ps.setString(34, sellInvoice.produceNum)
                ps.setString(35, sellInvoice.setPrice)
                ps.setString(36, sellInvoice.unit)
                ps.setString(37, sellInvoice.quantity)
                ps.setString(38, sellInvoice.discount)
                ps.setString(39, sellInvoice.amount)
                ps.setString(40, sellInvoice.tax)
                ps.setString(41, sellInvoice.taxAmount)
                ps.setString(42, sellInvoice.costPrice)
                ps.setString(43, sellInvoice.costAmount)
                ps.setString(44, sellInvoice.accountDate) // Date object's string representation
                ps.setString(45, sellInvoice.allocatedNoTaxAmount)
            }

            override fun getBatchSize(): Int {
                return sellInvoices.size
            }

        })
    }
}
