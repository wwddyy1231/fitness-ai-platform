CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY,
    username VARCHAR(32) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    nickname VARCHAR(40) NOT NULL,
    email VARCHAR(100),
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    deleted TINYINT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY,
    code VARCHAR(32) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    deleted TINYINT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE sys_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES sys_role(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE cms_category (
    id BIGINT PRIMARY KEY,
    parent_id BIGINT NOT NULL DEFAULT 0,
    name VARCHAR(50) NOT NULL,
    slug VARCHAR(60) NOT NULL UNIQUE,
    sort INT NOT NULL DEFAULT 0,
    enabled TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    deleted TINYINT NOT NULL DEFAULT 0,
    INDEX idx_category_parent_sort(parent_id, sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE cms_tag (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    slug VARCHAR(60) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    deleted TINYINT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE cms_article (
    id BIGINT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    summary VARCHAR(500),
    content LONGTEXT NOT NULL,
    cover_url VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    recommended TINYINT NOT NULL DEFAULT 0,
    view_count BIGINT NOT NULL DEFAULT 0,
    published_at DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    deleted TINYINT NOT NULL DEFAULT 0,
    INDEX idx_article_category_publish(category_id, status, published_at),
    INDEX idx_article_hot(status, view_count),
    INDEX idx_article_recommended(status, recommended, published_at),
    CONSTRAINT fk_article_category FOREIGN KEY(category_id) REFERENCES cms_category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE cms_article_tag (
    article_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY(article_id, tag_id),
    CONSTRAINT fk_article_tag_article FOREIGN KEY(article_id) REFERENCES cms_article(id),
    CONSTRAINT fk_article_tag_tag FOREIGN KEY(tag_id) REFERENCES cms_tag(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO sys_role(id, code, name, created_at, updated_at, deleted) VALUES
(1, 'MEMBER', '普通会员', UTC_TIMESTAMP(), UTC_TIMESTAMP(), 0),
(2, 'EDITOR', '内容编辑', UTC_TIMESTAMP(), UTC_TIMESTAMP(), 0),
(3, 'ADMIN', '管理员', UTC_TIMESTAMP(), UTC_TIMESTAMP(), 0);

INSERT INTO cms_category(id, parent_id, name, slug, sort, enabled, created_at, updated_at, deleted) VALUES
(101, 0, '健身文章', 'fitness', 10, 1, UTC_TIMESTAMP(), UTC_TIMESTAMP(), 0),
(102, 0, '健身营养', 'nutrition', 20, 1, UTC_TIMESTAMP(), UTC_TIMESTAMP(), 0),
(103, 0, '健身器材', 'equipment', 30, 1, UTC_TIMESTAMP(), UTC_TIMESTAMP(), 0);
