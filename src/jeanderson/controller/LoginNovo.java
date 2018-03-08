/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.controller;

import admin.Admin;
import admin.classesxml.Aplicacao;
import admin.classesxml.ConfigApplication;
import br.jeanderson.annotations.DefineConfiguration;
import br.jeanderson.annotations.ValidateField;
import br.jeanderson.componentes.EasyFXFunctions;
import br.jeanderson.control.ControlWindow;
import br.jeanderson.enums.DialogType;
import br.jeanderson.util.DialogFX;
import br.jeanderson.util.FunctionAnnotations;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import jeanderson.application.InfoApplication;
import jeanderson.enums.SessaoLogin;
import jeanderson.factory.BancoDeDados;
import jeanderson.model.ExecutaOperacoes;
import jeanderson.model.Funcionario;
import jeanderson.model.Usuario;
import jeanderson.util.Email;
import jeanderson.util.HibernateUtil;
import jeanderson.util.Log;
import jeanderson.util.ResultadoBD;
import org.apache.commons.io.FileUtils;
import org.controlsfx.control.Notifications;

/**
 * FXML Controller class
 *
 * @author jeand
 */
@DefineConfiguration(url_fxml = "/jeanderson/view/LoginNovo.fxml", url_icon = "/jeanderson/assets/img/icone_principal.png", title = "Faça o Login", resizable = false)
public class LoginNovo extends EasyFXFunctions implements Initializable {

    @FXML
    @ValidateField(fieldName = "Usuário")
    private JFXTextField txtUsuario;
    @FXML
    @ValidateField(fieldName = "Senha")
    private JFXPasswordField txtSenha;
    @FXML
    private JFXSpinner spVerificando;
    @FXML
    private Text txtVerificando;
    private ControlWindow<CadastraUsuarioController> janelaCadastra;
    private ControlWindow<HomeController> janelaHome;
    private ControlWindow<RecuperaSenhaController> janelaRecupera;

