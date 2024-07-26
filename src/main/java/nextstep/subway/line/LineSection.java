package nextstep.subway.line;

import nextstep.subway.Station;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class LineSection {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "line_id")
  private Line line;

  @OneToOne
  private Station upStation;

  @OneToOne
  private Station downStation;

  private int distance;

  public LineSection() {
  }

  public LineSection(Station upStation, Station downStation, int distance) {
    this.upStation = upStation;
    this.downStation = downStation;
    this.distance = distance;
  }

  public LineSection(Line line, Station upStation, Station downStation, int distance) {
    this.line = line;
    this.upStation = upStation;
    this.downStation = downStation;
    this.distance = distance;
  }

  public Long getId() {
    return id;
  }

  public Line getLine() {
    return line;
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

  public void applyLine(Line line) {
    this.line = line;
  }

  public boolean containStation(Station station) {
    return upStation.equals(station) || downStation.equals(station);
  }

  public void decreaseDistance(int distance) {
    this.distance -= distance;
  }

  public void changeUpStation(Station upStation) {
    this.upStation = upStation;
  }

  public void changeDownStation(Station downStation) {
    this.downStation = downStation;
  }
}
