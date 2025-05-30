package team.overfullow.tolonbgeub;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import team.overfullow.tolonbgeub.auth.blacklist.TokenRepository;
import team.overfullow.tolonbgeub.debate.debate.repository.DebateRepository;
import team.overfullow.tolonbgeub.debate.subject.SubjectRepository;
import team.overfullow.tolonbgeub.report.ReportRepository;
import team.overfullow.tolonbgeub.user.Repository.UserRepository;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class ApiTestSupport {

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    DebateRepository debateRepository;

    @Autowired
    TokenRepository tokenRepository;

    @BeforeEach
    void beforeEach() {
        tokenRepository.deleteAll();
        debateRepository.deleteAll();
        subjectRepository.deleteAll();
        reportRepository.deleteAll();
        userRepository.deleteAll();
    }
}
