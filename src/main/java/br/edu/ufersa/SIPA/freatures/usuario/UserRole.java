public enum UserRole {
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
