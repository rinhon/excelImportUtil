package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImCustomer
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ImCustomerRepository : CrudRepository<ImCustomer, Long> {

    @Modifying
    @Query(
        """
            INSERT INTO IM_CUSTOMER (
                F_KEY_ID, F_CODE, F_NAME, F_ABBR, F_CATALOG, 
                F_IN_UNIT, F_NATURE1, F_NATURE2, F_CUSTOMER_TYPE, F_AREA,
                F_BANK_ACCOUNT, F_BANK, F_CORRESPONDENCE_CONTACT, F_CARD_TYPE, F_CARD_NO,
                F_CORRESPONDENCE_TEL, F_CORRESPONDENCE_ADDRESS, F_ID, F_TOP_BANK_ID, F_AREA_ID,
                F_CUSTOMER_TYPE_ID, F_CARD_TYPE_ID
            ) VALUES (
                :keyId, :code, :name, :abbr, :catalog,
                :inUnit, :nature1, :nature2, :customerType, :area,
                :bankAccount, :bank, :correspondenceContact, :cardType, :cardNo,
                :correspondenceTel, :correspondenceAddress, :id, :topBankId, :areaId,
                :customerTypeId, :cardTypeId
            )
        """
    )
    fun insertCustomer(
        @Param("keyId") keyId: Long?,
        @Param("code") code: String?,
        @Param("name") name: String?,
        @Param("abbr") abbr: String?,
        @Param("catalog") catalog: String?,
        @Param("inUnit") inUnit: String?,
        @Param("nature1") nature1: String?,
        @Param("nature2") nature2: String?,
        @Param("customerType") customerType: String?,
        @Param("area") area: String?,
        @Param("bankAccount") bankAccount: String?,
        @Param("bank") bank: String?,
        @Param("correspondenceContact") correspondenceContact: String?,
        @Param("cardType") cardType: String?,
        @Param("cardNo") cardNo: String?,
        @Param("correspondenceTel") correspondenceTel: String?,
        @Param("correspondenceAddress") correspondenceAddress: String?,
        @Param("id") id: Long?,
        @Param("topBankId") topBankId: Long?,
        @Param("areaId") areaId: Long?,
        @Param("customerTypeId") customerTypeId: Long?,
        @Param("cardTypeId") cardTypeId: Long?
    )
}