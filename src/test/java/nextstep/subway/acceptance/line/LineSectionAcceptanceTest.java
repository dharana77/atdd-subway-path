package nextstep.subway.acceptance.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.StationRequest;
import nextstep.subway.StationResponse;
import nextstep.subway.line.dto.LineCreateRequest;
import nextstep.subway.line.dto.LineSectionAppendRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

@DisplayName("지하철 노선 구간 관리 기능")
@Sql(scripts = "classpath:truncate-tables.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LineSectionAcceptanceTest {

  private long 종합운동장_id;
  private long 잠실새내_id;
  private long 잠실_id;
  private long 이호선_id;


  @BeforeEach
  void setUp() {
    this.종합운동장_id = RestAssured.given().log().all()
      .body( new StationRequest("종합운동장"))
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/stations")
      .then().log().all()
      .extract().as(StationResponse.class).getId();

    this.잠실새내_id = RestAssured.given().log().all()
      .body( new StationRequest("잠실새내"))
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/stations")
      .then().log().all()
      .extract().as(StationResponse.class).getId();

    this.잠실_id = RestAssured.given().log().all()
      .body( new StationRequest("잠실"))
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/stations")
      .then().log().all()
      .extract().as(StationResponse.class).getId();

    RestAssured.given().log().all()
      .body(new LineCreateRequest("2호선", "green", this.종합운동장_id, this.잠실_id, 10))
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/lines")
      .then().log().all()
      .extract();

    this.이호선_id = 1L;
  }

  @DisplayName("새로운 구간을 등록합니다.")
  @Test
  void addSection() {
    // given
    RestAssured.given().log().all()
      .body(new LineSectionAppendRequest(종합운동장_id, 잠실새내_id, 10))
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/lines/{lineId}/sections", 이호선_id)
      .then().log().all()
      .extract();

    long newDownStationId = RestAssured.given().log().all()
        .body(new StationRequest("새로운 상행역"))
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when().post("/stations")
        .then().log().all()
        .extract().as(StationResponse.class).getId();

    long newUpStationId = RestAssured.given().log().all()
        .body(new StationRequest("새로운 하행역"))
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when().post("/stations")
        .then().log().all()
        .extract().as(StationResponse.class).getId();

    // when
    // 1. upStation 기준 중간역 추가
    RestAssured.given().log().all()
      .body(new LineSectionAppendRequest(종합운동장_id, 잠실새내_id, 10))
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/lines/{lineId}/sections", 이호선_id)
      .then().log().all()
      .extract();

    // 2. downStation 기준 중간역 추가
    RestAssured.given().log().all()
      .body(new LineSectionAppendRequest(종합운동장_id, 잠실새내_id, 10))
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/lines/{lineId}/sections", 이호선_id)
      .then().log().all()
      .extract();

    // then
    ExtractableResponse<Response> result = RestAssured.given().log().all()
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().get("/lines/1")
      .then().log().all()
      .extract();

    Assertions.assertThat(result.jsonPath().getList("stations", StationResponse.class)).hasSize(5)
      .extracting("name")
      .contains(
        "종합운동장",
        "잠실새내",
        "새로운 상행역",
        "새로운 하행역",
        "잠실"
      );
  }
}
