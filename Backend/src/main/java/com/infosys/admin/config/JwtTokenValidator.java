package com.infosys.admin.config;

import java.io.IOException;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtTokenValidator extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, 
			FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
			String jwt=request.getHeader(JwtConstant.JWT_HEADER);
			//Bearer token
			if(jwt!=null) {
				jwt=jwt.substring(7);
				try {
					SecretKey key=Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());
					Claims claim=Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
					
					String email=String.valueOf(claim.get("email"));
					String authorities=String.valueOf(claim.get("authorities"));
					
					List<GrantedAuthority> auth=AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
					Authentication authentication=new UsernamePasswordAuthenticationToken(email,null, auth);
					SecurityContextHolder.getContext().setAuthentication(authentication);
					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					throw new BadCredentialsException("Invalid token");
				}
			}
			
			filterChain.doFilter(request, response);
		
	}

}
