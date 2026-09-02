CREATE TABLE cms_article_favorite (
    user_id BIGINT NOT NULL,
    article_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (user_id, article_id),
    INDEX idx_article_favorite_article_user (article_id, user_id),
    CONSTRAINT fk_article_favorite_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
    CONSTRAINT fk_article_favorite_article FOREIGN KEY (article_id) REFERENCES cms_article(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
