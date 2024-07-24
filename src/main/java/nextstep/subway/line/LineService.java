package nextstep.subway.line;

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

  private final LineSectionRepository lineSectionRepository;

  public LineService(LineRepository lineRepository, StationRepository stationRepository, LineSectionRepository lineSectionRepository) {
    this.lineRepository = lineRepository;
    this.stationRepository = stationRepository;
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
    List<LineSection> lineSections = getLineSectionsByLineId(id);

    long index = lineSections.stream().map(item -> item.getId()).mapToLong(Long::longValue).max().orElse(1L);
    Set<Long> lineSectionStations = getUniqueStationIds(lineSections);
    if(isReqUpstationIdNotEqualsIndex(lineSectionAppendRequest, index) || isReqStationIdExist(lineSectionStations, lineSectionAppendRequest)){
      throw new SubwayException(BAD_REQUEST);
    }

    LineSection lineSection = createNewLineSection(lineSectionAppendRequest);
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

  private List<LineSection> getLineSectionsByLineId(Long lineId) {
    return lineSectionRepository.findAllByLineId(lineId);
  }


  private Set<Long> getUniqueStationIds(List<LineSection> lineSections) {
    Set<Long> lineSectionStations = new HashSet<>();
    lineSections.forEach(item -> {
      lineSectionStations.add(item.getUpStation().getId());
      lineSectionStations.add(item.getDownStation().getId());
    });
    return lineSectionStations;
  }

  private boolean isReqUpstationIdNotEqualsIndex(LineSectionAppendRequest lineSectionAppendRequest, Long index) {
    return !lineSectionAppendRequest.getUpStationId().equals(index);
  }

  private boolean isReqStationIdExist(Set lineSectionStations, LineSectionAppendRequest lineSectionAppendRequest) {
    return lineSectionStations.contains(lineSectionAppendRequest.getDownStationId());
  }

  private LineSection createNewLineSection(LineSectionAppendRequest lineSectionAppendRequest) {
    return new LineSection(
      stationRepository.findById(lineSectionAppendRequest.getUpStationId()).orElseThrow(() -> new SubwayException(NOT_FOUND)),
      stationRepository.findById(lineSectionAppendRequest.getDownStationId()).orElseThrow(() -> new SubwayException(NOT_FOUND)),
      lineSectionAppendRequest.getDistance());
  }
}
