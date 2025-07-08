package com.zhile.excelutil.dao.impl

import com.zhile.excelutil.dao.ImStockInitRepository
import com.zhile.excelutil.entity.ImStockInit
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement

@Repository
class ImStockInitRepositoryImpl(private val jdbcTemplate: JdbcTemplate) : ImStockInitRepository {
    override fun deleteAllImStockInit() {
        jdbcTemplate.update("DELETE FROM IM_STOCK_INIT")
    }

    override fun batchInsertStockInits(stockInits: List<ImStockInit>) {
        if (stockInits.isEmpty()) {
            return
        }

        val sql = """
        INSERT INTO IM_STOCK_INIT (
            F_ITEM_CODE,
            F_ITEM_NAME,
            F_ISBN,
            F_SPEC,
            F_POSITION_CODE,
            F_POSITION_NAME,
            F_FRIST_IN_DATE,
            F_PRODUCE_NUM,
            F_QUANTITY,
            F_COST_PRICE,
            F_AMOUNT

        ) VALUES  (?,?,?,?,?,?,?,?,?,?,?)
        """.trimIndent()

        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                val stockInit = stockInits[i]

                ps.setString(1, stockInit.itemCode)
                ps.setString(2, stockInit.itemName)
                ps.setString(3, stockInit.isbn)
                ps.setString(4, stockInit.spec)
                ps.setString(5, stockInit.positionCode)
                ps.setString(6, stockInit.positionName)
                ps.setString(7, stockInit.fristInDate)
                ps.setString(8, stockInit.produceNum)
                ps.setString(9, stockInit.quantity)
                ps.setString(10, stockInit.costPrice)
                ps.setString(11, stockInit.amount)
            }

            override fun getBatchSize(): Int {
                return stockInits.size
            }

        })

    }
}
