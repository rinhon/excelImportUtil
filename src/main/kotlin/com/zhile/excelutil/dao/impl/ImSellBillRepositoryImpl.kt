package com.zhile.excelutil.dao.impl

import com.zhile.excelutil.dao.ImSellBillRepository
import com.zhile.excelutil.entity.ImSellBill
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement

@Repository
class ImSellBillRepositoryImpl(private val jdbcTemplate: JdbcTemplate) : ImSellBillRepository {

    override suspend fun batchInsertSellBills(sellBills: List<ImSellBill>) {
        if (sellBills.isEmpty()) return

        val sql = """
            INSERT INTO IM_SELL_BILL (
            F_DIRECTION,
            F_ORDER_BILL_NO,
            F_ORDER_BILL_DATE,
            F_ORDER_BILL_ROWNUM,
            F_OUT_BILL_NO,
            F_OUT_BILL_DATE,
            F_OUT_BILL_ROWNUM,
            F_CUSTOMER_CODE,
            F_CUSTOMER_NAME,
            F_AREA,
            F_RECEIVING_ADDRESS,
            F_RECEIVING_LINKMAN,
            F_RECEIVING_LINKMAN_TEL,
            F_GIST,
            F_USER_CODE,
            F_USER_NAME,
            F_DEPARTMENT_CODE,
            F_DEPARTMENT_NAME,
            F_ITEM_CODE,
            F_ITEM_NAME,
            F_PRODUCE_NUM,
            F_SET_PRICE,
            F_UNIT,
            F_NO_INVOICE_QUANTITY,
            F_NO_INVOICE_DISCOUNT,
            F_NO_INVOICE_AMOUNT,
            F_NO_INVOICE_REAL_AMOUNT,
            F_TAX,
            F_TAX_AMOUNT,
            F_POSITION_CODE,
            F_POSITION_NAME,
            F_COST_PRICE,
            F_COST_AMOUNT,
            F_REMARKS,
            F_FIRST_IN_TIME
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                ps.setString(1, sellBills[i].direction)
                ps.setString(2, sellBills[i].orderBillNo)
                ps.setString(3, sellBills[i].orderBillDate)
                ps.setString(4, sellBills[i].orderBillRownum)
                ps.setString(5, sellBills[i].outBillNo)
                ps.setString(6, sellBills[i].outBillDate)
                ps.setString(7, sellBills[i].outBillRownum)
                ps.setString(8, sellBills[i].customerCode)
                ps.setString(9, sellBills[i].customerName)
                ps.setString(10, sellBills[i].area)
                ps.setString(11, sellBills[i].receivingAddress)
                ps.setString(12, sellBills[i].receivingLinkman)
                ps.setString(13, sellBills[i].receivingLinkmanTel)
                ps.setString(14, sellBills[i].gist)
                ps.setString(15, sellBills[i].userCode)
                ps.setString(16, sellBills[i].userName)
                ps.setString(17, sellBills[i].departmentCode)
                ps.setString(18, sellBills[i].departmentName)
                ps.setString(19, sellBills[i].itemCode)
                ps.setString(20, sellBills[i].itemName)
                ps.setString(21, sellBills[i].produceNum)
                ps.setString(22, sellBills[i].setPrice)
                ps.setString(23, sellBills[i].unit)
                ps.setString(24, sellBills[i].noInvoiceQuantity)
                ps.setString(25, sellBills[i].noInvoiceDiscount)
                ps.setString(26, sellBills[i].noInvoiceAmount)
                ps.setString(27, sellBills[i].noInvoiceRealAmount)
                ps.setString(28, sellBills[i].tax)
                ps.setString(29, sellBills[i].taxAmount)
                ps.setString(30, sellBills[i].positionCode)
                ps.setString(31, sellBills[i].positionName)
                ps.setString(32, sellBills[i].costPrice)
                ps.setString(33, sellBills[i].costAmount)
                ps.setString(34, sellBills[i].remarks)
                ps.setString(35, sellBills[i].firstInTime)
            }

            override fun getBatchSize(): Int {
                return sellBills.size
            }
        })
    }

    override suspend fun deleteAllImSellBill() {
        val sql = "DELETE FROM IM_SELL_BILL"
        jdbcTemplate.update(sql)
    }
}
