package com.zhile.excelutil.dao.impl

import com.zhile.excelutil.dao.ImTopicRecordRepository
import com.zhile.excelutil.entity.ImTopicRecord
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement

@Repository
class ImTopicRecordRepositoryImpl(private val jdbcTemplate: JdbcTemplate) : ImTopicRecordRepository {
    override fun deleteAllImTopicRecord() {
        jdbcTemplate.update("DELETE FROM IM_TOPIC_RECORD")
    }

    override fun batchInsertTopicRecord(topicRecords: List<ImTopicRecord>) {
        if (topicRecords.isEmpty()) {
            return
        }

        val sql = """
          INSERT INTO IM_TOPIC_RECORD 
          (F_TOPIC_RECORD_BILL_NO, F_ITEM_CODE, F_BOOK_NAME, F_DUTY_EDITOR_CODE, F_DUTY_EDITOR_NAME, F_OTHER_DUTY_EDITOR, F_TOPIC_RECORD_DEPARTMENT_CODE, F_TOPIC_RECORD_DEPARTMENT_NAME, F_TOPIC_RECORD_BILL_DATE, F_PART_BOOK_NAME, F_FOREIGN_NAME, F_VICE_BOOK_NAME, F_SERIES_NAME, F_SINO_BOOK_TYPE, F_NOTE_LANGUAGE, F_LANGUAGE, F_MAIN_AUTHOR, F_WORD_COUNT, F_PUBLISH_TYPE, F_PUBLISH_METHOD, F_EDITION_YEAR_MONTH, F_EDITION_NO, F_PRINTING_YEAR_MONTH, F_PRINTING_NO, F_BOOK_FORMAT_SIZE, F_BOOK_FORMAT, F_SHEET_COUNT, F_BINDING_TYPE, F_PRINT_COUNT, F_PRINT_COUNT_TOTAL, F_SET_PRICE, F_BOOK_HEIGHT, F_BOOK_WIDTH, F_SUMMARY, F_TARGET_READER, F_PRESS_SIMILAR_COMPARE, F_NATION_SIMILAR_COMPARE, F_SELL_POLICY, F_CANAL_ANALY, F_IMPORTANT_RECORD_TYPE, F_PARTNER, F_VIRTUAL_BOOK, F_TRANSLATE_BOOK, F_MAP, F_TOPIC_YEAR, F_PRODUCE_NUM, F_TOPIC_ORIGINAL, F_PUBLIC_BOOK, F_PRIMARY_TEXTBOOK, F_TEACHING_AUXILIARY, F_UNIVERSITY_TEXTBOX, F_INTRODUCING_BOOK, F_INTRODUCING_BOOK_NAME, F_INTRODUCING_BOOK_ADDRESS, F_INTRODUCING_BOOK_AUTHOR, F_INTRODUCING_BOOK_ISBN, F_INTRODUCING_BOOK_WAY, F_INTRODUCING_BOOK_NO, F_EXPECT_SUBMIT_TIME, F_TEXT_LANGUAGE, F_PUBLISH_RANGE, F_CARRY_FORM, F_BOOK_TYPE, F_TOPIC_RECORD_REMARKS, F_THIRD_TRIAL_BILL_NO, F_THIRD_TRIAL_BILL_DATE, F_THIRD_TRIAL_DEPARTMENT_CODE, F_THIRD_TRIAL_DEPARTMENT_NAME, F_THIRD_TRIAL_USER_CODE, F_THIRD_TRIAL_USER_NAME, F_TOPIC_NUMBER, F_FIRST_TRIAL_PERSON_CODE, F_FIRST_TRIAL_PERSON_NAME, F_FIRST_TRIAL_DATE, F_FIRST_TRIAL_OPINION, F_SECOND_TRIAL_PERSON_CODE, F_SECOND_TRIAL_PERSON_NAME, F_SECOND_TRIAL_DATE, F_SECOND_TRIAL_OPINION, F_THIRD_TRIAL_PERSON_CODE, F_THIRD_TRIAL_PERSON_NAME, F_THIRD_TRIAL_DATE, F_THIRD_TRIAL_OPINION, F_PUBLISH_BILL_NO, F_PUBLISH_BILL_DATE, F_PUBLISH_PRINTING_YEAR_MONTH, F_PUBLISH_PRINTING_NO, F_PUBLISH_BUSINESS_TYPE, F_PUBLISH_DEPARTMENT_CODE, F_PUBLISH_DEPARTMENT_NAME, F_PUBLISH_USER_CODE, F_PUBLISH_USER_NAME, F_PUBLISH_PERIOD, F_REPRINT_ITEM_ISBN, F_REPRINT_ITEM_NAME, F_BOOK_NUM_APPLY_BILL_NO, F_BOOK_NUM_BILL_DATE, F_BOOK_NUM_DEPARTMENT_CODE, F_BOOK_NUM_DEPARTMENT_NAME, F_BOOK_NUM_USER_CODE, F_BOOK_NUM_USER_NAME, F_ISBN, F_CIP_INFO, F_EXTRA_CODE, F_CIP_TYPE,F_ORGAN_ID)
          VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
        """.trimIndent()

        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                val topicRecord = topicRecords[i]

                ps.setString(1, topicRecord.topicRecordBillNo)
                ps.setString(2, topicRecord.itemCode)
                ps.setString(3, topicRecord.bookName)
                ps.setString(4, topicRecord.dutyEditorCode)
                ps.setString(5, topicRecord.dutyEditorName)
                ps.setString(6, topicRecord.otherDutyEditor)
                ps.setString(7, topicRecord.topicRecordDepartmentCode)
                ps.setString(8, topicRecord.topicRecordDepartmentName)
                ps.setString(9, topicRecord.topicRecordBillDate)
                ps.setString(10, topicRecord.partBookName)
                ps.setString(11, topicRecord.foreignName)
                ps.setString(12, topicRecord.viceBookName)
                ps.setString(13, topicRecord.seriesName)
                ps.setString(14, topicRecord.sinoBookType)
                ps.setString(15, topicRecord.noteLanguage)
                ps.setString(16, topicRecord.language)
                ps.setString(17, topicRecord.mainAuthor)
                ps.setString(18, topicRecord.wordCount)
                ps.setString(19, topicRecord.publishType)
                ps.setString(20, topicRecord.publishMethod)
                ps.setString(21, topicRecord.editionYearMonth)
                ps.setString(22, topicRecord.editionNo)
                ps.setString(23, topicRecord.printingYearMonth)
                ps.setString(24, topicRecord.printingNo)
                ps.setString(25, topicRecord.bookFormatSize)
                ps.setString(26, topicRecord.bookFormat)
                ps.setString(27, topicRecord.sheetCount)
                ps.setString(28, topicRecord.bindingType)
                ps.setString(29, topicRecord.printCount)
                ps.setString(30, topicRecord.printCountTotal)
                ps.setString(31, topicRecord.setPrice)
                ps.setString(32, topicRecord.bookHeight)
                ps.setString(33, topicRecord.bookWidth)
                ps.setString(34, topicRecord.summary)
                ps.setString(35, topicRecord.targetReader)
                ps.setString(36, topicRecord.pressSimilarCompare)
                ps.setString(37, topicRecord.nationSimilarCompare)
                ps.setString(38, topicRecord.sellPolicy)
                ps.setString(39, topicRecord.canalAnaly)
                ps.setString(40, topicRecord.importantRecordType)
                ps.setString(41, topicRecord.partner)
                ps.setString(42, topicRecord.virtualBook)
                ps.setString(43, topicRecord.translateBook)
                ps.setString(44, topicRecord.map)
                ps.setString(45, topicRecord.topicYear)
                ps.setString(46, topicRecord.produceNum)
                ps.setString(47, topicRecord.topicOriginal)
                ps.setString(48, topicRecord.publicBook)
                ps.setString(49, topicRecord.primaryTextbook)
                ps.setString(50, topicRecord.teachingAuxiliary)
                ps.setString(51, topicRecord.universityTextbox)
                ps.setString(52, topicRecord.introducingBook)
                ps.setString(53, topicRecord.introducingBookName)
                ps.setString(54, topicRecord.introducingBookAddress)
                ps.setString(55, topicRecord.introducingBookAuthor)
                ps.setString(56, topicRecord.introducingBookIsbn)
                ps.setString(57, topicRecord.introducingBookWay)
                ps.setString(58, topicRecord.introducingBookNo)
                ps.setString(59, topicRecord.expectSubmitTime)
                ps.setString(60, topicRecord.textLanguage)
                ps.setString(61, topicRecord.publishRange)
                ps.setString(62, topicRecord.carryForm)
                ps.setString(63, topicRecord.bookType)
                ps.setString(64, topicRecord.topicRecordRemarks)
                ps.setString(65, topicRecord.thirdTrialBillNo)
                ps.setString(66, topicRecord.thirdTrialBillDate)
                ps.setString(67, topicRecord.thirdTrialDepartmentCode)
                ps.setString(68, topicRecord.thirdTrialDepartmentName)
                ps.setString(69, topicRecord.thirdTrialUserCode)
                ps.setString(70, topicRecord.thirdTrialUserName)
                ps.setString(71, topicRecord.topicNumber)
                ps.setString(72, topicRecord.firstTrialPersonCode)
                ps.setString(73, topicRecord.firstTrialPersonName)
                ps.setString(74, topicRecord.firstTrialDate)
                ps.setString(75, topicRecord.firstTrialOpinion)
                ps.setString(76, topicRecord.secondTrialPersonCode)
                ps.setString(77, topicRecord.secondTrialPersonName)
                ps.setString(78, topicRecord.secondTrialDate)
                ps.setString(79, topicRecord.secondTrialOpinion)
                ps.setString(80, topicRecord.thirdTrialPersonCode)
                ps.setString(81, topicRecord.thirdTrialPersonName)
                ps.setString(82, topicRecord.thirdTrialDate)
                ps.setString(83, topicRecord.thirdTrialOpinion)
                ps.setString(84, topicRecord.publishBillNo)
                ps.setString(85, topicRecord.publishBillDate)
                ps.setString(86, topicRecord.publishPrintingYearMonth)
                ps.setString(87, topicRecord.publishPrintingNo)
                ps.setString(88, topicRecord.publishBusinessType)
                ps.setString(89, topicRecord.publishDepartmentCode)
                ps.setString(90, topicRecord.publishDepartmentName)
                ps.setString(91, topicRecord.publishUserCode)
                ps.setString(92, topicRecord.publishUserName)
                ps.setString(93, topicRecord.publishPeriod)
                ps.setString(94, topicRecord.reprintItemIsbn)
                ps.setString(95, topicRecord.reprintItemName)
                ps.setString(96, topicRecord.bookNumApplyBillNo)
                ps.setString(97, topicRecord.bookNumBillDate)
                ps.setString(98, topicRecord.bookNumDepartmentCode)
                ps.setString(99, topicRecord.bookNumDepartmentName)
                ps.setString(100, topicRecord.bookNumUserCode)
                ps.setString(101, topicRecord.bookNumUserName)
                ps.setString(102, topicRecord.isbn)
                ps.setString(103, topicRecord.cipInfo)
                ps.setString(104, topicRecord.extraCode)
                ps.setString(105, topicRecord.cipType)
                ps.setString(106, topicRecord.organId?.toString())
            }

            override fun getBatchSize(): Int {
                return topicRecords.size
            }
        })
    }
}
