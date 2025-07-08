package com.zhile.excelutil.dao.impl

import com.zhile.excelutil.dao.ImFeeBillRepository
import com.zhile.excelutil.entity.ImFeeBill
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement

@Repository
class ImFeeBillRepositoryImpl(private val jdbcTemplate: JdbcTemplate) : ImFeeBillRepository {
    override fun deleteAllFeeBill() {
        jdbcTemplate.update("DELETE FROM IM_FEE_BILL")
    }

    override fun batchInsertFeeBills(feeBills: List<ImFeeBill>) {
        if (feeBills.isEmpty()) {
            return
        }
        val sql = """
                INSERT INTO IM_FEE_BILL
                (F_ITEM_CODE, F_ITEM_NAME, F_PRINTING_NO, F_FEE_ESTIMATE_BILL_NO, F_FEE_ESTIMATE_ROW_NUM, F_COST_ITEM, F_CUSTOMER_CODE, F_CUSTOMER_NAME, F_ESTIMATE_DATE, F_ESTIMATE_VOUCHER_DATE, F_ESTIMATE_DEPARTMENT_CODE, F_ESTIMATE_DEPARTMENT_NAME, F_ESTIMATE_USER_CODE, F_ESTIMATE_USER_NAME, F_SELF_MATERIAL_AMOUNT, F_ESTIMATE_AMOUNT, F_ESTIMATE_TAX, F_ESTIMATE_TAX_AMOUNT, F_FEE_SETTLE_BILL_NO, F_FEE_SETTLE_ROW_NUM, F_SETTLE_CUSTOMER_CODE, F_SETTLE_CUSTOMER_NAME, F_SETTLE_DATE, F_SETTLE_VOUCHER_DATE, F_SETTLE_DEPARTMENT_CODE, F_SETTLE_DEPARTMENT_NAME, F_SETTLE_USER_CODE, F_SETTLE_USER_NAME, F_SETTLE_AMOUNT, F_SETTLE_TAX, F_SETTLE_TAX_AMOUNT, F_ADD_VALUE_TAX, F_URBAN_CONSTRUCT_TAX, F_EDUCATE_ADDITION_TAX, F_LOCAL_EDUCATE_ADDITION_TAX, F_TAXABLE_INCOME, F_OTHER_AMOUNT, F_OTHER_REASON, F_INVOICE_TYPE, F_INVOICE_NUMBER, F_SETTLE_COMPLETION, F_CUSTOMER_ACCOUNT_BANK_NAME, F_CUSTOMER_BANK_NAME, F_CUSTOMER_ACCOUNT_NAME, F_CUSTOMER_ACCOUNT_NO, F_ORGAN_BANK_NAME, F_ORGAN_ACCOUNT_NO, F_REMARKS, F_PAY_BILL_NO, F_PAY_ROW_NUM, F_PAY_DATE, F_PAY_VOUCHER_DATE, F_PAY_DEPARTMENT_CODE, F_PAY_DEPARTMENT_NAME, F_PAY_USER_CODE, F_PAY_USER_NAME, F_PAY_AMOUNT, F_PAY_TAX_AMOUNT, F_ORGAN_ID) 
                VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """.trimIndent()
        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                val feeBill = feeBills[i]
                ps.setString(1, feeBill.itemCode)
                ps.setString(2, feeBill.itemName)
                ps.setString(3, feeBill.printingNo)
                ps.setString(4, feeBill.feeEstimateBillNo)
                ps.setString(5, feeBill.feeEstimateRowNum)
                ps.setString(6, feeBill.costItem)
                ps.setString(7, feeBill.customerCode)
                ps.setString(8, feeBill.customerName)
                ps.setString(9, feeBill.estimateDate)
                ps.setString(10, feeBill.estimateVoucherDate)
                ps.setString(11, feeBill.estimateDepartmentCode)
                ps.setString(12, feeBill.estimateDepartmentName)
                ps.setString(13, feeBill.estimateUserCode)
                ps.setString(14, feeBill.estimateUserName)
                ps.setString(15, feeBill.selfMaterialAmount)
                ps.setString(16, feeBill.estimateAmount)
                ps.setString(17, feeBill.estimateTax)
                ps.setString(18, feeBill.estimateTaxAmount)
                ps.setString(19, feeBill.feeSettleBillNo)
                ps.setString(20, feeBill.feeSettleRowNum)
                ps.setString(21, feeBill.settleCustomerCode)
                ps.setString(22, feeBill.settleCustomerName)
                ps.setString(23, feeBill.settleDate)
                ps.setString(24, feeBill.settleVoucherDate)
                ps.setString(25, feeBill.settleDepartmentCode)
                ps.setString(26, feeBill.settleDepartmentName)
                ps.setString(27, feeBill.settleUserCode)
                ps.setString(28, feeBill.settleUserName)
                ps.setString(29, feeBill.settleAmount)
                ps.setString(30, feeBill.settleTax)
                ps.setString(31, feeBill.settleTaxAmount)
                ps.setString(32, feeBill.addValueTax)
                ps.setString(33, feeBill.urbanConstructTax)
                ps.setString(34, feeBill.educateAdditionTax)
                ps.setString(35, feeBill.localEducateAdditionTax)
                ps.setString(36, feeBill.taxableIncome)
                ps.setString(37, feeBill.otherAmount)
                ps.setString(38, feeBill.otherReason)
                ps.setString(39, feeBill.invoiceType)
                ps.setString(40, feeBill.invoiceNumber)
                ps.setString(41, feeBill.settleCompletion)
                ps.setString(42, feeBill.customerAccountBankName)
                ps.setString(43, feeBill.customerBankName)
                ps.setString(44, feeBill.customerAccountName)
                ps.setString(45, feeBill.customerAccountNo)
                ps.setString(46, feeBill.organBankName)
                ps.setString(47, feeBill.organAccountNo)
                ps.setString(48, feeBill.remarks)
                ps.setString(49, feeBill.payBillNo)
                ps.setString(50, feeBill.payRowNum)
                ps.setString(51, feeBill.payDate)
                ps.setString(52, feeBill.payVoucherDate)
                ps.setString(53, feeBill.payDepartmentCode)
                ps.setString(54, feeBill.payDepartmentName)
                ps.setString(55, feeBill.payUserCode)
                ps.setString(56, feeBill.payUserName)
                ps.setString(57, feeBill.payAmount)
                ps.setString(58, feeBill.payTaxAmount)
                ps.setString(59, feeBill.organId.toString())
            }

            override fun getBatchSize(): Int {
                return feeBills.size
            }

        })
    }
}
