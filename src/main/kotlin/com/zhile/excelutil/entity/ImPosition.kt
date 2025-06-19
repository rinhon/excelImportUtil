package com.zhile.excelutil.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

/**
 *导入货位中间表
 */
@Table("IM_POSITION")
data class ImPosition(
    @Id
    @Column("F_KEY_ID")
    var keyId: Long? = null,
    @Column("F_ID")
    var id: Long? = null,
    @Column("F_CODE")
    var code: String? = null,
    @Column("F_NAME")
    var name: String? = null,
    @Column("F_REMARKS")
    var remarks: String? = null,
    @Column("F_MANAGER")
    var manager: String? = null,
    @Column("F_ADDRESS")
    var address: String? = null

)
