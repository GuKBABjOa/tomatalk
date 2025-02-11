package team.overfullow.tolonbgeub.subject;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@Component
@RequiredArgsConstructor
public class SubjectApiClient {

    private final MockMvc mockMvc;

    public ResultActions callGetById(Long subjectId) throws Exception {
        return mockMvc.perform(get("/api/subjects/{subjectId}", subjectId));
    }
}
