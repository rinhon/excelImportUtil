package com.zhile.excelutil.entity

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("IM_COST_ITEM_ACCOUNT")
data class ImCostItemAccount(
    @Column("F_COST_ITEM_CODE")
    var costItemCode: String? = null,
    @Column("F_COST_ITEM_NAME")
    var costItemName: String? = null,
    @Column("F_PARENT_COST_ITEM")
    var parentCostItem: String? = null,
    @Column("F_TAX")
    var tax: String? = null,
    @Column("F_REMARKS")
    var remarks: String? = null,
    @Column("F_PROT_COST_ACCT_CODE")
    var protCostAcctCode: String? = null, //生产成本科目
    @Column("F_PAY_PROT_COST_ACCT_CODE")
    var payProtCostAcctCode: String? = null, //应付生产成本科目
    @Column("F_SETT_COPE_ACCT_CODE")
    var settCopeAcctCode: String? = null, //结算应付科目
    @Column("F_PRE_PAY_ACCT_CODE")
    var prePayAcctCode: String? = null, //预付科目
    @Column("F_PAY_ACCT_CODE")
    var payAcctCode: String? = null, //支付科目
    @Column("F_INPUT_TAX_ACCT_CODE")
    var inputTaxAcctCode: String? = null, //暂估进项税科目
    @Column("F_INPUT_TAX_INVOICE_ACCT_CODE")
    var inputTaxInvoiceAcctCode: String? = null, //进项税已开票
    @Column("F_VALUE_TAX_ACCT_CODE")
    var valueTaxAcctCode: String? = null, //增值税科目
    @Column("F_CITY_MAINTAIN_ACCT_CODE")
    var cityMaintainAcctCode: String? = null, //城市维护建设税科目
    @Column("F_EDUCATION_ACCT_CODE")
    var educationAcctCode: String? = null, //教育费附加科目
    @Column("F_LOCAL_EDUCATION_ACCT_CODE")
    var localEducationAcctCode: String? = null, //地方教育费附加科目
    @Column("F_LABOR_TAX_ACCT_CODE")
    var laborTaxAcctCode: String? = null, //劳务税科目
    @Column("F_ROYALTIES_TAX_ACCT_CODE")
    var royaltiesTaxAcctCode: String? = null, //稿酬税科目

    @Column("F_COST_ITEM_ID")
    var costItemId: Long? = null,

    )
