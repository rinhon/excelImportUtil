package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImPurchaseInvoice
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

/**
 * @author zlhp
 * @date 2025/7/1 16:11
 * @description:
 */
@Repository
interface ImPurchaseInvoiceRepository : CrudRepository<ImPurchaseInvoice, Long> {


    @Modifying
    @Query(
        value = """
        INSERT INTO IM_PURCHASE_INVOICE (
            F_INVOICE_BILL_NO, F_INVOICE_TYPE, F_INVOICE_NO, F_INVOICE_USER_CODE, F_INVOICE_USER_NAME,
            F_INVOICE_DEPARTMENT_CODE, F_INVOICE_DEPARTMENT_NAME, F_INVOICE_DATE, F_REMARKS, F_ORDER_BILL_NO,
            F_ORDER_DATE, F_ORDER_ROW_NO, F_IN_BILL_NO, F_IN_DATE, F_IN_ROW_NO,
            F_POSITION, F_CUSTOMER_CODE, F_CUSTOMER_NAME, F_AREA, F_SEND_ADDRESS,
            F_SEND_MAN, F_SEND_PHONE, F_GIST, F_ORDER_USER_CODE, F_ORDER_USER_NAME,
            F_ORDER_DEPARTMENT_CODE, F_ORDER_DEPARTMENT_NAME, F_ITEM_CODE, F_ITEM_NAME, F_PRODUCE_NUM,
            F_SET_PRICE, F_DISCOUNT, F_UNIT, F_QUANTITY, F_AVERAGE_DISCOUNT,
            F_NO_INVOICE_AMOUNT, F_TAX, F_TAX_AMOUNT, F_ACCOUNT_BANK_NAME, F_BANK_NAME,
            F_ACCOUNT_NAME, F_ACCOUNT_NO, F_ACCOUNT_DATE, F_INVOICE_TYPE_ID, F_INVOICE_USER_ID,
            F_INVOICE_DEPARTMENT_ID, F_POSITION_ID, F_CUSTOMER_ID, F_ORDER_USER_ID, F_ORDER_DEPARTMENT_ID,
            F_ITEM_ID, F_TAX_ID, F_UNIT_ID, F_ORDER_BILL_ID, F_RECEIVE_BILL_ID,
            F_IN_BILL_ID, F_INVOICE_ID, F_INVO_VERI_ID
        ) VALUES (
            :invoiceBillNo, :invoiceType, :invoiceNo, :invoiceUserCode, :invoiceUserName,
            :invoiceDepartmentCode, :invoiceDepartmentName, :invoiceDate, :remarks, :orderBillNo,
            :orderDate, :orderRowNo, :inBillNo, :inDate, :inRowNo,
            :position, :customerCode, :customerName, :area, :sendAddress,
            :sendMan, :sendPhone, :gist, :orderUserCode, :orderUserName,
            :orderDepartmentCode, :orderDepartmentName, :itemCode, :itemName, :produceNum,
            :setPrice, :discount, :unit, :quantity, :averageDiscount,
            :noInvoiceAmount, :tax, :taxAmount, :accountBankName, :bankName,
            :accountName, :accountNo, :accountDate, :invoiceTypeId, :invoiceUserId,
            :invoiceDepartmentId, :positionId, :customerId, :orderUserId, :orderDepartmentId,
            :itemId, :taxId, :unitId, :orderBillId, :receiveBillId,
            :inBillId, :invoiceId, :invoVeriId
        )
        """
    )
    fun insertPurchaseInvoice(
        @Param("invoiceBillNo") invoiceBillNo: String?,
        @Param("invoiceType") invoiceType: String?,
        @Param("invoiceNo") invoiceNo: String?,
        @Param("invoiceUserCode") invoiceUserCode: String?,
        @Param("invoiceUserName") invoiceUserName: String?,
        @Param("invoiceDepartmentCode") invoiceDepartmentCode: String?,
        @Param("invoiceDepartmentName") invoiceDepartmentName: String?,
        @Param("invoiceDate") invoiceDate: String?,
        @Param("remarks") remarks: String?,
        @Param("orderBillNo") orderBillNo: String?,
        @Param("orderDate") orderDate: String?,
        @Param("orderRowNo") orderRowNo: String?,
        @Param("inBillNo") inBillNo: String?,
        @Param("inDate") inDate: String?,
        @Param("inRowNo") inRowNo: String?,
        @Param("position") position: String?,
        @Param("customerCode") customerCode: String?,
        @Param("customerName") customerName: String?,
        @Param("area") area: String?,
        @Param("sendAddress") sendAddress: String?,
        @Param("sendMan") sendMan: String?,
        @Param("sendPhone") sendPhone: String?,
        @Param("gist") gist: String?,
        @Param("orderUserCode") orderUserCode: String?,
        @Param("orderUserName") orderUserName: String?,
        @Param("orderDepartmentCode") orderDepartmentCode: String?,
        @Param("orderDepartmentName") orderDepartmentName: String?,
        @Param("itemCode") itemCode: String?,
        @Param("itemName") itemName: String?,
        @Param("produceNum") produceNum: String?,
        @Param("setPrice") setPrice: String?,
        @Param("discount") discount: String?,
        @Param("unit") unit: String?,
        @Param("quantity") quantity: String?,
        @Param("averageDiscount") averageDiscount: String?,
        @Param("noInvoiceAmount") noInvoiceAmount: String?,
        @Param("tax") tax: String?,
        @Param("taxAmount") taxAmount: String?,
        @Param("accountBankName") accountBankName: String?,
        @Param("bankName") bankName: String?,
        @Param("accountName") accountName: String?,
        @Param("accountNo") accountNo: String?,
        @Param("accountDate") accountDate: String?,
        @Param("invoiceTypeId") invoiceTypeId: Long?,
        @Param("invoiceUserId") invoiceUserId: Long?,
        @Param("invoiceDepartmentId") invoiceDepartmentId: Long?,
        @Param("positionId") positionId: Long?,
        @Param("customerId") customerId: Long?,
        @Param("orderUserId") orderUserId: Long?,
        @Param("orderDepartmentId") orderDepartmentId: Long?,
        @Param("itemId") itemId: Long?,
        @Param("taxId") taxId: Long?,
        @Param("unitId") unitId: Long?,
        @Param("orderBillId") orderBillId: Long?,
        @Param("receiveBillId") receiveBillId: Long?,
        @Param("inBillId") inBillId: Long?,
        @Param("invoiceId") invoiceId: Long?,
        @Param("invoVeriId") invoVeriId: Long?
    )

    @Modifying
    @Transactional
    @Query(
        value = """
            DELETE FROM IM_PURCHASE_INVOICE
        """
    )
    fun deleteAllPurchaseInvoice()
}
