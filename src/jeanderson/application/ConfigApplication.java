/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.application;

import jeanderson.interfaces.ConfigStage;

/**
 *
 * @author jeanderson
 */
public enum ConfigApplication implements ConfigStage {
    STARTAPP {
        @Override
        public String URL_fxml() {
            return "/jeanderson/view/StartApp.fxml";
        }

        @Override
        public String title() {
            return "Inicializando aplicação";
        }

    },
    LOGIN {
        @Override
        public String URL_fxml() {
            return "/jeanderson/view/Login.fxml";
        }

        @Override
        public String title() {
            return "Faça o Login para continuar";
        }

    },
    CADASTRA_USUARIO {
        @Override
        public String URL_fxml() {
            return "/jeanderson/view/CadastraUsuario.fxml";
        }

        @Override
        public String title() {
            return "Cadastro de Usuários";
        }

    },
    HOME {
        @Override
        public String URL_fxml() {
            return "/jeanderson/view/Home.fxml";
        }

        @Override
        public String title() {
            return "Gerência Instituto";
        }

    },
    NOVO_CONTATO {
        @Override
        public String URL_fxml() {
            return "/jeanderson/view/usuario/NovoContato.fxml";
        }

        @Override
        public String title() {
            return "Adicionar Novo Contato";
        }

    },
    MEUS_CONTATOS {
        @Override
        public String URL_fxml() {
            return "/jeanderson/view/usuario/MeusContatos.fxml";
        }

        @Override
        public String title() {
            return "Meus Contatos";
        }

    },
    INFO_CONTATOS {
        @Override
        public String URL_fxml() {
            return "/jeanderson/view/usuario/InfoContato.fxml";
        }

        @Override
        public String title() {
            return "Informações Sobre o Contato";
        }

    },
    CADASTRA_TURMA {
        @Override
        public String URL_fxml() {
            return "/jeanderson/view/cadastro/CadastraTurma.fxml";
        }

        @Override
        public String title() {
            return "Cadastro de Turmas";
        }

    },
    CADASTRA_CURSO {
        @Override
        public String URL_fxml() {
            return "/jeanderson/view/cadastro/CadastraCurso.fxml";
        }

        @Override
        public String title() {
            return "Cadastro de Cursos";
        }

    },
    FAZER_MATRICULA {
        @Override
        public String URL_fxml() {
            return "/jeanderson/view/cadastro/FazerMatricula.fxml";
        }

        @Override
        public String title() {
            return "Fazer matrículas de Alunos";
        }

    },
    INFO_FREQUENCIA {
        @Override
        public String URL_fxml() {
            return "/jeanderson/view/gerencia/InfoFrequencia.fxml";
        }

        @Override
        public String title() {
            return "Frequência do Aluno";
        }

    },
    FREQUENCIA {
        @Override
        public String URL_fxml() {
            return "/jeanderson/view/gerencia/Frequencia.fxml";
        }

        @Override
        public String title() {
            return "Frequência de Alunos";
        }

    },
    ALUNOS_MATRICULADOS {
        @Override
        public String URL_fxml() {
            return "/jeanderson/view/gerencia/Aluno.fxml";
        }

        @Override
        public String title() {
            return "Alunos Matrículados";
        }

    },
    INFO_ALUNO {
        @Override
        public String URL_fxml() {
            return "/jeanderson/view/gerencia/InfoAluno.fxml";
        }

        @Override
        public String title() {
            return "Informações do Aluno";
        }

    },
    CONTAS_A_RECEBER {
        @Override
        public String URL_fxml() {
            return "/jeanderson/view/financeiro/ContasReceber.fxml";
        }

        @Override
        public String title() {
            return "Contas a Receber";
        }

    },
    INFO_CONTAS_A_RECEBER {
        @Override
        public String URL_fxml() {
            return "/jeanderson/view/financeiro/InfoContasReceber.fxml";
        }

        @Override
        public String title() {
            return "Informações do Devedor";
        }

    },
    GERAR_MENSALIDADES {
        @Override
        public String URL_fxml() {
            return "/jeanderson/view/financeiro/GerarContas.fxml";
        }

        @Override
        public String title() {
            return "Gerador de Mensalidades";
        }

    },
    TURMAS {
        @Override
        public String URL_fxml() {
            return "/jeanderson/view/gerencia/Turmas.fxml";
        }

        @Override
        public String title() {
            return "Turmas Cadastradas";
        }

    },
    INFO_TURMAS {
        @Override
        public String URL_fxml() {
            return "/jeanderson/view/gerencia/InfoTurmas.fxml";
        }

        @Override
        public String title() {
            return "Informações da Turma";
        }

    },
    MEUS_LEMBRETES {
        @Override
        public String URL_fxml() {
            return "/jeanderson/view/usuario/MeusLembretes.fxml";
        }

        @Override
        public String title() {
            return "Meus Lembretes";
        }

    },
    CADASTRA_FUNCIONARIO {
        @Override
        public String URL_fxml() {
            return "/jeanderson/view/cadastro/CadastraFuncionario.fxml";
        }

        @Override
        public String title() {
            return "Cadastra Funcionário";
        }

    },
    CADASTRA_USUARIO_HOME {
        @Override
        public String URL_fxml() {
            return "/jeanderson/view/cadastro/CadastrarUsuario.fxml";
        }

        @Override
        public String title() {
            return "Cadastro de Usuário";
        }

    },
    CURSOS{
        @Override
        public String URL_fxml() {
            return "/jeanderson/view/gerencia/Cursos.fxml";
        }

        @Override
        public String title() {
            return "Cursos cadastrados";
        }
        
    }, 
    INFO_CURSOS{
        @Override
        public String URL_fxml() {
            return "/jeanderson/view/gerencia/InfoCurso.fxml";
        }

        @Override
        public String title() {
            return "Informações do Curso";
        }
        
    },
    CAIXA{
        @Override
        public String URL_fxml() {
            return "/jeanderson/view/financeiro/Caixa.fxml";
        }

        @Override
        public String title() {
            return "Meu Caixa";
        }
        
    },
    NOTA_VERSAO{
        @Override
        public String URL_fxml() {
            return "/jeanderson/view/usuario/NotaVersao.fxml";
        }

        @Override
        public String title() {
            return "Informações da Versão";
        }
        
    },
    ADICIONAR_LEMBRETE{
        @Override
        public String URL_fxml() {
            return "/jeanderson/view/usuario/AdicionaLembrete.fxml";
        }

        @Override
        public String title() {
            return "Adicionar Lembrete Pessoal";
        }
        
    },
    INFO_LEMBRETE{
        @Override
        public String URL_fxml() {
            return "/jeanderson/view/usuario/InfoLembrete.fxml";
        }

        @Override
        public String title() {
            return "Informações de Lembrete Pessoal";
        }
        
    },
    NOVA_FREQUENCIA{
        @Override
        public String URL_fxml() {
            return "/jeanderson/view/gerencia/NovaFrequencia.fxml";
        }

        @Override
        public String title() {
            return "Nova Frequência";
        }
        
    };

}
