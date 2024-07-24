package nextstep.subway.line;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class LineSections {

  @OneToMany(mappedBy = "line", fetch = FetchType.LAZY)
  private List<LineSection> sections = new ArrayList<>();

  public List<LineSection> getSections() {
    return sections;
  }

  public void addSection(LineSection lineSection) {
    if(sections.isEmpty()) {
      sections.add(lineSection);
      return;
    }

    List<LineSection> upSection = sections.stream()
            .filter(section -> section.containStation(lineSection.getUpStation()))
            .collect(Collectors.toList());;

    List<LineSection> downSection = sections.stream()
            .filter(section -> section.containStation(lineSection.getDownStation()))
            .collect(Collectors.toList());

  }
}
