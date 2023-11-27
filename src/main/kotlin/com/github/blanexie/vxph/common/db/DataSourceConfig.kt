package com.github.blanexie.vxph.common.db

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@Configuration
class DataSourceConfig(
    @Value("\${vxph.data.dir}")
    private val dataDir: String,
) {

    @Bean
    fun dataSource(): DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName("org.h2.Driver")
        dataSource.url = "jdbc:h2:file:${dataDir}/vxph;AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE"
        dataSource.username = "admin"
        dataSource.password = "123456"
        return dataSource
    }

    @Bean
    fun jdbcTemplate(dataSource: DataSource): JdbcTemplate {
        return JdbcTemplate(dataSource)
    }
}