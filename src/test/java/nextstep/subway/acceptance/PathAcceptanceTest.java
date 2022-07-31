package nextstep.subway.acceptance;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.경로조회_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 경로 검색 인수 테스트")
public class PathAcceptanceTest extends AcceptanceTest {
	private Long 교대역;
	private Long 강남역;
	private Long 양재역;
	private Long 남부터미널역;
	private Long 이호선;
	private Long 신분당선;
	private Long 삼호선;

	/**
	 *              10
	 * 교대역    --- *2호선* ---   강남역
	 * |                        |
	 * *3호선* 2                 *신분당선*  10
	 * |                        |
	 * 남부터미널역  --- *3호선* ---   양재
	 *                  3
	 *
	 *             5
	 * 서현역 --- *분당선* --- 이매역
	 */
	@BeforeEach
	public void setUp() {
		super.setUp();
		교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
		강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
		양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
		남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

		이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10);
		신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10);
		삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2);

		지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
	}

	/**
	 * given 노선과 구간이 생성되어 있음
	 * when 경로 찾기 요청(남부터미널 -> 강남역)
	 * then 남부터미널 -> 교대 -> 강남 의 경로가 조회됨
	 */
	@DisplayName("경로 탐색 테스트(남부터미널 -> 강남역)")
	@Test
	void searchShortestPath() {

		//given //when
		ExtractableResponse<Response> result = 경로조회_요청(남부터미널역, 강남역);

		//then
		assertAll(
			() -> assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(result.jsonPath().getList("stations.id")).hasSize(3),
			() -> assertThat(result.jsonPath().getList("stations.id", Long.class)).containsExactly(남부터미널역, 교대역, 강남역),
			() -> assertThat(result.jsonPath().getInt("distance")).isEqualTo(12)
		);
	}

	/**
	 * given 노선과 구간이 생성되어 있음
	 * when 경로 찾기 요청(남부터미널 -> 남부터미널)
	 * then 에러가 발생함
	 */
	@DisplayName("출발역과 도착역이 같은 경우 에러 발생 테스트")
	@Test
	void exceptionSameSourceAndTarget() {

		//given //when
		ExtractableResponse<Response> result = 경로조회_요청(남부터미널역, 남부터미널역);

		//then
		assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	/**
	 * given 노선과 구간이 생성되어 있음 and 위 약도에 추가로 분당선 구간이 생성되어 있음
	 * when 경로 찾기 요청(양재역 -> 서현역 *연결되어 있지 않음*)
	 * then 에러가 발생함
	 */
	@DisplayName("출발역과 도착역이 연결되어있지 않은 경우 에러 발생")
	@Test
	void exceptionNotConnectedStation() {

		//given
		Long 서현역 = 지하철역_생성_요청("서현역").jsonPath().getLong("id");
		Long 이매역 = 지하철역_생성_요청("이매역").jsonPath().getLong("id");
		지하철_노선_생성_요청("분당선", "yellow", 서현역, 이매역, 5);
		ExtractableResponse<Response> result = 경로조회_요청(양재역, 이매역);

		//then
		assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

	}

	/**
	 * given 노선과 구간이 생성되어 있음
	 * when 경로 찾기 요청(서현역(10L)*존재하지 않음* -> 남부터미널)
	 * then 에러가 발생함
	 */
	@DisplayName("존재하지 않는 역으로 조회시 에러 발생")
	@Test
	void exceptionNotExistStation() {

		//given //when
		ExtractableResponse<Response> result = 경로조회_요청(10L, 남부터미널역);

		//then
		assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
		Map<String, String> params = new HashMap<>();
		params.put("upStationId", upStationId + "");
		params.put("downStationId", downStationId + "");
		params.put("distance", distance + "");
		return params;
	}
}