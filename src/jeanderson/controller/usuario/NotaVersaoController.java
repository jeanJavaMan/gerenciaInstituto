/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller.usuario;

import br.jeanderson.annotations.DefineConfiguration;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import jeanderson.application.InfoApplication;

/**
 * FXML Controller class
 *
 * @author jeand
 */
@DefineConfiguration(url_fxml = "/jeanderson/view/usuario/NotaVersao.fxml", title = "Informações da Versão")
public class NotaVersaoController implements Initializable {

    @FXML
    private Label txtVersao;
    @FXML
    private TextArea txtNotaVersao;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtVersao.setText(InfoApplication.VERSION);
        String texto = "Versão 1.9.4\n"
                + "*Erro Lembretes visivel para todos os funcionários foi corrigido, somente o funcionario pode ver.\n"
                + "*Opção para adicionar uma observação de aluno como lembrete.\n"
                + "*Janela de novidade implementada para saber o que de mais importante mudou.\n"
                + "*Pequenas melhorias de código pensando em desempenho.\n"
                + "Versão 1.9.3\n"
                + "*Bug ao tentar apagar lembrete referente aos contatos foi resolvido.\n"
                + "*3 novos tipos de relatórios adicionados (Mensalidade,Turma e Caixa).\n"
                + "*Novo design para  janela de Alunos Matrículados.\n"
                + "Versão 1.9.2\n"
                + "*Relatórios de erro são gerados com mais detalhes para ajudar na correção de bugs.\n"
                + "*Relatório de erro é enviado para o desenvolvedor a cada 3 dias(se houver relatório).\n"
                + "\nVersão 1.9.1\n"
                + "*CPF e RG são verificados na hora do digito.\n"
                + "*Nova opção disponível Gerar Parcela! Agora é possível gerar parcelas separadas da forma que quiser.\n"
                + "*Quando marcar a opção \"Ja paga\" referente a matrícula, a data de pagamento ficará com a data da geração da matricula.\n"
                + "*Em caso de um erro ocorrer ao gerar certar parcela da mensalidade, uma mensagem será exibida informando a partir de qual mensalidade o erro ocorreu. Obs: assim você poderá gerar as mensalidades restantes em Financeiro>Gerar Parcela.\n"
                + "\nVersão 1.9\n"
                + "*Ao concluir 100% da frequência o aluno será removido automáticamente da turma.\n"
                + "*Nova opção disponível em CADASTRO, agora é mais fácil matricular um aluno em outros cursos.\n"
                + "*Agora ao clicar sobre a lista de cursos, você poderá digitar os nomes do curso que será filtrado.\n"
                + "*Não é mais possível editar uma frequência, caso queria alterar algo deverá excluir e criar outra frequência.\n"
                + "*Verifica erros encontrados a cada 4 dias e envia para o desenvolvedor.\n"
                + "*Melhorias na atualização, será atualizado somente se os arquivos estarem totalmente integros, caso o contrário não será atualizado.\n"
                + "*Melhorias e erros corrigidos.\n"
                + "\nVersão 1.8\n"
                + "*São exibidas mensalidades a partir da data atual.\n"
                + "*Verifica Matrículas que não foram pagas a mais de 7 dias.\n"
                + "*Em contas a receber, mostra quantidade de Mensalidades vencidas e matrículas não pagas a mais de 7 dias.\n"
                + "*Relatório mostra o total de alunos por filtro.\n"
                + "*Frequência agora é organizada por porcentagem de conclusão.\n"
                + "*Melhorias de código.\n"
                + "*Botão para restaurar versão anterior fica na Tela principal.";
        txtNotaVersao.setText(texto);
    }

}
