/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.model;

import java.io.Serializable;
import java.time.Duration;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Jeanderson
 */
@Entity
@Table(name = "cursos")
public class Curso implements Serializable {

    private final IntegerProperty id;
    private final StringProperty nome;
    private Duration cargaHoraria;
    private final StringProperty conteudo;
    private final DoubleProperty valor;

    public Curso() {
        this.id = new SimpleIntegerProperty();
        this.nome = new SimpleStringProperty();
        this.conteudo = new SimpleStringProperty();
        this.valor = new SimpleDoubleProperty();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "curso_id")
    public Integer getId() {
        return id.get();
    }

    @Column(name = "nome", length = 40, nullable = false)
    public String getNome() {
        return nome.get();
    }

    @Column(name = "carga_horaria")
    public Duration getCargaHoraria() {
        return cargaHoraria;
    }

    @Column(name = "conteudo", columnDefinition = "text")
    public String getConteudo() {
        return this.conteudo.get();
    }

    @Column(name = "valor", columnDefinition = "DECIMAL(10,2)")
    public double getValor() {
        return this.valor.get();
    }

    public StringProperty nomeProperty() {
        return this.nome;
    }

    public StringProperty conteudoProperty() {
        return this.conteudo;
    }

    public DoubleProperty valorProperty() {
        return this.valor;
    }

    protected void setId(int id) {
        this.id.set(id);
    }

    public void setNome(String nome) {
        this.nome.set(nome);
    }

    public void setCargaHoraria(Duration cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public void setConteudo(String conteudo) {
        this.conteudo.set(conteudo);
    }

    public void setValor(double valor) {
        this.valor.set(valor);
    }

}
