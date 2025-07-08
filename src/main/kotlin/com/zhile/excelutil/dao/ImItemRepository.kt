package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImItem

/**
 * @author Rinhon
 * @date 2025/6/17 09:55
 * @description:
 */
interface ImItemRepository {

    suspend fun deleteAllImItem()

    suspend fun batchInsertItems(items: List<ImItem>)

}
