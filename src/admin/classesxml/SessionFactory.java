/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package admin.classesxml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jeanderson
 */
@XStreamAlias("session-factory")
public class SessionFactory {
    @XStreamAlias("property")
    @XStreamImplicit
    private List<Property> propertys;
    @XStreamAlias("mapping")
    @XStreamImplicit
    private List<Mapping> mappings;

    public SessionFactory() {
        this.propertys = new ArrayList<>();
        this.mappings = new ArrayList<>();
    }

    
    public List<Property> getPropertys() {
        return propertys;
    }

    public void setPropertys(List<Property> propertys) {
        this.propertys = propertys;
    }   

    public List<Mapping> getMappings() {
        return mappings;
    }

    public void setMappins(List<Mapping> mappings) {
        this.mappings = mappings;
    }
    
    public void setURL(String url){
        this.propertys.stream().filter(property -> property.getName().contains("url")).findFirst().get().setContent(url);
    }
    
    public void setUSER(String user){
        this.propertys.stream().filter(property -> property.getName().contains("username")).findFirst().get().setContent(user);
    }
    
    public void setPASSWORD(String password){
        this.propertys.stream().filter(property -> property.getName().contains("password")).findFirst().get().setContent(password);
    }
    
    public void addMapping(String className) {
    	boolean contains = false;
    	for(Mapping map : mappings) {
    		if(map.getClassName().contains(className)) {
    			contains = true;
    			break;
    		}
    	}
    	if(!contains) {
    		mappings.add(new Mapping(className));
    	}
    }
}
