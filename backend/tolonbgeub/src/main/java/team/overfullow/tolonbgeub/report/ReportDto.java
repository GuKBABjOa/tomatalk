package team.overfullow.tolonbgeub.report;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ReportDto {
    private Long id;
    private List<Long> userIds; // 다수의 사용자 ID를 담을 리스트
    private String contents;

    public static ReportDto fromEntity(ReportEntity report) {
        ReportDto dto = new ReportDto();
        dto.setId(report.getId());
        // ReportUser에서 User의 id를 추출
        dto.setUserIds(
                report.getReportUsers().stream()
                        .map(ru -> ru.getUser().getId())
                        .collect(Collectors.toList())
        );
        dto.setContents(report.getContents());
        return dto;
    }
}
