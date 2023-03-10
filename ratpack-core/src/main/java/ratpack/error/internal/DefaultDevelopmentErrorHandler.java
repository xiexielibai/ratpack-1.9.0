/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ratpack.error.internal;

import com.google.common.base.Throwables;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.handling.Context;
import ratpack.http.Request;

/**
 * A simple server and client error handler that prints out information in plain text to the response.
 * <p>
 * <b>This is not suitable for use in production</b> as it exposes internal information about your application via stack traces.
 */
public class DefaultDevelopmentErrorHandler implements ErrorHandler {

  private final static Logger LOGGER = LoggerFactory.getLogger(DefaultDevelopmentErrorHandler.class);

  /**
   * {@link Exception#printStackTrace() Prints the stacktrace} of the given exception to the response with a 500 status.
   *
   *  @param ctx The ctx being processed
   * @param throwable The exception that occurred
   */
  @Override
  public void error(Context ctx, Throwable throwable) throws Exception {
    LOGGER.error("exception thrown for request to " + ctx.getRequest().getRawUri(), throwable);
    ctx.getResponse().status(500);

    ctx.byContent(s -> s
        .plainText(() ->
            ctx.render(Throwables.getStackTraceAsString(throwable) + "\n\nThis stacktrace error response was generated by the default development error handler.")
        )
        .html(() ->
            new ErrorPageRenderer() {
              @Override
              protected void render() {
                render(ctx, "Internal Error", w -> {
                  messages(w, "Internal Error", () -> {
                    Request request = ctx.getRequest();
                    meta(w, m -> m
                        .put("URI:", request.getRawUri())
                        .put("Method:", request.getMethod().getName())
                    );
                  });
                  stack(w, null, throwable);
                });
              }
            }
        )
        .noMatch("text/plain")
    );

  }

  /**
   * Prints the string "Client error ??statusCode??" to the response as text with the given status code.
   *
   * @param ctx The ctx
   * @param statusCode The 4xx status code that explains the problem
   */
  @Override
  public void error(Context ctx, int statusCode) throws Exception {
    HttpResponseStatus status = HttpResponseStatus.valueOf(statusCode);
    Request request = ctx.getRequest();
    LOGGER.error(statusCode + " client error for request to " + request.getRawUri());
    ctx.getResponse().status(statusCode);

    ctx.byContent(s -> s
        .plainText(() ->
            ctx.render("Client error " + statusCode)
        )
        .html(() ->
            new ErrorPageRenderer() {
              protected void render() {
                render(ctx, status.reasonPhrase(), w ->
                    messages(w, "Client Error", () ->
                        meta(w, m -> m
                            .put("URI:", request.getRawUri())
                            .put("Method:", request.getMethod().getName())
                            .put("Status Code:", status.code())
                            .put("Phrase:", status.reasonPhrase())
                        )
                    )
                );
              }
            }
        )
        .noMatch("text/plain")
    );
  }

}
