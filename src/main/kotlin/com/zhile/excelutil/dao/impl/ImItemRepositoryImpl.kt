package com.zhile.excelutil.dao.impl

import com.zhile.excelutil.dao.ImItemRepository
import com.zhile.excelutil.entity.ImItem
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement

@Repository
class ImItemRepositoryImpl(private val jdbcTemplate: JdbcTemplate) : ImItemRepository {
    override suspend fun deleteAllImItem() {
        val sql = "DELETE FROM IM_ITEM"
        jdbcTemplate.update(sql)
    }

    override suspend fun batchInsertItems(items: List<ImItem>) {
        if (items.isEmpty()) return

        val sql = """
            INSERT INTO IM_ITEM (
                F_CODE, F_NAME, F_ABBR, F_BAR_CODE, F_SET_PRICE, F_SPEC, F_PUBLISH_TYPE, 
                F_PUBLISH_METHOD, F_CATEGORY, F_ITEM_TYPE, F_NATURE, F_LENGTH, F_WIDTH, 
                F_HEIGHT, F_PACK_UNIT, F_UNIT, F_KIT, F_ISBN, F_AUX_CODE, F_SERIES_NAME, 
                F_VICE_BOOK_NAME, F_EDITION_YEAR_MONTH, F_EDITION_NO, F_MAIN_AUTHOR, 
                F_DEPARTMENT_CODE, F_DEPARTMENT_NAME, F_DUTY_EDITOR_CODE, 
                F_DUTY_EDITOR_NAME, F_PUBLISH_PERIOD, F_PRINT_SHEET, F_FORMAT, 
                F_FORMAT_SIZE, F_TOPIC_TYPE, F_BINDING_TYPE, F_LANGUAGE, F_NOTE_LANGUAGE,
                F_SUMMARY, F_PERFACE, F_CATALOG, F_BOOK_REVIEW, F_BOOK_ABSTRACT, F_CIP_INFO,
                F_REMARKS, F_CIP_TYPE
            )
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
        """.trimIndent()


        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                ps.setString(1, items[i].code)
                ps.setString(2, items[i].name)
                ps.setString(3, items[i].abbr)
                ps.setString(4, items[i].barCode)
                ps.setString(5, items[i].setPrice)
                ps.setString(6, items[i].spec)
                ps.setString(7, items[i].publishType)
                ps.setString(8, items[i].publishMethod)
                ps.setString(9, items[i].category)
                ps.setString(10, items[i].itemType)

                ps.setString(11, items[i].nature)
                ps.setString(12, items[i].length)
                ps.setString(13, items[i].width)
                ps.setString(14, items[i].height)
                ps.setString(15, items[i].packUnit)
                ps.setString(16, items[i].unit)
                ps.setString(17, items[i].kit)
                ps.setString(18, items[i].isbn)
                ps.setString(19, items[i].auxCode)
                ps.setString(20, items[i].seriesName)

                ps.setString(21, items[i].viceBookName)
                ps.setString(22, items[i].editionYearMonth)
                ps.setString(23, items[i].editionNo)
                ps.setString(24, items[i].mainAuthor)
                ps.setString(25, items[i].departmentCode)
                ps.setString(26, items[i].departmentName)
                ps.setString(27, items[i].dutyEditorCode)
                ps.setString(28, items[i].dutyEditorName)
                ps.setString(29, items[i].publishPeriod)
                ps.setString(30, items[i].printSheet)

                ps.setString(31, items[i].format)
                ps.setString(32, items[i].formatSize)
                ps.setString(33, items[i].topicType)
                ps.setString(34, items[i].bindingType)
                ps.setString(35, items[i].language)
                ps.setString(36, items[i].noteLanguage)
                ps.setString(37, items[i].summary)
                ps.setString(38, items[i].perface)
                ps.setString(39, items[i].catalog)
                ps.setString(40, items[i].bookReview)

                ps.setString(41, items[i].bookAbstract)
                ps.setString(42, items[i].cipInfo)
                ps.setString(43, items[i].remarks)
                ps.setString(44, items[i].cipType)

            }

            override fun getBatchSize(): Int {
                return items.size
            }
        })
    }
}
