package com.zhile.excelutil.dao.impl

import com.zhile.excelutil.dao.ImCostItemAccountRepository
import com.zhile.excelutil.entity.ImCostItemAccount
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement

@Repository
class ImCostItemAccountRepositoryImpl(private val jdbcTemplate: JdbcTemplate) : ImCostItemAccountRepository {

    override suspend fun batchInsertCostItems(costItemAccount: List<ImCostItemAccount>) {
        if (costItemAccount.isEmpty()) return

        val sql = """
            INSERT INTO IM_COST_ITEM_ACCOUNT
            (F_COST_ITEM_CODE, F_COST_ITEM_NAME, F_PARENT_COST_ITEM,
             F_TAX, F_REMARKS, F_PROT_COST_ACCT_CODE, F_PAY_PROT_COST_ACCT_CODE,
              F_SETT_COPE_ACCT_CODE, F_PRE_PAY_ACCT_CODE, F_PAY_ACCT_CODE, F_INPUT_TAX_ACCT_CODE, 
              F_INPUT_TAX_INVOICE_ACCT_CODE, F_VALUE_TAX_ACCT_CODE, F_CITY_MAINTAIN_ACCT_CODE, F_EDUCATION_ACCT_CODE,
               F_LOCAL_EDUCATION_ACCT_CODE, F_LABOR_TAX_ACCT_CODE, F_ROYALTIES_TAX_ACCT_CODE)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
        """.trimIndent()

        jdbcTemplate.batchUpdate(
            sql, object : BatchPreparedStatementSetter {
                override fun setValues(ps: PreparedStatement, i: Int) {
                    ps.setString(1, costItemAccount[i].costItemCode)
                    ps.setString(2, costItemAccount[i].costItemName)
                    ps.setString(3, costItemAccount[i].parentCostItem)
                    ps.setString(4, costItemAccount[i].tax)
                    ps.setString(5, costItemAccount[i].remarks)
                    ps.setString(6, costItemAccount[i].protCostAcctCode)
                    ps.setString(7, costItemAccount[i].payProtCostAcctCode)
                    ps.setString(8, costItemAccount[i].settCopeAcctCode)
                    ps.setString(9, costItemAccount[i].prePayAcctCode)
                    ps.setString(10, costItemAccount[i].payAcctCode)
                    ps.setString(11, costItemAccount[i].inputTaxAcctCode)
                    ps.setString(12, costItemAccount[i].inputTaxInvoiceAcctCode)
                    ps.setString(13, costItemAccount[i].valueTaxAcctCode)
                    ps.setString(14, costItemAccount[i].cityMaintainAcctCode)
                    ps.setString(15, costItemAccount[i].educationAcctCode)
                    ps.setString(16, costItemAccount[i].localEducationAcctCode)
                    ps.setString(17, costItemAccount[i].laborTaxAcctCode)
                    ps.setString(18, costItemAccount[i].royaltiesTaxAcctCode)
                }

                override fun getBatchSize(): Int {
                    return costItemAccount.size
                }
            }

        )
    }


    override suspend fun deleteAllImCostItemAccount() {
        val sql = "DELETE FROM IM_COST_ITEM_ACCOUNT"
        jdbcTemplate.update(sql)
    }
}
