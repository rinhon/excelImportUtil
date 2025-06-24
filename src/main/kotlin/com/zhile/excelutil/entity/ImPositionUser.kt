package com.zhile.excelutil.entity

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("IM_POSITION_USER")
data class ImPositionUser(

    @Column("F_USER_CODE")
    var userCode: String? = null,
    @Column("F_USER_NAME")
    var userName: String? = null,
    @Column("F_POSITION_CODE")
    var positionCode: String? = null,
    @Column("F_POSITION_NAME")
    var positionName: String? = null,
    @Column("F_USER_ID")
    var userId: Long? = null,
    @Column("F_POSITION_ID")
    var positionId: Long? = null
)
