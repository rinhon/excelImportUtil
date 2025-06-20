package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImDepartment
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ImDepartmentRepository : CrudRepository<ImDepartment, Long> {

    @Modifying
    @Query(
        """
              INSERT INTO IM_DEPARTMENT (
                F_CODE, F_NAME, F_FULL_NAME, F_PARENT_CODE, 
                F_PARENT_NAME, F_DEPARTMENT_TYPE, F_REMARKS, F_PARENT_ID, F_TYPE_ID
            ) VALUES  (
                :code, :name, :fullName, :parentCode, 
                :parentName, :departmentType, :remarks, :parentId, :typeId
            )
        """
    )
    fun insertDepartment(
        @Param("code") code: String?,
        @Param("name") name: String?,
        @Param("fullName") fullName: String?,
        @Param("parentCode") parentCode: String?,
        @Param("parentName") parentName: String?,
        @Param("departmentType") departmentType: String?,
        @Param("remarks") remarks: String?,
        @Param("parentId") parentId: Long?,
        @Param("typeId") typeId: Long?
    ) {
    }
}