package com.manruhomerun.yadan.auth.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manruhomerun.yadan.auth.error.AuthErrorCode;
import com.manruhomerun.yadan.auth.error.exception.AuthException;
import com.manruhomerun.yadan.auth.token.JwtProvider;
import com.manruhomerun.yadan.global.dto.ErrorResponse;
import com.manruhomerun.yadan.global.error.BaseErrorCode;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
// Access Token을 검증하고 사용자 ID를 요청에 저장하는 필터
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String API_PREFIX = "/api/";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String USER_ID_ATTRIBUTE = "userId";

    // Access Token 없이 호출할 수 있는 인증 API
    private static final Set<String> PUBLIC_API_PATHS = Set.of(
            "/api/auth/login",
            "/api/auth/refresh"
    );

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    // 인증이 필요하지 않은 요청이면 필터 실행 생략
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = getRequestPath(request);

        return "OPTIONS".equalsIgnoreCase(request.getMethod())
                || !path.startsWith(API_PREFIX)
                || PUBLIC_API_PATHS.contains(path)
                || isSwaggerPath(path);
    }

    // 실제 JWT 인증 처리 -> AccessToken 검증, 검증된 userId를 이후 컨트롤러에 전달
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String accessToken = resolveAccessToken(request);
            String userId = jwtProvider.verifyAccessToken(accessToken);

            request.setAttribute(USER_ID_ATTRIBUTE, userId);
            filterChain.doFilter(request, response);
        } catch (AuthException exception) {
            writeErrorResponse(request, response, exception);
        }
    }

    // Authorization 헤더에서 Access Token 추출
    private String resolveAccessToken(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            throw new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN);
        }

        String accessToken = authorization.substring(BEARER_PREFIX.length()).trim();
        if (accessToken.isBlank()) {
            throw new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN);
        }

        return accessToken;
    }

    // 필터에서 발생한 인증 예외는 ControllerAdvice를 거치지 않으므로 직접 JSON 응답 작성
    private void writeErrorResponse(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthException exception
    ) throws IOException {
        BaseErrorCode errorCode = exception.getErrorCode();

        response.setStatus(errorCode.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(
                response.getWriter(),
                ErrorResponse.of(errorCode, exception.getMessage(), request.getRequestURI())
        );
    }

    // Context Path를 제외해 필터 경로 비교에 사용할 요청 경로 반환
    private String getRequestPath(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();

        return contextPath.isEmpty()
                ? requestUri
                : requestUri.substring(contextPath.length());
    }

    // Swagger UI와 OpenAPI 문서 요청은 인증 대상에서 제외
    private boolean isSwaggerPath(String path) {
        return path.equals("/api/swagger-ui.html")
                || path.startsWith("/api/swagger-ui/")
                || path.equals("/api/v3/api-docs")
                || path.startsWith("/api/v3/api-docs/");
    }
}
