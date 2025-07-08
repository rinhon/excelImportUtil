package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImFeeBill

/**
 * @author zlhp
 * @date 2025/7/3 13:14
 * @description:
 */
interface ImFeeBillRepository {

    fun deleteAllFeeBill()

    fun batchInsertFeeBills(feeBills: List<ImFeeBill>)
}
