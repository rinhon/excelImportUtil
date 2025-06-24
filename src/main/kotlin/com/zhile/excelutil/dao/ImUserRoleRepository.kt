package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImUserRole
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository

import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * @author zlhp
 * @date 2025/6/20 14:19
 * @description:
 */
@Repository
interface ImUserRoleRepository : CrudRepository<ImUserRole, Long> {
    @Modifying
    @Query(
        """
        INSERT INTO IM_USER_ROLE (
            F_USER_CODE, F_USER_NAME, F_ROLE_CODE, F_ROLE_NAME, F_USER_ID, F_ROLE_ID
        ) VALUES  (
            :userCode, :userName, :roleCode, :roleName, :userId, :roleId
        )
    """
    )
    fun insertUser(
        @Param("userCode") userCode: String?,
        @Param("userName") userName: String?,
        @Param("roleCode") roleCode: String?,
        @Param("roleName") roleName: String?,
        @Param("userId") userId: Long?,
        @Param("roleId") roleId: Long?
    ) {
    }

}