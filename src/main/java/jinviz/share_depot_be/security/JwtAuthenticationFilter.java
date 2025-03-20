package jinviz.share_depot_be.security;

import jinviz.share_depot_be.exception.CustomException;
import jinviz.share_depot_be.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = jwtTokenProvider.resolveToken(request);

            // 토큰이 유효한 경우 인증 정보 설정
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.debug("Set Authentication to security context for '{}', uri: {}", auth.getName(), request.getRequestURI());
            } else if (StringUtils.hasText(token)) {
                log.debug("Invalid JWT token.");
                throw new CustomException(ErrorCode.JWT_TOKEN_INVALID);
            }
        } catch (CustomException ex) {
            log.error("Could not set user authentication in security context: {}", ex.getMessage());
            // 예외 처리는 SecurityConfig에서 처리
            // 이 필터에서는 예외를 잡아서 처리하지 않고 필터 체인에 계속 전달
            // ExceptionTranslationFilter에서 처리됨
        }

        filterChain.doFilter(request, response);
    }
}