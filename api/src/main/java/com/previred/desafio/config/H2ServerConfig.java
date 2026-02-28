package com.previred.desafio.config;

import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.sql.SQLException;

// levanta un servidor tcp de h2 en el puerto 9092 para permitir conexiones externas (visor)
@Configuration
@Profile("dev")
@Slf4j
public class H2ServerConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2TcpServer() throws SQLException {
        log.info("levantando servidor h2 tcp en el puerto 9092");
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092", "-ifNotExists");
    }
}
