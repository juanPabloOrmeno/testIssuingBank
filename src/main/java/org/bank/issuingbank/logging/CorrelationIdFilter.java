package org.bank.issuingbank.logging;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * Filtro HTTP que gestiona el correlationId para trazabilidad de requests
 * El correlationId se propaga a través de MDC (Mapped Diagnostic Context)
 * y se incluye automáticamente en todos los logs
 */
@Component
@Order(1)
public class CorrelationIdFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(CorrelationIdFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            // Obtener correlationId del header o generar uno nuevo
            String correlationId = httpRequest.getHeader(LoggingConstants.CORRELATION_ID_HEADER);

            if (correlationId == null || correlationId.isEmpty()) {
                correlationId = generateCorrelationId();
                log.debug("Generated new correlationId: {}", correlationId);
            } else {
                log.debug("Using existing correlationId from header: {}", correlationId);
            }

            // Inyectar correlationId en MDC (estará disponible en todos los logs)
            MDC.put(LoggingConstants.CORRELATION_ID_KEY, correlationId);

            // Agregar correlationId al response header
            httpResponse.setHeader(LoggingConstants.CORRELATION_ID_HEADER, correlationId);

            // Log de inicio de request
            log.info("Incoming request: {} {} - correlationId: {}",
                    httpRequest.getMethod(),
                    httpRequest.getRequestURI(),
                    correlationId);

            // Continuar con la cadena de filtros
            chain.doFilter(request, response);

            // Log de fin de request
            log.info("Completed request: {} {} - status: {} - correlationId: {}",
                    httpRequest.getMethod(),
                    httpRequest.getRequestURI(),
                    httpResponse.getStatus(),
                    correlationId);

        } finally {
            // CRÍTICO: Limpiar MDC para evitar memory leaks
            MDC.clear();
        }
    }

    /**
     * Genera un nuevo UUID como correlationId
     */
    private String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }
}
