package subway.line;

import lombok.Builder;
import subway.Station;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Builder
@Entity
public class Line {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private String color;

  @OneToOne
  @JoinColumn(name = "up_station_id")
  private Station upStation;

  @OneToOne
  @JoinColumn(name = "down_station_id")
  private Station downStation;

  private int distance;

  public Line() {
  }

  public Line(Long id, String name, String color, Station upStation, Station downStation, int distance) {
    this.id = id;
    this.name = name;
    this.color = color;
    this.upStation = upStation;
    this.downStation = downStation;
    this.distance = distance;
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

  public Station getUpStation() {
    return upStation;
  }

  public Station getDownStation() {
    return downStation;
  }

  public int getDistance() {
    return distance;
  }

  public void updateName(String name) {
    this.name = name;
  }

  public void updateColor(String color) {
    this.color = color;
  }
}
