package com.zhile.excelutil.service

import org.springframework.jdbc.core.SqlOutParameter
import org.springframework.jdbc.core.SqlParameter
import org.springframework.jdbc.core.simple.SimpleJdbcCall
import org.springframework.stereotype.Service
import java.sql.Types
import javax.sql.DataSource

/**
 * @author Rinhon
 * @date 2025/6/23 15:19
 * @description:
 */
@Service
class OraclePackageService(private val dataSource: DataSource) {
    private fun createJdbcCall(functionName: String): SimpleJdbcCall {
        return SimpleJdbcCall(dataSource)
            .withCatalogName("IMPORT_INIT_DATA") // 包名
            .withFunctionName(functionName) // 函数名
            .withoutProcedureColumnMetaDataAccess() // 禁用元数据访问
            .declareParameters(
                SqlOutParameter("RESULT", Types.NUMERIC) // 所有函数都返回 NUMBER
            )
    }

    // 无参数函数调用模板
    private fun callFunctionWithoutParams(functionName: String): Int {
        val jdbcCall = createJdbcCall(functionName)
        val result: Int = jdbcCall.executeFunction(Int::class.java) ?: 1
        return result
    }

    // 带organ_id参数的函数调用模板
    private fun callFunctionWithOrganId(functionName: String, organId: Long): Int {
        val jdbcCall = createJdbcCall(functionName).apply {
            declareParameters(
                SqlParameter("P_ORGAN_ID", Types.NUMERIC)
            )
        }
        val result: Int = jdbcCall.executeFunction(
            Int::class.java,
            mapOf("P_ORGAN_ID" to organId)
        ) ?: 1
        return result
    }

    // 1.1 部门 类型检查
    fun checkDepartmentTypeExcel(): Int =
        callFunctionWithoutParams("CHECK_DEPARTMENT_TYPE_EXCEL")

    // 1.1 部门 类型导入
    fun importDepartmentType(): Int =
        callFunctionWithoutParams("IMPORT_DEPARTMENT_TYPE")

    // 1.2 部门检查
    fun checkDepartmentExcel(): Int =
        callFunctionWithoutParams("CHECK_DEPARTMENT_EXCEL")

    // 1.2 部门导入
    fun importDepartment(organId: Long): Int =
        callFunctionWithOrganId("IMPORT_DEPARTMENT", organId)

    // 2. 职员检查
    fun checkUserExcel(): Int =
        callFunctionWithoutParams("CHECK_USER_EXCEL")

    // 2. 职员导入
    fun importUser(organId: Long): Int =
        callFunctionWithOrganId("IMPORT_USER", organId)

    // 3.1 角色检查
    fun checkRoleExcel(): Int =
        callFunctionWithoutParams("CHECK_ROLE_EXCEL")

    // 3.1 角色导入
    fun importRole(organId: Long): Int =
        callFunctionWithOrganId("IMPORT_ROLE", organId)

    // 3.2 职员角色检查
    fun checkUserRoleExcel(): Int =
        callFunctionWithoutParams("CHECK_USER_ROLE_EXCEL")

    // 3.2 职员角色导入
    fun importUserRole(organId: Long): Int =
        callFunctionWithOrganId("IMPORT_USER_ROLE", organId)

    // 4.1 货位检查
    fun checkPositionExcel(): Int =
        callFunctionWithoutParams("CHECK_POSITION_EXCEL")

    // 4.1 货位导入
    fun importPosition(organId: Long): Int =
        callFunctionWithOrganId("IMPORT_POSITION", organId)

    // 4.2 职员货位检查
    fun checkPositionUserExcel(): Int =
        callFunctionWithoutParams("CHECK_POSITION_USER_EXCEL")

    // 4.2 职员货位导入
    fun importPositionUser(organId: Long): Int =
        callFunctionWithOrganId("IMPORT_POSITION_USER", organId)

    // 5. 往来单位检查
    fun checkCustomerExcel(): Int =
        callFunctionWithoutParams("CHECK_CUSTOMER_EXCEL")

    // 5. 往来单位导入
    fun importCustomer(organId: Long): Int =
        callFunctionWithOrganId("IMPORT_CUSTOMER", organId)

    // 6. 物品检查
    fun checkItemExcel(): Int =
        callFunctionWithoutParams("CHECK_ITEM_EXCEL")

    // 6. 物品导入
    fun importItem(organId: Long): Int =
        callFunctionWithOrganId("IMPORT_ITEM", organId)

    // 7.1 财务分类科目检查
    fun checkNatureAccountExcel(): Int =
        callFunctionWithoutParams("CHECK_NATURE_ACCOUNT_EXCEL")

    // 7.1 财务分类科目导入
    fun importNatureAccount(organId: Long): Int =
        callFunctionWithOrganId("IMPORT_NATURE_ACCOUNT", organId)

    // 7.2 费用项目科目检查
    fun checkCostAccountExcel(): Int =
        callFunctionWithoutParams("CHECK_COST_ACCOUNT_EXCEL")

    // 7.2 费用项目科目导入
    fun importCostAccount(organId: Long): Int =
        callFunctionWithOrganId("IMPORT_COST_ACCOUNT", organId)

    // 8. 库存期初检查
    fun checkStockInitExcel(): Int =
        callFunctionWithoutParams("CHECK_STOCK_INIT_EXCEL")

    // 8. 库存期初导入
    fun importStockInit(organId: Long): Int =
        callFunctionWithOrganId("IMPORT_STOCK_INIT", organId)

    // 9. 销售在途检查
    fun checkSellBillExcel(): Int =
        callFunctionWithoutParams("CHECK_SELL_BILL_EXCEL")

    // 9. 销售在途导入
    fun importSellBill(organId: Long): Int =
        callFunctionWithOrganId("IMPORT_SELL_BILL", organId)
}