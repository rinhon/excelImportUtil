package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImPosition
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

/**
 * @author Rinhon
 * @date 2025/6/17 09:34
 * @description:
 */
interface ImPositionRepository : CrudRepository<ImPosition, Long> {

    @Modifying
    @Query(
        """
        INSERT INTO IM_POSITION (
             F_CODE, F_NAME, F_REMARKS, F_MANAGER, 
            F_TYPE, F_ADDRESS, F_ID
        ) VALUES (
             :code, :name, :remarks, :manager, 
            :type, :address, :id
        )
    """
    )
    fun insertPosition(
        @Param("code") code: String?,
        @Param("name") name: String?,
        @Param("remarks") remarks: String?,
        @Param("manager") manager: String?,
        @Param("type") type: String?,
        @Param("address") address: String?,
        @Param("id") id: Long?
    ) {
    }

    @Modifying
    @Query(
        """
              DELETE FROM IM_POSITION
        """
    )
    fun deleteAllImPosition()
}