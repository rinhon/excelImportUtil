package com.zhile.excelutil.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("IM_ITEM_NATURE_ACCOUNT")
data class ImItemNatureAccount(
    @Id
    @Column("F_KEY_ID")
    var keyId: Long? = null,
    @Column("F_ITEM_NATURE")
    var itemNature: String? = null,
    @Column("F_INVENTORY_ACCT_CODE")
    var inventoryAcctCode: String? = null, // 存货科目
    @Column("F_INCOME_ACCT_CODE")
    var incomeAcctCode: String? = null, //主营业务收入科目
    @Column("F_COST_ACCT_CODE")
    var costAcctCode: String? = null, //主营业务成本科目
    @Column("F_INPUT_TAX_ACCT_CODE")
    var inputTaxAcctCode: String? = null, //进项税科目
    @Column("F_OUTPUT_TAX_ACCT_CODE")
    var outputTaxAcctCode: String? = null, //销项税科目
    @Column("F_STOCK_OUT_ITEM_ACCT_CODE")
    var stockOutItemAcctCode: String? = null, //发出商品科目
    @Column("F_PROL_ESTE_ACCT_CODE")
    var prolEsteAcctCode: String? = null, //暂估应付科目
    @Column("F_PROL_ESTE_RECEIVE_ACCT_CODE")
    var prolEsteReceiveAcctCode: String? = null, //暂估应收款科目
    @Column("F_PROL_ESTE_INPUT_ACCT_CODE")
    var prolEsteInputAcctCode: String? = null, //暂估进项科目
    @Column("F_PROL_ESTE_OUTPUT_ACCT_CODE")
    var prolEsteOutputAcctCode: String? = null,  //暂估销项科目
    @Column("F_ITEM_NATURE_ID")
    var itemNatureId: Long? = null
)