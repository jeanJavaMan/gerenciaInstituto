/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller;

import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.componentes.EasyFXFunctions;
import br.jeanderson.control.ControlWindow;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;
import java.util.List;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.StageStyle;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.Aluno;
import jeanderson.model.Aluno_;
import jeanderson.model.ExecutaOperacoes;
import jeanderson.model.Frequencia;
import jeanderson.model.Frequencia_;
import jeanderson.model.Turma;
import jeanderson.model.Turma_;
import jeanderson.util.HibernateUtil;
import jeanderson.util.ResultadoBD;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 * FXML Controller class
 *
 * @author jeand
 */
@DefineConfiguration(url_fxml = "/jeanderson/view/ExecutaOperacoes.fxml", title = "Aguarde...", resizable = false, stageStyle = StageStyle.UNDECORATED)
public class ExecutaOperacoesController extends EasyFXFunctions {

    @FXML
    private ImageView ivResultado;
    @FXML
    private JFXSpinner spCarregando;
    @FXML
    private Text txtStatus;
    @FXML
    private JFXButton btnFechar;
    private ControlWindow<ExecutaOperacoesController> janela;
    @FXML
    private Text txtMsg;
    private String totalOperacao;
    private ExecutaOperacoes executaOperacoes;

    @FXML
    private void actionFechar(ActionEvent event) {
        this.janela.close();
    }

    @Override
    public void afterConstruct(ControlWindow control) {
        this.janela = control;
    }

    public void receberOperacao(ExecutaOperacoes executaOperacoes) {
        this.executaOperacoes = executaOperacoes;
    }

    @Override
    public void afterShow() {
        Thread t = new Thread(this::executarOperacao);
        t.setDaemon(true);
        t.start();
    }

    private void executarOperacao() {
        this.totalOperacao = "6";
        this.txtStatus.setText("1/" + totalOperacao);
        List<Turma> turmas = procurarTurmaComAlunosConcluidos();
        if (turmas.size() > 0) {
            txtStatus.setText("3/" + totalOperacao);
            Session sessao = HibernateUtil.getSession();
            CriteriaBuilder cb = sessao.getCriteriaBuilder();
            CriteriaQuery<Frequencia> cq = cb.createQuery(Frequencia.class);
            Root<Frequencia> from = cq.from(Frequencia.class);
            Predicate condicao = cb.equal(from.get(Frequencia_.porcentagemConcluida), 100);
            txtStatus.setText("4/" + totalOperacao);
            Query<Frequencia> query = sessao.createQuery(cq.select(from).where(condicao));
            List<Frequencia> frequencias = query.getResultList();
            sessao.close();
            txtStatus.setText("5/" + totalOperacao);
            if (frequencias.size() > 0) {
                boolean operacaoComErro = false;
                for (Frequencia frequencia : frequencias) {
                    for (Turma turma : turmas) {
                        if (turma.getCurso().getId().equals(frequencia.getCurso().getId())) {
                            ResultadoBD result = BancoDeDados.turma().removeAluno(frequencia.getAluno(), turma);
                            if (!result.resultado()) {
                                operacaoComErro = true;
                                break;
                            }
                        }

                    }
                    if (operacaoComErro) {
                        break;
                    }
                }
                if (operacaoComErro) {
                    Platform.runLater(() -> {
                        ivResultado.setImage(new Image("/jeanderson/assets/img/error-icon.png"));
                        spCarregando.setVisible(false);
                        txtMsg.setText("Houve um erro na execução da operação! Contate o desenvolvedor.");
                        btnFechar.setVisible(true);
                        executaOperacoes.operacaoExecutada();
                    });
                } else {
                    Platform.runLater(() -> {
                        txtStatus.setText("6/" + totalOperacao);
                        ivResultado.setImage(new Image("/jeanderson/assets/img/success.png"));
                        spCarregando.setVisible(false);
                        txtMsg.setText("Operação executada com sucesso! Não esqueça de olhar o que mudou em Notas da versão!");
                        btnFechar.setVisible(true);
                        executaOperacoes.operacaoExecutada();
                    });
                }
            }
        }
    }

    private List<Turma> procurarTurmaComAlunosConcluidos() {

        Session sessao = HibernateUtil.getSession();
        CriteriaBuilder cb = sessao.getCriteriaBuilder();
        CriteriaQuery<Turma> cq = cb.createQuery(Turma.class);
        Root<Turma> from = cq.from(Turma.class);
        Root<Frequencia> fromFrequencia = cq.from(Frequencia.class);
        Join<Turma, Aluno> joinTurmaComAluno = from.join(Turma_.alunos);
        Predicate condicao = cb.equal(joinTurmaComAluno.get(Aluno_.id), fromFrequencia.get(Frequencia_.aluno));
        condicao = cb.and(condicao, cb.equal(fromFrequencia.get(Frequencia_.porcentagemConcluida), 100));
        txtStatus.setText("2/" + totalOperacao);
        Query<Turma> query = sessao.createQuery(cq.select(from).where(condicao));
        List<Turma> turmas = query.getResultList();
        sessao.close();
        return turmas;
    }

}
