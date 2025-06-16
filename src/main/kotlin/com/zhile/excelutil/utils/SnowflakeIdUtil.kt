package com.zhile.excelutil.utils

import java.util.concurrent.locks.ReentrantLock
import kotlin.math.abs


/**
 * 10位数雪花算法ID生成器
 * 适用于数据库主键，保证唯一性和递增性
 */
class SnowflakeIdUtil(machineId: Long) {
    private val machineId: Long
    private var sequence = 0L
    private var lastTimestamp = -1L

    private val lock = ReentrantLock()

    init {
        require(!(machineId > MAX_MACHINE_ID || machineId < 0)) {
            String.format(
                "机器ID必须在0到%d之间",
                MAX_MACHINE_ID
            )
        }
        this.machineId = machineId
    }

    /**
     * 生成10位数的唯一ID
     * @return 10位数长整型ID
     */
    fun nextId(): Long {
        lock.lock()
        try {
            var timestamp = customTimestamp

            if (timestamp < lastTimestamp) {
                throw RuntimeException("时钟回拨异常")
            }

            if (timestamp == lastTimestamp) {
                sequence = (sequence + 1) and MAX_SEQUENCE
                if (sequence == 0L) {
                    // 序列号用完，等待下一个时间单位
                    timestamp = waitNextTimestamp(lastTimestamp)
                }
            } else {
                sequence = 0L
            }

            lastTimestamp = timestamp


            // 组装ID
            val id = ((timestamp shl TIMESTAMP_SHIFT.toInt())
                    or (machineId shl MACHINE_ID_SHIFT.toInt())
                    or sequence)


            // 确保是10位数
            return (abs(id.toDouble()) % 10000000000L).toLong()
        } finally {
            lock.unlock()
        }
    }

    private val customTimestamp: Long
        /**
         * 获取自定义时间戳（秒级，相对于起始时间）
         */
        get() = (System.currentTimeMillis() - START_TIMESTAMP) / 1000

    /**
     * 等待下一个时间戳
     */
    private fun waitNextTimestamp(lastTimestamp: Long): Long {
        var timestamp = customTimestamp
        while (timestamp <= lastTimestamp) {
            timestamp = customTimestamp
        }
        return timestamp
    }

    /**
     * 解析ID信息（用于调试）
     */
    fun parseId(id: Long): IdInfo {
        val sequence = id and MAX_SEQUENCE
        val machineId = (id shr MACHINE_ID_SHIFT.toInt()) and MAX_MACHINE_ID
        val timestamp = id shr TIMESTAMP_SHIFT.toInt()

        return IdInfo(timestamp + START_TIMESTAMP / 1000, machineId, sequence)
    }

    /**
     * ID信息类
     */
    class IdInfo(// Getters
        val timestamp: Long, val machineId: Long, val sequence: Long
    ) {
        override fun toString(): String {
            return String.format(
                "IdInfo{timestamp=%d, machineId=%d, sequence=%d}",
                timestamp, machineId, sequence
            )
        }
    }

    companion object {
        // 起始时间戳 (2024-01-01 00:00:00)
        private const val START_TIMESTAMP = 1704067200000L

        // 各部分位数
        private const val MACHINE_ID_BITS = 4L // 机器ID位数
        private const val SEQUENCE_BITS = 8L // 序列号位数

        // 最大值
        private const val MAX_MACHINE_ID = (-1L shl MACHINE_ID_BITS.toInt()).inv()
        private const val MAX_SEQUENCE = (-1L shl SEQUENCE_BITS.toInt()).inv()

        // 位移
        private const val MACHINE_ID_SHIFT = SEQUENCE_BITS
        private const val TIMESTAMP_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS

        // 测试方法
        @JvmStatic
        fun main(args: Array<String>) {
            val generator = SnowflakeIdUtil(1)

            println("生成10个10位数ID:")
            for (i in 0..9) {
                val id = generator.nextId()
                System.out.printf(
                    "ID: %010d, 解析: %s%n",
                    id, generator.parseId(id)
                )


                // 短暂延迟以观察时间戳变化
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                    break
                }
            }


            // 性能测试
            println("\n性能测试 - 生成100万个ID:")
            val startTime = System.currentTimeMillis()
            for (i in 0..999999) {
                generator.nextId()
            }
            val endTime = System.currentTimeMillis()
            System.out.printf(
                "耗时: %d毫秒, 平均每秒生成: %.0f个ID%n",
                endTime - startTime,
                1000000.0 / (endTime - startTime) * 1000
            )
        }
    }
}