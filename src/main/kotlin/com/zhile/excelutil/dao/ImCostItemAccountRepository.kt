package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImCostItemAccount

/**
 * @author zlhp
 * @date 2025/6/20 15:04
 * @description:
 */
interface ImCostItemAccountRepository {

    suspend fun batchInsertCostItems(costItemAccount: List<ImCostItemAccount>)

    suspend fun deleteAllImCostItemAccount()
}
