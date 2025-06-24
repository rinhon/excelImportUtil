package com.zhile.excelutil.dao

import com.zhile.excelutil.entity.ImUser
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ImUserRepository : CrudRepository<ImUser, Long> {

    @Modifying
    @Query(
        """
        INSERT INTO IM_USER (
            F_CODE, F_NAME, F_PHONE, F_DEPARTMENT_CODE, 
            F_DEPARTMENT_NAME, F_SEX, F_REMARKS,F_LOGIN,F_ATTRIBUTE,F_PERMIT, F_ID, F_DEPARTMENT_ID
        ) VALUES  (
            :code, :name, :phone, :departmentCode, 
            :departmentName, :sex, :remarks,:login,:attribute,:permit, :id, :departmentId
        )
    """
    )
    fun insertUser(
        @Param("code") code: String?,
        @Param("name") name: String?,
        @Param("phone") phone: String?,
        @Param("departmentCode") departmentCode: String?,
        @Param("departmentName") departmentName: String?,
        @Param("sex") sex: String?,
        @Param("remarks") remarks: String?,
        @Param("login") login: String?,
        @Param("attribute") attribute: String?,
        @Param("permit") permit: String?,
        @Param("id") id: Long?,
        @Param("departmentId") departmentId: Long?
    ) {
    }

}