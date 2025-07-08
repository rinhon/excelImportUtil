package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImDepartment


interface ImDepartmentRepository {

    fun deleteAllImDepartment()

    fun batchInsertDepartments(departments: List<ImDepartment>)
}