    private ControlWindow<LoginNovo> janelaLogin;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.janelaCadastra = new ControlWindow(CadastraUsuarioController.class);
        this.janelaHome = new ControlWindow(HomeController.class);
        this.janelaRecupera = new ControlWindow(RecuperaSenhaController.class);
        this.txtVerificando.visibleProperty().bindBidirectional(spVerificando.visibleProperty());
        spVerificando.setVisible(false);
    }

    @FXML
    private void KeyReleasedFazerLogin(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            spVerificando.setVisible(true);
            this.actionFazerLogin(null);
        }
    }

    @FXML
    private void actionFazerLogin(ActionEvent event) {
        if (FunctionAnnotations.validateFields(this)) {
            spVerificando.setVisible(true);
            this.fazerLogin();
        }
    }

    @FXML
    private void actionEsqueceuSenha(ActionEvent event) {
        this.janelaRecupera.show(janelaLogin);
    }

    @FXML
    private void actionCadastrar(ActionEvent event) {
        this.janelaCadastra.show(janelaLogin);
    }

    @FXML
    private void actionJanelaAdmin(ActionEvent event) {
        String senhaDigitada = DialogFX.showInputText("Senha de autorização", "Informa a senha do administrador", "Senha: ");
        if (!senhaDigitada.isEmpty()) {
            if (senhaDigitada.equals("admin1023x")) {
                ControlWindow<Admin> janelaAdmin = new ControlWindow(Admin.class);
                janelaAdmin.show();
            } else {
                DialogFX.showMessage("Senha incorreta!", "Acesso não autorizado", DialogType.ERRO);
            }
        }
    }

    private void fazerLogin() {
        Thread t = new Thread(() -> {
            Usuario usuarioLogin = new Usuario();
            usuarioLogin.setUsuario(txtUsuario.getText());
            usuarioLogin.setSenha(txtSenha.getText());
            Usuario usuarioEncontrado = BancoDeDados.usuario().whereUsuarioAndSenha(usuarioLogin);
            if (usuarioEncontrado != null) {
                SessaoLogin.USUARIO_LOGADO.addCurrentUser(usuarioEncontrado);
                Platform.runLater(() -> {
                    janelaLogin.close();
                    janelaHome.show();
                    janelaHome.getController().carregarDados();
                    janelaHome.getStage().setOnCloseRequest((WindowEvent evento) -> {
                        HibernateUtil.shutdown();
                    });
                });
//                this.verificarMensalidadesAVencer(usuarioEncontrado);
//                this.verificaUpdate();
                this.executaTarefas(usuarioEncontrado);
            } else {
                Platform.runLater(() -> {
                    DialogFX.showMessage("Usuário ou Senha Incorretos", "Usuário Incorreto", DialogType.WARNING);
                    spVerificando.setVisible(false);
                });
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void exibirNotificacao(Funcionario funcionario) {
        Long resultadoMensalidades = BancoDeDados.mensalidade().countMensalidadesPorDataHoje();
        Long resultadoMatriculas = BancoDeDados.mensalidade().countMatriculasNaoPagas();
        String msg = "Olá " + funcionario.getNome();
        double duracao = 10.0;
        boolean exibirNotificao = false;
        if (resultadoMensalidades > 0 && resultadoMatriculas > 0) {
            exibirNotificao = true;
            duracao = 15.0;
            msg += ", Existe " + resultadoMensalidades.toString() + " Mensalidade(s) a Vencer Hoje!\nHá " + resultadoMatriculas.toString() + " matrícula(s) que não foi pagar a mais de 7 dias!\nPor favor vá em Financeiro e depois em Contas a Receber.";
        } else if (resultadoMatriculas > 0) {
            exibirNotificao = true;
            msg += ", Existe " + resultadoMatriculas.toString() + " Matrícula(s) que não foram paga(s) a mais de 7 dias!\nPor favor vá em Financeiro e depois em Contas a Receber.";
        } else if (resultadoMensalidades > 0) {
            exibirNotificao = true;
            msg += ", Existe " + resultadoMensalidades.toString() + " Mensalidade(s) a Vencer Hoje!\nPor favor vá em Financeiro e depois em Contas a Receber.";
        }
        if (exibirNotificao) {
            Notifications notificao = Notifications.create()
                    .title("Mensalidades a Vencer hoje")
                    .text(msg)
                    .darkStyle()
                    .graphic(new ImageView("/jeanderson/assets/img/contas_a_pagar_icon.png"))
                    .hideAfter(Duration.seconds(duracao))
                    .position(Pos.TOP_RIGHT);
            Platform.runLater(() -> {
                notificao.show();
            });
        }
    }

    private void verificarAtualizacoes() {
        try {
            File arqUpdate = new File("updateNew.xml");
            FileUtils.copyURLToFile(new URL("https://drive.google.com/uc?export=download&id=0B6YGL0lcp06mQUFXS1l5dVdNYjQ"), arqUpdate);
            if (arqUpdate.exists()) {
                XStream xstream = new XStream();
                xstream.processAnnotations(Aplicacao.class);
                Aplicacao app = (Aplicacao) xstream.fromXML(arqUpdate);
                if (!app.getVersao().equals(InfoApplication.VERSION)) {
                    Platform.runLater(() -> {
                        if (DialogFX.showConfirmation("Nova Atualização Disponível\nSua Versao: " + InfoApplication.VERSION + "\nVersão Atual: " + app.getVersao() + "\nDeseja Atualizar agora?", "Atualização disponível")) {
                            try {
                                //Runtime.getRuntime().exec("java -jar C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\Instituto\\Update.jar");
                                Runtime.getRuntime().exec("java -jar " + new File("UpdateNew.jar").getAbsolutePath());
                                this.janelaHome.close();
                            } catch (IOException ex) {
                                Logger.getLogger(LoginNovo.class.getName()).log(Level.SEVERE, null, ex);
                                Log.salvaLogger(ex);
                                DialogFX.showMessage("Não foi possível atualizar! Motivo: " + ex.getMessage(), "Erro ao Atualizar", DialogType.ERRO);
                            }
                        }
                    });
                }
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(LoginNovo.class.getName()).log(Level.SEVERE, null, ex);
            Log.salvaLogger(ex);
        } catch (IOException ex) {
            Logger.getLogger(LoginNovo.class.getName()).log(Level.SEVERE, null, ex);
            Log.salvaLogger(ex);
        }
    }

    private void enviaLogs() {
        File arqDataLogVerificado = new File("logVd.txt");
        File arqLog = new File("log.txt");
        Email email = new Email();
        try {
            if (arqLog.exists()) {
                if (arqDataLogVerificado.exists()) {
                    List<String> textoDataDaUltimaVerificao = Files.readAllLines(arqDataLogVerificado.toPath());
                    LocalDate dataUltimaVerificao = LocalDate.parse(textoDataDaUltimaVerificao.get(0));
                    LocalDate dataAtual = LocalDate.now();
                    System.out.println("Data comparada: " + dataUltimaVerificao.until(dataAtual, ChronoUnit.DAYS));
                    if (dataUltimaVerificao.until(dataAtual, ChronoUnit.DAYS) >= 3) {
                        this.enviarLogsAutomaticamente(email, arqDataLogVerificado, arqLog);
                    }
                } else {
                    if (arqDataLogVerificado.createNewFile()) {
                        enviarLogsAutomaticamente(email, arqDataLogVerificado, arqLog);
                    }
                }
            }
        } catch (IOException ex) {
            Log.salvaLogger(ex);
            Logger.getLogger(LoginNovo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void enviarLogsAutomaticamente(Email email, File arqDataLogVerificado, File arqLog) throws IOException {
        ResultadoBD result = email.reportarErros("Reportando Erros da data: " + LocalDate.now().toString(), "Sistema versão: " + InfoApplication.VERSION);
        if (result.resultado()) {
            List<String> dataDeVerificacao = new ArrayList<>();
            dataDeVerificacao.add(LocalDate.now().toString());
            Files.write(arqDataLogVerificado.toPath(), dataDeVerificacao, StandardOpenOption.WRITE);
            arqLog.delete();
        }
    }

    private void executaTarefas(Usuario usuarioLogado) {
        this.exibirNotificacao(usuarioLogado.getFuncionario());
        this.verificarAtualizacoes();
        this.enviaLogs();
        this.verificaNovidadeEoperacao();
    }

    @Override
    public void afterConstruct(ControlWindow control) {
        this.janelaLogin = control;
    }

    private void verificaNovidadeEoperacao() {
        File pasta = new File("Config");
        if (!pasta.exists()) {
            pasta.mkdir();
        }
        File arquivoConfig = new File(pasta + "/ConfigApplication.xml");
        XStream stream = new XStream();
        stream.processAnnotations(ConfigApplication.class);
        ConfigApplication configApplication;
        if (arquivoConfig.exists()) {
            configApplication = (ConfigApplication) stream.fromXML(arquivoConfig);
            if (!configApplication.getVersaoAtual().equals(InfoApplication.VERSION)) {
                configApplication.setVersaoAtual(InfoApplication.VERSION);
                try {
                    stream.toXML(configApplication, new FileOutputStream(arquivoConfig));
                    Platform.runLater(() -> {
                        this.janelaHome.getController().exibirJanelaNovidade();
                    });
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(LoginNovo.class.getName()).log(Level.SEVERE, null, ex);
                    Log.salvaLogger(ex);
                }
            }
        } else {
            configApplication = new ConfigApplication();
            configApplication.setVersaoAtual("1.9.4");
            configApplication.setCodigoDeOperacao("1");
            try {
                stream.toXML(configApplication, new FileOutputStream(arquivoConfig));
                Platform.runLater(() -> {
                    this.janelaHome.getController().exibirJanelaNovidade();
                });
            } catch (FileNotFoundException ex) {
                Logger.getLogger(LoginNovo.class.getName()).log(Level.SEVERE, null, ex);
                Log.salvaLogger(ex);
            }
        }
        ExecutaOperacoes executaOperacoes = new ExecutaOperacoes(configApplication, stream);
        if (executaOperacoes.verificaSeTemOperacoes()) {
            Platform.runLater(executaOperacoes::ChamarJanelaDeOperacao);
        }
    }

}
