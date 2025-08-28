// package com.wallstreet.stock.market.simulation.controller;

// import com.wallstreet.stock.market.simulation.model.RefreshToken;
// import com.wallstreet.stock.market.simulation.model.User;
// import com.wallstreet.stock.market.simulation.repository.UserRepository;
// import com.wallstreet.stock.market.simulation.security.JwtProperties;
// import com.wallstreet.stock.market.simulation.security.JwtService;
// import com.wallstreet.stock.market.simulation.security.TokenPair;
// import com.wallstreet.stock.market.simulation.service.RefreshTokenService;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseCookie;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.BadCredentialsException;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.web.bind.annotation.*;

// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import java.time.Duration;
// import java.time.OffsetDateTime;
// import java.util.Optional;
// import jakarta.servlet.http.Cookie;


// /**
//  * AuthController — login / refresh / logout endpoints.
//  *
//  * - Access tokens are returned in response body (short-lived JWT).
//  * - Refresh tokens are HttpOnly Secure SameSite=Strict cookies (single-session + rotation).
//  */
// @RestController
// @RequestMapping("/api/auth")
// public class AuthController {

//     private final AuthenticationManager authenticationManager;
//     private final UserRepository userRepository;
//     private final JwtService jwtService;
//     private final RefreshTokenService refreshTokenService;
//     private final JwtProperties jwtProperties;

//     public AuthController(AuthenticationManager authenticationManager,
//                           UserRepository userRepository,
//                           JwtService jwtService,
//                           RefreshTokenService refreshTokenService,
//                           JwtProperties jwtProperties) {
//         this.authenticationManager = authenticationManager;
//         this.userRepository = userRepository;
//         this.jwtService = jwtService;
//         this.refreshTokenService = refreshTokenService;
//         this.jwtProperties = jwtProperties;
//     }

//     /**
//      * POST /api/auth/login
//      * Body: { "email": "...", "password": "..." }
//      * Response: TokenPair (access token + expiry) in JSON. Refresh token delivered as HttpOnly cookie.
//      */
//     @PostMapping("/login")
//     public ResponseEntity<?> login(@RequestBody AuthRequest req,
//                                    HttpServletRequest request,
//                                    HttpServletResponse response) {
//         try {

//             // PasswordEncoder encoder = new BCryptPasswordEncoder();
//             // String encodedPassword = encoder.encode(req.getPassword());
//             // System.out.println("Encoded: " + encodedPassword);


//             Authentication authentication = authenticationManager.authenticate(
//                     new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
//             );

            
//             // fetch user entity
//             Optional<User> maybeUser = userRepository.findByEmail(req.getEmail());
//             if (maybeUser.isEmpty()) {
//                 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("User not found"));
//             }
//             User user = maybeUser.get();

//             System.out.println(user);

//             // create access token
//             String accessToken = jwtService.generateAccessToken(user);
//             OffsetDateTime accessExpiry = OffsetDateTime.now().plusMinutes(jwtProperties.getAccessTokenExpirationMinutes());

//             // create refresh token (single-session; revokes previous tokens)
//             String ip = extractClientIp(request);
//             String ua = request.getHeader("User-Agent");
//             RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, ip, ua);

//             // set refresh token cookie
//             ResponseCookie cookie = buildRefreshTokenCookie(refreshToken.getToken(), jwtProperties.getRefreshTokenExpirationDays());
//             response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

//             TokenPair pair = new TokenPair(accessToken, accessExpiry, null, refreshToken.getExpiresAt());
//             return ResponseEntity.ok(pair);

//         } catch (BadCredentialsException ex) {
//             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(ex.getMessage()));
//         } catch (Exception ex) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(ex.getMessage()));
//         }
//     }

//     /**
//      * POST /api/auth/refresh
//      * Reads refresh token cookie, validates and rotates it, issues new access JWT and a new refresh cookie.
//      */
//     @PostMapping("/refresh")
//     public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
//         String currentRefreshToken = readRefreshTokenFromCookie(request);
//         if (currentRefreshToken == null) {
//             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Refresh token missing"));
//         }

