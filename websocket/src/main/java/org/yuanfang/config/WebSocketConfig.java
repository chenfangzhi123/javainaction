package org.yuanfang.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableAutoConfiguration
@EnableWebSocket
public class WebSocketConfig extends SpringBootServletInitializer implements WebSocketConfigurer {

    // @Override
    // public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    //     registry.addHandler(echoWebSocketHandler(), "/echo").withSockJS();
    //     registry.addHandler(snakeWebSocketHandler(), "/snake").withSockJS();
    // }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(WebSocketConfig.class);
    }


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(marcoHandler(), "/socket");
    }

    @Bean
    public SocketHandler marcoHandler() {
        return new SocketHandler();
    }

    public static void main(String[] args) {
        SpringApplication.run(WebSocketConfig.class);

    }
}
