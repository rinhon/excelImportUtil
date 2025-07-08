package com.zhile.excelutil.dao.impl

import com.zhile.excelutil.dao.ImTopicRecordAuthorRepository
import com.zhile.excelutil.entity.ImTopicRecordAuthor
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement

@Repository
class ImTopicRecordAuthorRepositoryImpl(private val jdbcTemplate: JdbcTemplate) : ImTopicRecordAuthorRepository {
    override fun deleteAllTopicRecordAuthor() {
        jdbcTemplate.update("DELETE FROM IM_TOPIC_RECORD_AUTHOR")
    }

    override fun batchInsertTopicRecordAuthor(topicRecordAuthors: List<ImTopicRecordAuthor>) {
        if (topicRecordAuthors.isEmpty()) {
            return
        }

        val sql = """
           
       INSERT INTO IM_TOPIC_RECORD_AUTHOR (
            F_TOPIC_RECORD_BILL_NO, F_BOOK_NAME, F_AUTHOR_CODE, F_AUTHOR_NAME,
            F_MAIN_AUTHOR, F_WRITE_TYPE, F_AUTHOR_COMPANY, F_AUTHOR_TITLE,
            F_MAJOR_WORKS, F_BACKGROUND_DETAIL, F_MARKET_CONDITIONS, F_REMARKS,
            F_NATION, F_DYNASTY, F_ORGAN_ID
        ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
        """.trimIndent()

        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                val topicRecordAuthor = topicRecordAuthors[i]

                ps.setString(1, topicRecordAuthor.topicRecordBillNo)
                ps.setString(2, topicRecordAuthor.bookName)
                ps.setString(3, topicRecordAuthor.authorCode)
                ps.setString(4, topicRecordAuthor.authorName)
                ps.setString(5, topicRecordAuthor.mainAuthor)
                ps.setString(6, topicRecordAuthor.writeType)
                ps.setString(7, topicRecordAuthor.authorCompany)
                ps.setString(8, topicRecordAuthor.authorTitle)
                ps.setString(9, topicRecordAuthor.majorWorks)
                ps.setString(10, topicRecordAuthor.backgroundDetail)
                ps.setString(11, topicRecordAuthor.marketConditions)
                ps.setString(12, topicRecordAuthor.remarks)
                ps.setString(13, topicRecordAuthor.nation)
                ps.setString(14, topicRecordAuthor.dynasty)
                ps.setString(15, topicRecordAuthor.organId?.toString())
            }

            override fun getBatchSize(): Int {
                return topicRecordAuthors.size
            }
        })
    }
}
