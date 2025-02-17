package team.overfullow.tolonbgeub.report;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {

    @Query("SELECT DISTINCT r FROM ReportEntity r " +
            "JOIN r.reportUsers ru " +
            "JOIN ru.user u " +
            "WHERE u.id = :userId")
    Slice<ReportEntity> findReportsByUserId(@Param("userId") Long userId, Pageable pageable);

    // 필요 시 전체 리스트 조회 메서드도 제공 가능
    @Query("SELECT DISTINCT r FROM ReportEntity r " +
            "JOIN r.reportUsers ru " +
            "JOIN ru.user u " +
            "WHERE u.id = :userId")
    List<ReportEntity> findAllReportsByUserId(@Param("userId") Long userId);
}
