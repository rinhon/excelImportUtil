package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImPosition

/**
 * @author Rinhon
 * @date 2025/6/17 09:34
 * @description:
 */
interface ImPositionRepository {

    fun deleteAllImPosition()

    fun batchInsertPositions(positions: List<ImPosition>)

}
