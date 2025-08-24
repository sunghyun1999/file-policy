CREATE TABLE blocked_extension (
                                   id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                   ext_value VARCHAR(20) NOT NULL,
                                   ext_type  VARCHAR(10) NOT NULL,
                                   enabled   BIT NOT NULL DEFAULT 0,
                                   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                   CONSTRAINT uq_blocked_extension_value UNIQUE (ext_value)
);

CREATE INDEX idx_blocked_extension_type ON blocked_extension (ext_type);
