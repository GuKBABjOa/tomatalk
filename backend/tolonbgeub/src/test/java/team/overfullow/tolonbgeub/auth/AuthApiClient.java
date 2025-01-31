package team.overfullow.tolonbgeub.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@Component
@RequiredArgsConstructor
public class AuthApiClient {

    private final MockMvc mockMvc;
    private final AuthConfigProps authConfigProps;

    public ResultActions autnenticate(String accessToken) throws Exception {
        return mockMvc.perform(get("/api/auth/authentication")
                .header(authConfigProps.header, String.join(" ", authConfigProps.scheme, accessToken)));
    }

    public ResultActions authorize(String accessToken) throws Exception {
        return mockMvc.perform(get("/api/auth/authorization")
                .header(authConfigProps.header, String.join(" ", authConfigProps.scheme, accessToken)));
    }
}
