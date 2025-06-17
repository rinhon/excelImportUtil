package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImCustomer
import org.springframework.data.repository.CrudRepository

/**
 * @author Rinhon
 * @date 2025/6/17 09:31
 * @description:
 */
interface ImCustomerRepository : CrudRepository<ImCustomer, Long> {
}