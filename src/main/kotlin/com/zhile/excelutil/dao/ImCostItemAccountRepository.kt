package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImCostItemAccount
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * @author zlhp
 * @date 2025/6/20 15:04
 * @description:
 */
@Repository
interface ImCostItemAccountRepository : CrudRepository<ImCostItemAccount, Long> {

    @Modifying
    @Query(
        """
        INSERT INTO IM_COST_ITEM_ACCOUNT (
            F_COST_ITEM_CODE, F_COST_ITEM_NAME, F_PARENT_COST_ITEM, F_TAX, F_REMARKS, 
            F_PROT_COST_ACCT_CODE, F_PAY_PROT_COST_ACCT_CODE, F_SETT_COPE_ACCT_CODE, 
            F_PRE_PAY_ACCT_CODE, F_PAY_ACCT_CODE, F_INPUT_TAX_ACCT_CODE, 
            F_INPUT_TAX_INVOICE_ACCT_CODE, F_VALUE_TAX_ACCT_CODE, F_CITY_MAINTAIN_ACCT_CODE, 
            F_EDUCATION_ACCT_CODE, F_LOCAL_EDUCATION_ACCT_CODE, F_LABOR_TAX_ACCT_CODE, 
            F_ROYALTIES_TAX_ACCT_CODE, F_COST_ITEM_ID
        ) VALUES  (
            :costItemCode, :costItemName, :parentCostItem, :tax, :remarks, 
            :protCostAcctCode, :payProtCostAcctCode, :settCopeAcctCode, 
            :prePayAcctCode, :payAcctCode, :inputTaxAcctCode, 
            :inputTaxInvoiceAcctCode, :valueTaxAcctCode, :cityMaintainAcctCode, 
            :educationAcctCode, :localEducationAcctCode, :laborTaxAcctCode, 
            :royaltiesTaxAcctCode, :costItemId
        )
    """
    )
    fun insertCostItem(
        @Param("costItemCode") costItemCode: String?,
        @Param("costItemName") costItemName: String?,
        @Param("parentCostItem") parentCostItem: String?,
        @Param("tax") tax: String?,
        @Param("remarks") remarks: String?,
        @Param("protCostAcctCode") protCostAcctCode: String?,
        @Param("payProtCostAcctCode") payProtCostAcctCode: String?,
        @Param("settCopeAcctCode") settCopeAcctCode: String?,
        @Param("prePayAcctCode") prePayAcctCode: String?,
        @Param("payAcctCode") payAcctCode: String?,
        @Param("inputTaxAcctCode") inputTaxAcctCode: String?,
        @Param("inputTaxInvoiceAcctCode") inputTaxInvoiceAcctCode: String?,
        @Param("valueTaxAcctCode") valueTaxAcctCode: String?,
        @Param("cityMaintainAcctCode") cityMaintainAcctCode: String?,
        @Param("educationAcctCode") educationAcctCode: String?,
        @Param("localEducationAcctCode") localEducationAcctCode: String?,
        @Param("laborTaxAcctCode") laborTaxAcctCode: String?,
        @Param("royaltiesTaxAcctCode") royaltiesTaxAcctCode: String?,
        @Param("costItemId") costItemId: Long?
    ) {
    }

    @Modifying
    @Query(
        """
            DELETE FROM IM_COST_ITEM_ACCOUNT
        """
    )
    fun deleteAllImCostItemAccount()
}