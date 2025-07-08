package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImUser


interface ImUserRepository {

    fun deleteAllImUser()

    fun batchInsertUsers(users: List<ImUser>)

}
