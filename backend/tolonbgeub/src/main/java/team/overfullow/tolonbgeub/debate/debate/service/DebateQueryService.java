package team.overfullow.tolonbgeub.debate.debate.service;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.overfullow.tolonbgeub.core.dto.CursorRequest;
import team.overfullow.tolonbgeub.core.dto.SortBy;
import team.overfullow.tolonbgeub.debate.Category;
import team.overfullow.tolonbgeub.debate.debate.DebateException;
import team.overfullow.tolonbgeub.debate.debate.domain.Debate;
import team.overfullow.tolonbgeub.debate.debate.domain.DebateStatus;
import team.overfullow.tolonbgeub.debate.debate.domain.DebateUser;
import team.overfullow.tolonbgeub.debate.debate.dto.CategoryDebateCount;
import team.overfullow.tolonbgeub.debate.debate.dto.DebateInfoResponse;
import team.overfullow.tolonbgeub.debate.debate.dto.DebateRoomDto;
import team.overfullow.tolonbgeub.debate.debate.dto.DebateUserDto;
import team.overfullow.tolonbgeub.debate.debate.repository.DebateRepository;
import team.overfullow.tolonbgeub.user.User;
import team.overfullow.tolonbgeub.webrtc.OpenViduHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DebateQueryService {

    private final DebateRepository debateRepository;
    private final OpenViduHandler openViduHandler;

    private static final String IMAGE_URL = "%s/api/users/%s/image"; // todo refactor 유저 프로필 이미지 처리
    @Value("${app.image.base-url}")
    private String imageBaseUrl;

    public DebateRoomDto getRoomById(Long id, @Nullable Long requestUserId) {
        Debate debate = debateRepository.findById(id)
                .orElseThrow(() -> new DebateException(HttpStatus.NOT_FOUND,"id에 해당하는 토론을 찾을 수 없습니다."));
        return toDebateRoomResponse(requestUserId, debate);
    }

    public Page<DebateInfoResponse> search(
            CursorRequest cursorRequest,
            SortBy sortBy,
            Set<Category> categories,
            DebateStatus status,
            String keyword
    ){
        Page<Debate> page = debateRepository.searchByCursor(cursorRequest, sortBy, categories, status, keyword);
        return page.map(this::toDebateInfoResponse);
    }

    public List<DebateUserDto> getUsersByDebateId(Long debateId) {
        return getById(debateId).getDebateUsers().stream()
                .map(this::toDebateUserResponse)
                .toList();
    }

    protected Debate getById(Long id) {
        return debateRepository.findById(id).orElseThrow(() -> new DebateException(HttpStatus.NOT_FOUND));
    }

    private DebateRoomDto toDebateRoomResponse(Long requestUserId, Debate debate) {
        return DebateRoomDto.builder()
                .openviduToken(openViduHandler.createConnection(debate.getId().toString()).getToken())
                .debateId(debate.getId().toString())
                .category(debate.getCategory().name())
                .subject(debate.getSubject().getSubject())
                .participant(debate.getDebateUsers().stream()
                        .map(this::toDebateUserResponse)
                        .anyMatch(u -> u.userId().equals(Objects.isNull(requestUserId)?null:requestUserId.toString())))
                .users(debate.getDebateUsers().stream()
                        .map(this::toDebateUserResponse)
                        .toList())
                .build();
    }

    private DebateUserDto toDebateUserResponse(DebateUser du) {
        User user = du.getUser();
        return DebateUserDto.builder()
                .userId(user.getId().toString()) // todo 매칭 후 nullpointerex 발생
                .nickname(user.getNickname())
                .profileImageUrl((user.getProfileImage() != null) ?
                        String.format(IMAGE_URL, imageBaseUrl, user.getId())
                        : null) // todo refactor: user와 profileImage 분리
                .position(du.getPosition())
                .positionOrder(du.getPositionOrder())
                .speechOrder(du.getSpeechOrder())
                .build();
    }

    private DebateInfoResponse toDebateInfoResponse(Debate debate) {
        LocalDateTime createdAt = debate.getCreatedAt();
        return DebateInfoResponse.builder()
                .debateId(debate.getId().toString())
                .category(debate.getCategory().name())
                .subject(debate.getSubject().getSubject())
                .status(debate.getStatus())
                .startedAtHour(createdAt.getHour()) // WAITING->STARTED 로 상태 바뀔 때,별도로 계산하는 게 좋을 거 같음
                .startedAtMinute(createdAt.getMinute())
                .createdAt(debate.getCreatedAt())
                .lastModifiedAt(debate.getLastModifiedAt())
                .build();
    }

    public List<CategoryDebateCount> countInProgressDebatesByCategory(){
        return debateRepository.countInProgressDebatesByCategory();
    }
}
