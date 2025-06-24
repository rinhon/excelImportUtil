package com.zhile.excelutil.entity

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("IM_ROLE")
data class ImRole(
    @Column("F_CODE")
    var code: String? = null,

    @Column("F_NAME")
    var name: String? = null,
    @Column("F_ID")
    var id: Long? = null,

    )
