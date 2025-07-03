package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImFeeBill
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * @author zlhp
 * @date 2025/7/3 13:14
 * @description:
 */
@Repository
interface ImFeeBillRepository : CrudRepository<ImFeeBill, String> { // 主键类型为 String

    // 自定义插入方法
    @Modifying
    @Query(
        """
        INSERT INTO IM_FEE_BILL (
            F_ITEM_CODE, F_ITEM_NAME, F_PRINTING_NO, F_FEE_ESTIMATE_BILL_NO,
            F_FEE_ESTIMATE_ROW_NUM, F_COST_ITEM, F_CUSTOMER_CODE, F_CUSTOMER_NAME,
            F_ESTIMATE_DATE, F_ESTIMATE_VOUCHER_DATE, F_ESTIMATE_DEPARTMENT_CODE,
            F_ESTIMATE_DEPARTMENT_NAME, F_ESTIMATE_USER_CODE, F_ESTIMATE_USER_NAME,
            F_SELF_MATERIAL_AMOUNT, F_ESTIMATE_AMOUNT, F_ESTIMATE_TAX, F_ESTIMATE_TAX_AMOUNT,
            F_FEE_SETTLE_BILL_NO, F_FEE_SETTLE_ROW_NUM, F_SETTLE_CUSTOMER_CODE,
            F_SETTLE_CUSTOMER_NAME, F_SETTLE_DATE, F_SETTLE_VOUCHER_DATE,
            F_SETTLE_DEPARTMENT_CODE, F_SETTLE_DEPARTMENT_NAME, F_SETTLE_USER_CODE,
            F_SETTLE_USER_NAME, F_SETTLE_AMOUNT, F_SETTLE_TAX, F_SETTLE_TAX_AMOUNT,
            F_ADD_VALUE_TAX, F_URBAN_CONSTRUCT_TAX, F_EDUCATE_ADDITION_TAX,
            F_LOCAL_EDUCATE_ADDITION_TAX, F_TAXABLE_INCOME, F_OTHER_AMOUNT,
            F_OTHER_REASON, F_INVOICE_TYPE, F_INVOICE_NUMBER, F_SETTLE_COMPLETION,
            F_CUSTOMER_ACCOUNT_BANK_NAME, F_CUSTOMER_BANK_NAME, F_CUSTOMER_ACCOUNT_NAME,
            F_CUSTOMER_ACCOUNT_NO, F_ORGAN_BANK_NAME, F_ORGAN_ACCOUNT_NO, F_REMARKS,
            F_PAY_BILL_NO, F_PAY_ROW_NUM, F_PAY_DATE, F_PAY_VOUCHER_DATE,
            F_PAY_DEPARTMENT_CODE, F_PAY_DEPARTMENT_NAME, F_PAY_USER_CODE,
            F_PAY_USER_NAME, F_PAY_AMOUNT, F_PAY_TAX_AMOUNT, F_ORGAN_ID
        ) VALUES (
            :itemCode, :itemName, :printingNo, :feeEstimateBillNo,
            :feeEstimateRowNum, :costItem, :customerCode, :customerName,
            :estimateDate, :estimateVoucherDate, :estimateDepartmentCode,
            :estimateDepartmentName, :estimateUserCode, :estimateUserName,
            :selfMaterialAmount, :estimateAmount, :estimateTax, :estimateTaxAmount,
            :feeSettleBillNo, :feeSettleRowNum, :settleCustomerCode,
            :settleCustomerName, :settleDate, :settleVoucherDate,
            :settleDepartmentCode, :settleDepartmentName, :settleUserCode,
            :settleUserName, :settleAmount, :settleTax, :settleTaxAmount,
            :addValueTax, :urbanConstructTax, :educateAdditionTax,
            :localEducateAdditionTax, :taxableIncome, :otherAmount,
            :otherReason, :invoiceType, :invoiceNumber, :settleCompletion,
            :customerAccountBankName, :customerBankName, :customerAccountName,
            :customerAccountNo, :organBankName, :organAccountNo, :remarks,
            :payBillNo, :payRowNum, :payDate, :payVoucherDate,
            :payDepartmentCode, :payDepartmentName, :payUserCode,
            :payUserName, :payAmount, :payTaxAmount, :organId
        )
        """
    )
    fun insertFeeBill(
        @Param("itemCode") itemCode: String?,
        @Param("itemName") itemName: String?,
        @Param("printingNo") printingNo: String?,
        @Param("feeEstimateBillNo") feeEstimateBillNo: String?,
        @Param("feeEstimateRowNum") feeEstimateRowNum: String?,
        @Param("costItem") costItem: String?,
        @Param("customerCode") customerCode: String?,
        @Param("customerName") customerName: String?,
        @Param("estimateDate") estimateDate: String?,
        @Param("estimateVoucherDate") estimateVoucherDate: String?,
        @Param("estimateDepartmentCode") estimateDepartmentCode: String?,
        @Param("estimateDepartmentName") estimateDepartmentName: String?,
        @Param("estimateUserCode") estimateUserCode: String?,
        @Param("estimateUserName") estimateUserName: String?,
        @Param("selfMaterialAmount") selfMaterialAmount: String?,
        @Param("estimateAmount") estimateAmount: String?,
        @Param("estimateTax") estimateTax: String?,
        @Param("estimateTaxAmount") estimateTaxAmount: String?,
        @Param("feeSettleBillNo") feeSettleBillNo: String?,
        @Param("feeSettleRowNum") feeSettleRowNum: String?,
        @Param("settleCustomerCode") settleCustomerCode: String?,
        @Param("settleCustomerName") settleCustomerName: String?,
        @Param("settleDate") settleDate: String?,
        @Param("settleVoucherDate") settleVoucherDate: String?,
        @Param("settleDepartmentCode") settleDepartmentCode: String?,
        @Param("settleDepartmentName") settleDepartmentName: String?,
        @Param("settleUserCode") settleUserCode: String?,
        @Param("settleUserName") settleUserName: String?,
        @Param("settleAmount") settleAmount: String?,
        @Param("settleTax") settleTax: String?,
        @Param("settleTaxAmount") settleTaxAmount: String?,
        @Param("addValueTax") addValueTax: String?,
        @Param("urbanConstructTax") urbanConstructTax: String?,
        @Param("educateAdditionTax") educateAdditionTax: String?,
        @Param("localEducateAdditionTax") localEducateAdditionTax: String?,
        @Param("taxableIncome") taxableIncome: String?,
        @Param("otherAmount") otherAmount: String?,
        @Param("otherReason") otherReason: String?,
        @Param("invoiceType") invoiceType: String?,
        @Param("invoiceNumber") invoiceNumber: String?,
        @Param("settleCompletion") settleCompletion: String?,
        @Param("customerAccountBankName") customerAccountBankName: String?,
        @Param("customerBankName") customerBankName: String?,
        @Param("customerAccountName") customerAccountName: String?,
        @Param("customerAccountNo") customerAccountNo: String?,
        @Param("organBankName") organBankName: String?,
        @Param("organAccountNo") organAccountNo: String?,
        @Param("remarks") remarks: String?,
        @Param("payBillNo") payBillNo: String?,
        @Param("payRowNum") payRowNum: String?,
        @Param("payDate") payDate: String?,
        @Param("payVoucherDate") payVoucherDate: String?,
        @Param("payDepartmentCode") payDepartmentCode: String?,
        @Param("payDepartmentName") payDepartmentName: String?,
        @Param("payUserCode") payUserCode: String?,
        @Param("payUserName") payUserName: String?,
        @Param("payAmount") payAmount: String?,
        @Param("payTaxAmount") payTaxAmount: String?,
        @Param("organId") organId: Long?
    )

    @Modifying
    @Query(
        """
            DELETE FROM IM_FEE_BILL
        """
    )
    fun deleteAllFeeBill()
}
