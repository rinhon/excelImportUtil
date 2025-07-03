package com.zhile.excelutil.entity

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("IM_PURCHASE_INVOICE")
data class ImPurchaseInvoice(
    // 采购发票单号
    @Column("F_INVOICE_BILL_NO")
    var invoiceBillNo: String? = null,

    // 采购发票类型
    @Column("F_INVOICE_TYPE")
    var invoiceType: String? = null,

    // 采购发票号
    @Column("F_INVOICE_NO")
    var invoiceNo: String? = null,

    // 采购发票业务员编码
    @Column("F_INVOICE_USER_CODE")
    var invoiceUserCode: String? = null,

    // 采购发票业务员名称
    @Column("F_INVOICE_USER_NAME")
    var invoiceUserName: String? = null,

    // 采购发票业务部门编码
    @Column("F_INVOICE_DEPARTMENT_CODE")
    var invoiceDepartmentCode: String? = null,

    // 采购发票业务部门名称
    @Column("F_INVOICE_DEPARTMENT_NAME")
    var invoiceDepartmentName: String? = null,

    // 开票日期
    @Column("F_INVOICE_DATE")
    var invoiceDate: String? = null,

    // 备注
    @Column("F_REMARKS")
    var remarks: String? = null,

    // 采购订单号
    @Column("F_ORDER_BILL_NO")
    var orderBillNo: String? = null,

    // 采购订单日期
    @Column("F_ORDER_DATE")
    var orderDate: String? = null,

    // 采购订单行号
    @Column("F_ORDER_ROW_NO")
    var orderRowNo: String? = null,

    // 入库单据号
    @Column("F_IN_BILL_NO")
    var inBillNo: String? = null,

    // 入库日期
    @Column("F_IN_DATE")
    var inDate: String? = null,

    // 入库单行号
    @Column("F_IN_ROW_NO")
    var inRowNo: String? = null,

    // 仓库货位
    @Column("F_POSITION")
    var position: String? = null,

    // 供应商编码
    @Column("F_CUSTOMER_CODE")
    var customerCode: String? = null,

    // 供应商名称
    @Column("F_CUSTOMER_NAME")
    var customerName: String? = null,

    // 地区
    @Column("F_AREA")
    var area: String? = null,

    // 发货地址
    @Column("F_SEND_ADDRESS")
    var sendAddress: String? = null,

    // 发货人
    @Column("F_SEND_MAN")
    var sendMan: String? = null,

    // 发货电话
    @Column("F_SEND_PHONE")
    var sendPhone: String? = null,

    // 订书依据
    @Column("F_GIST")
    var gist: String? = null,

    // 采购订单业务员编码
    @Column("F_ORDER_USER_CODE")
    var orderUserCode: String? = null,

    // 采购订单业务员名称
    @Column("F_ORDER_USER_NAME")
    var orderUserName: String? = null,

    // 采购订单部门编码
    @Column("F_ORDER_DEPARTMENT_CODE")
    var orderDepartmentCode: String? = null,

    // 采购订单部门名称
    @Column("F_ORDER_DEPARTMENT_NAME")
    var orderDepartmentName: String? = null,

    // 图书编码
    @Column("F_ITEM_CODE")
    var itemCode: String? = null,

    // 图书名称
    @Column("F_ITEM_NAME")
    var itemName: String? = null,

    // 批次
    @Column("F_PRODUCE_NUM")
    var produceNum: String? = null,

    // 定价
    @Column("F_SET_PRICE")
    var setPrice: String? = null,

    // 扣率
    @Column("F_DISCOUNT")
    var discount: String? = null,

    // 计量单位
    @Column("F_UNIT")
    var unit: String? = null,

    // 开票未付款数量
    @Column("F_QUANTITY")
    var quantity: String? = null,

    // 开票未付款平均折扣
    @Column("F_AVERAGE_DISCOUNT")
    var averageDiscount: String? = null,

    // 开票未付款金额
    @Column("F_NO_INVOICE_AMOUNT")
    var noInvoiceAmount: String? = null,

    // 税率
    @Column("F_TAX")
    var tax: String? = null,

    // 税金
    @Column("F_TAX_AMOUNT")
    var taxAmount: String? = null,

    // 开户行名称
    @Column("F_ACCOUNT_BANK_NAME")
    var accountBankName: String? = null,

    // 开户分行名称
    @Column("F_BANK_NAME")
    var bankName: String? = null,

    // 账户名称
    @Column("F_ACCOUNT_NAME")
    var accountName: String? = null,

    // 银行账号
    @Column("F_ACCOUNT_NO")
    var accountNo: String? = null,

    // 记账日期
    @Column("F_ACCOUNT_DATE")
    var accountDate: String? = null,

    // 发票类型ID
    @Column("F_INVOICE_TYPE_ID")
    var invoiceTypeId: Long? = null,

    // 发票业务员ID
    @Column("F_INVOICE_USER_ID")
    var invoiceUserId: Long? = null,

    // 发票部门IDInvoice Department ID)
    @Column("F_INVOICE_DEPARTMENT_ID")
    var invoiceDepartmentId: Long? = null,

    // 货位IDPosition ID)
    @Column("F_POSITION_ID")
    var positionId: Long? = null,

    // 往来单位ID
    @Column("F_CUSTOMER_ID")
    var customerId: Long? = null,

    // 采购订单业务员ID
    @Column("F_ORDER_USER_ID")
    var orderUserId: Long? = null,

    // 采购订单部门ID
    @Column("F_ORDER_DEPARTMENT_ID")
    var orderDepartmentId: Long? = null,

    // 物品IDItem ID)
    @Column("F_ITEM_ID")
    var itemId: Long? = null,

    // 税率ID
    @Column("F_TAX_ID")
    var taxId: Long? = null,

    // 计量单位ID
    @Column("F_UNIT_ID")
    var unitId: Long? = null,

    // 采购订单ID
    @Column("F_ORDER_BILL_ID")
    var orderBillId: Long? = null,

    // 采购收货单ID
    @Column("F_RECEIVE_BILL_ID")
    var receiveBillId: Long? = null,

    // 采购入库单ID
    @Column("F_IN_BILL_ID")
    var inBillId: Long? = null,

    // 采购发票ID
    @Column("F_INVOICE_ID")
    var invoiceId: Long? = null,

    // 采购发票核销ID
    @Column("F_INVO_VERI_ID")
    var invoVeriId: Long? = null
)
