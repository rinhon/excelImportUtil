package com.zhile.excelutil.entity

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

/**
 * @author Rinhon
 * @date 2025/7/3 13:11
 * @description:
 */
@Table("IM_FEE_BILL")
data class ImFeeBill(
    // 物品编码
    @Column("F_ITEM_CODE")
    var itemCode: String? = null,

    // 物品名称
    @Column("F_ITEM_NAME")
    var itemName: String? = null,

    // 印次
    @Column("F_PRINTING_NO")
    var printingNo: String? = null,

    // 预估单号
    @Column("F_FEE_ESTIMATE_BILL_NO")
    var feeEstimateBillNo: String? = null,

    // 预估单行号
    @Column("F_FEE_ESTIMATE_ROW_NUM")
    var feeEstimateRowNum: String? = null,

    // 费用项目
    @Column("F_COST_ITEM")
    var costItem: String? = null,

    // 往来单位编码 (预估)
    @Column("F_CUSTOMER_CODE")
    var customerCode: String? = null,

    // 往来单位名称 (预估)
    @Column("F_CUSTOMER_NAME")
    var customerName: String? = null,

    // 业务日期 (预估)
    @Column("F_ESTIMATE_DATE")
    var estimateDate: String? = null,

    // 记账日期 (预估)
    @Column("F_ESTIMATE_VOUCHER_DATE")
    var estimateVoucherDate: String? = null,

    // 业务部门编码 (预估)
    @Column("F_ESTIMATE_DEPARTMENT_CODE")
    var estimateDepartmentCode: String? = null,

    // 业务部门名称 (预估)
    @Column("F_ESTIMATE_DEPARTMENT_NAME")
    var estimateDepartmentName: String? = null,

    // 业务员编码 (预估)
    @Column("F_ESTIMATE_USER_CODE")
    var estimateUserCode: String? = null,

    // 业务员名称 (预估)
    @Column("F_ESTIMATE_USER_NAME")
    var estimateUserName: String? = null,

    // 自备材料金额
    @Column("F_SELF_MATERIAL_AMOUNT")
    var selfMaterialAmount: String? = null,

    // 预估金额(元)
    @Column("F_ESTIMATE_AMOUNT")
    var estimateAmount: String? = null,

    // 税率(%) (预估)
    @Column("F_ESTIMATE_TAX")
    var estimateTax: String? = null,

    // 暂估税金(元)
    @Column("F_ESTIMATE_TAX_AMOUNT")
    var estimateTaxAmount: String? = null,

    // 结算单号
    @Column("F_FEE_SETTLE_BILL_NO")
    var feeSettleBillNo: String? = null,

    // 结算单行号
    @Column("F_FEE_SETTLE_ROW_NUM")
    var feeSettleRowNum: String? = null,

    // 往来单位编码 (结算)
    @Column("F_SETTLE_CUSTOMER_CODE")
    var settleCustomerCode: String? = null,

    // 往来单位名称 (结算)
    @Column("F_SETTLE_CUSTOMER_NAME")
    var settleCustomerName: String? = null,

    // 业务日期 (结算)
    @Column("F_SETTLE_DATE")
    var settleDate: String? = null,

    // 记账日期 (结算)
    @Column("F_SETTLE_VOUCHER_DATE")
    var settleVoucherDate: String? = null,

    // 业务部门编码 (结算)
    @Column("F_SETTLE_DEPARTMENT_CODE")
    var settleDepartmentCode: String? = null,

    // 业务部门名称 (结算)
    @Column("F_SETTLE_DEPARTMENT_NAME")
    var settleDepartmentName: String? = null,

    // 业务员编码 (结算)
    @Column("F_SETTLE_USER_CODE")
    var settleUserCode: String? = null,

    // 业务员名称 (结算)
    @Column("F_SETTLE_USER_NAME")
    var settleUserName: String? = null,

    // 本次结算金额(元)
    @Column("F_SETTLE_AMOUNT")
    var settleAmount: String? = null,

    // 税率(%) (结算)
    @Column("F_SETTLE_TAX")
    var settleTax: String? = null,

    // 税金(元) (结算)
    @Column("F_SETTLE_TAX_AMOUNT")
    var settleTaxAmount: String? = null,

    // 增值税(元)
    @Column("F_ADD_VALUE_TAX")
    var addValueTax: String? = null,

    // 城建税(元)
    @Column("F_URBAN_CONSTRUCT_TAX")
    var urbanConstructTax: String? = null,

    // 教育费附加税(元)
    @Column("F_EDUCATE_ADDITION_TAX")
    var educateAdditionTax: String? = null,

    // 地方教育附加税(元)
    @Column("F_LOCAL_EDUCATE_ADDITION_TAX")
    var localEducateAdditionTax: String? = null,

    // 应纳税所得额(元)
    @Column("F_TAXABLE_INCOME")
    var taxableIncome: String? = null,

    // 其他扣款金额(元)
    @Column("F_OTHER_AMOUNT")
    var otherAmount: String? = null,

    // 扣款原因(元)
    @Column("F_OTHER_REASON")
    var otherReason: String? = null,

    // 发票类型
    @Column("F_INVOICE_TYPE")
    var invoiceType: String? = null,

    // 发票号
    @Column("F_INVOICE_NUMBER")
    var invoiceNumber: String? = null,

    // 是否结算完成
    @Column("F_SETTLE_COMPLETION")
    var settleCompletion: String? = null,

    // 收款方-开户行名称
    @Column("F_CUSTOMER_ACCOUNT_BANK_NAME")
    var customerAccountBankName: String? = null,

    // 收款方-开户分行名称
    @Column("F_CUSTOMER_BANK_NAME")
    var customerBankName: String? = null,

    // 收款方-账户名称
    @Column("F_CUSTOMER_ACCOUNT_NAME")
    var customerAccountName: String? = null,

    // 收款方-银行账号
    @Column("F_CUSTOMER_ACCOUNT_NO")
    var customerAccountNo: String? = null,

    // 付款方-开户行名称
    @Column("F_ORGAN_BANK_NAME")
    var organBankName: String? = null,

    // 付款方-银行账号
    @Column("F_ORGAN_ACCOUNT_NO")
    var organAccountNo: String? = null,

    // 备注 (结算)
    @Column("F_REMARKS")
    var remarks: String? = null,

    // 付款单号
    @Column("F_PAY_BILL_NO")
    var payBillNo: String? = null,

    // 付款单行号
    @Column("F_PAY_ROW_NUM")
    var payRowNum: String? = null,

    // 业务日期 (付款)
    @Column("F_PAY_DATE")
    var payDate: String? = null,

    // 记账日期 (付款)
    @Column("F_PAY_VOUCHER_DATE")
    var payVoucherDate: String? = null,

    // 业务部门编码 (付款)
    @Column("F_PAY_DEPARTMENT_CODE")
    var payDepartmentCode: String? = null,

    // 业务部门名称 (付款)
    @Column("F_PAY_DEPARTMENT_NAME")
    var payDepartmentName: String? = null,

    // 业务员编码 (付款)
    @Column("F_PAY_USER_CODE")
    var payUserCode: String? = null,

    // 业务员名称 (付款)
    @Column("F_PAY_USER_NAME")
    var payUserName: String? = null,

    // 本次支付金额(元)
    @Column("F_PAY_AMOUNT")
    var payAmount: String? = null,

    // 税金(元) (付款)
    @Column("F_PAY_TAX_AMOUNT")
    var payTaxAmount: String? = null,

    // 机构ID
    @Column("F_ORGAN_ID")
    var organId: Long? = null
)
