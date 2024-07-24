package nextstep.subway.line;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LineRepository extends JpaRepository<Line, Long> {

//  @Query("SELECT l FROM Line l JOIN FETCH l.upStation JOIN FETCH l.downStation")
//  List<Line> findAllJoin();

  List<Line> findAll();
}
