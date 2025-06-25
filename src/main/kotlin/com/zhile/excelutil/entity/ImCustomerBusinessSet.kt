package com.zhile.excelutil.entity

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("IM_CUSTOMER_BUSINESS_SET")
data class ImCustomerBusinessSet(
    @Column("F_CUSTOMER_CODE")
    var customerCode: String? = null,
    @Column("F_CUSTOMER_NAME")
    var customerName: String? = null,
    @Column("F_PURCHASE_USER_CODE")
    var purchaseUserCode: String? = null,
    @Column("F_PURCHASE_USER_NAME")
    var purchaseUserName: String? = null,
    @Column("F_PURCHASE_DEPARTMENT_NAME")
    var purchaseDepartmentName: String? = null,
    @Column("F_PURCHASE_DEPARTMENT_CODE")
    var purchaseDepartmentCode: String? = null,
    @Column("F_SALE_USER_CODE")
    var saleUserCode: String? = null,
    @Column("F_SALE_USER_NAME")
    var saleUserName: String? = null,
    @Column("F_SALE_DEPARTMENT_CODE")
    var saleDepartmentCode: String? = null,
    @Column("F_SALE_DEPARTMENT_NAME")
    var saleDepartmentName: String? = null,
    @Column("F_REMARKS")
    var remarks: String? = null,
    @Column("F_CUSTOMER_ID")
    var customerId: Long? = null,
    @Column("F_PURCHASE_USER_ID")
    var purchaseUserId: Long? = null,
    @Column("F_PURCHASE_DEPARTMENT_ID")
    var purchaseDepartmentId: Long? = null,
    @Column("F_SALE_USER_ID")
    var saleUserId: Long? = null,
    @Column("F_SALE_DEPARTMENT_ID")
    var saleDepartmentId: Long? = null
)
