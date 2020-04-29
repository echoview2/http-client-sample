/*
 * ConsoleAppMain
 *
 * All Rights Reserved, Copyright (C) 2019 echoview2
 *
 */

package echoview.sample;

import java.io.IOException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.List;


import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;


/**
 * 
 * @author       Takahiro Sato
 * @version      1.0
 */
public class ConsoleAppMain {
  public static void main(String args[]) throws IOException {
    ConsoleAppMain httpClientSample = new ConsoleAppMain();

    for (int i = 0; i < 5000; i++) {
      if ((i % 100) == 0) {
        System.out.println(LocalDateTime.now().toString() + ", i=" + i);
      }
            
      String response = httpClientSample.execute();
    }
  }

  public String execute() throws IOException {
    // 一時オブジェクトを作成
    Object obj = new Object() {
        @Override public void finalize() {
          System.out.println(LocalDateTime.now().toString() +
                             ", Thread=" + Thread.currentThread().getName() +
                             "(" + Thread.currentThread().getId() + ")" +
                             ", finalizer called(" + this.toString() + ")");
          // Thread.dumpStack();
        }
      };

    try {
      CloseableHttpClient httpclient = HttpClients.createDefault();
      HttpGet httpget = new HttpGet("http://jboss-server.local/spring-sample/person");

      ResponseHandler<String> rh = new ResponseHandler<String>() {
          @Override
          public String handleResponse(final HttpResponse response) throws IOException {
            StatusLine statusLine = response.getStatusLine();
            HttpEntity entity = response.getEntity();
            if (statusLine.getStatusCode() >= 300) {
              throw new HttpResponseException(
                  statusLine.getStatusCode(),
                  statusLine.getReasonPhrase());
            }

            if (entity == null) {
              throw new ClientProtocolException("Response contains no content");
            }
            
            ContentType contentType = ContentType.getOrDefault(entity);
            Charset charset = contentType.getCharset();
            Reader reader = new InputStreamReader(entity.getContent(), charset);

            String responseStr = readerToString(reader);
            return responseStr;
          }
        };

      return httpclient.execute(httpget, rh);

    } finally {
      // DO NOTHING
    }
  }

  protected String readerToString(Reader in) throws IOException {
    final int bufferSize = 1024;
    final char[] buffer = new char[bufferSize];
    final StringBuilder out = new StringBuilder();

    int charsRead;
    while((charsRead = in.read(buffer, 0, buffer.length)) > 0) {
      out.append(buffer, 0, charsRead);
    }

    return out.toString();
  }
}
