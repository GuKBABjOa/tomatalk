package team.overfullow.tolonbgeub.debate.subject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import team.overfullow.tolonbgeub.debate.subject.dto.SubjectResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping("/api/subjects/{subjectId}")
    public ResponseEntity<SubjectResponse> getById(@PathVariable Long subjectId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(subjectService.getById(subjectId));
    }
}
