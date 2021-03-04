package tourGuide.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UUIDException extends Exception {

    private Logger logger = LoggerFactory.getLogger(UUIDException.class);
    public UUIDException(String s) {
        super(s);
        logger.error(s);
    }
}
