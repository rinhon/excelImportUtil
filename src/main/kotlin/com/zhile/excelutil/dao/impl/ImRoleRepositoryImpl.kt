package com.zhile.excelutil.dao.impl

import com.zhile.excelutil.dao.ImRoleRepository
import com.zhile.excelutil.entity.ImRole
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement

@Repository
class ImRoleRepositoryImpl(private val jdbcTemplate: JdbcTemplate) : ImRoleRepository {
    override fun deleteAllImRole() {
        jdbcTemplate.update("DELETE FROM IM_ROLE")
    }

    override fun batchInsertRoles(roles: List<ImRole>) {
        if (roles.isEmpty()) {
            return
        }

        val sql = """
            INSERT INTO IM_ROLE
            (F_CODE,F_NAME)
            VALUES(?,?)
        """.trimIndent()

        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                ps.setString(1, roles[i].code)
                ps.setString(2, roles[i].name)
            }

            override fun getBatchSize(): Int {
                return roles.size
            }

        })
    }
}
