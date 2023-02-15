package nextstep.subway.applicaion.dto;

import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Getter;
import nextstep.subway.domain.Station;

import java.util.List;

@Getter
@Builder
public class PathRequest {

    @NotNull
    private Long source;
    @NotNull
    private Long target;

    private PathRequest(Long source, Long target) {
        this.source = source;
        this.target = target;
    }

    public static PathRequest of(Long source, Long target) {
        return new PathRequest(source, target);
    }

    public List<Long> toStationIds() {
        return List.of(source, target);
    }

    public Station getSourceStation() {
        return new Station(source);
    }

    public Station getTargetStation() {
        return new Station(target);
    }

    public boolean isSourceAndTargetSame() {
        return source.equals(target);
    }
}
