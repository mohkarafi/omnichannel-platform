package omnicanal.gateway.Filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;

/*
GlobalFilter (Filtre Global)
S'applique à TOUTES les routes automatiquement
Comme un "portier principal" qui vérifie tout le monde
*/

@Slf4j // Annotation Lombok pour générer automatiquement le logger
@Component
public class LoggingFilter implements GlobalFilter, Ordered {
    // ↑ implements Ordered : On peut définir son ordre
    @Override
    public int getOrder() {
        return -1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // ↑ Cette méthode est appelée pour CHAQUE requête HTTP
        //  ServerWebExchange : Un "conteneur" qui contient la requête ET la réponse.


        ServerHttpRequest request = exchange.getRequest();  // Récupérer la REQUÊTE
        String method = request.getMethod().name(); // EXTRAIRE LES INFORMATIONS (  GET , POST , DELETE )
        String path = request.getURI().toString();   //  Exemple : "/api/users"
        String queryParams = request.getQueryParams().toString(); // Exemple : "page=1&size=10"
        HttpHeaders headers = request.getHeaders();

        long startTime = Instant.now().toEpochMilli();   // ÉTAPE 3 : NOTER L'HEURE DE DÉBUT EN MILISECONDE
        log.info("========== INCOMING REQUEST ==========");
        log.info("Method: {}", method);
        log.info("Path: {}", path);
        log.info("Query Params: {}", queryParams != null ? queryParams : "none");
        log.info("Headers: {}", headers);
        log.info("Remote Address: {}",
                request.getRemoteAddress() != null ?
                        request.getRemoteAddress().getAddress().getHostAddress() : "unknown");

        return chain.filter(exchange) // passe au filter suivant
                .then(Mono.fromRunnable(() -> {
                    // Cette partie s'exécute APRÈS que la réponse soit prête

                    ServerHttpResponse response = exchange.getResponse(); // Récupérer la RÉPONSE
                    long endTime = Instant.now().toEpochMilli();
                    long duration = endTime - startTime;

                    log.info("========== OUTGOING RESPONSE ==========");
                    log.info("Status Code: {}", response.getStatusCode());
                    log.info("Duration: {} ms", duration);
                    log.info("Response Headers: {}", response.getHeaders());
                    log.info("=======================================\n");
                }));
    }
}
