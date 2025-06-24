package com.zhile.excelutil.entity

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("IM_DEPARTMENT_TYPE")
data class ImDepartmentType(
    // 部门类型名称
    @Column("F_NAME")
    var name: String? = null,

    // 备注信息
    @Column("F_REMARKS")
    var remarks: String? = null,

    // 唯一标识ID
    @Column("F_ID")
    var id: Long? = null,

    // 部门类型编码
    @Column("F_CODE")
    var code: String? = null
)
