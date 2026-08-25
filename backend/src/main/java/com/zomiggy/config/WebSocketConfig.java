package com.zomiggy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import com.zomiggy.repository.UserRepository;
import com.zomiggy.security.JwtService;
import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final JwtService jwt;
    private final UserRepository users;
    private final String configuredOrigins;

    public WebSocketConfig(JwtService jwt, UserRepository users, @Value("${app.cors.origin}") String configuredOrigins) {
        this.jwt = jwt;
        this.users = users;
        this.configuredOrigins = configuredOrigins;
    }
    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns(java.util.Arrays.stream(configuredOrigins.split(","))
            .map(String::trim).filter(origin -> !origin.isEmpty()).toArray(String[]::new));
    }

    @Override
    public void configureClientInboundChannel(@NonNull org.springframework.messaging.simp.config.ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            @Nullable
            public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
                StompHeaderAccessor headers = StompHeaderAccessor.wrap(message);
                if (StompCommand.CONNECT.equals(headers.getCommand())) {
                    String authorization = headers.getFirstNativeHeader("Authorization");
                    if (authorization != null && authorization.startsWith("Bearer ")) {
                        users.findByMobile(jwt.subject(authorization.substring(7))).ifPresent(user -> headers.setUser(new UsernamePasswordAuthenticationToken(user.getMobile(), null, List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())))));
                    }
                }
                return message;
            }
        });
    }
}
