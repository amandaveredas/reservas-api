package io.github.cwireset.tcc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cwireset.tcc.domain.FormaPagamento;
import io.github.cwireset.tcc.domain.Periodo;
import io.github.cwireset.tcc.fixture.CadastrarReservaRequestFixture;
import io.github.cwireset.tcc.fixture.PeriodoFixture;
import io.github.cwireset.tcc.request.CadastrarReservaRequest;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReservaControllerIntegrationTest {

    private static final String BASE_PATH = "/reservas";
    private static final long ID_ANUNCIO_COM_HOTEL = 4;
    private static final long ID_ANUNCIO_COM_POUSADA = 5;
    private static final long ID_RESERVA_INEXISTENTE = 9999;
    private static final long ID_SOLICITANTE_EXISTENTE = 2; //Este ID possui 3 reservas
    private static final long ID_ANUNCIANTE_EXISTENTE = 1; //Este Id possui 6 anuncios
    private static final long ID_RESERVA_DINHEIRO_PIX_PENDENTE = 2;
    private static final long ID_RESERVA_PAGO = 8;
    private static final long ID_RESERVA_PENDENTE = 7;

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

    // 4.1. Realizar uma reserva

    @Test
    @Order(10)
    // Não deve ser possível reservar um imóvel com um período cuja data final seja menor que a data inicial
    void deveRetornarBadRequestAoTentarReservarImovelComPeriodoComDataFinalMenorQueDatainicial() {

        CadastrarReservaRequest requestComDataInvalida = CadastrarReservaRequestFixture
            .get()
            .valido()
            .comDataHoraFinal(LocalDateTime.now().minusDays(2))
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(requestComDataInvalida)
        .when()
            .post(BASE_PATH)
        .then()
            .assertThat()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", equalTo("Período inválido! A data final da reserva precisa ser maior do que a data inicial."));

    }

    // Não deve ser possível reservar um imóvel com um período cuja diferença entre a data final e inicial seja menor que 1 dia
    void deveRetornarBadRequestAoTentarReservarImovelComPeriodoInferiorAUmDia() {
        // FIXME Esse teste não será executado, pois com a alteração da regra para forçar hora de inicio e fim, nunca acontecerá.
        // A não ser que seja validado antes da alteração dos horários, porém não faria sentido
        // A validação fica apenas como referência

        Periodo periodoComMenosDeUmDiaDeDiferenca = PeriodoFixture
            .get()
            .valido()
            .comDataHoraFinal(LocalDateTime.now())
            .comDataHoraInicial(LocalDateTime.now())
            .build();

        CadastrarReservaRequest requestComDataInicialEFinalIguais = CadastrarReservaRequestFixture
            .get()
            .valido()
            .comPeriodo(periodoComMenosDeUmDiaDeDiferenca)
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(requestComDataInicialEFinalIguais)
        .when()
            .post(BASE_PATH)
        .then()
            .assertThat()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", equalTo("Período inválido! O número mínimo de diárias precisa ser maior ou igual à 1."));
    }

    @Test
    @Order(20)
    // Não deve ser possível reservar um imóvel cujo solicitante seja o mesmo anunciante
    void deveRetornarBadRequestAoTentarReservarImovelComSolicitanteIgualAoAnunciante() {
        CadastrarReservaRequest reservaComSolicitanteEAnuncianteIguais = CadastrarReservaRequestFixture
                .get()
                .valido()
                .comIdAnunciante(1)
                .comIdSolicitante(1)
                .build();

        given()
            .contentType(ContentType.JSON)
            .body(reservaComSolicitanteEAnuncianteIguais)
        .when()
            .post(BASE_PATH)
        .then()
            .assertThat()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", equalTo("O solicitante de uma reserva não pode ser o próprio anunciante."));
    }

    @Test
    @Order(40)
    // Caso a reserva seja de um Hotel, o número mínimo de pessoas é 2
    void deveRetornarBadRequestAoTentarReservarImovelDoTipoHotelComMenosDeDuasPessoas() {
        CadastrarReservaRequest reservaHotelComUmaPessoa = CadastrarReservaRequestFixture
            .get()
            .valido()
            .comIdAnunciante(ID_ANUNCIO_COM_HOTEL)
            .comQuantidadePessoas(1)
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(reservaHotelComUmaPessoa)
        .when()
            .post(BASE_PATH)
        .then()
            .assertThat()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", equalTo("Não é possivel realizar uma reserva com menos de 2 pessoas para imóveis do tipo Hotel"));
    }

    @Test
    @Order(50)
    // Caso a reserva seja de uma Pousada, o número mínimo de diárias é 5
    void deveRetornarBadRequestAoTentarReservarImovelDoTipoPousadaComMenosDeCincoDiarias() {
        Periodo periodoDeTresDias = PeriodoFixture
            .get()
            .comDataHoraInicial(LocalDateTime.of(2022,1,1,14,0,0))
            .comDataHoraFinal(LocalDateTime.of(2022,1,4,12,0,0))
            .build();

        CadastrarReservaRequest reservaComTresDiarias = CadastrarReservaRequestFixture
            .get()
            .valido()
            .comIdAnunciante(ID_ANUNCIO_COM_POUSADA)
            .comPeriodo(periodoDeTresDias)
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(reservaComTresDiarias)
        .when()
            .post(BASE_PATH)
        .then()
            .assertThat()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", equalTo("Não é possivel realizar uma reserva com menos de 5 diárias para imóveis do tipo Pousada"));
    }

    @Test
    @Order(60)
    void deveReservarImovelComSucesso() {
        Periodo periodo = PeriodoFixture
                .get()
                .comDataHoraInicial(LocalDateTime.of(2021, 11, 19, 14, 0))
                .comDataHoraFinal(LocalDateTime.of(2021, 11, 25, 12, 0))
                .build();

        CadastrarReservaRequest reservaValida = CadastrarReservaRequestFixture
            .get()
            .valido()
            .comPeriodo(periodo)
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(reservaValida)
        .when()
            .post(BASE_PATH)
        .then()
            .assertThat()
            .statusCode(HttpStatus.CREATED.value());

    }

    // R2 = 2021-11-23 > 2021-11-26 - Sobreposição da Inicial solicitada com a final existente
    @Test
    @Order(61)
    void deveRetornarBadRequestAoTentarReservarMesmoImovelComSobreposicaoNaDataInicial() {
        Periodo periodo = PeriodoFixture
            .get()
            .comDataHoraInicial(LocalDateTime.of(2021, 11, 23, 14, 0))
            .comDataHoraFinal(LocalDateTime.of(2021, 11, 26, 12, 0))
            .build();

        CadastrarReservaRequest reservaValida = CadastrarReservaRequestFixture
            .get()
            .valido()
            .comPeriodo(periodo)
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(reservaValida)
        .when()
            .post(BASE_PATH)
        .then()
            .assertThat()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", equalTo("Este anuncio já esta reservado para o período informado."));
    }

    // R3 = 2021-11-12 > 2021-11-20 - Sobreposição da final solicitada com a inicial existente
    @Test
    @Order(62)
    void deveRetornarBadRequestAoTentarReservarMesmoImovelComSobreposicaoNaDataFinal() {
        Periodo periodo = PeriodoFixture
            .get()
            .comDataHoraInicial(LocalDateTime.of(2021, 11, 12, 14, 0))
            .comDataHoraFinal(LocalDateTime.of(2021, 11, 20, 12, 0))
            .build();

        CadastrarReservaRequest reservaValida = CadastrarReservaRequestFixture
            .get()
            .valido()
            .comPeriodo(periodo)
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(reservaValida)
        .when()
            .post(BASE_PATH)
        .then()
            .assertThat()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", equalTo("Este anuncio já esta reservado para o período informado."));
    }

    // R4 = 2021-11-18 > 2021-11-26 - Ambas fora, sobreposição total com a existente
    @Test
    @Order(63)
    void deveRetornarBadRequestAoTentarReservarMesmoImovelComSobreposicaoExternaComExistente() {
        Periodo periodo = PeriodoFixture
            .get()
            .comDataHoraInicial(LocalDateTime.of(2021, 11, 18, 14, 0))
            .comDataHoraFinal(LocalDateTime.of(2021, 11, 26, 12, 0))
            .build();

        CadastrarReservaRequest reservaValida = CadastrarReservaRequestFixture
            .get()
            .valido()
            .comPeriodo(periodo)
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(reservaValida)
        .when()
            .post(BASE_PATH)
        .then()
            .assertThat()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", equalTo("Este anuncio já esta reservado para o período informado."));
    }

    // R5 = 2021-11-21 > 2021-11-24 - Ambas Dentro, sobreposição total com a existente
    @Test
    @Order(64)
    void deveRetornarBadRequestAoTentarReservarMesmoImovelComSobreposicaoInternaComExistente() {
        Periodo periodo = PeriodoFixture
            .get()
            .comDataHoraInicial(LocalDateTime.of(2021, 11, 21, 14, 0))
            .comDataHoraFinal(LocalDateTime.of(2021, 11, 24, 12, 0))
            .build();

        CadastrarReservaRequest reservaValida = CadastrarReservaRequestFixture
            .get()
            .valido()
            .comPeriodo(periodo)
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(reservaValida)
        .when()
            .post(BASE_PATH)
        .then()
            .assertThat()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", equalTo("Este anuncio já esta reservado para o período informado."));
    }

    // R6 = 2021-11-25 > 2021-11-26 - Sem sopreposição, mas mesma data inicial da existente
    @Test
    @Order(65)
    void deveReservarComSucessoComDataFinalIgualAInicialExistente() {
        Periodo periodo = PeriodoFixture
            .get()
            .comDataHoraInicial(LocalDateTime.of(2021, 11, 25, 14, 0))
            .comDataHoraFinal(LocalDateTime.of(2021, 11, 26, 12, 0))
            .build();

        CadastrarReservaRequest reservaValida = CadastrarReservaRequestFixture
            .get()
            .valido()
            .comPeriodo(periodo)
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(reservaValida)
        .when()
            .post(BASE_PATH)
        .then()
            .assertThat()
            .statusCode(HttpStatus.CREATED.value());
    }

    // R7 = 2021-11-18 > 2021-11-19 - Sem sobreposição, mas mesma data final da existente
    @Test
    @Order(66)
    void deveReservarComSucessoComDataInicialIgualAFinalExistente() {
        Periodo periodo = PeriodoFixture
            .get()
            .comDataHoraInicial(LocalDateTime.of(2021, 11, 18, 14, 0))
            .comDataHoraFinal(LocalDateTime.of(2021, 11, 19, 12, 0))
            .build();

        CadastrarReservaRequest reservaValida = CadastrarReservaRequestFixture
            .get()
            .valido()
            .comPeriodo(periodo)
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(reservaValida)
        .when()
            .post(BASE_PATH)
        .then()
            .assertThat()
            .statusCode(HttpStatus.CREATED.value());
    }

    // R8 = 2021-11-27 > 2021-11-29 - Ambas fora após a existente
    @Test
    @Order(66)
    void deveReservarComSucessoComDatasSuperioresAExistente() {
        Periodo periodo = PeriodoFixture
            .get()
            .comDataHoraInicial(LocalDateTime.of(2021, 11, 27, 14, 0))
            .comDataHoraFinal(LocalDateTime.of(2021, 11, 29, 12, 0))
            .build();

        CadastrarReservaRequest reservaValida = CadastrarReservaRequestFixture
            .get()
            .valido()
            .comPeriodo(periodo)
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(reservaValida)
        .when()
            .post(BASE_PATH)
        .then()
            .assertThat()
            .statusCode(HttpStatus.CREATED.value());
    }

    // R9 = 2021-11-15 > 2021-11-17 - Ambas fora antes da existente
    @Test
    @Order(67)
    void deveReservarComSucessoComDatasInferioresAExistente() {
        Periodo periodo = PeriodoFixture
            .get()
            .comDataHoraInicial(LocalDateTime.of(2021, 11, 15, 14, 0))
            .comDataHoraFinal(LocalDateTime.of(2021, 11, 17, 12, 0))
            .build();

        CadastrarReservaRequest reservaValida = CadastrarReservaRequestFixture
            .get()
            .valido()
            .comPeriodo(periodo)
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(reservaValida)
        .when()
            .post(BASE_PATH)
        .then()
            .assertThat()
            .statusCode(HttpStatus.CREATED.value());
    }

    // 4.2. Listar reservas de um solicitante específico

    @Test
    @Order(70)
    // O sistema deve retornar somente as reservas do solicitante informado
    // Listar as reservas com paginação, em ordem pela data do fim da reserva (valores maiores primeiro)
    void deveListarReservasDoSolicitanteOrdenadoPeloFimDaReservaComSucesso_Desafio() {

        given()
        .when()
            .get(BASE_PATH + "/solicitantes/" + ID_SOLICITANTE_EXISTENTE + "?dataHoraFinal=2021-03-11 12:00:00&dataHoraInicial=2021-01-10 12:00:00")
        .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body(containsString("content"))
            .body("content[0].periodo.dataHoraFinal", equalTo("2021-03-10 12:00:00"))
            .body("content[1].periodo.dataHoraFinal", equalTo("2021-01-30 12:00:00"));
    }

    @Test
    @Order(80)
    // Caso nenhuma período seja informado, o sistema deve retornar todas as reservas.
    void deveRetornarTodasAsReservasQuandoNaoInformadoPeriodo() {
        given()
        .when()
            .get(BASE_PATH + "/solicitantes/" + ID_SOLICITANTE_EXISTENTE)
        .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body(containsString("content"))
            .body("content[0].periodo.dataHoraFinal", equalTo("2021-03-10 12:00:00"))
            .body("content[1].periodo.dataHoraFinal", equalTo("2021-01-30 12:00:00"))
            .body("content[2].periodo.dataHoraFinal", equalTo("2021-01-06 12:00:00"));
    }

    @Test
    @Order(90)
    // Caso nenhuma período seja informado, o sistema deve retornar todas as reservas.
    void deveContabilizarOTotalDeTodasAsReservasQuandoNaoInformadoPeriodo() {
        given()
        .when()
            .get(BASE_PATH + "/solicitantes/" + ID_SOLICITANTE_EXISTENTE)
        .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body(containsString("content"))
            .body("totalElements", equalTo(3));
    }

    // 4.3. Listar reservas de um anunciante específico

    @Test
    @Order(100)
    // O sistema deve retornar somente as reservas do anunciante informado
    // Listar as reservas com paginação, em ordem pela dataHoraFinal (valores maiores primeiro)
    void deveListarReservasDeUmAnuncianteEspecificoOrdenadoPelaDaTaHoraFinalComSucesso_Desafio() {

        given()
        .when()
            .get(BASE_PATH + "/anuncios/anunciantes/" + ID_ANUNCIANTE_EXISTENTE)
        .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body(containsString("content"))
            .body("content[0].periodo.dataHoraFinal", equalTo("2021-03-10 12:00:00"))
            .body("content[1].periodo.dataHoraFinal", equalTo("2021-01-23 12:00:00"))
            .body("content[2].periodo.dataHoraFinal", equalTo("2021-01-20 12:00:00"))
            .body("content[3].periodo.dataHoraFinal", equalTo("2021-01-12 12:00:00"))
            .body("content[4].periodo.dataHoraFinal", equalTo("2021-01-12 12:00:00"))
            .body("content[5].periodo.dataHoraFinal", equalTo("2021-01-06 12:00:00"));
    }

    @Test
    @Order(110)
    // O sistema deve retornar somente as reservas do anunciante informado
    // Listar as reservas com paginação, em ordem pela dataHoraFinal (valores maiores primeiro)
    void deveContabilizarOTotalDeReservasDeUmAnuncianteEspecificoComSucesso() {

        given()
        .when()
            .get(BASE_PATH + "/anuncios/anunciantes/" + ID_ANUNCIANTE_EXISTENTE)
        .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body(containsString("content"))
            .body("totalElements", equalTo(6));
    }

    // 4.4. Pagar reserva

    @Test
    @Order(120)
    // A aplicação deve obter a Reserva através do Id informado.
    void deveRetornarNotFoundAoTentarPagarReservaComIdQueNaoExiste() throws JsonProcessingException {

        given()
            .contentType(ContentType.JSON)
            .body(new ObjectMapper().writeValueAsString(FormaPagamento.DINHEIRO))
        .when()
            .put(BASE_PATH + "/{idReserva}/pagamentos", ID_RESERVA_INEXISTENTE)
        .then()
            .assertThat()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("message", equalTo("Nenhum(a) Reserva com Id com o valor '9999' foi encontrado."));
    }

    @Test
    @Order(130)
    void deveRetornarBadRequestAoTentarPagarReservaComFormaPagamentoNaoAceita() throws JsonProcessingException {

        given()
            .contentType(ContentType.JSON)
            .body(new ObjectMapper().writeValueAsString(FormaPagamento.CARTAO_CREDITO))
        .when()
            .put(BASE_PATH + "/{idReserva}/pagamentos", ID_RESERVA_DINHEIRO_PIX_PENDENTE)
        .then()
            .assertThat()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", equalTo("O anúncio não aceita CARTAO_CREDITO como forma de pagamento. As formas aceitas são DINHEIRO, PIX."));
    }

    @Order(140)
    @ParameterizedTest(name = "Teste Parametrizado: {index} - Reserva de id {arguments}" )
    @CsvSource(value = {"8, Status PAGO", "9, Status ESTORNADO", "10, Status CANCELADO"})
        // Não deve ser possível realizar o pagamento de uma reserva paga, estornada ou cancelada
    void deveRetornarBadRequestAoTentarPagarReservaComStatusInvalido(Long idReserva, String statusNomeDisplayMetodo) throws JsonProcessingException { //o campo status foi utilizado apenas para formatar o nome do método na apresentação do console

        given()
            .contentType(ContentType.JSON)
            .body(new ObjectMapper().writeValueAsString(FormaPagamento.DINHEIRO))
        .when()
            .put(BASE_PATH + "/{idReserva}/pagamentos", idReserva)
        .then()
            .assertThat()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", equalTo("Não é possível realizar o pagamento para esta reserva, pois ela não está no status PENDENTE."));
    }

    @Test
    @Order(150)
    void devePagarReservaComSucesso() throws JsonProcessingException {
        given()
            .contentType(ContentType.JSON)
            .body(new ObjectMapper().writeValueAsString(FormaPagamento.CARTAO_CREDITO))
        .when()
            .put(BASE_PATH + "/{idReserva}/pagamentos", ID_RESERVA_PENDENTE)
        .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value());
    }

    // 4.5. Cancelar uma reserva

    @Test
    @Order(160)
    // A aplicação deve obter a Reserva através do Id informado.
    void deveRetornarNotFoundAoTentarCancelarReservaComIdQueNaoExiste() {
        given()
        .when()
            .put(BASE_PATH + "/{idReserva}/pagamentos/cancelar", ID_RESERVA_INEXISTENTE)
        .then()
            .assertThat()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("message", equalTo("Nenhum(a) Reserva com Id com o valor '9999' foi encontrado."));

    }

    @Order(170)
    @ParameterizedTest(name ="Teste Parametizado: {index} - Reserva de id {arguments}")
    @CsvSource(value = {"8, Status PAGO", "9, Status ESTORNADO", "10, Status CANCELADO" })
    // Não deve ser possível realizar o cancelamento de uma reserva paga, estornada, cancelada
    void deveRetornarBadRequestAoTentarCancelarReservaComStatusInvalido(Long idReserva, String statusNomeDisplayMetodo) { //o campo status foi utilizado apenas para formatar o nome do método na apresentação do console
        given()
        .when()
            .put(BASE_PATH + "/{idReserva}/pagamentos/cancelar", idReserva)
        .then()
            .assertThat()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", equalTo("Não é possível realizar o cancelamento para esta reserva, pois ela não está no status PENDENTE."));
    }

    @Test
    @Order(180)
    void deveCancelarReservaComSucesso() {
        given()
        .when()
            .put(BASE_PATH + "/{idReserva}/pagamentos/cancelar", ID_RESERVA_DINHEIRO_PIX_PENDENTE)
        .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value());
    }

    // 4.6. Estornar reserva

    @Test
    @Order(190)
    void deveRetornarNotFoundAoTentarEstornarReservaComIdQueNaoExiste() {
        given()
        .when()
            .put(BASE_PATH + "/{idReserva}/pagamentos/estornar", ID_RESERVA_INEXISTENTE)
        .then()
            .assertThat()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("message", equalTo("Nenhum(a) Reserva com Id com o valor '9999' foi encontrado."));
    }

    @Order(200)
    @ParameterizedTest(name ="Teste Parametizado: {index} - Reserva de id {arguments}")
    @CsvSource(value = {"6, Status PENDENTE, ", "9, Status ESTORNADO" ,"10, Status CANCELADO"})
    // Não deve ser possível estornar o pagamento de uma reserva pendente, estornada, cancelada
    void deveRetornarBadRequestAoTentarEstornarReservaComStatusInvalido(Long idReserva, String statusNomeDisplayMetodo) { //o campo status foi utilizado apenas para formatar o nome do método na apresentação do console
        given()
        .when()
            .put(BASE_PATH + "/{idReserva}/pagamentos/estornar", idReserva)
        .then()
            .assertThat()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", equalTo("Não é possível realizar o estorno para esta reserva, pois ela não está no status PAGO."));
    }

    @Test
    @Order(210)
    void deveEstornarReservaComSucesso() {
        given()
        .when()
            .put(BASE_PATH + "/{idReserva}/pagamentos/estornar", ID_RESERVA_PAGO)
        .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value());
    }
}