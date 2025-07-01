package com.zhile.excelutil.entity

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("IM_SELL_INVOICE")
data class ImSellInvoice(
    //销售发票单据号
    @Column("F_INVOICE_NO")
    var invoiceNo: String? = null,
    //发票类型
    @Column("F_INVOICE_TYPE")
    var invoiceType: String? = null,
    //发票号
    @Column("F_INVOICE_NUMBER")
    var invoiceNumber: String? = null,
    //发票业务员编码
    @Column("F_INVOICE_USER_CODE")
    var invoiceUserCode: String? = null,
    //发票业务员名称
    @Column("F_INVOICE_USER_NAME")
    var invoiceUserName: String? = null,
    //发票部门编码
    @Column("F_INVOICE_DEPARTMENT_CODE")
    var invoiceDepartmentCode: String? = null,
    //发票部门名称
    @Column("F_INVOICE_DEPARTMENT_NAME")
    var invoiceDepartmentName: String? = null,
    //开票日期
    @Column("F_INVOICE_DATE")
    var invoiceDate: String? = null,
    //开票单位
    @Column("F_INVOICE_CUSTOMER")
    var invoiceCustomer: String? = null,
    //备注
    @Column("F_REMARKS")
    var remarks: String? = null,

    //调减金额
    @Column("F_ADJUST_AMOUNT")
    var adjustAmount: String? = null,
    //调减后税金
    @Column("F_ADJUST_TAX_AMOUNT")
    var adjustTaxAmount: String? = null,
    //销售订单单据号
    @Column("F_ORDER_BILL_NO")
    var orderBillNo: String? = null,
    //订单日期
    @Column("F_ORDER_BILL_DATE")
    var orderBillDate: String? = null,
    //订单行号
    @Column("F_ORADER_BILL_ROW_NO")
    var orderBillRowNo: String? = null,
    //出库单单据号
    @Column("F_OUT_BILL_NO")
    var outBillNo: String? = null,
    //出库单日期
    @Column("F_OUT_BILL_DATE")
    var outBillDate: String? = null,
    //出库单行号
    @Column("F_OUT_BILL_ROW_NO")
    var outBillRowNo: String? = null,
    //货位编码
    @Column("F_POSITION_CODE")
    var positionCode: String? = null,
    //货位名称
    @Column("F_POSITION_NAME")
    var positionName: String? = null,
    //往来单位编码
    @Column("F_CUSTOMER_CODE")
    var customerCode: String? = null,
    //往来单位名称
    @Column("F_CUSTOMER_NAME")
    var customerName: String? = null,
    //地区
    @Column("F_AREA")
    var area: String? = null,
    //收货地址
    @Column("F_RECEIVE_ADDRESS")
    var receiveAddress: String? = null,
    //收货人
    @Column("F_RECEIVE_MAN")
    var receiveMan: String? = null,
    //收货电话
    @Column("F_RECEIVE_PHONE")
    var receivePhone: String? = null,
    //订书依据
    @Column("F_GIST")
    var gist: String? = null,
    //订单业务员编码
    //订单业务员姓名
    //订单部门编码
    //订单部门名称
    @Column("F_ORDER_USER_CODE")
    var orderUserCode: String? = null,
    @Column("F_ORDER_USER_NAME")
    var orderUserName: String? = null,
    @Column("F_ORDER_DEPARTMENT_CODE")
    var orderDepartmentCode: String? = null,
    @Column("F_ORDER_DEPARTMENT_NAME")
    var orderDepartmentName: String? = null,
    //物品编码
    //物品名称
    @Column("F_ITEM_CODE")
    var itemCode: String? = null,
    @Column("F_ITEM_NAME")
    var itemName: String? = null,
    //批次
    @Column("F_PRODUCE_NUM")
    var produceNum: String? = null,
    //定价
    @Column("F_SET_PRICE")
    var setPrice: String? = null,
    //计量单位
    @Column("F_UNIT")
    var unit: String? = null,
    //开票未收款数量
    @Column("F_QUANTITY")
    var quantity: String? = null,
    //平均折扣
    @Column("F_DISCOUNT")
    var discount: String? = null,
    //开票未收款金额
    @Column("F_AMOUNT")
    var amount: String? = null,
    //税率
    @Column("F_TAX")
    var tax: String? = null,
    //税金
    @Column("F_TAX_AMOUNT")
    var taxAmount: String? = null,
    //成本单价


    @Column("F_COST_PRICE")
    var costPrice: String? = null,
    //成本金额

    @Column("F_COST_AMOUNT")
    var costAmount: String? = null,
    //记账日期

    @Column("F_ACCOUNT_DATE")
    var accountDate: String? = null,
    //调账后不含税金额
    @Column("F_ALLOCATED_NO_TAX_AMOUNT")
    var allocatedNoTaxAmount: String? = null,
    //往来单位ID
    //物品ID
    //单位ID
    //税率ID
    //货位ID
    //部门ID
    //发票类型ID
    //发票业务员ID
    //发票部门ID
    //销售订单ID
    //销售发货单ID
    //销售出库单ID
    //销售发票ID
    //销售发票核销ID

)
