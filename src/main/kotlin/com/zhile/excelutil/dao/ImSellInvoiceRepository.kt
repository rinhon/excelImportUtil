package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImSellInvoice
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

/**
 * @author zlhp
 * @date 2025/6/30 15:39
 * @description:
 */
interface ImSellInvoiceRepository : CrudRepository<ImSellInvoice, Long> {
    @Modifying
    @Query(
        """
        INSERT INTO IM_SELL_INVOICE (
            F_INVOICE_NO, F_INVOICE_TYPE, F_INVOICE_NUMBER, F_INVOICE_USER_CODE, F_INVOICE_USER_NAME,
            F_INVOICE_DEPARTMENT_CODE, F_INVOICE_DEPARTMENT_NAME, F_INVOICE_DATE, F_INVOICE_CUSTOMER, F_REMARKS,
            F_ADJUST_AMOUNT, F_ADJUST_TAX_AMOUNT, F_ORDER_BILL_NO, F_ORDER_BILL_DATE, F_ORADER_BILL_ROW_NO,
            F_OUT_BILL_NO, F_OUT_BILL_DATE, F_OUT_BILL_ROW_NO, F_POSITION_CODE,F_POSITION_NAME, F_CUSTOMER_CODE, F_CUSTOMER_NAME, F_AREA,
            F_RECEIVE_ADDRESS, F_RECEIVE_MAN, F_RECEIVE_PHONE, F_GIST, F_ORDER_USER_CODE, F_ORDER_USER_NAME,
            F_ORDER_DEPARTMENT_CODE, F_ORDER_DEPARTMENT_NAME, F_ITEM_CODE, F_ITEM_NAME, F_PRODUCE_NUM, F_SET_PRICE,
            F_UNIT, F_QUANTITY, F_DISCOUNT, F_AMOUNT, F_TAX, F_TAX_AMOUNT, F_COST_PRICE, F_COST_AMOUNT, F_ACCOUNT_DATE
        ) VALUES  (
            :invoiceNo,:invoiceType,:invoiceNumber,:invoiceUserCode,:invoiceUserName,
            :invoiceDepartmentCode,:invoiceDepartmentName,:invoiceDate,:invoiceCustomer,:remarks,
            :adjustAmount,:adjustTaxAmount,:orderBillNo,:orderBillDate,:orderBillRowNo,
            :outBillNo,:outBillDate,:outBillRowNo,:positionCode,:positionName,:customerCode,:customerName,:area,
            :receiveAddress,:receiveMan,:receivePhone,:gist,:orderUserCode,:orderUserName,
            :orderDepartmentCode,:orderDepartmentName,:itemCode,:itemName,:produceNum,:setPrice,
            :unit,:quantity,:discount,:amount,:tax,:taxAmount,:costPrice,:costAmount,:accountDate
        )
    """
    )
    fun insertSellInvoice(
        @Param("invoiceNo") invoiceNo: String?,
        @Param("invoiceType") invoiceType: String?,
        @Param("invoiceNumber") invoiceNumber: String?,
        @Param("invoiceUserCode") invoiceUserCode: String?,
        @Param("invoiceUserName") invoiceUserName: String?,
        @Param("invoiceDepartmentCode") invoiceDepartmentCode: String?,
        @Param("invoiceDepartmentName") invoiceDepartmentName: String?,
        @Param("invoiceDate") invoiceDate: String?,
        @Param("invoiceCustomer") invoiceCustomer: String?,
        @Param("remarks") remarks: String?,
        @Param("adjustAmount") adjustAmount: String?,
        @Param("adjustTaxAmount") adjustTaxAmount: String?,
        @Param("orderBillNo") orderBillNo: String?,
        @Param("orderBillDate") orderBillDate: String?,
        @Param("orderBillRowNo") orderBillRowNo: String?,
        @Param("outBillNo") outBillNo: String?,
        @Param("outBillDate") outBillDate: String?,
        @Param("outBillRowNo") outBillRowNo: String?,
        @Param("positionCode") positionCode: String?,
        @Param("positionName") positionName: String?,
        @Param("customerCode") customerCode: String?,
        @Param("customerName") customerName: String?,
        @Param("area") area: String?,
        @Param("receiveAddress") receiveAddress: String?,
        @Param("receiveMan") receiveMan: String?,
        @Param("receivePhone") receivePhone: String?,
        @Param("gist") gist: String?,
        @Param("orderUserCode") orderUserCode: String?,
        @Param("orderUserName") orderUserName: String?,
        @Param("orderDepartmentCode") orderDepartmentCode: String?,
        @Param("orderDepartmentName") orderDepartmentName: String?,
        @Param("itemCode") itemCode: String?,
        @Param("itemName") itemName: String?,
        @Param("produceNum") produceNum: String?,
        @Param("setPrice") setPrice: String?,
        @Param("unit") unit: String?,
        @Param("quantity") quantity: String?,
        @Param("discount") discount: String?,
        @Param("amount") amount: String?,
        @Param("tax") tax: String?,
        @Param("taxAmount") taxAmount: String?,
        @Param("costPrice") costPrice: String?,
        @Param("costAmount") costAmount: String?,
        @Param("accountDate") accountDate: String?,
    ) {
    }

    @Modifying
    @Query(
        """
            DELETE FROM IM_SELL_INVOICE
        """
    )
    fun deleteAllImSellInvoice()

}
