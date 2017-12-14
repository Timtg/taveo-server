package no.timesaver.jwt;

import no.timesaver.domain.User;
import no.timesaver.domain.types.UserTypeEnum;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Optional;

@Service
public class JwtVerifier {

    private final JwtKeyProvider jwtKeyProvider;

    @Autowired
    public JwtVerifier(JwtKeyProvider jwtKeyProvider) {
        this.jwtKeyProvider = jwtKeyProvider;
    }

    public Optional<User> verify(String authorizationHeader) {
        try {
            JwtClaims body = getBody(authorizationHeader);
            return Optional.of(getUserFromJwsBody(body));
        } catch (Exception e){
            return Optional.empty();
        }
    }

    public boolean isIssuedByServer(String jwt){
        try {
            JwtClaims jwtClaims = getBody(jwt);
            Map<String, Object> body = jwtClaims.getClaimsMap();
            return body.get("iss") != null && body.get("email").equals(jwtKeyProvider.ISSUER);
        } catch (Exception e){
            return false;
        }
    }

    private User getUserFromJwsBody(JwtClaims jwtClaims) {
        Map<String, Object> body = jwtClaims.getClaimsMap();
        User u = new User();
        u.setId((Long)body.get("id"));
        u.setEmail((String)body.get("email"));
        u.setMobile((String)body.get("mobile"));
        u.setName((String)body.get("name"));
        u.setStoreId((Long)body.get("storeId"));
        u.setType(UserTypeEnum.ofDescription((String)body.get("type")));
        u.setMobileVerified((Boolean) body.get("mobileVerified"));
        u.setEmailVerified((Boolean) body.get("emailVerified"));
        u.setAcceptedDisclaimer((Boolean) body.get("acceptedDisclaimer"));
        return u;
    }

    private JwtClaims getBody(String token){
        try {
            return getJwtConsumer().processToClaims(token);
        } catch (InvalidJwtException e) {
            throw new SecurityException("Invalid token: ",e);
        }
    }

    private JwtConsumer getJwtConsumer() {
        try {
            return new JwtConsumerBuilder()
                    .setRequireExpirationTime() // the JWT must have an expiration time
                    .setRequireSubject() // the JWT must have a subject claim
                    .setExpectedIssuer(jwtKeyProvider.ISSUER) // whom the JWT needs to have been issued by
                    .setVerificationKey(jwtKeyProvider.getSigningKey()) // verify the signature with the public key
                    .build(); // create the JwtConsumer instance
        } catch (UnsupportedEncodingException e) {
            throw new SecurityException("Unable to create jwtConsumer due to: ", e);
        }
    }

}
