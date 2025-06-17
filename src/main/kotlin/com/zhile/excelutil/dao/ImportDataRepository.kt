package com.zhile.excelutil.dao


import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * 存储过程接口
 */
@Repository
interface ImportDataRepository {

    // 调用 Oracle 包函数 import_init_data.import_department
//    @Procedure(value = "IMPORT_INIT_DATA.IMPORT_DEPARTMENT") // 注意：包名.函数名
    fun callImportDepartment(
        @Param("p_organ_id") organId: Long // 参数名需要与存储过程中的参数名一致
    ): Int // 函数返回 0 或 1

}