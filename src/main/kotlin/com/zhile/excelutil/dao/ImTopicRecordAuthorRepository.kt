package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImTopicRecordAuthor
import org.springframework.stereotype.Repository

/**
 * @author Rinhon
 * @date 2025/7/3 11:30
 * @description:
 */
@Repository
interface ImTopicRecordAuthorRepository { // 主键类型为 String

    fun deleteAllTopicRecordAuthor()

    fun batchInsertTopicRecordAuthor(topicRecordAuthors: List<ImTopicRecordAuthor>)
}
