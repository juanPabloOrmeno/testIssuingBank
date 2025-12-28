package org.bank.issuingbank.logging;

/**
 * Constantes para el sistema de logging
 */
public final class LoggingConstants {

    private LoggingConstants() {
        // Prevenir instanciación
    }

    /**
     * Key para el correlationId en MDC
     */
    public static final String CORRELATION_ID_KEY = "correlationId";

    /**
     * Header HTTP para el correlationId
     */
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    /**
     * Nombre del servicio para logs
     */
    public static final String SERVICE_NAME = "issuing-bank-service";

    /**
     * Formato de timestamp para logs
     */
    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
}
