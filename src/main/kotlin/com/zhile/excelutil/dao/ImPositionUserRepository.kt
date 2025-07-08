package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImPositionUser

/**
 * @author Rinhon
 * @date 2025/6/20 14:40
 * @description:
 */

interface ImPositionUserRepository {

    fun deleteAllImPositionUser()

    fun batchInsertPositionUsers(positionUsers: List<ImPositionUser>)
}
