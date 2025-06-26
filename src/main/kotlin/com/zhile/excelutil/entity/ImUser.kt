package com.zhile.excelutil.entity

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

/**
 * 导入职员数据中间表
 */
@Table("IM_USER")
data class ImUser(
    @Column("F_KEY_ID")
    var keyId: Long? = null,

    @Column("F_ID")
    var id: Long? = null,
    //编码
    @Column("F_CODE")
    var code: String? = null,
    //名称
    @Column("F_NAME")
    var name: String? = null,
    //电话
    @Column("F_PHONE")
    var phone: String? = null,
    //部门编码
    @Column("F_DEPARTMENT_CODE")
    var departmentCode: String? = null,
    //部门名称
    @Column("F_DEPARTMENT_NAME")
    var departmentName: String? = null,
    //性别
    @Column("F_SEX")
    var sex: String? = null,
    //备注
    @Column("F_REMARKS")
    var remarks: String? = null,
    //部门Id
    @Column("F_DEPARTMENT_ID")
    var departmentId: Long? = null,    //性别
    @Column("F_LOGIN")
    var login: String? = null,
    //备注
    @Column("F_ATTRIBUTE")
    var attribute: String? = null,
    //部门Id
    @Column("F_QUERY_RIGHT")
    var queryRight: String? = null,

    )