package com.zhile.excelutil.entity


import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table


/**
 * @author Rinhon
 * @date 2025/6/17 09:52
 * @description: 导入物品中间表
 */
@Table("IM_ITEM")
data class ImItem(

    @Id
    @Column("F_KEY_ID")
    var keyId: Long? = null,
    @Column("F_CODE")
    var code: String? = null,  //编码
    @Column("F_NAME")
    var name: String? = null,  //名称
    @Column("F_ABBR")
    var abbr: String? = null,  //物品简称
    @Column("F_BAR_CODE")
    var barCode: String? = null,  //条码
    @Column("F_SET_PRICE")
    var setPrice: String? = null,  //定价
    @Column("F_SPEC")
    var spec: String? = null,  //规格型号
    @Column("F_PUBLISH_TYPE")
    var publishType: String? = null,  //出版类别
    @Column("F_PUBLISH_METHOD")
    var publishMethod: String? = null,  //经营方式
    @Column("F_CATEGORY")
    var category: String? = null,  //物品分类
    @Column("F_ITEM_TYPE")
    var itemType: String? = null,  //物品类型
    @Column("F_NATURE")
    var nature: String? = null,  //财务分类
    @Column("F_LENGTH")
    var length: String? = null,  //长度
    @Column("F_WIDTH")
    var width: String? = null,  //宽度
    @Column("F_HEIGHT")
    var height: String? = null,  //高度
    @Column("F_PACK_UNIT")
    var packUnit: String? = null,  //规格包装
    @Column("F_UNIT")
    var unit: String? = null,  //计量单位
    @Column("F_KIT")
    var kit: String? = null,  //是否套装物品
    @Column("F_ISBN")
    var isbn: String? = null,  //isbn
    @Column("F_AUX_CODE")
    var auxCode: String? = null,  //附加码
    @Column("F_SERIES_NAME")
    var seriesName: String? = null,  //丛书名
    @Column("F_VICE_BOOK_NAME")
    var viceBookName: String? = null,  //副书名
    @Column("F_EDITION_YEAR_MONTH")
    var editionYearMonth: String? = null,  //版次时间-年月
    @Column("F_EDITION_NO")
    var editionNo: String? = null,  //版次序号
    @Column("F_MAIN_AUTHOR")
    var mainAuthor: String? = null,  //主要作者
    @Column("F_DEPARTMENT_CODE")
    var departmentCode: String? = null,  //编辑部门编码
    @Column("F_DEPARTMENT_NAME")
    var departmentName: String? = null,  //编辑部门名称
    @Column("F_DUTY_EDITOR_NAME")
    var dutyEditorName: String? = null,  //责任编辑
    @Column("F_DUTY_EDITOR_CODE")
    var dutyEditorCode: String? = null,  //责任编辑
    @Column("F_PUBLISH_PERIOD")
    var publishPeriod: String? = null,  //出版期间
    @Column("F_PRINT_SHEET")
    var printSheet: String? = null,  //印张
    @Column("F_FORMAT")
    var format: String? = null,  //开本
    @Column("F_FORMAT_SIZE")
    var formatSize: String? = null,  //开本尺寸
    @Column("F_TOPIC_TYPE")
    var topicType: String? = null,  //选题类别
    @Column("F_BINDING_TYPE")
    var bindingType: String? = null,  //装订方式
    @Column("F_LANGUAGE")
    var language: String? = null,  //文种
    @Column("F_NOTE_LANGUAGE")
    var noteLanguage: String? = null,  //正文文字
    @Column("F_SUMMARY")
    var summary: String? = null,  //内容简介
    @Column("F_PERFACE")
    var perface: String? = null,  //前言
    @Column("F_CATALOG")
    var catalog: String? = null,  //目录
    @Column("F_BOOK_REVIEW")
    var bookReview: String? = null,  //书评
    @Column("F_BOOK_ABSTRACT")
    var bookAbstract: String? = null,  //摘要
    @Column("F_CIP_INFO")
    var cipInfo: String? = null,  //CIP信息
    @Column("F_REMARKS")
    var remarks: String? = null,  //备注
    @Column("F_ID")
    var id: Long? = null,
    @Column("F_ITEM_TYPE_ID")
    var itemTypeId: Long? = null,
    @Column("F_EDIT_DEPARTMENT_ID")
    var editDepartmentId: Long? = null,
    @Column("F_DUTY_EDITOR_ID")
    var dutyEditorId: Long? = null,
    @Column("F_PUBLISH_PERIOD_ID")
    var publishPeriodId: Long? = null,
    @Column("F_FORMAT_ID")
    var formatId: Long? = null,
    @Column("F_FORMAT_SIZE_ID")
    var formatSizeId: Long? = null,
    @Column("F_NATURE_ID")
    var natureId: Long? = null,
    @Column("F_LANGUAGE_ID")
    var languageId: Long? = null,
    @Column("F_PUBLISH_METHOD_ID")
    var publishMethodId: Long? = null,
    @Column("F_UNIT_ID")
    var unitId: Long? = null,
    @Column("F_BINDING_TYPE_ID")
    var bindingTypeId: Long? = null
)
