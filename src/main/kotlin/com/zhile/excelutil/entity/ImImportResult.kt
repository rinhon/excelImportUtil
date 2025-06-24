package com.zhile.excelutil.entity

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("IM_IMPORT_RESULT")
data class ImImportResult(
    @Column("F_CHECK_TYPE")
    var checkType: String? = null,
    @Column("F_ERROR_INFO")
    var errorInfo: String? = null,
    @Column("F_ERROR_LOCATION")
    var errorLocation: String? = null
)