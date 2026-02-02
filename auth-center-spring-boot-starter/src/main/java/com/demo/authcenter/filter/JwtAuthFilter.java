package com.demo.authcenter.filter;

import com.demo.authcenter.security.AuthCenterProperties;
import com.demo.authcenter.security.JwtUtil;
import com.demo.authcenter.security.TokenStore;
import com.demo.authcenter.spi.AuthUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {

    public static final String REQ_ATTR_AUTH_ERROR_CODE = "AUTH_CENTER_AUTH_ERROR_CODE";

    public static final int CODE_TOKEN_MISSING = 40101;
    public static final int CODE_TOKEN_EXPIRED = 40102;
    public static final int CODE_TOKEN_INVALID = 40103;
    public static final int CODE_TOKEN_BLACKLISTED = 40104;

    private final JwtUtil jwtUtil;
    private final TokenStore tokenStore;
    private final AuthUserService authUserService;
    private final AuthCenterProperties props;

    private final AntPathMatcher matcher = new AntPathMatcher();

    public JwtAuthFilter(JwtUtil jwtUtil,
                         TokenStore tokenStore,
                         AuthUserService authUserService,
                         AuthCenterProperties props) {
        this.jwtUtil = jwtUtil;
        this.tokenStore = tokenStore;
        this.authUserService = authUserService;
        this.props = props;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return props.getIgnorePaths().stream().anyMatch(pattern -> matcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        // 避免重复解析
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(req, res);
            return;
        }

        String token = jwtUtil.extractBearerToken(req.getHeader("Authorization"));
        if (token == null) {
            req.setAttribute(REQ_ATTR_AUTH_ERROR_CODE, CODE_TOKEN_MISSING);
            chain.doFilter(req, res);
            return;
        }

        try {
            // 1) 校验签名/exp/iss
            Claims claims = jwtUtil.parseAndValidate(token);

            // 2) 校验 aud
            jwtUtil.validateAudience(claims);

            // 3) 校验 typ=access
            jwtUtil.validateAccessType(claims);

            // 4) ⭐️ 黑名单校验：jti 是否被拉黑（登出/踢下线）
            String jti = jwtUtil.getJti(claims);
            if (jti == null || jti.isBlank()) {
                throw new IllegalArgumentException("Missing jti");
            }
            if (tokenStore.isBlacklisted(jti)) {
                SecurityContextHolder.clearContext();
                req.setAttribute(REQ_ATTR_AUTH_ERROR_CODE, CODE_TOKEN_BLACKLISTED);
                chain.doFilter(req, res);
                return;
            }

            // 5) sub=userId（强制规范）
            Long userId = jwtUtil.getUserId(claims);
            if (userId == null) {
                throw new IllegalArgumentException("Missing or invalid sub(userId)");
            }

            var user = authUserService.loadByUserId(userId);

            var authorities = user.authorities().stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            var authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (ExpiredJwtException e) {
            SecurityContextHolder.clearContext();
            req.setAttribute(REQ_ATTR_AUTH_ERROR_CODE, CODE_TOKEN_EXPIRED);

        } catch (JwtException | IllegalArgumentException e) {
            SecurityContextHolder.clearContext();
            req.setAttribute(REQ_ATTR_AUTH_ERROR_CODE, CODE_TOKEN_INVALID);

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            req.setAttribute(REQ_ATTR_AUTH_ERROR_CODE, CODE_TOKEN_INVALID);
        }

        chain.doFilter(req, res);
    }
}
