package yapp.domain.townMap.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yapp.domain.townMap.entity.Infra;
import yapp.domain.townMap.entity.Theme;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Long> {

  List<Theme> findThemeByCategory(String category);

}
