package com.zhile.excelutil.entity

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("IM_TOPIC_RECORD")
data class ImTopicRecord(
    // 选题申报相关字段
    @Column("F_TOPIC_RECORD_BILL_NO")
    var topicRecordBillNo: String? = null, // 选题单号

    @Column("F_ITEM_CODE")
    var itemCode: String? = null, // 物品编码

    @Column("F_BOOK_NAME")
    var bookName: String? = null, // 物品名称(书名)

    @Column("F_DUTY_EDITOR_CODE")
    var dutyEditorCode: String? = null, // 责任编辑编码

    @Column("F_DUTY_EDITOR_NAME")
    var dutyEditorName: String? = null, // 责任编辑名称

    @Column("F_OTHER_DUTY_EDITOR")
    var otherDutyEditor: String? = null, // 其他编辑

    @Column("F_TOPIC_RECORD_DEPARTMENT_CODE")
    var topicRecordDepartmentCode: String? = null, // 业务部门编码

    @Column("F_TOPIC_RECORD_DEPARTMENT_NAME")
    var topicRecordDepartmentName: String? = null, // 业务部门名称

    @Column("F_TOPIC_RECORD_BILL_DATE")
    var topicRecordBillDate: String? = null, // 业务日期

    @Column("F_PART_BOOK_NAME")
    var partBookName: String? = null, // 分卷册名

    @Column("F_FOREIGN_NAME")
    var foreignName: String? = null, // 外文书名

    @Column("F_VICE_BOOK_NAME")
    var viceBookName: String? = null, // 副书名

    @Column("F_SERIES_NAME")
    var seriesName: String? = null, // 丛(套)书名

    @Column("F_SINO_BOOK_TYPE")
    var sinoBookType: String? = null, // 中图分类

    @Column("F_NOTE_LANGUAGE")
    var noteLanguage: String? = null, // 正文文种

    @Column("F_LANGUAGE")
    var language: String? = null, // 正文文字

    @Column("F_MAIN_AUTHOR")
    var mainAuthor: String? = null, // 主要作者

    @Column("F_WORD_COUNT")
    var wordCount: String? = null, // 书稿字数(千字)

    @Column("F_PUBLISH_TYPE")
    var publishType: String? = null, // 出版类别

    @Column("F_PUBLISH_METHOD")
    var publishMethod: String? = null, // 经营方式

    @Column("F_EDITION_YEAR_MONTH")
    var editionYearMonth: String? = null, // 版次时间-年月

    @Column("F_EDITION_NO")
    var editionNo: String? = null, // 版次

    @Column("F_PRINTING_YEAR_MONTH")
    var printingYearMonth: String? = null, // 印次时间-年月

    @Column("F_PRINTING_NO")
    var printingNo: String? = null, // 印次

    @Column("F_BOOK_FORMAT_SIZE")
    var bookFormatSize: String? = null, // 开本尺寸名称

    @Column("F_BOOK_FORMAT")
    var bookFormat: String? = null, // 开本别名

    @Column("F_SHEET_COUNT")
    var sheetCount: String? = null, // 印张

    @Column("F_BINDING_TYPE")
    var bindingType: String? = null, // 装订方式

    @Column("F_PRINT_COUNT")
    var printCount: String? = null, // 印数(册)

    @Column("F_PRINT_COUNT_TOTAL")
    var printCountTotal: String? = null, // 累计印数(册)

    @Column("F_SET_PRICE")
    var setPrice: String? = null, // 定价(元)

    @Column("F_BOOK_HEIGHT")
    var bookHeight: String? = null, // 成品尺寸(mm)-长

    @Column("F_BOOK_WIDTH")
    var bookWidth: String? = null, // 成品尺寸(mm)-宽

    @Column("F_SUMMARY")
    var summary: String? = null, // 内容简介

    @Column("F_TARGET_READER")
    var targetReader: String? = null, // 目标读者

    @Column("F_PRESS_SIMILAR_COMPARE")
    var pressSimilarCompare: String? = null, // 本社同类书比较

    @Column("F_NATION_SIMILAR_COMPARE")
    var nationSimilarCompare: String? = null, // 国内同类书比较

    @Column("F_SELL_POLICY")
    var sellPolicy: String? = null, // 营销策略

    @Column("F_CANAL_ANALY")
    var canalAnaly: String? = null, // 渠道分析

    @Column("F_IMPORTANT_RECORD_TYPE")
    var importantRecordType: String? = null, // 重要选题类型

    @Column("F_PARTNER")
    var partner: String? = null, // 合作方

    @Column("F_VIRTUAL_BOOK")
    var virtualBook: String? = null, // 是否虚拟选题

    @Column("F_TRANSLATE_BOOK")
    var translateBook: String? = null, // 是否翻译作品

    @Column("F_MAP")
    var map: String? = null, // 是否地图

    @Column("F_TOPIC_YEAR")
    var topicYear: String? = null, // 选题年度

    @Column("F_PRODUCE_NUM")
    var produceNum: String? = null, // 选题批次

    @Column("F_TOPIC_ORIGINAL")
    var topicOriginal: String? = null, // 是否原创

    @Column("F_PUBLIC_BOOK")
    var publicBook: String? = null, // 是否公版

    @Column("F_PRIMARY_TEXTBOOK")
    var primaryTextbook: String? = null, // 是否中小学教材

    @Column("F_TEACHING_AUXILIARY")
    var teachingAuxiliary: String? = null, // 是否中小学教辅

    @Column("F_UNIVERSITY_TEXTBOX")
    var universityTextbox: String? = null, // 是否高校教材

    @Column("F_INTRODUCING_BOOK")
    var introducingBook: String? = null, // 是否引进图书

    @Column("F_INTRODUCING_BOOK_NAME")
    var introducingBookName: String? = null, // 引进版图书原书名

    @Column("F_INTRODUCING_BOOK_ADDRESS")
    var introducingBookAddress: String? = null, // 引进版图书原出版地

    @Column("F_INTRODUCING_BOOK_AUTHOR")
    var introducingBookAuthor: String? = null, // 引进版图书原出版者

    @Column("F_INTRODUCING_BOOK_ISBN")
    var introducingBookIsbn: String? = null, // 引进版图书外版ISBN

    @Column("F_INTRODUCING_BOOK_WAY")
    var introducingBookWay: String? = null, // 引进方式

    @Column("F_INTRODUCING_BOOK_NO")
    var introducingBookNo: String? = null, // 版权登记号

    @Column("F_EXPECT_SUBMIT_TIME")
    var expectSubmitTime: String? = null, // 预计来稿时间

    @Column("F_TEXT_LANGUAGE")
    var textLanguage: String? = null, // 正文文字

    @Column("F_PUBLISH_RANGE")
    var publishRange: String? = null, // 发行范围

    @Column("F_CARRY_FORM")
    var carryForm: String? = null, // 载体形式

    @Column("F_BOOK_TYPE")
    var bookType: String? = null, // 图书类型

    @Column("F_TOPIC_RECORD_REMARKS")
    var topicRecordRemarks: String? = null, // 选题单备注

    // 三审单相关字段
    @Column("F_THIRD_TRIAL_BILL_NO")
    var thirdTrialBillNo: String? = null, // 三审单号

    @Column("F_THIRD_TRIAL_BILL_DATE")
    var thirdTrialBillDate: String? = null, // 业务日期

    @Column("F_THIRD_TRIAL_DEPARTMENT_CODE")
    var thirdTrialDepartmentCode: String? = null, // 业务部门编码

    @Column("F_THIRD_TRIAL_DEPARTMENT_NAME")
    var thirdTrialDepartmentName: String? = null, // 业务部门名称

    @Column("F_THIRD_TRIAL_USER_CODE")
    var thirdTrialUserCode: String? = null, // 业务员编码

    @Column("F_THIRD_TRIAL_USER_NAME")
    var thirdTrialUserName: String? = null, // 业务员名称

    @Column("F_TOPIC_NUMBER")
    var topicNumber: String? = null, // 选题号

    @Column("F_FIRST_TRIAL_PERSON_CODE")
    var firstTrialPersonCode: String? = null, // 初审人编码

    @Column("F_FIRST_TRIAL_PERSON_NAME")
    var firstTrialPersonName: String? = null, // 初审人名称

    @Column("F_FIRST_TRIAL_DATE")
    var firstTrialDate: String? = null, // 初审日期

    @Column("F_FIRST_TRIAL_OPINION")
    var firstTrialOpinion: String? = null, // 初审意见

    @Column("F_SECOND_TRIAL_PERSON_CODE")
    var secondTrialPersonCode: String? = null, // 复审人编码

    @Column("F_SECOND_TRIAL_PERSON_NAME")
    var secondTrialPersonName: String? = null, // 复审人名称

    @Column("F_SECOND_TRIAL_DATE")
    var secondTrialDate: String? = null, // 复审日期

    @Column("F_SECOND_TRIAL_OPINION")
    var secondTrialOpinion: String? = null, // 复审意见

    @Column("F_THIRD_TRIAL_PERSON_CODE")
    var thirdTrialPersonCode: String? = null, // 终审人编码

    @Column("F_THIRD_TRIAL_PERSON_NAME")
    var thirdTrialPersonName: String? = null, // 终审人名称

    @Column("F_THIRD_TRIAL_DATE")
    var thirdTrialDate: String? = null, // 终审日期

    @Column("F_THIRD_TRIAL_OPINION")
    var thirdTrialOpinion: String? = null, // 终审意见

    // 发稿单相关字段
    @Column("F_PUBLISH_BILL_NO")
    var publishBillNo: String? = null, // 发稿单号

    @Column("F_PUBLISH_BILL_DATE")
    var publishBillDate: String? = null, // 业务日期

    @Column("F_PUBLISH_PRINTING_YEAR_MONTH")
    var publishPrintingYearMonth: String? = null, // 发稿单印次年月

    @Column("F_PUBLISH_PRINTING_NO")
    var publishPrintingNo: String? = null, // 发稿单印次

    @Column("F_PUBLISH_BUSINESS_TYPE")
    var publishBusinessType: String? = null, // 发稿单业务类型

    @Column("F_PUBLISH_DEPARTMENT_CODE")
    var publishDepartmentCode: String? = null, // 业务部门编码

    @Column("F_PUBLISH_DEPARTMENT_NAME")
    var publishDepartmentName: String? = null, // 业务部门名称

    @Column("F_PUBLISH_USER_CODE")
    var publishUserCode: String? = null, // 业务员编码

    @Column("F_PUBLISH_USER_NAME")
    var publishUserName: String? = null, // 业务员名称

    @Column("F_PUBLISH_PERIOD")
    var publishPeriod: String? = null, // 出版期间

    @Column("F_REPRINT_ITEM_ISBN")
    var reprintItemIsbn: String? = null, // 重印物品书号

    @Column("F_REPRINT_ITEM_NAME")
    var reprintItemName: String? = null, // 重印物品名称

    // 书号申请相关字段
    @Column("F_BOOK_NUM_APPLY_BILL_NO")
    var bookNumApplyBillNo: String? = null, // 书号申请单号

    @Column("F_BOOK_NUM_BILL_DATE")
    var bookNumBillDate: String? = null, // 业务日期

    @Column("F_BOOK_NUM_DEPARTMENT_CODE")
    var bookNumDepartmentCode: String? = null, // 业务部门编码

    @Column("F_BOOK_NUM_DEPARTMENT_NAME")
    var bookNumDepartmentName: String? = null, // 业务部门名称

    @Column("F_BOOK_NUM_USER_CODE")
    var bookNumUserCode: String? = null, // 业务员编码

    @Column("F_BOOK_NUM_USER_NAME")
    var bookNumUserName: String? = null, // 业务员名称

    @Column("F_ISBN")
    var isbn: String? = null, // 书号

    @Column("F_CIP_INFO")
    var cipInfo: String? = null, // CIP信息

    @Column("F_EXTRA_CODE")
    var extraCode: String? = null, // 附加码

    @Column("F_CIP_TYPE")
    var cipType: String? = null, // CIP分类

    @Column("F_BOOK_NUM_SINO_BOOK_TYPE")
    var bookNumSinoBookType: String? = null, // 中图分类

    // 内部字段 (ID 类型)
    @Column("F_ITEM_ID")
    var itemId: Long? = null, // 物品ID

    @Column("F_TOPIC_RECORD_DEPARTMENT_ID")
    var topicRecordDepartmentId: Long? = null, // 业务部门ID

    @Column("F_DUTY_EDITOR_ID")
    var dutyEditorId: Long? = null, // 责任编辑ID

    @Column("F_NOTE_LANGUAGE_ID")
    var noteLanguageId: Long? = null, // 正文文种ID

    @Column("F_LANGUAGE_ID")
    var languageId: Long? = null, // 正文文字ID

    @Column("F_PUBLISH_METHOD_ID")
    var publishMethodId: Long? = null, // 经营方式ID

    @Column("F_SINO_BOOK_TYPE_ID")
    var sinoBookTypeId: Long? = null, // 中图分类ID

    @Column("F_BOOK_FORMAT_ID")
    var bookFormatId: Long? = null, // 开本ID

    @Column("F_BOOK_FORMAT_SIZE_ID")
    var bookFormatSizeId: Long? = null, // 开本尺寸ID

    @Column("F_BINDING_TYPE_ID")
    var bindingTypeId: Long? = null, // 装订方式ID

    @Column("F_IMPORTANT_RECORD_TYPE_ID")
    var importantRecordTypeId: Long? = null, // 重要选题类型ID

    @Column("F_PUBLISH_TYPE_ID")
    var publishTypeId: Long? = null, // 出版类别ID

    @Column("F_PARTNER_ID")
    var partnerId: Long? = null, // 合作方ID

    @Column("F_EXPECT_FINALIZATION_TIME_ID")
    var expectFinalizationTimeId: Long? = null, // 预计来稿时间ID

    @Column("F_EP_CARRIER_METHOD_ID")
    var epCarrierMethodId: Long? = null, // 载体形式ID

    @Column("F_BOOK_TYPE_ID")
    var bookTypeId: Long? = null, // 图书类型ID

    @Column("F_THIRD_TRIAL_DEPARTMENT_ID")
    var thirdTrialDepartmentId: Long? = null, // 三审单业务部门ID

    @Column("F_THIRD_TRIAL_USER_ID")
    var thirdTrialUserId: Long? = null, // 三审单业务员ID

    @Column("F_PUBLISH_DEPARTMENT_ID")
    var publishDepartmentId: Long? = null, // 发稿单业务部门ID

    @Column("F_PUBLISH_USER_ID")
    var publishUserId: Long? = null, // 发稿单业务员ID

    @Column("F_PUBLISH_BUSINESS_TYPE_ID")
    var publishBusinessTypeId: Long? = null, // 发稿单业务类型ID

    @Column("F_PUBLISH_PERIOD_ID")
    var publishPeriodId: Long? = null, // 出版期间ID

    @Column("F_BOOK_NUM_DEPARTMENT_ID")
    var bookNumDepartmentId: Long? = null, // 书号申请部门ID

    @Column("F_BOOK_NUM_APPLY_USER_ID")
    var bookNumApplyUserId: Long? = null, // 书号申请业务员ID

    // 审批状态相关字段
    @Column("F_TOPIC_RECORD_BILL_ID")
    var topicRecordBillId: Long? = null, // 选题申报单ID

    @Column("F_TOPIC_APPLY_STATUS")
    var topicApplyStatus: Int? = null, // 选题申报审批状态

    @Column("F_TOPIC_APPLY_LAST_USER_ID")
    var topicApplyLastUserId: Long? = null, // 选题申报最后审批人

    @Column("F_TOPIC_APPLY_LAST_TIME")
    var topicApplyLastTime: String? = null, // 选题申报最后审批时间

    @Column("F_THIRD_TRIAL_BILL_ID")
    var thirdTrialBillId: Long? = null, // 三审单ID

    @Column("F_THIRD_APPLY_STATUS")
    var thirdApplyStatus: Int? = null, // 三审单审批状态

    @Column("F_THIRD_APPLY_LAST_USER_ID")
    var thirdApplyLastUserId: Long? = null, // 三审单最后审批人

    @Column("F_THIRD_APPLY_LAST_TIME")
    var thirdApplyLastTime: String? = null, // 三审单最后审批时间

    @Column("F_PUBLISH_BILL_ID")
    var publishBillId: Long? = null, // 发稿单ID

    @Column("F_PUBLISH_APPLY_STATUS")
    var publishApplyStatus: Int? = null, // 发稿单审批状态

    @Column("F_PUBLISH_APPLY_LAST_USER_ID")
    var publishApplyLastUserId: Long? = null, // 发稿单最后审批人

    @Column("F_PUBLISH_APPLY_LAST_TIME")
    var publishApplyLastTime: String? = null, // 发稿单最后审批时间

    @Column("F_BOOK_NUM_APPLY_BILL_ID")
    var bookNumApplyBillId: Long? = null, // 书号申请单ID

    @Column("F_BOOK_NUM_APPLY_STATUS")
    var bookNumApplyStatus: Int? = null, // 书号申请审批状态

    @Column("F_BOOK_NUM_APPLY_LAST_USER_ID")
    var bookNumApplyLastUserId: Long? = null, // 书号申请最后审批人

    @Column("F_BOOK_NUM_APPLY_LAST_TIME")
    var bookNumApplyLastTime: String? = null, // 书号申请最后审批时间

    // 导入相关字段
    @Column("F_IM_TOPIC_RECORD_BILL_ID")
    var imTopicRecordBillId: Long? = null, // 导入的选题申报单ID

    @Column("F_IM_THIRD_TRIAL_BILL_ID")
    var imThirdTrialBillId: Long? = null, // 导入的三审单ID

    @Column("F_IM_PUBLISH_BILL_ID")
    var imPublishBillId: Long? = null, // 导入的发稿单ID

    @Column("F_IM_BOOK_NUM_APPLY_BILL_ID")
    var imBookNumApplyBillId: Long? = null, // 导入的书号申请单ID

    @Column("F_ROW_NO")
    var rowNo: Long? = null, // 行号

    @Column("F_ERR_MSG")
    var errMsg: String? = null, // 错误消息

    @Column("F_ORGAN_ID")
    var organId: Long? = null // 机构ID
)
