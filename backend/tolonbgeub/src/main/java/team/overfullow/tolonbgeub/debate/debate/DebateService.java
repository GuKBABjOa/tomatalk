package team.overfullow.tolonbgeub.debate.debate;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.overfullow.tolonbgeub.auth.util.IdGenerator;
import team.overfullow.tolonbgeub.debate.Category;
import team.overfullow.tolonbgeub.debate.debate.dto.DebateRoomResponse;
import team.overfullow.tolonbgeub.debate.debate.dto.DebateUserResponse;
import team.overfullow.tolonbgeub.debate.subject.SubjectService;
import team.overfullow.tolonbgeub.user.Repository.UserRepository;
import team.overfullow.tolonbgeub.user.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DebateService {
    private static final String[] positions = new String[]{"찬성", "반대", "찬성", "반대"};

    private final IdGenerator idGenerator;
    private final DebateRepository debateRepository;
    private final SubjectService subjectService;
    private final UserRepository userRepository; //todo userRepo 직접 사용 지양

    @Transactional
    public Long create(Category category, List<Long> userIds) {
        List<User> users = userRepository.findAllById(userIds);
        if (users.size() != 4) {
            throw new IllegalArgumentException("기본 룰 유저 수는 4명입니다.");
        }

        List<DebateUser> debateUsers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            debateUsers.add(DebateUser.builder()
                    .id(idGenerator.generate())
                    .user(users.get(i))
                    .position(positions[i])
                    .positionOrder(i % 2 + 1) // 1,2,1,2 기본 룰 값
                    .speechOrder(i + 1) // 1,2,3,4 기분 룰 값
                    .build());
        }

        Debate debate = Debate.builder()
                .id(idGenerator.generate())
                .category(category)
                .subject(subjectService.getRandomSubject(category))
                .debateUsers(debateUsers)
                .build();

        return debateRepository.save(debate).getId();
    }

    @Transactional(readOnly = true)
    public DebateRoomResponse getRoomById(Long id, @Nullable Long requestUserId) {
        Debate debate = debateRepository.findById(id)
                .orElseThrow(() -> new DebateException(HttpStatus.NOT_FOUND));
        return toDebateRoomResponse(requestUserId, debate);
    }

    @Deprecated
    @Transactional(readOnly = true)
    public List<DebateRoomResponse> getAllForTest(){
        return debateRepository.findAll().stream().map(d -> toDebateRoomResponse(null, d)).toList();
    }

    @Transactional(readOnly = true)
    public List<DebateUserResponse> getUsersByDebateId(Long debateId) {
        return getById(debateId).getDebateUsers().stream()
                .map(this::toDebateUserResponse)
                .toList();
    }

    private Debate getById(Long id){
        return debateRepository.findById(id).orElseThrow(() -> new DebateException(HttpStatus.NOT_FOUND));
    }

    private DebateRoomResponse toDebateRoomResponse(Long requestUserId, Debate debate) {
        return DebateRoomResponse.builder()
                .debateId(debate.getId().toString())
                .category(debate.getCategory().name())
                .subject(debate.getSubject().getSubject())
                .participant(debate.getDebateUsers().stream()
                        .map(this::toDebateUserResponse)
                        .anyMatch(u -> u.userId().equals(requestUserId)))
                .users(debate.getDebateUsers().stream()
                        .map(this::toDebateUserResponse)
                        .toList())
                .build();
    }

    private DebateUserResponse toDebateUserResponse(DebateUser du) {
        User user = du.getUser();
        return DebateUserResponse.builder()
                .userId(user.getId().toString())
                .nickname(user.getNickname())
                .profileImageUrl("제공 예정")
                .position(du.getPosition())
                .positionOrder(du.getPositionOrder())
                .speechOrder(du.getSpeechOrder())
                .build();
    }


    public void start(Long debateId) {
        getById(debateId).start();
    }

    @Deprecated
    public long getTestDebateId(Long userId){
        return debateRepository.findAll().stream()
                .filter(d -> d.getDebateUsers().stream()
                        .anyMatch(u -> u.getUser().getId().equals(userId)))
                .sorted(Comparator.comparing(Debate::getCreatedAt).reversed())
                .map(d -> d.getId())
                .findFirst().get();
    }
}
