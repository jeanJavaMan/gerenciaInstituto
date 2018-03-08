/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.enums;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import javafx.util.StringConverter;
import jeanderson.interfaces.StringConverters;
import jeanderson.model.Curso;
import jeanderson.model.Funcionario;
import jeanderson.model.Lembrete;
import jeanderson.util.FuncoesUtil;

/**
 *
 * @author jeanderson
 */
public enum Converter implements StringConverters {
    LEMBRETE_PARCIAL {
        @Override
        public StringConverter getStringConverter() {
            StringConverter<Lembrete> converter = new StringConverter<Lembrete>() {
                @Override
                public String toString(Lembrete t) {
                    if (t.getContato() != null) {
                        return (t.getContato().getSituacao() + ": " + t.getContato().getNome());
                    }else if(t.getAluno() != null){
                        return "Observação referente ao aluno: " + t.getAluno().getNome();
                    }else {
                        return "Lembrete Pessoal: " + t.getObservacoes();
                    }

                }

                @Override
                public Lembrete fromString(String string) {
                    return null;
                }
            };
            return converter;
        }
    },
    LEMBRETE_COMPLETO {
        @Override
        public StringConverter getStringConverter() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    },
    CURSOS {
        @Override
        public StringConverter getStringConverter() {
            StringConverter<Curso> converter = new StringConverter<Curso>() {
                Curso objeto;

                @Override
                public String toString(Curso object) {
                    if (object == null) {
                        return "";
                    } else {
                        objeto = object;
                        return object.getNome();
                    }
                }

                @Override
                public Curso fromString(String string) {
                    return objeto;
                }
            };
            return converter;
        }

    },
    DATAS {
        @Override
        public StringConverter getStringConverter() {
            StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
                LocalDate data;

                @Override
                public String toString(LocalDate data) {
                    if (data != null) {
                        this.data = data;
                        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    } else {
                        return "";
                    }
                }

                @Override
                public LocalDate fromString(String string) {
                    if (string == null) {
                        return data;
                    } else {
                        return LocalDate.parse(string, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    }
                }
            };
            return converter;
        }

    },
    SITUACAO_PAGAMENTO {
        @Override
        public StringConverter getStringConverter() {
            StringConverter<Boolean> converter = new StringConverter<Boolean>() {

                @Override
                public String toString(Boolean valor) {
                    if (valor != null) {
                        return valor ? "PAGA" : "NÃO PAGA";
                    } else {
                        return "";
                    }
                }

                @Override
                public Boolean fromString(String string) {
                    if (string != null) {
                        return string.startsWith("t");
                    } else {
                        return false;
                    }
                }
            };
            return converter;
        }

    },
    SITUACAO_VENCIMENTO {
        @Override
        public StringConverter getStringConverter() {
            StringConverter<Boolean> converter = new StringConverter<Boolean>() {

                @Override
                public String toString(Boolean valor) {
                    if (valor != null) {
                        return valor ? "A VENCER" : "VENCIDA";
                    } else {
                        return "";
                    }
                }

                @Override
                public Boolean fromString(String string) {
                    if (string != null) {
                        return string.startsWith("t");
                    } else {
                        return false;
                    }
                }
            };
            return converter;
        }

    },
    SITUACAO_HISTORICO {
        @Override
        public StringConverter getStringConverter() {
            StringConverter<Boolean> converter = new StringConverter<Boolean>() {

                @Override
                public String toString(Boolean valor) {
                    if (valor != null) {
                        return valor ? "Concluído" : "Não Concluído";
                    } else {
                        return "";
                    }
                }

                @Override
                public Boolean fromString(String string) {
                    if (string != null) {
                        return string.startsWith("t");
                    } else {
                        return false;
                    }
                }
            };
            return converter;
        }

    },
    SLIDER_HORAS {
        @Override
        public StringConverter getStringConverter() {
            StringConverter<Double> converter = new StringConverter<Double>() {
                @Override
                public String toString(Double valor) {
                    int valorInteiro = valor.intValue();
                    return valorInteiro + " Hrs";
                }

                @Override
                public Double fromString(String string) {
                    String valor = string.replace(" Hrs", "");
                    return Double.parseDouble(valor);
                }
            };
            return converter;
        }

    },
    TURMA_DISPONIBILIDADE {
        @Override
        public StringConverter getStringConverter() {
            StringConverter<Boolean> converter = new StringConverter<Boolean>() {

                @Override
                public String toString(Boolean valor) {
                    if (valor != null) {
                        return valor ? "DISPONÍVEL" : "INDISPONÍVEL";
                    } else {
                        return "";
                    }
                }

                @Override
                public Boolean fromString(String string) {
                    if (string != null) {
                        return string.startsWith("t");
                    } else {
                        return false;
                    }
                }
            };
            return converter;
        }

    },
    FUNCIONARIO {
        @Override
        public StringConverter getStringConverter() {
            StringConverter<Funcionario> converter = new StringConverter<Funcionario>() {
                Funcionario funcionario;

                @Override
                public String toString(Funcionario object) {
                    if (object != null) {
                        this.funcionario = object;
                        return object.getNome();
                    } else {
                        return "";
                    }
                }

                @Override
                public Funcionario fromString(String string) {
                    if (string != null) {
                        if (Objects.equals(string, funcionario.getNome())) {
                            return funcionario;
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                }
            };
            return converter;
        }

    },
    VALORES_DECIMAL {
        @Override
        public StringConverter getStringConverter() {
            StringConverter<Double> converter = new StringConverter<Double>() {
                @Override
                public String toString(Double valor) {
                    return FuncoesUtil.toDecimalFormat(valor);
                }

                @Override
                public Double fromString(String string) {
                    if (string.isEmpty()) {
                        return 0.0;
                    } else {
                        return FuncoesUtil.validaValor(string);
                    }
                }
            };
            return converter;
        }

    },
    FUNCIONARIO_SITUACAO {
        @Override
        public StringConverter getStringConverter() {
            StringConverter<Boolean> converter = new StringConverter<Boolean>() {

                @Override
                public String toString(Boolean valor) {
                    if (valor != null) {
                        return valor ? "ATIVO" : "INATIVO";
                    } else {
                        return "";
                    }
                }

                @Override
                public Boolean fromString(String string) {
                    if (string != null) {
                        return string.startsWith("t");
                    } else {
                        return false;
                    }
                }
            };
            return converter;
        }

    };
}
