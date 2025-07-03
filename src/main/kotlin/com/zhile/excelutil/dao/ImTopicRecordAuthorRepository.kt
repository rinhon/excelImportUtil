package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImTopicRecordAuthor
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * @author Rinhon
 * @date 2025/7/3 11:30
 * @description:
 */
@Repository
interface ImTopicRecordAuthorRepository : CrudRepository<ImTopicRecordAuthor, String> { // 主键类型为 String

    // 自定义插入方法
    @Modifying
    @Query(
        """
        INSERT INTO IM_TOPIC_RECORD_AUTHOR (
            F_TOPIC_RECORD_BILL_NO, F_BOOK_NAME, F_AUTHOR_CODE, F_AUTHOR_NAME,
            F_MAIN_AUTHOR, F_WRITE_TYPE, F_AUTHOR_COMPANY, F_AUTHOR_TITLE,
            F_MAJOR_WORKS, F_BACKGROUND_DETAIL, F_MARKET_CONDITIONS, F_REMARKS,
            F_NATION, F_DYNASTY, F_ORGAN_ID
        ) VALUES (
            :topicRecordBillNo, :bookName, :authorCode, :authorName,
            :mainAuthor, :writeType, :authorCompany, :authorTitle,
            :majorWorks, :backgroundDetail, :marketConditions, :remarks,
            :nation, :dynasty, :organId
        )
        """
    )
    fun insertTopicRecordAuthor(
        @Param("topicRecordBillNo") topicRecordBillNo: String?,
        @Param("bookName") bookName: String?,
        @Param("authorCode") authorCode: String?,
        @Param("authorName") authorName: String?,
        @Param("mainAuthor") mainAuthor: String?,
        @Param("writeType") writeType: String?,
        @Param("authorCompany") authorCompany: String?,
        @Param("authorTitle") authorTitle: String?,
        @Param("majorWorks") majorWorks: String?,
        @Param("backgroundDetail") backgroundDetail: String?,
        @Param("marketConditions") marketConditions: String?,
        @Param("remarks") remarks: String?,
        @Param("nation") nation: String?,
        @Param("dynasty") dynasty: String?,
        @Param("organId") organId: Long?
    )

    @Modifying
    @Query(
        """
            DELETE FROM IM_TOPIC_RECORD_AUTHOR
        """
    )
    fun deleteAllTopicRecordAuthor()
}
