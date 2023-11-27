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


}