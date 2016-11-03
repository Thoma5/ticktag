package io.ticktag.restinterface

import io.ticktag.ApplicationProperties
import io.ticktag.persistence.user.UserRepository
import io.ticktag.persistence.user.entity.Role
import io.ticktag.service.Principal
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.token.KeyBasedPersistenceTokenService
import org.springframework.security.core.token.TokenService
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.WebAuthenticationDetails
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.filter.OncePerRequestFilter
import java.security.SecureRandom
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
open class RestSecurityConfig @Inject constructor(
        @Named("restAuthFilter") private val restAuthFilter: Filter
) : WebSecurityConfigurerAdapter() {

    companion object {
        private val CORS_CONFIG = initCorsConfig()

        private fun initCorsConfig(): CorsConfiguration {
            val cc = CorsConfiguration()
            cc.addAllowedHeader(CorsConfiguration.ALL)
            cc.addAllowedMethod(CorsConfiguration.ALL)
            cc.addAllowedOrigin(CorsConfiguration.ALL)
            return cc
        }
    }

    override fun configure(http: HttpSecurity) {
        http
                .addFilterBefore(restAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
                .authorizeRequests()
                .antMatchers("/webjars/**", "/swagger/**", "/v2/api-docs").permitAll()
                .antMatchers("/auth/login").permitAll()
                .anyRequest().authenticated().and()
                .logout().disable()
                .formLogin().disable()
                .csrf().disable()
                .cors().configurationSource { CORS_CONFIG }.and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(object : AuthenticationProvider {
            override fun authenticate(authentication: Authentication): Authentication {
                throw RuntimeException("This code MUST NOT be reachable.")
            }

            override fun supports(authentication: Class<*>): Boolean {
                throw RuntimeException("This code MUST NOT be reachable.")
            }
        })
    }

}

@Configuration
open class RestSecurityConfigBeans {
    companion object {
        private val LOG = LoggerFactory.getLogger(RestSecurityConfigBeans::class.java)
    }

    @Bean("restAuthTokenService")
    open fun restAuthTokenService(props: ApplicationProperties): TokenService {
        val s = KeyBasedPersistenceTokenService()
        s.setSecureRandom(SecureRandom())
        s.setServerSecret(props.serverSecret)
        s.setServerInteger(Integer.valueOf(props.serverNumber))
        return s
    }

    @Bean("restAuthFilter")
    open fun restAuthFilter(@Named("restAuthTokenService") tokenService: TokenService, users: UserRepository): Filter {
        return object : OncePerRequestFilter() {
            override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
                // TODO invalidate tokens after some time!
                val tokenKey = request.getHeader("X-Authorization")
                if (tokenKey != null) {
                    try {
                        val token = tokenService.verifyToken(tokenKey)
                        if (token != null) {
                            val userId = UUID.fromString(token.extendedInformation)
                            val user = users.findById(userId)
                            if (user != null) {
                                val authorities = when (user.role) {
                                    Role.USER -> setOf("USER")
                                    Role.OBSERVER -> setOf("USER", "OBSERVER")
                                    Role.ADMIN -> setOf("USER", "OBSERVER", "ADMIN")
                                }
                                val principal = Principal(user.id, authorities)
                                val auth = PreAuthenticatedAuthenticationToken(principal, null, principal.authorities.map(::SimpleGrantedAuthority))
                                auth.details = WebAuthenticationDetails(request)
                                SecurityContextHolder.getContext().authentication = auth
                            }
                        }
                    } catch (ex: Exception) {
                        LOG.info("Got an illegal token (exception was $ex)")
                    }
                }

                filterChain.doFilter(request, response)
            }
        }
    }
}
