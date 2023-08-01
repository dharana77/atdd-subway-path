package nextstep.subway.path.dto;

import nextstep.subway.exception.ErrorCode;
import nextstep.subway.exception.SubwayException;
import nextstep.subway.line.entity.Line;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.domain.SectionWeightedEdge;
import nextstep.subway.section.entity.Section;
import nextstep.subway.section.entity.Sections;
import nextstep.subway.station.entity.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GeneratedPathFinder implements PathFinder {

    private final WeightedMultigraph<Station, SectionWeightedEdge> graph = new WeightedMultigraph<>(SectionWeightedEdge.class);

    public GeneratedPathFinder(List<Line> lines) {
        addPath(lines);
    }

    public void addPath(List<Line> lines) {
        lines.stream()
                .flatMap(it -> it.getStations().stream())
                .forEach(this::addVertex);

        lines.stream()
                .flatMap(x -> x.getSections().getSections().stream())
                .map(SectionWeightedEdge::new)
                .forEach(x -> graph.addEdge(x.getSource(), x.getTarget(), x));
    }

    @Override
    public Sections findPath(Station source, Station target) {
        validationStations(source, target);
        DijkstraShortestPath<Station, SectionWeightedEdge> shortestPath = new DijkstraShortestPath<>(graph);

        GraphPath<Station, SectionWeightedEdge> path = shortestPath.getPath(source, target);
        validationPath(path);
        List<Section> sections = path.getEdgeList().stream()
                .map(SectionWeightedEdge::toSection)
                .collect(Collectors.toList());

        return new Sections(sections);
    }

    private void validationStations(Station source, Station target) {
        if (source.equals(target)) {
            throw new SubwayException(ErrorCode.EQUALS_STATIONS);
        }
    }

    private void validationPath(GraphPath<Station, SectionWeightedEdge> path) {
        if (path == null) {
            throw new SubwayException(ErrorCode.NOT_FOUND_PATH);
        }
    }

    private void addVertex(Station vertex) {
        graph.addVertex(vertex);
    }

    public Set<Station> getVertexSet() {
        return graph.vertexSet();
    }
}
