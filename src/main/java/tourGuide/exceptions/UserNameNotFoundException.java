package tourGuide.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/***
 * Gestion de l'exception User not found
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNameNotFoundException extends Exception {

    private final Logger logger = LoggerFactory.getLogger(UserNameNotFoundException.class);

    public UserNameNotFoundException(String s) {
        super(s);
        logger.error(s);
    }
}
