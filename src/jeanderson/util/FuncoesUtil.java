/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;

/**
 *
 * @author jeanderson
 */
public class FuncoesUtil {

    /**
     * Transforma um valor passado como String em um valor válido do tipo
     * double.
     *
     * @param valor
     * @return
     */
    public static double validaValor(String valor) {
        String valorPreparado = "0";
        if (!valor.matches("[a-zA-Z]+")) {
            if (valor.contains(".") && !valor.contains(",")) {
                valorPreparado = valor.replace(".", ",");

            } else if (!valor.isEmpty()) {
                valorPreparado = valor;
            }
        } else {
            valorPreparado = "0";
        }
        try {

            NumberFormat formatar = NumberFormat.getInstance();
            double valorValido;
            valorValido = formatar.parse(valorPreparado).doubleValue();
            return valorValido;
        } catch (ParseException ex) {
            Logger.getLogger(FuncoesUtil.class.getName()).log(Level.SEVERE, null, ex);
            Log.salvaLogger(ex);
            return 0.0;
        }

    }

    public static TableCell factoryColunaDisponibilidade() {
        TableCell<Object, Boolean> tableCell = new TableCell<Object, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                if (empty || item == null) {
                    setStyle("");
                    setText("");
                } else {
                    if (item) {
                        setStyle("-fx-background-color: chartreuse;");
                        setText("DISPONÍVEL");
                    } else {
                        setStyle("-fx-background-color: red;");
                        setText("INDISPONÍVEL");
                    }
                }
            }

        };
        return tableCell;
    }

    public static String toDecimalFormat(Double valor) {
        DecimalFormat formatador = new DecimalFormat(".##");
        return formatador.format(valor);
    }

    public static ObservableList<Integer> getQuantidadeParcela() {
        ObservableList<Integer> quantParcelas = FXCollections.observableArrayList();
        int quantidade = 12;
        for (int i = 0; i < quantidade; i++) {
            quantParcelas.add((i + 1));
        }
        return quantParcelas;
    }

    public static TableCell factoryColunaSituacaoConta() {
        TableCell<Object, Boolean> tableCell = new TableCell<Object, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                    setText("");
                } else {
                    if (item) {
                        setStyle("-fx-background-color: lawngreen;-fx-alignment: center;");
                        setText("PAGA");
                    } else {
                        setStyle("-fx-background-color: orangered;-fx-alignment: center;");
                        setText("NÃO PAGA");
                    }
                }
            }
        };
        return tableCell;
    }

    public static Integer calculaPorcentagem(int cargaHoraria, double cargaCumprida) {
        Integer porcentagem = (int) ((cargaCumprida / cargaHoraria) * 100);
        return porcentagem;
    }

    public static Double calculaJuros(Double valor, Double juros) {
        Double valorPorcentagem = ((valor / 100) * juros);
        Double valorComJuros = (valor + valorPorcentagem);
        return valorComJuros;
    }

    public static TableCell factoryColunaPorcentagem() {
        TableCell<Object, Integer> tableCell = new TableCell<Object, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                if (empty || item == null) {
                    setStyle("");
                    setText("");
                } else {
                    if (item < 25) {
                        setStyle("-fx-background-color: red;-fx-alignment: center;");
                        setText(item.toString());
                    } else if (item < 50) {
                        setStyle("-fx-background-color: orange; -fx-alignment: center;");
                        setText(item.toString());
                    } else if (item < 75) {
                        setStyle("-fx-background-color: yellow; -fx-alignment: center;");
                        setText(item.toString());
                    } else if (item < 95) {
                        setStyle("-fx-background-color: limegreen; -fx-alignment: center;");
                        setText(item.toString());
                    } else {
                        setStyle("-fx-background-color: lawngreen; -fx-alignment: center;");
                        setText(item.toString());
                    }
                }
            }

        };
        return tableCell;
    }

    public static TableCell factoryColunaSituacaoVencimento() {
        TableCell<Object, LocalDate> tableCell = new TableCell<Object, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                if (empty || item == null) {
                    setStyle("");
                    setText("");
                } else {
                    LocalDate dataAtual = LocalDate.now();
                    if (item.compareTo(dataAtual) >= 0) {
                        setStyle("-fx-background-color: lawngreen;-fx-alignment: center;");
                        setText("A VENCER");
                    } else {
                        setStyle("-fx-background-color: red;-fx-alignment: center;");
                        setText("VENCIDO");
                    }
                }
            }

        };
        return tableCell;
    }

    public static TableCell factoryColunaSituacaoHistorico() {
        TableCell<Object, Boolean> tableCell = new TableCell<Object, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                    setText("");
                } else {
                    if (item) {
                        setStyle("-fx-background-color: lawngreen;-fx-alignment: center;");
                        setText("Concluído");
                    } else {
                        setStyle("-fx-background-color: orangered;-fx-alignment: center;");
                        setText("Não Concluído");
                    }
                }
            }
        };
        return tableCell;
    }
    public static TableCell factoryColunaSituacaoFuncionario() {
        TableCell<Object, Boolean> tableCell = new TableCell<Object, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                    setText("");
                } else {
                    if (item) {
                        setStyle("-fx-background-color: lawngreen;-fx-alignment: center;");
                        setText("ATIVO");
                    } else {
                        setStyle("-fx-background-color: red;-fx-alignment: center;");
                        setText("INATIVO");
                    }
                }
            }
        };
        return tableCell;
    }
}
