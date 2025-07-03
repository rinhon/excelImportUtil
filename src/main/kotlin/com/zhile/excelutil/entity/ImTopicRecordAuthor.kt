package com.zhile.excelutil.entity

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("IM_TOPIC_RECORD_AUTHOR")
data class ImTopicRecordAuthor(
    // 选题单号
    @Column("F_TOPIC_RECORD_BILL_NO")
    var topicRecordBillNo: String? = null,

    // 书名
    @Column("F_BOOK_NAME")
    var bookName: String? = null,

    // 作者编码
    @Column("F_AUTHOR_CODE")
    var authorCode: String? = null,

    // 作者姓名
    @Column("F_AUTHOR_NAME")
    var authorName: String? = null,

    // 是否主要作者
    @Column("F_MAIN_AUTHOR")
    var mainAuthor: String? = null,

    // 著作方式
    @Column("F_WRITE_TYPE")
    var writeType: String? = null,

    // 所在单位
    @Column("F_AUTHOR_COMPANY")
    var authorCompany: String? = null,

    // 职称
    @Column("F_AUTHOR_TITLE")
    var authorTitle: String? = null,

    // 作者简介
    @Column("F_MAJOR_WORKS")
    var majorWorks: String? = null,

    // 作者背景审查情况
    @Column("F_BACKGROUND_DETAIL")
    var backgroundDetail: String? = null,

    // 作者已出版书市场情况
    @Column("F_MARKET_CONDITIONS")
    var marketConditions: String? = null,

    // 备注
    @Column("F_REMARKS")
    var remarks: String? = null,

    // 国籍
    @Column("F_NATION")
    var nation: String? = null,

    // 朝代
    @Column("F_DYNASTY")
    var dynasty: String? = null,

    // 机构ID
    @Column("F_ORGAN_ID")
    var organId: Long? = null
)
