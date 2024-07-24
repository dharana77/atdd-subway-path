package nextstep.subway.line.dto;

import nextstep.subway.StationResponse;
import nextstep.subway.line.Line;
import nextstep.subway.line.LineSection;

import java.util.List;

public class LineResponse {

  private Long id;

  private String name;

  private String color;

  private List<LineSection> sections;

  public LineResponse() {
  }

  public LineResponse(Long id, String name, String color, List<LineSection> sections) {
    this.id = id;
    this.name = name;
    this.color = color;
    this.sections = sections;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getColor() {
    return color;
  }

  public List<LineSection> getSections() {
    return sections;
  }

  public static LineResponse from(Line line) {
    return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getSections());
  }
}
