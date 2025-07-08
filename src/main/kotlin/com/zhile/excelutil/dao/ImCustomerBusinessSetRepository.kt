package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImCustomerBusinessSet


interface ImCustomerBusinessSetRepository {

    suspend fun batchInsertCustomerBusinessSets(customerBusinessSets: List<ImCustomerBusinessSet>)

    suspend fun deleteAllImCustomerBusinessSet()

}
