package com.zhile.excelutil.entity

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("IM_STOCK_INIT")
data class ImStockInit(
    @Column("F_ITEM_CODE")
    var itemCode: String? = null,
    @Column("F_ITEM_NAME")
    var itemName: String? = null,
    @Column("F_ISBN")
    var isbn: String? = null,
    @Column("F_SPEC")
    var spec: String? = null,
    @Column("F_POSITION_CODE")
    var positionCode: String? = null,
    @Column("F_POSITION_NAME")
    var positionName: String? = null,
    @Column("F_FRIST_IN_DATE")
    var fristInDate: String? = null,
    @Column("F_PRODUCE_NUM")
    var produceNum: String? = null,
    @Column("F_QUANTITY")
    var quantity: String? = null,
    @Column("F_COST_PRICE")
    var costPrice: String? = null,
    @Column("F_AMOUNT")
    var amount: String? = null,
    @Column("F_ITEM_ID")
    var itemId: Long? = null,
    @Column("F_POSITION_ID")
    var positionId: Long? = null,
    @Column("F_BILL_ID")
    var billId: Long? = null
)