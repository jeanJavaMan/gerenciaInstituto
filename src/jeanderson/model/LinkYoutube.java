/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.model;

import lombok.Builder;
import lombok.Data;

/**
 *
 * @author jeand
 */
@Data
@Builder
public class LinkYoutube {    
    private String nome;
    private String link;    

    public boolean linkIsNull(){
        return link == null;
    }
    
    @Override
    public String toString() {
        return nome;
    }
    
}
