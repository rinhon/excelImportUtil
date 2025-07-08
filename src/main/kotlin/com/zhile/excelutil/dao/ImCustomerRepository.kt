package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImCustomer
import org.springframework.stereotype.Repository

@Repository
interface ImCustomerRepository {

    suspend fun deleteAllImCustomer()

    suspend fun batchInsertSellReserves(customers: List<ImCustomer>)
}
