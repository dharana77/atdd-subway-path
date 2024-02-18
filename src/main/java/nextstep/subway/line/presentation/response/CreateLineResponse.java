package nextstep.subway.line.presentation.response;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.service.dto.CreateLineSectionDto;

import java.util.List;
import java.util.stream.Collectors;

public class CreateLineResponse {

    private Long lineId;

    private String name;

    private String color;

    private List<CreateLineSectionDto> sections;

    private CreateLineResponse() {
    }

    private CreateLineResponse(Long lineId, String name, String color, List<CreateLineSectionDto> sections) {
        this.lineId = lineId;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public static CreateLineResponse from(Line line) {
        return new CreateLineResponse(
                line.getLineId(),
                line.getName(),
                line.getColor(),
                line.getSections().stream()
                        .map(CreateLineSectionDto::from)
                        .collect(Collectors.toList())
        );
    }

    public Long getLineId() {
        return lineId;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<CreateLineSectionDto> getSections() {
        return sections;
    }

}
