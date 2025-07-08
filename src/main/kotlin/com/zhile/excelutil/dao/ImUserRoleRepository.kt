package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImUserRole

/**
 * @author zlhp
 * @date 2025/6/20 14:19
 * @description:
 */
interface ImUserRoleRepository {

    fun deleteAllImUserRole()

    fun batchInsertUserRole(userRoles: List<ImUserRole>)
}
