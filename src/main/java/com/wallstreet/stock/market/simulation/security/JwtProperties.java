// package com.wallstreet.stock.market.simulation.security;

// import org.springframework.boot.context.properties.ConfigurationProperties;
// import org.springframework.stereotype.Component;

// /**
//  * Binds properties under 'security.jwt' from application.yml / properties.
//  */
// @Component
// @ConfigurationProperties(prefix = "security.jwt")
// public class JwtProperties {

//     /**
//      * Secret key used for signing JWTs. Should be long and random (256+ bits).
//      * Store it in environment variables or a secrets manager in production.
//      */
//     private String secret;

//     /** Access token lifetime in minutes (e.g. 10) */
//     private long accessTokenExpirationMinutes = 10;

//     /** Refresh token lifetime in days (e.g. 7) */
//     private long refreshTokenExpirationDays = 7;

//     public String getSecret() {
//         return secret;
//     }

//     public void setSecret(String secret) {
//         this.secret = secret;
//     }

//     public long getAccessTokenExpirationMinutes() {
//         return accessTokenExpirationMinutes;
//     }

//     public void setAccessTokenExpirationMinutes(long accessTokenExpirationMinutes) {
//         this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
//     }

//     public long getRefreshTokenExpirationDays() {
//         return refreshTokenExpirationDays;
//     }

//     public void setRefreshTokenExpirationDays(long refreshTokenExpirationDays) {
//         this.refreshTokenExpirationDays = refreshTokenExpirationDays;
//     }
// }
