package ifTG.travelPlan.filter;

import com.google.gson.JsonObject;
import ifTG.travelPlan.exception.StatusCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.info("message = {}", accessDeniedException.getMessage());
        log.debug("AuthenticationEntryPoint : {}", (Object) accessDeniedException.getStackTrace());
        log.info("request = servletPath: {}, param-key: {}, param-value: {}, method: {}", request.getServletPath(), request.getParameterMap().keySet(), request.getParameterMap().values(), request.getMethod());


        StatusCode statusCode = StatusCode.AUTHORITY_FAILED;
        response.setStatus(statusCode.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", statusCode.getMessage());
        jsonObject.addProperty("code", statusCode.getCode());

        response.getWriter().write(jsonObject.toString());
        response.getWriter().flush();
    }
}
