package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImRole

/**
 * @author Rinhon
 * @date 2025/6/20 14:08
 * @description:
 */

interface ImRoleRepository {

    fun deleteAllImRole()

    fun batchInsertRoles(roles: List<ImRole>)
}
