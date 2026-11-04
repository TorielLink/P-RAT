package cpar.backend.config;

import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/** Application déployée : sert le front Angular compilé et renvoie index.html pour ses routes */
@Configuration
public class SpaConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(@NonNull String chemin, @NonNull Resource dossier) throws IOException {
                        Resource fichier = dossier.createRelative(chemin);
                        if (fichier.exists() && fichier.isReadable()) {
                            return fichier;
                        }
                        // adresse API inconnue → vrai 404, pas la page Angular
                        if (chemin.startsWith("api/")) {
                            return null;
                        }
                        // route Angular → index.html, puis le routeur Angular
                        Resource index = dossier.createRelative("index.html");
                        return index.exists() ? index : null;
                    }
                });
    }
}