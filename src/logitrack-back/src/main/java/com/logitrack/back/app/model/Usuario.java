package com.logitrack.back.app.model;

public class Usuario {

    private String username;
    private String password;
    private Role role;

    public Usuario(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    public Role getRole() {
        return role;
    }
    
}
