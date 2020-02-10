package atdd.station.Service;

import atdd.station.domain.Station;
import atdd.station.domain.dto.StationDto;
import atdd.station.repository.StationRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service("stationService")
public class StationService
{
    @Resource(name = "stationRepository")
    private StationRepository stationRepository;

    public Station create(StationDto stationDto)
    {
        return stationRepository.save(stationDto.toEntity());
    }

    public List<Station> list()
    {
        List<Station> returnList = stationRepository.findAll();
        return returnList;
    }

    public Optional<Station> findById(long id)
    {
        return stationRepository.findById(id);
    }

    public void deleteStationById(long id)
    {
        stationRepository.deleteById(id);
    }
}