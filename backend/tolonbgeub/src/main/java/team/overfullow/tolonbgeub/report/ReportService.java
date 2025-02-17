package team.overfullow.tolonbgeub.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    public Slice<ReportDto> getReportsByUser(Long userId, int page, int size) {
        // 페이지는 0 부터 시작
        Pageable pageable = PageRequest.of(page, size);

        // userId 기준으로 조회
        Slice<ReportEntity> reportSlice = reportRepository.findReportsByUserId(userId, pageable);

        // 엔티티를 DTO로 매핑
        Slice<ReportDto> dtoSlice = reportSlice.map(ReportDto::fromEntity);
        return dtoSlice;
    }
}
