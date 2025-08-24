CREATE TABLE blocked_extension (
                                   id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                   ext_value VARCHAR(20) NOT NULL,     -- ✅ value 대신 ext_value
                                   type      VARCHAR(10) NOT NULL,     -- 'FIXED' | 'CUSTOM'
                                   enabled   BIT NOT NULL DEFAULT 0,
                                   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                   CONSTRAINT uq_blocked_extension_value UNIQUE (ext_value),
                                   INDEX idx_blocked_extension_type (type)
);
