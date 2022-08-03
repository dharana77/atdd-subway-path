package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;


@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {

    }

    public Section findSection(Section firstSection, BiPredicate<Section, Section> filter) {
        Section findSection = firstSection;
        while(findSection != null) {
            firstSection = findSection;
            findSection = matchSection(firstSection, filter);
            if(firstSection.equals(findSection)) {
                break;
            }
        }
        return firstSection;
    }

    private Section matchSection(Section firstSection, BiPredicate<Section, Section> filter) {
        for(Section section : this.sections) {
            if(filter.test(firstSection, section)) {
                return section;
            }
        }
        return null;
    }
    public boolean addSectionToMiddleSection(Line line, Station upStation, Station downStation, int distance, Predicate<Section> filter) {
        for(Section section : this.sections)
        {
            if(filter.test(section)) {
                return this.sections.add(section.divide(line, upStation, downStation, distance));
            }
        }
        return false;
    }

    public boolean addSectionToMiddleSectionMatchUpStation(Line line, Station upStation, Station downStation, int distance) {
        return addSectionToMiddleSection(line, upStation, downStation, distance,
                section -> section.upStationMatchFromStation(upStation));
    }

    public boolean addSectionToMiddleSectionMatchDownStation(Line line, Station upStation, Station downStation, int distance) {
        return addSectionToMiddleSection(line, upStation, downStation, distance,
                section -> section.downStationMatchFromStation(downStation));
    }

    public Section findTopSection() {
        Section firstSection = findFirstSection();
        return findSection(firstSection, (findSection, section) -> findSection.upStationMatchFromDownStation(section));
    }

    public Section findDownSection() {
        Section firstSection = findFirstSection();
        return findSection(firstSection, (findSection, section) -> findSection.downStationMatchFromUpStation(section));
    }

    public Section findFirstSection() {
        return this.sections.get(0);
    }

    protected boolean addSection(Section section) {
        return this.sections.add(section);
    }

    public boolean addSection(Line line, Station upStation, Station downStation, int distance) {

        // 구간 사이에 들어갈 케이스
        validateEqualsSection(new Section(line, upStation, downStation, distance));

        if(addSectionToMiddleSectionMatchUpStation(line, upStation, downStation, distance) ||
                addSectionToMiddleSectionMatchDownStation(line, upStation, downStation, distance)) {
            return true;
        }

        if(addSectionToTopSection(line, upStation, downStation, distance) ||
                addSectionToDownSection(line, upStation, downStation, distance)) {
            return true;
        }

        throw new IllegalStateException();
    }

    private void validateEqualsSection(Section otherSection) {
        if(this.sections.stream().anyMatch(s -> s.equals(otherSection))) {
            throw new IllegalStateException();
        }
    }

    private void throwsExceptionSectionCountOne() {
        if(this.sections.size() == 1) {
            throw new IllegalStateException();
        }
    }

    private boolean addSectionToTopSection(Line line, Station upStation, Station downStation, int distance) {
        final Section topSection = findTopSection();
        if(topSection.upStationMatchFromDownStation(downStation)) {
            return this.sections.add(new Section(line, upStation, downStation, distance));
        }
        return false;
    }

    private boolean addSectionToDownSection(Line line, Station upStation, Station downStation, int distance) {
        final Section downSection = findDownSection();
        if(downSection.downStationMatchFromUpStation(upStation)) {
            return this.sections.add(new Section(line, upStation, downStation, distance));
        }
        return false;
    }

    public boolean removeSection(Station station) {

        throwsExceptionSectionCountOne();

        //상행 구간 제거
        Section topSection = findTopSection();
        if(topSection.upStationMatchFromStation(station)) {
            return this.sections.remove(topSection);
        }

        //하행 구간 제거
        Section downSection = findDownSection();
        if(downSection.downStationMatchFromStation(station)) {
            return this.sections.remove(downSection);
        }

        //중간 구간 제거
        Section nextSection = topSection;
        while(nextSection != null) {
            Section prevSection = nextSection;
            nextSection = findNextSection(prevSection.getDownStation());
            if(prevSection.downStationMatchFromStation(station)) {
                //두 구간을 제거 후 합친 구간을 추가
                Section combineSection = prevSection.combine(nextSection);
                this.sections.removeAll(Arrays.asList(prevSection, nextSection));
                return this.sections.add(combineSection);
            }
        }

        throw new IllegalStateException();
    }

    public Section findNextSection(Station station) {
        return this.sections.stream()
                .filter(s -> s.upStationMatchFromDownStation(station))
                .findFirst()
                .orElse(null);
    }

    public List<Station> stations() {

        List<Station> stations = new ArrayList<>();
        Section topSection = findTopSection();

        stations.add(topSection.getUpStation());

        while(topSection != null) {
            stations.add(topSection.getDownStation());
            topSection = findNextSection(topSection.getDownStation());
        }
        return stations;
    }

    public List<Section> list() {
        return this.sections;
    }

    public boolean isEmpty() {
        return this.sections.isEmpty();
    }

    public int size() {
        return this.sections.size();
    }


}
