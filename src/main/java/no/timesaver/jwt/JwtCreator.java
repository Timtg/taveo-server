package no.timesaver.jwt;

import no.timesaver.domain.User;
import no.timesaver.domain.types.UserTypeEnum;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class JwtCreator {

    private final JwtKeyProvider jwtKeyProvider;

    @Autowired
    public JwtCreator(JwtKeyProvider jwtKeyProvider) {
        this.jwtKeyProvider = jwtKeyProvider;
    }

    public String getJwtForUser(User u) throws JoseException, UnsupportedEncodingException {
        JwtClaims claims = getJwtClaimsForUser(u);

        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setKey(jwtKeyProvider.getSigningKey());
        jws.setAlgorithmHeaderValue(jwtKeyProvider.getAlgorithm());

        return jws.getCompactSerialization();
    }

    private JwtClaims getJwtClaimsForUser(User u) {
        JwtClaims claims = new JwtClaims();
        claims.setIssuer(jwtKeyProvider.ISSUER);  // who creates the token and signs it
        claims.setExpirationTimeMinutesInTheFuture(getJwtExpirationMinutes(u.getType())); // time when the token will expire (10 minutes from now)
        claims.setGeneratedJwtId(); // a unique identifier for the token
        claims.setIssuedAtToNow();  // when the token was issued/created (now)
        claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)
        claims.setSubject(""+u.getId()); // the subject/principal is whom the token is about


        Long storeId = (u.getStoreId() == null || u.getStoreId().equals(0L)) ? null : u.getStoreId();
        claims.setClaim("id",u.getId());
        claims.setClaim("email",u.getEmail());
        claims.setClaim("mobile",u.getMobile());
        claims.setClaim("name",u.getName());
        claims.setClaim("type",u.getType().toString());
        claims.setClaim("mobileVerified",u.isMobileVerified());
        claims.setClaim("emailVerified",u.isEmailVerified());
        if(storeId != null) {
            claims.setClaim("storeId", storeId);
        }
        claims.setClaim("acceptedDisclaimer",u.isAcceptedDisclaimer());
        return claims;
    }

    float getJwtExpirationMinutes(UserTypeEnum type) {
        //one day
        if(UserTypeEnum.A.equals(type) || UserTypeEnum.M.equals(type)){
            return 60F *24F;
        }
        //one month
        return 60F * 24F * 7F *4F;
    }
}
