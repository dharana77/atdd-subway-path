package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import nextstep.subway.domain.exception.NotValidDeleteTargetStation;
import nextstep.subway.domain.exception.NotValidSectionDistanceException;
import nextstep.subway.domain.exception.NotValidSectionStationsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SectionsTest {

    private Sections sut;

    private Line 분당선;
    private Station 청량리역;
    private Station 왕십리역;

    @BeforeEach
    void setUp() {
        분당선 = new Line("분당선", "yellow");
        청량리역 = new Station("청량리역");
        왕십리역 = new Station("왕십리역");
        sut = new Sections();
        sut.add(분당선, 청량리역, 왕십리역, 10);
    }

    @DisplayName("마지막 구간 추가")
    @Test
    void addLastSection() {
        var 서울숲역 = new Station("서울숲역");
        var distance = 10;
        sut.add(분당선, 왕십리역, 서울숲역, distance);

        var sections = sut.getOrderedSections();
        var lastSection = sections.get(sections.size() - 1);
        assertAll(
                () -> assertThat(lastSection.getUpStation()).isEqualTo(왕십리역),
                () -> assertThat(lastSection.getDownStation()).isEqualTo(서울숲역),
                () -> assertThat(lastSection.getDistance()).isEqualTo(distance),
                () -> assertThat(sut.getStations()).containsExactly(청량리역, 왕십리역, 서울숲역)
        );
    }

    @DisplayName("첫 구간 추가")
    @Test
    void addFirstSection() {
        var 새로운역 = new Station("새로운역");
        var distance = 10;
        sut.add(분당선, 새로운역, 청량리역, distance);

        var firstSection = sut.getOrderedSections().get(0);
        assertAll(
                () -> assertThat(firstSection.getUpStation()).isEqualTo(새로운역),
                () -> assertThat(firstSection.getDownStation()).isEqualTo(청량리역),
                () -> assertThat(firstSection.getDistance()).isEqualTo(distance),
                () -> assertThat(sut.getStations()).containsExactly(새로운역, 청량리역, 왕십리역)
        );
    }

    @DisplayName("구간 사이에 새 구간 추가 (하행역이 신규역)")
    @Test
    void addSectionWithNewDownStationInMiddle() {
        var 중간역 = new Station("중간역");
        sut.add(분당선, 청량리역, 중간역, 5);

        var sections = sut.getOrderedSections();
        var newSection = sections.get(0);
        var updatedSection = sections.get(1);
        assertAll(
                () -> assertThat(newSection.getUpStation()).isEqualTo(청량리역),
                () -> assertThat(newSection.getDownStation()).isEqualTo(중간역),
                () -> assertThat(newSection.getDistance()).isEqualTo(5),
                () -> assertThat(updatedSection.getUpStation()).isEqualTo(중간역),
                () -> assertThat(updatedSection.getDownStation()).isEqualTo(왕십리역),
                () -> assertThat(updatedSection.getDistance()).isEqualTo(5),
                () -> assertThat(sut.getStations()).containsExactly(청량리역, 중간역, 왕십리역)
        );
    }

    @DisplayName("구간 사이에 새 구간 추가 (상행역이 신규역)")
    @Test
    void addSectionWithNewUpStationInMiddle() {
        var 중간역 = new Station("중간역");
        sut.add(분당선, 중간역, 왕십리역, 5);

        var sections = sut.getOrderedSections();
        var newSection = sections.get(1);
        var updatedSection = sections.get(0);
        assertAll(
                () -> assertThat(newSection.getUpStation()).isEqualTo(중간역),
                () -> assertThat(newSection.getDownStation()).isEqualTo(왕십리역),
                () -> assertThat(newSection.getDistance()).isEqualTo(5),
                () -> assertThat(updatedSection.getUpStation()).isEqualTo(청량리역),
                () -> assertThat(updatedSection.getDownStation()).isEqualTo(중간역),
                () -> assertThat(updatedSection.getDistance()).isEqualTo(5),
                () -> assertThat(sut.getStations()).containsExactly(청량리역, 중간역, 왕십리역)
        );
    }

    @ParameterizedTest(name = "구간 사이의 새 구간의 거리가 기존 구간보다 크거나 같으면 추가 실패 / distance = {0}")
    @ValueSource(ints = {10, 15})
    void sectionAdditionFailsWhenDistanceOfNewSectionInMiddleIsGreater(int distance) {
        var 중간역 = new Station("중간역");

        assertThrows(NotValidSectionDistanceException.class, () -> sut.add(분당선, 청량리역, 중간역, distance));
    }

    @DisplayName("구간의 상하행역이 모두 노선에 존재하지 않으면 추가 실패")
    @Test
    void sectionAdditionFailsWhenNeitherUpAndDownStationNotExist() {
        var 새로운역 = new Station("새로운역");
        var 다른새로운역 = new Station("다른새로운역");

        assertThrows(NotValidSectionStationsException.class, () -> sut.add(분당선, 새로운역, 다른새로운역, 10));
    }

    @DisplayName("구간 제거")
    @Test
    void removeSection() {
        sut.removeByStation(왕십리역);

        assertThat(sut.getStations()).isEmpty();
    }

    @DisplayName("마지막 역이 아닌 역으로 구간 제거시 예외 발생")
    @Test
    void cantRemoveSectionByStationInMiddle() {
        assertThrows(NotValidDeleteTargetStation.class, () -> sut.removeByStation(청량리역));
    }

}