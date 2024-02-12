package io.github.amithkoujalgi.ollama4j.core.models.chat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.amithkoujalgi.ollama4j.core.utils.Options;
import io.github.amithkoujalgi.ollama4j.core.utils.Utils;

/**
 * Helper class for creating {@link OllamaChatRequestModel} objects using the builder-pattern.
 */
public class OllamaChatRequestBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(OllamaChatRequestBuilder.class);

    private OllamaChatRequestBuilder(String model, List<OllamaChatMessage> messages){
        request = new OllamaChatRequestModel(model, messages);
    }

    private OllamaChatRequestModel request;

    public static OllamaChatRequestBuilder getInstance(String model){
        return new OllamaChatRequestBuilder(model, new ArrayList<>());
    }

    public OllamaChatRequestModel build(){
        return request;
    }

    public void reset(){
        request = new OllamaChatRequestModel(request.getModel(), new ArrayList<>());
    }

    public OllamaChatRequestBuilder withMessage(OllamaChatMessageRole role, String content){
        return withMessage(role, content, (String)null);
    }

    public OllamaChatRequestBuilder withMessage(OllamaChatMessageRole role, String content, List<File> images){
        List<OllamaChatMessage> messages = this.request.getMessages();

        List<byte[]> binaryImages = images.stream().map(file -> {
            try {
                return Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                LOG.warn(String.format("File '%s' could not be accessed, will not add to message!",file.toPath()), e);
                return new byte[0];
            }
        }).collect(Collectors.toList());

        messages.add(new OllamaChatMessage(role,content,binaryImages));
        return this;
    }

    public OllamaChatRequestBuilder withMessage(OllamaChatMessageRole role, String content, String... imageUrls){
        List<OllamaChatMessage> messages = this.request.getMessages();
        List<byte[]> binaryImages = null;
        if(imageUrls.length>0){
            binaryImages = new ArrayList<>();
            for (String imageUrl : imageUrls) {
                try{
                    binaryImages.add(Utils.loadImageBytesFromUrl(imageUrl));
                }
                    catch (URISyntaxException e){
                        LOG.warn(String.format("URL '%s' could not be accessed, will not add to message!",imageUrl), e);
                }
                catch (IOException e){
                    LOG.warn(String.format("Content of URL '%s' could not be read, will not add to message!",imageUrl), e);
                }
            }
        }
        
        messages.add(new OllamaChatMessage(role,content,binaryImages));
        return this;
    }

    public OllamaChatRequestBuilder withMessages(List<OllamaChatMessage> messages){
        this.request.getMessages().addAll(messages);
        return this;
    }

    public OllamaChatRequestBuilder withOptions(Options options){
        this.request.setOptions(options);
        return this;
    }

    public OllamaChatRequestBuilder withFormat(String format){
        this.request.setFormat(format);
        return this;
    }

    public OllamaChatRequestBuilder withTemplate(String template){
        this.request.setTemplate(template);
        return this;
    }

    public OllamaChatRequestBuilder withStreaming(){
        this.request.setStream(true);
        return this;
    }

    public OllamaChatRequestBuilder withKeepAlive(String keepAlive){
        this.request.setKeepAlive(keepAlive);
        return this;
    }

}
