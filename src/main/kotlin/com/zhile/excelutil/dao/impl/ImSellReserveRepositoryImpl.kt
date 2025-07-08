package com.zhile.excelutil.dao.impl

import com.zhile.excelutil.dao.ImSellReserveRepository
import com.zhile.excelutil.entity.ImSellReserve
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement

@Repository
class ImSellReserveRepositoryImpl(private val jdbcTemplate: JdbcTemplate) : ImSellReserveRepository {
    override fun deleteAllImSellReserve() {
        jdbcTemplate.update("DELETE FROM IM_SELL_RESERVE")
    }

    override fun batchInsertSellReserves(sellReserves: List<ImSellReserve>) {
        if (sellReserves.isEmpty()) {
            return
        }

        val sql = """
            INSERT INTO IM_SELL_RESERVE
            (F_BILL_NO, F_CUSTOMER_CODE, F_CUSTOMER_NAME, F_DATE, F_DEPARTMENT_CODE, F_DEPARTMENT_NAME, F_USER_CODE, F_USER_NAME, F_IS_INVOICE, F_INVOICE_TYPE, F_INVOICE_NO, F_INVOICE_DATE, F_INVOICE_CUSTOMER, F_CONTRACT_NO, F_RECEIVE_DATE, F_ITEM_CODE, F_ITEM_NAME, F_TOPIC_APPLY, F_QUANTITY, F_AMOUNT, F_TAX, F_TAX_AMOUNT, F_REAL_AMOUNT, F_REMARKS, F_IN_AMOUNT, F_ACCOUNT_DATE)
            VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
        """.trimIndent()

        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                val sellReserve = sellReserves[i]

                ps.setString(1, sellReserve.billNo)
                ps.setString(2, sellReserve.customerCode)
                ps.setString(3, sellReserve.customerName)
                ps.setString(4, sellReserve.date) // Date object's string representation
                ps.setString(5, sellReserve.departmentCode)
                ps.setString(6, sellReserve.departmentName)
                ps.setString(7, sellReserve.userCode)
                ps.setString(8, sellReserve.userName)
                ps.setString(9, sellReserve.isInvoice) // Boolean or Int in DB, converts to String
                ps.setString(10, sellReserve.invoiceType)
                ps.setString(11, sellReserve.invoiceNo)
                ps.setString(12, sellReserve.invoiceDate) // Date object's string representation
                ps.setString(13, sellReserve.invoiceCustomer)
                ps.setString(14, sellReserve.contractNo)
                ps.setString(15, sellReserve.receiveDate) // Date object's string representation
                ps.setString(16, sellReserve.itemCode)
                ps.setString(17, sellReserve.itemName)
                ps.setString(18, sellReserve.topicApply)
                ps.setString(19, sellReserve.quantity)
                ps.setString(20, sellReserve.amount)
                ps.setString(21, sellReserve.tax)
                ps.setString(22, sellReserve.taxAmount)
                ps.setString(23, sellReserve.realAmount)
                ps.setString(24, sellReserve.remarks)
                ps.setString(25, sellReserve.inAmount)
                ps.setString(26, sellReserve.accountDate) // Date object's string representation
            }

            override fun getBatchSize(): Int {
                return sellReserves.size
            }
        })
    }
}
