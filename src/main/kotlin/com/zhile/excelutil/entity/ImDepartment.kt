package com.zhile.excelutil.entity


import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table


@Table("IM_DEPARTMENT")
data class ImDepartment(
    //id主键
    @Id
    @Column("F_ID")
    @Transient
    var id: Long? = null,

    //编码
    @Column("F_CODE")
    var code: String? = null,

    //名称
    @Column("F_NAME")
    var name: String? = null,

    //全称
    @Column("F_FULL_NAME")
    var fullName: String? = null,

    //父级编码
    @Column("F_PARENT_CODE")
    var parentCode: String? = null,

    //父级名称
    @Column("F_PARENT_NAME")
    var parentName: String? = null,

    //部门类型
    @Column("F_DEPARTMENT_TYPE")
    var departmentType: String? = null,

    //备注
    @Column("F_REMARKS")
    var remarks: String? = null,

    //父级id
    @Column("F_PARENT_ID")
    var parentId: Long? = null,

    //类型id
    @Column("F_TYPE_ID")
    var typeId: Long? = null

)
