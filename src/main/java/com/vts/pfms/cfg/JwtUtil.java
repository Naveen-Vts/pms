package com.vts.pfms.cfg;

import java.security.Key;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {

    private static final String SECRET = "javainuserrewrereerreerererrqesddfsferttrtrtrtrtrtrtrtrtrtrrttrtrrt";

	private static int jwtExpirationInMs=700000000;
	//private int refreshExpirationDateInMs;
	
    private static Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    public static Claims validateToken(String token) {
      
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(getSignInKey()).parseClaimsJws(token);
			  return claims.getBody();
		} catch (Exception ex) {
			throw ex;
		}
    }

    
    
    public static String generateToken(String userName) {
		Map<String, Object> claims = new HashMap<>();
			claims.put("isAdmin", true);
		return doGenerateToken(claims, userName);
	}
    
    private static String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(SignatureAlgorithm.HS256, getSignInKey()).compact();

    }
}