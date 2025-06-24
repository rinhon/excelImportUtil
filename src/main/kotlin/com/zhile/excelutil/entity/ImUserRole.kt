package com.zhile.excelutil.entity

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("IM_USER_ROLE")
data class ImUserRole(
    @Column("F_USER_CODE")
    var userCode: String? = null,
    @Column("F_USER_NAME")
    var userName: String? = null,
    @Column("F_ROLE_CODE")
    var roleCode: String? = null,
    @Column("F_ROLE_NAME")
    var roleName: String? = null,
    @Column("F_USER_ID ")
    var userId: Long? = null,
    @Column("F_ROLE_ID ")
    var roleId: Long? = null
)
