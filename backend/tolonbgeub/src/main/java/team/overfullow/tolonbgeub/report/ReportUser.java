package team.overfullow.tolonbgeub.report;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team.overfullow.tolonbgeub.user.User;

@Entity
@Table(name = "report_user")
@Getter
@Setter
@NoArgsConstructor
public class ReportUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 여러 ReportUser는 하나의 ReportEntity에 속함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private ReportEntity report;

    // 여러 ReportUser는 하나의 User에 속함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
