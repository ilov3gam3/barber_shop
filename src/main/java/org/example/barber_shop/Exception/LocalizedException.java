package org.example.barber_shop.Exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@Getter
@Setter
public class LocalizedException extends RuntimeException {
    private final String messageKey;
    private final Object[] args;

    public LocalizedException(String messageKey, Object... args) {
        this.messageKey = messageKey;
        this.args = args;
    }

    public String getLocalizedMessage(MessageSource messageSource) {
        return messageSource.getMessage(messageKey, args, LocaleContextHolder.getLocale());
    }
}
