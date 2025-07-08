package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImItemNatureAccount

/**
 * @author Rinhon
 * @date 2025/6/17 10:02
 * @description:
 */
interface ImItemNatureAccountRepository {

    suspend fun deleteAllImItemNatureAccount()

    suspend fun batchInsertImItemNatureAccount(accounts: List<ImItemNatureAccount>)
}
