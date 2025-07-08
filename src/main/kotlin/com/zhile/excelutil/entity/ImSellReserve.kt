package com.zhile.excelutil.entity

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("IM_SELL_RESERVE")
data class ImSellReserve(
    //单据号
    @Column("F_BILL_NO")
    var billNo: String? = null,
    //单位编码
    @Column("F_CUSTOMER_CODE")
    var customerCode: String? = null,
    //单位名称
    @Column("F_CUSTOMER_NAME")
    var customerName: String? = null,
    //业务日期
    @Column("F_DATE")
    var date: String? = null,
    //业务部门编码
    @Column("F_DEPARTMENT_CODE")
    var departmentCode: String? = null,
    //业务部门名称
    @Column("F_DEPARTMENT_NAME")
    var departmentName: String? = null,
    //业务员
    @Column("F_USER_CODE")
    var userCode: String? = null,
    //业务员
    @Column("F_USER_NAME")
    var userName: String? = null,
    //是否开票
    @Column("F_IS_INVOICE")
    var isInvoice: String? = null,
    //发票类型
    @Column("F_INVOICE_TYPE")
    var invoiceType: String? = null,
    //发票号
    @Column("F_INVOICE_NO")
    var invoiceNo: String? = null,
    //开票日期
    @Column("F_INVOICE_DATE")
    var invoiceDate: String? = null,
    //开票单位
    @Column("F_INVOICE_CUSTOMER")
    var invoiceCustomer: String? = null,
    //合同号
    @Column("F_CONTRACT_NO")
    var contractNo: String? = null,
    //收款日期
    @Column("F_RECEIVE_DATE")
    var receiveDate: String? = null,
    //物品编码
    @Column("F_ITEM_CODE")
    var itemCode: String? = null,
    //物品名称
    @Column("F_ITEM_NAME")
    var itemName: String? = null,
    @Column("F_TOPIC_APPLY")
    var topicApply: String? = null,
    //数量
    @Column("F_QUANTITY")
    var quantity: String? = null,
    //不含税金额
    @Column("F_AMOUNT")
    var amount: String? = null,
    //税率
    @Column("F_TAX")
    var tax: String? = null,
    //税额
    @Column("F_TAX_AMOUNT")
    var taxAmount: String? = null,
    //实洋金额
    @Column("F_REAL_AMOUNT")
    var realAmount: String? = null,
    //备注
    @Column("F_REMARKS")
    var remarks: String? = null,
    //收款金额
    @Column("F_IN_AMOUNT")
    var inAmount: String? = null,

    @Column("F_ACCOUNT_DATE")
    var accountDate: String? = null

)
