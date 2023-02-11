package yapp.domain.townMap.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yapp.domain.townMap.entity.Infra;

@Repository
public interface InfraRepository extends JpaRepository<Infra, Long> {

  List<Infra> findInfraByCategory(String category);

}
