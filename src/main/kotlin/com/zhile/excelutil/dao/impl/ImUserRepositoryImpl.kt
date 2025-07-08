package com.zhile.excelutil.dao.impl

import com.zhile.excelutil.dao.ImUserRepository
import com.zhile.excelutil.entity.ImUser
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement

@Repository
class ImUserRepositoryImpl(private val jdbcTemplate: JdbcTemplate) : ImUserRepository {
    override fun deleteAllImUser() {
        jdbcTemplate.update("DELETE FROM IM_USER")
    }

    override fun batchInsertUsers(users: List<ImUser>) {
        if (users.isEmpty()) {
            return
        }

        val sql = """
            INSERT INTO IM_USER
            ( F_NAME,  F_CODE,  F_PHONE,  F_DEPARTMENT_CODE,  F_DEPARTMENT_NAME,  F_SEX,  F_LOGIN,  F_REMARKS,  F_ATTRIBUTE,  F_QUERY_RIGHT)
            VALUES (?,?,?,?,?,?,?,?,?,?)
        """.trimIndent()
        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                val user = users[i]

                ps.setString(1, user.name)
                ps.setString(2, user.code)
                ps.setString(3, user.phone)
                ps.setString(4, user.departmentCode)
                ps.setString(5, user.departmentName)
                ps.setString(6, user.sex)
                ps.setString(7, user.login)
                ps.setString(8, user.remarks)
                ps.setString(9, user.attribute)
                ps.setString(10, user.queryRight)
            }

            override fun getBatchSize(): Int {
                return users.size
            }

        })
    }
}
