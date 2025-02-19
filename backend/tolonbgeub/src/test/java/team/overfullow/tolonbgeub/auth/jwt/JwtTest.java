package team.overfullow.tolonbgeub.auth.jwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import team.overfullow.tolonbgeub.core.util.UniqueIdGenerator;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class JwtTest {

    @Nested
    @DisplayName("JwtProvider 테스트")
    class JWT_생성_테스트 {

        @Test
        @DisplayName("jwt가 정상적으로 생성된다")
        void generate() throws Exception {
            // given
            JwtConfigProps jwtConfigProps = new JwtConfigProps();
            jwtConfigProps.setIssuer("test");
            jwtConfigProps.setSecret("test");
            jwtConfigProps.setExpirySeconds(3600);

            JwtProvider sut = new JwtProvider(jwtConfigProps, new UniqueIdGenerator());

            // when
            SignedJwt jwt = sut.generate("value", Collections.emptyList(), JwtType.ACCESS);

            // then
            assertSoftly(softly -> {
                assertThat(jwt).isNotNull();
                assertThat(jwt.value()).isNotNull();
                assertThat(jwt.expirySeconds()).isEqualTo(3600);
            });
        }
    }

    @Nested
    @DisplayName("JwtValidator 테스트")
    class JWT_검증_테스트 {

        @Test
        @DisplayName("jwt가 정상적으로 검증되면 claim을 확인할 수 있다")
        void validate() throws Exception {
            // given
            JwtConfigProps jwtConfigProps = new JwtConfigProps();
            jwtConfigProps.setIssuer("test");
            jwtConfigProps.setSecret("test");
            jwtConfigProps.setExpirySeconds(3600);

            JwtProvider jwtProvider = new JwtProvider(jwtConfigProps, new UniqueIdGenerator());
            SignedJwt jwt = jwtProvider.generate("value", Collections.emptyList(), JwtType.ACCESS);
            JwtValidator sut = new JwtValidator(jwtProvider);

            // when
            JwtClaims claims = sut.validate(jwt.value());

            // then
            assertSoftly(softly -> {
                assertThat(claims.roles()).isEmpty();
                assertThat(claims.type()).isEqualTo(JwtType.ACCESS);
                assertThat(claims.userId()).isEqualTo("value");
            });
        }

        @Test
        @DisplayName("jwt가 만료되면 검증 예외가 발생한다")
        void expiredToken() throws Exception {
            // given
            JwtConfigProps jwtConfigProps = new JwtConfigProps();
            jwtConfigProps.setIssuer("test");
            jwtConfigProps.setSecret("test");
            jwtConfigProps.setExpirySeconds(0);

            JwtProvider jwtProvider = new JwtProvider(jwtConfigProps, new UniqueIdGenerator());
            SignedJwt jwt = jwtProvider.generate("value", Collections.emptyList(), JwtType.ACCESS);
            JwtValidator sut = new JwtValidator(jwtProvider);

            // expected
            assertThatThrownBy(() -> sut.validate(jwt.value()))
                    .isInstanceOf(JwtValidationException.class);
        }

        @Test
        @DisplayName("다른 secret으로 서명된 jwt 검증 시 예외가 발생한다")
        void invalidSecret() throws Exception {
            // given
            JwtConfigProps jwtConfigProps = new JwtConfigProps();
            jwtConfigProps.setIssuer("test");
            jwtConfigProps.setSecret("test");
            jwtConfigProps.setExpirySeconds(0);

            JwtProvider jwtProvider = new JwtProvider(jwtConfigProps, new UniqueIdGenerator());
            SignedJwt jwt = jwtProvider.generate("value", Collections.emptyList(), JwtType.ACCESS);

            jwtConfigProps.setSecret("WRONG_SECRET");
            JwtValidator sut = new JwtValidator(new JwtProvider(jwtConfigProps, new UniqueIdGenerator()));

            // expected
            assertThatThrownBy(() -> sut.validate(jwt.value()))
                    .isInstanceOf(JwtValidationException.class);
        }
    }
}