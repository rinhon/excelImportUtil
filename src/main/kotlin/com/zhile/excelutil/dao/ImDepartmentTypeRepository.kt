package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImDepartmentType

/**
 * @author Rinhon
 * @date 2025/6/20 11:21
 * @description:
 */

interface ImDepartmentTypeRepository {

    fun deleteAllDepartmentType()

    fun batchInsertDepartmentTypes(departmentTypes: List<ImDepartmentType>)

}
