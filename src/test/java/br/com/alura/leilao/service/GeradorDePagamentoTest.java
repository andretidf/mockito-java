package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Pagamento;
import br.com.alura.leilao.model.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class GeradorDePagamentoTest {

    @InjectMocks
    private GeradorDePagamento service;
    @Mock
    private PagamentoDao pagamentos;

    @Captor
    private ArgumentCaptor<Pagamento> captor;

    @Test
    void deveriaCriarPagamentoParaVencedorDoLeilao() {
        Lance lanceVencedor = leilao().getLanceVencedor();

        service.gerarPagamento(lanceVencedor);

        Mockito.verify(pagamentos).salvar(captor.capture());

        Pagamento pagamento = captor.getValue();

        Assertions.assertEquals(LocalDate.now().plusDays(1), pagamento.getVencimento());
        Assertions.assertEquals(lanceVencedor.getValor(), pagamento.getValor());
        Assertions.assertFalse(pagamento.getPago());
        Assertions.assertEquals(lanceVencedor.getUsuario(), pagamento.getUsuario());
    }


    private Leilao leilao() {

        Leilao leilao = new Leilao("Celular",
                new BigDecimal("500"),
                new Usuario("Fulano"));

        Lance primeiro = new Lance(new Usuario("Beltrano"),
                new BigDecimal("600"));
        Lance segundo = new Lance(new Usuario("Ciclano"),
                new BigDecimal("900"));

        leilao.propoe(primeiro);
        leilao.propoe(segundo);
        leilao.setLanceVencedor(segundo);

        return leilao;

    }
}