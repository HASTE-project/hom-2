package com.benblamey.hom.manager;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.io.IOException;
import java.util.Map;

//@JacksonXmlRootElement(localName = "tier")

//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "__class")
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = JexlDeploymentTier.class, name = "com.benblamey.hom.manager.JexlDeploymentTier")
})
public abstract class Tier {


    String outputTopic;
    String uniqueTierId;
    String friendlyTierId;
    private TopicSampler sampler;

    // Intended only for the 'InputTier'
    Tier(int index, String outputTopic) {
        this.friendlyTierId = Integer.toString(index);
        this.uniqueTierId = Util.generateGUID();
        this.outputTopic = outputTopic;
        init();
    }

    // For the other kinds of Tier, where the output topic is uniquely-generated.
    Tier(int index) {
        this.friendlyTierId = Integer.toString(index);
        this.uniqueTierId = Util.generateGUID();
        this.outputTopic = "hom-topic-" + this.friendlyTierId + "-" + this.uniqueTierId;
        init();
    }

    private void init() {
        String sampleJsonlPath = CommandLineArguments.getDataPath()+"sample-tier-" + friendlyTierId + ".jsonl";
        sampler = new TopicSampler(outputTopic, sampleJsonlPath);

        try {
            NotebooksFromTemplates.CreateAnalyzeTierNotebookFromTemplate(sampleJsonlPath,
                    CommandLineArguments.getDataPath()+"analyze-tier-" + friendlyTierId + ".ipynb");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove() throws IOException, InterruptedException {
        if (sampler != null) {
            sampler.close();
            sampler = null;
        }
    }

    // Note: values must be non-null
    abstract Map<String, Object> toMap();

    abstract void setScale(int newScale) throws IOException, InterruptedException;

    abstract String getKafkaApplicationID();

    public String getOutputTopic() {
        return this.outputTopic;
    }

}
