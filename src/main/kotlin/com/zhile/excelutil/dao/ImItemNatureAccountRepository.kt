package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImItemNatureAccount
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * @author Rinhon
 * @date 2025/6/17 10:02
 * @description:
 */
@Repository
interface ImItemNatureAccountRepository : CrudRepository<ImItemNatureAccount, Long> {

    @Modifying
    @Query(
        """
        INSERT INTO IM_ITEM_NATURE_ACCOUNT (
            F_ITEM_NATURE, F_INVENTORY_ACCT_CODE, F_INCOME_ACCT_CODE, F_COST_ACCT_CODE,
            F_INPUT_TAX_ACCT_CODE, F_OUTPUT_TAX_ACCT_CODE, F_STOCK_OUT_ITEM_ACCT_CODE, F_PROL_ESTE_ACCT_CODE,
            F_PROL_ESTE_RECEIVE_ACCT_CODE, F_PROL_ESTE_INPUT_ACCT_CODE, F_PROL_ESTE_OUTPUT_ACCT_CODE, F_ITEM_NATURE_ID         
        ) VALUES (
            :itemNature, :inventoryAcctCode, :incomeAcctCode, :costAcctCode,
            :inputTaxAcctCode, :outputTaxAcctCode, :stockOutItemAcctCode, :prolEsteAcctCode,
            :prolEsteReceiveAcctCode, :prolEsteInputAcctCode, :prolEsteOutputAcctCode, :itemNatureId
        )
        """
    )
    fun insertItemNatureAccount(
        @Param("itemNature") itemNature: String?,
        @Param("inventoryAcctCode") inventoryAcctCode: String?,
        @Param("incomeAcctCode") incomeAcctCode: String?,
        @Param("costAcctCode") costAcctCode: String?,
        @Param("inputTaxAcctCode") inputTaxAcctCode: String?,
        @Param("outputTaxAcctCode") outputTaxAcctCode: String?,
        @Param("stockOutItemAcctCode") stockOutItemAcctCode: String?,
        @Param("prolEsteAcctCode") prolEsteAcctCode: String?,
        @Param("prolEsteReceiveAcctCode") prolEsteReceiveAcctCode: String?,
        @Param("prolEsteInputAcctCode") prolEsteInputAcctCode: String?,
        @Param("prolEsteOutputAcctCode") prolEsteOutputAcctCode: String?,
        @Param("itemNatureId") itemNatureId: Long?
    )

    @Modifying
    @Query(
        """
        DELETE FROM IM_ITEM_NATURE_ACCOUNT
        """
    )
    fun deleteAllImItemNatureAccount()
}