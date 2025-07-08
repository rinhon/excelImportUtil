package com.zhile.excelutil.dao.impl

import com.zhile.excelutil.dao.ImPositionRepository
import com.zhile.excelutil.entity.ImPosition
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement

@Repository
class ImPositionRepositoryImpl(private val jdbcTemplate: JdbcTemplate) : ImPositionRepository {
    override fun deleteAllImPosition() {
        jdbcTemplate.update("DELETE FROM IM_POSITION")
    }

    override fun batchInsertPositions(positions: List<ImPosition>) {
        if (positions.isEmpty()) {
            return
        }

        val sql = """
            INSERT INTO IM_POSITION
            ( F_CODE,F_NAME,F_REMARKS,F_MANAGER,F_TYPE,F_ADDRESS) 
            VALUES (?,?,?,?,?,?)
        """.trimIndent()

        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                val position = positions[i]
                ps.setString(1, position.code)
                ps.setString(2, position.name)
                ps.setString(3, position.remarks)
                ps.setString(4, position.manager)
                ps.setString(5, position.type)
                ps.setString(6, position.address)
            }

            override fun getBatchSize(): Int {
                return positions.size
            }

        })
    }
}
