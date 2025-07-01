package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImSellReserve
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface ImSellReserveRepository : CrudRepository<ImSellReserve, Long> {
    @Modifying
    @Query(
        """
        INSERT INTO IM_SELL_RESERVE (
            F_BILL_NO, F_CUSTOMER_CODE, F_CUSTOMER_NAME, F_DATE, F_DEPARTMENT_CODE, F_DEPARTMENT_NAME,
            F_USER_CODE, F_USER_NAME, F_IS_INVOICE, F_INVOICE_TYPE, F_INVOICE_NO, F_INVOICE_DATE,
            F_INVOICE_CUSTOMER, F_CONTRACT_NO, F_RECEIVE_DATE, F_ITEM_CODE, F_ITEM_NAME, F_TOPIC_APPLY,
            F_QUANTITY, F_AMOUNT, F_TAX, F_TAX_AMOUNT, F_REAL_AMOUNT, F_REMARKS, F_IN_AMOUNT, 
            F_ACCOUNT_DATE, F_CUSTOMER_ID, F_INVOICE_CUSTOMER_ID, F_DEPARTMENT_ID, F_ITEM_ID, 
            F_USER_ID, F_INVOICE_TYPE_ID, F_TAX_ID, F_RESERVE_BILL_ID, F_RECEIVE_BILL_ID
        ) VALUES  (
            :billNo,:customerCode,:customerName,:date,:departmentCode,:departmentName,:userCode,:userName,
            :isInvoice,:invoiceType,:invoiceNo,:invoiceDate,:invoiceCustomer,:contractNo,:receiveDate,
            :itemCode,:itemName,:topicApply,:quantity,:amount,:tax,:taxAmount,:realAmount,:remarks,
            :inAmount,:accountDate,:customerId,:invoiceCustomerId,:departmentId,:itemId,:userId,
            :invoiceTypeId,:taxId,:reserveBillId,:receiveBillId

        )
    """
    )
    fun insertSellReserve(
        @Param("billNo") billNo: String? = null,
        @Param("customerCode") customerCode: String? = null,
        @Param("customerName") customerName: String? = null,
        @Param("date") date: String? = null,
        @Param("departmentCode") departmentCode: String? = null,
        @Param("departmentName") departmentName: String? = null,
        @Param("userCode") userCode: String? = null,
        @Param("userName") userName: String? = null,
        @Param("isInvoice") isInvoice: String? = null,
        @Param("invoiceType") invoiceType: String? = null,
        @Param("invoiceNo") invoiceNo: String? = null,
        @Param("invoiceDate") invoiceDate: String? = null,
        @Param("invoiceCustomer") invoiceCustomer: String? = null,
        @Param("contractNo") contractNo: String? = null,
        @Param("receiveDate") receiveDate: String? = null,
        @Param("itemCode") itemCode: String? = null,
        @Param("itemName") itemName: String? = null,
        @Param("topicApply") topicApply: String? = null,
        @Param("quantity") quantity: String? = null,
        @Param("amount") amount: String? = null,
        @Param("tax") tax: String? = null,
        @Param("taxAmount") taxAmount: String? = null,
        @Param("realAmount") realAmount: String? = null,
        @Param("remarks") remarks: String? = null,
        @Param("inAmount") inAmount: String? = null,
        @Param("accountDate") accountDate: String? = null,
        @Param("customerId") customerId: String? = null,
        @Param("invoiceCustomerId") invoiceCustomerId: String? = null,
        @Param("departmentId") departmentId: String? = null,
        @Param("itemId") itemId: String? = null,
        @Param("userId") userId: String? = null,
        @Param("invoiceTypeId") invoiceTypeId: String? = null,
        @Param("taxId") taxId: String? = null,
        @Param("reserveBillId") reserveBillId: String? = null,
        @Param("receiveBillId") receiveBillId: String? = null
    ) {
    }

    @Modifying
    @Query(
        """
            DELETE FROM IM_SELL_RESERVE
        """
    )
    fun deleteAllImSellReserve()
}
