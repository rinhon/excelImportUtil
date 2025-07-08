package com.zhile.excelutil.dao.impl

import com.zhile.excelutil.dao.ImItemNatureAccountRepository
import com.zhile.excelutil.entity.ImItemNatureAccount
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement


@Repository
class ImItemNatureAccountRepositoryImpl(private val jdbcTemplate: JdbcTemplate) : ImItemNatureAccountRepository {

    override suspend fun deleteAllImItemNatureAccount() {
        val sql = "DELETE FROM IM_ITEM_NATURE_ACCOUNT"
        jdbcTemplate.update(sql)
    }

    override suspend fun batchInsertImItemNatureAccount(accounts: List<ImItemNatureAccount>) {
        if (accounts.isEmpty()) return

        val sql = """
            INSERT INTO IM_ITEM_NATURE_ACCOUNT (
                F_ITEM_NATURE, F_INVENTORY_ACCT_CODE, F_INCOME_ACCT_CODE, F_COST_ACCT_CODE,
                F_INPUT_TAX_ACCT_CODE, F_OUTPUT_TAX_ACCT_CODE, F_STOCK_OUT_ITEM_ACCT_CODE, F_PROL_ESTE_ACCT_CODE,
                F_PROL_ESTE_RECEIVE_ACCT_CODE, F_PROL_ESTE_INPUT_ACCT_CODE, F_PROL_ESTE_OUTPUT_ACCT_CODE
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                ps.setString(1, accounts[i].itemNature)
                ps.setString(2, accounts[i].inventoryAcctCode)
                ps.setString(3, accounts[i].incomeAcctCode)
                ps.setString(4, accounts[i].costAcctCode)
                ps.setString(5, accounts[i].inputTaxAcctCode)
                ps.setString(6, accounts[i].outputTaxAcctCode)
                ps.setString(7, accounts[i].stockOutItemAcctCode)
                ps.setString(8, accounts[i].prolEsteAcctCode)
                ps.setString(9, accounts[i].prolEsteReceiveAcctCode)
                ps.setString(10, accounts[i].prolEsteInputAcctCode)
                ps.setString(11, accounts[i].prolEsteOutputAcctCode)

            }

            override fun getBatchSize(): Int {
                return accounts.size
            }
        })
    }
}
