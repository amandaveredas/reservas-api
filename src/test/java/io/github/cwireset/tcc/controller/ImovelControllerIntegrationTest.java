package io.github.cwireset.tcc.controller;

import io.github.cwireset.tcc.domain.Endereco;
import io.github.cwireset.tcc.fixture.CadastrarImovelRequestFixture;
import io.github.cwireset.tcc.fixture.EnderecoFixture;
import io.github.cwireset.tcc.request.CadastrarImovelRequest;
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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ImovelControllerIntegrationTest {

    private static final String BASE_PATH = "/imoveis";
    private static final Integer ID_IMOVEL_CRIADO = 13; // Existem 12 no import. Logo o próximo deve ser o 13
    private static final Integer ID_PROPRIETARIO_COM_SEIS_IMOVEIS = 4;
    private static final Integer ID_IMOVEL_COM_ANUNCIO = 1;

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

    // 2.2. Listar imóveis

    @Test
    @Order(1)
    void deveListarImoveisComPaginacaoComSucesso() {
        given()
        .when()
            .get(BASE_PATH)
        .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body(containsString("content"))
            .body("totalElements", equalTo(12)); // Quantidade de imóveis cadastrados no import.sql
    }

    @Test
    @Order(2)
    void deveListarImoveisComPaginacaoComSucessoOrdenadoPeloCampoIdentificacao_Desafio(){
        given()
        .when()
            .get(BASE_PATH)
        .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body(containsString("content"))
            .body("content[0].identificacao", equalTo("AP Mobiliado"))
            .body("content[1].identificacao", equalTo("AP Mobiliado"))
            .body("content[2].identificacao", equalTo("AP Mobiliado"))
            .body("content[3].identificacao", equalTo("Casa ampla para familia"))
            .body("content[4].identificacao", equalTo("Casa grande perto da praia"));
    }

    @Test
    @Order(3)
    void deveListarImoveisDeUmProprietarioComPaginacaoComSucesso() {
        given()
        .when()
            .get(BASE_PATH + "/proprietarios/{idProprietario}", ID_PROPRIETARIO_COM_SEIS_IMOVEIS)
        .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body(containsString("content"))
            .body("totalElements", equalTo(6));
    }

    @Test
    @Order(4)
    void deveListarImoveisDeUmProprietarioComPaginacaoComSucessoOrdenadoPeloCampoIdentificacao_Desafio(){
        given()
            .when()
            .get(BASE_PATH + "/proprietarios/{idProprietario}", ID_PROPRIETARIO_COM_SEIS_IMOVEIS)
            .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body(containsString("content"))
            .body("content[0].identificacao", equalTo("AP Mobiliado"))
            .body("content[1].identificacao", equalTo("AP Mobiliado"))
            .body("content[2].identificacao", equalTo("Casa ampla para familia"))
            .body("content[3].identificacao", equalTo("Quarto amplo em hotel"));
    }

    // 2.1. Cadastro de Imóvel

    @Test
    @Order(5)
    void deveRetornarBadRequestAoTentarCriarImovelSemCamposObrigatorios() {
        CadastrarImovelRequest cadastrarImovelRequest = CadastrarImovelRequestFixture.get()
                .semCamposObrigatorios()
                .build();

        given()
            .contentType(ContentType.JSON)
            .body(cadastrarImovelRequest)
        .when()
            .post(BASE_PATH)
        .then()
            .assertThat()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", equalTo("Validation failed for object='cadastrarImovelRequest'. Error count: 4"));
    }

    @Test
    @Order(6)
    void deveRetornarBadRequestAoTentarCriarImovelComCepEmFormatoInvalido() {
        Endereco enderecoComCepInvalido = EnderecoFixture.get()
                .valido()
                .comCep("99999999")
                .build();
        CadastrarImovelRequest cadastrarImovelRequest = CadastrarImovelRequestFixture.get()
                .valido()
                .comEndereco(enderecoComCepInvalido)
                .build();

        given()
            .contentType(ContentType.JSON)
            .body(cadastrarImovelRequest)
        .when()
            .post(BASE_PATH)
        .then()
            .assertThat()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("errors[0].defaultMessage", equalTo("O CEP deve ser informado no formato 99999-999."));
    }

    @Test
    @Order(7)
    void deveRetornarNotFoundAoTentarCriarImovelComIdProprietarioQueNaoExiste() {
        CadastrarImovelRequest cadastrarImovelRequest = CadastrarImovelRequestFixture.get()
                .valido()
                .comIdProprietario(999L)
                .build();

        given()
            .contentType(ContentType.JSON)
            .body(cadastrarImovelRequest)
        .when()
            .post(BASE_PATH)
        .then()
            .assertThat()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("message", equalTo("Nenhum(a) Usuario com Id com o valor '999' foi encontrado."));
    }

    @Test
    @Order(8)
    void deveCriarImovelComSucesso() {
        CadastrarImovelRequest cadastrarImovelRequest = CadastrarImovelRequestFixture.get()
                .valido()
                .build();

        given()
            .contentType(ContentType.JSON)
            .body(cadastrarImovelRequest)
        .when()
            .post(BASE_PATH)
        .then()
            .assertThat()
            .statusCode(HttpStatus.CREATED.value())
            .body("id", equalTo(ID_IMOVEL_CRIADO));
    }

    // 2.4. Buscar um imóvel por id

    @Test
    @Order(9)
    void deveRetornarNotFoundAoBuscarImovelPorIdQueNaoExiste() {
        given()
        .when()
            .get(BASE_PATH + "/{idImovel}", 999)
        .then()
            .assertThat()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("message", equalTo("Nenhum(a) Imovel com Id com o valor '999' foi encontrado."));
    }

    @Test
    @Order(10)
    void deveBuscarImovelPorIdComSucesso() {
        CadastrarImovelRequest cadastrarImovelRequest = CadastrarImovelRequestFixture.get().valido().build();

        given()
        .when()
            .get(BASE_PATH + "/{idImovel}", ID_IMOVEL_CRIADO)
        .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("id", equalTo(ID_IMOVEL_CRIADO))
            .body("tipoImovel", equalTo(cadastrarImovelRequest.getTipoImovel().name()))
            .body("identificacao", equalTo(cadastrarImovelRequest.getIdentificacao()));
    }

    // 2.5. Excluir um imóvel

    @Test
    @Order(11)
    void deveRetornarNotFoundAoExcluirImovelComIdQueNaoExiste() {
        given()
        .when()
            .delete(BASE_PATH + "/{idImovel}", 999)
        .then()
            .assertThat()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("message", equalTo("Nenhum(a) Imovel com Id com o valor '999' foi encontrado."));
    }

    @Test
    @Order(12)
    void deveRetornarBadRequestAoTentarExcluirImovelQuePossuaUmAnuncio() {
        given()
        .when()
            .delete(BASE_PATH + "/{idImovel}", ID_IMOVEL_COM_ANUNCIO)
        .then()
            .assertThat()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", equalTo("Não é possível excluir um imóvel que possua um anúncio."));
    }

    @Test
    @Order(13)
    void deveExcluirImovelComSucesso() {
        given()
        .when()
            .delete(BASE_PATH + "/{idImovel}", ID_IMOVEL_CRIADO)
        .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value());
    }

    @Test
    @Order(14)
    void deveRetornarNotFoundAoExcluirImovelQueFoiExcluidoAcima(){
        given()
            .when()
            .delete(BASE_PATH + "/{idImovel}", ID_IMOVEL_CRIADO)
            .then()
            .assertThat()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("message", equalTo("Nenhum(a) Imovel com Id com o valor '" + ID_IMOVEL_CRIADO + "' foi encontrado."));
    }

}