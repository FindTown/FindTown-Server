package yapp.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yapp.common.domain.ResidentStatistics;

@Repository
public interface ResidentStatisticsRepository extends JpaRepository<ResidentStatistics, Long> {
}
