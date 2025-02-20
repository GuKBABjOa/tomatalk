package team.overfullow.tolonbgeub.debate.subject;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.overfullow.tolonbgeub.core.util.IdGenerator;
import team.overfullow.tolonbgeub.debate.Category;
import team.overfullow.tolonbgeub.debate.subject.dto.SubjectResponse;
import team.overfullow.tolonbgeub.debate.subject.dto.RandomSubjectResponse;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubjectService {

    private final IdGenerator idGenerator;
    private final SubjectRepository subjectRepository;

    private final List<Subject> subjectCache = new ArrayList<>(); // 모든 주제 캐싱

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

    public RandomSubjectResponse getRandomSubjects() {
        subjectCache.clear();
        subjectCache.addAll(subjectRepository.findAll()); // 모든 주제 가져오기
        if (subjectCache.size() < 4) {
            throw new SubjectException(HttpStatus.NOT_FOUND, "주제 개수가 4개보다 적습니다.");
        }
    
        // 중복 없이 4개의 랜덤한 주제 선택
        Set<Subject> randomSubjects = new HashSet<>();
        while (randomSubjects.size() < 4) {
            int randomIndex = ThreadLocalRandom.current().nextInt(subjectCache.size());
            randomSubjects.add(subjectCache.get(randomIndex)); // 중복 방지
        }
    
        // 리스트로 변환
        List<Subject> selectedSubjects = new ArrayList<>(randomSubjects);
    
        // `RandomSubjectResponse` 형식에 맞게 변환하여 반환
        return RandomSubjectResponse.builder()
                .subject1(selectedSubjects.get(0).getSubject())
                .subject2(selectedSubjects.get(1).getSubject())
                .subject3(selectedSubjects.get(2).getSubject())
                .subject4(selectedSubjects.get(3).getSubject())
                .build();
    }
    

}