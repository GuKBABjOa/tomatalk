package team.overfullow.tolonbgeub;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import team.overfullow.tolonbgeub.debate.Category;
import team.overfullow.tolonbgeub.debate.debate.service.DebateService;
import team.overfullow.tolonbgeub.report.ReportEntity;
import team.overfullow.tolonbgeub.report.ReportRepository;
import team.overfullow.tolonbgeub.report.ReportUser;
import team.overfullow.tolonbgeub.user.Repository.UserRepository;
import team.overfullow.tolonbgeub.user.User;
import team.overfullow.tolonbgeub.user.dto.UserRequest;
import team.overfullow.tolonbgeub.user.dto.UserResponse;
import team.overfullow.tolonbgeub.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserService userService;
    private final DebateService debateService;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

//    @Bean
//    public ApplicationRunner dataLoader() {
//        return args -> initializeData();
//    }

    public void initializeData() {
        List<Long> userIds = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            String username = "user" + i;
            UserResponse user = userService.createUser(UserRequest.builder()
                    .email(String.format("%s@test.email", username))
                    .nickname(username)
                    .build());
            userIds.add(Long.valueOf(user.userId()));
        }
        Random random = new Random();

        // Debate 생성: 4명씩 묶어서 토론 데이터를 생성하고, 토론 ID를 저장
        List<Long> debateIds = new ArrayList<>();
        int debatesCount = userIds.size() / 4;
        for (int i = 0; i < debatesCount; i++) {
            int anInt = random.nextInt(100);
            Category category = Category.values()[anInt % Category.values().length];
            // 4명의 참여자 ID 목록 (예: user0~user3, user4~user7, ...)
            List<Long> participantIds = userIds.subList(i * 4, i * 4 + 4);
            // debateService.create(...)가 토론 생성 후 토론 ID를 반환한다고 가정합니다.
            Long debateId = debateService.create(category, participantIds);
            debateIds.add(debateId);
        }

        // 각 토론당 하나의 Report 생성
        // "토론에 참여한 4명의 사용자"가 대상이며, 레포트 내용은 더미 내용으로 설정합니다.
        for (int i = 0; i < debateIds.size(); i++) {
            // 해당 토론에 참여한 사용자 목록 (4명)
            List<Long> participantIds = userIds.subList(i * 4, i * 4 + 4);

            ReportEntity report = new ReportEntity();
            report.setContents("Dummy report content for debate " + debateIds.get(i)
                    + "\n[대화 내용 및 채점 내용 포함]");

            List<ReportUser> reportUsers = new ArrayList<>();
            for (Long uid : participantIds) {
                User user = userRepository.findById(uid)
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + uid));
                ReportUser reportUser = new ReportUser();
                reportUser.setUser(user);
                reportUser.setReport(report);
                reportUsers.add(reportUser);
            }
            report.setReportUsers(reportUsers);
            reportRepository.save(report);
        }
    }
}

