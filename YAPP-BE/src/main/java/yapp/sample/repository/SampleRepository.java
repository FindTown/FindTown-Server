package yapp.sample.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yapp.sample.entity.SampleEntity;

@Repository
public interface SampleRepository extends JpaRepository<SampleEntity, Long> {
}
