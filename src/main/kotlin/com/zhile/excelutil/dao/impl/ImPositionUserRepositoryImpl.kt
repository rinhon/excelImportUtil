package com.zhile.excelutil.dao.impl

import com.zhile.excelutil.dao.ImPositionUserRepository
import com.zhile.excelutil.entity.ImPositionUser
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement

@Repository
class ImPositionUserRepositoryImpl(private val jdbcTemplate: JdbcTemplate) : ImPositionUserRepository {
    override fun deleteAllImPositionUser() {
        jdbcTemplate.update("DELETE FROM IM_POSITION_USER")
    }

    override fun batchInsertPositionUsers(positionUsers: List<ImPositionUser>) {
        if (positionUsers.isEmpty()) {
            return
        }

        val sql = """
            INSERT INTO IM_POSITION_USER
            (F_USER_CODE, F_USER_NAME, F_POSITION_CODE, F_POSITION_NAME) 
            VALUES (?,?,?,?)
        """.trimIndent()

        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                val positionUser = positionUsers[i]
                ps.setString(1, positionUser.userCode)
                ps.setString(2, positionUser.userName)
                ps.setString(3, positionUser.positionCode)
                ps.setString(4, positionUser.positionName)
            }

            override fun getBatchSize(): Int {
                return positionUsers.size
            }

        })
    }
}
