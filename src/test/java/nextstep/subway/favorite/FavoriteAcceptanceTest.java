package nextstep.subway.favorite;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.auth.acceptance.AuthAcceptanceTest;
import nextstep.subway.auth.domain.LoginMember;
import nextstep.subway.auth.dto.TokenRequest;
import nextstep.subway.auth.dto.TokenResponse;
import nextstep.subway.favorite.dto.FavoriteRequest;
import nextstep.subway.line.acceptance.LineAcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.member.MemberAcceptanceTest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("즐겨찾기 관련 기능")
public class FavoriteAcceptanceTest extends AcceptanceTest {
    public static final String EMAIL = "test@email.com";
    public static final String PASSWORD = "1111";
    public static final int AGE = 40;
    private LineResponse 신분당선;
    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 정자역;
    private StationResponse 광교역;
    private LoginMember 사용자;
    private String uri = "/favorites";

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역").as(StationResponse.class);
        양재역 = StationAcceptanceTest.지하철역_등록되어_있음("양재역").as(StationResponse.class);
        정자역 = StationAcceptanceTest.지하철역_등록되어_있음("정자역").as(StationResponse.class);
        광교역 = StationAcceptanceTest.지하철역_등록되어_있음("광교역").as(StationResponse.class);

        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 10);
        신분당선 = LineAcceptanceTest.지하철_노선_등록되어_있음(lineRequest).as(LineResponse.class);

        MemberAcceptanceTest.회원_생성을_요청(EMAIL, PASSWORD, AGE);
        TokenRequest tokenRequest = new TokenRequest(EMAIL, PASSWORD);
        TokenResponse userToken = AuthAcceptanceTest.로그인_토큰_생성(tokenRequest).as(TokenResponse.class);
        사용자 = AuthAcceptanceTest.로그인_토큰_유효체크(userToken.getAccessToken());
    }

    @Test
    @DisplayName("즐겨찾기를 관리한다.")
    void manageMember() {
        //when
        ExtractableResponse<Response> createResponse = 즐겨찾기_생성_요청(사용자, 강남역, 정자역);
        //then
        즐겨찾기_생성됨(createResponse);

        //when
        ExtractableResponse<Response> findResponse = 즐겨찾기_목록_조회_요청();
        //then
        즐겨찾기_목록_조회됨(findResponse);

        String uri = createResponse.header("Location");
        //when
        ExtractableResponse<Response> deleteResponse = 즐겨찾기_삭제_요청(uri);
        //then
        즐겨찾기_삭제됨(deleteResponse);
    }

    private void 즐겨찾기_삭제됨(ExtractableResponse<Response> deleteResponse) {
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 즐겨찾기_삭제_요청(String uri) {
        return RestAssured
                .given()
                .when()
                .delete(uri)
                .then()
                .extract();
    }

    private ExtractableResponse<Response> 즐겨찾기_생성_요청(LoginMember user, StationResponse source, StationResponse target) {
        FavoriteRequest params = new FavoriteRequest(user, source.getId(), target.getId());

        return RestAssured
                .given()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(uri)
                .then()
                .extract();
    }

    private ExtractableResponse<Response> 즐겨찾기_목록_조회_요청() {
        return RestAssured
                .given()
                .when()
                .get(uri)
                .then()
                .extract();
    }

    private void 즐겨찾기_생성됨(ExtractableResponse<Response> createResponse) {
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 즐겨찾기_목록_조회됨(ExtractableResponse<Response> findResponse) {
        assertThat(findResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

}