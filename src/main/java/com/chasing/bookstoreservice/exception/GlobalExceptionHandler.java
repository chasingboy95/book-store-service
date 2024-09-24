package com.chasing.bookstoreservice.exception;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Provider
@Slf4j
public class GlobalExceptionHandler {



    @Provider
    public static class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

        @Context
        private UriInfo uriInfo;

        @Override
        public Response toResponse(BusinessException e) {
            log.error("Request: {}, BusinessException", uriInfo.getPath(), e);
            return Response.status(e.getHttpStatus()).entity(e.getMessage()).build();
        }
    }

    @Provider
    public static class ThrowableMapper implements ExceptionMapper<Throwable> {

        @Context
        private UriInfo uriInfo;

        @Override
        public Response toResponse(Throwable e) {
            log.error("Request: {}, Throwable", uriInfo.getPath(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
