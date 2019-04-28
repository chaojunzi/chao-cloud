package com.chao.cloud.common.support.feign.encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;

/**
 * The type Spring multipart encoder.
 *
 * @author dax.
 * @version v1.0
 * @since 2017 /12/26 14:16
 */
public class SpringMultipartEncoder implements Encoder {

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private static final Class MULTIPART_ARRAY_CLAZZ = MultipartFile[].class;
    private static final String FILES_KEY = "multipartFiles";
    private final List<HttpMessageConverter<?>> converters = new RestTemplate().getMessageConverters();
    private final HttpHeaders multipartHeaders = new HttpHeaders();
    private final HttpHeaders jsonHeaders = new HttpHeaders();

    /**
     * Instantiates a new Spring multipart encoder.
     */
    public SpringMultipartEncoder() {
        multipartHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
        if (isFormRequest(bodyType)) {

            encodeMultipartFormRequest(object, template);
        } else {
            encodeRequest(object, jsonHeaders, template);
        }
    }

    /**
     * Encodes the request as a multipart form. It can detect a single
     * {@link MultipartFile}, an array of {@link MultipartFile}s, or POJOs (that
     * are converted to JSON).
     *
     * @param object
     * @param template
     * @throws EncodeException
     */
    private void encodeMultipartFormRequest(Object object, RequestTemplate template) throws EncodeException {
        if (object == null) {
            throw new EncodeException("Cannot encode request with null form.");
        }
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

        if (isMultipartFile(object)) {
            MultipartFile multipartFile = (MultipartFile) object;

            map.add(multipartFile.getName(), encodeMultipartFile(multipartFile));
        } else if (isMultipartFileArray(object)) {
            encodeMultipartFiles(map, FILES_KEY, Arrays.asList((MultipartFile[]) object));
        } else {
            map.add("", encodeJsonObject(object));
        }

        encodeRequest(map, multipartHeaders, template);
    }

    private boolean isMultipartFile(Object object) {
        return object instanceof MultipartFile;
    }

    private boolean isMultipartFileArray(Object o) {
        return o != null && o.getClass().isArray()
                && MultipartFile.class.isAssignableFrom(o.getClass().getComponentType());
    }

    /**
     * Wraps a single {@link MultipartFile} into a {@link HttpEntity} and sets
     * the {@code Content-type} header to {@code application/octet-stream}
     *
     * @param file
     * @return
     */
    private HttpEntity<?> encodeMultipartFile(MultipartFile file) {
        HttpHeaders filePartHeaders = new HttpHeaders();
        filePartHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        try {
            Resource multipartFileResource = new MultipartFileResource(file.getOriginalFilename(), file.getSize(),
                    file.getInputStream());
            return new HttpEntity<>(multipartFileResource, filePartHeaders);
        } catch (IOException ex) {
            throw new EncodeException("Cannot encode request.", ex);
        }
    }

    /**
     * Fills the request map with {@link HttpEntity}s containing the given
     * {@link MultipartFile}s. Sets the {@code Content-type} header to
     * {@code application/octet-stream} for each file.
     *
     * @param map
     *            current request map.
     * @param name
     *            the name of the array field in the multipart form.
     * @param files
     */
    private void encodeMultipartFiles(LinkedMultiValueMap<String, Object> map, String name,
            List<? extends MultipartFile> files) {
        HttpHeaders filePartHeaders = new HttpHeaders();
        filePartHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        try {
            for (MultipartFile file : files) {
                Resource multipartFileResource = new MultipartFileResource(file.getOriginalFilename(), file.getSize(),
                        file.getInputStream());
                map.add(name, new HttpEntity<>(multipartFileResource, filePartHeaders));
            }
        } catch (IOException ex) {
            throw new EncodeException("Cannot encode request.", ex);
        }
    }

    /**
     * Wraps an object into a {@link HttpEntity} and sets the
     * {@code Content-type} header to {@code application/json}
     *
     * @param o
     * @return
     */
    private HttpEntity<?> encodeJsonObject(Object o) {
        HttpHeaders jsonPartHeaders = new HttpHeaders();
        jsonPartHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(o, jsonPartHeaders);
    }

    /**
     * Calls the conversion chain actually used by
     * {@link org.springframework.web.client.RestTemplate}, filling the body of
     * the request template.
     *
     * @param value
     * @param requestHeaders
     * @param template
     * @throws EncodeException
     */
    private void encodeRequest(Object value, HttpHeaders requestHeaders, RequestTemplate template)
            throws EncodeException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpOutputMessage dummyRequest = new HttpOutputMessageImpl(outputStream, requestHeaders);
        try {
            Class<?> requestType = value.getClass();
            MediaType requestContentType = requestHeaders.getContentType();
            for (HttpMessageConverter<?> messageConverter : converters) {
                if (messageConverter.canWrite(requestType, requestContentType)) {
                    ((HttpMessageConverter<Object>) messageConverter).write(value, requestContentType, dummyRequest);
                    break;
                }
            }
        } catch (IOException ex) {
            throw new EncodeException("Cannot encode request.", ex);
        }
        HttpHeaders headers = dummyRequest.getHeaders();
        if (headers != null) {
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                template.header(entry.getKey(), entry.getValue());
            }
        }
        /*
         * we should use a template output stream... this will cause issues if
         * files are too big, since the whole request will be in memory.
         */
        template.body(outputStream.toByteArray(), DEFAULT_CHARSET);
    }

    /**
     * Heuristic check for multipart requests.
     *
     * @param type
     *            the type
     * @return boolean boolean
     * @see feign.Types
     */
    private static boolean isFormRequest(Type type) {

        return MAP_STRING_WILDCARD.equals(type) || MULTIPART_ARRAY_CLAZZ.equals(type);
    }

}
