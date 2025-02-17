package team.overfullow.tolonbgeub.report;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import team.overfullow.tolonbgeub.user.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "report")
@Getter
@Setter
public class ReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob  // 내용이 길다면
    private String contents;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportUser> reportUsers = new ArrayList<>();


}
