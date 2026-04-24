package com.logitrack.back.app.model;

public class Sesion {

    private String token;
    private String username;
    private Role role;

    public Sesion(String token, String username, Role role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public String getUsername(){
        return username;
    }

    public Role getRole() {
        return role;
    }

}
