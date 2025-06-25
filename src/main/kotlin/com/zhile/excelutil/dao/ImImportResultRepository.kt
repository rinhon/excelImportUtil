package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImImportResult
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * @author zlhp
 * @date 2025/6/23 11:42
 * @description:
 */
@Repository
interface ImImportResultRepository : CrudRepository<ImImportResult, Long> {
    @Modifying
    @Query(
        """
            DELETE FROM IM_IMPORT_RESULT
            WHERE F_CHECK_TYPE = :type
        """
    )
    fun deleteByType(@Param("type") type: String)

}