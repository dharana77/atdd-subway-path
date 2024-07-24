package nextstep.subway.line;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class LineSections {

  @OneToMany(mappedBy = "line", fetch = FetchType.LAZY)
  private List<LineSection> sections = new ArrayList<>();

  public List<LineSection> getSections() {
    return sections;
  }

  public void addSection(LineSection lineSection) {
    sections.add(lineSection);
  }
}
