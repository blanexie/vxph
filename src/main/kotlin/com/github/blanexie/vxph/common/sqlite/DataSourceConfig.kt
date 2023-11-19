package com.github.blanexie.vxph.common.sqlite

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@Configuration
class DataSourceConfig(
    @Value("\${vxph.data.dir}")
    private val dataDir:String,

) {

    /**
     * spring:
     *   jpa:
     *     database-platform: org.hibernate.community.dialect.SQLiteDialect
     *     show-sql: true
     *     hibernate:
     *       ddl-auto: update
     *   datasource:
     *     url: jdbc:sqlite:db/vxph.sqlite
     *     driver-class-name: org.sqlite.JDBC
     *     username:
     *     password:
     */
    @Bean
    fun dataSource(): DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName("org.sqlite.JDBC")
        dataSource.url = "jdbc:sqlite:${dataDir}/vxph.sqlite"
        dataSource.username = ""
        dataSource.password = ""
        return dataSource
    }

    @Bean
    fun jdbcTemplate(dataSource: DataSource): JdbcTemplate {
        return JdbcTemplate(dataSource)
    }
}