package team.overfullow.tolonbgeub.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import team.overfullow.tolonbgeub.auth.UserId;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // /api/reports/me?userId=123&page=0&size=10
    @GetMapping("/me")
    public Slice<ReportDto> getMyReports(
            @AuthenticationPrincipal UserId userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return reportService.getReportsByUser(Long.valueOf(userId.value()), page, size);
    }
}
