package omnicanal.gateway.Filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Filtre qui génère un Request-ID unique pour chaque requête
 * et l'ajoute aux headers de la requête transmise aux microservices
 *
 * Pourquoi c'est important ?
 * - Permet de tracer une requête à travers tous les services
 * - Facilite le debugging (tous les logs auront le même Request-ID)
 * - Essentiel pour la traçabilité distribuée
 */
@Slf4j
@Component
public class RequestIdFilter implements GlobalFilter, Ordered {

    // Nom du header qui contiendra le Request-ID
    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    // Nom du header pour la corrélation (optionnel)
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        // 1. VÉRIFIER SI UN REQUEST-ID EXISTE DÉJÀ
        String requestId = request.getHeaders().getFirst(REQUEST_ID_HEADER);

        // 2. SI ABSENT, EN GÉNÉRER UN NOUVEAU
        if (requestId == null || requestId.isEmpty()) {
            requestId = generateRequestId();
            log.debug("Generated new Request-ID: {}", requestId);
        } else {
            log.debug("Using existing Request-ID: {}", requestId);
        }

        // 3. AJOUTER LE REQUEST-ID À LA REQUÊTE
        ServerHttpRequest modifiedRequest = request.mutate()
                .header(REQUEST_ID_HEADER, requestId)
                .header(CORRELATION_ID_HEADER, requestId)  // Même valeur pour simplicité
                .build();

        // 4. REMPLACER LA REQUÊTE ORIGINALE PAR LA REQUÊTE MODIFIÉE
        ServerWebExchange modifiedExchange = exchange.mutate()
                .request(modifiedRequest)
                .build();

        // 5. LOGGER POUR TRAÇABILITÉ
        log.info("Request-ID [{}] - {} {}",
                requestId,
                request.getMethod(),
                request.getURI().getPath());

        // 6. CONTINUER LA CHAÎNE AVEC LA REQUÊTE MODIFIÉE
        return chain.filter(modifiedExchange);
    }

    /**
     * Génère un Request-ID unique
     * Format: UUID version 4 (aléatoire)
     *
     * Exemple: "550e8400-e29b-41d4-a716-446655440000"
     */
    private String generateRequestId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Ce filtre doit s'exécuter TRÈS TÔT
     * Mais APRÈS le LoggingFilter pour que le Request-ID soit loggé
     */
    @Override
    public int getOrder() {
        return 0;  // Juste après le LoggingFilter (-1)
    }
}