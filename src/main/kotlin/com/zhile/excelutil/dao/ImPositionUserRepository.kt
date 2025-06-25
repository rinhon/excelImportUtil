package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImPositionUser
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * @author Rinhon
 * @date 2025/6/20 14:40
 * @description:
 */
@Repository
interface ImPositionUserRepository : CrudRepository<ImPositionUser, Long> {
    @Modifying
    @Query(
        """
        INSERT INTO IM_POSITION_USER (
            F_USER_CODE, F_USER_NAME, F_POSITION_CODE, F_POSITION_NAME, F_USER_ID, F_POSITION_ID
        ) VALUES (
            :userCode, :userName, :positionCode, :positionName, :userId, :positionId
        )
        """
    )
    fun insertRole(
        @Param("userCode") userCode: String?,
        @Param("userName") userName: String?,
        @Param("positionCode") positionCode: String?,
        @Param("positionName") positionName: String?,
        @Param("userId") userId: Long?,
        @Param("positionId") positionId: Long?
    )

    @Modifying
    @Query(
        """
              DELETE FROM IM_POSITION_USER
        """
    )
    fun deleteAllImPositionUser()

}