public enum UserRole {
    //papel do usuario
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");
    private final String roleName;
    UserRole(String rolenome){
        this.roleName = rolenome;
    }
    public String getRoleName() {
        return roleName;
    }
}
