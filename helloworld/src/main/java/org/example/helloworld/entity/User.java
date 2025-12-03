package org.example.helloworld.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`IdUser`")
    private Integer idUser;

    @Column(name = "`UserName`", nullable = false, unique = true)
    private String userName;

    @Column(name = "`Password`", nullable = false)
    private String password;

    @Column(name = "`Token`")
    private String token;

    // Getter & Setter
    public Integer getIdUser() { return idUser; }
    public void setIdUser(Integer idUser) { this.idUser = idUser; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
