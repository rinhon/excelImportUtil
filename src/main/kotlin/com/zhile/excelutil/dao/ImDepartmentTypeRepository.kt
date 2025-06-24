package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImDepartmentType
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * @author Rinhon
 * @date 2025/6/20 11:21
 * @description:
 */
@Repository
interface ImDepartmentTypeRepository : CrudRepository<ImDepartmentType, Long> {
    // 自定义插入方法（如果需要处理特殊逻辑）
    @Modifying
    @Query(
        """
        INSERT INTO IM_DEPARTMENT_TYPE (
            F_NAME, F_REMARKS, F_ID, F_CODE
        ) VALUES (
            :name, :remarks, :id, :code
        )
        """
    )
    fun insertDepartmentType(
        @Param("name") name: String?,
        @Param("remarks") remarks: String?,
        @Param("id") id: Long?,
        @Param("code") code: String?
    )
}