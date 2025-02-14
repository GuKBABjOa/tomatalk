package team.overfullow.tolonbgeub.subject;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.overfullow.tolonbgeub.auth.util.IdGenerator;
import team.overfullow.tolonbgeub.debate.Category;
import team.overfullow.tolonbgeub.subject.dto.SubjectResponse;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubjectService {

    private final IdGenerator idGenerator;
    private final SubjectRepository subjectRepository;

    public SubjectResponse getById(Long topicId) {
        return mapToSubjectResponse(subjectRepository.findById(topicId)
                .orElseThrow(() -> new SubjectException(HttpStatus.NOT_FOUND, "주제를 찾을 수 없습니다")));
    }

    private SubjectResponse mapToSubjectResponse(Subject domain) {
        return SubjectResponse.builder()
                .subjectId(domain.getId().toString())
                .subject(domain.getSubject())
                .description(domain.getDescription())
                .build();
    }

    public Subject getRandomSubject(Category category) {
        // todo 구현
        return subjectRepository.save(
                Subject.builder()
                        .id(idGenerator.generate())
                        .category(category)
                        .subject("카테고리에 해당하는 랜덤 주제")
                        .description("주제에 대한 추가적인 설명")
                        .build());
    }
}