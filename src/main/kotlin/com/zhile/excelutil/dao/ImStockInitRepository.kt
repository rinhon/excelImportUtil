package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImStockInit
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * @author zlhp
 * @date 2025/6/20 15:06
 * @description:
 */
@Repository
interface ImStockInitRepository : CrudRepository<ImStockInit, Long> {
    @Modifying
    @Query(
        """
        INSERT INTO IM_STOCK_INIT (
            F_ITEM_CODE, F_ITEM_NAME, F_ISBN, F_SPEC, F_POSITION, 
            F_FRIST_IN_DATE, F_PRODUCE_NUM, F_QUANTITY, F_COST_PRICE, F_AMOUNT, 
            F_ITEM_ID, F_POSITION_ID, F_BILL_ID
        ) VALUES  (
            :itemCode, :itemName, :isbn, :spec, :position, 
            :fristInDate, :produceNum, :quantity, :costPrice, :amount, 
            :itemId, :positionId, :billId
        )
    """
    )
    fun insertImStockInit(
        @Param("itemCode") itemCode: String?,
        @Param("itemName") itemName: String?,
        @Param("isbn") isbn: String?,
        @Param("spec") spec: String?,
        @Param("position") position: String?,
        @Param("fristInDate") fristInDate: String?,
        @Param("produceNum") produceNum: String?,
        @Param("quantity") quantity: String?,
        @Param("costPrice") costPrice: String?,
        @Param("amount") amount: String?,
        @Param("itemId") itemId: Long?,
        @Param("positionId") positionId: Long?,
        @Param("billId") billId: Long?
    ) {
    }
}