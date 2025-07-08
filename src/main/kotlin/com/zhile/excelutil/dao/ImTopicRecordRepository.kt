package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImTopicRecord

/**
 * @author Rinhon
 * @date 2025/7/3 10:21
 * @description:
 */
interface ImTopicRecordRepository {

    fun deleteAllImTopicRecord()

    fun batchInsertTopicRecord(topicRecords: List<ImTopicRecord>)
}
