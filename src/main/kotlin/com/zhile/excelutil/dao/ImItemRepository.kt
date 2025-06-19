package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImItem
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * @author Rinhon
 * @date 2025/6/17 09:55
 * @description:
 */
@Repository
interface ImItemRepository : CrudRepository<ImItem, Long> {


    @Modifying
    @Query(
        """
        INSERT INTO IM_ITEM (
            F_KEY_ID, F_CODE, F_NAME, F_ABBR, F_BAR_CODE, 
            F_SET_PRICE, F_SPEC, F_PUBLISH_TYPE, F_PUBLISH_METHOD, F_CATEGORY, 
            F_ITEM_TYPE, F_NATURE, F_LENGTH, F_WIDTH, F_HEIGHT, 
            F_PACK_UNIT, F_UNIT, F_KIT, F_ISBN, F_AUX_CODE, 
            F_SERIES_NAME, F_VICE_BOOK_NAME, F_EDITION_YEAR_MONTH, F_EDITION_NO, F_MAIN_AUTHOR, 
            F_DEPARTMENT_CODE, F_DEPARTMENT_NAME, F_DUTY_EDITOR_CODE, F_DUTY_EDITOR_NAME, F_PUBLISH_PERIOD, 
            F_PRINT_SHEET, F_FORMAT, F_FORMAT_SIZE, F_TOPIC_TYPE, F_BINDING_TYPE, 
            F_LANGUAGE, F_NOTE_LANGUAGE, F_SUMMARY, F_PERFACE, F_CATALOG, 
            F_BOOK_REVIEW, F_BOOK_ABSTRACT, F_CIP_INFO, F_REMARKS, F_ID, 
            F_ITEM_TYPE_ID, F_EDIT_DEPARTMENT_ID, F_DUTY_EDITOR_ID, F_PUBLISH_PERIOD_ID, F_FORMAT_ID, 
            F_FORMAT_SIZE_ID, F_NATURE_ID, F_LANGUAGE_ID, F_NOTE_LANGUAGE_ID, F_PUBLISH_METHOD_ID, 
            F_UNIT_ID, F_BINDING_TYPE_ID
        ) VALUES (
            :keyId, :code, :name, :abbr, :barCode, 
            :setPrice, :spec, :publishType, :publishMethod, :category, 
            :itemType, :nature, :length, :width, :height, 
            :packUnit, :unit, :kit, :isbn, :auxCode, 
            :seriesName, :viceBookName, :editionYearMonth, :editionNo, :mainAuthor, 
            :departmentCode, :departmentName, :dutyEditorCode, :dutyEditorName, :publishPeriod, 
            :printSheet, :format, :formatSize, :topicType, :bindingType, 
            :language, :noteLanguage, :summary, :perface, :catalog, 
            :bookReview, :bookAbstract, :cipInfo, :remarks, :id, 
            :itemTypeId, :editDepartmentId, :dutyEditorId, :publishPeriodId, :formatId, 
            :formatSizeId, :natureId, :languageId, :noteLanguageId, :publishMethodId, 
            :unitId, :bindingTypeId
        )
        """
    )
    fun insertItem(
        @Param("keyId") keyId: Long?,
        @Param("code") code: String?,
        @Param("name") name: String?,
        @Param("abbr") abbr: String?,
        @Param("barCode") barCode: String?,
        @Param("setPrice") setPrice: String?,
        @Param("spec") spec: String?,
        @Param("publishType") publishType: String?,
        @Param("publishMethod") publishMethod: String?,
        @Param("category") category: String?,
        @Param("itemType") itemType: String?,
        @Param("nature") nature: String?,
        @Param("length") length: String?,
        @Param("width") width: String?,
        @Param("height") height: String?,
        @Param("packUnit") packUnit: String?,
        @Param("unit") unit: String?,
        @Param("kit") kit: String?,
        @Param("isbn") isbn: String?,
        @Param("auxCode") auxCode: String?,
        @Param("seriesName") seriesName: String?,
        @Param("viceBookName") viceBookName: String?,
        @Param("editionYearMonth") editionYearMonth: String?,
        @Param("editionNo") editionNo: String?,
        @Param("mainAuthor") mainAuthor: String?,
        @Param("departmentCode") departmentCode: String?,
        @Param("departmentName") departmentName: String?,
        @Param("dutyEditorCode") dutyEditorCode: String?,
        @Param("dutyEditorName") dutyEditorName: String?,
        @Param("publishPeriod") publishPeriod: String?,
        @Param("printSheet") printSheet: String?,
        @Param("format") format: String?,
        @Param("formatSize") formatSize: String?,
        @Param("topicType") topicType: String?,
        @Param("bindingType") bindingType: String?,
        @Param("language") language: String?,
        @Param("noteLanguage") noteLanguage: String?,
        @Param("summary") summary: String?,
        @Param("perface") perface: String?,
        @Param("catalog") catalog: String?,
        @Param("bookReview") bookReview: String?,
        @Param("bookAbstract") bookAbstract: String?,
        @Param("cipInfo") cipInfo: String?,
        @Param("remarks") remarks: String?,
        @Param("id") id: Long?,
        @Param("itemTypeId") itemTypeId: Long?,
        @Param("editDepartmentId") editDepartmentId: Long?,
        @Param("dutyEditorId") dutyEditorId: Long?,
        @Param("publishPeriodId") publishPeriodId: Long?,
        @Param("formatId") formatId: Long?,
        @Param("formatSizeId") formatSizeId: Long?,
        @Param("natureId") natureId: Long?,
        @Param("languageId") languageId: Long?,
        @Param("noteLanguageId") noteLanguageId: Long?,
        @Param("publishMethodId") publishMethodId: Long?,
        @Param("unitId") unitId: Long?,
        @Param("bindingTypeId") bindingTypeId: Long?
    )
}

