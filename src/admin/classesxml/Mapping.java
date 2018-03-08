/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package admin.classesxml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 *
 * @author jeanderson
 */
@XStreamAlias("mapping")
public class Mapping {

    @XStreamAlias("class")
    @XStreamAsAttribute
    private String className;

    public Mapping(String className) {
        this.className = "jeanderson.model." + className;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
 
    public void addClassName(String className){
        this.className = "jeanderson.model." + className;
    }
    
}
