/*
 * Copyright 2016 the original author or authors.
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

package ratpack.http.client;

import io.netty.buffer.ByteBufAllocator;
import io.netty.resolver.AddressResolverGroup;
import io.netty.resolver.DefaultAddressResolverGroup;
import io.netty.resolver.dns.DnsNameResolverBuilder;
import ratpack.exec.ExecController;
import ratpack.exec.Operation;
import ratpack.func.Action;
import ratpack.server.ServerConfig;

import java.time.Duration;

/**
 * An additive specification of a HTTP client.
 * <p>
 * See {@link HttpClient#of(Action)}.
 *
 * @since 1.4
 */
public interface HttpClientSpec {

  /**
   * The buffer allocator to use.
   * <p>
   * Defaults to {@link ByteBufAllocator#DEFAULT}.
   *
   * @param byteBufAllocator the buffer allocator
   * @return {@code this}
   */
  HttpClientSpec byteBufAllocator(ByteBufAllocator byteBufAllocator);

  /**
   * The exec controller to associate with.
   * <p>
   * Defaults to {@link ExecController#current()}, when the HttpClient is constructed.
   * <p>
   * If the HTTP client is being constructed outside of a managed thread,
   * explicitly specifying the execution controller may be necessary dependending on the configuration of the HTTP client.
   *
   * @param execController the execution controller
   * @return {@code this}
   * @since 1.9
   */
  HttpClientSpec execController(ExecController execController);

  /**
   * The maximum number of connections to maintain to a given protocol/host/port.
   * <p>
   * Defaults to 0.
   * <p>
   * Setting this number to &gt; 0 enables connection pooling (a.k.a. HTTP Keep Alive).
   * The given value dictates the number of connections to a given target, not the overall size.
   * Calling {@link HttpClient#close()} will close all current connections.
   *
   * @param poolSize the connection pool size
   * @return {@code this}
   */
  HttpClientSpec poolSize(int poolSize);

  /**
   * The maximum number of requests that will be queued if connection pool was depleted.
   * <p>
   * Defaults to {@link Integer#MAX_VALUE}.
   * <p>
   * Setting this option is recommended, because the http client queues requests when the pool is depleted. Once
   * a connection is available, the request is processed and all resources released.
   * <p>
   * The option is not applied if pool size is not set.
   *
   * @param poolQueueSize the connection pool queue size
   * @return {@code this}
   * @since 1.6
   */
  HttpClientSpec poolQueueSize(int poolQueueSize);

  /**
   * The default amount of time to allow a connection to remain idle in the connection pool.
   * <p>
   * If the connection is idle for the timeout value, it will be closed.
   * <p>
   * A value of {@link Duration#ZERO} is interpreted as no timeout.
   * The value is never {@link Duration#isNegative()}.
   *
   * @return {@code this}
   * @since 1.7
   */
  HttpClientSpec idleTimeout(Duration idleTimeout);

  /**
   * The maximum size to allow for responses.
   * <p>
   * Defaults to {@link ServerConfig#DEFAULT_MAX_CONTENT_LENGTH}.
   *
   * @param maxContentLength the maximum response content length
   * @return {@code this}
   */
  HttpClientSpec maxContentLength(int maxContentLength);

  /**
   * The read timeout value for responses.
   * <p>
   * Defaults to 30 seconds.
   *
   * @param readTimeout the read timeout value for responses
   * @return {@code this}
   */
  HttpClientSpec readTimeout(Duration readTimeout);

  /**
   * The connect timeout value for requests.
   * <p>
   * Defaults to 30 seconds.
   *
   * @param connectTimeout the connect timeout value for requests
   * @return {@code this}
   * @since 1.5
   */
  HttpClientSpec connectTimeout(Duration connectTimeout);

  /**
   * The max size of the chunks to emit when reading a response as a stream.
   * <p>
   * Defaults to 8192.
   * <p>
   * Increasing this value can increase throughput at the expense of memory use.
   *
   * @param numBytes the max number of bytes to emit
   * @return {@code this}
   * @since 1.5
   */
  HttpClientSpec responseMaxChunkSize(int numBytes);

  /**
   * Add an interceptor for all requests handled by this client.
   * <p>
   * This function is additive.
   *
   * @param interceptor the action to perform on the spec before transmitting.
   * @return {@code} this
   * @since 1.6
   */
  HttpClientSpec requestIntercept(Action<? super RequestSpec> interceptor);

  /**
   * Add an interceptor for all responses returned by this client.
   * <p>
   * This function is additive.
   *
   * @param interceptor the action to perform on the response before returning.
   * @return {@code this}
   * @since 1.6
   */
  HttpClientSpec responseIntercept(Action<? super HttpResponse> interceptor);

  /**
   * Execute the provide {@link Operation} for all responses returned by this client.
   * <p>
   * This function will wrap the provided operation and subscribe to it.
   * This function is additive with {@link #responseIntercept(Action)}.
   *
   * @param operation the operation to subscribe to before return the response.
   * @return {@code this}
   * @since 1.6
   */
  HttpClientSpec responseIntercept(Operation operation);

  /**
   * Add an interceptor for errors thrown by this client (eg. connection refused, timeouts)
   * <p>
   * This function is additive.
   *
   * @param interceptor the action to perform on the error before propagating.
   * @return {@code this}
   * @since 1.6
   */
  HttpClientSpec errorIntercept(Action<? super Throwable> interceptor);

  /**
   * Enable metric collection on HTTP Client.
   * <p>
   * Defaults to false.
   *
   * @param enableMetricsCollection A boolean used to enable metric collection.
   * @return {@code this}
   * @since 1.6
   */
  HttpClientSpec enableMetricsCollection(boolean enableMetricsCollection);

  /**
   * Configure a HTTP proxy for outgoing calls from this client.
   *
   * @param proxy the proxy configuration
   * @return {@code this}
   * @since 1.8
   */
  HttpClientSpec proxy(Action<? super ProxySpec> proxy);

  /**
   * Specifies a custom name resolver to use.
   *
   * @param resolver the resolver group
   * @return {@code this}
   * @since 1.9
   */
  HttpClientSpec addressResolver(AddressResolverGroup<?> resolver);

  /**
   * Specifies a custom name resolver to use.
   *
   * @param resolver the configuration of the resolver to use
   * @return {@code this}
   * @since 1.9
   */
  HttpClientSpec addressResolver(Action<? super DnsNameResolverBuilder> resolver);

  /**
   * Specifies that the JDK name resolver should be used.
   * <p>
   * This option should only be used when it is absolutely necessary to use the JDK implementation.
   * By default, a Netty based implementation is used that provides better performance.
   *
   * @return this
   * @since 1.9
   */
  default HttpClientSpec useJdkAddressResolver() {
    return addressResolver(DefaultAddressResolverGroup.INSTANCE);
  }

}