//         Optional<RefreshToken> maybe = refreshTokenService.validateToken(currentRefreshToken);
//         if (maybe.isEmpty()) {
//             // invalid or expired
//             clearRefreshCookie(response);
//             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid or expired refresh token"));
//         }

//         RefreshToken oldToken = maybe.get();

//         // rotate -> returns new RefreshToken
//         String ip = extractClientIp(request);
//         String ua = request.getHeader("User-Agent");
//         RefreshToken newToken;
//         try {
//             newToken = refreshTokenService.rotate(oldToken, ip, ua);
//         } catch (Exception ex) {
//             // rotation failed -> revoke old token for safety
//             refreshTokenService.revokeToken(oldToken);
//             clearRefreshCookie(response);
//             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Failed to rotate refresh token"));
//         }

//         // issue new access token
//         User user = newToken.getUser();
//         String accessToken = jwtService.generateAccessToken(user);
//         OffsetDateTime accessExpiry = OffsetDateTime.now().plusMinutes(jwtProperties.getAccessTokenExpirationMinutes());

//         // set rotated refresh cookie
//         ResponseCookie cookie = buildRefreshTokenCookie(newToken.getToken(), jwtProperties.getRefreshTokenExpirationDays());
//         response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

//         TokenPair pair = new TokenPair(accessToken, accessExpiry, null, newToken.getExpiresAt());
//         return ResponseEntity.ok(pair);
//     }

//     /**
//      * POST /api/auth/logout
//      * Revoke refresh token (if present) and clear cookie.
//      */
//     @PostMapping("/logout")
//     public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
//         String currentRefreshToken = readRefreshTokenFromCookie(request);
//         if (currentRefreshToken != null) {
//             Optional<RefreshToken> maybe = refreshTokenService.findByToken(currentRefreshToken);
//             maybe.ifPresent(refreshTokenService::revokeToken);
//         }
//         clearRefreshCookie(response);
//         return ResponseEntity.ok(new SimpleMessage("Logged out"));
//     }

//     /* ----------------- Helpers & inner DTO classes ----------------- */

//     private ResponseCookie buildRefreshTokenCookie(String tokenValue, long expiryDays) {
//         long maxAgeSeconds = Duration.ofDays(expiryDays).getSeconds();
//         return ResponseCookie.from("refresh_token", tokenValue)
//                 .httpOnly(true)
//                 .secure(true)        // keep true in production (requires HTTPS)
//                 .path("/")
//                 .maxAge(maxAgeSeconds)
//                 .sameSite("Strict")
//                 .build();
//     }

//     private void clearRefreshCookie(HttpServletResponse response) {
//         ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
//                 .httpOnly(true)
//                 .secure(true)
//                 .path("/")
//                 .maxAge(0)
//                 .sameSite("Strict")
//                 .build();
//         response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
//     }

//     private String readRefreshTokenFromCookie(HttpServletRequest request) {
//         if (request.getCookies() == null) return null;
//         return java.util.Arrays.stream(request.getCookies())
//             .filter(c -> "refresh_token".equals(c.getName()))
//             .map(c -> c.getValue())
//             .findFirst()
//             .orElse(null);
//     }

//     private String extractClientIp(HttpServletRequest request) {
//         String forwarded = request.getHeader("X-Forwarded-For");
//         if (forwarded != null && !forwarded.isBlank()) {
//             return forwarded.split(",")[0].trim();
//         }
//         return request.getRemoteAddr();
//     }

//     // Request DTO for login
//     public static class AuthRequest {
//         private String email;
//         private String password;
//         public AuthRequest() {}
//         public AuthRequest(String email, String password) { this.email = email; this.password = password; }
//         public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }
//         public String getPassword() { return password; } public void setPassword(String password) { this.password = password; }
//     }

//     // Simple error response DTO
//     public static class ErrorResponse {
//         private String error;
//         public ErrorResponse() {}
//         public ErrorResponse(String error) { this.error = error; }
//         public String getError() { return error; } public void setError(String error) { this.error = error; }
//     }

//     // Simple message wrapper
//     public static class SimpleMessage {
//         private String message;
//         public SimpleMessage() {}
//         public SimpleMessage(String message) { this.message = message; }
//         public String getMessage() { return message; } public void setMessage(String message) { this.message = message; }
//     }
// }
