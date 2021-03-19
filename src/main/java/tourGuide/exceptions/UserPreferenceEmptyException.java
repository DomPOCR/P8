package tourGuide.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserPreferenceEmptyException extends Exception {

    private final Logger logger = LoggerFactory.getLogger(UserPreferenceEmptyException.class);

    public UserPreferenceEmptyException(String s) {
        super(s);
        logger.error(s);
    }
}
