package no.acntech.listener;

import org.jooq.ExecuteContext;
import org.jooq.impl.DefaultExecuteListener;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

public class ExceptionTranslatorExecuteListener extends DefaultExecuteListener {

    @Override
    public void exception(final ExecuteContext context) {
        var dialect = context.configuration().dialect();
        var translator = new SQLErrorCodeSQLExceptionTranslator(dialect.thirdParty().springDbName());
        context.exception(translator.translate("Access database using jOOQ", context.sql(), context.sqlException()));
    }
}
