package nextstep.subway.line;

import nextstep.subway.StationService;
import nextstep.subway.line.dto.LineModifyRequest;
import nextstep.subway.line.dto.LineSectionAppendRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nextstep.subway.Station;
import nextstep.subway.StationRepository;
import nextstep.subway.exceptions.errors.SubwayException;
import nextstep.subway.line.dto.LineCreateRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.StationsAtLine;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static nextstep.subway.exceptions.errors.SubwayErrorCode.BAD_REQUEST;
import static nextstep.subway.exceptions.errors.SubwayErrorCode.NOT_FOUND;

@Transactional(readOnly = true)
@Service
public class LineService {
  private final LineRepository lineRepository;
  private final StationRepository stationRepository;
  private final StationService stationService;
  private final LineSectionRepository lineSectionRepository;

  public LineService(LineRepository lineRepository, StationRepository stationRepository,
                     StationService stationService, LineSectionRepository lineSectionRepository) {
    this.lineRepository = lineRepository;
    this.stationRepository = stationRepository;
    this.stationService = stationService;
    this.lineSectionRepository = lineSectionRepository;
  }

  @Transactional
  public Line createLine(LineCreateRequest lineCreateRequest) {
    StationsAtLine stationsAtLine = getStationsAtLine(lineCreateRequest.getUpStationId(), lineCreateRequest.getDownStationId());
    LineSection lineSection = new LineSection(stationsAtLine.getUpStation(), stationsAtLine.getDownStation(), lineCreateRequest.getDistance());
    return lineRepository.save(new Line());
  }

  public List<LineResponse> getLines() {
    return lineRepository.findAll().stream()
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

  @Transactional
  public void appendLineSection(Long id, LineSectionAppendRequest lineSectionAppendRequest) {
    Line line = getLineById(id);
    Station upStation = stationService.findById(lineSectionAppendRequest.getUpStationId());
    Station downStation = stationService.findById(lineSectionAppendRequest.getDownStationId());

    LineSection lineSection = new LineSection(upStation, downStation, lineSectionAppendRequest.getDistance());
    line.appendSection(lineSection);
    lineSectionRepository.save(lineSection);
  }

  @Transactional
  public void deleteLineSection(Long lineId, Long stationId) {
    List<LineSection> lineSection = lineSectionRepository.findAllByLineId(lineId);

    LineSection lastLineSection = lineSection.stream().max((a, b) -> (int) (a.getIndex() - b.getIndex()))
      .orElseThrow(() -> new SubwayException(NOT_FOUND));

    if (lastLineSection.getDownStation().getId() != stationId || lineSection.size() <= 1) {
      throw new SubwayException(BAD_REQUEST);
    }

    lineSectionRepository.deleteByLineIdAndStationId(stationId);
  }
}
