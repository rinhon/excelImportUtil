package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImCustomerBusinessSet
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ImCustomerBusinessSetRepository : CrudRepository<ImCustomerBusinessSet, Long> {
    @Modifying
    @Query(
        """
        INSERT INTO IM_CUSTOMER_BUSINESS_SET (
            F_CUSTOMER_CODE, F_CUSTOMER_NAME, F_PURCHASE_USER_CODE, F_PURCHASE_USER_NAME, 
            F_PURCHASE_DEPARTMENT_CODE, F_PURCHASE_DEPARTMENT_NAME, F_SALE_USER_CODE, 
            F_SALE_USER_NAME, F_SALE_DEPARTMENT_CODE, F_SALE_DEPARTMENT_NAME, 
            F_REMARKS, F_CUSTOMER_ID, F_PURCHASE_USER_ID, F_PURCHASE_DEPARTMENT_ID, 
            F_SALE_USER_ID, F_SALE_DEPARTMENT_ID
        ) VALUES  (
            :customerCode, :customerName, :purchaseUserCode, :purchaseUserName, 
            :purchaseDepartmentCode, :purchaseDepartmentName, :saleUserCode, 
            :saleUserName, :saleDepartmentCode, :saleDepartmentName, 
            :remarks, :customerId, :purchaseUserId, :purchaseDepartmentId, 
            :saleUserId, :saleDepartmentId
        )
    """
    )
    fun insertCustomerInfo(
        @Param("customerCode") customerCode: String?,
        @Param("customerName") customerName: String?,
        @Param("purchaseUserCode") purchaseUserCode: String?,
        @Param("purchaseUserName") purchaseUserName: String?,
        @Param("purchaseDepartmentCode") purchaseDepartmentCode: String?,
        @Param("purchaseDepartmentName") purchaseDepartmentName: String?,
        @Param("saleUserCode") saleUserCode: String?,
        @Param("saleUserName") saleUserName: String?,
        @Param("saleDepartmentCode") saleDepartmentCode: String?,
        @Param("saleDepartmentName") saleDepartmentName: String?,
        @Param("remarks") remarks: String?,
        @Param("customerId") customerId: Long?,
        @Param("purchaseUserId") purchaseUserId: Long?,
        @Param("purchaseDepartmentId") purchaseDepartmentId: Long?,
        @Param("saleUserId") saleUserId: Long?,
        @Param("saleDepartmentId") saleDepartmentId: Long?
    )

    @Modifying
    @Query(
        """
            DELETE FROM IM_CUSTOMER_BUSINESS_SET
    """
    )
    fun deleteAllImCustomerBusinessSet()

}
