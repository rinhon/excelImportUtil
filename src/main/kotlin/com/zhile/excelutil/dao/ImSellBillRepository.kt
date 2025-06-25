package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImSellBill
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * @author Rinhon
 * @date 2025/6/20 11:08
 * @description:
 */
@Repository
interface ImSellBillRepository : CrudRepository<ImSellBill, Long> {
    @Modifying
    @Query(
        """
        INSERT INTO IM_SELL_BILL (
            F_ORDER_BILL_NO, F_ORDER_BILL_DATE, F_ORDER_BILL_ROWNUM, F_OUT_BILL_NO, F_OUT_BILL_DATE, F_OUT_BILL_ROWNUM,
            F_CUSTOMER_CODE, F_CUSTOMER_NAME, F_AREA, F_RECEIVING_ADDRESS, F_RECEIVING_LINKMAN, F_RECEIVING_LINKMAN_TEL,
            F_GIST, F_USER_CODE, F_USER_NAME, F_DEPARTMENT_CODE, F_DEPARTMENT_NAME, F_ITEM_CODE, F_ITEM_NAME, F_PRODUCE_NUM,
            F_SET_PRICE, F_UNIT, F_NO_INVOICE_QUANTITY, F_NO_INVOICE_DISCOUNT, F_NO_INVOICE_AMOUNT, F_NO_INVOICE_REAL_AMOUNT,
            F_TAX, F_TAX_AMOUNT, F_POSITION, F_COST_PRICE, F_COST_AMOUNT, F_REMARKS, F_FIRST_IN_TIME
        ) VALUES (
            :orderBillNo, :orderBillDate, :orderBillRownum, :outBillNo, :outBillDate, :outBillRownum,
            :customerCode, :customerName, :area, :receivingAddress, :receivingLinkman, :receivingLinkmanTel,
            :gist, :userCode, :userName, :departmentCode, :departmentName, :itemCode, :itemName, :produceNum,
            :setPrice, :unit, :noInvoiceQuantity, :noInvoiceDiscount, :noInvoiceAmount, :noInvoiceRealAmount,
            :tax, :taxAmount, :position, :costPrice, :costAmount, :remarks, :firstInTime
        )
        """
    )
    fun insertSellBill(
        @Param("orderBillNo") orderBillNo: String?,
        @Param("orderBillDate") orderBillDate: String?,
        @Param("orderBillRownum") orderBillRownum: String?,
        @Param("outBillNo") outBillNo: String?,
        @Param("outBillDate") outBillDate: String?,
        @Param("outBillRownum") outBillRownum: String?,
        @Param("customerCode") customerCode: String?,
        @Param("customerName") customerName: String?,
        @Param("area") area: String?,
        @Param("receivingAddress") receivingAddress: String?,
        @Param("receivingLinkman") receivingLinkman: String?,
        @Param("receivingLinkmanTel") receivingLinkmanTel: String?,
        @Param("gist") gist: String?,
        @Param("userCode") userCode: String?,
        @Param("userName") userName: String?,
        @Param("departmentCode") departmentCode: String?,
        @Param("departmentName") departmentName: String?,
        @Param("itemCode") itemCode: String?,
        @Param("itemName") itemName: String?,
        @Param("produceNum") produceNum: String?,
        @Param("setPrice") setPrice: String?,
        @Param("unit") unit: String?,
        @Param("noInvoiceQuantity") noInvoiceQuantity: String?,
        @Param("noInvoiceDiscount") noInvoiceDiscount: String?,
        @Param("noInvoiceAmount") noInvoiceAmount: String?,
        @Param("noInvoiceRealAmount") noInvoiceRealAmount: String?,
        @Param("tax") tax: String?,
        @Param("taxAmount") taxAmount: String?,
        @Param("position") position: String?,
        @Param("costPrice") costPrice: String?,
        @Param("costAmount") costAmount: String?,
        @Param("remarks") remarks: String?,
        @Param("firstInTime") firstInTime: String?
    )

    @Modifying
    @Query(
        """
        DELETE FROM IM_SELL_BILL
        """
    )
    fun deleteAllImSellBill()

}