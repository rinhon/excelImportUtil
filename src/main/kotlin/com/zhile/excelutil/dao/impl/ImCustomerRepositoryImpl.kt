package com.zhile.excelutil.dao.impl

import com.zhile.excelutil.dao.ImCustomerRepository
import com.zhile.excelutil.entity.ImCustomer
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement

@Repository
class ImCustomerRepositoryImpl(private val jdbcTemplate: JdbcTemplate) : ImCustomerRepository {
    override suspend fun deleteAllImCustomer() {
        jdbcTemplate.update("DELETE FROM IM_CUSTOMER")
    }

    override suspend fun batchInsertSellReserves(customers: List<ImCustomer>) {
        if (customers.isEmpty()) {
            return
        }

        val sql = """
            INSERT INTO IM_CUSTOMER
            (F_CODE, F_NAME, F_ABBR, F_CATALOG, F_DYNASTY, F_IN_UNIT, F_NATURE1, F_NATURE2, F_CUSTOMER_TYPE, F_AREA, F_CARD_TYPE, F_CARD_NO, F_CORRESPONDENCE_CONTACT, F_CORRESPONDENCE_TEL, F_CORRESPONDENCE_ADDRESS, F_ACCOUNT_NAME, F_ACCOUNT_NO, F_BANK_NAME)
             VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
        """.trimIndent()

        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                val customer = customers[i] // Get the current customer object for this batch item

                // Set values for each placeholder in the SQL query
                ps.setString(1, customer.code)
                ps.setString(2, customer.name)
                ps.setString(3, customer.abbr)
                ps.setString(4, customer.catalog)
                ps.setString(5, customer.dynasty)
                ps.setString(6, customer.inUnit)
                ps.setString(7, customer.nature1)
                ps.setString(8, customer.nature2)
                ps.setString(9, customer.customerType)
                ps.setString(10, customer.area)
                ps.setString(11, customer.cardType)
                ps.setString(12, customer.cardNo)
                ps.setString(13, customer.correspondenceContact)
                ps.setString(14, customer.correspondenceTel)
                ps.setString(15, customer.correspondenceAddress)
                ps.setString(16, customer.accountName)
                ps.setString(17, customer.accountNo)
                ps.setString(18, customer.bankName)

            }

            override fun getBatchSize(): Int {
                return customers.size
            }

        })
    }
}
