package com.zhile.excelutil.service

import com.zhile.excelutil.dao.ImDepartmentRMSRepository
import com.zhile.excelutil.entity.ImDepartmentRMS
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TestService(private val imDepartmentRmsRepository: ImDepartmentRMSRepository) {
    @Transactional
    fun add() {
        val save = imDepartmentRmsRepository.save(
            ImDepartmentRMS(
                code = "1",
                name = "版权和数字出版中心",
                fullName = "版权和数字出版中心",
                parentCode = "",
                parentName = "",
                departmentType = "编辑部门，数字出版部门",
                remarks = "1234"
            )
        )
        println(save)
        val count = imDepartmentRmsRepository.count()
        println(count)
    }

}