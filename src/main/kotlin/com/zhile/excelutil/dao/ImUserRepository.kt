package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImUser
import org.springframework.data.repository.CrudRepository

interface ImUserRepository : CrudRepository<ImUser, Long> {
}