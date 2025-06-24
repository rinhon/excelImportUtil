package com.zhile.excelutil.controller

import com.zhile.excelutil.dao.ImDepartmentRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


/**
 * @author Rinhon
 * @date 2025/6/17 09:09
 * @description:  测试
 */
@RestController
@RequestMapping(name = "test", value = ["/test"])
class TestController(
    private val imDepartmentRepository: ImDepartmentRepository,

    ) {


    @GetMapping(value = ["/test"])
    fun test(): String {

        try {
//            val findAll = imDepartmentRepository.findAll()
//            println(findAll)
//            val save = imDepartmentRepository.save(
//                ImDepartment(
//                    code = "1",
//                    name = "版权和数字出版中心",
//                    fullName = "版权和数字出版中心",
//                    parentCode = "",
//                    parentName = "",
//                    departmentType = "编辑部门，数字出版部门",
//                    remarks = "1234"
//                )
//            )
//            println("-------------------------")
//            println(save)


        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "test"
    }

}