package com.zhile.excelutil.dao.impl

import com.zhile.excelutil.dao.ImCustomerBusinessSetRepository
import com.zhile.excelutil.entity.ImCustomerBusinessSet
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement

@Repository
class ImCustomerBusinessSetRepositoryImpl(private val jdbcTemplate: JdbcTemplate) : ImCustomerBusinessSetRepository {
    override suspend fun batchInsertCustomerBusinessSets(customerBusinessSets: List<ImCustomerBusinessSet>) {
        if (customerBusinessSets.isEmpty())
            return

        val sql = """
            INSERT INTO IM_CUSTOMER_BUSINESS_SET
           (F_CUSTOMER_CODE, F_CUSTOMER_NAME, F_PURCHASE_USER_CODE, F_PURCHASE_USER_NAME, F_PURCHASE_DEPARTMENT_CODE, F_PURCHASE_DEPARTMENT_NAME, F_SALE_USER_CODE, F_SALE_USER_NAME, F_SALE_DEPARTMENT_CODE, F_SALE_DEPARTMENT_NAME, F_REMARKS) VALUES (?,?,?,?,?,?,?,?,?,?,?)
        """.trimIndent()

        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                ps.setString(1, customerBusinessSets[i].customerCode)
                ps.setString(2, customerBusinessSets[i].customerName)
                ps.setString(3, customerBusinessSets[i].purchaseUserCode)
                ps.setString(4, customerBusinessSets[i].purchaseUserName)
                ps.setString(5, customerBusinessSets[i].purchaseDepartmentCode)
                ps.setString(6, customerBusinessSets[i].purchaseDepartmentName)
                ps.setString(7, customerBusinessSets[i].saleUserCode)
                ps.setString(8, customerBusinessSets[i].saleUserName)
                ps.setString(9, customerBusinessSets[i].saleDepartmentCode)
                ps.setString(10, customerBusinessSets[i].saleDepartmentName)
                ps.setString(11, customerBusinessSets[i].remarks)
            }

            override fun getBatchSize(): Int {
                return customerBusinessSets.size
            }
        })
    }

    override suspend fun deleteAllImCustomerBusinessSet() {
        jdbcTemplate.update("DELETE FROM IM_CUSTOMER_BUSINESS_SET")
    }
}
