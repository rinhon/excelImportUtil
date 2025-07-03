package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImTopicRecord
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * @author Rinhon
 * @date 2025/7/3 10:21
 * @description:
 */
@Repository
interface ImTopicRecordRepository : CrudRepository<ImTopicRecord, Long> {

    // 批量插入方法
    @Modifying
    @Query(
        """
            INSERT INTO IM_TOPIC_RECORD (
                F_TOPIC_RECORD_BILL_NO, F_ITEM_CODE, F_BOOK_NAME, F_DUTY_EDITOR_CODE, F_DUTY_EDITOR_NAME, F_OTHER_DUTY_EDITOR, F_TOPIC_RECORD_DEPARTMENT_CODE, F_TOPIC_RECORD_DEPARTMENT_NAME, F_TOPIC_RECORD_BILL_DATE, F_PART_BOOK_NAME, F_FOREIGN_NAME, F_VICE_BOOK_NAME, F_SERIES_NAME, F_SINO_BOOK_TYPE, F_NOTE_LANGUAGE, F_LANGUAGE, F_MAIN_AUTHOR, F_WORD_COUNT, F_PUBLISH_TYPE, F_PUBLISH_METHOD, F_EDITION_YEAR_MONTH, F_EDITION_NO, F_PRINTING_YEAR_MONTH, F_PRINTING_NO, F_BOOK_FORMAT_SIZE, F_BOOK_FORMAT, F_SHEET_COUNT, F_BINDING_TYPE, F_PRINT_COUNT, F_PRINT_COUNT_TOTAL, F_SET_PRICE, F_BOOK_HEIGHT, F_BOOK_WIDTH, F_SUMMARY, F_TARGET_READER, F_PRESS_SIMILAR_COMPARE, F_NATION_SIMILAR_COMPARE, F_SELL_POLICY, F_CANAL_ANALY, F_IMPORTANT_RECORD_TYPE, F_PARTNER, F_VIRTUAL_BOOK, F_TRANSLATE_BOOK, F_MAP, F_TOPIC_YEAR, F_PRODUCE_NUM, F_TOPIC_ORIGINAL, F_PUBLIC_BOOK, F_PRIMARY_TEXTBOOK, F_TEACHING_AUXILIARY, F_UNIVERSITY_TEXTBOX, F_INTRODUCING_BOOK, F_INTRODUCING_BOOK_NAME, F_INTRODUCING_BOOK_ADDRESS, F_INTRODUCING_BOOK_AUTHOR, F_INTRODUCING_BOOK_ISBN, F_INTRODUCING_BOOK_WAY, F_INTRODUCING_BOOK_NO, F_EXPECT_SUBMIT_TIME, F_TEXT_LANGUAGE, F_PUBLISH_RANGE, F_CARRY_FORM, F_BOOK_TYPE, F_TOPIC_RECORD_REMARKS, F_THIRD_TRIAL_BILL_NO, F_THIRD_TRIAL_BILL_DATE, F_THIRD_TRIAL_DEPARTMENT_CODE, F_THIRD_TRIAL_DEPARTMENT_NAME, F_THIRD_TRIAL_USER_CODE, F_THIRD_TRIAL_USER_NAME, F_TOPIC_NUMBER, F_FIRST_TRIAL_PERSON_CODE, F_FIRST_TRIAL_PERSON_NAME, F_FIRST_TRIAL_DATE, F_FIRST_TRIAL_OPINION, F_SECOND_TRIAL_PERSON_CODE, F_SECOND_TRIAL_PERSON_NAME, F_SECOND_TRIAL_DATE, F_SECOND_TRIAL_OPINION, F_THIRD_TRIAL_PERSON_CODE, F_THIRD_TRIAL_PERSON_NAME, F_THIRD_TRIAL_DATE, F_THIRD_TRIAL_OPINION, F_PUBLISH_BILL_NO, F_PUBLISH_BILL_DATE, F_PUBLISH_PRINTING_YEAR_MONTH, F_PUBLISH_PRINTING_NO, F_PUBLISH_BUSINESS_TYPE, F_PUBLISH_DEPARTMENT_CODE, F_PUBLISH_DEPARTMENT_NAME, F_PUBLISH_USER_CODE, F_PUBLISH_USER_NAME, F_PUBLISH_PERIOD, F_REPRINT_ITEM_ISBN, F_REPRINT_ITEM_NAME, F_BOOK_NUM_APPLY_BILL_NO, F_BOOK_NUM_BILL_DATE, F_BOOK_NUM_DEPARTMENT_CODE, F_BOOK_NUM_DEPARTMENT_NAME, F_BOOK_NUM_USER_CODE, F_BOOK_NUM_USER_NAME, F_ISBN, F_TOPIC_RECORD_DEPARTMENT_ID, F_CIP_INFO, F_EXTRA_CODE, F_CIP_TYPE, F_BOOK_NUM_SINO_BOOK_TYPE,F_ORGAN_ID
            ) VALUES (
                :topicRecordBillNo, :itemCode, :bookName, :dutyEditorCode, :dutyEditorName, :otherDutyEditor, :topicRecordDepartmentCode, :topicRecordDepartmentName, :topicRecordBillDate, :partBookName, :foreignName, :viceBookName, :seriesName, :sinoBookType, :noteLanguage, :language, :mainAuthor, :wordCount, :publishType, :publishMethod, :editionYearMonth, :editionNo, :printingYearMonth, :printingNo, :bookFormatSize, :bookFormat, :sheetCount, :bindingType, :printCount, :printCountTotal, :setPrice, :bookHeight, :bookWidth, :summary, :targetReader, :pressSimilarCompare, :nationSimilarCompare, :sellPolicy, :canalAnaly, :importantRecordType, :partner, :virtualBook, :translateBook, :map, :topicYear, :produceNum, :topicOriginal, :publicBook, :primaryTextbook, :teachingAuxiliary, :universityTextbox, :introducingBook, :introducingBookName, :introducingBookAddress, :introducingBookAuthor, :introducingBookIsbn, :introducingBookWay, :introducingBookNo, :expectSubmitTime, :textLanguage, :publishRange, :carryForm, :bookType, :topicRecordRemarks, :thirdTrialBillNo, :thirdTrialBillDate, :thirdTrialDepartmentCode, :thirdTrialDepartmentName, :thirdTrialUserCode, :thirdTrialUserName, :topicNumber, :firstTrialPersonCode, :firstTrialPersonName, :firstTrialDate, :firstTrialOpinion, :secondTrialPersonCode, :secondTrialPersonName, :secondTrialDate, :secondTrialOpinion, :thirdTrialPersonCode, :thirdTrialPersonName, :thirdTrialDate, :thirdTrialOpinion, :publishBillNo, :publishBillDate, :publishPrintingYearMonth, :publishPrintingNo, :publishBusinessType, :publishDepartmentCode, :publishDepartmentName, :publishUserCode, :publishUserName, :publishPeriod, :reprintItemIsbn, :reprintItemName, :bookNumApplyBillNo, :bookNumBillDate, :bookNumDepartmentCode, :bookNumDepartmentName, :bookNumUserCode, :bookNumUserName, :isbn, :topicRecordDepartmentId, :cipInfo, :extraCode, :cipType, :bookNumSinoBookType,:organId
            )
            """
    )
    fun insertTopicRecord(
        @Param("topicRecordBillNo") topicRecordBillNo: String?,
        @Param("itemCode") itemCode: String?,
        @Param("bookName") bookName: String?,
        @Param("dutyEditorCode") dutyEditorCode: String?,
        @Param("dutyEditorName") dutyEditorName: String?,
        @Param("otherDutyEditor") otherDutyEditor: String?,
        @Param("topicRecordDepartmentCode") topicRecordDepartmentCode: String?,
        @Param("topicRecordDepartmentName") topicRecordDepartmentName: String?,
        @Param("topicRecordBillDate") topicRecordBillDate: String?,
        @Param("partBookName") partBookName: String?,
        @Param("foreignName") foreignName: String?,
        @Param("viceBookName") viceBookName: String?,
        @Param("seriesName") seriesName: String?,
        @Param("sinoBookType") sinoBookType: String?,
        @Param("noteLanguage") noteLanguage: String?,
        @Param("language") language: String?,
        @Param("mainAuthor") mainAuthor: String?,
        @Param("wordCount") wordCount: String?,
        @Param("publishType") publishType: String?,
        @Param("publishMethod") publishMethod: String?,
        @Param("editionYearMonth") editionYearMonth: String?,
        @Param("editionNo") editionNo: String?,
        @Param("printingYearMonth") printingYearMonth: String?,
        @Param("printingNo") printingNo: String?,
        @Param("bookFormatSize") bookFormatSize: String?,
        @Param("bookFormat") bookFormat: String?,
        @Param("sheetCount") sheetCount: String?,
        @Param("bindingType") bindingType: String?,
        @Param("printCount") printCount: String?,
        @Param("printCountTotal") printCountTotal: String?,
        @Param("setPrice") setPrice: String?,
        @Param("bookHeight") bookHeight: String?,
        @Param("bookWidth") bookWidth: String?,
        @Param("summary") summary: String?,
        @Param("targetReader") targetReader: String?,
        @Param("pressSimilarCompare") pressSimilarCompare: String?,
        @Param("nationSimilarCompare") nationSimilarCompare: String?,
        @Param("sellPolicy") sellPolicy: String?,
        @Param("canalAnaly") canalAnaly: String?,
        @Param("importantRecordType") importantRecordType: String?,
        @Param("partner") partner: String?,
        @Param("virtualBook") virtualBook: String?,
        @Param("translateBook") translateBook: String?,
        @Param("map") map: String?,
        @Param("topicYear") topicYear: String?,
        @Param("produceNum") produceNum: String?,
        @Param("topicOriginal") topicOriginal: String?,
        @Param("publicBook") publicBook: String?,
        @Param("primaryTextbook") primaryTextbook: String?,
        @Param("teachingAuxiliary") teachingAuxiliary: String?,
        @Param("universityTextbox") universityTextbox: String?,
        @Param("introducingBook") introducingBook: String?,
        @Param("introducingBookName") introducingBookName: String?,
        @Param("introducingBookAddress") introducingBookAddress: String?,
        @Param("introducingBookAuthor") introducingBookAuthor: String?,
        @Param("introducingBookIsbn") introducingBookIsbn: String?,
        @Param("introducingBookWay") introducingBookWay: String?,        //引进方式
        @Param("introducingBookNo") introducingBookNo: String?,        //版权登记号
        @Param("expectSubmitTime") expectSubmitTime: String?,        //预计来稿时间
        @Param("textLanguage") textLanguage: String?,        //正文文字
        @Param("publishRange") publishRange: String?,        //发行范围
        @Param("carryForm") carryForm: String?,         //载体形式
        @Param("bookType") bookType: String?,        //图书类型
        @Param("topicRecordRemarks") topicRecordRemarks: String?,         //选题单备注

        //pe_third_trial
        @Param("thirdTrialBillNo") thirdTrialBillNo: String?,        //三审单号
        @Param("thirdTrialBillDate") thirdTrialBillDate: String?,        //业务日期
        @Param("thirdTrialDepartmentCode") thirdTrialDepartmentCode: String?,         //业务部门编码
        @Param("thirdTrialDepartmentName") thirdTrialDepartmentName: String?,         //业务部门名称
        @Param("thirdTrialUserCode") thirdTrialUserCode: String?,         //业务员编码
        @Param("thirdTrialUserName") thirdTrialUserName: String?,         //业务员名称
        @Param("topicNumber") topicNumber: String?,        //选题号(该字段设计在选题计划明细中, 三审单中没有)
        @Param("firstTrialPersonCode") firstTrialPersonCode: String?,        //初审人编码
        @Param("firstTrialPersonName") firstTrialPersonName: String?,        //初审人名称
        @Param("firstTrialDate") firstTrialDate: String?,        //初审日期
        @Param("firstTrialOpinion") firstTrialOpinion: String?,        //初审意见
        @Param("secondTrialPersonCode") secondTrialPersonCode: String?,        //复审人编码
        @Param("secondTrialPersonName") secondTrialPersonName: String?,        //复审人名称
        @Param("secondTrialDate") secondTrialDate: String?,        //复审日期
        @Param("secondTrialOpinion") secondTrialOpinion: String?,        //复审意见
        @Param("thirdTrialPersonCode") thirdTrialPersonCode: String?,        //终审人编码
        @Param("thirdTrialPersonName") thirdTrialPersonName: String?,        //终审人名称
        @Param("thirdTrialDate") thirdTrialDate: String?,        //终审日期
        @Param("thirdTrialOpinion") thirdTrialOpinion: String?,        //终审意见

        //pe_topic_publish_order        //-
        @Param("publishBillNo") publishBillNo: String?,         //发稿单号
        @Param("publishBillDate") publishBillDate: String?,        //业务日期
        @Param("publishPrintingYearMonth") publishPrintingYearMonth: String?,        //发稿单印次年月
        @Param("publishPrintingNo") publishPrintingNo: String?,        //发稿单印次
        @Param("publishBusinessType") publishBusinessType: String?,        //发稿单业务类型
        @Param("publishDepartmentCode") publishDepartmentCode: String?,         //业务部门编码
        @Param("publishDepartmentName") publishDepartmentName: String?,         //业务部门名称
        @Param("publishUserCode") publishUserCode: String?,        //业务员编码
        @Param("publishUserName") publishUserName: String?,        //业务员名称
        @Param("publishPeriod") publishPeriod: String?,        //出版期间
        @Param("reprintItemIsbn") reprintItemIsbn: String?,         //重印物品书号
        @Param("reprintItemName") reprintItemName: String?,        //重印物品名称

        //pe_book_num_apply
        @Param("bookNumApplyBillNo") bookNumApplyBillNo: String?,        //书号申请单号
        @Param("bookNumBillDate") bookNumBillDate: String?,        //业务日期
        @Param("bookNumDepartmentCode") bookNumDepartmentCode: String?,        //业务部门编码
        @Param("bookNumDepartmentName") bookNumDepartmentName: String?,        //业务部门名称
        @Param("bookNumUserCode") bookNumUserCode: String?,        //业务员编码
        @Param("bookNumUserName") bookNumUserName: String?,        //业务员名称
        @Param("isbn") isbn: String?,        //书号
        @Param("topicRecordDepartmentId") topicRecordDepartmentId: Long?,         //选题申报: 业务部门ID
        @Param("cipInfo") cipInfo: String?,        //CIP信息
        @Param("extraCode") extraCode: String?,         //附加码
        @Param("cipType") cipType: String?,        //cip分类
        @Param("bookNumSinoBookType") bookNumSinoBookType: String?,        //中图分类
        @Param("organId") organId: Long?,        //中图分类


    )

    @Modifying
    @Query(
        """
        DELETE  FROM IM_TOPIC_RECORD
    """
    )
    fun deleteAllImTopicRecord()
}
