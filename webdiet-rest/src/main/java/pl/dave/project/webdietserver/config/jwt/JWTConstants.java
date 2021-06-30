package pl.dave.project.webdietserver.config.jwt;

public class JWTConstants {
    public static final String SECRET = "Teidbew2152";
    public static final String HEADER_STRING = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final long EXPIRATION_TIME = 86_400_000; //1 day
}
