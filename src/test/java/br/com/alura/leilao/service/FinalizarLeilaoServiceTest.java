package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class FinalizarLeilaoServiceTest {

    @InjectMocks
    private FinalizarLeilaoService service;

    @Mock
    private LeilaoDao leilaoDao;

    @Mock
    private EnviadorDeEmails enviadorDeEmails;



    @Test
    void deveriaFinalizarUmLeilao() {
        List<Leilao> list = leiloes();
        Leilao leilao = list.get(0);
        Mockito.when(leilaoDao.buscarLeiloesExpirados()).thenReturn(list);
        service.finalizarLeiloesExpirados();


        Assertions.assertTrue(leilao.isFechado());
        Assertions.assertEquals(new BigDecimal("900"), leilao.getLanceVencedor().getValor());

        Mockito.verify(leilaoDao).salvar(leilao);

    }
    @Test
    void deveriaEnviarEmailParaVencedorDoLeilao(){
        List<Leilao> list = leiloes();
        Leilao leilao = list.get(0);
        Mockito.when(leilaoDao.buscarLeiloesExpirados()).thenReturn(list);
        service.finalizarLeiloesExpirados();

        Lance lanceVencedor = leilao.getLanceVencedor();



        Mockito.verify(enviadorDeEmails).enviarEmailVencedorLeilao(lanceVencedor);
    }

    @Test
    void naoDeveriaEnviarEmailParaVencedorEmCasoDeErroNoLeilao(){
        List<Leilao> list = leiloes();
        Mockito.when(leilaoDao.buscarLeiloesExpirados()).thenReturn(list);
        Mockito.when(leilaoDao.salvar(Mockito.any())).thenThrow(RuntimeException.class);


        try {
            service.finalizarLeiloesExpirados();
            Mockito.verifyNoInteractions(enviadorDeEmails);
        }catch (Exception ex){}

    }


    // Trecho de c??digo omitido

    private List<Leilao> leiloes() {
        List<Leilao> lista = new ArrayList<>();

        Leilao leilao = new Leilao("Celular",
                new BigDecimal("500"),
                new Usuario("Fulano"));

        Lance primeiro = new Lance(new Usuario("Beltrano"),
                new BigDecimal("600"));
        Lance segundo = new Lance(new Usuario("Ciclano"),
                new BigDecimal("900"));

        leilao.propoe(primeiro);
        leilao.propoe(segundo);

        lista.add(leilao);

        return lista;

    }
}