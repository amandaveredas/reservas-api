package io.github.cwireset.tcc.controller;

import io.github.cwireset.tcc.fixture.CadastrarAnuncioRequestFixture;
import io.github.cwireset.tcc.request.CadastrarAnuncioRequest;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AnuncioControllerIntegrationTest {

    private static final String BASE_PATH = "/anuncios";
    private static final int ID_ANUNCIANTE_DOIS = 2;

    @LocalServerPort
    int port;

    @BeforeAll
    public void setUpRestAssuredFilters() {
//        RestAssured.replaceFiltersWith(new RequestLoggingFilter(), new ResponseLoggingFilter());
        RestAssured.replaceFiltersWith(new AllureRestAssured());
    }

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    // 3.2. Listar anúncios

    @Test
    @Order(10)
    void deveListarAnunciosComPaginacaoComSucesso() {
        given()
        .when()
            .get(BASE_PATH)
        .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body(containsString("content"))
            .body("totalElements", equalTo(10));
    }

    @Test
    @Order(20)
    void deveListarAnunciosComPaginacaoOrdenadoPeloValorDaDiariaComSucesso_Desafio() {
        given()
        .when()
            .get(BASE_PATH)
        .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body(containsString("content"))
            .body("content[0].valorDiaria", equalTo(80.0F))  //Valor anúncio 8
            .body("content[1].valorDiaria", equalTo(120.0F)) //Valor anúncio 3
            .body("content[2].valorDiaria", equalTo(120.0F)) //Valor anúncio 10
            .body("content[3].valorDiaria", equalTo(250.0F)) //Valor anúncio 2
            .body("content[4].valorDiaria", equalTo(310.0F));//Valor anúncio 4
    }

    @Test
    @Order(21)
    void deveListarAnunciosSemContabilizarRegistrosExcluidos() {
        given()
                .when()
                .get(BASE_PATH)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(containsString("content"))
                .body("totalElements", equalTo(10));
    }

    // 3.3. Listar anúncios de um anunciante específico

    @Test
    @Order(30)
    void deveListarAnunciosDeUmAnuncianteComPaginacaoComSucesso() {
        given()
        .when()
            .get(BASE_PATH + "/anunciantes/{idAnunciante}", ID_ANUNCIANTE_DOIS )
        .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body(containsString("content"))
            .body("totalElements", equalTo(7));
    }

    @Test
    @Order(40)
    void deveListarAnunciosDeUmAnuncianteComPaginacaoOrdenadoPeloValorDaDiariaComSucesso_Desafio() {
        given()
        .when()
            .get(BASE_PATH + "/anunciantes/{idAnunciante}", ID_ANUNCIANTE_DOIS )
        .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body(containsString("content"))
            .body("content[0].valorDiaria", equalTo(80.0F))  //Valor anúncio 8
            .body("content[1].valorDiaria", equalTo(120.0F)) //Valor anúncio 3
            .body("content[2].valorDiaria", equalTo(120.0F)) //Valor anúncio 10
            .body("content[3].valorDiaria", equalTo(365.0F)) //Valor anúncio 6
            .body("content[4].valorDiaria", equalTo(420.0F)) //Valor anúncio 5
            .body("content[5].valorDiaria", equalTo(680.0F)) //Valor anúncio 9
            .body("content[6].valorDiaria", equalTo(880.0F));//Valor anúncio 7
    }

    @Test
    @Order(41)
    void deveListarAnunciosDeUmAnuncianteSemContabilizarRegistrosExcluidos() {
        given()
                .when()
                .get(BASE_PATH + "/anunciantes/{idAnunciante}", ID_ANUNCIANTE_DOIS )
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(containsString("content"))
                .body("totalElements", equalTo(7)); // existem 8 registros para o anunciante 2 no import.sql, sendo 1 com excluido true
    }

    // 3.1 Anunciar imóvel

    @Test
    @Order(50)
    void deveRetornarNotFoundAoTentarAnunciarComIdAnuncianteQueNaoExiste() {
        CadastrarAnuncioRequest requestComIdAnuncianteInexistente = CadastrarAnuncioRequestFixture
            .get()
            .valido()
            .comIdAnunciante(999L)
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(requestComIdAnuncianteInexistente)
        .when()
            .post(BASE_PATH)
        .then()
            .assertThat()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("message", equalTo("Nenhum(a) Usuario com Id com o valor '999' foi encontrado."));

    }

    @Test
    @Order(60)
    void deveRetornarNotFoundAoTentarAnunciarComIdImovelQueNaoExiste() {
        CadastrarAnuncioRequest anuncioRequestComIdImovelInexistente = CadastrarAnuncioRequestFixture
            .get()
            .valido()
            .comIdImovel(999L)
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(anuncioRequestComIdImovelInexistente)
        .when()
            .post(BASE_PATH)
        .then()
            .assertThat()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("message",equalTo("Nenhum(a) Imovel com Id com o valor '999' foi encontrado."));
    }

    @Test
    @Order(70)
    void deveRetornarBadRequestAoTentarAnunciarSemCamposObrigatorios() {
        CadastrarAnuncioRequest anuncioRequestSemCamposObrigatorios = CadastrarAnuncioRequestFixture
            .get()
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(anuncioRequestSemCamposObrigatorios)
        .when()
            .post(BASE_PATH)
        .then()
            .assertThat()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Order(80)
    void deveRetornarBadRequestAoTentarCriarOutroAnuncioParaImovelJaAnunciado() {
        CadastrarAnuncioRequest anuncioRequestComIdImovelInexistente = CadastrarAnuncioRequestFixture
            .get()
            .valido()
            .comIdImovel(1L)
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(anuncioRequestComIdImovelInexistente)
        .when()
            .post(BASE_PATH)
        .then()
            .assertThat()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message",equalTo("Já existe um recurso do tipo Anuncio com IdImovel com o valor '1'."));
    }

    @Test
    @Order(81)
    void deveRetornarOKAoCriarOutroAnuncioParaImovelJaAnunciadoAnteriormentePoremCujoAnuncioFoiExcluido() {
        CadastrarAnuncioRequest anuncioRequestComMesmoIdImovelDeAnuncioExcluido = CadastrarAnuncioRequestFixture
                .get()
                .valido()
                .comIdImovel(12L)
                .build();

        given()
            .contentType(ContentType.JSON)
            .body(anuncioRequestComMesmoIdImovelDeAnuncioExcluido)
        .when()
            .post(BASE_PATH)
        .then()
            .assertThat()
            .statusCode(HttpStatus.CREATED.value())
            .body("id", notNullValue());
    }

    @Test
    @Order(90)
    void deveCadastrarAnuncioComSucesso() {
        CadastrarAnuncioRequest anuncioRequestComMesmoIdImovelDeAnuncioExcluido = CadastrarAnuncioRequestFixture
                .get()
                .valido()
                .comIdImovel(11L)
                .build();

        given()
            .contentType(ContentType.JSON)
            .body(anuncioRequestComMesmoIdImovelDeAnuncioExcluido)
        .when()
            .post(BASE_PATH)
        .then()
            .assertThat()
            .statusCode(HttpStatus.CREATED.value())
            .body("id", notNullValue());
    }

    // 3.4. Excluir um anúncio

    @Test
    @Order(100)
    void deveRetornarNotFoundAoExcluirAnuncioComIdQueNaoExiste() {

        given()
        .when()
            .delete(BASE_PATH + "/{id}", 999 )
        .then()
            .assertThat()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("message",equalTo("Nenhum(a) Anuncio com Id com o valor '999' foi encontrado."));
    }

    @Test
    @Order(110)
    void deveExcluirAnuncioComSucesso() {
        given()
        .when()
            .delete(BASE_PATH + "/{id}", 3 )
        .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value());
    }

}