package subway.line;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.Station;
import subway.StationRepository;
import subway.exceptions.SubwayException;

import java.util.List;
import java.util.stream.Collectors;

import static subway.exceptions.errors.SubwayError.NOT_FOUND;

@Transactional(readOnly = true)
@Service
public class LineService {

  private final LineRepository lineRepository;

  private final StationRepository stationRepository;

  public LineService(LineRepository lineRepository, StationRepository stationRepository) {
    this.lineRepository = lineRepository;
    this.stationRepository = stationRepository;
  }

  @Transactional
  public Line createLine(LineCreateRequest lineCreateRequest) {
    StationsAtLine stationsAtLine = getStationsAtLine(lineCreateRequest.getUpStationId(), lineCreateRequest.getDownStationId());
    return lineRepository.save(lineCreateRequest.toLine(stationsAtLine.getUpStation(), stationsAtLine.getDownStation()));
  }

  public List<LineResponse> getLines() {
    return lineRepository.findAllJoin().stream()
            .map(LineResponse::from)
            .collect(Collectors.toList());
  }

  public LineResponse getLine(Long id) {
    return LineResponse.from(lineRepository.findById(id).orElseThrow(() -> new SubwayException(NOT_FOUND)));
  }

  @Transactional
  public LineResponse modifyLine(Long id, LineModifyRequest lineModifyRequest) {
    Line line = getLineById(id);
    line.updateName(lineModifyRequest.getName());
    line.updateColor(lineModifyRequest.getColor());
    return LineResponse.from(line);
  }

  @Transactional
  public void deleteLine(Long id){
    lineRepository.deleteById(id);
  }

  private StationsAtLine getStationsAtLine(Long upStationId, Long downStationId) {
    Station upStation = stationRepository.findById(upStationId).orElseThrow(()-> new SubwayException(NOT_FOUND));
    Station downStation = stationRepository.findById(downStationId).orElseThrow(()-> new SubwayException(NOT_FOUND));

    return new StationsAtLine(upStation, downStation);
  }

  private Line getLineById(Long id) {
    return lineRepository.findById(id).orElseThrow(()-> new SubwayException(NOT_FOUND));
  }
}
