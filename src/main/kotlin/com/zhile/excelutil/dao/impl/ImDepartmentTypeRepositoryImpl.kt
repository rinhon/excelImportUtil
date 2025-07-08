package com.zhile.excelutil.dao.impl

import com.zhile.excelutil.dao.ImDepartmentTypeRepository
import com.zhile.excelutil.entity.ImDepartmentType
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement

@Repository
class ImDepartmentTypeRepositoryImpl(private val jdbcTemplate: JdbcTemplate) : ImDepartmentTypeRepository {
    override fun deleteAllDepartmentType() {
        jdbcTemplate.update("DELETE FROM IM_DEPARTMENT_TYPE")
    }

    override fun batchInsertDepartmentTypes(departmentTypes: List<ImDepartmentType>) {
        if (departmentTypes.isEmpty()) {
            return
        }
        val sql = """
            INSERT INTO IM_DEPARTMENT_TYPE
            (F_SEQ, F_NAME, F_REMARKS, F_CODE)
            VALUES (?,?,?,?)
        """.trimIndent()

        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                val departmentType = departmentTypes[i]
                ps.setString(1, departmentType.seq)
                ps.setString(2, departmentType.name)
                ps.setString(3, departmentType.remarks)
                ps.setString(4, departmentType.code)
            }

            override fun getBatchSize(): Int {
                return departmentTypes.size
            }

        })
    }
}
