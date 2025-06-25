package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImRole
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * @author Rinhon
 * @date 2025/6/20 14:08
 * @description:
 */
@Repository
interface ImRoleRepository : CrudRepository<ImRole, Long> {
    // 自定义插入方法
    @Modifying
    @Query(
        """
        INSERT INTO IM_ROLE (
            F_CODE, F_NAME, F_ID
        ) VALUES (
            :code, :name, :id
        )
        """
    )
    fun insertRole(
        @Param("code") code: String?,
        @Param("name") name: String?,
        @Param("id") id: Long?
    )

    @Modifying
    @Query(
        """
              DELETE FROM IM_ROLE
        """
    )
    fun deleteAllImRole() {
    }
}