package team.overfullow.tolonbgeub.debate.debate.service;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.overfullow.tolonbgeub.core.util.IdGenerator;
import team.overfullow.tolonbgeub.core.dto.CursorRequest;
import team.overfullow.tolonbgeub.core.dto.CursorResponse;
import team.overfullow.tolonbgeub.core.dto.SortBy;
import team.overfullow.tolonbgeub.debate.Category;
import team.overfullow.tolonbgeub.debate.debate.domain.Debate;
import team.overfullow.tolonbgeub.debate.debate.domain.DebateStatus;
import team.overfullow.tolonbgeub.debate.debate.domain.DebateUser;
import team.overfullow.tolonbgeub.debate.debate.dto.DebateInfoResponse;
import team.overfullow.tolonbgeub.debate.debate.dto.DebateRoomResponse;
import team.overfullow.tolonbgeub.debate.debate.dto.DebateUserResponse;
import team.overfullow.tolonbgeub.debate.debate.repository.DebateRepository;
import team.overfullow.tolonbgeub.debate.subject.SubjectService;
import team.overfullow.tolonbgeub.user.Repository.UserRepository;
import team.overfullow.tolonbgeub.user.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DebateService {
    private static final String[] positions = new String[]{"찬성", "반대", "찬성", "반대"};

    private final IdGenerator idGenerator;
    private final DebateQueryService queryService;
    private final DebateRepository debateRepository;
    private final SubjectService subjectService;
    private final UserRepository userRepository; //todo userRepo 직접 사용 지양

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
                .status(DebateStatus.READY)
                .category(category)
                .subject(subjectService.getRandomSubject(category))
                .debateUsers(debateUsers)
                .build();

        return debateRepository.save(debate).getId();
    }

    public void start(Long debateId) {
        queryService.getById(debateId).start();
    }

    public DebateRoomResponse getRoomById(Long id, @Nullable Long requestUserId) {
        return queryService.getRoomById(id, requestUserId);
    }

    public List<DebateUserResponse> getUsersByDebateId(Long debateId) {
        return queryService.getUsersByDebateId(debateId);
    }

    public Page<DebateInfoResponse> search(CursorRequest cursorRequest, SortBy sortBy, Set<Category> categories, DebateStatus status, String keyword) {
        return queryService.search(cursorRequest, sortBy, categories, status, keyword);
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
