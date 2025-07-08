package com.zhile.excelutil.dao.impl

import com.zhile.excelutil.dao.ImDepartmentRepository
import com.zhile.excelutil.entity.ImDepartment
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement

@Repository
class ImDepartmentRepositoryImpl(private val jdbcTemplate: JdbcTemplate) : ImDepartmentRepository {
    override fun deleteAllImDepartment() {
        jdbcTemplate.update("DELETE FROM IM_DEPARTMENT")
    }

    override fun batchInsertDepartments(departments: List<ImDepartment>) {
        if (departments.isEmpty()) {
            return
        }
        val sql = """
            INSERT INTO IM_DEPARTMENT 
            (F_CODE, F_NAME, F_FULL_NAME, F_PARENT_CODE, F_PARENT_NAME, F_DEPARTMENT_TYPE, F_REMARKS) VALUES (?,?,?,?,?,?,?)
        """.trimIndent()

        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                val department = departments[i]
                ps.setString(1, department.code)
                ps.setString(2, department.name)
                ps.setString(3, department.fullName)
                ps.setString(4, department.parentCode)
                ps.setString(5, department.parentName)
                ps.setString(6, department.departmentType)
                ps.setString(7, department.remarks)
            }

            override fun getBatchSize(): Int {
                return departments.size
            }
        })
    }
}
