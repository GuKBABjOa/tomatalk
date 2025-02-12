package team.overfullow.tolonbgeub.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import team.overfullow.tolonbgeub.ApiTestSupport;
import team.overfullow.tolonbgeub.auth.jwt.JwtProvider;
import team.overfullow.tolonbgeub.auth.jwt.JwtType;
import team.overfullow.tolonbgeub.auth.jwt.SignedJwt;
import team.overfullow.tolonbgeub.user.dto.UserRequest;
import team.overfullow.tolonbgeub.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthApiTest extends ApiTestSupport {

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    AuthApiClient authApiClient;

    @Autowired
    UserService userService;

    @Nested
    @DisplayName("로그아웃 API")
    class Logout {
        @Test
        @DisplayName("요청이 성공하면 엑세스 토큰을 만료 처리한다")
        void logout() throws Exception {
            // given
            Long userId = userService.createUser(UserRequest.builder()
                    .email("test@test.com")
                    .nickname("test")
                    .build()).userId();
            String accessToken = jwtProvider.generate(userId.toString(), List.of(UserRole.USER.role()), JwtType.ACCESS).value();

            // when
            ResultActions firstResult = authApiClient.autnenticate(accessToken);
            ResultActions logoutResult = authApiClient.logout(accessToken);
            ResultActions secondResult = authApiClient.autnenticate(accessToken);

            // then
            firstResult.andExpectAll(
                    status().isOk(),
                    jsonPath("$").value(userId.toString())
            );
            logoutResult.andExpect(status().isNoContent());
            secondResult.andExpectAll(
                    status().isUnauthorized(),
                    jsonPath("$.message").exists()
            );
        }
    }

    @Nested
    @DisplayName("인증 테스트")
    class AuthenticationTest {
        @Test
        @DisplayName("인증 성공")
        void authenticationSuccess() throws Exception {
            // given
            Long userId = 1L;
            SignedJwt jwt = jwtProvider.generate(userId.toString(), List.of(UserRole.USER.role), JwtType.ACCESS);
            String accessToken = jwt.value();

            // when
            ResultActions result = authApiClient.autnenticate(accessToken);

            // then
            result.andExpect(status().isOk());
            assertThat(result.andReturn().getResponse().getContentAsString()).isEqualTo(userId.toString());
        }

        @Test
        @DisplayName("인증 실패")
        void authenticationFailure() throws Exception {
            // given
            String token = "wrong_token";

            // when
            ResultActions result = authApiClient.autnenticate(token);

            // then
            result.andExpectAll(
                    status().isUnauthorized(),
                    jsonPath("$.message").exists()
            );
        }
    }

    @Nested
    @DisplayName("인가 테스트")
    class AuthorizationTest {
        @Test
        @DisplayName("어드민 권한이 있어야 인가에 성공한다")
        void authorizationSuccess() throws Exception {
            // given
            Long userId = 1L;
            SignedJwt jwt = jwtProvider.generate(userId.toString(), List.of(UserRole.ADMIN.role()), JwtType.ACCESS);
            String accessToken = jwt.value();

            // when
            ResultActions result = authApiClient.authorize(accessToken);

            // then
            result.andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("어드민 권한이 없으면 인가에 실패한다")
        void authorizationFailure() throws Exception {
            // given
            Long userId = 1L;
            SignedJwt jwt = jwtProvider.generate(userId.toString(), List.of(UserRole.USER.role()), JwtType.ACCESS);
            String accessToken = jwt.value();

            // when
            ResultActions result = authApiClient.authorize(accessToken);

            // then
            result.andExpectAll(
                    status().isForbidden(),
                    jsonPath("$.message").exists()
            );
        }
    }
}