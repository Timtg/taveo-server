package no.timesaver.jwt;

import org.jose4j.jws.AlgorithmIdentifiers;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.Key;

@Component
class JwtKeyProvider {

    private final String signingKeyPlain = "9G93MeD939XO6x4fke7sl6ens93lf83m";
    private final static String jwtSigningAlgorithm = "HmacSHA256";
    private final String algorithm = AlgorithmIdentifiers.HMAC_SHA256;
    final String ISSUER = "Taveo-server";

    private boolean verifyKey(String key){
        return key.length() >= 32;
    }

    Key getSigningKey() throws UnsupportedEncodingException {
        if(verifyKey(signingKeyPlain)) {
            return new SecretKeySpec(signingKeyPlain.getBytes("UTF-8"), jwtSigningAlgorithm);
        } else {
            throw new SecurityException("Invalid private key");
        }
    }

    String getAlgorithm() {
        return algorithm;
    }
}
