package com.fitnessai.platform.ai.rag;

import com.fitnessai.platform.ai.config.AiProperties;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.stereotype.Service;

@Service
public class FitnessKnowledgeRagService {
    private final KnowledgeSourceRepository sources;
    private final EmbeddingStore<TextSegment> store;
    private final EmbeddingModel embeddingModel;
    private final AiProperties properties;
    private final AtomicBoolean indexed = new AtomicBoolean(false);

    public FitnessKnowledgeRagService(KnowledgeSourceRepository sources, EmbeddingStore<TextSegment> store,
                                      EmbeddingModel embeddingModel, AiProperties properties) {
        this.sources = sources; this.store = store; this.embeddingModel = embeddingModel; this.properties = properties;
    }

    public List<Content> retrieve(String question) {
        ensureIndexed();
        ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(store).embeddingModel(embeddingModel)
                .maxResults(properties.rag().maxResults()).minScore(properties.rag().minScore()).build();
        return retriever.retrieve(Query.from(question));
    }

    public synchronized int refresh() {
        store.removeAll();
        List<Document> documents = sources.findPublishedSources().stream()
                .filter(source -> source.content() != null && !source.content().isBlank())
                .map(source -> Document.from(source.content(), Metadata.from(Map.of(
                        "sourceType", source.type(), "sourceId", source.id(), "title", source.title()))))
                .toList();
        if (!documents.isEmpty()) {
            EmbeddingStoreIngestor.builder().documentSplitter(DocumentSplitters.recursive(800, 100))
                    .embeddingModel(embeddingModel).embeddingStore(store).build().ingest(documents);
        }
        indexed.set(true);
        return documents.size();
    }

    private void ensureIndexed() {
        if (!indexed.get()) refresh();
    }
}
