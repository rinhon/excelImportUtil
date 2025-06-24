package com.zhile.excelutil.entity

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("IM_SELL_BILL")
data class ImSellBill(
    //销售订单号
    @Column("F_ORDER_BILL_NO")
    var orderBillNo: String? = null,
    //订单日期
    @Column("F_ORDER_BILL_DATE")
    var orderBillDate: String? = null,
    //销售订单行号
    @Column("F_ORDER_BILL_ROWNUM")
    var orderBillRownum: String? = null,
    //出库单据号
    @Column("F_OUT_BILL_NO")
    var outBillNo: String? = null,
    //出库单据日期
    @Column("F_OUT_BILL_DATE")
    var outBillDate: String? = null,
    //出库单行号
    @Column("F_OUT_BILL_ROWNUM")
    var outBillRownum: String? = null,
    //往来单位编码
    @Column("F_CUSTOMER_CODE")
    var customerCode: String? = null,
    //往来单位名称
    @Column("F_CUSTOMER_NAME")
    var customerName: String? = null,
    //地区
    @Column("F_AREA")
    var area: String? = null,
    @Column("F_RECEIVING_ADDRESS")                                                          //收货地址
    var receivingAddress: String? = null,
    @Column("F_RECEIVING_LINKMAN")                                                          //收货人
    var receivingLinkman: String? = null,
    @Column("F_RECEIVING_LINKMAN_TEL")                                                          //收货电话
    var receivingLinkmanTel: String? = null,
    @Column("F_GIST")                                                          //订书依据
    var gist: String? = null,
    @Column("F_USER_CODE")                                                          //业务员编码
    var userCode: String? = null,
    @Column("F_USER_NAME")                                                          //业务员名称
    var userName: String? = null,
    @Column("F_DEPARTMENT_CODE")                                                          //部门编码
    var departmentCode: String? = null,
    @Column("F_DEPARTMENT_NAME")                                                          //部门名称
    var departmentName: String? = null,
    @Column("F_ITEM_CODE")                                                          //物品编码
    var itemCode: String? = null,
    @Column("F_ITEM_NAME")                                                          //物品名称
    var itemName: String? = null,
    @Column("F_PRODUCE_NUM")                                                          //批次
    var produceNum: String? = null,
    @Column("F_SET_PRICE")                                                          //定价
    var setPrice: String? = null,
    @Column("F_UNIT")                                                          //计量单位
    var unit: String? = null,
    @Column("F_NO_INVOICE_QUANTITY")                                                          //未开票数量
    var noInvoiceQuantity: String? = null,
    @Column("F_NO_INVOICE_DISCOUNT")                                                          //未开票平均折扣
    var noInvoiceDiscount: String? = null,
    @Column("F_NO_INVOICE_AMOUNT")                                                          //未开票码洋
    var noInvoiceAmount: String? = null,
    @Column("F_NO_INVOICE_REAL_AMOUNT")                                                          //未开票金额_实洋
    var noInvoiceRealAmount: String? = null,
    @Column("F_TAX")                                                          //税率
    var tax: String? = null,
    @Column("F_TAX_AMOUNT")                                                          //税金
    var taxAmount: String? = null,
    @Column("F_POSITION")                                                          //仓库货位
    var position: String? = null,
    @Column("F_COST_PRICE")                                                          //成本单价
    var costPrice: String? = null,
    @Column("F_COST_AMOUNT")                                                          //成本金额
    var costAmount: String? = null,
    @Column("F_REMARKS")                                                          //备注
    var remarks: String? = null,
    @Column("F_FIRST_IN_TIME")                                                          //首次入库日期
    var firstInTime: String? = null,

    var id: String? = null
)
