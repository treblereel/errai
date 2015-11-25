package org.jboss.errai.demo.jms.shared;

import java.io.Serializable;
import java.util.Date;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class User implements Serializable {
  private String name;
  private Date age;
  private Boolean alive;
  
  public User(){
    
  }
  
  public User(String name, Date age, Boolean alive){
    this.setName(name);
    this.setAge(age);
    this.setAlive(alive);
  }

  public Boolean getAlive() {
    return alive;
  }

  public void setAlive(Boolean alive) {
    this.alive = alive;
  }

  public Date getAge() {
    return age;
  }

  public void setAge(Date age) {
    this.age = age;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
  @Override
  public String toString(){
    return " name :" + name + " age : " + age  + " alive :" + alive;
  }

}
 