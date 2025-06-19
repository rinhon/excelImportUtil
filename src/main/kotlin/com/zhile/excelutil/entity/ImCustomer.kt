package com.zhile.excelutil.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

/**
 * @author Rinhon
 * @date 2025/6/17 09:30
 * @description:
 */
@Table("IM_CUSTOMER")
data class ImCustomer(
    @Id
    @Column("F_KEY_ID")
    var keyId: Long? = null,

    @Column("F_CODE")
    var code: String? = null,
    @Column("F_NAME")
    var name: String? = null,
    @Column("F_ABBR")
    var abbr: String? = null,
    @Column("F_CATALOG")
    var catalog: String? = null,
    @Column("F_IN_UNIT")
    var inUnit: String? = null,
    @Column("F_NATURE1")
    var nature1: String? = null,
    @Column("F_NATURE2")
    var nature2: String? = null,
    @Column("F_CUSTOMER_TYPE")
    var customerType: String? = null,
    @Column("F_AREA")
    var area: String? = null,
    @Column("F_BANK_ACCOUNT")
    var bankAccount: String? = null,
    @Column("F_BANK")
    var bank: String? = null,
    @Column("F_CORRESPONDENCE_CONTACT")
    var correspondenceContact: String? = null,
    @Column("F_CARD_TYPE")
    var cardType: String? = null,
    @Column("F_CARD_NO")
    var cardNo: String? = null,
    @Column("F_CORRESPONDENCE_TEL")
    var correspondenceTel: String? = null,
    @Column("F_CORRESPONDENCE_ADDRESS")
    var correspondenceAddress: String? = null,

    @Column("F_ID")
    var id: Long? = null,
    @Column("F_TOP_BANK_ID")
    var topBankId: Long? = null,
    @Column("F_AREA_ID")
    var areaId: Long? = null,
    @Column("F_CUSTOMER_TYPE_ID")
    var customerTypeId: Long? = null,
    @Column("F_CARD_TYPE_ID")
    var cardTypeId: Long? = null
)
