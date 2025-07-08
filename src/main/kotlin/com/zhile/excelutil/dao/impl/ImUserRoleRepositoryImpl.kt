package com.zhile.excelutil.dao.impl

import com.zhile.excelutil.dao.ImUserRoleRepository
import com.zhile.excelutil.entity.ImUserRole
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement

@Repository
class ImUserRoleRepositoryImpl(private val jdbcTemplate: JdbcTemplate) : ImUserRoleRepository {
    override fun deleteAllImUserRole() {
        jdbcTemplate.update("DELETE FROM IM_USER_ROLE")
    }

    override fun batchInsertUserRole(userRoles: List<ImUserRole>) {
        if (userRoles.isEmpty()) {
            return
        }

        val sql = """
       INSERT INTO IM_USER_ROLE (
            F_USER_CODE, F_USER_NAME, F_ROLE_CODE, F_ROLE_NAME, F_USER_ID, F_ROLE_ID
        ) VALUES  (?,?,?,?,?,?)
        """.trimIndent()

        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                val userRole = userRoles[i]

                ps.setString(1, userRole.userCode)
                ps.setString(2, userRole.userName)
                ps.setString(3, userRole.roleCode)
                ps.setString(4, userRole.roleName)
                ps.setString(5, userRole.userId?.toString())
                ps.setString(6, userRole.roleId?.toString())
            }

            override fun getBatchSize(): Int {
                return userRoles.size
            }

        })
    }
}
