package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImItem
import org.springframework.data.repository.CrudRepository

/**
 * @author zlhp
 * @date 2025/6/17 09:55
 * @description:
 */
interface ImItemRepository : CrudRepository<ImItem, Long> {
}